<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="page/current_section/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_section}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>
		<div class="page-content m-t">

			<xsl:if test="$seo/text != ''">
				<div class="page-content">
					<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
				</div>
			</xsl:if>

			<div class="catalog-items"><!-- добавить класс lines для отображения по строкам -->
				<xsl:for-each select="page/current_section/section">
					<xsl:variable name="main_pic" select="product[1]/gallery[1]"/>
					<xsl:variable name="sec_id" select="@id"/>
					<xsl:variable name="has_sub" select="//page/catalog//section[@id = $sec_id and section]"/>
					<div class="catalog-item">
						<xsl:variable name="sec_pic" select="if(main_pic != '') then concat(@path, main_pic) else ''"/>
						<xsl:variable name="product_pic" select="if (product[1]/main_pic != '') then concat(product[1]/@path, product[1]/main_pic) else ''"/>
						<xsl:variable name="pic_path" select="if($sec_pic != '') then $sec_pic else $product_pic"/>
						<a href="{if ($has_sub) then show_section else show_products}" class="image-container" style="background-image: url({if($pic_path != '') then $pic_path else 'images/no-image.png'})"></a>
						<div>
							<a href="{if ($has_sub) then show_section else show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>


		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template name="SEO_TEXT"/>
</xsl:stylesheet>