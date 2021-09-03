<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
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

	<xsl:variable name="pv" select="page/variables"/>
	<xsl:variable name="common" select="page/common"/>
	<xsl:variable name="base" select="page/base" />
	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>
	<xsl:variable name="currencies" select="page/catalog/currencies"/>
	<xsl:variable name="h1" select="'not-set'"/>
	<xsl:variable name="sel_news_id" select="page/selected_news/@id"/>
	<xsl:variable name="city" select="f:value_or_default($pv/city, 'Минск')"/>
	<xsl:variable name="query" select="$pv/q"/>
	<xsl:variable name="is_index" select="page/@name = 'index'"/>
	<xsl:variable name="is_catalog" select="page/@name = 'catalog'"/>
	<xsl:variable name="only_available" select="$pv/minqty = '0'"/>
	<xsl:variable name="search_strict" select="$pv/search = 'strict'"/>

	<xsl:variable name="active_menu_item"/>	<!-- переопределяется -->
	<xsl:variable name="user" select="page/user"/>
	<xsl:variable name="is_user_registered" select="$user/group/@name = 'registered'"/>
	<xsl:variable name="reg" select="page/registration"/>


	<!-- ****************************    НАСТРОЙКИ ОТОБРАЖЕНИЯ    ******************************** -->

	<xsl:variable name="page_menu" select="page/optional_modules/display_settings/side_menu_pages"/>
    <xsl:variable name="has_quick_search" select="page/optional_modules/display_settings/catalog_quick_search = ('simple', 'advanced')"/>

	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="''" />
	<xsl:variable name="meta_description" select="''" />
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />

	<xsl:variable name="default_canonical" select="if($is_index) then concat('/', tokenize(page/source_link, '\?')[1]) else ''" />
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






	<xsl:template name="CONTENT">
		<div class="content__main">
			<xsl:call-template name="PAGE_PATH"/>
			<xsl:call-template name="PAGE_HEADING"/>
			<xsl:if test="$seo[1]/text">
				<div class="section-text">
					<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
				</div>
			</xsl:if>
			<xsl:call-template name="CONTENT_INNER"/>
		</div>
	</xsl:template>



	<xsl:template name="CONTENT_INNER"/>
	<xsl:template name="EXTRA_SCRIPTS"/>
	<xsl:template name="SCRIPTS">
		<script>
			$(document).ready(function() {
				$(".device__zoom, .example1").fancybox({
					padding: [15,15,15,15],
					helpers: {
						overlay: {
							locked: false
						}
					},
					beforeLoad: function() {
						this.title = $(this.element).attr('caption');
					}
				});
				<xsl:text disable-output-escaping="yes">
				/*
				$('#search').submit(function(e) {
					if ($(this).find('#q-ipt').val().length &lt; 3) {
						e.preventDefault();
					}
				});*/
				</xsl:text>
			});

			$(".full_match_only").change(function(e) {
				var checked = $(this).find("input[type=checkbox]").is(":checked");
				if(checked){
					$(this).closest('form').attr("action", "<xsl:value-of select="page/search_strict_link" />");
					$(this).css("background", "rgb(173, 203, 53) none repeat scroll 0% 0%");
				} else {
					$(this).closest('form').attr("action", "<xsl:value-of select="page/search_link" />");
					$(this).css("background", "");
				}
			});
		</script>
	</xsl:template>




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
			<xsl:for-each select="$common/phone" >
				<xsl:if test="position() != 1">,</xsl:if>{
				"@type":"ContactPoint",
				"telephone":"<xsl:value-of select="tokenize(., '_')[1]"/>",
				"contactType":"<xsl:value-of select="tokenize(., '_')[2]"/>"
				}
			</xsl:for-each>
			]
			<xsl:if test="$common/email != ''">
				,"email":[<xsl:for-each select="$common/email" >
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


	<xsl:template name="TAB_SCRIPT">
		<script  type="text/javascript">
			$(document).ready(function() {
				$('.tab').click(function(e) {
					e.preventDefault();
					var a = $(this);
					$('.tab-container').hide();
					$('.tab-container' + a.attr('href')).show();
					$('.tab').removeClass('tab_active');
					a.addClass('tab_active');
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



	<xsl:variable name="message" select="$pv/message"/>
	<xsl:variable name="success" select="$pv/success = ('true', 'yes')"/>

	<xsl:template name="MESSAGE">
		<xsl:if test="$message and not($success)">
			<div class="alert alert-danger" style="background: #ffffcc; border: 1px solid #bb8; color: #bb0000;">
				<p><xsl:value-of select="$message"/></p>
			</div>
		</xsl:if>
		<xsl:if test="$message and $success">
			<div class="alert alert-success" style="background: #eeffee; border: 1px solid #9c9; color: #007700;">
				<p><xsl:value-of select="$message"/></p>
			</div>
		</xsl:if>
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
			<a class="side-menu__link" href="{show_products}"><xsl:value-of select="name"/></a>
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
				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<xsl:call-template name="SEO"/>
				<link rel="stylesheet" type="text/css" href="http://main.must.by/magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/styles.css?version=1.01"/>
				<link rel="stylesheet" href="css/fixes.css?version=1.10"/>
				<link href="css/fotorama.css" rel="stylesheet"/>
				<link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css"/>
				<link href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"/>
				<link href="js/jquery-ui/jquery-ui.css" rel="stylesheet" type="text/css"/>
				<script src="js/jquery-3.5.1.min.js"></script>
				<script src="js/jquery.form.min.js"></script>
				<script src="js/fotorama.js"></script>
				<script src="js/slick.min.js"></script>
				<script type="text/javascript" src="js/nanogallery/jquery.nanogallery2.js"></script>
				<script type="text/javascript" src="js/script.js"></script>
				<script type="text/javascript" src="js/web.js"></script>
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
					$(document).ready(function() {
						initQuickSearch();
					});
				</script>
			</head>

			<body>
				<div class="wrapper">
					<div class="top-info">
						<div class="container">
							<div class="top-info__wrap wrap">
								<xsl:value-of select="$common/topper/block[1]/text" disable-output-escaping="yes"/>
								<xsl:value-of select="$common/topper/block[2]/text" disable-output-escaping="yes"/>
							</div>
						</div>
					</div>
					<div class="header">
						<div class="container">
							<div class="header__wrap wrap">
								<div class="header__column logo">
									<a href="{page/index_link}">
										<img class="logo__image" src="img/logo.png" alt=""/>
									</a>
									<xsl:value-of select="$common/topper/block[3]/text" disable-output-escaping="yes"/>
								</div>
								<div class="header__column header__search header-search">
                                    <script>
                                        function strictRedirect() {
                                            <xsl:choose>
                                                <xsl:when test="page/@name = 'search'">
                                                    var form = $('#search');
                                                    form.attr('action', '<xsl:value-of select="page/search_strict_link" />');
                                                    form.submit();
                                                </xsl:when>
                                                <xsl:when test="page/@name = 'search_strict'">
                                                    var form = $('#search');
                                                    form.attr('action', '<xsl:value-of select="page/search_link" />');
                                                    form.submit();
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:if test="$search_strict">
                                                        location.replace('<xsl:value-of select="concat(page/base, page/set_search_normal_link)"/>');
                                                    </xsl:if>
                                                    <xsl:if test="not($search_strict)">
                                                        location.replace('<xsl:value-of select="concat(page/base, page/set_search_strict_link)"/>');
                                                    </xsl:if>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        }
                                    </script>
                                    <form action="{if ($search_strict) then page/search_strict_link else page/search_link}" method="post" id="search">
										<div>
											<a class="useless-button" href="#" onclick="$(this).closest('form').submit(); return false;">
												<img src="img/icon-search.png" alt=""/>
											</a>
											<input class="input header-search__input"
												   ajax-href="{page/search_ajax_link}" result="search-result"
												   query="q" min-size="3" id="q-ipt" type="text" minlength="3"
												   placeholder="Введите поисковый запрос" autocomplete="off"
												   name="q" value="{$query}" autofocus="autofocus" onfocus="this.selectionStart = this.selectionEnd = this.value.length"/>
											<a class="header-search__reset" href="">
												<img src="img/icon-close.png" alt=""/>
											</a>
											<button class="button header-search__button" type="submit">Найти</button>
										</div>
										<div>
											<div class="header-search__option">
												<label style="padding: 4px;{' background: rgb(173, 203, 53) none repeat scroll 0% 0%;'[$only_available]}">
													<input style="display: inline-block; vertical-align: middle;"
                                                           type="checkbox"
                                                           onclick="location.replace('{page/base}/{if ($only_available) then page/show_all else page/show_only_available}')">
                                                        <xsl:if test="$only_available">
                                                            <xsl:attribute name="checked">checked</xsl:attribute>
                                                        </xsl:if>
                                                    </input>
													только по товарам в наличии
												</label>
											</div>
											<div class="header-search__option">
                                                <label style="padding: 4px;{' background: rgb(173, 203, 53) none repeat scroll 0% 0%;'[$search_strict]}" class="full_match_only">
													<input style="display: inline-block; vertical-align: middle;"
														   type="checkbox"
														   onclick="strictRedirect()">
														<xsl:if test="$search_strict">
															<xsl:attribute name="checked">checked</xsl:attribute>
														</xsl:if>
													</input>
													строгое соответствие
												</label>
											</div>
										</div>
										<div class="suggest" id="search-result">
											+++ SEARCH DESKTOP +++
										</div>
									</form>
								</div>
								<div class="header__column header__column_links header-icons">
									<div class="header-icons__icon header-icon" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no" >
										<div class="header-icon__icon">
											<img src="img/icon-cart.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<a>Корзина ..........</a>
										</div>
									</div>
                                    <xsl:if test="$currencies">
                                        <xsl:variable name="currency_link" select="page/set_currency"/>
                                        <div class="header-icons__icon header-icon">
                                            <div class="header-icon__icon">
                                                <img src="img/icon-currency.png" alt=""/>
                                            </div>
                                            <div class="header-icon__info">
                                                <a><xsl:value-of select="if ($currency) then $currency else 'BYN'" /></a>
                                                <a class="header-icon__dd">
                                                    <img src="img/icon-caret-down-small.png" alt=""/>
                                                </a>
                                            </div>
                                            <div class="dropdown header-icon__dropdown">
												<a class="dropdown__item{' active'[$currency = 'BYN']}" href="{concat($currency_link, 'BYN')}">BYN</a>
												<xsl:for-each select="$currencies/*[ends-with(name(), '_rate')]">
													<xsl:variable name="cur" select="substring-before(name(), '_rate')"/>
													<a class="dropdown__item{' active'[$currency = $cur]}"
													   href="{concat($currency_link, $cur)}"><xsl:value-of select="$cur"/></a>
												</xsl:for-each>
                                            </div>
                                        </div>
                                    </xsl:if>
									<div class="result header-icons__icon header-icon" id="fav_ajax" ajax-href="{page/fav_ajax_link}" ajax-show-loader="no" >
										<div class="header-icon__icon">
											<img src="img/icon-star.png" alt=""/>
										</div>
										<div class="header-icon__info"><a>Избранное</a></div>
									</div>
									<div class="header-icons__icon header-icon" id="some_ajax">
										<div class="header-icon__icon">
											<img src="img/icon-truck.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<a href="{page/order_search_link}">Где заказ</a>
										</div>
									</div>
									<div class="header-icons__icon header-icon">
										<div class="header-icon__icon">
											<img src="img/icon-user.png" alt=""/>
										</div>
										<div class="header-icon__info">
											<xsl:if test="not($is_user_registered)">
												<a href="{page/login_page_link}">Вход</a>
											</xsl:if>
											<xsl:if test="$is_user_registered">
												<a href="{page/personal_link}">
													<xsl:value-of select="if ($is_jur) then substring-before($reg/contact_name, ' ') else $reg/name"/>
												</a>
											</xsl:if>
											<a class="header-icon__dd">
												<img src="img/icon-caret-down-small.png" alt=""/>
											</a>
										</div>
										<div class="dropdown dropdown_last header-icon__dropdown">
											<xsl:if test="not($is_user_registered)">
												<a class="dropdown__item" href="{page/login_page_link}">Вход</a>
												<a class="dropdown__item" href="{page/register_link}">Регистрация</a>
											</xsl:if>
											<xsl:if test="$is_user_registered">
												<a class="dropdown__item" href="{page/personal_link}">Кабинет</a>
												<a class="dropdown__item" href="{page/purchase_history_link}">История заказов</a>
												<a class="dropdown__item" href="{page/logout_link}">Выход</a>
											</xsl:if>
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
								<div class="main-menu__item{' active'[$is_index]}">
									<a href="{page/index_link}">
										<span>Главная</span>
									</a>
								</div>
								<div class="main-menu__item{' active'[$is_catalog or $cur_sec]}">
									<a href="{page/catalog_link}">
										<span>Каталог</span>
									</a>
								</div>
								<xsl:apply-templates select="page/custom_pages/*[in_main_menu = 'да']" mode="menu_first"/>
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
											<a class="side-menu__title" href="{page/catalog_link}">Каталог товаров</a>

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
								<xsl:value-of select="$common/footer/block[1]/text" disable-output-escaping="yes"/>
								<xsl:value-of select="$common/footer/block[2]/text" disable-output-escaping="yes"/>
								<xsl:value-of select="$common/footer/block[3]/text" disable-output-escaping="yes"/>
								<xsl:value-of select="$common/footer/block[4]/text" disable-output-escaping="yes"/>
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
									<form action="{if ($search_strict) then page/search_strict_link else page/search_link}" method="post">
										<div>
											<input ajax-href="{page/search_ajax_link}" result="search-result-mobile"
												   query="q" min-size="3" type="text" minlength="3"
												   placeholder="Введите поисковый запрос" autocomplete="off"
												   name="q" value="{$query}"/>
											<div class="search-mobile__reset">
												<img src="img/icon-close.png" alt=""/>
											</div>
											<button class="button" type="submit">Найти</button>
										</div>
										<div class="search-mobile__options">
											<div class="search-mobile__option search-option">
												<label style="{'background: rgb(173, 203, 53) none repeat scroll 0% 0%;'[$only_available]}">
													<input style="display: inline-block; vertical-align: middle;"
														   type="checkbox"
														   onclick="location.replace('{page/base}/{if ($only_available) then page/show_all else page/show_only_available}')">
														<xsl:if test="$only_available">
															<xsl:attribute name="checked">checked</xsl:attribute>
														</xsl:if>
													</input>
													только по товарам в наличии
												</label>
											</div>
											<div class="search-mobile__option search-option">
												<input type="checkbox"/>
												<label style="padding: 4px;{' background: rgb(173, 203, 53) none repeat scroll 0% 0%;'[$search_strict]}" class="full_match_only">
													<input style="display: inline-block; vertical-align: middle;"
														   type="checkbox"
														   onclick="strictRedirect()">
														<xsl:if test="$search_strict">
															<xsl:attribute name="checked">checked</xsl:attribute>
														</xsl:if>
													</input>
													строгое соответствие
												</label>
											</div>
										</div>
									</form>
									<div class="suggest" id="search-result-mobile">
										+++ SEARCH_MOBILE +++
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<xsl:call-template name="SCRIPTS"/>

				<xsl:call-template name="EXTRA_SCRIPTS"/>
				<xsl:for-each select="$body-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<div class="popup" style="display: none;" id="modal_popup" > +++ </div>
				<script type="text/javascript" src="admin/ajax/ajax.js"/>
			</body>
		</html>
	</xsl:template>


</xsl:stylesheet>
