(function($){
	/** 数据源 */
	var dataSource = [];
	/** 上层页面传过来的数据 */
	var data;
	/** 让分页按钮disabled的classs */
	var disabledClass = "l-btn-disabled l-btn-plain-disabled";
	function deviceTypeDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	deviceTypeDetail.prototype={
		constructor:deviceTypeDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,
			buttons=[{
				text:'关闭',
				iconCls:'ico ico-cancel',
				handler:function(){
					dlg.dialog('close');
				}
			}];
			buttons.unshift({
				text:'确定',
				iconCls:'ico ico-ok',
				handler:function(){
					if($("#dTbody .active").length>0){
					
						var model = $("#dTbody .active").find("td[model]").attr("model");
						oc.util.ajax({
							url: oc.resource.getUrl('portal/3d/model/update.htm'),
							data:{type:cfg.self.type,model:model},
							successMsg:null,
							success: function(data){
								if(data.code&&data.code==200){
									dlg.dialog('close');
									$(".thirdBuildSettings").find("li.active").click();
									$("a").each(function(){
										if($(this).attr('module')=='module/3d/devicetype/devicetype.html'){$(this).click()}
										});
								}else{alert("error");}
							}
						});
					}else{
						alert("请选择关联型号");
					}
					
				}
			});
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/3d/device/devicetype.html'),
				title:'设置型号',
				width:980,
				height:542,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg,cfg);
				}
			});
		},
		openInner:function(){
			this._init(null,this.cfg);
		},
		_defaults:{
			id:undefined,//数据id
			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#deviceTypeDetail',
		_init:function(dlg,cfg){
			this.id=oc.util.generateId();
			data = cfg;
			initPage();
			search();
			$("#searchBtn").linkbutton('RenderLB',{
				iconCls: 'icon-search',
				onClick : function(){
					search();
				}
			});
			$("#textfield").on("keydown",function(e){
				if(e.keyCode == 13){
					search();
				}
			}).on('focus',function(e){
				if($(this).val()=="") 
					$(this).val("搜索型号/描述/厂商"); 
				else 
					$(this).val("");
			}).on('blur',function(e){
				if($(this).val()=="") 
					$(this).val("搜索型号/描述/厂商");
			});
		}
	};
	/**
	 * 为分页标签添加点击事件
	 */
	function initPage(){
		$(".pageList").val("9");
		$(".pageNum").val(0);
		$("#from").html("0");
		$("#to").html("0");
		$("#total").html("0");
		$("#pageSize").html("0");
		$("#firstPage").click(function(){
			if(!$(this).hasClass(disabledClass)) gotoPage("first");
		});
		$("#prevPage").click(function(){
			if(!$(this).hasClass(disabledClass)) gotoPage("prev");
		});
		$("#nextPage").click(function(){
			if(!$(this).hasClass(disabledClass)) gotoPage("next");
		});
		$("#lastPage").click(function(){
			if(!$(this).hasClass(disabledClass)) gotoPage("last");
		});
		$(".pageNum").on("keydown",function(e){
			if(e.keyCode == 13){
				if(isNaN($(".pageNum").val())){
					$(".pageNum").val("1");
				}
				gotoPage("goto");
			}
				
		});
		$(".pageList").change(function(){
			gotoPage("");
		})
		$("#refresh").click(function(){gotoPage("");});
	}
	/**
	 * 检查分页标签是否可用
	 */
	function setPage(){
		//数据总长度
		var len = dataSource.length;
		//每页显示数
		var pageList = parseInt($(".pageList").val());
		//总页数
		var pageSize = Math.ceil(len/pageList);
		//当前页数
		var pageNum = parseInt($(".pageNum").val());
		if(pageNum+1>pageSize){
			$("#nextPage,#lastPage").addClass(disabledClass);
		}else{
			$("#nextPage,#lastPage").removeClass(disabledClass);
		}
		if(pageNum-1<1){
			$("#firstPage,#prevPage").addClass(disabledClass);
		}else{
			$("#firstPage,#prevPage").removeClass(disabledClass);
		}
	}
	/**
	 * opt 操作类型
	 */
	function gotoPage(opt){
		//数据总长度
		var len = dataSource.length;
		//每页显示数
		var pageList = parseInt($(".pageList").val());
		//当前页数
		var pageNum = parseInt($(".pageNum").val());
		//总页数
		var pageSize = Math.ceil(len/pageList);
		switch(opt){
			case "next"://下一页
				pageNum++;
				break;
			case "prev"://上一页
				pageNum--;
				break;
			case "first"://第一页
				pageNum = 1;
				break;
			case "last"://最后一页
				pageNum = pageSize;
				break;
			case "goto"://跳转
				if(pageNum>pageSize){
					pageNum = pageSize;
				}
				if(pageNum<1){
					pageNum = 1;
				}
				break;
			default://初始化
				setPage();
				pageNum = 1;
				if(len==0){
					$("#dTable").find("#dTbody").empty();
					$(".pageNum").val(0);
					$("#from").html("0");
					$("#to").html("0");
					$("#total").html("0");
					$("#pageSize").html("0");
					return;
				}
				break;
		}
		var rowStart = pageList*(pageNum-1)+1,
			rowEnd = pageList*pageNum;
		if(rowEnd>len) rowEnd=len;
		$(".pageNum").val(pageNum);
		$("#from").html(rowStart);
		$("#to").html(rowEnd);
		$("#total").html(len);
		$("#pageSize").html(pageSize);
		createTable();
		active();
		setPage();
	}
	/**
	 * 激活选中效果
	 */
	function active(){
		$(".sys-box").on('click',function(e){
			$("#dTbody .active").removeClass('active').css("border","none");
			$(this).addClass('active').css("border","2px solid #000079");
		});
	}
	
	/**
	 * 动态创建表格
	 */
	function createTable(){
		var table=$("#dTable"),tr;
		table.find("#dTbody").empty();
		if(dataSource.length>0){
			var rowStart = parseInt($("#from").html()),
			  	rowEnd = parseInt($("#to").html());
			for(var i=rowStart-1;i<rowEnd;i++){
				if((i-rowStart+1)%3==0){
					tr = $("<tr/>");
					table.find("#dTbody").append(tr);
				}
				var product = dataSource[i],picSrc="",model="",brand="",desc="";
				if(product){
					picSrc = data.threedInfo.picturePath + product['图片'];
					model = product['型号'];
					brand = product['品牌'];
					desc = product['描述']||desc;
				}
				tr.append($(''
						+ '<td>'
		           		+ '<div class="sys-box ' + (data.self.model==model?'active"':'"')
           				+ ' style="height:112px; width:300px; overflow: left">'
		               	+ '<div class="sys-boxcon">'
		                + '<table class="td_b_no" width="100%" border="0" cellpadding="0" cellspacing="0">'
		                + '<tbody>'
		                + '<tr>'
		                + '<td rowspan="3" width="27%"><div class="sys-boxpic">'
		                + '<img src="' + picSrc + '" alt="" height="86" width="76"></div></td>'
		                + '<td height="33" width="73%" align="left" title="' + model
		                + '" model="' + model + '"> 设备型号：' + limitLength(model,14) + ' </td>'
		                + '</tr>'
		                + '<tr>'
		                + '<td height="33" align="left" title="' + brand + '" brand="' + brand + '">厂商：' 
		                + limitLength(brand,20) + '</td>'
		                + '</tr>'
		                + '<tr>'
//		                + '<td height="33" align="left">描述：' + desc + '</td>'
		                + '<td height="33" align="left" title="' + desc + '">描述：' + limitLength(desc,20) + '</td>'
		                + '</tr>'
		                + '</tbody>'
		                + '</table>'
		                + '</div>'
		                + '</div>'
		                + '</td>'
						+ ''));
			}
		}		
	}
	/**
	 * 模糊搜索
	 */
	function search(){
		var arr = [];
		var index = 0;
		var value = $("#textfield").val()=="搜索型号/描述/厂商"?"":$("#textfield").val();
		var reg = new RegExp(value,"i");
		
		for(var i in data.products){
			var model = data.products[i];
			if(!(reg.test(model.型号) || reg.test(model.品牌) || reg.test(model.描述))){
				continue;
			}
			arr.push(model);
			if(data.self.model==model.型号){
				index = arr.length-1;			
			}
		}
		//把选中的型号放在第一位
		if(arr.length>0){
			var temp = arr[0];
			arr[0] = arr[index];
			arr[index] = temp;
		}
		dataSource = arr;
		gotoPage("")
		return arr;
	}
	
	/**
	 * 字符串处理函数
	 */
	function limitLength(value, byteLength) { 
		if(value==undefined || value==null || value.toString().trim()=="") return "";
		var newvalue = value.toString()&&(value.toString().replace(/[^\x00-\xff]/g, "**")); 
		var length = newvalue.length; 
	    //当填写的字节数小于设置的字节数 
	    if (length * 1 <=byteLength * 1){ 
	    	return value; 
	    } 
	    var limitDate = newvalue.substr(0, byteLength); 
	    var count = 0; 
	    var limitvalue = ""; 
	    for (var i = 0; i < limitDate.length; i++) { 
	    	var flat = limitDate.substr(i, 1); 
	    	if (flat == "*") { 
	    		count++; 
	        } 
	    } 
	    var size = 0; 
	    var istar = newvalue.substr(byteLength * 1 - 1, 1);//校验点是否为“×” 
	    //if 基点是×; 判断在基点内有×为偶数还是奇数  
	    if (count % 2 == 0) { 
	    	//当为偶数时 
	        size = count / 2 + (byteLength * 1 - count); 
	        limitvalue = value.substr(0, size)+"..."; 
	    } else { 
	    	//当为奇数时 
	        size = (count - 1) / 2 + (byteLength * 1 - count); 
	        limitvalue = value.substr(0, size)+"..."; 
	    } 
	    return limitvalue; 
	}
	
	oc.ns('oc.module.thirdd.resource.devicetype');
	oc.module.thirdd.resource.devicetype={
		open:function(cfg){
			var deviceType=new deviceTypeDetail(cfg);
			deviceType.open(cfg);
		},
		openInner:function(cfg){
			var deviceType=new deviceTypeDetail(cfg);
			deviceType.openInner(cfg);
		}
	};
})(jQuery);
