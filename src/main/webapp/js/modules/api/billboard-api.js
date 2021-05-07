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
        namespace: '../billboard/',
        "billboardList": {
            url: "billboardList.do"
        },
        "addBillboard": {
            type: 'POST',
            url: "addBillboard.do"
        }
    };

    var result = $.extend({}, baseApi, url);

    exports('billboard-api', result);
});