(function($){
	function bizServiceDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	bizServiceDetail.prototype={
		constructor:bizServiceDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type,ul=cfg.ul,rowDiv=cfg.rowDiv,
				buttons=[{
					text:'关闭',
					iconCls:'ico ico-cancel',
					handler:function(){
						dlg.dialog('close');
					}
				}];
			if(type!='view'){
				buttons.unshift({
					iconCls:'ico ico-ok',
					text:'保存',
					handler:function(){
						if(that.form.validate() && that.form.find("input[name=name]").val().trim() != ""){
							if(that.form.find("input[name='file']").val().trim()!=""
								&& !that.form.find("input[name='file']").val().trim().match(/gif|GIF|jpg|JPG|jpeg|JPEG|bmp|BMP|png|PNG$/)){
								alert("图片只能上传jpg、jpeg、gif、png这几种格式");
								return;
							}
							that.form.submitWithFile({
								url:oc.resource.getUrl(
										(type=='add')?'portal/business/service/insert.htm':'portal/business/service/update.htm'),
								data:that.form.val(),
								successMsg:null,
								success:function(data){
									if(data.code && data.code==200){
										cfg.saveCall&&cfg.saveCall(type,ul,data.data,that.form.val(),rowDiv);
										dlg.dialog('close');
										alert("保存成功");
									}else if(data.code==299){
										alert(data.data);
									}else if(data.code==201){
										alert("名称重复");
										return;
									}
								}
							})
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/business-service/business_service_detail.html'),
				title:(type=='add')?'新建业务应用':'编辑'+cfg.row.name,
				height:390,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					(type!='add')&&that._loadResource(cfg.row.id);
					if(that.cfg.type=='view'){
						that.form.disable();
					}
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:function(type,bizAccordion,id,json,rowDiv){
				var row = eval(json),user=oc.index.getUser();
				if(type=='add'){
					  row.id = id;
					  oc.business.service.west.appendBizAccordionCfg(row,bizAccordion,user,true);
				}else if(type=="edit"){
					rowDiv.find(".light-ico").text(row.name);
				}
				bizAccordion.find("span[value="+row.id+"]").click();
			}//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#bizServiceDetail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.bizServiceDetailForm'),
				filebox:[{
					selector:'[name=file]',
					width:164,
					onChange:function(newValue,oldValue){
						setImagePreview($("input[type=file]"),$("#localImag"),$("#preview"));
						$("#fileId").val("");
					}
				}]
			});
			this.form.find("#preview").attr("src",
					oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+1000))
					.css({'display':'block',width:'67px',height:'67px'});
			var user = oc.index.getUser();
			if(user.userType==3){
				this.form.find("input[name='entry_id']").val(user.id);
				this.form.find("input[name='entryName']").val(user.name);
			}
			this.form.find("input[name='entryName'],#selectEntryBtn").on('click',function(e){
				e.stopPropagation();
				selectEntry();
			});
			function selectEntry(){
				var entryDlg=$('<div/>'),entryContent=null,
				entryButtons=[{
					text:'确定',
					iconCls:'ico ico-ok',
					handler:function(){
						var selectRow = entryContent.datagrid('getChecked');
						if(selectRow&&selectRow.length>0){
							$("input[name='entryName']").val(selectRow[0].name);
							$("input[name='entry_id']").val(selectRow[0].id);
							entryDlg.dialog('close');
						}else{
							alert('请选择负责人');
						}
					}
				},{
					text:'取消',
					iconCls:'ico ico-cancel',
					handler:function(){
						entryDlg.dialog('close');
					}
				}],
				entryContent = $("<div id='entryContent'/>");
				entryDlg.dialog({
					content:entryContent,
					title:'选择负责人',
					width:410,
					height:340,
					resizable:true,
					buttons:entryButtons,
					cache:false
				});
				var entryDatagrid = oc.ui.datagrid({
					pagination:false,
					selector:entryContent,
					singleSelect:true,
					url:oc.resource.getUrl('portal/business/service/getSystemUsers.htm'),
					columns:[[
						 {field: 'id',title: '<input type="radio" style="display:none;"/>',width: 20,
							 formatter: function(value, rowData, rowIndex){
							 return '<input type="radio" name="selectRadio" id="selectRadio' + 
							 	rowIndex + '" value="' + rowData.oid + '" />';
						 }},
				         {field:'account',title:'用户名',sortable:true,align:'center',width:80},
				         {field:'name',title:'姓名',sortable:true,align:'center',width:80}
				     ]],
				     onLoadSuccess:function(data){
				    	 var item = entryContent.datagrid('getRows'),index;
				    	 if(item){
				    		 for(var i=item.length-1; i >= 0; i--){
				    			 if(item[i].id == $("input[name='entry_id']").val()){
				    				 index = entryContent.datagrid('getRowIndex', item[i]);
				    				 ($("input[type='radio']")[index+1]).checked=true;
				    				 break;
				    			 }
				    		 }
				    	 }
				     },
				     onSelect : function(index,row){
				    	 $('#selectRadio' + index).attr("checked",true);
				     }
				});
			}
		},
		_loadResource:function(id){
			var that=this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/get.htm'),
				data:{id:id},
				successMsg:null,
				success:function(d){
					that.form.val(d.data);
					that.form.find("#oldName").val(d.data.name);
					that.form.find("#preview").attr("src",
							oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+d.data.fileId))
							.css({'display':'block',width:'67px',height:'67px'});
				}
			});
		}
	};
	
	
	function setImagePreview(docObj,localImagId,imgObjPreview){
		docObj = docObj.get(0);
        if(docObj.files && docObj.files[0]){
            //火狐下，直接设img属性
        	imgObjPreview.css({'display':'block',width:'67px',height:'67px'})
        	.attr("src",window.URL.createObjectURL(docObj.files[0]));
        }else{
            //IE下，使用滤镜
            docObj.select();
            var imgSrc = document.selection.createRange().text;
            //必须设置初始大小
            localImagId.css({width:'67px',height:'67px'})
            //图片异常的捕捉，防止用户修改后缀来伪造图片
            try{
                localImagId.get(0).style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                localImagId.get(0).filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
             }catch(e){
                $.messager.alert("info","提示","您上传的图片格式不正确，请重新选择!");
                return false;
              }                         
              imgObjPreview.style.display = 'none';
              document.selection.empty();
        }
        return true;
    }
	
	oc.ns('oc.module.biz.service');
	oc.module.biz.service={
		open:function(cfg){
			var detail=new bizServiceDetail(cfg);
			detail.open();
		}
	};
})(jQuery);