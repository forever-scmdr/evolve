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
				<h1 class="pageTitle">Список контрагентов загружен на сайт</h1>
				<p>Эту вкладку теперь можно закрыть и отключить доступ к интернету на сервере БД контрагентов</p>
			</div>
			<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
			<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		</body>
	</html>
	</xsl:template>

</xsl:stylesheet>