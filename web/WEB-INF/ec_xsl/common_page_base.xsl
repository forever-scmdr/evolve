<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>

	<!-- SEO VARS -->
	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>
	<xsl:variable name="default_h1"/>
	<xsl:variable name="title" select="'САКУРА БЕЛ - Комплексные системы безопасности'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $default_h1"/>

	<xsl:variable name="meta_description" select="''" />
	<xsl:variable name="base" select="'https://www.sakurabel.com'" />
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base" />

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


	<xsl:variable name="active_menu_item"/>



	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->






	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="has-top-stripe desktop">
			<div class="container">
				<i class="fas fa-phone"></i>
				<xsl:value-of select="page/common/top" disable-output-escaping="yes"/>
			</div>
		</div>
		<div class="container header desktop">
			<div class="header-container" style="position: relative;">
				<div class="logo">
					<a href="{$base}"><img src="img/logo.png" alt="sakurabel.com" /></a>
				</div>
				<div class="search">
					<form action="{page/search_link}" method="post">
						<input type="text" placeholder="Введите поисковый запрос" name="q" value="{page/variables/q}"/>
						<input type="submit" value="Найти"/>
					</form>
				</div>
				<div class="other-container">
					<div class="cart" id="cart_ajax" ajax-href="{page/cart_ajax_link}" ajax-show-loader="no">
						<p><i class="fas fa-shopping-cart"/>&#160;<strong>Корзина пуста</strong></p>
					</div>
					<div class="user">
						<xsl:call-template name="PERSONAL_DESKTOP"/>
						<div id="fav_ajax" ajax-href="{page/fav_ajax_link}">
							<p><i class="fas fa-star"/> <a href="">&#160;</a></p>
						</div>
						<div id="compare_ajax" ajax-href="{page/compare_ajax_link}">
							<p><i class="fas fa-balance-scale"/> <a href="compare.html">&#160;</a></p>
						</div>
					</div>
				</div>
				<div class="main-menu">
					<!-- <a href="{page/index_link}">Главная</a> -->
					<a href="{page/catalog_link}" id="catalog_main_menu" class="{'active'[$active_menu_item = 'catalog']}"><i class="fas fa-bars"/>Каталог</a>
					<a href="{page/news_link}" class="{'active'[$active_menu_item = 'news']}">Новости</a>
					<xsl:for-each select="page/custom_pages/menu_custom[in_main_menu = 'да']">
						<xsl:variable name="key" select="@key"/>
						<xsl:if test="not(menu_custom)">
							<a href="{show_page}" class="{'active'[$active_menu_item = $key]}">
								<xsl:value-of select="header"/>
							</a>
						</xsl:if>
						<xsl:if test="menu_custom">
							<a href="#ts-{@id}" class="show-sub{' active'[$active_menu_item = $key]}">
								<xsl:value-of select="header"/>
							</a>
						</xsl:if>
					</xsl:for-each>
					 <a href="{page/contacts_link}" class="{'active'[$active_menu_item = 'contacts']}">Контакты</a>
				</div>
				<div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
				 	<div class="sections">
						<xsl:for-each select="page/catalog/section">
						    <a href="{if(section) then show_section else show_products}"
						       class="cat_menu_item_1" rel="#sub_{@id}"><xsl:value-of select="name" /></a>
						</xsl:for-each>
				 	</div>
					<xsl:for-each select="page/catalog/section">
					    <div class="subsections" style="display: none" id="sub_{@id}">
							<xsl:for-each select="section">
						        <a href="{if(section) then show_section else show_products}"><xsl:value-of select="name" /></a>
							</xsl:for-each>
					    </div>
					</xsl:for-each>
				</div>
				<xsl:for-each select="page/custom_pages/menu_custom[in_main_menu = 'да' and menu_custom]">
					<div class="popup-text-menu" style="position: absolute; display: none;" id="ts-{@id}">
						<div class="sections">
							<xsl:for-each select="menu_custom">
								<a href="{show_page}">
									<xsl:value-of select="header"/>
								</a>
							</xsl:for-each>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="INC_MOBILE_HEADER">
		<div class="header mobile">
			<div class="header-container">
				<a href="{$base}" class="logo">
					<img src="img/logo.png" alt="sakurabel.com" style="height: 1.5em; max-width: 100%;"/>
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
		<div class="footer-placeholder"></div><!-- ружен скрипт, задающий блоку высоту футера -->
		<div class="footer">
			<div class="container">
				<div class="row">
					<div class="col-xs-12">
						<div class="footer-container">
							<div class="block">
								<p><strong>© sakurabel.com, 2018</strong></p>
								<div class="forever">
									<a href="http://forever.by">Разработка сайта -<xsl:call-template name="BR"/>студия веб-дизайна Forever</a>
								</div>
							</div>
							<div class="rating" itemscope="" itemtype="http://data-vocabulary.org/Review-aggregate">
				<p>
					<span itemprop="itemreviewed">Наш рейтинг</span> 4,9
					<br />
					голосов: <span itemprop="votes">250</span>
					<span itemprop="rating" itemscope="" itemtype="http://data-vocabulary.org/Rating">
                        <meta itemprop="value" content="4.8"/>
                        <meta itemprop="best" content="5"/>
                    </span>
				</p>
				<i class="fas fa-star" rel="1"></i>
				<i class="fas fa-star" rel="2"></i>
				<i class="fas fa-star" rel="3"></i>
				<i class="fas fa-star" rel="4"></i>
				<i class="far fa-star" rel="5"></i>
			</div>
							
							
							<div class="block">
								<p>Принимаем к оплате<xsl:call-template name="BR"/> пластиковые карточки</p>
								<div class="cards">
									<img src="img/mastercard.svg" alt="mastercard"/>
									<img src="img/visa.svg" alt="visa"/>
								</div>
							</div>
							<div class="block contacts">
								<xsl:value-of select="page/common/bottom" disable-output-escaping="yes"/>
							</div>
							<div class="block address">
								<xsl:value-of select="page/common/bottom_address" disable-output-escaping="yes"/>
								<!-- <p>Мы в социальных сетях</p>
								<div class="social">
									<a href="" target="_blank"><i class="fab fa-facebook" /></a>
									<a href="" target="_blank"><i class="fab fa-vk" /></a>
									<a href="" target="_blank"><i class="fab fa-odnoklassniki" /></a>
									<a href="" target="_blank"><i class="fab fa-youtube" /></a>
									<a href="" target="_blank"><i class="fab fa-instagram" /></a>
								</div> -->
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- FOOTER END -->

		<!-- MODALS BEGIN -->
		<!-- modal login -->
		<xsl:call-template name="LOGIN_FORM"/>

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
					<li><i class="fas fa-shopping-cart"></i> <a rel="nofollow" href="{page/cart_link}">Корзина</a></li>
					<li><i class="fas fa-star"></i> <a rel="nofollow" href="{page/fav_link}">Избранное</a></li>
					<li><i class="fas fa-balance-scale"></i> <a rel="nofollow" href="{page/compare_link}">Сравнение</a></li>
				</ul>
				<ul>
					<li><a href="{page/news_link}">Новости</a></li>
					<xsl:for-each select="page/custom_pages/menu_custom">
						<li><a href="{show_page}"><xsl:value-of select="header"/></a></li>
					</xsl:for-each>
				</ul>
			</div>
		</div>
		<script>
			function showMobileCatalogMenu() {
				$('#mobile_catalog_menu').toggle();
			}

			$(document).ready(function() {
				$("#mobile_catalog_menu .content li a[rel^='#']").click(function(event) {
					event.preventDefault();
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
		<div id="mobile_catalog_menu" class="nav-container mobile" style="display: none; position:absolute; width: 100%">
			<div class="content" id="m_sub_cat">
				<div class="small-nav">
					<a class="header">Каталог продукции</a>
					<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
				</div>
				<ul>
					<xsl:for-each select="page/catalog/section">
						<li>
							<a href="{show_section}" rel="{if (section) then concat('#m_sub_', @id) else ''}"><xsl:value-of select="name"/></a>
							<xsl:if test="section">
								<i class="fas fa-chevron-right"></i>
							</xsl:if>
						</li>
					</xsl:for-each>
				</ul>
			</div>
			<xsl:for-each select="page/catalog/section[section]">
				<div class="content next" id="m_sub_{@id}">
					<div class="small-nav">
						<a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a>
						<a href="{show_section}" class="header"><xsl:value-of select="name"/></a>
						<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
					</div>
					<ul>
						<xsl:for-each select="section">
							<li>
								<a href="{if (section) then show_section else show_products}" rel="{if (section) then concat('#m_sub_', @id) else ''}"><xsl:value-of select="name"/></a>
								<xsl:if test="section">
									<i class="fas fa-chevron-right"></i>
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
						<a href="{if (section) then show_section else show_products}" class="header"><xsl:value-of select="name"/></a>
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
						<a href="{if(section) then show_section else show_products}"><xsl:value-of select="name"/> </a>
					</div>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<xsl:for-each select="section">
						<xsl:variable name="l2_active" select="@id = $sel_sec_id"/>
						<div class="level-2{' active'[$l2_active]}"><a href="{if(section) then show_section else show_products}"><xsl:value-of select="name"/></a></div>
						<xsl:if test=".//@id = $sel_sec_id">
							<xsl:for-each select="section">
								<xsl:variable name="l3_active" select="@id = $sel_sec_id"/>
								<div class="level-3{' active'[$l3_active]}"><a href="{if(section) then show_section else show_products}"><xsl:value-of select="name"/></a></div>
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
		<div class="actions">
			<div class="h3">Акции</div>
			<div class="actions-container">
				<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
			</div>
		</div>
		<div class="contacts">
			<xsl:value-of select="page/common/left" disable-output-escaping="yes"/>
		</div>
	</xsl:template>



	<xsl:template name="CATALOG_LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>



	<xsl:template name="ACTIONS_MOBILE">
		<div class="actions mobile">
			<div class="h3">Акции</div>
			<div class="actions-container">
				<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
			</div>
		</div>
	</xsl:template>


	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>

	<xsl:template match="accessory | set | probe | product">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<div class="catalog-item">
			<!--
			<div class="tags">
				<span>Акция</span>
			</div>
			-->
			<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>
			<a href="{show_product}" class="image-container" style="background-image: url({$pic_path});">
				<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->
			</a>
			<div class="name">
				<a href="{show_product}" title="{name}"><xsl:value-of select="concat(vendor_code,' ',name)"/></a>
				<p><xsl:value-of select="substring-before(substring-after(short, 'description&quot;&gt;'), '&lt;')" disable-output-escaping="yes"/></p>
			</div>
			<div class="art-number">
				<p>арт. <xsl:value-of select="code"/></p>
			</div>
			<div class="price">
				<xsl:if test="$has_price">
					<!--<p><span>Старая цена</span>100 р.</p>-->
					<p></p>
					<p>Цена <xsl:value-of select="price"/> р.</p>
				</xsl:if>
				<xsl:if test="not($has_price)">
					<p>По запросу</p>
				</xsl:if>
			</div>
			<div class="order">
				<div id="cart_list_{code}" class="product_purchase_container">
					<form action="{to_cart}" method="post">
						<xsl:if test="$has_price and qty != '0'">
							<input type="number" name="qty" value="1" min="0"/>
							<input type="submit" value="Купить"/>
						</xsl:if>
						<xsl:if test="not($has_price) or not(qty != '0')">
							<input type="number" name="qty" value="1" min="0"/>
							<input type="submit" class="not_available" value="Под заказ"/>
						</xsl:if>
					</form>
				</div>
				<xsl:choose>
					<xsl:when test="qty and qty != '0'"><div class="quantity">Осталось <xsl:value-of select="qty"/> шт.</div></xsl:when>
					<xsl:otherwise><div class="quantity">Нет на складе</div></xsl:otherwise>
				</xsl:choose>
				
				<div class="links">
					<div id="compare_list_{code}">
						<span><a href="{to_compare}" rel="nofollow" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i><!-- в сравнение --></a></span>
					</div>
					<xsl:choose>
						<xsl:when test="$is_fav">
							<span class="active"><a href="{from_fav}" rel="nofollow"><i class="fas fa-star"></i> убрать</a></span>
						</xsl:when>
						<xsl:otherwise>
							<div id="fav_list_{code}">
								<span><a href="{to_fav}" rel="nofollow" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i><!-- в избранное --></a></span>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</div>
				
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
		<div class="container">
			<div class="row">
				<!-- LEFT COLOUMN BEGIN -->
				<div class="col-md-3 lc desktop">
					<xsl:call-template name="LEFT_COLOUMN"/>
				</div>
				<!-- LEFT COLOUMN END -->

				<!-- RIGHT COLOUMN BEGIN -->
				<div class="col-md-9 col-xs-12 main-content">
					<div class="mc-container">
						<xsl:call-template name="INC_MOBILE_HEADER"/>
						<xsl:call-template name="CONTENT"/>
					</div>
				</div>
				<!-- RIGHT COLOUMN END -->
			</div>
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
			<base href="{page/base}"/>
			<meta charset="utf-8"/>
			<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
			<meta name="viewport" content="width=device-width, initial-scale=1"/>
			<xsl:call-template name="SEO"/>
			<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
			<link rel="stylesheet" href="css/new/app.css"/>
			<!-- <link rel="stylesheet" href="css/app.css"/> -->
			<link rel="stylesheet" type="text/css" href="slick/slick.css"/>
			<link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
			<link rel="stylesheet" href="fotorama/fotorama.css"/>
			<link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"/>
			<script defer="defer" src="js/font_awesome_all.js"/>
			<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
		</head>
		<body>
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

			<script type="text/javascript" src="js/bootstrap.js"/>
			<script type="text/javascript" src="admin/ajax/ajax.js"/>
			<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
			<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
			<script type="text/javascript" src="js/fwk/common.js"/>
			<script type="text/javascript" src="slick/slick.min.js"></script>
			<script type="text/javascript">
				$(document).ready(function(){
					$(".footer-placeholder").height($(".footer").outerHeight()+40);
					$('.slick-slider').slick({
						infinite: true,
						slidesToShow: 6,
						slidesToScroll: 6,
						dots: true,
						arrows: false,
						responsive: [
							{
						      breakpoint: 767,
						      settings: {
						        slidesToShow: 2,
						        slidesToScroll: 2,
						        infinite: true,
						        dots: true
						      }
						    }
						]
					});

					initCatalogPopupMenu('#catalog_main_menu', '.popup-catalog-menu');
					initCatalogPopupSubmenu('.sections', '.sections a', '.subsections');
				});
			</script>
			<xsl:call-template name="EXTRA_SCRIPTS"/>
			<xsl:call-template name="USER_DEFINED_SCRIPTS"/>
		</body>
	</html>
	</xsl:template>

	


	<xsl:template name="USER_DEFINED_SCRIPTS">
		<xsl:for-each select="page/modules/named_code">
			<xsl:value-of select="code" disable-output-escaping="yes"/>
		</xsl:for-each>
	</xsl:template>

	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->






	<xsl:template match="*" mode="content">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	<xsl:apply-templates select="text_part | gallery_part" mode="content"/>	
	</xsl:template>

	<xsl:template match="text_part" mode="content">
	<div class="h3"><xsl:value-of select="name"/></div>
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
		<xsl:if test="$seo">
			<xsl:apply-templates select="$seo"/>
		</xsl:if>
		<xsl:if test="not($seo) or $seo = ''">
			<title>
				<xsl:value-of select="$title"/>
			</title>
			<meta name="description" content="{replace($meta_description, $quote, '')}"/>
		</xsl:if>
		<link rel="canonical" href="{if(page/@name = 'index') then $base else if(//canonical_link != '') then concat($base, //canonical_link) else concat($base, '/', /page/source_link)}" />
		<xsl:value-of select="page/common/google_verification" disable-output-escaping="yes"/>
		<xsl:value-of select="page/common/yandex_verification" disable-output-escaping="yes"/>
		<xsl:call-template name="MARKUP" />
		<xsl:call-template name="PAGINATION_LINKS" />
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

	<xsl:template name="SEO_TEXT">
		<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
		<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>
		<xsl:if test="$seo[1]/text != '' and (page/variables/page = '1' or not(page/variables/page))">
			<div class="page-content m-t">
				<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PAGINATION_LINKS">
		<xsl:variable name="pages" select="//*[ends-with(name(), '_pages')]"/>
		<xsl:if test="$pages">
			<xsl:variable name="current_page" select="if(page/variables/page) then number(page/variables/page) else 1"/>
			
			<xsl:variable name="prev" select="$pages/page[$current_page - 1]"/>
			<xsl:variable name="next" select="$pages/page[$current_page + 1]"/>
			
			<xsl:if test="$prev">
				<link rel="prev" href="{$prev/link}" />
			</xsl:if>
			<xsl:if test="$next">
				<link rel="next" href="{$next/link}" />
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>