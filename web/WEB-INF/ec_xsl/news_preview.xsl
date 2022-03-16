<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Завтрашние новости'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>

	<xsl:variable name="pagination" select="/page/selected_news/news_item_pages"/>
	<xsl:variable name="prev" select="$pagination/page[number(/page/variables/p)-1]"/>
	<xsl:variable name="next" select="$pagination/page[number(/page/variables/p)+1]"/>
	<xsl:variable name="sel" select="page/variables/sel"/>
	<xsl:variable name="quote">"</xsl:variable>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						<xsl:value-of select="$h1"/>
					</h1>
					<div class="lead">
						Смотри сегодня в завтрашний день! Новости для не только лишь всех
					</div>
				</div>
			</div>
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:for-each select="/page/search/success/(news_item | small_news_item)">
						<xsl:variable name="link" select="concat('news_item_preview/?id=', @id)"/>
						<xsl:apply-templates select="." mode="masonry">
							<xsl:with-param name="link" select="$link"/>
						</xsl:apply-templates>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

</xsl:stylesheet>