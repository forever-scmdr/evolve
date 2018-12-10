<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="extra-header-class" select="' s-pageheader--home'"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row masonry-wrap">
				<div class="masonry">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="/page/news_item" mode="masonry"/>
				</div>
			</div>
		</section>
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
						<div class="slider__slide">
							<img src="images/thumbs/masonry/gallery/gallery-1-400.jpg"
								 srcset="images/thumbs/masonry/gallery/gallery-1-400.jpg 1x, images/thumbs/masonry/gallery/gallery-1-800.jpg 2x" alt=""/>
						</div>
					</div>
				</div>
			</xsl:if>

			<!-- TEXT -->
			<div class="entry__text">
				<div class="entry__header">
					<div class="entry__date">
						<a href="single-standard.html"><xsl:value-of select="date"/></a>
					</div>
					<div class="h1 entry__title"><a href="{show_page}"><xsl:value-of select="header"/></a></div>
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

	<xsl:template name="EXTRA_HEADER_CONTENT">
		<xsl:variable name="featured" select="page/main_page/featured" />
		<xsl:if test="page/main_page/featured">
			<div class="pageheader-content row">
				<div class="col-full">
					<div class="featured">
						<div class="featured__column featured__column--big">
							<xsl:apply-templates select="$featured[1]"/>
						</div>
						<xsl:if test="$featured[position() &gt; 1]">
							<div class="featured__column featured__column--small">
								<xsl:apply-templates select="$featured[position() &gt; 1]"/>
							</div>
						</xsl:if>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="featured">
		<div class="entry" style="background-image:url('{concat(@path,main_pic)}')">
			<div class="entry__content">
				<xsl:for-each select="tag">
					<span class="entry__category">
						<a href="{concat(tag_link, '/?tag=', .)}">
							<xsl:value-of select="."/>
						</a>
					</span>
				</xsl:for-each>
				<h1><a href="{link}" title="name"><xsl:value-of select="name"/></a></h1>

				<div class="entry__info">
					<ul class="entry__meta" style="margin-left:0;">
						<li style="margin-left:0;"><xsl:value-of select="date"/></li>
					</ul>
				</div>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>
