//-- сутки
window.DAY = 24*60*60*1000;
// -- названия месяцев
window.mNames = ["январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"];

function buildUbeTable(date, startMs, endMs){
	//-- кругляем дату
	t = Math.floor(date.getTime()/DAY)*DAY;
	
	//-- Создаем таблицу
	table = $("<table>",{id: "room-days"});

	//-- ряд с цифрами от 1 до 31. Первая ячейка пустая
	tr = $("<tr>");
	for (var i = 0; i < 32; i++) {
		td = (i>0)? $("<th>",{text: i}) : $("<th>");
		tr.append(td);
	}
	table.append(tr);

	//-- текущий месяц
	currMonth = date.getMonth();
	//-- текущий год
	currYear = date.getFullYear();

	//-- первый ряд таблицы
	tr = $("<tr>", {html: "<td class='month-name'>"+mNames[currMonth]+"</td>"});
	//-- длинная невидимая ячейка (чтобы сдвинуть вервую видимую ячейку куда надо)
	if(date.getDate() != 1){
		tr.append($("<td>", {colspan: date.getDate() - 1}));
	}

	for (var i = 0; i < 365; i++) {
		//-- прибавляем по дню
		if(i!=0){t += DAY};
		//-- получаем дату
		d = new Date(t);
		m = d.getMonth();
		y = d.getFullYear();
		//-- если месяц изменился - делаем новый ряд 
		if(m != currMonth){
			currMonth = m;
			table.append(tr);
			tr = $("<tr>", {html: "<td class='month-name'>"+mNames[currMonth]+"</td>"});			
		}
		//-- если месяц год - делаем новый ряд с серым фоном
		if( y != currYear){
			currYear = y;
			table.append($("<tr>",{html:"<td colspan='32'>"+currYear+"</td>", "class":"year-sep"}));
		}
		//-- день

		dayClass = (t >= startMs && t <= endMs)? "day selected" : "day";

		tr.append($("<td>",{"class": dayClass, "date" : t, title: mNames[currMonth]+", "+d.getDate()}));
	}
	table.append(tr);
	$("#ubertable").append(table);
}

function settableInterval(startMs, endMs){
	$("#ubertable").find(".day[date='"+startMs+"']").trigger("click");
	end = $("#ubertable").find(".day[date='"+endMs+"']");
	endIndex = days.index(end);
	$("#ubertable").find(".day:gt("+ssi1001+")").addClass("between");
	$("#ubertable").find(".day:gt("+endIndex+")").removeClass("between");
	end.trigger("click");
}

function getTableInterval(){
	s = $("#ubertable").find(".day.selected:eq(0)").attr("date");
	e = $("#ubertable").find(".day.selected:last").attr("date");
	return [Number(s), Number(e)];
}

function isIntervalSet() {
	return $("#ubertable").find(".day.selected").length != 0;
}

//-- ОБРАБОТЧИКИ СОБЫТИЙ

$(document).on("click", "#ubertable .day", function(e){
	days = $("#ubertable").find(".day");
	if($("#ubertable").is(".clicked")){
		$("#ubertable").find(".sel-start").removeClass("sel-start").addClass("selected");
		$("#ubertable").find(".sel-end").removeClass("sel-end").addClass("selected");
		$("#ubertable").find(".between").removeClass("between").addClass("selected");
		start = (ssi1001 < days.index(this))? $("#ubertable").find(".day").eq(ssi1001) : $("#ubertable").find(".day").eq(days.index(this));
		end = (ssi1001 > days.index(this))? $("#ubertable").find(".day").eq(ssi1001) : $("#ubertable").find(".day").eq(days.index(this));
		start = start.attr("date")*1;
		end = end.attr("date")*1;
		// console.log(new Date(start));
		// console.log(new Date(end));
	}
	else{
		days.removeClass("selected");
		$("#ubertable").find(".sel-start").removeClass("sel-start");
	}
	$(this).addClass("sel-start");
	$("#ubertable").toggleClass("clicked");
	window.ssi1001 = days.index(this);
});
$(document).on("mouseenter", "#ubertable.clicked .day", function(){
	$(this).addClass("sel-end");
	days = $("#ubertable").find(".day");
	endIndex = days.index(this);
	if(endIndex > ssi1001){
		$("#ubertable").find(".day:gt("+ssi1001+")").addClass("between");
		$("#ubertable").find(".day:gt("+endIndex+")").removeClass("between");
	}
	if(endIndex < ssi1001){
		$("#ubertable").find(".day:gt("+endIndex+")").addClass("between");
		$("#ubertable").find(".day:gt("+ssi1001+")").removeClass("between");
	}
});
$(document).on("mouseleave", "#ubertable.clicked .day", function(){
	$(this).removeClass("sel-end");
	days = $("#ubertable").find(".day");
	days.removeClass("between");
});