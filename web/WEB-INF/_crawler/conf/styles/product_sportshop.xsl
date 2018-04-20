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
		<xsl:apply-templates select="//div[contains(@class, 'catsf')]/div[@class='catsf']"/>
		<xsl:for-each select="//div[contains(@class, 'taxonomy')]/ul/li[position() &gt; 2]/a">
			<section id="{substring-after(@href, '~group_id__n25=')}">
				<xsl:if test="span">
					<name action="ignore"><xsl:value-of select="span"/></name>
				</xsl:if>
				<xsl:if test="not(span)">
					<name action="ignore"><xsl:value-of select="."/></name>
				</xsl:if>
			</section>
		</xsl:for-each>
	</result>
	</xsl:template>

	<xsl:template match="div">
		<item id="{substring-after(//body/@source, '~id__n25=')}_shp">
			<name><xsl:value-of select="h5"/></name>
			<brand></brand>
			<short>
				<xsl:call-template name="CDATA_START"/>
				<xsl:copy-of select="p[position() &lt;= 3]"/>
				<xsl:call-template name="CDATA_END"/>
			</short>
			<description>
				<xsl:call-template name="CDATA_START"/>
				<xsl:copy-of select="p"/>
				<xsl:call-template name="CDATA_END"/>
			</description>
			<picture download="{//div[@id = 'cbimg']/a/@href}">main.jpg</picture>
			<xsl:for-each select="//div[contains(@class, 'taxonomy')]/ul/li[position() &gt; 2]/a">
				<h_parent parsedItem="{substring-after(@href, '~group_id__n25=')}" element="section"/>
			</xsl:for-each>
		</item>
	</xsl:template>
	
	

</xsl:stylesheet>