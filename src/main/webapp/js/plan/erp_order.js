(function () {
  $(function () {
    var root = rootPath;
    var page = 1;
    var total = 0;

    function message(text, ok) {
      $('#erp-order-feedback').removeClass('text-success text-danger')
        .addClass(ok ? 'text-success' : 'text-danger').text(text || '请求失败');
    }

    function cell(row, value) {
      $('<td>').text(value == null ? '' : value).appendTo(row);
    }

    function loadOrders() {
      $.getJSON(root + '/plan/erp_order_data.shtml', {
        q: $('#erp-order-query').val(), page: page
      }).done(function (response) {
        if (!response.result) { message(response.message, false); return; }
        var data = response.data;
        total = data.total;
        var body = $('#erp-order-rows').empty();
        $.each(data.rows, function (_, order) {
          var row = $('<tr>').appendTo(body);
          cell(row, order.erpOrderNo);
          cell(row, order.customerName);
          cell(row, order.itemNo);
          cell(row, order.orderedQuantity);
          cell(row, order.expectedDeliveryDate);
          cell(row, order.shoporderNo);
          cell(row, order.createdAt);
        });
        $('#erp-order-page-state').text('第 ' + data.page + ' 页，共 ' + total + ' 条');
        $('#erp-order-prev').prop('disabled', page <= 1);
        $('#erp-order-next').prop('disabled', page * data.pageSize >= total);
      }).fail(function () { message('订单列表加载失败', false); });
    }

    $.getJSON(root + '/plan/erp_order_shoporders.shtml').done(function (response) {
      if (!response.result) return;
      var select = $('#erp-shoporder');
      $.each(response.data, function (_, order) {
        $('<option>').val(order.id).text(order.shoporderNo + ' · ' + order.itemNo)
          .appendTo(select);
      });
    });

    $('#erp-order-form').on('submit', function (event) {
      event.preventDefault();
      $.ajax({url: root + '/plan/erp_order_create.shtml', type: 'POST',
        data: $(this).serialize(), dataType: 'json'})
        .done(function (response) {
          message(response.message, response.result);
          if (response.result) {
            $('#erp-order-form')[0].reset();
            page = 1;
            loadOrders();
          }
        }).fail(function () { message('订单登记失败', false); });
    });

    $('#erp-order-search').on('submit', function (event) {
      event.preventDefault();
      page = 1;
      loadOrders();
    });
    $('#erp-order-prev').on('click', function () { if (page > 1) { page--; loadOrders(); } });
    $('#erp-order-next').on('click', function () { if (page * 25 < total) { page++; loadOrders(); } });
    loadOrders();
  });
}());
