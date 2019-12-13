<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="t_"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="prod" select="//div[@class = 'page-item']"/>
			<xsl:variable name="code" select="normalize-space(tokenize($prod//div[@class = 'page-item__1c-id']/text(), ' ')[2])"/>
			<xsl:variable name="name" select="$prod//div[@class = 'page-item__title']/text()"/>
			<product id="{$code}">
				<name><xsl:value-of select="normalize-space($name)" /></name>
				<code><xsl:value-of select="$code" /></code>
				<gallery>
					<xsl:for-each select="$prod//div[@class = 'page-item__img']//a">
						<pic download="{@href}" link="{@href}"/>
					</xsl:for-each>
					<xsl:for-each select="$prod//div[@class = 'page-item__img_shema']//img">
						<pic download="{@src}" link="{@src}"/>
					</xsl:for-each>
					<xsl:for-each select="$prod//div[@class = 'instruction_gallery']//a">
						<pic download="{@href}" link="{@href}"/>
					</xsl:for-each>
				</gallery>
				<text type="html">
					<xsl:copy-of select="$prod//div[@class = 'instruction_text']/*"/>
				</text>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>