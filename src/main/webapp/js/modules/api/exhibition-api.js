
/**
 * 展品api
 */
var requireModules =[
	'base-url'
];

window.top.registeModule(window,requireModules);
layui.define('base-url', function(exports) {
	var $ = layui.jquery;
	var baseApi = layui['base-url'];

	var url = {
		namespace: '../exhibition/',
		"exhibitionList": {
			url: "exhibitionList.do"
		} ,
        "uploadImg": {//上传图片
            type: 'POST',
            url: "uploadImg.do"
        } ,
        "uploadContentImage": {//上传产品介绍富文本图片
            type: 'POST',
            url: "uploadContentImage.do"
        } ,
        "uploadPdf": {//上传产品pdf
            type: 'POST',
            url: "uploadPdf.do"
        } ,
        "addExhibition": {
            type: 'POST',
            url: "addExhibition.do"
        },
        "updateExhibition": {
            type: 'POST',
            url: "updateExhibition.do"
        } ,
        "deleteExhibition": {
            url: "deleteExhibition.do"
        } ,
        "getExhibitionCategory": {
            url: "getExhibitionCategory.do"
        },
        "getExhibition": {
            type: 'POST',
            url: "getExhibition.do"
        }


	}
	var result = $.extend({}, baseApi, url);

	exports('exhibition-api', result);
});