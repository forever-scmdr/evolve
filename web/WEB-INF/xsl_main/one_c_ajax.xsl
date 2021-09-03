<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	
	<xsl:template match="/">
		<div id="order_status" class="result">
			<xsl:if test="//ResponseStatus = 'OK'">
				<table class="order-status">
					<tr>
						<td>Дата заказа:</td>
						<td><xsl:value-of select="//PurchaseDate"/></td>
					</tr>
					<tr>
						<td>Итоговая стоимость заказа:</td>
						<td><xsl:value-of select="//PurchaseSum"/></td>
					</tr>
					<tr>
						<td>Статус заказа:</td>
						<td><xsl:value-of select="//PurchaseStatusMessage"/></td>
					</tr>
					<tr>
						<td>Комментарий к заказу:</td>
						<td><xsl:value-of select="//PurchaseComment"/></td>
					</tr>
					<tr>
						<td>Трэк-номер для отслеживания:</td>
						<td><xsl:value-of select="//PurchaseTrackNumber"/></td>
					</tr>
				</table>
			</xsl:if>
			<xsl:if test="not(//ResponseStatus = 'OK')">
				<p style="font-size: 18px; font-weight: 700; text-align: center;">
					<xsl:value-of select="//ResponseMessage"/>
				</p>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>