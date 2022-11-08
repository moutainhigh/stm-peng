$(function(){

	function topoViewConfig(opt) {
		this.opt = {
			panelArea : $(".widget-edit-panel .right"),//默认对象显示区域
			showConfigDialog: undefined,//注册父级调用函数
			args:undefined
		};
		$.extend(this.opt,opt,true);
		
		this.showConfigDialog = this.opt.showConfigDialog;
		if(!this.showConfigDialog){
			throw '调用失败！';
		}

		this.widgetLoader = this.opt.widgetLoader;
		this.moduleCode  = 'topoView';

		this.init();
	}
	var tvcp = topoViewConfig.prototype;

	tvcp.init = function(){
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/topoConfig.html'),function(){
			oc.resource.loadScript(['resource/module/home/edit/js/topoViewConfig.js'], function(){
			 	_this._init();
			});
		});
	}

	tvcp._init = function(){
		var _this = this;
		$('#topoType').combobox({
			width:160,
			panelHeight:'auto',
			editable:false,
			data:[
			    {value:'0',text:'二层拓扑',selected:true},
			    {value:'1',text:'三层拓扑'},
			    {value:'2',text:'机房拓扑'},
			    {value:'map',text:'地图拓扑'}
	      ]
		});
		$('#topoType').combobox({
			onSelect:function(obj){
				var id = obj.value;
				_this.getSubTopo(id);
			}
		});
		var data = {};
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
		}
	
		if(data.title == '' || data.title == null){
			$("#title").val("拓扑视图");
		}else{
			$("#title").val(data.title);
		}
		$("#showName").attr('checked',data.showName);
		if(data.topoType){
			$('#topoType').combobox('setValue', data.topoType);
			_this.getSubTopo(data.topoType,data.topoId);
		}else{
			$('#topoType').combobox('setValue', '0');
			_this.getSubTopo(0);
		}

		_this.showConfigDialog.regConfirmFunction(function(){
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        cfg.props = _this._getData();
			_this.showConfigDialog.save(_this.moduleCode,cfg);
        });

        _this.regEvent();

	}

	tvcp.regEvent = function(){
		var _this = this;
		
		//截取标题字段
        $("#title").bind('change',function(){
        	var maxlength = parseInt($(this).attr('maxlength'));
        	var inData = $(this).val();
        	var inlength = $(this).val().length;
        	if(inlength > maxlength){
        		inData = inData.substring(0,maxlength - 1);
        		$(this).val(inData);
        	}
        });
	}

	tvcp.getSubTopo = function(id,selected){
		var _this = this;
		if(!isNaN(id))
				_this._getSubTopo(id,selected);
		else
			_this._getMap(id,selected);
		//alert(id + '还未实现！');
	}


	tvcp._getSubTopo = function(id,selected){
		var _this = this;
		var url = '/topo/'+ id + '/subTopos.htm';
//		var text = $("#topoType").find('option:selected').text();
		if(id == 0){
			text = '二层拓扑';
		}else if( id == 1){
			text = '三层拓扑';
		}else if( id == 2){
			text = '机房拓扑';
		}else{
			text = '地图拓扑';
		}

		selected = selected?selected:id;
		
		var rt =[{
			text:text,
			id:id,
			children:[]
		}];

		$.getJSON(url,function(data){
            /*BUG #49174 浅色皮肤：首页拓扑视图配置为显示二层拓扑，重新编辑图表时拓扑类型显示为ID huangping 2017/12/11 start*/
            /*$('#topoType').combobox('setValue', selected);*/
            $('#topoType').combobox('setValue', id);
            /*BUG #49174 浅色皮肤：首页拓扑视图配置为显示二层拓扑，重新编辑图表时拓扑类型显示为ID huangping 2017/12/11 end*/
			var $t = $("#selectTopo");
			var p = $t.parent();
			if($t.hasClass('combo-f')){
				var c = $t.combotree('destroy');
				p.append(c);
			}

			rt[0].children = data;
			$("#selectTopo").combotree({
				width:160,
				onChange:function(nv,ov){
					_this.refreshView();
				},
			});
			$t.combotree('loadData',rt);
			$t.combotree('setValue',selected);
//			_this.refreshView();
			if(_this.opt.args && !_this.opt.args.cfg){
				_this.refreshView();
			}
		});
	}

	tvcp._getMap = function(id,selected){
		var _this = this;
		var $t = $("#selectTopo");
		var p = $t.parent();
		if($t.hasClass('combo-f')){
			var c = $t.combotree('destroy');
			p.append(c);
		}

		$t.combotree({
			url:oc.resource.getUrl("resource/module/map/json/chinaTree.json"),
			method:"get",
			onChange:function(nv,ov){
					_this.refreshView();
			},
			onLoadSuccess:function(node,data){
				if(!selected)
					selected=0;
				$t.combotree('setValue',selected);
				_this.refreshView();
			}
		});
		
	}

	tvcp.refreshView = function(){
		var _this = this;   
		var ps = _this._getData();
	    ps.selector = $(".overview");
	    _this.widgetLoader.loadWedget(_this.moduleCode,ps);

	}

	tvcp._getData = function(){
		var data = {};
		if($("#title").val() == ''){
			data.title = "拓扑视图";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
    	data.topoType =$('#topoType').combobox('getValue');
    	data.type = isNaN(data.topoType)?data.topoType:'topo';
        data.topoId = $("#selectTopo").combotree('getValue');
        if(data.type == 'map'){
        	var ctree = $("#selectTopo").combotree('tree');
        	var d = ctree.tree('getSelected');
        	var cp = ctree.tree('getParent',d.target);
        	if(d){
        		data.id = d.id;
        		data.level = d.level;
        		data.cityName = d.text;
        	}
        }

        return data;
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.topoViewConfig = function(opt){
    	return new topoViewConfig(opt);
    }

})