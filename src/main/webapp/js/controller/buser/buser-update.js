var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: 2018011001
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'toast',
    'buser-api'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    toast,
                                    buserApi) {
    var $ = layui.jquery;
    var layedit = layui.layedit;
    var f = layui.form;
    var categoryData ;//分类相关的信息

    var param = ajax.getAllUrlParam();
    if(!$.isEmptyObject(param)) {
        formUtil.renderData($('#buser-update-form'), param);
    }

    //提交form表单
    f.on('submit(buser-update-form)', function(data) {
        ajax.request(buserApi.getUrl('review'), data.field, function() {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.list.refresh();
            toast.success('修改成功');

        });
        return false;
    });

});