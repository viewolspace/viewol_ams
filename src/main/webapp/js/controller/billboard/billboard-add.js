var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: true
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'toast',
    'key-bind',
    'billboard-api',
    'upload',
    'layer',
    'table'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    toast,
                                    keyBind,
                                    billboardApi,
                                    upload,
                                    layer,
                                    table) {
    var $ = layui.jquery;
    var f = layui.form;

    ajax.request(billboardApi.getUrl('billboardList'), null, function (result) {
        formUtil.renderData($('#billboard-form'), result.data);

        formUtil.renderData($('#billboard-form'), result.adMedia);

        form.render();
    });


    //提交form表单
    f.on('submit(billboard-form)', function (data) {
        var dataHtml = '';
        $('#billboardTable').find('tbody').each(function () {
            $(this).find('tr').each(function () {
                $(this).find('td').each(function (index, element) {
                    dataHtml += $(this).text() + '@';
                });
                dataHtml += '|';
            });

            console.log('dataHtml= ' + dataHtml);
            return false;
        });


        var selectHtml = '';
        $.each($('input:checkbox:checked'), function () {
            selectHtml += $(this).attr("name") + '=' + $(this).val() + '|';
        });
        console.log('selectHtml= ' + selectHtml);

        ajax.request(billboardApi.getUrl('addBillBoard'), {
            dataHtml: dataHtml,
            selectHtml: selectHtml,
            name: $("#name").val(),
            phone: $("#phone").val()
        }, function (result) {

        });

        return false;
    });

});