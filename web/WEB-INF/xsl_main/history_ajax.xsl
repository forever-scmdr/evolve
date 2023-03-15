<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:import href="purchase_history.xsl"/>
	<xsl:import href="snippets/constants.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="base" select="page/base" />
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="msg" select="page/variables/message"/>
	<xsl:variable name="rslt" select="page/variables/result"/>


	<xsl:template match="/">
		<html lang="ru">
			<head>
				<base href="{$main_host}"/>
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/styles.css?version=1.65"/>
				<link rel="stylesheet" href="css/fixes.css?version=1.0"/>
				<link  href="css/fotorama.css" rel="stylesheet" />
				<link rel="stylesheet" type="text/css" href="css/unia.css"/>
			</head>
			<body>
				<xsl:apply-templates select="page/purchase"/>
			</body>
		</html>
	</xsl:template>


</xsl:stylesheet>