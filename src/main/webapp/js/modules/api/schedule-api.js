
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
		namespace: '../schedule/',
		"scheduleList": {
			url: "scheduleList.do"
		} ,
        "addSchedule": {
            type: 'POST',
            url: "addSchedule.do"
        },
        "updateSchedule": {
            type: 'POST',
            url: "updateSchedule.do"
        } ,
        "deleteSchedule": {
            url: "deleteSchedule.do"
        },
        "scheduleUserList": {//活动报名查询
            type: 'POST',
            url: "scheduleUserList.do"
        }
	}
	var result = $.extend({}, baseApi, url);

	exports('schedule-api', result);
});