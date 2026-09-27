package com.ruoyi;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * web容器中进行部署
 * <p>
 * 当项目打包为 war 包并部署到外部 Servlet 容器（如 Tomcat）时使用。
 * 外部容器启动时不会执行 {@link RuoYiApplication#main} 方法，
 * 而是通过 Servlet 3.0 的 ServletContainerInitializer 机制找到
 * {@link SpringBootServletInitializer} 的子类，并调用 {@link #configure} 来引导 Spring Boot 应用。
 * <p>
 * 若使用内嵌容器（直接运行 jar 包或 main 方法），则此类不会生效。
 * 
 * @author ruoyi
 */
public class RuoYiServletInitializer extends SpringBootServletInitializer
{
    /**
     * 配置 Spring Boot 应用的启动源
     *
     * @param application Spring 应用构建器，由外部 Servlet 容器启动时传入
     * @return 指定了主启动类 {@link RuoYiApplication} 的应用构建器
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        // 指定主配置类，使外部容器能按 RuoYiApplication 上的注解（自动配置、组件扫描等）加载应用
        return application.sources(RuoYiApplication.class);
    }
}
