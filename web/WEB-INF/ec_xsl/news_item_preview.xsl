<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="ni" select="/page/search/success/(news_item | small_news_item)"/>

	<xsl:variable name="title" select="$ni/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>
	<xsl:variable name="meta_description" select="$ni/twitter_description"/>
	<xsl:variable name="meta_keywords" select="string-join(($ni/tag), ', ')"/>


	<xsl:variable name="parent" select="/page/news[@id = $ni/news[1]/@id]" />
	<xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>
	<xsl:variable name="format" select="if($ni/video_url != '') then 'video' else if($ni/top_gal/main_pic != '') then 'gallery' else 'standard'"/>

	<xsl:variable name="utm" select="concat('utm_source=tempting&amp;utm_medium=banner&amp;utm_campaign=', $ni/@key, '&amp;utm_term=', $parent/@key)"/>

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
		<!-- <section class="s-content s-content- -no-padding-bottom s-content- -no-padding-top white"> -->
		<section class="s-content s-content--narrow s-content--no-padding-bottom s-content--no-padding-top white">
			<article class="row format-{$format} white">
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
						<noindex>
							<div class="tags">
								<xsl:if test="$ni/complexity != ''">
									<span id="complexity" class="entry__category yellow" style="padding-left: .5rem;">
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
									
										<!-- <a style="padding-left: .5rem;">Сложность: <b><xsl:value-of select="$ni/complexity" /></b></a> -->
									</span>
								</xsl:if>
								<xsl:if test="$ni/read_time != ''">
									<span id="read-time-container" class="entry__category blue">
										<!-- <a style="padding-left: .5rem;">Время прочтения: <b id="read-time"><xsl:value-of select="$ni/read_time" /></b></a> -->
									</span>
								</xsl:if>
								<xsl:if test="$ni/size != ''">
									<span id="article-size" class="entry__category red" style="padding-left: .5rem;">
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
									
									</span>
								</xsl:if>
								<xsl:text disable-output-escaping="yes">
									&lt;!--googleon: index--&gt;&lt;!--/noindex--&gt;
								</xsl:text>
							</div>
						</noindex>

						<script>
								<xsl:text disable-output-escaping="yes">
								c = $("&lt;a&gt;",{"style" : "padding-left: .5rem;"}).text("Сложность: ");
								cv = $("&lt;b&gt;").text('</xsl:text><xsl:value-of select="$ni/complexity"/><xsl:text disable-output-escaping="yes">');
								c.append(cv);
								$("#complexity").append(c);
								</xsl:text>

								<xsl:text disable-output-escaping="yes">
								t = $("&lt;a&gt;",{"style" : "padding-left: .5rem;"}).text("Время прочтения: ");
								tv = $("&lt;b&gt;",{"id": "read-time"}).text('</xsl:text><xsl:value-of select="$ni/read_time"/><xsl:text disable-output-escaping="yes">');
								t.append(tv);
								$("#read-time-container").append(t);
								</xsl:text>

								<xsl:text disable-output-escaping="yes">
								s = $("&lt;a&gt;").text("Размер статьи: ");
								sv = $("&lt;b&gt;").text('</xsl:text><xsl:value-of select="$ni/size"/><xsl:text disable-output-escaping="yes">');
								s.append(sv);
								$("#article-size").append(s);
								</xsl:text>
							</script>

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
								<xsl:variable name="prefix">
									<xsl:choose>
										<xsl:when test="starts-with($ni/author, 'Алина')">
											<xsl:value-of select="'Подготовила: '"/>
										</xsl:when>
										<xsl:when test="starts-with($ni/author, 'Подготовил')">
											<xsl:value-of select="''"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="'Подготовил: '"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<p>
									<em>
										<span><xsl:value-of select="$prefix"/></span>
										<form method="post" action="/author">
											<input type="hidden" name="author" value="{$ni/author}"/>
											<a style="cursor:pointer" onclick="$(this).closest('form').submit()">
												<xsl:value-of select="$ni/author"/>
											</a>
										</form>
									</em>
								</p>
							</xsl:if>

					</div>
					<div>
						 <xsl:if test="$ni/tag">
							 <p class="s-content__tags">
								 <span>Теги</span>
								 <span class="s-content__tag-list">
									 <xsl:for-each select="$ni/tag">
										 <a href="{concat('tag/?tag=', replace(., '&amp;', '%26'))}">
											 <xsl:value-of select="."/>
										 </a>
									 </xsl:for-each>
								 </span>

							 </p>
						 </xsl:if>

					</div>

				</div>
				<div class="col-three md-four tab-full desctop-only latest">
					<div class="border-top"></div>
					<h3 style="margin-top:0;">Последние статьи</h3>

					<div class="soc-right">
						<h4>Следите за нами в социальных сетях</h4>
						<a href="https://twitter.com/TemptingPro">
							<img src="images/twitter.png" alt="twitter"/>
						</a>
						<a href="https://www.instagram.com/temptingpro/">
							<img src="images/instagram.png" alt="instagram"/>
						</a>
						<a href="https://vk.com/tempting_pro">
							<img src="images/vk.png" alt="vk.com"/>
						</a>
						<a href="https://facebook.com/Tempting.Pro/">
							<img src="images/facebook.png" alt="facebook"/>
						</a>
					</div>
				</div>
			</article>
		</section>

		<!-- popular_posts -->
		<section class="s-extra">
			<div class="row top">
				<div class="col-full md-six tab-full popular mobile-only">
					<div class="border-top"></div>
					<h3>Последние статьи</h3>

				</div>
			</div>

			<div class="row top">
				<div class="col-full md-six tab-full popular">
				<h3>Популярные статьи</h3>

				</div>
			</div>
		</section>

		<div id="wikitip"></div>

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