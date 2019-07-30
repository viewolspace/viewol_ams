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
    'exhibition-api',
    'upload',
    'layer'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    toast,
                                    keyBind,
                                    exhibitionApi,
                                    upload,
                                    layer) {
    var $ = layui.jquery;
    var f = layui.form;
    var productIdeaData;//创新产品信息

    var param = ajax.getAllUrlParam();
    ajax.request(exhibitionApi.getUrl('getProductIdea'), {
        id: param.id	//产品ID
    }, function (result) {
        productIdeaData = result.data;
        if (!$.isEmptyObject(productIdeaData)) {
            formUtil.renderData($('#exhibition-idea-add-form'), productIdeaData);

            $('#logoAvatarId').attr('src', param.image);
            $('#logo').val(param.image);

            $('#productPicAvatarId').attr('src', param.image);
            $('#productPic').val(param.image);

            $('#comLogoAvatarId').attr('src', param.image);
            $('#comLogo').val(param.image);
        }
    });

    //上传产品商标
    upload.render({
        elem: '#logoBtn'
        , url: exhibitionApi.getUrl('uploadImg').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , before: function (obj) {
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        }
        , done: function (res) {
            layer.closeAll('loading');
            if (res.status == false) {
                return layer.msg('上传失败');
            } else {
                $('#logoAvatarId').attr('src', res.imageUrl);
                $('#logo').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //上传产品图片
    upload.render({
        elem: '#productPicBtn'
        , url: exhibitionApi.getUrl('uploadImg').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , before: function (obj) {
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        }
        , done: function (res) {
            layer.closeAll('loading');
            if (res.status == false) {
                return layer.msg('上传失败');
            } else {
                $('#productPicAvatarId').attr('src', res.imageUrl);
                $('#productPic').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //上传展商LOGO
    upload.render({
        elem: '#comLogoBtn'
        , url: exhibitionApi.getUrl('uploadImg').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , before: function (obj) {
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        }
        , done: function (res) {
            layer.closeAll('loading');
            if (res.status == false) {
                return layer.msg('上传失败');
            } else {
                $('#comLogoAvatarId').attr('src', res.imageUrl);
                $('#comLogo').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //上传pdf
    upload.render({
        elem: '#extBtn',
        url: exhibitionApi.getUrl('uploadPdf').url,
        accept: 'file',
        ext: 'rar|zip',
        size: 10240, //最大允许上传的文件大小kb
        before: function (obj) {
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        },
        done: function (res) {
            layer.closeAll('loading');
            if (res.status == false) {
                return layer.msg('上传失败');
            } else {
                $('#ext').val(res.pdfUrl);
                toast.msg("上传成功");
            }
        },
        error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //提交form表单
    f.on('submit(exhibition-idea-add-form)', function (data) {
        ajax.request(exhibitionApi.getUrl('addProductIdea'), data.field, function () {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.list.refresh();
            toast.success('修改成功');

        });
        return false;
    });

});