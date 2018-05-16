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
				<manual><xsl:value-of select="//div[@class='product-attributes-pdfs']/a[1]/@href"/></manual>
				<xsl:variable name="spins" select="//iframe[@class='degree']"/>
				<xsl:if test="$spins">
					<spin link="{$spins[1]/@src}"/>
				</xsl:if>
				<xsl:for-each select="//iframe[not(@class='degree') and @id]">
					<video link="{@src}"/>
				</xsl:for-each>
				<gallery>
					<xsl:for-each select="//div[@id='popupGallery']//div[@class='thumbnail']//img[ends-with(@src, '.jpg') or ends-with(@src, '.jpeg')]">
						<pic download="{@src}" link="{@src}"/>
					</xsl:for-each>
				</gallery>
				<assoc>
					<xsl:for-each select="//div[@class='widgetBoxBottomRound']//a/span">
						<code><xsl:value-of select="substring-before(substring-after(., '('), ')')"/></code>
					</xsl:for-each>
				</assoc>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>