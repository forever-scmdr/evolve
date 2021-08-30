tinymce.PluginManager.add('spoiler', function(editor, url) {

	console.log('inited');
	
	editor.ui.registry.addIcon('eye-open', '<svg width="24" height="24" ><circle cx="12" cy="12" r="12" fill="black" /></svg>');
	editor.ui.registry.addIcon('eye-close', '<svg width="24" height="24" ><circle cx="12" cy="12" r="11" fill="white" stroke="black" /></svg>');
	
	editor.contentCSS.push(url + '/spoiler.css');
	var spoilerCaption = editor.getParam('spoiler_caption', 'Spoiler!');


	function addSpoiler()
	  {
		var selection = editor.selection;
		var node = selection.getNode();
		if (node) {
		  editor.undoManager.transact(function() {
		  var content = selection.getContent();
		  if (!content) {
			content = 'Spoiler text.';
		  }
		  selection.setContent('<div class="spoiler" contenteditable="false">' +
						  '<div class="spoiler-toggle">' + spoilerCaption + ' </div>' +
						  '<div class="spoiler-text" contenteditable="true">' +
						  content +
						  '</div></div>');
		  });
		  editor.nodeChanged();
		}
	  }

	  function removeSpoiler()
	  {
		console.log(editor.contentCSS);
		var selection = editor.selection;
		var node = selection.getNode();
		if (node && node.className == 'spoiler')
		{
		  editor.undoManager.transact(function()
		  {
			var newPara = document.createElement('p');
			newPara.innerHTML = node.getElementsByClassName('spoiler-text')[0].innerHTML;
			node.parentNode.replaceChild(newPara, node);
		  });
		  editor.nodeChanged();
		}
	  }

	
	editor.ui.registry.addButton('spoiler-add', {
		tooltip: 'add spoiler'
		,icon: 'eye-open'
		,onAction: function () {
			addSpoiler();
		}
	});
	editor.ui.registry.addButton('spoiler-remove', {
		 tooltip: 'remove spoiler'
		,icon: 'eye-close'
		,onAction: function () {
			removeSpoiler();
		}
	});
});