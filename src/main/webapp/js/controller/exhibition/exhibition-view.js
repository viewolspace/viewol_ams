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
        formUtil.renderData($('#exhibition-view-form'), param);
    }
    $('#imageAvatarId').attr('src', param.image);

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

            $("#erCodeImg").attr('src', "data:image/png;base64,"+result.data.ercode);
        }
    });

    $("#erCodePrint").click(function(){
        $("#erCodeImg").jqprint({
            debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
            importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
            printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
            operaSupport: false//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
        });
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

});