<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.w3.org/1999/xhtml"
	version="2.0"
	xmlns:f="f:f"
	exclude-result-prefixes="xsl">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="CDATA_START"><xsl:text disable-output-escaping="yes">
	&lt;![CDATA[
	</xsl:text></xsl:template>
	
	<xsl:template name="CDATA_END"><xsl:text disable-output-escaping="yes">
	]]&gt;
	</xsl:text></xsl:template>

	<xsl:function name="f:url_id">
		<xsl:param name="url"/>
		<xsl:value-of select="tokenize(tokenize($url, '/')[last()], '\.')[1]"/>
	</xsl:function>

</xsl:stylesheet>