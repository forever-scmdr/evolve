<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="extra-header-class" select="' s-pageheader--home'"/>

	<xsl:template name="CONTENT">
		<section class="s-content">

			<div class="row masonry-wrap">
				<div class="masonry">
					<div class="grid-sizer"></div>
					<xsl:apply-templates select="page/news_item" mode="masonry"/>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="news_item" mode="masonry">
		<article class="masonry__brick entry format-standard" data-aos="fade-up">
			<div class="entry__thumb">
				<a href="{show_page}" class="entry__thumb-link">
					<img src="@path/main_pic" alt="{name}" />
				</a>
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
