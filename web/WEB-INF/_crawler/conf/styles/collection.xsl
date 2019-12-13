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
		<xsl:for-each select="//div[contains(@class, 'prod')]">
			<dress id="{tokenize(a/@href, '/')[last()]}">
				<small action="ignore" download="{//base/@href}{a/img/@src}">small.jpg</small>
			</dress>
		</xsl:for-each>
	</result>
	</xsl:template>

</xsl:stylesheet>