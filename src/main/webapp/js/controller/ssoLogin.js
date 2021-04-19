var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: true
});


var requireModules = [
    'form',
    'layer',
    'login',
    'login-api',
    'key-bind',
    'request',
    'toast'
];

registeModule(window, requireModules);

layui.use(requireModules, function (
    form,
    layer,
    login,
    loginApi,
    keyBind,
    ajax,
    toast) {

    var param = ajax.getAllUrlParam();

    login.ssoLogin(param, function () {
        toast.msg("统一信用代码登录失败");
    });
})