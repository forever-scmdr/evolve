<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="z_"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="code" select="normalize-space(tokenize(//div[@class = 'code1' and starts-with(text(), 'Код')]/text(), ':')[2])"/>
			<xsl:variable name="name" select="normalize-space(//h1[1])"/>
			<product id="{$code}">
				<name><xsl:value-of select="normalize-space($name)" /></name>
				<code><xsl:value-of select="$code" /></code>
				<gallery>
					<xsl:for-each select="//div[@class = 'icons2']//a">
						<pic download="{@href}" link="{@href}"/>
					</xsl:for-each>
				</gallery>
				<text type="html">
					<xsl:value-of select="//div[@class = 'tovarText']"/>
				</text>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>