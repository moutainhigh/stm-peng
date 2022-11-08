/**
 * 用于资源选择 RS为resourceSelect缩写
 */
(function(){
	var firstType={},secondType=[],subType={};
	function loadResources(fn){
		oc.util.ajax({
			url:oc.resource.getUrl('system/resource/getResourcesByResource.htm'),
			data:{lifeState:'MONITORED',domainIds:oc.home.workbench.domainId},
			success:function(d){
				var reses=d.data;
				for(var i=0,len=reses.length,sType;i<len;i++){
					d=reses[i];
					if(sType=subType[d.categoryId]){
						sType.resources=sType.resources||[];
						sType.resources.push(d);
					}
				}
				
				for(var i=0,len=secondType.length,sType,fType;i<len;i++){
					sType=secondType[i];
					if(sType.resources){
						fType=firstType[sType.pid]=firstType[sType.pid]||[];
						fType.push(sType);
					}
				}
				secondType=subType=null;
				fn&&fn();
			}
		});
	}
	//远程加载所有资源类型并缓存
	function loadResourceType(fn){
		if(secondType){
			oc.util.ajax({
				url:oc.resource.getUrl('system/resource/getCategory.htm'),
				success:function(ds){
					ds=ds.data.children;
					for(var i=0,len=ds.length,fD,sD,fC,subCategoryIds;i<len;i++){
						if(fD=ds[i]){//一级类型
							if(fC=fD.children){
								for(var j=0,jLen=fC.length;j<jLen;j++){
									if(sD=fC[j]){//二级类型
										secondType.push(sD);
										if(subCategoryIds=sD.subCategoryIds){
											for(var m=0,mLen=subCategoryIds.length;m<mLen;m++){subType[subCategoryIds[m]]=sD;}
										}
									}
								}
							}
						}
					}
					loadResources(fn);
				}
			});
		}else{
			fn&&fn();
		}
	}

	//根据大资源类型获取子资源类型
	function getSubResourceTypes(type){
		var t=[];
		if(type=='all'){
			for(var k in firstType){
				firstType[k]&&(t=t.concat(firstType[k]));
			}
		}else if(type=='app'){
			//数据库、目录服务器、J2EE应用、LotusDomino、邮件服务器、Web服务器、中间件和基础服务(标准服务)
			firstType.Database&&(t=t.concat(firstType.Database));
			firstType.Middleware&&(t=t.concat(firstType.Middleware));
			firstType.J2EEAppServer&&(t=t.concat(firstType.J2EEAppServer));
			firstType.WebServer&&(t=t.concat(firstType.WebServer));
			firstType.MailServer&&(t=t.concat(firstType.MailServer));
			firstType.Directory&&(t=t.concat(firstType.Directory));
			firstType.LotusDomino&&(t=t.concat(firstType.LotusDomino));
			firstType.StandardService&&(t=t.concat(firstType.StandardService));
		}else{
			return firstType[type]||t;
		}
		return t;
	}
	
	var rs=null;
	
	function RS(cfg){
		this.con=$(this._dom.content);
		this.form=oc.ui.form({selector:this.con.find('form:first')});
	}
	
	RS.prototype={
		constructor:RS,
		_defaults:{
			type:'all',//显示的大资源类型可取Host(主机)，NetworkDevice（网络设备）,app（应用）  默认为'all' 表示显示所有资源
			title:'主机',
			confirmFn:0,//点击确认回调函数，函数回传两个参数，分别为所选择资源的id和资源详细数据
			maxSelects:1,//最多选择的资源个数
			width:350,
			height:420,
			value:0,
			onClose:function(){
				rs.form.jq.children().remove();
			},
			buttons:[{
				text:'确定',
				iconCls:'fa fa-check-circle',
				handler:function(){
					var v=rs.form.val().resource, interfaceTypeV = rs.form.val().interfaceType;
					if(v){
						rs.dlg.dialog('close');
						rs.cfg.confirmFn&&setTimeout(function(){
							if(interfaceTypeV){
								rs.cfg.confirmFn(v, {selfExt1:interfaceTypeV});
							}else{
								rs.cfg.confirmFn(v);
							}
						},100);
					}else{
						alert('请选择一个资源！');
					}
				}
			},{
				text:'取消',
				iconCls:'fa fa-times-circle',
				handler:function(){
					rs.dlg.dialog('close');
				}
			}]
		},
		_dom:{
			content:'<div class="oc-home-resource-select w-darkbg padding-bottom8"><form class="oc-form oc-w-bgcolor" style="height:100%;overflow:auto;"></form></div>',
			type:'<div class="type"><div class="title i-host-tittle oc-type-line"><span class="i-host-ico"></span></div><div class="content" style="display: none;"></div></div>',
			selectRadio:'<div class="select i-host-bg" radio><label><input type="radio" name="resource"></label></div>',
			selectCheckbox:'<div class="select i-host-bg" radio><label><input type="checkbox" name="resource"></label></div>'
		},
		val:function(){
			if(this.cfg.value){
				var vs=this.cfg.value.split(',');
				this.form.val({resource:vs});
				var temp=this.form.selector.find('[value='+vs[0]+']').parents('.type').find('.title');
				if(temp.length>0){
					temp.click();
					return true;
				}
			}
			return false;
		},
		open:function(cfg){
			var self=this;
			this.cfg=cfg=$.extend({},this._defaults,cfg);
			if(this.dlg){
				this.dlg.dialog('open');
			}else{
				this.dlg=this.con.dialog(cfg);
			}
			loadResourceType(function(){
				self._generateContent(cfg.type);
			});
		},
		renderResources:function(rses,categoryJq){
			if(rses.length>0){
				var self=this;
				for(var j=0,jLen=rses.length,rs,showVal,con=categoryJq.find('.content'),
					inp,sDom=(self.cfg.maxSelects==1)?self._dom.selectRadio:self._dom.selectCheckbox,sJq;
					j<jLen;j++){
					rs=rses[j];
					sJq=$(sDom);
					inp=sJq.find('input').val(rs.id).click(function(e){
						var v=self.form.val();
						if(v.resource&&v.resource.split(',').length>self.cfg.maxSelects){
							alert('最多只能选择'+self.cfg.maxSelects+'个资源！');
							return false;
						}
					});
					showVal=('('+(rs.discoverIP||'无IP')+')')+(rs.showName||rs.name);
					inp.after(showVal).parent().attr('title',showVal);//设置资源值和名称
					con.append(sJq);
				}
			}
		},
		_generateContent:function(type){
			var subResourceTypes = getSubResourceTypes(type||'all'), t = this, formJq = t.form.jq, innerContainer = formJq, firstTitle=0;
			if('NetworkDevice' == type){
				var accordionContainer = $("<div class='home_interface_set_accordion' style='height:100%;'></div>").appendTo(formJq.css({'overflow':'hidden'}))
				var innerContainer = $("<div title='资源选择'/>");
				accordionContainer.append(innerContainer).append("<div title='面板图设置'>" +
						"<div class='select i-host-bg' radio><label><input type='radio' name='interfaceType' value='all' checked='checked'>全部接口</label></div>" +
						"<div class='select i-host-bg' radio><label><input type='radio' name='interfaceType' value='real'>物理接口</label></div>" +
						"<div class='select i-host-bg' radio><label><input type='radio' name='interfaceType' value='logic'>逻辑接口</label></div>" +
					"</div>");
				accordionContainer.accordion({
					fit : true
				});
				if(t.cfg.ext1Value){
					accordionContainer.find("[name='interfaceType'][value='" + t.cfg.ext1Value + "']").attr("checked",'checked');
				}
			}else{
				formJq.css({'overflow':'auto'});
			}
			if(subResourceTypes.length>0){
				for(var i=0, len=subResourceTypes.length, categoryJq, tempTitle; i<len; i++){
					category=subResourceTypes[i];
					categoryJq=$(this._dom.type);
					t.renderResources(category.resources, categoryJq);
					tempTitle=categoryJq.appendTo(innerContainer).find('.title').click(function(){
						var self=$(this).toggleClass("active");
						if(self.is('.active')){
							self.next().fadeIn().prev().find('span:first').addClass('i-host-icodown').removeClass('i-host-tittle');
						}else{
							self.next().fadeOut().prev().find('span:first').removeClass('i-host-icodown');
						}
						self.closest('div.type').siblings().find('div.content:first').hide().prev().removeClass("active").find('span:first').removeClass('i-host-icodown');
					}).append(category.name);//设置资源类型名称
					if(i==0){
						firstTitle = tempTitle;
					}
				}
				if((!t.val())&&firstTitle){
					firstTitle.click();
				}
			}else{
				innerContainer.append('<div class="oc-home-rs-empty"/>');
			}
		}
	};
	
	oc.ns('oc.home.workbench.resource');
	
	rs=new RS();
	
	oc.home.workbench.resource.select={
		open:function(cfg,fn){
			rs.open(cfg);
		},
		setDomain:function(domainId){
			firstType={};secondType=[];subType={};
			oc.home.workbench.domainId=domainId;
		}
	};
})();