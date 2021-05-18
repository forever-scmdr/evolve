<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="my_price_ajax.xsl"/>
	<xsl:import href="one_click_ajax.xsl"/>
	<xsl:import href="subscribe_ajax.xsl"/>
	<xsl:import href="snippets/product.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>


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


	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->



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


	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="top-info">
			<div class="container">
				<!-- <xsl:value-of select="$common/top" disable-output-escaping="yes"/> -->
				<xsl:variable name="has_city" select="$common/topper/block[header = $city]"/>
				<xsl:variable name="has_many_cities" select="count($common/topper/block) &gt; 1"/>
				<xsl:for-each select="$common/topper/block">
					<xsl:variable name="active" select="($has_city and header = $city) or (not($has_city) and position() = 1)"/>
					<div class="top-info__wrap wrap" id="{@id}" style="display: {'flex'[$active]}{'none'[not($active)]}">
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</xsl:for-each>
			</div>
		</div>

		<div class="header">
			<div class="container">
				<div class="header__wrap">
					<a href="{$main_host}" class="header__logo">
						<img src="img/logo.png" alt="" />
					</a>
					<div class="header__search">
						<form action="{page/search_link}" method="post">
							<input type="text" placeholder="Поиск по каталогу товаров" autocomplete="off" name="q" value="{page/variables/q}" autofocus="autofocus" id="q-ipt" />
							<button class="button" type="submit">Найти</button>
						</form>
					</div>
					<div class="header__mobile-icons header-mobile-icons">
						<div class="header-mobile-icon">
							<div class="header-mobile-icon__icon">
								<img src="img/icon-cart.png" alt="" />
							</div>
							<!-- <div class="header-mobile-icon__label">2</div> -->
							<a class="header-mobile-icon__link" href="/cart"></a>
						</div>
						<div class="header-mobile-icon">
							<div class="header-mobile-icon__icon">
								<img src="img/icon-phone.png" alt="" />
							</div>
							<a class="header-mobile-icon__link" href="/kontakty"></a>
						</div>
						<div class="header-mobile-icon">
							<div class="header-mobile-icon__icon">
								<img src="img/icon-menu.png" alt="" />
							</div>
							<a class="header-mobile-icon__link" href="javascript:showMobileMainMenu()"></a>
						</div>
					</div>
					<div class="header__icons header-icons">
						<div class="header-icon" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no"></div>
						<div class="header-icon" id="fav_ajax" ajax-href="{page/fav_ajax_link}" ajax-show-loader="no"></div>
						<div class="header-icon" id="compare_ajax" ajax-href="{page/compare_ajax_link}" ajax-show-loader="no"></div>
						<div class="header-icon" id="personal_desktop" ajax-href="{page/personal_ajax_link}" ajax-show-loader="no"></div>
					</div>
				</div>
			</div>
		</div>

		<div class="main-menu">
			<div class="container">
				<div class="main-menu__wrap">
					<div class="main-menu__item">
						<a href="{page/catalog_link}" class="{'active'[$active_menu_item = 'catalog']}" id="catalog_main_menu">
							<div class="main-menu__icon">
								<img src="img/icon-catalog.png" alt="" />
							</div>
							<span>Каталог товаров</span>
						</a>
						<div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
							<div class="sections">
								<xsl:for-each select="page/catalog/section">
									<xsl:if test="section">
										<a href="{show_products}" rel="#sub_{@id}">
											<xsl:value-of select="name" />
										</a>
									</xsl:if>
									<xsl:if test="not(section)">
										<a href="{show_products}">
											<xsl:value-of select="name" />
										</a>
									</xsl:if>
								</xsl:for-each>
							</div>

							<xsl:for-each select="page/catalog/section">
								<div class="subsections" style="display: none" id="sub_{@id}">
									<xsl:for-each select="section">
										<a href="{show_products}"><xsl:value-of select="name" /></a>
									</xsl:for-each>
								</div>
							</xsl:for-each>
						</div>
					</div>
					<xsl:for-each select="page/news[in_main_menu = 'да']">
						<xsl:variable name="key" select="@key"/>
						<xsl:variable name="sel" select="page/varibles/sel"/>
						<div class="main-menu__item {'active'[$sel = $key]}">
							<a href="{show_page}"><span><xsl:value-of select="name"/></span></a>
						</div>
					</xsl:for-each>
					<xsl:apply-templates select="page/custom_pages/*[in_main_menu = 'да']" mode="menu_first"/>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="block" mode="footer">
		<div class="footer__column">
			<xsl:if test="header and not(header = '')"><div class="footer__title"><xsl:value-of select="header" /></div></xsl:if>
			<div class="footer__text"><xsl:value-of select="text" disable-output-escaping="yes"/></div>
		</div>
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
						<a href="http://forever.by" class="forever">Разработка сайта — <br />студия веб-дизайна Forever
						</a>
					</div>
					<xsl:apply-templates select="$footer/block[position() &gt; 1]" mode="footer"/>
				</div>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="INC_MOBILE_MENU">
		<div class="menu-container mobile">
			<div class="menu-overlay" onclick="showMobileMainMenu()"></div>
			<div class="menu-content">
				<div class="menu-content__item" id="personal_mobile">
<!-- 					<div class="menu-content__icon">
						<img src="img/icon-user.png" alt=""/>
					</div>
					<div class="menu-content__links">
						<a href="{page/login_link}" class="menu-content__link">Вход / регистрация</a>
					</div> -->
				</div>
				<div class="menu-content__line"></div>
				<div class="menu-content__item">
					<div class="menu-content__icon">
						<img src="img/icon-catalog-dark.png" alt=""/>
					</div>
					<div class="menu-content__links">
						<a onclick="showMobileCatalogMenu(); return false" class="menu-content__link">Каталог товаров</a>
					</div>
				</div>
				<div class="menu-content__item">
					<div class="menu-content__icon">
						<img src="img/icon-cart.png" alt=""/>
					</div>
					<div class="menu-content__links">
						<a href="{page/cart_link}" class="menu-content__link">Корзина</a>
					</div>
				</div>
				<div class="menu-content__item">
					<div class="menu-content__icon">
						<img src="img/icon-star.png" alt=""/>
					</div>
					<div class="menu-content__links">
						<a href="{page/fav_link}" class="menu-content__link">Избранное</a>
					</div>
				</div>
				<div class="menu-content__item">
					<div class="menu-content__icon">
						<img src="img/icon-compare.png" alt=""/>
					</div>
					<div class="menu-content__links">
						<a href="{page/compare_link}" class="menu-content__link">Сравнение</a>
					</div>
				</div>
				<div class="menu-content__line"></div>
				<div class="menu-content__item">
					<div class="menu-content__icon">
						<!-- <img src="img/icon-compare.png" alt=""/> -->
					</div>
					<div class="menu-content__links">
						<xsl:for-each select="page/news">
							<a href="{show_page}" class="menu-content__link"><xsl:value-of select="name"/></a>
						</xsl:for-each>
						<xsl:for-each select="page/custom_pages/custom_page">
							<a href="{show_page}" class="menu-content__link"><xsl:value-of select="header"/></a>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>
		<script>
			function showMobileCatalogMenu() {
				$('#mobile_catalog_menu').toggle();
			}

			$(document).ready(function() {
				$("#mobile_catalog_menu .content li a[rel]").click(function(event) {
					//event.preventDefault();
					var menuItem = $(this);
					var parentMenuContainer = menuItem.closest('.content');
					parentMenuContainer.css('left', '-100%');
					var childMenuContainer = $(menuItem.attr('rel'));
					childMenuContainer.css('left', '0%');
				});

				$('#mobile_catalog_menu a.back').click(function(event) {
					event.preventDefault();
					var back = $(this);
					var childMenuContainer = back.closest('.content');
					childMenuContainer.css('left', '100%');
					var parentMenuContainer = $(back.attr('rel'));
					parentMenuContainer.css('left', '0%');
				});
			});

			function hideMobileCatalogMenu() {
				$("#mobile_catalog_menu .content").css('left', '100%');
				$("#m_sub_cat").css('left', '0%');
				$('#mobile_catalog_menu').hide();
			}
		</script>
	</xsl:template>



	<xsl:template name="INC_MOBILE_NAVIGATION">
		<div id="mobile_catalog_menu" class="nav-container mobile" style="display: none; position:absolute; width: 100%; overflow:hidden">
			<div class="content" id="m_sub_cat">
				<div class="small-nav">
					<a class="header">Каталог продукции</a>
					<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;">×</a>
				</div>
				<ul>
					<xsl:for-each select="page/catalog/section">
						<li>
							<xsl:if test="section">
								<a rel="{concat('#m_sub_', @id)}">
									<xsl:value-of select="name"/>
								</a>
								<span>></span>
							</xsl:if>
							<xsl:if test="not(section)">
								<a href="{show_products}">
									<xsl:value-of select="name"/>
								</a>
							</xsl:if>
						</li>
					</xsl:for-each>
				</ul>
			</div>
			<xsl:for-each select="page/catalog/section[section]">
				<div class="content next" id="m_sub_{@id}">
					<div class="small-nav">
						<a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a>
						<a href="{show_products}" class="header"><xsl:value-of select="name"/></a>
						<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
					</div>
					<ul>
						<xsl:for-each select="section">
							<li>
								<xsl:if test="section">
									<a rel="{concat('#m_sub_', @id)}">
										<xsl:value-of select="name"/>
									</a>
									<i class="fas fa-chevron-right"></i>
								</xsl:if>
								<xsl:if test="not(section)">
									<a href="{show_products}" >
										<xsl:value-of select="name"/>
									</a>
								</xsl:if>
							</li>
						</xsl:for-each>
					</ul>
				</div>
			</xsl:for-each>
			<xsl:for-each select="page/catalog/section/section[section]">
				<div class="content next" id="m_sub_{@id}">
					<div class="small-nav">
						<a href="" class="back" rel="#m_sub_{../@id}"><i class="fas fa-chevron-left"></i></a>
						<a href="{show_products}" class="header"><xsl:value-of select="name"/></a>
						<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
					</div>
					<ul>
						<xsl:for-each select="section">
							<li>
								<a href="{show_products}"><xsl:value-of select="name"/></a>
							</li>
						</xsl:for-each>
					</ul>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>



	<xsl:template name="INC_SIDE_MENU_INTERNAL">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL_CATALOG"/>
	</xsl:template>

	<xsl:template name="INC_SIDE_MENU_INTERNAL_NEWS">
		<div class="side-menu">
			<xsl:for-each select="page/news">
				<xsl:variable name="id" select="@id"/>
				<div class="side-menu__item side-menu__item_level_1">
					<a class="side-menu__link{' side-menu__link_active'[$id = $sel_news_id]}" href="{show_page}">
						<xsl:value-of select="name"/>
					</a>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name="INC_SIDE_MENU_INTERNAL_CATALOG">
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<xsl:variable name="l1_active" select="@id = $sel_sec_id"/>
				<div class="side-menu__item side-menu__item_level_1">
					<a class="side-menu__link{' side-menu__link_active'[$l1_active]}" href="{show_products}"><xsl:value-of select="name"/> </a>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<xsl:for-each select="section">
						<xsl:variable name="l2_active" select="@id = $sel_sec_id"/>
						<div class="side-menu__item side-menu__item_level_2">
							<a href="{show_products}" class="side-menu__link{' side-menu__link_active'[$l2_active]}"><xsl:value-of select="name"/></a>
						</div>
						<xsl:if test=".//@id = $sel_sec_id">
							<xsl:for-each select="section">
								<xsl:variable name="l3_active" select="@id = $sel_sec_id"/>
								<div class="side-menu__item side-menu__item_level_3">
									<a href="{show_products}" class="side-menu__link{' side-menu__link_active'[$l3_active]}"><xsl:value-of select="name"/></a>
								</div>
								<xsl:if test=".//@id = $sel_sec_id">
									<xsl:for-each select="section">
										<xsl:variable name="l4_active" select="@id = $sel_sec_id"/>
										<div class="side-menu__item side-menu__item_level_4">
											<a href="{show_products}" class="side-menu__link{' side-menu__link_active'[$l4_active]}"><xsl:value-of select="name"/></a>
										</div>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>



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






	<!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->




	<xsl:template name="COMMON_LEFT_COLOUMN">
		<xsl:value-of select="$common/left" disable-output-escaping="yes"/>
	</xsl:template>



	<xsl:template name="CATALOG_LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL_CATALOG"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
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




	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


	<xsl:template name="MAIN_CONTENT">
		<div class="content">
			<div class="container">
				<div class="content__wrap">
					<div class="content__side">
						<xsl:call-template name="LEFT_COLOUMN"/>
					</div>
					<div class="content__main">
						<xsl:call-template name="PAGE_PATH"/>
						<xsl:call-template name="PAGE_HEADING"/>
						<xsl:if test="$seo/text != '' and page/@name != 'section' and page/@name != 'sub'">
							<div class="text">
								<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
							</div>
						</xsl:if>
						<xsl:call-template name="CONTENT"/>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="LEFT_COLOUMN" />
	<xsl:template name="CONTENT" />
	<xsl:template name="INDEX_BLOCKS"/>
	<xsl:template name="EXTRA_SCRIPTS"/>






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
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/styles.css?version=1.50"/>
				<link rel="stylesheet" href="css/fixes.css?version=1.0"/>
				<link  href="fotorama/fotorama.css" rel="stylesheet" />
				<link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css"/>
				<link  href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"/>

				<xsl:if test="page/styles">
					<xsl:if test="page/styles/css != ''">
						<link rel="stylesheet" type="text/css" href="{concat(page/styles/@path,page/styles/css)}"/>
					</xsl:if>
					<style type="text/css">
						<xsl:for-each select="page/styles/label_style">
							.<xsl:value-of select="f:translit(name)"/>{
								<xsl:value-of select="style" disable-output-escaping="yes"/>
							}
						</xsl:for-each>
					</style>
				</xsl:if>

				<script type="text/javascript" src="js/nanogallery/jquery.nanogallery2.js"/>
				<xsl:if test="$seo/extra_style">
					<style>
						<xsl:value-of select="$seo/extra_style" disable-output-escaping="yes"/>
					</style>
				</xsl:if>
				<xsl:for-each select="$head-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
			</head>
			<body>
				<xsl:if test="$seo/body_class">
					<xsl:attribute name="class" select="$seo/body_class"/>
				</xsl:if>
				<xsl:for-each select="$body-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>
				<xsl:if test="page/@name = 'index'"><xsl:attribute name="class" select="'index'"/></xsl:if>

				<!-- ALL CONTENT BEGIN -->
				<div class="wrapper">
					<xsl:call-template name="INC_DESKTOP_HEADER"/>
					<xsl:call-template name="MAIN_CONTENT"/>
					<xsl:call-template name="INDEX_BLOCKS"/>
					<xsl:call-template name="INC_FOOTER"/>
				</div>
				<!-- ALL CONTENT END -->


				<xsl:call-template name="INC_MOBILE_MENU"/>
				<xsl:call-template name="INC_MOBILE_NAVIGATION"/>
				<xsl:call-template name="MY_PRICE_FORM"/>
				<xsl:call-template name="ONE_CLICK_FORM"/>
				<xsl:call-template name="SUBSCRIBE_FORM"/>
				<script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script>
				<!-- <script type="text/javascript" src="js/bootstrap.js"/> -->
				<script type="text/javascript" src="admin/ajax/ajax.js"/>
				<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
				<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
				<script type="text/javascript" src="js/web.js"/>
				<!-- <script type="text/javascript" src="slick/slick.min.js"></script> -->
				<script type="text/javascript">
					$(document).ready(function(){
						$(".magnific_popup-image, a[rel=facebox]").magnificPopup({
							type: 'image',
							closeOnContentClick: true,
							mainClass: 'mfp-img-mobile',
							image: {
								verticalFit: true
							}
						});

						initCatalogPopupMenu('#catalog_main_menu', '.popup-catalog-menu');
						initCatalogPopupSubmenu('.sections', '.sections a', '.subsections');
						initDropDownHeader();
						<xsl:if test="$has_quick_search">
												$("#q-ipt").keyup(function(){
							searchAjax(this);
						});
												</xsl:if>
					});


					function initDropDownHeader() {
						$('.dd_menu_item').click(function() {
							var mi = $(this);
							$('#dropdownMenuLink').html(mi.html() + '<i class="fas fa-caret-down"></i>');
							$('.dd_block').hide();
							$('#' + mi.attr('dd-id')).show();
						});
					}


					function searchAjax(el){
						var $el = $(el);
						<!-- console.log($el); -->
						var val = $el.val();
						if(val.length > 2){
							<xsl:text disable-output-escaping="yes">
								var $form = $("&lt;form&gt;",
							</xsl:text>
								{'method' : 'post', 'action' : '<xsl:value-of select="page/search_ajax_link"/>', 'id' : 'tmp-form'}
							);
							<xsl:text disable-output-escaping="yes">
								var $ipt2 = $("&lt;input&gt;",
							</xsl:text>
							 {'type' : 'text', 'value': val, 'name' : 'q'});

							 $ipt2.val(val);

							$form.append($ipt2);
							$('body').append($form);
							postForm('tmp-form', 'search-result');
							$('#tmp-form').remove();
							$('#search-result').show();
						}
					}

					$(document).on('click', 'body', function(e){
						var $trg = $(e.target);
						if($trg.closest('#search-result').length > 0 || $trg.is('#search-result') || $trg.is('input')) return;
						$('#search-result').hide();
						$('#search-result').html('');
					});

					function showMobileMainMenu() {
						$('.wrapper').toggleClass('visible-no');
						$('.menu-container').toggleClass('visible-yes');
					}

				</script>
				<xsl:call-template name="EXTRA_SCRIPTS"/>
				<xsl:for-each select="$body-end-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>


				<div class="popup" style="display: none;" id="modal_popup" > +++ </div>

			</body>
		</html>
	</xsl:template>




	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->






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


	<xsl:template name="PAGE_TITLE">
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


	<xsl:template name="MARKUP"/>


	<xsl:template match="seo | url_seo">
		<title>
			<xsl:value-of select="title"/>
		</title>
		<meta name="description" content="{description}"/>
		<meta name="keywords" content="{keywords}"/>
		<xsl:value-of select="meta" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template name="PRINT"/>
	<xsl:template name="ACTIONS_MOBILE"/>
</xsl:stylesheet>
