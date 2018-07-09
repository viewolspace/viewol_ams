
/**
 * 广告管理api
 */
var requireModules =[
	'base-url'
];

window.top.registeModule(window,requireModules);
layui.define('base-url', function(exports) {
	var $ = layui.jquery;
	var baseApi = layui['base-url'];

	var url = {
		namespace: '../exhibitor/',
        "getExhibitor": {//查询展商信息
            type: 'POST',
            url: "getExhibitor.do"
        },
        "uploadLogo": {//上传展商Logo
            type: 'POST',
            url: "uploadLogo.do"
        } ,
        "uploadBanner": {//上传展商形象图
            type: 'POST',
            url: "uploadBanner.do"
        } ,
        "uploadImage": {//上传展商图片
            type: 'POST',
            url: "uploadImage.do"
        },
        "updateContent": {//修改展商介绍
            type: 'POST',
            url: "updateContent.do"
        },
        "uploadContentImage": {//展商富文本上传图片
            type: 'POST',
            url: "uploadContentImage.do"
        }


	}
	//下面这种避免不同api相同key取值相同的问题
	var result = $.extend({}, baseApi, url);

	exports('exhibitor-api', result);
});