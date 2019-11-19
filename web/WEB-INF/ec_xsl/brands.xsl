<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список брендов'"/>
    <xsl:variable name="active_menu_item" select="'brands'"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>
		<div class="page-content m-t">
			<div class="catalog-items">
				<xsl:for-each select="/page/brands/brand">
					<div class="catalog-item">
						<a href="{show_brand}" class="image-container" style="background-image: url({@path}{pic})"><!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> --></a>
						<div>
							<a href="{show_brand}" style="height: unset;"><xsl:value-of select="name"/></a>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>
		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


</xsl:stylesheet>