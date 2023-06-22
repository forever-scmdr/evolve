<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="../utils_inc.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />


	<xsl:variable name="o" select="page/order"/>
	<xsl:variable name="main_form" select="$o/order_form[@id = $o/main_form]"/>
	<xsl:variable name="kid_form" select="$o/order_form[person_type = 'Ребенок']"/>
	<xsl:variable name="adult_form" select="$o/order_form[person_type = 'Взрослый' and @id != $main_form/@id]"/>
	<xsl:variable name="new_form" select="$o/order_form[not(person_type) or (person_type != 'Взрослый' and person_type != 'Ребенок')]"/>


	<xsl:variable name="cart" select="page"/>
	<xsl:variable name="message" select="page"/>

	<xsl:template match="/">
	<html xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<style>
			td{
			font: normal 12px Verdana;
			}
			.st1, td.st1{
			color: Black;
			background: Silver;
			font: bold 12px Verdana;
			white-space: nowrap;
			}
			.st2, td.st2{
			color: Black;
			background: #DCDCDC;
			font: bold 12px Verdana;
			}
			.st3, td.st3{
			color: Black;
			background: #DCDCDC;
			font: normal 12px Verdana;
			}
			.stdata, td.stdata{
			background: #DCDCDC;
			color: #262626;
			font: normal 12px Verdana;
			white-space: nowrap;
			}
			span.data{
			background: #DCDCDC;
			color: #262626;
			color: Black;
			font: normal 12px Verdana;
			}
			tr td:first-child {
			white-space: normal;
			}
		</style>
	</head>
	<body>
		<table width="700" cellspacing="2" cellpadding="0" align="center"><!--  bgcolor="#C0C0C0"> -->
			<tbody>
				<tr>
					<td colspan="10" align="left">
						<p>
						Ваша бронь подтверждена, спасибо что выбрали санаторий "Спутник"!
						</p>
						<p>
						Для оплаты банковской картой онлайн перейдите по ссылке ниже.
						</p>
						<p>
						<a href="http://sansputnik.by/{page/process_link}">ссылка для онлайн оплаты</a>
						</p>
						<p>
						Вы можете оплатить услуги санатория в отделении банка, для этого к письму приложена счет-фактура. 
						Договор на оказание услуг санаторием также приложен к этому письму.
						</p>
						<p>
						Если у администратора возникунут вопросы, он свяжется с вами по тел.: <xsl:value-of select="$main_form/phone"/>
						</p>
						<br/>
					</td>
				</tr>
				<tr>
					<td bgcolor="#ffffff">
						<table width="700" cellspacing="2" cellpadding="5" align="center">
							<tbody>
								<tr>
									<td class="st2">
										Номер договора: <span class="data"><xsl:value-of select="$o/num"/></span>
									</td>
									<td class="st2" colspan="9">
										Дата заказа: <xsl:value-of select="$o/received_date"/>
									</td>
								</tr>
								<!-- 
								<tr>
									<td class="st1" colspan="10" align="center">
										<strong>Данные клиента</strong>
									</td>
								</tr>
								<xsl:if test="$cart/file = '' or not($cart/file)">
									<tr>
										<td class="st2" colspan="4" align="right">Заказчик</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/enterprise"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Телефон</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/phone"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Контактное лицо</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/contact"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Телефон</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/phone"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Юридический адрес</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/j_address"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Адрес доставки</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/s_address"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Расчетный счет</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/account"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">Банк</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/bank"/></td>
									</tr>
									<tr>
										<td class="st2" colspan="4" align="right">УНП</td>
										<td class="st3" colspan="6" align="left"><xsl:value-of select="$cart/unp"/></td>
									</tr>
								</xsl:if>
								<xsl:if test="$cart/file != ''">
									<tr>
										<td class="st2" colspan="10" align="right">Контактные данные находятся в прикрепленном файле.</td>
									</tr>						
								</xsl:if>
								
								 -->
							<!-- конец данных клиента -->	
							
								<tr>
									<td class="st1" colspan="4" align="center">
										<strong>Что заказано</strong>
									</td>
								</tr>
								
							<!-- товары -->
								<tr>
									<td class="st2" colspan="">Номер</td>
									<td class="st2">Даты</td>
									<td class="st2">Основные места</td>
									<td class="st2">Дополнительные места</td>
								</tr>
								<xsl:for-each select="$o/free_room">
									<tr>
										<td><xsl:value-of select="type_name"/></td>
										<td>
											<xsl:value-of select="f:day_month_short_string(f:format_date(f:millis_to_date(from)))"/>-
											<xsl:value-of select="f:day_month_short_string(f:format_date(f:millis_to_date(to)))"/>
										</td>
										<td>
											<xsl:for-each select="order_form_base">
												<p>
												<xsl:variable name="form" select="$o/order_form[@id = current()]"/>
												<xsl:value-of select="$form/person_type"/>, <xsl:value-of select="$form/voucher_type"/>
												</p>
											</xsl:for-each>
										</td>
										<td>
											<xsl:if test="not(order_form_extra)">нет</xsl:if>
											<xsl:for-each select="order_form_extra">
												<p>
												<xsl:variable name="form" select="$o/order_form[@id = current()]"/>
												<xsl:value-of select="$form/person_type"/>, <xsl:value-of select="$form/voucher_type"/>
												</p>
											</xsl:for-each>
										</td>
									</tr>
								</xsl:for-each>
								
							<!-- ИТОГО: -->
								<tr>
									<td class="st1" colspan="3">ИТОГО К ОПЛАТЕ</td>
									<td class="st1" align="right"><xsl:value-of select="$o/sum"/></td>
								</tr>
								<!-- 
								<tr>
									<td class="st1" colspan="10" align="left">***Это письмо
										сформировано автоматически, отвечать на него не нужно***</td>
								</tr>
								 -->
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="10" align="left">
						<br/>
						<p>
						Пожалуйста, после проведения платежа банковской картой, сохраняйте полученные карт-чеки (подтверждения оплаты) 
						для сверки с выпиской из карт-счета, с целью подтверждения совершения операции в случае возникновения спорных ситуаций.
						</p>
						<p>
						Условия отмены брони:
						</p>
						<p>
						В случае досрочного отъезда (неприбытия) Заказчика по уважительной причине: смерть или болезнь близких родственников, 
						болезнь  самого отдыхающего, вызов государственными органами, вызов на работу или учебу, 
						производится возврат денежных средств за неиспользованные дни путевки. Вышеназванные факты должны быть подтверждены документально: 
						телеграмма, копия свидетельства о смерти, копия справки или больничного листа, подтверждение вызова на работу или учебу.
						</p>
						Возврат денежных средств производится в течение 10 банковских дней, начиная со дня, 
						следующего за днем подачи письменного заявления Заказчика и копий документов, 
						удостоверяющие уважительность причин согласно требованиям  договора. 
						Вышеназванные документы должны быть представлены Исполнителю Заказчиком не позднее одного месяца со дня отъезда (неприбытия) в санаторий. 
						В случае непредставления Исполнителю вышеуказанных документов в сроки, определенные договором, возврат денежных средств не производится.
						Возврат денежных средств производится за вычетом комиссионного вознаграждения причитающегося  банку на ту карту, 
						с которой была совершена оплата.
						В случаях, не предусмотренных  договором, возврат денежных средств Заказчику не производится.
						<p>
						Исполнитель  не несет ответственность за отмену бронирования, сделанную в устной форме.
						В случае возврата средств фактическое зачисление денежных средств на счет банковской карты 
						Заказчика может занимать до 30 дней в зависимости от правил и условий, межбанковских процессинговых центров и банков, 
						участвующих в данной операции. Возврат осуществляется на ту же карту, с которой была произведена оплата!
						</p>
					</td>
				</tr>
			</tbody>
		</table>
	</body>
	</html>
	</xsl:template>



	<xsl:template match="order_form" mode="select">
		<xsl:if test="(not(pay_only) or pay_only = '0') and person_type and person_type != ''">
			<option value="{@id}"><xsl:value-of select="substring(person_type, 1, 3)"/>: <xsl:value-of select="first_name"/>&#160;<xsl:value-of select="last_name"/></option>
		</xsl:if>
	</xsl:template>


	<xsl:template name="CONTENT">
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
								<h5>Дополнительные места</h5>
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

</xsl:stylesheet>