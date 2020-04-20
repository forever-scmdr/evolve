<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$ni/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>


	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="parent" select="/page/news[@id = $ni/news[1]/@id]" />
	<xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>
	<xsl:variable name="format" select="if($ni/video_url != '') then 'video' else if($ni/top_gal/main_pic != '') then 'gallery' else 'standard'"/>

	<xsl:template name="TWITTER_MARKUP">
		<meta name="twitter:card" content="summary_large_image" />
		<meta name="twitter:image" content="{concat($main_host, '/',$ni/@path, if( $ni/soc_image != '') then $ni/soc_image else $ni/main_pic)}"/>
		<meta name="twitter:image:alt" content="{$h1}" />
	</xsl:template>

	<xsl:template name="FACEBOOK_MARKUP">
		<meta property="og:url" content="{concat($main_host, $canonical)}" />
		<!-- <meta property="og:type" content="article" /> -->
		<meta property="og:locale" content="ru_RU" />
		<meta property="og:title" content="{$h1}" />
		<meta property="og:description" content="{$ni/twitter_description}" />
		<meta property="og:image" content="{concat($main_host, '/',$ni/@path,  if( $ni/soc_image != '') then $ni/soc_image else $ni/main_pic)}" />
		<meta name="og:app_id" content="552626568232392" />
	</xsl:template>


	<xsl:template name="CONTENT">
		<section class="s-content s-content--narrow s-content--no-padding-bottom white">
			<article class="row format-{$format}">
				<div class="s-content__header col-full">
					<h1 class="s-content__header-title">
						<xsl:value-of select="$h1"/>
					</h1>
					<ul class="s-content__header-meta">
						<xsl:if test="$ni/source">
							<li class="cat">
								Источник: <a href="{$ni/source_link}" >
									<xsl:value-of select="$ni/source"/>
								</a>
							</li>
						</xsl:if>

						<li class="date" data-utc="{$ni/date/@millis}">
							<xsl:value-of select="f:utc_millis_to_bel_date($ni/date/@millis)"/>
							<xsl:if test="$ni/update != ''">&#160;(обновлено: <xsl:value-of select="$ni/update"/>)</xsl:if>
						</li>
						<li class="cat">
							Категория:	<a href="{$parent/show_page}" >
								<xsl:value-of select="$parent/name"/>
							</a>
						</li>
					</ul>
					<xsl:text disable-output-escaping="yes">
						&lt;!--noindex--&gt;&lt;!--googleoff: index--&gt;
					</xsl:text>
					<xsl:if test="$ni/complexity != '' or $ni/read_time != '' or $ni/size != ''">
						<div class="tags">
							<xsl:if test="$ni/complexity != ''">
								<span class="entry__category yellow" style="padding-left: .5rem;">
								<span style="
display: inline-block;
margin-top:-1px;
height: 2rem;
vertical-align: middle;
line-height: 2rem;
border-radius: 2rem;
width: 2rem;
background: #fff;
text-transform: lowercase;
font-size: 1.5rem;
font-family: georgia;
 cursor: pointer;
font-style: italic;
" class="tip desctop-only" title="A1 - текст будет понятен для широкого круга читателей&#13;B2 - текст будет понятен тем, кто в какой-то степени уже знаком с данной тематикой и обладает какой-то информацией и знаниями&#13;C3 - сложный текст, в основном для людей, которые наверняка разбираются в данной сфере">i</span>
								
									<a style="padding-left: .5rem;">Сложность: <b><xsl:value-of select="$ni/complexity" /></b></a>
								</span>
							</xsl:if>
							<xsl:if test="$ni/read_time != ''">
								<span class="entry__category blue">
									<a style="padding-left: .5rem;">Время прочтения: <b id="read-time-"><xsl:value-of select="$ni/read_time" /></b></a>
								</span>
							</xsl:if>
							<xsl:if test="$ni/size != ''">
								<span class="entry__category red" style="padding-left: .5rem;">
								<span style="display: inline-block;
margin-top:-1px;
height: 2rem;
vertical-align: middle;
line-height: 2rem;
border-radius: 2rem;
width: 2rem;
background: #fff;
text-transform: lowercase;
font-size: 1.5rem;
font-family: georgia;
 cursor: pointer;
font-style: italic;" class="tip desctop-only" title="Маленький - до 400 слов&#13;Средний - до 800 слов&#13;Большой - свыше 800 слов">i</span>
								
									<a >Размер статьи: <b><xsl:value-of select="$ni/size" /></b></a>
								</span>
							</xsl:if>
							<xsl:text disable-output-escaping="yes">
								&lt;!--googleon: index--&gt;&lt;!--/noindex--&gt;
							</xsl:text>
						</div>
					</xsl:if>
				</div>

				<xsl:call-template name="NEWS_TOPPER"/>

<!--				<div class="col-full s-content__main">-->
				 <div class="col-nine md-eight tab-full s-content__main">
					<div class="content-text">
						<xsl:apply-templates select="$ni" mode="content"/>
						<!-- Telegram -->
						<p class="_article_paragraph article_paragraph"><strong>Подписывайтесь на наш <a href="https://t.me/temptingpro" target="_blank">Telegram-канал</a> и получайте актуальную информацию из мира новостей еще быстрее.</strong></p>
						<xsl:if test="$ni/author != ''">
							<p>
								<em>Автор: <xsl:value-of select="$ni/author"/></em>
							</p>
						</xsl:if>
					</div>
				</div>
				<div class="col-three md-four tab-full desctop-only latest">
					<h3 style="margin-top:0;">Последние статьи</h3>
					<xsl:for-each select="page/latest">
						<article class="col-block">
							<a href="{show_page}" class="popular__thumb">
								<img src="{concat('https://tempting.pro/',@path,small_pic)}" alt="{name}"/>
							</a>
<!--							<section class="popular__meta">-->
<!--								<span class="popular__date"><time datetime="{date}" data-utc="{date/@millis}"><xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/></time></span>-->
<!--							</section>-->
							<h5><a href="{show_page}"><xsl:value-of select="name"/></a></h5>
						</article>
					</xsl:for-each>
				</div>
				<div class="col-full s-content__main">
					<xsl:call-template name="BANNER_FOLLOW"/>
					<div style="margin-bottom: 1.5rem;"></div>
					<xsl:call-template name="BANNER_DONATE"/>

					<xsl:if test="$ni/tag">
						<p class="s-content__tags">
							<span>Теги</span>
							<span class="s-content__tag-list">
								<xsl:for-each select="$ni/tag">
									<a href="{concat('tag/?tag=', .)}">
										<xsl:value-of select="."/>
									</a>
								</xsl:for-each>
							</span>

						</p>
					</xsl:if>
					<div class="ya-share2" data-services="vkontakte,facebook,twitter" data-limit="3"></div>

					<xsl:call-template name="ALSO"/>

					<xsl:call-template name="PREV-NEXT" />
				</div>
			</article>

			<xsl:call-template name="COMMENTS"/>

		</section>

		<!-- popular_posts -->
		<section class="s-extra">
			<div class="row top">
				<div class="col-full md-six tab-full popular mobile-only">
					<h3>Последние статьи</h3>
					<xsl:for-each select="page/latest">
						<article class="col-block popular__post">
							<a href="{show_page}" class="popular__thumb">
								<img src="{concat(@path,small_pic)}" alt="{name}"/>
							</a>
							<h5><a href="{show_page}"><xsl:value-of select="name"/></a></h5>
							<section class="popular__meta">
								<span class="popular__date"><time datetime="{date}" data-utc="{date/@millis}"><xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/></time></span>
							</section>
						</article>
					</xsl:for-each>
				</div>
			</div>

			<div class="row top">
				<div class="col-full md-six tab-full popular">
				<h3>Популярные статьи</h3>
					<div class="block-1-3 block-m-full popular__posts">
						<xsl:for-each select="page/popular">
							<article class="col-block popular__post">
								<a href="{show_page}" class="popular__thumb">
									<img src="{concat(@path,small_pic)}" alt="{name}"/>
								</a>
								<h5><a href="{show_page}"><xsl:value-of select="name"/></a></h5>
								<section class="popular__meta">
									<xsl:if test="source != ''">
										<!--<span class="popular__author">-->
										<!--<span>Источник</span> <a href="{source_link}"><xsl:value-of select="source"/></a>-->
										<!--</span>-->
									</xsl:if>
									<span class="popular__date"><time datetime="{date}" data-utc="{date/@millis}"><xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/></time></span>
								</section>
							</article>
						</xsl:for-each>
					</div>
				</div>
			</div>
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
					<time class="comment__time"><xsl:value-of select="f:utc_millis_to_bel_date($comment/date/@millis)"/></time>
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

	<xsl:template name="ALSO">
		<xsl:if test="$ni/news/news_item">
			<div class="also">
				<div class="also-header">
					Читайте также:
				</div>
				<xsl:for-each-group select="$ni/news/news_item" group-by="@id">
					<xsl:if test="position() &lt; 4">
						<a href="{current-group()[1]/show_page}">
							<xsl:value-of select="current-group()[1]/name"/>
						</a>
					</xsl:if>
				</xsl:for-each-group>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PREV-NEXT">
		<xsl:if test="$ni/news/prev|$ni/news/next">
			<div class="s-content__pagenav">
				<div class="s-content__nav">
					<xsl:if test="$ni/news/prev">
						<div class="s-content__prev">
							<a href="{$ni/news/prev/show_page}" rel="prev">
								<span>Предыдущая статья</span>
								<img src="{concat($ni/news/prev/@path, $ni/news/prev/small_pic)}" alt=""/>
								<xsl:value-of select="$ni/news/prev/name"/>
								
								
							</a>
						</div>
					</xsl:if>
					<xsl:if test="$ni/news/next">
						<div class="s-content__next">
							<a href="{$ni/news/next/show_page}" rel="next">
								<span>Следующая статья</span>
								<img src="{concat($ni/news/next/@path, $ni/news/next/small_pic)}" alt=""/>
								<xsl:value-of select="$ni/news/next/name"/>
								
								
							</a>
						</div>
					</xsl:if>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
		<script src="//yastatic.net/share2/share.js"></script>
		
	</xsl:template>

	<xsl:template name="NEWS_TOPPER">
		<xsl:variable name="has_big_img" select="$ni/main_pic | $ni/top_gal/main_pic | $ni/video_url != ''" />
		<xsl:variable name="has_audio" select="$ni/main_audio != ''"/>

		<xsl:if test="$has_big_img">
			<xsl:if test="$format = 'standard'">
				<div class="s-content__media col-full">
					<div class="s-content__post-thumb">
						<figure>
						<img src="{concat('https://tempting.pro/', $ni/@path, $ni/main_pic)}"
							 srcset="{concat('https://tempting.pro/',$ni/@path, $ni/main_pic)} 2000w,
									 {concat('https://tempting.pro/',$ni/@path, $ni/medium_pic)} 1000w,
									 {concat('https://tempting.pro/',$ni/@path, $ni/small_pic)} 500w"
							 sizes="(max-width: 2000px) 100vw, 2000px" alt="" />
							<xsl:if test="$has_audio">
								<div class="audio-wrap" >
									<audio id="player{$ni/@id}" src="{concat($ni/@path, $ni/main_audio)}" width="100%" height="42" controls="controls"/>
								</div>
							</xsl:if>
							<figcaption>
								<xsl:value-of select="$ni/figcaption"/>
							</figcaption>
						</figure>
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
						<xsl:if test="$has_audio">
							<div class="audio-wrap">
								<audio id="player2" src="{concat($ni/@path, $ni/main_audio)}" width="100%" height="42" controls="controls"/>
							</div>
						</xsl:if>
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
		</xsl:if>
		<xsl:if test="not($has_big_img) and $has_audio">
			<div class="s-content__media col-full">
				<div class="s-content__post-thumb">
					<div class="audio-wrap">
						<audio id="player{$ni/@id}" src="{concat($ni/@path, $ni/main_audio)}" width="100%" height="42" controls="controls"/>
					</div>
				</div>
			</div>
		</xsl:if>

		<xsl:if test="not($has_big_img) and not($has_audio) and $format = 'standard'">
			<div class="s-content__media col-full">
				<!-- <div style="margin-bottom: 2rem; background: lime;"></div> -->
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>