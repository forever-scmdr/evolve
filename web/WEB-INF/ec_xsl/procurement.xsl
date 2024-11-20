<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="agent_domains.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<!-- ****************************    ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->

<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:template>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="agent" select="page/agent"/>

	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml" xmlns:f="f:f">
		<head>
			<base href="{page/base}"/>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<meta charset="utf-8" />
			<title>Контрагенты</title>
			<link rel="stylesheet" type="text/css" href="intra_css/styles.css" />
		</head>
		<body>
			<div class="header">
				<div class="headerContainer">
					<div class="user">Администратор <input type="submit" value="Выход" class="buttonSubmit"/></div>
					<a href=""><img src="intra_img/termo_logo.jpg" alt=""/></a>
				</div>
			</div>
			<div class="mainContainer">
				<div class="path"><a href="{page/to_agents}">Назад к списку контрагентов</a></div>
				<h1 class="pageTitle"><xsl:value-of select="$agent/organization"/></h1>
				<div class="agentInfo">
					<p><xsl:value-of select="$agent/country"/>, <xsl:value-of select="$agent/region"/>, <xsl:value-of select="$agent/city"/>, <xsl:value-of select="$agent/address"/></p>
					<p><xsl:value-of select="$agent/contact_name"/></p>
					<p><xsl:value-of select="$agent/phone"/>&#160;<xsl:value-of select="$agent/email"/>&#160;<xsl:value-of select="$agent/site"/></p>
				</div>
				<table style="width: 100%;">
					<tr>
						<th>Дата</th>
						<th>Заказ</th>
					</tr>
					<xsl:for-each select="$agent/procurement">
					<tr>
						<td><p><xsl:value-of select="date"/></p></td>
						<td>
							<p><xsl:value-of select="text"/></p>
						</td>
					</tr>
					</xsl:for-each>
				</table>
			</div>
		</body>
	</html>
	</xsl:template>

	<!-- ****************************    Добавление параметра к ссылке    ******************************** -->
	
	<!-- Удаление параметра с определенным значением -->
	<xsl:function name="f:remove_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="replace(encode-for-uri($value), '%20', '\\+')"/>
		<xsl:value-of 
			select="replace(replace($url, concat('(\?|&amp;)', $name, '=', $val_enc, '($|&amp;)'), '$1'), '&amp;$|\?$', '')"/>
	</xsl:function>

	<!-- Усановка параметра, если его нет, или замена значения параметра (в том числе удаление) -->
	<xsl:function name="f:set_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="encode-for-uri(string($value))"/>
		<xsl:value-of 
			select="if (not($val_enc) or $val_enc = '') then replace(replace($url, concat('(\?|&amp;)', $name, '=', '.*?($|&amp;)'), '$1'), '&amp;$|\?$', '')
					else if (contains($url, concat($name, '='))) then replace($url, concat($name, '=', '.*?($|&amp;)'), concat($name, '=', $value, '$1'))
					else if (contains($url, '?')) then concat($url, '&amp;', $name, '=', $val_enc)
					else concat($url, '?', $name, '=', $val_enc)"/>
	</xsl:function>
	
	<xsl:template match="*" mode="LINK_ADD_VARIABLE_QUERY">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<a class="{$class}" href="{.}{'?'[not(contains(current(), '?'))]}{'&amp;'[contains(current(), '?')]}{$name}={$value}"><xsl:value-of select="$text"/></a>
	</xsl:template>

</xsl:stylesheet>