<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>



	<xsl:template match="/">
		<table>
			<tbody id="extra-search-ajax">
			<xsl:if test="not(page/product)">
				<tr>
					<td colspan="10">
						В дополнительном катлоге ничего не найдено
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="page/product">
				<xsl:apply-templates select="page/product" />
			</xsl:if>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="page/product">
		<tr>
			<td>
				<b><xsl:value-of select="name"/></b>
			</td>
			<td></td>
			<td>
				<xsl:value-of select="vendor"/>
			</td>
			<td><xsl:value-of select="qty"/></td>
			<td>
				<xsl:value-of select="concat(page/price_catalog/default_ship_time, ' дней')"/>
			</td>
			<td>шт.</td>
			<td><xsl:value-of select="min_qty"/></td>
			<td>
				<xsl:for-each select="spec_price_map" >
					<p>
						<xsl:value-of select="@price"/>
					</p>
				</xsl:for-each>
			</td>
			<td>
				<xsl:for-each select="spec_price_map" >
					<p>
						<xsl:value-of select="concat('x', @qty, ' = ', @sum)" />
					</p>
				</xsl:for-each>
			</td>
			<td id="cart_search_{@id}">
				<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_search_{@id}">
					<input type="number" name="qty" value="{$min_qty}" min="{$min_qty}" step="{$min_qty}"/>
					<input type="submit" value="Заказать"/>
				</form>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>