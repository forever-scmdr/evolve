<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="login_form_ajax.xsl"/>
	<xsl:import href="personal_ajax.xsl"/>
	<xsl:import href="my_price_ajax.xsl"/>
	<xsl:import href="one_click_ajax.xsl"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>

	<!-- <TITLE> -->
	
	<xsl:template name="TITLE">Метабо</xsl:template>

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


	<xsl:variable name="active_menu_item"/>



	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->






	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="container-fluid top-stripe desktop">
			<div class="container">
				<i class="fas fa-phone"></i>
				<xsl:value-of select="page/common/top" disable-output-escaping="yes"/>
			</div>
		</div>
		<div class="container header desktop">
			<div class="row">
				<div class="col-xs-12">
					<div class="header-container" style="position: relative;">
						<div class="logo">
							<a href="{page/index_link}"><img src="img/logo.jpg" alt="" /></a>
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
							<xsl:for-each select="page/menu_custom">
								<xsl:variable name="key" select="@key"/>
								<a href="{show_page}" class="{'active'[$active_menu_item = $key]}"><xsl:value-of select="header"/></a>
							</xsl:for-each>
							<!-- <a href="{page/articles_link}">Статьи</a>
							<a href="">Наши проекты</a>
							<a href="{page/dealers_link}">Дилеры</a>
							<a href="/about">О компании</a>
							<a href="{page/docs_link}">Документация</a>
							 -->
							 <a href="{page/contacts_link}" class="{'active'[$active_menu_item = 'contacts']}">Контакты</a>
						</div>
						<div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
						 	<div class="sections">
								<xsl:for-each select="page/catalog/section">
								    <a href="{if (section) then show_section else show_products}"
								       class="cat_menu_item_1" rel="#sub_{@id}"><xsl:value-of select="name" /></a>
								</xsl:for-each>
						 	</div>
							<xsl:for-each select="page/catalog/section">
							    <div class="subsections" style="display: none" id="sub_{@id}">
									<xsl:for-each select="section">
								        <a href="{if (section) then show_section else show_products}"><xsl:value-of select="name" /></a>
									</xsl:for-each>
							    </div>
							</xsl:for-each>
						</div>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="INC_MOBILE_HEADER">
		<div class="header mobile">
			<div class="header-container">
				<a href="" class="logo">
					<img src="img/logo.jpg" alt="" style="height: 1.5em; max-width: 100%;"/>
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
								<p><strong>© metabo-shop.by, 2018</strong></p>
								<div class="forever">
							<!--		<a href="http://forever.by">Разработка сайта -<xsl:call-template name="BR"/>студия веб-дизайна Forever</a> -->
								</div>
							</div>
							<div class="block">
								<p>Принимаем к оплате<xsl:call-template name="BR"/> пластиковые карточки</p>
								<img src="http://mobileplus.by/images/2/icon_card_mc.png" alt=""/>
								<img src="http://mobileplus.by/images/2/icon_card_visa.png" alt=""/>
							</div>
							<div class="block contacts">
								<xsl:value-of select="page/common/bottom" disable-output-escaping="yes"/>
							</div>
							<div class="block address">
								<!-- <xsl:value-of select="page/common/bottom_address" disable-output-escaping="yes"/> -->
								<p>Мы в социальных сетях</p>
								<div class="social">
									<a href="https://vk.com/yuksmarket" target="_blank"><i class="fab fa-vk" style="color: #4F73A6;" /></a>
									<!-- <a href=""><i class="fab fa-facebook" style="color: #425796;" /></a>
									<a href=""><i class="fab fa-youtube" style="color: #FF2000;" /></a> -->
									<a href="https://www.instagram.com/yuksmarket/" target="_blank"><i class="fab fa-instagram" style="color: #333;" /></a>
								</div>
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
					<li><i class="fas fa-shopping-cart"></i> <a href="{page/cart_link}">Корзина</a></li>
					<li><i class="fas fa-star"></i> <a href="{page/fav_link}">Избранное</a></li>
					<li><i class="fas fa-balance-scale"></i> <a href="{page/compare_link}">Сравнение</a></li>
				</ul>
				<ul>
					<li><a href="{page/news_link}">Новости</a></li>
					<xsl:for-each select="page/menu_custom">
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
				$('#mobile_catalog_menu .content li a[rel!=""]').click(function(event) {
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
						<a href="{show_section}" class="header" rel=""><xsl:value-of select="name"/></a>
						<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
					</div>
					<ul>
						<xsl:for-each select="section">
							<li>
								<a href="{show_products}" rel="{if (section) then concat('#m_sub_', @id) else ''}"><xsl:value-of select="name"/></a>
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
						<a href="{show_section}" class="header" rel=""><xsl:value-of select="name"/></a>
						<a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a>
					</div>
					<ul>
						<xsl:for-each select="section">
							<li>
								<a href="{if (section) then show_section else show_products}" rel=""><xsl:value-of select="name"/></a>
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
						<a href="{if (section) then show_section else show_products}"><xsl:value-of select="name"/> </a>
					</div>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<xsl:for-each select="section">
						<xsl:variable name="l2_active" select="@id = $sel_sec_id"/>
						<div class="level-2{' active'[$l2_active]}">
							<a href="{if (section) then show_section else show_products}"><xsl:value-of select="name"/></a>
						</div>
						<xsl:if test=".//@id = $sel_sec_id">
							<xsl:for-each select="section">
								<xsl:variable name="l3_active" select="@id = $sel_sec_id"/>
								<div class="level-3{' active'[$l3_active]}">
									<a href="{if (section) then show_section else show_products}"><xsl:value-of select="name"/></a>
								</div>
								<xsl:if test=".//@id = $sel_sec_id">
									<xsl:for-each select="section">
										<xsl:variable name="l4_active" select="@id = $sel_sec_id"/>
										<div class="level-4{' active'[$l4_active]}">
											<a href="{if (section) then show_section else show_products}"><xsl:value-of select="name"/></a>
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




	<xsl:template name="PRINT">
		<span><i class="fas fa-print"></i> <a href="javascript:window.print()">Распечатать</a></span>
	</xsl:template>




	<!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->




	<xsl:template name="COMMON_LEFT_COLOUMN">
		<div class="actions">
			<h3>Акции</h3>
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
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
			</div>
		</div>
	</xsl:template>


	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>

	<xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<div class="catalog-item">
			<!--
			<div class="tags">
				<span>Акция</span>
				<span>Скидка</span>
				<span>Распродажа</span>
				<span>Горячая цена</span>
			</div>
			-->
			<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>
			<a href="{show_product}" class="image-container" style="background-image: url({$pic_path});">
				<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->
			</a>
			<div>
				<a href="{show_product}" title="{name}">
					<xsl:value-of select="name"/><xsl:text> </xsl:text>
					<xsl:value-of select="type"/><xsl:text> </xsl:text>
					<!-- (<xsl:value-of select="code"/>) -->
				</a>
				<div class="art-number">
					№ для заказа: <xsl:value-of select="code"/>
				</div>
				<p><xsl:value-of select="short" disable-output-escaping="yes"/></p>
				<xsl:variable name="extra" select="parse-xml(concat('&lt;extra&gt;', extra_xml, '&lt;/extra&gt;'))/extra"/>
				<div class="item-icons">
					<xsl:for-each select="$extra/pic"><span><img src="{@link}" alt="{.}"  data-toggle="tooltip" data-placement="left" title="{.}"/></span></xsl:for-each>
					<script>
						$(function () {
							$('[data-toggle="tooltip"]').tooltip()
						})
					</script>
				</div>
			</div>
			<div class="price">
				<xsl:if test="$has_price">
					<xsl:if test="price_old and not(price_old = '')"><p><span>Цена</span><b>
						<xsl:value-of select="price_old"/> р.</b></p></xsl:if>
					<p><xsl:if test="price_old and not(price_old = '')"><span>Цена со скидкой</span></xsl:if><xsl:value-of select="price"/> р.</p>
				</xsl:if>
				<xsl:if test="not($has_price)">
					<!-- <p><span>&#160;</span>&#160;</p>
					<p><span>&#160;</span>&#160;</p> -->
					<p>Уточняйте цену</p>
				</xsl:if>
			</div>
			<div class="order">
				<xsl:variable name="available" select="available = '1'"/>
				<div id="cart_list_{code}" class="product_purchase_container">
					<form action="{to_cart}" method="post">
						<xsl:if test="$available">
							<input type="number" name="qty" value="1" min="0"/>
							<input type="submit" value="В корзину"/>
						</xsl:if>
						<xsl:if test="not($available)">
							<input type="number" name="qty" value="1" min="0"/>
							<input type="submit" class="not_available" value="Под заказ"/>
						</xsl:if>
					</form>
				</div>
				<!--<xsl:choose>-->
					<!--<xsl:when test="qty and qty != '0'"><div class="quantity">Осталось <xsl:value-of select="qty"/> шт.</div></xsl:when>-->
					<!--<xsl:otherwise><div class="quantity">Нет на складе</div></xsl:otherwise>-->
				<!--</xsl:choose>-->
				<div class="extra-links">
					<a href="{my_price_link}" ajax="true" data-toggle="modal" data-target="#modal-my_price">Моя цена</a>
					<a href="{one_click_link}" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</div>
				<div class="links">
					<div id="compare_list_{code}">
						<span><i class="fas fa-balance-scale"></i> <a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}">в сравнение</a></span>
					</div>
					<xsl:choose>
						<xsl:when test="$is_fav">
							<span><i class="fas fa-star"></i> <a href="{from_fav}">убрать</a></span>
						</xsl:when>
						<xsl:otherwise>
							<div id="fav_list_{code}">
								<span><i class="fas fa-star"></i> <a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}">в избранное</a></span>
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
		<div class="container-fluid inner-content">
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
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>


	<xsl:template name="LEFT_COLOUMN">
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<div class="level-1">
					<div class="capsule">
						<a href="{show_section}"><xsl:value-of select="name"/></a>
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
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
	</xsl:text>
	<html lang="en">
		<head>
			<base href="{page/base}"/>
			<meta charset="utf-8"/>
			<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
			<meta name="viewport" content="width=device-width, initial-scale=1"/>
			<title><xsl:call-template name="TITLE"/></title>
			<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700&amp;subset=cyrillic" rel="stylesheet" />
			<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:400,700&amp;subset=cyrillic" rel="stylesheet" />
			<link rel="stylesheet" href="css/app.css"/>
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
			<xsl:call-template name="MY_PRICE_FORM"/>
			<xsl:call-template name="ONE_CLICK_FORM"/>

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

</xsl:stylesheet>