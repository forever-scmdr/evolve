<%@page import="ecommander.controllers.BasicServlet"%>
<%@page import="ecommander.pages.ValidationResults.LineMessage"%>
<%@page import="ecommander.pages.ValidationResults.StructureMessage"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %><%
	ArrayList<LineMessage> lineErrors = (ArrayList<LineMessage>)request.getAttribute(BasicServlet.MODEL_ERRORS_NAME);
	ArrayList<StructureMessage> structErrors = (ArrayList<StructureMessage>)request.getAttribute(BasicServlet.PAGES_ERRORS_NAME);
	String e = (String)request.getAttribute(BasicServlet.EXCEPTION_NAME);
%><!DOCTYPE html>
<html lang="ru">
   <head>
      <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8">
<!--
				/page_404
-->
				
      <base href="http://localhost:8080">
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta name="viewport" content="width=device-width, initial-scale=1"><script defer src="js/font_awesome_all.js"></script><script src="js/jquery-3.5.1.min.js"></script><script src="js/fotorama.js"></script><script src="js/slick.min.js"></script><script src="js/script.js"></script><link rel="canonical" href="http://localhost:8080//page_404">
      <title>404</title>
      <meta name="description" content="">
      <link rel="stylesheet" type="text/css" href="magnific_popup/magnific-popup.css">
      <link rel="stylesheet" href="css/styles.css?version=1.502">
      <link rel="stylesheet" href="css/fixes.css?version=1.0">
      <link href="css/fotorama.css" rel="stylesheet">
      <link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css">
      <link href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"><script type="text/javascript" src="js/nanogallery/jquery.nanogallery2.js"></script></head>
   <body>
      <div class="mitaba">
         <div class="popup" style="display: none;" id="product-ajax-popup">
            <div class="popup__body">
               <div class="popup__content" id="product-ajax-content"><a class="popup__close" onclick="clearProductAjax();">×</a></div>
            </div>
         </div>
         <div class="wrapper">
            <div class="vl_blue">
               <div class="top-info">
                  <div class="container">
                     <div class="top-info__wrap wrap" id="386385" style="display: flex">
                        <div class="top-info__location"><a href="#" class="link icon-link icon-link_after" onclick=""><span>Минск</span></a></div>
                        <div class="top-info__content"><p>+375 29 55-33-000 (Viber, WhatsApp, Telegram)</p>
<p>Адрес салона: ул. Веры Хоружей,6Б ТЦ Зеркало, этаж 2, павильон 16А</p>
<p>Время работы: с 10.00 до 20.00</p></div>
                     </div>
                     <ul class="location-list" style="display:none">
                        <li><a href="#" onclick="return showCityHeader('386385', 'Минск')">Минск</a></li>
                     </ul><script>
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
				</script></div>
               </div>
               <div class="header">
                  <div class="container">
                     <div class="header__wrap wrap"><a href="http://localhost:8080" class="header__column logo"><img src="img/logo_baaz.png" alt="" class="logo__image"></a><div class="header__column header__search header-search">
                           <form action="search/" method="post"><input class="input header-search__input" type="text" placeholder="Введите поисковый запрос" autocomplete="off" name="q" value="" autofocus id="q-ipt"><button class="button header-search__button" type="submit">Найти</button><div id="search-result"></div>
                           </form>
                        </div>
                        <div class="header__column header__column_links">
                           <div class="cart" id="cart_ajax" ajax-href="cart_ajax/" ajax-show-loader="no"><a href="cart/" class="icon-link">
                                 <div class="icon"><img src="img/icon-cart.svg" alt=""></div><span class="icon-link__item">Загрузка...</span></a></div>
                           <div class="links"><a href="/kontakty" class="icon-link">
                                 <div class="icon"><img src="img/icon-phone.svg" alt=""></div></a><a href="javascript:showMobileMainMenu()" class="icon-link">
                                 <div class="icon"><img src="img/icon-bars.svg" alt=""></div></a></div>
                           <div class="user">
                              <div id="personal_desktop" ajax-href="personal_ajax/" ajax-show-loader="no"><a href="register/?login=true" class="icon-link">
                                    <div class="icon"><img src="img/icon-lock.svg" alt=""></div><span class="icon-link__item">Вход / Регистрация</span></a></div>
                              <div id="fav_ajax" ajax-href="fav_ajax/" ajax-show-loader="no"><a class="icon-link">
                                    <div class="icon"><img src="img/icon-star.svg" alt=""></div><span class="icon-link__item">Избранное</span></a></div>
                              <div id="compare_ajax" ajax-href="compare_ajax/" ajax-show-loader="no"><a class="icon-link">
                                    <div class="icon"><img src="img/icon-balance.svg" alt=""></div><span class="icon-link__item">Сравнение</span></a></div>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="main-menu">
                  <div class="container">
                     <div class="main-menu__wrap wrap">
                        <div class="main-menu__item"><a href="catalog/" class="icon-link " id="catalog_main_menu">
                              <div class="icon"><img src="img/icon-bars.svg" alt=""></div><span>Каталог</span></a><div class="popup-catalog-menu" style="position: absolute; display: none" id="cat_menu">
                              <div class="sections"><a href="/razdel_0/" rel="#sub_392926">Автомобильный транспорт</a><a href="/geleznodorognyi_transport/">Железнодорожный транспорт</a><a href="/selskohozyaistvennaya_tehnika/">Сельскохозяйственная техника</a><a href="/instrument_i_oborudovanie/">Инструмент и оборудование</a><a href="/nelikvidy/">Неликвиды</a></div>
                              <div class="subsections" style="display: none" id="sub_392926"><a href="/razdel_0/sistema_podveski/">Система подвески</a><a href="/razdel_0/sistema_rulevogo_upravleniya/">Система рулевого управления</a><a href="/razdel_0/tormoznaya_sistema/">Тормозная система</a><a href="/razdel_0/sistema_stsepleniya/">Система сцепления</a><a href="/razdel_0/dvigatel_i_transmissiya/">Двигатель и трансмиссия</a><a href="/razdel_0/sistema_elektrooborudovaniya/">Система электрооборудования</a><a href="/razdel_0/kuzov_i_oborudovanie/">Кузов и оборудование</a><a href="/razdel_0/shlangi/">Шланги</a><a href="/razdel_0/krepyog/">Крепёж</a></div>
                              <div class="subsections" style="display: none" id="sub_392959"></div>
                              <div class="subsections" style="display: none" id="sub_392960"></div>
                              <div class="subsections" style="display: none" id="sub_392961"></div>
                              <div class="subsections" style="display: none" id="sub_392962"></div>
                           </div>
                        </div>
                        <div class="main-menu__item "><a href="/novosti/"><span>Новости</span></a></div>
                        <div class="main-menu__item "><a href="/oplata/" class=""><span>Оплата</span></a></div>
                        <div class="main-menu__item" style="position: relative;"><a href="#ts_386375" class="show-sub"><span>Услуги</span></a><div id="ts_386375" class="popup-text-menu" style="position: absolute; z-index: 2; display: none;">
                              <div class="sections"><a href="/montag/">Монтаж</a><a href="/remont/">Ремонт</a></div>
                           </div>
                        </div>
                        <div class="main-menu__item "><a href="/kak_zakazat/" class=""><span>Как заказать</span></a></div>
                        <div class="main-menu__item "><a href="/o_nas/" class=""><span>О нас</span></a></div>
                        <div class="main-menu__item "><a href="/kontakty/" class=""><span>Контакты</span></a></div>
                        <div class="main-menu__item" style="position: relative;"><a href="#ts_387030" class="show-sub"><span>Тест</span></a><div id="ts_387030" class="popup-text-menu" style="position: absolute; z-index: 2; display: none;">
                              <div class="sections"><a href="/test_1/">тест 1</a><a href="/test_2/">тест 2</a><a href="/test_3387034/">тест 3</a><a href="/test_4/">тест 4</a><a href="/test_5/">тест 5</a><a href="/test_6/">тест 6</a><a href="/test_7/">тест 7</a><a href="/test_8/">тест 8</a><a href="/test_9/">тест 9</a><a href="/test_10/">тест 10</a><a href="/test_11/">тест 11</a><a href="/test_12/">тест 12</a></div>
                           </div>
                        </div>
                        <div class="main-menu__item"><a href="https://www.onliner.by/">ССЫЛКА</a></div>
                     </div>
                  </div>
               </div>
            </div>
            <div class="content">
               <div class="container">
                  <div class="content__wrap">
                     <div class="content__side">
                        <div class="side-menu"></div><div class="side-banner">
<div class="side-banner__image"><img src="img/side-banner-01.png" alt="" /></div>
<div class="side-banner__link">&nbsp;</div>
</div>
<div class="side-links">
<div class="title title_3">Акции</div>
<div class="side-links__list"><a class="side-links__link" href="#">Акция месяца</a> <a class="side-links__link" href="#">Акция для новоселов</a> <a class="side-links__link" href="#">При заказе на сумму от 100 рублей скидка на карнизы 20%!</a> <a class="side-links__link" href="#">При заказе на сумму от 500 рублей выезд дизайнера в подарок!</a></div>
</div>
<div class="side-banner">
<div class="side-banner__image"><img src="img/side-banner-02.png" alt="" /></div>
<div class="side-banner__link">&nbsp;</div>
</div>
<div class="side-info">
<div class="title title_3">Заказ и консультация</div>
<p>+375 (29) 55-33-000</p>
<p>+375(29) 55-44-000</p>
<p>email: <a href="mailto:zakaz@texform.by">zakaz@texform.by</a></p>
</div>
<div class="side-button"><button class="button">Форма обратной связи</button></div>
<div class="side-info">
<p><a href="contacts.html">Схема проезда к салону</a></p>
</div>
                     </div>
                     <div class="content__main">
                        <div class="path path_common">
                           <div class="path__item"><a href="http://localhost:8080" class="path__link">Главная страница</a><div class="path__arrow"></div>
                           </div>
                        </div>
                        <div class="title title_1">Страница не найдена</div>
                        <div class="text">
                           <div class="link" onclick="$('#bug_info').toggle()"><span>Отладочная информация</span></div>
                           <div class="content bug-info" id="bug_info" style="display: block;"><%
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
		<% } %></div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
            <div class="footer vl_blue2">
               <div class="container">
                  <div class="footer__wrap">
                     <div class="footer__column">
                        <div class="footer__title">ООО «ТЕКСФОРМ», 2020</div><a href="http://forever.by" class="forever"><img src="img/forever.png" alt=""><span>Разработка сайта <br>студия веб-дизайна Forever</span></a><div class="google-rating">
                           <div class="google-rating__stars"><img src="img/icon-google-rating.png" alt=""></div>
                           <div class="google-rating__text">
                              								Наш рейтинг: 4,8 (188 голосов)<br> на основе <a href="https://google.com">отзывов</a> Google
                              							
                           </div>
                        </div>
                     </div>
                     <div class="footer__column">
                        <div class="footer__title">Полезная информация</div>
                        <div class="footer__text"><ul class="footer__list">
<li><a>Виды тканей</a></li>
<li><a>Виды швейных изделий</a></li>
<li><a>Советы по выбору</a></li>
<li><a>Символы по уходу</a></li>
<li><a>Инструкции</a></li>
</ul></div>
                     </div>
                     <div class="footer__column">
                        <div class="footer__title">Заказ и консультация</div>
                        <div class="footer__text"><p>+375 (29) 55-33-0000</p>
<p>+375 (29) 55-44-0000</p>
<p>email: <a href="mailto:zakaz@texform.by">zakaz@texform.by</a></p></div>
                     </div>
                     <div class="footer__column">
                        <div class="footer__title">Адрес и время работы</div>
                        <div class="footer__text"><p>г. Минск, ул. Веры Хоружей, 6Б<br />ТЦ Зеркало, этаж 2, секция Галерея, павильон 16А</p>
<p>Пн-Вс: с 10.00 до 20.00<br />без выходных</p></div>
                     </div>
                     <div class="footer__column">
                        <div class="footer__title">Прочие сведения</div>
                        <div class="footer__text"><p>Зарегистрировано в Реестре бытовых услуг РБ от 24.01.2020 № 83640</p>
<p>Принимаем заказы из всех городов Беларуси</p></div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
         <div class="menu-container mobile">
            <div class="menu-overlay" onclick="showMobileMainMenu()"></div>
            <div class="menu-content">
               <ul>
                  <li><a href="#" class="icon-link">
                        <div class="icon"><img src="img/icon-lock.svg" alt=""></div><span class="icon-link__item">Вход / регистрация</span></a></li>
               </ul>
               <ul>
                  <li><a href="#" onclick="showMobileCatalogMenu(); return false" class="icon-link">
                        <div class="icon"><img src="img/icon-cart.svg" alt=""></div><span class="icon-link__item">Каталог продукции</span></a></li>
               </ul>
               <ul>
                  <li><a href="cart/" class="icon-link">
                        <div class="icon"><img src="img/icon-cart.svg" alt=""></div><span class="icon-link__item">Корзина</span></a></li>
                  <li><a href="fav/" class="icon-link">
                        <div class="icon"><img src="img/icon-star.svg" alt=""></div><span class="icon-link__item">Избранное</span></a></li>
                  <li><a href="compare/" class="icon-link">
                        <div class="icon"><img src="img/icon-balance.svg" alt=""></div><span class="icon-link__item">Сравнение</span></a></li>
               </ul>
               <ul>
                  <li><a href="/novosti/">Новости</a></li>
                  <li><a href="/oplata/">Оплата</a></li>
                  <li><a href="/uslugi/">Услуги</a></li>
                  <li><a href="/kak_zakazat/">Как заказать</a></li>
                  <li><a href="/o_nas/">О нас</a></li>
                  <li><a href="/kontakty/">Контакты</a></li>
                  <li><a href="/testovaya_stranitsa/">Тест</a></li>
                  <li><a href="/ssylka_https___www_onliner_by_/"></a></li>
                  <li><a href="https://www.onliner.by/">ССЫЛКА</a></li>
               </ul>
               <ul>
                  <li class="catalog-currency"><i class="far fa-money-bill-alt"></i>&nbsp;<strong>Валюта</strong>&nbsp;
                     							
                     <ul class="currency-options">
                        <li class="active">BYN</li>
                        <li class=""><a href="page_404/?cur=RUB">RUB</a></li>
                        <li class=""><a href="page_404/?cur=USD">USD</a></li>
                        <li class=""><a href="page_404/?cur=EUR">EUR</a></li>
                     </ul>
                  </li>
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
               <div class="small-nav"><a class="header">Каталог продукции</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;">×</a></div>
               <ul>
                  <li><a rel="#m_sub_392926">Автомобильный транспорт</a><span>&gt;</span></li>
                  <li><a href="/geleznodorognyi_transport/">Железнодорожный транспорт</a></li>
                  <li><a href="/selskohozyaistvennaya_tehnika/">Сельскохозяйственная техника</a></li>
                  <li><a href="/instrument_i_oborudovanie/">Инструмент и оборудование</a></li>
                  <li><a href="/nelikvidy/">Неликвиды</a></li>
               </ul>
            </div>
            <div class="content next" id="m_sub_392926">
               <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/razdel_0/" class="header">Автомобильный транспорт</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
               <ul>
                  <li><a rel="#m_sub_392927">Система подвески</a><i class="fas fa-chevron-right"></i></li>
                  <li><a rel="#m_sub_392935">Система рулевого управления</a><i class="fas fa-chevron-right"></i></li>
                  <li><a rel="#m_sub_392947">Тормозная система</a><i class="fas fa-chevron-right"></i></li>
                  <li><a rel="#m_sub_392951">Система сцепления</a><i class="fas fa-chevron-right"></i></li>
                  <li><a href="/razdel_0/dvigatel_i_transmissiya/">Двигатель и трансмиссия</a></li>
                  <li><a href="/razdel_0/sistema_elektrooborudovaniya/">Система электрооборудования</a></li>
                  <li><a href="/razdel_0/kuzov_i_oborudovanie/">Кузов и оборудование</a></li>
                  <li><a href="/razdel_0/shlangi/">Шланги</a></li>
                  <li><a href="/razdel_0/krepyog/">Крепёж</a></li>
               </ul>
            </div>
            <div class="content next" id="m_sub_392927">
               <div class="small-nav"><a href="" class="back" rel="#m_sub_392926"><i class="fas fa-chevron-left"></i></a><a href="/razdel_0/sistema_podveski/" class="header">Система подвески</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
               <ul>
                  <li><a href="/razdel_0/sistema_podveski/amortizatory_gidravlicheskie/">Амортизаторы гидравлические</a></li>
                  <li><a href="/razdel_0/sistema_podveski/shtangi_reaktivnye/">Штанги реактивные</a></li>
                  <li><a href="/razdel_0/sistema_podveski/shkvorni_i_vtulki/">Шкворни и втулки</a></li>
                  <li><a href="/razdel_0/sistema_podveski/remkomplekty_shkvornevye/">Ремкомплекты шкворневые</a></li>
                  <li><a href="/razdel_0/sistema_podveski/stremyanki_ressor/">Стремянки рессор</a></li>
                  <li><a href="/razdel_0/sistema_podveski/paltsy_ushek_ressor/">Пальцы ушек рессор</a></li>
                  <li><a href="/razdel_0/sistema_podveski/paltsy_shtang/">Пальцы штанг</a></li>
               </ul>
            </div>
            <div class="content next" id="m_sub_392935">
               <div class="small-nav"><a href="" class="back" rel="#m_sub_392926"><i class="fas fa-chevron-left"></i></a><a href="/razdel_0/sistema_rulevogo_upravleniya/" class="header">Система рулевого управления</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
               <ul>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/mehanizmy_rulevye_s_raspredelitelem/">Механизмы рулевые с распределителем</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/razdel_0392938/">Насосы гидравлического усилителя</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/valy_kardannye_rulevogo_upravleniya/">Валы карданные рулевого управления</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/sharniry_kardannye_rulevogo_upravleniya/">Шарниры карданные рулевого управления</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/tyagi_rulevye/">Тяги рулевые</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/nakonechniki_rulevyh_tyag/">Наконечники рулевых тяг</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/paltsy_sharovye/">Пальцы шаровые</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/suhari_i_vkladyshi/">Сухари и вкладыши</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/nabory_zapasnyh_chastei_tyag_rulevyh/">Наборы запасных частей тяг рулевых</a></li>
                  <li><a href="/razdel_0/sistema_rulevogo_upravleniya/soshki/">Сошки</a></li>
               </ul>
            </div>
            <div class="content next" id="m_sub_392947">
               <div class="small-nav"><a href="" class="back" rel="#m_sub_392926"><i class="fas fa-chevron-left"></i></a><a href="/razdel_0/tormoznaya_sistema/" class="header">Тормозная система</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
               <ul>
                  <li><a href="/razdel_0/tormoznaya_sistema/vlagomaslootdelitel/">Влагомаслоотделитель</a></li>
                  <li><a href="/razdel_0/tormoznaya_sistema/stakan_opora/">Стакан, опора</a></li>
                  <li><a href="/razdel_0/tormoznaya_sistema/supporty_tormoznye/">Суппорты тормозные</a></li>
               </ul>
            </div>
            <div class="content next" id="m_sub_392951">
               <div class="small-nav"><a href="" class="back" rel="#m_sub_392926"><i class="fas fa-chevron-left"></i></a><a href="/razdel_0/sistema_stsepleniya/" class="header">Система сцепления</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
               <ul>
                  <li><a href="/razdel_0/sistema_stsepleniya/tsilindry_podpedalnye/">Цилиндры подпедальные</a></li>
                  <li><a href="/razdel_0/sistema_stsepleniya/klapany_stsepleniya/">Клапаны сцепления</a></li>
               </ul>
            </div>
         </div>
         <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="modal-my_price" style="display:none">
            		+++
            		
         </div>
         <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="modal-one_click"></div>
         <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="modal-subscribe"></div>
         <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="cheaper" show-loader="yes">
            <div class="modal-dialog" role="document">
               <div class="modal-content">
                  <div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button><div class="modal-title h4">Нашли этот же инструмент дешевле? Мы сделаем скидку!</div>
                  </div>
                  <div class="modal-body"></div>
               </div>
            </div>
         </div><script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script><script type="text/javascript" src="admin/ajax/ajax.js"></script><script type="text/javascript" src="admin/js/jquery.form.min.js"></script><script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"></script><script type="text/javascript" src="js/bootstrap.min.js"></script><script type="text/javascript" src="js/web.js"></script><script type="text/javascript">
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
						
						var val = $el.val();
						if(val.length > 2){
							
								var $form = $("<form>",
							
								{'method' : 'post', 'action' : 'search_ajax/', 'id' : 'tmp-form'}
							);
							
								var $ipt2 = $("<input>",
							
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
					function showDetails(link){
						$("#product-ajax-popup").show();
						insertAjax(link, 'product-ajax-content', function(){
							$("#fotorama-ajax").fotorama();
							$("#product-ajax-popup").find('a[data-toggle="tab"]').on('click', function(e){
								e.preventDefault();
								$("#product-ajax-popup").find('a[data-toggle="tab"]').removeClass("tabs__link_active");
								$("#product-ajax-popup").find('.tabs__content').removeClass("active");
								$("#product-ajax-popup").find('.tabs__content').hide();
								$(this).addClass("tabs__link_active");
								var href = $(this).attr("href");
								$(href).show();
								$(href).addClass("active");
							});
						});
					}
				</script><!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-151018102-3"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'UA-151018102-3');
</script>
<!-- Yandex.Metrika counter -->
<script type="text/javascript" >
   (function(m,e,t,r,i,k,a){m[i]=m[i]||function(){(m[i].a=m[i].a||[]).push(arguments)};
   m[i].l=1*new Date();k=e.createElement(t),a=e.getElementsByTagName(t)[0],k.async=1,k.src=r,a.parentNode.insertBefore(k,a)})
   (window, document, "script", "https://mc.yandex.ru/metrika/tag.js", "ym");

   ym(56638765, "init", {
        clickmap:true,
        trackLinks:true,
        accurateTrackBounce:true,
        webvisor:true,
        ecommerce:"dataLayer"
   });
</script>
<noscript><div><img src="https://mc.yandex.ru/watch/56638765" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
<!-- /Yandex.Metrika counter --><div class="popup" style="display: none;" id="modal_popup"> +++ </div>
      </div>
   </body>
</html>