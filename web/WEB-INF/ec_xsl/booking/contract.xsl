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


	<xsl:variable name="o" select="page/order"/>
	<xsl:variable name="main_form" select="$o/order_form[@id = $o/main_form]"/>
	<xsl:variable name="kid_form" select="$o/order_form[person_type = 'Ребенок']"/>
	<xsl:variable name="adult_form" select="$o/order_form[person_type = 'Взрослый' and not(@id = $main_form/@id)]"/>
	<xsl:variable name="new_form" select="$o/order_form[not(person_type) or (person_type != 'Взрослый' and person_type != 'Ребенок')]"/>
	
	<xsl:variable name="message" select="page/variables/message"/>

	<xsl:template match="order_form">
		<xsl:param name="main"/><!-- 1 или 0 -->
		<div class="form">
			<label>
				Фамилия:
				<input type="text" name="{new_last_name}" value="{last_name}"/>
			</label>
			<xsl:if test="$main != '1'">
				<a class="cross-button" onclick="submitOrder('{delete}')">Удалить</a>
			</xsl:if>
			<label>
				Имя:
				<input type="text" name="{new_first_name}" value="{first_name}" />
			</label>
			<label>
				Отчество:
				<input type="text" name="{new_second_name}" value="{second_name}" />
			</label>
			<label>
				Гражданство (для цены):
				<select name="{new_citizen}" value="{citizen}">
					<option value="РБ">Беларусь</option>
					<option value="ЕАЭС">Россия, Казахстан</option>
					<option value="Другое">Другое</option>
				</select>
			</label>
			<label>
				Гражданство:
				<input type="text" name="{new_citizen_name}" value="{citizen_name}"/>
			</label>
			<label>
				Возраст:
				<select name="{new_person_type}" value="{person_type}">
					<option value="">-выбрать-</option>
					<option value="Взрослый">Взрослый</option>
					<option value="Ребенок">Ребенок</option>
				</select>
			</label>
			<label>
				Путевка:
				<select name="{new_voucher_type}" value="{voucher_type}">
					<option value="Санаторно-курортная">Санаторно-курортная</option>
					<option value="Оздоровительная">Оздоровительная</option>
				</select>
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
			<xsl:if test="$main = '1'">
				<label>
					Оплата:
					<select name="{new_pay_only}" value="{pay_only}">
						<option value="0">Оплата и отдых</option>
						<option value="1">Только оплата</option>
					</select>
				</label>
				<input type="hidden" name="{new_is_contractor}" value="1"/>
			</xsl:if>
			<xsl:if test="not(person_type = 'Ребенок') and $main != '1'">
				<a class="blue-button" onclick="submitOrder('{set_main}')" style="margin-right: 0px">Сделать контрагентом</a>
				<br/>
			</xsl:if>
		</div>
	</xsl:template>


<!-- 	<xsl:template match="order_form" mode="select"> -->
<!-- 	<select style="width: 120px;" value="{person_type}" name="{new_person_type}"> -->
<!-- 		<option value="">-Не занято-</option> -->
<!-- 		<option value="Взрослый">Взрослый</option> -->
<!-- 		<option value="Ребенок">Ребенок</option> -->
<!-- 	</select> -->
<!-- 	</xsl:template> -->

	<xsl:template match="order_form" mode="select">
		<xsl:if test="(not(pay_only) or pay_only = '0') and person_type and person_type != ''">
			<option value="{@id}"><xsl:value-of select="substring(person_type, 1, 3)"/>: <xsl:value-of select="first_name"/>&#160;<xsl:value-of select="last_name"/></option>
		</xsl:if>
	</xsl:template>


	<xsl:template name="CONTENT">
	<a href="{page/back_link}" style="position: relative; top: 20px; font-size: 14px;">Назад к списку заявок</a>
	<h1>Редактирование заявки</h1>
	<xsl:if test="$message">
		<div style="margin-top: 10px">
			<xsl:for-each select="$message">
				<p><span style="color: red"><xsl:value-of select="."/></span></p>
			</xsl:for-each>
		</div>
	</xsl:if>
	<form method="post" action="{page/manage_order}" id="order_forms">
		<div class="ilb w-500 form-wrap">
			<div class="ilb ">
				<table class="ddd">
					<tr>
						<td>
							<label>Номер договора
								<input type="text" name="{$o/new_num}" value="{$o/num}"/>
							</label>
						</td>
						<td>
							<label>ПИН код
								<input type="text" name="{$o/new_pin}" value="{$o/pin}"/>
							</label>
						</td>
					</tr>
				</table>
	
				<div class="birth-date">
					<span>Оплата до:</span>
					<input type="text" class="datepicker" name="{$o/new_pay_until_date}" style="display: inline-block"
						value="{if ($o/pay_until_date and $o/pay_until_date != '') then f:format_date(f:xsl_date($o/pay_until_date)) else ''}"
						min-date="{f:format_date(current-date())}"/>
					<xsl:if test="$o/pay_until_date and $o/pay_until_date != ''">
						<span class="total"><xsl:value-of select="days-from-duration(f:xsl_date($o/pay_until_date) - current-date())"/> дней</span>
					</xsl:if>
				</div>
	
				<label>
					Примечане:
					<textarea name="{$o/new_extra}"><xsl:value-of select="$o/extra"/></textarea>
				</label>
			</div>
	
			<div class="ilb order-room-list" >
				<xsl:if test="$o/status = ('2', '3')">
				<div class="downloadFiles" style="padding-bottom: 20px;">
					<a href="{$o/@path}{$o/contract}" download="" style="padding-right: 10px;">Скачать договор</a>
					<a href="{$o/@path}{$o/bill}" download="">Скачать счёт-фактуру</a>
				</div>
				</xsl:if>
				<p><b><xsl:value-of select="$o/sum"/>&#160;<xsl:value-of select="$o/cur"/></b></p>
				<p><xsl:value-of select="count($o/order_form[person_type = 'Взрослый' and (not(pay_only) or pay_only != '1')])"/> взрослых</p>
				<p><xsl:value-of select="count($o/order_form[person_type = 'Ребенок'])"/> детей</p>
				<xsl:for-each select="$o/free_room">
					<xsl:variable name="base_count" select="count($o/order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')])"/>
					<xsl:variable name="extra_count" select="count($o/order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')])"/>
					<p class="r">
						<xsl:value-of select="type_name"/>(<xsl:value-of select="$base_count"/>+<xsl:value-of select="$extra_count"/>)
						<xsl:if test="from and to">
							<span><xsl:value-of 
										select="f:day_month_short_string(f:format_date(f:millis_to_date(from)))"/>-<xsl:value-of 
											select="f:day_month_short_string(f:format_date(f:millis_to_date(to)))"/></span>
						</xsl:if>
					</p>
				</xsl:for-each>
			</div>
		</div>
		<div class="ilb confirm">
			<xsl:if test="$o/status = '1'">
				<a onclick="submitOrder('{page/confirm}')" class="submit">Подтвердить заявку</a>
				<p>Клиенту будет отправлена ссылка на оплату договора и счет-фактура.</p>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$o/status = '1'"><h2>Заявка НЕ подтверждена</h2></xsl:when>
				<xsl:when test="$o/status = '2'"><h2>Заявка подтверждена. Ожидание оплаты</h2></xsl:when>
				<xsl:when test="$o/status = '3'"><h2>Заявка оплачена</h2></xsl:when>
				<xsl:otherwise><h2>Новая заявка</h2></xsl:otherwise>
			</xsl:choose>
			<a onclick="submitOrder('{page/save}')" class="blue-button">Сохранить изменения</a>
			<xsl:if test="$o/status = '2'">
				<a onclick="submitOrder('{page/send_docs}')" class="blue-button">Отправить документы повторно</a>
			</xsl:if>
			<xsl:if test="$o/status = '2'">
				<a onclick="submitOrder('{page/set_paid}')" class="blue-button">Подтвердить оплату</a>
			</xsl:if>
			<xsl:if test="$o/status = '3'">
				<a onclick="submitOrder('{page/set_not_paid}')" class="blue-button">Отменить оплату</a>
			</xsl:if>
			<a onclick="confirmLink('{page/delete}', 'Вы действительно хотите удалить заявку?')" class="blue-button">Удалить заявку</a>
		</div>

		<div class="order-room">
			<table style="width: 100%">
				<tr>
					<td style="width: 47%"><h2>Номера</h2></td>
					<td>
						<select name="{new_type}" class="room_sel_select" style="vertical-align: middle" id="room_type">
							<xsl:for-each select="//page/rooms/type">
								<option value="{@id}"><xsl:value-of select="name"/></option>
							</xsl:for-each>
						</select>
						<a class="blue-button" style="vertical-align: middle; margin-left: 3px" 
							onclick="submitOrder(addVariableToUrl('{page/add_room}', 'room_type', $('#room_type').val()))">Добавить номер</a>
					</td>
				</tr>
			</table>
			<xsl:for-each select="$o/free_room">
				<xsl:variable name="type" select="//page/rooms/type[@id = current()/type]"/>
				<xsl:variable name="room" select="."/>
				<div class="room-sel">
					<table>
						<tbody>
							<tr>
								<td style="width: 187px; text-align: right;">
									<p>&#160;</p>
									<p style="font-weight: bold; color: black; font-size: 12px"><xsl:value-of select="type_name"/></p>
								</td>
								<td>
									№<xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
									<input name="{new_num}" class="nmbr" type="text" value="{num}"/>
								</td>
								<td>
									&#160;
									<a onclick="submitOrder('{delete}')" class="cross-button">Удалить</a>
								</td>
							</tr>
						</tbody>
					</table>
					<div>
						<div id="in-out-1" >
							<label style="display: inline-block; margin-right: 20px; margin-top: 10px;">
								Дата заезда
								<input type="text" style="margin-top: 4px;" class="datepicker" id="date_from_{@id}"
									value="{if (from and from != '') then f:format_date(f:millis_to_date(from)) else ''}"
									onchange="setMillis('date_from_{@id}', 'date_from_millis_{@id}')"/>
								<input type="hidden" id="date_from_millis_{@id}" name="{new_from}" value="{from}"/>
							</label>
							<label style="display: inline-block; margin-top: 10px;">
								Дата выезада
								<input type="text" style="margin-top: 4px;" class="datepicker" id="date_to_{@id}"
									value="{if (to and to != '') then f:format_date(f:millis_to_date(to)) else ''}"
									onchange="setMillis('date_to_{@id}', 'date_to_millis_{@id}')"/>
								<input type="hidden" id="date_to_millis_{@id}" name="{new_to}" value="{to}"/>
							</label>
						</div>
					</div>
					<table>
						<tr>
							<td>
								<h5>Основные места</h5>
								<xsl:for-each select="1 to $type/base_beds">
									<xsl:variable name="index" select="."/>
									<select style="width: 120px;" name="{$room/new_order_form_base}" value="{$room/order_form_base[$index]}">
										<option value="">-не занято-</option>
										<xsl:apply-templates select="$o/order_form" mode="select"/>
									</select>
								</xsl:for-each>
<!-- 								<xsl:apply-templates select="$o/order_form[@id = current()/order_form_base]" mode="select"/> -->
							</td>
							<td style="padding-left: 10px;">
								<h5>Доп. места</h5>
								<xsl:for-each select="1 to $type/extra_beds">
									<xsl:variable name="index" select="."/>
									<select style="width: 120px;" name="{$room/new_order_form_extra}" value="{$room/order_form_extra[$index]}">
										<option value="">-не занято-</option>
										<xsl:apply-templates select="$o/order_form" mode="select"/>
									</select>
								</xsl:for-each>
<!-- 								<xsl:apply-templates select="$o/order_form[@id = current()/order_form_extra]" mode="select"/> -->
							</td>
						</tr>
					</table>
				</div>
			</xsl:for-each>
			<div class="clear"/>
			<a class="blue-button" onclick="submitOrder('{page/save}')" style="margin-top: 10px;">Сохранить</a>
			<a class="blue-button" onclick="submitOrder('{page/add_form}')" style="margin-top: 10px;">Добавить форму</a>
		</div>
		<div class="personal-forms">
			<h2>Анкета</h2>
			<div class="form-wrap">
				<h3>С кем заключается договор</h3>
				<xsl:apply-templates select="$main_form">
					<xsl:with-param name="main" select="'1'"/>
				</xsl:apply-templates>
			</div>
			<div class="form-wrap">
				<xsl:if test="$new_form">
					<h3>Новая форма</h3>
					<xsl:for-each select="$new_form">
						<xsl:apply-templates select=".">
							<xsl:with-param name="main" select="'0'"/>
						</xsl:apply-templates>
						<xsl:if test="position() != last()"><div class="sep"></div></xsl:if>
					</xsl:for-each>
				</xsl:if>
				<h3>Сопровождающие взрослые (<xsl:value-of select="count($adult_form)"/>)</h3>
				<xsl:for-each select="$adult_form">
					<xsl:apply-templates select=".">
						<xsl:with-param name="main" select="'0'"/>
					</xsl:apply-templates>
					<xsl:if test="position() != last()"><div class="sep"></div></xsl:if>
				</xsl:for-each>
			</div>
			<div class="form-wrap">
				<h3>Сопровождающие дети (<xsl:value-of select="count($kid_form)"/>)</h3>
				<xsl:for-each select="$kid_form">
					<xsl:apply-templates select=".">
						<xsl:with-param name="main" select="'0'"/>
					</xsl:apply-templates>
					<xsl:if test="position() != last()"><div class="sep"></div></xsl:if>
				</xsl:for-each>
			</div>
		</div>
	</form>
	</xsl:template>


	<xsl:template name="SCRIPTS">
	<script>
		<xsl:call-template name="SELECT_SCRIPT"/>
		function submitOrder(url) {
			$('.birth-date').each(function() {
				var birth = $(this);
				if (birth.find('.days').length == 0)
					return;
				var date = birth.find('.days').val() + '.' + birth.find('.months').val() + '.' + birth.find('.years').val();
				birth.find('input').val(date);
			});
			$('#order_forms').attr('action', url);
			$('#order_forms').submit();
		}

		function setCitizen(select) {
			var citizen = select.val();
			var citizenInp = select.closest('label').find('input').first();
			if ('Беларусь'.indexOf(citizen) != -1)
				citizenInp.val('РБ');
			else if ('Россия, Казахстан, Армения'.indexOf(citizen) != -1)
				citizenInp.val('ЕАЭС');
			else 
				citizenInp.val(citizen);
		}
		
		function setMillis(stringInp, millisInp) {
			var parts = $('#' + stringInp).val().split('.');
			if (parts.length != 3)
				return;
			$('#' + millisInp).val(Date.parse(parts[2] + '-' + parts[1] + '-' + parts[0]));
		}
	</script>
	</xsl:template>

</xsl:stylesheet>