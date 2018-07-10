var webName = getWebName();

layui.config({
	base: webName + '/js/modules/',
    version: 2018011001
});

var requireModules = [
	'element',
	'form',
	'layer',
	'request',
	'form-util',
	'buser-api',
	'table-util',
	'btns',
	'authority',
	'toast',
    'table'

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
    buserApi,
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
		renderTable: function() {
            return $table.render({
                elem: '#buser-list'
                ,height: 'full-100'
                ,url: buserApi.getUrl('buserList').url
				,method: 'post'
                ,page: true
                ,limits:[10,50,100,200]
                ,cols: [[
                    {type:'numbers'},
                    {field: 'userId', title: '业务员ID', width:100},
                    {field: 'userName', title: '业务员姓名', width:150},
                    {field: 'phone', title: '手机号', width:120},
                    {field: 'status', title: '状态', width:100, templet: function (d) {
                            if(d.status == 1){
                                return '<span>审核通过</span>';
                            } else if(d.status == 0) {
                                return '<span>待审</span>';
                            } else {
                                return '<span>打回</span>';
                            }
                        }},

                    {field: 'position', title: '职位', width:120},
                    {field: 'openId', title: 'openId', width:200},
                    {field: 'uuid', title: 'uuid', width:200},
                    {field: 'cTime', title: '创建时间', width:160, templet: function (d) {
						return moment(d.cTime).format("YYYY-MM-DD HH:mm:ss");
                    }},
                    {field: 'mTime', title: '修改时间', width:160, templet: function (d) {
                            return moment(d.mTime).format("YYYY-MM-DD HH:mm:ss");
                        }},
                    {fixed: 'right',width:80, align:'center', toolbar: '#barDemo'}
                ]]
            });
		},

        review: function(rowdata) {
            var url = request.composeUrl(webName + '/views/buser/buser-update.html', rowdata);
            var index = layer.open({
                type: 2,
                title: "授权管理",
                area: ['550px', '350px'],
                offset: '5%',
                scrollbar: false,
                content: url,
                success: function(ly, index) {
                    // layer.iframeAuto(index);
                }
            });
		},

		refresh: function() {
            mainTable.reload();
		},

		bindEvent: function() {
            $table.on('tool(test)', function(obj){
                var data = obj.data;
                if(obj.event === 'row-review'){
                    MyController.review(data);
                }

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