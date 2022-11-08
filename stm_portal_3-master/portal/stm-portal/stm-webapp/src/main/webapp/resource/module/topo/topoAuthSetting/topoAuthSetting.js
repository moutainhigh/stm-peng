/**
 * 拓扑权限设置
 */
function TopoAuthSetting(args){
	this.args=args;
	this.root = $("#oc_topo_auth_setting");
	this.datagridDiv = $("#userAuthDatagrid");
	this.authEdit = "authEdit";
	this.authSelect = "authSelect";
};

TopoAuthSetting.prototype={
	init:function(){	//初始化页面
		var ctx = this;
		//检索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")] = tmp;
		});
		//初始化布局
		this.root.layout({
			fit:true
		});
		//初始化左侧拓扑列表
		this.fields.authAccordion.accordion({fit:true,width:200});
		//初始化二层网络拓扑列表
		this.fields.secondTopo.tree({
			url:oc.resource.getUrl("topo/0/subTopos.htm"),
			method:"get",
			onSelect:function(node){
				ctx.selectedNode = node;
			},
			onClick:function(node){
				ctx.menuClick(node,"secondTopo");
			}
		});
		//初始化三层网络拓扑列表
		this.fields.thirdTopo.tree({
			url:oc.resource.getUrl("topo/1/subTopos.htm"),
			method:"get",
			onSelect:function(node){
				ctx.selectedNode = node;
			},
			onClick:function(node){
				ctx.menuClick(node,"thirdTopo");
			}
		});
		//初始化机房视图列表
		this.fields.machineRoom.tree({
			url:oc.resource.getUrl("topo/2/subTopos.htm"),
			method:"get",
			onSelect:function(node){
				ctx.selectedNode = node;
			},
			onClick:function(node){
				ctx.menuClick(node,"machineRoom");
			}
		});
		
		this.regEvent();
		this.fields.secondTopo.siblings("div").trigger("click");
	},
	//检测是否存在全选框
	existAllSelcet:function(){
		if(!$(".title_edit_position").length){
			this.initUI();
		}
	},
	//绑定事件
	regEvent:function(){
		var ctx = this;
		this.fields.secondTopo.siblings("div").on("click",function(){
			ctx.cancelMenuChecked();
			ctx.existAllSelcet();
			ctx.selectedNode = {id:0};
			ctx.initDatagrid(0);
		});
		this.fields.thirdTopo.siblings("div").on("click",function(){
			ctx.cancelMenuChecked();
			ctx.selectedNode = {id:1};
			ctx.initDatagrid(1);
		});
		this.fields.machineRoom.siblings("div").on("click",function(){
			ctx.cancelMenuChecked();
			ctx.selectedNode = {id:2};
			ctx.initDatagrid(2);
		});
		this.fields.mapTopo.siblings("div").on("click",function(){
			ctx.cancelMenuChecked();
			ctx.selectedNode = {id:3};
			ctx.initDatagrid(3);
		});
	},
	//取消选中的树节点
	cancelMenuChecked:function(){
		$("li div.tree-node").removeClass("tree-node-selected");
	},
	menuClick:function(node,subtopo){	//左侧菜单项单击
		var topo;
		if("secondTopo" == subtopo) {
			topo = this.fields.secondTopo;
		}else if("thirdTopo" == subtopo){
			topo = this.fields.thirdTopo;
		}else if("machineRoom" == subtopo){
			topo = this.fields.machineRoom;
		}else{
			topo = this.fields.mapTopo;
		}
		var isLeaf = topo.tree("isLeaf",node.target);
		if(!isLeaf){
			if("closed" == node.state){
				topo.tree('expand',node.target);
			}else{
				topo.tree('collapse',node.target);
			}
		}
		//初始化表格
		this.initDatagrid(node.id);
	},
	initUI:function(){	//全选复选框
		var ctx = this;
		var $allEditDom = $("<label class='title_edit_position'><input type='checkbox' class='title_inline'>编辑权限</label>");
		$allEditDom.appendTo(ctx.root);
		var $allSelectDom = $("<label class='title_select_position'><input type='checkbox' class='title_inline'>查看权限</label>");
		$allSelectDom.appendTo(ctx.root);
		
		this.$allEdit = $allEditDom.find("[type='checkbox']");
		this.$allSelect = $allSelectDom.find("[type='checkbox']");
		
		//全选编辑权限框
		this.$allEdit.on("click",function(){
			var ck = $(this).is(":checked");
			ctx.checkSlect(ctx.authEdit,ck);	//所有列表【编辑】框赋值
            /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 start*/
            /*ctx._checkAll_select(ck);			//处理【查看全选】框（所有【查看】框选中/取消，并可用/不可用）*/
            /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 end*/
			var rows = ctx.datagridDiv.datagrid("getRows");
			$.each(rows,function(index,row){
				ctx.changeCheckVal(row,ck,ctx.authEdit);
			})
		});
		
		//【全选查看】权限框值改变事件
		this.$allSelect.on("change",function(){
			var check = $(this).is(":checked");
			ctx.checkSlect(ctx.authSelect,check);
			var rows = ctx.datagridDiv.datagrid("getRows");
			$.each(rows,function(index,row){
				ctx.changeCheckVal(row,check,ctx.authSelect);
			})
		});
	},
	//可用/不可用【查看全选】框
	_checkAll_select:function(check){
		//有编辑权限就有查看权限
		this.$allSelect.prop("checked",check);	//【查看全选】框赋值
		this.$allSelect.prop("disabled",check);
		this.$allSelect.trigger("change");		//触发列表【查看】框赋值
		$("[data-select='"+this.authSelect+"']").prop("disabled",check);
	},
	//单个查看框
	_checkSingle_select:function(el,check){
		if(check){
			el.attr("disabled",true);
		}else{
			el.attr("disabled",false);
		}
	},
	checkSlect:function(dataSelect,checked){
		$("[data-select='"+dataSelect+"']").prop("checked",checked);
	},
	initDatagrid:function(id){	//加载表格数据
		var ctx = this;
		var searchUrl = "topo/auth/setting.htm?subtopoId="+id;
		var	datagridDiv = ctx.datagridDiv;
		var	datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			url : oc.resource.getUrl(searchUrl),
			singleSelect:false,
			pagination:false,
			columns:[[
		         {field:'userName',title:'用户名',width:100},
		         {field:'editAuth',width:100,formatter:function(value,row,index){
		        		 return ctx.formatCheckbox(value,row,index,'编辑',ctx.authEdit);
		         	}
		         },
		         {field:'selectAuth',width:100,formatter: function(value,row,index){
		        		 return ctx.formatCheckbox(value,row,index,'查看',ctx.authSelect);
		 			}
		         }
	         ]],
	         loadFilter:function(data){
	        	 return {total:data.data.length,rows:data.data};
	        },
	        //数据加载成功后，初始化复选框状态
	        onLoadSuccess:function(data){
	        	var $selectCheck = $editCheck = true;	//全选框
	        	$.each(data.rows,function(index,row){
	        		//有编辑、查看权限未选，则全选不勾上
	        		if(!row.editAuth) $editCheck = false;
	        		if(!row.selectAuth) $selectCheck = false;
	        	});
	        	ctx.$allEdit.attr("checked",$editCheck);
	        	ctx.$allSelect.attr("checked",$selectCheck);
	        }
		});
	},
	//格式化复选框
	formatCheckbox:function(value,row,index,text,dataSelect){
		var ctx = this;
	   	var tmp = $("<label><input type='checkbox' class='title_inline' data-select='"+dataSelect+"' />"+text+"</label>");
	   	var tmpCheckbox = tmp.find("[type='checkbox']");
        /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 start*/
        //初始化选中状态
        /*if(ctx.authSelect == dataSelect && value && row.editAuth){
            tmpCheckbox.attr("disabled",true);
        }*/
        /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 end*/
	   	tmpCheckbox.attr("checked",value);
	   	//注册点击事件
	   	tmpCheckbox.on("click",function(event){
	   		var checked = $(this).is(":checked");
	   		ctx.checkAllSelected(dataSelect);
	   		//点击【编辑】列表框
	   		if(ctx.authEdit == dataSelect){
                /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 start*/
                /*var dom = $(this).parents("tr").find("[data-select='"+ctx.authSelect+"']");
                if($(this).prop("checked")){
                    dom.prop("disabled",true);
                    dom.prop("checked",true);
                    ctx.checkAllSelected(ctx.authSelect);
                }else{
                    dom.attr("disabled",false);
                    ctx.$allSelect.attr("disabled",false);
                }*/
                if ($(this).prop("checked")) {
                    ctx.checkAllSelected(ctx.authSelect);
                }
                /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 end*/
	   		}
	   		ctx.changeCheckVal(row,checked,dataSelect);
	   		ctx.checkAllSelected(dataSelect);	//是否全选
	   	});
	   	return tmp.get(0);
	},
	//点击列表复选框是否选中全选框
	checkAllSelected:function(dataSelect){
		var totalRows = $("[data-select='"+dataSelect+"']").length;	//总行数
		var checkedRows = 0;	//选中行数
		$("[data-select='"+dataSelect+"']").each(function(){
			if($(this).is(":checked")) checkedRows += 1;
		});
		var allSelectClass = ".title_edit_position";
		if(this.authSelect == dataSelect) allSelectClass = ".title_select_position";
		var allCk = (totalRows == checkedRows);
		$(allSelectClass).find("[type='checkbox']").attr("checked",allCk);	//是否全选
        /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 start*/
        /*if(allCk && this.authEdit==dataSelect){
            this.$allSelect.prop("disabled",true);
        }*/
        /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 end*/
	},
	save:function(dia){	//保存
		oc.util.ajax({
			url : oc.resource.getUrl('topo/auth/save.htm'),
			failureMsg:'保存失败',
			successMsg:null,
			data : {
				subtopoId : this.selectedNode.id,
				authSetting : this.datagridDiv.datagrid("getRows")
			},
			success : function(data) {
				alert(data.data);
			}
		});
	},
	changeCheckVal:function(row,checked,dataSelect){//点击编辑复选框
		var checkedVal = checked==false?0:1;
		if(this.authEdit == dataSelect){
			row.editAuthInt = checkedVal;
            /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 start*/
            /*if(checkedVal){
                row.selectAuthInt = checkedVal;
            }*/
            /* 任务 #46982 【西安合作项目】_ITM_拓扑管理_细化 huangping 2017/11/22 end*/
		}else{
			row.selectAuthInt = checkedVal;
		}
	}
};
//#sourceURL=topoAuthSetting.js