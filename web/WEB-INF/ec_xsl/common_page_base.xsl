<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>


	<xsl:variable name="common" select="page/common"/>


	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="''" />
	<xsl:variable name="meta_description" select="''" />
	<xsl:variable name="meta_keywords" select="''" />
	<xsl:variable name="base" select="page/base" />
	<xsl:variable name="main_host_tmp" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />
	<xsl:variable name="main_host" select="if(ends-with($main_host_tmp, '/')) then $main_host_tmp else concat($main_host_tmp, '/')" />

	<xsl:variable name="default_canonical" select="if(page/@name != 'index') then concat('/', tokenize(page/source_link, '\?')[1]) else ''" />
	<xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

	<xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


	<xsl:variable name="active_menu_item"/>


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
				<xsl:call-template name="SEARCH_FORM" />
<!--				<form class="header__search header__column" action="{page/search_link}" method="get" style="flex-wrap: wrap">-->
<!--					<input type="text" class="text-input header__field" name="q" value="{page/variables/q}" autocomplete="off" />-->
<!--					<input type="submit" class="button header__button" value="Поиск" />-->
<!--					<div style="color: #9f9e9e; display: block; flex-basis: 100%;">-->
<!--					Поиск по нашему складу и складам партнеров-->
<!--					</div>-->
<!--				</form>-->
				<div class="cart-info header__column" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no">
					<a href=""><i class="fas fa-shopping-cart"></i>Корзина</a>
					<!-- <div>Товаров: <strong>2</strong></div>
					<div>Cумма: <strong>1250 руб.</strong></div> -->
				</div>
				<div class="user-links header__column">
<!--					<xsl:call-template name="PERSONAL_DESKTOP"/>-->
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
						<input type="text" placeholder="Введите поисковый запрос" name="q" value="{page/variables/q}"/>
					</form>
					<p style="color: #9f9f9f;font-size: 1.45rem;">поиск по нашему складу и складам партнеров</p>
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
				<div class="footer__column">
					<p>Электронные компоненты Чип Электроникс</p>
                  <p>Адрес: г. Минск, 220070, пр-т Партизанский 14, к. 514A
                     								<br />Ст. метро «Пролетарская»
                  </p>
                  <div class="rating" style="font-weight: normal" itemscope="" itemtype="http://data-vocabulary.org/Review-aggregate">
                     <p><span itemprop="itemreviewed">Наш рейтинг</span> 4,9
                     <br />
                       голосов: <span itemprop="votes">115</span><span itemprop="rating" itemscope="" itemtype="http://data-vocabulary.org/Rating">
                       <meta itemprop="value" content="4.9" />
                       <meta itemprop="best" content="5" /></span></p><i class="fas fa-star" rel="1"></i><i class="fas fa-star" rel="2"></i><i class="fas fa-star" rel="3"></i><i class="fas fa-star" rel="4"></i><i class="far fa-star" rel="5"></i></div>
				</div>
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
							<input type="submit"  value="Войти"/>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- modal feedback -->
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-special-wrap" ajax-href="ajax_special_order" show-loader="yes"></div>
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
<!--		<div class="block-title block-title_normal">Каталог</div>-->
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<xsl:variable name="l1_active" select="@id = $sel_sec_id"/>
				<div class="level-1{' active'[$l1_active]}">
					<div class="capsule">
						<a href="{show_products}"><xsl:value-of select="name"/> </a>
					</div>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<div style="margin-bottom:14px; padding:0;"></div>
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


	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>

	<xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'  and f:num(qty) != 0"/>
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
			<a href="{show_product}" class="device__title" title="{name}"><xsl:value-of select="name"/></a>
			<div class="device__article-number"><xsl:value-of select="code"/></div>
			<xsl:if test="$has_price">
				<div class="device__price" style="flex-direction: column">
					<xsl:if test="price_old">
						<div class="price_old">
							<span><xsl:value-of select="f:price_catalog(price_old ,'', min_qty)"/>.</span>
						</div>
					</xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:price_catalog(price, unit, min_qty)"/>.</div>
					<div class="nds">*цена включает НДС</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price">
					Цена по запросу 
				</div>
			</xsl:if>
			<div class="device__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="f:num(qty) &gt; 0">
								<input type="number" class="text-input" name="qty" value="{min_qty}" step="{min_qty}" min="{min_qty}"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="f:num(qty) = 0">
								<input type="hidden" class="text-input" name="qty" value="{min_qty}" step="{min_qty}" min="{min_qty}"/>
								<input type="submit" class="button not_available" value="Под заказ"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
			</div>
			<xsl:if test="f:num(qty) != 0">
				<div class="device__in-stock"><i class="fas fa-check"></i> в наличии <xsl:value-of select="concat(qty, unit,'.')"/></div>
			</xsl:if>
			<xsl:if test="f:num(qty) = 0">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>нет в наличии</div>
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
		<xsl:variable name="has_price" select="f:num(price) != 0 and f:num(qty) != 0"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>


		<div class="device device_row">
			<!-- <div class="tags"><span>Акция</span></div> -->
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}">&#160;</a>
			<div class="device__info">
				<a href="{show_product}" class="device__title"><xsl:value-of select="name"/></a>
				<div class="device__description">
					<!-- <xsl:value-of select="description" disable-output-escaping="yes"/> -->
					<xsl:for-each select="params/param">
						<span style="color: #616161;"><xsl:value-of select="@caption"/></span>&#160;-&#160;<xsl:value-of select="."/>
						<xsl:text>;</xsl:text><br/>
					</xsl:for-each>
				</div>
			</div>
			<div class="device__article-number"><xsl:value-of select="code"/></div>
			<div class="device__actions device_row__actions">
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
			<xsl:if test="$has_price">
				<div class="device__price device_row__price">
					<xsl:if test="price_old"><div class="price_old"><span><xsl:value-of select="f:price_catalog(price_old, '','')"/></span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:price_catalog(price, unit, min_qty)"/></div>
					<div class="nds">*цена c НДС</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price device_row__price">
					Цена по запросу
				</div>
			</xsl:if>
			<div class="device__order device_row__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="{min_qty}" step="{min_qty}" min="{min_qty}"/>
								<input type="submit" class="button" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="{min_qty}" step="{min_qty}" min="{min_qty}"/>
								<input type="submit" class="button not_available" value="Под заказ"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
				<xsl:if test="f:num(qty) != 0">
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-check"></i> в наличии <xsl:value-of select="concat(qty, unit,'.')"/></div>
				</xsl:if>
				<xsl:if test="f:num(qty) = 0">
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-check"></i>нет в наличии</div>
				</xsl:if>
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
					<xsl:if test="$seo/bottom_text != ''">
						<div class="page-content m-t">
							<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
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
	<xsl:template name="BANNERS"/>
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

				<xsl:for-each select="$head-start-modules">
					<xsl:value-of select="code" disable-output-escaping="yes"/>
				</xsl:for-each>

				<xsl:call-template name="SEO"/>
				<link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,700,900&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
				<link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css"/>
				<link rel="stylesheet" href="css/app.css?version=1.25"/>
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
				<script type="text/javascript" src="admin/ajax/ajax.js?v=1.0"/>
				<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
				<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
				<script type="text/javascript" src="js/fwk/common.js?v=1.01"/>
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
					});

					$(window).resize(function(){
					var oh = $(".footer").outerHeight();
					$(".footer-placeholder").height(oh+40);
					$(".footer").css("margin-top", -1*oh);
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

	<xsl:template name="SEARCH_FORM">
		<form class="header__search header__column" action="{page/search_link}" method="get" style="flex-wrap: wrap">
			<input type="text" class="text-input header__field" name="q" value="{page/variables/q}" autocomplete="off" />
			<input type="submit" class="button header__button" value="Поиск" />
			<div style="color: #9f9e9e; display: block; flex-basis: 100%;">
				Поиск по складу в Минске и складам ПЛАТАНа, DIGIKEY, FARNELL, VERICAL
			</div>
		</form>
	</xsl:template>

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
			<meta name="keywords" content="{replace($meta_keywords, $quote, '')}"/>
		</xsl:if>
		<xsl:if test="$common/google_verification">
			<meta name="google-site-verification" content="{$common/google_verification}"/>
		</xsl:if>
		<xsl:if test="$common/yandex_verification">
			<meta name="google-site-verification" content="{$common/yandex_verification}"/>
		</xsl:if>
		<xsl:call-template name="MARKUP" />
	</xsl:template>

	<xsl:template name="TOP_SEO">
		<xsl:if test="$seo/text != ''">
			<div class="page-content m-t">
				<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
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
