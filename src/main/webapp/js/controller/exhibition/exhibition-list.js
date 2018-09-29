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
	'exhibition-api',
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
    exhibitionApi,
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
                elem: '#exhibition-list'
                ,height: 'full-100'
                ,url: exhibitionApi.getUrl('exhibitionList').url
				,method: 'post'
                ,page: true //开启分页
                ,limits:[10,50,100,200]
                ,cols: [[ //表头
                    {type:'numbers'},
                    {field: 'id', title: '展品ID', width:100},
                    {field: 'name', title: '产品名称', width:100},
                    {field: 'status', title: '状态', width:100, templet: function (d) {
                            if(d.status == 1){
                                return '<span>下架</span>';
                            } else {
                                return '<span>上架</span>';
                            }
                        }},

                    {field: 'categoryId', title: '分类id', width:100},
                    {field: 'image', title: '产品图片', width:170, templet: function (d) {
                            return "<a href='"+d.image+"' target='_blank'><img src='"+d.image+"' /></a>";
                        }},
                    {field: 'regImage', title: '首页推荐图片', width:155, templet: function (d) {
                            return "<a href='"+d.regImage+"' target='_blank'><img src='"+d.regImage+"' /></a>";
                        }},
                    {field: 'pdfName', title: '说明书的名字', width:120},
                    {field: 'pdfUrl', title: '说明书下载地址', width:300},
                    {field: 'mTime', title: '修改时间', width:160, templet: function (d) {
						return moment(d.mTime).format("YYYY-MM-DD HH:mm:ss");
                    }},
                    {field: 'cTime', title: '录入时间', width:160, templet: function (d) {
						return moment(d.cTime).format("YYYY-MM-DD HH:mm:ss");
					}},
                    {fixed: 'right',width:180, align:'center', toolbar: '#barDemo'}
                ]]
            });
		},

		add: function() {
			var index = layer.open({
				type: 2,
				title: "添加产品",
                area: ['900px', '450px'],
				offset: '5%',
				scrollbar: false,
				content: webName + '/views/exhibition/exhibition-add.html',
				success: function(ly, index) {
					// layer.iframeAuto(index);
				}
			});
		},

		modify: function(rowdata) {
			var url = request.composeUrl(webName + '/views/exhibition/exhibition-update.html', rowdata);
			var index = layer.open({
				type: 2,
				title: "修改产品",
                area: ['900px', '450px'],
				offset: '5%',
				scrollbar: false,
				content: url,
				success: function(ly, index) {
					// layer.iframeAuto(index);
				}
			});
		},

        view: function(rowdata) {
            var url = request.composeUrl(webName + '/views/exhibition/exhibition-view.html', rowdata);
            var index = layer.open({
                type: 2,
                title: "查看产品",
                area: ['900px', '450px'],
                offset: '5%',
                scrollbar: false,
                content: url,
                success: function(ly, index) {
                    // layer.iframeAuto(index);
                }
            });
        },

		delete: function(rowdata) {
			layer.confirm('确认删除数据?', {
				icon: 3,
				title: '提示',
				closeBtn: 0
			}, function(index) {
				layer.load(0, {
					shade: 0.5
				});
				layer.close(index);

				request.request(exhibitionApi.getUrl('deleteExhibition'), {
					id: rowdata.id
				}, function() {
					layer.closeAll('loading');
					toast.success('成功删除！');
					MyController.refresh();
				},true,function(){
					layer.closeAll('loading');
				});
			});
		},

		refresh: function() {
            mainTable.reload();
		},

		bindEvent: function() {
            $table.on('tool(test)', function(obj){
                var data = obj.data;
                if(obj.event === 'row-view'){
                    MyController.view(data);
                } else if(obj.event === 'row-edit'){//编辑
                    MyController.modify(data);
                } else if(obj.event === 'row-delete'){//删除
                    MyController.delete(data);
                }

            });

			//点击查询按钮
			$('#search-btn').on('click', function() {
                mainTable.reload({
                    where: MyController.getQueryCondition()
                });
			});

            //点击刷新
            $('body').on('click', '.refresh', MyController.refresh);
			//点击添加
			$('body').on('click', '.add', MyController.add);

		}
	};

	window.list = {
		refresh: MyController.refresh
	}

	MyController.init();

});