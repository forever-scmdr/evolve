<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="feedback_ajax.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text>
	</xsl:template>

	<xsl:variable name="quote">"</xsl:variable>

	<!-- ****************************    UTM    ******************************** -->

	<xsl:variable name="utm"/>

	<!-- ****************************    SEO    ******************************** -->

	<xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
	<xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

	<xsl:variable name="title" select="'Respectiva.Pro'"/>
	<xsl:variable name="meta_description" />
	<xsl:variable name="meta_keywords" />
	<xsl:variable name="base" select="page/base"/>
	<!-- <xsl:variable name="main_host"
				  select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base"/> -->
	<xsl:variable name="main_host" select="$base"/>

	<xsl:variable name="default_canonical"
				  select="if(page/@name != 'index') then tokenize(page/source_link, '\?')[1] else ''"/>
	<xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

	<xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>

	<xsl:variable name="cur_sec" select="page//current_section"/>
	<xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
	<xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


	<xsl:variable name="active_menu_item" select="page/@name"/>
	<xsl:variable name="extra-header-class"/>
	<!-- Инфа, общая для всех страниц -->
	<xsl:variable name="common" select="page/common" />


	<!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

	<xsl:variable name="source_link" select="/page/source_link"/>
	<xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

	<xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
	<xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
	<xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
	<xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>
	<xsl:variable name="adv" select="page/advertisement"/>
	<xsl:variable name="adv_top" select="$adv/top_728x90"/>
	<xsl:variable name="adv_side" select="$adv/side_240x400"/>
	<xsl:variable name="adv_bottom" select="$adv/bottom_900x600"/>
	<xsl:variable name="adv_fixed" select="$adv/bottom_fixed"/>
	<xsl:variable name="current_tag" select="if(page/@name = 'news') then string(page/variables/tag) else ''"/>


	<!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->
	




	<!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->
	<xsl:template name="HEAD">
		<head>
			<base href="{$main_host}"/> 
			<xsl:call-template name="SEO"/>
			<meta charset="utf-8"/>
			<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
			<meta name="author" content="Respectiva"/>
			<meta name="theme-color" content="#fff"/>
			<meta property="og:type" content="website"/>
			<meta name="twitter:image" content="img/logo.svg" />
			<link rel="icon" type="image/png" href="img/favicon.png" />
			<link rel="apple-touch-icon" type="image/png" sizes="76x76" href="img/logo.svg" />
			<!-- <link rel="canonical" href="{$canonical}" /> -->
			<link rel="preconnect" href="https://fonts.googleapis.com"/>
			<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="crossorigin" />
			<link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&amp;display=swap" rel="stylesheet" />
			<link rel="stylesheet" href="css/style.css" />
			<xsl:for-each select="$head-end-modules">
				<xsl:value-of select="code" disable-output-escaping="yes"/>
			</xsl:for-each>
		</head>
	</xsl:template>

	<xsl:template name="HEADER">
		<xsl:if test="/page/@name != 'index'">
			<xsl:call-template name="TICKERS_INTERNAL"/>
		</xsl:if>
			<header>
				<div class="container flx">
					<div class="left flx">
						<div class="burger">
							<span></span>
							<span></span>
							<span></span>
						</div>

						<a href="{$base}" class="logo">
							<img src="img/logo.svg" alt="logo" title="logo" class="light" />
							<img src="img/logo_wh.png" alt="logo" title="logo" class="shadow" />
						</a>

						<div class="search_box flx">
							<form action="{page/search_link}" method="post">
								<input type="text" value="{page/variables/search}" name="search"/>
								<button></button>
							</form>
						</div>
					</div>

					<div class="right flx">
						<div class="night_mode">
							<label>
								<input type="checkbox" />
								<span></span>
							</label>
						</div>
						<!-- <div class="lang_box flx">
							<span class="active">RU</span>
							<img src="img/arrow_down.svg" alt="arrow_down" class="arrow light" />
							<img src="img/arrow_down_wh.svg" alt="arrow_down" class="arrow shadow" />
							<ul>
								<li><a href="https://en.repectiva.pro">EN</a></li>
							</ul>
						</div> -->
					</div>
				</div>
			</header>
	</xsl:template>

	<xsl:template name="TOP_NAV">

		<xsl:variable name="most_read" select="('finance', 'business', 'technology')" />

		<section class="first_screen">
			<div class="container">
				<nav>
					<ul class="flx">
						<li><a href="{$main_host}" class="{'active'[$active_menu_item = 'index']}">Главная</a></li>
						
						<li><a href="{page/news_link}" class="{'active'[$current_tag = '' and $active_menu_item = 'news']}">Новости</a></li>

						<xsl:for-each select="page/news[@key = $most_read]">
							<xsl:variable name="k" select="@key"/>
							<li><a href="{show_page}" class="{'active'[$active_menu_item = $k]}"><xsl:value-of select="name"/></a></li>
						</xsl:for-each>
						<li class="other">
							<a>Статьи</a>
							<div class="more_box">
								<xsl:for-each select="page/news[not(@key = $most_read)]">
									<xsl:variable name="k" select="@key"/>
									<a href="{show_page}" class="{'active'[$active_menu_item = $k]}"><xsl:value-of select="name"/></a>
								</xsl:for-each>
							</div>
						</li>
						<li><a href="/contacts" class="{'active'[$active_menu_item = 'contacts']}">Контакты</a></li>
					</ul>
				</nav>

			<xsl:if test="/page/@name = 'index'">
				<xsl:call-template name="TICKERS"/>
			</xsl:if>
			</div>
		</section>
	</xsl:template>

	<xsl:template name="TICKERS">

		<xsl:variable name="inf_wrap" select="/page/informer_wrap"/>

		<xsl:if test="$inf_wrap">
			<div class="market_box">
				<div class="categ_wrap" id="desctop-only-tickers">
					<ul class="flx" id="ticker-menu">
						<xsl:for-each select="$inf_wrap">
							<li><a style="cursor: pointer;" data-informer="#informer-{@id}" class="informer-link{if(position() = 1) then ' active ' else ''}"><xsl:value-of select="name"/></a></li>
						</xsl:for-each>
					</ul>
				</div>
				<div class="market_list tradingview-widget-container__widget" id="ticker_box"></div>
				<script>
					var tickerBox = document.getElementById('ticker_box')  
					var width = tickerBox.offsetWidth
					var tabs = [
								<xsl:for-each select="$inf_wrap">
									<xsl:variable name="sep" select="if(position() != 1) then ',' else ''"/>
									<xsl:value-of select="$sep"/>{
										 "title": "<xsl:value-of select="name" />"
										,"id":"<xsl:value-of select="@id"/>" 
										,"symbols":[
											<xsl:for-each select="informer">
												<xsl:variable name="s" select="if(position() != 1) then ',' else ''"/>
												<xsl:value-of select="$s"/>{"s": "<xsl:value-of select="pro_name" />", "d": "<xsl:value-of select="name" />"}
											</xsl:for-each>
										]
									}
								</xsl:for-each>
							]
					
					
					var desctopMenu = document.getElementById('desctop-only-tickers')
					if (width &lt; 500 &amp;&amp; desctopMenu){

						desctopMenu.parentNode.removeChild(desctopMenu);
						var settings = {
							"colorTheme": "light",
							"dateRange": "12M",
							"showChart": false,
							"locale": "ru",
							"largeChartUrl": "",
							"isTransparent": false,
							"showSymbolLogo": true,
							"showFloatingTooltip": false,
							"width": "100%",
							"height": "375",
							"tabs": tabs
						}

						var newScript = document.createElement('script');
						newScript.setAttribute('type', 'text/javascript');
						newScript.setAttribute('src', 'https://s3.tradingview.com/external-embedding/embed-widget-market-overview.js');
						newScript.textContent = JSON.stringify(settings);

						tickerBox.innerHTML = '';
						tickerBox.appendChild(newScript);
					}
					else{

						for(let i = 0; i &lt; tabs.length; i++){
							symbols = tabs[i]
							tickerBox.classList.remove("tradingview-widget-container__widget");

							for(let i = 0; i &lt; symbols['symbols'].length; i++){
								symbols['symbols'][i] = {"proName": symbols['symbols'][i]["s"], "title": symbols['symbols'][i]["d"]}							
							}

							settings = {
								"showSymbolLogo": true,
								"colorTheme":"light",
								"isTransparent": false,
								"width": "100%",
								"height": (symbols['symbols'].length * 72) + "",
								"locale": "ru",
								"symbols": symbols['symbols']

 							}


 							display = i > 0? 'none' : ''
 							newDiv = document.createElement('div');
 							newDiv.setAttribute('id', 'informer-' + symbols['id'])
 							newDiv.setAttribute('calss', 'tradingview-widget-container__widget traiding-wrap')
 							newDiv.style.display = display

 							var newScript = document.createElement('script');
							newScript.setAttribute('type', 'text/javascript');
							newScript.setAttribute('src', 'https://s3.tradingview.com/external-embedding/embed-widget-tickers.js');
							newScript.textContent = JSON.stringify(settings);

							newDiv.appendChild(newScript);
							tickerBox.appendChild(newDiv);

							
						}
					}
				</script>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="TICKERS_INTERNAL">

			<xsl:variable name="informers" select="/page/informer_wrap/informer"/>
			<xsl:if test="$informers">
				<!-- <div class="header__content row"> -->
					<div class="market_box">
						<div class="tradingview-widget-container">
							<div class="tradingview-widget-container__widget"></div>
							<!-- <div class="tradingview-widget-copyright">
								<a href="https://ru.tradingview.com" rel="noopener" target="_blank">
									<span class="blue-text">Финансовые рынки</span>
								</a> от TradingView
							</div> -->
							<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-ticker-tape.js">{
									"showSymbolLogo": true,
									"colorTheme": "light",
									"isTransparent": false,
									"displayMode": "adaptive",
									"locale": "ru",
									"symbols": [
										<xsl:for-each select="$informers">
											<xsl:if test="position() &gt; 1">,</xsl:if>
											{
												"proName": <xsl:value-of select="concat('&#34;', pro_name, '&#34;')" disable-output-escaping="yes"/>,
												"title": <xsl:value-of select="concat('&#34;', name, '&#34;')" disable-output-escaping="yes"/>
											}
										</xsl:for-each>
									]									
								}</script>
						</div>
					<!-- </div> -->
				</div>
			</xsl:if>

	</xsl:template>

	<xsl:template name="FOOTER">
		<footer>
			<div class="container top">
				<div class="left_side">
					<!-- <img src="img/footer_logo.svg" alt="footer_logo" class="footer_logo" /> -->
					<!-- <p>Каждый день мы рассказываем, что происходит в мире прямо сейчас.</p> -->
					<ul class="footer_soc" style="padding_top: 32px">
						<xsl:for-each select="$common/soc_link">
							<li><a href="{link}"><img src="/{@path}{icon}" alt="{name}" /></a></li>
						</xsl:for-each>
					</ul>
				</div>
				<div class="right_side">
					<ul>
						<li><a href="{$main_host}">Главная страница</a></li>
						<li><a href="{page/news_link}">Новости</a></li>
						<xsl:for-each select="page/menu_custom">
							<xsl:variable name="p" select="position() + 2" />
							<li><a href="{show_page}"><xsl:value-of select="name"/></a></li>

							<xsl:if test="$p mod 11 = 0">
								<xsl:text disable-output-escaping="yes">&lt;/ul&gt;&lt;ul&gt;</xsl:text>
							</xsl:if>

						</xsl:for-each>
						<xsl:for-each select="page/news">
							<xsl:variable name="p" select="position() + count(page/menu_custom)" />

							<li><a href="{show_page}"><xsl:value-of select="name"/></a></li>
							<xsl:if test="$p mod 12 = 0">
								<xsl:text disable-output-escaping="yes">&lt;/ul&gt;&lt;ul&gt;</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</ul>
				</div>
			</div>
			<div class="container">
				<div class="copy_box">
					<p>© Respectiva.Pro 2024</p>
					<p>Нашли ошибку? Выделите текст и нажмите Ctrl+Enter.</p>
					<p><a href="mailto:info@tempting.pro">info@tempting.pro</a></p>
				</div>
			</div>
		</footer>
	</xsl:template>


	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->
	<xsl:template name="CONTENT"/>
	<xsl:template name="EXTRA_SCRIPTS"/>
	<xsl:template name="COMMON_SCRIPTS"></xsl:template>


	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
		<html lang="ru">
			<xsl:call-template name="HEAD"/>
			<body id="top">
				<xsl:call-template name="HEADER"/>
				<xsl:call-template name="TOP_NAV"/>

				<xsl:call-template name="CONTENT"/>

				<xsl:call-template name="FOOTER"/>

				<div class="desc_menu_box">
					<ul>
						<li><a href="{$main_host}" >Главная</a></li>
						<li><a href="{page/news_link}" >Новости</a></li>
						<!-- <li><a href="{page/news_link_fin}" >Финансы</a></li>
						<li><a href="{page/news_link_biz}" >Бизнес</a></li>
						<li><a href="{page/news_link_tech}" >Технологии</a></li> -->
						<xsl:variable name="list" select="tokenize('Финансы,Бизнес,Технологии', ',')"/>
						<xsl:for-each select="page/news">
							<!-- <xsl:variable name="show" select="index-of($list, name)"/> -->
							<!-- <xsl:if test="$show"> -->
								<li><a href="{show_page}"><xsl:value-of select="name"/></a></li>
							<!-- </xsl:if> -->
						</xsl:for-each>
					</ul>
				</div>

				<script src="js/plugins.js"></script>
				<script src="js/main.js"></script>
				<script>
					$("a.informer-link").on('click', function(e){
						$('#ticker_box > div').hide();
						$t = $($(this).attr("data-informer"))
						$t.show();
						$('.informer-link').removeClass('active');
						$(this).addClass('active')
					})
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
		<xsl:apply-templates select="text_part | gallery_part | code_part | traidingview_part | advanced_spoiler | more_news" mode="content"/>
	</xsl:template>

	<xsl:template match="audio">
		<div class="audio-wrap">
			<audio id="player{../@id}" src="{concat(../@path, .)}" width="100%" height="42" controls="controls"/>
		</div>
	</xsl:template>

	<xsl:template match="text_part" mode="content">
		<h3>
			<xsl:value-of select="name"/>
		</h3>
		<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="gallery_part" mode="content">
		<div class="fotorama" data-fit="cover">
			<xsl:for-each select="picture_pair">
				<img src="{@path}{big}" alt="{name}" data-caption="{name}"/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="code_part" mode="content">
		<xsl:value-of select="code" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="traidingview_part" mode="content">
		<div class="tradingview-widget-container">
		<div class="tradingview-widget-container__widget"></div>
		<div class="tradingview-widget-copyright">
		<a href="https://ru.tradingview.com/symbols/NASDAQ-{id}/" rel="noopener"></a>
		</div></div>
		<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-symbol-info.js" async="async">
			{
  "symbol":  <xsl:value-of select="concat($quote, id, $quote)"/>,
  "width": "100%",
  "locale": "ru",
  "colorTheme": "light",
  "isTransparent": false
}
		</script>
	</xsl:template>

	<xsl:template match="advanced_spoiler" mode="content">
		<div></div>
		<div class="advanced-spoiler">
			<div class="spoiler__title">
				<xsl:value-of select="name"/>
			</div>

			<div class="spoiler__content" style="display:none;">
				<xsl:apply-templates select="./*" mode="content"/>
			</div>
		</div>
		<div></div>
	</xsl:template>

	<xsl:template match="more_news" mode="content">
		<div class="col-full recommended">
			<div class="desktop-only background-rectangle"></div>
			<h3>Рекомендовано</h3>
			<xsl:if test="count(news_item | small_news_item) = 1">
				<xsl:for-each select="news_item | small_news_item">
					<div class="col-full recommended-item">
						<a href="{show_page}">
							<img src="{concat($base,'/', @path, small_pic)}"/>
							<span>
								<xsl:value-of select="name"/>
							</span>
						</a>
					</div>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="count(news_item | small_news_item) &gt; 1">
				<xsl:for-each select="news_item | small_news_item">
					<div class="col-six tab-full recommended-item">
						<a href="{show_page}">
							<img src="{concat($base,'/', @path, small_pic)}"/>
							<span>
								<xsl:value-of select="name"/>
							</span>
						</a>
					</div>
				</xsl:for-each>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="PAGE_TITLE">
		<xsl:param name="page"/>
		<xsl:if test="$page/header_pic != ''">
			<h1>
				<img src="{$page/@path}{$page/header_pic}" alt="{$page/name}"/>
			</h1>
		</xsl:if>
		<xsl:if test="not($page/header_pic) or $page/header_pic = ''">
			<h1>
				<xsl:value-of select="$page/name"/>
			</h1>
		</xsl:if>
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
			<option value="{$current}">
				<xsl:value-of select="$current"/>
			</option>
			<xsl:call-template name="number_option">
				<xsl:with-param name="max" select="$max"/>
				<xsl:with-param name="current" select="number($current) + number(1)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="SEO">
		<link rel="canonical" href="{concat($main_host, $canonical)}"/>
		<meta name="twitter:url" content="{concat($main_host, $canonical)}" />
		<meta property="og:url" content="{concat($main_host, $canonical)}" />
		<xsl:if test="$seo">
			<xsl:apply-templates select="$seo[1]"/>
		</xsl:if>
		<xsl:if test="not($seo) or $seo = ''">
			<title>
				<xsl:value-of select="concat($title, ' – Новости Respectiva.Pro')"/>
			</title>
			<meta property="og:title" content="{$title}" />
			<meta property="og:description" content="{replace($meta_description, $quote, '')}" />
			<meta name="twitter:title" content="{$title}" />
			<meta name="keywords" content="{replace($meta_keywords, $quote, '')}"/>	
		</xsl:if>
		<!-- <meta name="google-site-verification" content="{page/url_seo_wrap/google_verification}"/>
		<meta name="yandex-verification" content="{page/url_seo_wrap/yandex_verification}"/> -->
		<xsl:call-template name="MARKUP"/>
	</xsl:template>

	<xsl:template name="MARKUP"/>

	<xsl:template match="seo | url_seo">
		<title>
			<xsl:value-of select="concat(if(title != '') then title else $title, ' – Новости Respectiva.Pro')"/>	
		</title>
		<meta property="og:title" content="{if(title != '') then title else $title}"/>
		<xsl:if test="/page/variables/sni and //news_item[@key = /page/variables/sni]/main_pic != ''">
			<xsl:variable name="ni" select="//news_item[@key = /page/variables/sni]"/>
			<meta property="og:image" content="{concat($main_host, '/',$ni/@path,  if( $ni/soc_image != '') then $ni/soc_image else $ni[1]/main_pic)}"/>
		</xsl:if>
		<meta name="twitter:title" content="{$title}"/>
		<meta name="description" content="{description}"/>
		<meta property="og:description" content="{replace($meta_description, $quote, '')}" />
		<meta name="keywords" content="{keywords}"/>
		<xsl:value-of select="meta" disable-output-escaping="yes"/>
	</xsl:template>




</xsl:stylesheet>
