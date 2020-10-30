$(document).ready(
		function() {
			// 点击出现上传框
			$("#word").click(function() {
				$("#file").trigger("click");
			})
			// 上传文件
			$("#file").change(
					function(e) {
						// 选择了文件
						if ($("#file").val() != "") {
							var type = $("#file").val().substr($("#file").val().lastIndexOf("."));
                            var ori = $("#file").val().substr($("#file").val().lastIndexOf("\\")+1,$("#file").val().lastIndexOf("."));
                            if (type == ".pdf" ) {
								if ($("#file").get(0).files[0].size/1024/1024>10){
									 //文件大小超过10M
									 $("#warn").css("visibility","visible");
									 $("#warnspan").text("文件大于10Mb!");
								}else{  
									   $("#filetype").attr("value",type);
                                       $("#oriName").attr("value",ori);
									   $("#form").submit();  
								}
							} else {
								// 格式不支持
								$("#warn").css("visibility","visible");
								 $("#warnspan").text("格式不支持!请选择pdf文档");
							}
						}
					});
		});
