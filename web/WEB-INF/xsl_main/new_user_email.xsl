<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ext="http://exslt.org/common"
	xmlns="http://www.w3.org/1999/xhtml"
	version="2.0"
	xmlns:f="f:f"
	exclude-result-prefixes="xsl ext">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>


<xsl:template match="/">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div class="text-container">
	<p style="font-style: italic;">
	Это письмо отправлено автоматически.
	</p>
	<h2>Запрос на регистрацию на сайте <b><xsl:value-of select="page/variables/base"/></b></h2>
	<p>После подтверждения регистрации пользователю будет доступен личный кабинет с историей заказов.</p>
	<p></p>
	<xsl:if test="page/new_user">
		<h2>Данные пользователя</h2>
		<xsl:for-each select="page/new_user/input/field">
			<p><xsl:value-of select="@caption"/>: <xsl:value-of select="." /></p>
		</xsl:for-each>
		<hr/>
		<p>Ссылка для подтверждения запроса: <b><a href="{page/variables/base}/{page/confirm_link}">ПОДТВЕРДИТЬ</a></b></p>
	</xsl:if>
	<xsl:if test="not(page/new_user)">
		<h1>Регистрация подтверждена</h1>
	</xsl:if>
</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>