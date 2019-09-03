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
    'table',
    'upload',
    'btns',
    'authority'

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
                                    table,
                                    upload,
                                    btns,
                                    authority) {
    var $ = layui.jquery;
    var f = layui.form;

    ajax.request(exhibitorApi.getUrl('getShow'), null, function (result) {
        var data = result.data;
        if (!$.isEmptyObject(data)) {
            formUtil.renderData($('#show-form'), data);
        }

        var imgurls;
        $.each(data.imgUrl, function (index, element) {
            if (element != 'undefined') {
                $('#publicityDiv').append('<img src="' + element + '" class="layui-upload-img">');
                imgurls = imgurls + "," + element;
            }
        });
        $('#publicityImgUrls').val(imgurls);

        var proImgUrls;
        $.each(data.productUrl, function (index, element) {
            if (element != 'undefined') {
                $('#productDiv').append('<img src="' + element + '" class="layui-upload-img">');
                proImgUrls = proImgUrls + "," + element;
            }
        });
        $('#productImgUrls').val(proImgUrls);

        $.each(data.progresses, function (index, element) {
            if (index == 0) {
                $('#times').val(element.times);
                $('#des').val(element.des);
            } else {
                var html = '<div class="layui-form-item"><div class="layui-inline"><label class="layui-form-label">年份</label><div class="layui-input-inline"><input type="text" name="times" lay-verify="required" value="' + element.times + '" autocomplete="off" class="layui-input"></div></div><div class="layui-inline"><label class="layui-form-label">事件</label><div class="layui-input-inline" style="width: 450px"><input type="text" name="des" lay-verify="required" value="' + element.des + '" autocomplete="off" class="layui-input" style="width: 450px"></div></div><button class="layui-btn layui-btn-danger" type="button" id="del">删除</button></div>';
                $('#qiyefazhanlicheng').append(html);
            }
        });
    });

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
            toast.msg("所有图片上传成功");

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
        $('#publicityImgUrls').val("");
    });

    $("#cleanImgsProduct").click(function () {
        $('#productDiv').html("");
        $('#productImgUrls').val("");
    });

    $("#addInput").click(function () {
        var len = $("#qiyefazhanlicheng .layui-form-item").length;
        if (len > 4) {
            toast.msg("最多添加5条发展历程");
            return;
        }

        var html = '<div class="layui-form-item"><div class="layui-inline"><label class="layui-form-label">年份</label><div class="layui-input-inline"><input type="text" name="times" lay-verify="required" autocomplete="off" class="layui-input"></div></div><div class="layui-inline"><label class="layui-form-label">事件</label><div class="layui-input-inline" style="width: 450px"><input type="text" name="des" lay-verify="required" autocomplete="off" class="layui-input" style="width: 450px"></div></div><button class="layui-btn layui-btn-danger" type="button" id="del">删除</button></div>';
        $(this).parent().append(html);
    });

    $(document).on('click', '#del', function () {
        $(this).parent().remove();
        toast.msg('删除成功，请点击下方《保存》按钮保存！');
    });

    //保存展商秀
    f.on('submit(show-form)', function (data) {
        var timesArray = [];
        $("#qiyefazhanlicheng .layui-form-item").each(function (index, element) {
            var timesRow = {};
            $(element).find("input").each(function (idx, ele) {
                // str = str + '\"' + ele.name + '":' + ele.value + ',';
                if (ele.name == 'times') {
                    timesRow.times = ele.value;
                } else {
                    timesRow.des = ele.value;
                }
            });
            timesArray.push(timesRow);
        });
        data.field.progress = JSON.stringify(timesArray);

        ajax.request(exhibitorApi.getUrl('updateShow'), data.field, function () {
            toast.success('修改成功');
            location.reload();
        });
        return false;
    });


});