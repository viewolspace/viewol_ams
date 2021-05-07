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

    /**
     * 动态选择Checkbox
     */
    $('input:checkbox[name="南登录厅正面吊旗广告"]').each(function (i) {
        if($(this).val()==='W-2'){
            $(this).prop("checked", true);
        }

    });
    f.render();
    //提交form表单
    f.on('submit(billboard-form)', function (data) {


        html = '';
        $('#billboardTable').find('tbody').each(function () {
            $(this).find('tr').each(function () {
                $(this).find('td').each(function (index, element) {
                    console.log(element);
                    html += index + '=' + $(this).text() + ',';
                });
                html += '|';
            });

            console.log('html= ' + html);
            return false;
        });

        // var TVGoods = $('input[name="南登录厅正面吊旗广告"]:checked').map(function () {
        //     return this.value;
        // }).get().join(",");
        // console.log(TVGoods)
        //
        // $(this).attr("checked"))


        $.each($('input:checkbox:checked'), function () {
            console.log($('input[type=checkbox]:checked').length + "个，其中有：" + $(this).attr("name") + ', ' + $(this).val());
        });


        // ajax.request(exhibitionApi.getUrl('addProductIdea'), data.field, function () {
        //     var index = parent.layer.getFrameIndex(window.name);
        //     parent.layer.close(index);
        //     parent.list.refresh();
        //     toast.success('修改成功');
        //
        // });
        return false;
    });

});