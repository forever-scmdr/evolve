<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="crumbs" select="html//div[@id='breadCrumb'][1]//span[@itemprop='title']"/>
			<xsl:for-each select="$crumbs">
				<xsl:variable name="pos" select="position()"/>
				<section id="{string-join($crumbs[position() &lt; $pos] | ., '_')}">
					<xsl:if test="$crumbs[position() &lt; $pos]">
						<h_parent parent="{string-join($crumbs[position() &lt; $pos], '_')}"/>
					</xsl:if>
					<name><xsl:value-of select="." /></name>
				</section>
			</xsl:for-each>
			<xsl:variable name="header" select="//h1"/>
			<xsl:variable name="code" select="substring-before(substring-after($header, ' ('), ')')"/>
			<product id="{$code}">
				<h_parent parent="{string-join($crumbs, '_')}"/>
				<name><xsl:value-of select="substring-before($header, ' (')"/></name>
				<code><xsl:value-of select="$code"/></code>
				<type><xsl:value-of select="substring-after($header, ') ')"/></type>
				<name_extra><xsl:value-of select="//h2"/></name_extra>
				<short>
					<xsl:copy-of select="//div[@class='productMainInfo']//div[@class='attributes']"/>
				</short>
				<extra>
					<xsl:copy-of select="//div[@class='prodinfo']/ul"/>
				</extra>
				<description>
					<xsl:copy-of select="//div[@id='description']"/>
				</description>
				<tech>
					<xsl:copy-of select="//div[@id='attributes']"/>
				</tech>
				<package>
					<xsl:copy-of select="//div[@id='scopeofdelivery']"/>
				</package>
				<symbols>
					<xsl:for-each select="//div[@class='attributeSymbol']">
						<pic link="{.//img/@src}" download="{.//img/@src}"><xsl:value-of select=".//img/@title" /></pic>
					</xsl:for-each>
				</symbols>
				<xsl:if test="//iframe[@class='degree']">
					<spin link="{//iframe[@class='degree'][1]/@src}"/>
				</xsl:if>
				<xsl:for-each select="//iframe[not(@class='degree') and @id]">
					<video link="{@src}"/>
				</xsl:for-each>
				<gallery>
					<xsl:for-each select="//div[@id='popupGallery']//div[@class='thumbnail']//img[ends-with(@src, '.jpg') or ends-with(@src, '.jpeg')]">
						<pic download="{@src}" link="{@src}"/>
					</xsl:for-each>
				</gallery>
			</product>
		</result>
		<!--
		<xsl:variable name="code" select="replace(substring-after(//p[@itemprop = 'mpn'][1], '.'), '\D', '')"/
		<product id="{$code}">
			<code><xsl:value-of select="$code"/></code>
			<name><xsl:value-of select="normalize-space(//h1[1])"/></name>
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
		-->
	</xsl:template>

</xsl:stylesheet>