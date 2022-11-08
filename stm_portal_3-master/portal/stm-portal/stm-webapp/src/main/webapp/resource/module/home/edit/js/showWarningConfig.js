$(function(){

	function showWarningConfig(opt) {
		this.opt = {
			panelArea : $(".widget-edit-panel .right"),//默认对象显示区域
			showConfigDialog: undefined,//注册父级调用函数
            widgetLoader:undefined,
			args:undefined
		};
		$.extend(this.opt,opt,true);
		
		this.showConfigDialog = this.opt.showConfigDialog;
		if(!this.showConfigDialog){
			throw '调用失败！';
		}

        this.widgetLoader = this.opt.widgetLoader;
		
		this.moduleCode  = 'showWarning';

		this.init();
	}

	var swcp = showWarningConfig.prototype;
	
	swcp.init = function() {
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/warningConfig.html'),function(){
			_this._init();
		});
	};

	swcp._init = function() {
		var _this = this;
		$('#warningCount').combobox({
			width:160,
			panelHeight:'auto',
			editable:false,
			data:[
			    {value:'5',text:'5',selected:true},
			    {value:'10',text:'10'},
			    {value:'15',text:'15'},
			    {value:'20',text:'20'}
	      ]
		});
		$('#time').combobox({
			width:160,
			panelHeight:'auto',
			editable:false,
			data:[
			    {value:'24h',text:'最近24小时',selected:true},
			    {value:'1w',text:'最近一周'},
			    {value:'1m',text:'最近一个月'},
			    {value:'all',text:'全部'}
	      ]
		});

		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
			if(data.title == ''){
				$("#title").val("告警一览");
			}else{
				$("#title").val(data.title);
			}
	        $("#showName").attr('checked', data.showName);
	        $("#domains").val(data.domains);
	        if(data.domains){
	        	var user =  oc.index.getUser();
				var domains = user.domains;
				var domainsObj = {};
				for(var i=0; i< domains.length; i++){
					domainsObj[domains[i].id] = domains[i];
				}
				var dms = ""; 
				var csDoms = data.domains.split(',');
				for(var i=0; i< csDoms.length; i++){
					var id = csDoms[i] ;
					if(domainsObj[id] != undefined)
						dms += domainsObj[id].name + "，";
				}        
                if(dms.length >0){ 
                    dms = dms.substring(0,dms.length-1);
                }
                if(data.domains == "-1"){
                	dms = "所有域";
                }
	        	$(".view-domain").val(dms);
	        }
	        if(data.warningCount != undefined)
//	        	$("#warningCount").val(data.warningCount);
	        	$('#warningCount').combobox('setValue', data.warningCount);
	        if(data.alarmLevel == ''){
	        	$('[name=alarmLevel]').attr('checked',false);
	        }else{
	        	var allType = ['CRITICAL','SERIOUS','WARN'];
	        	if(data.alarmLevel == null){
	        		data.alarmLevel = "";
	        	}
	        	var type = data.alarmLevel.split(",");
	        	for(var i = 0; i < allType.length; ++i){
	        		if(type.indexOf(allType[i]) != -1){
	        			$('[name=alarmLevel][value="' + allType[i] + '"]').attr('checked',true);
	        		}else{
	        			$('[name=alarmLevel][value="' + allType[i] + '"]').attr('checked',false);
	        		}
	        	}
	        }
	        if(data.time != undefined)
	        	$('#time').combobox('setValue', data.time);
//	        	$("#time").val(data.time);
//	        _this.refreshView();
	    }else{
	    	//初始化选中所有域
	    	var domains = oc.index.getUser().domains;
        	if(domains && domains.length > 0){
        		//所有域
//        		var dms = "";
//        		var vals = "";
//        		for(var domainsIndex = 0; domainsIndex < domains.length;++domainsIndex){
//        			var obj = domains[domainsIndex];
//        			dms += obj.name + "，";
//                    vals += obj.id + ",";
//        		}
//    		 	if(dms.length >0){ 
//               	 	dms = dms.substring(0,dms.length-1);
//                }
    		 	$(".view-domain").val("所有域");
                $("#domains").val("-1");
        	}
	    }

		_this.regEvent();

		_this.showConfigDialog.regConfirmFunction(function(){
			var cfg = {};
			if(_this.opt.args && _this.opt.args.cfg){
	        	cfg =$.extend({},_this.opt.args.cfg,true);
	        }
	        cfg.props = _this._getData();
			_this.showConfigDialog.save(_this.moduleCode,cfg);
        });
	}

	swcp.regEvent = function(){
		var _this = this;

		//域选择
        $("#choiseDomain").unbind().bind("click",function(){
            var user =  oc.index.getUser();
            var $obj = $("<div/>");
            var domains = user.domains;
           	var divid = oc.util.generateId();
           	var $that = $("<div/>")
           	$that.attr('id',divid);
            $that.dialog({
            	href: oc.resource.getUrl('resource/module/home/edit/domainSelection.html'),
                title:'选择域',
                width:200,
                height:300,
                onLoad: function(){
                	//判断当前是否存在所选域，不存在则选中
		          	var checkDomain =  $("#domains").val();
		          	
		          	if(checkDomain.indexOf('-1') > -1){
		          		$("#allHint").next().remove();
		          	}else{
		          		$("#assignDomain").prop("checked",true);
		          		$("#allHint").css("display","none");
		          		App($obj);
		          	};
                	
                	
                	
                	//绑定单选时间
                	$("#allDomain").unbind().bind("click",function(){
                		$("#allHint").css("display","");
                		$("#allHint").next().remove();
                	});
                	$("#assignDomain").unbind().bind("click",function(){
                		$("#allHint").css("display","none");
                		App($obj);
                	});
                },
                buttons:[{
                    text:'确定',
                    handler:function(){
                    	var checkRadio = $("input[name='domain']:checked").val();
                    	if(checkRadio == -1){
                    		$(".view-domain").val("所有域");
                    		$("#domains").val("-1"); 
                    	}else{
	                        var dats = $obj.datagrid('getChecked');
	                        var dms = "";
	                        var vals = ""; 
	                        $.each(dats,function(k,obj){
	                            dms += obj.name + "，";
	                            vals += obj.id + ",";
	                        });
	                        if(dms.length >0){ 
	                            dms = dms.substring(0,dms.length-1);
	                        }
	                        $(".view-domain").val(dms);
	                        $("#domains").val(vals); 
	                        //_this.refreshView();
                    	}
                    	$that.dialog('close');
                    }
                }]
            });
            function App($obj){
	            $that.append($obj);
	            $obj.datagrid({
	                singleSelect:false,
	                fitColumns : true,
	                pagination:false,
	                width:200,
	                data:domains,
	                columns:[[{field:'ck',checkbox:true},
	                    {
	                        field:'id',
	                        title:'id', 
	                        hidden:true,
	                        width:80
	                    },
	                    {   
	                        field:'name',
	                        title:'域名称', 
	                        align:'center',
	                        formatter: function(value,row,index){
	                            return '<label title="' + value + '" >' + value + '</label>';
	                        },
	                        width:80
	                    }
	                ]],
	                onLoadSuccess:function(row){
	                	var data = _this._getData();
	                	var domains = data.domains;
	                	domains = domains.split(',');
	                	var p = {};
	                	for(var i=0;i<domains.length;i++){
	                		p[domains[i]] = domains[i];
	                	}
	                	var rowData = row.rows;
	                	$.each(rowData,function(idx,val){
	                		if(p[val.id])
	                        	$obj.datagrid("selectRow", idx);
	               		});
	                	 $obj.prev().attr("style","width:196;height:190px;overflow:auto;");
	                }
	            });
            }
        });
        //刷新展示效果
//        $("#warningCount,#time,[name=alarmLevel]").unbind(".refreshView").bind("change.refreshView",function(){
//
//        	_this.refreshView();
//        });
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

	swcp._getData = function(){
		var data = {};
		if(data.title = ''){
			data.title = "告警一览";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
        data.domains = $("#domains").val();
        data.warningCount = $('#warningCount').combobox('getValue');
        var alarmLevel = $("input[name='alarmLevel']:checked");
        var type = [];
        for(var i = 0; i < alarmLevel.length; ++i){
        	type.push(alarmLevel[i].value);
        }
        data.alarmLevel = type.join(",");;
        data.time = $('#time').combobox('getValue');
        
        return data;
	}

	swcp.refreshView = function(){
		var _this = this;   
		var ps = _this._getData();
	    ps.selector = $(".overview");
	    _this.widgetLoader.loadWedget('showWarning',ps);
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.showWarningConfig = function(opt){
    	return new showWarningConfig(opt);
    }

})