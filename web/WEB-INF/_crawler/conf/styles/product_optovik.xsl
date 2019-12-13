<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.w3.org/1999/xhtml"
	version="1.0"
	exclude-result-prefixes="xsl">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
	<result>
		<xsl:apply-templates select="//div[contains(@id, 'catalog-item-')]"/>
		<xsl:for-each select="//ul[contains(@class, 'breadcrumb-navigation')]/li[position() &gt; 1]/a">
			<section id="{@href}">
				<name action="ignore"><xsl:value-of select="span"/></name>
			</section>
		</xsl:for-each>
	</result>
	</xsl:template>

	<xsl:template match="div">
		<item id="{tokenize(@id,'-')[position() = last()]}_opt">
			<name><xsl:value-of select=".//div[contains(@class, 'el-title')]/div[contains(@class, 'text')]/h1"/></name>
			<brand><xsl:value-of select=".//div[contains(@class, 'el-title')]/div[contains(@class, 'text')]/h2/a"/></brand>
			<short>
				<xsl:call-template name="CDATA_START"/>
				<xsl:value-of select=".//div[contains(@class, 'el-offers')]//div[contains(@class, 'inner')]/*"/>
				<xsl:call-template name="CDATA_END"/>
			</short>
			<description>
				<xsl:call-template name="CDATA_START"/>
				<xsl:copy-of select=".//div[contains(@class, 'el-information')]/div[contains(@class, 'description')]/*"/>
				<xsl:call-template name="CDATA_END"/>
			</description>
			<picture download="http://sportoptovik.ru{.//div[contains(@class, 'el-preview')]/img/@src}">main.jpg</picture>
			<xsl:for-each select="//ul[contains(@class, 'breadcrumb-navigation')]/li[position() &gt; 1]/a">
				<h_parent parent="{@href}" element="section"/>
			</xsl:for-each>
		</item>
	</xsl:template>
	
	

</xsl:stylesheet>