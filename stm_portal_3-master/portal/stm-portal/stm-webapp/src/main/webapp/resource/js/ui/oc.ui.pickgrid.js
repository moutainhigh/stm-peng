/**
 * cfg{ selector:容器选择器, leftColumns:左边树‘Grid列设置， rightColumns:右边树Grid列设置，
 * leftUrl:左右GridUrl 
 * rightUrl:右边GridUrl 
 * isInteractive:是否显示中间可移动组件，默认显示 
 * leftOptions:{}
 * rightOptions:{}
 * moveBeforeEvent(row,direction) pickGrid 移动前事件
 * onMoveSuccess(srcGridData,targetGridData) pickGrid移动完成事件
 * }
 * 1
 * 方法： getLeftSelectionsRows 获取左边Grid选中行 getRightSelectionsRows 获取右边Grid选中行
 * getLeftRows 获取左边Grid所有行 getRightRows 获取右边Grid所有行
 * reload(position,param) 刷新datagrid数据，param查询参数，position（l/left|r/right)左边或右边
 * loadData(position,data) 重新加载datagrid数据，data要加载的数据，position（l/left|r/right)左边或右边
 * @author zhangjunfeng
 */
(function($) {
	var myGrid;
	function pickgrid(cfg) {
		var _cfg=this._cfg=$.extend({},this._defaults,cfg);
		if(this.checkCfg(_cfg)){
			this.selector = (typeof _cfg.selector == 'string') ? $(_cfg.selector): _cfg.selector;
			this._initPanel(_cfg);
			this._loadData(_cfg);
		}
	}
	
	// 移动数据
	function moveGrid(srcGrid,targetGrid,direction){
		var srcDataGrid =  $("#"+srcGrid);
		var targetDataGrid = $("#"+targetGrid);
		var rows = srcDataGrid.datagrid('getSelections');
		if(rows && rows.length>0){
			oc.ui.progress();
			setTimeout(function(){
				setTimeout(function(){
					function addGroupRows(targetGrid,rows){
						if(rows && rows.length>0){
							var groupSize = 100;
							var rowsize = rows.length;
							var groupCount = rowsize/groupSize;
							var sc = Math.ceil(groupCount);
							var i=0;
							function groupRows() {
								if(i<sc){
									var groupList = [];
									if(i+1==sc){
										groupList = rows.slice(i*groupSize,rows.length)
									}else{
										groupList = rows.slice(i*groupSize,(i+1)*groupSize);
									}
									setTimeout(function(){
										addRows(targetGrid,groupList);
										i++;
										groupRows();
									},0);
								}
							}
							groupRows();
						}
					}
					if(myGrid._cfg.moveBeforeEvent){
						var newRows = myGrid._cfg.moveBeforeEvent(rows,direction);
						if(isArray(newRows)){
							addGroupRows(targetGrid,newRows);
						}				
					}else{
						addGroupRows(targetGrid,rows);
					}
				},0);
				setTimeout(function(){
					//删除行
					if(rows && rows.length>0){
						var groupSize = 100;
						var rowsize = rows.length;
						var groupCount = rowsize/groupSize;
						var sc = Math.ceil(groupCount);
						var i=0;
						function groupRows() {
							if(i<sc){
								var groupList = [];
								if(i+1==sc){
									groupList = rows.slice(i*groupSize,rows.length)
								}else{
									groupList = rows.slice(i*groupSize,(i+1)*groupSize);
								}
								setTimeout(function(){
									i++;
									deleteRows(srcGrid,groupList,(i==sc)?true:false,targetGrid,direction,groupRows);
								},0);
							}
						}
						groupRows();
						
					}
				},0);
			},350);
		}else{
			alert("请选择要移动的数据！");
		}
	}
	
	function addRows(targetGrid,rows){
		var grid = $("#"+targetGrid);
		var i=0;
		append();
		function append(){
			if(i<rows.length){
				setTimeout(function(){
					grid.datagrid('appendRow',rows[i]);
					var index = grid.datagrid('getRowIndex',rows[i]);
					grid.datagrid('scrollTo',index);
					i++;
					append();
				},0);
			}else{
				grid.datagrid("unselectAll");
				grid.datagrid("uncheckAll");
			}
		}
	}
	
	/**
	 * targetGrid 要删除的目标GRID
	 * rows要删除的行
	 * isClose是否移动完成，关闭进度条
	 * anotherGrid 另一个GRID
	 * */
	function deleteRows(targetGrid,rows,isClose,anotherGrid,direction,groupRows){
		var grid = $("#"+targetGrid);
		var i=0;
		delRow();
		function delRow(){
			if(i<rows.length){
				setTimeout(function(){
					var index = grid.datagrid("getRowIndex",rows[i]); 
					grid.datagrid('deleteRow',index);
					grid.datagrid('scrollTo',index);
					i++;
					delRow();
				},0);
			}else{
				if(isClose){
					var srcGridData = grid.datagrid('getRows');
					var targetGridData = $("#"+anotherGrid).datagrid('getRows');
					if(!srcGridData || srcGridData.length==0){
						grid.prev().find("input[type=checkbox]").attr("checked",false);
					}
					myGrid._cfg.onMoveSuccess&&myGrid._cfg.onMoveSuccess(srcGridData,targetGridData,direction);
					$.messager.progress('close');
				}else{
					groupRows();
				}
				
			}
		}
	}
	
	/**
	 * 当移动的数据大于200条时候的处理方法
	 * */
	function bigDataAddMove(targetGrid,rows){
		var targetDataGrid = $("#"+targetGrid);
		var targetAllRows = targetDataGrid.datagrid('getRows');
		for(var i=0;i<rows.length;i++){
			targetAllRows.push(rows[i]);
		}
		targetDataGrid.datagrid("loadData",targetAllRows);
	}
	
	pickgrid.prototype = {
		isInteractive:true,// 是否显示可移动箭头
		constructor : pickgrid,
		leftGridId:'',
		rightGridId:'',
		leftGrid:null,
		rightGrid:null,
		_cfg:undefined,
		_dom : {
			div : '<div class="oc-pickList" style="height:100%;">'
					+ '<div class="pickList-left-width">'
					+ '<div class="pickList-left">' + '<div class="oc_ui_pickgrid" style="height: 100%;overflow: auto;"></div>'
					+ '</div>' + '</div>' + '<div class="pickList-center">'
					+ '<div class="tab-tree-arrow">'
					+ '<a class="tree-arrow-right"></a>'
					+ '<a class="tree-arrow-left"></a>' + '</div>' + '</div>'
					+ '<div class="pickList-right-width">'
					+ '<div class="pickList-right">'
					+ '<div class="oc_ui_pickgrid" style="height: 100%; overflow: auto;"></div>' + '</div>' + '</div>'
					+ '</div>'
		},
		_defaults:{
			selector : '',// 放置pickTree的目标容器
			leftColumns:[],
			rightColumns:[],
			leftUrl:'',
			leftQueryForm:undefined,
			rightUrl:'',
			rightQueryForm:undefined,
			isInteractive:true
		},
		_initPanel : function(cfg) {// 初始化pickTree面板
			var leftId = this.leftGridId = oc.util.generateId();// 生成左边树ID
			var rightId = this.rightGridId = oc.util.generateId();// 生成右边树ID
			var div = $(this._dom.div);
			div.find('.pickList-left').css('height',this.selector.height());
			div.find('.pickList-right').css('height',this.selector.height());
			div.find('.pickList-left .oc_ui_pickgrid').attr('id', leftId);
			div.find('.pickList-right .oc_ui_pickgrid').attr('id', rightId);
			div.appendTo(this.selector);
			if(!cfg.isInteractive){
				div.find('.tab-tree-arrow').hide();
			}else{
				div.find('.tree-arrow-right').click(function(){
					moveGrid(leftId,rightId,"right");// 绑定向右移箭头事件
				});
				div.find('.tree-arrow-left').click(function() {
					moveGrid(rightId, leftId,"left");// 绑定向左移箭头事件
				});
			}
		},
		_loadData:function(cfg){
			var leftGridId = this.leftGridId;
			var rightGridId = this.rightGridId;
			var leftOptions = {
					selector:"#"+leftGridId,
					url:cfg.leftUrl,
					pagination:false,
					columns:cfg.leftColumns,
					autoRowHeight:false,
					queryForm:cfg.leftQueryForm
				};
			this.leftGrid = oc.ui.datagrid($.extend({},leftOptions,this._cfg.leftOptions));
			var rightOptions = {
					selector:"#"+rightGridId,
					url:cfg.rightUrl,
					pagination:false,
					columns:cfg.rightColumns,
					autoRowHeight:false,
					queryForm:cfg.rightQueryForm
				};
			this.rightGrid =  oc.ui.datagrid($.extend({},rightOptions,this._cfg.rightOptions));
		},
		getLeftSelectionsRows:function(){
			return this.leftGrid.getSelections();
		},
		getRightSelectionsRows:function(){
			return this.rightGrid.getSelections();
		},
		getLeftRows:function(){
			var leftGrid = $("#"+this.leftGridId);
			return leftGrid.datagrid("getRows");
		},
		getRightRows:function(){
			var rightGrid = $("#"+this.rightGridId);
			return rightGrid.datagrid("getRows");
		},
		reload:function(position,param){
			if(position=='l' || position=="left"){
				this.leftGrid.reLoad(param);
			}else if(position=='r' || position=="right"){
				this.rightGrid.reLoad(param);
			}
		},
		loadData:function(position,data){
			if(position=='l' || position=="left"){
				this.leftGrid.selector.datagrid("loadData",data);
			}else if(position=='r' || position=="right"){
				this.rightGrid.selector.datagrid("loadData",data);
			}
		},
		checkCfg : function(cfg) {
			if (cfg) {
				if (!cfg.selector) {
					alert('没有提供选择器或者jquery对象', 'error');
					return false;
				}
				return true;
			}
			return false;
		}
	};
	oc.ui.pickgrid = function(cfg) {
		myGrid = new pickgrid(cfg);
		return myGrid;
	};

	var isArray = function(obj){
		return Object.prototype.toString.call(obj) === '[object Array]';   
	}
})(jQuery);
