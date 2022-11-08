(function($) {
    var mainId = oc.util.generateId(),
    //表格实例
	datagrid = null;
        //模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
    mainDiv = $('#profile_list_main').attr('id', mainId).panel({
        title: '',
        fit: true,
        isOcAutoWidth: true
    });
    //表格查询表单对象
	
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
							//queryForm.ocui[1].reLoad(oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id='+ id));
						}
					}, {
						selector: '[name=resourceId]',
						filter: function(data) {
							return data.data;
						},
						onChange: function(id) {
							datagrid.reLoad();
						}
					}]
				});
				//表格文档对象
				datagridDiv = mainDiv.find('.profile_main_list_datagrid');
				
				function gridOpenFormatter(value, rec, index) {
					return '<a href="javascript:void(0)" onclick="" >O</a>';
				}
				datagrid = oc.ui.datagrid({
					selector: datagridDiv,
					url: oc.resource.getUrl('strategy/strategyAll/getDefaultStrategyAll.htm'),
					queryForm: queryForm,
					hideReset: true,
					hideSearch: true,
					octoolbar: {
						left: [queryForm.selector],
						right: [{
							id: 'copyProfileBtn',
							text: '复制',
							iconCls: 'icon-copy',
							onClick: function() {
								var id = datagrid.getSelectId();
								if (id == undefined || id == "") {
									alert('请选择一条需要复制的策略数据');
								} else if (datagrid.getSelectIds().length > 1) {
									alert('请选择一条需要复制的策略数据');
								} else {
									_open('copy', id);
								}
							}
						}]
					},
					columns: [
					          [{
					        	  field: 'id',
					        	  checkbox: true,
					        	  sortable: true
					          }, {
					        	  field: 'strategyName',
					        	  title: '策略名称',
					        	  sortable: true,
					        	  align: 'left',
					        	  width: '50%',
					        	  formatter: function(value, rowData, rowIndex) {
					        		  
					        		  return "<a class='show_profile' id='" + rowData.id + "'>" + '<span title="' + value + '">' + rowData.strategyName + '</span>' + "</a>";
					        	  }
					          }, {
					        	  field: 'strategyType',
					        	  title: '策略类型',
					        	  sortable: true,
					        	  align: 'left',
					        	  width: '49%'
					          }]
					          ],
					          onLoadSuccess: function(data) {
					        	  
					        	  $(".show_profile").linkbutton({
					        		  plain: true,
					        		  onClick: function() {
					        			  var profileId = $(this).attr('id');
					         			 oc.resource.loadScript('resource/module/resource-management/js/default_strategy_detail.js',function(){
						     					oc.defaultprofile.strategy.detail.show(profileId);
						     			 });
					        		  }
					        	  
					        	  });
					          }
				});
				// 新建策略的权限控制
				var user = oc.index.getUser();
				if (!user.domainUser && !user.systemUser) {
					$("#copyProfileBtn").hide();
				}
			}else{
				alert('查询虚拟化类别错误!');
			}
		}
	});
		
	collapse_btn()
    
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

    function _open(type, id) {
        oc.resource.loadScript('resource/module/resource-management/strategy/js/profile_main_detail.js', function() {
            oc.module.profile.detail.open({
                type: type,
                id: id,
                saveCall: function(data) {
                    datagrid.reLoad();
                }
            });
        });

    }
})(jQuery);
