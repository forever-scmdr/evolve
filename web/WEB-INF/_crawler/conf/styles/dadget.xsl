<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="prod" select="//div[@class = 'content-row-body']"/>
			<xsl:variable name="code" select="normalize-space($prod//div[@id = 'sku_article']/text())"/>
			<product id="{$code}">
				<name><xsl:value-of select="string-join($prod//h1/span, ' ')" /></name>
				<code><xsl:value-of select="$code" /></code>
				<gallery>
					<xsl:for-each select="$prod//div[@id = 'carousel-product']//a">
						<pic download="{@href}" link="{@href}"/>
					</xsl:for-each>
				</gallery>
				<description type="html">
					<xsl:copy-of select="$prod//div[@class = 'list-item-text']/*"/>
				</description>
				<text type="html">
					<xsl:copy-of select="//div[@class = 'product-description-text']/*"/>
				</text>
				<params_xml>
					<xsl:for-each select="//div[@class = 'product-ttx-item']">
						<parameter>
							<name><xsl:value-of select="normalize-space(span[1])" /></name>
							<value><xsl:value-of select="normalize-space(span[2])" /></value>
						</parameter>
					</xsl:for-each>
				</params_xml>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>