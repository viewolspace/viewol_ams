var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: 2018011001
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'exhibitor-api',
    'buser-api',
    'layer',
    'toast',
    'layedit',
    'upload'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    exhibitorApi,
                                    buserApi,
                                    layer,
                                    toast,
                                    layedit,
                                    upload) {
    var $ = layui.jquery;
    var f = layui.form;

    var data = ajax.getAllUrlParam();

    //加载邀请函二维码
    ajax.request(exhibitorApi.getUrl('getExhibitor'), null, function(result) {
        if(result.status == true){
            var company = result.data;
            var inviteQrcode = new QRCode(document.getElementById("inviteEr"), {
                width : 200,
                height : 200
            });
            inviteQrcode.makeCode(company.inviteErUrl);
        } else {
            toast.error("查询展商失败");
        }
    });

    //加载业务员授权二维码
    ajax.request(buserApi.getUrl('loadErCode'), null, function(result) {
        if(result.status == true){
            var ercodeUrl = result.ercode;
            var buserQrcode = new QRCode(document.getElementById("accreditEr"), {
                width : 200,
                height : 200
            });
            buserQrcode.makeCode(ercodeUrl);
        }
    });

    //展商主页小程序码
    ajax.request(exhibitorApi.getUrl('getCompanyMaErCode'), {"width":200}, function(result) {
        if(result.status == true){
            var base64Str = result.ercode;
            $("#companyErImg").attr('src', "data:image/png;base64,"+base64Str);
        } else {
            toast.error("获取展商小程序码失败");
        }
    });
});