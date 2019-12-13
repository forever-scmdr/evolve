<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="n_"/>
	<xsl:variable name="prod" select="//div[@class = 'product_card_frame'][1]"/>

	<xsl:template match="/">
		<xsl:variable name="name" select="//h1[1]"/>
		<xsl:variable name="text" select="$prod//div[@class = 'description-product']//div[@class = 'entry'][1]"/>
		<xsl:variable name="pic_links" select="$prod//div[@id = 'carousel-custom']//div[@class = 'carousel-inner']//a"/>
		<xsl:variable name="tech_table" select="$prod//div[contains(@class, 'holder_table')]//table"/>
		<result>
			<xsl:for-each select="$prod//form[1]//select[1]/option">
				<xsl:variable name="code" select="tokenize(normalize-space(@class), ' ')[1]"/>
				<xsl:variable name="colour" select="normalize-space(.)"/>
				<product id="{$code}">
					<name><xsl:value-of select="normalize-space($name)" /><xsl:text> </xsl:text><xsl:value-of select="$colour" /></name>
					<code><xsl:value-of select="$code" /></code>
					<!--<name_extra><xsl:value-of select="$colour" /></name_extra>-->
					<gallery>
						<xsl:for-each select="$pic_links">
							<pic download="http://nora-m.ru{@href}" link="http://nora-m.ru{@href}"/>
						</xsl:for-each>
					</gallery>
					<text>
						<xsl:copy-of select="$text"/>
					</text>
					<params_xml>
						<xsl:for-each select="$tech_table//tr[position() &gt; 1]">
							<parameter>
								<name><xsl:value-of select="normalize-space(td[1])" /></name>
								<value><xsl:value-of select="normalize-space(td[2])" /></value>
							</parameter>
						</xsl:for-each>
					</params_xml>
				</product>
			</xsl:for-each>
		</result>
	</xsl:template>

</xsl:stylesheet>