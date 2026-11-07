<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="/common/common.jspf" %>
<title>编辑工单</title>
</head>
<body>
<div class="contentPanel">
  <form id="shoporder-edit" class="form-horizontal">
    <input type="hidden" name="shopOrderFormMap.id" value="<c:out value='${shopOrder.id}'/>">
    <div class="form-group"><label class="col-sm-3 control-label">所属站点</label><div class="col-sm-9"><input class="form-control" value="<c:out value='${shopOrder.site}'/>" readonly></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">工单编号</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.shoporder_no" value="<c:out value='${shopOrder.shoporder_no}'/>" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">工单数量</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.shoporder_numbers" value="<c:out value='${shopOrder.shoporder_numbers}'/>" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">物料编号</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.shoporder_item" value="<c:out value='${shopOrder.shoporder_item}'/>" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">工艺路线</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.process_route" value="<c:out value='${shopOrder.process_route}'/>" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">计划开始时间</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.shoporder_start_date" value="<c:out value='${shopOrder.shoporder_start_date}'/>" placeholder="yyyy-MM-dd HH:mm:ss" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">计划结束时间</label><div class="col-sm-9"><input class="form-control" name="shopOrderFormMap.shoporder_end_date" value="<c:out value='${shopOrder.shoporder_end_date}'/>" placeholder="yyyy-MM-dd HH:mm:ss" required></div></div>
    <div class="form-group"><label class="col-sm-3 control-label">是否可用</label><div class="col-sm-9"><select class="form-control" name="shopOrderFormMap.shoporder_status"><option value="1" <c:if test="${shopOrder.shoporder_status == '1'}">selected</c:if>>是</option><option value="0" <c:if test="${shopOrder.shoporder_status == '0'}">selected</c:if>>否</option></select></div></div>
    <div class="text-right"><button type="submit" class="btn btn-success">保存</button></div>
  </form>
</div>
<script>
$(function () {
  $('#shoporder-edit').on('submit', function (event) {
    event.preventDefault();
    $.ajax({url: '${ctx}/product_plan/shoporder_maintenance_editEntity.shtml',
      type: 'POST', data: $(this).serialize(), dataType: 'json'})
      .done(function (result) {
        if (result === 'success') {
          parent.grid.loadData();
          parent.layer.close(parent.pageii);
        } else {
          layer.alert('保存失败');
        }
      })
      .fail(function () { layer.alert('保存失败'); });
  });
});
</script>
</body>
</html>
