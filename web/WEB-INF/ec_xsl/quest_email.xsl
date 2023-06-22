<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	
	<xsl:import href="utils_inc.xsl" />
	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="country_domain" select="('РФ', 'РБ', 'Иное', '', '')"/>
	<xsl:variable name="first_domain" select="('Да', 'Нет', '', '', '')"/>
	<xsl:variable name="sex_domain" select="('Мужской', 'Женский', '', '', '')"/>
	<xsl:variable name="age_domain" select="('до 25 лет', '25—35', '36–45', '46–55', 'старше 55')"/>
	<xsl:variable name="how_domain" select="('Турагентство', 'ОАО «АСБ Беларусбанк»', 'Отдел маркетинга санатория')"/>
	<xsl:variable name="number_domain" select="(10,9,8,7,6,5,4,3,2,1,0)"/>

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html lang="ru">
			<head>
				<meta charset="UTF-8" />
				<meta name="viewport" content="width=device-width, initial-scale=1.0" />
				<title>Sansputnik form</title>
				<link rel="stylesheet" href="/css/app.css" />
				<link rel="stylesheet" href="/css/styles.css" />

			</head>
				<style >
					.wrap p.lead {
					  font-size: 18px;
					  line-height: 1.4; }
					
					.wrap table {
					  font-size: 14px;
					  border-collapse: collapse;
					  border: none;
					  width: 100%; }
					  .wrap table + p {
						margin-top: 10px; }
					  .wrap table.ten label {
						font-size: 13px;
						color: gray; }
					
					.wrap td, .wrap th {
					  border: solid 1px #d8d8d8;
					  padding: 5px; }
					
					.wrap th {
					  font-weight: bold; }
					
					.wrap label {
					  font-weight: normal;
					  margin-left: 5px; }
					
					.wrap textarea {
					  width: 100%;
					  height: 100px;
					  padding: 10px; }
					
					.wrap h4 {
					  margin-top: 50px; }
					  .wrap h4:first-child {
						margin-top: 0; }
					
					.wrap.email {
					  max-width: 850px;
					  margin: 50px; }
					  .wrap.email .selected, .wrap.email td.sel div, .wrap.email th.sel div, .wrap.email th.sel {
						font-weight: bold;
						color: #000; }
					  .wrap.email .dimmed, .wrap.email td div, .wrap.email th div, .wrap.email th {
						font-weight: normal;
						color: #bdbdbd; }
					</style>
			<body>
				<div class="wrap email" style="max-width: 850px;margin: 50px;">
					<table class="highlight" style="font-size: 14px;border-collapse: collapse;border: none;width: 100%;">
						<xsl:call-template name="TEXT_PARAM">
							<xsl:with-param name="caption" select="'Ваше гражданство'"/>
							<xsl:with-param name="domain" select="$country_domain"/>
							<xsl:with-param name="value" select="page/variables/country"/>
						</xsl:call-template>
						<xsl:call-template name="YN_PARAM">
							<xsl:with-param name="caption" select="'Это Ваш первый визит в наш санаторий?'"/>
							<xsl:with-param name="domain" select="$first_domain"/>
							<xsl:with-param name="value" select="page/variables/first"/>
						</xsl:call-template>
						<xsl:call-template name="YN_PARAM">
							<xsl:with-param name="caption" select="'Ваш пол'"/>
							<xsl:with-param name="domain" select="$sex_domain"/>
							<xsl:with-param name="value" select="page/variables/sex"/>
						</xsl:call-template>
						<xsl:call-template name="TEXT_PARAM">
							<xsl:with-param name="caption" select="'Ваш возраст'"/>
							<xsl:with-param name="domain" select="$age_domain"/>
							<xsl:with-param name="value" select="page/variables/age"/>
						</xsl:call-template>
					</table>
					<h4 style="margin-top: 50px;">1. Каким образом Вы приобретали путёвку? (напротив верного варианта поставить галочку)</h4>
					<table style="font-size: 14px;border-collapse: collapse;border: none;width: 100%;">
						<xsl:call-template name="TR_TEXT_PARAM">
							<xsl:with-param name="domain" select="$how_domain"/>
							<xsl:with-param name="value" select="page/variables/purchase_type"/>
						</xsl:call-template>
					</table>
					<xsl:if test="not($how_domain = page/variables/purchase_type)">
						<p>Иное:</p>
						<p>
							<xsl:value-of select="page/variables/purchase_type" disable-output-escaping="yes"/>
						</p>
					</xsl:if>
					<h4 style="margin-top: 50px;">2. Оцените следующие параметры по 11-тибальной шкале:</h4>
					<table class="highlight ten" style="font-size: 14px;border-collapse: collapse;border: none;width: 100%;">
						<tr>
							<th style="border: solid 1px #d8d8d8;padding: 5px;font-weight: normal;color: #bdbdbd;"></th>
							<xsl:for-each select="$number_domain">
								<th style="border: solid 1px #d8d8d8;padding: 5px;font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></th>
							</xsl:for-each>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество обслуживания при бронировании'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/booking"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество обслуживания в регистратуре'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/reception"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'работа службы охраны'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/security"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'уровень комфорта и оснащение номера'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/comfort"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество уборка номера'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/cleaning"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество Wi-fi'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/wifi"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'состояние территории санатория'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/territory"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'прием врача'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/doctor"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество обслуживания на медицинских процедурах'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/treatment"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'ассортимент оказываемых в санатории медицинских услуг'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/treatment_variety"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество обслуживания в столовой'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/dinner_serv"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество приготовления блюд'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/cooking"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'разнообразие предлагаемого меню'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/menu"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'разнообразие предлагаемых мероприятий'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/many_tusa"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'качество проведения мероприятий для досуга'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/good_tusa"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'уровень обслуживания в кафе (баре)'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/bar"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'ассортимент в кафе (баре)'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/bar_menu"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'уровень обслуживания в СПА-центре'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/spa"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'перечень предлагаемых услуг в СПА-центре'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/spa_menu"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'организация работы магазина'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/shop"/>
							</xsl:call-template>
							<xsl:call-template name="NUMBER_PARAM">
								<xsl:with-param name="caption" select="'организация работы спортивного комплекса'"/>
								<xsl:with-param name="domain" select="$number_domain"/>
								<xsl:with-param name="value" select="page/variables/sport"/>
							</xsl:call-template>
						</tr>
					</table>
					<h4 style="margin-top: 50px;">3. Как в целом Вы можете оценить впечатления от пребывания в санатории по 11-ти бальной шкале?</h4>
					<table style="font-size: 14px;border-collapse: collapse;border: none;width: 100%;">
						<tr>
							<xsl:variable name="all" select="page/variables/all"/>
							<xsl:for-each select="$number_domain">
								<xsl:if test="not($all != '') or f:num($all) != .">
									<th style="border: solid 1px #d8d8d8;padding: 5px;font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></th>
								</xsl:if>
								<xsl:if test="$all != '' and f:num($all) = .">
									<th class="sel" style="border: solid 1px #d8d8d8;padding: 5px;font-weight: bold;color: #000;"><xsl:value-of select="."/></th>
								</xsl:if>
							</xsl:for-each>
						</tr>
					</table>
					<h4 style="margin-top: 50px;">4. Какова вероятность того, что Вы порекомендуете «Спутник» друзьям (знакомым, коллегам) по 11-ти бальной шкале от 0 (ни в коем случае) до 10 (обязательно порекомендую)?</h4>
					<xsl:variable name="recommend" select="page/variables/recommend"/>
					<table style="font-size: 14px;border-collapse: collapse;border: none;width: 100%;">
						<tr>
							<xsl:for-each select="$number_domain">
								<xsl:if test="not($recommend != '') or f:num($recommend) != .">
									<th style="border: solid 1px #d8d8d8;padding: 5px;font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></th>
								</xsl:if>
								<xsl:if test="$recommend != '' and f:num($recommend) = .">
									<th class="sel" style="border: solid 1px #d8d8d8;padding: 5px;font-weight: bold;color: #000;"><xsl:value-of select="."/></th>
								</xsl:if>
							</xsl:for-each>
						</tr>
					</table>
					<xsl:variable name="rec" select="f:num($recommend)"/>
					<xsl:choose>
					<xsl:when test="$rec = 10 or $rec = 9">
						<h4 style="margin-top: 50px;">Назовите главную причину, по которой Вы готовы порекомендовать наш санаторий:</h4>
					</xsl:when>
					<xsl:when test="rec = 8 or $rec = 7">
						<h4 style="margin-top: 50px;">Чего Вам не хватило в работе санатория Спутник, чтобы поставить более высокую оценку?:</h4>
					</xsl:when>
					<xsl:otherwise>
						<h4 style="margin-top: 50px;">Вы поставили низкую оценку. Поясните, пожалуйста, почему. Нам важно Ваше мнение:</h4>
					</xsl:otherwise>
					</xsl:choose>
					<p>
						<xsl:value-of select="page/variables/reason" disable-output-escaping="yes"/>
					</p>
					<h4 style="margin-top: 50px;">5. Ваши пожелания и предложения:</h4>
					<p>
						<xsl:value-of select="page/variables/comment" disable-output-escaping="yes"/>
					</p>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="NUMBER_PARAM">
		<xsl:param name="caption"/>
		<xsl:param name="domain"/>
		<xsl:param name="value"/>
		<tr>
			<td style="border: solid 1px #d8d8d8;padding: 5px;">
				<xsl:value-of select="$caption"/>
			</td>
			<xsl:for-each select="$domain">
				<xsl:if test="f:num($value) != . or not($value != '')">
					<td  style="border: solid 1px #d8d8d8;padding: 5px;"></td>
				</xsl:if>
				<xsl:if test="f:num($value) = . and $value != ''">
					<td class="sel"  style="border: solid 1px #d8d8d8;padding: 5px;">
						<div style="font-weight: bold;color: #000;"><xsl:value-of select="$value"/></div>
					</td>
				</xsl:if>
			</xsl:for-each>
		</tr>
		
	</xsl:template>

	<xsl:template name="TEXT_PARAM">
		<xsl:param name="caption"/>
		<xsl:param name="domain"/>
		<xsl:param name="value"/>
		
		<tr>
			<xsl:if test="$caption != ''">
				<td style="border: solid 1px #d8d8d8;padding: 5px;">
					<xsl:value-of select="$caption"/>
				</td>
			</xsl:if>
			<xsl:for-each select="$domain">
				<xsl:if test="lower-case(.) = lower-case($value)">
					<td class="sel" style="border: solid 1px #d8d8d8;padding: 5px;">
						<div style="font-weight: bold;color: #000;"><xsl:value-of select="."/></div>
					</td>
				</xsl:if>
				<xsl:if test="not(lower-case(.) = lower-case($value))">
					<td style="border: solid 1px #d8d8d8;padding: 5px;">
						<div style="font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></div>
					</td>
				</xsl:if>
			</xsl:for-each>
		</tr>
	</xsl:template>
	
	
	<xsl:template name="TR_TEXT_PARAM">
		<xsl:param name="domain"/>
		<xsl:param name="value"/>
		
		
			<xsl:for-each select="$domain">
				<xsl:if test="lower-case(.) = lower-case($value)">
					<tr >
						<td class="sel"  style="border: solid 1px #d8d8d8;padding: 5px;">
							<div  style="font-weight: bold;color: #000;"><xsl:value-of select="."/></div>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="not(lower-case(.) = lower-case($value))">
					<tr>
						<td  style="border: solid 1px #d8d8d8;padding: 5px;">
							<div style="font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></div>
						</td>
					</tr>
				</xsl:if>
			</xsl:for-each>
		
	</xsl:template>
	
	<xsl:template name="YN_PARAM">
		<xsl:param name="caption"/>
		<xsl:param name="domain"/>
		<xsl:param name="value"/>
		
		<xsl:variable name="v" select="$domain[2 - f:num($value)]"/>
		<tr>
			<td style="border: solid 1px #d8d8d8;padding: 5px;">
				<xsl:value-of select="$caption"/>
			</td>
			<xsl:for-each select="$domain">
				<xsl:if test="lower-case(.) = lower-case($v)">
					<td class="sel" style="border: solid 1px #d8d8d8;padding: 5px;">
						<div  style="font-weight: bold;color: #000;"><xsl:value-of select="."/></div>
					</td>
				</xsl:if>
				<xsl:if test="not(lower-case(.) = lower-case($v))">
					<td style="border: solid 1px #d8d8d8;padding: 5px;">
						<div style="font-weight: normal;color: #bdbdbd;"><xsl:value-of select="."/></div>
					</td>
				</xsl:if>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
		</xsl:text>
	</xsl:template>
</xsl:stylesheet>