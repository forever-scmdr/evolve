<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="pages" select="//news_item_pages"/>
	<xsl:variable name="p" select="number(page/variables/page)+1"/>
	
	<xsl:template match="/">
		<div>
			<xsl:apply-templates select="/page/news_wrap/news_item" mode="masonry"/>
			<xsl:if test="$pages/next">
				<div class="row">
					<div class="col-full">
						<nav class="pgn">
							<ul>
								<li id="load_more">
									<a class="pgn__num" id="load-more-link"
									   href="{$pages/page[$p]/link}">Загрузить еще
									</a>
								</li>
							</ul>
						</nav>
					</div>
				</div>
			</xsl:if>
		</div>
	</xsl:template>



</xsl:stylesheet>
