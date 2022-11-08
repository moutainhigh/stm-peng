(function() {
	function videoplay() {}
	videoplay.prototype = {
		construtor : videoplay,
		cfg : undefined,
		_videoData : [],
		_defaults : {},
		open : function() {
			
			var that = this;
			
			oc.util.ajax({
				url: oc.resource.getUrl('portal/video/getAll.htm'),
				success:function(data){
					if(data.code == 200){
						
						that._videoData = $.extend(true,[],data.data);
						var videoData = data.data;
						
						var height = $('#oc-video').height() / 3 - 3;
						
						for(var i = 0 ; i < videoData.length ; i ++){
							if(jwplayer('jwplayer' + videoData[i].id).id){
								jwplayer('jwplayer' + videoData[i].id).remove();
							}
							if(videoData[i].path && videoData[i].path.trim()){
								
								var jwplayerDiv = $('<div style="width:25%;height:' + height + 'px;float:left"><div id="jwplayer' + videoData[i].id + '"></div></div>');
								$('#videoWorkbenchPannelsId').append(jwplayerDiv);
								that.showVideo(videoData[i].id,videoData[i].path,jwplayerDiv.height(),jwplayerDiv.width());
								
							}else{
								
								var emptyDiv = $('<div style="width:25%;height:' + height + 'px;float:left"></div>');
								$('#videoWorkbenchPannelsId').append(emptyDiv);
								that.showEmptyVideo(emptyDiv,videoData[i].id);
								
							}
						}
						
						$('#oc-video').find('.no_info').click(function(e){
							  
							that.showUrlSetWindow($(e.target).attr('videoid'));
							
						});
						
					}else{
						alert('获取视频信息错误！');
					}
				}
			});
			
		},
		showUrlSetWindow : function(id,video,isJwPlayer){
			  
			  var that = this;
			  var updateVideoPath = $('<div/>');
			  
			  var fileUrl = '';
			  
			  if(isJwPlayer && !video.getPlaylist()[0]){
				  var index = parseInt(id) - 1;
				  fileUrl = that._videoData[index].path;
			  }else{
				  fileUrl = video ? video.getPlaylist()[0].file : '';
			  }

			  var updateVideoPathFrom = $('<form class="oc-form col1"></form>');
			  updateVideoPathFrom.append('<div class="form-group"><label>视频地址 : </label><div><textarea style="resize: none;height:80px" name="videoPath" required>' + fileUrl + '</textarea></div></div>');
			  updateVideoPath.append(updateVideoPathFrom);
			  updateVideoPath.append('<div class="oc-toolbar" align="right"></div>');
			  
			  //设置视频播放地址
			  var updateVideoPathDialog = $('<div/>').dialog({
				  width:'530px',
				  height:'170px',
				  title: '设置视频播放地址',
				  content:updateVideoPath
			  });
			  
			  updateVideoPath.find('.oc-toolbar').append($('<a/>').linkbutton('RenderLB',{
				  text:'确定',
				  iconCls:"fa fa-check-circle",
				  onClick:function(){
					  var videoPath = updateVideoPath.find('textarea').val().trim();
					  
					  oc.util.ajax({
					  	  successMsg:null,
						  url: oc.resource.getUrl('portal/video/updatePath.htm'),
						  data:{id:id,path:videoPath},
						  success:function(data){
							  if(data.code == 200 && data.data){
								  alert('修改成功！');
								  
								  var index = parseInt(id) - 1;
								  that._videoData[index].path = videoPath;
								  
								  if(videoPath && isJwPlayer){
									  //刷新播放器播放地址
									  jwplayer('jwplayer' + id).remove();
									  var jwParent = $('#jwplayer' + id).parent();
									  jwParent.html('');
									  jwParent.append('<div id="jwplayer' + id + '"></div>');
									  that.showVideo(id,videoPath,jwParent.height(),jwParent.width());
								  }else if(!videoPath && isJwPlayer){
									  
									  //清空播放器
									  jwplayer('jwplayer' + id).remove();
									  var jwParent = $('#jwplayer' + id).parent();
									  jwParent.html('');
									  that.showEmptyVideo(jwParent,id);
									  $('#oc-video').find('.no_info').unbind('click');
									  $('#oc-video').find('.no_info').click(function(e){
										  that.showUrlSetWindow($(e.target).attr('videoid'));
									  });
									  
								  }else if(videoPath && !isJwPlayer){
									  
									  //添加播放器
									  var emptyDiv = $('#emptyDiv' + id).parent();
									  emptyDiv.html('');
									  emptyDiv.append('<div id="jwplayer' + id + '"></div>');
									  that.showVideo(id,videoPath,emptyDiv.height(),emptyDiv.width());
								  }
								  
								  updateVideoPathDialog.dialog('destroy');
							  }else{
								  alert('修改失败！');
							  }
							  
						  }
					  });
				  }
			  })).append($('<a/>').linkbutton('RenderLB',{
				  text:'取消',
				  iconCls:"fa fa-times-circle",
				  onClick:function(){
					  updateVideoPathDialog.dialog('destroy');
				  }
			  }));
		},
		showEmptyVideo : function(container,id){
			
			container.append('<div id="emptyDiv' + id + '" class="module easyui-layout layout easyui-fluid" data-options="fit:true">' +
					'<div class="panel layout-panel-center">' +
					'<div class="h-middle-l panel-body panel-body-noheader layout-body" data-options="region:\'center\'" title="">' +
					'<div class="h-middle-r" style="height: 100%;">' +
					'<div class="h-middle-m" style="height: 100%;">' +
					'<div class="h-middle-content" style="height: 100%;">' +
					'<div class="border-out border-none" style="height: 100%;">' +
					'<div class="sub-layout-div width100" style="height:100%;">' +
					'<div class="sub-border-inside" style="height:100%;">' +
					'<div class="border-doted sub-notice"><div videoid="' + id + '" class="n-main-infor no_info host_no_data">请选择视频播放地址</div></div>' +
					'</div>' +
					'</div>' +
					'</div>' +
					'</div>' +
					'</div>' +
					'</div>' +
					'</div></div>' +
				'</div>');
			
		},
		showVideo : function(id,path,height,width){
			
			var that = this;
			
			//加载播放器
			jwplayer('jwplayer' + id).setup({
				'file': path,
				'skin': 'glow',
				'flashplayer': "third/jwplayer7/jwplayer.flash.swf",
				'controls':true,
				'autostart':'muted',
				'height' : height,
				'width' : width,
				'controlbar': 'bottom',
				'stretching': 'uniform',
				'smoothing':'true',
				'stretching': "fit",
				'mute':true,
				'volume':0,
				'events': {
					onReady: function(){
						var video = this;
						this.onFullscreen(function(obj){
							
							if(obj.fullscreen){
								$('#' + video.id).find('.video-path-set:first').hide();
							}else{
								$('#' + video.id).find('.video-path-set:first').show();
							}
							
						});
						
						var playerId = this.id;
						var resetButton = $('<div class="jw-icon jw-icon-inline jw-button-color jw-reset video-path-set" style="background: url(themes/'+Highcharts.theme.currentSkin+'/images/icons/btnico.png) no-repeat scroll 0 0;background-position: 0 -46px;left:6px;height:18px;"></div>');
						$('#' + playerId).find('.jw-controlbar-right-group').append(resetButton);
						resetButton.on('click',function(){
							that.showUrlSetWindow(playerId.replace('jwplayer',''),video,true);
						});
					}
					
				}
			});
			
		}
		
	};
	var videoplayOject = undefined;
	
	oc.ns('oc.video.set');
	oc.video.set = {
			open : function(){
				videoplayOject = new videoplay();
				videoplayOject.open();
			},
			showSetDialog : function(id,video){
				videoplayOject.showUrlSetWindow(id,video,true);
			}
	};
	
})(jQuery);