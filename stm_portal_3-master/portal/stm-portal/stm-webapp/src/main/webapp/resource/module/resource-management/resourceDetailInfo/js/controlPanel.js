(function($) {
	var instance_id = null;
	function controlPanelClass() {
	}
	
	controlPanelClass.prototype = {
		constructor : controlPanelClass,	
		onselects: false,
		open : function(resourceId,childType) {
			var Idx=0;
			var childType = childType;
			instance_id = resourceId;
			var controlPanelDiv = $('#control_panel_Div');
			var that = this;
			var leftDom = $("<div/>"), leftUpDom = $("<div/>"), leftDownDom = $("<div/>");
			leftDom.append(leftDownDom).append(leftUpDom);
			controlPanelDiv.append(leftDom);
			
			leftDom.addClass("intro").css('float', 'left').width('100%').height('166px');
			leftUpDom.addClass("intro intro-panel").css('overflow-y', 'auto').css('border', 'none').height('449px');
			leftDownDom.addClass("intro intro-bgcolor").width('100%');
			
			var state = ['green', 'green-red', 'red', 'white'];
			var stateName = ['UP', '协议 DOWN', '管理 DOWN', '未监控'];
			var childValue = "NetInterface";
			
			for(var i = 0; i < state.length; i ++){
				var stateNode = $("<div/>").attr('state', state[i])
								.append($("<span/>").addClass("interface-eq interfaceSmall-" + state[i]).attr('style','display:inline-block;'))
								.append($("<span/>").addClass('oc-spanlocate').height('100%').html(stateName[i]));
				leftDownDom.append(stateNode);
			}
			leftDownDom.append($("<div/>").addClass('locate-right')
				.append("<span><input class='logicInterfaceOnOff' type='checkbox' checked=checked style='vertical-align:middle;'></span>").append("<span>显示逻辑接口</span>"));
			
			if(childType.childrenType!=undefined && childType.childrenType.length>0){
				for(var i = 0; i < childType.childrenType.length; i ++){
					if(childType.childrenType[i].type=="NetInterface"){
						childValue = childType.childrenType[i].type;
					}
				}
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getChildInstance.htm'),
				data : {
					instanceId : instance_id,
					//childType : childType.childrenType[childType.childrenType.length-1].type
					childType : childValue
				},
				
				successMsg:null,
				success : function(json) {
					if (json.code == 200) {
						var childArray = [], noLogicInterfaceArray = [], noLogicInterfaceNo = 0;
						for(var i = 0 ; i < json.data.length; i ++){
							var child = json.data[i];
							child.no = i;
							childArray.push(child);
							if(child.ifType == 'ethernetCsmacd' || child.ifType == 'gigabitEthernet' || child.ifType == 'fibreChannel'){
								child.noLogicNo = noLogicInterfaceNo ++;
								noLogicInterfaceArray.push(child);
							}
						}
						// 加入子资源
						that.addChild(leftUpDom, childArray, false);
						// 接口状态记数
						that.countInterfaceState(childArray);
						// 修改相应状态总数
						that.updateStateNodeCount(leftDownDom, state, stateName);
						// 过滤逻辑接口事件
						leftDownDom.find(".logicInterfaceOnOff").on('click', function(e){
							if($(this).is(":checked")){
								// 接口状态记数
								that.countInterfaceState(childArray);
								// 修改相应状态总数
								that.updateStateNodeCount(leftDownDom, state, stateName);
								that.addChild(leftUpDom, childArray, false);
							}else{
								// 接口状态记数
								that.countInterfaceState(noLogicInterfaceArray);
								// 修改相应状态总数
								that.updateStateNodeCount(leftDownDom, state, stateName);
								that.addChild(leftUpDom, noLogicInterfaceArray, true);
							}
						});
					} else {
						alert('没有查询到对应的资源信息');
						return false;
					}
				}
			});
		}else{
			that.updateStateNodeCount(leftDownDom, state, stateName);
			alert('没有查询到对应的资源信息');
		}
		},
			
			//
		addChild : function(leftUpDom, childData, isNoLogicInterface){
			leftUpDom.empty();
//			this.rightDom.empty();
//			debugger;
			var groupCnt = Math.ceil(parseInt(childData.length)/12);
			var rowCnt = Math.ceil(parseInt(groupCnt)/4);
			for(var i = 0; i < rowCnt; i ++){
				var rowDom = $("<div/>").addClass('row').width('100%').height('130px');
				for(var j = 0; j < 4; j ++){
					this.addChildGroup(rowDom, i, j, childData, isNoLogicInterface);
				}
				var customDom = $("<div/>").addClass('row').width('100%').height('150px');
				customDom.append(rowDom);
				leftUpDom.append(customDom);
			}
		},
		addChildGroup : function(rowDom, rowNo, groupNo, childData, isNoLogicInterface){
			var that = this;
			var groupDom = $("<div/>").addClass('group').css('float', 'left').width('23%').height('70%');
			var startNo = (rowNo * 8 * 6) + (groupNo * 12);
			var endNo = Math.min((startNo + 12), childData.length);
			for(var i = startNo; i < endNo; i ++){
				var child = childData[i];
				var childDom = $("<span/>").css('cursor', 'pointer').addClass("interface-eq").attr('style','display:inline-block;');
				switch (child.state) {
				case 'NOT_MONITORED':
					childDom.addClass("interface-white").attr('no', child.no);
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					childDom.addClass("interface-green-red").attr('no', child.no);
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					childDom.addClass("interface-red").attr('no', child.no);
					break;
//				case 'UNKOWN':
//				case 'UNKNOWN_NOTHING':
//					childDom.addClass("interface-gray").attr('no', child.no);
//					break;
				default:
					childDom.addClass("interface-green").attr('no', child.no);
					break;
				}
				if(child.noLogicNo != undefined){
					childDom.attr('noLogicNo', child.noLogicNo);
				}
//				childDom.tooltip({
//					position : 'right',
//					content : child.name
//				});
//				childDom.attr('title', child.name);
				groupDom.append(childDom);
			}
			//初始化右键menu
			$('#portMenu').menu({
				onClick:function(item){
					var condition = '';
					var instanceId = $(item.target).attr('instanceid');
					if(item.text.indexOf('打开') > -1){
						condition = 'ifSetAdminUp';
					}else if(item.text.indexOf('关闭') > -1){
						condition = 'ifSetAdminDown';
					}
					oc.util.ajax({
						url:oc.resource.getUrl("portal/resource/resourceDetailInfo/editPortStatus.htm"),
						data:{
							instanceId:instanceId,
							condition:condition
						},
						success:function(data){
							if(data.data==1){
								alert('操作成功！');
							}else{
								alert('操作失败！');
							}
						}
					});
				},
			});
			groupDom.find('span').each(function(){
				var interDom = $(this);
				var no = isNoLogicInterface ? parseInt(interDom.attr('noLogicNo')) : parseInt(interDom.attr('no'));
				interDom.on('mousedown', function(e){
					if(1==e.which){	//左键点击打开详情列表
						that.childResourceEvent(childData[no].id, childData[no].ifIndex);
					}else if(3==e.which){	//右键菜单
						var user = oc.index.getUser();
						if(user.systemUser){
							$(document).bind('contextmenu.interface-eq',function(e){
								e.preventDefault();
								$('#portMenu').find('#menu_open').attr('instanceId',childData[no].id);
								$('#portMenu').find('#menu_close').attr('instanceId',childData[no].id);
								$('#portMenu').menu('show',{
									left:e.pageX,
									top:e.pageY
								});
								$(document).unbind(".interface-eq");
							});
						}
					}
				});
				interDom.on('mouseover', function(e){
					if(that.tipTimeId){
						clearTimeout(that.tipTimeId);
					}
					that.tipTimeId=null;
					that.tipTimeId=setTimeout(function(){
						oc.topo.tips.show({
							type:"port",
							x:e.pageX,
							y:e.pageY-215*1.9,
							value:{
								instanceId:childData[no].id,
								ifIndex:childData[no].ifIndex
							}
						});
						that.tipTimeId=null;
					},500);
				});
				interDom.mouseout(function(){
					if(that.tipTimeId){
						clearTimeout(that.tipTimeId);
						that.tipTimeId=null;
					}
					oc.topo.tips.hide();
				});
			});
			if(groupDom.find(".interface-eq").length > 0)
				rowDom.append(groupDom);
		},
		childResourceEvent : function(instanceId, ifIndex){
			var that = this;
			this.createDownDevice(instanceId, ifIndex);
		},
		createDownDevice : function(subInstanceId, ifIndex){
			var that = this;
			oc.resource.loadScript("resource/module/topo/backboard/DownDeviceDia.js",function(){
				$.post(oc.resource.getUrl("topo/backboard/downinfo.htm"),{
					subInstanceId:subInstanceId,
					mainInstanceId:instance_id
				},function(result){
					new DownDeviceDia({
						ip:result.ip,		//上联 设备ip
						intface:result.IfName,//上联设备接口
						deviceName:result.deviceShowName,//上联设备名称
						intfaceIndex:ifIndex	//接口索引
					});
				},"json");
			});
		},
		countInterfaceState : function(childArray){
			var that = this;
			that.child_up = 0;
			that.child_down = 0;
			that.child_unkown = 0;
			that.child_up_down = 0;
			that.child_no_monitor = 0;
			for(var i = 0 ; i < childArray.length; i ++){
				var child = childArray[i];
				switch (child.state) {
				case 'NOT_MONITORED':
					that.child_no_monitor++;
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					that.child_up_down++;
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					that.child_down++;
					break;
//				case 'UNKOWN':
//				case 'UNKNOWN_NOTHING':
//					that.child_unkown++;
//					break;
				default:
					that.child_up++;
					break;
				}
			}
		},
		updateStateNodeCount : function(leftDownDom, state, stateName){
			var that = this;
			for(var i = 0; i < state.length; i ++){
				var stateNode = leftDownDom.find("div[state='" + state[i] + "']").find('.oc-spanlocate');
				var count = 0;
				if(state[i] == 'green'){
					count = that.child_up==undefined?0:that.child_up;
				}else if(state[i] == 'red'){
					count = that.child_down==undefined?0:that.child_down;
				}else if(state[i] == 'gray'){
					count = that.child_unkown==undefined?0:that.child_unkown;
				}else if(state[i] == 'green-red'){
					count = that.child_up_down==undefined?0:that.child_up_down;
				}else{
					count = that.child_no_monitor==undefined?0:that.child_no_monitor;
				}
				stateNode.html(stateName[i] + "<span class='oc-spanlocate'> " + count + "</span>");
			}
		}
		
	};
	
	 //命名空间
	oc.ns('oc.resource.controlpanel.list');
	oc.resource.controlpanel.list={
			open:function(resourceId,childType){
				return new controlPanelClass().open(resourceId,childType);
			}
	};
	
})(jQuery);