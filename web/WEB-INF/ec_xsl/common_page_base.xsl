<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
		</xsl:text>
	</xsl:template>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>
	
	<xsl:template name="NBSP"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:template>
	
	<xsl:template name="arrow"><xsl:text disable-output-escaping="yes">&amp;nbsp;→&amp;nbsp;</xsl:text></xsl:template>

	<!-- <TITLE> -->
	
	<xsl:template name="TITLE">Санаторий «Спутник»</xsl:template>

	<xsl:template name="CONTENT"></xsl:template>
	<xsl:template name="SCRIPTS"></xsl:template>

	<xsl:decimal-format name="ru" decimal-separator="," grouping-separator="&#160;" />

	<xsl:variable name="f_mask" select="'###&#160;###,##'"/>

	<xsl:function name="f:day_month" as="xs:string">
		<xsl:param name="date" as="xs:string" />
		<xsl:variable name="parts" select="tokenize(tokenize($date, '\s+')[1], '\.')"/>
		<xsl:variable name="month" select="number($parts[2])"/>
		<xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
<!-- 		<xsl:value-of select="concat(number($parts[1]), ' ', $months[$month], ' ', $parts[3])"/> -->
		<xsl:value-of select="concat(number($parts[1]), ' ', $months[$month])"/>
	</xsl:function>

	<xsl:function name="f:day_month_year" as="xs:string">
		<xsl:param name="date" as="xs:string" />
		<xsl:variable name="parts" select="tokenize(tokenize($date, '\s+')[1], '\.')"/>
		<xsl:variable name="month" select="number($parts[2])"/>
		<xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
<!-- 		<xsl:value-of select="concat(number($parts[1]), ' ', $months[$month], ' ', $parts[3])"/> -->
		<xsl:value-of select="concat(number($parts[1]), ' ', $months[$month], ' ', $parts[3])"/>
	</xsl:function>

	<xsl:function name="f:price">
		<xsl:param name="room"/>
		<xsl:param name="period" as="xs:string"/>
		<xsl:param name="type" as="xs:string"/>
		<xsl:param name="currency" as="xs:string"/>
		<xsl:variable name="price_tag" select="concat('price_', $type, '_', $currency, '_', $period)"/>
		<xsl:variable name="price" select="$room/*[local-name() = $price_tag]"/>
		<xsl:value-of select="if (not($price) or $price = '') then 0 else number(translate(translate($price, '&#160;', ''), ',', '.'))"/>
	</xsl:function>
	
	<xsl:function name="f:price_extra">
		<xsl:param name="price" as="xs:double"/>
		<xsl:param name="quot" as="xs:double"/>
		<xsl:param name="currency" as="xs:string"/>
		<xsl:variable name="extra" select="$price * $quot"/>
		<!-- <xsl:value-of select="if ($currency = 'rus') then round($extra div 10) * 10 else $extra"/> -->
		<xsl:value-of select="if ($currency = 'eur') then round($extra) else $extra"/>
	</xsl:function>

	<xsl:variable name="pname" select="page/@name"/>


	<!-- ****************************    HEADER    ******************************** -->

	<xsl:template name="HEAD">
	<head>
		<title><xsl:call-template name="TITLE"/></title>
		<base href="{page/base}"/>
		<meta charset="utf-8"/>
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<title>Главная Sputnik</title>
		<link rel="stylesheet" href="css/app.css"/>
		<link href="admin/jquery_css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" media="screen" />
		<link rel="stylesheet" href="fancybox/jquery.fancybox.css" type="text/css" media="screen" />
		<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="admin/js/jquery-ui-1.10.3.custom.min.js"/>
		<script type="text/javascript" src="admin/js/regional-ru.js"/>
		<script src="js/bootstrap.min.js"></script>
		<script src="js/tooltip.js"></script>
		<script src="js/jquery.spincrement.min.js"></script>
		<script type="text/javascript" src="js/jquery.form.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="js/utils.js"></script>
		<script type="text/javascript" src="js/jquery.number.min.js"></script>
		<script type="text/javascript" src="js/regional-ru.js"></script>
		<link  href="http://cdnjs.cloudflare.com/ajax/libs/fotorama/4.6.4/fotorama.css" rel="stylesheet"/> <!-- 3 KB -->
		<script src="http://cdnjs.cloudflare.com/ajax/libs/fotorama/4.6.4/fotorama.js"></script> <!-- 16 KB -->
		<script type="text/javascript" src="fancybox/jquery.fancybox.pack.js"></script>
		<!-- COMBOBOX JS -->
		<script type="text/javascript" src="js/combobox.js"></script>
		<!-- BOOKING FORM -->
		<script type="text/javascript" src="js/booking-form.js"></script>
	</head>
	</xsl:template>




	<!-- ****************************    ВЕРХ    ******************************** -->



	<xsl:template name="HEADER">
	<nav class="navbar navbar-fixed-top{' scrolled'[not($pname = 'index')]}">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="{page/index_link}">
				<img id="main_logo_img" src="{if ($pname = 'index') then 'img/logo.png' else 'img/logo_sputnik.jpg'}" alt="">
					<xsl:if test="not($pname = 'index')"><xsl:attribute name="old-src" select="'img/logo_sansputnik.jpg'"/></xsl:if>
				</img>
			</a>
		</div>
		<div id="navbar" class="collapse navbar-collapse">
			<div class="booking-online hidden-md hidden-lg">
				<button type="button" class="btn btn-primary btn-block toggle-button" rel="#booking-online">Онлайн-бронирование</button>
			</div>
			<ul class="extra-links">
				<li><i class="fa fa-phone"></i><a href="" data-toggle="modal" data-target="#modal-phones"><xsl:value-of select="/page/common/phone" /> <span class="caret"/></a></li>
				<li><i class="fa fa-arrow-down"></i><a href="" data-toggle="modal" data-target="#modal-documents">Скачать договоры</a></li>
				<li><i class="fa fa-envelope"></i><a href="" data-toggle="modal" data-target="#modal-message">Написать сообщение</a></li>
				<li><i class="fa fa-calendar"></i><a href="" data-toggle="modal" data-target="#modal-calendar">Календарь</a></li>
				<li><i class="fa fa-university"></i><a href="https://belarusbank.by">Беларусбанк</a></li>
				<li><i class="fa fa-globe"></i><a href="http://eng.sansputnik.by">In english</a></li>
			</ul>
			<ul class="nav navbar-nav">
				<h3 class="hidden-md hidden-lg">Меню</h3>
				<li class="dropdown">
					<a href="{/page/about/abstract_page[1]/show_page}" class="dropdown-toggle" data-toggle="dropdown" role="button">О санатории <span class="caret"/></a>
					<ul class="dropdown-menu">
						<xsl:for-each select="/page/about/abstract_page">
							<li><a href="{show_page}"><xsl:value-of select="header" /></a></li>
						</xsl:for-each>
					</ul>
				</li>
				<li><a href="{/page/rooms_link}">Номера</a></li>


				<xsl:for-each select="page/services">
				<li>
					<a href="{if (service) then show_pages else show_page}" class="dropdown-toggle" data-toggle="dropdown" role="button">
						<xsl:value-of select="header" /> <span class="caret"/>
					</a>
					<xsl:if test="service">
						<ul class="dropdown-menu">
							<xsl:for-each select="service">
								<li><a href="{if (service) then show_pages else show_page}"><xsl:value-of select="name" /></a></li>
							</xsl:for-each>
						</ul>
					</xsl:if>
				</li>
				</xsl:for-each>
				<li><a href="{/page/book_link}">Бронирование и цены</a></li>
				<li><a href="{/page/news_link}">Новости</a></li>
				<li><a href="{/page/contacts_link}">Контакты</a></li>
			</ul>
		</div>
		<div class="booking-online hidden-xs hidden-sm">
			<button type="button" class="btn btn-primary btn-lg toggle-button" rel="#booking-online">Бронирование</button>
		</div>
	</nav>
	<div class="call-back">
		<a class="round-button" href="" data-toggle="modal" data-target="#modal-callback"><i class="fa fa-phone"></i></a>
		<span>Обратный звонок</span>
	</div>
	</xsl:template>


	<xsl:template name="SOCIAL">
	<div class="social-icons">
		<div><a href="https://vk.com/asbsansputnik"><i class="fa fa-vk fa-2x"></i></a></div>
		<div><a href="https://twitter.com/SanSputnik"><i class="fa fa-twitter fa-2x"></i></a></div>
		<div><a href="https://www.instagram.com/asbsansputnik/"><i class="fa fa-instagram fa-2x"></i></a></div>
		<div><a href="https://www.facebook.com/asbsansput/"><i class="fa fa-facebook fa-2x"></i></a></div>
	</div>
	</xsl:template>



	<!-- ****************************    НИЗ    ******************************** -->

	<xsl:template name="FOOTER">
	<footer class="p-t-b-small">
		<div class="container">
			<div class="row visible-xs-block">
				<div class="col-xs-12">
					<xsl:call-template name="SOCIAL"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-3 p-b-small">
					<form id="serch" action="{page/search_link}" method="post">
						<div class="input-group">
							<input type="text" class="form-control" placeholder="Поиск по сайту" name="query"/>
							<span class="input-group-btn">
								<button class="btn btn-primary" type="button" onclick="$(this).closest('form').submit()">Найти</button>
							</span>
						</div>
					</form>
					<div class="btn-group btn-group-justified" role="group">
						<a type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-weather">Прогноз погоды</a>
						<a type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-money">Курс валют</a>
					</div>
					<div class="forever">
						<a href="http://forever.by">
							Разработка сайта -<xsl:call-template name="BR"/>студия веб-дизайна Forever
						</a>
					</div>
				</div>
				<div class="col-md-6 official-text p-b-small">
					<p>Унитарное предприятие «АСБ Санаторий Спутник»<xsl:call-template name="BR"/>
					222395, Минская область, Мядельский район, <nobr>К. П. Нарочь</nobr>, ул.&#160;Туристская, д.14,комната 1<xsl:call-template name="BR"/>
					УНН 690313229<xsl:call-template name="BR"/>
					Свидетельство о&#160;государственной регистрации выдано Мядельским райисполкомом 31&#160;марта 2014 года 
					с&#160;регистрационным номером 690313229 Специальное разрешение (лицензия) 
					на&#160;право осуществления медицинской деятельности выдано Министерством 
					здравоохранения Республики Беларусь 27 сентября 2011 года &#8470;&#160;02040/6984</p>
				</div>
				<div class="col-md-3 payment-logos">
					<img src="img/payment_details.jpg" alt=""/>
				</div>
			</div>
		</div>
	</footer>

	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-31231996-37', 'auto');
	  ga('send', 'pageview');
	
	</script>	
	
	</xsl:template>
	
	
	<!-- ****************************    MODAL    ******************************** -->
	
	
	<xsl:template name="MODAL">
	<div class="modal fade" id="modal-phones" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Наши телефоны</h4>
				</div>
				<div class="modal-body">
					<p>Звоните нам, чтобы забронировать номер или если у вас есть вопросы.</p>
					<xsl:value-of select="/page/common/phone_hidden" disable-output-escaping="yes" />
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-documents" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-md" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Скачать договоры</h4>
				</div>
				<div class="modal-body">
					<xsl:value-of select="page/about/abstract_page[@key = 'dogovory_']/text_part/text" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-calendar" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Календарь</h4>
				</div>
				<div class="modal-body datepicker">

				</div>
			</div>
		</div>
	</div>
	
	<xsl:call-template name="MESSAGE_MODAL">
		<xsl:with-param name="message_link" select="//page/message_link"/>
	</xsl:call-template>
	
	<xsl:call-template name="CALLBACK_MODAL">
		<xsl:with-param name="callback_link" select="//page/callback_link"/>
	</xsl:call-template>
	
	<div class="modal fade" id="modal-gallery" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Фотографии номера</h4>
				</div>
				<div class="modal-body">
					
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="modal-weather" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Прогноз погоды</h4>
				</div>
				<div class="modal-body">
					<div class="weather">
						<div style="padding:0; overflow: hidden;">
							<a href="http://clck.yandex.ru/redir/dtype=stred/pid=7/cid=1228/*http://pogoda.yandex.ru/naroch">
							<img src="http://info.weather.yandex.net/naroch/4.ru.png" border="0" alt=""/>
							<img width="1" height="1" src="http://clck.yandex.ru/click/dtype=stred/pid=7/cid=1227/*http://img.yandex.ru/i/pix.gif" alt="" border="0"/>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-money" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Курс валют</h4>
				</div>
				<div class="modal-body">
					<div id="informerBelarusbank"></div>
					<script type="text/javascript">
					$(document).ready(function() {
						insertAjax('<xsl:value-of select="/page/currency_link"/>');
					});
					</script>
					<script type="text/javascript" src="//belarusbank.by/informer?logotyp=1&amp;ColorTextTitle=000000&amp;ColorTextInformer=969696&amp;ColorBackGround=ffffff&amp;ColorTitleBackGround=ffffff&amp;ColorBorder=006030"></script>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-booking-conditions" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Условия бронирования и инструкции</h4>
				</div>
				<div class="modal-body">
					<xsl:value-of select="page/booking_text/booking" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</div>
	
	<div class="dim-block" style="display: none;" id="booking-online">
		<a href="" class="dim-close" onclick="$('#booking-online').hide(); return false;">Закрыть <i class="fa fa-times fa-2x"></i></a>
		<div class="center">
			<h2>Онлайн-бронирование номера</h2>
			<!-- 
			<xsl:value-of select="page/booking_text/restrictions" disable-output-escaping="yes"/>
			 -->
			<p>Оформление брони номеров в рамках одной заявки доступно только для граждан одной страны, 
			если вы хотите оформить бронь для граждан разных государств свяжитесь с нашими менеджерами по телефону: +375 (1797) 45-542.</p>
			<form method="post" action="{page/room_search_link}" class="form-inline p-t-b-default">
				<div class="form-group">
					<input type="text" name="date" id="date" class="form-control datepicker" placeholder="Дата заезда"
						onchange="setMillisModal('date', 'date_millis'); checkInputsModal()"/>
					<input type="hidden" name="date_millis" id="date_millis" value=""/>
				</div>
				<div class="form-group">
					<select name="total" id="qty" class="form-control" onchange="checkInputsModal()">
						<option value="0">Количество человек</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="6">6</option>
						<option value="7">7</option>
						<option value="8">8</option>
						<option value="9">9</option>
						<option value="10">10</option>
					</select>
				</div>
				<div class="form-group">
					<select name="citizen_name" id="citizen_sel" onchange="setCitizenModal($(this).val()); checkInputsModal()" class="form-control">
						<option value="нет">Выберите гражданство</option>
						<option value="Беларусь">Беларусь</option>
						<option value="Россия">Россия</option>
						<option value="Казахстан">Казахстан</option>
						<option value="Другое">Другое</option>
					</select>
					<input type="hidden" name="citizen" id="citizen_inp" value=""/>
				</div>
				<button type="submit" style="visibility:hidden" class="btn btn-primary" id="submit_link">Показать номера</button>
			</form>
			<a href="" data-toggle="modal" data-target="#modal-booking-pin">Ввести пин-код</a>&#160;&#160;&#160;&#160;&#160;
			<a href="" data-toggle="modal" data-target="#modal-booking-conditions">Условия бронирования и инструкции</a>
		</div>
	</div>
	
	<div class="modal fade" id="modal-booking-conditions" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Условия бронирования и инструкции</h4>
				</div>
				<div class="modal-body">
					<xsl:value-of select="page/booking_text/booking" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-booking-pin" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title">Введите пин-код</h4>
				</div>
				<div class="modal-body">
					<form action="{page/enter_pin}" method="post" class="m-b-small">
						<div class="input-group">
							<input id="pin" name="pin" type="text" class="form-control" placeholder="Пин-код"/>
							<span class="input-group-btn">
								<button onclick="if ($.trim($(this).closest('form').find('input').val()) != '') $(this).closest('form').submit(); return false;" 
									class="btn btn-primary" type="button">Подтвердить</button>
							</span>
						</div>
					</form>
					<p>Пин-код вам скажет менеджер, после бронирования номера по телефону. 
					Номер и тип путевки уже будут выбраны. 
					Вам останется заполнить персональные данные для заключения договора.</p>
					<p>Для бронирования номера свяжитесь с нами по телефону <strong>+375(1797) 45-542.</strong></p>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>
	
	

	<!-- ****************************    СТРАНИЦА    ******************************** -->



	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html xmlns="http://www.w3.org/1999/xhtml">
		<xsl:call-template name="HEAD"/>
		<body class="{'internal-page'[not($pname = 'index')]}">
			<div class="page-wrapper">
				<xsl:call-template name="HEADER"/>
				<xsl:call-template name="SOCIAL"/>
				<xsl:call-template name="CONTENT"/>
				<xsl:call-template name="FOOTER"/>
			</div>
			<xsl:call-template name="MODAL"/>
			<xsl:call-template name="SCRIPTS"/>

			<script>
			<xsl:text disable-output-escaping="yes">
				$(document).ready(function() {
					$("a[rel='fancybox'], a.fancybox").fancybox({
						padding: 0,
						openEffect : 'elastic',
						openSpeed  : 150,
						closeEffect : 'elastic',
						closeSpeed  : 150,
						closeClick : false,
						hideOnOverlayClick : false,
						hideOnContentClick : false,
						helpers : {
							overlay : null
							,title : {
								type : 'over'
							}
						}
					});
				});

				function setDays(q, intervals) {
					q = $(q);
					v = parseInt(q.val());
					
					$("#sel_denom").val(v);
					var selector = ".pi";
					if(typeof intervals != "string"){
						pref = "";
						for(i = 0; i &lt; intervals.length; i++ ){
							pref += "td[price='"+intervals[i]+"'] "+selector;
							if(i &lt; intervals.length-1){pref += ", ";}
						}
						selector = pref;		
					}
					else {
						selector = "td[price='"+intervals+"'] "+selector;
					}
					q.closest("table").find(selector).each(function(){
						p = Math.round(parseInt($(this).attr("price"))*v*100)/100;
						$(this).text(p+"".replace(".",","));
					});
				}
				
				function setCitizenModal(country) {
					if ('Беларусь'.indexOf(country) != -1) {
						$('#citizen_inp').val('РБ');
					} else if ('Россия, Казахстан'.indexOf(country) != -1) {
						$('#citizen_inp').val('ЕАЭС');
					} else if ('Другое'.indexOf(country) != -1) {
						$('#citizen_inp').val(country);
					} else {
						$('#citizen_inp').val('');
					}
				}
		
				function setMillisModal(stringInp, millisInp) {
					var parts = $('#' + stringInp).val().split('.');
					if (parts.length != 3) {
						$('#' + millisInp).val('');
						return;
					}
					$('#' + millisInp).val(Date.parse(parts[2] + '-' + parts[1] + '-' + parts[0]));
				}
				
				function checkInputsModal() {
					var hasQty = $('#qty').val() != '0';
					var hasDate = !($('#date_millis').val() == '');
					var hasCitizen = !($('#citizen_inp').val() == '');
					if (hasQty &amp;&amp; hasDate &amp;&amp; hasCitizen)
						$('#submit_link').css('visibility', '');
					else
						$('#submit_link').css('visibility', 'hidden');
				}
			</xsl:text>
			</script>

			<!-- Yandex.Metrika counter -->
			<script type="text/javascript">
			(function (d, w, c) {
			    (w[c] = w[c] || []).push(function() {
			        try {
			            w.yaCounter19430230 = new Ya.Metrika({id:19430230,
			                    webvisor:true,
			                    clickmap:true,
			                    trackLinks:true,
			                    accurateTrackBounce:true});
			        } catch(e) { }
			    });
			
			    var n = d.getElementsByTagName("script")[0],
			        s = d.createElement("script"),
			        f = function () { n.parentNode.insertBefore(s, n); };
			    s.type = "text/javascript";
			    s.async = true;
			    s.src = (d.location.protocol == "https:" ? "https:" : "http:") + "//mc.yandex.ru/metrika/watch.js";
			
			    if (w.opera == "[object Opera]") {
			        d.addEventListener("DOMContentLoaded", f, false);
			    } else { f(); }
			})(document, window, "yandex_metrika_callbacks");
			</script>
			<noscript><div><img src="//mc.yandex.ru/watch/19430230" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
			<!-- /Yandex.Metrika counter -->		
			<!-- BEGIN JIVOSITE CODE {literal}-->
			<script type='text/javascript'>
			(function(){ var widget_id = 'STWrPUxCZk';
			var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);})();</script>
			 <!--{/literal} END JIVOSITE CODE -->
			<script type='text/javascript'>
				$(document).ready(function() {
					$.datepicker.setDefaults($.datepicker.regional["ru"]);
					$(".datepicker").datepicker();
				});
			</script>
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