<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<xsl:variable name="title" select="concat('Поиск по запросу ', page/variables/q)" />

	<xsl:variable name="news_items" select="/page/news_item"/>
	<xsl:variable name="news_parts" select="/page/text_part[news_item]"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						Новости по заппросу "<xsl:value-of select="page/variables/q"/>"
					</h1>
				</div>
			</div>
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="$news_items | $news_parts" mode="masonry"/>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="text_part" mode="masonry">
		<xsl:variable name="nid" select="news_item/@id"/>
		<xsl:variable name="cid" select="news_item/@id"/>
		<xsl:if test="not($news_items[@id = $nid])">
			<xsl:apply-templates select="news_item" mode="masonry"/>
		</xsl:if>
	</xsl:template>



</xsl:stylesheet>