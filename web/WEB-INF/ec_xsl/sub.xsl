<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat($sel_sec/name, ' купить в Минске с доставкой - интернет магазин SakuraBel')"/>
	<xsl:variable name="meta_description" select="concat('Купить ', $sel_sec/name, ' в Минске ✅ Доставка во все регионы Беларуси. Хорошая цена! Звоните ☎☎☎  +375 17 396 44 29, +375 29 311 44 29 Консультация и установка')"/>
	<xsl:variable name="default_h1" select="page/current_section/name"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="catalog" select="page/catalog"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:if test="current()/@id != $sel_sec_id">
						<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
						<a href="{show_section}"><xsl:value-of select="name"/></a>
					</xsl:if>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>
		<div class="page-content m-t">
			<div class="catalog-items"><!-- добавить класс lines для отображения по строкам -->
				<xsl:for-each select="page/current_section/section">
					<xsl:variable name="href" select="if($catalog//section[@id = current()/@id]/section) then show_section else show_products"/>
					<xsl:variable name="main_pic" select="product[1]/gallery[1]"/>
					<div class="catalog-item">
						<xsl:variable name="pic_path" select="if ($main_pic) then concat(product[1]/@path, $main_pic) else 'img/no_image.png'"/>
						<a href="{$href}" class="image-container" style="background-image: url({$pic_path});">
							<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->
						</a>
						<div class="name">
							<a href="{$href}" style="height: unset;"><xsl:value-of select="name"/></a>
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

</xsl:stylesheet>