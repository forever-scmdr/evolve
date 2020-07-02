<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$ni/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>
	<xsl:variable name="soc_image" select="if($ni/soc_image != '') then $ni/soc_image else $ni/medium_pic"/>


	<xsl:variable name="ni" select="page/small_news_item"/>
	<xsl:variable name="parent" select="/page/news[@id = $ni/news/@id]"/>
	<xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>
	<xsl:variable name="format"
				  select="if($ni/video_url != '') then 'video' else if($ni/top_gal/main_pic != '') then 'gallery' else 'standard'"/>

	<xsl:variable name="utm" select="concat('utm_source=tempting&amp;utm_medium=banner&amp;utm_campaign=', $ni/@key, '&amp;utm_term=', $ni/tag[1])"/>			  


	<xsl:template name="FACEBOOK_MARKUP">
		<meta property="og:url" content="{concat($main_host, $canonical)}" />
		<meta property="og:type" content="article" />
		<meta property="og:locale" content="ru_RU" />
		<meta property="og:title" content="{$h1}" />
		<meta property="og:description" content="{$ni/twitter_description}" />
		<meta property="og:image" content="{concat($main_host, '/',$ni/@path, $soc_image)}" />
	   <meta property="og:app_id" content="552626568232392" />
	</xsl:template>

	<xsl:template name="TWITTER_MARKUP">
		<meta name="twitter:card" content="summary_large_image" />
		<meta name="twitter:image" content="{concat($main_host, '/',$ni/@path, $soc_image)}"/>
		<meta name="twitter:image:alt" content="{$h1}" />
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
font-style: italic;" class="tip desctop-only" title="Маленький - до 150 слов&#13;Средний - до 300 слов&#13;Большой - свыше 300 слов">i</span>
								
									<a >Размер новости: <b><xsl:value-of select="$ni/size" /></b></a>
								</span>
							</xsl:if>
							<xsl:text disable-output-escaping="yes">
								&lt;!--googleon: index--&gt;&lt;!--/noindex--&gt;
							</xsl:text>
						</div>
					</xsl:if>
				</div>



<!--				<div class="col-full s-content__main" style="margin-top: 2.5rem;">-->
				 <div class="col-nine md-eight tab-full" style="margin-top: 2.5rem;">
					<div id="nil">
						<div class="content-text">
						   <xsl:apply-templates select="$ni/audio"/>
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
				</div>

				<div class="col-three md-four tab-full desctop-only latest" style="margin-top: 3.3rem;">
					<xsl:call-template name="BANNER_SIDE" />
					<div class="border-top"></div>
					<h3 style="margin-top:0;">Последние новости</h3>
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

				<div class="col-full s-content__main" >
					<div>
						<xsl:call-template name="BANNER_FOLLOW"/>

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
						<div class="soc-container">
						<div class="share">
							Поделиться:
							<div class="ya-share2" style="margin-left: -4px;" data-services="vkontakte,facebook,twitter" data-limit="3"></div>
						</div>
						<div class="follow mobile-only">
							Подписаться:
							<div class="links">
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
					</div>
					</div>

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
					<h3>Последние новости</h3>
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

	<xsl:template name="PREV-NEXT">
		<xsl:if test="page/prev[@id != $ni/@id]|page/next[@id != $ni/@id]">
			<div class="s-content__pagenav">
				<div class="s-content__nav">
					<xsl:if test="page/prev[@id != $ni/@id]">
						<div class="s-content__prev">
							<a href="{page/prev/show_page}" rel="prev">
								<span>Предыдущая новость</span>
								<xsl:value-of select="page/prev/name"/>
							</a>
						</div>
					</xsl:if>
					<xsl:if test="page/next[@id != $ni/@id]">
						<div class="s-content__next">
							<a href="{page/next/show_page}" rel="next">
								<span>Следующая новость</span>
								<xsl:value-of select="page/next/name"/>
							</a>
						</div>
					</xsl:if>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ALSO">
		<xsl:if test="page/also">
			<div class="also">
				<div class="also-header">
					Читайте также:
				</div>
				<xsl:for-each-group select="page/also" group-by="@id">
					<xsl:if test="position() &lt; 4">
						<a href="{current-group()[1]/show_page}">
							<xsl:value-of select="current-group()[1]/name"/>
						</a>
					</xsl:if>
				</xsl:for-each-group>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="COMMENTS">
		<div style="height: 3rem;"></div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
		<script src="//yastatic.net/share2/share.js"></script>
		
	</xsl:template>


</xsl:stylesheet>