var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: true
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'exhibitor-api',
    'layer',
    'toast',
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
                                    upload) {
    var $ = layui.jquery;
    var f = layui.form;
    var index;

    var data = ajax.getAllUrlParam();

    //上传企业宣传图
    upload.render({
        elem: '#publicityBtn'
        , url: exhibitorApi.getUrl('upload').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , number: 2   //最多上传两张图
        , multiple: true
        , before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                $('#publicityDiv').append('<img src="' + result + '" alt="' + file.name + '" class="layui-upload-img">')
            });
        },
        data: {
            id: function () {
                return $('#id').val();
            }
        }
        , done: function (res) {
            layer.closeAll('loading');
            if (res.code == 1) {
                return layer.msg('上传失败');
            } else {
                var imgurls = $('#publicityImgUrls').val() + "," + res.data.src;
                $('#publicityImgUrls').val(imgurls);
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
        , allDone: function (obj) {
            toast.msg("所有图片上传成功");
        }
    });

    //上传产品图
    upload.render({
        elem: '#productBtn'
        , url: exhibitorApi.getUrl('upload').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , number: 4   //最多上传四张图
        , multiple: true
        , before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                $('#productDiv').append('<img src="' + result + '" alt="' + file.name + '" class="layui-upload-img">')
            });
        },
        data: {
            id: function () {
                return $('#id').val();
            }
        }
        , done: function (res) {
            layer.closeAll('loading');
            if (res.code == 1) {
                return layer.msg('上传失败');
            } else {
                var imgurls = $('#productImgUrls').val() + "," + res.data.src;
                $('#productImgUrls').val(imgurls);
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
        , allDone: function (obj) {
            toast.msg("所有图片上传成功");
        }
    });

    $("#cleanImgsPublicity").click(function () {
        $('#publicityDiv').html("");
    });

    $("#cleanImgsProduct").click(function () {
        $('#productDiv').html("");
    });

    //保存展商秀
    f.on('submit(show-form)', function (data) {
        ajax.request(exhibitorApi.getUrl('updateShow'), data.field, function () {
            toast.success('修改成功');
        });
        return false;
    });


});