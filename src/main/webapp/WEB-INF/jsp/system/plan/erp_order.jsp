<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="${pageContext.request.contextPath}/js/plan/erp_order.js"></script>
<div class="contentPanel" id="erp-order-page">
  <div class="panel panel-default">
    <div class="panel-heading"><h3 class="panel-title">ERP 订单登记</h3></div>
    <div class="panel-body">
      <p class="text-muted">手工登记外部订单；当前未连接 ERP，也不会自动创建或下达 MES 工单。</p>
      <form id="erp-order-form" class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label" for="erp-order-no">ERP 订单号</label><div class="col-sm-4"><input id="erp-order-no" name="erpOrderNo" class="form-control" maxlength="80" required></div>
          <label class="col-sm-2 control-label" for="erp-customer">客户</label><div class="col-sm-4"><input id="erp-customer" name="customerName" class="form-control" maxlength="255"></div></div>
        <div class="form-group"><label class="col-sm-2 control-label" for="erp-item">物料编号</label><div class="col-sm-4"><input id="erp-item" name="itemNo" class="form-control" maxlength="255" required></div>
          <label class="col-sm-2 control-label" for="erp-quantity">订单数量</label><div class="col-sm-4"><input id="erp-quantity" name="quantity" type="number" min="0.001" step="0.001" class="form-control" required></div></div>
        <div class="form-group"><label class="col-sm-2 control-label" for="erp-date">预计交付日</label><div class="col-sm-4"><input id="erp-date" name="expectedDeliveryDate" type="date" class="form-control"></div>
          <label class="col-sm-2 control-label" for="erp-shoporder">关联 MES 工单</label><div class="col-sm-4"><select id="erp-shoporder" name="shoporderId" class="form-control"><option value="">不关联</option></select></div></div>
        <div class="form-group"><label class="col-sm-2 control-label" for="erp-note">备注</label><div class="col-sm-10"><input id="erp-note" name="note" class="form-control" maxlength="500"></div></div>
        <div class="text-right"><button type="submit" class="btn btn-primary">登记订单</button></div>
      </form>
      <p id="erp-order-feedback" role="status" aria-live="polite"></p>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading"><h3 class="panel-title">当前站点的 ERP 订单</h3></div>
    <div class="panel-body">
      <form id="erp-order-search" class="form-inline"><label for="erp-order-query">订单号</label> <input id="erp-order-query" class="form-control" maxlength="80"> <button type="submit" class="btn btn-default">查询</button></form>
      <div class="table-responsive"><table class="table table-striped"><thead><tr><th>ERP 订单号</th><th>客户</th><th>物料</th><th>数量</th><th>预计交付日</th><th>关联工单</th><th>登记时间</th></tr></thead><tbody id="erp-order-rows"></tbody></table></div>
      <button id="erp-order-prev" class="btn btn-default" type="button">上一页</button> <span id="erp-order-page-state"></span> <button id="erp-order-next" class="btn btn-default" type="button">下一页</button>
    </div>
  </div>
</div>
