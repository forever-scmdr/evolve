<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="p_"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="code" select="normalize-space(tokenize(//div[@class = 'item-info']//div[starts-with(text(), 'Артикул')]/text(), ' ')[2])"/>
			<xsl:variable name="name" select="//h1[1]"/>
			<product id="{$code}">
				<name><xsl:value-of select="normalize-space($name)" /></name>
				<code><xsl:value-of select="$code" /></code>
				<gallery>
					<xsl:for-each select="//div[@class = 'item-image']//a">
						<pic download="http://petronik.ru{@href}" link="http://petronik.ru{@href}"/>
					</xsl:for-each>
				</gallery>
				<text type="html">
					<xsl:copy-of select="//div[@class = 'item-body']/*"/>
				</text>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>