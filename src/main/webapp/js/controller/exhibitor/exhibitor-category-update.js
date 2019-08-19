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
    'toast'

];

registeModule(window, requireModules, {
    'role&authority-api': 'api/role&authority-api'
});

layui.use(requireModules, function (form,
                                    formUtil,
                                    ajax,
                                    exhibitorApi,
                                    layer,
                                    toast) {
    var $ = layui.jquery;
    var f = layui.form;
    var index;

    var data = ajax.getAllUrlParam();
    var categoryIds ;//分类相关的信息

    //加载展商信息
    ajax.request(exhibitorApi.getUrl('getExhibitor'), null, function(result) {
        if(result.status == true){
            var company = result.data;
            formUtil.renderData($('#update-category-form'), company);

            categoryIds = company.categoryIds;
        } else {
            toast.error("查询展商失败");
        }
    });

    $('#categoryName').click(function() {
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
                        layer.msg("修改成功", {icon: 1, time: 100}, function () {
                            setTimeout('window.location.reload()', 100);
                        });
                    }
                });
            }

        });
    });

});