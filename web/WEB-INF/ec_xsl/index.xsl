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
					<xsl:if test="/page/small_news_item">
						<article class="masonry__brick entry format-standard" data-aos="fade-up">
							<div class="entry__text">
								<div class="entry__header">
									<div class="h1 entry__title">
										Последние новости:
									</div>
								</div>
								<xsl:for-each select="/page/small_news_item">
									<div class="brief-news">
										<div class="date">
											<xsl:value-of select="date"/>
										</div>
										<div>
											<a href="{show_page}">
												<xsl:value-of select="name"/>
											</a>
										</div>
									</div>
								</xsl:for-each>
								<div class="entry__meta caps">
									<span class="entry__meta-links">
										<a href="{page/news_link}">
											Все новости >
										</a>
									</span>
								</div>
							</div>
						</article>
					</xsl:if>
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
					<h3>Популярные статьи</h3>
					<div class="block-1-2 block-m-full popular__posts">
						<xsl:for-each select="page/popular">
							<article class="col-block popular__post">
								<a href="#0" class="popular__thumb">
									<img src="{concat(@path,small_pic)}" alt="{name}"/>
								</a>
								<h5><a href="{show_page}"><xsl:value-of select="name"/></a></h5>
								<section class="popular__meta">
									<xsl:if test="source != ''">
										<span class="popular__author">
											<span>Источник</span> <a href="{source_link}"><xsl:value-of select="source"/></a>
										</span>
									</xsl:if>
									<span class="popular__date"><time datetime="{date}"><xsl:value-of select="date"/></time></span>
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
						<a href="{cat_link}">
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
