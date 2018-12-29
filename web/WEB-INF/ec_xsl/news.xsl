<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="/page/selected_news/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>

	<xsl:variable name="pagination" select="/page/selected_news/news_item_pages"/>
	<xsl:variable name="prev" select="$pagination/page[number(/page/variables/p)-1]"/>
	<xsl:variable name="next" select="$pagination/page[number(/page/variables/p)+1]"/>

	<xsl:template name="CONTENT">
		<section class="s-content">
			<div class="row narrow">
				<div class="col-full s-content__header" data-aos="fade-up">
					<h1>
						<xsl:value-of select="$h1"/>
					</h1>
					<div class="lead">
						<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
			<div class="row masonry-wrap">
				<div class="masonry" id="add-content">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="/page/selected_news/news_item" mode="masonry"/>
				</div>
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