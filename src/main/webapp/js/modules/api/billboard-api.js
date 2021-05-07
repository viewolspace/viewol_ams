/**
 * 广告合作
 */
var requireModules = [
    'base-url'
];

window.top.registeModule(window, requireModules);
layui.define('base-url', function (exports) {
    var $ = layui.jquery;
    var baseApi = layui['base-url'];

    var url = {
        namespace: '../billBoard/',
        "billboardList": {
            type: 'POST',
            url: "billboardList.do"
        },
        "addBillBoard": {
            type: 'POST',
            url: "addBillBoard.do"
        }
    };

    var result = $.extend({}, baseApi, url);

    exports('billboard-api', result);
});