/**
 * 评论
 */
var requireModules = [
    'base-url'
];

window.top.registeModule(window, requireModules);
layui.define('base-url', function (exports) {
    var $ = layui.jquery;
    var baseApi = layui['base-url'];

    var url = {
        namespace: '../interact/',
        "interactList": {
            type: 'POST',
            url: "interactList.do"
        },
        "addInteract": {
            type: 'POST',
            url: "addInteract.do"
        }
    };

    var result = $.extend({}, baseApi, url);

    exports('user-interact-api', result);
});