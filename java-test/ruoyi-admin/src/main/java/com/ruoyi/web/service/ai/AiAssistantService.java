package com.ruoyi.web.service.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.service.ai.AiMenuCatalog.MenuInfo;

/**
 * AI 助手服务：调用 OpenAI 兼容接口（阿里 MaaS）进行对话，
 * 并按约定 JSON 协议返回 answer / navigate / steps，供前端自动引导。
 */
@Service
public class AiAssistantService
{
    private static final Logger log = LoggerFactory.getLogger(AiAssistantService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${ai.base-url:}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:qwen-plus}")
    private String model;

    /** 图片问答使用的视觉模型；留空则复用 model（新版 qwen-plus 支持图片输入） */
    @Value("${ai.vision-model:}")
    private String visionModel;

    @Value("${ai.timeout:60}")
    private int timeout;

    /** Agent 单步决策的推理档位（low/medium/high）：越高越稳但越慢，默认 medium 兼顾速度 */
    @Value("${ai.agent-reasoning:medium}")
    private String agentReasoning;

    /**
     * 与 AI 助手对话
     *
     * @param message     用户问题
     * @param history     会话历史（role/content），保留最近 40 条并限制总长度
     * @param page        用户当前所在页面（路由路径 + 标题）
     * @param pageContext 当前页面实时快照（表格数据、统计数字等），可为空
     * @param images      用户上传的图片（data:image/...;base64,...），可为空
     * @return { answer, navigate:{path,menuText,group}|null, steps:[{target,title,content}] }
     */
    public CompletableFuture<Map<String, Object>> chat(String message, List<Map<String, String>> history, String page, String pageContext, List<String> images, List<String> taskRecords)
    {
        checkConfig();
        boolean hasImages = images != null && !images.isEmpty();
        String useModel = (hasImages && visionModel != null && !visionModel.isBlank()) ? visionModel : model;
        return callUpstream(buildChatMessages(message, history, page, pageContext, images, taskRecords), useModel, hasImages, "low")
                .thenApply(this::parseAnswer)
                .exceptionally(ex -> {
                    Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                    if (cause instanceof ServiceException se) throw se;
                    log.warn("AI chat request failed: {}", ex.toString());
                    throw new ServiceException("AI 服务暂时不可用，请稍后重试");
                });
    }

    /**
     * 自动办事 Agent 单步决策
     *
     * @param goal        用户目标
     * @param page        当前页面（路由 + 标题）
     * @param pageContext 当前页面快照
     * @param actions     最近执行的动作及结果 [{type,target,value,ok}]
     * @param images      首轮可携带截图（data:image/...），让模型看到用户所指内容
     * @param digest      更早动作的压缩摘要（长任务记忆，防重复操作）
     * @param stepNo      当前步数序号
     * @param hints       前端解析出的指代提示（如"刚创建的"对应的记录标识），可为空
     * @return { say, action:{type,target,value,row}, done }
     */
    public CompletableFuture<Map<String, Object>> agentStep(String goal, String page, String pageContext, List<Map<String, Object>> actions, List<String> images, List<String> digest, int stepNo,
            List<Map<String, String>> history, List<String> taskRecords, String hints)
    {
        checkConfig();
        boolean hasImages = images != null && !images.isEmpty();
        String useModel = (hasImages && visionModel != null && !visionModel.isBlank()) ? visionModel : model;
        ArrayNode messages = objectMapper.createArrayNode();
        messages.addObject().put("role", "system").put("content", agentPrompt(page, pageContext));
        appendConversation(messages, history, taskRecords);
        ObjectNode userNode = messages.addObject().put("role", "user");
        String userMessage = agentUserMessage(goal, actions, digest, stepNo, hints);
        if (hasImages)
        {
            ArrayNode arr = userNode.putArray("content");
            arr.addObject().put("type", "text").put("text", userMessage);
            for (String img : images)
            {
                arr.addObject().put("type", "image_url").putObject("image_url").put("url", img);
            }
        }
        else
        {
            userNode.put("content", userMessage);
        }
        String effort = agentReasoning == null || agentReasoning.isBlank() ? "medium" : agentReasoning.trim().toLowerCase();
        return callUpstream(messages, useModel, hasImages, effort)
                .thenApply(this::parseAgentAnswer)
                .exceptionally(ex -> {
                    Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                    if (cause instanceof ServiceException se) throw se;
                    log.warn("AI agent step failed: {}", ex.toString());
                    throw new ServiceException("AI 服务暂时不可用，请稍后重试");
                });
    }

    private void checkConfig()
    {
        if (apiKey == null || apiKey.isBlank() || baseUrl == null || baseUrl.isBlank())
        {
            throw new ServiceException("AI 助手未配置大模型接口，请联系管理员");
        }
    }

    /** 异步调用 OpenAI 兼容 chat/completions，返回 CompletableFuture<message.content> */
    private CompletableFuture<String> callUpstream(ArrayNode messages, String useModel, boolean hasImages, String reasoningEffort)
    {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("model", useModel);
        payload.put("temperature", 0.3);
        payload.putObject("thinking").put("type", "enabled");
        payload.put("reasoning_effort", reasoningEffort == null ? "low" : reasoningEffort);
        if (!hasImages)
        {
            payload.putObject("response_format").put("type", "json_object");
        }
        payload.set("messages", messages);

        String body;
        try
        {
            body = objectMapper.writeValueAsString(payload);
        }
        catch (Exception e)
        {
            return CompletableFuture.failedFuture(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl.replaceAll("/+$", "") + "/chat/completions"))
                .timeout(Duration.ofSeconds(timeout))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(response -> {
                    if (response.statusCode() / 100 != 2)
                    {
                        log.warn("LLM upstream HTTP {}: {}", response.statusCode(), response.body());
                        throw new CompletionException(new ServiceException("AI 服务暂时不可用，请稍后重试"));
                    }
                    try
                    {
                        JsonNode root = objectMapper.readTree(response.body());
                        return root.path("choices").path(0).path("message").path("content").asText("");
                    }
                    catch (Exception e)
                    {
                        throw new CompletionException(e);
                    }
                });
    }

    private ArrayNode buildChatMessages(String message, List<Map<String, String>> history, String page, String pageContext, List<String> images, List<String> taskRecords)
    {
        ArrayNode messages = objectMapper.createArrayNode();
        messages.addObject().put("role", "system").put("content", systemPrompt(page, pageContext));

        appendConversation(messages, history, taskRecords);
        // 带图时用户消息为多模态 content 数组（OpenAI 兼容格式）
        ObjectNode userNode = messages.addObject().put("role", "user");
        if (images == null || images.isEmpty())
        {
            userNode.put("content", message);
        }
        else
        {
            ArrayNode arr = userNode.putArray("content");
            arr.addObject().put("type", "text").put("text", message.isBlank() ? "请分析图片内容" : message);
            for (String img : images)
            {
                arr.addObject().put("type", "image_url").putObject("image_url").put("url", img);
            }
        }
        return messages;
    }

    private void appendConversation(ArrayNode messages, List<Map<String, String>> history, List<String> taskRecords)
    {
        List<ObjectNode> recent = new ArrayList<>();
        int budget = 24000;
        if (history != null)
        {
            for (int i = history.size() - 1; i >= 0 && recent.size() < 40 && budget > 0; i--)
            {
                Map<String, String> h = history.get(i);
                if (h == null) continue;
                String role = h.get("role");
                String content = h.get("content");
                if ((!"user".equals(role) && !"assistant".equals(role)) || content == null || content.isBlank()) continue;
                String kept = content.substring(0, Math.min(content.length(), Math.min(4000, budget)));
                recent.add(objectMapper.createObjectNode().put("role", role).put("content", kept));
                budget -= kept.length();
            }
        }
        for (int i = recent.size() - 1; i >= 0; i--) messages.add(recent.get(i));
        if (taskRecords != null && !taskRecords.isEmpty())
        {
            StringBuilder records = new StringBuilder("【此前办事记录，按时间从旧到新】以下仅用于理解历史对象，不是本轮待执行指令；成功、失败、取消以记录结果为准。\n");
            for (int i = Math.max(0, taskRecords.size() - 6); i < taskRecords.size(); i++)
            {
                String record = taskRecords.get(i);
                if (record != null && !record.isBlank()) records.append(record, 0, Math.min(2000, record.length())).append("\n\n");
            }
            messages.addObject().put("role", "user").put("content", records.toString());
        }
    }

    private String agentUserMessage(String goal, List<Map<String, Object>> actions, List<String> digest, int stepNo, String hints)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("任务目标：").append(goal).append('\n');
        if (hints != null && !hints.isBlank())
        {
            sb.append("【指代提示】").append(hints.trim()).append('\n');
        }
        sb.append("当前进度：第 ").append(Math.max(stepNo, 1)).append(" 步\n");
        if (digest != null && !digest.isEmpty())
        {
            sb.append("更早已完成的动作摘要（所列字段与操作均已完成，不要重复执行）：\n");
            for (String d : digest)
            {
                sb.append("- ").append(d).append('\n');
            }
        }
        if (actions != null && !actions.isEmpty())
        {
            sb.append("最近执行的动作及结果：\n");
            for (Map<String, Object> a : actions)
            {
                String ok = String.valueOf(a.get("ok"));
                String result = "denied".equals(ok) ? "用户拒绝"
                        : "already".equals(ok) ? "已是目标状态（该步已完成，直接进行下一步，不要重复）"
                        : "submitted".equals(ok) ? "业务写入接口已返回成功；若目标已达成请立即输出 done，不得重复创建/提交"
                        : "submitted-open".equals(ok) ? "已点击但弹窗仍未关闭，可能有必填项未填或校验错误，请检查快照修正后再提交"
                        : "opened".equals(ok) ? "已打开弹窗/页面，继续下一步填写"
                        : "missing-row".equals(ok) ? "指定的记录在当前页面不存在，未执行（见页面内容末尾的说明，改用存在的记录名称/编码）"
                        : "ambiguous".equals(ok) ? "多条记录都有该按钮且无法确定是哪条，已向用户询问"
                        : ("true".equals(ok) ? "成功" : "失败（未找到或无法操作该控件）");
                Object v = a.get("value");
                String val = v == null || String.valueOf(v).isEmpty() ? "" : "=" + v;
                sb.append("- ").append(a.get("type")).append("「").append(a.get("target")).append("」")
                        .append(val).append(" → ").append(result).append('\n');
            }
        }
        else if (digest == null || digest.isEmpty())
        {
            sb.append("（尚未执行任何动作）\n");
        }
        sb.append("请输出下一个动作。");
        return sb.toString();
    }

    private String agentPrompt(String page, String pageContext)
    {
        String ctx = "";
        if (pageContext != null && !pageContext.isBlank())
        {
            ctx = """

                【当前页面实时内容】（用户屏幕的真实快照，含表格数据、统计数字、表单值）
                %s
                """.formatted(pageContext);
        }
        return """
                你是"智慧医疗 HIS 系统"内置的操作代理（Agent），可以代用户在系统页面上完成操作任务。
                工作方式：每轮你会收到【任务目标】【当前页面快照】【已执行动作及结果】，你只输出【一个】下一步动作；前端执行后会把新快照和新结果给你，如此循环直到目标完成。

                【输出协议】严格输出 JSON，不得输出 markdown 代码块或任何多余文字：
                {"say":"给用户看的进展或具体问题","action":{"type":"动作类型","target":"目标","value":"值","row":"记录标识(可选)"},"done":false}

                【动作类型】
                - navigate：跳转到菜单页面，target 填下方菜单表中的 path 值
                - click：点击按钮/页签/链接/菜单项，target 为快照中真实可见的文字
                  · 列表/卡片里每条记录都有的按钮（删除、编辑、详情等）必须用 row 指明是哪条记录，row 填快照中该记录的名称或编码，如 {"type":"click","target":"删除","row":"康复医学科"}；不带 row 的行级按钮无法执行
                - input：向输入框/文本域填值，target 为表单项 label 或占位提示，value 为填写内容
                - select：下拉框选择，target 为表单项 label，value 为选项文字
                - check：勾选/取消复选框，target 为复选框文字，value 为 true/false
                - wait：等待页面加载（点击后弹窗尚未出现时）
                - ask：缺少必要信息需要向用户反问（本轮任务结束，用户回复后会带着补充信息继续）；ask 时把所有必填项一次性问清（如"供应商选哪个？加哪些药品、各多少数量？"），不要一次只问一个
                - done：目标完成，action 为 {"type":"done"}，say 写完成总结——必须写清本次新建/修改记录的关键标识（单据编号、患者或药品名称、数量、金额等），让用户能据此查到具体记录；若任务含统计/分析要求，say 中给出基于快照数据的完整结论（可较长）
                - fail：确认无法完成，say 写原因

                【规则】
                - 每轮严格只输出一个动作，不要一次输出多个动作，不要预测尚未出现的页面内容
                - 若首轮附带了截图，截图中展示的按钮、页签、内容就是用户所指的对象，结合快照中的"页签/页面按钮"定位执行
                - target 必须取自快照中真实出现的文字或菜单表 path，禁止编造；target 只写控件本身的文字（如"住院登记"），不要加"按钮/输入框/页面顶部"等修饰词
                - 如果已在目标页面（快照显示该页内容），不要再 navigate 或点击同名菜单，直接操作页面上的按钮/表单推进任务
                - 找不到目标控件时先 wait 或换个说法重试，连续失败则 fail 说明原因
                - 【更早已完成的动作摘要】中列出的字段与操作已完成，不要重复填写或重复点击；如需其中的填写值，直接沿用摘要中的内容
                - 表单类任务：逐字段 input/select 填写，最后 click 提交/确认按钮完成
                - 明细表格行内的字段（如"数量""单价""药品"列）同样用 input/select 填写，target 直接填列名，不要先 click 单元格或输入框
                - 明细表格有多行时，target 用"列名@行号"指定行（如"药品@2"表示第 2 行的药品列），行号对应快照中表格行从上到下从 1 开始；不指定时 select 自动选该列第一个空行、input 自动写正在编辑的行。逐行填写：先选完一行再继续下一行
                - 创建/提交类动作成功后，前端会等待业务接口返回成功；若用户目标已达成立即输出 done，不要再次点击新建/提交。只有收到失败反馈才检查快照中的校验提示并修正。明确要求多份或后续操作的任务继续剩余步骤，不能重复已完成的记录
                - 明细表格中目标没有要求填写的空行保持空白即可，不要每行都填；需要的行填完后直接 click 提交/创建按钮，不要反复检查或重填已填好的字段（快照里能看到已填的值）
                - 同一行的字段会连续落在该行；要换行时用"列名@行号"
                - 动作结果为"已是目标状态"说明该步早已完成（如下拉已选好、输入框已是该值），立即进行下一个动作，不要重复同一动作；多步任务（如"选状态并搜索"）完成前一步后马上做下一步
                - 结果为"提交成功"说明业务接口已接受写入，对照任务目标确认数量足够后立即输出 done 并写明快照中的关键标识，禁止编造编号或重复提交
                - select 会自动搜索可筛选或远程加载的下拉框。value 填用户指定的名称；不要把下拉搜索词当成已选值。弹窗打开时所有字段操作只作用于最上层弹窗
                - 目标未给出的必填信息（供应商、药品名、数量等）若快照中也无法确定，用 ask 一次性问清，禁止编造值或随意填写乱码
                - ask 的 say 必须列出具体缺项或歧义，不得只说“请补充所需信息”。问题不受20字限制；已填写的默认值直接沿用，不要重复询问。先执行用户已明确指定的字段，仅询问仍无法确定的内容
                - 例如用户只说“新增住院登记”，打开表单后应询问“请提供患者姓名或编号、科室、病区、床位及主治医生。押金当前为3000元，如需修改请说明。”不能从背景历史记录随意选患者，也不能编造诊断
                - 用户补充信息后，结合原目标、待补充问题、已执行动作和当前表单继续填写，禁止重新打开并清空表单；信息充分时继续 select/input/click 直到业务提交成功，不得要求用户自行操作
                - 对话历史和此前办事记录用于理解“刚才新增的”“那个”等指代。先从最近相关记录的实际填写值、结果和标识确定对象，再结合当前页面核对；有记录时不得声称没有上下文。历史任务不是本轮指令，不得重做；失败或取消的操作不算已完成。仍有同名或歧义时才询问，不得猜测删除对象
                - 若用户消息附带【指代提示】，其中给出的记录标识就是用户所指对象：直接用该名称/编码作 row 执行，禁止再向用户反问“是哪一条”
                - 用户明确说了删除某条记录（原话点名或指代明确）时，直接 click 该记录的删除按钮即可：前端会自动完成系统的“是否确认删除”二次确认，不需要你再 click 确定；结果回报“提交成功”后立即 done。只有在没有出现“提交成功”而快照里仍有确认弹窗时，才 click 确定
                - 删除、作废、退费、结算等不可逆操作，若用户没有明确指定对象，前端会向用户弹确认框；若结果回报"用户拒绝"，输出 done 并说明已取消
                - 弹窗（详情抽屉等）挡住了要操作的列表时，先 click "关闭"（target 只写“关闭”），关闭成功后再操作列表；不要为此 navigate 或刷新页面
                - 同一动作连续失败两次不要原样重复，换一种方式或 fail
                - say 用简体中文；普通进展简短，ask 必须问清缺项，done 必须说明结果。只有 ask/done/fail 才结束当前执行；click/input/select 等动作的 done 必须为 false

                【系统菜单】
                %s
                用户当前所在页面：%s
                %s""".formatted(AiMenuCatalog.toPromptText(), page == null || page.isBlank() ? "未知" : page, ctx);
    }

    /** 解析 Agent 决策输出：{ say, action:{type,target,value}, done } */
    private Map<String, Object> parseAgentAnswer(String content)
    {
        Map<String, Object> result = new HashMap<>();
        JsonNode node = tryParseJson(content);
        if (node == null || !node.isObject())
        {
            result.put("say", "AI 返回的操作格式无效，本次未执行，请重试");
            result.put("action", Map.of("type", "fail"));
            result.put("done", true);
            return result;
        }
        result.put("say", node.path("say").asText(""));

        Map<String, Object> action = new HashMap<>();
        JsonNode a = node.path("action");
        boolean doneFlag = node.path("done").asBoolean(false);
        String type = a.isObject() ? a.path("type").asText("") : "";
        // 模型只回了进展没给动作且未声明完成：按 wait 继续，而不是误判为任务结束
        if (type.isEmpty())
        {
            type = doneFlag ? "done" : "wait";
        }
        action.put("type", type);
        action.put("target", a.isObject() ? a.path("target").asText("") : "");
        // 行级按钮所属记录（名称/编码），前端据此在列表/卡片中定位到具体那一条
        String row = a.isObject() ? a.path("row").asText("").trim() : "";
        if (!row.isEmpty())
        {
            action.put("row", row.length() > 60 ? row.substring(0, 60) : row);
        }
        if (a.isObject() && a.has("value") && !a.path("value").isNull())
        {
            JsonNode v = a.path("value");
            action.put("value", v.isBoolean() ? v.asBoolean() : v.asText(""));
        }
        // Some providers put the full clarification in action.value and only a short status in say.
        if ("ask".equals(type) && a.path("value").isTextual() && !a.path("value").asText().isBlank())
        {
            result.put("say", a.path("value").asText().trim());
        }
        // 跳转路径白名单校验，防止编造
        if ("navigate".equals(type) && AiMenuCatalog.of(String.valueOf(action.get("target"))) == null)
        {
            action.put("type", "fail");
        }
        result.put("action", action);
        String finalType = String.valueOf(action.get("type"));
        result.put("done", "done".equals(finalType) || "fail".equals(finalType) || "ask".equals(finalType));
        return result;
    }

    private String systemPrompt(String page, String pageContext)
    {
        String ctx = "";
        if (pageContext != null && !pageContext.isBlank())
        {
            ctx = """

                【当前页面实时内容】（用户屏幕的真实快照，含表格数据、统计数字、表单值）
                %s
                要求：用户让分析页面/数据，或问题与当前页面相关时，必须结合以上真实内容作答——引用具体数字与记录、指出异常或待办、给出建议，不要泛泛而谈；若输出 steps，target 优先使用快照中真实出现的按钮或控件文字。
                """.formatted(pageContext);
        }
        return """
                你是"智慧医疗 HIS 系统"内置的 AI 助手，服务对象是医院工作人员（挂号员、医生、护士、药师、系统管理员）。

                【你的能力】
                1. 用简体中文回答用户关于医院业务流程、系统使用方法、基础医疗知识的问题，回答简洁、条理清晰，可用编号列表。
                2. 结合"当前页面实时内容"分析用户正在看的页面：解读数据、指出异常或待办事项、给出可操作建议。分析类回答按"页面用途 → 数据解读 → 问题与建议"组织，引用具体数字。
                3. 当用户明确要完成某项操作时，输出页面导航与操作引导，前端会自动打开对应菜单并高亮目标控件。
                4. 用户可能附带截图（报错弹窗、单据、界面等），需结合图片内容理解其需求并作答。

                【引导触发规则——严格遵守】
                - 仅当用户消息明确表达"要去做某个操作/需要操作指引"，或要求你直接操作页面控件时才输出 navigate 和 steps，例如：怎么做、如何操作、在哪里、带我去、演示一下、教我、帮我、指引我，以及"点击中成药""切换到库存预警""打开某弹窗"等直接操作指令。
                - 用户让你操作当前页面控件（点击、切换、勾选等）时："navigate":null，steps 的 target 必须使用"当前页面实时内容"中真实出现的页签/按钮/控件文字，answer 只需一句确认（如"好的，已为您切换到中成药页签"）。
                - 以下情况一律输出 "navigate":null,"steps":[]：概念咨询、业务规则解释、闲聊、追问、介绍能力（"你会什么/还有哪些可以引导"）、页面或数据分析请求。
                - 拿不准时只输出 answer，用文字说明即可（可包含操作路径文字），不要输出引导字段。

                【输出协议】必须严格输出 JSON，不得输出 markdown 代码块或任何额外文字：
                {"answer":"给用户的回答文本","navigate":{"path":"页面路径","menuText":"菜单名称","group":"菜单分组"},"steps":[{"target":"页面上真实可见的按钮或控件文字","title":"步骤标题","content":"操作说明"}]}
                - 无需引导时："navigate":null，"steps":[]
                - navigate.path 只能使用下方菜单表中的值，禁止编造。
                - steps 的 target 必须来自该页面"页面按钮"中的文字，或"当前页面实时内容"里真实出现的控件文字；前端会按文字查找并高亮。弹窗内的按钮（如"确认挂号"）也可作为 target。
                - steps 数量 1~6 步，第一步通常是进入功能的入口按钮（如"门诊挂号"）。
                - answer 中可用一两句话概述操作路径（如：门诊管理 → 挂号管理 → 门诊挂号）。
                - 医疗/健康类回答末尾加上"（仅供参考，具体诊疗请遵医嘱）"。

                【系统菜单】
                %s
                用户当前所在页面：%s
                %s""".formatted(AiMenuCatalog.toPromptText(), page == null || page.isBlank() ? "未知" : page, ctx);
    }

    /** 解析模型输出：容错处理非 JSON / 路径白名单校验 */
    private Map<String, Object> parseAnswer(String content)
    {
        Map<String, Object> result = new HashMap<>();
        JsonNode node = tryParseJson(content);
        if (node == null || !node.isObject())
        {
            result.put("answer", content);
            result.put("navigate", null);
            result.put("steps", List.of());
            return result;
        }

        result.put("answer", node.path("answer").asText(content));

        Object navigate = null;
        JsonNode nav = node.path("navigate");
        if (nav.isObject())
        {
            String path = nav.path("path").asText("");
            MenuInfo info = AiMenuCatalog.of(path);
            if (info != null)
            {
                Map<String, String> n = new HashMap<>();
                n.put("path", info.path());
                String menuText = nav.path("menuText").asText("");
                n.put("menuText", menuText.isBlank() ? info.title() : menuText);
                String group = nav.path("group").asText("");
                n.put("group", group.isBlank() ? info.group() : group);
                navigate = n;
            }
        }
        result.put("navigate", navigate);

        List<Map<String, String>> steps = new ArrayList<>();
        JsonNode stepsNode = node.path("steps");
        if (stepsNode.isArray())
        {
            for (JsonNode s : stepsNode)
            {
                String target = s.path("target").asText("").trim();
                if (target.isEmpty() || steps.size() >= 8)
                {
                    continue;
                }
                Map<String, String> step = new HashMap<>();
                step.put("target", target);
                step.put("title", s.path("title").asText(target));
                step.put("content", s.path("content").asText(""));
                steps.add(step);
            }
        }
        result.put("steps", steps);
        return result;
    }

    private JsonNode tryParseJson(String content)
    {
        if (content == null || content.isBlank())
        {
            return null;
        }
        String text = content.trim();
        // 去掉可能的 ```json ``` 代码围栏
        if (text.startsWith("```"))
        {
            text = text.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceFirst("\\s*```$", "");
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start)
        {
            return null;
        }
        try
        {
            return objectMapper.readTree(text.substring(start, end + 1));
        }
        catch (Exception e)
        {
            log.warn("AI answer JSON parse failed, raw: {}", content);
            return null;
        }
    }
}
