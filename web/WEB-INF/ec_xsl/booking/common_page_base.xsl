<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="../currency_ajax.xsl" />

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<xsl:template name="TITLE">Санаторий «Спутник»</xsl:template>
	
	<xsl:template name="CONTENT"></xsl:template>
	<xsl:template name="SCRIPTS"></xsl:template>

	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>Санаторий «Спутник»</title>
		<base href="{page/base}"/>
		<link href="http://sansputnik.by/images/favicon.ico" rel="shortcut icon"/>
		<link rel="stylesheet" type="text/css" href="css/admin.css"/>
		<link rel="stylesheet" type="text/css" href="js/datepicker.css"/>
		<link href="admin/jquery_css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" media="screen"/>	
	</head>
	<body>
		<div class="header">
			<div class="inner">
				<h2>Система бронирования</h2>
				<!-- 
				<div>25 сентабря 2016</div>
				<div>USD 2.00/1.95</div>
				<div>EUR 0.50/0.60</div>
				<div title="раша сакз!">RUB 0.00/0.00</div>
				 -->
				<a href="admin_initialize.action" class="logout blue-button">Выйти</a>
			</div>
		</div>
		<div class="mainwrap">			
			<xsl:call-template name="CONTENT"/>
		</div>
		<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="admin/js/jquery-ui-1.10.3.custom.min.js"/>
		<script type="text/javascript" src="js/regional-ru.js"/>
		<script type="text/javascript" src="js/jquery.form.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="js/jquery.number.min.js"></script> 
		<script type="text/javascript" src="js/combobox.js"></script>
		<script type="text/javascript" src="js/booking-form.js"></script>
		<script type="text/javascript" src="js/ubertable.js"></script>
		<script type="text/javascript" src="js/utils.js"></script>
		<xsl:call-template name="SCRIPTS"/>
	</body>
	</html>
	</xsl:template>

</xsl:stylesheet>