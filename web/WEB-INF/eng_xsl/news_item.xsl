<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path">
			<a href="{/page/index_link}">Homepage</a><xsl:call-template name="arrow"/>
			<a href="{/page/news_link}">News</a><xsl:call-template name="arrow"/>
		</div>
		<h1><xsl:value-of select="/page/sn/header"/></h1>
		<div class="news-item">
			<xsl:value-of select="/page/sn/text" disable-output-escaping="yes"/>
		</div>
		<a href="{/page/news_link}">Back to news</a>
	</div>
	</xsl:template>


</xsl:stylesheet>