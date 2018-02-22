<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>

	<!-- <TITLE> -->
	
	<xsl:template name="TITLE">Белтесто</xsl:template>

	<xsl:variable name="sel_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>

	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->


	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="container header desktop">
			<div class="row">
				<div class="col-xs-12">
					<div class="header-container">
						<div class="logo">
							<a href=""><img src="img/logo_big.svg" alt="" style="height: 6rem; max-width: 100%;"/></a>
						</div>
						<div class="search">
							<form action=""><input type="text" placeholder="Введите поисковый запрос"/><input type="submit" value="Найти"/></form>
						</div>
						<div class="other-container">
							<div class="contacts">
								<p><i class="fas fa-phone"></i> <strong>Заказ и консультация:</strong></p>
								<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - городской;</p>
								<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - велком;</p>
								<p><a href="" data-toggle="modal" data-target="#modal-feedback">Форма обратной связи</a></p>
							</div>
							<div class="cart">
								<p><i class="fas fa-shopping-cart"></i> <strong>Ваш заказ:</strong></p>
								<p>Сумма: 91,00 р.</p>
								<p>Наименований: 1</p>
								<p><a href="cart.html">Оформить заказ</a></p>
							</div>
							<div class="user">
								<p><i class="fas fa-lock"/>
									<a href="" data-toggle="modal" data-target="#modal-login">Вход</a> / <a href="registration.html">Регистрация</a>
								</p>
								<p><i class="fas fa-star"/> <a href="">Избранное (2)</a></p>
								<p><i class="fas fa-balance-scale"/> <a href="compare.html">Сравнение (3)</a></p>
							</div>
						</div>
						<div class="main-menu">
							<a href="index.html">Главная</a>
							<a href="catalog.html">Каталог</a>
							<a href="">Новости</a>
							<a href="info_section.html">Статьи</a>
							<a href="">Наши проекты</a>
							<a href="dealers.html">Дилеры</a>
							<a href="">Документация</a>
							<a href="contacts.html">Контакты</a>
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
					<img src="img/logo_big.svg" alt="" style="height: 1.5em; max-width: 100%;"/>
				</a>
				<div class="icons-container">
					<a href=""><i class="fas fa-phone"></i></a>
					<a href=""><i class="fas fa-shopping-cart"></i></a>
					<a href=""><i class="fas fa-bars"></i></a>
				</div>
				<div class="search-container">
					<form action=""><input type="text"/></form>
				</div>
			</div>
		</div>
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
								<p><strong>© Белтесто, 2017</strong></p>
								<div class="forever">
									<a href="http://forever.by">Разработка сайта -<xsl:call-template name="BR"/>студия веб-дизайна Forever</a>
								</div>
							</div>
							<div class="block">
								<p>Принимаем к оплате<xsl:call-template name="BR"/> пластиковые карточки</p>
								<img src="http://mobileplus.by/images/2/icon_card_mc.png" alt=""/>
								<img src="http://mobileplus.by/images/2/icon_card_visa.png" alt=""/>
							</div>
							<div class="block contacts">
								<p>Заказ и консультация</p>
								<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - городской;</p>
								<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - велком;</p>
							</div>
							<div class="block address">
								<p>Адрес: г. Минск, 220070, пр-т Партизанский 14, к. 514A</p>
								<p>
									Режим работы:<xsl:call-template name="BR"/>
									Пн-Пт 10.00-18.00<xsl:call-template name="BR"/>
									Сб-Вс 10.00-17.00 (прием заказов)
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- FOOTER END -->

		<!-- MODALS BEGIN -->
		<!-- modal login -->
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-login">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
						<h4 class="modal-title">Вход</h4>
					</div>
					<div class="modal-body">
						<form action="" method="post">
							<div class="form-group">
								<label for="">Электронная почта:</label>
								<input type="text" class="form-control" id=""/>
							</div>
							<div class="form-group">
								<label for="">Пароль:</label>
								<input type="password" class="form-control" id=""/>
							</div>
							<input type="submit" name="" value="Отправить заказ"/>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- modal feedback -->
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-feedback">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
						<h4 class="modal-title">Напишите нам сообщение</h4>
					</div>
					<div class="modal-body">
						<form action="" method="post">
							<div class="form-group">
								<label for="">Ваше имя:</label>
								<input type="text" class="form-control" id=""/>
							</div>
							<div class="form-group">
								<label for="">Адрес:</label>
								<input type="text" class="form-control" id=""/>
							</div>
							<div class="form-group">
								<label for="">Телефон:</label>
								<input type="text" class="form-control" id=""/>
							</div>
							<div class="form-group">
								<label for="">Электронная почта:</label>
								<input type="text" class="form-control" id=""/>
							</div>
							<div class="form-group">
								<label for="">Сообщение:</label>
								<textarea class="form-control" rows="3"></textarea>
							</div>
							<input type="submit" name="" value="Отправить сообщение"/>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- MODALS END -->
	</xsl:template>




	<xsl:template name="INC_MOBILE_MENU">
		<div class="menu-container mobile">
			<div class="overlay"></div>
			<div class="content">
				<ul>
					<li>
						<i class="fas fa-lock"></i>
						<a href="" data-toggle="modal" data-target="#modal-login">Вход</a> / <a href="registration.html">Регистрация</a>
					</li>
				</ul>
				<ul>
					<li><i class="fas fa-th-list"></i> <a href="">Каталог продукции</a></li>
				</ul>
				<ul>
					<li><i class="fas fa-shopping-cart"></i> <a href="">Корзина (1)</a></li>
					<li><i class="fas fa-star"></i> <a href="">Избранное (3)</a></li>
					<li><i class="fas fa-balance-scale"></i> <a href="">Сравнение (6)</a></li>
				</ul>
				<ul>
					<li><a href="">Новости</a></li>
					<li><a href="">Статьи</a></li>
					<li><a href="">Наши проекты</a></li>
					<li><a href="">Дилеры</a></li>
					<li><a href="">Документация</a></li>
					<li><a href="">Контакты</a></li>
					<li><a href="">Новости</a></li>
					<li><a href="">Статьи</a></li>
					<li><a href="">Наши проекты</a></li>
					<li><a href="">Дилеры</a></li>
					<li><a href="">Документация</a></li>
					<li><a href="">Контакты</a></li>
				</ul>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="INC_MOBILE_NAVIGATION">
		<div class="nav-container mobile" style="display: none;">
			<div class="content">
				<div class="small-nav">
					<a href="" class="back"><i class="fas fa-chevron-left"></i></a>
					<a href="" class="header">Электроника</a>
					<a href="" class="close"><i class="fas fa-times"></i></a>
				</div>
				<ul>
					<li><a href="">Новости</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Статьи</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Наши проекты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Дилеры</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Документация</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Контакты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Новости</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Статьи</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Наши проекты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Дилеры</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Документация</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Контакты</a><i class="fas fa-chevron-right"></i></li>
				</ul>
			</div>
			<div class="content next">
				<div class="small-nav">
					<a href="" class="back"><i class="fas fa-chevron-left"></i></a>
					<a href="" class="header">Электроника</a>
					<a href="" class="close"><i class="fas fa-times"></i></a>
				</div>
				<ul>
					<li><a href="">Новости</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Статьи</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Наши проекты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Дилеры</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Документация</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Контакты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Новости</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Статьи</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Наши проекты</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Дилеры</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Документация</a><i class="fas fa-chevron-right"></i></li>
					<li><a href="">Контакты</a><i class="fas fa-chevron-right"></i></li>
				</ul>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="INC_SIDE_MENU_INTERNAL">
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<div class="level-1">
					<div class="capsule">
						<a href="{show_section}"><xsl:value-of select="name"/> </a>
					</div>
				</div>
				<xsl:if test=".//@id = $sel_sec_id">
					<xsl:for-each select="section">
						<div class="level-2"><a href="{show_section}"><xsl:value-of select="name"/></a></div>
						<xsl:if test=".//@id = $sel_sec_id">
							<xsl:for-each select="section">
								<div class="level-3"><a href="{show_section}"><xsl:value-of select="name"/></a></div>
								<xsl:if test=".//@id = $sel_sec_id">
									<xsl:for-each select="section">
										<div class="level-4"><a href="{show_section}"><xsl:value-of select="name"/></a></div>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>




	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


	<xsl:template name="LEFT_COLOUMN"/>
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
			<link rel="stylesheet" href="css/app.css"/>
			<script defer="defer" src="js/font_awesome_all.js"/>
		</head>
		<body>
			<!-- ALL CONTENT BEGIN -->
			<div class="content-container">
				<xsl:call-template name="INC_DESKTOP_HEADER"/>

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

				<xsl:call-template name="BANNERS"/>

				<xsl:call-template name="INC_FOOTER"/>

			</div>
			<!-- ALL CONTENT END -->


			<xsl:call-template name="INC_MOBILE_MENU"/>
			<xsl:call-template name="INC_MOBILE_NAVIGATION"/>


			<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
			<script type="text/javascript" src="js/bootstrap.min.js"/>
			<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
			<script type="text/javascript" src="admin/js/ajax.js"/>
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