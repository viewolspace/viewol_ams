
/**
 * 名片夹api
 */
var requireModules =[
	'base-url'
];

window.top.registeModule(window,requireModules);
layui.define('base-url', function(exports) {
	var $ = layui.jquery;
	var baseApi = layui['base-url'];

	var url = {
		namespace: '../card/',
		"cardList": {
			url: "cardList.do"
		}
	}
	var result = $.extend({}, baseApi, url);

	exports('card-api', result);
});