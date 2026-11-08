(function () {
  $(function () {
    var statusNames = {'0': '创建', '1': '生产中', '2': '已完成', '3': '报废', '4': '记录不良'};

    function cell(row, value) {
      $('<td>').text(value == null ? '' : value).appendTo(row);
    }

    $('#sfc-step-search').on('submit', function (event) {
      event.preventDefault();
      var sfc = $('#sfc-step-input').val();
      $.getJSON(rootPath + '/produce/sfc_step_data.shtml', {sfc: sfc})
        .done(function (response) {
          $('#sfc-step-feedback').text(response.message);
          var body = $('#sfc-step-rows').empty();
          if (!response.result) { $('#sfc-step-summary').text(''); return; }
          var product = response.data.product;
          $('#sfc-step-summary').text('工单 ' + product.shoporderNo + '，SFC 状态 '
            + (statusNames[String(product.status)] || product.status));
          $.each(response.data.rows, function (_, step) {
            var row = $('<tr>').appendTo(body);
            cell(row, step.operation);
            cell(row, step.processRoute);
            cell(row, statusNames[String(step.status)] || step.status);
            cell(row, step.workcenter);
            cell(row, step.byUser);
            cell(row, step.createdAt);
            cell(row, step.finishedAt);
          });
        }).fail(function () { $('#sfc-step-feedback').text('工序查询失败'); });
    });
  });
}());
