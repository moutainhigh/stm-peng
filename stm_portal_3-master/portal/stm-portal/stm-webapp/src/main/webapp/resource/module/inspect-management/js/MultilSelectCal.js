(function($) {
	var inputField;
	function MultilSelectCal(cfg) {
		inputField = $('<input name="inspectTypeDate" type="hidden" >');
		cfg.inputField = inputField;
		this._cfg = $.extend({}, this._defaults, cfg);
		this.openCal();
		if(cfg.dates) {//如果有值，就赋值
			this.setValues(cfg.dates);
		}
		inputField.val(this.getValues());
	}
	
	MultilSelectCal.prototype = {
		
		_table: undefined,
		_selectedClass: 'cal-multi-date-selected',
		_cfg: null,
		
		_initCalTable: function() {
			var $this = this;
			
			var div = $('<div class="gen-multi-date" style=""></div>')
			var table = $('<table class="multil-date-select-table" style="border-collapse:collapse;border-spacing:0;border: 1px solid #bbbbbb;" >');
			var buildTd = function(i) {
				var row = $('<tr style="height: 20px !important; "></tr>');//background: url(third/jquery-easyui-1.4/themes/default/images/date-numbg.png) repeat-x;
				for(var c=i; c<i+7; c++) {
					var col = $('<td class="multil-date-cell" style="border:1px solid #1C8BC4;cursor: pointer;"></td>');//border: 1px solid #bbbbbb; color: #333333;
					if(c <= 31) {
						col.attr('dateval', c);
						col.click(function(e) {
							$this._click(this);
							e.stopPropagation();
						});
						col.append(c+'号');
					} else if(c == 32) {
						col.css('text-align', 'center');
						col.attr('dateval', 'L');
						col.attr('colspan', 2);
						col.click(function(e) {
							$this._click(this);
						});
						col.append('最后一天');
					} else if(c >= 35) {
						continue;
					}
/*					else if(c == 34) {
						col.click(function(e) {
							$this._selectAll(this);
							e.stopPropagation();
						});
						col.append('全选')
					} else if(c == 35) {
						col.click(function(e) {
							$this._unSelectAll(this);
							e.stopPropagation();
						});
						col.append('取消');
					}*/
					row.append(col);
				}
				return row;
			}
			for(var i=0; i<5; i++) {
				var index = i*7+1;
				var row = buildTd(index);
				table.append(row);
			}
			div.append(table);
			$this._table = div;
			return table;
		},
		_defaults: {
			contailer: undefined,
			dates: undefined,
			inputField: undefined
		},
		_selectAll: function(td) {
			var $this = this;
			td = $(td);
			var tds = $this._table.find('td');
			tds.each(function(i, t) {
				if(!$(this).hasClass($this._selectedClass) && ($(this).attr('dateval')*1 < 32)) {
					$(this).addClass($this._selectedClass);
				}
			});
			$($this._cfg.inputField).val($this.getValues());
		},
		_unSelectAll: function(td) {
			var $this = this;
			td = $(td);
			var tds = $this._table.find('td');
			tds.each(function(i, t) {
				if($(this).hasClass($this._selectedClass) && ($(this).attr('dateval')*1 < 32)) {
					$(this).removeClass($this._selectedClass);
				}
			});
			$this._cfg.inputField.val($this.getValues());
		},
		_click: function(td) {
			var $this = this;
			td = $(td);
			if(td.hasClass($this._selectedClass)) {
				td.removeClass($this._selectedClass);
			} else {
				td.addClass($this._selectedClass);
			}
			$this._cfg.inputField.val($this.getValues());
		},
		getValues: function() {
			var $this = this, dates = [];
			var tds = $this._table.find('.' + $this._selectedClass);
			tds.each(function(i, d) {
				var date = $(this).attr('dateval');
				dates.push(date);
			});
			return dates;
		},
		setValues: function(dates) {
			//清除所有选中项
			var $this = this;
			var all = $this._table.find('td');
			for(var ii=0,lenii=all.length; ii<lenii; ii++) {
				var tar = all[ii];
				$(tar).removeClass($this._selectedClass);
			}
			//设置选中项
			if(dates.constructor == Array) {
				for(var i=0,len=dates.length; i<len; i++) {
					if(dates[i] > 31) break;
					var date = dates[i];
					var srcDates = $this._table.find('td');
					for(var j=0,len2=srcDates.length; j<len2; j++) {
						var srcDate = srcDates[j];
						if(($(srcDate).attr('dateval') == date) || $(srcDate).attr('dateval')*1 == date*1) {
							$(srcDate).addClass($this._selectedClass);
						}
					}
				}
			} else if(dates.constructor == String) {
				if(dates.length > 0) {
					var dateArray = dates.split(',');
					for(var i=0,len=dateArray.length; i<len; i++) {
						var d = dateArray[i];
						var srcDates = $this._table.find('td');
						for(var j=0,len2=srcDates.length; j<len2; j++) {
							var srcDate = srcDates[j];
							if(($(srcDate).attr('dateval') == date) || $(srcDate).attr('dateval')*1 == d*1) {
								$(srcDate).addClass($this._selectedClass);
							}
						}
					}
				}
			}
			inputField.val(this.getValues());
		},
		openCal: function() {
			var $this = this;
			$this._initCalTable();
			$($this._cfg.container).empty().append($this._cfg.inputField).append($this._table);
/*			var readonly = $($this._cfg.inputField).attr('readonly');
			if(readonly == undefined) {
				$($this._cfg.inputField).attr('readonly', 'readonly');
			}
			$($this._cfg.inputField).click(function(e) {
				$this.show();
				$this.setValues($(this).val());
				$($this._cfg.container).append($this._table);
				$($this._cfg.inputField).parent().append($this._table);
				e.stopPropagation();
			});
			var cell = $this._table.find('.multil-date-cell')
			$(document).on('click', function(e) {
				if(e.target != $($this._cfg.inputField) || e.target != cell) {
					$this.hide();
				}
			});
*/
		},
		hide: function() {
			var $this = this;
			$this._table.addClass('hide');
		},
		show: function() {
			var $this = this;
			$this._table.removeClass('hide');
			$this._table.css({
				
			});
		},
		getTexts: function() {
			var $this = this, texts = [];
			var tds = $this._table.find($this._selectedClass);
			tds.each(function(i, d) {
				var text = $(this).html();
				texts.push(text);
			});
			return texts;
		}
		
	}
	
	oc.ui.multidateselect = function(cfg) {
		return new MultilSelectCal(cfg);
	}
	
})(jQuery)