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
		<xsl:for-each select="//div[@id = 'left_column']//li[@class = 'active']//li/a">
			<collection id="{@href}">
				<name action="ignore"><xsl:value-of select="."/></name>
			</collection>
		</xsl:for-each>
		
		<dress id="{tokenize(//body/@source, '/')[last()]}">
			<name action="ignore"><xsl:value-of select="//div[@class = 'image-title'][1]"/></name>
			<desc action="ignore"><xsl:value-of select="//div[@class = 'image-kollections'][1]"/></desc>
			<xsl:for-each select="//a[@class = 'thumb']">
			<picture download="{//base/@href}{@href}"><xsl:value-of select="position()"/>.jpg</picture>
			</xsl:for-each>
			<h_parent parsedItem="{//span[@typeof = 'v:Breadcrumb'][3]/a/@href}" element="collection"/>
		</dress>
	</result>
	</xsl:template>

</xsl:stylesheet>