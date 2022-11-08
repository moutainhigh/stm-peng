(function($) {
    var mainId = oc.util.generateId();
    var mainDiv = $('#custom_strategy_list_main').attr('id', mainId).panel({
        title: '',
        fit: true,
        isOcAutoWidth: true
    });

    var datagrid = null;
    
	//获取虚拟化资源类别
	oc.util.ajax({
		successMsg:null,
		url: oc.resource.getUrl('portal/vm/vmProfileAction/getVmResourceCategory.htm'),
		data:null,
		success:function(data){
			if(data.data){
				var queryForm = oc.ui.form({
					selector: mainDiv.find('.oc-form'),
					combobox: [{
						selector: mainDiv.find('.oc-form').find('[name=profileDesc]'),
						selected:0,
						placeholder:false,
						data:data.data,
						onChange : function(id){
							mainDiv.find('.oc-form').find('[name=resourceId]').val("");
							if(datagrid != undefined)
								datagrid.reLoad();
							// queryForm.ocui[1].reLoad(oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id='+ id));
						}
					}, {
						selector: '[name=resourceId]',
						// url:oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id=' + data.data[0].id),
						filter: function(data) {
							return data.data;
						},
						onChange: function(id) {
							datagrid.reLoad();
						}
					}]
				});
				var datagridDiv = mainDiv.find('.custom_strategy_list_datagrid');
				
				function gridOpenFormatter(value, rec, index) {
					return '<a href="javascript:void(0)" onclick="" >O</a>';
				}
				datagrid = oc.ui.datagrid({
					selector: datagridDiv,
					url: oc.resource.getUrl('strategy/strategyAll/getCustomStrategyAll.htm'),
					queryForm: queryForm,
					hideReset: true,
					hideSearch: true,
					checkOnSelect: false,
					octoolbar: {
						left: [queryForm.selector],
						right: [{
							id: 'createProfileBtn',
							text: '新建',
							iconCls: 'fa fa-plus',
							onClick: function() {
								_open('add');
							}
						}, '&nbsp;', {
							id: 'delProfileBtn',
							iconCls: 'fa fa-trash-o',
							text: "删除",
							onClick: function() {
								var ids = datagrid.getSelectIds();
								if (ids == undefined || ids == "") {
									alert("请勾选需要删除的策略");
								} else {
									oc.ui.confirm("是否确认删除策略？", function() {
										oc.util.ajax({
											url: oc.resource.getUrl('strategy/strategyAll/batchDelCustomStrategy.htm'),
											data: {
												ids: ids.join()
											},
											successMsg: null,
											success: function(d) {
												datagrid.load();
												alert("删除策略成功");
											}
										});
									});
								}
							}
						}]
					},
					columns: [
					          [{
					        	  field: 'createUser',
					        	  hidden: true
					          }, {
					        	  field: 'updateUser',
					        	  hidden: true
					          }, {
					        	  field: 'id',
					        	  checkbox: true,
					        	  sortable: true,
					        	  isDisabled: function(value, row, index) {
					        		  // 非系统管理员且不是创建人则不能勾选策略
					        		  var user = oc.index.getUser();
					        		  if (!user.systemUser && row.createUser != user.id) {
					        			  return true;
					        		  } else {
					        			  return false;
					        		  }
					        	  }
					          }, {
					        	  field: 'strategyName',
					        	  title: '策略名称',
					        	  sortable: true,
					        	  align: 'left',
					        	  width: '25%',
					        	  formatter: function(value, rowData, rowIndex) {
					        		  return "<a class='show_profile' id='" + rowData.id + "'>" + '<span title="' + value + '">' + rowData.strategyName + '</span>' + "</a>";
					        	  }
					          }, {
					        	  field: 'strategyType',
					        	  title: '策略类型',
					        	  sortable: true,
					        	  align: 'left',
					        	  width: '25%'
					          }, {
					        	  field: 'domainName',
					        	  title: '域',
					        	  sortable: true,
					        	  align: 'left',
					        	  width: '24%'
					          }, {
					        	  field: 'timeline',
					        	  title: '基线标示',
					        	  align: 'left',
					        	  width: '25%',
					        	  formatter: function(value, rowData, rowIndex) {
					        		  return "<a class='show_timeline ico ico-mark' id='" + rowData.id + "'></a>";
					        	  }
					          }]
					          ],
					          onClickCell: function(rowIndex, field, value) {
					        	  if (field == 'timeline') {
					        		  var row = $(this).datagrid('getRows')[rowIndex];
					        		  var profileId = row.id;
					        		  var createUserId = row.createUser;
					        		  oc.resource.loadScript('resource/module/resource-management/timeline/js/timeline.js', function() {
					        			  oc.timeline.dialog.open(profileId, row.resourceId, createUserId);
					        		  });
					        	  }
					        	  
					        	  if (field == 'strategyName') {
					        		  var row = $(this).datagrid('getRows')[rowIndex];
					        		  var profileId = row.id;
					         		 oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
					  					oc.customprofile.strategy.detail.show(profileId);
					         		 });
					        	  }
					          }
					
				});
				// 新建和删除策略的权限控制
				var user = oc.index.getUser();
				if (!user.domainUser && !user.systemUser) {
					$("#createProfileBtn").hide();
					$("#delProfileBtn").hide();
				}
				
			}else{
				alert('查询虚拟化类别错误!');
			}
		}
	});
	collapse_btn();
	
    
	//左侧菜单折叠按钮
	function collapse_btn(){
		$("#collapse_btn").click(function(){
			if($(this).hasClass('fa-angle-double-left')){
				$('.oc_vm_index').layout('collapse','west');
				$('.oc_vm_index').layout('panel','center').parent().css('left','0px');
				$(this).removeClass('fa-angle-double-left');
				$(this).addClass('fa-angle-double-right');
			}else{
				$('.oc_vm_index').layout('expand','west');
				$(this).removeClass('fa-angle-double-right');
				$(this).addClass('fa-angle-double-left');
			}
		});
	}

    function _open(type) {
        var id = undefined;
        oc.resource.loadScript('resource/module/resource-management/strategy/js/customStrategyDetail.js', function() {
            oc.module.custom.detail.open({
                type: type,
                id: id,
                isVm:true,
                saveCall: function(data) {
                    datagrid.reLoad();
                }
            });
        });
    }

    // 命名空间
    oc.ns('oc.module.custom.strategy');
    oc.module.custom.strategy = {
        querydatagrid: function() {
            datagrid.reLoad();
        }
    };

})(jQuery);
