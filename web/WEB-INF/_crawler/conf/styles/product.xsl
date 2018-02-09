<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<xsl:variable name="code" select="normalize-space(substring-after(//p[@itemprop = 'mpn'][1]/text(), ';'))"/><!--//p[@itemprop = 'mpn'][1]/text()-->
		<product id="{$code}">
			<code><xsl:value-of select="$code"/></code>
			<name><xsl:value-of select="normalize-space(//h1[1])"/></name><!-- //div[contains(@class, 'breadcrumbs')]//h1 -->
			<!--
			<type><xsl:value-of select="//p[contains(@class, 'product-shop__fn')]"/></type>
			<producer><xsl:value-of select="//p[contains(@class, 'product-shop__mnf')]/a/span/text()"/></producer>
			-->
			<short><xsl:value-of select="//div[contains(@class, 'short-description')]/div"/></short>
			<!--
			<price><xsl:value-of select="normalize-space(//div[contains(@class, 'product-shop')]//div[contains(@class, 'price-box')]/div[contains(@class, 'min')]/p/text())"/></price>
			<desc><xsl:value-of select="//div[@id='description']/div[last()]"/></desc>
			<xsl:variable name="pic" select="//div[contains(@class, 'product-image')]//img"/>
			<medium_pic download="{$pic/@src}" name="{$pic/@alt}">med_<xsl:value-of select="tokenize($pic/@src, '/')[last()]"/></medium_pic>
			<xsl:variable name="large_pic" select="//div[contains(@class, 'product-image')]/a"/>
			<large_pic download="{$large_pic/@href}" name="{$large_pic/@title}">large_<xsl:value-of select="tokenize($large_pic/@href, '/')[last()]"/></large_pic>
			<tech>
				<xsl:for-each select="//div[@id='techdata']/ul/li">
					<par>
						<name><xsl:value-of select="span[1]"/></name>
						<value><xsl:value-of select="span[2]"/></value>
					</par>
				</xsl:for-each>
			</tech>
			<docs>
				<xsl:for-each select="//div[@id='docs']/ul/li">
					<doc download="http://www.tinko.ru{a/@href}" name="{normalize-space(a/text())}"><xsl:value-of select="tokenize(a/@href, '/')[last()]"/></doc>
				</xsl:for-each>
			</docs>
			<h_parent parent="{$parentId}" element="section"/>
			<related>
				<xsl:for-each select="//div[@id = 'slider-related-products']//h2/a">
					<code><xsl:value-of select="substring-before(substring-after(@href, '-'), '.')"/></code>
				</xsl:for-each>
			</related>
			-->
		</product>
	</xsl:template>

</xsl:stylesheet>