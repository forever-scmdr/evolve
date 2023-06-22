<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />


	<xsl:variable name="w" select="page/wellness_form"/>

	<xsl:template match="/">
	<html lang="en">
	
	<head>
		<meta charset="utf-8"/>
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<title>Персональная веллнес-программа</title>
	</head>
	
	<body style="background-color: #E6E6E6; margin: 0;">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="center" style="background-color: #E6E6E6;">
					<div style="max-width: 600px; width: 600px; margin: auto; background-color: #fff; font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 13px;">
						<!-- начало -->
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td style="padding: 0 15px;">
									<h1 style="margin-top: 30px;">Здравствуйте!</h1>
									<p style="font-size: 16px; line-height: 1.4em; margin-top: 1em;">
										Спасибо, что сформировали веллнес-программу на нашем сайте <a href="http://sansputnik.by">sansputnik.by</a>
									</p>
									<p style="font-size: 16px; line-height: 1.4em; margin-top: 1em;">
										Если возникнут вопросы, наши сотрудники свяжутся с Вами для уточнения деталей по телефону в течение двух дней.
									</p>
									<h2>Веллнес-программа</h2>
									<p style="font-size: 16px; line-height: 1.4em; margin-top: 1em;">
										Продолжительность путевки — <xsl:value-of select="$w/extra[@input='days']"/> дней.
									</p>
									<table style="font-size: 14px; border-width: 1px; border-style: solid; border-color: #bababa; border-collapse: collapse;" border="1" cellpadding="0" cellspacing="0">
										<tr>
											<td style="padding: 5px 10px;"><strong>Дни</strong></td>
											<td style="padding: 5px 10px;"><strong>Название модуля</strong></td>
										</tr>
										<xsl:for-each select="(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39)">
											<xsl:variable name="day" select="."/>
											<xsl:variable name="value" select="$w/extra[@input=concat('day_', $day)]"/>
											<xsl:if test="$value and not($value = '')">
												<tr>
													<td style="padding: 5px 10px;"><xsl:value-of select="$day"/>–<xsl:value-of select="$day + 1"/></td>
													<td style="padding: 5px 10px;"><xsl:value-of select="$value"/></td>
												</tr>
											</xsl:if>
										</xsl:for-each>
									</table>
									<h2>Ваши данные</h2>
									<p style="font-size: 14px; line-height: 1.5em; margin-top: 1em;">
										Ф.И.О.: <xsl:value-of select="$w/first_name"/><br/>
										Дата рождения: <xsl:value-of select="$w/birth_date"/><br/>
										Гражданство: <xsl:value-of select="$w/citizen_name"/><br/>
										Телефон: <xsl:value-of select="$w/phone"/><br/>
										Дата заезда: <xsl:value-of select="$w/check_in_date"/><br/>
										Эл. почта: <a href="mailto:{$w/email}"><xsl:value-of select="$w/email"/></a><br/>
									</p>
									<p style="font-size: 12px; line-height: 1.3em; margin-top: 3em; margin-bottom: 5em; font-style: italic; color: #555;">
										С уважением, отдел маркетинга Унитарного предприятия "АСБ Санаторий Спутник"<br/><br/>
										Республика Беларусь, Минская обл., Мядельский р-н,<br/>
										к.п.Нарочь, ул.Туристская, д.14<br/>
										Телефон: (+37517 97) 28-888, 25-594, 25-592<br/>
										GSM: (+375 29) 358-39-51 (viber/WhatsApp), 166-22-57<br/>
										skype: АСБ Санаторий Спутник<br/><br/>
										<!-- <a href="https://vk.com/asbsansputnik"><img src="http://forever.by/share/sansputnik/11/img/logo_vk.png" alt="" /></a>
										<a href="https://www.facebook.com/asbsansput/"><img src="http://forever.by/share/sansputnik/11/img/logo_fb.png" alt="" /></a>
										<a href="https://www.instagram.com/asbsansputnik/"><img src="http://forever.by/share/sansputnik/11/img/logo_in.png" alt="" /></a>
										<a href="https://twitter.com/SanSputnik"><img src="http://forever.by/share/sansputnik/11/img/logo_tw.png" alt="" /></a> -->
									</p>
								</td>
							</tr>
						</table>
						<!-- конец -->
					</div>
				</td>
			</tr>
		</table>
	</body>
	
	</html>
	</xsl:template>





</xsl:stylesheet>