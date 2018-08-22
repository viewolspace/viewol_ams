var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: 2018011001
});

var requireModules = [
    'form',
    'form-util',
    'request',
    'toast',
    'key-bind',
    'layedit',
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
                                    layedit,
                                    exhibitionApi,
                                    upload,
                                    layer) {
    var $ = layui.jquery;
    var layedit = layui.layedit;
    var f = layui.form;
    var categoryData ;//分类相关的信息

    var param = ajax.getAllUrlParam();
    if(!$.isEmptyObject(param)) {
        formUtil.renderData($('#exhibition-update-form'), param);
    }
    $('#imageAvatarId').attr('src', param.image);
    $('#imageAvatar').val(param.image);

    $('#regImageAvatarId').attr('src', param.regImage);
    $('#regImageAvatar').val(param.regImage);

    /**
     * 根据产品ID查询产品所属分类
     */
    ajax.request(exhibitionApi.getUrl('getExhibitionCategory'), {
        categoryId: param.categoryId	//展品所属类别ID
    }, function(result) {
        categoryData = result.data;
        $("#categoryName").val(categoryData.categoryNames);
    });


    //产品介绍富文本编辑初始化
    var index;
    layedit.set({
        uploadImage: {
            url: exhibitionApi.getUrl('uploadContentImage').url,
            type: 'POST'
        }
    });

    ajax.request(exhibitionApi.getUrl('getExhibition'), {
        id: param.id	//展品所属类别ID
    }, function(result) {
        if(result.status == true){
            $("#content").val(result.data.content);
            index = layedit.build('content');//产品介绍富文本编辑初始化
        }
    });

    //初始化产品分类树
    $('#choose-category').click(function() {
        var ids = categoryData?categoryData.ids:'';

        var url = ajax.composeUrl(webName + '/views/exhibition/category-tree.html', {
            check: true,
            recheckData: ids,	//前端回显数据
            type: 2			//分类的类型
        });

        layer.open({
            type: 2,
            title: "选择分类",
            content:url ,
            area:['50%','80%'],
            btn: ['确定了', '取消了'],
            yes: function(index, layero) {
                var iframeWin = window[layero.find('iframe')[0]['name']];
                categoryData = iframeWin.tree.getAuthorityData();
                layer.close(index);
                $("#categoryName").val(categoryData.categoryNames);
            }

        });
    });

    //上传产品图片
    upload.render({
        elem: '#imageBtn'
        ,url: exhibitionApi.getUrl('uploadImg').url
        ,ext: 'jpg|png|gif|bmp'
        ,type: 'image'
        ,before: function(obj){
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        }
        ,done: function(res){
            layer.closeAll('loading');
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#imageAvatarId').attr('src', res.imageUrl);
                $('#imageAvatar').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        ,error: function(){
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //上传首页推荐产品图片
    upload.render({
        elem: '#regImageBtn'
        ,url: exhibitionApi.getUrl('uploadImg').url
        ,ext: 'jpg|png|gif|bmp'
        ,type: 'image'
        ,before: function(obj){
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        }
        ,done: function(res){
            layer.closeAll('loading');
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#regImageAvatarId').attr('src', res.imageUrl);
                $('#regImageAvatar').val(res.imageUrl);
                toast.msg("上传成功");
            }
        }
        ,error: function(){
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });


    //上传pdf
    upload.render({
        elem: '#pdfBtn',
        url: exhibitionApi.getUrl('uploadPdf').url,
        accept: 'file',
        ext: 'pdf',
        size: 51200, //最大允许上传的文件大小kb
        before: function(obj){
            //预读本地文件
            layer.load(0, {
                shade: 0.5
            });
        },
        done: function(res){
            layer.closeAll('loading');
            if(res.status == false){
                return layer.msg('上传失败');
            } else {
                $('#pdfUrl').val(res.pdfUrl);
                toast.msg("上传成功");
            }
        },
        error: function(){
            layer.closeAll('loading');
            return layer.msg('数据请求异常');
        }
    });

    //提交form表单
    f.on('submit(exhibition-update-form)', function(data) {
        var datas = $.extend(true, data.field, categoryData, {"content": layedit.getContent(index)});
        ajax.request(exhibitionApi.getUrl('updateExhibition'), datas, function() {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.list.refresh();
            toast.success('修改成功');

        });
        return false;
    });

});