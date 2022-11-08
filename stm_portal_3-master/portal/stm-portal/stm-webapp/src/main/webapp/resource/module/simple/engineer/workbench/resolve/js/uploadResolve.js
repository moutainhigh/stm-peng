$(function(){
	var form="",
		$alertPageContainer= $('#alertPageContainer'),
		$uploadResolveForm = $('#uploadResolveForm'),
		$solveDetail = $('#solveDetail');
	init();
	var myEditor;
	function init(){
		UMinstance();
		multiFileBoxInstance();
		uploadKnowleDetail();
		closeDetailPage();
		backAnalyzeEngine();
	}
	function UMinstance(){
		myEditor = UM.getEditor('myEditor');
		$(".edui-container #myEditor").width("100%");
//		myEditor.addListener("selectionchange",function(){
//			checkContentLength();
//		});
	}
	
	function checkContentLength(){
		var textContent =  myEditor.getContentTxt(),totalNum = 3500;
		$("#numOfInput").text(textContent.length);
		$("#numOfSurplus").text(totalNum-textContent.length);
		if(textContent.length>totalNum){
			$("#normalLength").hide();
			$("#longLength").show();
		}else{
			$("#normalLength").show();
			$("#longLength").hide();
		}
	}
	function uploadKnowleDetail(){
		$('.btn_first_upload').on('click',function(){
			var title = $.trim($uploadResolveForm.find('.sol_name_txt').val()),umContent = $.trim(UM.getEditor('myEditor').getContent());
			if(title == ""){
				alert("解决方案标题不能为空！");
			}else if(umContent == ""){
				alert("解决方案内容不能为空！");
			}else if(title.length > 50){
				alert("解决方案标题长度不能超过50个字符");
			}else if(umContent.length > 5000){
				alert("解决方案内容长度不能超过5000个字符");
			}else{
				var data ={knowledgeId:snapshotStr.id,resolveTitle:title,resolveContent:umContent};
				data = $.extend(data,form.val());
				oc.util.ajax({
					url:oc.resource.getUrl('knowledge/local/saveKnowledgeResolve.htm'),
					data:data,
					success:function(t){
						alert('发表成功');
						$solveDetail.find('#back').click();
						$alertPageContainer.find('.change_cor .oc_simp_stxt').click();
					}
				});
			}
		});
	}
	function multiFileBoxInstance(){
		var sessionId;
		oc.util.ajax({
			url : oc.resource.getUrl('system/login/getSessionId.htm'),
			successMsg : null,
			async:false,
			success : function(data) {
				sessionId = data.data;
			}
		});
		 form = oc.ui.form({
			selector:$uploadResolveForm,
			multiFileBox:{
				buttonClass:'oc_simple_btn_white oc_simple_btn_bg',
				selector:'[name=file_upload]',
				uploader:oc.resource.getUrl('platform/file/fileUpload.htm?jsessionid='+sessionId),
				showSelector:'#showFileDiv',
				fileItem:'<div id="${fileId}" class="uploadify-queue-item">\
	                    <div class="cancel">\
						<a href="javascript:void(0);">X</a>\
                    </div>\
                    <span class="fileName">${fileName}</span>\
                </div>',
                uploadLimit:6
			}
		});
	}
	/**
	 * 关闭分析详情界面
	 * */
	function closeDetailPage(){
		$solveDetail.on('click','.oc_simple_close_btn',function(){
			$solveDetail.remove();
			$alertPageContainer.empty();
			UM.getEditor('myEditor').destroy();
		});
	}
	function backAnalyzeEngine(){
		$solveDetail.on('click','#back',function(){
			$solveDetail.remove();
			$alertPageContainer.show();
			UM.getEditor('myEditor').destroy();
		});
	}
});