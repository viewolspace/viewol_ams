var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: 2018011001
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'layer',
    'toast',
    'key-bind',
    'layedit'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    layer,
                                    toast,
                                    keyBind,
                                    layedit) {
    var $ = layui.jquery;
    var layedit = layui.layedit;

    var index = layedit.build('ddddddddddd');
    var data = ajax.getAllUrlParam();

    //加载展商信息

    //给编辑按钮绑定点击事件
    $(document).on('click', '#editInfo', function () {
        var url = ajax.composeUrl(webName + '/views/exhibitor/exhibitor-edit.html?id=1');
        var index = layer.open({
            type: 2,
            title: "编辑展商信息",
            area: ['800px', '450px'],
            offset: '5%',
            scrollbar: false,
            content: url,
            success: function (ly, index) {
                // layer.iframeAuto(index);
            }
        });
        layer.full(index);
    });


});