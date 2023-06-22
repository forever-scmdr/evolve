<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="../utils_inc.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />

	<xsl:template name="DOCtype">
		<xsl:text disable-output-escaping="yes">&lt;!DOCtype html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<xsl:variable name="o" select="page/order"/>
	<xsl:variable name="f" select="$o/order_form[@id = $o/main_form]"/>

	<xsl:template match="/">
	<xsl:call-template name="DOCtype"/>
	<html xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
		<title>Санаторий «Спутник»</title>
		<base href="{page/base}"/>
		<link href="http://sansputnik.by/images/favicon.ico" rel="shortcut icon"/>
		<link rel="stylesheet" type="text/css" href="css/admin.css"/>
	</head>
	<body>
		<div class="mainwrap">			
			<h1>Онлайн-оплата</h1>
			<form action="https://pay148.paysec.by/pay/order.cfm" method="POST">
				<input type="hidden" name="Merchant_ID" value="459975"/>
				<input type="hidden" name="OrderNumber" value="{$o/num}"/>
				<input type="hidden" name="OrderAmount" value="{f:num($o/sum)}"/>
				<input type="hidden" name="OrderCurrency" value="{$o/cur_code}"/>
				<input type="hidden" name="Lastname" value="{$f/last_name}"/>
				<input type="hidden" name="Firstname" value="{$f/first_name}"/>
				<input type="hidden" name="Middlename" value="{$f/second_name}"/>
				<input type="hidden" name="Delay" value="0"/>
				<input type="hidden" name="Language" value="RU"/>
				<input type="hidden" name="Email" value="{$f/email}"/>
				<input type="hidden" name="Address" value="{$f/address}"/>
				<input type="hidden" name="MobilePhone" value="{$f/phone}"/>
				<input type="hidden" name="OrderComment" value="Оплата бронирования по договору {$o/num}"/>
				<input type="hidden" name="CardPayment" value="1"/>
				<p>Если переход к оплате не происходит автоматически в течение 5 секунд, нажмите, пожалуйста, кнопку</p>
				<br/>
				<input type="submit" value="Перейти к оплате"/> 
			</form>
		</div>
		<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="js/utils.js"></script>
		<script>
			$(document).ready(function() {
				$('form').submit();
			});
		</script>
	</body>
	</html>
	</xsl:template>

</xsl:stylesheet>