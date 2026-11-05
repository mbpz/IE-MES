# 静态功能基线

所有条目状态：待运行验证。路由包括源码中的声明，需进一步排除注释并结合类级路径。

## src/main/java/com/iemes/controller/base/BaseResponse.java


## src/main/java/com/iemes/controller/base/ConstantController.java


## src/main/java/com/iemes/controller/base/baseExtController.java

- `"/base/")`
- `"getOperation")`
- `"getOperation2")`
- `"getResource")`
- `"getPodFunction")`
- `"getWorkOrder")`
- `"getUnFinishedWorkOrder")`
- `"getfWorkCenter")`
- `"getfWorkCenterLine")`
- `"getfWorkCenterChild")`
- `"getItemBom")`
- `"getItem")`
- `"getItemChild")`
- `"getItemMain")`
- `"getRoute")`
- `"getNcCodeGroup")`
- `"getNcCode")`
- `"getSite")`
- `"getSfc")`
- `"getExamInfoList")`
- `"getSfcByAssemble")`

## src/main/java/com/iemes/controller/index/BackgroundController.java

- `"/")`
- `value = "login", method = RequestMethod.GET, produces = "text/html; charset=utf-8")`
- `value = "login", method = RequestMethod.POST, produces = "text/html; charset=utf-8")`
- `"index")`
- `"menu")`
- `"findAuthority")`
- `"download")`
- `value = "logout", method = RequestMethod.GET)`
- `"install")`

## src/main/java/com/iemes/controller/index/BaseController.java


## src/main/java/com/iemes/controller/index/InitController.java


## src/main/java/com/iemes/controller/inventory/WorkshopInventoryController.java

- `"/workshop_inventory/")`
- `"workshop_inventory_list")`
- `"workshop_inventory_findByPage")`
- `"workshop_inventory_addUI")`
- `"workshop_inventory_addEntity")`
- `"workshop_inventory_getItemInfo")`
- `"workshop_inventory_getItemInfo2")`

## src/main/java/com/iemes/controller/kanban/KanbanController.java

- `"/kanban/")`
- `"kanbanUI")`
- `"getMaterialConsumptionDataFindByPage")`

## src/main/java/com/iemes/controller/plan/DeliverNumberController.java

- `"/plan/")`
- `"deliver_number")`

## src/main/java/com/iemes/controller/plan/ErpOrderController.java

- `"/plan/")`
- `"erp_order")`

## src/main/java/com/iemes/controller/plan/ShoporderMaintenanceController.java

- `"/product_plan/")`
- `"shoporder_maintenance_list")`
- `"shoporder_maintenance_findByPage")`
- `"shoporder_maintenance_addUI")`
- `"shoporder_maintenance_addEntity")`
- `"shoporder_maintenance_editUI")`
- `"shoporder_maintenance_editEntity")`
- `"unfinished_workcenter_findByPage")`
- `"isExist")`

## src/main/java/com/iemes/controller/plan/ShoporderMakeController.java

- `"/plan/")`
- `"shoporder_Issued")`
- `"getShoporderInfoByNo")`
- `"shoporderIssued")`
- `"isLssued")`

## src/main/java/com/iemes/controller/popup/PopupTableController.java

- `"/PopupTable/")`
- `"assemble_sfclist")`

## src/main/java/com/iemes/controller/produce/AssemblePodController.java

- `"/produce/")`
- `"assemble_pod")`
- `"assemble_item")`
- `"assemble_item_list")`
- `"assemblingStartValadata")`
- `"process_start")`
- `"process_start_list")`
- `"process_finish")`
- `"pod_bom_findByPage")`
- `"fit_out")`
- `"operation_input_changed")`
- `"recode_nccode_ui")`
- `"recode_nccode_methods")`
- `"isInProduction")`
- `"getScoreInfo")`

## src/main/java/com/iemes/controller/produce/EquipmentTestingPodController.java

- `"/produce/")`
- `"equipment_testing_pod")`

## src/main/java/com/iemes/controller/produce/PackagePodController.java

- `"/produce/")`
- `"package_pod")`

## src/main/java/com/iemes/controller/produce/PodButtonController.java

- `"/produce/")`
- `"pod_button_list")`
- `"pod_button_findPage")`
- `"pod_button_addUI")`
- `"pod_button_addEntity")`
- `"isExistButton")`
- `"pod_button_editUI")`
- `"pod_button_editEntity")`
- `"pod_button_deleteEntity")`
- `"selButton")`

## src/main/java/com/iemes/controller/produce/PodFunctionController.java

- `"/produce/")`
- `"podfunction_list")`
- `"pod_function_findPage")`
- `"pod_function_addUI")`
- `"pod_function_addEntity")`
- `"isExist")`
- `"pod_function_editUI")`
- `"pod_function_editEntity")`
- `"pod_function_deleteEntity")`

## src/main/java/com/iemes/controller/produce/RepairPodController.java

- `"/produce/")`
- `"repair_pod")`

## src/main/java/com/iemes/controller/produce/SamplingPodController.java

- `"/produce/")`
- `"sampling_pod")`

## src/main/java/com/iemes/controller/produce/SfcStepController.java

- `"/produce/")`
- `"sfc_step")`

## src/main/java/com/iemes/controller/produce/VisualInspectionPodController.java

- `"/produce/")`
- `"visual_inspection_pod")`

## src/main/java/com/iemes/controller/qc/NcCodeController.java

- `"/qc/")`
- `"nc_list")`
- `"nc_findByPage")`
- `"nc_addUI")`
- `"nc_addEntity")`
- `"nc_editUI")`
- `"nc_editEntity")`
- `"nc_deleteEntity")`

## src/main/java/com/iemes/controller/qc/NcCodeGroupController.java

- `"/nc_group/")`
- `"nc_group_list")`
- `"nc_group_findByPage")`
- `"nc_group_addUI")`
- `"nc_group_addEntity")`
- `"nc_group_editUI")`
- `"nc_group_editEntity")`
- `"nc_group_deleteEntity")`

## src/main/java/com/iemes/controller/qc/NcHandleController.java

- `"/qc/")`
- `"nc_handle")`
- `"getNcHandleSFCData")`
- `"addNcHandleMsg")`

## src/main/java/com/iemes/controller/report/CapacityReportController.java

- `"/capacity_report/")`
- `"capacityReportUi")`
- `"getReportData")`
- `"forwardTracingReportUI")`
- `"get_sfc_findByPage")`
- `"forward_tracing_findByPage")`
- `"forward_tracing_list")`

## src/main/java/com/iemes/controller/report/ReportController.java

- `"/report/")`
- `"testReportUi")`
- `"scheduler_reportUi")`
- `"inventory_reportUi")`
- `"unqualified_reportUi")`
- `"reverseTracing_ReportUi")`
- `"get_sfcinfo")`
- `"productionRecord_reportUI")`
- `"sfc_assemblyInfo_listUI")`
- `"get_sfc_assemblyInfo_list")`
- `"get_productionRecord_list")`
- `"get_productionRecord_sfc_list")`
- `"get_productionRecord_sfc_assembly_list")`
- `"get_productionRecord_sfc_nc_list")`
- `"getReportData")`
- `"getReverseTracingData")`
- `"getUnqualifiedData")`
- `"getSfcInfoData")`
- `"getProductionRecordListData")`
- `"getProductionRecord_sfcListData")`
- `"getProductionRecord_sfc_assemblyListData")`
- `"getProductionRecord_sfc_ncListData")`
- `"sfc_assembly_info_listData")`

## src/main/java/com/iemes/controller/system/HelperController.java

- `"/help/")`
- `"open")`

## src/main/java/com/iemes/controller/system/LogController.java

- `"/log/")`
- `"list")`
- `"findByPage")`

## src/main/java/com/iemes/controller/system/MessageUploadController.java

- `"/message/")`
- `"upload")`
- `"studentInfoUpload")`
- `"userInfoDownLoad")`
- `"mainUpload")`
- `"masterDataDownLoad")`
- `"masterDataUpload")`

## src/main/java/com/iemes/controller/system/ResourcesController.java

- `"/resources/")`
- `"treelists")`
- `"reslists")`
- `"list")`
- `"editUI")`
- `"addUI")`
- `"permissions")`
- `"addEntity")`
- `"editEntity")`
- `"deleteEntity")`
- `"sortUpdate")`
- `"findRes")`
- `"addUserRes")`
- `"findByButtom")`
- `"isExist")`

## src/main/java/com/iemes/controller/system/RoleController.java

- `"/role/")`
- `"list")`
- `"findByPage")`
- `"addUI")`
- `"addEntity")`
- `"deleteEntity")`
- `"editUI")`
- `"editEntity")`
- `"selRole")`

## src/main/java/com/iemes/controller/system/UserController.java

- `"/user/")`
- `"list")`
- `"findByPage")`
- `"/export")`
- `"addUI")`
- `"addEntity")`
- `"deleteEntity")`
- `"editUI")`
- `"editEntity")`
- `"isExist")`
- `"updatePassword")`
- `"editPassword")`

## src/main/java/com/iemes/controller/system/UserLoginController.java

- `"/userlogin/")`
- `"listUI")`
- `"findByPage")`

## src/main/java/com/iemes/controller/teaching/pointGradeController.java

- `"/point_grade/")`
- `"point_grade_list")`
- `"grade_list")`
- `"putScore")`
- `"point_grade_findByPage")`
- `"point_grade_editUI")`
- `"point_grade_update_all")`
- `"exam_save")`
- `"point_exam_list")`
- `"exam_findBySite")`
- `"exam_findBySite")`

## src/main/java/com/iemes/controller/test/JayceController.java

- `"/test/")`
- `"jayce_list")`
- `"jayce_findByPage")`
- `"jayce_addUI")`
- `"jayce_addEntity")`
- `"isExist")`
- `"jayce_editUI")`
- `"jayce_editEntity")`
- `"jayce_deleteEntity")`

## src/main/java/com/iemes/controller/test/SukeyController.java

- `"/test2/")`
- `"sukey_list")`
- `"sukey_findByPage")`
- `"sukey_addUI")`
- `"sukey_addEntity")`
- `"isExist")`
- `"sukey_editUI")`
- `"sukey_editEntity")`
- `"sukey_deleteEntity")`

## src/main/java/com/iemes/controller/workshop/BomController.java

- `"/item_resource/")`
- `"bom_list")`
- `"bom_findByPage")`
- `"bom_addUI")`
- `"bom_addEntity")`
- `"bom_editUI")`
- `"bom_editEntity")`
- `"bom_deleteEntity")`

## src/main/java/com/iemes/controller/workshop/ItemController.java

- `"/item_resource/")`
- `"item_list")`
- `"getItemChildFindByPage")`
- `"getItemMainFindByPage")`
- `"getItemFindByPage")`
- `"item_findByPage")`
- `"item_addUI")`
- `"item_addEntity")`
- `"item_editUI")`
- `"item_editEntity")`
- `"item_deleteEntity")`

## src/main/java/com/iemes/controller/workshop/OperationController.java

- `"/workshop/")`
- `"operation_list")`
- `"operation_findByPage")`
- `"operation_addUI")`
- `"operation_addEntity")`
- `"operation_editUI")`
- `"operation_editEntity")`
- `"operation_deleteEntity")`
- `"operation_isExist")`

## src/main/java/com/iemes/controller/workshop/OperationResourceController.java

- `"/workshop/")`
- `"operation_resource_list")`
- `"operation_resource_findByPage")`
- `"operation_resource_addUI")`
- `"operation_resource_addEntity")`
- `"operation_resource_editUI")`
- `"operation_resource_editEntity")`
- `"operation_resource_deleteEntity")`
- `"operation_resource_isExist")`

## src/main/java/com/iemes/controller/workshop/RoutingController.java

- `"/workshop/")`
- `"routing_list")`
- `"routing_findByPage")`
- `"routing_addUI")`
- `"routing_designerUI")`
- `"routing_addEntity")`
- `"routing_isExist")`
- `"routing_editUI")`
- `"routing_editEntity")`
- `"routing_queryData")`
- `"routing_deleteEntity")`
- `"routing_getOperation")`

## src/main/java/com/iemes/controller/workshop/SiteController.java

- `"/workshop/")`
- `"site_list")`
- `"site_findByPage")`
- `"site_addUI")`
- `"site_addEntity")`
- `"isExist")`
- `"site_editUI")`
- `"site_editEntity")`
- `"site_deleteEntity")`

## src/main/java/com/iemes/controller/workshop/WorkcenterController.java

- `"/workshop/")`
- `"workcenter_list")`
- `"workcenter_findByPage")`
- `"workcenter_workShop_findByPage")`
- `"workcenter_line_findByPage")`
- `"workcenter_child_findByPage")`
- `"workcenter_add")`
- `"workcenter_addEntity")`
- `"workcenter_edit")`
- `"workcenter_editEntity")`
- `"workcenter_deleteEntity")`
- `"workcenter_isExist")`

## 初始化脚本表

- ly_buttom
- ly_exam
- ly_grade_calculate
- ly_item
- ly_item_bom
- ly_item_bom_relation
- ly_log
- ly_nc_code
- ly_nc_code_group
- ly_nc_code_relation
- ly_nc_repair
- ly_operation
- ly_operation_pod
- ly_operation_resource
- ly_pod_button
- ly_pod_function
- ly_process_route
- ly_res_role
- ly_resource_type
- ly_resources
- ly_role
- ly_route_step
- ly_sfc_assembly
- ly_sfc_nc
- ly_sfc_step
- ly_shoporder
- ly_shoporder_sfc
- ly_site
- ly_teaching_testinfo
- ly_user
- ly_user_role
- ly_userlogin
- ly_workcenter
- ly_workcenter_relation
- ly_workshop_inventory

## MyBatis 内嵌源码基线（2026-11-05）

对照 `org.mybatis:mybatis:3.2.8:sources`：项目内嵌 236 个 `org/apache/ibatis` Java 文件，198 个内容相同，18 个仅空白差异，20 个还有非空白差异。`Plugin.joinSql(Connection, MappedStatement, BoundSql, Map, List)` 只存在于项目编译类，依赖 JAR 不提供；`com.iemes.plugin.PagePlugin` 在两处调用它。WAR 同时包含内嵌类与原 MyBatis JAR，迁移时保留内嵌源码，后续升级 MyBatis 需要逐一复核这些差异。


## 菜单与权限运行基线（2026-11-05）

`ly_resources` 有 30 个 `type=1` 菜单项：26 个 `/...shtml` 页面入口在管理员会话中通过 GET 验证，其余 4 个是分组占位路径。Shiro 动态 `perms[resKey]` 规则需先于 `/** = authc`；已用无角色临时账号验证 `/role/list.shtml` 的拒绝内容、管理员页面仍可访问，并验证退出后重新受限。测试账号及其登录记录已删除。此清单仅证明页面入口与该权限例子，业务写入和每一项权限组合仍需逐项验收。


## 现状核对摘要（2026-11-05）

源码中有 40 个 `@Controller` 类、306 处 `@RequestMapping` 注解、120 个 JSP、12 个 Mapper XML、1 处 `@Scheduled`；当前初始化后数据库有 38 张表、111 条权限资源。这里的注解数包括类级映射，不等于可直接访问的独立 URL 数。外部运行依赖为 MySQL、SMTP 和旧 Sigar 工具代码；ARM64/Linux 定时告警已改用 JDK 管理接口，SMTP 在本地由 Mailpit 捕获。

**已运行验证**：数据库初始化、管理员登录/退出、无角色用户越权拒绝、26 个页面型菜单入口、物料新增/删除、库存报表 JSON、工单日期、Excel 模板下载、中文文件名空模板上传、主数据单行导入、定时邮件告警及数据库记录、上传/配置卷保持、独立备份恢复及恢复应用健康。

**仍待逐项验证**：BOM 与工艺路线编辑、计划订单生命周期的其他分支、各生产工位操作、库存变更、质量与返修、教学评分的完整交互、更多导入数据类型、各业务异常的回滚、IDEA GUI 内导入与附加调试。真实用户单行与主数据八表样例已在隔离库导入；用户与主数据导入的中途错误已验证整批回滚。

**仓库原有页面缺口**：下面 12 个 Controller 返回的 JSP 路径在当前仓库和 Git 历史中均不存在，管理员 GET 实测均为 HTTP 404。用户若有 Eclipse 工作区未提交的页面，需要以原文件恢复；仅凭相似文件名不能保证业务行为一致。

- `/workshop/routing_designerUI.shtml` → `WEB-INF/jsp/process_designer/gooFlow/demo.jsp`
- `/workshop/routing_editUI.shtml` → `WEB-INF/jsp/process_designer/gooFlow/routing_edit.jsp`
- `/plan/deliver_number.shtml` → `WEB-INF/jsp/system/plan/deliver_number.jsp`
- `/product_plan/shoporder_maintenance_editUI.shtml` → `WEB-INF/jsp/product_plan/shoporder_maintenance/shoporder_maintenance_edit.jsp`
- `/plan/erp_order.shtml` → `WEB-INF/jsp/system/plan/erp_order.jsp`
- `/point_grade/point_grade_editUI.shtml` → `WEB-INF/jsp/teaching_test/pointGrade_list.jsp`
- `/produce/repair_pod.shtml` → `WEB-INF/jsp/system/produce/repair_pod.jsp`
- `/produce/package_pod.shtml` → `WEB-INF/jsp/system/produce/package_pod.jsp`
- `/produce/sampling_pod.shtml` → `WEB-INF/jsp/system/produce/sampling_pod.jsp`
- `/produce/sfc_step.shtml` → `WEB-INF/jsp/system/produce/sfc_step.jsp`
- `/produce/equipment_testing_pod.shtml` → `WEB-INF/jsp/system/produce/equipment_testing_pod.jsp`
- `/produce/visual_inspection_pod.shtml` → `WEB-INF/jsp/system/produce/visual_inspection_pod.jsp`


**质量详情页条件**：`WEB-INF/jsp/qc/nc_handle_info.jsp` 包含完整的条件式表单，仅当 Controller 查询到匹配的不良产品并设置 `exist=true` 才渲染。隔离测试 SFC 未满足 `findNCBySFC` 的代码/站点关联，因此只返回页面骨架；不能据此认定 JSP 缺失。当前种子库站点 `*` 有 3 条打开的不良记录，但没有任何一条能在相同站点找到对应 `ly_nc_code`，该页面在此种子数据下仍未通过可视验收。

**MyBatis 运行约束**：`PagePlugin` 在 StatementHandler.prepare 才替换通用 Mapper SQL，而 MyBatis 会话缓存键已按替换前 SQL 生成；同一事务内多个 `findByNames` 调用可能错误复用不同表的结果。现由 `mybatis-config.xml` 将 localCacheScope 设为 STATEMENT，已用隔离两步工单终结与 28 个表格/9 种报表接口复验。


**浏览器加载复核（2026-11-05）**：独立无头 Chrome 登录后通过实际页面容器加载 26 个菜单路由，DevTools 观测 26/26 个 HTTP 200，未见脚本异常或失败资源请求。只证明页面能够加载；未覆盖所有按钮、表单及未挂菜单的 12 个缺失页面路由。
