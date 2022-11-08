$(function(){
	oc.ns('oc.statist.query.main');
	function statistQueryMain(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this._init();
	}
	statistQueryMain.prototype = {
		constructor : statistQueryMain,
		mainDiv : undefined,
		centerDiv : undefined,
		_defaults : {},
		performances : undefined,
		propertyCounts : undefined,
		_init : function(){
			var id=oc.util.generateId(), that = this;
			this.mainDiv = $('.statistQuery').attr('id',id);
			this.mainMenu = this.mainDiv.layout().find('#statistQueryMainMenu').accordion();
			this.centerDiv = this.mainDiv.layout('panel','center');
			// 让顶部 报表管理标签高亮   性能报告高亮
			this.mainDiv.find('#addGroupButton').parent().parent().addClass('oc-accordion-selected oc-accordion-selected-report');
			
			this.addGroupButtonEvent();
			
			this.initStatQNavList();
			
			this.createEmptyMainCenter();
		},
		addGroupButtonEvent : function(){
			this.mainDiv.find("#addGroupButton").on('click', function(){
				oc.resource.loadScript('resource/module/statist-query/js/statist_query_detail.js',function(){
					oc.statist.query.detail.open({openType : 'add'});
				});
			});
		},
		initStatQNavList : function(){
			var performanceArr = [], propertyCountArr = [], that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/statistQuery/detail/getAllSQMain.htm'),
				success : function(data){
					for (var i = 0; i < data.data.length; i++) {
						var statQMain = data.data[i];
						var dataOption = {
							id : statQMain.id,
							name : statQMain.name,
							type : statQMain.type,
							createUserId : statQMain.createUserId,
							createTime : statQMain.createTime,
							categoryId : statQMain.categoryId,
							subResourceId : statQMain.subResourceId,
							domainId : statQMain.domainId
						};
						var href = oc.resource.getUrl('resource/module/statist-query/statist_query_main_center.html');
						var name = '<span class="oc-text-ellipsis" style="width:85%;" name="titleName-'+dataOption.id+'" title="'+dataOption.name+'" type='+dataOption.type+' >'+dataOption.name+'</span>';
						var option = that.createNavOption(href, name, dataOption, that);
						switch (parseInt(dataOption.type)) {
						case 1:
							performanceArr.push(option);
							break;
						case 2:
							propertyCountArr.push(option);
							break;
						}
					}
					that.performances = oc.ui.navsublist({
						selector : that.mainDiv.find('.performance').empty(),
						data : performanceArr,
						click : that.clickNavsublist
					});
					that.propertyCounts = oc.ui.navsublist({
						selector : that.mainDiv.find('.propertyCount').empty(),
						data : propertyCountArr,
						click : that.clickNavsublist
					});
					// 显示默认数据
					if(performanceArr.length > 0){
						that.performances.get(0).trigger('click');
					}else if(propertyCountArr.length > 0){
						that.mainMenu.accordion('select', 1);
						that.propertyCounts.get(0).trigger('click');
					}
				}
			});
		},
		addStatQNavsublistItem : function(dataOption){
			var that = this;
			var href = oc.resource.getUrl('resource/module/statist-query/statist_query_main_center.html');
			var name = '<span class="oc-text-ellipsis" style="width:85%;" name="titleName-'+dataOption.id+'" title="'+dataOption.name+'" type='+dataOption.type+' >'+dataOption.name+'</span>';
			var option = that.createNavOption(href, name, dataOption, that);
			switch (parseInt(dataOption.type)) {
			case 1:
				that.performances.add(option);
				break;
			case 2:
				that.propertyCounts.add(option);
				break;
			}
		},
		editStatQNavsublistItem : function(dataOption){
			var that = this, navList;
			var href = oc.resource.getUrl('resource/module/statist-query/statist_query_main_center.html');
			var name = '<span class="oc-text-ellipsis" style="width:85%;" name="titleName-'+dataOption.id+'" title="'+dataOption.name+'" type='+dataOption.type+' >'+dataOption.name+'</span>';
			var option = that.createNavOption(href, name, dataOption, that);
			switch (parseInt(dataOption.type)) {
			case 1:
				navList = that.performances;
				break;
			case 2:
				navList = that.propertyCounts;
				break;
			}
			for(var i = 0; navList.data != undefined && i < navList.data.length; i++){
				if(dataOption.id == navList.data[i].dataOption.id){
					navList.data[i].dataOption = dataOption;
					navList.get(i).trigger('click');
					break;
				}
			}
			that.mainDiv.find('[name="titleName-' + dataOption.id+'"]').each(function(){
				$(this).html(dataOption.name);
				$(this).attr('title', dataOption.name);
			});
		},
		createNavOption : function(href, name, dataOption, that){
			var option = {
				href : href,
				name:name,
				dataOption : dataOption,
				that : that
			};
			return option;
		},
		clickNavsublist : function(href, data, e){
			var that = data.that;
			that.centerDiv.children().remove();
			var htmlStr = data.name;
			htmlStr += '<span class="icon-edit locate-right statQEditBtn" title="编辑报表!"></span>';
			htmlStr += '<span class="ico ico-del locate-right statQDelBtn" title="删除!"></span></div>';
			$('<div style="width:100%;height:100%;"></div>').appendTo(that.centerDiv).panel('RenderP',{
				title : htmlStr,
				href : href,
				onLoad:function(){
					oc.resource.loadScript('resource/module/statist-query/js/statist_query_main_center.js', function(){
						oc.statist.query.maincenter.open(data.dataOption);
					});
					that.centerDiv.find(".statQEditBtn").on('click', function(){
						that.addClickEdit(data.dataOption);
					});
					that.centerDiv.find(".statQDelBtn").on('click', function(){
						that.addClickDel(data.dataOption);
					});
				}
			});
		},
		addClickEdit : function(dataOption){
			oc.resource.loadScript('resource/module/statist-query/js/statist_query_detail.js',function(){
				oc.statist.query.detail.open({openType : 'edit', dataOption : dataOption});
			});
		},
		addClickDel : function(dataOption){
			var that = this;
			oc.ui.confirm('是否要删除？', function(){
				oc.util.ajax({
					url : oc.resource.getUrl('portal/statistQuery/detail/delSQMainByStatQId.htm'),
					data : {statQMainId : dataOption.id},
					success : function(data){
						if(data.code == 200){
							alert('删除成功!');
							that.initStatQNavList();
							that.createEmptyMainCenter();
						}else{
							alert('删除失败!');
						}
					}
				});
			}, function(){});
		},
		createEmptyMainCenter : function(){
			var that = this;
			that.centerDiv.children().remove();
			$('<div style="width:100%;height:100%;"></div>').appendTo(that.centerDiv).panel('RenderP',{
				title : '统计报表'
			});
		}
	}
	var statQMain = new statistQueryMain();
	oc.statist.query.main = {
		addStatQNavsublistItem : function(dataOption){
			statQMain.addStatQNavsublistItem(dataOption);
		},
		editStatQNavsublistItem : function(dataOption){
			statQMain.editStatQNavsublistItem(dataOption);
		}
	}
});