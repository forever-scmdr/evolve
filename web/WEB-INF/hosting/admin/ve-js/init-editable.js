$(document).on("click", "#edit-mode", function(e){
	$(this).css({background: "lime"});
	initEditor();
});

function initEditor(){
	$(".editable").focus(function(){});
	tinymce.init({
		selector: '.editable.string',
		inline: true,
		toolbar: 'undo redo',
		menubar: false
		,setup: function(editor){
			editor.on("blur", function(e){
				id = "#" + editor.id;
				el = $(id);
				n = el.attr("data-field");
				form = $("#page-changes");
				l= form.find("input[name='"+n+"']").length;
				
				if(l == 0){
					ipt = $("<input>", {name : n});
					ipt.val(editor.getContent());
					form.append(ipt);
				}
				else{
					ipt = form.find("input[name='"+n+"']");
					ipt.val(editor.getContent());
				}
			});
		}
	});
	tinymce.init({
		selector: '.editable.text',
		inline: true,
		plugins: [
			'advlist autolink lists link image charmap print preview anchor',
			'searchreplace visualblocks code fullscreen',
			'insertdatetime media table contextmenu paste'
		],
		toolbar: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image'
		,setup: function(editor){
			editor.on("blur", function(e){
				id = "#" + editor.id;
				el = $(id);
				n = el.attr("data-field");
				form = $("#page-changes");
				l= form.find("input[name='"+n+"']").length;
				
				if(l == 0){
					ipt = $("<input>", {name : n, type: "text"});
					ipt.val(editor.getContent());
					form.append(ipt);
				}
				else{
					ipt = form.find("input[name='"+n+"']");
					ipt.val(editor.getContent());
				}
			});
		}
	});
	
}

