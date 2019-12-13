<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="m_"/>
	<xsl:variable name="code" select="tokenize(//html//base/@href, '/')[last()-1]"/>
	<xsl:variable name="prod" select="//div[@class = 'goodsDetail'][1]"/>

	<xsl:template match="/">
		<xsl:variable name="name" select="$prod//h1[1]"/>
		<xsl:variable name="text" select="$prod//div[@class = 'goodsDetNote']"/>
		<xsl:variable name="pic_links" select="$prod//div[@class = 'photoSlider']//a"/>
		<result>
			<product id="{$code}">
				<name><xsl:value-of select="normalize-space($name)" /></name>
				<code><xsl:value-of select="$code" /></code>
				<!--<name_extra><xsl:value-of select="$colour" /></name_extra>-->
				<gallery>
					<xsl:for-each select="$pic_links">
						<pic download="http://www.metallist.org{@href}" link="http://www.metallist.org{@href}"/>
					</xsl:for-each>
				</gallery>
				<text>
					<xsl:copy-of select="$text//span[1]"/>
					<table>
						<xsl:for-each select="$text//table[1]//tr">
							<tr>
								<xsl:for-each select="td[position() != 2]">
									<xsl:copy-of select="."/>
								</xsl:for-each>
							</tr>
						</xsl:for-each>
					</table>
				</text>
				<params_xml>
					<parameter>
						<name>Покрытие</name>
						<xsl:for-each select="$prod//div[@class = 'modifications']//span[@class = 'featureValue']">
							<value><xsl:value-of select="." /></value>
						</xsl:for-each>
					</parameter>
				</params_xml>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>