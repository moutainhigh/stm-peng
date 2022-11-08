(function($){
	function MyAccodition(cfg){
		this._cfg=$.extend({},this._defaults,cfg);
		this.jq = $(this._cfg.selector);
		this._init();
	}
	
	MyAccodition.prototype = {
		constructor:MyAccodition,
		jq:undefined, 
		_defaults:{
			selector:undefined,	//选择器
			url:undefined,	//列表数据
			type:'post',
			params:{},	//参数
			columns:[],	//列信息
			contentEllipsis:false,
			filter:function(data){
				return data&&data.data ? data.data : [];
			},	//数据过滤
			loadColumnContentFn:function(row){	//每行内部内容组装
				var children = row.roles||[],
				div = $("<div/>");
				if(children.length>0){
					for(var i=0; i<children.length;i++){
						var ellipsis = this._cfg.contentEllipsis ? (" style='text-overflow:ellipsis;white-space: nowrap;overflow: hidden;' title='"+children[i].name+"' ") : "";
						$("<label"+ellipsis +"><input type='checkbox' class='oc-checkbox-locate' name='id' data-parent='"+row.id+"' value='"+children[i].id+"' />"+children[i].name+"</label>")
						.appendTo(div)
						.css("width", '33.3%')
						.find("[name=id]")
						.data("row", children[i])
						.data("parentRow", row);
					}
				}
				return div;
			}
		},
		_init:function(){
			function initTitle(titleDiv){
				var columns = this._cfg.columns,
				html = "<div style='width:25px;'>&nbsp;</div>";
				for(var i=0; i<columns.length; i++){
					html += "<div style='float:left;width:"+(columns[i].width ? columns[i].width+'px' : 'auto')+"'>" + (columns[i].title||"") + "</div>";
				}
				titleDiv.html(html).find("div").first().css("float",'left');
			}
			var titleDiv = this.jq
			.html('')
			.append("<div class='oc-useraccordion-title'/>").find(".oc-useraccordion-title").first(),
			contentDiv = titleDiv.parent().append("<div class='content'  style='clear: both;'/>").find(".content").first();
			initTitle.call(this,titleDiv);
			this._initRemoteData(contentDiv);
			this.jq.data("contentDiv", contentDiv);
		},
		_initContent:function(contentDiv){
			var datas = this.jq.data("datas"),
			rows = {},
			columns = this._cfg.columns,
			headObj = {};
			for(var i=0; i<datas.length; i++){
				var data = datas[i]||{},
				row = $("<div class='row' />").data("row", data),
				row_head = $("<div class='row_head' style='clear: both;'/>").on('click',function(){
					if($(this).find(">div>a.accordion-collapse").size()>0){
						return;
					}
					if(headObj.heads){
						headObj.heads.next().hide();
						headObj.heads.find(">div>a").removeAttr("class").addClass("accordion-expand");
					}
					$(this).next().show(200);
					$(this).find(">div>a").removeAttr("class").addClass("accordion-collapse");
				}).appendTo(row),
				row_content = $("<div class='row_content'  style='clear: both; width:100%;'/>").appendTo(row),
				head_html = "",
				children = data.children||[];
				head_html+="<div class='oc-tool-ico'><a href='javascript:void(0)' class='accordion-expand'></a></div>";
				for(var j=0; j<columns.length; j++){
					var field = columns[j].field;																//(columns[j].width ? columns[j].width+'px' : 'auto')
					head_html += "<div class='field"+(columns[j].label ? ' label_field' : '')+"' data-field='"+field+"' style='width:"+'auto'+" ;float: left;padding:0 2px;'>" + 
					(columns[j].format ? columns[j].format(i, data) : data[field]||"") + "</div>";
				}
				row_content.html('').append(this._cfg.loadColumnContentFn.call(this,data));
				
				(function(row, i){
					row.find(".row_content").find(":input[type=checkbox]").click(function(e,ext){
						var checked=ext?!this.checked:this.checked;
						var label = row.find(">div .label_field").first();
						if(label.size()>0){
							var text = $(this).parent().text();
							if(checked){
								label.append("<label class='oc-system-lable'> "+text+" </label>");
							}else{
								label.find("label").each(function(){
									var $this = $(this);
									$this.text().trim()==text&&$this.remove();
								});
							}
						}
					});
				})(row, i);
				row_head.html('').append(head_html).find("div").first().css("float",'left');
				rows[JSON.stringify(data)] = row;
				contentDiv.append(row);
			}
			headObj.heads = contentDiv.find(".row_head");
			headObj.heads.first().trigger("click");
			this.jq.data("rows", rows);
		},
		_initRemoteData:function(contentDiv){
			var that = this;
			oc.util.ajax({
	    		url:that._cfg.url,
	    		data:that._cfg.params,
	    		successMsg:null,
	    		async:false,
	    		failureMsg:'加载失败！',
	    		success:function(result){
	    			result.data = result.data||[];
	    			that._cfg.filter&&(result = that._cfg.filter(result));
	    			that.jq.data("datas", result);	//保存原始数据
	    			that._initContent(contentDiv);
	    			
	    		}
	    	});
		},
		select:function(parentId, id){
			this.jq.find(".row_content :input[type=checkbox][data-parent="+parentId+"][value="+id+"]").trigger("click",'ext');
		},
		getSelected:function(){	//此处返回的是复选框的jQuery对象，方便自己获取元素上面的信息，可以使用data保存数据
			var contentDiv = this.jq.data("contentDiv");
			if(contentDiv){
				return contentDiv.find("[type=checkbox]:checked");
			}else{
				return 0;
			}
		},
		//禁用所有复选框 
		disabledAll:function(){
			var contentDiv = this.jq.data("contentDiv");
			if(contentDiv){
				return contentDiv.find("[type=checkbox]").attr("disabled",true);
			}else{
				return 0;
			}
		},
		//取消禁用所有复选框 
		unDisabledAll:function(){
			var contentDiv = this.jq.data("contentDiv");
			if(contentDiv){
				return contentDiv.find("[type=checkbox]").attr("disabled",false);
			}else{
				return 0;
			}
		}
	}
	oc.ns('system.ui');
	system.ui.accodtion = function(options){
		return new MyAccodition(options);
	} 
})(jQuery);