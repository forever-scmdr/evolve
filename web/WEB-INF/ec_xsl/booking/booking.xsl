<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="common_page_base.xsl" />
	<xsl:import href="../utils_inc.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:variable name="status" select="page/variables/status"/>
	<xsl:variable name="unpaid" select="page/variables/unpaid"/>
	<xsl:variable name="by_pin" select="page/variables/by_pin"/>

	<xsl:variable name="mNames" select="('январь', 'февраль', 'март', 'апрель', 'май', 'июнь', 'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь')"/>
	<xsl:variable name="totalDays" select="25"/>

	<xsl:template name="month">
		<xsl:param name="start_date"/>
		<xsl:param name="date"/>
		<xsl:param name="total_days"/>
		<xsl:variable name="prev_date" select="$date - xs:dayTimeDuration('P1D')"/>
		<xsl:variable name="month" select="month-from-date($date)"/>
		<xsl:variable name="prev_month" select="if ($start_date = $date) then number(0) else number(month-from-date($prev_date))"/>
		<xsl:choose>
			<xsl:when test="$month != $prev_month">
				<td class="month"><span style="position: absolute; background: white">&#160;<xsl:value-of select="$mNames[$month]"/></span>&#160;</td>
			</xsl:when>
			<xsl:otherwise>
				<td>&#160;</td>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="days-from-duration($date - $start_date) &lt;= $total_days">
			<xsl:call-template name="month">
				<xsl:with-param name="start_date" select="$start_date"/>
				<xsl:with-param name="date" select="$date + xs:dayTimeDuration('P1D')"/>
				<xsl:with-param name="total_days" select="$total_days"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="day">
		<xsl:param name="start_date"/>
		<xsl:param name="date"/>
		<xsl:param name="total_days"/>
		<xsl:variable name="day" select="day-from-date($date)"/>
		<xsl:choose>
			<xsl:when test="$day = 1">
				<td class="m-start">1</td>
			</xsl:when>
			<xsl:otherwise>
				<td><xsl:value-of select="$day"/></td>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="days-from-duration($date - $start_date) &lt;= $total_days">
			<xsl:call-template name="day">
				<xsl:with-param name="start_date" select="$start_date"/>
				<xsl:with-param name="date" select="$date + xs:dayTimeDuration('P1D')"/>
				<xsl:with-param name="total_days" select="$total_days"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="room_day">
		<xsl:param name="start_date"/>
		<xsl:param name="date"/>
		<xsl:param name="total_days"/>
		<xsl:param name="room"/>
		<xsl:variable name="day_before_millis" select="f:date_to_millis($date - xs:dayTimeDuration('P1D'))"/>
		<xsl:variable name="millis" select="f:date_to_millis($date)"/>
		<xsl:variable name="start_millis" select="f:date_to_millis($start_date)"/>
		<xsl:choose>
			<xsl:when test="$millis &gt;= $room/from and $millis &lt;= $room/to">
				<td class="used">
					<xsl:variable name="base_millis" select="if ($room/from &lt; $start_millis) then $start_millis else $room/from"/>
					<xsl:if test="not($day_before_millis &gt;= $base_millis and $day_before_millis &lt;= $room/to)">
						<xsl:value-of select="days-from-duration(f:millis_to_date($room/to) - f:millis_to_date($base_millis)) + 1"/>
					</xsl:if>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td></td>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="days-from-duration($date - $start_date) &lt;= $total_days">
			<xsl:call-template name="room_day">
				<xsl:with-param name="start_date" select="$start_date"/>
				<xsl:with-param name="date" select="$date + xs:dayTimeDuration('P1D')"/>
				<xsl:with-param name="total_days" select="$total_days"/>
				<xsl:with-param name="room" select="$room"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CONTENT">
	<h1>Заявки и номера</h1>
	<span class="sbmt"><a href="{page/new_contract}" class="submit">Добавить заявку</a></span>
	<div class="order-table">
		<p class="filter">
			Показывать <a href="{page/show_order_all}" class="{'active'[not($status)]}">Все</a>
			<a href="{page/show_order_new}" class="{'active'[$status = '0']}">Новые</a>
			<a href="{page/show_order_unconfirmed}" class="{'active'[$status = '1']}">Неподтвержденные</a>
			<a href="{page/show_order_confirmed}" class="{'active'[$status = '2' and $unpaid = '0']}">Подтвержденные</a>
			<a href="{page/show_order_unpaid}" class="{'active'[$status = '2' and $unpaid = '1']}">Срочно оплатить</a>
			<a href="{page/show_order_paid}" class="{'active'[$status = '3']}">Оплаченные</a>
			<a href="{page/show_order_by_pin}" class="{'active'[$by_pin]}">По заявке</a>
		</p>
		<div class="sep"></div>
		<table>
			<tbody>
				<tr>
					<th class="date">Дата</th>
					<th class="status"></th>
					<th class="name">Имя</th>
					<th class="rooms">Номера</th>
					<th class="in-out">Даты</th>
					<th class="days">Дни</th>
					<th class="phone">Телефон</th>
				</tr>
				<xsl:for-each select="page/order[$unpaid = '0' or (days-from-duration(f:xsl_date(pay_until_date) - current-date()) &lt;= 2)]">
					<xsl:variable name="order" select="."/>
					<xsl:variable name="count" select="count(free_room)"/>
					<xsl:variable name="main_form" select="order_form[is_contractor = '1']"/>
					<xsl:for-each select="free_room">
						<xsl:variable name="first" select="position() = 1"/>
						<xsl:variable name="base_count" select="count($order/order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')])"/>
						<xsl:variable name="extra_count" select="count($order/order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')])"/>
						<tr>
							<xsl:if test="$first">
								<td class="date" rowspan="{$count}"><xsl:value-of select="tokenize($order/received_date, '\s+')[1]"/></td>
								<td class="status" rowspan="{$count}">
									<xsl:if test="not($order/status) or $order/status = '0'">
										<div class="new"></div>
									</xsl:if>
									<xsl:if test="$order/status = ('1', '2')">
										<xsl:variable name="days" select="days-from-duration(f:xsl_date($order/pay_until_date) - current-date())"/>
										<div class="{'red'[$days &lt;= 2]}">
											<span><xsl:value-of select="$days"/></span>
										</div>
									</xsl:if>
									<xsl:if test="$order/status = '3'">
										<div class="ok"></div>
									</xsl:if>
								</td>
								<td class="name" rowspan="{$count}">
									<a href="{$order/show_contract}">
										<xsl:value-of select="$main_form/first_name"/><xsl:text> </xsl:text>
										<xsl:value-of select="$main_form/second_name"/><xsl:text> </xsl:text>
										<xsl:value-of select="$main_form/last_name"/>
										<xsl:if test="not($main_form/first_name != '' or $main_form/second_name != '' or $main_form/last_name != '')">Редактировать</xsl:if>
									</a>
								</td>
							</xsl:if>
							<td class="rooms">
								<xsl:value-of select="type_name"/> (<xsl:value-of select="$base_count"/>+<xsl:value-of select="$extra_count"/>)
							</td>
							<td class="in-out">
								<xsl:value-of 
									select="f:day_month_short_string(f:format_date(f:millis_to_date(from)))"/>-<xsl:value-of 
										select="f:day_month_short_string(f:format_date(f:millis_to_date(to)))"/>
							</td>
							<td class="days">
								<xsl:value-of select="days-from-duration(f:millis_to_date(to) - f:millis_to_date(from))"/>
							</td>
							<xsl:if test="$first">
								<td class="phone" rowspan="{$count}">
									<xsl:value-of select="$main_form/phone"/>
								</td>
							</xsl:if>
						</tr>
					</xsl:for-each>
					<xsl:if test="not(free_room)">
						<tr>
							<td class="date"><xsl:value-of select="tokenize($order/received_date, '\s+')[1]"/></td>
							<td class="status">
								<div class="new"></div>
							</td>
							<td class="name">
								<a href="{$order/show_contract}">
									<xsl:value-of select="$main_form/first_name"/><xsl:text> </xsl:text>
									<xsl:value-of select="$main_form/second_name"/><xsl:text> </xsl:text>
									<xsl:value-of select="$main_form/last_name"/>
								</a>
							</td>
							<td class="rooms" colspan="3">
								<a href="{$order/show_contract}">Редактировать</a>
							</td>
							<td class="phone">
								<xsl:value-of select="$main_form/phone"/>
							</td>
						</tr>
					</xsl:if>
				</xsl:for-each>
			</tbody>
		</table>
	</div>
	<div class="intervals-table">
		<xsl:for-each select="page/room">
			<xsl:variable name="free" select="//page/free_rooms/free_room[type = current()/@id]"/>
			<div>
				<h3><xsl:value-of select="name"/></h3>
				<span class="sbmt"><a class="submit" href="{add_room}">Добавить номер</a></span>
				<div class="time-table">
					<div class="ctrl">
						<table class="intervals">
							<tr>
								<td>&#160;</td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>&#160;</td>
								<td></td>
								<td></td>
							</tr>
							<xsl:for-each select="$free">
								<tr class="{'fade'[not(current()/show) or current()/show = '0']}">
									<td class="num"><xsl:value-of select="position()"/>.</td>
									<td class="room-num">
										<a href="{show_room}"><xsl:value-of select="num"/></a>
									</td>
									<td class="checkbox">
										<label onclick="location.replace('{toggle_room}')">
											<input type="checkbox" style="display:none;" onchange="fadeTr(this);" name=""/>
										</label>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</div>
					<xsl:if test="count($free) != 0">
						<div class="time">
							<table class="intervals">
								<tr>
									<xsl:call-template name="month">
										<xsl:with-param name="start_date" select="current-date()"/>
										<xsl:with-param name="date" select="current-date()"/>
										<xsl:with-param name="total_days" select="number($totalDays)"/>
									</xsl:call-template>
								</tr>
								<tr>
									<xsl:call-template name="day">
										<xsl:with-param name="start_date" select="current-date()"/>
										<xsl:with-param name="date" select="current-date()"/>
										<xsl:with-param name="total_days" select="number($totalDays)"/>
									</xsl:call-template>
								</tr>
								<xsl:for-each select="$free">
									<tr class="{'fade'[not(current()/show) or current()/show = '0']}">
										<xsl:call-template name="room_day">
											<xsl:with-param name="start_date" select="current-date()"/>
											<xsl:with-param name="date" select="current-date()"/>
											<xsl:with-param name="total_days" select="number($totalDays)"/>
											<xsl:with-param name="room" select="current()"/>
										</xsl:call-template>
									</tr>
								</xsl:for-each>
							</table>
						</div>
					</xsl:if>
					<div style="clear:both;"></div>
				</div>
			</div>
		</xsl:for-each>
	</div>
	</xsl:template>

</xsl:stylesheet>