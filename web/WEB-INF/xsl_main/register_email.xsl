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
	<p>Спасибо за регистрацю на сайте <b><xsl:value-of select="page/variables/base"/></b></p>
	<p>После входа в учетную запись на сайте вам будет доступен личный кабинет с историей заказов.</p>
	<p></p>
	<p><b>Данные для входа</b></p>
	<p>Логин: <xsl:value-of select="page/user/email" /></p>
	<p>Пароль: <xsl:value-of select="page/user/password" /></p>
</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>