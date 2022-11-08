(function($){
	var _dateBoxDefaults={
			width:160,
			editable:true,
			placeholder:'${oc.local.ui.select.placeholder}'
		};
	
	var _numberBoxDefaults={
			width:160
		};
	
	var _numberSpinnerDefaults={
			width:160,
			increment:10,
			min:0,
			max:100,
			value:0
		};
	
	var _timeSpinnerDefaults={
			width:160,
			showSeconds:true
		};
	
	var _sliderDefaults={
			width:160,
			showTip:true
		};

	var _uploadOpts = {
		url : oc.resource.getUrl('platform/file/fileUpload.htm'),
		type : 'POST',
//		accept:'gif|jpg|png|bmp|swf',
		iframe : true,
		data:{fileGroup:'Portal',fileId:0},
		timeout: 300000,
		async:false,
		success : function(responseText, statusText, xhr, $form) {
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert('文件上传失败！');
			log(XMLHttpRequest, textStatus, errorThrown);
		}
	};
	
	function uploadify_onSelectError(file, errorCode, errorMsg) {
        var msgText = "上传失败\n";
        switch (errorCode) {
            case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
                //this.queueData.errorMsg = "每次最多上传 " + this.settings.queueSizeLimit + "个文件";
                msgText += "每次最多上传 " + this.settings.uploadLimit + "个文件";
                break;
            case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
                msgText += "文件大小超过限制( " + this.settings.fileSizeLimit + " )";
                break;
            case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
                msgText += "文件大小为0";
                break;
            case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
                msgText += "文件格式不正确，仅限 " + this.settings.fileTypeExts;
                break;
            default:
                msgText += "错误代码：" + errorCode + "\n" + errorMsg;
        }
        alert(msgText);
    }
 
    function uploadify_onUploadError(file, errorCode, errorMsg, errorString) {
        // 手工取消不弹出提示
        if (errorCode == SWFUpload.UPLOAD_ERROR.FILE_CANCELLED
                || errorCode == SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
            return;
        }
        var msgText = "上传失败\n";
        switch (errorCode) {
            case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
                msgText += "HTTP 错误\n" + errorMsg;
                break;
            case SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL:
                msgText += "上传文件丢失，请重新上传";
                break;
            case SWFUpload.UPLOAD_ERROR.IO_ERROR:
                msgText += "IO错误";
                break;
            case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
                msgText += "安全性错误\n" + errorMsg;
                break;
            case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
                msgText += "每次最多上传 " + this.settings.uploadLimit + "个";
                break;
            case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
                msgText += errorMsg;
                break;
            case SWFUpload.UPLOAD_ERROR.SPECIFIED_FILE_ID_NOT_FOUND:
                msgText += "找不到指定文件，请重新操作";
                break;
            case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
                msgText += "参数错误";
                break;
            default:
                msgText += "文件:" + file.name + "\n错误码:" + errorCode + "\n"
                        + errorMsg + "\n" + errorString;
        }
        alert(msgText);
    }
	
	var _multiFileOpts ={
				buttonClass:'oc-filestyle',
				formData:{fileGroup:'Portal',fileId:0},
				fileObjName:'file',
				swf           : oc.resource.getUrl('resource/third/uploadify/uploadify.swf'),
				uploader      : oc.resource.getUrl('platform/file/fileUpload.htm'),
				width         : 80,
				multi :true,
				auto :true,
				buttonText :'选择文件',
				fileSizeLimit:'5120KB',
				method:'post',
				removeCompleted:true,
				removeTimeout:0,
				//fileTypeDesc: '*.jpg;*.png;*.doc;*.xls;*.xlsx;*.txt;*.zip;*.rar;',//文件后缀描述
               // fileTypeExts: '*.jpg;*.png;*.doc;*.xls;*.xlsx;*.txt;*.zip;*.rar;',//文件后缀限制
                onSelect:function(){},
                onSelectError : uploadify_onSelectError,
                onUploadError : uploadify_onUploadError
			};
	
	//获取表单名称
	function gN(inp){
		if(inp.is('[name]'))return inp.attr('name');
		return inp.find('[name]').attr('name');
	}
	function form(cfg){
		this.easyui=[];
		this.ocui=[];
		this.coms={};
		cfg=this.cfg=$.extend({},this._defaults,cfg);
		if(typeof cfg.selector=='string')cfg.selector=$(cfg.selector);
		cfg.selector.is('form')||(cfg.selector=cfg.selector.find('form'));
		this.jq=cfg.selector.form();
		this.jq.find('[required]').after('<span class="oc-valid-required-star">*</span>');
		if(cfg.combobox)this._initCombobox(cfg.combobox);
		if(cfg.combotree)this._initCombotree(cfg.combotree);
		if(cfg.combogrid)this._initCombogrid(cfg.combogrid);
		if(cfg.datebox)this._initDatebox(cfg.datebox);
		if(cfg.datetimebox)this._initDatetimebox(cfg.datetimebox);
		if(cfg.numberbox)this._initNumberbox(cfg.numberbox);
		if(cfg.numberspinner)this._initNumberspinner(cfg.numberspinner);
		if(cfg.timespinner)this._initTimespinner(cfg.timespinner);
		if(cfg.slider)this._initSlider(cfg.slider);
		if(cfg.filebox)this._initFilebox(cfg.filebox);
		if(cfg.multiFileBox)this._initMultiFileBox(cfg.multiFileBox);
		this._initValid();
		this.selector=cfg.selector;
		if(cfg.loaded){
			setTimeout(cfg.loaded,300);
		}
		this._initVal=this.val();
	}
	
	form.prototype={
		constructor:form,
		id:undefined,//每个组件都可以有一个随机id
		jq:undefined,//所有ui组件需提供该对象，用于表示ui组件最顶层文档元素对应的jquery对象
		cfg:undefined,//组件渲染时所使用的配置 简写能够很容易表达其意的时候使用简写
		easyui:[],
		ocui:[],
		coms:{},
		filebox:[],
		multiFileBox:{},
		multiFileIds:[],
		_defaults:{
			combobox:[],
			combotree:[],
			combogrid:[],
			datebox:[],
			datetimebox:[],
			loaded:0
		},
		getInput:function(inpName){
			this.coms[inpName];
		},
		submitWithFile:function(ajaxOpts){
			if(this.validate()){
				var that=this,val=that.val();
				if(this.filebox.length>0){
					var filebox=this.filebox[0];
					filebox.selector.filebox("getValue") ? this.jq.ajaxSubmit($.extend({},filebox,_uploadOpts,{
						success:function(responseText){
							var fileKey=Number($(responseText).text());
							if(fileKey){
								val.fileId=fileKey;
								ajaxOpts=$.extend(true,ajaxOpts,{data:val});
								oc.util.ajax(ajaxOpts);
							}else{
								alert('文件上传失败！');
							}
						}
					})) : oc.util.ajax(ajaxOpts);;
				}
			}
		},
		find:function(selector){
			return this.jq.find(selector);
		},
		disable:function(){
			this.disableValidation();
			this.jq.find(':input:not(:radio,:checkbox)').attr('readOnly',true);
			this.jq.find(':radio,:checkbox').attr('disabled',true);	//改为	:input可以选中select和textarea之类的 input型标签
			for(var i=0,len=this.easyui.length,ui;i<len;i++){
				ui=this.easyui[i];
				ui.selector[ui['uiType']]('disable');
			}
			for(var i=0,len=this.ocui.length;i<len;i++){
				this.ocui[i].disable();
			}
		},
		enable:function(){
			this.enableValidation();
			this.jq.find(':input:not(:radio,:checkbox)').attr('readOnly',false);
			this.jq.find(':radio,:checkbox').attr('disabled',false);
			for(var i=0,len=this.easyui.length,ui;i<len;i++){
				ui=this.easyui[i];
				ui.selector[ui['uiType']]('enable');
			}
			for(var i=0,len=this.ocui.length;i<len;i++){
				this.ocui[i].enable();
			}
		},
		val:function(obj){
			if(obj){
				this.jq.form('load',obj);
				return this;
			}else{
				var json={},ps=this.jq.serialize(),p;
				ps=ps.replace(/(\d{1,4}-\d{1,2}-\d{1,2})\+(\d{1,2}:\d{1,2}:\d{1,2})/g,'$1 $2').replace(/\+/g,' ');
				ps=ps.split('&');
				for(var i=0;i<ps.length;i++){
					p=ps[i].split('=');
					if(p[1]){
						p[1]=decodeURIComponent(p[1]);
						if(json[p[0]]){
							json[p[0]]+=','+p[1].toString().trim();
						}else{
							json[p[0]]=p[1].toString().trim();
						}
					}
				}
				if(this.cfg.multiFileBox)json.fileIds = this.multiFileIds.join();
				return json;
			}
		},
		multiFileVal:function(obj){
			if(obj){
				var jq=this.jq,that = this;
				var mfbox = this.multiFileBox;
				var fileItem = mfbox.fileItem;
				if(mfbox!=undefined){
					for(var i=0;i<obj.length;i++){
						var file = obj[i];
						var itemTemplate = "";
						if(fileItem!=undefined && fileItem!=""){
							itemTemplate = fileItem.replace('${fileId}',file.id).replace('${fileName}',file.name);
						}else{
							itemTemplate = '<div id="'+file.id+'" class="uploadify-queue-item">\
				                    <div class="cancel">\
										<a href="javascript:void(0);">X</a>\
				                    </div>\
				                    <span class="fileName">'+file.name+'</span>\
				                </div>';
						}
						var showDetail;
						if(mfbox.showSelector){
							showDetail = jq.find(mfbox.showSelector);
						}
						else{
							showDetail = jq.find('#file_upload-queue');
						}
						showDetail.append(itemTemplate).find("#"+file.id).children('.cancel').children('a:first').bind('click',(function(f){
							return function(){
								that._deleteMultiFile(f.id);
							};
						})(file));;
						that.multiFileIds.push(file.id);
					}
				}
				return this;
			}else{
				var json={};
				if(this.cfg.multiFileBox)json.fileIds = this.multiFileIds.join();
				return json;
			}
		},
		load:function(url){
			this.jq.form('load',url);
		},
		clear:function(){
			this.jq.form('clear');
		},
		reset:function(){
			this.jq.form('reset');
			this.val(this._initVal);
		},
		validate:function(){
			return this.jq.form('validate');
		},
		getInValidate:function(){
			var jqs=this.jq.find(".validatebox-invalid");
			jqs.filter(":not(:disabled):first").focus();
			return jqs;
		},
		disableValidation:function(inps){
			if(inps){
				inps=$.isArray(inps)?inps:[inps];
				for(var i=0,len=inps.length;i<len;i++){
					this.jq.find('[name='+inps[i]+']').validatebox('disableValidation');
				}
			}else{
				this.jq.form('disableValidation');
			}
		},
		enableValidation:function(inps){
			if(inps){
				inps=$.isArray(inps)?inps:[inps];
				for(var i=0,len=inps.length;i<len;i++){
					this.jq.find('[name='+inps[i]+']').validatebox('enableValidation');
				}
			}else{
				this.jq.form('enableValidation');
			}
		},
		hide:function(inps){
			if(inps){
				inps=$.isArray(inps)?inps:[inps];
				for(var i=0,len=inps.length;i<len;i++){
					this.jq.find('[name='+inps[i]+']').closest('.form-group').hide();
				}
			}else{
				this.jq.hide();
			}
		},
		show:function(inps){
			if(inps){
				inps=$.isArray(inps)?inps:[inps];
				for(var i=0,len=inps.length;i<len;i++){
					this.jq.find('[name='+inps[i]+']').closest('.form-group').hide();
				}
			}else{
				this.jq.show();
			}
		},
		_initValid:function(){
			this.jq.find('[validType],[required]').each(function(i,j){
				j=$(j);
				var isRequired=j.attr('required');
				var validType = j.attr('validType');
				if(validType&&(validType.indexOf("[")==0||validType.indexOf("{")==0)){
					validType = eval(validType);
				}
				j.validatebox({
					required:isRequired,
					validType:validType
				});
			});
		},
		_initSlider:function(cfg){
			var c,jq=this.jq;
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({},_sliderDefaults,cfg[i]);
				c.uiType='slider';
				c.selector=jq.find(c.selector).slider(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		},
		_initFilebox:function(cfg){
			var c,jq=this.jq;
			this.filebox=[];
			for(var i=0,len=cfg.length;i<len;i++){
				c=cfg[i];
				c.uiType='filebox';
				c.selector=jq.find(c.selector).filebox(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
				this.filebox.push(c);
			}
		},
		_initMultiFileBox:function(cfg){
			var c,jq=this.jq,that = this;
			this.multiFileBox ={};
			this.multiFileIds = [];
			var selector = cfg.selector;
			var showSelector = cfg.showSelector;
			var fileItem = cfg.fileItem;
			//data为文件上传成功后的文件数据ID，file.id为文件上传组件自动生成的默认文件ID
			c=$.extend({onUploadSuccess:function(file, data, response){
				var reg = /^[0-9]*[1-9][0-9]*$/;
				if(file && reg.test(data)){
					var itemTemplate ="";
					if(fileItem!=undefined && fileItem!=""){
						itemTemplate = fileItem.replace('${fileId}',data).replace('${fileName}',file.name);
					}else{
						itemTemplate = '<div id="'+data+'" class="uploadify-queue-item">\
			                    <div class="cancel">\
									<a href="javascript:void(0);">X</a>\
			                    </div>\
			                    <span class="fileName">'+file.name+'</span>\
			                </div>';
					}
					var showDetail;
					if(showSelector){
						showDetail = jq.find(showSelector);
					}
					else{
						showDetail = jq.find('#file_upload-queue');
					}
					showDetail.append(itemTemplate).find("#"+data).find('.cancel a:first').bind('click',(function(d){
						return function(){
							jq.find(selector).uploadify('cancel',file.id);
							that._deleteMultiFile(d);
						};
					})(data));
					that.multiFileIds.push(data);
					jq.find(selector).uploadify('settings','buttonText','继续上传');
					if(cfg.uploadSuccess){
						cfg.uploadSuccess(file, data, response);
					}
				}else{
					alert("文件上传失败！");
				}
			}},_multiFileOpts,cfg);
			jq.find(selector).uploadify(c);
			this.easyui.push(c);
			this.multiFileBox= c;
			
		},
		_deleteMultiFile:function(fileId){
			var jq=this.jq,that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('platform/file/deleteFile.htm'),
				async:false,
				data:{fileId:fileId},
				success : function(s) {
					if(that.multiFileBox.showSelector){
						jq.find(that.multiFileBox.showSelector).find("#"+fileId).remove();
					}
					else{
						jq.find('#file_upload-queue').find("#"+fileId).remove();
					}
					
					that.multiFileIds.splice(that.multiFileIds.indexOf(fileId), 1);
					if(that.multiFileIds==undefined || that.multiFileIds.length<=0){
						jq.find(that.multiFileBox.selector).uploadify('settings','buttonText','选择文件');
					}
				},
				successMsg:""
			});
		},
		_initNumberspinner:function(cfg){
			var c,jq=this.jq;
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({},_numberSpinnerDefaults,cfg[i]);
				c.uiType='numberspinner';
				c.selector=jq.find(c.selector).numberspinner(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		},
		_initTimespinner:function(cfg){
			var c,jq=this.jq,time=new Date().stringify('hh:MM:ss');
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({value:time},_timeSpinnerDefaults,cfg[i]);
				c.uiType='timespinner';
				c.selector=jq.find(c.selector).timespinner(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		},
		_initNumberbox:function(cfg){
			var c,jq=this.jq;
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({},_numberBoxDefaults,cfg[i]);
				c.uiType='numberbox';
				c.selector=jq.find(c.selector).numberbox(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		},
		_initCombobox:function(boxCfg){
			var box,jq=this.jq;
			for(var i=0,len=boxCfg.length;i<len;i++){
				box=boxCfg[i];
				box.selector=jq.find(box.selector);
				this.ocui.push(this.coms[gN(box.selector)]=oc.ui.combobox(box));
			}
		},
		_initCombotree:function(treeCfg){
			var tree,jq=this.jq;
			for(var i=0,len=treeCfg.length;i<len;i++){
				tree=treeCfg[i];
				tree.selector=jq.find(tree.selector);
				this.ocui.push(this.coms[gN(tree.selector)]=oc.ui.combotree(tree));
			}
		},
		_initCombogrid:function(gridCfg){
			var grid,jq=this.jq;
			for(var i=0,len=gridCfg.length;i<len;i++){
				grid=gridCfg[i];
				grid.selector=jq.find(grid.selector);
				this.ocui.push(this.coms[gN(grid.selector)]=oc.ui.combogrid(grid));
			}
		},
		_initDatebox:function(cfg){
			var c,jq=this.jq,date=new Date().stringify('yyyy-mm-dd');
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({value:date},_dateBoxDefaults,cfg[i]);
				c.uiType='datebox';
				c.selector=jq.find(c.selector).datebox(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		},
		_initDatetimebox:function(cfg){
			var c,jq=this.jq,date=new Date().stringify();
			for(var i=0,len=cfg.length;i<len;i++){
				c=$.extend({value:date},_dateBoxDefaults,cfg[i]);
				c.uiType='datetimebox';
				c.selector=jq.find(c.selector).datetimebox(c);
				this.coms[gN(c.selector)]=c.selector;
				this.easyui.push(c);
			}
		}
	};
	
	oc.ui.form=function(cfg){
		return new form(cfg);
	};
})(jQuery);