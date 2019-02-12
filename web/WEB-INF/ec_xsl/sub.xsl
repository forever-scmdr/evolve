<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_section" select="//section[@key = /page/variables/sec]"/>
	<xsl:variable name="sel_sec_id" select="current_section/@id"/>
	<xsl:variable name="subs" select="$current_section/section"/>

	<xsl:variable name="title" select="$current_section/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>
	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template name="CONTENT">
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> &gt; <a href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and @id != $sel_sec_id]">
					<i class="fas fa-angle-right"></i>
					<a href="{show_products}">
						<xsl:value-of select="name"/>
					</a>
					<i class="fas fa-angle-right"></i>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>
			<xsl:value-of select="$h1"/>
		</h1>
		<div class="page-content m-t">
			<div class="catalog-items">
				<xsl:apply-templates select="$current_section/section"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="section">

		<xsl:variable name="link" select="if(section != '') then show_sub else show_products" />

		<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
		<xsl:variable name="product_pic" select="product/picture[1]"/>
		<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
		<div class="device items-catalog__section">
			<a href="{$link}" class="device__image device_section__image" style="background-image: url({$pic});"></a>
			<a href="{$link}" class="device__title"><xsl:value-of select="name"/></a>
		</div>
	</xsl:template>

	<!--<xsl:template name="EXTRA_SCRIPTS">-->
		<!--<xsl:call-template name="CART_SCRIPT"/>-->
	<!--</xsl:template>-->

</xsl:stylesheet>