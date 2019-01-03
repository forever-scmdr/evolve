<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="quot">"</xsl:variable>
	<xsl:variable name="dt" select="if(page/variables/min_date != '') then concat(' за', f:day_month_year(f:millis_to_date(page/variables/min_date))) else ''"/>
	<xsl:variable name="postfix" select="if(page/variables/tag != '') then concat($dt, ' по тегу ',$quot, page/variables/tag, $quot) else ''" />
	<xsl:variable name="title" select="concat('Новости', $postfix)" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>

	<xsl:variable name="pagination" select="/page/small_news/small_news_item_pages"/>
	<xsl:variable name="prev" select="$pagination/page[number(/page/variables/p)-1]"/>
	<xsl:variable name="next" select="$pagination/page[number(/page/variables/p)+1]"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						<xsl:value-of select="$h1"/>
					</h1>
					<xsl:if test="page/tag">
						<div class="tags">
							<span class="entry__category gray">
								<a href="{page/news_link}">
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
						</div>
					</xsl:if>
					<div class="lead">
						<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>

			<div class="row">
				<xsl:for-each select="page/small_news/small_news_item">
					<div class="col-four tab-full small-news-item">
						<p class="date">
							<xsl:value-of select="date"/>
						</p>
						<p class="name{if(not(tag)) then ' botmar' else ' mar-0'}">
							<a href="{show_page}">
								<xsl:value-of select="name"/>
							</a>
						</p>
						<xsl:if test="tag">
						<p class="tags botmar">
							Теги: <xsl:for-each select="tag">
								<xsl:if test="position() &gt; 1">
									<xsl:text>, </xsl:text>
								</xsl:if>
								<a href="{concat('small_news/?tag=', .)}">
									<xsl:value-of select="."/>
								</a>
							</xsl:for-each>
						</p>
						</xsl:if>
					</div>
				</xsl:for-each>
			</div>

			<xsl:if test="$pagination">
				<div class="row">
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
				</div>
			</xsl:if>
		</section>
	</xsl:template>



</xsl:stylesheet>