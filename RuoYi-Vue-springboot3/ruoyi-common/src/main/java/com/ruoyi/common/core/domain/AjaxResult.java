package com.ruoyi.common.core.domain;

import java.util.HashMap;
import java.util.Objects;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.StringUtils;

/**
 * 操作消息提醒（统一返回结果封装类）
 *
 * 【这个类是干什么的？】
 * 后端每个接口返回给前端的数据，格式必须统一，前端才能用同一套代码处理。
 * 若依约定的返回 JSON 格式如下：
 * {
 *     "code": 200,          // 状态码：200 成功，500 失败，401 未登录，601 警告
 *     "msg": "操作成功",     // 提示消息，前端拿到后直接弹窗显示给用户
 *     "data": {...}         // 具体数据，可以是对象、集合，也可以没有
 * }
 *
 * 【为什么继承 HashMap？】
 * 因为返回的 JSON 本质就是"键值对"（code/msg/data 三个键）。
 * 继承 HashMap 后，AjaxResult 本身就是一个 Map，
 * Spring MVC 在把返回值序列化成 JSON 时会直接输出 Map 里的键值对。
 * 这样就不需要定义 code/msg/data 三个字段再写 getter/setter，写法更简洁。
 *
 * 【在 Controller 里怎么用？】
 *     return AjaxResult.success(userList);       // 成功并携带数据
 *     return AjaxResult.error("用户名已存在");    // 失败并携带提示
 *
 * 【设计模式小知识】
 * 这种类叫做 DTO（Data Transfer Object，数据传输对象），
 * 下面的 success()/error() 则是典型的"静态工厂方法"模式。
 * 
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object>
{
    /**
     * 序列化版本号：类实现 Serializable（可序列化）接口时用于版本校验，
     * 固定写 1L 是惯例，不参与业务逻辑
     */
    private static final long serialVersionUID = 1L;

    // 下面三个常量是 JSON 里的"键名"，统一用常量而不是直接写 "code" 这样的字符串，
    // 好处：将来键名要改只需改这一处，且写错会有编译提示（魔法字符串写错不会有任何报错）

    /** 状态码：JSON 中的 "code" 键 */
    public static final String CODE_TAG = "code";

    /** 返回内容（提示消息）：JSON 中的 "msg" 键 */
    public static final String MSG_TAG = "msg";

    /** 数据对象：JSON 中的 "data" 键 */
    public static final String DATA_TAG = "data";

    /**
     * 无参构造：创建一个空的 AjaxResult（Map 里什么键都没有）。
     * 一般不直接用，都是通过下面的 success()/error() 静态方法创建。
     */
    public AjaxResult()
    {
    }

    /**
     * 两参构造：只放 code 和 msg 两个键，不带数据
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg)
    {
        // super.put 调用的是父类 HashMap 的 put 方法，即往 Map 里存键值对
        // 等价于 map.put("code", code)
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 三参构造：完整版，code + msg + data 三个键都放
     * 注意：只有 data 不为 null 时才放 "data" 键，
     * 所以无数据的返回结果 JSON 里根本不会有 "data" 这个键（而不是出现 "data": null）
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        // StringUtils.isNotNull 是若依封装的判空工具，比 != null 判断得更全面
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    // ==================== success()：成功结果的一组静态工厂方法 ====================
    // 所谓"静态工厂方法"：不通过 new 创建对象，而是 类名.方法名() 直接调用，如 AjaxResult.success()
    // 好处：方法名见名知意，调用方不用关心 code 到底该传 200 还是 500
    // 下面有 4 个重载版本（方法名相同、参数列表不同），
    // 层层调用，最终都汇聚到参数最全的 success(String msg, Object data) 这一个真正创建对象的入口

    /**
     * 返回成功消息（无参版）：提示固定为"操作成功"，不带数据。
     * 常用于新增/修改/删除操作成功后的返回：return AjaxResult.success();
     * 
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        // 调用带 msg 参数的重载版本，层层往下传
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据（只带数据版）：提示固定为"操作成功"。
     * 常用于查询接口：return AjaxResult.success(userList);
     * 
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息（只带自定义提示版）
     * 
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息（完整版）：code 固定为 200（HttpStatus.SUCCESS）。
     * 前面的重载最终都调用这里，真正创建 AjaxResult 对象的地方只有这一个
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回警告消息：code 为 601（HttpStatus.WARN）。
     * 前端收到 601 会以黄色警告样式弹窗（区别于 500 的红色错误样式）
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult warn(String msg)
    {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 返回警告消息（带数据版）
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult warn(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.WARN, msg, data);
    }

    // ==================== error()：失败结果的一组静态工厂方法 ====================
    // 和 success() 同样的套路：4 个重载，最终都汇聚到下面真正创建对象的地方
    // 注意：error() 有两个"终点"—— 一个 code 固定 500，另一个 code 由调用方自己传

    /**
     * 返回错误消息（无参版）：提示固定为"操作失败"，code 固定 500
     * 
     * @return 错误消息
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息（只带提示版）：code 固定 500。
     * 最常用，比如：return AjaxResult.error("验证码错误");
     * 
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息（带提示和数据版）：code 固定 500
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息（自定义状态码版）：code 由调用方指定。
     * 常用于特定业务码，比如 401 未登录：AjaxResult.error(HttpStatus.UNAUTHORIZED, "登录状态已过期")
     * 全局异常处理器 GlobalExceptionHandler 里就大量用到这个重载
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }

    // ==================== 三个判断方法：给 Java 代码内部用的 ====================
    // 比如拦截器里判断结果：if (ajaxResult.isSuccess()) { ... }
    // 注意：前端判断成功用的是 code == 200，这里的判断逻辑和它保持一致

    /**
     * 是否为成功消息（code 是否等于 200）
     * Objects.equals(a, b) 是 JDK 工具：先判空再比较，避免空指针异常
     * 等价于 Integer.valueOf(HttpStatus.SUCCESS).equals(this.get(CODE_TAG)) 但写法更安全
     *
     * @return 结果
     */
    public boolean isSuccess()
    {
        return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG));
    }

    /**
     * 是否为警告消息（code 是否等于 601）
     *
     * @return 结果
     */
    public boolean isWarn()
    {
        return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG));
    }

    /**
     * 是否为错误消息（code 是否等于 500）
     *
     * @return 结果
     */
    public boolean isError()
    {
        return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG));
    }

    /**
     * 重写父类 HashMap 的 put 方法，方便链式调用
     *
     * 【为什么要重写？】
     * 父类 HashMap.put 的返回值是 Object（旧位置上的值），没法继续点下去；
     * 这里改成返回 AjaxResult 自身（this），就可以一行连着写：
     *     AjaxResult.success().put("total", 100).put("rows", list);
     * 这种"返回自身"的写法叫链式调用（方法链）。
     *
     * 【实际使用场景】
     * 分页查询时常用来往结果里附加额外数据，比如：
     *     return AjaxResult.success(list).put("count", total);
     * 返回的 JSON 就是 {"code":200, "msg":"操作成功", "data":..., "count":...}
     *
     * @param key 键
     * @param value 值
     * @return 数据对象（返回 this 自身，支持链式调用）
     */
    @Override
    public AjaxResult put(String key, Object value)
    {
        // 先正常往 Map 里存键值对
        super.put(key, value);
        // 再把自身返回出去，这样调用方就能继续 .put(...) 连着写
        return this;
    }
}
