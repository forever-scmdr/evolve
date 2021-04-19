<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:ext="http://exslt.org/common"
		xmlns="http://www.w3.org/1999/xhtml"
		version="2.0"
		xmlns:f="f:f"
		exclude-result-prefixes="xsl ext">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="cart" select="page/cart"/>


	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<table>
					<tr>
						<td>Артикул</td>
						<td>Наименование</td>
						<td>Количество</td>
						<td>ед. изм.</td>
						<td>Цена отпускная</td>
						<td>Цена поставщика</td>
						<td>Поставщик</td>
						<td>Срок поставки</td>
						<td>Описание</td>
						<td>Производитель</td>
					</tr>
					<xsl:apply-templates select="$cart/bought"/>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="bought">

		<xsl:variable name="product" select="product"/>
		<xsl:variable name="price" select="if(aux != '') then concat(f:cart_price_platan($product/price),' ', upper-case($curr)) else f:price_catalog($product/price,'', $product/min_qty)"/>
		<xsl:variable name="sum" select="if(aux != '') then concat(f:cart_price_platan(sum),' ', upper-case($curr)) else f:price_catalog(sum, '', '')"/>

		<tr>
			<td>
				<xsl:value-of select="if(aux = 'promelec' and contains($product/code, 'v')) then substring-before($product/code, 'v') else $product/code"/>
			</td>
			<td>
				<xsl:value-of select="$product/name"/>
			</td>
			<td>
				<xsl:value-of select="qty"/>
			</td>
			<td>
				<xsl:value-of select="if($product/unit != '') then $product/unit else 'шт'"/>
			</td>
			<td>
				<xsl:value-of select="$price"/>
			</td>
			<td>
				<xsl:value-of select="if(aux != '') then concat($product/price_original, $product/currency_id) else concat($product/price_opt, 'BYN')"/>
			</td>
			<td>
				<xsl:value-of select="if(aux != '') then aux else 'ictrade'"/>
			</td>
			<td>
				<xsl:value-of select="delivery_time"/>
			</td>
			<td>
				<xsl:value-of select="$product/description"/>
			</td>
			<td>
				<xsl:value-of select="$product/vendor"/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>