<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="currency_ajax.xsl" />

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>
	
	<xsl:template name="NBSP"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:template>
	
	<xsl:template name="arrow"><xsl:text disable-output-escaping="yes">&amp;nbsp;→&amp;nbsp;</xsl:text></xsl:template>

	<!-- <TITLE> -->
	
	<xsl:template name="TITLE">Санаторий «Спутник»</xsl:template>

	<xsl:template name="CONTENT"></xsl:template>

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
		<xsl:value-of select="if ($currency = 'rus') then round($extra div 10) * 10 else $extra"/>
	</xsl:function>


	<!-- ****************************    HEADER    ******************************** -->

	<xsl:template name="HEAD">
	<head>
		<title><xsl:call-template name="TITLE"/></title>
		<base href="{page/base}"/>
	
		<link href="http://sansputnik.by/images/favicon.ico" rel="shortcut icon" />
		<link rel="stylesheet" type="text/css" href="css/main.css" />
		<link rel="stylesheet" type="text/css" href="js/datepicker.css" />
		<link rel="stylesheet" type="text/css" href="nivo-slider/nivo-slider.css" />
		<link rel="stylesheet" type="text/css" href="js/fotorama/fotorama.css" />
		<link rel="stylesheet" href="nivo-slider/themes/default/default.css" type="text/css" media="screen" />
		<link href="admin/jquery_css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" media="screen" />
		<link rel="stylesheet" type="text/css" href="css/update_prices.css" />
		<xsl:text disable-output-escaping="yes">
		&lt;!--[if lte IE 8]&gt;
		  &lt;link rel="stylesheet" type="text/css" href="css/ie.css" /&gt;
		&lt;![endif]--&gt;
		&lt;!--[if lte IE 7]&gt;
			 &lt;link rel="stylesheet" type="text/css" href="css/ie7.css" /&gt;
		&lt;![endif]--&gt;
		</xsl:text>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script type="text/javascript" src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<script  type="text/javascript" src="js/datepicker.js"></script>
		<script type="text/javascript" src="fancybox/jquery.fancybox.pack.js"></script>
		<link rel="stylesheet" href="fancybox/jquery.fancybox.css" type="text/css" media="screen" />
		<script type="text/javascript" src="js/fotorama/fotorama.js"></script>
		<script type="text/javascript" src="js/sputnik.js"></script>
		<script type="text/javascript" src="js/jquery.form.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="js/jquery.number.min.js"></script>
		<script type="text/javascript">
		$(document).ready(function() {
		$("a[rel='fancybox']").fancybox({
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
		$("a.fancyframe").fancybox({
			'autoScale' : false,
			'transitionIn' : 'none',
			'transitionOut' : 'none',
			'type' : 'iframe',
			'width' : 1024,
			'height' : 768
		}); 
		})
		</script>
	</head>
	</xsl:template>




	<!-- ****************************    ВЕРХ    ******************************** -->

	<xsl:variable name="current_page_class" select="'none'"/>

	<xsl:template name="HEADER">
		<div class="header">

			<div class="top" style="position: relative; z-index: 100;">
				<div class="w1k">
					<div class="item blank">
						<a href="{/page/index_link}" class="logo">
							<xsl:call-template name="NBSP" />
						</a>
					</div>
					<div class="item phones">
						<p>
							<xsl:value-of select="/page/common/phone" />
						</p>
						<span class="javascript show">все телефоны</span>
						<div class="hidden" style="margin-left: -46px;">
							<div class="arrow-top"></div>
							<xsl:value-of select="/page/common/phone_hidden" disable-output-escaping="yes" />
						</div>
					</div>
					<div class="item docs">
						<a href="about/dogovory_/">Скачать договоры</a>
					</div>
					<div class="item mail">
						<xsl:variable name="form" select="/page/fform" />
						<script>
							function postForm() {
							$.post("email.item", $("#feedback_form").serialize() );
							$(".send").hide();
							$(".msg_ok").show();
							}
							function postMore() {
							$('.msg_ok').hide();
							$('.send').show();
							return false;
							}
						</script>
						<a href="#" id="mail" onclick="return false;">Написать сообщение</a>
						<a href="mailto:info@sansputnik.by">info@sansputnik.by</a>
						<div class="hidden2 mail">
							<div class="close">
								<img src="images/button_close.png" alt="Закрыть" />
							</div>
							<div class="send">
								<h3>
									<img src="images/title_soobschenie.png" alt="Сообщение" />
								</h3>
								<form id="feedback_form" action="email.item" method="post">
									<input type="hidden" name="targetUrl" value="{/page/post_feedback_link}" />
									<xsl:for-each select="$form/hidden/field">
										<input type="hidden" name="{@input}" value="{@value}" />
									</xsl:for-each>
									Ваше имя:
									<input type="text" name="{$form/field[@name='name']/@input}" />
									Адрес электронной почты или телефон:
									<input type="text" name="{$form/field[@name='phone']/@input}" />
									Сообщение:
									<textarea name="{$form/field[@name='message']/@input}"></textarea>
									<a class="submit" href="javascript:postForm()">Отправить</a>
								</form>
							</div>
							<div class="msg_ok">
								<img src="images/title_soobschenie_otpr.png" alt="сообщение отправлено" />
								<a href="#" id="one_more" onclick="return postMore()">Написать еще сообщение</a>
							</div>
						</div>
					</div>
					<div class="item map">
						<p>
							<a id="map" href="#hidden_map" rel="fancybox">
								pacположение
								<xsl:call-template name="BR" />
								санатория на карте
							</a>
						</p>
						<div id="hidden_map" style="display: none;">
							<div class="yamap" style="background: url('images/map.jpg') center no-repeat;  margin-top:0;">
								<script type="text/javascript" charset="utf-8" src="//api-maps.yandex.ru/services/constructor/1.0/js/?sid=l3Ko5dt7TfnGTfkTmVWbgR73lQMAUtAg&amp;width=974&amp;height=500"></script>
							</div>
						</div>
					</div>
					<div class="item calendar">
						<p>
							<span id="calendar" class="">
								показать
								<xsl:call-template name="BR" />
								календарь
							</span>
						</p>
						<div style="display:none;">
							<input name="data" />
						</div>
					</div>
					<div class="item bank last">
						<a href="http://belarusbank.by/" target="blank">ОАО АСБ "Беларусбанк"</a>
					</div>
				</div>
			</div>
			<div class="menu">
				<table style="width: 100%;">
					<tr>
						<td class="side">

						</td>
						<td style="width:1200px; position: relative;" id="td-f">
							
							<ul class="main" id="main-menu">
								<li class="about lnk has_sub">
									<a href="{/page/about/abstract_page[1]/show_page}" class="lvl1">О санатории</a>
									<ul style="display: none;" class="submenu">
										<xsl:for-each select="/page/about/abstract_page">
											<li>
												<a href="{show_page}">
													<xsl:value-of select="header" />
												</a>
											</li>
										</xsl:for-each>
									</ul>
								</li>
								<li class="lnk">
									<a class="lvl1" href="{/page/rooms_link}">Номера</a>
								</li>
								<xsl:for-each select="page/services">
									<li class="about lnk has_sub">
										<a href="{show_page}" class="lvl1">
											<xsl:value-of select="header" />
										</a>
										<ul style="display: none;" class="submenu">
											<xsl:for-each select="service">
												<li>
													<a href="{show_page}">
														<xsl:value-of select="name" />
													</a>
													<xsl:if test="service">
														<ul class="lv3">
															<xsl:for-each select="service">
																<li>
																	<a href="{show_page}">
																		<xsl:value-of select="name" />
																	</a>
																</li>
															</xsl:for-each>
														</ul>
													</xsl:if>
												</li>
											</xsl:for-each>
										</ul>
									</li>
								</xsl:for-each>
								<li class="lnk">
									<a style="line-height:19px; height: 30px; padding: 13px 30px 12px; text-decoration: none;" class="lvl1" href="{/page/book_link}">
										<span style=" text-decoration: underline;">Бронирование</span>
										<br />
										<span style="font-size: 12px; font-weight: normal;">(цены)</span>
									</a>
								</li>
								<li class="lnk">
									<a href="{/page/news_link}" class="lvl1">Новости</a>
								</li>
								<li class="lnk">
									<a href="{/page/contacts_link}" class="lvl1" style="">Контакты</a>
								</li>
							</ul>
							<div class="clear"></div>
							<a id="lang-switch" class="eng-link" href="http://eng.sansputnik.by/" title="Go to english version">english</a>
						</td>
						<td class="side">
							
						</td>
					</tr>
				</table>
				
			
			</div>
		</div>
	</xsl:template>




	<!-- ****************************    НИЗ    ******************************** -->

	<xsl:template name="FOOTER">
	<div class="footer">
		<div class="inner">
			<div class="weather" style="padding-top: 12px;">
				<span class="javascript" style="color: white; font-size: 14px; padding-bottom: 1px; display: inline; border-bottom: 1px solid white; cursor: pointer">Прогноз погоды</span>
				<div class="hidden" style="padding:0; margin-top: -200px; width: 150px; height: 150px; overflow: hidden;">
					<a href="http://clck.yandex.ru/redir/dtype=stred/pid=7/cid=1228/*http://pogoda.yandex.ru/naroch"><img src="http://info.weather.yandex.net/naroch/4.ru.png" border="0" alt=""/><img width="1" height="1" src="http://clck.yandex.ru/click/dtype=stred/pid=7/cid=1227/*http://img.yandex.ru/i/pix.gif" alt="" border="0"/></a>
				</div>
			</div>
			<div class="curs" style="padding-top: 12px;">
				<span class="javascript" style="color: white; font-size: 14px; padding-bottom: 1px; display: inline; border-bottom: 1px solid white; cursor: pointer">Курсы валют</span>
				<div class="hidden" style="overflow: hidden; height: 122px; margin-top: -150px; padding: 0px;">
					<div id="informerBelarusbank"></div>
					<xsl:if test="$current_page_class != 'book'">
						<script type="text/javascript">
						$(document).ready(function() {
							insertAjax('<xsl:value-of select="/page/currency_link"/>');
						});
						</script>
					</xsl:if>
					<!-- <script type="text/javascript" src="//belarusbank.by/informer?logotyp=1&amp;ColorTextTitle=000000&amp;ColorTextInformer=969696&amp;ColorBackGround=ffffff&amp;ColorTitleBackGround=ffffff&amp;ColorBorder=006030"></script>  -->
				</div>
			</div>
			<div class="search">
				Поиск по сайту
				<form id="serch" action="{page/search_link}" method="post">
						<input type="text" name="query"/>
						<input class="submit" type="submit"/>
				</form>
			</div>
			<div class="copyright">
				<xsl:value-of select="/page/common/copy" disable-output-escaping="yes"/>
			</div>
			<table border="0" class="logo">
					<tr >
						<td class="forever"><a href="http://forever.by/"><img src="images/icon_forever.png"/></a></td>
						<td><a href="http://forever.by/">Разработка сайта</a><br/>Forever студия веб-дизайна </td>
					</tr>
			</table>
		</div>
	</div>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-31231996-37', 'auto');
  ga('send', 'pageview');

</script>	
	
	</xsl:template>
	
	
	

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template name="FRAME"/>

	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html xmlns="http://www.w3.org/1999/xhtml">
		<xsl:call-template name="HEAD"/>
		<body>
		
<div id="forever-banner-left-client_6" class="forever-banner" cid="6"></div>
<div id="forever-banner-right-client_6" class="forever-banner"></div>
<div id="forever-banner-top-client_6" class="forever-banner"></div>
<script src="http://test9.must.by/js/insert_banner.js" ></script> 

		<xsl:call-template name="FRAME"/>
		<div class="page blue">
			<xsl:call-template name="HEADER"/>
			<div class="mainwrap">
				<xsl:call-template name="CONTENT"/>
				<div class="clear"></div>
			</div>
		</div>
		<xsl:call-template name="FOOTER"/>
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
		<!-- BEGIN JIVOSITE CODE {literal} -->
<script type='text/javascript'>
(function(){ var widget_id = 'STWrPUxCZk';
var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);})();</script>
<!-- {/literal} END JIVOSITE CODE -->

<script type="text/javascript" src="js/menu_aim.js"></script>
<script type="text/javascript" src="js/menu_driver.js"></script>
		</body>
	</html>
	</xsl:template>
	
	
	

	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->

	<xsl:template match="*"></xsl:template>

	<xsl:template match="text_part">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>
	
	<xsl:template match="gallery_part">
	<div class="gallerey">
		<div class="fotorama" data-nav="thumbs">
			<xsl:for-each select="picture_pair">
				<a href="{@path}{big}"><img src="{@path}{small}" alt="{name}" width="90" height="60"/></a>
			</xsl:for-each>
		</div>
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