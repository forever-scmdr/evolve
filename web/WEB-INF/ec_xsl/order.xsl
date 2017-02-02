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
		<div class="form">
			<label>
				Фамилия:
				<input type="text" name="{new_last_name}" value="{last_name}"/>
			</label>
			<label>
				Имя:
				<input type="text" name="{new_first_name}" value="{first_name}" />
			</label>
			<label>
				Отчество:
				<input type="text" name="{new_second_name}" value="{second_name}" />
			</label>
			<label>
				Гражданство:
				<p><xsl:value-of select="$order/citizen_name"/></p>
			</label>
			<div class="birth-date">
				<span>Дата рожденния:</span>
				<input class="inp" type="hidden" name="{new_birth_date}" value="{birth_date}"/>
				<xsl:variable name="birth_date" select="birth_date[. != '']"/>
				<select class="days" value="{if ($birth_date) then f:num(tokenize($birth_date, '\.')[1]) else ''}">
					<xsl:for-each select="1 to 31">
						<option value="{.}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<select class="months" value="{if ($birth_date) then f:num(tokenize($birth_date, '\.')[2]) else ''}">
					<xsl:for-each select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')">
						<option value="{position()}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<select class="years" value="{if ($birth_date) then tokenize($birth_date, '\.')[3] else ''}">
					<xsl:for-each select="year-from-date(current-date()) to 1900">
						<option value="{.}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<label>
				Телефон:
				<input type="text" name="{new_phone}" value="{phone}" />
			</label>
			<label>
				Адрес:
				<input type="text" name="{new_address}" value="{address}" />
			</label>
			<xsl:if test="$main = '1'">
				<label>
					Адрес электронной почты (e-mail):
					<input type="text" name="{new_email}" value="{email}" />
				</label>
			</xsl:if>
			<label>
				Серия и номер паспорта:
				<input type="text" name="{new_passport}" value="{passport}" />
			</label>
<!-- 			<label> -->
<!-- 				Идентификационный номер: -->
<!-- 				<input type="text" name="{new_id}" value="{id}" /> -->
<!-- 			</label> -->
			<div class="birth-date">
				<span>Дата выдачи:</span>
				<input class="inp" type="hidden" name="{new_passport_issued_date}" value="{passport_issued_date}"/>
				<xsl:variable name="issued_date" select="passport_issued_date[. != '']"/>
				<select class="days" value="{if ($issued_date) then f:num(tokenize($issued_date, '\.')[1]) else ''}">
					<xsl:for-each select="1 to 31">
						<option value="{.}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<select class="months" value="{if ($issued_date) then f:num(tokenize($issued_date, '\.')[2]) else ''}">
					<xsl:for-each select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')">
						<option value="{position()}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<select class="years" value="{if ($issued_date) then tokenize($issued_date, '\.')[3] else ''}">
					<xsl:for-each select="1900 to year-from-date(current-date())">
						<option value="{.}"><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<label>
				Кем выдан:
				<input type="text" name="{new_passport_issued}" value="{passport_issued}" />
			</label>
			<xsl:if test="$main = '1' and $is_not_pin">
				<label>
					Оплата:<br/>
<!-- 					<select name="{new_pay_type}" value="{pay_type}"> -->
<!-- 						<option>Оплата и отдых</option> -->
<!-- 						<option>Только оплата</option> -->
<!-- 					</select> -->
					<xsl:choose>
						<xsl:when test="pay_only = '1'">
							Оплата путевки только для иных лиц<br/>
							Изменить: <p/>
							<a style="padding: 6px; 6px;" href="#" class="blue-button" onclick="submitOrder('{set_pay_and_stay}'); return false;">Оплата путевки для себя и иных лиц</a>
						</xsl:when>
						<xsl:otherwise>
							Оплата путевки для себя и иных лиц<br/>
							Изменить: <p/>
							<a href="#" class="blue-button" style="padding: 6px; 6px;" onclick="submitOrder('{set_pay_only}'); return false;">Оплата путевки только для иных лиц</a>
						</xsl:otherwise>
					</xsl:choose>
				</label>
				<input type="hidden" name="{new_is_contractor}" value="1"/>
			</xsl:if>
		</div>
	</xsl:template>
	

	<xsl:template name="CONTENT">
	<div class="common">
		<h1>Выбранные номера и путевки</h1>
		<xsl:if test="$order_error">
			<h3 style="color: red">Введен неверный пин-код. Регистрация невозможна</h3>
			<div style="width: 400px; margin-left:0;">
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
		<xsl:if test="not($order_error)">
			<div class="selected">
				<p>
					<b>Гражданство отдыхающих</b><xsl:text> </xsl:text><xsl:value-of select="$order/citizen_name"/>.
				</p>
			</div>
			<xsl:for-each select="$order/free_room">
				<div class="selected">
					<p>
						<b><xsl:value-of select="type_name"/></b>, 
						<xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> - <xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/>.
						<br/>
						Основные места: 
						<xsl:variable name="base_forms" select="$order//order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')]"/>
						<xsl:for-each select="$base_forms">
							<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
							<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
						</xsl:for-each>
						<br/>
						<xsl:variable name="extra_forms" select="$order//order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')]"/>
						Дополнительные места: 
						<xsl:for-each select="$extra_forms">
							<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
							<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
						</xsl:for-each>
						<xsl:if test="not($extra_forms)">нет.</xsl:if>
					</p>
				</div>
			</xsl:for-each>
			<div class="summ">
				<span><b>К оплате</b>&#160;<xsl:value-of select="$order/sum"/>&#160;<xsl:value-of select="$order/cur"/></span>
				<xsl:if test="$is_not_pin">
					<a href="{page/room_search_link}">Изменить выбор</a>
				</xsl:if>
			</div>
			<div class="personal-forms">
				<form method="post" action="{page/proceed}" id="order_forms">
					<h2>Данные для договора</h2>
					<xsl:if test="$message">
						<div style="margin-top: 10px">
							<xsl:for-each select="$message">
								<p><span class="error"><xsl:value-of select="."/></span></p>
							</xsl:for-each>
						</div>
					</xsl:if>
					<div class="form-wrap">
						<h3>С кем заключается договор</h3>
						<xsl:apply-templates select="$main_form">
							<xsl:with-param name="main" select="'1'"/>
						</xsl:apply-templates>
					</div>
					<xsl:if test="$adult_form">
						<div class="form-wrap">
							<h3>Сопровождающие взрослые (<xsl:value-of select="count($adult_form)"/>)</h3>
							<xsl:for-each select="$adult_form">
								<xsl:apply-templates select=".">
									<xsl:with-param name="main" select="'0'"/>
								</xsl:apply-templates>
								<xsl:if test="position() != last()"><div class="sep"></div></xsl:if>
							</xsl:for-each>
						</div>
					</xsl:if>
					<xsl:if test="$kid_form">
						<div class="form-wrap">
							<h3>Сопровождающие дети (<xsl:value-of select="count($kid_form)"/>)</h3>
							<xsl:for-each select="$kid_form">
								<xsl:apply-templates select=".">
									<xsl:with-param name="main" select="'0'"/>
								</xsl:apply-templates>
								<xsl:if test="position() != last()"><div class="sep"></div></xsl:if>
							</xsl:for-each>
						</div>
					</xsl:if>
					<div class="clear"/>
					<br/>
					<label>
						<xsl:variable name="b" select="page/booking"/>
						Я ознакомлен(а) с договором (<a href="{$b/@path}{$b/contract}" download="{$b/@path}{$b/contract}">скачать договор</a>):
						<input type="checkbox" name="{$cart_order/new_contract_agreed}" value="1">
							<xsl:if test="$cart_order/contract_agreed = '1'"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
						</input>
					</label>
				</form>
			</div>
			<div class="submit-container">
				<a href="#" class="submit" onclick="submitOrder('{$order/proceed}'); return false;">Забронировать</a>
			</div>
		</xsl:if>
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