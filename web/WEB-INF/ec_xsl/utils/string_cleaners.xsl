<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">

<xsl:function name="f:clean_id">
	<xsl:param name="s" />
	<xsl:sequence select="if($s and string($s) != '') then replace(string($s), '[^a-zA-z0-9\-_]', '-') else ''"/>
</xsl:function>

</xsl:stylesheet>