<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text>
	</xsl:template>

	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="'Tempting.Pro'"/>
	<xsl:variable name="meta_description" select="''"/>
	<xsl:variable name="base" select="page/base"/>
	<xsl:variable name="main_host"
				  select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base"/>

	<xsl:variable name="default_canonical"
				  select="if(page/@name != 'index') then concat('/', tokenize(page/source_link, '\?')[1]) else ''"/>
	<xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

	<xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


	<xsl:variable name="active_menu_item" select="page/@name"/>
	<xsl:variable name="extra-header-class"/>
	<!-- Инфа, общая для всех страниц -->
	<xsl:variable name="common" select="page/common" />


	<!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

	<xsl:variable name="source_link" select="/page/source_link"/>
	<xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

	<xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
	<xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
	<xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
	<xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>


	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->
	<xsl:template name="HEADER">
<!--		<section class="s-pageheader{$extra-header-class}" style="{if(page/main_page/padding != '') then concat('padding-top: ', page/main_page/padding, 'px') else ''}">-->
		<section class="s-pageheader{$extra-header-class}">
			<header class="header">
				<div class="header__content row">
					<div class="header__logo">
						<a class="logo" href="{$base}">
							<img src="images/logo.png" alt="на главную - {$main_host}"/>
						</a>
					</div>
					<xsl:if test="$common/soc_link">
						<ul class="header__social">
							<xsl:for-each select="$common/soc_link">
								<li>
									<a href="{link}" target="_blank">
										<i class="fa fa-{name}" aria-hidden="true"></i>
									</a>
								</li>
							</xsl:for-each>
					</ul>
					</xsl:if>
					<xsl:call-template name="SEARCH"/>
					<xsl:call-template name="HEADER_NAV" />
				</div>
				<xsl:call-template name="VIDGET_CODE"/>
			</header>

			<xsl:call-template name="EXTRA_HEADER_CONTENT"/>
		</section>
	</xsl:template>

	<xsl:template name="HEADER_NAV">
		<a class="header__toggle-menu" href="#0" title="Меню">
			<span>Меню</span>
		</a>
		<nav class="header__nav-wrap">
			<h2 class="header__nav-heading h6">Главное меню</h2>
			<ul class="header__nav">
				<li class="{'current'[$active_menu_item = 'index']}"><a href="{$main_host}">Главная страница</a></li>

				<li class="{'current'[$active_menu_item = 'small_news']}">
					<a href="{page/news_link}">Новости</a>
				</li>
				<li class="has-children">
					<a>Статьи</a>
					<ul class="sub-menu">
						<xsl:for-each select="page/news">
							<xsl:variable name="k" select="@key"/>
							<li class="{'current'[$active_menu_item = $k]}">
								<a href="{show_page}"><xsl:value-of select="name"/></a>
							</li>
						</xsl:for-each>
					</ul>
				</li>
				<xsl:for-each select="page/menu_custom[name != 'Использование Cookie' and name != 'О проекте']">
					<xsl:variable name="k" select="@key"/>
					<li class="{'current'[$active_menu_item = $k]}">
						<a href="{show_page}">
							<xsl:value-of select="name"/>
						</a>
					</li>
				</xsl:for-each>
					<!--<xsl:if test="count(page/news) = 1">-->
						<!--<xsl:variable name="k" select="page/news/@key"/>-->
						<!--<li class="{'current'[$active_menu_item = $k]}">-->
							<!--<a href="{page/news/show_page}"><xsl:value-of select="page/news/name"/></a>-->
						<!--</li>-->
					<!--</xsl:if>-->
					<!--<xsl:if test="count(page/news) &gt; 1">-->
						<!--<xsl:variable name="k" select="@key"/>-->
						<!--<li class="has-children{' current'[$active_menu_item = $k]}">-->
							<!--<a>-->
							  <!--Новости-->
							<!--</a>-->
							<!--<ul class="sub-menu">-->
								<!--<xsl:for-each select="page/news" >-->
									<!--<xsl:variable name="k" select="$k"/>-->
									<!--<li class="{'current'[$active_menu_item = $k]}">-->
										<!--<a href="{show_page}"><xsl:value-of select="name"/></a>-->
									<!--</li>-->
								<!--</xsl:for-each>-->
							<!--</ul>-->
						<!--</li>-->
					<!--</xsl:if>-->

			   <!--  <li class="{'current'[$active_menu_item = 'contacts']}">
					<a href="{page/contacts_link}">Контакты</a>
				</li> -->
			</ul>
			<a href="#0" title="Скрыть меню" class="header__overlay-close close-mobile-menu">Закрыть</a>
		</nav>
	</xsl:template>

	<xsl:template name="SEARCH">
		<a class="header__search-trigger" href="#0"></a>
		<div class="header__search">
			<form role="search" method="get" class="header__search-form" action="{page/search_link}">
				<label>
					<span class="hide-content">Поиск:</span>
					<input type="search" class="search-field" placeholder="Введите запрос" value="{page/variables/q}" name="q" title="запрос" autocomplete="off"/>
				</label>
				<input type="submit" class="search-submit" value="Искать"/>
			</form>
			<a href="#0" title="Закрыть" class="header__overlay-close">Закрыть</a>
		</div>
	</xsl:template>


	<xsl:template name="INC_FOOTER">
		<footer class="s-footer">

			<div class="s-footer__main">
				<div class="row">
					<div class="col-two md-four mob-full s-footer__sitelinks">
						<h4>Разделы</h4>
						<ul class="s-footer__linklist">
							<li><a href="{$main_host}">Главная страница</a></li>
							<li><a href="{page/news_link}">Новости</a></li>
							<xsl:for-each select="page/menu_custom">
								<li><a href="{show_page}"><xsl:value-of select="name"/></a></li>
							</xsl:for-each>
							<!--  <li><a href="{page/contacts_link}">Контакты</a></li> -->
                    </ul>
					</div>
					<div class="col-two md-four mob-full s-footer__archives">
						<h4>Статьи</h4>
						<ul class="s-footer__linklist">
							<xsl:for-each select="page/news">
								<li><a href="{show_page}"><xsl:value-of select="name"/></a></li>
							</xsl:for-each>
						</ul>
					</div>
					<div class="col-two md-four mob-full s-footer__social">
						<h4>Соцсети</h4>
						<ul class="s-footer__linklist">
							<xsl:for-each select="$common/soc_link">
								<li><a href="{link}"><xsl:value-of select="name"/></a></li>
							</xsl:for-each>
						</ul>
					</div>
					<div class="col-five md-full end s-footer__subscribe">
						<xsl:value-of select="page/common/bottom" disable-output-escaping="yes"/>                        
					</div>
				</div>
			</div>
			<div class="s-footer__bottom">
				<div class="row">
					<div class="col-full">
						<div class="s-footer__copyright">
							<span>© Tempting.Pro 2019</span>
						</div>

						<div class="go-top">
							<a class="smoothscroll" title="Вернуться наверх" href="#top"></a>
						</div>
					</div>
				</div>
			</div>
		</footer>
	</xsl:template>


	<!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->

	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>

	<xsl:template match="*" mode="product"></xsl:template>

	<xsl:template match="*" mode="product-lines"></xsl:template>

	<xsl:template name="CART_SCRIPT"></xsl:template>


	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


	<xsl:template name="MAIN_CONTENT"></xsl:template>
	<xsl:template name="CONTENT"/>
	<xsl:template name="EXTRA_HEADER_CONTENT"/>
	<xsl:template name="EXTRA_SCRIPTS"/>
	<xsl:template name="COMMON_SCRIPTS">
		<script src="js/plugins.js"></script>
		<!--<script src="https://maps.googleapis.com/maps/api/js"></script>-->
		<script src="js/main.js"></script>

		<script type="text/javascript" src="admin/js/jquery.form.min.js"></script>
	</xsl:template>


	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
		<html lang="ru">
			<head>
				<xsl:text disable-output-escaping="yes">
&lt;!--
				</xsl:text>
				<xsl:value-of select="page/source_link"/>
				<xsl:text disable-output-escaping="yes">
--&gt;
				</xsl:text>
<!--				<base href="{$main_host}"/>-->
				<base href="{page/base}"/>
				<!--- basic page needs -->
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<!-- USER-defined code -->
				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<!--- mobile specific meta -->
				<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
				<!-- CSS -->
				<link rel="stylesheet" href="css/base.css"/>
				<link rel="stylesheet" href="css/vendor.css"/>
				<link rel="stylesheet" href="css/main.css"/>

				<!-- SEO -->
				<xsl:call-template name="SEO"/>
				<xsl:call-template name="TWITTER_MARKUP"/>
				 <xsl:call-template name="FACEBOOK_MARKUP"/>
				<!-- SCRIPTS -->
				<script src="js/jquery-3.2.1.min.js"></script>
				<script type="text/javascript" src="admin/ajax/ajax.js"></script>
				<script src="js/modernizr.js"></script>
				<script src="js/pace.min.js"></script>
				<xsl:for-each select="$head-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
			</head>
			<body id="top">
				<xsl:for-each select="$body-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<xsl:call-template name="HEADER"/>
				<xsl:call-template name="CONTENT"/>
				<xsl:call-template name="INC_FOOTER"/>
				<xsl:call-template name="COMMON_SCRIPTS" />
				<xsl:call-template name="EXTRA_SCRIPTS"/>
				<xsl:for-each select="$body-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>





	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->


	<xsl:template match="*" mode="content">
		<xsl:value-of select="text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="text_part | gallery_part | code_part" mode="content"/>
	</xsl:template>

	<xsl:template match="text_part" mode="content">
		<h3>
			<xsl:value-of select="name"/>
		</h3>
		<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="gallery_part" mode="content">
		<div class="fotorama" data-fit="cover">
			<xsl:for-each select="picture_pair">
				<img src="{@path}{big}" alt="{name}" data-caption="{name}"/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="code_part" mode="content">
	
		<xsl:value-of select="code" disable-output-escaping="yes"/>
	
	</xsl:template>

	<xsl:template name="PAGE_TITLE">
		<xsl:param name="page"/>
		<xsl:if test="$page/header_pic != ''">
			<h1>
				<img src="{$page/@path}{$page/header_pic}" alt="{$page/name}"/>
			</h1>
		</xsl:if>
		<xsl:if test="not($page/header_pic) or $page/header_pic = ''">
			<h1>
				<xsl:value-of select="$page/name"/>
			</h1>
		</xsl:if>
	</xsl:template>

	<xsl:template name="number_option">
		<xsl:param name="max"/>
		<xsl:param name="current"/>
		<xsl:if test="not($current)">
			<xsl:call-template name="number_option">
				<xsl:with-param name="max" select="$max"/>
				<xsl:with-param name="current" select="number(1)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="number($current) &lt;= number($max)">
			<option value="{$current}">
				<xsl:value-of select="$current"/>
			</option>
			<xsl:call-template name="number_option">
				<xsl:with-param name="max" select="$max"/>
				<xsl:with-param name="current" select="number($current) + number(1)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="SEO">

		<xsl:variable name="quote">"</xsl:variable>

		<link rel="canonical" href="{concat($main_host, $canonical)}"/>

		<xsl:if test="$seo">
			<xsl:apply-templates select="$seo"/>
		</xsl:if>
		<xsl:if test="not($seo) or $seo = ''">
			<title>
				<xsl:value-of select="$title"/>
			</title>
			<meta name="description" content="{replace($meta_description, $quote, '')}"/>
		</xsl:if>
		<meta name="google-site-verification" content="{page/url_seo_wrap/google_verification}"/>
		<meta name="yandex-verification" content="{page/url_seo_wrap/yandex_verification}"/>
		<xsl:call-template name="MARKUP"/>
		<xsl:call-template name="PAGINATION_LINKS"/>
	</xsl:template>

	<xsl:template name="MARKUP"/>

	<xsl:template match="seo | url_seo">
		<title>
			<xsl:value-of select="if(title != '') then title else $title"/>
		</title>
		<meta name="description" content="{description}"/>
		<meta name="keywords" content="{keywords}"/>
		<xsl:value-of select="meta" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template name="PAGINATION_LINKS">
		<xsl:variable name="pages" select="//*[ends-with(name(), '_pages')]"/>
		<xsl:if test="$pages">
			<xsl:variable name="current_page" select="number(page/variables/page)"/>

			<xsl:variable name="prev" select="$pages/page[$current_page - 1]"/>
			<xsl:variable name="next" select="$pages/page[$current_page]"/>

			<xsl:if test="$prev">
				<link rel="prev" href="{$prev/link}"/>
			</xsl:if>
			<xsl:if test="$next">
				<link rel="next" href="{$next/link}"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="news_item" mode="masonry">

		<xsl:variable name="category" select="if(../name() = 'text_part') then ../news else news" />
		<xsl:variable name="format" select="if(video_url != '') then 'video' else if(top_gal/main_pic != '') then 'gallery' else 'standard'"/>
		<article class="masonry__brick entry format-{$format}" data-aos="fade-up">
			<!-- STANDARD -->
			<xsl:if test="$format = 'standard'">
				<div class="entry__thumb">
					<a href="{show_page}" class="entry__thumb-link">
						<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>
					</a>
				</div>
			</xsl:if>

			<!-- VIDEO -->
			<xsl:if test="$format = 'video'">
				<div class="entry__thumb video-image">
					<a href="{video_url}" data-lity="">
						<img src="{concat(@path, small_pic)}" srcset="{concat(@path, small_pic)} 1x, {concat(@path, medium_pic)} 2x" alt=""/>
					</a>
				</div>
			</xsl:if>

			<xsl:if test="$format = 'gallery'">
				<div class="entry__thumb slider">
					<div class="slider__slides">
						<xsl:variable name="path" select="top_gal/@path"/>
						<xsl:for-each select="top_gal/small_pic">
							<xsl:variable name="p" select="position()"/>
							<div class="slider__slide">
								<img src="{concat($path,.)}" srcset="{concat($path,.)} 1x, {concat($path,../medium_pic[$p])} 2x" alt=""/>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</xsl:if>

			<!-- TEXT -->
			<div class="entry__text">
				<div class="entry__header">
					<div class="entry__date">
						<a href="{show_page}" data-utc="{date/@millis}"><xsl:value-of select="date"/></a>
					</div>
					<div class="h1 entry__title"><a href="{show_page}"><xsl:value-of select="name"/></a></div>
				</div>
				<div class="entry__excerpt">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
				<div class="entry__meta">
					<span class="entry__meta-links">
						<a href="{$category/show_page}">
							<xsl:value-of select="$category/name"/>
						</a>
					</span>
				</div>
			</div>

		</article>
	</xsl:template>


	<xsl:template name="TWITTER_MARKUP"/>
	<xsl:template name="FACEBOOK_MARKUP"/>

	<xsl:template name="VIDGET_CODE">
		<xsl:variable name="inf" select="page/main_page/informer_wrap"/>
		<xsl:if test="$inf">
			<div class="header__content row">
				<div class="informers-container">
					<div class="informer-menu">
						<a class="toggler" onclick="$('.informer-link').not('.active').toggleClass('visible'); updateHeight();">
							<span>Свернуть/развенруть меню</span>
						</a>
						<xsl:for-each select="$inf">
							<a href="{informers_ajax_link}" class="informer-link informer-ajax-caller{if(position() = 1) then ' active ' else ''}">
								<xsl:value-of select="name"/>
							</a>
						</xsl:for-each>
					</div>
					<div id="informers">
						<div style="height: 72px; text-align: center;">Loading...</div>
					</div>
					<script type="text/javascript">
						$(document).ready(function(){
						var tickersPerRequest = Math.floor($(".row").width()/229);
						var link = $(".informer-link:eq(0)").attr("href");
						link = link + "&amp;limit=" + tickersPerRequest + "&amp;tickers_per_request=" + tickersPerRequest;
						insertAjax(link,'informers', function(){
								setTimeout(updateHeight, 500);
							});
						});
						$(document).on("click", ".informer-ajax-caller", function(e){
							e.preventDefault();
							el = $(this);
							$(".informer-link").removeClass("visible");
							if(!el.is(".no-active")){
								$(".informer-link").not(el).removeClass("active");
								el.addClass("active");
							}
							var tickersPerRequest = Math.floor($(".row").width()/229);
							var link = el.attr("href");
							link = link + "&amp;tickers_per_request=" + tickersPerRequest;
							insertAjax(link,'informers', function(){
								setTimeout(updateHeight,250);
							});
						});

						function updateHeight(){
							$(".s-pageheader--home").css({"padding-top" : $(".header").height() + 45});
							var pt = $(".s-pageheader--home").css("padding-top");
							pt = parseFloat(pt.replace("px", ""));
							var h = $(".informers-container").outerHeight() + $(".header__logo").height();
							if(pt &lt; h){
								setTimeout(updateHeight,250);
							}
						}
					</script>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
