<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="currency" select="'RUB'"/>


	<xsl:template name="ALL_PRICES">
		<xsl:param name="product"/>
		<xsl:variable name="price_intervals" select="$product/prices/break"/>
		<xsl:for-each select="$price_intervals">
			<xsl:variable name="min_interval_qty" select="f:num(@qty)"/>
			<xsl:variable name="min_pack" select="$min_interval_qty"/>
			<xsl:variable name="unit_price" select="f:exchange(current(), 'price', 0)"/>
			<pb qty="{$min_pack}"><xsl:value-of select="f:format_currency_precise($unit_price)"/></pb>
		</xsl:for-each>
		<xsl:if test="not($price_intervals)">
			<xsl:variable name="unit_price" select="f:exchange($product/price, 'price', 0)"/>
			<pb qty="1"><xsl:value-of select="f:format_currency_precise($unit_price)"/></pb>
		</xsl:if>
	</xsl:template>



	<xsl:template match="/">
		<data version="2.0">
			<xsl:for-each select="page/command/product_list/results/product">
				<xsl:variable name="min_qty" select="if (min_qty and f:num(min_qty) &gt; 0) then f:num(min_qty) else 1"/>
				<xsl:variable name="step" select="if (step and f:num(step) &gt; 0) then f:num(step) else 1"/>
				<item>
					<part><xsl:value-of select="name"/></part>
					<mfg><xsl:value-of select="vendor"/></mfg>
					<cur>RUB</cur>
					<xsl:call-template name="ALL_PRICES">
						<xsl:with-param name="product" select="current()"/>
					</xsl:call-template>
					<moq><xsl:value-of select="$min_qty" /></moq>
					<mpq><xsl:value-of select="$step" /></mpq>
					<note>ship from: <xsl:value-of select="category_id"/></note>
					<stock>В наличии</stock>
					<instock>1</instock>
					<dlv><xsl:value-of select="next_delivery"/> дней</dlv>
					<bid>0</bid>
				</item>
			</xsl:for-each>
		</data>
	</xsl:template>


</xsl:stylesheet>