$(function(){

	function statisticViewConfig(opt) {
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

        this.moduleCode = 'statisticView';

		this.systemResoures = {};//供选择的资源类型

		//console.log(this.opt);

		this.init();
	}

	var svcp = statisticViewConfig.prototype;

	svcp.init = function(){
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/statisticsConfig.html'),function(){
			//加载所有资源类型
			$.post(oc.resource.getUrl("portal/resource/discoverResource/getResPrototype.htm"),{
                dataType:'json'
            },function(data){
            	var systemResoure = {};
                systemResoure.resources = [];
                systemResoure.resourcesObj={};
                $.each(data.data,function(k,v){
                    if(v.pid=='Resource'){
                        systemResoure.resources.push(v);
                        systemResoure.resourcesObj[v.id] = v;
                }});
                _this.systemResoures = systemResoure;
                _this._init();
            });
		});
	}

	svcp._init = function(){
		var _this = this;
		$('#time').combobox({
			width:180,
			panelHeight:'auto',
			editable:false,
			data:[
			    {value:'24h',text:'最近24小时',selected:true},
			    {value:'1w',text:'最近一周'},
			    {value:'1m',text:'最近一个月'},
	      ]
		});
		
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
			$("#resources").val( data.resources);
			if(data.resources){
				var drs = data.resources.split(',');
				var dms = ""; 
				for(var i=0; i< drs.length; i++){
					var id =drs[i];
					var re = _this.systemResoures.resourcesObj[id] ;
					if(re != undefined)
						dms += re.name + "，";
				}        
                if(dms.length >0){ 
                    dms = dms.substring(0,dms.length-1);
                }
	        	$(".view-resources").val(dms);
                _this.selectedResource = data.selectedResource;
			}
	        $('[name=type][value="' + data.type + '"]').attr('checked',true);
            if(data.type == 'warming'){
                $(".timearea").show();
            }else{
                $(".timearea").hide();
            }
            
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
	        if(data.title == ''){
	        	$("#title").val("统计视图");
	        }else{
	        	$("#title").val(data.title);
	        }
	        $("#showName").attr('checked', data.showName);
	        $('#time').combobox('setValue', data.time);
	    }else{
	    	//初始化选中所有域和统计对象
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
//           	 	 	vals = vals.substring(0,vals.length-1);
//                }
    		 	$(".view-domain").val("所有域");
                $("#domains").val("-1");
        	}
        	//所有资源
        	var resourcesArr = this.systemResoures.resources;
        	if(resourcesArr && resourcesArr.length > 0){
        		_this.selectedResource = {}
                var dms = "";
                var vals = "";

                $.each(resourcesArr,function(k,obj){
                    dms += obj.name + "，";
                    vals += obj.id + ",";
                    _this.selectedResource[obj.id] = $.extend({},obj,true);
                });

                if(dms.length >0){ 
                    dms = dms.substring(0,dms.length-1);
                    vals = vals.substring(0,vals.length-1);
                }
                $(".view-resources").val(dms);
                $("#resources").val(vals);
        	}
	    }
		_this.regEvent();
	}

	svcp.regEvent = function(){
		var _this = this;

		//域选择
        $("#choiseDomain").unbind().bind("click",function(){
            var user =  oc.index.getUser();
            var $obj =  $("<div/>");

            var domains = user.domains;
           	var divid = oc.util.generateId();
           	var $that = $("<div/>");
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
                    	//判断单选项
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
                    			vals = vals.substring(0,vals.length-1);
                    		}
                    		$(".view-domain").val(dms);
                    		$("#domains").val(vals); 
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
            		height:200,
            		data:user.domains,
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
            		        	  //$obj.parent().find("div.datagrid-header-check").children("input[type=\"checkbox\"]").eq(0).attr("style", "display:none;");
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

        //资源选择
        $("#choiseObjs").unbind().bind("click",function(){
            /*$.post(oc.resource.getUrl("portal/resource/discoverResource/getResPrototype.htm"),{
                dataType:'json'
            },function(data){
                //console.log(data);
                var resources = [];
                $.each(data.data,function(k,v){
                    if(v.pid=='Resource'){
                       // console.log(v);
                        resources.push(v);
                }});//*/
                var resources = _this.systemResoures.resources;
                var $obj =$("<div/>");
                
                var divid = oc.util.generateId();
	           	var $that = $("<div/>")
	           	$that.attr('id',divid);
                $that.dialog({
                    title:'选择资源类型',
                    width:200,
                    height:300,
                    buttons:[{
                        text:'确定',
                        handler:function(){
                            var dats = $obj.datagrid('getChecked');
                            
                            _this.selectedResource = {}
                            //console.log(dats);

                            var dms = "";
                            var vals = "";

                            $.each(dats,function(k,obj){
                                dms += obj.name + "，";
                                vals += obj.id + ",";
                                _this.selectedResource[obj.id] = $.extend({},obj,true);
                            });

                            if(dms.length >0){ 
                                dms = dms.substring(0,dms.length-1);
                                vals = vals.substring(0,vals.length-1);
                            }
                            $(".view-resources").val(dms);
                            $("#resources").val(vals);
                            $that.dialog('close');
                        }
                    }]
                });
                $that.append($obj);
                $obj.datagrid({
                    singleSelect:false,
                    fitColumns : true,
                    pagination:false,
                    width:200,
                    data:resources,
                    columns:[[{field:'ck',checkbox:true},
                        {
                            field:'id',
                            title:'id', 
                            hidden:true,
                            width:80
                        },
                        {   
                            field:'name',
                            title:'资源名称',
                            align:'center', 
                            width:80
                        }
                    ]],
	                onLoadSuccess:function(row){
	                	var data = _this._getData();
	                	var resources = data.resources;
	                	resources = resources.split(',');
	                	var p = {};
	                	for(var i=0;i<resources.length;i++){
	                		p[resources[i]] = resources[i];
	                	}
	                	var rowData = row.rows;
	                	$.each(rowData,function(idx,val){
	                		if(p[val.id])
	                        	$obj.datagrid("selectRow", idx);
	               		});
	                }
                });
            //});
        });

        $(".st-list-d").unbind().bind("click",function(){
            $obj = $(this).find('input[type=radio]');
            $obj.prop('checked',true);
            
            var type = $obj.val();
            if(type == 'warming'){
                $(".timearea").show();
            }else{
                $(".timearea").hide();
            }
        })
        $("[name=type]").unbind().bind("click",function(){
            var type = $(this).val();
            if(type == 'warming'){
                $(".timearea").show();
            }else{
                $(".timearea").hide();
            }
        });
        
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

        _this.showConfigDialog.regConfirmFunction(function(){
            var cfg = {};
            if(_this.opt.args && _this.opt.args.cfg){
                cfg =$.extend({},_this.opt.args.cfg,true);
            }
            cfg.props = _this._getData();
            //判断域或统计对象，不能为空
            if(cfg.props.domains == ""){
            	alert("请选择域")
            	return false;
            }else if(cfg.props.resources == ""){
            	alert("请选择统计对象")
            	return false;
            }else{
            	_this.showConfigDialog.save(_this.moduleCode,cfg);
            	return true;
            }
        });
	}

    svcp._getData = function(){
        var data = {};
        data.resources = $("#resources").val();
        data.selectedResource =this.selectedResource;
        data.type = $("[name=type]:checked").val();
        data.domains = $("#domains").val();
        if($("#title").val() == ''){
        	data.title = "统计视图";
        }else{
        	data.title = $("#title").val();
        }
        data.showName = $("#showName").is(":checked");
        data.time = $('#time').combobox('getValue');;
        return data;
    }

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.statisticViewConfig = function(opt){
    	return new statisticViewConfig(opt);
    }

})