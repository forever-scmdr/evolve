/*Cover with loader 2.0*/
function ve_buildLoader(el){
	el = $(el);
	p = el.offset();
	elT = p.top;
	ell = p.left;
	w = el.outerWidth();
	h = el.outerHeight();

	posStyle = el.css("position");
	prst = (posStyle == "fixed")? "fixed" : "absolute";
	elT = (posStyle == "fixed")? el.position().top : elT;

	div = $("<div>", {
		 "class": 'loader-container'
	}).css({
		 position: prst
		,background: '#fff'
		,"z-index": 10000
		,top: elT
		,left: ell
		,width: w
		,height: h
		,"box-shadow": "10px 10px 5px 0px rgba(0,0,0,0.75)"
	});

	$("body").append(div);


}

function VeLoaderController(){
	var genereteId = function(el){
		id = (el.index("body")+new Date().getTime()).toString(32);
//		alert(id);
//		console.log(id);
		return id;
	}
	this.buildLoader = function(el){
		el = $(el);
		el.each(function(i){
			t = $(this);
			p = t.offset();
			tT = p.top;
			tl = p.left;
			w = t.outerWidth();
			h = t.outerHeight();

			posStyle = el.css("position");
			prst = (posStyle == "fixed")? "fixed" : "absolute";
			tT = (posStyle == "fixed")? t.position().top : tT;

			id = genereteId(t);

			div = $("<div>", {
		 		 "class": 'loader-container'
		 		, id:  id
			}).css({
				 position: prst
				,background: '#fff'
				,"z-index": 10000
				,top: tT
				,left: tl
				,width: w
				,height: h
				,"box-shadow": "10px 10px 5px 0px rgba(0,0,0,0.75)"
			});
			$("body").append(div);
			t.data({loaderId: id});
		});
	}
	this.updateLoaderContent = function(){return;}
}