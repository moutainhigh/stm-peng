(function(){
	function auditburule(){
		this.initDom();
		this.initData();
		this.registerEvent();
	}
	
	auditburule.prototype={
		constructor:auditburule,
		data:0,
		initDom:function(){
			this.id=oc.util.generateId();
			this.dom=$('#auditRule').attr('id',this.id);
			this.isOpenDom=this.dom.find('.isOpen:first span');
			this.form=oc.ui.form({
				selector:this.dom
			});
			var that = this;
			that._initMethods.baseInfo(that);
		},
		registerEvent:function(){
			var t=this;
			t.dom.find('.l-btn-small').linkbutton('RenderLB',{
				text:'应用',
				iconCls:"fa fa-check-circle",
				onClick:function(){
					var backupDay = t.form.find("input[name='backup_day']").val();
					if(isNaN(backupDay)) {
						alert("备份周期请输入数字!");
						return;
					}
					if(backupDay < 1||backupDay>31) {
						alert("备份周期不能小于1天大于31天!");
						return;
					}
					t.save();
				}
			});
			this.isOpenDom.click(function(){
				t.setOpen(!t.isOpenDom.is('.open'));
				t.save();
			});
		},
		save:function(){
			var t=this,d=t.data,form=t.form;
			if(d.open=t.isOpenDom.is('.open')){
				if(!form.validate())return;
				$.extend(d,form.val());
			}
			oc.util.ajax({
				url:oc.resource.getUrl('system/auditlog/insertRule.htm'),
				data:{auditlogRule:d},
				success:function(data){
					  if(data=="FALSE"){
						  alert("操作失败");
					  }else{
						  alert("操作成功");
					  }
					  
				  },
				successMsg:'操作成功！'
			});
		},
		initData:function(){
			var t=this;
			oc.util.ajax({
				url:oc.resource.getUrl('system/auditlog/getRule.htm'),
				success:function(d){
					t.data=d.data;
					t.setOpen(t.data.open);
					t.form.val(d.data);
				}
			});
		},
		setOpen:function(open){
			this.isOpenDom.removeClass('open close');
			if(open){
				this.isOpenDom.addClass('open');
				this.form.selector.fadeIn();
			}else{
				this.isOpenDom.addClass('close');
				this.form.selector.fadeOut();
			}
		},
		_initMethods : {
			'baseInfo' : function(that) {
				that._inspectPlanBasicInfoForm = oc.ui.form({
					selector : that.dom.find('.col1'),
					combobox: [
					    {
						selector: '[name=backupDateHour]',
						placeholder: false,
						fit: false,
						width: 90,
						data:[
						      {id:00, name:'00时'},
						      {id:01, name:'01时'},
						      {id:02, name:'02时'},
						      {id:03, name:'03时'},
						      {id:04, name:'04时'},
						      {id:05, name:'05时'},
						      {id:06, name:'06时'},
						      {id:07, name:'07时'},
						      {id:08, name:'08时'},
						      {id:09, name:'09时'},
						      {id:10, name:'10时'},
						      {id:11, name:'11时'},
						      {id:12, name:'12时'},
						      {id:13, name:'13时'},
						      {id:14, name:'14时'},
						      {id:15, name:'15时'},
						      {id:16, name:'16时'},
						      {id:17, name:'17时'},
						      {id:18, name:'18时'},
						      {id:19, name:'19时'},
						      {id:20, name:'20时'},
						      {id:21, name:'21时'},
						      {id:22, name:'22时'},
						      {id:23, name:'23时'}
						]
					},{
						selector: '[name=backupDateMinute]',
						placeholder: false,
						fit: false,
						width: 100,
						data:[
						      {id:00, name:'00分'},
						      {id:05, name:'05分'},
						      {id:10, name:'10分'},
						      {id:15, name:'15分'},
						      {id:20, name:'20分'},
						      {id:25, name:'25分'},
						      {id:30, name:'30分'},
						      {id:35, name:'35分'},
						      {id:40, name:'40分'},
						      {id:45, name:'45分'},
						      {id:50, name:'50分'},
						      {id:55, name:'55分'}
						]
					}]
				});
			}
		}
	};
	
	new auditburule();
})();