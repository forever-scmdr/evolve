<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="p" select="page/variables/p"/>

	<xsl:template match="/">
		<div>
			<xsl:apply-templates select="//news_item" mode="masonry"/>
		</div>
	</xsl:template>


	<!--<xsl:template match="news_item" mode="masonry">-->

		<!--<xsl:variable name="category" select="if(../name() = 'text_part') then ../news else news" />-->
		<!--<xsl:variable name="format" select="if(video_url != '') then 'video' else if(top_gal/main_pic != '') then 'gallery' else 'standard'"/>-->
		<!--<article class="masonry__brick entry format-{$format}" data-aos="fade-up" style="background: lime;">-->
			<!--&lt;!&ndash; STANDARD &ndash;&gt;-->
			<!--<xsl:if test="$format = 'standard'">-->
				<!--<div class="entry__thumb">-->
					<!--<a href="{show_page}" class="entry__thumb-link">-->
						<!--<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt="{concat('Cтраница: ', $p, '. Новость: ', position())}"/>-->
					<!--</a>-->
				<!--</div>-->
			<!--</xsl:if>-->

			<!--&lt;!&ndash; VIDEO &ndash;&gt;-->
			<!--<xsl:if test="$format = 'video'">-->
				<!--<div class="entry__thumb video-image">-->
					<!--<a href="{video_url}" data-lity="">-->
						<!--<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>-->
					<!--</a>-->
				<!--</div>-->
			<!--</xsl:if>-->

			<!--<xsl:if test="$format = 'gallery'">-->
				<!--<div class="entry__thumb slider">-->
					<!--<div class="slider__slides">-->
						<!--<xsl:variable name="path" select="top_gal/@path"/>-->
						<!--<xsl:for-each select="top_gal/small_pic">-->
							<!--<xsl:variable name="p" select="position()"/>-->
							<!--<div class="slider__slide">-->
								<!--<img src="{concat($path,.)}" srcset="{concat($path,.)} 1x, {concat($path,../medium_pic[$p])} 2x" alt=""/>-->
							<!--</div>-->
						<!--</xsl:for-each>-->
					<!--</div>-->
				<!--</div>-->
			<!--</xsl:if>-->

			<!--&lt;!&ndash; TEXT &ndash;&gt;-->
			<!--<div class="entry__text">-->
				<!--<div class="entry__header">-->
					<!--<div class="entry__date">-->
						<!--<a href="{show_page}" data-utc="{date/@millis}"><xsl:value-of select="date"/></a>-->
					<!--</div>-->
					<!--<div class="h1 entry__title"><a href="{show_page}"><xsl:value-of select="name"/></a></div>-->
				<!--</div>-->
				<!--<div class="entry__excerpt">-->
					<!--<xsl:value-of select="short" disable-output-escaping="yes"/>-->
				<!--</div>-->
				<!--<div class="entry__meta">-->
					<!--<span class="entry__meta-links">-->
						<!--<a href="{$category/show_page}">-->
							<!--<xsl:value-of select="$category/name"/>-->
						<!--</a>-->
					<!--</span>-->
				<!--</div>-->
			<!--</div>-->

		<!--</article>-->
	<!--</xsl:template>-->

</xsl:stylesheet>