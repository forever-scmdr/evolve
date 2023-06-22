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

	<xsl:variable name="pname" select="'internal'"/>

	<xsl:variable name="rooms" select="page/rooms"/>
	<xsl:variable name="cart_order" select="page/order"/>
	<xsl:variable name="pin_order" select="page/pin_order"/>
	<xsl:variable name="order" select="page/pin_order[$pin_order] | page/order[not($pin_order)]"/>
	<xsl:variable name="is_not_pin" select="not($pin_order)"/>
	<xsl:variable name="order_error" select="not($pin_order) and not($cart_order)"/>
	
	<xsl:variable name="main_form" select="$order/order_form[@id = $order/main_form]"/>
	<xsl:variable name="kid_form" select="$order/order_form[person_type = 'Ребенок']"/>
	<xsl:variable name="adult_form" select="$order/order_form[person_type = 'Взрослый' and @id != $main_form/@id]"/>
	
	<xsl:variable name="message" select="page/variables/message"/>

	<xsl:template match="order_form">
		<xsl:param name="main"/><!-- 1 или 0 -->

		<div class="form-group">
			<label for="">Фамилия</label>
			<input class="form-control" type="text" name="{new_last_name}" value="{last_name}"/>
		</div>
		<div class="form-group">
			<label for="">Имя</label>
			<input class="form-control" type="text" name="{new_first_name}" value="{first_name}" />
		</div>
		<div class="form-group">
			<label for="">Отчество</label>
			<input class="form-control" type="text" name="{new_second_name}" value="{second_name}" />
		</div>
		<div class="form-group">
			Гражданство:
			<p><xsl:value-of select="$order/citizen_name"/></p>
		</div>
		<div class="form-group date-select birth-date">
			<label for="">Дата рождения</label>
			<input class="inp" type="hidden" name="{new_birth_date}" value="{birth_date}"/>
			<xsl:variable name="birth_date" select="birth_date[. != '']"/>
			<select class="form-control days" value="{if ($birth_date) then f:num(tokenize($birth_date, '\.')[1]) else ''}">
				<xsl:for-each select="1 to 31">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
			<select class="form-control months" value="{if ($birth_date) then f:num(tokenize($birth_date, '\.')[2]) else ''}">
				<xsl:for-each select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')">
					<option value="{position()}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
			<select class="form-control years" value="{if ($birth_date) then tokenize($birth_date, '\.')[3] else ''}">
				<xsl:for-each select="year-from-date(current-date()) to 1900">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label for="">Телефон</label>
			<input class="form-control" type="text" name="{new_phone}" value="{phone}" />
		</div>
		<div class="form-group">
			<label for="">Адрес</label>
			<input class="form-control" type="text" name="{new_address}" value="{address}" />
		</div>
		<xsl:if test="$main = '1'">
			<div class="form-group">
				<label for="">Адрес эл. почты</label>
				<input class="form-control" type="text" name="{new_email}" value="{email}" />
			</div>
		</xsl:if>
		<div class="form-group">
			<label for="">Серия и номер паспорта</label>
			<input class="form-control" type="text" name="{new_passport}" value="{passport}" />
		</div>
<!-- 			<label> -->
<!-- 				Идентификационный номер: -->
<!-- 				<input type="text" name="{new_id}" value="{id}" /> -->
<!-- 			</label> -->
		<div class="form-group date-select birth-date">
			<label for="">Дата выдачи</label>
			<input class="inp" type="hidden" name="{new_passport_issued_date}" value="{passport_issued_date}"/>
			<xsl:variable name="issued_date" select="passport_issued_date[. != '']"/>
			<select class="form-control days" value="{if ($issued_date) then f:num(tokenize($issued_date, '\.')[1]) else ''}">
				<xsl:for-each select="1 to 31">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
			<select class="form-control months" value="{if ($issued_date) then f:num(tokenize($issued_date, '\.')[2]) else ''}">
				<xsl:for-each select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')">
					<option value="{position()}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
			<select class="form-control years" value="{if ($issued_date) then tokenize($issued_date, '\.')[3] else ''}">
				<xsl:for-each select="1900 to year-from-date(current-date())">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label for="">Кем выдан</label>
			<input class="form-control" type="text" name="{new_passport_issued}" value="{passport_issued}" />
		</div>
		<xsl:if test="$main = '1' and $is_not_pin">
			<div class="form-group">
				<label for="">Оплата путевки</label>
				<xsl:choose>
					<xsl:when test="pay_only = '1'">
						Оплата путевки только для иных лиц<br/>
						Изменить: <p/>
						<a href="#" class="btn btn-default btn-sm btn-block" onclick="submitOrder('{set_pay_and_stay}'); return false;">Оплата путевки для себя и иных лиц</a>
					</xsl:when>
					<xsl:otherwise>
						Оплата путевки для себя и иных лиц<br/>
						Изменить: <p/>
						<a href="#" class="btn btn-default btn-sm btn-block" onclick="submitOrder('{set_pay_only}'); return false;">Оплата путевки только для иных лиц</a>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<input type="hidden" name="{new_is_contractor}" value="1"/>
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="CONTENT">
	<div class="content-container">
		<section class="p-t-default">
			<div class="container">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{page/index_link}">Главная страница</a> →
						</div>
						<h2 class="m-t-zero">Выбранные номера и путевки</h2>
						<xsl:if test="$order_error">
							<h3 style="color: red">Введен неверный пин-код. Регистрация невозможна</h3>
							<b>Введитие код, который сообщил Вам менеджер</b>
							<form action="{page/enter_pin}" method="post" class="m-b-small">
								<div class="input-group">
									<input id="pin" name="pin" type="text" class="form-control" placeholder="Пин-код"/>
									<span class="input-group-btn">
										<button onclick="if ($.trim($(this).closest('form').find('input').val()) != '') $(this).closest('form').submit(); return false;" 
											class="btn btn-primary" type="button">Подтвердить</button>
									</span>
								</div>
							</form>
							<p>Пин-код вам скажет менеджер, после бронирования номера по телефону. 
							Номер и тип путевки уже будут выбраны. 
							Вам останется заполнить персональные данные для заключения договора.</p>
							<p>Для бронирования номера свяжитесь с нами по телефону <strong>+375(1797) 45-542.</strong></p>
						</xsl:if>						
						<xsl:if test="not($order_error)">
							<p>Гражданство отдыхающих <xsl:value-of select="$order/citizen_name"/>.</p>
							<xsl:for-each select="$order/free_room">
								<p>
									<strong><xsl:value-of select="type_name"/>, 
									<xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> - <xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/>.
									</strong>
								</p>
								<p>
									Основные места: 
									<xsl:variable name="base_forms" select="$order//order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')]"/>
									<xsl:for-each select="$base_forms">
										<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
										<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
									</xsl:for-each>
								</p>
								<p>
									<xsl:variable name="extra_forms" select="$order//order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')]"/>
									Дополнительные места: 
									<xsl:for-each select="$extra_forms">
										<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
										<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
									</xsl:for-each>
									<xsl:if test="not($extra_forms)">нет.</xsl:if>
								</p>
							</xsl:for-each>
							<div class="summ">
								<span><b>К оплате</b>&#160;<xsl:value-of select="$order/sum"/>&#160;<xsl:value-of select="$order/cur"/></span>
								<xsl:if test="$is_not_pin">
									<a href="{page/room_search_link}">Изменить выбор</a>
								</xsl:if>
							</div>
							<h3>Данные для договора</h3>
						</xsl:if>
					</div>
				</div>
				<xsl:if test="not($order_error)">
					<form method="post" action="{page/proceed}" id="order_forms">
						<div class="row">
							<xsl:for-each select="$message">
								<p><span class="error"><xsl:value-of select="."/></span></p>
							</xsl:for-each>
							<div class="col-md-4">
								<h4>С кем заключается договор</h4>
								<xsl:apply-templates select="$main_form">
									<xsl:with-param name="main" select="'1'"/>
								</xsl:apply-templates>
							</div>
							<xsl:if test="$adult_form">
								<div class="col-md-4">
									<xsl:for-each select="$adult_form">
										<h4>Сопровождающий взрослый №<xsl:value-of select="position()"/></h4>
										<xsl:apply-templates select=".">
											<xsl:with-param name="main" select="'0'"/>
										</xsl:apply-templates>
									</xsl:for-each>
								</div>
							</xsl:if>
							<xsl:if test="$kid_form">
								<div class="col-md-4">
									<xsl:for-each select="$kid_form">
										<h4>Сопровождающий ребенок №<xsl:value-of select="position()"/></h4>
										<xsl:apply-templates select=".">
											<xsl:with-param name="main" select="'0'"/>
										</xsl:apply-templates>
									</xsl:for-each>
								</div>
							</xsl:if>						
						</div>
						<div class="row">
							<label>
								<xsl:variable name="b" select="page/booking"/>
								Я ознакомлен(а) с договором (<a href="{$b/@path}{$b/contract}" download="{$b/@path}{$b/contract}">скачать договор</a>):
								<input type="checkbox" name="{$cart_order/new_contract_agreed}" value="1">
									<xsl:if test="$cart_order/contract_agreed = '1'"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
								</input>
							</label>
							<div class="col-md-2 col-md-offset-10">
								<a href="#" class="btn btn-lg btn-primary btn-block" onclick="submitOrder('{$order/proceed}'); return false;">Забронировать</a>
							</div>
						</div>
					</form>
				</xsl:if>
			</div>
		</section>
	</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script><!-- 
		$(document).ready(function() {
			<xsl:if test="$adult &gt; 0">
				$('#adult_sel').val('<xsl:value-of select="$adult"/>');
				$('#infant_sel').val('<xsl:value-of select="$infant"/>');
			</xsl:if>
			<xsl:if test="$citizen_var or $citizen_var != ''">
				$('#citizen_var').val('<xsl:value-of select="$citizen_var"/>');
			</xsl:if>
		});
		 -->
		<xsl:call-template name="SELECT_SCRIPT"/>
		
		function submitOrder(url) {
			$('.birth-date').each(function() {
				var birth = $(this);
				var date = birth.find('.days').val() + '.' + birth.find('.months').val() + '.' + birth.find('.years').val();
				birth.find('input').val(date);
			});
			$('#order_forms').attr('action', url);
			$('#order_forms').submit();
		}
	</script>
	</xsl:template>

</xsl:stylesheet>