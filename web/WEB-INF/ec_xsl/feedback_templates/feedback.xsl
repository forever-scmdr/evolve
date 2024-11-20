<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	
	<xsl:variable name="form" select="page/form"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			</head>
			<body>
				<xsl:for-each select="$form/field">
					<xsl:variable name="tag" select="@name"/>
					<xsl:if test="//*[name() = $tag] != ''">
						<p>
							<b><xsl:value-of select="@caption"/>: </b>
							<xsl:value-of select="//*[name() = $tag]"/>
						</p>
					</xsl:if>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>