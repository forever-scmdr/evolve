var currrentEditor = top.tinymce.activeEditor;
var args = currrentEditor.windowManager.getParams();
var form = document.getElementById('uploadForm');
form.onsubmit = checkForm;
function checkForm() {
	var fileIpt = document.getElementById('file');
	var filePath = fileIpt.value;
	if(filePath == '' || typeof filePath == "undefined"){return false; }
}