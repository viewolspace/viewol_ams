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
    'user-interact-api',
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
                                    userInteractApi,
                                    upload,
                                    layer,
                                    table) {
    var $ = layui.jquery;
    var f = layui.form;

    var param = ajax.getAllUrlParam();
    if(!$.isEmptyObject(param)) {
        formUtil.renderData($('#interact-form'), param);
    }

    f.on('submit(interact-form)', function (data) {
        ajax.request(userInteractApi.getUrl('addInteract'), data.field, function () {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.list.refresh();
            toast.success('保存成功');
        });
        return false;
    });

});