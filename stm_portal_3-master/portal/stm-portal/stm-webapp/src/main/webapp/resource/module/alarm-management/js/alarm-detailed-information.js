(function($) {
	var skin=Highcharts.theme.currentSkin;

	function initFrom(type,alarmDialog,data,sysId){
		var fromDiv = alarmDialog.find('#alarm_detailed-information_id');
		
		var from =oc.ui.form({
			selector:fromDiv,
			combobox:[{
				selector:fromDiv.find("[name='ipAddress']"),
				data:[{id:data.data.ipAddress, name:data.data.ipAddress}],
				value:data.data.ipAddress,
				placeholder:null
			}]
		});

		fromDiv.find("[name='alarmContent']").val(data.data.alarmContent);
		fromDiv.find("[name='alarmType']").val(data.data.alarmType);
		fromDiv.find("[name='resourceName']").val(data.data.resourceName);
	
		
		function formatDate(date){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		}
		fromDiv.find("[name='collectionTime']").val(formatDate(new Date(data.data.collectionTime)));
		function toStatus(){
			 var status =  data.data.instanceStatus;
			 var values = "";
			 if(status =="red"){
				 values = "致命";
				 $("#alarmLight").addClass("redlight");
				 return values;
			 }else if(status =="orange"){
				 values = "严重";
				 $("#alarmLight").addClass("orangelight");
				 return values;
			 }else if(status =="yellow"){
				 values = "警告";
				 $("#alarmLight").addClass("yellowlight");
				 return values;
			 }else if(status =="green"){
				 values = "正常";
				 $("#alarmLight").addClass("greenlight");
				 return values;
			 }else{
				 values = "正常";
				 $("#alarmLight").addClass("greenlight");
				 return values;
//				 values = "未知";
//				 $("#alarmLight").addClass("graylight");
//				 return values;
			 }
		}
		fromDiv.find("[name='alarmStatus']").val(toStatus());
		var queryForm=oc.ui.form({selector:$('#alarm_information_form')});
		$('#alarm_information_form_resourceId').val(data.data.resourceId);
		if(type == 1 || type == 2){
			 $('#alarm_information_form_metricId').val(data.data.recoverKey);
		}
		if(type == 2){
			$('#alarm_information_form_recoveryAlarmID').val(data.data.recoveryAlarmId);
		}
		$("#alarm_information_form_sysId").val(sysId);
		var datagridDiv=alarmDialog.find('#alarm_information_datagrid');

		 getUrl = oc.resource.getUrl('alarm/alarmManagement/getHistoryAlarm.htm');
/*		 if(type == 1){
			 getUrl = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm');
		 }else if(type == 2){
			 getUrl = oc.resource.getUrl('alarm/alarmManagement/getRestoreAlarm.htm');
		 }else if(type == 3){
			 getUrl = oc.resource.getUrl('alarm/alarmManagement/getSyslogAlarm.htm');
		 }else if(type == 4){
			 getUrl = oc.resource.getUrl('alarm/alarmManagement/getThirdPartyAlarm.htm');
		 }*/
            function openVideobySystem(tdData) {
                var ip = tdData.ip;// 服务器IP
                var port = tdData.port;// 服务器端口
                var usr = tdData.usr;// 用户名
                var pwd = tdData.pwd;// 密码
                var chnno = tdData.chnno;// 通道号 写死传0
                var chnid = tdData.chnid;// 待点播摄像机ID
                var systemtype = tdData.systemtype;// 服务器类型 1表示直连设备
                var param = '{' + ' "keep":"' + systemtype + '",  '
                    + ' "serverip":"' + ip + '",						' + ' "port":"' + port
                    + '",						' + ' "user":"' + usr + '",						' + ' "pwd": "'
                    + pwd + '",   					' + ' "devid":"' + chnid
                    + '",	        			' + ' "chnno":"' + chnno + '"						' + '}';
                var r = play.PlayVideo(param);
            }

            //加载视频插件
            oc.util.ajax({
                url: oc.resource.getUrl('portal/resource/cameradetail/getIsCamera.htm'),
                data:{instanceId : data.data.resourceId},
                async:false,
                success:function(isCameradata){
                    if(isCameradata.code == 200){
                        if(isCameradata.data.result == "1"){
                            oc.util.ajax({
                                url: oc.resource.getUrl('portal/resource/cameradetail/getCameraConInfo.htm'),
                                data:{instanceId : data.data.resourceId},
                                async:false,
                                success:function(cameradata){
                                    if(cameradata.code == 200){
                                        var videoPlay = $('#alarm_information_form_camera');
                                        var userAgent = navigator.userAgent;
                                        if (userAgent.indexOf("compatible") > -1
                                            && userAgent.indexOf("MSIE") > -1 && !isOpera) {
                                            videoPlay.append('<object style="border: 1px solid #ccc;" classid="clsid:F84FBCC2-B61C-40FA-BF2D-D60E8F1098D4" id="play" width="390px" height="250px" style="padding-top:2px;"  codebase="dzocx.cab#version=1,0"></object>');
                                            openVideobySystem(cameradata.data);
                                        } else {
                                            var popUpVideo = $('<img id="videoBKImg" width="390px" height="250px" src="'
                                                + oc.resource
                                                    .getUrl('resource/themes/blue/images/bk.jpg')
                                                + '"></img>');
                                            videoPlay.append(popUpVideo);
                                            popUpVideo.on('click', function() {
                                                var ip = cameradata.data.devIP;// 服务器IP
                                                var port = cameradata.data.devPort;// 服务器端口
                                                var usr = cameradata.data.devUser;// 用户名
                                                var pwd = cameradata.data.devPwd;// 密码
                                                var chnno = cameradata.data.chnNo;// 通道号 写死传0
                                                var chnid = cameradata.data.channelID;// 待点播摄像机ID
                                                var systemtype = cameradata.data.keep;// 服务器类型 1表示直连设备
                                                var url = 'video://play?param=' + '{' + '"keep":"' + systemtype
                                                    + '","serverip":"' + ip + '","port":"' + port
                                                    + '","user":"' + usr + '","pwd":"' + pwd
                                                    + '","devid":"' + chnid + '","chnno":"' + chnno
                                                    + '"}';
                                                url = url.replace(/"/g, '%22');// 引号转码为%22
                                                window.location = url;// 地址跳转，实现视频点播
                                            });
                                        }
                                    }
                                }
                            });
                            var videoPlay = $('#alarm_information_form_camera');
                            videoPlay.append('<img id="cameraImg" align="right" width="390px" height="250px" src="'
                                + oc.resource
                                    .getUrl('portal/resource/cameradetail/getalarmFileInputStream.htm?instanceId='
                                        + data.data.resourceId) + '&time='+ data.data.collectionTime +'"></img>');
                        }else{
                            alarmDialog.find(".alarmCorrelation #video-camera-show").remove();
                            // $('#alarm_detailed-information_camera').tabs('close','视频诊断');
                        }
                    }
                }
            });
        //alarmCorrelation tabs
        var correlationTabs = alarmDialog.find(".alarmCorrelation").tabs();
        // getUrl = oc.resource.getUrl('alarm/alarmManagement/getHistoryAlarm.htm');
		datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			queryForm:queryForm,
			fit : false,
			height : '300px',
			hideRest:true,
			hideSearch:true,
			url:getUrl,
			columns:[[
		         {
		        	 field:'alarmContent',
		        	 title:'资源相关告警',
		        	 sortable:true,
		        	 align:'left',
		        	 width:'69%',
		        	 formatter:function(value,row,rowIndex){
		        		 var formatterString = '';
							if(value == null){
								formatterString = '<label  class="light-ico '+row.instanceStatus+'light" > </label>';
							}else{
								formatterString = '<label  class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
						 	}
							return formatterString; 
		        	 }
		         },
		         {
		        	 field : 'collectionTime',
		        	 title : '产生时间',
		        	 align:'left',
		          	 width:'25%',
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        			 return  formatDate(new Date(value));
		        		 }
		        		 return null;
		        	 },
		        	 sortable:true
		         }, 
					{
						field : 'snapShotJSON',
						align : 'left',
						hidden:	false,
						title : '快照',
					  	 width:'8%',
						 formatter:function(value,row,rowIndex){
							if(value == null || value == undefined || value == ''){
								return '<a id="'+row.alarmId +'" class="fa  fa-camera-retro blue_gray" ></a>';
							}else{
								return '<a id="'+row.alarmId +'" class="fa  fa-camera-retro light_blue " ></a>';
							}
							
		        	     }
					}
		     ]],
			onClickCell : function(rowIndex, field, value){
				if(field == 'snapShotJSON'){
					var row = $(this).datagrid('getRows')[rowIndex];
					var snapShotJSON = row.snapShotJSON;
					if(snapShotJSON != null && snapShotJSON != undefined){
						oc.resource.loadScript('resource/module/alarm-management/js/alarm-querySnapShot.js', function(){
							oc.module.alarm.management.querysnapshot.open(row);
						});
					}
				}
			}
		});
		
		var rootRelation = null;
		var effectRelation = null;
		rootRelation = $("<div/>").css({
			width : '788px',
			height : '266px'
		}).attr('id',111111);
		
		effectRelation = $("<div/>").css({
			width : '788px',
			height : '266px'
		});
		// 非致命
		/*
		data.data.relation.rootRelation = '{ "links" : [{ "from" : "3898516", "to": "3898508" }],'
				+		'"nodes" : [{'
				+			'"isDown" : true,'
				+			'"isRoot" : "true",'
				+			'"id" : "3898516",'
				+			'"parentId" : "3898516",'
				+			'"parentIp" : "172.16.7.200",'
				+			'"parentShowName" : "Switch",'
				+			'"parentDeviceType" : "Database",'
				+			'"childDeviceId":"111",'
				+			'"childDeviceName":"g/34"'
				+		'},{'
				+			'"isDown" : "false",'
				+			'"isRoot" : "false",'
				+			'"id" : "3898508",'
				+			'"parentId" : "3898508",'
				+			'"parentIp" : "172.16.7.200",'
				+			'"parentShowName" : "Host",'
				+			'"parentDeviceType" : "Host"'
				+		'}]'
				+	'}';
		*/
/*
		// 屏蔽下列功能选项
		if(data.data.relation != null){
			var rootData = JSON.parse(data.data.relation.rootRelation);
			var affectedData = JSON.parse(data.data.relation.affectedRelation);
			correlationTabs.tabs('add',{
				title : '根源分析',
				selected : false,
				content : rootRelation
			});
			correlationTabs.tabs('add',{
				title : '影响分析',
				selected : false,
				content : effectRelation
			});
			
//			var rootReTemp={
//				    "links": [{"from": "3898516","to": "3898508"}],
//				     "nodes": [
//				        {
//				             "isDown": true,
//				             "isRoot": "true",
//				             "id": "3898516",
//				             "parentId": "3898516",
//				             "parentIp": "172.16.7.200",
//				             "parentShowName": "Switch",
//				             "parentDeviceType": "Database",
//				             "childDeviceId": "111",
//				             "childDeviceName": "g/34" 
//				        },
//				        {   "isDown": "false",
//				            "isRoot": "false",
//				             "id": "3898508",
//				             "parentId": "3898508",
//				             "parentIp": "172.16.7.200",
//				             "parentShowName": "Host",
//				             "parentDeviceType": "Host" 
//				        }
//				    ]
//				};
//			var effectReTemp={
//				    "links": [{"from": "3898516","to": "3898507"}],
//				     "nodes": [
//				        {
//				             "isDown": true,
//				             "isRoot": "true",
//				             "id": "3898516",
//				             "parentId": "3898516",
//				             "parentIp": "172.16.7.200",
//				             "parentShowName": "Switch",
//				             "parentDeviceType": "Database",
//				             "childDeviceId": "111",
//				             "childDeviceName": "g/34" 
//				        },
//				        {   "isDown": "false",
//				            "isRoot": "false",
//				             "id": "3898507",
//				             "parentId": "3898508",
//				             "parentIp": "172.16.7.200",
//				             "parentShowName": "Host",
//				             "parentDeviceType": "Host" 
//				        }
//				    ]
//				};
//			
//			relationDrawNew(rootRelation, rootReTemp);
			
			relationDraw(rootRelation, rootData);
			relationDraw(effectRelation, affectedData);
		}
*/
		correlationTabs.find(".tabs-wrap").addClass('w-choosebtn');
		return from;
	}
	
	
	
	
	
	function relationDrawNew(relation, data){
		if(!!!data){
			return false;
		}
		var relationWidth = relation.width();
		var relationHeight = relation.height();
		
		
		var edit;
		Container.prototype.click = function(){
			edit.setCon(this);
			this.C.click(this);
		}
		Container.prototype.proxyDrag = function(dx,dy,x,y){
			edit.hide();
			this.drag(dx,dy,x,y);		
		}
		var canvas = new Canvas(document.getElementById("111111"));
		edit = new Edit(canvas);
		edit.dragEdit = function(bbox){
			//this.edit(bbox);
		}
		edit.edit = function(bbox){
			this.con.rects[0].rx = (bbox.x1+bbox.x)/2-this.con.x;
			this.con.rects[0].ry = (bbox.y1+bbox.y)/2-this.con.y;
			this.con.rects[0].attr({rx:(bbox.x1-bbox.x)/2,ry:(bbox.y1-bbox.y)/2});
			this.con.setBBox(bbox);
		}
		canvas.editable = true;
		
		//渲染node
		var nodeNum = data.nodes.length;
		var cons = [];
		var conn = canvas.container(0,0,relationWidth,relationHeight);
		conn.proxyDrag = function(){
			
		};
		conn.click = function(){
			//edit.hide();
			edit.setCon(this);
			this.C.click(this);
		}
		
		for(var i = 0; i < nodeNum; i ++){
			var node = data.nodes[i];
			node.icon = 'themes/'+skin+'/images/bizser/resourceimgHD/Host.png';
			
			if(node.isDown){
				
			}
			if(node.isRoot){
				
			}
			
			var json = {texts:[{text:node.parentShowName,rx:27.5,ry:65}],
					imgs:[{src:node.icon,rx:0,ry:0,w:55,h:55}],
					rects:[{type:3,r:5,w:65,h:75,rx:-5,ry:-5,isDrag:true,attr:{fill:"red","fill-opacity":0,"stroke-width":0}}],containers:[]};
			
			var con = canvas.container(0,0,60,70);
			con.set(json);
			cons.push(con);
			
			conn.addContainer(con,100*i+100,100);
		}
		
		//links
		var linkLength = data.links.length;
		for(var i = 0;i<linkLength;i++){
			var linkObj = data.links[i];
			var fromObj = null,toObj = null;
			for(var x=0;x<cons.length;x++){
				
				if(data.nodes[x].id == linkObj.from){
					fromObj = cons[x];
				}else if(data.nodes[x].id == linkObj.to){
					toObj = cons[x];
				}
			}
			
			if(fromObj && toObj){
				canvas.onLine(fromObj,toObj,{arrow:true,type:"L",attr:{"stroke-width":3,stroke:"green"}});
			}
			
		}
		
		
	}
	
	
	
	
	
	
	
	// node of state : normal,warning,danger,disabled,severity
	// link of state : normal,warning,danger,disabled,severity,nodata
	function relationDraw(relation, data){
		if(!!!data){
			return false;
		}
		for(var i = 0; i < data.nodes.length; i ++){
			var node = data.nodes[i];
			node.iconHeight = 55;
			node.iconWidth = 55;
			node.lineName = " ";
			if(node.down == true){
				node.state = 'danger';
			}
			// 如果有子资源则加上子资源名称
			var nodeName = node.parentShowName;
			if(!!node.childDeviceId && node.childDeviceId != ""){
				nodeName += "(" + node.childDeviceName + ")";
			}
			node.nodeName = nodeName;
			// 资源类型不同显示不同的图标
			var iconName = node.parentDeviceType;
			switch (iconName) {
			case "Database":
				iconName = "Database";
				break;
			case "WebServer":
				iconName = "WebServer";
				break;
			case "Storage":
				iconName = "Storage";
				break;
			case "StandardService":
				iconName = "StandardService";
				break;
			case "NetworkDevice":
				iconName = "NetworkDevice";
				break;
			case "Middleware":
				iconName = "Middleware";
				break;
			case "MailServer":
				iconName = "MailServer";
				break;
			case "LotusDomino":
				iconName = "LotusDomino";
				break;
			case "Link":
				iconName = "Link";
				break;
			case "J2EEAppServer":
				iconName = "J2EEAppServer";
				break;
			case "Host":
				iconName = "Host";
				break;
			case "Directory":
				iconName = "Directory";
				break;
			case "VM":
				iconName = "VM";
				break;
			case "BUSINESS":
				iconName = "BUSINESS";
				break;
			case "SnmpOthers":
				iconName = "SnmpOthers";
				break;
			default:
				iconName = "StandardService";
				break;
			}
			node.icon = 'resource/themes/'+skin+'/images/bizser/resourceimgHD/' + iconName + '.png';
		}
		data.cfg = {
			lineTitle : "lineName",
			showLineTitle : true,
			nodeTitle : "nodeName",
			width : relation.width(),
			height : relation.height(),
			holder : relation
		};
		relation.load(oc.resource.getUrl("resource/module/topo/api/alarm.jsp"),function(){
			alarmTopo.loadData(data);
		});
	}
	
	/**
	 * 打开告警详细
	 */
	function open(alarmId,type,sysId){
		var alarmDialogDiv=$('<div/>');
		var alarmDialog=alarmDialogDiv.dialog({
		    title: '告警详情',
		    flt:false,
		    width: 850,
		    height: 500,
		    href: oc.resource.getUrl('resource/module/alarm-management/alarm-detailed-information.html'),
		    onLoad:function(){
		    	// alarmType 1、未恢复 2、已恢复
		    	oc.util.ajax({
		    		url: oc.resource.getUrl('alarm/alarmManagement/getAlarmById.htm'),
		    		data:{alarmId:alarmId,alarmType:type},
		    		successMsg:null,
		    		success:function(data){
		    			if(data.data == null){
		    				alert("该条未恢复告警已恢复");
		    			}else{
		    			   initFrom(type,alarmDialog,data,sysId);
		    			}
		    		}
		    	});
		    }
		});
		return alarmDialog;
	}
	
	 //命名空间
	oc.ns('oc.alarm.detail.inform');
	oc.alarm.detail.inform={
			open:function(alarmId,type,sysId){
				return open(alarmId,type,sysId);
			}
	};
})(jQuery);