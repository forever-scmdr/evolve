<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<!--<xsl:import href="feedback_ajax.xsl"/>-->
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>


	<xsl:variable name="common" select="page/common"/>
	<xsl:variable name="registration" select="page/registration[1]"/>
	<xsl:variable name="is_reg_jur" select="$registration/@type = 'user_jur'"/>
	<xsl:variable name="debt" select="if ($registration/debt and not($registration/debt = '') and not(normalize-space($registration/debt) = '0')) then $registration/debt else false() "/>
	<xsl:variable name="discount" select="if ($is_reg_jur and $registration/discount and not($registration/discount = '')) then f:num($registration/discount) else 0"/>


	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="''" />
	<xsl:variable name="meta_description" select="''" />
	<xsl:variable name="base" select="page/base" />
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />

	<xsl:variable name="default_canonical" select="if(page/@name != 'index') then concat('/', tokenize(page/source_link, '\?')[1]) else ''" />
	<xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

	<xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>





	<!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

	<xsl:variable name="source_link" select="/page/source_link"/>
	<xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

	<xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
	<xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
	<xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
	<xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>




	<!-- ****************************    ГЛАВНОЕ МЕНЮ    ******************************** -->

	<xsl:variable name="active_menu_item" select="'index'"/>

	<xsl:template match="page_link" mode="menu">
		<xsl:variable name="page" select="tokenize(link, '/')"/>
		<a href="{link}" class="menu__item{' active'[$page = $active_menu_item]}"><xsl:value-of select="name" /></a>
	</xsl:template>

	<xsl:template match="menu_custom" mode="menu">
		<a href="{show_page}" class="menu__item{' active'[current()/@key = $active_menu_item]}"><xsl:value-of select="header" /></a>
	</xsl:template>

	<xsl:template name="MAIN_MENU">
		<a href="/catalog" class="menu__item">Каталог</a>
		<xsl:for-each select="page/news">
			<a href="{show_page}" class="menu__item{' active'[current()/@key = $active_menu_item]}">
				<xsl:value-of select="name"/>
			</a>
		</xsl:for-each>
		<xsl:apply-templates select="page/custom_pages/page_link | page/custom_pages/menu_custom" mode="menu"/>
		<a href="{page/contacts_link}" class="menu__item{' active'['contacts' = $active_menu_item]}">
			Контакты
		</a>
	</xsl:template>



	<!-- ****************************    ВЕРХНЯЯ ЧАСТЬ    ******************************** -->



	<xsl:template name="INC_DESKTOP_HEADER">
		<section class="header desktop">
			<div class="container">
				<a href="{$main_host}" class="logo"><img src="img/logo.png" alt="" /></a>
				<form action="{page/search_link}" method="post" class="header__search header__column">
					<input type="text" class="text-input header__field" placeholder="Поиск по каталогу" autocomplete="off" name="q" value="{page/variables/q}" autofocus="autofocus" id="q-ipt" />
					<input type="submit" class="button header__button" value="Поиск" />
					<div id="search-result"></div>
				</form>
				<div class="phones">
					<xsl:value-of select="$common/top" disable-output-escaping="yes"/>
				</div>
				<div class="cart-info header__column" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no"></div>
				<!-- <div class="main-menu">
					<xsl:for-each select="page/catalog/section">
						<div class="main-menu__item"><a href="{show_products}"><span><xsl:value-of select="name" /></span></a></div>
					</xsl:for-each>
				</div> -->
			</div>
		</section>
		<section class="menu desktop">
			<div class="container">
				<xsl:call-template name="MAIN_MENU"/>
				<div class="auth">
					<xsl:call-template name="PERSONAL_DESKTOP"/>
					<!-- login form -->
					<xsl:call-template name="LOGIN_FORM"/>
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
						<div id="search-result"></div>
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



	<xsl:template name="INC_FOOTER">
		<!-- FOOTER BEGIN -->
		<div class="footer-placeholder"></div>
		<footer class="footer">
			<div class="container">
				<xsl:variable name="footer" select="page/common/footer"/>
				<div class="footer__column">
					<div class="title_3">ЧТУП «Фрезерпром», 2020</div>
					<div class="forever">
						<img src="img/forever.png" alt="" />
						<a href="http://forever.by" target="_blank">Разработка сайта <br/>студия веб-дизайна Forever</a>
					</div>
				</div>
				<div class="footer__column">
					<xsl:if test="$footer/block[1]/header and not($footer/block[1]/header = '')">
						<div class="title_3"><xsl:value-of select="$footer/block[1]/header" /></div>
					</xsl:if>
					<xsl:value-of select="$footer/block[1]/text" disable-output-escaping="yes"/>
				</div>
				<xsl:apply-templates select="$footer/block[position() &gt; 1]" mode="footer"/>
			</div>
		</footer>
		<!-- FOOTER END -->

		<!-- MODALS BEGIN -->

		<!-- modal feedback -->
		<!-- <xsl:call-template name="FEEDBACK_FORM"/>-->
		<!-- MODALS END -->
	</xsl:template>


	<xsl:template match="block" mode="footer">
		<div class="footer__column">
			<xsl:if test="header and not(header = '')"><div class="title_3"><xsl:value-of select="header" /></div></xsl:if>
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
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
					<li><i class="fas fa-shopping-cart"></i> <a href="{page/cart_link}" rel="nofolow">Корзина</a></li>
					<!-- <li><i class="fas fa-star"></i> <a href="{page/fav_link}">Избранное</a></li> -->
					<!-- <li><i class="fas fa-balance-scale"></i> <a href="{page/compare_link}">Сравнение</a></li> -->
				</ul>
				<ul>
					<xsl:for-each select="page/news">
						<li><a href="{show_page}">
							<xsl:value-of select="name"/>
						</a></li>
					</xsl:for-each>
					<xsl:for-each select="page/custom_pages/menu_custom">
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

			function hideMobileCatalogMenu() {
				$("#mobile_catalog_menu .content").css('left', '100%');
				$("#m_sub_cat").css('left', '0%');
				$('#mobile_catalog_menu').hide();
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

				initCatalogPopupMenu('#login_click', '.login-popup', 'mouseenter');
			});

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
		<div class="contacts">
			<div class="block-title block-title_normal">Заказ и консультация</div>
			<xsl:value-of select="$common/left" disable-output-escaping="yes"/>
		</div>
	</xsl:template>



	<xsl:template name="CATALOG_LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL"/>
		<!-- <xsl:call-template name="COMMON_LEFT_COLOUMN"/> -->
	</xsl:template>


	<xsl:template name="ACTIONS_MOBILE">
		<div class="actions mobile" style="display:none;">
			<div class="h3">Акции</div>
			<div class="actions-container">
				<a href="{$common/link_link}"><xsl:value-of select="$common/link_text"/></a>
			</div>
		</div>
	</xsl:template>


	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>

	<xsl:template match="accessory | set | probe | product | assoc | analog | support | similar">

		<xsl:variable name="pic_ref" select="pic_ref"/>

		<xsl:variable name="has_price" select="if ($is_reg_jur) then (price_opt and price_opt != '0') else (price and price != '0')"/>
		<xsl:variable name="price" select="if ($is_reg_jur and $has_price) then f:number_decimal(f:num(price_opt) div 100 * (100 - $discount)) else price"/>
		<xsl:variable name="price_old" select="if ($is_reg_jur) then price_opt_old else price_old"/>

		<xsl:variable name="discount_percent" select="f:discount($price, $price_old)"/>
		<xsl:variable name="qty" select="if ($is_reg_jur) then qty_opt else qty"/>
		<xsl:variable name="available_qty" select="if ($qty and f:num($qty) &gt; 0) then f:num($qty) else 0"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device items-catalog__device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<xsl:if test="not($pic_ref)">
				<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
					<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
						<i class="fas fa-search-plus"></i>
					</a>
				</xsl:if>
				<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_path,');')}"></a>
			</xsl:if>
			<xsl:if test="$pic_ref != ''">
				<xsl:if test="$pic_ref[starts-with(.,'device_pics/small_')]">
					<a href="/{$pic_ref[1]}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
						<i class="fas fa-search-plus"></i>
					</a>
					<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_ref[2],');')}"></a>
				</xsl:if>
				<xsl:if test="not($pic_ref[starts-with(.,'device_pics/small_')])">
					<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_ref[1],');')}"></a>
				</xsl:if>
			</xsl:if>
			<a href="{show_product}" class="device__title" title="{name}"><xsl:value-of select="name"/></a>
			<div class="device__article-number">
				Артикул: <xsl:value-of select="vendor_code"/>
				<!-- UPDATE 10.06/2019 discount label -->
				<xsl:if test="$discount_percent != ''">
					&#160;<span class="discount" style="color: red;">-<xsl:value-of select="$discount_percent" />%</span>
				</xsl:if>
				<!-- END_UPDATE 10.06/2019 discount label -->
			</div>
			<xsl:if test="$has_price">
				<div class="device__price">
					<xsl:if test="$price_old"><div class="price_old"><span><xsl:value-of select="$price_old"/> руб.</span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="$price"/> руб.</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price">

				</div>
			</xsl:if>
			<div class="device__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:variable name="max" select="if ($available_qty &gt; 0) then $available_qty else 1000000"/>
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
			</div>
			<xsl:choose>
				<xsl:when test="$available_qty &gt; 10">
					<div class="device__in-stock"><i class="fas fa-signal"></i> есть на складе</div>
				</xsl:when>
				<xsl:when test="$available_qty &gt; 0">
					<div class="device__in-stock device__in-stock_maybe"><i class="fas fa-signal"></i><xsl:text>на складе: </xsl:text><xsl:value-of select="$qty"/> шт.</div>
				</xsl:when>
				<xsl:otherwise>
					<!-- <div class="device__in-stock device__in-stock_no" ><i class="fas fa-truck"></i>Поставка: <xsl:value-of select="substring(//catalog/ship_date, 1,10)"/></div> -->
					<div class="device__in-stock device__in-stock_no" ><i class="fas fa-truck"></i>Наличие - уточняте у менеджера</div>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="extra_xml and extra_xml != ''">
				<xsl:variable name="parsed" select="parse-xml(extra_xml)"/>
				<div class="device__in-stock"><i class="fas fa-signal"></i>склад 1: <xsl:value-of select="normalize-space($parsed/offer/sklad1)" />; склад 2: <xsl:value-of select="normalize-space($parsed/offer/sklad2)" /></div>
			</xsl:if>


			<xsl:for-each select="tag">
				<div class="device__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
		</div>
	</xsl:template>



	<xsl:template match="accessory | set | probe | product | assoc | analog | support | similar" mode="lines">
		<xsl:variable name="has_price" select="if ($is_reg_jur) then (price_opt and price_opt != '0') else (price and price != '0')"/>
		<xsl:variable name="price" select="if ($is_reg_jur and $has_price) then f:number_decimal(f:num(price_opt) div 100 * (100 - $discount)) else price"/>
		<xsl:variable name="price_old" select="if ($is_reg_jur) then price_opt_old else price_old"/>

		<xsl:variable name="discount_percent" select="f:discount($price, $price_old)"/>
        <xsl:variable name="qty" select="if ($is_reg_jur) then qty_opt else qty"/>
		<xsl:variable name="available_qty" select="if ($qty and f:num($qty) &gt; 0) then f:num($qty) else 0"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device device_row">
			<!-- <div class="tags"><span>Акция</span></div> -->
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<xsl:variable name="pic_ref" select="pic_ref"/>

			<xsl:if test="not($pic_ref)">

			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}">&#160;</a>
			</xsl:if>

			<xsl:if test="$pic_ref != ''">
				<xsl:if test="$pic_ref[starts-with(.,'device_pics/small_')]">
					<a href="/{$pic_ref[1]}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
						<i class="fas fa-search-plus"></i>
					</a>
					<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(/',$pic_ref[2],');')}"></a>
				</xsl:if>
				<xsl:if test="not($pic_ref[starts-with(.,'device_pics/small_')])">
					<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_ref[1],');')}"></a>
				</xsl:if>
			</xsl:if>

			<div class="device__info">
				<a href="{show_product}" class="device__title"><xsl:value-of select="name"/></a>
				<div class="device__description">
					<p><xsl:value-of select="short" disable-output-escaping="yes"/></p>
				</div>
			</div>
			<div class="device__article-number">Артикул: <xsl:value-of select="vendor_code"/></div>
			<xsl:if test="$has_price">
				<div class="device__price device_row__price">
					<xsl:if test="$price_old"><div class="price_old"><span><xsl:value-of select="$price_old"/> руб.</span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="$price"/> руб.</div>

<!-- UPDATE 10.06/2019 discount label -->
					<xsl:if test="$discount_percent != ''">
						<span class="discount" style="color: #ED1C24; font-size: 12px;">Скидка: <xsl:value-of select="$discount_percent" />%</span>
					</xsl:if>
<!-- END_UPDATE 10.06/2019 discount label -->

				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price device_row__price">

				</div>
			</xsl:if>
			<div class="device__order device_row__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:variable name="max" select="if ($available_qty &gt; 0) then $available_qty else 1000000"/>
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$available_qty &gt; 10">
						<div class="device__in-stock device_row__in-stock"><i class="fas fa-signal"></i> есть на складе</div>
					</xsl:when>
					<xsl:when test="$available_qty &gt; 0">
						<div class="device__in-stock device_row__in-stock device__in-stock_maybe"><i class="fas fa-signal"></i><xsl:text>на складе: </xsl:text><xsl:value-of select="$qty"/> шт.</div>
					</xsl:when>
					<xsl:otherwise>
						<!-- <div class="device__in-stock device_row__in-stock device__in-stock_no"><i class="fas fa-truck"></i>Поставка: <xsl:value-of select="substring(//catalog/ship_date, 1,10)"/></div> -->
						<div class="device__in-stock device_row__in-stock device__in-stock_no"><i class="fas fa-truck"></i>Наличие - уточняте у менеджера</div>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="extra_xml and extra_xml != ''">
					<xsl:variable name="parsed" select="parse-xml(extra_xml)"/>
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-signal"></i>склад 1: <xsl:value-of select="normalize-space($parsed/offer/sklad1)" />; склад 2: <xsl:value-of select="normalize-space($parsed/offer/sklad2)" /></div>
				</xsl:if>
			</div>
			<xsl:for-each select="tag">
				<div class="device__tag device_row__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
		</div>
	</xsl:template>


	<xsl:template match="accessory | set | probe | product | assoc | analog | support | similar" mode="special">
		<xsl:variable name="has_price" select="if ($is_reg_jur) then (price_opt and price_opt != '0') else (price and price != '0')"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="price" select="if ($is_reg_jur and $has_price) then f:number_decimal(f:num(price_opt) div 100 * (100 - $discount)) else price"/>
		<xsl:variable name="price_old" select="if ($is_reg_jur) then price_opt_old else price_old"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device_special">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
			<!-- <xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if> -->
			<div>
				<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}">&#160;</a>
				<div style="flex: 1; margin-left: 8px;">
					<div class="device__info">
						<a href="{show_product}" class="device__title"><xsl:value-of select="name"/></a>
						<div class="device__description">
							<p><xsl:value-of select="short" disable-output-escaping="yes"/></p>
						</div>
					</div>
					<xsl:if test="$has_price">
						<div class="device__price">
							<xsl:if test="$price_old"><div class="price_old"><span><xsl:value-of select="$price_old"/> руб.</span></div></xsl:if>
							<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="$price"/> руб.</div>
						</div>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<div class="device__pric">

						</div>
					</xsl:if>
				</div>
			</div>
			<xsl:for-each select="tag">
				<div class="device__tag device_row__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
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
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="CONTENT"/>
	<xsl:template name="BANNERS"/>
	<xsl:template name="EXTRA_SCRIPTS"/>






	<!-- ****************************    СТРАНИЦА    ******************************** -->
	<xsl:template name="HERO" />

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
				<link rel="stylesheet" href="css/app.css?version=0.4"/>
				<link rel="stylesheet" type="text/css" href="css/tmp_fix.css"/>
				<xsl:if test="page/@name = 'index'">
					<link rel="stylesheet" type="text/css" href="slick/slick.css"/>
					<link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
				</xsl:if>
				<link rel="stylesheet" href="fotorama/fotorama.css"/>
				<link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"/>
				<script defer="defer" src="js/font_awesome_all.js"/>
				<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
				<script type="text/javascript" src="js/fwk/common.js"/>
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
				<div class="content-container">
					<xsl:call-template name="HERO" />
					<xsl:call-template name="INC_DESKTOP_HEADER"/>

					<xsl:call-template name="MAIN_CONTENT"/>

					<xsl:call-template name="BANNERS"/>

					<xsl:call-template name="INC_FOOTER"/>

				</div>
				<!-- ALL CONTENT END -->


				<xsl:call-template name="INC_MOBILE_MENU"/>
				<xsl:call-template name="INC_MOBILE_NAVIGATION"/>
				<script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script>
				<script type="text/javascript" src="js/bootstrap.js"/>
				<script type="text/javascript" src="admin/ajax/ajax.js"/>
				<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
				<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
				<xsl:if test="page/@name = 'index'">
					<script type="text/javascript" src="slick/slick.min.js"></script>
				</xsl:if>
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
						$(".footer-placeholder").height(oh+110);
						$(".footer").css("margin-top", -1*oh);

						<xsl:if test="page/@name = 'index'">
						$('.slick-slider').slick({
							infinite: true,
							slidesToShow: 4,
							slidesToScroll: 4,
							dots: true,
							arrows: false,
							responsive: [
								{
									breakpoint: 1440,
									settings: {
										slidesToShow: 4,
										slidesToScroll: 4,
										infinite: true,
										dots: true
									}
								},
								{
									breakpoint: 1200,
									settings: {
										slidesToShow: 3,
										slidesToScroll: 3,
										infinite: true,
										dots: true
									}
								},
								{
									breakpoint: 992,
									settings: {
										slidesToShow: 2,
										slidesToScroll: 2,
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
									breakpoint: 426,
									settings: {
										slidesToShow: 1,
										slidesToScroll: 1,
										infinite: true,
										dots: true
									}
								}
							]
						});
						</xsl:if>
						//initCatalogPopupMenu('#catalog_main_menu', '.popup-catalog-menu');
						//initCatalogPopupSubmenu('.sections', '.sections a', '.subsections');
						initDropDownHeader();
						$("#q-ipt").keyup(function(){
							searchAjax(this);
						});
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
		<xsl:apply-templates select="text_part | gallery_part" mode="content"/>
	</xsl:template>

	<xsl:template match="text_part" mode="content">
		<h3><xsl:value-of select="name"/></h3>
		<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="gallery_part" mode="content">
		<div class="fotorama" data-fit="cover">
			<xsl:for-each select="picture_pair">
				<img src="{@path}{big}" alt="{name}" data-caption="{name}"/>
			</xsl:for-each>
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
