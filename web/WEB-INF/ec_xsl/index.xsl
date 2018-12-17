<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="extra-header-class" select="' s-pageheader--home'"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="/page/news_wrap/news_item" mode="masonry"/>
					<!--<xsl:apply-templates select="/page/news_wrap/news_item" mode="masonry"/>-->
					<!--<xsl:apply-templates select="/page/news_wrap/news_item" mode="masonry"/>-->
				</div>
			</div>
			<xsl:if test="/page/news_wrap/news_item_pages">
				<div class="row">
					<div class="col-full">
						<nav class="pgn">
							<ul>
								<li id="load_more">
									<a class="pgn__num" id="load-more-link" href="{page/load_more}">Загрузить еще</a>
								</li>
							</ul>
						</nav>
					</div>
				</div>
			</xsl:if>
		</section>
		<section class="s-extra">
			<div class="row top">
				<div class="col-eight md-six tab-full popular">
					<h3>Статьи</h3>
					<div class="block-1-2 block-m-full popular__posts">
						<xsl:for-each select="page/custom">
							<article class="col-block popular__post">
								<a href="#0" class="popular__thumb">
									<img src="{concat(@path,small_pic)}" alt="header"/>
								</a>
								<h5><a href="{show_page}"><xsl:value-of select="header"/></a></h5>
								<section class="popular__meta">
									<!--<span class="popular__author"><span>By</span> <a href="#0"> John Doe</a></span>-->
									<!--<span class="popular__date"><span>on</span> <time datetime="2017-12-19">Dec 19, 2017</time></span>-->
								</section>
							</article>
						</xsl:for-each>
					</div>
				</div>
				<div class="col-four md-six tab-full about">
					<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
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
