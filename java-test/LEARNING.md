# Java 后端快速学习路线（对着敲版）

> 本项目已剥离所有业务代码，只保留 RuoYi 框架骨架 + 一个商品 CRUD 练习模板。
> 学习方法：**不要看，要敲**。每个文件对着原项目（`../RuoYi-Vue-springboot3`）手敲一遍，敲完就懂了。

---

## 一、项目结构（先花 30 分钟看懂）

```
java-test/
├── ruoyi-admin/          # 启动模块（main方法、配置文件、Controller入口）
│   └── src/main/java/com/ruoyi/
│       ├── RuoYiApplication.java   # ★ 启动类（SpringBoot入口）
│       └── web/controller/
│           ├── common/              # 框架级：验证码、文件上传
│           ├── system/SysLoginController.java  # 框架级：登录
│           └── demo/ProductController.java     # ◆ 练习模板（从这里开始敲）
│
├── ruoyi-framework/      # 框架核心（Security、拦截器、数据源、Redis、异常处理）
│   └── config/SecurityConfig.java   # ★ 安全配置（重点理解）
│
├── ruoyi-system/         # 系统业务（认证依赖，保留；你写的业务也放这里）
│   ├── domain/           # 实体类
│   ├── mapper/           # Mapper接口
│   ├── service/          # Service接口
│   └── service/impl/     # Service实现
│
├── ruoyi-common/         # 通用工具（所有模块依赖）
│   └── core/
│       ├── controller/BaseController.java    # ★ Controller基类（分页、响应封装）
│       └── domain/
│           ├── BaseEntity.java               # ★ 实体基类（公共字段）
│           ├── AjaxResult.java               # ★ 统一响应体
│           └── entity/SysUser.java           # 用户实体（框架依赖）
│
└── sql/                  # 数据库脚本
    └── product.sql       # ◆ 练习用建表语句
```

### 分层调用链（必须背下来）
```
浏览器请求
  → Controller（接收请求、参数校验、调用Service）
    → Service接口 → ServiceImpl（业务逻辑、事务、调用Mapper）
      → Mapper接口 → Mapper.xml（SQL）
        → MySQL数据库
```

---

## 二、7天快速学习计划

### Day 1：跑起来 + 看懂启动流程（2小时）

**目标**：项目能启动，理解 SpringBoot 启动机制

1. 用 IDEA 打开 `java-test` 文件夹，等待 Maven 依赖下载完成
2. 配置数据库：修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库连接
3. 导入 `sql/` 下的 RuoYi 基础数据库脚本（从原项目复制）
4. 启动 Redis（本地默认 6379，无密码）
5. 运行 `RuoYiApplication.java` 的 main 方法
6. 浏览器访问 `http://localhost:8081/swagger-ui.html` 看接口文档

**对着敲练习**：
- 打开 `RuoYiApplication.java`，逐行理解注解含义（@SpringBootApplication）
- 打开 `application.yml`，理解每个配置块的作用

---

### Day 2-3：Controller 层（最核心，前端最容易理解）（每天3小时）

**目标**：能独立写出 RESTful CRUD Controller

**学习顺序**（从简单到复杂）：
1. **先看练习模板**：`ruoyi-admin/web/controller/demo/ProductController.java`
   - 这是完整的 CRUD Controller，逐行读注释
2. **对照原项目敲**：打开原项目 `ruoyi-admin/web/controller/system/SysPostController.java`
   - 新建一个空文件，对着原文件手敲一遍（不要复制粘贴！）
   - 敲完对比，理解每个注解：@RestController、@RequestMapping、@GetMapping、@PostMapping、@PutMapping、@DeleteMapping、@PreAuthorize、@Log、@RequestBody、@PathVariable
3. **自己写一个**：在 `demo` 包下新建 `CategoryController.java`（分类管理），不看参考，凭记忆写 CRUD

**必须掌握的知识点**：
| 概念 | 说明 | 对应前端类比 |
|------|------|-------------|
| @RestController | = @Controller + @ResponseBody，返回JSON | 类似 axios 响应拦截器统一处理 |
| @RequestMapping | 定义URL基础路径 | 类似 Vue Router 的父路由 |
| @GetMapping/@PostMapping | HTTP方法映射 | 类似 axios.get()/post() |
| @RequestBody | 接收JSON请求体 | 类似前端传的 data |
| @PathVariable | URL路径参数 /user/1 | 类似 Vue Router 的动态路由 :id |
| @RequestParam | 查询参数 ?name=xxx | 类似前端传的 params |
| BaseController | 基类，含分页/响应封装 | 类似你封装的通用 request.js |
| AjaxResult | 统一响应体 {code, msg, data} | 类似前端统一的响应格式 |
| TableDataInfo | 分页响应 {total, rows, code, msg} | 类似前端表格组件的数据格式 |
| startPage() | 启动分页（PageHelper） | 类似前端传 pageNum/pageSize |
| @PreAuthorize | 权限控制 | 类似前端的路由守卫/按钮权限 |
| @Log | 操作日志AOP | 类似前端的埋点上报 |

---

### Day 4-5：Service 层（每天3小时）

**目标**：理解业务逻辑层，掌握事务管理

**学习顺序**：
1. 看练习模板：`ruoyi-system/service/IProductService.java` + `impl/ProductServiceImpl.java`
2. 对照原项目敲：`ruoyi-system/service/ISysPostService.java` + `impl/SysPostServiceImpl.java`
3. 进阶：看 `SysUserServiceImpl.java`（最复杂的业务，含权限、缓存、事务）
4. 自己写：`ICategoryService` + `CategoryServiceImpl`

**必须掌握的知识点**：
- @Service 注解：标记为 Spring 管理的 Bean
- @Autowired：依赖注入（类似前端的 import 后使用，但 Spring 自动实例化）
- 接口 + 实现类分离：面向接口编程（解耦、方便AOP代理）
- @Transactional：事务管理（出错自动回滚）
- 业务逻辑写在 Service，不写在 Controller（Controller 只做参数接收和响应）

---

### Day 6-7：Mapper / MyBatis（每天3小时）

**目标**：掌握 MyBatis SQL 映射，能写复杂查询

**学习顺序**：
1. 看练习模板：`ruoyi-system/mapper/ProductMapper.java` + `resources/mapper/system/ProductMapper.xml`
2. 对照原项目敲：`SysPostMapper.java` + `SysPostMapper.xml`
3. 进阶：看 `SysUserMapper.xml`（含多表关联查询）
4. 自己写：`CategoryMapper.java` + `CategoryMapper.xml`

**必须掌握的知识点**：
| 概念 | 说明 |
|------|------|
| namespace | XML中必须与Mapper接口全限定名一致 |
| resultMap | 数据库列名 → Java属性映射（驼峰命名转换） |
| #{} | 预编译参数（防SQL注入，推荐） |
| ${} | 字符串直接拼接（有注入风险，仅用于排序字段等） |
| \<if test=""> | 动态SQL条件判断 |
| \<where> | 自动处理WHERE关键字和AND/OR |
| \<foreach> | 循环（批量操作、IN查询） |
| \<trim> | 去除多余的前缀/后缀/逗号 |
| useGeneratedKeys | 自增主键回填 |

---

## 三、框架原理（第二阶段，1-2周）

CRUD 熟练后，再深入框架原理。按这个顺序看：

1. **统一异常处理**：`ruoyi-framework/web/exception/GlobalExceptionHandler.java`
   - 类似前端的 axios 响应拦截器统一处理错误
2. **AOP 日志**：`ruoyi-framework/aspectj/LogAspect.java` + `ruoyi-common/annotation/Log.java`
   - 类似前端的拦截器/中间件
3. **Security 认证流程**：
   - `SecurityConfig.java`（配置哪些URL需要登录）
   - `JwtAuthenticationTokenFilter.java`（JWT Token 验证过滤器）
   - `SysLoginController.java`（登录入口）
   - `TokenService.java`（Token生成/验证）
   - `UserDetailsServiceImpl.java`（用户信息加载）
4. **Redis 缓存**：`ruoyi-common/core/redis/RedisCache.java`
5. **数据源配置**：`ruoyi-framework/datasource/` + `application-druid.yml`
6. **拦截器**：`ruoyi-framework/interceptor/RepeatableFilter.java`（防重复提交）

---

## 四、练习清单（打勾完成）

### 基础练习（必做）
- [ ] 手敲 ProductController.java 一遍（不看参考，写完对比）
- [ ] 手敲 ProductServiceImpl.java 一遍
- [ ] 手敲 ProductMapper.xml 一遍
- [ ] 新建 Category 模块（domain/mapper/service/controller/xml/sql），完整CRUD
- [ ] 给 Category 加导出功能（ExcelUtil）
- [ ] 给 Category 加数据权限（@DataScope）

### 进阶练习（选做）
- [ ] 实现一个一对多关联查询（如商品+商品图片）
- [ ] 实现批量导入（ExcelUtil 导入）
- [ ] 给某个接口加 Redis 缓存
- [ ] 自定义一个注解 + AOP 实现耗时统计

---

## 五、常见问题

**Q: 启动报数据库连接失败？**
A: 检查 application-druid.yml 中的 url/username/password，确保 MySQL 已启动且数据库已创建。

**Q: 启动报 Redis 连接失败？**
A: 确保本地 Redis 已启动（默认 6379 端口，无密码）。或修改 application.yml 中的 Redis 配置。

**Q: 接口返回 401 未授权？**
A: 除了 /login、/captchaImage 等白名单接口，其他接口都需要在请求头带 Authorization: Bearer {token}。先调用登录接口获取 token。

**Q: @PreAuthorize 权限不生效？**
A: 需要在数据库 sys_menu 表中配置对应的权限标识（如 demo:product:list），并给角色分配该菜单权限。开发阶段可以先去掉 @PreAuthorize 注解。

**Q: 原项目代码在哪？**
A: `../RuoYi-Vue-springboot3/` —— 这是完整的原版 RuoYi，所有业务代码都在里面，对着敲时参考它。
