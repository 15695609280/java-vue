package com.ruoyi.web.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.web.controller.ai.AiAssistantController;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/** Standalone regression: local fake model, no business APIs or credentials. */
public class AiConversationRegression
{
    private static void check(boolean ok, String message)
    {
        if (!ok) throw new AssertionError(message);
    }

    private static void field(Object target, String name, Object value) throws Exception
    {
        var field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    public static void main(String[] args) throws Exception
    {
        var json = new ObjectMapper();
        var captured = new AtomicReference<JsonNode>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat/completions", exchange -> {
            captured.set(json.readTree(exchange.getRequestBody()));
            String answer = "{\"answer\":\"ok\",\"say\":\"ok\",\"action\":{\"type\":\"ask\"},\"done\":true}";
            byte[] response = json.writeValueAsBytes(Map.of("choices", List.of(Map.of("message", Map.of("content", answer)))));
            exchange.sendResponseHeaders(200, response.length);
            try (var out = exchange.getResponseBody()) { out.write(response); }
        });
        server.start();
        try
        {
            var service = new AiAssistantService();
            field(service, "baseUrl", "http://127.0.0.1:" + server.getAddress().getPort());
            field(service, "apiKey", "local-regression");
            field(service, "model", "test");
            field(service, "timeout", 10);
            var controller = new AiAssistantController();
            field(controller, "aiAssistantService", service);
            List<Map<String, String>> history = new ArrayList<>();
            for (int i = 0; i < 30; i++) history.add(Map.of("role", i % 2 == 0 ? "user" : "assistant", "content", "conversation-" + i));
            history.add(Map.of("role", "system", "content", "invalid-role"));
            var body = Map.<String, Object>of("message", "latest-question", "goal", "latest-goal", "history", history,
                    "taskRecords", List.of("created department TESTKF"));
            for (boolean agent : List.of(false, true))
            {
                if (agent) controller.agent(body); else controller.chat(body);
                JsonNode messages = captured.get().path("messages");
                String text = messages.toString();
                check(text.contains("conversation-0") && text.contains("conversation-29"), "Effective conversation lost");
                check(text.contains("created department TESTKF"), "Task identity lost");
                check(!text.contains("invalid-role"), "Untrusted system role accepted");
                check(messages.get(messages.size() - 1).path("content").asText().contains(agent ? "latest-goal" : "latest-question"), "Current request not last");
            }
            history.clear();
            for (int i = 0; i < 50; i++) history.add(Map.of("role", "user", "content", "turn-" + i + "x".repeat(5000)));
            var records = new ArrayList<String>();
            for (int i = 0; i < 10; i++) records.add("record-" + i + "r".repeat(3000));
            controller.agent(Map.of("goal", "latest-goal", "history", history, "taskRecords", records));
            JsonNode messages = captured.get().path("messages");
            int total = 0;
            for (JsonNode message : messages)
            {
                String content = message.path("content").asText();
                if (content.startsWith("turn-")) { total += content.length(); check(content.length() <= 4000, "Per-message budget exceeded"); }
            }
            check(total == 24000 && messages.toString().contains("turn-49"), "History budget or recency failed");
            check(!messages.toString().contains("record-3") && messages.toString().contains("record-9"), "Task record bound failed");
            controller.agent(Map.of("goal", "fresh-goal"));
            check(captured.get().path("messages").size() == 2, "History leaked between requests");
            System.out.println("PASS: chat and agent history, task records, role filtering, length bounds, fresh session");
        }
        finally { server.stop(0); }
    }
}
