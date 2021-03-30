<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Каталог продукции'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<!-- TODO исправить верстку -->
	<xsl:template name="CONTENT">
		<div class="catalog-items">
			<div class="catalog-items__wrap">
				<xsl:for-each select="/page/catalog/section">
					<div class="catalog-section">
						<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
						<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
						<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
						<a class="catalog-section__image" href="{show_products}"><img src="{$pic}"  onerror="$(this).attr('src', 'img/no_image.png')" alt="{name}" /></a>
						<!-- <div class="catalog-section__subtitle">Раздел</div> -->
						<a class="catalog-section__title" href="{show_products}"><xsl:value-of select="name"/></a>
						<!-- <div class="catalog-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div> -->
					</div>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>
