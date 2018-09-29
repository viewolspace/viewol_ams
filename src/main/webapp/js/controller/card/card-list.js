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
	'card-api',
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
layui.use(requireModules, function(
	element,
	form,
	layer,
	request,
	formUtil,
    cardApi,
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
		init: function() {
			var navId = request.getFixUrlParams("navId");

			var totalBtns = authority.getNavBtns(navId);
			var btnObjs = btns.getBtns(totalBtns);
			MyController.pageBtns = btns.getPageBtns(btnObjs);
			MyController.switchPageBtns = btns.getSwitchPageBtns(btnObjs);

			MyController.rowBtns = btns.getRowBtns(btnObjs);
			MyController.rowSwitchBtns = btns.getSwitchBtns(MyController.rowBtns);
			MyController.rowIconBtns = btns.getIconBtns(MyController.rowBtns);

			$('#page-btns').html(btns.renderBtns(MyController.pageBtns)+btns.renderSwitchBtns(MyController.switchPageBtns));
            btns.renderLayuiTableBtns(MyController.rowIconBtns, $("#barDemo"));

            mainTable = MyController.renderTable();
			MyController.bindEvent();
		},
		getQueryCondition: function() {
			var condition = formUtil.composeData($("#condition"));
			return condition;
		},
		renderTable: function() {
            return $table.render({
                elem: '#card-list'
                ,height: 'full-100'
                ,url: cardApi.getUrl('cardList').url
				,method: 'post'
                ,page: true
                ,limits:[10,50,100,200]
                ,cols: [[
                    {type:'numbers'},
                    {field: 'BUserName', title: '业务员', width:100},
                    // {field: 'shortName', title: '展商简称', width:100},
                    {field: 'fUserId', title: '客户id', width:80},
                    {field: 'fUserName', title: '客户姓名', width:100},
                    {field: 'FCompany', title: '客户公司', width:150},
                    {field: 'fPhone', title: '客户电话', width:120},
                    {field: 'fPosition', title: '客户职位', width:120},
                    {field: 'fEmail', title: '客户Email', width:150},
                    {field: 'FAge', title: '客户年龄', width:100},
                    {field: 'cTime', title: '创建时间', width:160, templet: function (d) {
						return moment(d.cTime).format("YYYY-MM-DD HH:mm:ss");
                    }}
                ]]
            });
		},

		refresh: function() {
            mainTable.reload();
		},

		bindEvent: function() {
			//点击查询按钮
			$('#search-btn').on('click', function() {
                mainTable.reload({
                    where: MyController.getQueryCondition()
                });
			});
		}
	};

	window.list = {
		refresh: MyController.refresh
	}

	MyController.init();

});