package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
// @SpringBootApplication 是 Spring Boot 的核心注解，它是一个组合注解，包含三个功能：
//   1. @Configuration：标识该类是一个配置类，可以向 Spring 容器中注册 Bean
//   2. @EnableAutoConfiguration：开启自动配置，Spring Boot 会根据 pom.xml 中引入的依赖
//      自动装配对应的组件（例如引入了 spring-boot-starter-web 就自动配置 Tomcat、SpringMVC）
//   3. @ComponentScan：自动扫描当前类所在包（com.ruoyi）及其所有子包下的
//      @Component、@Service、@Controller、@Repository 等注解标注的类，并注册为 Bean
//
// exclude = { DataSourceAutoConfiguration.class } 表示排除"数据源自动配置"：
//   若依框架使用了自定义的多数据源方案（Druid 连接池 + 多数据源动态切换），
//   数据源是由 DruidConfig 配置类手动创建的，如果这里不排除自动配置，
//   Spring Boot 会尝试用 application.yml 中的 spring.datasource 配置自动创建数据源，
//   两者会冲突导致启动报错，所以这里必须排除掉
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    /**
     * 程序入口 main 方法，整个后端项目从这里开始运行
     *
     * @param args 启动时传入的命令行参数，例如：--server.port=8080 可以临时覆盖端口
     */
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        // 上面这行被注释掉的代码作用是：关闭 spring-boot-devtools 的热部署自动重启功能
        // 默认情况下引入了 devtools 依赖后，修改代码保存会自动重启应用，不需要时可打开这行

        // SpringApplication.run() 是启动 Spring Boot 应用的核心方法，它会完成以下工作：
        //   1. 创建 Spring 应用上下文（IOC 容器）
        //   2. 加载所有自动配置和扫描到的 Bean（Controller、Service、Mapper 等）
        //   3. 读取 application.yml 配置文件
        //   4. 启动内嵌的 Tomcat 服务器（默认端口 8080）
        //   5. 整个启动过程是阻塞的，启动完成后 main 线程继续往下执行打印下面的图案
        SpringApplication.run(RuoYiApplication.class, args);

        // 启动成功后，在控制台打印 ASCII 字符画和提示语
        // \n 表示换行，+ 是字符串拼接，打印出来是一个 "RuoYi" 艺术字图案
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
