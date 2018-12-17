<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$ni/header" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>


	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>
	<xsl:variable name="format" select="if($ni/video_url != '') then 'video' else if($ni/top_gal/main_pic != '') then 'gallery' else 'standard'"/>


	<xsl:template name="CONTENT">
		<section class="s-content s-content--narrow s-content--no-padding-bottom">
			<article class="row format-{$format}">
				<div class="s-content__header col-full">
					<h1 class="s-content__header-title">
						<xsl:value-of select="$h1"/>
					</h1>
					<ul class="s-content__header-meta">
						<li class="date"><xsl:value-of select="date"/></li>
						<li class="cat">
							<xsl:for-each select="tag">
								<a href="{tag-link}">
									<xsl:value-of select="tag"/>
								</a>
							</xsl:for-each>
						</li>
					</ul>
				</div>

				<xsl:if test="$format = 'standard'">
					<div class="s-content__media col-full">
						<div class="s-content__post-thumb">
							<img src="{concat($ni/@path, $ni/main_pic)}"
								 srcset="{concat($ni/@path, $ni/main_pic)} 2000w,
                                 {concat($ni/@path, $ni/medium_pic)} 1000w,
                                 {concat($ni/@path, $ni/small_pic)} 500w"
								 sizes="(max-width: 2000px) 100vw, 2000px" alt="" />
						</div>
					</div>
				</xsl:if>

				<xsl:if test="$format = 'gallery'">
					<div class="s-content__media col-full">
						<div class="s-content__slider slider">
							<div class="slider__slides">
								<xsl:variable name="gal" select="$ni/top_gal"/>
								<xsl:variable name="path" select="$gal/@path"/>
								<xsl:for-each select="$ni/top_gal/main_pic">
									<xsl:variable name="p" select="position()"/>
									<div class="slider__slide">
										<img src="{concat($path, .)}"
											 srcset="{concat($path, .)} 2000w,
											 {concat($path, $gal/medium_pic[$p])} 1000w,
											 {concat($path, $gal/small_pic[$p])} 500w"
											 sizes="(max-width: 2000px) 100vw, 2000px" alt=""/>
									</div>
								</xsl:for-each>
							</div>
						</div>
					</div>
				</xsl:if>

				<xsl:if test="$format = 'video'">
					<div class="s-content__media col-full">
						<div class="video-container">
							<iframe src="{$ni/video_url}" width="640" height="360" frameborder="0" webkitallowfullscreen="" mozallowfullscreen="" allowfullscreen=""></iframe>
						</div>
					</div>
				</xsl:if>

				<div class="col-full s-content__main">
					<xsl:apply-templates select="$ni" mode="content"/>
					<div class="s-content__pagenav">
						<xsl:variable name="parent" select="/page/news[@id = $ni/news/@id]" />
						<div class="s-content__nav">
							<div class="s-content__next">
								<a href="{$parent/show_page}" rel="next">
									<span>Еще</span>
									<xsl:value-of select="$parent/name"/>
								</a>
							</div>
						</div>
					</div>
				</div>

			</article>
		</section>
	</xsl:template>

</xsl:stylesheet>