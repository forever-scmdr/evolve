<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text>
	</xsl:template>

	<!-- ****************************    UTM    ******************************** -->

	<xsl:variable name="utm"/>

	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="'Tempting.Pro'"/>
	<xsl:variable name="meta_description" />
	<xsl:variable name="meta_keywords" />
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
	<xsl:variable name="adv" select="page/advertisement"/>
	<xsl:variable name="adv_top" select="$adv/top_728x90"/>
	<xsl:variable name="adv_side" select="$adv/side_240x400"/>
	<xsl:variable name="adv_bottom" select="$adv/bottom_900x600"/>
	<xsl:variable name="adv_fixed" select="$adv/bottom_fixed"/>


	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->
	<xsl:template name="HEADER">
		<xsl:if test="page/telegram_link/name != ''">
			<div class="telagram-link-top">
				<xsl:if test="link != ''">
					<a href="{page/telegram_link/link}" style="{page/telegram_link/style}">
						<xsl:value-of select="page/telegram_link/name"/>
					</a>
				</xsl:if>
				<xsl:if test="not(link != '')">
					<a style="{page/telegram_link/style}">
						<xsl:value-of select="page/telegram_link/name"/>
					</a>
				</xsl:if>
			</div>
		</xsl:if>
<!--		<section class="s-pageheader{$extra-header-class}" style="{if(page/main_page/padding != '') then concat('padding-top: ', page/main_page/padding, 'px') else ''}">-->
		<xsl:call-template name="BANNER_TOP" />

		<section class="s-pageheader{$extra-header-class}">
			<header class="header">
				<xsl:call-template name="INTERNAL_VIDGET_CODE"/>
				<div class="header__content row">
					<div class="header__logo">
						<a class="logo" href="{$base}">
							<img src="images/logo.png" alt="на главную - {$main_host}"/>
						</a>
					</div>
					<div class="header__social">
						<xsl:if test="$common/soc_link">
							<ul >
								<xsl:for-each select="$common/soc_link">
									<li>
										<a href="{link}" target="_blank">
											<i class="fa fa-{name}" aria-hidden="true"></i>
										</a>
									</li>
								</xsl:for-each>
							</ul>
						</xsl:if>
						<a class="lang" href="https://eng.tempting.pro/">
							English version
						</a>
					</div>
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
								<a class="lang mobile-only" style="width: 0;
height: 20px;
overflow: hidden;
padding-left: 28px;
position: absolute;left:4.2rem; margin-top: -3rem;" href="https://eng.tempting.pro/">
							English version
						</a>
			<ul class="header__nav">

				<li class="{'current'[$active_menu_item = 'index']}"><a href="{$main_host}">Главная</a></li>

				<li class="has-children">
					<a href="{page/news_link}">Новости</a>
					<ul class="sub-menu">
						<xsl:variable name="current_tag" select="if(page/@name = 'news') then page/variables/tag else ''"/>
						<li class="{'current'[$current_tag = '' and $active_menu_item = 'small_news']}">
							<a href="{page/news_link}">Все новости</a>
						</li>
						<li class="{'active'[$current_tag = 'Политика']}">
							<a href="{page/news_link_pol}">Политика</a>
						</li>
						<li class="{'active'[$current_tag = 'Финансы']}">
							<a href="{page/news_link_fin}">Финансы</a>
						</li>
						<li class="{'active'[$current_tag = 'Бизнес']}">
							<a href="{page/news_link_biz}">Бизнес</a>
						</li>
						<li class="{'active'[$current_tag = 'Технологии']}">
							<a href="{page/news_link_tech}">Технологии</a>
						</li>
						<li class="{'active'[$current_tag = 'Экономика']}">
							<a href="{page/news_link_econ}">Экономика</a>
						</li>
						<li class="{'active'[$current_tag = 'Биржа']}">
							<a href="{page/news_link_stock}">Биржа</a>
						</li>
					</ul>
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
				<xsl:for-each select="page/menu_custom[in_menu = '1']">
					<xsl:variable name="k" select="@key"/>
					<li class="{'current'[$active_menu_item = $k]}">
						<a href="{show_page}">
							<xsl:value-of select="name"/>
						</a>
					</li>
				</xsl:for-each>
				<xsl:for-each select="page/link">
					<li >
						<a target="_blank" href="{link}" rel="nofollow" >
							<span class="link-span" style="{style}"><xsl:value-of select="name"/></span>
						</a>
					</li>
				</xsl:for-each>
				<!--  <li class="{'current'[$active_menu_item = 'contacts']}">
					<a href="{page/contacts_link}">Контакты</a>
				</li> -->
			</ul>

			<xsl:if test="page/hot_tags/tag != ''">
				<xsl:variable name="t" select="page/hot_tags/tag"/>
				<ul class="header__nav header_tags orange">
					<xsl:for-each select="$t">
						<xsl:if test="position() &lt; 6">
							<!-- <li style="{if(color != '') then concat('background-color: ', color) else ''}" class="{if(color != '') then 'no-sep' else ''}"> -->
							<li style="{if(color != '') then concat('background-color: ', color) else ''}">
								<a href="{hot_link}" style="{if(text_color != '') then concat('color: ', text_color, ';') else ''}"><xsl:value-of select="name"/></a>
							</li>
						</xsl:if>
					</xsl:for-each>
				</ul>
				<xsl:if test="count($t) &gt; 5">
					<ul class="header__nav header_tags yellow">
						<xsl:for-each select="$t">
							<xsl:if test="position() &gt; 5">
								<!-- <li style="{if(color != '') then concat('background-color: ', color) else ''}" class="{if(color != '') then 'no-sep' else ''}"> -->
							<li style="{if(color != '') then concat('background-color: ', color) else ''}">
									<a href="{hot_link}"  style="{if(text_color != '') then concat('color: ', text_color, ';') else ''}"><xsl:value-of select="name"/></a>
								</li>
							</xsl:if>
						</xsl:for-each>
					</ul>
				</xsl:if>
				<div class="margin-bottom-stub"></div>
			</xsl:if>

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
						<div class="footer-code-container">
							<xsl:value-of select="page/common/bottom_plain_text" disable-output-escaping="yes"/>
						</div>
					</div>
				</div>
			</div>
			<div class="s-footer__bottom">
				<div class="row">
					<div class="col-full">
						<div class="s-footer__copyright">
							<span>© Tempting.Pro 2021</span>
							<span>Нашли ошибку? Выделите текст и нажмите Ctrl+Enter.</span>
							<span><a href="mailto:info@tempting.pro">info@tempting.pro</a></span>
						</div>

						<div class="go-top">
							<a class="smoothscroll" title="Вернуться наверх" href="#top"></a>
						</div>
					</div>
				</div>
			</div>
			<!-- <a href="//orphus.ru" id="orphus" target="_blank">
				<img alt="Система Orphus" src="/orphus/orphus.gif" border="0" width="88" height="31"/>
			</a> -->
		</footer>
		<div id="wikitip"></div>
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
		<script src="js/cookies.js"></script>
		<script src="js/plugins.js"></script>
		<!--<script src="https://maps.googleapis.com/maps/api/js"></script>-->
		<script src="js/main.js?version=1"></script>

		<script type="text/javascript" src="admin/js/jquery.form.min.js"></script>
		<!-- <script type="text/javascript" src="/orphus/orphus.js"></script> -->
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
				<link rel="stylesheet" href="css/base.css?version=1.1"/>
				<link rel="stylesheet" href="css/vendor.css?version=1"/>
				<link rel="stylesheet" href="css/main.css?version=1.525"/>

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

				<xsl:call-template name="BANNER_FIXED" />

				<xsl:call-template name="COMMON_SCRIPTS" />
				<xsl:call-template name="EXTRA_SCRIPTS"/>

				<script>
					var timeoutCounter = 0;
					var googleTimeout = setTimeout(checkAds ,500);
					function checkAds(){
						if(typeof googleTimeout != "undefined"){
							clearTimeout(googleTimeout);
						}
						var $iframe = $("#aswift_0");
						timeoutCounter++;
						if(timeoutCounter &lt; 40 &amp;&amp; $iframe.length == 0){
							googleTimeout = setTimeout(checkAds ,500);
						}else if($iframe.length != 0){
							var src = $iframe.attr("src");
							var $input = $("&lt;input&gt;", {"type": "hidden", "name" : "url", value : src});
							var $form = $("&lt;form&gt;", {"method": "post", "action" : "check_google_ads"});
							$form.append($input);
							$form.ajaxSubmit({
								success: function(data, status, arg3) {
									var res = $(data).find("result");
									var exception = $(data).find("exception");
									if(res.text != "true"){
										showDefaultAd();
									}
								}
								,error: function() {
									console.log("error checking ads");
								}
							});
						}else{
							showDefaultAd();
						}
					}
					function showDefaultAd(){
						var href = '<xsl:value-of select="$adv_top/link"/>';
						var src = '<xsl:value-of select="concat($adv_top/@path, $adv_top/pic)"/>';
						var $a = $("&lt;a&gt;", {"href" : href, "target" : "_blank", });
						var $img = $("&lt;img&gt;",{"src" : src});
						$a.append($img);
						$("#banner-top-700").html($a);
					}
				</script>

				<xsl:for-each select="$body-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>

			</body>
		</html>
	</xsl:template>





	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->


	<xsl:template match="*" mode="content">
		<xsl:value-of select="text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="text_part | gallery_part | code_part | more_news" mode="content"/>
	</xsl:template>

	<xsl:template match="audio">
		<div class="audio-wrap">
			<audio id="player{../@id}" src="{concat(../@path, .)}" width="100%" height="42" controls="controls"/>
		</div>
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

	<xsl:template match="more_news" mode="content">
		<div class="col-full recommended">
			<div class="desktop-only background-rectangle"></div>
			<h3>Рекомендовано</h3>
			<xsl:if test="count(news_item | small_news_item) = 1">
				<xsl:for-each select="news_item | small_news_item">
					<div class="col-full recommended-item">
						<a href="{show_page}">
							<img src="{concat($base,'/', @path, small_pic)}"/>
							<span>
								<xsl:value-of select="name"/>
							</span>
						</a>
					</div>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="count(news_item | small_news_item) &gt; 1">
				<xsl:for-each select="news_item | small_news_item">
					<div class="col-six tab-full recommended-item">
						<a href="{show_page}">
							<img src="{concat($base,'/', @path, small_pic)}"/>
							<span>
								<xsl:value-of select="name"/>
							</span>
						</a>
					</div>
				</xsl:for-each>
			</xsl:if>
		</div>
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
			<meta name="keywords" content="{replace($meta_keywords, $quote, '')}"/>
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
						<a href="{show_page}" data-utc="{date/@millis}">
							<xsl:value-of select="f:utc_millis_to_bel_date(date/@millis)"/>
							<xsl:if test="update != ''">&#160;(обновлено: <xsl:value-of select="update"/>)</xsl:if>
						</a>
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

	<xsl:template name="INTERNAL_VIDGET_CODE">
		<xsl:if test="page/@name != 'index'">
			<xsl:variable name="informers" select="/page/informer"/>
			<xsl:if test="$informers">
				<!-- <div class="header__content row"> -->
					<div style="margin-bottom: 1rem;position: relative;">
						<div class="tradingview-widget-container">
							<div class="tradingview-widget-container__widget"></div>
							<!-- <div class="tradingview-widget-copyright">
								<a href="https://ru.tradingview.com" rel="noopener" target="_blank">
									<span class="blue-text">Финансовые рынки</span>
								</a> от TradingView
							</div> -->
							<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-ticker-tape.js">{
									"showSymbolLogo": true,
									"colorTheme": "light",
									"isTransparent": false,
									"displayMode": "adaptive",
									"locale": "ru",
									"symbols": [
										<xsl:for-each select="$informers">
											<xsl:if test="position() &gt; 1">,</xsl:if>
											{
												"proName": <xsl:value-of select="concat('&#34;', pro_name, '&#34;')" disable-output-escaping="yes"/>,
												"title": <xsl:value-of select="concat('&#34;', name, '&#34;')" disable-output-escaping="yes"/>
											}
										</xsl:for-each>
									]
								}</script>
						</div>
					<!-- </div> -->
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="VIDGET_CODE">
		<xsl:variable name="inf" select="page/main_page/informer_wrap"/>
		<xsl:if test="$inf">
			<div class="header__content row">
				<div class="desctop-only" style="font-size: 1.5rem; font-family: Trebuchet MS, roboto, ubuntu, sans-serif; text-align: right; margin-top: -20px;">
					По данным <a style="text-decoration: underline;" href="https://ru.tradingview.com/" target="_blank">TraidingView</a>
				</div>
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
							<!-- $(".s-pageheader- -home").css({"padding-top" : $(".header").height() + 45});
							var pt = $(".s-pageheader- -home").css("padding-top");
							pt = parseFloat(pt.replace("px", ""));
							var h = $(".informers-container").outerHeight() + $(".header__logo").height();
							if(pt &lt; h){
								setTimeout(updateHeight,250);
							} -->
						}
					</script>
				</div>
			</div>
			<div class="mobile-only" style="margin: 4px 0; position: relative; font-size: 1.5rem; font-family: Trebuchet MS, roboto, ubuntu, sans-serif;">
				По данным <a style="text-decoration: underline;" href="https://ru.tradingview.com/" target="_blank">TraidingView</a>
			</div>
			<div style="margin-bottom: 20px;"></div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="BANNER_FOLLOW">

		<xsl:variable name="sep" select="if(contains(page/common/banner[1]/link, '?')) then '&amp;' else '?'"/>

		<div class="content-text banner banner-follow">
			<div class="banner-wrap">
				<xsl:value-of select="/page/common/banner[1]/text" disable-output-escaping="yes"/>
				<a target="_blank" rel="nofollow" href="{concat(/page/common/banner[1]/link, $sep, $utm)}" class="utm-link"></a>
			</div>
		</div>
		<!-- <div class="content-text banner banner-follow">
			<p class="follow-text">Подписывайтесь на наши соцсети:
				<a href="https://twitter.com/TemptingPro"><img src="images/twitter.png" alt="twitter"/></a>
				<a href="https://www.instagram.com/temptingpro/"><img src="images/Instagram_icon-icons.com_66804.png" alt="instagram"/></a>
				<a href="https://vk.com/tempting_pro"><img src="images/11-512.png" alt="vk.com"/></a>
				<a href="https://facebook.com/Tempting.Pro/"><img src="images/124010.png" alt="facebook"/></a>
			</p>
			<p class="small-text desctop-only">Чтобы быть в курсе последних мировых событий и не пропускать свежие образовательные материалы
				<img style="height: 3.5rem; display: inline-block; vertical-align: middle;  margin-left: .5px; margin-top: -1rem;" src="images/live.png"/>
				<img style="height: 3.5rem; display: inline-block; vertical-align: middle; margin-left: .5px; margin-top: -1rem;" src="images/icons8-news-96.png"/>
			</p>			
		</div> -->
	</xsl:template>


	<!-- ADVERTISEMENT -->
	<xsl:template name="BANNER_SIDE">
		<xsl:variable name="sep" select="if(contains(page/common/banner[1]/link, '?')) then '&amp;' else '?'"/>
		<xsl:if test="$adv_side">
			<div class="border-top"></div>
			<div class="adv_side" id="#adv_side">
				<xsl:if test="$adv_side/pic != ''">
					<a href="{concat($adv_side/link, $sep, $utm)}" target="_blank">
						<img src="{concat($adv_side/@path, $adv_side/pic)}"/>
					</a>
				</xsl:if>
				<xsl:value-of select="$adv_side/text" disable-output-escaping="yes"/>
				<xsl:value-of select="$adv_side/code" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BANNER_TOP">
		<xsl:if test="$adv_top != ''">
			<section style="{$adv_top/style}" id="banner-top-700">
				<div class="row" style="padding-left:0; padding-right:0;">
					<div class="col-full">
						<div class="banner-top-700" style="line-height: 1; margin:0 auto;">
<!--							<xsl:if test="$adv_top/pic != ''">-->
<!--							<a href="{$adv_top/link}" target="_blank"><img src="{concat($adv_top/@path, $adv_top/pic)}"/></a>-->
<!--							</xsl:if>-->
							<xsl:value-of select="$adv_top/code" disable-output-escaping="yes"/>
						</div>
					</div>
				</div>
			</section>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BANNER_FIXED">
		<xsl:if test="$adv_fixed and not(page/variables/fixed_banner_updated = $adv_fixed/@last-modified)">
			<section id="banner-fixed-bottom">
				<div class="row" style="padding-left:0; padding-right:0;">
					<a class="close" style="cursor: pointer; padding: 1rem;" onclick="hideFixedBanner({$adv_fixed/@last-modified})">×</a>
					<div class="col-full">
						<xsl:if test="$adv_fixed/pic !=''">
							<a href="{$adv_fixed/link}" target="_blank">
								<img src="{concat($adv_fixed/@path, $adv_fixed/pic)}"/>
							</a>
						</xsl:if>
					</div>
				</div>
			</section>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BANNER_DONATE">
		<xsl:if test="$adv_bottom">
			<div style="margin-bottom: 1.5rem;"></div>
			<div class="content-text banner banner-donate">
				<xsl:if test="$adv_bottom/pic != ''">
					<a class="donate-link" href="{$adv_bottom/link}" target="_blank">
						<img src="{concat($adv_bottom/@path, $adv_bottom/pic)}"/>
					</a>
				</xsl:if>
				<xsl:value-of select="$adv_bottom/text" disable-output-escaping="yes"/>
				<xsl:value-of select="$adv_bottom/code" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
