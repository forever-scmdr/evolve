<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="page/selected_news/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>
	<xsl:variable name="news" select="page/selected_news"/>

	<xsl:variable name="p" select="page/product"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL_NEWS"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="CONTENT">

		<div class="news info-items info-items_section">
			<div class="info-items__wrap">
				<xsl:for-each select="page/selected_news/news_item">
					<xsl:variable name="main_pic" select="if(main_pic != '') then concat(@path, main_pic) else concat($news/@path, $news/main_pic)"/>
					<div class="info-items ">
						<!--<div class="info-item__image img"><img src="{$main_pic}" alt="" style="max-width:100%;" /></div>-->
						<div class="info-item__info">
							<a href="{show_news_item}" class="info-item__title"><xsl:value-of select="header"/></a>
							<div class="info-item__date"><xsl:value-of select="date"/></div>
							<div class="info-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
						</div>
						
					</div>
				</xsl:for-each>
			</div>
		</div>


		<xsl:if test="page//news_item_pages">
			<div class="pagination">
				<div class="pagination__label">Страницы:</div>
				<div class="pagination__wrap">
					<xsl:for-each select="page//news_item_pages/page">
						<a href="{link}" class="pagination__item{' pagination__item_active'[current()/@current]}">
							<xsl:value-of select="number"/>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>