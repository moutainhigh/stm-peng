(function(){
	
	function healthDlg(cfg){
		this.cfg = cfg;
	};
	
	healthDlg.prototype={
		cfg : undefined,
		init : function(){
			var that = this;
			
			var dlgItem = $('<div style="top:130px;left:100px;">').html('<div class="health margin0">'+
	          		 //'<h5>业务健康度</h5>'+   class="health"
	            	 '<div class="fraction"><span class="spanFraction"></span></div>'+
	                '<div class="health_dl_list"><dl></dl>'+
	                '</div></div>');
			
			var healthDiv = dlgItem.find('.health');
			
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getHealthDetailAndScore.htm'),
				data:{bizId:that.cfg.bizId},//628501
				successMsg:null,
				success:function(data){
					var nodeList = healthDiv.find('.health_dl_list');
					var nodeArr ,score,status;
					
					if(data.data){
						nodeArr = data.data.detail;
						score = data.data.health;
						status = data.data.status;
					}else{
						dlgItem.html('暂无健康度信息!');
						return;
					}
					
					var addSpanFractionClass = '';
					switch(status){
					case 0:
					default :
						break;
					case 1:
						addSpanFractionClass = 'yellow_fraction_bg';
						break;
					case 2:
						addSpanFractionClass = 'orange_fraction_bg';
						break;
					case 3:
						addSpanFractionClass = 'red_fraction_bg';
						break;
					}
					healthDiv.find('.spanFraction').html(score+'分').parent().addClass(addSpanFractionClass);
					
					for(var i=0;i<nodeArr.length;i++){
						var nodeObj = nodeArr[i];
						var resourceClass;
						
						var nodeStutasParent =1;
//						nodeObj.nodeStatus = -1;
						switch(nodeObj.nodeStatus){
						//0正常1警告 2严重 3致命-1未监控
						case -1:
							nodeStutasParent = -1;
							resourceClass = 'light-ico graylight';// pos-top8
							break;
						case 0:
						case 0:
						default :
							resourceClass = 'status_ico status_ico-green';// float_6
							break;
						case 1:
							resourceClass = 'light-ico yellowlight';// pos-top8
							break;
						case 2:
							resourceClass = 'light-ico orangelight'; //pos-top8
							break;
						case 3:
							resourceClass = 'light-ico redlight';//pos-top8
							break;
						}
						
						
						var showName = nodeObj.showName;
						var dl = $('<dl/>');
//						var dt = $('<dt/>').append('<span class="'+resourceClass+'"></span>'+showName);
						var dt = $('<dt/>').append('<div title="'+showName+'" style="width:170px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis; "><span class="'+resourceClass+'"></span>'+showName+'</div>');
						dl.append(dt);
						
						var nodeMetricArr = nodeObj.bind;
						if(nodeMetricArr){
							for(var x=0;x<nodeMetricArr.length;x++){
								var nodeMetricObj = nodeMetricArr[x];
								if(nodeStutasParent<0){
									//都为未监控状态
									nodeMetricObj.status=-1;
								}
								
								var childClass ;
								switch(nodeObj.type){
//								1主资源 2子资源 3指标
								case 1:
								case 2:
								default:
									switch(nodeMetricObj.status){
									//0正常1警告 2严重 3致命
									case -1:
										childClass = 'light-ico graylight';
										break;
									case 0:
									default :
										childClass = 'status_ico status_ico-green';
										break;
									case 1:
										childClass = 'light-ico yellowlight';
										break;
									case 2:
										childClass = 'light-ico orangelight';
										break;
									case 3:
										childClass = 'light-ico redlight';
										break;
									}
									break;
								case 3:
									switch(nodeMetricObj.status){
									//0正常1警告 2严重 3致命
									case -1:
										childClass = 'light-ico graylight';
										break;
									case 0:
									default :
										childClass = 'status_ico status_ico-green';
										break;
									case 1:
										childClass = 'light-ico yellowlight';
										break;
									case 2:
										childClass = 'light-ico orangelight';
										break;
									case 3:
										childClass = 'light-ico redlight';
										break;
									}
									break;
								}
								var dd = $('<dd />').html('<div class="health_L" title="'+nodeMetricObj.name+'">'+nodeMetricObj.name+'</div><div class="dotted_line"></div><div class="health_R"><span class="'+childClass+'"></span></div>');
								
								dl.append(dd);
							}
						}
						nodeList.append(dl);
					}
					
				}
			});
			
			
			dlgItem.dialog({
			    title: '业务健康度',
			    width : 225,
				height : 525,
//				modal : false,
			    onLoad:function(){
			    }
			});
		}
	};
	
	oc.ns('oc.module.biz.healthdlg');
	oc.module.biz.healthdlg={
		open:function(cfg){
			var health = new healthDlg(cfg);
			health.init();
		}
	};
	
})()