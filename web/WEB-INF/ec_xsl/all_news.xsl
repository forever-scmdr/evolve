<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="quot">"</xsl:variable>
	<xsl:variable name="dt" select="if(page/variables/min_date != '') then concat(' за', f:day_month_year(f:millis_to_date(page/variables/min_date))) else ''"/>
	<xsl:variable name="postfix" select="if(page/variables/tag != '') then concat($dt, ' по тегу ',$quot, page/variables/tag, $quot) else ''" />
	<xsl:variable name="title" select="concat('Статьи', $postfix)" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>

	<xsl:variable name="pagination" select="/page/news_wrap/news_item_pages"/>
	<xsl:variable name="prev" select="$pagination/page[number(/page/variables/p)-1]"/>
	<xsl:variable name="next" select="$pagination/page[number(/page/variables/p)+1]"/>
	<xsl:variable name="quote">"</xsl:variable>
	<xsl:variable name="pl" select="//page_link"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						<xsl:value-of select="$h1"/>
					</h1>
					<xsl:if test="page/tag">
							<a href="all_news" class="all" >
								Все статьи
							</a>
							<p class="s-content__tags" style="margin-top:0;text-align: left;">
								<!-- <span>Теги</span> -->
								<!-- <span class="s-content__tag-list" > -->
									
									<!-- <xsl:for-each select="page/tag">
										<a href="{show_tag}">
											<xsl:value-of select="tag"/>
										</a>
									</xsl:for-each> -->
								<!-- </span> -->
							</p>
						
						<!-- <div class="tags">
							<span class="entry__category gray">
								<a href="all_news">
									Все
								</a>
							</span>
							<xsl:for-each select="page/tag">
								<xsl:variable name="class">
									<xsl:choose>
										<xsl:when test="tag = 'Бизнес'">dark-blue</xsl:when>
										<xsl:when test="tag = 'Политика'">red</xsl:when>
										<xsl:when test="tag = 'Технологии'">yellow</xsl:when>
										<xsl:when test="tag = 'Инфографика'">orange</xsl:when>
										<xsl:when test="tag = 'Менеджмент'">blue</xsl:when>
										<xsl:otherwise>gray</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>

								<span class="entry__category {$class}">
									<a href="{show_tag}">
										<xsl:value-of select="tag"/>
									</a>
								</span>
							</xsl:for-each>
						</div> -->
					</xsl:if>
					<div class="lead">
						<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="/page/news_wrap/news_item" mode="masonry"/>
				</div>
			</div>
			<xsl:if test="$pagination">
				<!-- <div class="row">
					<div class="col-full">
						<nav class="pgn">
							<ul>
								<xsl:if test="$prev">
									<li><a class="pgn__prev" href="{$prev/link}">Prev</a></li>
								</xsl:if>
								<xsl:for-each select="$pagination/page">
									<xsl:if test="not(@current = 'current')">
										<li><a class="pgn__num" href="{link}"><xsl:value-of select="number"/></a></li>
									</xsl:if>
									<xsl:if test="@current = 'current'">
										<li><span class="pgn__num current"><xsl:value-of select="number"/></span></li>
									</xsl:if>
								</xsl:for-each>
								<xsl:if test="$next">
								<li><a class="pgn__next" href="{$next/link}">Next</a></li>
								</xsl:if>
							</ul>
						</nav>
					</div>
				</div> -->
				<script>
					window.pagination = <xsl:value-of select="concat('[',string-join($pagination/page[position() &gt; 1]/concat($quote, $pl,'&amp;page=', number, $quote), ','),']')" />;
				</script>
			</xsl:if>
		</section>
	</xsl:template>



</xsl:stylesheet>