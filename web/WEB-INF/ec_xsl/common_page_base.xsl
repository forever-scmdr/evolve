<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="cheaper_form.xsl"/>
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="warranty_form.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="one_click_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>


	<xsl:variable name="common" select="page/common"/>


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

	<!-- discount cookies -->
	<xsl:variable name="discount_time" select="page/variables/discount_active = 'yes'" />
	<xsl:variable name="discount" select="1 - f:num(page/common/discount)" />
	<!-- -->

	<xsl:variable name="active_menu_item"/>


	<!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

	<xsl:variable name="source_link" select="/page/source_link"/>
	<xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

	<xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
	<xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
	<xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
	<xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>


	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->



	<xsl:template name="INC_DESKTOP_HEADER">
		<section class="top-stripe desktop">
			<div class="container">
				<xsl:variable name="topper" select="page/common/topper"/>
				<div class="dropdown">
					<a class="dropdown-toggle top-stripe__location" href="#" rel="nofollow" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<xsl:value-of select="$topper/block[1]/header"/>&#160;<i class="fas fa-caret-down"></i>
					</a>

					<div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
						<xsl:for-each select="$topper/block">
							<a class="dropdown-item dd_menu_item" href="#" rel="nofollow" dd-id="dd_block_{@id}"><xsl:value-of select="header"/></a>
						</xsl:for-each>
					</div>
				</div>
				<xsl:for-each select="$topper/block">
					<xsl:variable name="not_first" select="position() != 1"/>
					<div class="dd_block" id="dd_block_{@id}" style="{'display: none'[$not_first]}">
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</xsl:for-each>
			</div>
		</section>
		<section class="header desktop">
			<div class="container">
				<a href="{$main_host}" class="logo"><img src="img/logo.svg" alt="" /></a>
				<div class="header__content">
					<div class="header__columns">
						<form action="{page/search_link}" method="post" class="header__search header__column">
							<input type="text" class="text-input header__field" name="q" value="{page/variables/q}" id="q-ipt" />
							<input type="submit" class="button header__button" value="Найти" />
							<div id="search-result"></div>
						</form>
						<div class="cart-info header__column" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no">
							<a href="#" rel="nofollow"><i class="fas fa-shopping-cart"></i>Корзина</a>
						</div>
						<div class="user-links header__column">
							<xsl:call-template name="PERSONAL_DESKTOP"/>
							<div id="fav_ajax" ajax-href="{page/fav_ajax_link}">
								<a href="#" rel="nofollow"><i class="fas fa-star"/>Избранное</a>
							</div>
							<div id="compare_ajax" ajax-href="{page/compare_ajax_link}">
								<a href="{page/compare_link}"><i class="fas fa-balance-scale"/>Сравнение</a>
							</div>
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
						<xsl:for-each select="page/custom_pages/menu_custom[in_main_menu = 'да']">
							<xsl:variable name="key" select="@key"/>
							<xsl:if test="not(menu_custom)">
								<div class="main-menu__item">
									<a href="{if (name) then link else show_page}" class="{'active'[$active_menu_item = $key]}">
										<xsl:value-of select="if (name) then name else header"/>
									</a>
								</div>
							</xsl:if>
							<xsl:if test="menu_custom">
								<div class="main-menu__item" style="position: relative;">
									<a href="#ts-{@id}" rel="nofollow" class="show-sub{' active'[$active_menu_item = $key]}">
										<span><xsl:value-of select="header"/></span>
									</a>
									<div id="ts-{@id}" class="popup-text-menu" style="position: absolute; z-index: 2; display: none;">
										<div class="sections">
											<xsl:for-each select="menu_custom">
												<a href="{show_page}">
													<xsl:value-of select="header"/>
												</a>
											</xsl:for-each>
										</div>
									</div>
								</div>
							</xsl:if>
						</xsl:for-each>
					<!-- 	<div class="main-menu__item">
							<a href="#" rel="nofollow"><span>Акции</span></a>
						</div>
						<div class="main-menu__item">
							<a href="#" rel="nofollow"><span>Новости</span></a>
						</div>
						<div class="main-menu__item">
							<a href="#" rel="nofollow"><span>О компании</span></a>
						</div>
						<div class="main-menu__item">
							<a href="#" rel="nofollow"><span>Помощь</span></a>
						</div> -->
						<div class="main-menu__item">
							<a href="{page/contacts_link}"><span>Контакты</span></a>
						</div>
					</div>
				</div>
			</div>
		</section>
		
	</xsl:template>



	<xsl:template name="INC_MOBILE_HEADER">
		<div class="header mobile">
			<div class="header-container">
				<a href="{$main_host}" class="logo">
					<img src="img/logo.svg" alt="На главную страницу" style="height: 1.5em; max-width: 100%;"/>
				</a>
				<div class="icons-container">
					<a href="{page/contacts_link}"><i class="fas fa-phone"></i></a>
					<a href="{page/cart_link}" rel="nofollow"><i class="fas fa-shopping-cart"></i></a>
					<a href="javascript:showMobileMainMenu()" rel="nofollow"><i class="fas fa-bars"></i></a>
				</div>
				<div class="search-container">
					<form action="{page/search_link}" method="post">
						<input id="q-ipt-mobile" type="text" placeholder="Введите поисковый запрос" name="q" value="{page/variables/q}"/>
					</form>
					<div id="search-result-mobile"></div>
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
		<section class="footer">
			<div class="container">
				<!-- <div class="footer__menu">
					<a href="#" rel="nofollow">Каталог</a>
					<a href="#" rel="nofollow">Новости</a>
					<a href="#" rel="nofollow">Акции</a>
					<a href="#" rel="nofollow">О компании</a>
					<a href="#" rel="nofollow">Сервис</a>
					<a href="#" rel="nofollow">Оплата</a>
					<a href="#" rel="nofollow">Доставка</a>
					<a href="#" rel="nofollow">Контакты</a>
				</div> -->
				<div class="footer__content">
					<xsl:variable name="footer" select="page/common/footer"/>
					<div class="footer__column">
						<xsl:if test="$footer/block[1]/header and not($footer/block[1]/header = '')">
							<div class="title_3"><xsl:value-of select="$footer/block[1]/header" /></div>
						</xsl:if>
						<xsl:value-of select="$footer/block[1]/text" disable-output-escaping="yes"/>
						<!-- <div class="forever">
							<img src="img/forever.png" alt="" />
							<a href="http://forever.by" target="_blank">Разработка сайта студия веб-дизайна Forever</a>
						</div> -->
					</div>
					<xsl:apply-templates select="$footer/block[position() &gt; 1]" mode="footer"/>
				</div>
			</div>
		</section>
		<!-- FOOTER END -->

		<!-- MODALS BEGIN -->
		<!-- modal login -->
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-login">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
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
		<!-- modal XXL-warranty -->
		<xsl:call-template name="WARRANTY_FORM"/>
		<!-- modal XXL-warranty -->
		<xsl:call-template name="CHEAPER_FORM"/>
		<!-- modal feedback -->
		<xsl:call-template name="FEEDBACK_FORM"/>
		<!-- one click form -->
		<xsl:call-template name="ONE_CLICK_FORM"/>
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
					<li><i class="fas fa-th-list"></i> <a href="#" rel="nofollow" onclick="showMobileCatalogMenu(); return false">Каталог продукции</a></li>
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
					<a href="#" rel="nofollow" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
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
						<a href="#" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a>
						<a href="{show_products}" class="header"><xsl:value-of select="name"/></a>
						<a href="#" rel="nofollow" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
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
						<a href="#" class="back" rel="#m_sub_{../@id}"><i class="fas fa-chevron-left"></i></a>
						<a href="{show_products}" class="header"><xsl:value-of select="name"/></a>
						<a href="#" rel="nofollow" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
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
		<span><i class="fas fa-print"></i> <a href="javascript:window.print()" rel="nofollow">Распечатать</a></span>
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
			<xsl:value-of select="$common/left" disable-output-escaping="yes"/>
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


	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>

	<xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="price" select="if($discount_time) then format-number(f:num(price)*$discount, '#0.00') else price"/>
		<xsl:variable name="price_old" select="if($discount_time) then format-number(f:num(price), '#0.00') else format-number(f:num(price_old), '#0.00')"/>

		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device items-catalog__device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_path,');')}"></a>
			<a href="{show_product}" class="device__title" title="{name}"><xsl:value-of select="type"/><xsl:text> </xsl:text><xsl:value-of select="name"/></a>
			<div class="device__article-number">Артикул: <xsl:value-of select="code"/></div>
			<xsl:if test="$has_price">
				<div class="device__price">
					<xsl:if test="f:num($price_old) != 0"><div class="price_old"><span><xsl:value-of select="$price_old"/></span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="$price"/> р.</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price">

				</div>
			</xsl:if>
			<div class="extra-links">
				<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
			</div>
			<div class="device__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
			</div>
			<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
				<div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div>
			</xsl:if>
			<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div>
			</xsl:if>
			<div class="device__actions">
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" class="icon-link device__action-link" ajax="true" ajax-loader-id="compare_list_{@id}">
							<i class="fas fa-balance-scale"></i>сравнить
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<span><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" class="icon-link device__action-link" ajax="true" ajax-loader-id="fav_list_{@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<xsl:for-each select="tag">
				<div class="device__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
		</div>
	</xsl:template>



	<xsl:template match="accessory | set | probe | product | assoc" mode="lines">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>

		<xsl:variable name="price" select="if($discount_time) then format-number(f:num(price)*$discount, '#0.00') else price"/>
		<xsl:variable name="price_old" select="if($discount_time) then format-number(f:num(price), '#0.00') else format-number(f:num(price_old), '#0.00')"/>

		<div class="device device_row">
			<!-- <div class="tags"><span>Акция</span></div> -->
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}"></a>
			<div class="device__info">

				<a href="{show_product}" class="device__title"><strong><xsl:value-of select="type"/><xsl:text> </xsl:text><xsl:value-of select="name"/></strong></a>
				<div class="device__description">
					<p><xsl:value-of select="short" disable-output-escaping="yes"/></p>
					<xsl:variable name="extra" select="parse-xml(concat('&lt;extra&gt;', extra_xml, '&lt;/extra&gt;'))/extra"/>
					<div class="item-icons">
						<xsl:for-each select="$extra/pic">
							<xsl:variable name="et" select="replace(., '&lt;br/&gt;', ' ')"/>
							<span><img src="{@link}" alt="{$et}"  data-toggle="tooltip" data-placement="left" title="{$et}"/></span>
						</xsl:for-each>
						<script>
							$(function () {
								$('[data-toggle="tooltip"]').tooltip()
							})
						</script>
					</div>
				</div>
			</div>
			<div class="device__article-number">Артикул: <br/><xsl:value-of select="code"/></div>
			
			<xsl:if test="$has_price">
				<div class="device__price device_row__price">
					<xsl:if test="f:num($price_old) != 0"><div class="price_old"><span><xsl:value-of select="$price_old"/></span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="$price"/> р.</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price device_row__price">

				</div>
			</xsl:if>
			<div class="device__order device_row__order">
				<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
				<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-check"></i> в наличии</div>
				</xsl:if>
				<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
					<div class="device__in-stock_no device_row__in-stock"><i class="far fa-clock"></i> под заказ</div>
				</xsl:if>
				<div class="device__actions device_row__actions">
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" rel="nofollow" class="icon-link device__action-link" ajax="true" ajax-loader-id="compare_list_{@id}">
							<i class="fas fa-balance-scale"></i>сравнить
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<span><i class="fas fa-balance-scale"></i>&#160;<a rel="nofollow" href="{from_compare}">убрать</a></span>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" rel="nofollow" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" rel="nofollow" class="icon-link device__action-link" ajax="true" ajax-loader-id="fav_list_{@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
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
				$('[data-toggle="popover"]')
					.popover({ trigger: "manual" , html: true, animation:false})
					.on("mouseenter", function () {
						var _this = this;
						$(this).popover("show");
						$(".popover").on("mouseleave", function () {
							$(_this).popover('hide');
						});
					})
					.on("mouseleave", function () {
						var _this = this;
						setTimeout(function () {
							if (!$(".popover:hover").length) {
								$(_this).popover("hide");
							}
						}, 300);
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
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<div class="level-1">
					<div class="capsule">
						<a href="{show_products}"><xsl:value-of select="name"/></a>
					</div>
				</div>
			</xsl:for-each>
		</div>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="CONTENT"/>
	<xsl:template name="BANNERS"/>
	<xsl:template name="EXTRA_SCRIPTS"/>






	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template name="BODY">
		<div id="discount-popup-2" class="message" style="display: none;"></div>
		<div id="discount-popup" class="discount-alert" style="display: none;"></div>
	</xsl:template>



	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
		<html lang="ru">
			<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
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
				<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/app.css"/>
				<link rel="stylesheet" type="text/css" href="css/tmp_fix.css"/>
				<link rel="stylesheet" type="text/css" href="slick/slick.css"/>
				<link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
				<link rel="stylesheet" href="fotorama/fotorama.css"/>
				<link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"/>
				<script defer="defer" src="js/font_awesome_all.js"/>
				<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
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
				<xsl:call-template name="BODY"/>
				<!-- ALL CONTENT BEGIN -->
				<div class="content-container">
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
				<script type="text/javascript" src="js/fwk/common.js"/>
				<script type="text/javascript" src="slick/slick.min.js"></script>
				<script type="text/javascript" src="js/search-tip.js"></script>
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
						<xsl:if test="page/@name = 'index'">
							$('.slick-slider').slick({
							infinite: true,
							slidesToShow: 6,
							slidesToScroll: 6,
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
						</xsl:if>
					});

					initCatalogPopupMenu('#catalog_main_menu', '.popup-catalog-menu');
					initCatalogPopupSubmenu('.sections', '.sections a', '.subsections');
					initDropDownHeader();

					$(document).on('click', '.print-img', function(e){
						e.preventDefault();
						printImage($(this).attr('href'));
						return false;
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


					function printImage(imgId) {
						var img = document.getElementById(imgId);
						var win = window.open(img.src,"_blank");
						win.onload = function(){win.print();}
					}
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
		<!--<link rel="canonical" href="{concat($main_host, $canonical)}" />-->
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
			<xsl:value-of select="if(title != '') then title else $title"/>
		</title>
		<meta name="description" content="{description}"/>
		<meta name="keywords" content="{keywords}"/>
		<xsl:value-of select="meta" disable-output-escaping="yes"/>
	</xsl:template>
</xsl:stylesheet>
