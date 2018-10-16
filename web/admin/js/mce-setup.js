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

function translit(russianString) {
	var RUSSIAN_CHRS = "1234567890_abcdefghijklmnopqrstuvwxyzабвгдеёжзиыйклмнопрстуфхцчшщэюя. ,?/\|:-\"='%";
	var REPLACEMENT_CHARS = [
        "1","2","3","4","5","6","7","8","9","0","_",
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
        "a","b","v","g","d","e","yo","g","z","i","y","i","k","l","m","n","o","p","r","s","t",
        "u","f","h","ts","ch","sh","sch","e","yu","ya",".","_","","ask","_","_","_","_","_","","","","_"
	];
	russianString = russianString.toLowerCase();
	var arr = russianString.split("");
    console.log(arr);
	for(i = 0; i<arr.length; i++){
		var idx = RUSSIAN_CHRS.indexOf(arr[i]);
		if(idx != -1){
			arr[i] = REPLACEMENT_CHARS[idx];
		}else arr[i] = '_';
	}
	var translited = arr.join("");
    console.log(translited);
	return translited;
}

$(document).ready(function(){
	filePickerTypes = "";
	if(typeof imgId != undefined) filePickerTypes += "image";
	if(typeof fileId != undefined) filePickerTypes += " file";
	
	hasFiles = typeof imgId != undefined || typeof fileId != undefined;
	for(var setting in mceSettings){
		if(hasFiles && mceSettings[setting].plugins[0].indexOf(" image ") != -1){
			mceSettings[setting] = $.extend(mceSettings[setting], {
				file_picker_types: 'file image'
					,file_picker_callback: function(callback, value, meta) {
						if((meta.filetype == 'image' && typeof imgId == "undefined") || (meta.filetype == 'file' && typeof fileId == "undefined")) return;
					
						formId = "frm_"+ new Date().getTime();
						paramId = (meta.filetype == 'image')? imgId : fileId;
						form = $("<form>",{ method: "post", action: "admin_upload_img.action?itemId="+itemId+"&multipleParamId="+paramId, enctype: "multipart/form-data", id: formId});
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
						var val = $(this).val().replace("C:\\fakepath\\", "");
						val = translit(val);
						callback(uploadPath + val);
						});
					}
			});
		}
		//console.log(mceSettings[setting]);
		tinymce.init(mceSettings[setting]);
	}
});