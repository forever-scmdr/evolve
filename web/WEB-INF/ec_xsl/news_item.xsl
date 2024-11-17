<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="id" select="page/variables/id"/>
	<xsl:variable name="nis" select="//news_item[@key = /page/variables/sni] | //*[@id = $id] "/>
	<xsl:variable name="ni" select="$nis[1]"/>

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
		<meta name="twitter:image" content="{concat($main_host, '/',$ni[1]/@path, if( $ni/soc_image != '') then $ni/soc_image else $ni/main_pic)}"/>
		<meta name="twitter:image:alt" content="{$h1}" />
	</xsl:template>

	<xsl:template name="FACEBOOK_MARKUP">
		<meta property="og:url" content="{concat($main_host, $canonical)}" />
		<!-- <meta property="og:type" content="article" /> -->
		<meta property="og:locale" content="ru_RU" />
		<meta property="og:title" content="{$h1}" />
		<meta property="og:description" content="{$ni/twitter_description}" />
		<meta property="og:image" content="{concat($main_host, '/',$ni/@path,  if( $ni/soc_image != '') then $ni/soc_image else $ni[1]/main_pic)}" />
		<meta name="og:app_id" content="552626568232392" />
	</xsl:template>

	<xsl:variable name="banner" select="page/common/banner"/>
	<xsl:variable name="sep1" select="if(contains(page/common/banner[1]/link, '?')) then '&amp;' else '?'"/>
	<xsl:variable name="sep2" select="if(contains(page/common/banner[2]/link, '?')) then '&amp;' else '?'"/>
	<xsl:variable name="sep3" select="if(contains(page/common/banner[3]/link, '?')) then '&amp;' else '?'"/>
	<xsl:variable name="sep4" select="if(contains(page/common/banner[4]/link, '?')) then '&amp;' else '?'"/>



	<xsl:template name="CONTENT">
		<section class="news_single">
			<div class="container">
				<div class="single_box">
					<div class="left">
						<h1><xsl:value-of select="$ni/name"/></h1>
						<p><xsl:value-of select="$ni/twitter_description"/></p>
						<div class="source_box">
							<xsl:if test="$ni/source != ''">
								<p class="source_who">Источник: <a href="{$ni/source_link}" target="_blank" rel="nofollow"><xsl:value-of select="$ni/source"/></a></p>
							</xsl:if>
							<p><xsl:value-of select="$ni/date"/></p>
							<xsl:variable name="href" select="if($ni/news) then $ni/news/show_page else concat('news?tag=', $ni/tag[1])"/>
							<xsl:if test="$ni/news">
								<p>Категория: <a href="{$href}"><xsl:value-of select="$ni/news/name" /></a></p>
							</xsl:if>
						</div>
						<div class="diff_box">
							<!-- <xsl:if test="$ni/complexity"><span>Сложность: <xsl:value-of select="$ni/complexity"/></span></xsl:if> -->
							<span class="time"><img src="img/timer_ico.svg" alt="timer"/>&#160;<xsl:value-of select="$ni/read_time"/></span>
						</div>
						<xsl:if test="$ni/author != ''">
							<p class="by"><xsl:value-of select="$ni/author"/></p>
						</xsl:if>
						<xsl:if test="($ni/@type != 'small_news_item' or (not($ni/@type) and name($ni) = 'news_item')) and $ni/main_pic">
							<picture class="main_pic">
								<img src="{$ni/@path}{$ni/main_pic}" alt="{$ni/name}"/>
								<p class="caption"><xsl:value-of select="$ni/figcaption"/></p>
							</picture>
						</xsl:if>
						<div class="news-content">
							<xsl:apply-templates select="$ni" mode="content"/>
						</div>
						<div class="one_img_box">
							<a href="{$banner[1]/link}">
								<xsl:value-of select="$banner[1]/text" disable-output-escaping="yes" />
							</a>
							<!-- <a href="{$banner[1]/link}"><img src="{$banner[1]/@path}{}" alt="single"/></a> -->
						</div>
						<div class="tags_box">
							<xsl:for-each select="$ni/tag">
								<a href="{concat('tag/?tag=', replace(., '&amp;', '%26'))}">
									<xsl:value-of select="concat('#',.)"/>
								</a>
							</xsl:for-each>
						</div>
						<div class="news_on_theme">
							<div calss="h2">Новости по теме</div>
							<ul>
								<xsl:for-each select="//also">
									<li><a href="{show_page}"><xsl:value-of select="name" /></a></li>
								</xsl:for-each>
							</ul>
						</div>
					</div>
					<div class="right">
						<xsl:call-template name="BANNER_SIDE"/>
						<div style="margin-bottom:1rem"></div>
						<div class="head">
							<div class="title h2">Последнее</div>
							<div class="line"></div>
							<a href="{page/news_link}" class="look_all">Смотреть все 
								<img src="img/look_all_right.svg" alt="look_all" class="light"/>
								<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow"/>
							</a>
						</div>
						<div class="right_news_list">
							<xsl:for-each select="page/latest">
								<a href="{show_page}" class="item">
									<span class="text">
										<span class="top_info_box">
											<!-- <span class="name">Источник: <xsl:value-of select="if ($ni/source != '') then 'Respectiva' else 'Respectiva'"/></span> -->
											<span class="dot"></span>
											<span class="when" data-timestamp="date/@millis"><xsl:value-of select="date"/></span>
										</span>
										<p><xsl:value-of select="name"/></p>
									</span>
									<img src="{@path}{small_pic}" alt="market_small" class="list_img" />
								</a>
							</xsl:for-each>
						</div>
					</div>
				</div>
				<div class="read_also_box">
					<div class="head">
						<div class="title h2">Читать также</div>
						<div class="line"></div>
					</div>
					<div class="read_also_list">
						<xsl:variable name="prev" select="page/prev | $ni/news/prev"/>
						<xsl:variable name="next" select="page/next | $ni/news/next"/>

						<a href="{$prev/show_page}" class="news_item">
							<img src="{$prev/@path}{$prev/small_pic}" alt="news_rec_img"/>
							<span class="inner_text">
								<span class="top_info_box">
								<!-- <span class="name">Источник: <xsl:value-of select="if ($prev/source != '') then $prev/source else 'Respectiva'"/></span> -->
									<span class="dot"></span>
									<span class="when" data-timestamp="$prev/date/@millis"><xsl:value-of select="$prev/date"/></span>
								</span>
								<p><xsl:value-of select="$prev/name"/></p>
								<span class="top_info_box">
								<span class="when"><xsl:value-of select="$prev/tag[1]"/></span>
								<span class="dot"></span>
									<span class="when"><xsl:value-of select="$prev/read_time"/> читать</span>
								</span>
							</span>
						</a>
						<xsl:if test="$next">
							<a href="{$next/show_page}" class="news_item">
								<img src="{$next/@path}{$next/small_pic}" alt="news_rec_img"/>
								<span class="inner_text">
									<span class="top_info_box">
										<!-- <span class="name">Источник: <xsl:value-of select="if ($next/source != '') then $next/source else 'Respectiva'"/></span> -->
										<span class="dot"></span>
										<span class="when" data-timestamp="$next/date/@millis"><xsl:value-of select="$next/date"/></span>
									</span>
									<p><xsl:value-of select="$next/name"/></p>
									<span class="top_info_box">
										<span class="when"><xsl:value-of select="$next/tag[1]"/></span>
										<span class="dot"></span>
										<span class="when"><xsl:value-of select="$next/read_time"/> читать</span>
									</span>
								</span>
						</a>
						</xsl:if>
						<div class="arrow_top"></div>
					</div>
				</div>
			</div>
		</section>

		<section class="popular_single">
			<div class="container">
				<div class="head">
					<div class="title h2">Популярное</div>
					<div class="line"></div>
				</div>
				<div class="other_list">
					<xsl:for-each select="page/popular">
						<a href="{show_page}" class="news_outer">
							<div class="img_box">
								<img src="{@path}{small_pic}" alt="news_other"/>
								<span class="name_title"><xsl:value-of select="tag[1]"/></span>
							</div>
							<p><xsl:value-of select="name"/></p>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template name="COMMENTS">
		<div style="padding-top: 15px;"></div>
		<div id="disqus_thread"></div>
		<script>

			/**
			*  RECOMMENDED CONFIGURATION VARIABLES: EDIT AND UNCOMMENT THE SECTION BELOW TO INSERT DYNAMIC VALUES FROM YOUR PLATFORM OR CMS.
			*  LEARN WHY DEFINING THESE VARIABLES IS IMPORTANT: https://disqus.com/admin/universalcode/#configuration-variables*/
			/*
			var disqus_config = function () {
			this.page.url = '<xsl:value-of select="$canonical"/>';  // Replace PAGE_URL with your page's canonical URL variable
			this.page.identifier = '<xsl:value-of select="concat($h1, ' ', f:utc_millis_to_bel_date(date/@millis))"/>'; // Replace PAGE_IDENTIFIER with your page's unique identifier variable
			};
			*/
			(function() { // DON'T EDIT BELOW THIS LINE
			var d = document, s = d.createElement('script');
			s.src = 'https://tempting-pro.disqus.com/embed.js';
			s.setAttribute('data-timestamp', +new Date());
			(d.head || d.body).appendChild(s);
			})();
		</script>
		<noscript>Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
	</xsl:template>


	<xsl:template name="ALSO"></xsl:template>

	<xsl:template name="PREV-NEXT"></xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
		<script src="//yastatic.net/share2/share.js"></script>
		
	</xsl:template>

	<xsl:template name="NEWS_TOPPER"></xsl:template>


	<!-- ADVERTISEMENT -->
	<xsl:template name="BANNER_SIDE">
		<xsl:variable name="sep" select="if(contains(page/common/banner[1]/link, '?')) then '&amp;' else '?'"/>
		<xsl:if test="$adv_side">
			<a href="{concat($adv_side/link, $sep, $utm)}" target="_blank" id="#adv_side" style="max-width:350px">
				<img src="{concat($adv_side/@path, $adv_side/pic)}" style="max-width:100%"/>
			</a>
			<xsl:value-of select="$adv_side/text" disable-output-escaping="yes"/>
			<xsl:value-of select="$adv_side/code" disable-output-escaping="yes"/>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>