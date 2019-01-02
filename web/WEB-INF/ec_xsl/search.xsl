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

	<xsl:variable name="custom_pages" select="/page/custom_page"/>
	<xsl:variable name="custom_parts" select="/page/text_part[custom_page]"/>

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
			
			<xsl:if test="$custom_pages | $custom_parts">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						Статьи по заппросу "<xsl:value-of select="page/variables/q"/>"
					</h1>
				</div>
			</div>
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="$custom_pages | $custom_parts" mode="masonry"/>
				</div>
			</div>
		</xsl:if>
		</section>
	</xsl:template>

	<xsl:template match="text_part" mode="masonry">
		<xsl:variable name="nid" select="news_item/@id"/>
		<xsl:variable name="cid" select="news_item/@id"/>
		<xsl:if test="not($news_items[@id = $nid])">
			<xsl:apply-templates select="news_item" mode="masonry"/>
		</xsl:if>
		<xsl:if test="not($custom_pages[@id = custom_page/@id])">
			<xsl:apply-templates select="custom_page" mode="masonry"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="custom_page" mode="masonry">


		<article class="masonry__brick entry format-standard" data-aos="fade-up">

			<div class="entry__thumb">
				<a href="{show_page}" class="entry__thumb-link">
					<img src="{concat(@path, small_pic)}"
						 srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>
				</a>
			</div>

			<!-- TEXT -->
			<div class="entry__text">
				<div class="entry__header">
					<div class="entry__date">
						<a href="{show_custom_page}">
							<xsl:value-of select="date"/>
						</a>
					</div>
					<div class="h1 entry__title">
						<a href="{show_page}">
							<xsl:value-of select="name"/>
						</a>
					</div>
				</div>
				<div class="entry__excerpt">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
			</div>

		</article>
	</xsl:template>


</xsl:stylesheet>