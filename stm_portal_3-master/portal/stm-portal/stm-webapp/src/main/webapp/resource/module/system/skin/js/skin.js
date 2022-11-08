$(function() {
	function skin(){
		var t=this;
		this.initDom();
		this.registEvent();
		oc.util.ajax({
			url:oc.resource.getUrl('system/skin/get.htm'),
			success:function(d){
				t.data=$.parseJSON(d.data);
				for(var i=0,skins=t.data.skins,len=skins.length,dom=t.skinsDom;i<len;i++){
					dom.append('<div class="skin '+skins[i].style+'" title="'+skins[i].name+'"></div>');
				}
				log(t.skinsDom.find('.'+t.data.selected),t.data.selected);
				t.skinsDom.find('.'+t.data.selected).click();
			}
		});
	}
	
	skin.prototype={
		constructor:skin,
		id:0,
		dom:0,
		skinsDom:0,
		currentSkinDom:0,
		initDom:function(){
			this.id=oc.util.generateId();
			this.dom=$('#systemSkin').attr('id',this.id);
			this.skinsDom=this.dom.find('.skins');
			this.currentSkinDom=this.dom.find('.current-skin');
		},
		registEvent:function(){
			var t=this;
			
			t.skinsDom.click(function(e){
				var jq=$(e.target);
				if(jq.is('.skin')){
					jq.addClass('active').siblings().removeClass('active');
					t.currentSkinDom.attr('class','current-skin')
						.addClass(jq.attr('class').replace('skin','').replace('active','').trim());
				}
			});
			
			t.dom.find('button').linkbutton('RenderLB',{
				onClick:function(e){
					t.data.selected=t.currentSkinDom.attr('class').replace('current-skin','').trim();
					oc.util.ajax({
						url:oc.resource.getUrl('system/skin/save.htm'),
						data:{skin:t.data},
						success : function(data){
							oc.resource.loadScript('resource/index_skin.js?'+(new Date()), function() {
								var navList = $("#oc_index_layout").find(".oc-nav > .nav-list");
								if(data.data != null && data.data != undefined && data.data.length > 0){
									for(var i = 0; i < data.data.length; i++){
										var right = data.data[i];
										var img = navList.find("li[data-id='" + right.id + "'] img");
										img.attr('src', oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId=' + right.fileId))
									}
								}
							});
						},
						successMsg:'保存皮肤设置成功！'
					});
				}
			});
		}
	};
	
	new skin();
});