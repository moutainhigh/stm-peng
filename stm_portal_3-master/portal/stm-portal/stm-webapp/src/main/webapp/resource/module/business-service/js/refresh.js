(function($){
	var skin=Highcharts.theme.currentSkin;
	var statusImg = {
			NORMAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang.png",
			WARN : "themes/"+skin+"/images/bizser/statusIMG/jingao.png",
			SERIOUS : "themes/"+skin+"/images/bizser/statusIMG/yanzhong.png",
			CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhiming.png",
			CRITICAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhiming_nothing.png",
			UNKNOWN_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/weizhi_nothing.png",
			NORMAL_CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_zhiming.png",
			NORMAL_UNKNOWN : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_weizhi.png",
			NORMAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_nothing.png"
		};
	var statusAppImg = {
			NORMAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhengchang.png",
			WARN : "themes/"+skin+"/images/bizser/statusAppIMG/jingao.png",
			SERIOUS : "themes/"+skin+"/images/bizser/statusAppIMG/yanzhong.png",
			CRITICAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhiming.png"
		};
	var chooseRowData,updateFlag=false,refreshInterval;
	function refresh(id,canvas,obj){
		refreshInterval = setInterval(
				function(){
					if($(obj).length==1){
						update(id,canvas,obj);
					}else{
						clearInterval(refreshInterval);
					}
		},1000*60);
	}
	
	/**
	 * 刷新业务拓扑图元信息数据
	 */
	function update(id,canvas,obj){
		updateFlag=false;
		oc.util.ajax({
			url: oc.resource.getUrl('portal/business/service/get.htm?id='+id),
			data:null,
			async:false,
			successMsg:null,
			failureMsg:null,
			startProgress:null,
			success: function(data){
				if(data.code && data.code==200){
					chooseRowData = data.data;
				}else{
					clearInterval(refreshInterval);
					return;
				}
			}
		});
		
		var containers = canvas.containers;
		var bizDepContainers = [],bizSerContainers = [],resourceContainers = [],
		bizDepIds=[],bizSerIds=[],resourceIds=[],bizDepOrSerObj,resourceObj,quyuAndBgIds=[],
		quyuAndBgContainers=[],quyuAndBgObj;
		for (var key in containers){
			if(containers[key].data && containers[key].data.type){
				var container = containers[key],
					type = container.data.type,
					id = container.data.id;
				if(type == "bizDep"){
					bizDepContainers.push({key:container.ID,value:id});
					bizDepIds.push(id);
				}else if(type == "bizSer"){
					bizSerContainers.push({key:container.ID,value:id});
					bizSerIds.push(id);
				}else if(type == "resource"){
					resourceContainers.push({key:container.ID,value:id});
					resourceIds.push(id);
				}else if(type == "quyupic"){
					quyuAndBgContainers.push({key:container.ID,value:id});
					quyuAndBgIds.push(id);
				}else if(type == "bizMain"){
					if(chooseRowData.name != container.texts[0].attr("text")){
						container.texts[0].attr({text:chooseRowData.name});
					}
					container.imgs[1].attr({src:statusAppImg[chooseRowData.status]});
					container.imgs[0].attr({src:container.imgs[0].attrs.src.substring(0,
							container.imgs[0].attrs.src.indexOf('=')+1)+chooseRowData.fileId});
				}
			}
		}
		oc.util.ajax({
			url: oc.resource.getUrl('portal/business/service/dep/getListByIds.htm?ids='
					+bizDepIds.concat(bizSerIds).join()+'&resourceIds='+resourceIds.join()
					+'&picIds='+quyuAndBgIds.join()),
			data:null,
			async:false,
			successMsg:null,
			failureMsg:null,
			startProgress:null,
			success: function(data){
				bizDepOrSerObj = data.data[0];
				resourceObj = data.data[1];
				quyuAndBgObj = data.data[2];
			}
		});
		//删除已经不存在的业务单位，业务服务的containers
		var bizDepOrSerContainers = bizDepContainers.concat(bizSerContainers);
		for(var m in bizDepOrSerContainers){
			var flag = true;
			for(var i in bizDepOrSerObj){
				if(new Number(bizDepOrSerContainers[m].value)==bizDepOrSerObj[i].id){
					flag = false;
					var curr = containers[bizDepOrSerContainers[m].key];
					curr.imgs[0].attr({"src":curr.imgs[0].attrs.src.substring(0,
							curr.imgs[0].attrs.src.indexOf('=')+1)+bizDepOrSerObj[i].fileId});
					if(bizDepOrSerObj[i].name != curr.texts[0].attr("text")){
						curr.texts[0].attr({"text":bizDepOrSerObj[i].name});
					}
					break;
				}
			}
			if(flag && bizDepOrSerContainers[m] && bizDepOrSerContainers[m].key){//删除业务单位或者业务服务containers
				canvas.containers[bizDepOrSerContainers[m].key].remove();
				updateFlag = true;
			}
		}
		
		//更新资源状态数据(资源名称或者资源状态图标)，删除不存在的资源
		for(var m in resourceContainers){
			var flag = true;
			for(var i in resourceObj){
				if(new Number(resourceContainers[m].value)==resourceObj[i].id){
					flag = false;
					var curr = containers[resourceContainers[m].key];
					if(resourceObj[i].text != curr.texts[0].attr("text")){
						curr.texts[0].attr({text:resourceObj[i].text});
					}
					curr.imgs[1].attr({src:statusImg[resourceObj[i].attributes['status']]});
				}
			}
			if(flag && resourceContainers[m] && resourceContainers[m].key){//删除资源containers
				canvas.containers[resourceContainers[m].key].remove();
				updateFlag = true;
			}
		}
		return updateFlag;
	}
	oc.ns('oc.business.service.canvas');
	oc.business.service.canvas={
		refresh:function(id,canvas,obj){
			refresh(id,canvas,obj);
		},
		update:function(id,canvas,obj){
			return update(id,canvas,obj);
		}
	};
})(jQuery);