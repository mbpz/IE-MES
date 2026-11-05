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
