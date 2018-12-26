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
	<xsl:variable name="news_parts" select="/page/text_part[news_item[@id != $news_items/@id]]"/>

	<xsl:variable name="custom_pages" select="/page/custom_page"/>
	<xsl:variable name="custom_parts" select="/page/text_part[custom_page[@id != $custom_pages/@id]]"/>

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
		<xsl:apply-templates select="custom_page | news_item" mode="masonry"/>
	</xsl:template>
	<xsl:template match="news_item" mode="masonry">

		<xsl:variable name="format" select="if(video_url != '') then 'video' else if(top_gal/main_pic != '') then 'gallery' else 'standard'"/>

		<article class="masonry__brick entry format-{$format}" data-aos="fade-up">

			<!-- STANDARD -->
			<xsl:if test="$format = 'standard'">
				<div class="entry__thumb">
					<a href="{show_page}" class="entry__thumb-link">
						<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>
					</a>
				</div>
			</xsl:if>

			<!-- VIDEO -->
			<xsl:if test="$format = 'video'">
				<div class="entry__thumb video-image">
					<a href="{video_url}" data-lity="">
						<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>
					</a>
				</div>
			</xsl:if>

			<xsl:if test="$format = 'gallery'">
				<div class="entry__thumb slider">
					<div class="slider__slides">
						<xsl:variable name="path" select="top_gal/@path"/>
						<xsl:for-each select="top_gal/small_pic">
							<xsl:variable name="p" select="position()"/>
							<div class="slider__slide">
								<img src="{concat($path,.)}" srcset="{concat($path,.)} 1x, {concat($path,../medium_pic[$p])} 2x" alt=""/>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</xsl:if>

			<!-- TEXT -->
			<div class="entry__text">
				<div class="entry__header">
					<div class="entry__date">
						<a href="{show_page}"><xsl:value-of select="date"/></a>
					</div>
					<div class="h1 entry__title"><a href="{show_page}"><xsl:value-of select="name"/></a></div>
				</div>
				<div class="entry__excerpt">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
				<div class="entry__meta">
					<span class="entry__meta-links">
						<a href="category.html">Что за ссылки?</a>
						<a href="category.html">Куда они ведут?</a>
						<a href="category.html">А они вообще нужны?</a>
					</span>
				</div>
			</div>

		</article>
	</xsl:template>


</xsl:stylesheet>