$(document).ready(
		function() {
			if (typeof searchQuery == "string") {
				deleteCookie("current-query");
				setCookie("current-query", searchQuery, 1);
			}
			searchQuery = (typeof searchQuery == "string") ? searchQuery
					: getCookie("current-query");
			
			if (typeof searchQuery != "string")
				return;
			else {
				h = checkHistory();
			//	h = true;
				if (h) {
					if (document.location.href.indexOf("/prod/") > 6) {
						$("#mods-tab").trigger("click");
					}
					highlight(searchQuery);
				}
			}
		});

function checkHistory() {
	return document.referrer.indexOf("search/?q=") > 7;
}
function highlight(q) {

	t = $(".link");
	t = t.add($(".mods"));
	t = t.add($(".text-content p, .text-content td"));
	t.each(function(i) {
		ths = $(this);
		//--clear HTML
		strip(ths, "span, sup, sub");
		//-- extract HTML
		htm = ths.html();
		regExp = new RegExp(q, 'gim');
		found = htm.match(regExp);
		if(found == null) return;
		found = found.unique();
		for(i=0; i<found.length; i++){
			htm = htm.replaceAll(found[i], "<match>" + found[i] + "</match>");
		}
		ths.html(htm);
	});
}

String.prototype.replaceAll = function(search, replacement) {
	var target = this;
	return target.replace(new RegExp(search, 'gm'), replacement);
};

function strip(el, junkSelector){
	$(el).find(junkSelector).each(function(){
		var content = $(this).contents();
		 $(this).replaceWith(content);
	});
}
Array.prototype.unique = function() {
    var a = [];
    for (var i=0, l=this.length; i<l; i++)
        if (a.indexOf(this[i]) === -1)
            a.push(this[i]);
    return a;
}