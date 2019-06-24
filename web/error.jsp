<%@page import="ecommander.pages.ValidationResults.LineMessage"%>
<%@page import="ecommander.pages.ValidationResults.StructureMessage"%>
<%@page import="ecommander.controllers.BasicServlet"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>



	<html xmlns:f="f:f" lang="ru">
   <head>
      <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8">
<!--
				/index/
-->

      <base href="http://alimarket.must.by">
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <link rel="canonical" href="http://alimarket.must.by">
      <title>Даджет</title>
      <meta name="description" content="">
      <meta name="keywords" content=""><script type="application/ld+json">
			{
				"@context":"http://schema.org",
				"@type":"Organization",
				"url":"http://alimarket.must.by/",
				"name":"",
				"logo":"http://alimarket.must.by/img/logo_big.svg",
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

				]

			}
		</script><link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,700,900&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet">
      <link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet">
      <link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet">
      <link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css">
      <link rel="stylesheet" href="css/app.css">
      <link rel="stylesheet" type="text/css" href="css/tmp_fix.css">
      <link rel="stylesheet" type="text/css" href="slick/slick.css">
      <link rel="stylesheet" type="text/css" href="slick/slick-theme.css">
      <link rel="stylesheet" href="fotorama/fotorama.css">
      <link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"><script defer src="js/font_awesome_all.js"></script><script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"></script></head>
   <body class="index">



<%
	ArrayList<LineMessage> lineErrors = (ArrayList<LineMessage>)request.getAttribute(BasicServlet.MODEL_ERRORS_NAME);
	ArrayList<StructureMessage> structErrors = (ArrayList<StructureMessage>)request.getAttribute(BasicServlet.PAGES_ERRORS_NAME);
	String e = (String)request.getAttribute(BasicServlet.EXCEPTION_NAME);
%>


<div class="content-container">
	 <section class="top-stripe desktop">
			<div class="container"><div class="top-stripe__phone"><img src="img/velcom_logo.svg" />(+375 44) 567-49-26 (ТЦ Столица);</div>
<div class="top-stripe__phone"><img src="img/velcom_logo.svg" />(+375 29) 171-71-72 (ТЦ Титан);</div>
<div class="top-stripe__address">Время работы: пн. - пт. с 9 до 18;</div></div>
	 </section>
	 <section class="header desktop">
			<div class="container"><a href="http://alimarket.must.by" class="logo"><img src="img/logo.svg" alt=""></a><form action="search/" method="post" class="header__search header__column"><input type="text" class="text-input header__field" name="q" value=""><input type="submit" class="button header__button" value="Поиск"></form>
				 <div class="cart-info header__column" id="cart_ajax" ajax-href="cart_ajax/" ajax-show-loader="no"><a href=""><i class="fas fa-shopping-cart"></i>Корзина</a></div>
				 <div class="user-links header__column">
						<div id="personal_desktop" ajax-href="personal_ajax/"><i class="fas fa-lock"></i><a href="register/?login=true"> Вход / Регистрация</a></div>
						<div id="fav_ajax" ajax-href="fav_ajax/"><a href=""><i class="fas fa-star"></i>Избранное</a></div>
						<div id="compare_ajax" ajax-href="compare_ajax/"><a href="compare.html"><i class="fas fa-balance-scale"></i>Сравнение</a></div>
				 </div>
				 <div class="main-menu">
						<div class="main-menu__item main-menu__special" style="position: relative;"><a href="catalog/" class="" id="catalog_main_menu"><span><i class="fas fa-bars"></i> Каталог</span></a><div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
									<div class="sections"><a href="/detyam/" class="cat_menu_item_1">Детям</a><a href="/zdorovaya_gizn/" class="cat_menu_item_1">Здоровая жизнь</a><a href="/dlya_doma_i_dachi/" class="cat_menu_item_1">Для дома и дачи</a><a href="/bezopasnost/" class="cat_menu_item_1">Безопасность</a><a href="/avto___moto/" class="cat_menu_item_1">Авто-мото</a><a href="/audio_video/" class="cat_menu_item_1">Аудио-видео</a><a href="/istochniki_pitaniya/" class="cat_menu_item_1">Источники питания</a><a href="/puteshestviya/" class="cat_menu_item_1">Путешествия</a><a href="/sdelai_sam/" class="cat_menu_item_1">Сделай сам</a></div>
									<div class="subsections" style="display: none" id="sub_369285"></div>
									<div class="subsections" style="display: none" id="sub_369286"></div>
									<div class="subsections" style="display: none" id="sub_369287"></div>
									<div class="subsections" style="display: none" id="sub_369288"></div>
									<div class="subsections" style="display: none" id="sub_369289"></div>
									<div class="subsections" style="display: none" id="sub_369290"></div>
									<div class="subsections" style="display: none" id="sub_369291"></div>
									<div class="subsections" style="display: none" id="sub_369292"></div>
									<div class="subsections" style="display: none" id="sub_369293"></div>
							 </div>
						</div>
						<div class="main-menu__item"><a href="/novosti/" class=""><span>Новости</span></a></div>
						<div class="main-menu__item"><a href="/dostavka/" class="">Доставка</a></div>
						<div class="main-menu__item"><a href="/oplata/" class="">Оплата</a></div>
						<div class="main-menu__item"><a href="/adresa_magazinov/" class="">Адреса магазинов</a></div>
						<div class="main-menu__item"><a href="/gde_moi_zakaz/" class="">Где мой заказ</a></div>
						<div class="main-menu__item"><a href="/optovym_klientam/" class="">Оптовым клиентам</a></div>
						<div class="main-menu__item"><a href="contacts/"><span>Контакты</span></a></div>
				 </div>
			</div>
	 </section>
	 <div class="container">
			<div class="row">
				 <div class="col-md-12 col-xs-12 main-content">
						<div class="mc-container">
							 <div class="header mobile">
									<div class="header-container"><a href="http://alimarket.must.by" class="logo"><img src="img/logo.svg" alt="На главную страницу" style="height: 1.5em; max-width: 100%;"></a><div class="icons-container"><a href="contacts/"><i class="fas fa-phone"></i></a><a href="cart/"><i class="fas fa-shopping-cart"></i></a><a href="javascript:showMobileMainMenu()"><i class="fas fa-bars"></i></a></div>
										 <div class="search-container">
												<form action="search/" method="post"><input type="text" placeholder="Введите поисковый запрос" name="q" value=""></form>
										 </div>
									</div>
							 </div><script>
function showMobileMainMenu() {
$('.content-container').toggleClass('visible-no');
$('.menu-container').toggleClass('visible-yes');
}
</script></div>
				 </div>
			</div>
	 </div>
	 <section>
			<div class="container pt pb">
				<h1 class="page-title">404 - Страница не найдена</h1>
				<p class="text">Возможно, вы ошиблись, набирая адрес страницы или перешли по ссылке, которая устарела. Попробуйте начать с <a href="/">главной страницы</a>.</p>
				<div id="fform" class="bug-report-form"><!-- ВСТАВЛЯЕТСЯ ИЗ центра поддрежки --></div>


				<div class="link" onclick="$('.content').toggle()">
					<span style="color: gray; border-bottom: 1px dotted gray; display: inline-block;">Отладочная информация</span>
				</div>
				<div class="content bug-info" style="display: block;">
					<%
						if (lineErrors != null && lineErrors.size() > 0) {
					%>
					<h2>Информационная модель сайта</h2>
					<table class="structure">
						<%
							for (LineMessage error : lineErrors) {
						%>
						<tr>
							<td class="side"><%=error.lineNumber%>:</td>
							<td class="info"><%=error.message%></td>
						</tr>
						<%
							}
						%>
					</table>
					<%
						}
						if (structErrors != null && structErrors.size() > 0) {
					%>
					<h2>Страницы сайта</h2>
					<table class="pages">
						<%
							for (StructureMessage error : structErrors) {
						%>
						<tr>
							<td class="side"><%=error.originator%></td>
							<td class="info"><%=error.message%></td>
						</tr>
						<%
							}
						%>
					</table>
					<%
						}
						if (e != null) {
					%>
					<table class="exeption">
						<h2>Exception</h2>
						<p>
						<pre><%=e %></pre>
						</p>
					</table>
					<% } %>
				</div>
			</div>
	 </section>
	 <div class="footer-placeholder"></div>
	 <footer class="footer">
			<div class="container">
				 <div class="footer__column">
						<div class="title_3">© alimarket.by, 2018</div><div class="forever"><a href="http://forever.by" target="_blank">Разработка сайта студия веб-дизайна Forever</a></div>
<div class="rating">
<div class="stars"><img src="img/star.svg" alt="" /> <img src="img/star.svg" alt="" /> <img src="img/star.svg" alt="" /> <img src="img/star.svg" alt="" /> <img src="img/star-no.svg" alt="" /></div>
<p>Наш рейтинг: 4,2 (188 голосов) <br /> на основе <a style="display: inline;" href="https://www.google.com">отзывов</a> Google</p>
</div>
				 </div>
				 <div class="footer__column">
						<div class="title_3">Заказ и констультация</div><p>+375 (44) 567-49-26 (ТЦ Столица)<br />+375 (29) 171-71-72 (ТЦ Титан)</p>
<p>email: <a href="mailto:info@alimarket.by">info@alimarket.by</a></p>
				 </div>
				 <div class="footer__column">
						<div class="title_3">Контакты</div><p>Республика Беларусь,&nbsp;г. Минск, ул. Солнечная, д.1</p>
<p>email:&nbsp;<a href="mailto:info@alimarket.by">info@alimarket.by</a></p>
<p><strong>Режим работы</strong></p>
<p>Пн.-пт.: с 9:00 до 20:00</p>
				 </div>
				 <div class="footer__column"><p>Работаем с физическими, юридическими лицами и индивидуальными предпринимателями по наличному и безналичному расчёту</p></div>
			</div>
	 </footer>
	 <div class="modal fade" tabindex="-1" role="dialog" id="modal-login">
			<div class="modal-dialog modal-sm" role="document">
				 <div class="modal-content">
						<div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button><div class="modal-title h4">Вход</div>
						</div>
						<div class="modal-body">
							 <form action="" method="post">
									<div class="form-group"><label for="">Электронная почта:</label><input type="text" class="form-control"></div>
									<div class="form-group"><label for="">Пароль:</label><input type="password" class="form-control"></div><input type="submit" name="" value="Отправить заказ"></form>
						</div>
				 </div>
			</div>
	 </div>
	 <div xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="modal-feedback" ajax-href="feedback_ajax/" show-loader="yes">
					+++

	 </div>
</div>
<div class="menu-container mobile">
	 <div class="overlay" onclick="showMobileMainMenu()"></div>
	 <div class="content">
			<ul>
				 <li>
						<div id="personal_mobile"><i class="fas fa-lock"></i><a href="register/?login=true"> Вход / Регистрация</a></div>
				 </li>
			</ul>
			<ul>
				 <li><i class="fas fa-th-list"></i><a href="#" onclick="showMobileCatalogMenu(); return false">Каталог продукции</a></li>
			</ul>
			<ul>
				 <li><i class="fas fa-shopping-cart"></i><a href="cart/" rel="nofolow">Заявки</a></li>
				 <li><i class="fas fa-star"></i><a href="fav/">Избранное</a></li>
				 <li><i class="fas fa-balance-scale"></i><a href="compare/">Сравнение</a></li>
			</ul>
			<ul>
				 <li><a href="/novosti/">Новости</a></li>
				 <li><a href="/dostavka/">Доставка</a></li>
				 <li><a href="/oplata/">Оплата</a></li>
				 <li><a href="/adresa_magazinov/">Адреса магазинов</a></li>
				 <li><a href="/gde_moi_zakaz/">Где мой заказ</a></li>
				 <li><a href="/optovym_klientam/">Оптовым клиентам</a></li>
				 <li><a href="contacts/">Контакты</a></li>
			</ul>
	 </div>
</div><script>
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
</script><div id="mobile_catalog_menu" class="nav-container mobile" style="display: none; position:absolute; width: 100%; overflow:hidden">
	 <div class="content" id="m_sub_cat">
			<div class="small-nav"><a class="header">Каталог продукции</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
			<ul>
				 <li><a href="/detyam/">Детям</a></li>
				 <li><a href="/zdorovaya_gizn/">Здоровая жизнь</a></li>
				 <li><a href="/dlya_doma_i_dachi/">Для дома и дачи</a></li>
				 <li><a href="/bezopasnost/">Безопасность</a></li>
				 <li><a href="/avto___moto/">Авто-мото</a></li>
				 <li><a href="/audio_video/">Аудио-видео</a></li>
				 <li><a href="/istochniki_pitaniya/">Источники питания</a></li>
				 <li><a href="/puteshestviya/">Путешествия</a></li>
				 <li><a href="/sdelai_sam/">Сделай сам</a></li>
			</ul>
	 </div>
</div><script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script><script type="text/javascript" src="js/bootstrap.js"></script><script type="text/javascript" src="admin/ajax/ajax.js"></script><script type="text/javascript" src="admin/js/jquery.form.min.js"></script><script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"></script><script type="text/javascript" src="js/fwk/common.js"></script><script type="text/javascript" src="slick/slick.min.js"></script><script type="text/javascript">
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

		initCatalogPopupMenu('#catalog_main_menu', '.popup-catalog-menu');
		initCatalogPopupSubmenu('.sections', '.sections a', '.subsections');
		});

		$(window).resize(function(){
		var oh = $(".footer").outerHeight();
		$(".footer-placeholder").height(oh+40);
		$(".footer").css("margin-top", -1*oh);
		});
	</script><script type="text/javascript" src="fotorama/fotorama.js"></script></body>






</html>
