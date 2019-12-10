<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="qot">"</xsl:variable>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<xsl:variable name="title" select="if(page/@name = 'search') then concat('Поиск по запросу ', page/variables/q) else concat('Новости и статьи по тегу ', page/variables/tag)" />

	<xsl:variable name="news_items" select="/page/news_item | page/news_wrap/news_item"/>
	<xsl:variable name="news_parts" select="/page/text_part[news_item]"/>

	<xsl:variable name="small_news" select="page/small_news_item | page/small_news/small_news_item"/>
	<xsl:variable name="h1_1" select="if(page/@name = 'search') then concat('Новости по запросу: ',$qot,page/variables/q,$qot) else  concat('Новости по тегу: ',$qot,page/variables/tag, $qot)" />
	<xsl:variable name="h1_2" select="if(page/@name = 'search') then concat('Статьи по запросу: ',$qot,page/variables/q,$qot) else  concat('Статьи по тегу: ',$qot,page/variables/tag,$qot)" />

	<xsl:template name="CONTENT">
		<section class="s-content">
			<xsl:if test="count($small_news) &gt; 0">
				<div class="row narrow">
					<div class="col-full s-content__header" data-aos="fade-up">
						<h1>
							<xsl:value-of select="$h1_1"/>
						</h1>
					</div>
				</div>
				<div class="row masonry-wrap">
					<div class="" id="small-news">
						<xsl:for-each select="$small_news">
							<xsl:sort select="number(date/@millis)" order="descending"/>
							<div class="col-four tab-full small-news-item" data-aos="fade-up">
								<!-- <div class="col-four tab-full small-news-item masonry__brick" data-aos="fade-up"> -->
								<p class="date" data-utc="{date/@millis}">
									<xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/>
									<xsl:if test="update != ''">&#160;(обновлено: <xsl:value-of select="update"/>)</xsl:if>
								</p>
								<p class="name{if(not(tag)) then ' botmar' else ' mar-0'}">
									<a href="{show_page}">
										<xsl:value-of select="name"/>
									</a>
								</p>
							</div>

							<xsl:variable name="pos" select="position()"/>
							<xsl:if test="$pos mod 3 = 0">
								<div class="three-col-border"></div>
							</xsl:if>
						</xsl:for-each>
					</div>
					<xsl:if test="page/small_news/small_news_item_pages">
						<xsl:variable name="last" select="count(page/small_news/small_news_item_pages/page)"/>
						<xsl:variable select="number(page/small_news/small_news_item_pages/page[@current = 'current']/number)" name="z"/>
						<div class="row">
							<div class="col-full">
								<nav class="pgn">
									<ul>
										<li id="load_more">
											<a href="{page/small_page_link}&amp;page={$z+1}" rel="#small-news" data-page="{$z}/{$last}" class="pgn__num load-more-small-link">
												Загрузить еще
											</a>
										</li>
									</ul>
								</nav>
							</div>
						</div>
					</xsl:if>
				</div>
				<div style="margin-bottom: 2.5rem;"></div>
			</xsl:if>
			<xsl:if test="count($news_items|$news_parts) &gt; 0">
				<div class="row narrow">
					<div class="col-full s-content__header" data-aos="fade-up">
						<h1>
							<xsl:value-of select="$h1_2"/>
						</h1>
					</div>
				</div>
				<div class="row masonry-wrap">
					<div class="masonry" id="add-content">
						<div class="grid-sizer"></div>
						<xsl:for-each select="$news_items | $news_parts">
							<xsl:sort select="number((if(date) then date else /../date)/@millis)" order="descending"/>
							<xsl:apply-templates select="." mode="masonry"/>
						</xsl:for-each>
					</div>
					<xsl:if test="page/news_wrap/news_item_pages">
						<xsl:variable name="last" select="count(page/news_wrap/news_item_pages/page)"/>
						<xsl:variable select="number(page/news_wrap/news_item_pages/page[@current = 'current']/number)" name="z"/>
						<div class="row">
							<div class="col-full">
								<nav class="pgn">
									<ul>
										<li id="load_more">
											<a href="{page/page_link}&amp;page={$z+1}" rel="#add-content" data-page="{$z}/{$last}" class="pgn__num load-more-small-link">
												Загрузить еще
											</a>
										</li>
									</ul>
								</nav>
							</div>
						</div>
					</xsl:if>
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
	</xsl:template>

	<xsl:template match="small_news_item" mode="masonry">
		<div class="col-four tab-full small-news-item" data-aos="fade-up" style="background: transparent;">
			<p class="date" data-utc="{date/@millis}">
				<xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/>
			</p>
			<p class="name{if(not(tag)) then ' botmar' else ' mar-0'}">
				<a href="{show_small_news_item}">
					<xsl:value-of select="name"/>
				</a>
			</p>
		</div>
		<xsl:variable name="pos" select="position()"/>
		<xsl:if test="$pos mod 3 = 0">
			<div class="three-col-border"></div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="news_item" mode="masonry">

		<xsl:variable name="category" select="if(../name() = 'text_part') then ../news else news" />
		<xsl:variable name="format" select="if(video_url != '') then 'video' else if(top_gal/main_pic != '') then 'gallery' else 'standard'"/>

		<article class="masonry__brick entry format-{$format}" data-aos="fade-up">
			<!-- STANDARD -->
			<xsl:if test="$format = 'standard'">
				<div class="entry__thumb">
					<a href="{show_news_item}" class="entry__thumb-link">
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
						<a href="{show_news_item}" data-utc="{date/@millis}"><xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/></a>
					</div>
					<div class="h1 entry__title"><a href="{show_news_item}"><xsl:value-of select="name"/></a></div>
				</div>
				<div class="entry__excerpt">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
				<div class="entry__meta">
					<span class="entry__meta-links">
						<a href="{$category/show_page}">
							<xsl:value-of select="$category/name"/>
						</a>
					</span>
				</div>
			</div>

		</article>
	</xsl:template>



</xsl:stylesheet>