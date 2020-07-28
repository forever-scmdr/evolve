<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="utils/utils.xsl"/>
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



	<xsl:template match="custom_page" mode="menu_first">
		<xsl:variable name="key" select="@key"/>
		<xsl:if test="not(custom_page)">
			<div class="main-menu__item {'active'[$active_menu_item = $key]}">
				<a href="{show_page}" class="{'active'[$active_menu_item = $key]}">
					<xsl:value-of select="header"/>
				</a>
			</div>
		</xsl:if>
		<xsl:if test="custom_page or page_link">
			<div class="main-menu__item" style="position: relative;">
				<a href="#ts-{@id}" class="show-sub{' active'[$active_menu_item = $key]}">
					<span><xsl:value-of select="header"/></span>
				</a>
				<div id="ts-{@id}" class="popup-text-menu" style="position: absolute; z-index: 2; display: none;">
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
		<section class="top-stripe desktop">
			<div class="container">
				<xsl:value-of select="$common/top" disable-output-escaping="yes"/>
			</div>
		</section>
		<section class="header desktop">
			<div class="container">
				<a href="{$main_host}" class="logo"><img src="img/logo.png" alt="" /></a>
				<form action="{page/search_link}" method="post" class="header__search header__column">
					<input type="text" class="text-input header__field" placeholder="Поиск по каталогу" autocomplete="off" name="q" value="{page/variables/q}" autofocus="autofocus" id="q-ipt" />
					<input type="submit" class="button header__button" value="Поиск" />
					<xsl:if test="$has_quick_search"><div id="search-result"></div></xsl:if>
				</form>
				<xsl:if test="$has_currency_rates and $currencies">
					<div class="other-container">
						<div class="catalog-currency">
							<i class="far fa-money-bill-alt"/>&#160;<strong>Валюта</strong>&#160;
							<ul class="currency-options">
								<xsl:variable name="currency_link" select="page/set_currency"/>
								<li class="{'active'[$currency = 'BYN']}">
									<xsl:if test="not($currency = 'BYN')"><a href="{concat($currency_link, 'BYN')}">BYN</a></xsl:if>
									<xsl:if test="$currency = 'BYN'">BYN</xsl:if>
								</li>
								<xsl:for-each select="$currencies/*[ends-with(name(), '_rate')]">
									<xsl:variable name="cur" select="substring-before(name(), '_rate')"/>
									<xsl:variable name="active" select="$currency = $cur"/>
									<li class="{'active'[$active]}">
										<xsl:if test="not($active)"><a href="{concat($currency_link, $cur)}"><xsl:value-of select="$cur"/></a></xsl:if>
										<xsl:if test="$active"><xsl:value-of select="$cur"/></xsl:if>
									</li>
								</xsl:for-each>
							</ul>
						</div>
					</div>
				</xsl:if>
				<div class="cart-info header__column" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no">
					<a href=""><i class="fas fa-shopping-cart"></i>Корзина</a>
					<!-- <div>Товаров: <strong>2</strong></div>
					<div>Cумма: <strong>1250 руб.</strong></div> -->
				</div>
				<div class="user-links header__column">
					<xsl:call-template name="PERSONAL_DESKTOP"/>
					<div id="fav_ajax" ajax-href="{page/fav_ajax_link}">
						<a href=""><i class="fas fa-star"/>Избранное</a>
					</div>
					<div id="compare_ajax" ajax-href="{page/compare_ajax_link}">
						<a href="compare.html"><i class="fas fa-balance-scale"/>Сравнение</a>
					</div>
				</div>
				<div class="main-menu">
					<div class="main-menu__item main-menu__special" style="position: relative;">
						<a href="{page/catalog_link}" class="{'active'[$active_menu_item = 'catalog']}" id="catalog_main_menu"><span><i class="fas fa-bars"></i> Каталог</span></a>
						<div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
							<div class="sections">
								<xsl:for-each select="page/catalog/section">
									<xsl:if test="section">
										<a href="{show_products}" class="cat_menu_item_1" rel="#sub_{@id}">
											<xsl:value-of select="name" />
										</a>
									</xsl:if>
									<xsl:if test="not(section)">
										<a href="{show_products}" class="cat_menu_item_1">
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
					<xsl:for-each select="page/news">
						<xsl:variable name="key" select="@key"/>
						<xsl:variable name="sel" select="page/varibles/sel"/>
						<div class="main-menu__item">
							<a href="{show_page}" class="{'active'[$sel = $key]}">
								<span>
									<xsl:value-of select="name"/></span>
							</a>
						</div>
					</xsl:for-each>
					<xsl:apply-templates select="page/custom_pages/*[in_main_menu = 'да']" mode="menu_first"/>
					<div class="main-menu__item">
						<a href="{page/contacts_link}"><span>Контакты</span></a>
					</div>
				</div>
			</div>
		</section>

	</xsl:template>



	<xsl:template name="INC_MOBILE_HEADER">
		<div class="header mobile">
			<div class="header-container">
				<a href="{$main_host}" class="logo"> 
					<img src="img/logo.png" alt="На главную страницу" style="height: 1.5em; max-width: 100%;"/>
				</a>
				<div class="icons-container">
					<a href="{page/contacts_link}"><i class="fas fa-phone"></i></a>
					<a href="{page/cart_link}"><i class="fas fa-shopping-cart"></i></a>
					<a href="javascript:showMobileMainMenu()"><i class="fas fa-bars"></i></a>
				</div>
				<div class="search-container">
					<form action="{page/search_link}" method="post">
						<input type="text" placeholder="Введите поисковый запрос" autocomplete="off" name="q" value="{page/variables/q}"/>
                        <xsl:if test="$has_quick_search"><div id="search-result"></div></xsl:if>
					</form>
				</div>
			</div>
		</div>
		<script>
			function showMobileMainMenu() {
			$('.content-container').toggleClass('visible-no');
			$('.menu-container').toggleClass('visible-yes');
			}
		</script>
	</xsl:template>


	<xsl:template match="block" mode="footer">
		<div class="footer__column">
			<xsl:if test="header and not(header = '')"><div class="title_3"><xsl:value-of select="header" /></div></xsl:if>
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template name="INC_FOOTER">
		<!-- FOOTER BEGIN -->
		<div class="footer-placeholder"></div>
		<footer class="footer">
			<div class="container">
				<xsl:variable name="footer" select="page/common/footer"/>
				<div class="footer__column">
					<xsl:if test="$footer/block[1]/header and not($footer/block[1]/header = '')">
						<div class="title_3"><xsl:value-of select="$footer/block[1]/header" /></div>
					</xsl:if>
					<xsl:value-of select="$footer/block[1]/text" disable-output-escaping="yes"/>

				</div>
				<xsl:apply-templates select="$footer/block[position() &gt; 1]" mode="footer"/>
				<!-- <div class="footer__column">
					<p>Мы в социальных сетях</p>
					<div class="social">
						<a href="https://www.instagram.com/taktsminsk/"><i class="fab fa-instagram" style="color: #ffffff;" /></a>
						<a href="https://www.facebook.com/stihlminsk/"><i class="fab fa-facebook" style="color: #ffffff;" /></a>
					</div>
				</div> -->
			</div>
		</footer>
		<!-- FOOTER END -->

		<!-- MODALS BEGIN -->
		<!-- modal login -->
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-login">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
						<div class="modal-title h4">Вход</div>
					</div>
					<div class="modal-body">
						<form action="" method="post">
							<div class="form-group">
								<label for="">Электронная почта:</label>
								<input type="text" class="form-control" />
							</div>
							<div class="form-group">
								<label for="">Пароль:</label>
								<input type="password" class="form-control" />
							</div>
							<input type="submit" name="" value="Отправить заказ"/>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- modal feedback -->
		<xsl:call-template name="FEEDBACK_FORM"/>
		<!-- MODALS END -->
	</xsl:template>




	<xsl:template name="INC_MOBILE_MENU">
		<div class="menu-container mobile">
			<div class="overlay" onclick="showMobileMainMenu()"></div>
			<div class="content">
				<ul>
					<li>
						<xsl:call-template name="PERSONAL_MOBILE"/>
					</li>
				</ul>
				<ul>
					<li><i class="fas fa-th-list"></i> <a href="#" onclick="showMobileCatalogMenu(); return false">Каталог продукции</a></li>
				</ul>
				<ul>
					<li><i class="fas fa-shopping-cart"></i> <a href="{page/cart_link}" rel="nofolow">Заявки</a></li>
					<li><i class="fas fa-star"></i> <a href="{page/fav_link}">Избранное</a></li>
					<li><i class="fas fa-balance-scale"></i> <a href="{page/compare_link}">Сравнение</a></li>
				</ul>
				<ul>
					<xsl:for-each select="page/news">
						<li><a href="{show_page}">
							<xsl:value-of select="name"/>
						</a></li>
					</xsl:for-each>
					<xsl:for-each select="page/custom_pages/custom_page">
						<li><a href="{show_page}"><xsl:value-of select="header"/></a></li>
					</xsl:for-each>
					<li>
						<a href="{page/contacts_link}">Контакты</a>
					</li>
				</ul>
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
					<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
				</div>
				<ul>
					<xsl:for-each select="page/catalog/section">
						<li>
							<xsl:if test="section">
								<a rel="{concat('#m_sub_', @id)}">
									<xsl:value-of select="name"/>
								</a>
								<i class="fas fa-chevron-right"></i>
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



	<xsl:template name="INC_SIDE_MENU_INTERNAL_CATALOG">
		<div class="block-title block-title_normal">Каталог</div>
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<xsl:variable name="l1_active" select="@id = $sel_sec_id"/>
				<div class="level-1{' active'[$l1_active]}">
					<div class="capsule">
						<a href="{show_products}"><xsl:value-of select="name"/> </a>
					</div>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<xsl:for-each select="section">
						<xsl:variable name="l2_active" select="@id = $sel_sec_id"/>
						<div class="level-2{' active'[$l2_active]}"><a href="{show_products}"><xsl:value-of select="name"/></a></div>
						<xsl:if test=".//@id = $sel_sec_id">
							<xsl:for-each select="section">
								<xsl:variable name="l3_active" select="@id = $sel_sec_id"/>
								<div class="level-3{' active'[$l3_active]}"><a href="{show_products}"><xsl:value-of select="name"/></a></div>
								<xsl:if test=".//@id = $sel_sec_id">
									<xsl:for-each select="section">
										<xsl:variable name="l4_active" select="@id = $sel_sec_id"/>
										<div class="level-4{' active'[$l4_active]}"><a href="{show_products}"><xsl:value-of select="name"/></a></div>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>



	<xsl:template name="PRINT">
		<span><i class="fas fa-print"></i> <a href="javascript:window.print()">Распечатать</a></span>
	</xsl:template>




	<!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->




	<xsl:template name="COMMON_LEFT_COLOUMN">
		<!-- <div class="actions">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="{$common/link_link}"><xsl:value-of select="$common/link_text"/></a>
			</div>
		</div> -->
		<div class="contacts">
			<div class="block-title block-title_normal">Заказ и консультация</div>
			<xsl:value-of select="$common/left" disable-output-escaping="yes"/>
			<!-- <strong>Принимаем к оплате</strong>
			<div class="pay-cards">
				<div class="pay-cards__item">
					<img src="img/card1.jpg" />
				</div>
				<div class="pay-cards__item">
					<img src="img/card2.jpg" />
				</div>
				<div class="pay-cards__item">
					<img src="img/card3.jpg" />
				</div>
				<div class="pay-cards__item">
					<img src="img/card4.jpg" />
				</div>
				<div class="pay-cards__item">
					<img src="img/card5.jpg" />
				</div>
				<div class="pay-cards__item">
					<img src="img/card6.jpg" />
				</div>
			</div> -->
		</div>
	</xsl:template>



	<xsl:template name="CATALOG_LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>



	<xsl:template name="ACTIONS_MOBILE">
		<div class="actions mobile" style="display:none;">
			<div class="h3">Акции</div>
			<div class="actions-container">
				<a href="{$common/link_link}"><xsl:value-of select="$common/link_text"/></a>
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




	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


	<xsl:template name="MAIN_CONTENT">
		<!-- MAIN COLOUMNS BEGIN -->
		<div class="container columns">
			<!-- LEFT COLOUMN BEGIN -->
			<div class="column-left desktop">
				<xsl:call-template name="LEFT_COLOUMN"/>
			</div>
			<!-- LEFT COLOUMN END -->
			
			<!-- RIGHT COLOUMN BEGIN -->
			<div class="column-right main-content">
				<div class="mc-container">
					<xsl:call-template name="INC_MOBILE_HEADER"/>
					<xsl:call-template name="CONTENT"/>
					<xsl:if test="$seo/text != '' and page/@name != 'section' and page/@name != 'sub'">
						<div class="page-content">
							<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
						</div>
					</xsl:if>
				</div>
			</div>
			<!-- RIGHT COLOUMN END -->
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>


	<xsl:template name="LEFT_COLOUMN">
		<!-- <div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<div class="level-1">
					<div class="capsule">
						<a href="{show_products}"><xsl:value-of select="name"/></a>
					</div>
				</div>
			</xsl:for-each>
		</div> -->
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="CONTENT"/>
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
				<!--<base href="https://ttd.by"/> -->
				<base href="{$main_host}"/>
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>

				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>

				<xsl:call-template name="SEO"/>
				<link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,700,900&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/app.css"/>
				<link rel="stylesheet" href="css/styles.css"/>
				<link rel="stylesheet" href="css/styles-2.css"/>
				<link rel="stylesheet" type="text/css" href="css/tmp_fix.css"/>
				<link rel="stylesheet" type="text/css" href="slick/slick.css"/>
				<link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
				<link rel="stylesheet" href="fotorama/fotorama.css"/>
				<link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"/>
				<link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css"/>
				<link  href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"/>
				<link rel="stylesheet" href="css/styles-mtb.css"/>
				<script defer="defer" src="js/font_awesome_all.js"/>
				<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
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
				<div class="popup" style="display: none;" id="product-ajax-popup">
					<div class="popup__body">
						<div class="popup__content" id="product-ajax-content">
							<a class="popup__close" onclick="clearProductAjax();">×</a>
						</div>
					</div>
				</div>
				<!-- ALL CONTENT BEGIN -->
				<div class="content-container">
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
				<script type="text/javascript" src="js/bootstrap.js"/>
				<script type="text/javascript" src="admin/ajax/ajax.js"/>
				<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
				<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
				<script type="text/javascript" src="js/web.js"/>
				<script type="text/javascript" src="slick/slick.min.js"></script>
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
					var oh = $(".footer").outerHeight();
					$(".footer-placeholder").height(oh+40);
					$(".footer").css("margin-top", -1*oh);
					$('.slick-slider').slick({
					infinite: true,
					slidesToShow: 5,
					slidesToScroll: 5,
					dots: true,
					arrows: false,
					responsive: [
						{
							breakpoint: 1440,
							settings: {
								slidesToShow: 5,
								slidesToScroll: 5,
								infinite: true,
								dots: true
							}
						},
						{
							breakpoint: 1200,
							settings: {
								slidesToShow: 4,
								slidesToScroll: 4,
								infinite: true,
								dots: true
							}
						},
						{
							breakpoint: 992,
							settings: {
								slidesToShow: 3,
								slidesToScroll: 3,
								infinite: true,
								dots: true
							}
						},
						{
							breakpoint: 768,
							settings: {
								slidesToShow: 2,
								slidesToScroll: 2,
								infinite: true,
								dots: true
							}
						},
						{
							breakpoint: 375,
							settings: {
								slidesToShow: 1,
								slidesToScroll: 1,
								infinite: true,
								dots: true
							}
						}
					]
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

					$(window).resize(function(){
						var oh = $(".footer").outerHeight();
						$(".footer-placeholder").height(oh+40);
						$(".footer").css("margin-top", -1*oh);
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

				</script>
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
		<xsl:apply-templates select="page_text | common_gallery | simple_gallery | custom_block" mode="content"/>
	</xsl:template>

	<xsl:template match="page_text" mode="content">
		<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="common_gallery" mode="content">
		<div id="nanogallery{@id}">
			<script>
				$(document).ready(function () {

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
				});
			</script>
		</div>
	</xsl:template>


	<xsl:template match="simple_gallery" mode="content">
		<div id="nanogallery{@id}">
			<script>
				$(document).ready(function () {

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
				});
			</script>
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
</xsl:stylesheet>
