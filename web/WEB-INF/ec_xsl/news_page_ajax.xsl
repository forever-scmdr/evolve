<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="p" select="page/variables/p"/>

	<xsl:template match="/">
		<div>
			<xsl:apply-templates select="//news_item" mode="masonry"/>
		</div>
	</xsl:template>

</xsl:stylesheet>