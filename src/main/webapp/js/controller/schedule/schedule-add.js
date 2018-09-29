var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: true
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'schedule-api',
    'role&authority-api',
    'toast',
    'laydate'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    scheduleApi,
                                    roleApi,
                                    toast,
                                    laydate) {
    var $ = layui.jquery;
    var f = layui.form;

    var data = ajax.getAllUrlParam();

    laydate.render({
        elem: '#sTime',
        type: 'datetime',
        format: 'yyyy-MM-dd HH:mm:ss',
        value: moment(new Date()).format("YYYY-MM-DD HH:mm:ss")
    });

    laydate.render({
        elem: '#eTime',
        type: 'datetime',
        format: 'yyyy-MM-dd HH:mm:ss',
        value: moment(new Date()).add(7, 'days').format("YYYY-MM-DD HH:mm:ss")
    });

    f.on('submit(schedule-add-form)', function (data) {

        ajax.request(scheduleApi.getUrl('addSchedule'), data.field, function () {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.list.refresh();
            toast.success('保存成功');
        });
        return false;
    });
});