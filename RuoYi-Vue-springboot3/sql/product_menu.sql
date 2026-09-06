-- ----------------------------
-- 商品管理菜单（练习用）
-- 【执行方式】在 DBeaver 中连接 ry-vue 数据库，手动执行本脚本
-- 【说明】
--   - menu_id 用 2100 起避免与若依默认菜单(1~1061)冲突
--   - 若 sys_menu 已有 2100+ 的记录，请先查询确认再调整 ID
--   - 组件路径 demo/product/index 对应前端文件
--     RuoYi-Vue3-master/src/views/demo/product/index.vue
-- ----------------------------

-- 菜单（C 类型：页面菜单，左侧导航可见）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2100, '商品管理', 0, 5, 'product', 'demo/product/index', '', '', 1, 0, 'C', '0', '0', 'demo:product:list', 'shopping', 'admin', sysdate(), '', null, '商品管理菜单');

-- 按钮（F 类型：功能权限，控制页面上的按钮显隐）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2101, '商品查询', 2100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'demo:product:query',  '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2102, '商品新增', 2100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'demo:product:add',    '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2103, '商品修改', 2100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'demo:product:edit',   '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2104, '商品删除', 2100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'demo:product:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2105, '商品导出', 2100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'demo:product:export', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 注意：admin 是超级管理员，默认拥有所有权限，无需再分配角色。
-- 若要用其他账号访问，需在【系统管理-角色管理】中勾选商品相关权限。
-- ----------------------------
