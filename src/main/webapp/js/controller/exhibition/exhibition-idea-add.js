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

            if(productIdeaData.categoryId == '其它'){
                $("#otherCategoryDiv").show();
            } else {
                $("#otherCategoryDiv").hide();
                $("#otherCategoryDiv").val("");
            }

            $('#promisePicAvatarId').attr('src', productIdeaData.promisePic);
            $('#promisePic').val(productIdeaData.promisePic);

            $('#productPicAvatarId').attr('src', productIdeaData.productPic);
            $('#productPic').val(productIdeaData.productPic);

            $('#comLogoAvatarId').attr('src', productIdeaData.comLogo);
            $('#comLogo').val(productIdeaData.comLogo);
        }
    });

    //上传申报单位承诺,单位盖章图片
    upload.render({
        elem: '#promisePicBtn'
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
                $('#promisePicAvatarId').attr('src', res.imageUrl);
                $('#promisePic').val(res.imageUrl);
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

    //专利，软著相关图片（ZIP压缩包）
    upload.render({
        elem: '#achievementZipBtn',
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
                $('#achievementZip').val(res.pdfUrl);
                toast.msg("上传成功");
            }
        },
        error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //相关证明（ZIP压缩包）
    upload.render({
        elem: '#corePicBtn',
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
                $('#corePic').val(res.pdfUrl);
                toast.msg("上传成功");
            }
        },
        error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    f.on('select(categoryId)', function(data){
        if(data.value == '其它'){
            $("#otherCategoryDiv").show();
        } else {
            $("#otherCategoryDiv").hide();
            $("#otherCategoryDiv").val("");
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