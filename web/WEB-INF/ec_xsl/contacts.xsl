<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Контакты'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="active_menu_item" select="'contacts'"/>

	<xsl:template name="CONTENT">
		<section class="s-content s-content--narrow">
			<div class="row">
				<div class="s-content__header col-full">
					<h1 class="s-content__header-title">
						<xsl:value-of select="$h1"/>
					</h1>
				</div>
				<div class="s-content__media col-full">
					<div id="map-wrap">
						<xsl:value-of select="/page/contacts/map" disable-output-escaping="yes"/>
					</div>
				</div>
				<div class="col-full s-content__main">
					<xsl:value-of select="/page/contacts/text" disable-output-escaping="yes"/>
					<div class="row">
						<div class="col-six tab-full">
							<h3>Адрес</h3>
							<xsl:value-of select="/page/contacts/address" disable-output-escaping="yes"/>
						</div>
						<div class="col-six tab-full">
							<h3>Контактные данные</h3>
							<xsl:value-of select="/page/contacts/phones" disable-output-escaping="yes"/>
						</div>

					</div>
					<xsl:call-template name="FEEDBACK_FORM"></xsl:call-template>
				</div>
			</div>

		</section>
	</xsl:template>

</xsl:stylesheet>