(function(){
	var workbench=$('<div/>'),panels=null,selects=null,ul=null,maxCount=20,datas=0,form=0,tempLis=[];
	
	var domainCombo=null,initContent=function(){
			if(!datas){
				oc.util.ajax({
					url:oc.resource.getUrl('home/workbench/main/getAllWorkbenchs.htm'),
					successMsg:null,
					async:false,
					success:function(d){
						datas=d.data;
					}
				});
			}
			for(var i=0,len=datas.length,d;i<len;i++){
				d=datas[i];
				$('<div class="content"><div class="image '+d.icon+'"></div><div class="title">'+d.title+'</div></div>').click((function(dd){
					return function(){addLi(dd);};
				})(d)).appendTo(panels);
			}
		},initDom=function(){
			panels=workbench.find('.panels:first');
			selects=workbench.find('> div:last');
			ul=selects.find('ul:first').sortable();
			domainCombo=oc.ui.combobox({
				selector:workbench.find('select[name=domainId]'),
				placeholder:0,
				data:oc.index.getUser().domains
			});
			form=oc.ui.form({selector:selects.find('form:first')});
		},initUl=function(){
			if(ul){
				oc.util.ajax({
					url:oc.resource.getUrl('home/workbench/main/getUserWorkbenchs.htm'),
					successMsg:null,
					success:function(d){
						datas=d.data;
						for(var i=0,len=datas.length,dd;i<len;i++){
							dd=datas[i];
							addLi(dd);
							if(i==0)domainCombo.jq.combobox('setValue',dd.domainId);
						}
					}
				});
			}
		},initBtns=function(){
			selects.find('> div:last button:first').linkbutton('RenderLB',{
				text:'提交',
				iconCls:'fa fa-check-circle',
				onClick:function(){
					var ds=[],domainId=domainCombo.jq.combobox('getValue'),userId=oc.index.getUser().id;
					ul.find('li').each(function(i){
						var jq=$(this),d=jq.data('oc-data');
						d=$.extend({},d||(d={workbenchId:jq.find('input').val()}));
						d.workbenchId=d.workbenchId||d.id;
						d.sort=i;
						d.userId=userId;
						d.domainId=domainId;
						ds.push(d);
					});
//					if(obj.workbenchId){
						oc.util.ajax({
							url:oc.resource.getUrl('home/workbench/main/setUserWorkbenchs.htm'),
							data:{uws:ds},
							success:function(d){
								if(oc.home.workbench.resource.select){
									oc.home.workbench.resource.select.setDomain(domainId);
								}
								oc.home.workbench.loadWorkbench();
								workbench.dialog('close');
							}
						});
//					}else{
//						alert('请选择一个要展示的工作台！');
//					}
				}
			}).next().linkbutton('RenderLB',{
				text:'取消',
				iconCls:'fa fa-times-circle',
				onClick:function(){
					workbench.dialog('close');
				}
			});
		},addLi=function(o){
			if(tempLis.length==maxCount){
				alert('最多只能添加'+maxCount+'个选项到工作台！');
				return;
			}
			var li=0;
			ul.append(li=$('<li class="oc-workbench-rtitle"><span>'+o.title
				+'</span><input type="hidden" name="workbenchId" value="'
				+o.workbenchId+'"/><span class="oc-workbench-rtitle-del"></span></li>')
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
			href:oc.resource.getUrl('resource/module/home/setting/workbench.html'),
			onClose:function(){
				ul.children().remove();
				tempLis.length=0;
			},
			resizable:true,
			width:1089,
			height:590,
			onLoad:function(){
				initDom();
				initContent();
				initBtns();
				initUl();
			},
			_onOpen:initUl
		});
		
	oc.ns('oc.home.setting');
	
	oc.home.setting.workbench={
		open:function(){
			workbench.dialog('open');
		}
	};
})();