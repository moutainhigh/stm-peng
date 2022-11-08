(function(){
	var workbench=$('<div/>'),panels=null,selects=null,ul=null,maxCount=20,datas=0,form=0,tempLis=[];
	
	var 
//	domainCombo=null,
	initContent=function(){
			if(!datas){
//				datas=[{
//		            "id": null, 
//		            "title": "TOPN", 
//		            "url": "resource/module/vm/topN/workbench/topn.html", 
//		            "icon": "topn", 
//		            "userId": null, 
//		            "workbenchId": 1, 
//		            "sort": 1, 
//		            "selfExt": null, 
//		            "domainId": 1
//		        }];
				oc.util.ajax({
					url:oc.resource.getUrl('portal/vm/topN/getAllWorkbenchs.htm'),
					successMsg:null,
					async:false,
					success:function(d){
						datas=d.data;
					}
				});
			}
			for(var i=0,len=datas.length,d;i<len;i++){
				d=datas[i];
				$('<div style="float:right;margin-right:5px;"><a class="addTopn" >添加TOPN</a></div>').click((function(dd){
					return function(){addLi(dd);};
				})(d)).appendTo(panels);
			}
		},initDom=function(){
			panels=workbench.find('.panels:first');
			selects=workbench.find('> div:last');
			ul=selects.find('ul:first').sortable();
//			domainCombo=oc.ui.combobox({
//				selector:workbench.find('select[name=domainId]'),
//				placeholder:0,
//				data:oc.index.getUser().domains
//			});
			form=oc.ui.form({selector:selects.find('form:first')});
		},initUl=function(){
			if(ul){
				oc.util.ajax({
					url:oc.resource.getUrl('portal/vm/topN/getTopNUserWorkbenchs.htm'),
					successMsg:null,
					success:function(d){
						datas=d.data;
						for(var i=0,len=datas.length,dd;i<len;i++){
							dd=datas[i];
							addLi(dd);
//							if(i==0)domainCombo.jq.combobox('setValue',dd.domainId);
						}
					}
				});
			}
		},initBtns=function(){
			panels.find(".addTopn").linkbutton('RenderLB',{
				text:'添加TOPN',
				iconCls:'fa fa-plus'
			});
			/*selects.find('> div:last button:first').linkbutton('RenderLB',{
				text:'提交',
				onClick:function(){
					var tuwb=[],
//					domainId=domainCombo.jq.combobox('getValue'),
					userId=oc.index.getUser().id;
					ul.find('li').each(function(i){
						var jq=$(this),dtuwb=$(this).data('oc-data');
						dtuwb=$.extend({},dtuwb||(dtuwb={workbenchId:$(this).find('input').val()}));
						dtuwb.workbenchId=dtuwb.workbenchId||dtuwb.id;
						dtuwb.sort=i;
						dtuwb.userId=userId;
//						d.domainId=domainId;
						tuwb.push(dtuwb);
					});
					oc.util.ajax({
						url:oc.resource.getUrl('portal/vm/topN/setTopNUserWorkbenchs.htm'),
						data:{tuwb:tuwb},
						success:function(d){
							oc.vm.workbench.loadWorkbench();
							workbench.dialog('close');
						}
					});
				}
			}).next().linkbutton('RenderLB',{
				text:'取消',
				onClick:function(){
					workbench.dialog('close');
				}
			});*/
		},addLi=function(o){
			if(tempLis.length==maxCount){
				alert('最多只能添加'+maxCount+'个选项到工作台！');
				return;
			}
			var li=0;
			ul.append(li=$('<li class="oc-workbench-rtitle"><span>'+o.title
				+'</span><input type="hidden" name="workbenchId" value="'
				+o.workbenchId+'"/><span class="oc-workbench-rtitle-del panel-tool-close"></span></li>')
				.data('oc-data',o));
			li.find('.oc-workbench-rtitle-del').click(function(){
				tempLis.splice(tempLis.indexOf(li),1);
				li.remove();
			});
			tempLis.push(li);
		};
		
		workbench.dialog({
			closed:true,
			title:'工作台窗口设置',
			href:oc.resource.getUrl('resource/module/vm/topN/setting/workbench.html'),
			onClose:function(){
				ul.children().remove();
				tempLis.length=0;
			},
			resizable:true,
			width:600,
			height:450,
			onLoad:function(){
				initDom();
				initContent();
				
				initBtns();
				initUl();
			},
			_onOpen:initUl,
			buttons:[{
				text: '提交',
				iconCls:'fa fa-check-circle',
				handler: function () {

					var tuwb=[],
//					domainId=domainCombo.jq.combobox('getValue'),
					userId=oc.index.getUser().id;
					ul.find('li').each(function(i){
						var jq=$(this),dtuwb=$(this).data('oc-data');
						dtuwb=$.extend({},dtuwb||(dtuwb={workbenchId:$(this).find('input').val()}));
						dtuwb.workbenchId=dtuwb.workbenchId||dtuwb.id;
						dtuwb.sort=i;
						dtuwb.userId=userId;
//						d.domainId=domainId;
						tuwb.push(dtuwb);
					});
					oc.util.ajax({
						url:oc.resource.getUrl('portal/vm/topN/setTopNUserWorkbenchs.htm'),
						data:{tuwb:tuwb},
						success:function(d){
							oc.vm.workbench.loadWorkbench();
							workbench.dialog('close');
						}
					});
				
				}
			},
			{
				text: '取消',
				iconCls:' fa fa-times-circle',
				handler: function () {
					workbench.dialog('close');
				}
			}]
		});
		
	oc.ns('oc.vm.setting');
	
	oc.vm.setting.workbench={
		open:function(){
			workbench.dialog('open');
		}
	};
})();