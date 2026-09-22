package com.ruoyi.web.controller.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 与 AI 助手对话
     * body: { message, history: [{role, content}], page, pageContext, images: [dataUrl] }
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, Object> body)
    {
        String message = String.valueOf(body.getOrDefault("message", "")).trim();

        // 图片附件（前端压缩后的 data:image/xxx;base64,...），最多 4 张
        List<String> images = new ArrayList<>();
        Object rawImages = body.get("images");
        if (rawImages instanceof List<?> list)
        {
            for (Object item : list)
            {
                if (item instanceof String s && s.startsWith("data:image/") && s.length() <= 3_000_000)
                {
                    images.add(s);
                }
                if (images.size() >= 4)
                {
                    break;
                }
            }
        }

        if (message.isEmpty() && images.isEmpty())
        {
            return error("消息不能为空");
        }
        if (message.length() > 2000)
        {
            return error("消息长度不能超过 2000 字");
        }

        List<Map<String, String>> history = new ArrayList<>();
        Object rawHistory = body.get("history");
        if (rawHistory instanceof List<?> list)
        {
            for (Object item : list)
            {
                if (item instanceof Map<?, ?> m)
                {
                    history.add(Map.of(
                            "role", String.valueOf(m.get("role")),
                            "content", String.valueOf(m.get("content"))));
                }
            }
        }
        String page = String.valueOf(body.getOrDefault("page", ""));
        Object rawCtx = body.get("pageContext");
        String pageContext = rawCtx == null ? "" : String.valueOf(rawCtx);
        if (pageContext.length() > 30000)
        {
            pageContext = pageContext.substring(0, 30000);
        }

        Map<String, Object> result = aiAssistantService.chat(message, history, page, pageContext, images);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("answer", result.get("answer"));
        ajax.put("navigate", result.get("navigate"));
        ajax.put("steps", result.get("steps"));
        return ajax;
    }

    /**
     * 自动办事 Agent：每轮返回一个待执行动作
     * body: { goal, page, pageContext, actions: [{type,target,value,ok}], digest: [String], stepNo, images }
     */
    @PostMapping("/agent")
    public AjaxResult agent(@RequestBody Map<String, Object> body)
    {
        String goal = String.valueOf(body.getOrDefault("goal", "")).trim();
        if (goal.isEmpty())
        {
            return error("目标不能为空");
        }
        if (goal.length() > 500)
        {
            return error("目标长度不能超过 500 字");
        }
        String page = String.valueOf(body.getOrDefault("page", ""));
        Object rawCtx = body.get("pageContext");
        String pageContext = rawCtx == null ? "" : String.valueOf(rawCtx);
        if (pageContext.length() > 30000)
        {
            pageContext = pageContext.substring(0, 30000);
        }

        // 首轮可携带截图（用户用图片指代操作目标，如"点一下这些"）
        List<String> images = new ArrayList<>();
        Object rawImages = body.get("images");
        if (rawImages instanceof List<?> list)
        {
            for (Object item : list)
            {
                if (item instanceof String s && s.startsWith("data:image/") && s.length() <= 3_000_000)
                {
                    images.add(s);
                }
                if (images.size() >= 4)
                {
                    break;
                }
            }
        }

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

        // 超出明细窗口的更早动作摘要（长任务记忆，防止重复填写/重复点击）
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
        int stepNo = 0;
        Object rawStep = body.get("stepNo");
        if (rawStep instanceof Number n)
        {
            stepNo = n.intValue();
        }

        Map<String, Object> result = aiAssistantService.agentStep(goal, page, pageContext, actions, images, digest, stepNo);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("say", result.get("say"));
        ajax.put("action", result.get("action"));
        ajax.put("done", result.get("done"));
        return ajax;
    }
}
