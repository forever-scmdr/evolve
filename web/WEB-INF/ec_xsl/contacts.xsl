<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'contacts'"/>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path">
			<a href="{/page/index_link}">Главная страница</a><xsl:call-template name="arrow"/>
		</div>
		<xsl:call-template name="PAGE_TITLE"><xsl:with-param name="page" select="/page/rooms"/></xsl:call-template>
		<xsl:value-of select="/page/contacts/text" disable-output-escaping="yes"/>
		<script type="text/javascript" charset="utf-8" src="//api-maps.yandex.ru/services/constructor/1.0/js/?sid=l3Ko5dt7TfnGTfkTmVWbgR73lQMAUtAg&amp;width=974&amp;height=500"></script>
	</div>
	</xsl:template>


</xsl:stylesheet>