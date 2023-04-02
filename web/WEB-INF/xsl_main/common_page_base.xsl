<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="my_price_ajax.xsl"/>
	<xsl:import href="one_click_ajax.xsl"/>
	<xsl:import href="subscribe_ajax.xsl"/>
	<xsl:import href="snippets/product.xsl"/>
	<xsl:import href="snippets/page_extra.xsl"/>

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
	<xsl:variable name="query" select="page/variables/q"/>
	<xsl:variable name="hide_side_menu" select="f:num(/page/custom_page/hide_side_menu) = 1"/>

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
				<!-- static -->
				<xsl:variable name="has_city" select="$common/topper/block[header = $city]"/>
				<xsl:variable name="has_many_cities" select="count($common/topper/block) &gt; 1"/>
				<xsl:for-each select="$common/topper/block">
					<xsl:variable name="active" select="($has_city and header = $city) or (not($has_city) and position() = 1)"/>
					<div class="top-info__wrap wrap" id="{@id}" style="display: {'flex'[$active]}{'none'[not($active)]}">
						<div class="top-info__location">
							<a href="#" class="link icon-link icon-link_after" onclick="{if ($has_many_cities) then 'return showCityHeaderSelector()' else ''}">
								<span><xsl:value-of select="header"/></span>
								<xsl:if test="$has_many_cities">
									<div class="icon icon_size_sm">
										<img src="img/icon-caret-down.svg" alt="" />
									</div>
								</xsl:if>
							</a>
						</div>
						<div class="top-info__content">
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each>
				<!-- static end -->
				<ul class="location-list" style="display:none">
					<xsl:for-each select="$common/topper/block">
						<li><a href="#" onclick="return showCityHeader('{@id}', '{header}')"><xsl:value-of select="header"/></a></li>
					</xsl:for-each>
				</ul>
				<script>
					function showCityHeaderSelector() {
						$('.location-list').show();
						return false;
					}
					function showCityHeader(cityId, cityName) {
						$('.top-info__wrap').hide();
						$('.location-list').hide();
						$('#' + cityId).show('fade', 200);
						insertAjax('set_city?city=' + cityName);
						return false;
					}
					<xsl:if test="$has_quick_search">
					$(document).ready(function() {
						initQuickSearch();
					});
					</xsl:if>
				</script>
			</div>
		</div>

		<div class="header">
			<div class="container">
				<div class="header__wrap wrap">
					<a href="{$main_host}" class="header__column logo">
						<img src="img/logo.png" alt="" class="logo__image" />
					</a>
					<div class="header__column header__search header-search">
						<form action="{page/search_link}" method="post">
							<input class="input header-search__input"
								   ajax-href="{page/search_ajax_link}" result="search-result"
								   query="q" min-size="3" id="q-ipt" type="text"
								   placeholder="Введите поисковый запрос" autocomplete="off"
								   name="q" value="{$query}" autofocus=""/>
							<button class="button header-search__button" type="submit">Найти</button>
							<!-- quick search -->
							<xsl:if test="$has_quick_search"><div id="search-result" style="display:none"></div></xsl:if>
							<!-- quick search end -->
						</form>
					</div>
					<!-- need styles -->
					<xsl:if test="$has_currency_rates and $currencies">
						<div class="header__column other-container side-menu">
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
					<!-- need styles end -->
					<div class="header__column header__column_links">
						<div class="cart" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no">
							<a href="{page/cart_link}" class="icon-link">
								<div class="icon"><img src="img/icon-cart.svg" alt="" /></div>
								<span class="icon-link__item">Загрузка...</span>
							</a>
						</div>
						<div class="links">
							<a href="/kontakty" class="icon-link">
								<div class="icon">
									<img src="img/icon-phone.svg" alt="" />
								</div>
							</a>
							<a href="javascript:showMobileMainMenu()" class="icon-link">
								<div class="icon">
									<img src="img/icon-bars.svg" alt="" />
								</div>
							</a>
						</div>
						<div class="user">
							<div id="personal_desktop" ajax-href="{page/personal_ajax_link}" ajax-show-loader="no">
								<a href="{page/login_link}" class="icon-link">
									<div class="icon">
										<img src="img/icon-lock.svg" alt="" />
									</div>
									<span class="icon-link__item">Вход / Регистрация</span>
								</a>
							</div>
							<div id="fav_ajax" ajax-href="{page/fav_ajax_link}" ajax-show-loader="no">
								<a class="icon-link">
									<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
									<span class="icon-link__item">Избранное</span>
								</a>
							</div>
							<div id="compare_ajax" ajax-href="{page/compare_ajax_link}" ajax-show-loader="no">
								<a class="icon-link">
									<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
									<span class="icon-link__item">Сравнение</span>
								</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="main-menu">
			<div class="container">
				<div class="main-menu__wrap wrap">
					<div class="main-menu__item">
						<a href="{page/catalog_link}" class="icon-link {'active'[$active_menu_item = 'catalog']}" id="catalog_main_menu"><div class="icon"><img src="img/icon-bars.svg" alt="" /></div><span>Каталог</span></a>
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
						<a href="http://forever.by" class="forever">
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




	<xsl:template name="INC_MOBILE_MENU">
		<div class="menu-container mobile">
			<div class="menu-overlay" onclick="showMobileMainMenu()"></div>
			<div class="menu-content">
				<ul>
					<li>
						<a href="#" class="icon-link">
							<div class="icon">
								<img src="img/icon-lock.svg" alt="" />
							</div>
							<span class="icon-link__item">Вход / регистрация</span>
						</a>
					</li>
				</ul>
				<ul>
					<li>
						<a href="#" onclick="showMobileCatalogMenu(); return false" class="icon-link">
							<div class="icon">
								<img src="img/icon-cart.svg" alt="" />
							</div>
							<span class="icon-link__item">Каталог продукции</span>
						</a>
					</li>
				</ul>
				<ul>
					<li>
						<a href="{page/cart_link}" class="icon-link">
							<div class="icon">
								<img src="img/icon-cart.svg" alt="" />
							</div>
							<span class="icon-link__item">Корзина</span>
						</a>
					</li>
					<li>
						<a href="{page/fav_link}" class="icon-link">
							<div class="icon">
								<img src="img/icon-star.svg" alt="" />
							</div>
							<span class="icon-link__item">Избранное</span>
						</a>
					</li>
					<li>
						<a href="{page/compare_link}" class="icon-link">
							<div class="icon">
								<img src="img/icon-balance.svg" alt="" />
							</div>
							<span class="icon-link__item">Сравнение</span>
						</a>
					</li>
				</ul>
				<ul>
					<xsl:for-each select="page/news[in_main_menu = 'да']">
						<li><a href="{show_page}">
							<xsl:value-of select="name"/>
						</a></li>
					</xsl:for-each>
					<xsl:for-each select="page/custom_pages/*[in_main_menu = 'да']">
						<xsl:if test="show_page != ''">
							<li><a href="{show_page}"><xsl:value-of select="header"/></a></li>
						</xsl:if>
						<xsl:if test="link != ''">
							<li><a href="{link}"><xsl:value-of select="name"/></a></li>
						</xsl:if>
					</xsl:for-each>
				</ul>
				<xsl:if test="$has_currency_rates and $currencies">
					<ul>
						<li class="catalog-currency">
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
						</li>
					</ul>
				</xsl:if>
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
<!--								<xsl:if test=".//@id = $sel_sec_id">-->
<!--									<xsl:for-each select="section">-->
<!--										<xsl:variable name="l4_active" select="@id = $sel_sec_id"/>-->
<!--										<div class="side-menu__item side-menu__item_level_4">-->
<!--											<a href="{show_products}" class="side-menu__link{' side-menu__link_active'[$l4_active]}"><xsl:value-of select="name"/></a>-->
<!--										</div>-->
<!--									</xsl:for-each>-->
<!--								</xsl:if>-->
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
					<xsl:if test="not($hide_side_menu)">
						<div class="content__side">
							<xsl:call-template name="LEFT_COLOUMN"/>
						</div>
					</xsl:if>
					<div class="content__main{' no-left-col'[$hide_side_menu]}">
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

				<script defer="defer" src="js/font_awesome_all.js"></script>
				<script src="js/jquery-3.5.1.min.js"></script>
				<script src="js/fotorama.js"></script>
				<script src="js/slick.min.js"></script>
				<script src="js/script.js"></script>

				<xsl:if test="//map_part">
					<script type="text/javascript" src="https://api-maps.yandex.ru/2.1/?lang=ru_RU&amp;apikey=da0c54ef-c061-454c-9069-536643a0d28a"></script>
				</xsl:if>

				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>

				<xsl:call-template name="SEO"/>
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/styles.css?version=1.66"/>
				<link rel="stylesheet" href="css/fixes.css?version=1.0"/>
				<link  href="css/fotorama.css" rel="stylesheet" />
				<link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css"/>
				<link  href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"/>
				<link rel="stylesheet" type="text/css" href="css/unia.css"/>

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
				<div class="mitaba">
					<div class="popup" style="display: none;" id="product-ajax-popup">
						 <div class="popup__body">
							<div class="popup__content" id="product-ajax-content"><a class="popup__close" onclick="clearProductAjax();">×</a></div>
						 </div>
					</div>
				</div>
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
				<!-- cheaper -->
	            <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="cheaper" show-loader="yes">
					<div class="modal-dialog" role="document">
					      <div class="modal-content">
					         <div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button><div class="modal-title h4">Нашли этот же инструмент дешевле? Мы сделаем скидку!</div>
					         </div>
					         <div class="modal-body">
					         </div>
					      </div>
					   </div>
	            </div>
				<!-- cheaper END -->
				<script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script>
				<!-- <script type="text/javascript" src="js/bootstrap.js"/> -->
				<script type="text/javascript" src="admin/ajax/ajax.js"/>
				<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
				<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
				<script type="text/javascript" src="js/bootstrap.min.js"/>
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
						/*
						<xsl:if test="$has_quick_search">
                        $("#q-ipt").keyup(function(){
							searchAjax(this);
						});
                        </xsl:if>
						*/
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
			<meta name="yandex-verification" content="{$common/yandex_verification}"/>
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
