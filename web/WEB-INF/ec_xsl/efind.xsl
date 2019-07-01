<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="price_catalogs" select="page/price_catalog"/>
	<xsl:variable name="price_intervals_default" select="$price_catalogs[name = 'default']/price_interval"/>
	<xsl:variable name="Q" select="f:num(page/price_catalog[name = 'default']/quotient)"/>
	<xsl:variable name="currency" select="'USD'"/>

	<xsl:template name="ALL_PRICES">
		<xsl:param name="section_name"/>
		<xsl:param name="price"/>
		<xsl:param name="min_qty"/>
		<xsl:variable name="intervals" select="$price_catalogs[name = $section_name]/price_interval"/>
		<xsl:variable name="price_intervals" select="if ($intervals) then $intervals else $price_intervals_default"/>
		<xsl:for-each select="$price_intervals">
			<xsl:variable name="quotient" select="f:num(quotient)"/>
			<xsl:variable name="unit_price" select="$price * $Q * $quotient"/>
			<xsl:if test="$price * $min_qty &lt; f:num(max)">
				<xsl:variable name="min_number" select="ceiling(f:num(min) div $price)"/>
				<xsl:variable name="number" select="if ($min_number &gt; 0) then ceiling($min_number div $min_qty) * $min_qty else $min_qty"/>
				<!--<xsl:variable name="sum" select="$unit_price * $number"/>-->
				<pb qty="{$number}"><xsl:value-of select="f:format_currency($unit_price)"/></pb>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>


	<xsl:template match="/">
		<data version="2.0">
			<xsl:variable name="find" select="page/product[plain_section]"/>
			<xsl:for-each select="$find[position() &lt;= 5]">
				<xsl:variable name="min_qty" select="if (min_qty) then f:num(min_qty) else 1"/>
				<item>
					<mfg><xsl:value-of select="vendor"/></mfg>
					<part><xsl:value-of select="name"/></part>
					<note><xsl:value-of select="type"/><xsl:text> </xsl:text><xsl:value-of select="name_extra"/></note>
					<img><xsl:value-of select="concat('http://alfacomponent.com/', @path, main_img)"/></img>
					<url>http://alfacomponent.com<xsl:value-of select="show_product"/></url>
					<sku><xsl:value-of select="code"/></sku>
					<cur>USD</cur>
					<xsl:if test="price_USD and f:num(price_USD) &gt; 0.001">
						<xsl:call-template name="ALL_PRICES">
							<xsl:with-param name="section_name" select="plain_section/name"/>
							<xsl:with-param name="min_qty" select="$min_qty"/>
							<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price'))"/>
						</xsl:call-template>
					</xsl:if>
					<stock><xsl:value-of select="qty"/></stock>
					<dlv>10-12 дней</dlv>
					<bid>0</bid>
				</item>
			</xsl:for-each>
		</data>
	</xsl:template>


</xsl:stylesheet>