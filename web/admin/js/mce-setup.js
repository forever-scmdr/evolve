var mceSettings = {
	"mce_big" : {
		selector : "textarea.mce_big",
		language : 'ru',
	//	theme : "modern",
		skin : "lightgray"
		,content_css : [
				"css/text-style.css",
				"font-awesome-4.6.3/css/font-awesome.min.css" ]
		,plugins : [
				'advlist autolink lists link image charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars", "spoiler" ],
		toolbar : "undo redo| spoiler-add spoiler-remove | fontsizeselect | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link image",
		fontsize_formats : 'inherit 12px 13px 14px 16px 18px 24px 36px',
		style_formats_merge : true,
		style_formats : [
			{title : 'Характеристики товара', selector : 'table', classes : 'features'},
			{
			title : 'Теги HTML-5',
			items : [ {
				title : 'section',
				block : 'section',
				wrapper : true,
				merge_siblings : false
			}, {
				title : 'article',
				block : 'article',
				wrapper : true,
				merge_siblings : false
			}, {
				title : 'hgroup',
				block : 'hgroup',
				wrapper : true
			}, {
				title : 'aside',
				block : 'aside',
				wrapper : true
			}, {
				title : 'figure',
				block : 'figure',
				wrapper : true
			}

			 ]
			 
		}

		],
		height : 300
		,rel_list: [
			 {title: '-', value: ''}
			,{title: 'Сыылка на внешний сайт', value: 'nofollow'}
			,{title: 'Картинка с увеичением', value: 'fancybox'}
  		]
	}
	,"mce_medium" : {
		selector : "textarea.mce_medium",
		language : 'ru',
	//	theme : "modern",
		skin : "lightgray"
		,content_css : ["css/text-style.css",
				"font-awesome-4.6.3/css/font-awesome.min.css" ]
		,plugins : [
				'advlist autolink lists link image charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars", "spoiler" ],
		toolbar : "undo redo| spoiler-add spoiler-remove  | fontsizeselect | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link image",
		fontsize_formats : 'inherit 16px 18px 24px 36px',
		style_formats_merge : true,
		style_formats : [
		{title : 'Характеристики товара', selector : 'table', classes : 'features'},
		{
			title : 'Теги HTML-5',
			items : [ {
				title : 'section',
				block : 'section',
				wrapper : true,
				merge_siblings : false
			}, {
				title : 'article',
				block : 'article',
				wrapper : true,
				merge_siblings : false
			}, {
				title : 'hgroup',
				block : 'hgroup',
				wrapper : true
			}, {
				title : 'aside',
				block : 'aside',
				wrapper : true
			}, {
				title : 'figure',
				block : 'figure',
				wrapper : true
			} 
			
			]
		}
		
		],
		height : 200
		,rel_list: [
			 {title: '-', value: ''}
			,{title: 'Сыылка на внешний сайт', value: 'nofollow'}
			,{title: 'Картинка с увеичением', value: 'fancybox'}
  		]
	}
	,"mce_small" : {
		selector : "textarea.mce_small",
		language : 'ru',
	//	theme : "modern",
		skin : "lightgray"
		,content_css : [ "css/text-style.css",
				"font-awesome-4.6.3/css/font-awesome.min.css" ]
		,plugins : [
				'advlist autolink lists link charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars"],
		toolbar : "undo redo  | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link",
		style_formats_merge : true,
		height : 200
		,rel_list: [
			 {title: 'Нет', value: ''}
			,{title: 'Ссылка на внешний сайт', value: 'nofollow'}
			,{title: 'Картинка с увеичением', value: 'fancybox'}
  		]
	}
};



$(document).ready(function(){
	
	var filePickerTypes = "";
	if(typeof window.imgId != undefined) filePickerTypes += "image";
	if(typeof window.fileId != undefined) filePickerTypes += " file";

	var hasFiles = typeof imgId != undefined || typeof fileId != undefined;
	
	for(var setting in mceSettings){
		if(hasFiles && mceSettings[setting].plugins[0].indexOf(" image ") != -1){
			mceSettings[setting] = $.extend(mceSettings[setting], {
				file_picker_types: 'file image'
					,file_picker_callback: function(callback, value, meta) {
						if((meta.filetype == 'image' && typeof imgId == "undefined") || (meta.filetype == 'file' && typeof fileId == "undefined")) return;
					
						var formId = "frm_"+ new Date().getTime();
						var paramId = (meta.filetype == 'image')? imgId : fileId;
						var form = $("<form>",{ method: "post", action: "admin_upload_img.action?itemId="+itemId+"&multipleParamId="+paramId, enctype: "multipart/form-data", id: formId});
						ipt = $("<input>", {type:"file", name: "multipleParamValue"});
						form.append(ipt);
						ipt.click();
						ipt.change(function(){
						$("body").append(form);
						form = $("#"+formId);
						form.ajaxSubmit({
							 error: function() {
								alert('Ошибка закачки файла');
								form.remove();
							}
							,success: function(data, status, arg3) {
								form.remove();
							}
						});
						callback(uploadPath + $(this).val());
						});
					}
			});
		}
		//console.log(mceSettings[setting]);
		tinymce.init(mceSettings[setting]);
	}
});