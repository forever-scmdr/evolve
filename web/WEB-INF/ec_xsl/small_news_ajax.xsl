<xsl:stylesheet version="2.0" xmlns:f="f:f" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>

	<xsl:template match="/">
		<xsl:for-each select="page/small_news/small_news_item">
			<div class="col-four tab-full small-news-item" data-aos="fade-up">
				<!-- <div class="col-four tab-full small-news-item masonry__brick" data-aos="fade-up"> -->
				<p class="date" data-utc="{date/@millis}">
					<xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/>
				</p>
				<p class="name{if(not(tag)) then ' botmar' else ' mar-0'}">
					<a href="{show_page}">
						<xsl:value-of select="name"/>
					</a>
				</p>
				<!-- <xsl:if test="tag">
					<p class="tags botmar">
						Теги: <xsl:for-each select="tag">
						<xsl:if test="position() &gt; 1">
							<xsl:text>, </xsl:text>
						</xsl:if>
						<a href="{concat('news/?tag=', .)}">
							<xsl:value-of select="."/>
						</a>
					</xsl:for-each>
					</p>
				</xsl:if> -->
			</div>

			<xsl:variable name="pos" select="position()"/>
			<!-- <xsl:if test="$pos mod 2 = 0">
				<div class="two-col-border"></div>
			</xsl:if> -->
			<xsl:if test="$pos mod 3 = 0">
				<div class="three-col-border"></div>
			</xsl:if>

		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>