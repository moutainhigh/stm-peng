(function(){
	var screen=$('<div/>'),screenObj=null;
	screen.dialog({
		closed:true,
		title:'大屏设置',
		href:oc.resource.getUrl('resource/module/home/setting/screen.html'),
		onClose:function(){},
		_onOpen:function(){
			screenObj.userJq&&screenObj.reRender();
			oc.util.ajax({
				url:oc.resource.getUrl('home/workbench/main/checkLicenseByBiz.htm'),
				success:function(d){
					if(d.data==0){//业务没有授权
						$("div[name='biz-summary']").attr('style','display:none');
					}
				}
			});
		},
		resizable:true,
		width:1089,
		height:590,
		onLoad:function(){
			screenObj.initDom();
			screenObj.handler();
			screenObj.reRender();
		}
	});

	function Screen(){}
	
	Screen.prototype={
		constructor:Screen,
		bizs:{},//业务id作为键
		tops:{},//业务id作为键
		userBiz:[],
		getDataCfg:{
			successMsg:null,
		},
		userJq:0,
		combobox:0,
		top_oc_data:"",
		userLiDom:'<li class="oc-setnumbg"><span></span><span class="oc-workbench-rtitle-del del-icolocate"></span></li>',
		initDom:function(){
			var that=this;
			screen.find('div.panels:first > .content').click(function(){
				
				var self=$(this),data=self.attr('oc-data').split(',');
				that.addUserLi({
					bizId:data[0],
					bizType:data[1],
					thumbnail:data[2],
					title:self.find('.title').text()
				});
			});
			this.userJq=screen.find('div.user-biz:first > ul').sortable();
			this.combobox=oc.ui.combobox({
				selector:screen.find('select[name=domainId]'),
				placeholder:0,
				data:oc.index.getUser().domains
			});
		},
		getData:function(userFn){
			var _self=this;
			oc.util.ajax({
				url:oc.resource.getUrl('home/screen/getScreenSetData.htm'),
				success:function(d){
					d=d.data;
					//设置业务视图默认为打开为第一个业务视图
					if(d.biz && d.biz.length > 0){
						var biz_oc_data = d.biz[0].bizId+",1";
						screen.find("div.content:[oc-data='0,1']").attr('oc-data',biz_oc_data);
					}
					//设置拓扑视图默认打开为二层拓扑
					if(d.top && d.top.length > 0){
						top_oc_data = d.top[0].bizId+",2";
						screen.find("div.content:[oc-data='0,2']").attr('oc-data',top_oc_data);
					}
					userBiz=_self.userBiz=d.user.sort(function(o1,o2){
						return o1.sort<o2.sort;
					});
					
					//控制业务是否显示
					screen.find('.content').each(function(){
						var thisObj = $(this);
						
						if(thisObj.attr('name')=='business-list'){
							if(d['ifHaveBizAuthority']){
								thisObj.css('display','block');
							}else{
								thisObj.css('display','none');
							}
							
						}
					});
					
					
					userFn&&userFn(userBiz);
				}
			});
		},
		reRender:function(){
			this.userJq.children().remove();
			var _self=this;
			
			this.getData(function(userBizs){
				for(var i=0,len=userBizs.length;i<len;i++){
					_self.addUserLi(userBizs[i]);
					if(userBizs[i].domainId)_self.combobox.jq.combobox('setValue',userBizs[i].domainId);
				}
			});
		},
		addUserLi:function(biz){
			var dom=$(this.userLiDom).data('biz-data',biz);
			dom.find('span:first').text(biz.title).next().click(function(){
				dom.remove();
			});
			
			oc.util.ellipcis(dom.children()[0],12);
			var childs=this.userJq[0].children;
			for(var i=0;i<childs.length;i++){
				if(dom[0].textContent==childs[i].textContent){
					alert(dom[0].textContent+"已存在，不能重复添加！");
					return;
				}
			}
			this.userJq.append(dom);
		},
		handler:function(){
			var _self=this,userId=oc.index.getUser().id;
			this.userJq.next().find('a:first').linkbutton('RenderLB',{
				iconCls:'fa fa-check-circle',
				onClick:function(){//提交
					var ds=[],biz,i=0,domainId=_self.combobox.jq.combobox('getValue');
					_self.userJq.find('> li').each(function(){
						biz=$(this).data('biz-data');
						biz.sort=i++;
						biz.userId=userId;
						biz.domainId=domainId;
						ds.push(biz);
					});
					oc.util.ajax({
						url:oc.resource.getUrl('home/screen/saveUserBizRels.htm'),
						successMsg:'保存大屏设置成功！',
						failureMsg:'保存大屏设置失败！',
						data:{userBizRel:ds},
						success:function(d){
							screen.dialog('close');
							oc.home.screen.reLoad();
						}
					});
				}
			}).next().linkbutton('RenderLB',{
				iconCls:'fa fa-times-circle',
				onClick:function(){//取消
					screen.dialog('close');
				}
			});
		}
	};

	screenObj=new Screen();
	
	oc.ns('oc.home.setting');
	
	oc.home.setting.screen={
		open:function(){
			screen.dialog('open');
		},
		getData:function(fn){
			screenObj.getData(fn);
		}
	};
})();