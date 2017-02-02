<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'news'"/>

	<xsl:variable name="rooms" select="page/rooms"/>
	<xsl:variable name="date" select="page/variables/date"/>
	<xsl:variable name="date_millis" select="page/variables/date_millis"/>
	<xsl:variable name="citizen_name" select="page/variables/citizen_name"/>
	<xsl:variable name="citizen" select="if (page/variables/citizen and page/variables/citizen != '') then page/variables/citizen else 'РБ'"/>
<!-- 	<xsl:variable name="citizen" select="if ($citizen_name = ('Россия', 'Казахстан', 'Армения')) then 'ЕАЭС' else $citizen_name"/> -->
<!-- 	<xsl:variable name="adult" select="f:num(page/variables/adult)"/> -->
<!-- 	<xsl:variable name="infant" select="f:num(page/variables/infant)"/> -->
<!-- 	<xsl:variable name="total" select="$adult + $infant"/> -->
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

	<xsl:template name="CONTENT">
	<div class="common">
		<h1>Бронирование</h1>
		<xsl:value-of select="page/booking/restrictions" disable-output-escaping="yes"/>
		<div class="form-1">
			<xsl:if test="not($date_millis)">
				<div style="float: right; width: 400px; margin-right:0;">
					<b>Введитие код, который сообщил Вам менеджер</b>
					<div style="margin: 7px 0; line-height: 16px;">
						 Для бронирования номера по заявке позвоните нам:
						<br/>
						<span class="number">+375(1797) 45-542</span>&#160;
						<span class="javascript">все телефоны</span>
						<div class="hidden phones" style="display: none;">
							<xsl:value-of select="/page/common/phone_hidden" disable-output-escaping="yes" />
						</div>
					</div>
					<form action="{page/enter_pin}" method="post">
						<input type="text" id="pin" name="pin" placeholder="пин-код" style="display: inline-block;"/>&#160;
						<a onclick="if ($.trim($(this).closest('form').find('input').val()) != '') $(this).closest('form').submit(); return false;" 
							href="#" style="display: inline-block; width: 150px;" id="set-pin" class="submit">
							Подтвердить пин-код
						</a>
					</form>
				</div>
			</xsl:if>
			<form method="post" action="{page/room_search_link}" class="form-1">
				<div>
					<span class="title">Дата заезда:</span>
					<label onclick="$(this).children('a').hide(); $(this).children('input').show();">
						<input value="{$date}" type="text" name="date" id="date" class="datepicker" 
							onchange="setMillis('date', 'date_millis');$('#submit_link').css('display', 'inline-block')"/>
						<input type="hidden" name="date_millis" id="date_millis" value="{$date_millis}"/>
					</label>
				</div>
				<div>
					<span class="title">Гражданство:</span>
					<select name="citizen_name" id="citizen_sel" value="{$citizen_name}" onchange="setCitizen($(this).val())">
						<option value="Беларусь">Беларусь</option>
						<option value="Россия">Россия</option>
						<option value="Казахстан">Казахстан</option>
						<option value="Другое">Другое</option>
					</select>
					<input type="hidden" name="citizen" id="citizen_inp" value="{$citizen}"/>
				</div>
				<div>
					<span class="title">Количество отдыхающих (взрослых и детей):</span>
					<div class="combobox" id="adults">
						<input type="text" name="total" title="отдыхающих" value="{if ($total &gt; 0) then $total else 1}" />
						<select id="adult_sel" value="{$total}">
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
					<a href="#" class="submit" id="submit_link" onclick="$(this).closest('form').submit(); return false;" 
						style="margin-left: 25px; height: 17px; {'display: none'[not($date) or $date = '']}{'display: inline-block;'[$date != '']}">Найти</a>
<!-- 					<div class="combobox" id="adults"> -->
<!-- 						<input type="text" name="adult" title="взрослых" value="{if ($adult &gt; 0) then $adult else 1}" /> -->
<!-- 						<select id="adult_sel" value="{$adult}"> -->
<!-- 							<option value="1">1 взрослый</option> -->
<!-- 							<option value="2">2 взрослых</option> -->
<!-- 							<option value="3">3 взрослых</option> -->
<!-- 							<option value="4">4 взрослых</option> -->
<!-- 							<option value="5">5 взрослых</option> -->
<!-- 						</select> -->
<!-- 					</div> -->
<!-- 					<div class="combobox" id="kids" style="margin-right:0;"> -->
<!-- 						<input type="text" name="infant" value="{$infant}" title="детей (0-16 лет)" /> -->
<!-- 						<select id="infant_sel" value="{$infant}"> -->
<!-- 							<option value="0">0 детей</option> -->
<!-- 							<option value="1">1 ребенок</option> -->
<!-- 							<option value="2">2 детей</option> -->
<!-- 							<option value="3">3 ребенка</option> -->
<!-- 							<option value="4">4 ребенка</option> -->
<!-- 							<option value="5">5 детей</option> -->
<!-- 						</select> -->
<!-- 					</div> -->
				</div>
			</form>
		</div>
		
		<xsl:if test="page/free_rooms/free_room">
			<div class="variants">
				<h2>Доступные варианты:</h2>
				<xsl:for-each-group select="page/free_rooms/free_room[f:num(room/base_beds) &lt;= $total]" group-by="type_name">
					<xsl:sort select="current-grouping-key()"/>
					<xsl:variable name="first" select="current-group()[1]"/>
					<xsl:variable name="room_type" select="//page/rooms/type[@id = $first/type]"/>
					<div class="variant">
						<h3><xsl:value-of select="$room_type/name"/></h3>
						<p> 
						<xsl:value-of select="$room_type/base_beds"/><xsl:text> </xsl:text>
						<xsl:value-of select="f:ending($room_type/base_beds, ('основное', 'основных', 'основных'))"/> и 
						<xsl:value-of select="$room_type/extra_beds"/> доп. <xsl:value-of select="f:ending($room_type/extra_beds, ('место', 'места', 'мест'))"/>. 
						<xsl:value-of select="f:room_price($room_type, $date, 'Санаторно-курортная', $citizen, $rooms, $quot)"/> - 
						<xsl:value-of select="f:room_price($room_type, $date, 'Оздоровительная', $citizen, $rooms, 1)"/><xsl:text> </xsl:text>
						<xsl:value-of select="if ($citizen = 'РБ') then 'бел. руб.' else if ($citizen = 'ЕАЭС') then 'рос. руб.' else 'евро'"/>
						за место в сутки.
						</p>
						<div class="room-pics">
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
							<div class="free">
								Свободен с <xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> по 
								<xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/>. 
								<xsl:choose>
									<xsl:when test="$order/free_room/@id = @id"><a class="added"><!-- ✓ Выбрано -->В заявке&#160;</a></xsl:when>
									<xsl:otherwise><a href="#" onclick="return postFR('{@id}', 'add')">Выбрать</a></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:for-each>
					</div>
				</xsl:for-each-group>
			</div>
		</xsl:if>
		<xsl:if test="not($date_millis)">
			<div class="pageText" style="margin-top: 40px;">
				<xsl:value-of select="page/booking/booking" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
		
		<div class="form-2">
		<form action="{page/manage_cart}" method="post" id="cart_form">
			<input type="hidden" name="fr" id="cart_form_fr"/>
			<input type="hidden" name="action" id="cart_form_action"/>
			<xsl:if test="$order/free_room">
				<h2>Ваша заявка</h2>
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
					<div id="room_{@id}" class="order_room" room-id="{$room_type/@id}">
						<div class="room">
							<span><xsl:value-of select="$room_type/name"/></span>
							<a href="#" onclick="return postFR('{@id}', 'delete')">Удалить</a>
						</div>
						<div>
							<span class="gray">Желаемые даты заезда и выезда:</span>
							<div>
								<label style="display: inline-block; margin-right: 20px; margin-top: 10px;">
									Дата заезда
									<input type="text" style="margin-top: 4px;" class="datepicker order_room_from" id="date_from_{@id}"
										value="{if (from and from != '') then f:format_date(f:millis_to_date(from)) else ''}"
										min-date="{f:format_date(current-date())}" 
										max-date="{f:format_date(f:millis_to_date($room_offer/to) - 4 * xs:dayTimeDuration('P1D'))}"
										onchange="setMillis('date_from_{@id}', 'date_from_millis_{@id}')"/>
									<input type="hidden" id="date_from_millis_{@id}" name="{new_from}" value="{from}"/>
								</label>
								<label style="display: inline-block; margin-top: 10px;">
									Дата выезада
									<input type="text" style="margin-top: 4px;" class="datepicker order_room_to" id="date_to_{@id}"
										value="{if (to and to != '') then f:format_date(f:millis_to_date(to)) else ''}"
										min-date="{f:format_date(current-date() + 4 * xs:dayTimeDuration('P1D'))}" 
										max-date="{f:format_date(f:millis_to_date($room_offer/to))}"
										onchange="setMillis('date_to_{@id}', 'date_to_millis_{@id}')"/>
									<input type="hidden" id="date_to_millis_{@id}" name="{new_to}" value="{to}"/>
								</label>
								<span class="price" style="margin-top: 31px"><span class="days_num">600</span><xsl:text> </xsl:text><span class="days_word">руб.</span></span>
								<div class="clear"></div>
							</div>
						</div>
						<div rel="room-1">
							<p>Основные места:</p>
							<xsl:for-each select="order_form_base">
								<xsl:variable name="form" select="//order/order_form[@id = current()]"/>
								<div id="order_form_{$form/@id}" class="order_form order_form_base">
									<label>
										Отдыхающий
										<select class="kids" name="{$form/new_person_type}" value="{$form/person_type}">
											<option value="Взрослый">Взрослый</option>
											<option value="Ребенок">Ребенок</option>
										</select>
									</label>
									<label>
										Тип путевки
										<select class="rest-type" name="{$form/new_voucher_type}" value="{$form/voucher_type}">
											<option value="Санаторно-курортная">Санаторно-курортная</option>
											<option value="Оздоровительная">Оздоровительная</option>
										</select>
									</label>
									<span class="price"><span class="price_sum">600</span><xsl:text> </xsl:text><span class="price_cur">руб.</span></span>
									<div class="clear"></div>
								</div>
							</xsl:for-each>
						</div>
						<div rel="room-1">
							<p>Допольнительные места:</p>
							<xsl:for-each select="order_form_extra">
								<xsl:variable name="form" select="//order/order_form[@id = current()]"/>
								<div id="order_form_{$form/@id}" class="order_form order_form_extra">
									<label>
										Отдыхающий
										<select class="kids" name="{$form/new_person_type}" value="{$form/person_type}">
											<option value=""></option>
											<option value="Взрослый">Взрослый</option>
											<option value="Ребенок">Ребенок</option>
										</select>
									</label>
									<label>
										Тип путевки
										<select class="rest-type" name="{$form/new_voucher_type}" value="{$form/voucher_type}">
											<option value=""></option>
											<option value="Санаторно-курортная">Санаторно-курортная</option>
											<option value="Оздоровительная">Оздоровительная</option>
										</select>
									</label>
									<span class="price"><span class="price_sum">-p-</span><xsl:text> </xsl:text><span class="price_cur">-c-</span></span>
									<div class="clear"></div>
								</div>
							</xsl:for-each>
						</div>
					</div>
					<xsl:if test="position() != last()"><hr/></xsl:if>
				</xsl:for-each>
				<a href="#" class="submit" onclick="return postFR('0', 'book')">Забронировать</a>
			</xsl:if>
		</form>
		</div>
	</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script><!-- 
		$(document).ready(function() {
			<xsl:if test="$adult &gt; 0">
				$('#adult_sel').val('<xsl:value-of select="$adult"/>');
				$('#infant_sel').val('<xsl:value-of select="$infant"/>');
			</xsl:if>
			<xsl:if test="$citizen_name or $citizen_name != ''">
				$('#citizen_name').val('<xsl:value-of select="$citizen_name"/>');
			</xsl:if>
		});
		 -->
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
			if ('Беларусь'.indexOf(country) != -1)
				$('#citizen_inp').val('РБ');
			else if ('Россия, Казахстан'.indexOf(country) != -1)
				$('#citizen_inp').val('ЕАЭС');
			else 
				$('#citizen_inp').val(country);
			citizen = $('#citizen_inp').val();
			updatePrices();
		}

		function setMillis(stringInp, millisInp) {
			var parts = $('#' + stringInp).val().split('.');
			if (parts.length != 3)
				return;
			$('#' + millisInp).val(Date.parse(parts[2] + '-' + parts[1] + '-' + parts[0]));
		}
		
		$(document).ready(function() {
			updatePrices();
			$('.order_room').find('input, select').change(function() {
				updatePrices();
			});
		});
	</script>
	</xsl:template>

</xsl:stylesheet>