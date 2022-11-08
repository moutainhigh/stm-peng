(function(){

	/**
	*负责创建和初始模块
	*/
	function widgetLoader(args){
		this.widgets = {};

		this.args = $.extend({
			id: null,//div块区id，必须有
			name:'',//模块唯一标识名称
			opt:{},//相应js加载模块,
			theme:Highcharts.theme.currentSkin//配置参数默认的方案为蓝色
		},args);
		
		this.theme = this.args.theme;
		if(!this.theme)
			theme ='blue';

		this._widgets = {};//存放加载后的函数
		this.properties = undefined;//模块配置项目
		this.init();
	}

	var wlp = widgetLoader.prototype;
	wlp.init = function(){

	}

	/**
	* @widgeId 模块标识，直接就是对应的js文件名称
	* @args 模块参数
	*/
	wlp.loadWedget = function(widgeId,args){
		$("#hintHome").remove();
		var obj={};
		obj[widgeId]=[args];
		this.loadWedgets(obj);
	}

	/**
	* 加载多个widget
	* widgets = {widgeId:[{args},{args}],...}
	*/
	wlp.loadWedgets = function(widgets){
		var _this = this;
		if(!widgets){
			console.log('load wedgets 参数错误');
			return;
		}
		
		var scripts = [
			'resource/module/home/widgets/widget.' + _this.theme +'.properties.js',
		];
		$.each(widgets,function(widgeId,aargs){
			scripts.push('resource/module/home/widgets/js/' + widgeId +'.js');
		});

		oc.resource.loadScripts(scripts,function(){
			//console.log(oc.index.home.widgetproperties);
			if(!_this.properties)
				_this.properties = oc.index.home.widgetproperties;

			$.each(widgets,function(widgeId,aargs){
				try{	//var iner = setTimeout(function(){//用于等待js加载完成},100);
					fuc = _this._widgets[widgeId];
					if(!fuc){
						fuc = eval('(' + widgeId + ')');
						 _this._widgets[widgeId] = fuc;
					}
					
					if(fuc) {
						for(var i=0; i < aargs.length; i++){
							var param = $.extend({},aargs[i]);
							param.properties = _this.properties;
							_this.widgets[param.index]= new fuc(param.selector,param);

							//模块标题
							var title = param.title;
							if(!param.showName){
								title = '';
							}
							param.selector.parent().find('.gridster-title').html(title);
						}
					}
				}catch(e){
					console.log('创建widge失败！')
					console.log(e);
				}
			});
		});
		
	}

	/*
	*刷新所有模块，如果index不为空，则刷新指定index的模块
	*/
	wlp.refresh = function(index,type){
	
		var fucObj = this.widgets[index];
		if(fucObj){
			if(fucObj.refresh)
				fucObj.refresh(type);
			else
				throw '模块未实现refresh()函数！';
			return;
		}

		for(idx in this.widgets){
			var fucObj = this.widgets[idx];
			if(fucObj.refresh)
				fucObj.refresh();
			else
				throw '模块未实现refresh()函数！';
		}
	}

	wlp.deleteWdeget = function(index){
		delete this.widgets[index];
	}

	/*
	* 获取模块采用的肤色方案
	*/
	wlp.getTheme = function(){
		return this.theme;
	}

	oc.ns('oc.index.home');
	oc.index.home.widgetLoader = function(args){
		return new widgetLoader(args);
	};

})();