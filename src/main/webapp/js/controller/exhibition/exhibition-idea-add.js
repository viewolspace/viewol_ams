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
            productIdeaData.proAsk = [productIdeaData.proAsk];
            productIdeaData.proView = [productIdeaData.proView];
            productIdeaData.proEvent = [productIdeaData.proEvent];
            productIdeaData.proVideo = [productIdeaData.proVideo];

            if (productIdeaData.proVideo[0] == '否') {
                $("#publicityVideoDiv").hide();
                $("#video").val("");
            } else {
                $("#publicityVideoDiv").show();
            }

            formUtil.renderData($('#exhibition-idea-add-form'), productIdeaData);

            form.render(); //更新全部

            if (productIdeaData.categoryId == '其它') {
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

            $('#vLogoAvatarId').attr('src', productIdeaData.vLogo);
            $('#vLogo').val(productIdeaData.vLogo);

            if(productIdeaData.vPic != undefined){
                var imgurls = productIdeaData.vPic.split(",");

                for (var i = 0; i < imgurls.length; i++) {
                    vpic = imgurls[i];
                    if (vpic != "" && vpic.length > 0) {
                        $('#v_pic').append('<img src="' + vpic + '" height="92px" width="92px" class="layui-upload-img uploadImgPreView">')
                    }
                }
                $('#vPic').val(productIdeaData.vPic);
            }
        }
    });

    //上传申报单位承诺,单位盖章图片
    upload.render({
        elem: '#promisePicBtn'
        , url: exhibitionApi.getUrl('uploadImg').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '1'
        }
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
        , data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '2'
        }
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
        , data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '3'
        }
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
        data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '1'
        },
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
        data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '2'
        },
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

    //视频上传
    upload.render({
        elem: '#videoBtn',
        url: exhibitionApi.getUrl('uploadPdf').url,
        accept: 'file',
        ext: 'mp4|MP4',
        size: 10240, //最大允许上传的文件大小kb
        data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '3'
        },
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
                $('#video').val(res.pdfUrl);
                toast.msg("上传成功");
            }
        },
        error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    f.on('select(categoryId)', function (data) {
        if (data.value == '其它') {
            $("#otherCategoryDiv").show();
        } else {
            $("#otherCategoryDiv").hide();
            $("#otherCategoryDiv").val("");
        }
    });

    f.on('radio(proVideo1)', function (data) {
        var value = data.value;   //  当前选中的value值
        if (value == '否') {
            $("#publicityVideoDiv").hide();
            $("#video").val("");
        } else {
            $("#publicityVideoDiv").show();
        }
    });

    //上传产品Logo，投票使用
    upload.render({
        elem: '#vLogoBtn'
        , url: exhibitionApi.getUrl('uploadImg').url
        , ext: 'jpg|png|gif|bmp'
        , type: 'image'
        , size: 1024 //最大允许上传的文件大小kb
        , data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '9'
        }
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
                $('#vLogoAvatarId').attr('src', res.imageUrl);
                $('#vLogo').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        , error: function () {
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    var success = 0;
    var fail = 0;
    var imgurls = "";

    upload.render({
        elem: '#test1',
        url: exhibitionApi.getUrl('uploadImg').url,
        multiple: true,
        auto: true,
        size: 10240,//            上传的单个图片大小
        number: 4,//            最多上传的数量
        field: 'file',
        // bindAction: '#test9',
        data: {
            productName: function () {
                return $('#productName').val();
            },
            categoryId: function () {
                return $('#categoryId').val();
            },
            companyName: function () {
                return $('#companyName').val();
            },
            type: '9'
        },
        before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                $('#v_pic').append('<img src="' + result + '" alt="' + file.name + '"height="92px" width="92px" class="layui-upload-img uploadImgPreView">')
            });
        },
        done: function (res, index, upload) {
            //每个图片上传结束的回调，成功的话，就把新图片的名字保存起来，作为数据提交
            if (res.status == false) {
                fail++;
            } else {
                success++;
                imgurls = imgurls + "" + res.imageUrl + ",";
                $('#vPic').val(imgurls);
            }
        },
        allDone: function (obj) {
            layer.msg("总上传图片数为：" + (fail + success) + "\r\n"
                + "，上传成功：" + success + "\r\n"
                + "，上传失败：" + fail
            )
        }
    });

    cleanImgsPreview();

    function cleanImgsPreview(){
        $("#cleanImgs").click(function(){
            success=0;
            fail=0;
            $('#v_pic').html("");
            $('#vPic').val("");
        });
    }

    //自定义验证规则
    f.verify({
        vDesLength: function (value) {
            var i, sum;
            sum = 0;
            for (i = 0; i < value.length; i++) {
                if ((value.charCodeAt(i) >= 0) && (value.charCodeAt(i) <= 255)) {
                    sum = sum + 1;
                } else {
                    sum = sum + 2;
                }
            }
            if (sum > 400) {
                return '产品介绍最多只能输入200个中文字';
            }
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