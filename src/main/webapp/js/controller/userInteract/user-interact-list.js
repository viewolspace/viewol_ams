var webName = getWebName();

layui.config({
    base: webName + '/js/modules/',
    version: true
});

var requireModules = [
    'element',
    'form',
    'layer',
    'request',
    'form-util',
    'user-interact-api',
    'table-util',
    'btns',
    'authority',
    'toast',
    'table',
    'valid-login'

];

registeModule(window, requireModules, {
    'good-api': 'api/good-api'
});

//参数有顺序
layui.use(requireModules, function (
    element,
    form,
    layer,
    request,
    formUtil,
    userInteractApi,
    tableUtil,
    btns,
    authority,
    toast,
    table
) {

    var $ = layui.jquery;
    var $table = table;
    var mainTable;
    var MyController = {
        init: function () {
            var navId = request.getFixUrlParams("navId");

            var totalBtns = authority.getNavBtns(navId);
            var btnObjs = btns.getBtns(totalBtns);
            MyController.pageBtns = btns.getPageBtns(btnObjs);
            MyController.switchPageBtns = btns.getSwitchPageBtns(btnObjs);

            MyController.rowBtns = btns.getRowBtns(btnObjs);
            MyController.rowSwitchBtns = btns.getSwitchBtns(MyController.rowBtns);
            MyController.rowIconBtns = btns.getIconBtns(MyController.rowBtns);

            $('#page-btns').html(btns.renderBtns(MyController.pageBtns) + btns.renderSwitchBtns(MyController.switchPageBtns));
            btns.renderLayuiTableBtns(MyController.rowIconBtns, $("#barDemo"));

            mainTable = MyController.renderTable();
            MyController.bindEvent();
        },
        getQueryCondition: function () {
            var condition = formUtil.composeData($("#condition"));
            return condition;
        },
        renderTable: function () {
            return $table.render({
                elem: '#interact-list'
                , height: 'full-100'
                , url: userInteractApi.getUrl('interactList').url
                , method: 'post'
                , page: true //开启分页
                , limits: [10, 50, 100, 200]
                , cols: [[
                    {type: 'numbers'},
                    {field: 'id', title: '评论ID', width: 100},
                    {field: 'companyId', title: '展商ID', width: 100},
                    {
                        field: 'classify', title: '评论', width: 100, templet: function (d) {
                            if (d.classify == 1) {
                                return '<span>展商</span>';
                            } else if (d.classify == 2) {
                                return '<span>产品</span>';
                            }
                        }
                    },

                    {
                        field: 'type', title: '类型', width: 100, templet: function (d) {
                            if (d.type == 1) {
                                return '<span>围观</span>';
                            } else if (d.type == 2) {
                                return '<span>点赞</span>';
                            } else if (d.type == 3) {
                                return '<span>评论</span>';
                            }
                        }
                    },

                    {field: 'thirdId', title: '展商（产品）ID', width: 150},
                    {field: 'userId', title: '用户ID', width: 200},
                    {field: 'userName', title: '用户名称', width: 200},
                    {field: 'comment', title: '评论内容', width: 200},
                    {field: 'reply', title: '回复内容', width: 200},
                    {
                        field: 'cTime', title: '创建时间', width: 160, templet: function (d) {
                            return moment(d.cTime).format("YYYY-MM-DD HH:mm:ss");
                        }
                    },
                    {fixed: 'right', width: 120, align: 'center', toolbar: '#barDemo'}
                ]]
            });
        },

        reply: function (rowdata) {
            console.log(rowdata);
            var url = request.composeUrl(webName + '/views/userInteract/user-interact-add.html', rowdata);
            var index = layer.open({
                type: 2,
                title: "评论回复",
                area: ['1000px', '600px'],
                offset: '5%',
                scrollbar: false,
                content: url,
                success: function (ly, index) {
                    // layer.iframeAuto(index);
                }
            });
        },

        refresh: function () {
            mainTable.reload();
        },

        bindEvent: function () {
            $table.on('tool(test)', function (obj) {
                var data = obj.data;
                if (obj.event === 'row-reply') {//回复
                    MyController.reply(data);
                }
            });

            //点击查询按钮
            $('#search-btn').on('click', function () {
                mainTable.reload({
                    where: MyController.getQueryCondition()
                });
            });

            //点击刷新
            $('body').on('click', '.refresh', MyController.refresh);
        }
    };

    window.list = {
        refresh: MyController.refresh
    }

    MyController.init();

});