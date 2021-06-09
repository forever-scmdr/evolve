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
      <link rel="stylesheet" href="css/styles.css?version=1.51">
      <link rel="stylesheet" href="css/fixes.css?version=1.0">
      <link href="css/fotorama.css" rel="stylesheet">
      <link rel="stylesheet" href="js/nanogallery/css/nanogallery2.woff.min.css">
      <link href="js/nanogallery/css/nanogallery2.min.css" rel="stylesheet" type="text/css"><script type="text/javascript" src="js/nanogallery/jquery.nanogallery2.js"></script></head>
   <body>
      <div class="popup" style="display: none;" id="product-ajax-popup">
         <div class="popup__body">
            <div class="popup__content" id="product-ajax-content"><a class="popup__close" onclick="clearProductAjax();">×</a></div>
         </div>
      </div>
      <div class="wrapper">
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
               <div class="header__wrap wrap"><a href="http://localhost:8080" class="header__column logo"><img src="img/logo.png" alt="" class="logo__image"></a><div class="header__column header__search header-search">
                     <form action="search/" method="post"><input class="input header-search__input" type="text" placeholder="Введите поисковый запрос" autocomplete="off" name="q" value="" autofocus id="q-ipt"><button class="button header-search__button" type="submit">Найти</button></form>
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
                        <div class="sections"><a href="/shtory/" rel="#sub_386337">Шторы</a><a href="/lambrekeny/" rel="#sub_386362">Ламбрекены</a><a href="/baldahiny/" rel="#sub_386365">Балдахины</a><a href="/podhvaty/" rel="#sub_386370">Подхваты</a><a href="/postelnoe-belyo/" rel="#sub_386366">Постельное белье</a><a href="/odeyala/" rel="#sub_387144">Одеяла</a><a href="/pokryvala/" rel="#sub_386363">Покрывала</a><a href="/podushki/" rel="#sub_386364">Подушки</a><a href="/myagkaya-mebel/" rel="#sub_387166">Мягкая мебель</a><a href="/chehly-na-mebel/" rel="#sub_386367">Чехлы на мебель</a><a href="/stolovoe-belyo/" rel="#sub_386368">Столовое белье</a><a href="/detskij-tekstil/" rel="#sub_386369">Детский текстиль</a><a href="/podarochnyj-tekstil/" rel="#sub_387198">Подарочный текстиль</a><a href="/abazhury/" rel="#sub_387204">Абажуры</a><a href="/tkani/" rel="#sub_386371">Ткани</a><a href="/karnizy/" rel="#sub_386372">Карнизы</a></div>
                        <div class="subsections" style="display: none" id="sub_386337"><a href="/shtory/shtory-na-lente/">Шторы на ленте</a><a href="/shtory/shtory-na-lyuversah/">Шторы на люверсах</a><a href="/shtory/shtory-na-petlyah/">Шторы на петлях</a><a href="/shtory/shtory-na-zavyazkah/">Шторы на завязках</a><a href="/shtory/shtory-na-kulise/">Шторы на кулисе</a><a href="/shtory/shtory-kafe/">Шторы кафе</a><a href="/shtory/shtory-panelnye/">Шторы панельные</a><a href="/shtory/shtory-rimskie/">Шторы римские</a><a href="/shtory/shtory-anglijskie/">Шторы английские</a><a href="/shtory/shtory-londonskie/">Шторы лондонские</a><a href="/shtory/shtory-avstrijskie/">Шторы австрийские</a><a href="/shtory/shtory-francuzskie/">Шторы французские</a><a href="/shtory/shtory-dilizhansnye/">Шторы дилижансные</a><a href="/shtory/shtory-kitajskie/">Шторы китайские</a><a href="/shtory/shtory-rulonnye/">Шторы рулонные</a><a href="/shtory/shtory-plisse/">Шторы плиссе</a><a href="/shtory/shtory-pesochnye-chasy/">Шторы песочные часы</a><a href="/shtory/shtory-parus/">Шторы парус</a><a href="/shtory/shtory-na-kosoe-okno/">Шторы на косое окно</a><a href="/shtory/shtory-dizajnerskie/">Шторы дизайнерские</a><a href="/shtory/shtory-rukav-episkopa/">Шторы рукав епископа</a><a href="/shtory/shtory-kolonna/">Шторы колонна</a></div>
                        <div class="subsections" style="display: none" id="sub_386362"><a href="/lambrekeny/lambrekeny-myagkie/">Ламбрекены мягкие</a><a href="/lambrekeny/lambrekeny-na-bando/">Ламбрекены на бандо</a><a href="/lambrekeny/lambrekeny-azhurnye/">Ламбрекены ажурные</a><a href="/lambrekeny/lambrekeny-kombinirovannye/">Ламбрекены комбинированные</a></div>
                        <div class="subsections" style="display: none" id="sub_386365"><a href="/baldahiny/baldahiny-korona/">Балдахины корона</a><a href="/baldahiny/baldahiny-kozyrek/">Балдахины козырек</a><a href="/baldahiny/baldahiny-palatka/">Балдахины палатка</a><a href="/baldahiny/baldahiny-shater/">Балдахины шатер</a><a href="/baldahiny/baldahiny-perimetr/">Балдахины периметр</a></div>
                        <div class="subsections" style="display: none" id="sub_386370"><a href="/podhvaty/podhvaty-tekstilnye/">Подхваты текстильные</a><a href="/podhvaty/podhvaty-s-kistyami/">Подхваты с кистями</a><a href="/podhvaty/podhvaty-iz-shnura/">Подхваты из шнура</a><a href="/podhvaty/podhvaty-iz-tesmy/">Подхваты из тесьмы</a><a href="/podhvaty/podhvaty-iz-dereva/">Подхваты из дерева</a><a href="/podhvaty/podhvaty-iz-busin/">Подхваты из бусин</a><a href="/podhvaty/podhvaty-azhurnye/">Подхваты ажурные</a><a href="/podhvaty/podhvaty-s-cvetami/">Подхваты с цветами</a><a href="/podhvaty/klipsy-magnitnye/">Клипсы магнитные</a><a href="/podhvaty/zakolki/">Заколки</a></div>
                        <div class="subsections" style="display: none" id="sub_386366"><a href="/postelnoe-belyo/komplekty-postelnogo-belya/">Комплекты постельного белья</a><a href="/postelnoe-belyo/navolochki/">Наволочки</a><a href="/postelnoe-belyo/prostyni/">Простыни</a><a href="/postelnoe-belyo/pododeyalniki/">Пододеяльники</a><a href="/postelnoe-belyo/namatrasniki/">Наматрасники</a><a href="/postelnoe-belyo/podmatrasniki/">Подматрасники</a></div>
                        <div class="subsections" style="display: none" id="sub_387144"><a href="/odeyala/odeyala-letnie/">Одеяла летние</a><a href="/odeyala/odeyala-zimnie/">Одеяла зимние</a><a href="/odeyala/odeyala-vsesezonnye/">Одеяла всесезонные</a><a href="/odeyala/meshki-spalnye/">Мешки спальные</a></div>
                        <div class="subsections" style="display: none" id="sub_386363"><a href="/pokryvala/pokryvala-steganye/">Покрывала стеганые</a><a href="/pokryvala/pokryvala-s-bufami/">Покрывала с буфами</a><a href="/pokryvala/pokryvala-s-kantom/">Покрывала с кантом</a><a href="/pokryvala/pokryvala-s-oborkami/">Покрывала с оборками</a><a href="/pokryvala/pokryvala-kombinirovannye/">Покрывала комбинированные</a><a href="/pokryvala/pokryvala-loskutnye/">Покрывала лоскутные</a><a href="/pokryvala/pokryvala-pledy/">Покрывала пледы</a><a href="/pokryvala/pokryvala-chehly/">Покрывала чехлы</a><a href="/pokryvala/pokryvala-sashe/">Покрывала саше</a></div>
                        <div class="subsections" style="display: none" id="sub_386364"><a href="/podushki/podushki-dekorativnye/">Подушки декоративные</a><a href="/podushki/valiki-dekorativnye/">Валики декоративные</a><a href="/podushki/podushki-mebelnye/">Подушки мебельные</a><a href="/podushki/podushki-na-stulya/">Подушки на стулья</a><a href="/podushki/podushki-spalnye/">Подушки спальные</a><a href="/podushki/podushki-ortopedicheskie/">Подушки ортопедические</a><a href="/podushki/podushki-dlya-beremennyh/">Подушки для беременных</a><a href="/podushki/podushki-dorozhnye/">Подушки дорожные</a></div>
                        <div class="subsections" style="display: none" id="sub_387166"><a href="/myagkaya-mebel/karkasnaya-mebel-s-bufami/">Каркасная мебель с буфами</a><a href="/myagkaya-mebel/mebel-so-stegannym-chehlom/">Мебель со стеганым чехлом</a><a href="/myagkaya-mebel/mebel-na-derevyannom-karkase/">Мебель на деревянном каркасе</a><a href="/myagkaya-mebel/dizajnerskie-myagkie-kresla/">Дизайнерские мягкие кресла</a><a href="/myagkaya-mebel/modulnaya-myagkaya-mebel/">Модульная мягкая мебель</a><a href="/myagkaya-mebel/myagkie-izgolovya-krovati/">Мягкие изголовья кровати</a><a href="/myagkaya-mebel/myagkie-stulya/">Мягкие стулья</a></div>
                        <div class="subsections" style="display: none" id="sub_386367"><a href="/chehly-na-mebel/chehly-na-stulya/">Чехлы на стулья</a><a href="/chehly-na-mebel/chehly-na-kresla/">Чехлы на кресла</a><a href="/chehly-na-mebel/chehly-na-pryamoj-divan/">Чехлы на прямой диван</a><a href="/chehly-na-mebel/chehly-na-uglovoj-divan/">Чехлы на угловой диван</a></div>
                        <div class="subsections" style="display: none" id="sub_386368"><a href="/stolovoe-belyo/skaterti/">Скатерти</a><a href="/stolovoe-belyo/naperony/">Напероны</a><a href="/stolovoe-belyo/dorozhki/">Дорожки</a><a href="/stolovoe-belyo/podtarelniki/">Подтарельники</a><a href="/stolovoe-belyo/salfetki/">Салфетки</a><a href="/stolovoe-belyo/kuverty/">Куверты</a><a href="/stolovoe-belyo/ruchniki/">Ручники</a><a href="/stolovoe-belyo/polotenca/">Полотенца</a><a href="/stolovoe-belyo/podskaterniki/">Подскатерники</a><a href="/stolovoe-belyo/furshetnye-yubki/">Фуршетные юбки</a><a href="/stolovoe-belyo/aksessuary-dlya-stolovogo-belya/">Аксессуары для столового белья</a></div>
                        <div class="subsections" style="display: none" id="sub_386369"><a href="/detskij-tekstil/shtory-dlya-detskoj/">Шторы для детской</a><a href="/detskij-tekstil/podhvaty-igrushki/">Подхваты игрушки</a><a href="/detskij-tekstil/baldahiny-dlya-detskoj/">Балдахины для детской</a><a href="/detskij-tekstil/detskoe-postelnoe-belyo/">Детское постельное белье</a><a href="/detskij-tekstil/detskie-odeyala/">Детские одеяла</a><a href="/detskij-tekstil/detskie-pokryvala/">Детские покрывала</a><a href="/detskij-tekstil/detskie-podushki/">Детские подушки</a><a href="/detskij-tekstil/detskaya-myagkaya-mebel/">Детская мягкая мебель</a></div>
                        <div class="subsections" style="display: none" id="sub_387198"><a href="/podarochnyj-tekstil/svadebnoe-postelnoe-belyo/">Свадебное постельное белье</a><a href="/podarochnyj-tekstil/podarochnoe-postelnoe-belyo/">Подарочное постельное белье</a><a href="/podarochnyj-tekstil/podarochnye-pokryvala/">Подарочные покрывала</a><a href="/podarochnyj-tekstil/podarochnye-podushki/">Подарочные подушки</a><a href="/podarochnyj-tekstil/podarochnoe-stolovoe-belyo/">Подарочное столовое белье</a></div>
                        <div class="subsections" style="display: none" id="sub_387204"><a href="/abazhury/abazhury-sovremennye/">Абажуры современные</a><a href="/abazhury/abazhury-klassicheskie/">Абажуры классические</a><a href="/abazhury/abazhury-v-derevenskom-stile/">Абажуры в деревенском стиле</a><a href="/abazhury/abazhury-v-ekostile/">Абажуры в экостиле</a><a href="/abazhury/abazhury-vintazhnye/">Абажуры винтажные</a><a href="/abazhury/abazhury-azhurnye/">Абажуры ажурные</a></div>
                        <div class="subsections" style="display: none" id="sub_386371"><a href="/tkani/tkani-tyulevye/">Ткани тюлевые</a><a href="/tkani/tkani-porternye/">Ткани портьерные</a><a href="/tkani/tkani-blackout/">Ткани блэкаут</a><a href="/tkani/tkani-postelnye/">Ткани постельные</a><a href="/tkani/tkani-pokryvalnye/">Ткани покрывальные</a><a href="/tkani/tkani-mebelnye/">Ткани мебельные</a><a href="/tkani/tkani-skatertnye/">Ткани скатертные</a><a href="/tkani/tkani-podkladochnye/">Ткани подкладочные</a><a href="/tkani/tkani-ulichnye/">Ткани уличные</a><a href="/tkani/tkani_shablony/">Ткани шаблоны</a></div>
                        <div class="subsections" style="display: none" id="sub_386372"><a href="/karnizy/karnizy-profilnye/">Карнизы профильные</a><a href="/karnizy/karnizy-s-upravleniem/">Карнизы с управлением</a><a href="/karnizy/karnizy-dlya-panelnyh-shtor/">Карнизы для панельных штор</a><a href="/karnizy/karnizy-dlya-podemnyh-shtor/">Карнизы для подъемных штор</a><a href="/karnizy/karnizy-trekovye/">Карнизы трековые</a><a href="/karnizy/karnizy-trubchatye/">Карнизы трубчатые</a><a href="/karnizy/karnizy-kovannye/">Карнизы кованные</a><a href="/karnizy/karnizy-kafe/">Карнизы кафе</a><a href="/karnizy/karnizy-derevyannye/">Карнизы деревянные</a><a href="/karnizy/karnizy-metalloplastikovye/">Карнизы металлопластиковые</a><a href="/karnizy/karnizy-shinnye/">Карнизы шинные</a><a href="/karnizy/karnizy-bagetnye/">Карнизы багетные</a><a href="/karnizy/karnizy-prozrachnye/">Карнизы прозрачные</a><a href="/karnizy/elektrokarnizy/">Электрокарнизы</a><a href="/karnizy/aksessuary-dlya-karnizov/">Аксессуары для карнизов</a></div>
                     </div>
                  </div>
                  <div class="main-menu__item "><a href="/oplata/" class=""><span>Оплата</span></a></div>
                  <div class="main-menu__item "><a href="/dostavka/" class=""><span>Доставка</span></a></div>
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
                     <div class="title title_1"></div>
                     <div class="text"><p><strong><span style="font-size: 36px;">Some shit happened.</span></strong></p>
<p><strong><span style="font-size: 36px;">Please, look for some other site...</span></strong></p>
<p>&nbsp;<img src="files/387/531f/text_pic_soloduha_1.jpg" /></p>
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
         <div class="footer">
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
               <li><a href="/oplata/">Оплата</a></li>
               <li><a href="/dostavka/">Доставка</a></li>
               <li><a href="/uslugi/">Услуги</a></li>
               <li><a href="/kak_zakazat/">Как заказать</a></li>
               <li><a href="/o_nas/">О нас</a></li>
               <li><a href="/kontakty/">Контакты</a></li>
               <li><a href="/testovaya_stranitsa/">Тест</a></li>
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
               <li><a rel="#m_sub_386337">Шторы</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386362">Ламбрекены</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386365">Балдахины</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386370">Подхваты</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386366">Постельное белье</a><span>&gt;</span></li>
               <li><a rel="#m_sub_387144">Одеяла</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386363">Покрывала</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386364">Подушки</a><span>&gt;</span></li>
               <li><a rel="#m_sub_387166">Мягкая мебель</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386367">Чехлы на мебель</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386368">Столовое белье</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386369">Детский текстиль</a><span>&gt;</span></li>
               <li><a rel="#m_sub_387198">Подарочный текстиль</a><span>&gt;</span></li>
               <li><a rel="#m_sub_387204">Абажуры</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386371">Ткани</a><span>&gt;</span></li>
               <li><a rel="#m_sub_386372">Карнизы</a><span>&gt;</span></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386337">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/shtory/" class="header">Шторы</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/shtory/shtory-na-lente/">Шторы на ленте</a></li>
               <li><a href="/shtory/shtory-na-lyuversah/">Шторы на люверсах</a></li>
               <li><a href="/shtory/shtory-na-petlyah/">Шторы на петлях</a></li>
               <li><a href="/shtory/shtory-na-zavyazkah/">Шторы на завязках</a></li>
               <li><a href="/shtory/shtory-na-kulise/">Шторы на кулисе</a></li>
               <li><a href="/shtory/shtory-kafe/">Шторы кафе</a></li>
               <li><a href="/shtory/shtory-panelnye/">Шторы панельные</a></li>
               <li><a href="/shtory/shtory-rimskie/">Шторы римские</a></li>
               <li><a href="/shtory/shtory-anglijskie/">Шторы английские</a></li>
               <li><a href="/shtory/shtory-londonskie/">Шторы лондонские</a></li>
               <li><a href="/shtory/shtory-avstrijskie/">Шторы австрийские</a></li>
               <li><a href="/shtory/shtory-francuzskie/">Шторы французские</a></li>
               <li><a href="/shtory/shtory-dilizhansnye/">Шторы дилижансные</a></li>
               <li><a href="/shtory/shtory-kitajskie/">Шторы китайские</a></li>
               <li><a href="/shtory/shtory-rulonnye/">Шторы рулонные</a></li>
               <li><a href="/shtory/shtory-plisse/">Шторы плиссе</a></li>
               <li><a href="/shtory/shtory-pesochnye-chasy/">Шторы песочные часы</a></li>
               <li><a href="/shtory/shtory-parus/">Шторы парус</a></li>
               <li><a href="/shtory/shtory-na-kosoe-okno/">Шторы на косое окно</a></li>
               <li><a href="/shtory/shtory-dizajnerskie/">Шторы дизайнерские</a></li>
               <li><a href="/shtory/shtory-rukav-episkopa/">Шторы рукав епископа</a></li>
               <li><a href="/shtory/shtory-kolonna/">Шторы колонна</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386362">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/lambrekeny/" class="header">Ламбрекены</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/lambrekeny/lambrekeny-myagkie/">Ламбрекены мягкие</a></li>
               <li><a href="/lambrekeny/lambrekeny-na-bando/">Ламбрекены на бандо</a></li>
               <li><a href="/lambrekeny/lambrekeny-azhurnye/">Ламбрекены ажурные</a></li>
               <li><a href="/lambrekeny/lambrekeny-kombinirovannye/">Ламбрекены комбинированные</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386365">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/baldahiny/" class="header">Балдахины</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/baldahiny/baldahiny-korona/">Балдахины корона</a></li>
               <li><a href="/baldahiny/baldahiny-kozyrek/">Балдахины козырек</a></li>
               <li><a href="/baldahiny/baldahiny-palatka/">Балдахины палатка</a></li>
               <li><a href="/baldahiny/baldahiny-shater/">Балдахины шатер</a></li>
               <li><a href="/baldahiny/baldahiny-perimetr/">Балдахины периметр</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386370">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/podhvaty/" class="header">Подхваты</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/podhvaty/podhvaty-tekstilnye/">Подхваты текстильные</a></li>
               <li><a href="/podhvaty/podhvaty-s-kistyami/">Подхваты с кистями</a></li>
               <li><a href="/podhvaty/podhvaty-iz-shnura/">Подхваты из шнура</a></li>
               <li><a href="/podhvaty/podhvaty-iz-tesmy/">Подхваты из тесьмы</a></li>
               <li><a href="/podhvaty/podhvaty-iz-dereva/">Подхваты из дерева</a></li>
               <li><a href="/podhvaty/podhvaty-iz-busin/">Подхваты из бусин</a></li>
               <li><a href="/podhvaty/podhvaty-azhurnye/">Подхваты ажурные</a></li>
               <li><a href="/podhvaty/podhvaty-s-cvetami/">Подхваты с цветами</a></li>
               <li><a href="/podhvaty/klipsy-magnitnye/">Клипсы магнитные</a></li>
               <li><a href="/podhvaty/zakolki/">Заколки</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386366">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/postelnoe-belyo/" class="header">Постельное белье</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/postelnoe-belyo/komplekty-postelnogo-belya/">Комплекты постельного белья</a></li>
               <li><a href="/postelnoe-belyo/navolochki/">Наволочки</a></li>
               <li><a href="/postelnoe-belyo/prostyni/">Простыни</a></li>
               <li><a href="/postelnoe-belyo/pododeyalniki/">Пододеяльники</a></li>
               <li><a href="/postelnoe-belyo/namatrasniki/">Наматрасники</a></li>
               <li><a href="/postelnoe-belyo/podmatrasniki/">Подматрасники</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_387144">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/odeyala/" class="header">Одеяла</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/odeyala/odeyala-letnie/">Одеяла летние</a></li>
               <li><a href="/odeyala/odeyala-zimnie/">Одеяла зимние</a></li>
               <li><a href="/odeyala/odeyala-vsesezonnye/">Одеяла всесезонные</a></li>
               <li><a href="/odeyala/meshki-spalnye/">Мешки спальные</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386363">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/pokryvala/" class="header">Покрывала</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/pokryvala/pokryvala-steganye/">Покрывала стеганые</a></li>
               <li><a href="/pokryvala/pokryvala-s-bufami/">Покрывала с буфами</a></li>
               <li><a href="/pokryvala/pokryvala-s-kantom/">Покрывала с кантом</a></li>
               <li><a href="/pokryvala/pokryvala-s-oborkami/">Покрывала с оборками</a></li>
               <li><a href="/pokryvala/pokryvala-kombinirovannye/">Покрывала комбинированные</a></li>
               <li><a href="/pokryvala/pokryvala-loskutnye/">Покрывала лоскутные</a></li>
               <li><a href="/pokryvala/pokryvala-pledy/">Покрывала пледы</a></li>
               <li><a href="/pokryvala/pokryvala-chehly/">Покрывала чехлы</a></li>
               <li><a href="/pokryvala/pokryvala-sashe/">Покрывала саше</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386364">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/podushki/" class="header">Подушки</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/podushki/podushki-dekorativnye/">Подушки декоративные</a></li>
               <li><a href="/podushki/valiki-dekorativnye/">Валики декоративные</a></li>
               <li><a href="/podushki/podushki-mebelnye/">Подушки мебельные</a></li>
               <li><a href="/podushki/podushki-na-stulya/">Подушки на стулья</a></li>
               <li><a href="/podushki/podushki-spalnye/">Подушки спальные</a></li>
               <li><a href="/podushki/podushki-ortopedicheskie/">Подушки ортопедические</a></li>
               <li><a href="/podushki/podushki-dlya-beremennyh/">Подушки для беременных</a></li>
               <li><a href="/podushki/podushki-dorozhnye/">Подушки дорожные</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_387166">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/myagkaya-mebel/" class="header">Мягкая мебель</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/myagkaya-mebel/karkasnaya-mebel-s-bufami/">Каркасная мебель с буфами</a></li>
               <li><a href="/myagkaya-mebel/mebel-so-stegannym-chehlom/">Мебель со стеганым чехлом</a></li>
               <li><a href="/myagkaya-mebel/mebel-na-derevyannom-karkase/">Мебель на деревянном каркасе</a></li>
               <li><a href="/myagkaya-mebel/dizajnerskie-myagkie-kresla/">Дизайнерские мягкие кресла</a></li>
               <li><a href="/myagkaya-mebel/modulnaya-myagkaya-mebel/">Модульная мягкая мебель</a></li>
               <li><a href="/myagkaya-mebel/myagkie-izgolovya-krovati/">Мягкие изголовья кровати</a></li>
               <li><a href="/myagkaya-mebel/myagkie-stulya/">Мягкие стулья</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386367">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/chehly-na-mebel/" class="header">Чехлы на мебель</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/chehly-na-mebel/chehly-na-stulya/">Чехлы на стулья</a></li>
               <li><a href="/chehly-na-mebel/chehly-na-kresla/">Чехлы на кресла</a></li>
               <li><a href="/chehly-na-mebel/chehly-na-pryamoj-divan/">Чехлы на прямой диван</a></li>
               <li><a href="/chehly-na-mebel/chehly-na-uglovoj-divan/">Чехлы на угловой диван</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386368">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/stolovoe-belyo/" class="header">Столовое белье</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/stolovoe-belyo/skaterti/">Скатерти</a></li>
               <li><a href="/stolovoe-belyo/naperony/">Напероны</a></li>
               <li><a href="/stolovoe-belyo/dorozhki/">Дорожки</a></li>
               <li><a href="/stolovoe-belyo/podtarelniki/">Подтарельники</a></li>
               <li><a href="/stolovoe-belyo/salfetki/">Салфетки</a></li>
               <li><a href="/stolovoe-belyo/kuverty/">Куверты</a></li>
               <li><a href="/stolovoe-belyo/ruchniki/">Ручники</a></li>
               <li><a href="/stolovoe-belyo/polotenca/">Полотенца</a></li>
               <li><a href="/stolovoe-belyo/podskaterniki/">Подскатерники</a></li>
               <li><a href="/stolovoe-belyo/furshetnye-yubki/">Фуршетные юбки</a></li>
               <li><a href="/stolovoe-belyo/aksessuary-dlya-stolovogo-belya/">Аксессуары для столового белья</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386369">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/detskij-tekstil/" class="header">Детский текстиль</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/detskij-tekstil/shtory-dlya-detskoj/">Шторы для детской</a></li>
               <li><a href="/detskij-tekstil/podhvaty-igrushki/">Подхваты игрушки</a></li>
               <li><a href="/detskij-tekstil/baldahiny-dlya-detskoj/">Балдахины для детской</a></li>
               <li><a href="/detskij-tekstil/detskoe-postelnoe-belyo/">Детское постельное белье</a></li>
               <li><a href="/detskij-tekstil/detskie-odeyala/">Детские одеяла</a></li>
               <li><a href="/detskij-tekstil/detskie-pokryvala/">Детские покрывала</a></li>
               <li><a href="/detskij-tekstil/detskie-podushki/">Детские подушки</a></li>
               <li><a href="/detskij-tekstil/detskaya-myagkaya-mebel/">Детская мягкая мебель</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_387198">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/podarochnyj-tekstil/" class="header">Подарочный текстиль</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/podarochnyj-tekstil/svadebnoe-postelnoe-belyo/">Свадебное постельное белье</a></li>
               <li><a href="/podarochnyj-tekstil/podarochnoe-postelnoe-belyo/">Подарочное постельное белье</a></li>
               <li><a href="/podarochnyj-tekstil/podarochnye-pokryvala/">Подарочные покрывала</a></li>
               <li><a href="/podarochnyj-tekstil/podarochnye-podushki/">Подарочные подушки</a></li>
               <li><a href="/podarochnyj-tekstil/podarochnoe-stolovoe-belyo/">Подарочное столовое белье</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_387204">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/abazhury/" class="header">Абажуры</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/abazhury/abazhury-sovremennye/">Абажуры современные</a></li>
               <li><a href="/abazhury/abazhury-klassicheskie/">Абажуры классические</a></li>
               <li><a href="/abazhury/abazhury-v-derevenskom-stile/">Абажуры в деревенском стиле</a></li>
               <li><a href="/abazhury/abazhury-v-ekostile/">Абажуры в экостиле</a></li>
               <li><a href="/abazhury/abazhury-vintazhnye/">Абажуры винтажные</a></li>
               <li><a href="/abazhury/abazhury-azhurnye/">Абажуры ажурные</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386371">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/tkani/" class="header">Ткани</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/tkani/tkani-tyulevye/">Ткани тюлевые</a></li>
               <li><a href="/tkani/tkani-porternye/">Ткани портьерные</a></li>
               <li><a href="/tkani/tkani-blackout/">Ткани блэкаут</a></li>
               <li><a href="/tkani/tkani-postelnye/">Ткани постельные</a></li>
               <li><a href="/tkani/tkani-pokryvalnye/">Ткани покрывальные</a></li>
               <li><a href="/tkani/tkani-mebelnye/">Ткани мебельные</a></li>
               <li><a href="/tkani/tkani-skatertnye/">Ткани скатертные</a></li>
               <li><a href="/tkani/tkani-podkladochnye/">Ткани подкладочные</a></li>
               <li><a href="/tkani/tkani-ulichnye/">Ткани уличные</a></li>
               <li><a href="/tkani/tkani_shablony/">Ткани шаблоны</a></li>
            </ul>
         </div>
         <div class="content next" id="m_sub_386372">
            <div class="small-nav"><a href="" class="back" rel="#m_sub_cat"><i class="fas fa-chevron-left"></i></a><a href="/karnizy/" class="header">Карнизы</a><a href="" class="close" onclick="hideMobileCatalogMenu(); return false;"><i class="fas fa-times"></i></a></div>
            <ul>
               <li><a href="/karnizy/karnizy-profilnye/">Карнизы профильные</a></li>
               <li><a href="/karnizy/karnizy-s-upravleniem/">Карнизы с управлением</a></li>
               <li><a href="/karnizy/karnizy-dlya-panelnyh-shtor/">Карнизы для панельных штор</a></li>
               <li><a href="/karnizy/karnizy-dlya-podemnyh-shtor/">Карнизы для подъемных штор</a></li>
               <li><a href="/karnizy/karnizy-trekovye/">Карнизы трековые</a></li>
               <li><a href="/karnizy/karnizy-trubchatye/">Карнизы трубчатые</a></li>
               <li><a href="/karnizy/karnizy-kovannye/">Карнизы кованные</a></li>
               <li><a href="/karnizy/karnizy-kafe/">Карнизы кафе</a></li>
               <li><a href="/karnizy/karnizy-derevyannye/">Карнизы деревянные</a></li>
               <li><a href="/karnizy/karnizy-metalloplastikovye/">Карнизы металлопластиковые</a></li>
               <li><a href="/karnizy/karnizy-shinnye/">Карнизы шинные</a></li>
               <li><a href="/karnizy/karnizy-bagetnye/">Карнизы багетные</a></li>
               <li><a href="/karnizy/karnizy-prozrachnye/">Карнизы прозрачные</a></li>
               <li><a href="/karnizy/elektrokarnizy/">Электрокарнизы</a></li>
               <li><a href="/karnizy/aksessuary-dlya-karnizov/">Аксессуары для карнизов</a></li>
            </ul>
         </div>
      </div>
      <div xmlns="http://www.w3.org/1999/xhtml" class="modal fade" tabindex="-1" role="dialog" id="modal-my_price"
           style="display:none">
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
      </div><script type="text/javascript" src="magnific_popup/jquery.magnific-popup.min.js"></script><script type="text/javascript" src="admin/ajax/ajax.js"></script><script type="text/javascript" src="admin/js/jquery.form.min.js"></script><script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"></script><script type="text/javascript" src="js/web.js"></script><script type="text/javascript">
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
   </body>
</html>