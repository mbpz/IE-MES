<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="${pageContext.request.contextPath}/js/plan/deliver_number.js"></script>
<div class="contentPanel" id="deliver-number-page">
  <div class="panel panel-default">
    <div class="panel-heading"><h3 class="panel-title">交付数量登记</h3></div>
    <div class="panel-body">
      <p class="text-muted">登记工单的实际交付数量；此台账不扣库存，也不改变 SFC 或工单生产状态。</p>
      <form id="deliver-number-form" class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label" for="deliver-shoporder">MES 工单</label><div class="col-sm-5"><select id="deliver-shoporder" name="shoporderId" class="form-control" required><option value="">请选择工单</option></select></div>
          <div class="col-sm-5"><span id="deliver-balance" class="help-block"></span></div></div>
        <div class="form-group"><label class="col-sm-2 control-label" for="deliver-quantity">本次交付数量</label><div class="col-sm-4"><input id="deliver-quantity" name="quantity" type="number" min="0.001" step="0.001" class="form-control" required></div>
          <label class="col-sm-2 control-label" for="deliver-reference">交付单号</label><div class="col-sm-4"><input id="deliver-reference" name="referenceNo" class="form-control" maxlength="80"></div></div>
        <div class="form-group"><label class="col-sm-2 control-label" for="deliver-note">备注</label><div class="col-sm-10"><input id="deliver-note" name="note" class="form-control" maxlength="500"></div></div>
        <div class="text-right"><button type="submit" class="btn btn-primary">登记交付</button></div>
      </form>
      <p id="deliver-feedback" role="status" aria-live="polite"></p>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading"><h3 class="panel-title">当前站点的交付记录</h3></div>
    <div class="panel-body">
      <div class="table-responsive"><table class="table table-striped"><thead><tr><th>MES 工单</th><th>交付数量</th><th>交付单号</th><th>备注</th><th>登记人</th><th>登记时间</th></tr></thead><tbody id="deliver-rows"></tbody></table></div>
      <button id="deliver-prev" class="btn btn-default" type="button">上一页</button> <span id="deliver-page-state"></span> <button id="deliver-next" class="btn btn-default" type="button">下一页</button>
    </div>
  </div>
</div>
