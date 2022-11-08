(function($) {
	
	function InspectDir(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	InspectDir.prototype={
		constructor : InspectDir,
		cfg : undefined,
		_planId: undefined,
		open:function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), 
				that = this, 
				type = that.cfg.type,
				_planId = that.cfg.id,
				reload = that.cfg.reload;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/inspect-management/inspect_plan_add_content_detail.html'),
				title : '巡检计划 - 巡检报告巡检目录设置<label style="font-weight:normal">(最多支持' + oc.module.inspect.plan.getProperties().directoryMax + '个章节/已设置<a class="directoryCount">0</a>章)</label>',
				width: 710,
				height : 400,
				top: 110,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._mainDiv.find("#insepct_manage_content_parentID").val(that.cfg.id);
					dlg.find(".easyui-linkbutton").linkbutton();
					that._init(dlg);
					//(type != 'add') && that._loadForm();//如果是编辑，加载域基本信息
				},
				onClose:function(){
					dlg.dialog("destroy");//add by sunhailiang on 20170615
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
					var form = that._mainDiv.find('#authForm');
					var inputs = form.find('input[name="inspectPlanItemName"]');
					for(var loop=0;loop<inputs.length;loop++){
						var content = $(inputs[loop]).val();
						if(content==''||content==undefined||content==null){
							alert("目录名字不能为空");
							return;
						}
					}
				
						that._mainDiv.find("#insepct_manage_content_deletedID").val(that._deleteID);
					
						var data=decodeURIComponent(dlg.find('#authForm').serialize(),true);
				
						$.ajax({
			           //     cache: true,
			                type: "POST",
			                url:oc.resource.getUrl('inspect/plan/updateInspectionItems.htm'),
			                data:data,
			                async: false,
			                error: function(request) {
			                    alert("Connection error");
			                },
			                success: function(data) {
			                   // $("#commonLay   out_appcreshi").parent().html(data);
			                }
			            }); 
						if(reload) reload();
						dlg.dialog('close');			
						}
					//}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
					/*	that._cancelBtn=true;*/
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_mainDiv : undefined,
		_id : '#inspectmanagement_content_detail',
		_deleteID : undefined, 
		
		_dirIndex: 1,
		
		_init: function(dlg) {
			
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			//设置报告名称
			var reportPrefix = '巡检报告名称：';
			that._mainDiv.find('.report-name-in-adding-dir').html(reportPrefix + (that.cfg.reportName || ''));
			
/*			that._mainDiv.find('#addInspectContentDetail').on('click', function(e) {
				addNewChildItem(that);
			});
*/
			that._mainDiv.on('click','.delete-dir', function(e) {
				delCurrentChildItem($(this));
			});
			var len = 0, id="";
				
			var content_id = that.cfg.id;
			if(content_id!=undefined&&content_id!=""){
				oc.util.ajax({
					url: oc.resource.getUrl('inspect/plan/loadInspectionItems.htm'),
	                data:{
	                	id:content_id
	                },
					async:false,
					success : function(data) {
						var flag = data.code == 200;
						if (flag) {
							var objs = data.data;		
							if(objs.length>0){
								for(var loop = 0; loop < objs.length;loop++){
									addNewEditChildItem(objs[loop].inspectPlanItemName,objs[loop].inspectPlanItemDescrible,objs[loop].id, that);
								}
							}else{
								addNewChildItem(that);
							}
						} else {
							alert('加载信息出错');
						}
					},
				});
			}
				
			function addNewChildItem(that){
				if(that._dirIndex > oc.module.inspect.plan.getProperties().directoryMax){
					alert('最多添加' + oc.module.inspect.plan.getProperties().directoryMax + '个章节');
					return;
				}
				var table=that._mainDiv.find("#insepct_manage_content_item_table");
					id = 'tr'+(len++);
				$('.directoryCount').html(that._dirIndex);
				var row = $("<tr id="+id+"></tr>");
				var td0 = $("<td style='width: 38px; text-align: center;'>"+(that._dirIndex++)+"</td>");
				var td1 = $("<td class='china-oc-inspect-management-td1_width'><input type='hidden' name='id' id='hidden"+id+"' value='-1'/><input type='text' class='china-oc-inspect-management-put_width' style='width: 100%;' name='inspectPlanItemName' maxlength='16'/></td>");
				var td2= $("<td><input type='text' class='china-oc-inspect-management-put_width' style='width: 100%;' name='inspectPlanItemDescrible' maxlength='29'/></td>");
				var td3 = $("<td class='china-oc-inspect-management-td3_width'><span class='delete-dir' title='删除该巡检章节'></span><span title='添加一条巡检章节' class='add-a-dir'></span></td>");
				row.append(td0);
				row.append(td1);
				row.append(td2);
				row.append(td3);
				initdraggable(table,id);
				table.append(row);
				
				table.find('.add-a-dir').linkbutton({
					iconCls: 'fa fa-plus',
					onClick: function() {
						addNewChildItem(that);
					}
				});
				table.find('.delete-dir').linkbutton({
					iconCls: 'fa fa-times-circle',
					onClick: function() {
						delCurrentChildItem($(this));
					}
				});
				
				//显示当前行的添加按钮，隐藏其他行的添加按钮
				row.find('.add-a-dir').removeClass('hide').attr("index","last");
				row.siblings().find('.add-a-dir').addClass('hide').removeAttr("index");
			}
			function addNewEditChildItem(val1,val2,val3, that){
				var table=that._mainDiv.find("#insepct_manage_content_item_table");
					id = 'tr'+(len++);
				$('.directoryCount').html(that._dirIndex);
				var row = $("<tr id="+id+"></tr>");
				var td0 = $("<td style='width: 38px; text-align: center;'>"+(that._dirIndex++)+"</td>");
				row.append(td0);
				var td1 = $("<td class='china-oc-inspect-management-td1_width'><input type='hidden' name='id' id='hidden"+id+"' value='"+val3+"'/><input type='text' class='china-oc-inspect-management-put_width' style='width: 100%;' name='inspectPlanItemName'  value='"+val1+"' maxlength='16'/></td>");
				row.append(td1);
				var td2= $("<td><input type='text' class='china-oc-inspect-management-put_width' style='width: 100%;' name='inspectPlanItemDescrible'  value='"+val2+"'  maxlength='29'/></td>");
				row.append(td2);
				var td3 = $("<td class='china-oc-inspect-management-td3_width'><span class='delete-dir' title='删除该巡检章节'></span><span title='添加一条巡检章节' class='add-a-dir'></span></td>");
				row.append(td3);
				initdraggable(table,id);
				table.append(row);
				
				table.find('.add-a-dir').linkbutton({
					iconCls: 'fa fa-plus',
					onClick: function() {
						addNewChildItem(that);
					}
				});
				table.find('.delete-dir').linkbutton({
					iconCls: 'fa fa-times-circle',
					onClick: function() {
						delCurrentChildItem($(this));
					}
				});
				
				//显示当前行的添加按钮，隐藏其他行的添加按钮
				row.find('.add-a-dir').removeClass('hide').attr("index","last");
				row.siblings().find('.add-a-dir').addClass('hide').removeAttr("index");
			}
			function initdraggable(sort,id){
				that._mainDiv.find('#'+id).on('mousedown',function(e){
		    		e.stopPropagation();
		    	});
    			sort.sortable({
    				opacity: 0.6,
    				revert: true
    			});
			}
			function delCurrentChildItem($this){
				var trs = $this.parents('table').find('tr');
				if(trs.length > 2) {
					var index = $this.parents('tr:first').attr('id');
					var deletedid = that._mainDiv.find("#hidden"+index).val();
					if(deletedid > -1){
						if(that._deleteID==undefined||that._deleteID==''){
							that._deleteID = deletedid;
						}else{
							that._deleteID = that._deleteID+","+deletedid;
						}
					}
					//$('tr[id="+index+"]').remove();//删除当前行
					$this.parents('tr:first').remove();
					that._dirIndex--;
					$('.directoryCount').html(that._dirIndex - 1);
					
					//显示最后一行tr的添加按钮
					var trsLen = trs.length;
					
					var row = trs[trsLen-("last"==$this.parent().find('.add-a-dir').attr("index")?2:1)];
					$(row).find('.add-a-dir').removeClass('hide').attr("index","last");
					$(row).siblings().find('.add-a-dir').addClass('hide').removeAttr("index");
				}
			}
			/**
			 * 向上移动tr
			 * */
			/*function arrow_up(id){
				var $this = that._mainDiv.find('#'+id);
				var table = that._mainDiv.find('#insepct_manage_content_item_table');
				var firstTr = table.find('tr:first');
				$this.on('click','.pop-arrow-up',function(){
					var prev = firstTr;
					$this.insertBefore(prev);
					checkArrowUp();
					checkArrowDown();
				});
				checkArrowUp();
			}*/
			/**
			 * 向下移动tr
			 * */		
		/*	function arrow_down(id){
				var $this = that._mainDiv.find('#'+id);
				$this.on('click','.pop-arrow-down',function(){
					var next = $this.next();
					$this.insertAfter(next);
					checkArrowDown();
					checkArrowUp();
			});
			checkArrowDown();
			}*/
			/**
			 * 控制向上箭头的显示仪隐藏
			 * */
		/*	function checkArrowUp(){
				var table = that._mainDiv.find('#insepct_manage_content_item_table');
				//var len = table.find('tr').length;
				var firstTr = table.find('tr:first');
				table.find('.firstTr').removeClass('firstTr');
				firstTr.find('.pop-arrow-up').addClass('firstTr');
			}*/
			/**
			 * 控制向下箭头的显示仪隐藏
			 * */
		/*	function checkArrowDown(){
				var table = that._mainDiv.find('#insepct_manage_content_item_table');
				var len = table.find('tr').length;
				var lastTr = table.find('tr:last');
				table.find('.lastTr').removeClass('lastTr');
				lastTr.find('.pop-arrow-down').addClass('lastTr');
			}*/
			
			
		}
		
	};//end
	
	 oc.ns('oc.module.inspect.inspectdir');
	 oc.module.inspect.inspectdir = {
		open : function(cfg) {
			new InspectDir(cfg).open();
		}
	};
})(jQuery); 
