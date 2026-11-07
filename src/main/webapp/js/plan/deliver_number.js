(function () {
  $(function () {
    var root = rootPath;
    var page = 1;
    var total = 0;
    var orders = {};

    function message(text, ok) {
      $('#deliver-feedback').removeClass('text-success text-danger')
        .addClass(ok ? 'text-success' : 'text-danger').text(text || '请求失败');
    }

    function cell(row, value) {
      $('<td>').text(value == null ? '' : value).appendTo(row);
    }

    function showBalance() {
      var order = orders[$('#deliver-shoporder').val()];
      if (!order) { $('#deliver-balance').text(''); return; }
      var remaining = Number(order.plannedQuantity) - Number(order.deliveredQuantity);
      if (!isFinite(remaining)) { $('#deliver-balance').text('工单数量无效'); return; }
      $('#deliver-balance').text('工单数量 ' + order.plannedQuantity
        + '，已交付 ' + order.deliveredQuantity + '，剩余 ' + remaining.toFixed(3));
    }

    function loadOrders() {
      var selected = $('#deliver-shoporder').val();
      $.getJSON(root + '/plan/deliver_number_orders.shtml').done(function (response) {
        if (!response.result) { message(response.message, false); return; }
        orders = {};
        var select = $('#deliver-shoporder').empty();
        $('<option>').val('').text('请选择工单').appendTo(select);
        $.each(response.data, function (_, order) {
          orders[order.id] = order;
          $('<option>').val(order.id).text(order.shoporderNo + ' · ' + order.itemNo)
            .appendTo(select);
        });
        select.val(selected);
        showBalance();
      }).fail(function () { message('工单列表加载失败', false); });
    }

    function loadDeliveries() {
      $.getJSON(root + '/plan/deliver_number_data.shtml', {
        shoporderId: $('#deliver-shoporder').val(), page: page
      }).done(function (response) {
        if (!response.result) { message(response.message, false); return; }
        var data = response.data;
        total = data.total;
        var body = $('#deliver-rows').empty();
        $.each(data.rows, function (_, delivery) {
          var row = $('<tr>').appendTo(body);
          cell(row, delivery.shoporderNo);
          cell(row, delivery.quantity);
          cell(row, delivery.referenceNo);
          cell(row, delivery.note);
          cell(row, delivery.createdBy);
          cell(row, delivery.deliveredAt);
        });
        $('#deliver-page-state').text('第 ' + data.page + ' 页，共 ' + total + ' 条');
        $('#deliver-prev').prop('disabled', page <= 1);
        $('#deliver-next').prop('disabled', page * data.pageSize >= total);
      }).fail(function () { message('交付记录加载失败', false); });
    }

    $('#deliver-shoporder').on('change', function () { page = 1; showBalance(); loadDeliveries(); });
    $('#deliver-number-form').on('submit', function (event) {
      event.preventDefault();
      $.ajax({url: root + '/plan/deliver_number_create.shtml', type: 'POST',
        data: $(this).serialize(), dataType: 'json'})
        .done(function (response) {
          message(response.message, response.result);
          if (response.result) {
            $('#deliver-quantity, #deliver-reference, #deliver-note').val('');
            page = 1;
            loadOrders();
            loadDeliveries();
          }
        }).fail(function () { message('交付登记失败', false); });
    });
    $('#deliver-prev').on('click', function () { if (page > 1) { page--; loadDeliveries(); } });
    $('#deliver-next').on('click', function () { if (page * 25 < total) { page++; loadDeliveries(); } });
    loadOrders();
    loadDeliveries();
  });
}());
