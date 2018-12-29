<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$ni/name" />
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
					<xsl:if test="$ni/tag">
						<div class="tags">
							<xsl:for-each select="$ni/tag">
								<xsl:variable name="class">
									<xsl:choose>
										<xsl:when test=". = 'Бизнес'">dark-blue</xsl:when>
										<xsl:when test=". = 'Политика'">red</xsl:when>
										<xsl:when test=". = 'Технологии'">yellow</xsl:when>
										<xsl:when test=". = 'Инфографика'">orange</xsl:when>
										<xsl:when test=". = 'Менеджмент'">blue</xsl:when>
										<xsl:otherwise>gray</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>

								<span class="entry__category {$class}">
									<a href="{concat('all_news/?tag=', .)}">
										<xsl:value-of select="." />
									</a>
								</span>
							</xsl:for-each>
						</div>
					</xsl:if>
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

			<xsl:call-template name="COMMENTS"/>

		</section>
	</xsl:template>

	<xsl:template name="COMMENTS">
		<xsl:if test="$ni/comments">
			<div class="comments-wrap">
				<div id="comments" class="row">
					<div class="col-full">
						<xsl:if test="$ni/comments/comment">
							<h3 class="h2"><xsl:value-of select="count($ni/comments//comment)"/> Комментариев</h3>
							<ol class="commentlist" id="comment-{$ni/comments/@id}">
								<xsl:for-each select="$ni/comments/comment">
									<xsl:call-template name="COMMENT">
										<xsl:with-param name="comment" select="."/>
										<xsl:with-param name="level" select="1" />
									</xsl:call-template>
								</xsl:for-each>
							</ol>
						</xsl:if>
						<div class="respond" id="modal-feedback" ajax-href="{$ni/comments/comment_link}" show-loader="yes">
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not($ni/comments)">
			<div style="height: 3rem;"></div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="COMMENT">
		<xsl:param name="comment"/>
		<xsl:param name="level"/>

		<li class="{'thread-alt '[$level = 0]}depth-{$level+1} comment" id="cmt-{$comment/@id}">
			<div class="comment__content">
				<cite><xsl:value-of select="$comment/name"/></cite>
				<div class="comment__meta">
					<time class="comment__time"><xsl:value-of select="$comment/date"/></time>
					<a class="reply" href="{$comment/@id}">Ответить</a>
				</div>
				<div class="comment__text">
					<xsl:value-of select="$comment/text" disable-output-escaping="yes"/>
				</div>
				<xsl:if test="$comment/comment">
					<ul class="children">
						<xsl:for-each select="$comment/comment">
							<xsl:call-template name="COMMENT">
								<xsl:with-param name="comment" select="."/>
								<xsl:with-param name="level" select="$level+1" />
							</xsl:call-template>
						</xsl:for-each>
					</ul>
				</xsl:if>
			</div>
		</li>

	</xsl:template>

</xsl:stylesheet>