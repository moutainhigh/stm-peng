(function($){

	function _16b(_16c, row) {
		var d= $.data(_16c, "datagrid");
		var view = d.options.view;
		view.insertRow.call(view, _16c, null, row);
		d.insertedRows.push(row);
		$(_16c).datagrid("getPager").pagination("refresh", {
			total : d.data.total
		});
	}
	
	$.fn.datagrid.methods.appendRow= function(jq, row){
		return jq.each(function() {
			_16b(this, row);
			var o=$.data(this, "datagrid").options;
			o.onAppendRow&&o.onAppendRow();
		});
	};
	$.fn.datagrid.defaults.noDataMsg='抱歉，没有可展示的数据！';
	$.fn.datagrid.defaults.finder.getTr= function(_227, _228, type, _229) {
		type = type || "body";
		_229 = _229 || 0;
		var _22a = $.data(_227, "datagrid");
		var dc = _22a.dc;
		var opts = _22a.options;
		if (_229 == 0) {
			var tr1 = opts.finder.getTr(_227, _228,
					type, 1);
			var tr2 = opts.finder.getTr(_227, _228,
					type, 2);
			return tr1.add(tr2);
		} else {
			var table_datanull='>.table-datanull>table>tbody>tr';
			if (type == "body") {
				var tr = $("#" + _22a.rowIdPrefix + "-"
						+ _229 + "-" + _228);
				if (!tr.length) {
					tr = (_229 == 1 ? dc.body1: dc.body2).find(table_datanull+"[datagrid-row-index="+ _228 + "]");
				}
				return tr;
			} else {
				if (type == "footer") {
					return (_229 == 1 ? dc.footer1
							: dc.footer2)
							.find(">table>tbody>tr[datagrid-row-index="
									+ _228 + "]");
				} else {
					if (type == "selected") {
						var selectedRows = (_229 == 1 ? dc.body1
								: dc.body2)
								.find(table_datanull+".datagrid-row-selected");
						if(!selectedRows||selectedRows.length<=0){
							selectedRows = (_229 == 1 ? dc.body1
								: dc.body2)
								.find(">table>tbody>tr.datagrid-row-selected");
						}
						return selectedRows;
					} else {
						if (type == "highlight") {
							return (_229 == 1 ? dc.body1
									: dc.body2)
									.find(table_datanull+".datagrid-row-over");
						} else {
							if (type == "checked") {
								var ttb=(_229 == 1 ? dc.body1: dc.body2),
								ttb1=ttb.find(".datagrid-row-checked");
								if(ttb1.length==0)ttb1=ttb.find(table_datanull+".datagrid-row-checked");
								return ttb1;
							} else {
								if (type == "last") {
									var ttb=(_229 == 1 ? dc.body1: dc.body2),
									ttb1=ttb.find(table_datanull + "[datagrid-row-index]:last");
									if(ttb1.length==0){
										ttb1=ttb.find(">table>tbody>tr[datagrid-row-index]:last");
									}
									return ttb1;
								} else {
									if (type == "allbody") {
										var ttb=(_229 == 1 ? dc.body1: dc.body2),
										ttb1=ttb.find(table_datanull+"[datagrid-row-index]");
										if(ttb1.length==0){
											ttb1=ttb.find(">table>tbody>tr[datagrid-row-index]");
										}
										return ttb1;
									} else {
										if (type == "allfooter") {
											return (_229 == 1 ? dc.footer1
													: dc.footer2)
													.find(table_datanull+"[datagrid-row-index]");
										}
									}
								}
							}
						}
					}
				}
			}
		}
	};
	
	$.fn.datagrid.methods.updateRow= function(jq, _1da) {
		return jq.each(function() {
				var opts = $.data(this, "datagrid").options;
				opts.view.updateRow.call(opts.view, this, _1da.index,
						_1da.row);
				opts.onUpdateRow&&opts.onUpdateRow();
			});
	};
	
	//easyui 默认行为更改
	$.extend($.fn.datagrid.defaults,{
		fitColumns:true,
		loadMsg:'加载数据，请稍候...',
		pagination:true,
		fit:true,
		queryForm:0,//查询表单
		queryConditionPrefix:'condition.',
		onBeforeLoad:function(p){
			p.startRow=p.page?((p.page-1)*p.rows):0;
			p.rowCount=p.rows;
			var opts=$(this).datagrid('options');
			if(opts.queryForm){
				var ps=opts.queryForm.val(),prefix=opts.queryConditionPrefix||'';
				for(var n in ps){
					p[prefix+n]=ps[n];
				}
			}
			return true;
		},
		loadFilter:function(d){
			if(Object.prototype.toString.call(d) === '[object Array]'){
				var data = {};
				data.rows = d;
				data.total=d.length;
				return data;
			}else{
				if(d.code==200){
					d=d.data;	
					d.total=d.totalRecord;
				}else{
					d.total=0;
					d.rows=[];
				}
				return d;
			}
		}
	});
	$.extend($.fn.datagrid.defaults.view,{
		render : function(_1e8, _1e9, _1ea) {
			var _1eb = $.data(_1e8, "datagrid");
			var opts = _1eb.options;
			var rows = _1eb.data.rows,
				rLen=rows?rows.length:0;
			var _1ec = $(_1e8).datagrid("getColumnFields", _1ea),_1ed;
			if (_1ea){
				if (!(opts.rownumbers || (opts.frozenColumns && opts.frozenColumns.length))){
					return;
				}
			}
			if(rLen==0){
//				_1ed='没有数据...';
				if(_1ea){
					_1ed=$("<div class='table-datanull'></div>"),_tbody=_1ed.find('tbody:first');
				}else{
					_1ed=$("<div class='table-datanull'><div class='table-dataRemind'>"+opts.noDataMsg+"</div></div>"),_tbody=_1ed.find('tbody:first');
				}
			}else{
				_1ed=$("<div class='table-datanull'><table class=\"datagrid-btable\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">"+
						"<tbody></tbody></table></div>"),_tbody=_1ed.find('tbody:first');
					for (var i = 0; i < rows.length; i++){
					var css = opts.rowStyler ? opts.rowStyler.call(_1e8, i, rows[i]) : "";
					var _1ee = "";
					var _1ef = "";
					if (typeof css == "string"){
						_1ef = css;
					}else{
						if(css){
							_1ee = css["class"] || "";
							_1ef = css["style"] || "";
						}
					}
					var cls = "class=\"datagrid-row "
							+ (i % 2 && opts.striped ? "datagrid-row-alt " : " ")
							+ _1ee + "\"";
					var _1f0 = _1ef ? "style=\"" + _1ef + "\"" : "";
					var _1f1 = _1eb.rowIdPrefix + "-" + (_1ea ? 1 : 2) + "-" + i;
					_tbody.append($("<tr id=\"" + _1f1 + "\" datagrid-row-index=\"" + i
						+ "\" " + cls + " " + _1f0 + "></tr>").append(this.renderRow.call(this, _1e8, _1ec, _1ea, i,rows[i])));
				}
			}
			_1e9.empty().append(_1ed);
		},
		renderFooter : function(_1f2, _1f3, _1f4) {
			var opts = $.data(_1f2, "datagrid").options;
			var rows = $.data(_1f2, "datagrid").footer || [];
			var _1f5 = $(_1f2).datagrid("getColumnFields", _1f4);
			var _1f6 = $("<table class=\"datagrid-ftable\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">"+
					"<tbody></tbody></table>"),_tbody=_1f6.find('tbody:first'),_tr;
			for (var i = 0; i < rows.length; i++) {
				_tr=$("<tr class=\"datagrid-row\" datagrid-row-index=\""+ i + "\"></tr>")
					.append(this.renderRow.call(this, _1f2, _1f5, _1f4, i,rows[i]));
				_tbody.append(_tr);
			}
			$(_1f3).append(_1f6);
		},
		renderRow : function(_1f7, _1f8, _1f9, _1fa, _1fb) {
			var opts = $.data(_1f7, "datagrid").options;
			var _pp=$('<div></div>'),_self,cc,_div,_temp,_valT;
			if (_1f9 && opts.rownumbers) {
				var _1fc = _1fa + 1;
				if (opts.pagination) {
					_1fc += (opts.pageNumber - 1) * opts.pageSize;
				}
				_pp.append("<td class=\"datagrid-td-rownumber\"><div class=\"datagrid-cell-rownumber\">"
								+ _1fc + "</div></td>");
			}
			for (var i = 0; i < _1f8.length; i++) {
				cc=[];
				var _1fd = _1f8[i];
				var col = $(_1f7).datagrid("getColumnOption", _1fd);
				if (col) {
					var _1fe = _1fb[_1fd];
					var css = col.styler ? (col.styler(_1fe, _1fb, _1fa) || "")
							: "";
					var _1ff = "";
					var _200 = "";
					if (typeof css == "string") {
						_200 = css;
					} else {
						if (css) {
							_1ff = css["class"] || "";
							_200 = css["style"] || "";
						}
					}
					var cls = _1ff ? "class=\"" + _1ff + "\"" : "";
					var _201 = col.hidden ? "style=\"display:none;" + _200
							+ "\"" : (_200 ? "style=\"" + _200 + "\"" : "");
					cc.push("<td field=\"" + _1fd + "\" " + cls + " " + _201
							+ ">");
					var _201 = "";
					if (!col.checkbox) {
						if (col.align) {
							_201 += "text-align:" + col.align + ";";
						}
						if (!opts.nowrap) {
							_201 += "white-space:normal;height:auto;";
						} else {
							if (opts.autoRowHeight) {
								_201 += "height:auto;";
							}
						}
					}
					_self=$(cc.join("")+"<div style=\""+_201+ "\" "+(col.checkbox ? 
							"class=\"datagrid-cell-check\"": "class=\"datagrid-cell " + col.cellClass + "\"")+"></div></td>");
					_div=_self.find('div:first');
					if (col.checkbox) {
						var disabled = col.isDisabled ? col.isDisabled(_1fe, _1fb, _1fa) : false;
						_div.append("<input type=\"checkbox\" "+ 
								(_1fb.checked ? "checked=\"checked\"" : "")+" name=\"" + _1fd + "\" value=\"" + 
								(_1fe != undefined ? _1fe : "") + "\">")
								.find("[type=checkbox]")
								.attr("disabled",disabled);
						disabled&&_div.removeClass("datagrid-cell-check").addClass("oc-datagrid-cell-check");
					} else {
						if (col.formatter) {
							_temp=col.formatter(_1fe, _1fb, _1fa,_div);
							_temp&&_div.append(_temp);
						} else {
							if((typeof _1fe=='number')||_1fe){
								_div.append(_1fe.toString());
								if(col.ellipsis){
									_div.css('text-overflow','ellipsis').attr('title',_1fe.toString());
								}
							}
						}
					}
					_pp.append(_self);
				}
			}
			return _pp.children('td');
		},
		refreshRow : function(_202, _203) {
			this.updateRow.call(this, _202, _203, {});
		},
		updateRow : function(_204, _205, row) {
			var opts = $.data(_204, "datagrid").options;
			var rows = $(_204).datagrid("getRows");
			$.extend(rows[_205], row);
			var css = opts.rowStyler ? opts.rowStyler.call(_204, _205,
					rows[_205]) : "";
			var _206 = "";
			var _207 = "";
			if (typeof css == "string") {
				_207 = css;
			} else {
				if (css) {
					_206 = css["class"] || "";
					_207 = css["style"] || "";
				}
			}
			var _206 = "datagrid-row "
					+ (_205 % 2 && opts.striped ? "datagrid-row-alt " : " ")
					+ _206;
			function _208(_209) {
				var _20a = $(_204).datagrid("getColumnFields", _209);
				var tr = opts.finder.getTr(_204, _205, "body", (_209 ? 1 : 2));
				var _20b = tr.find(
						"div.datagrid-cell-check input[type=checkbox]").is(
						":checked");
				tr.children().remove();
				tr.append(this.renderRow.call(this, _204, _20a, _209, _205,
						rows[_205]));
				tr.attr("style", _207).attr(
						"class",
						tr.hasClass("datagrid-row-selected") ? _206
								+ " datagrid-row-selected" : _206);
				if (_20b) {
					tr.find("div.datagrid-cell-check input[type=checkbox]")
							._propAttr("checked", true);
				}
			}
			;
			_208.call(this, true);
			_208.call(this, false);
			$(_204).datagrid("fixRowHeight", _205);
		},
		insertRow : function(_20c, _20d, row) {
			var _20e = $.data(_20c, "datagrid");
			var opts = _20e.options;
			var dc = _20e.dc;
			var data = _20e.data;
			if (_20d == undefined || _20d == null) {
				_20d = data.rows.length;
			}
			if (_20d > data.rows.length) {
				_20d = data.rows.length;
			}
			function _20f(_210) {
				var _211 = _210 ? 1 : 2;
				for (var i = data.rows.length - 1; i >= _20d; i--) {
					var tr = opts.finder.getTr(_20c, i, "body", _211);
					tr.attr("datagrid-row-index", i + 1);
					tr
							.attr("id", _20e.rowIdPrefix + "-" + _211 + "-"
									+ (i + 1));
					if (_210 && opts.rownumbers) {
						var _212 = i + 2;
						if (opts.pagination) {
							_212 += (opts.pageNumber - 1) * opts.pageSize;
						}
						tr.find("div.datagrid-cell-rownumber").html(_212);
					}
					if (opts.striped) {
						tr.removeClass("datagrid-row-alt").addClass(
								(i + 1) % 2 ? "datagrid-row-alt" : "");
					}
				}
			}
			;
			function _213(_214) {
				var _215 = _214 ? 1 : 2;
				var _216 = $(_20c).datagrid("getColumnFields", _214);
				var _217 = _20e.rowIdPrefix + "-" + _215 + "-" + _20d;
				var tr = "<tr id=\"" + _217
						+ "\" class=\"datagrid-row\" datagrid-row-index=\""
						+ _20d + "\"></tr>";
				if (_20d >= data.rows.length) {
					if (data.rows.length) {
						opts.finder.getTr(_20c, "", "last", _215).after(tr);
					} else {
						var cc = _214 ? dc.body1 : dc.body2;
						cc.html("<div class='table-datanull'><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>" + tr + "</tbody></table></div>");
					}
				} else {
					opts.finder.getTr(_20c, _20d + 1, "body", _215).before(tr);
				}
			}
			;
			_20f.call(this, true);
			_20f.call(this, false);
			_213.call(this, true);
			_213.call(this, false);
			data.total += 1;
			data.rows.splice(_20d, 0, row);
			this.refreshRow.call(this, _20c, _20d);
		},
		deleteRow : function(_218, _219) {
			var _21a = $.data(_218, "datagrid");
			var opts = _21a.options;
			var data = _21a.data;
			function _21b(_21c) {
				var _21d = _21c ? 1 : 2;
				for (var i = _219 + 1; i < data.rows.length; i++) {
					var tr = opts.finder.getTr(_218, i, "body", _21d);
					tr.attr("datagrid-row-index", i - 1);
					tr
							.attr("id", _21a.rowIdPrefix + "-" + _21d + "-"
									+ (i - 1));
					if (_21c && opts.rownumbers) {
						var _21e = i;
						if (opts.pagination) {
							_21e += (opts.pageNumber - 1) * opts.pageSize;
						}
						tr.find("div.datagrid-cell-rownumber").html(_21e);
					}
					if (opts.striped) {
						tr.removeClass("datagrid-row-alt").addClass(
								(i - 1) % 2 ? "datagrid-row-alt" : "");
					}
				}
			}
			;
			opts.finder.getTr(_218, _219).remove();
			_21b.call(this, true);
			_21b.call(this, false);
			data.total -= 1;
			data.rows.splice(_219, 1);
			if(!data.rows||data.rows.length<=0){
				$(_21a.dc.view2).find('div[class="table-datanull"]').append("<div class='table-dataRemind'>"+opts.noDataMsg+"</div>");
			}
		},
		onBeforeRender : function(_21f, rows) {
		},
		onAfterRender : function(_220) {
			var datagrid=$(_220),opts =datagrid.datagrid('options'),selectedCfg=opts.selectedCfg;
			if(selectedCfg){
				var sField=selectedCfg.field,sFieldValue=selectedCfg.fieldValue,
				rows=datagrid.datagrid('getRows');
				for(var i=0,len=rows.length;i<len;i++){
					if(rows[i][sField]==sFieldValue)datagrid.datagrid('selectRow',i);
				}
			}
			if (opts.showFooter) {
				$(_220).datagrid("getPanel").find("div.datagrid-footer")
				.find("div.datagrid-cell-rownumber,div.datagrid-cell-check").css("visibility", "hidden");
			}
			datagrid.datagrid('fitColumns');
			var header = datagrid.datagrid('getPanel').find(".datagrid-header"); 
			header.find(":input[type=checkbox]").click(function(){
				if(this.checked){
					header.next().find(".oc-datagrid-cell-check").parent().parent()
					.removeClass("datagrid-row-selected").removeClass("datagrid-row-checked")
				}
			});
			
			$.messager.progress('close');
		}
	},{
	});
	
	//动态隐藏展示列
	function initDynamicGridColumns(dataGrid,selectButton){
		//初始化筛选信息列表按钮
		var fields=dataGrid.datagrid('options').columns[0];
		var menuDiv = $('<div style="height:250px !important;"></div>');
		for(var i=0;i<fields.length;i++){
			if(fields[i].isDynamicHidden||fields[i].isDynamicHidden==undefined){
				menuDiv.append('<div><span class="text-over" title="'+ fields[i].title +'"><input class="dynamicHiddenGridColumns" type="checkbox" fieldId="' + fields[i].field + '" />' + fields[i].title + '</span></div>');
			}
		}
		menuDiv = menuDiv.menu({
			onShow:function(){
				menuDiv.css('overflow','auto');
			}
		});
		selectButton.menubutton({
		    menu: menuDiv
		});
		for(var j=0;j<fields.length;j++){
			if((fields[j].isDynamicHidden||fields[j].isDynamicHidden==undefined)&&!fields[j].hidden){
				menuDiv.find('.dynamicHiddenGridColumns[fieldId=' + fields[j].field + ']').attr('checked','checked');
			}
		}
		menuDiv.find('.dynamicHiddenGridColumns').on('click',function(e){
			e.stopPropagation();
			
			var viewObj = dataGrid.parent().find('div[class=datagrid-view2]');
			var resourceObj = viewObj.find('table[class=datagrid-htable]');
			
			var widthCount = 0;
			var widthSingle;
			widthCount = parseInt(resourceObj.css('width').replace('px',''));
			var targetObj = viewObj.find('table[class=datagrid-btable]');			
			var customObj = viewObj.find('div[class=datagrid-header]');
			widthSingle = parseInt(dataGrid.datagrid('getColumnOption',$(e.target).attr('fieldId')).width);
			if($(e.target).prop('checked')){				
				$(e.target).attr('checked','true');
				dataGrid.datagrid('showColumn',$(e.target).attr('fieldId'));
				targetObj.css('width',(widthCount+widthSingle)+'px');
				customObj.css('width',(widthCount+widthSingle)+'px');
			}else{
				$(e.target).removeAttr("checked");
				dataGrid.datagrid('hideColumn',$(e.target).attr('fieldId'));
				targetObj.css('width',(widthCount-widthSingle)+'px');
				customObj.css('width',(widthCount-widthSingle)+'px');
			}
			
		});
	}
	
	function datagrid(cfg){
		cfg=this.cfg=$.extend({},this._defaults,cfg);
		if((typeof cfg.selector)=='string'){
			cfg.selector=$(cfg.selector);
		}
		this._initOcToolbar(cfg.octoolbar,cfg.queryForm,cfg.delCfg,cfg.hideReset,cfg.hideSearch,cfg.resetIdx,cfg.searchIdx);
		var curCfg = $.extend(true,{},cfg);
		curCfg.onLoadSuccess = function(data){
			
			var viewObj = $(this).parent().find('div[class=datagrid-view2]');
			var resourceObj = viewObj.find('table[class=datagrid-htable]');
			
			var targetObj = viewObj.find('table[class=datagrid-btable]');
			targetObj.css('width',resourceObj.css('width'));
			
			cfg.onLoadSuccess&&cfg.onLoadSuccess(data);
		}
		curCfg.onResize = function(){

			var viewObj = $(this).parent().find('div[class=datagrid-view2]');
			var resourceObj = viewObj.find('table[class=datagrid-htable]');
			
			var targetObj = viewObj.find('table[class=datagrid-btable]');
			targetObj.css('width',resourceObj.css('width'));

		}
		this.selector=cfg.selector.datagrid(curCfg);
		if(!cfg.url && !cfg.data){
			//没有配置本地数据也没有配置远程数据
			var gridPanel=this.selector.datagrid('getPanel');
			gridPanel.find('.datagrid-view1>.datagrid-body>.datagrid-body-inner').append("<div class='table-datanull'></div>");
			gridPanel.find('.datagrid-view2>.datagrid-body').append("<div class='table-datanull'><div class='table-dataRemind'>"+cfg.noDataMsg+"</div></div>");
		}
		if(cfg.dynamicColumnsSelect){
			//配置了动态隐藏展示列
			if((typeof cfg.dynamicColumnsSelect)=='string'){
				cfg.dynamicColumnsSelect=$(cfg.dynamicColumnsSelect);
			}
			initDynamicGridColumns(this.selector,cfg.dynamicColumnsSelect);
		}
	}
	
	datagrid.prototype={
		constructor:datagrid,
		cfg:undefined,
		idField:'id',
		selector:undefined,
		queryForm:undefined,//查询表单
		octoolbar:false,
		_defaults:{
			dynamicColumnsSelect:0,//配置动态设置列的下拉列表jquery对象
			hideReset:false,//是否隐藏重置按钮
			resetIdx:2,
			hideSearch:false,//是否隐藏搜索按钮
			searchIdx:1,
			pageSize:15,
			pageList:[15,20,30,40,50],
			noDataMsg:'抱歉，没有可展示的数据！'
//			selectedCfg:{
//				field:'isChecked',
//				fieldValue:true
//			}
		},
		_delCfgDefaults:{//默认删除数据配置
			text:'删除',
			iconCls:'icon-remove',
			confirmMsg:'确认删除所选择的数据？',
			delMsg:'请选择至少一条要删除的数据！',
			remoteField:'ids',
			successMsg:'删除数据成功！',
			direction:'left',
			idx:'last',
			data:{}
		},
		reLoad:function(ps){
			this.selector.datagrid('reload',ps);
		},
		load:function(ps){
			this.selector.datagrid('load',ps);
		},
		getSelections:function(){
			return this.selector.datagrid('getSelections');
		},
		getSelectIds:function(){
			var objs=this.selector.datagrid('getSelections'),ids=[],idField=this.idField;
			for(var i=0,len=objs.length;i<len;i++){
				ids[i]=objs[i][idField];
			}
			return ids;
		},
		getSelectId:function(){
			var obj=this.selector.datagrid('getSelected');
			return obj&&obj[this.idField];
		},
		updateNoDataMsg:function(msg){
			this.selector.datagrid("getPanel").find('div[class="table-dataRemind"]').text(msg);
		},
		_initOcToolbar:function(oct,qf,delCfg,hideReset,hideSearch,resetIdx,searchIdx){
			var that=this;
			if(oct||delCfg){
				oct=$.extend({left:[],right:[]},oct);
				var left=oct.left,right=oct.right;
				if(qf){
					if(!hideSearch){
						right.splice(searchIdx,0,{text:'查询',iconCls:'icon-search',onClick:function(){that.load();}});
						qf.selector.find('.oc-enter').keypress(function(e,i){
							if(e.keyCode==13)that.load();
						});
					}
					if(!hideReset)right.splice(resetIdx,0,{text:'重置',iconCls:'ico_reset',onClick:function(){qf.reset();}});
				}
				if(delCfg){
					delCfg=$.extend({
						onClick:function(){
							var ids=that.getSelectIds(),len=ids.length;
							if(len){
								oc.ui.confirm(delCfg.confirmMsg,function(){
									delCfg.data[delCfg['remoteField']]=ids.join();
									oc.util.ajax(delCfg);
								});
							}else{
								alert(delCfg.delMsg,'danger');
							}
						},
						success:function(d){
							that.reLoad();
						}
					},that._delCfgDefaults,delCfg);
					if(delCfg.direction=='left'){
						left.splice((delCfg.idx=='last')?left.length:delCfg.idx,0,delCfg);
					}else{
						right.splice((delCfg.idx=='last')?right.length:delCfg.idx,0,delCfg);
					}
				}
				this.octoolbar=oc.ui.toolbar(oct);
				this.cfg.toolbar=this.octoolbar.jq;
			}
		}
	};
	
	oc.ui.datagrid=function(cfg){
		return new datagrid(cfg);
	};
})(jQuery);