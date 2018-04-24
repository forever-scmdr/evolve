<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Контакты'" />

	<xsl:variable name="active_menu_item" select="'contacts'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Контакты</h1>

		<div class="page-content m-t">
			<xsl:value-of select="page/contacts/text" disable-output-escaping="yes"/>
			<h3>Расположение нашего офиса на карте</h3>
			<div class="map-container">
				<xsl:value-of select="page/contacts/map" disable-output-escaping="yes"/>
			</div>
			<xsl:value-of select="page/contacts/bottom_text" disable-output-escaping="yes"/>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>