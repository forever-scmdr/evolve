<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Каталог продукции'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="catalog_hide_side_menu" select="$catalog/hide_side_menu"/>
	<xsl:variable name="hide_side_menu" select="$catalog_hide_side_menu = '1'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<!-- TODO исправить верстку -->
	<xsl:template name="CONTENT">
	<div class="catalog_vl">
		<div class="catalog-items">
			<div class="catalog-items__wrap">
				<xsl:for-each select="/page/catalog/section[f:num(hide) = 0]">
					<div class="catalog-item">
						<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
						<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
						<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
						<div class="catalog-item__image img"><img src="{$pic}"  onerror="{$onerror}" alt="{name}" /></div>
						<div class="catalog-item__info">
							<div class="catalog-item__title"><xsl:value-of select="name"/></div>
							<div class="catalog-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
							<a href="{show_products}" class="catalog-item__link"></a>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>
	</div>
	</xsl:template>


</xsl:stylesheet>