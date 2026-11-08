<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="${pageContext.request.contextPath}/js/production_process/sfc_step.js"></script>
<div class="contentPanel" id="sfc-step-page">
  <div class="panel panel-default">
    <div class="panel-heading"><h3 class="panel-title">SFC 工序查询</h3></div>
    <div class="panel-body">
      <form id="sfc-step-search" class="form-inline"><label for="sfc-step-input">产品 SFC</label> <input id="sfc-step-input" class="form-control" maxlength="255" required> <button type="submit" class="btn btn-default">查询</button></form>
      <p id="sfc-step-feedback" role="status" aria-live="polite"></p>
      <p id="sfc-step-summary"></p>
      <div class="table-responsive"><table class="table table-striped"><thead><tr><th>工序</th><th>工艺路线</th><th>状态</th><th>工作中心</th><th>操作人</th><th>开始时间</th><th>完成时间</th></tr></thead><tbody id="sfc-step-rows"></tbody></table></div>
    </div>
  </div>
</div>
