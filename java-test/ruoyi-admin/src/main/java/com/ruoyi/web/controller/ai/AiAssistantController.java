package com.ruoyi.web.controller.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.service.ai.AiAssistantService;

/**
 * AI 助手Controller：对话 + 页面操作引导
 */
@RestController
@RequestMapping("/ai")
public class AiAssistantController extends BaseController
{
    /** 只允许这几种 MIME 类型，拒绝 gif/svg/bmp 等 */
    private static final Set<String> ALLOWED_IMAGE_MIME = Set.of(
            "data:image/jpeg;", "data:image/jpg;", "data:image/png;", "data:image/webp;");

    @Autowired
    private AiAssistantService aiAssistantService;

    /** DeferredResult 超时时间，比 ai.timeout 多留 10 秒缓冲 */
    @Value("${ai.timeout:60}")
    private int aiTimeout;

    /** Agent 单任务最大步数，超过则拒绝，防止无限循环 */
    @Value("${ai.agent-max-steps:60}")
    private int agentMaxSteps;

    /**
     * 与 AI 助手对话
     * body: { message, history: [{role, content}], page, pageContext, images: [dataUrl] }
     */
    @PostMapping("/chat")
    public DeferredResult<AjaxResult> chat(@RequestBody Map<String, Object> body)
    {
        long timeoutMs = (aiTimeout + 10) * 1000L;
        DeferredResult<AjaxResult> deferred = new DeferredResult<>(timeoutMs,
                AjaxResult.error("AI 响应超时，请稍后重试"));

        String message = String.valueOf(body.getOrDefault("message", "")).trim();
        List<String> images = readImages(body);

        if (message.isEmpty() && images.isEmpty())
        {
            deferred.setResult(AjaxResult.error("消息不能为空"));
            return deferred;
        }
        if (message.length() > 2000)
        {
            deferred.setResult(AjaxResult.error("消息长度不能超过 2000 字"));
            return deferred;
        }

        List<Map<String, String>> history = readHistory(body);
        String page = String.valueOf(body.getOrDefault("page", ""));
        String pageContext = truncate(body.get("pageContext"), 30000);

        aiAssistantService.chat(message, history, page, pageContext, images, readTaskRecords(body))
                .whenComplete((result, ex) -> {
                    if (ex != null)
                    {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        deferred.setResult(AjaxResult.error(cause.getMessage()));
                    }
                    else
                    {
                        AjaxResult ajax = AjaxResult.success();
                        ajax.put("answer", result.get("answer"));
                        ajax.put("navigate", result.get("navigate"));
                        ajax.put("steps", result.get("steps"));
                        deferred.setResult(ajax);
                    }
                });
        return deferred;
    }

    /**
     * 自动办事 Agent：每轮返回一个待执行动作
     * body: { goal, page, pageContext, actions: [{type,target,value,ok}], digest: [String], stepNo, images }
     */
    @PostMapping("/agent")
    public DeferredResult<AjaxResult> agent(@RequestBody Map<String, Object> body)
    {
        long timeoutMs = (aiTimeout + 10) * 1000L;
        DeferredResult<AjaxResult> deferred = new DeferredResult<>(timeoutMs,
                AjaxResult.error("AI 响应超时，请稍后重试"));

        String goal = String.valueOf(body.getOrDefault("goal", "")).trim();
        if (goal.isEmpty())
        {
            deferred.setResult(AjaxResult.error("目标不能为空"));
            return deferred;
        }
        if (goal.length() > 4000)
        {
            deferred.setResult(AjaxResult.error("目标长度不能超过 4000 字"));
            return deferred;
        }

        int stepNo = 0;
        Object rawStep = body.get("stepNo");
        if (rawStep instanceof Number n)
        {
            stepNo = n.intValue();
        }
        if (stepNo >= agentMaxSteps)
        {
            deferred.setResult(AjaxResult.error("任务步数已达上限（" + agentMaxSteps + " 步），请重新发起任务"));
            return deferred;
        }

        String page = String.valueOf(body.getOrDefault("page", ""));
        String pageContext = truncate(body.get("pageContext"), 30000);
        List<String> images = readImages(body);

        List<Map<String, Object>> actions = new ArrayList<>();
        Object rawActions = body.get("actions");
        if (rawActions instanceof List<?> list)
        {
            for (Object item : list)
            {
                if (item instanceof Map<?, ?> m)
                {
                    Map<String, Object> a = new HashMap<>();
                    a.put("type", String.valueOf(m.get("type")));
                    a.put("target", String.valueOf(m.get("target")));
                    a.put("ok", String.valueOf(m.get("ok")));
                    Object v = m.get("value");
                    if (v != null && !String.valueOf(v).isEmpty())
                    {
                        a.put("value", v instanceof Boolean ? v : String.valueOf(v));
                    }
                    actions.add(a);
                }
            }
            if (actions.size() > 12)
            {
                actions = actions.subList(actions.size() - 12, actions.size());
            }
        }

        List<String> digest = new ArrayList<>();
        Object rawDigest = body.get("digest");
        if (rawDigest instanceof List<?> list)
        {
            for (Object item : list)
            {
                String s = String.valueOf(item);
                digest.add(s.length() > 160 ? s.substring(0, 160) : s);
                if (digest.size() >= 80)
                {
                    break;
                }
            }
        }

        String hints = body.get("hints") instanceof String h ? h.substring(0, Math.min(1000, h.length())) : "";

        aiAssistantService.agentStep(goal, page, pageContext, actions, images, digest, stepNo,
                readHistory(body), readTaskRecords(body), hints)
                .whenComplete((result, ex) -> {
                    if (ex != null)
                    {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        deferred.setResult(AjaxResult.error(cause.getMessage()));
                    }
                    else
                    {
                        AjaxResult ajax = AjaxResult.success();
                        ajax.put("say", result.get("say"));
                        ajax.put("action", result.get("action"));
                        ajax.put("done", result.get("done"));
                        deferred.setResult(ajax);
                    }
                });
        return deferred;
    }

    /** 读取并校验图片：MIME 白名单 + 3MB + 最多 4 张 */
    private List<String> readImages(Map<String, Object> body)
    {
        List<String> images = new ArrayList<>();
        Object rawImages = body.get("images");
        if (!(rawImages instanceof List<?> list))
        {
            return images;
        }
        for (Object item : list)
        {
            if (!(item instanceof String s)) continue;
            if (s.length() > 3_000_000) continue;
            boolean allowed = false;
            for (String mime : ALLOWED_IMAGE_MIME)
            {
                if (s.startsWith(mime)) { allowed = true; break; }
            }
            if (!allowed) continue;
            images.add(s);
            if (images.size() >= 4) break;
        }
        return images;
    }

    private List<Map<String, String>> readHistory(Map<String, Object> body)
    {
        List<Map<String, String>> history = new ArrayList<>();
        if (body.get("history") instanceof List<?> list)
        {
            for (Object item : list.subList(Math.max(0, list.size() - 40), list.size()))
            {
                if (item instanceof Map<?, ?> m && m.get("role") instanceof String role
                        && m.get("content") instanceof String content)
                {
                    history.add(Map.of("role", role, "content", content));
                }
            }
        }
        return history;
    }

    private List<String> readTaskRecords(Map<String, Object> body)
    {
        List<String> records = new ArrayList<>();
        if (body.get("taskRecords") instanceof List<?> list)
        {
            for (Object item : list.subList(Math.max(0, list.size() - 6), list.size()))
            {
                if (item instanceof String text && !text.isBlank())
                    records.add(text.substring(0, Math.min(2000, text.length())));
            }
        }
        return records;
    }

    private String truncate(Object value, int max)
    {
        if (value == null) return "";
        String s = String.valueOf(value);
        return s.length() > max ? s.substring(0, max) : s;
    }
}
