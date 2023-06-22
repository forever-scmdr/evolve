<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'news'"/>

	<xsl:variable name="rooms" select="page/rooms"/>
	<xsl:variable name="date" select="page/variables/date"/>
	<xsl:variable name="date_millis" select="page/variables/date_millis"/>
	<xsl:variable name="citizen_name" select="page/variables/citizen_name"/>
	<xsl:variable name="citizen" select="if (page/variables/citizen and page/variables/citizen != '') then page/variables/citizen else 'РБ'"/>
	<xsl:variable name="total" select="f:num(page/variables/total)"/>
	<xsl:variable name="quot" select="number(translate(page/rooms/extra_quotient, ',', '.'))"/>
	<xsl:variable name="message" select="page/variables/message"/>
	
	<xsl:variable name="order" select="page/order"/>

	<xsl:function name="f:room_price">
		<xsl:param name="room"/>
		<xsl:param name="date_str"/>
		<xsl:param name="type_str"/>
		<xsl:param name="citizen"/>
		<xsl:param name="rooms"/>
		<xsl:param name="quot"/>
		<xsl:variable name="date" select="f:xsl_date($date_str)"/>
		<xsl:variable name="first_start" select="f:xsl_date($rooms/first_start)"/><!-- TODO добавить текущий год вместо установленного в дате -->
		<xsl:variable name="second_start" select="f:xsl_date($rooms/second_start)"/>
		<xsl:variable name="third_start" select="f:xsl_date($rooms/third_start)"/>
		<xsl:variable name="type_part" select="if ($type_str = 'Оздоровительная') then '_san' else '_ozd'"></xsl:variable>
		<xsl:variable name="citizen_part" select="if ($citizen = 'РБ') then '_bel' else if ($citizen = 'ЕАЭС') then '_rus' else '_eur'"></xsl:variable>
		<xsl:variable name="time_part" select="if ($date &gt;= $first_start and $date &lt; $second_start) then '_first' else if ($date &lt; $third_start) then '_second' else '_third'"/>
		<xsl:variable name="param_name" select="concat('price', $type_part, $citizen_part, $time_part)"/>
		<xsl:variable name="price" select="f:num($room/*[local-name() = $param_name]) * $quot"/>
 		<xsl:sequence select="format-number($price, $f_mask, 'ru')"/>
	</xsl:function>

	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Бронирование</h2>
		<div class="alert alert-warning m-t-small">
			<xsl:value-of select="page/booking_text/restrictions" disable-output-escaping="yes"/>
		</div>
		<form method="post" action="{page/room_search_link}" class="form-inline p-t-small">
			<div class="form-group">
				<input value="{$date}" type="text" name="date" id="date" class="form-control datepicker" placeholder="Дата заезда" 
					onchange="setMillis('date', 'date_millis'); checkInputs()"/>
				<input type="hidden" name="date_millis" id="date_millis" value="{$date_millis}"/>
			</div>
			<div class="form-group">
				<select name="total" id="qty" class="form-control" onchange="checkInputs()" value="{$total}">
					<option value="0">Количество человек</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
					<option value="10">10</option>
				</select>
			</div>
			<div class="form-group">
				<select name="citizen_name" id="citizen_sel" onchange="setCitizen($(this).val()); checkInputs()" class="form-control" value="{$citizen_name}">
					<option value="нет">Выберите гражданство</option>
					<option value="Беларусь">Беларусь</option>
					<option value="Россия">Россия</option>
					<option value="Казахстан">Казахстан</option>
					<option value="Другое">Другое</option>
				</select>
				<input type="hidden" name="citizen" id="citizen_inp" value="{$citizen}"/>
			</div>
			<button style="visibility:hidden" type="submit" class="btn btn-primary" id="submit_link">Показать номера</button>
		</form>
		<p><a href="" data-toggle="modal" data-target="#modal-booking-conditions">Условия бронирования</a></p>
		<div class="row">
			<div class="col-md-6">
				<xsl:if test="page/free_rooms/free_room">
					<h3>Доступные варианты</h3>
					<xsl:for-each-group select="page/free_rooms/free_room[f:num(room/base_beds) &lt;= $total]" group-by="type_name">
						<xsl:sort select="current-grouping-key()"/>
						<xsl:variable name="first" select="current-group()[1]"/>
						<xsl:variable name="room_type" select="//page/rooms/type[@id = $first/type]"/>
						<div class="room">
							<h4><xsl:value-of select="$room_type/name"/></h4>
							<p> 
							<xsl:value-of select="$room_type/base_beds"/><xsl:text> </xsl:text>
							<xsl:value-of select="f:ending($room_type/base_beds, ('основное', 'основных', 'основных'))"/> и 
							<xsl:value-of select="$room_type/extra_beds"/> доп. <xsl:value-of select="f:ending($room_type/extra_beds, ('место', 'места', 'мест'))"/>. 
							<xsl:value-of select="f:room_price($room_type, $date, 'Санаторно-курортная', $citizen, $rooms, $quot)"/> - 
							<xsl:value-of select="f:room_price($room_type, $date, 'Оздоровительная', $citizen, $rooms, 1)"/><xsl:text> </xsl:text>
							<xsl:value-of select="if ($citizen = 'РБ') then 'бел. руб.' else if ($citizen = 'ЕАЭС') then 'рос. руб.' else 'евро'"/>
							за место в сутки.
							</p>
							<div class="gallery">
								<xsl:for-each select="$room_type/picture_pair[position() &lt;= 7]">
									<a href="{@path}{big}" class="fancybox" rel="group-{$room_type/@id}">
										<img src="{@path}{small}"/>
									</a>
								</xsl:for-each>
								<div style="display: none">
									<xsl:for-each select="$room_type/picture_pair[position() &gt; 7]">
										<a href="{@path}{big}" class="fancybox" rel="group-{$room_type/@id}">
											<img src="{@path}{small}"/>
										</a>
									</xsl:for-each>
								</div>
							</div>
							<xsl:for-each select="current-group()">
								<div class="row room-dates">
									<div class="col-md-9">
										<p>Свободен с <xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> по <xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/></p>
									</div>
									<div class="col-md-3">
										<xsl:choose>
											<xsl:when test="$order/free_room/@id = @id"><a href="" class="btn btn-primary btn-sm btn-block">В заявке</a></xsl:when>
											<xsl:otherwise><a href="#" class="btn btn-default btn-sm btn-block" onclick="return postFR('{@id}', 'add')">Выбрать</a></xsl:otherwise>
										</xsl:choose>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</xsl:for-each-group>
				</xsl:if>
				<xsl:if test="not(page/free_rooms/free_room)">
					<h3>На заданную дату найти варианты не удалось.</h3>
				</xsl:if>
			</div>
			<div class="col-md-6">
				<form action="{page/manage_cart}" method="post" id="cart_form">
					<input type="hidden" name="fr" id="cart_form_fr"/>
					<input type="hidden" name="action" id="cart_form_action"/>
					<xsl:if test="$order/free_room">
						<h3>Ваша заявка</h3>
						<xsl:if test="$message">
							<div style="margin-top: 10px">
								<xsl:for-each select="$message">
									<p><span class="error"><xsl:value-of select="."/></span></p>
								</xsl:for-each>
							</div>
						</xsl:if>
						<xsl:for-each select="$order/free_room">
							<xsl:variable name="room_type" select="//page/rooms/type[@id = current()/type]"/>
							<xsl:variable name="room_offer" select="//page/free_rooms/free_room[@id = current()/@id]"/>
							<div id="room_{@id}" class="room-options p-b-default order_room" room-id="{$room_type/@id}">
								<div class="row p-b-default">
									<div class="col-md-9"><h4 class="m-t-zero"><xsl:value-of select="$room_type/name"/></h4></div>
									<div class="col-md-3"><a href="#" onclick="return postFR('{@id}', 'delete')" class="btn btn-default btn-sm btn-block">Удалить</a></div>
								</div>
								<div class="row">
									<div class="col-md-5">
										<div class="form-group">
											<label for="">Дата заезда</label>
											<input type="text" class="form-control datepicker order_room_from" id="date_from_{@id}"
												value="{if (from and from != '') then f:format_date(f:millis_to_date(from)) else ''}"
												min-date="{f:format_date(current-date())}" 
												max-date="{f:format_date(f:millis_to_date($room_offer/to) - 4 * xs:dayTimeDuration('P1D'))}"
												onchange="setMillis('date_from_{@id}', 'date_from_millis_{@id}')"/>
											<input type="hidden" id="date_from_millis_{@id}" name="{new_from}" value="{from}"/>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group">
											<label for="">Дата выезда</label>
											<input type="text" class="form-control datepicker order_room_to" id="date_to_{@id}"
												value="{if (to and to != '') then f:format_date(f:millis_to_date(to)) else ''}"
												min-date="{f:format_date(current-date() + 4 * xs:dayTimeDuration('P1D'))}" 
												max-date="{f:format_date(f:millis_to_date($room_offer/to))}"
												onchange="setMillis('date_to_{@id}', 'date_to_millis_{@id}')"/>
											<input type="hidden" id="date_to_millis_{@id}" name="{new_to}" value="{to}"/>
										</div>
									</div>
								</div>
								<h4>Основные места</h4>
								<xsl:for-each select="order_form_base">
									<xsl:variable name="form" select="//order/order_form[@id = current()]"/>
									<div id="order_form_{$form/@id}" class="row order_form order_form_base">
										<div class="col-md-5">
											<div class="form-group">
												<label for="">Отдыхающий</label>
												<select class="form-control" name="{$form/new_person_type}" value="{$form/person_type}">
													<option value="Взрослый">Взрослый</option>
													<option value="Ребенок">Ребенок</option>
												</select>
											</div>
										</div>
										<div class="col-md-5">
											<div class="form-group">
												<label for="">Тип путевки</label>
												<select class="form-control rest-type" name="{$form/new_voucher_type}" value="{$form/voucher_type}">
													<option value="Санаторно-курортная">Санаторно-курортная</option>
													<option value="Оздоровительная">Оздоровительная</option>
												</select>
											</div>
										</div>
										<div class="col-md-2"><span class="price_sum price-real">-p-</span><xsl:text> </xsl:text><span class="price_cur">-c-</span></div>
									</div>
								</xsl:for-each>
								<h4>Дополнительные места</h4>
								<xsl:for-each select="order_form_extra">
									<xsl:variable name="form" select="//order/order_form[@id = current()]"/>
									<div id="order_form_{$form/@id}" class="row order_form order_form_extra">
										<div class="col-md-5">
											<div class="form-group">
												<label for="">Отдыхающий</label>
												<select class="form-control" name="{$form/new_person_type}" value="{$form/person_type}">
													<option value=""></option>
													<option value="Взрослый">Взрослый</option>
													<option value="Ребенок">Ребенок</option>
												</select>
											</div>
										</div>
										<div class="col-md-5">
											<div class="form-group">
												<label for="">Тип путевки</label>
												<select class="form-control rest-type" name="{$form/new_voucher_type}" value="{$form/voucher_type}">
													<option value=""></option>
													<option value="Санаторно-курортная">Санаторно-курортная</option>
													<option value="Оздоровительная">Оздоровительная</option>
												</select>
											</div>
										</div>
										<div class="col-md-2"><span class="price-real price_sum">-p-</span><xsl:text> </xsl:text><span class="price_cur">-c-</span></div>
									</div>
								</xsl:for-each>
							</div>
						</xsl:for-each>
						<div class="row">
							<div class="col-xs-12">
								<a href="#" class="btn btn-primary btn-lg" onclick="return postFR('0', 'book')">Забронировать</a>
							</div>
						</div>
					</xsl:if>
				</form>
			</div>
		</div>
	</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script>
		<xsl:call-template name="SELECT_SCRIPT"/>
		
		function postFR(roomId, action) {
			$('#cart_form_fr').val(roomId);
			$('#cart_form_action').val(action);
			$('#cart_form').submit();
			return false;
		}

		var PRICES = [];
		<xsl:for-each select="page/rooms/type">
			<xsl:variable name="id" select="@id"/>
			PRICES["<xsl:value-of select="$id"/>"] = [];
			<xsl:for-each select="*[starts-with(name(), 'price')]">
			PRICES["<xsl:value-of select="$id"/>"]["<xsl:value-of select="name()"/>"] = {sum: <xsl:value-of select="f:num(.)"/>, cur: "<xsl:value-of select="if (contains(name(), 'rus')) then 'рос. руб.' else if (contains(name(), 'eur')) then 'евро' else 'бел. руб.'"/>"};</xsl:for-each>
		</xsl:for-each>
		
		var citizen = "<xsl:value-of select="page/variables/citizen"/>";
		
		var FIRST_START = <xsl:value-of select="page/rooms/first_start/@millis"/>;
		var SECOND_START = <xsl:value-of select="page/rooms/second_start/@millis"/>;
		var THIRD_START = <xsl:value-of select="page/rooms/third_start/@millis"/>;
		
		function getPrice(roomId, date, citizen, type) {
			var paramName = "price";
			if (type.toLowerCase() == "Санаторно-курортная".toLowerCase())
				paramName += "_san";
			else
				paramName += "_ozd";
			var cur = "eur";
			if (citizen.toLowerCase() == "РБ".toLowerCase())
				paramName += "_bel";
			else if (citizen.toLowerCase() == "ЕАЭС".toLowerCase())
				paramName += "_rus";
			else
				paramName += "_eur";
			var baseDate = stringToDate(date);
			<xsl:text disable-output-escaping="yes">
			if (baseDate.getTime() &gt;= FIRST_START &amp;&amp; baseDate.getTime() &lt; SECOND_START)
				paramName += "_first";
			else if (baseDate.getTime() &gt;= SECOND_START &amp;&amp; baseDate.getTime() &lt; THIRD_START)
				paramName += "_second";
			else
				paramName += "_third";
			</xsl:text>
			//alert("third_start: <xsl:value-of select="page/rooms/third_start/@millis"/>" + " baseMillis: " + baseDate.getTime() + " " + baseDate + " " + citizen + ' ' + paramName);
			return PRICES[roomId][paramName];
		}
		
		function updatePrices() {
			$('.order_room').each(function() {
				var room = $(this);
				var fromStr = room.find('.order_room_from').first().val();
				var toStr = room.find('.order_room_to').first().val();
				if (fromStr == '' || toStr == '') {
					room.find('.price_sum').html('');
					room.find('.price_cur').html('');
					room.find('.days_num').html('');
					room.find('.days_word').html('');
					return;
				}
				var fromDate = stringToDate(fromStr);
				var toDate = stringToDate(toStr);
				var daysDiff = dayDiff(fromDate, toDate);
				<xsl:text disable-output-escaping="yes">
				if (daysDiff &lt; 1) {
					room.find('.price_sum').html('');
					room.find('.price_cur').html('');
					room.find('.days_num').html('');
					room.find('.days_word').html('');
					return;
				}
				room.find('.days_num').html(daysDiff);
				room.find('.days_word').html(getNumberWordEnding(daysDiff, ['день','дня','дней']));
				</xsl:text>
				room.find('.order_form').each(function() {
					var form = $(this);
					var type = form.find('.rest-type').first().val();
					if (type == '') {
						form.find('.price_sum').html('');
						form.find('.price_cur').html('');
						return;
					}
					var room = form.closest('.order_room');
					var roomId = room.attr('room-id');
					var fromStr = room.find('.order_room_from').first().val();
					var toStr = room.find('.order_room_to').first().val();
					var fromDate = stringToDate(fromStr);
					var toDate = stringToDate(toStr);
					var daysDiff = dayDiff(fromDate, toDate);
					var price = getPrice(roomId, fromStr, citizen, type);
					var quotient = form.hasClass('order_form_base') ? 1 : 0.8;
					form.find('.price_sum').html($.number(price.sum * daysDiff * quotient, 0, '.', ' '));
					form.find('.price_cur').html(price.cur);
				});
			});
		}
		
		function stringToDate(dateStr) {
			var dateParts = dateStr.split('.');
			return new Date(Date.UTC(dateParts[2], dateParts[1] - 1, dateParts[0]));
		}
		
		function dayDiff(first, second) {
		    return Math.round((second - first) / (1000 * 60 * 60 * 24));
		}
		
		function setCitizen(country) {
			if ('Беларусь'.indexOf(country) != -1) {
				$('#citizen_inp').val('РБ');
			} else if ('Россия, Казахстан'.indexOf(country) != -1) {
				$('#citizen_inp').val('ЕАЭС');
			} else if ('Другое'.indexOf(country) != -1) {
				$('#citizen_inp').val(country);
			} else {
				$('#citizen_inp').val('');
			}
			citizen = $('#citizen_inp').val();
			updatePrices();
		}

		function setMillis(stringInp, millisInp) {
			var parts = $('#' + stringInp).val().split('.');
			if (parts.length != 3) {
				$('#' + millisInp).val('');
				return;
			}
			$('#' + millisInp).val(Date.parse(parts[2] + '-' + parts[1] + '-' + parts[0]));
		}

		function checkInputs() {
			var hasQty = $('#qty').val() != '0';
			var hasDate = !($('#date_millis').val() == '');
			var hasCitizen = !($('#citizen_inp').val() == '');
			<xsl:text disable-output-escaping="yes">
			if (hasQty &amp;&amp; hasDate &amp;&amp; hasCitizen) {
				$('#submit_link').css('visibility', '');
			} else {
				$('#submit_link').css('visibility', 'hidden');
			}
			</xsl:text>
		}
		
		$(document).ready(function() {
			updatePrices();
			$('.order_room').find('input, select').change(function() {
				updatePrices();
			});
			checkInputs();
		});
	</script>
	</xsl:template>

</xsl:stylesheet>