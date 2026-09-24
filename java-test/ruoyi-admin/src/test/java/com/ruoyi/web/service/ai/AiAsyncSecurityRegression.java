package com.ruoyi.web.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.config.SecurityConfig;
import com.ruoyi.framework.config.properties.PermitAllUrlProperties;
import com.ruoyi.framework.security.filter.JwtAuthenticationTokenFilter;
import com.ruoyi.framework.security.handle.AuthenticationEntryPointImpl;
import com.ruoyi.framework.security.handle.LogoutSuccessHandlerImpl;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.controller.ai.AiAssistantController;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Real HTTP/ASYNC regression using production security and AI controllers, without credentials or business data.
 * From java-test, after mvn -pl ruoyi-admin -am -DskipTests package:
 * java -Dloader.main=com.ruoyi.web.service.ai.AiAsyncSecurityRegression
 *      -Dloader.path=ruoyi-admin/target/test-classes -cp ruoyi-admin/target/ruoyi-admin.jar
 *      org.springframework.boot.loader.launch.PropertiesLauncher
 */
public class AiAsyncSecurityRegression
{
    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, JwtAuthenticationTokenFilter.class, PermitAllUrlProperties.class,
            AiAssistantController.class})
    static class WebConfig
    {
        @Bean TomcatServletWebServerFactory webServer() { return new TomcatServletWebServerFactory(0); }
        @Bean DispatcherServlet dispatcherServlet() { return new DispatcherServlet(); }
        @Bean ServletRegistrationBean<DispatcherServlet> servlet(DispatcherServlet servlet)
        {
            var registration = new ServletRegistrationBean<>(servlet, "/");
            registration.setAsyncSupported(true);
            return registration;
        }
        @Bean DelegatingFilterProxyRegistrationBean securityRegistration()
        {
            var registration = new DelegatingFilterProxyRegistrationBean("springSecurityFilterChain");
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
            registration.setAsyncSupported(true);
            return registration;
        }
        @Bean CorsFilter corsFilter() { return new CorsFilter(new UrlBasedCorsConfigurationSource()); }
    }

    static class FakeAi extends AiAssistantService
    {
        final AtomicInteger calls = new AtomicInteger();
        private CompletableFuture<Map<String, Object>> reply(String mode)
        {
            calls.incrementAndGet();
            if (mode.equals("timeout")) return new CompletableFuture<>();
            return CompletableFuture.supplyAsync(() -> {
                if (mode.equals("failure")) throw new IllegalStateException("AI upstream unavailable");
                return Map.<String, Object>of("answer", "ok", "say", "ok", "done", true,
                        "action", Map.of("type", "ask"));
            }, CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS));
        }
        @Override public CompletableFuture<Map<String, Object>> chat(String message,
                List<Map<String, String>> history, String page, String context, List<String> images, List<String> records)
        { return reply(message); }
        @Override public CompletableFuture<Map<String, Object>> agentStep(String goal, String page, String context,
                List<Map<String, Object>> actions, List<String> images, List<String> digest, int step,
                List<Map<String, String>> history, List<String> records, String hints)
        { return reply(goal); }
    }

    public static void main(String[] args) throws Exception
    {
        var ai = new FakeAi();
        try (var context = new AnnotationConfigServletWebServerApplicationContext())
        {
            // Register test doubles as singletons so production token/AI configuration is never loaded.
            context.getBeanFactory().registerSingleton("tokenService", new TokenService() {
                @Override public LoginUser getLoginUser(HttpServletRequest request)
                {
                    if (!"Bearer regression-valid".equals(request.getHeader("Authorization"))) return null;
                    var user = new SysUser();
                    user.setUserName("regression-user");
                    return new LoginUser(1L, 1L, user, Set.of());
                }
                @Override public void verifyToken(LoginUser user) { }
            });
            context.getBeanFactory().registerSingleton("aiAssistantService", ai);
            context.getBeanFactory().registerSingleton("unauthorizedHandler", new AuthenticationEntryPointImpl());
            context.getBeanFactory().registerSingleton("logoutSuccessHandler", new LogoutSuccessHandlerImpl());
            // Controller adds 10 seconds; use a one-second timeout for the timeout regression.
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of("ai.timeout", -9)));
            context.register(WebConfig.class);
            context.refresh();
            var client = HttpClient.newHttpClient();
            var json = new ObjectMapper();
            String base = "http://localhost:" + context.getWebServer().getPort();
            int checks = 0;
            for (String endpoint : List.of("chat", "agent"))
            {
                for (String token : List.of("", "invalid", "regression-valid"))
                {
                    for (String mode : List.of("success", "failure", "timeout"))
                    {
                        int before = ai.calls.get();
                        var builder = HttpRequest.newBuilder(URI.create(base + "/ai/" + endpoint))
                                .timeout(Duration.ofSeconds(8)).header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(
                                        Map.of("message", mode, "goal", mode))));
                        if (!token.isEmpty()) builder.header("Authorization", "Bearer " + token);
                        var response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                        var body = json.readTree(response.body());
                        int expected = !token.equals("regression-valid") ? 401 : mode.equals("success") ? 200 : 500;
                        if (body.path("code").asInt() != expected)
                            throw new AssertionError(endpoint + "/" + mode + ": expected " + expected + " got " + body);
                        if (expected == 401 && ai.calls.get() != before)
                            throw new AssertionError("Unauthenticated request reached AI service");
                        if (expected == 200 && !body.path(endpoint.equals("chat") ? "answer" : "say").asText().equals("ok"))
                            throw new AssertionError("AI result was lost");
                        if (expected == 500 && !body.path("msg").asText().contains(mode.equals("timeout") ? "超时" : "upstream"))
                            throw new AssertionError("AI failure/timeout was replaced: " + body);
                        checks++;
                    }
                }
            }
            System.out.println("PASS: " + checks + " real HTTP cases: chat/agent success, failure, timeout; missing/invalid tokens stay rejected");
        }
    }
}
