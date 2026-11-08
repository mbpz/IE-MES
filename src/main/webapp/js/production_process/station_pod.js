(function () {
  $(function () {
    var panel = $('.station-pod-page').last();
    var station = panel.data('station');
    var page = 1;
    var total = 0;
    var root = rootPath + '/produce/' + station;

    function message(text, ok) {
      panel.find('.station-feedback').removeClass('text-success text-danger')
        .addClass(ok ? 'text-success' : 'text-danger').text(text || '请求失败');
    }

    function cell(row, value) {
      $('<td>').text(value == null ? '' : value).appendTo(row);
    }

    function resultText(value) {
      if (value === 'complete') return '完成';
      if (value === 'pass') return '通过';
      if (value === 'fail') return '未通过';
      return value;
    }

    function loadData() {
      $.getJSON(root + '_data.shtml', {sfc: panel.find('.station-query').val(), page: page})
        .done(function (response) {
          if (!response.result) { message(response.message, false); return; }
          var data = response.data;
          total = data.total;
          var body = panel.find('.station-rows').empty();
          $.each(data.rows, function (_, entry) {
            var row = $('<tr>').appendTo(body);
            cell(row, entry.sfc);
            cell(row, resultText(entry.result));
            cell(row, entry.referenceNo);
            cell(row, entry.note);
            cell(row, entry.createdBy);
            cell(row, entry.createdAt);
          });
          panel.find('.station-page-state').text('第 ' + data.page + ' 页，共 ' + total + ' 条');
          panel.find('.station-prev').prop('disabled', page <= 1);
          panel.find('.station-next').prop('disabled', page * data.pageSize >= total);
        }).fail(function () { message('记录加载失败', false); });
    }

    panel.find('.station-pod-form').on('submit', function (event) {
      event.preventDefault();
      var sfc = panel.find('.station-sfc').val();
      $.ajax({url: root + '_record.shtml', type: 'POST',
        data: $(this).serialize(), dataType: 'json'})
        .done(function (response) {
          message(response.message, response.result);
          if (response.result) {
            panel.find('.station-query').val(sfc);
            panel.find('[name=referenceNo], [name=note]').val('');
            page = 1;
            loadData();
          }
        }).fail(function () { message('登记失败', false); });
    });

    panel.find('.station-search').on('submit', function (event) {
      event.preventDefault();
      page = 1;
      loadData();
    });
    panel.find('.station-prev').on('click', function () { if (page > 1) { page--; loadData(); } });
    panel.find('.station-next').on('click', function () { if (page * 25 < total) { page++; loadData(); } });
    loadData();
  });
}());
