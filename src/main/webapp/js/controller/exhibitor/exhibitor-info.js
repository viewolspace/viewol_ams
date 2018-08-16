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
    var layedit = layui.layedit;
    var f = layui.form;
    var index;

    layedit.set({
        uploadImage: {
            url: exhibitorApi.getUrl('uploadContentImage').url,
            type: 'POST'
        }
    });

    var data = ajax.getAllUrlParam();
    var categoryIds ;//分类相关的信息

    //加载展商信息
    ajax.request(exhibitorApi.getUrl('getExhibitor'), null, function(result) {
        if(result.status == true){
            var company = result.data;
            $("#companyName").html(company.name);
            // $("#shortName").html(company.shortName);
            $("#categoryName").html(company.categoryName);
            categoryIds = company.categoryIds;
            $("#place").html(company.place);
            if(company.logo!=''){
                $("#logo").attr('src', company.logo);
                $("#logo_avatar").val(company.logo);
            }

            if(company.banner!=''){
                $("#banner").attr('src', company.banner);
                $("#banner_avatar").val(company.banner);
            }

            if(company.image!=''){
                $("#image").attr('src', company.image);
                $("#image_avatar").val(company.image);
            }

            $("#content").val(company.content);
            $("#id").val(company.id);
            var inviteQrcode = new QRCode(document.getElementById("inviteEr"), {
                width : 100,
                height : 100
            });
            inviteQrcode.makeCode(company.inviteErUrl);

            index = layedit.build('content');//构建富文本编辑器
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

    $('#choose-category').click(function() {
        var ids = categoryIds;

        var url = ajax.composeUrl(webName + '/views/exhibitor/category-tree.html', {
            check: true,
            recheckData: ids,	//前端回显数据
            type: 1			//分类的类型
        });

        layer.open({
            type: 2,
            title: "选择分类",
            content:url ,
            area:['50%','80%'],
            btn: ['确定了', '取消了'],
            yes: function(index, layero) {
                var iframeWin = window[layero.find('iframe')[0]['name']];
                var categoryData = iframeWin.tree.getAuthorityData();
                layer.close(index);
                $("#categoryName").html(categoryData.categoryNames.join(","));

                ajax.request(exhibitorApi.getUrl('uploadCategory'), {
                    id: $('#id').val(),
                    categoryIds: categoryData.ids
                }, function(result) {
                    if (result.status == true) {
                        toast.success("修改成功");
                    }
                });
            }

        });
    });


    $("#invitePrint").click(function(){
        $("#inviteEr").jqprint({
            debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
            importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
            printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
            operaSupport: false//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
        });
    });

    $("#companyPrint").click(function(){
        $("#companyEr").jqprint({
            debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
            importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
            printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
            operaSupport: false//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
        });
    });

    //上传展商Logo
    upload.render({
        elem: '#logoBtn'
        ,url: exhibitorApi.getUrl('uploadLogo').url
        ,ext: 'jpg|png|gif|bmp'
        ,type: 'image'
        ,before: function(obj){
            //预读本地文件
        },
        data: {
            id: function(){
                return $('#id').val();
            }
        }
        ,done: function(res){
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#logo').attr('src', res.imageUrl);
                $('#logo_avatar').val(res.imageUrl);
            }
        }
        ,error: function(){
            return layer.msg('数据请求异常');
        }
    });

    //上传展商形象图
    upload.render({
        elem: '#bannerBtn'
        ,url: exhibitorApi.getUrl('uploadBanner').url
        ,ext: 'jpg|png|gif|bmp'
        ,type: 'image'
        ,before: function(obj){
            //预读本地文件
        },
        data: {
            id: function(){
                return $('#id').val();
            }
        }
        ,done: function(res){
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#banner').attr('src', res.imageUrl);
                $('#banner_avatar').val(res.imageUrl);
            }
        }
        ,error: function(){
            return layer.msg('数据请求异常');
        }
    });

    //上传展商图片
    upload.render({
        elem: '#imageBtn'
        ,url: exhibitorApi.getUrl('uploadImage').url
        ,ext: 'jpg|png|gif|bmp'
        ,type: 'image'
        ,before: function(obj){
            //预读本地文件
        },
        data: {
            id: function(){
                return $('#id').val();
            }
        }
        ,done: function(res){
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#image').attr('src', res.imageUrl);
                $('#image_avatar').val(res.imageUrl);
            }
        }
        ,error: function(){
            return layer.msg('数据请求异常');
        }
    });


    //保存展商介绍
    f.on('submit(content-form)', function(data) {
        var datas = $.extend(true, data.field, {"content": layedit.getContent(index)});
        ajax.request(exhibitorApi.getUrl('updateContent'), datas, function() {
            toast.success('修改成功');
        });
        return false;
    });


});