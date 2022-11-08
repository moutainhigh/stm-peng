(function($){
	var $container = $('#oc_system_right_main').attr("id", oc.util.generateId());
	$container.find('#tag-panel').panel('RenderP',{
		title:'页签管理',
		width:'280px',
		tools:[{iconCls:'fa fa-plus'},'-',{iconCls:'fa fa-save'}]//icon-save icon-carda
	});
	//重写panel样式，适应IE8
	$container.find('.system-parent').find('.panel').removeClass('panel').addClass('systemPagePanel');
	var isAdd=true;
	
	//当前编辑页签name
	var editTab_title = null;
	
	var contentPanel = $container.find("#oc_system_right_content_panel")
	.attr("id", oc.util.generateId()).panel({
		title:'页签添加',
		onOpen:function(){
			$container.find("a.app_btn").first().linkbutton('RenderLB', {
				text : '应用',
				iconCls : 'fa fa-check-circle',
				onClick : function() {
					methods.formSubmit(isAdd ? 'add' : 'edit');
				}
			});
		}
	}).panel('RenderP');
	var  _picDefaults={
    	_baseSize : 1024,	//1KB
    	format : ['PNG','JPEG','BMP', 'JPG'],
    	formatMsg : '图片格式不符合要求，请更换',
    	maxSize : 100,	//单位KB
    	sizeMsg : '文件不能超过100KB'
    };
	var addForm=oc.ui.form({
		selector:$container.find(".oc-form"),
		filebox:[{
			selector:'[name=file]',
			onChange:function(newValue,oldValue){
				var filebox = addForm.filebox[0].selector,
				filePath = filebox.filebox('getValue');
				filebox.textbox('setText',newValue.substr(newValue.lastIndexOf("\\") + 1));
				cfg = $.extend({},_picDefaults);
				if(!filePath){
					return true;
				}
	    		if(cfg.format.indexOf(filePath.substring(filePath.lastIndexOf(".")+1).toUpperCase())==-1){
	    			filebox.filebox("initValue", "");
	    			alert(cfg.formatMsg);
	    			return true;
	    		}
	    		var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
	    		var fileSize = 0,
	    		file = filebox.data("textbox").textbox.find(".textbox-value").get(0);
	    	    if (isIE && !file.files) {
//	    	    	var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
//	    	    	fileSize = fileSystem.GetFil(filePath).Size;
	    	    	
	    	    } else {
	    	    	fileSize = file.files[0].size;
	    	    }
	    	    var maxSize = cfg._baseSize*cfg.maxSize;
	    	    if(fileSize>maxSize){
	    	    	filebox.filebox("initValue", "");
	    			alert(cfg.sizeMsg);
	    			return true;
	    	     }
	    	    return true;
			}
		}]
	});
    
    var methods = {
    		_form : addForm,
    		_cacheData : undefined,
    		_statusImgUrl :Highcharts.theme.currentSkin=="blue"? oc.resource.getUrl('resource/themes/blue/images/default.png') : oc.resource.getUrl('resource/themes/default/images/default.png'),
    		formSubmit:function(type){
    			var target = this;
    			//获取页签集合，遍历是否与新建名称冲突
    			var tabtips = new Array();
    			$('#tag-panel').find('span.oc-panel-font').each(function(){
    				tabtips.push($(this).text());
    			});
    			var newTabName = target._form.selector.find('input[name=name]').val().trim();
    			var sign_newTabNameIsAvailable = true;
    			
    			//新增页签名称不可与现有页签重复
    			//编辑页签名称可与自己名称重复，从现有页签列表中剔除自己项
    			if(type!='add'){
    				tabtips.remove(editTab_title);
    			}
    			
    			for (var i = 0; i < tabtips.length; i++) {
					if(newTabName == tabtips[i]){
						sign_newTabNameIsAvailable = false;
						break;
					}
				}
    			if(sign_newTabNameIsAvailable){
    				addForm.submitWithFile({
    					url: oc.resource.getUrl('system/right/'+(type=='add' ? 'insert' : 'update') + '.htm'),
    					data:target._form.val(),
    					success:function(data){
    						data.code==200&&(function(){
    							type=='add'&&target._form.reset();
    							target._loadRights();
    							oc.index.reloadLoginUser();
    						})();
    					}
    				});
    			}else{
    				alert('名称不可用');
    			}
    		},
    		del:function(id){
    			oc.util.ajax({
    				url:oc.resource.getUrl('system/right/del.htm'),
    				data:{id:id},
    				success:function(xhr){
    					xhr.code==200&&$container.find("#"+id).remove();
    				}
    			});
    		},
    		initDrags:function(sort){
    			$container.find('.l-btn-icon').on('mousedown',function(e){
		    		e.stopPropagation();
		    	});
    			sort.sortable({
    				opacity: 0.6,
    				revert: true
    			});
    		},
    		_loadRights : function (){
    			var target = this;
    	    	oc.util.ajax({
    	    		successMsg:null,
    				url:oc.resource.getUrl('system/right/getAll.htm'),
    				success:function(data){
    					var sort=$('<ul></ul>'),rights=data.data;
    					target._cacheData = {};
    					for(var i=0,len=rights.length; i<len; i++){
    						var right = rights[i];
    						target._cacheData[right.id] = $.extend({},right);
    				        
    						sort.append('<li><div class="main-box" id='+ right.id +'>'+
    				    	'<div class="main-box-icon">'+
    			            '     	<span class="oc-panel-pic"><img src="'+(right.fileId!=0 ? oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId=' + right.fileId) : target._statusImgUrl)+'"/></span>'
    			            	   + '<span class="oc-panel-font">'+right.name+'</span>' +
    			            '     </div>'+
    			            '     <div class="mian-btn-right">'+
    			            '         <div class="mian-btn-relative"><span title="修改" class="l-btn-icon icon-edit"></span></div>'+
    			            (right.type==1 ? '         <div class="mian-btn-relative"><span title="删除" class="l-btn-icon fa fa-trash-o"></span></div>' : '')+
    			            '     </div>'+
    				        '</div></li>');
    			        }
    			        $container.find('#pageTag-div').html("").append(sort).
    			        css({height:$container.parent().height()-30});
    					target.initDrags(sort);
    			        
    			        $container.find('.fa-trash-o').on('click',function(){
    			        	var id=$(this).parents('.main-box').attr('id');
    			        	oc.ui.confirm('您确定要删除这个页签吗！',function(){
    			        		target.del(id);
    						});
    			        });
    			        $container.find('.fa-plus').off().attr('title','添加').on('click',function(){
    			        	 editBtn.parents('.main-box').removeClass("active");
    				    	 isAdd=true;
    				    	 var url = target._form.selector.find('[name=url]');
    				    	 url.removeAttr('readonly');
    				    	 var isNewTag = $container.find('[name="isNewTag"]');
				    		 isNewTag.removeAttr('disabled');
    				    	 var options = url.validatebox("options");
				    		 options.validType = "url";
				    		 url.validatebox("enableValidation");
    				    	 target._form.reset();
    				    	 contentPanel.panel('setTitle','页签添加');
    				     });
    			         var editBtn = $container.find('.icon-edit').off().on('click',function(e){
    			        	 e.stopPropagation();
    			        	 editBtn.parents('.main-box').removeClass("active");
    				    	 isAdd=false;
    				    	 var id=$(this).parents('.main-box').addClass("active").attr('id');
    				    	 var formData = $.extend(target._cacheData[id],{file:undefined});
    				    	 target._form.reset();
    				    	 target._form.val(formData);
    				    	 editTab_title = target._cacheData[id].name;
//    				    	 debugger;
    				    	 if(formData.type==0){
    				    		 var url =$container.find('[name="url"]');
    				    		 url.attr('readonly','readonly');
    				    		 var isNewTag = $container.find('[name="isNewTag"]');
    				    		 isNewTag.attr('disabled','disabled');
//    				    		 var options = url.validatebox("options");
//    				    		 options.validType = "internalUrl";
    				    		 //修改时，系统内部的取消URL验证
    				    		 url.validatebox("disableValidation");
    				    	 }else{
    				    		 var url =$container.find('[name="url"]');
    				    		 url.removeAttr('readonly');
    				    		 var isNewTag = $container.find('[name="isNewTag"]');
    				    		 isNewTag.removeAttr('disabled');
    				    		 var options = url.validatebox("options");
    				    		 options.validType = "url";
    				    		 url.validatebox("enableValidation");
    				    	 }
    				    	 contentPanel.panel('setTitle','页签编辑');
    					 });
//    			         editBtn.parents('.main-box').click(function(){	//单击外层div去除选中状态（目前还未确定是否开启此功能）
//    			        	 editBtn.parents('.main-box').removeClass("active");
//    			         });
    			         $container.find('.fa-save').off().attr('title','保存').on('click',function(){//icon-save
    			        	 var parameters=[];
    				    	 $container.find('.main-box').each(function(index){
    				    		 parameters.push({id:this.id,sort:index});
    				    	 });
    				    	 oc.util.ajax({
    				 			url:oc.resource.getUrl('system/right/updateSort.htm'),
    				 			data:{datas:parameters},
    				 			success:function(data){
    				 				data.code==200&&target._form.reset();
    				 				oc.index.reloadLoginUser();
    				 			}
    				 		});
    				     });
    				}
    			});
    	    }
    };
    
    Array.prototype.indexOf = function(val) {              
        for (var i = 0; i < this.length; i++) {  
            if (this[i] == val) return i;  
        }  
        return -1;  
    };  

    Array.prototype.remove = function(val) {  
        var index = this.indexOf(val),that;  
        if (index > -1) {  
            that = this.splice(index, 1);  
        }
        return that;
    };
    
    methods._loadRights();
})(jQuery);