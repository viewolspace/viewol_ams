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
    'exhibitor-api'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    toast,
                                    exhibitorApi) {
    var $ = layui.jquery;
    var f = layui.form;

    //提交form表单
    f.on('submit(show-add-form)', function(data) {
    	ajax.request(exhibitorApi.getUrl('addShow'), data.field, function() {
    		var index = parent.layer.getFrameIndex(window.name);
    		parent.layer.close(index);
    		parent.list.refresh();
    		toast.success('保存成功');
    	});
    	return false;
    });

});