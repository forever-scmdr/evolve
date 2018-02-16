<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<xsl:variable name="code" select="replace(substring-after(//p[@itemprop = 'mpn'][1], '.'), '\D', '')"/><!--//p[@itemprop = 'mpn'][1]/text()-->
		<product id="{$code}">
			<code><xsl:value-of select="$code"/></code>
			<name><xsl:value-of select="normalize-space(//h1[1])"/></name><!-- //div[contains(@class, 'breadcrumbs')]//h1 -->
			<short><xsl:copy-of select="//section[@class, 'm-pdp-txt-position']/article[1]/*"/></short>
			<gallery>
				<xsl:for-each select="//div[@id = 'slider']//li/a[not(starts-with(@href, 'https://youtube.com'))]">
					<xsl:variable name="parts" select="tokenize(@href, '/')"/>
					<picture download="{@href}"><xsl:value-of select="$parts[count($parts)]"/></picture>
				</xsl:for-each>
				<xsl:for-each select="//div[@id = 'slider']//li/a[starts-with(@href, 'https://youtube.com')]">
					<video><xsl:value-of select="@href"/></video>
				</xsl:for-each>
			</gallery>
			<xsl:variable name="text" select="//div[@id = 'tab-details']/*"/>
			<xsl:variable name="apply" select="//div[@id = 'tab-applications']/*"/>
			<text>
				<xsl:copy-of select="$text"/>
			</text>
			<apply>
				<xsl:copy-of select="$apply"/>
			</apply>
			<textpics>
				<xsl:for-each select="$text//img | $apply//img">
					<xsl:variable name="parts" select="tokenize(@src, '/')"/>
					<img download="{@src}"><xsl:value-of select="$parts[count($parts)]"/></img>
				</xsl:for-each>
			</textpics>
			<associated>
				<xsl:variable name="accessiories" select="//div[@id = 'tab-accessories']//p[@class = 'order-number']"/>
				<xsl:variable name="sets" select="//div[@id = 'tab-sets']//p[@class = 'order-number']"/>
				<xsl:variable name="probes" select="//div[@id = 'tab-probes']//p[@class = 'order-number']"/>
				<xsl:for-each select="$accessiories">
					<accessory><xsl:value-of select="replace(substring-after(., ':'), '\D', '')"/></accessory>
				</xsl:for-each>
				<xsl:for-each select="$sets">
					<set><xsl:value-of select="replace(substring-after(., ':'), '\D', '')"/></set>
				</xsl:for-each>
				<xsl:for-each select="$probes">
					<probe><xsl:value-of select="replace(substring-after(., ':'), '\D', '')"/></probe>
				</xsl:for-each>
			</associated>
			<tech>
				<xsl:for-each select="//div[@id = 'tab-data']//table">
					<tag name="{normalize-space(replace(thead//th, '\p{Z}+?', ' '))}">
						<xsl:for-each select="tbody/tr">
							<parameter>
								<name><xsl:value-of select="normalize-space(replace(td[1]/p, '\p{Z}+?', ' '))"/></name>
								<xsl:for-each select="td[2]/p">
									<value><xsl:value-of select="normalize-space(replace(., '\p{Z}+?', ' '))"/></value>
								</xsl:for-each>
							</parameter>
						</xsl:for-each>
					</tag>
				</xsl:for-each>
			</tech>
		</product>
	</xsl:template>

</xsl:stylesheet>