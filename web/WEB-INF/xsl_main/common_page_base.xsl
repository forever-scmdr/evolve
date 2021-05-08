<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="my_price_ajax.xsl"/>
	<xsl:import href="one_click_ajax.xsl"/>
	<xsl:import href="subscribe_ajax.xsl"/>
	<xsl:import href="snippets/product.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>



	<!-- **************************************************************************************************************************************** -->
	<!-- ****************************                                   ************************************************************************* -->
	<!-- ****************************    	ОБЪЯВЛЕНИЕ ПЕРЕМЕННЫХ  		************************************************************************* -->
	<!-- ****************************                                   ************************************************************************* -->
	<!-- **************************************************************************************************************************************** -->



	<!-- ****************************    ОБЩИЕ ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ    ******************************** -->

	<xsl:variable name="common" select="page/common"/>
	<xsl:variable name="base" select="page/base" />
	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>
	<xsl:variable name="currencies" select="page/catalog/currencies"/>
	<xsl:variable name="h1" select="'not-set'"/>
	<xsl:variable name="sel_news_id" select="page/selected_news/@id"/>
	<xsl:variable name="city" select="f:value_or_default(page/variables/city, 'Минск')"/>

	<xsl:variable name="active_menu_item"/>	<!-- переопределяется -->


	<!-- ****************************    НАСТРОЙКИ ОТОБРАЖЕНИЯ    ******************************** -->

	<xsl:variable name="page_menu" select="page/optional_modules/display_settings/side_menu_pages"/>
    <xsl:variable name="has_quick_search" select="page/optional_modules/display_settings/catalog_quick_search = ('simple', 'advanced')"/>
	<xsl:variable name="has_currency_rates" select="page/optional_modules/display_settings/currency_rates = 'on'"/>

	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="''" />
	<xsl:variable name="meta_description" select="''" />
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />

	<xsl:variable name="default_canonical" select="if(page/@name != 'index') then concat('/', tokenize(page/source_link, '\?')[1]) else ''" />
	<xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

	<xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>




	<!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

	<xsl:variable name="source_link" select="/page/source_link"/>
	<xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

	<xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
	<xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
	<xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
	<xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>





	<!-- ****************************************************************************************************************************************** -->
	<!-- ****************************                                		 ********************************************************************** -->
	<!-- ****************************    ПЕРЕОПРЕДЕЛЯЕМЫЕ ЧАСТИ СТРАНИЦЫ     ********************************************************************** -->
	<!-- ****************************                                  		 ********************************************************************** -->
	<!-- ****************************************************************************************************************************************** -->






	<xsl:template name="CONTENT" />
	<xsl:template name="EXTRA_SCRIPTS"/>




	<xsl:template name="MARKUP">
		<script type="application/ld+json">
			{
			"@context":"http://schema.org",
			"@type":"Organization",
			"url":"<xsl:value-of select="$main_host"/>/",
			"name":"<xsl:value-of select="$title"/>",
			"logo":"<xsl:value-of select="concat($main_host, '/img/logo_big.svg')"/>",
			"aggregateRating": {
			"@type": "AggregateRating",
			"ratingCount": "53",
			"reviewCount": "53",
			"bestRating": "5",
			"ratingValue": "4,9",
			"worstRating": "1",
			"name": "TTD"
			},
			"contactPoint": [
			<xsl:for-each select="page/common/phone" >
				<xsl:if test="position() != 1">,</xsl:if>{
				"@type":"ContactPoint",
				"telephone":"<xsl:value-of select="tokenize(., '_')[1]"/>",
				"contactType":"<xsl:value-of select="tokenize(., '_')[2]"/>"
				}
			</xsl:for-each>
			]
			<xsl:if test="page/common/email != ''">
				,"email":[<xsl:for-each select="page/common/email" >
				<xsl:if test="position() != 1">, </xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>]
			</xsl:if>
			}
		</script>
	</xsl:template>




	<!-- ****************************************************************************************************************************************** -->
	<!-- ****************************                                		 ********************************************************************** -->
	<!-- ****************************   	 ЗАГОТОВКИ ЧАСТЕЙ СТРАНИЦЫ       ********************************************************************** -->
	<!-- ****************************                                  		 ********************************************************************** -->
	<!-- ****************************************************************************************************************************************** -->




	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">
			<xsl:value-of select="$h1"/>
		</div>
	</xsl:template>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="CART_SCRIPT">
		<script>
			$(document).ready(function() {
			$('.product_purchase_container').find('input[type="submit"]').click(function(event) {
			event.preventDefault();
			var qtyForm = $(this).closest('form');
			var lockId = $(this).closest('.product_purchase_container').attr('id');
			postForm(qtyForm, lockId, null);
			});
			});
		</script>
	</xsl:template>



	<xsl:template name="USER_PAGE_H1">
		<xsl:param name="page"/>
		<xsl:if test="$page/header_pic != ''"><h1><img src="{$page/@path}{$page/header_pic}" alt="{$page/header}"/></h1></xsl:if>
		<xsl:if test="not($page/header_pic) or $page/header_pic = ''"><h1><xsl:value-of select="$page/header"/></h1></xsl:if>
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
			<option value="{$current}"><xsl:value-of select="$current"/></option>
			<xsl:call-template name="number_option">
				<xsl:with-param name="max" select="$max"/>
				<xsl:with-param name="current" select="number($current) + number(1)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<xsl:template name="SEO">
		<xsl:variable name="quote">"</xsl:variable>
		<link rel="canonical" href="{concat($main_host, $canonical)}" />
		<xsl:if test="$seo">
			<xsl:apply-templates select="$seo"/>
		</xsl:if>
		<xsl:if test="not($seo) or $seo = ''">
			<title>
				<xsl:value-of select="$title"/>
			</title>
			<meta name="description" content="{replace($meta_description, $quote, '')}"/>
		</xsl:if>
		<xsl:if test="$common/google_verification">
			<meta name="google-site-verification" content="{$common/google_verification}"/>
		</xsl:if>
		<xsl:if test="$common/yandex_verification">
			<meta name="google-site-verification" content="{$common/yandex_verification}"/>
		</xsl:if>
		<xsl:call-template name="MARKUP" />
	</xsl:template>



	<xsl:template match="seo | url_seo">
		<title>
			<xsl:value-of select="title"/>
		</title>
		<meta name="description" content="{description}"/>
		<meta name="keywords" content="{keywords}"/>
		<xsl:value-of select="meta" disable-output-escaping="yes"/>
	</xsl:template>




	<!-- ****************************************************************************************************************************************** -->
	<!-- ****************************                                		 ********************************************************************** -->
	<!-- ****************************   ПРОСТО ШАБЛОНЫ (xsl:template match)  ********************************************************************** -->
	<!-- ****************************                                  		 ********************************************************************** -->
	<!-- ****************************************************************************************************************************************** -->



	<!-- ссылка на информационные разделы -->
	<xsl:template match="custom_page" mode="menu_first">
		<xsl:variable name="key" select="@key"/>
		<!-- без подразделов -->
		<xsl:if test="not(custom_page)">
			<div class="main-menu__item {'active'[$active_menu_item = $key]}">
				<a href="{show_page}" class="{'active'[$active_menu_item = $key]}">
					<span><xsl:value-of select="header"/></span>
				</a>
			</div>
		</xsl:if>
		<!-- с подразделами -->
		<xsl:if test="custom_page or page_link">
			<div class="main-menu__item" style="position: relative;">
				<a href="#ts_{@id}" class="show-sub{' active'[$active_menu_item = $key]}">
					<span><xsl:value-of select="header"/></span>
				</a>
				<div id="ts_{@id}" class="popup-text-menu" style="position: absolute; z-index: 2; display: none;">
					<div class="sections">
						<xsl:apply-templates select="custom_page | page_link" mode="menu"/>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template match="custom_page" mode="menu">
		<a href="{show_page}">
			<xsl:value-of select="header"/>
		</a>
	</xsl:template>


	<xsl:template match="page_link" mode="menu_first">
		<div class="main-menu__item">
			<a href="{link}">
				<xsl:value-of select="name"/>
			</a>
		</div>
	</xsl:template>

	<xsl:template match="page_link" mode="menu">
		<a href="{link}">
			<xsl:value-of select="name"/>
		</a>
	</xsl:template>



	<xsl:template match="block" mode="footer">
		<div class="footer__column">
			<xsl:if test="header and not(header = '')"><div class="footer__title"><xsl:value-of select="header" /></div></xsl:if>
			<div class="footer__text"><xsl:value-of select="text" disable-output-escaping="yes"/></div>
		</div>
	</xsl:template>


	<xsl:template match="section" mode="desktop">
		<xsl:param name="level"/>
        <xsl:variable name="active" select="@id = $sel_sec_id"/>
		<xsl:variable name="active_parent" select=".//section/@id = $sel_sec_id"/>
		<div class="side-menu__item side-menu__item_level_{$level}{' side-menu__item_active'[$active]}">
			<a class="side-menu__toggle" href="#" onclick="return toggleDesktopCatalogSection('{@id}');">
				<img src="{if (section) then (if ($active_parent) then 'img/icon-toggle-minus.png' else 'img/icon-toggle-plus.png') else 'img/icon-toggle-dash.png'}"
						alt="" id="cat_plus_{@id}"/>
			</a>
			<a class="side-menu__link" href="{if (section) then show_section else show_products}"><xsl:value-of select="name"/></a>
		</div>
		<xsl:if test="section">
			<div id="subsec_{@id}" style="{'display:none'[not($active_parent)]}">
				<xsl:apply-templates select="section" mode="desktop">
					<xsl:with-param name="level" select="number($level) + 1"/>
				</xsl:apply-templates>
			</div>
		</xsl:if>
	</xsl:template>



	<xsl:template match="section" mode="mobile">
		<div class="popup-menu__item">
			<div class="popup-menu__link">
				<a onclick="$('#mobile_sec{@id}').css('transform', ''); return false;"><xsl:value-of select="name"/></a>
			</div>
			<div class="popup-menu__arrow">
				<img src="img/icon-menu-right.png" alt=""/>
			</div>
		</div>
	</xsl:template>



	<!-- ****************************    БЛОКИ С ПРОИЗВОЛЬНЫМ КОНТЕНТОМ    ******************************** -->


	<xsl:template match="*" mode="content">
		<xsl:value-of select="text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="page_text | common_gallery | simple_gallery | custom_block | page_extra_code" mode="content"/>
	</xsl:template>

	<xsl:template match="page_text" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}" rel="Свернуть ↑">Подробнее ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="page_extra_code" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}" rel="Свернуть ↑">Подробнее ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="common_gallery" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}"  onclick="initNanoCommon{@id}(); return false;" rel="Скрыть галерею ↑">Галерея ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<div id="nanogallery{@id}">
				<script>
					<xsl:if test="f:num(spoiler) = 0">
						$(document).ready(function(){
						initNanoCommon<xsl:value-of select="@id"/>();
						});
					</xsl:if>
					function initNanoCommon<xsl:value-of select="@id"/>() {
					<xsl:if test="f:num(spoiler) &gt; 0">
						if(!$("<xsl:value-of select="concat('#nanogallery', @id)"/>").is(":visible")){
					</xsl:if>
					$("#nanogallery<xsl:value-of select="@id"/>").nanogallery2( {
					// ### gallery settings ###
					thumbnailHeight:  <xsl:value-of select="height"/>,
					thumbnailWidth:   <xsl:value-of select="width"/>,
					thumbnailBorderHorizontal :   <xsl:value-of select="border"/>,
					thumbnailBorderVertical :   <xsl:value-of select="border"/>,
					thumbnailGutterWidth :   <xsl:value-of select="gutter"/>,
					thumbnailGutterHeight :   <xsl:value-of select="gutter"/>,
					viewerToolbar: { display: false },
					galleryLastRowFull:  false,

					// ### gallery content ###
					items: [
					<xsl:for-each select="picture">
						{
						src: '<xsl:value-of select="concat(@path, pic)"/>',
						srct: '<xsl:value-of select="concat(@path, pic)"/>',
						title: '<xsl:value-of select="header"/>'
						},
					</xsl:for-each>
					]
					});
					<xsl:if test="f:num(spoiler) &gt; 0">
						}
					</xsl:if>
					}
				</script>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="simple_gallery" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<a class="toggle" href="#spoiler-{@id}" onclick="initNano{@id}(); return false;" rel="Скрыть галерею ↑">Галерея ↓</a>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<div id="nanogallery{@id}">
				<script>
					<xsl:if test="f:num(spoiler) = 0">
						$(document).ready(function(){
						initNano<xsl:value-of select="@id"/>();
						});
					</xsl:if>
					function initNano<xsl:value-of select="@id"/>(){
					<xsl:if test="f:num(spoiler) &gt; 0">
						if(!$("<xsl:value-of select="concat('#nanogallery', @id)"/>").is(":visible")){
					</xsl:if>
					$("#nanogallery<xsl:value-of select="@id"/>").nanogallery2( {
					// ### gallery settings ###
					thumbnailHeight:  <xsl:value-of select="height"/>,
					thumbnailWidth:   <xsl:value-of select="width"/>,
					thumbnailBorderHorizontal :   <xsl:value-of select="border"/>,
					thumbnailBorderVertical :   <xsl:value-of select="border"/>,
					thumbnailGutterWidth :   <xsl:value-of select="gutter"/>,
					thumbnailGutterHeight :   <xsl:value-of select="gutter"/>,
					viewerToolbar: { display: false },
					galleryLastRowFull:  false,

					// ### gallery content ###
					items: [
					<xsl:for-each select="pic">
						{ src: '<xsl:value-of select="concat(../@path, .)"/>', srct: '<xsl:value-of select="concat(../@path, .)"/>' },
					</xsl:for-each>
					]
					});
					<xsl:if test="f:num(spoiler) &gt; 0">
						}
					</xsl:if>
					}
				</script>
			</div>
		</div>
	</xsl:template>




	<!-- ****************************************************************************************************************************************** -->
	<!-- ****************************                                		 ********************************************************************** -->
	<!-- ****************************  	  		  	СТРАНИЦА  	 	  	 	 ********************************************************************** -->
	<!-- ****************************                                  		 ********************************************************************** -->
	<!-- ****************************************************************************************************************************************** -->


	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
		<html lang="en">
			<head>
				<base href="{$main_host}"/>
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
				<script src="js/jquery-3.5.1.min.js"></script>
				<script src="js/fotorama.js"></script>
				<script src="js/slick.min.js"></script>
				<script src="js/script.js"></script>
				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<xsl:call-template name="SEO"/>
				<link rel="stylesheet" type="text/css" href="http://main.must.by/magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/styles.css?version=1.0"/>
				<link href="css/fotorama.css" rel="stylesheet"/>
				<link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css"/>
				<link href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"/>
				<link href="js/jquery-ui/jquery-ui.css" rel="stylesheet" type="text/css"/>
				<script type="text/javascript" src="js/nanogallery/jquery.nanogallery2.js"></script>
				<script type="text/javascript" src="js/script.js"></script>
				<script type="text/javascript" src="fancybox/source/jquery.fancybox.pack.js"></script>
				<script type="text/javascript" src="js/jquery-ui/jquery-ui.js"></script>
				<link rel="stylesheet" type="text/css" href="fancybox/source/jquery.fancybox.css" media="screen" />
				<script  type="text/javascript">
					function toggleDesktopCatalogSection(id) {
						var subsec = $('#subsec_' + id);
						var isHidden = subsec.css('display') == 'none';
						if (isHidden) {
							subsec.show();
							$('#cat_plus_' + id).attr('src', 'img/icon-toggle-minus.png');
						} else {
							subsec.hide();
							$('#cat_plus_' + id).attr('src', 'img/icon-toggle-plus.png');
						}
						return false;
					}
				</script>
			</head>

			<body>
				<div class="wrapper">
					<div class="top-info">
						<div class="container">
							<div class="top-info__wrap wrap">
								<div class="top-info__location">
									<p>Магазин «Белчип», ул. Л.Беды, 2Б</p>
									<div>
										<a href="mailto:info@belchip.by">info@belchip.by</a>
										<a href="about.html">схема проезда</a>
									</div>
								</div>
								<div class="top-info__phones phones">
									<div class="phones__item">
										<div class="phones__number">+375 (29) 126-14-13</div>
										<div class="phones__description">розница</div>
									</div>
									<div class="phones__item">
										<div class="phones__number">+375 (29) 126-14-13</div>
										<div class="phones__description">розница</div>
									</div>
									<div class="phones__item">
										<div class="phones__number">+375 (29) 126-14-13</div>
										<div class="phones__description">розница</div>
									</div>
									<div class="phones__item">
										<div class="phones__number">+375 (29) 126-14-13</div>
										<div class="phones__description">розница</div>
									</div>
									<div class="phones__item">
										<div class="phones__number">+375 (29) 126-14-13</div>
										<div class="phones__description">розница</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="header">
						<div class="container">
							<div class="header__wrap wrap">
								<div class="header__column logo">
									<a href="index.html">
										<img class="logo__image" src="img/logo.png" alt=""/>
									</a>
									<div class="work-hours logo__hours">
										<p>пн.-пт.<span>9:00-20:00</span>
										</p>
										<p>сб.-вс.<span>10:00-17:00</span>
										</p>
									</div>
								</div>
								<div class="header__column header__search header-search">
									<form action="search/" method="post">
										<div>
											<a class="useless-button" href="">
												<img src="img/icon-search.png" alt=""/>
											</a>
											<input class="input header-search__input" id="q-ipt" type="text" placeholder="Введите поисковый запрос" autocomplete="off" name="" value="" autofocus=""/>
											<a class="header-search__reset" href="">
												<img src="img/icon-close.png" alt=""/>
											</a>
											<button class="button header-search__button" type="submit">Найти</button>
										</div>
										<div>
											<div class="header-search__option">
												<input type="checkbox"/>
												<label for="">только по товарам в наличии</label>
											</div>
											<div class="header-search__option">
												<input type="checkbox"/>
												<label for="">строгое соответствие</label>
											</div>
										</div>
										<div class="suggest">
											<div class="suggest__text">Продолжайте вводить текст или выберите результат</div>
											<div class="suggest__results">
												<div class="suggest__result suggest-result">
													<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
													<div class="suggest-result__info">
														<div class="suggest-result__code">код 04434</div>
														<div class="suggest-result__vendor">KLS</div>
														<div class="suggest-result__price">1,46 руб./шт.</div>
														<div class="suggest-result__status">на складе: 19 шт.</div>
													</div>
												</div>
												<div class="suggest__result suggest-result">
													<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
													<div class="suggest-result__info">
														<div class="suggest-result__code">код 04434</div>
														<div class="suggest-result__vendor">KLS</div>
														<div class="suggest-result__price">1,46 руб./шт.</div>
														<div class="suggest-result__status">на складе: 19 шт.</div>
													</div>
												</div>
												<div class="suggest__result suggest-result">
													<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
													<div class="suggest-result__info">
														<div class="suggest-result__code">код 04434</div>
														<div class="suggest-result__vendor">KLS</div>
														<div class="suggest-result__price">1,46 руб./шт.</div>
														<div class="suggest-result__status">на складе: 19 шт.</div>
													</div>
												</div>
												<div class="suggest__result suggest-result">
													<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
													<div class="suggest-result__info">
														<div class="suggest-result__code">код 04434</div>
														<div class="suggest-result__vendor">KLS</div>
														<div class="suggest-result__price">1,46 руб./шт.</div>
														<div class="suggest-result__status">на складе: 19 шт.</div>
													</div>
												</div>
											</div>
											<a class="suggest__all" href="search.html">Показать все результаты</a>
										</div>
									</form>
								</div>
								<div class="header__column header__column_links header-icons">
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-cart.png" alt=""/>
											<div class="header-icon__counter">2</div>
										</div>
										<div class="header-icon__info">
											<a href="">Корзина</a>
											<span>58,25</span>
											<a class="header-icon__dd">
												<img src="img/icon-caret-down-small.png" alt=""/>
											</a>
										</div>
										<div class="dd-cart">
											<div class="dd-cart__scroll">
												<div class="dd-cart__item">
													<a class="dd-cart__image" href="">
														<img src="img/image-device.jpg" alt=""/>
													</a>
													<div class="dd-cart__info">
														<a class="dd-cart__name" href="">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
														<div class="dd-cart__code">Кол-во: 6 шт.</div>
														<div class="ddcart__price">21,17 руб.</div>
													</div>
													<a class="dd-cart__close" href="">
														<img src="img/icon-close.png" alt=""/>
													</a>
												</div>
												<div class="dd-cart__item">
													<a class="dd-cart__image" href="">
														<img src="img/image-device.jpg" alt=""/>
													</a>
													<div class="dd-cart__info">
														<a class="dd-cart__name" href="">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
														<div class="dd-cart__code">Кол-во: 6 шт.</div>
														<div class="ddcart__price">21,17 руб.</div>
													</div>
													<a class="dd-cart__close" href="">
														<img src="img/icon-close.png" alt=""/>
													</a>
												</div>
												<div class="dd-cart__item">
													<a class="dd-cart__image" href="">
														<img src="img/image-device.jpg" alt=""/>
													</a>
													<div class="dd-cart__info">
														<a class="dd-cart__name" href="">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
														<div class="dd-cart__code">Кол-во: 6 шт.</div>
														<div class="ddcart__price">21,17 руб.</div>
													</div>
													<a class="dd-cart__close" href="">
														<img src="img/icon-close.png" alt=""/>
													</a>
												</div>
												<div class="dd-cart__item">
													<a class="dd-cart__image" href="">
														<img src="img/image-device.jpg" alt=""/>
													</a>
													<div class="dd-cart__info">
														<a class="dd-cart__name" href="">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
														<div class="dd-cart__code">Кол-во: 6 шт.</div>
														<div class="ddcart__price">21,17 руб.</div>
													</div>
													<a class="dd-cart__close" href="">
														<img src="img/icon-close.png" alt=""/>
													</a>
												</div>
												<div class="dd-cart__item">
													<a class="dd-cart__image" href="">
														<img src="img/image-device.jpg" alt=""/>
													</a>
													<div class="dd-cart__info">
														<a class="dd-cart__name" href="">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
														<div class="dd-cart__code">Кол-во: 6 шт.</div>
														<div class="ddcart__price">21,17 руб.</div>
													</div>
													<a class="dd-cart__close" href="">
														<img src="img/icon-close.png" alt=""/>
													</a>
												</div>
											</div>
											<a class="dd-cart__button" href="cart.html">Перейти в корзину</a>
										</div>
									</div>
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-currency.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<a href="">RUB</a>
											<a class="header-icon__dd">
												<img src="img/icon-caret-down-small.png" alt=""/>
											</a>
										</div>
										<div class="dropdown header-icon__dropdown">
											<a class="dropdown__item active" href="">RUB</a>
											<a class="dropdown__item" href="">RUB</a>
											<a class="dropdown__item" href="">RUB</a>
										</div>
									</div>
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-star.png" alt=""/>
											<div class="header-icon__counter">21</div>
										</div>
										<div class="header-icon__info">
											<a href="">Избранное</a>
										</div>
									</div>
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-truck.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<a href="">Где заказ</a>
										</div>
									</div>
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-user.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<a href="">Вход</a>
											<a class="header-icon__dd">
												<img src="img/icon-caret-down-small.png" alt=""/>
											</a>
										</div>
										<div class="dropdown dropdown_last header-icon__dropdown">
											<a class="dropdown__item" href="">Вход</a>
											<a class="dropdown__item" href="">Регистрация</a>
											<a class="dropdown__item" href="">Кабинет</a>
											<a class="dropdown__item" href="">История заказов</a>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="header-mobile">
						<div class="header-mobile__top">
							<a class="header-mobile__logo">
								<img src="img/logo.png" alt=""/>
							</a>
							<div class="header-mobile__small-icons">
								<div class="mobile-small-icon header-mobile__small-icon">
									<div class="mobile-small-icon__icon">
										<img src="img/icon-small-search.png" alt=""/>
									</div>
									<a class="mobile-small-icon__link"></a>
								</div>
								<div class="mobile-small-icon header-mobile__small-icon">
									<div class="mobile-small-icon__icon">
										<img src="img/icon-small-phone.png" alt=""/>
									</div>
									<a class="mobile-small-icon__link"></a>
								</div>
								<div class="mobile-small-icon header-mobile__small-icon">
									<div class="mobile-small-icon__icon">
										<img src="img/icon-small-menu.png" alt=""/>
									</div>
									<a class="mobile-small-icon__link"></a>
								</div>
							</div>
						</div>
						<div class="header-mobile__nav">
							<div class="header-mobile__menu">
								<button class="button">Каталог</button>
							</div>
							<div class="header-mobile__icons">
								<div class="header-mobile__icon mobile-icon">
									<div class="mobile-icon__icon">
										<img src="img/icon-cart.png" alt=""/>
									</div>
									<div class="mobile-icon__qty">2</div>
									<div class="mobile-icon__label">58,25</div>
									<a class="mobile-icon__link"></a>
								</div>
								<div class="header-mobile__icon mobile-icon">
									<div class="mobile-icon__icon">
										<img src="img/icon-star.png" alt=""/>
									</div>
									<div class="mobile-icon__qty">2</div>
									<div class="mobile-icon__label">Избранное</div>
									<a class="mobile-icon__link"></a>
								</div>
								<div class="header-mobile__icon mobile-icon">
									<div class="mobile-icon__icon">
										<img src="img/icon-currency.png" alt=""/>
									</div>
									<div class="mobile-icon__label">BYN</div>
									<a class="mobile-icon__link"></a>
								</div>
								<div class="header-mobile__icon mobile-icon">
									<div class="mobile-icon__icon">
										<img src="img/icon-user.png" alt=""/>
									</div>
									<div class="mobile-icon__label">Конст...</div>
									<a class="mobile-icon__link"></a>
								</div>
							</div>
						</div>
					</div>
					<div class="main-menu">
						<div class="container">
							<div class="main-menu__wrap wrap">
								<div class="main-menu__item active">
									<a href="index.html">
										<span>Главная</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="catalog.html">
										<span>Каталог</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="about.html">
										<span>О магазине</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="price.html">
										<span>Прайс-лист</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="payments.html">
										<span>Оплата и доставка</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="info.html">
										<span>Полезная информация</span>
									</a>
								</div>
								<div class="main-menu__item">
									<a href="">
										<span>Translate</span>
									</a>
								</div>
							</div>
						</div>
					</div>
					<div class="content">
						<div class="container">
							<div class="content__wrap">
								<div class="content__side">
									<div class="side-menu">
										<div class="side-menu__header">
											<a class="side-menu__title" href="catalog.html">Каталог товаров</a>

											<xsl:if test="not(/page/catalog/udate_in_progress = '1')">
												<div class="side-menu__update">Обновлен
													<xsl:value-of select="substring(/page/catalog/date, 0,11)" />
													в <xsl:value-of select="substring(/page/catalog/date, 12, 5)" />
												</div>
											</xsl:if>
											<xsl:if test="/page/catalog/udate_in_progress = '1'">
												<div class="side-menu__update">
													<b>Идет обновление каталога!</b><br/>
													Заказ товаров и поиск могут работать некорректно, пожалуйста, подождите несколько минут.
												</div>
											</xsl:if>
										</div>
										<div class="side-menu__links">
											<xsl:apply-templates select="page/catalog/section" mode="desktop">
												<xsl:with-param name="level" select="'1'"/>
											</xsl:apply-templates>
										</div>
									</div>
								</div>
								<xsl:call-template name="CONTENT"/>
							</div>
						</div>
					</div>
					<div class="footer">
						<div class="container">
							<div class="footer__wrap">
								<div class="footer__column">
									<div class="footer__title">Частное производственно-торговое унитарное предприятие «БелЧип»</div>
									<div class="footer__text">
										<p> </p>Республика Беларусь, 220040,г.Минск,ул Л.Беды, 2Б, пом.317
										<br/>УНП 191623250 Свидетельство №191623250
										<br/>выдано 17.01.2012г Минским горисполкомом
										<br/>Дата регистрации интернет-магазина в Торговом реестре Республики беларусь: 29.12.2016 г.
									</div>
								</div>
								<div class="footer__column">
									<div class="footer__phones phones">
										<div class="phones__item">
											<div class="phones__number">+375 (29) 126-14-13</div>
											<div class="phones__description">розница</div>
										</div>
										<div class="phones__item">
											<div class="phones__number">+375 (29) 126-14-13</div>
											<div class="phones__description">розница</div>
										</div>
										<div class="phones__item">
											<div class="phones__number">+375 (29) 126-14-13</div>
											<div class="phones__description">розница</div>
										</div>
										<div class="phones__item">
											<div class="phones__number">+375 (29) 126-14-13</div>
											<div class="phones__description">розница</div>
										</div>
										<div class="phones__item">
											<div class="phones__number">+375 (29) 126-14-13</div>
											<div class="phones__description">розница</div>
										</div>
										<div class="footer__phones-jur">Работа с юридическими лицами пн.-пт. с 9:00 до 17:30</div>
									</div>
								</div>
								<div class="footer__column">
									<div class="footer__social footer-social">
										<div class="footer-social__icons">
											<a class="footer-social__icon">
												<img src="img/icon-social-footer-01.png" alt=""/>
											</a>
											<a class="footer-social__icon">
												<img src="img/icon-social-footer-01.png" alt=""/>
											</a>
											<a class="footer-social__icon">
												<img src="img/icon-social-footer-01.png" alt=""/>
											</a>
										</div>
										<a href="mailto:info@belchip.by">info@belchip.by</a>
									</div>
									<div class="footer__copyright copyright">© «Белчип», 2012–2020</div>
									<a class="forever" href="">Разработка сайта
										<br/>студия веб-дизайна Forever
									</a>
								</div>
								<div class="footer__payments footer-payments">
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
									<div class="footer-payments__icon">
										<img src="img/icon-payment-footer-01.png" alt=""/>
									</div>
								</div>
							</div>
						</div>
					</div><!-- меню каталога-->
					<div class="popup" style="display: none;" id="mobile_catalog">
						<div class="popup__body">
							<div class="popup__content">
								<div class="popup__header">
									<div class="popup__title">Каталог продукции</div>
									<a class="popup__close" href="#" onclick="$('mobile_catalog').hide(); return false;">
										<img src="img/icon-menu-close.png" alt=""/>
									</a>
								</div>
								<div class="popup-menu">
									<xsl:apply-templates select="page/catalog/section" mode="mobile"/>
								</div>
							</div>
							<xsl:for-each select="page/catalog//section[section]">
								<div class="popup__content popup__content_next" id="mobile_sec_{@id}">
									<div class="popup__header">
										<a class="popup__back" href="#" onlick="$('#mobile_sec{@id}').css('transform', 'translateX(100%)'); return false;">
											<img src="img/icon-menu-back.png" alt=""/>
										</a>
										<div class="popup__title"><xsl:value-of select="name"/></div>
										<a class="popup__close" href="#" onclick="$('mobile_catalog').hide(); return false;">
											<img src="img/icon-menu-close.png" alt=""/>
										</a>
									</div>
									<div class="popup-menu">
										<xsl:apply-templates select="section" mode="mobile"/>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div><!-- главное меню-->
					<div class="popup" style="display: none;">
						<div class="popup__body">
							<div class="popup__content">
								<div class="popup__header">
									<div class="popup__title">Меню</div>
									<a class="popup__close">
										<img src="img/icon-menu-close.png" alt=""/>
									</a>
								</div>
								<div class="popup-menu">
									<div class="popup-menu__item">
										<div class="popup-menu__link">Главная страница</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
									<div class="popup-menu__item">
										<div class="popup-menu__link">Каталог товаров</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
									<div class="popup-menu__item">
										<div class="popup-menu__link">О магазине</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
									<div class="popup-menu__item">
										<div class="popup-menu__link">Прайс-лист</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
									<div class="popup-menu__item">
										<div class="popup-menu__link">Оплата и доставка</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
									<div class="popup-menu__item">
										<div class="popup-menu__link">Полезная информация</div>
										<div class="popup-menu__arrow">
											<img src="img/icon-menu-right.png" alt=""/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div><!-- поиск-->
					<div class="popup" style="display: none;">
						<div class="popup__body">
							<div class="popup__content">
								<div class="popup__header">
									<div class="popup__title">Поиск по каталогу</div>
									<a class="popup__close">
										<img src="img/icon-menu-close.png" alt=""/>
									</a>
								</div>
								<div class="search-mobile">
									<form>
										<div>
											<input type="text"/>
											<div class="search-mobile__reset">
												<img src="img/icon-close.png" alt=""/>
											</div>
											<button class="button">Найти</button>
										</div>
										<div class="search-mobile__options">
											<div class="search-mobile__option search-option">
												<input type="checkbox"/>
												<label for="">только по товарам в наличии</label>
											</div>
											<div class="search-mobile__option search-option">
												<input type="checkbox"/>
												<label for="">строгое соответствие</label>
											</div>
										</div>
									</form>
									<div class="suggest">
										<div class="suggest__text">Продолжайте вводить текст или выберите результат</div>
										<div class="suggest__results">
											<div class="suggest__result suggest-result">
												<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
												<div class="suggest-result__info">
													<div class="suggest-result__code">код 04434</div>
													<div class="suggest-result__vendor">KLS</div>
													<div class="suggest-result__price">1,46 руб./шт.</div>
													<div class="suggest-result__status">на складе: 19 шт.</div>
												</div>
											</div>
											<div class="suggest__result suggest-result">
												<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
												<div class="suggest-result__info">
													<div class="suggest-result__code">код 04434</div>
													<div class="suggest-result__vendor">KLS</div>
													<div class="suggest-result__price">1,46 руб./шт.</div>
													<div class="suggest-result__status">на складе: 19 шт.</div>
												</div>
											</div>
											<div class="suggest__result suggest-result">
												<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
												<div class="suggest-result__info">
													<div class="suggest-result__code">код 04434</div>
													<div class="suggest-result__vendor">KLS</div>
													<div class="suggest-result__price">1,46 руб./шт.</div>
													<div class="suggest-result__status">на складе: 19 шт.</div>
												</div>
											</div>
											<div class="suggest__result suggest-result">
												<a class="suggest-result__link" href="product.html">Резистор SMD 0402 11K 1% / RC0402FR-0711KL (10шт.)</a>
												<div class="suggest-result__info">
													<div class="suggest-result__code">код 04434</div>
													<div class="suggest-result__vendor">KLS</div>
													<div class="suggest-result__price">1,46 руб./шт.</div>
													<div class="suggest-result__status">на складе: 19 шт.</div>
												</div>
											</div>
										</div>
										<a class="suggest__all" href="search.html">Показать все результаты</a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<xsl:call-template name="EXTRA_SCRIPTS"/>
				<xsl:for-each select="$body-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<div class="popup" style="display: none;" id="modal_popup" > +++ </div>
			</body>
		</html>
	</xsl:template>








	<xsl:template name="INC_FOOTER">
		<div class="footer">
			<div class="container">
				<div class="footer__wrap">
					<xsl:variable name="footer" select="page/common/footer"/>
					<div class="footer__column">
						<xsl:if test="$footer/block[1]/header and not($footer/block[1]/header = '')">
							<div class="footer__title"><xsl:value-of select="$footer/block[1]/header" /></div>
						</xsl:if>
						<a href="" class="forever">
							<img src="img/forever.png" alt="" />
							<span>Разработка сайта <br />студия веб-дизайна Forever</span>
						</a>
						<div class="google-rating">
							<div class="google-rating__stars">
								<img src="img/icon-google-rating.png" alt="" />
							</div>
							<div class="google-rating__text">
								Наш рейтинг: 4,8 (188 голосов)<br /> на основе <a href="https://google.com">отзывов</a> Google
							</div>
						</div>
					</div>
					<xsl:apply-templates select="$footer/block[position() &gt; 1]" mode="footer"/>
				</div>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>
