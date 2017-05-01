var DIGITS = "1234567890";
var RUSSIAN_MATCH_LETTERS = DIGITS + "_abcdefghijklmnopqrstuvwxyzабвгдеёжзиыйклмнопрстуфхцчшщэюя. ,?/\\|:-\"='%";
var ENGLISH_REPLACEMENT_LETTERS;
ENGLISH_REPLACEMENT_LETTERS = [
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "_",
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
    "a", "b", "v", "g", "d", "e", "yo", "g", "z", "i", "y", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t",
    "u", "f", "h", "ts", "ch", "sh", "sch", "e", "yu", "ya", ".", "_", "", "ask", "_", "_", "_", "_", "-", "", "", "", "_"
];

function translit(russianString) {
	console.log(russianString);
	s = "";
	for(i=0; i<russianString.length; i++){
		idx = RUSSIAN_MATCH_LETTERS.indexOf(russianString.charAt(i));
		if(idx != -1){
			s += ENGLISH_REPLACEMENT_LETTERS[idx];
		}
	}
	return s;
}

var mceSettings = {
	"mce_big" : {
		selector : "textarea.mce_big",
		language : 'ru',
		theme : "modern",
		skin : "dark",
		content_css : ["css/text-style.css"],
		plugins : [
				'advlist autolink lists link image charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars", "spoiler" ],
		toolbar : "undo redo| spoiler-add spoiler-remove | styleselect | fontsizeselect | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link image",
		fontsize_formats : 'inherit 16px 18px 24px 36px',
		style_formats_merge : true,
		style_formats : [ {
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
			,{
				 title : "Ряд"
				,block : "div"
				,wrapper : true
				,classes: 'w-row'
				,merge_siblings : false
			}
			,{
				 title : "Показатель"
				,block : "div"
				,wrapper : true
				,classes: 'achievement w-col w-col-4 w-col-stack'
				,merge_siblings : false
			}
		],
		height : 300
	}
	,"mce_medium" : {
		selector : "textarea.mce_medium",
		language : 'ru',
		theme : "modern",
		skin : "dark",
		content_css : ["css/text-style.css"],
		plugins : [
				'advlist autolink lists link image charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars", "spoiler" ],
		toolbar : "undo redo| spoiler-add spoiler-remove | styleselect | fontsizeselect | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link image",
		fontsize_formats : 'inherit 16px 18px 24px 36px',
		style_formats_merge : true,
		style_formats : [ {
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
		,{
			 title : "Ряд"
			,block : "div"
			,wrapper : true
			,classes: 'w-row'
			,merge_siblings : false
		}
		,{
			 title : "Показатель"
			,block : "div"
			,wrapper : true
			,classes: 'achievement w-col w-col-4 w-col-stack'
			,merge_siblings : false
		}
	
		],
		height : 200
	}
	,"mce_small" : {
		selector : "textarea.mce_small",
		language : 'ru',
		theme : "modern",
		skin : "dark",
		content_css : ["css/text-style.css"]
		,plugins : [
				'advlist autolink lists link charmap print preview anchor textcolor',
				'searchreplace visualblocks code fullscreen',
				'insertdatetime media table contextmenu paste code'
				// ,"fontawesome noneditable"
				, "visualchars"],
		toolbar : "undo redo | styleselect | bold | italic | alignleft aligncenter alignright alignjustify  | forecolor backcolor | bullist numlist outdent indent | charmap | link",
		style_formats_merge : true,
		height : 200
	}
};



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
						alert($(this).val());
						v = translit($(this).val());
						callback(uploadPath + v);
						});
					}
			});
		}
		//console.log(mceSettings[setting]);
		tinymce.init(mceSettings[setting]);
	}
});

