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

	<xsl:variable name="inp" select="page/generic_form/input"/>

<xsl:template match="/">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div class="text-container">
	<p style="font-style: italic;">Электронное обращение</p>
	<p></p>
	<p>Название организации: <xsl:value-of select="$inp/company"/></p>
	<p>Юридический адрес: <xsl:value-of select="$inp/address"/></p>
	<p>УНП: <xsl:value-of select="$inp/unp"/></p>
	<p>ОКПО: <xsl:value-of select="$inp/okpo"/></p>
	<p></p>
	<p>
		<xsl:value-of select="$inp/message"/>
	</p>
</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>