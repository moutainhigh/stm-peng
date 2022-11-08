$(function() {
	function tinytoolSnmp(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	tinytoolSnmp.prototype = {
		constructor : tinytoolSnmp,
		cfg : undefined,
		snmpDiv : undefined,
		_defaults : {},
		open : function(){
			var that = this;
			this.snmpDiv = $("<div/>");
			this.snmpDiv.dialog({
				title : 'SNMP TEST',
				width : '800px',
				height : '600px',
				modal: false,
				href : oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool_snmp.html'),
				onLoad : function(){
					$("[securityLevel='2']").hide();
					$("[securityLevel='3']").hide();
					
					$('#securityLevel').combobox({
						valueField: 'id',
						textField: 'text',
						data: [{
							id: '1',
							text: 'NOAUTH_NOPRIV',
							selected:true 
						},{
							id: '2',
							text: 'AUTH_NOPRIV'
						},{
							id: '3',
							text: 'AUTH_PRIV'
						}],
						onSelect: function(rec){
							var p1=$("[name='authenticationPassphrase']");
							var p2=$("[name='privacyPassphrase']");
							
							if(rec.id==1){
								p1.val("").attr("disabled","disabled");								
								p2.val("").attr("disabled","disabled");
							}else if(rec.id==2){
								p1.removeAttr("disabled");
								p2.val("").attr("disabled","disabled");
							}else if(rec.id==3){
								p1.removeAttr("disabled");
								p2.removeAttr("disabled");
							}
							
						}
					});

					
					
					
					that.loadHtmlSuccess();
				}
			});
		},
		loadHtmlSuccess : function(){
			var that = this;
			
			var discoverNode=this.cfg.discoverNode;
			
			this.snmpDiv.find("input[name='ip']").val(this.cfg.ip);
			this.snmpDiv.find("input[name='version']").each(function() {
				var version = $(this);
				var showgroup = version.attr('showgroup');
				var hidegroup = version.attr('hidegroup');
				version.on('click', function() {
					that.showHide(showgroup, hidegroup);
				});
				if (version.attr('checked')) {
					that.showHide(showgroup, hidegroup);
				}
			});
			
			var snmpTestFrom=oc.ui.form({
				selector:$('#snmpTestFrom')
			});
			
			this.snmpDiv.find(".snmpTestBtn").linkbutton('RenderLB',{
				onClick : function(){
					alert("SNMP Test......")
					var snmpTestFromVal =snmpTestFrom.val();
					snmpTestFromVal.discoverNode=discoverNode;
					oc.util.ajax({
						url : oc.resource.getUrl('portal/resource/tinyTools/snmpTest.htm'),
						data :snmpTestFromVal,
						success : function(data){
							$('#test_result').text("");
							$.each(data.data, function(i,item) {
						        $('#test_result').append(item+'<br/>')
						    });
						}
					});
				}
			});
		},
		showHide : function(showgroup, hidegroup){
			this.snmpDiv.find("[group='" + showgroup + "']").each(function() {
				var showDiv = $(this);
				showDiv.show();
			});
			this.snmpDiv.find("[group='" + hidegroup + "']").each(function() {
				var hideDiv = $(this);
				hideDiv.hide();
			});
		}
	}
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolSnmp = {
		open : function(cfg) {
			var ttsnmp = new tinytoolSnmp(cfg);
			ttsnmp.open();
		}
	}
});