<xsl:stylesheet version="2.0" xmlns:f="f:f" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>

	<xsl:template match="/">
		<xsl:for-each select="//news_item">
			<a href="{show_page}" class="item">
				<img src="{@path}{small_pic}" alt="{name}" />
				<span class="text_box">
				<span class="top_info_box">
				<!-- <div class="name"><xsl:value-of select="if (source != '') then source else 'Respectiva'"/></div> -->
				<span class="dot"></span>
				<span class="when"><xsl:value-of select="date"/></span>
				</span>
					<span class="title"><xsl:value-of select="name"/></span>
					<p><xsl:value-of select="twitter_description"/></p>
					<span class="time_to_read"><xsl:value-of select="read_time"/> читать</span>
				</span>
			</a>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>