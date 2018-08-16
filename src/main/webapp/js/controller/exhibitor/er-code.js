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
                                    layer,
                                    toast,
                                    layedit,
                                    upload) {
    var $ = layui.jquery;
    var f = layui.form;

    var data = ajax.getAllUrlParam();

    //加载展商信息
    ajax.request(exhibitorApi.getUrl('getExhibitor'), null, function(result) {
        if(result.status == true){
            var company = result.data;
            var inviteQrcode = new QRCode(document.getElementById("inviteEr"), {
                width : 200,
                height : 200
            });
            inviteQrcode.makeCode(company.inviteErUrl);

            // var companyQrcode = new QRCode(document.getElementById("companyEr"), {
            //     width : 200,
            //     height : 200
            // });
            // companyQrcode.makeCode(company.companyErUrl);

            var accreditQrcode = new QRCode(document.getElementById("accreditEr"), {
                width : 200,
                height : 200
            });
            accreditQrcode.makeCode(company.companyErUrl);

        } else {
            toast.error("查询展商失败");
        }
    });

    ajax.request(exhibitorApi.getUrl('getCompanyMaErCode'), null, function(result) {
        if(result.status == true){
            var base64Str = result.ercode;
            $("companyEr").innerHTML= '<img src="'+base64Str+'" />';
        } else {
            toast.error("获取展商小程序码失败");
        }
    });

    // $("#companyPrint").click(function(){
    //     $("#companyEr").jqprint({
    //         debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
    //         importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
    //         printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
    //         operaSupport: false//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
    //     });
    // });
});