<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:saxon="http://icl.com/saxon"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="feedback.xsl"/>
	<xsl:import href="feedback2.xsl"/>
	<xsl:import href="paper_catalog.xsl"/>
	<xsl:import href="subscribe.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="critical_item"/>



	<!-- ****************************    СПЕЦ ВОЗМОЖНОСТИ    ******************************** -->

	<xsl:decimal-format name="ru" decimal-separator="," grouping-separator="&#160;" />

	<xsl:function name="f:strip_html" as="xs:string">
		<xsl:param name="html" as="xs:string?"/>
		<xsl:if test="$html != ''">
			<xsl:variable name="no_tags" select="replace($html, '&lt;/?\w+[^&lt;]*&gt;', '')"/>
			<xsl:variable name="no_lines" select="replace($no_tags, '&#xA;', ' ')"/>
			<xsl:variable name="nbsp" select="normalize-space(replace($no_lines, '&#38;nbsp;', ' '))"/>
			<xsl:value-of select="$nbsp" disable-output-escaping="yes"/>
		</xsl:if>
		<xsl:if test="not($html != '')">
			<xsl:sequence select="''" />
		</xsl:if>
	</xsl:function>

	<xsl:function name="f:format_price" as="xs:string">
		<xsl:param name="price" as="xs:string"/>
		<xsl:value-of select="format-number(number($price), '###&#160;###,##', 'ru')"/>
	</xsl:function>

	<!-- Выбирает нужную форму слова в зависимости от заданного числа. Формы слова - массив -->
	<xsl:function name="f:ending" as="xs:string">
		<xsl:param name="number_str" as="xs:string" />
		<xsl:param name="words" />
		<xsl:variable name="number" select="round(number($number_str))"/>
		<xsl:variable name="mod100" select="$number mod 100" />
		<xsl:variable name="mod10" select="$number mod 10" />
		<xsl:value-of select="if ($mod100 &gt; 10 and $mod100 &lt; 20) then $words[3]
						 else if ($mod10 = 1) then $words[1]
						 else if ($mod10 &gt; 0 and $mod10 &lt; 5) then $words[2]
						 else $words[3]" />
	</xsl:function>


	<xsl:function name="f:day_month" as="xs:string">
		<xsl:param name="date" as="xs:string" />
		<xsl:variable name="parts" select="tokenize($date, '\.')"/>
		<xsl:variable name="month" select="number($parts[2])"/>
		<xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
		<xsl:value-of select="concat(number($parts[1]), ' ', $months[$month])"/>
	</xsl:function>


	<xsl:template match="footer_link[not(file) or file = '']">
		<xsl:param name="block"/>
		<a href="{link}" class="btn btn-sm btn-success{' btn-block'[$block]}" type="button" data-toggle="modal">
			<xsl:if test="download='да'"><xsl:attribute name="download" select="tokenize(link, '/')[last()]"/></xsl:if>
			<xsl:value-of select="name"/>
		</a>
	</xsl:template>

	<xsl:template match="footer_link[file and file != '']">
		<xsl:param name="block"/>
		<a href="{@path}{file}" class="btn btn-sm btn-success{' btn-block'[$block]}" type="button" data-toggle="modal">
			<xsl:if test="download='да'"><xsl:attribute name="download" select="file"/></xsl:if>
			<xsl:value-of select="name"/>
		</a>
	</xsl:template>


	<!-- ****************************    SEO (для всех страниц)    ******************************** -->

	<xsl:variable name="source" select="concat('/', page/source_link)"/>
	<xsl:variable name="eng_base" select="'http://termobrest.net'"/>
	<xsl:variable name="base" select="'https://termobrest.ru'"/>
	<xsl:variable name="local_seo" select="//url_seo[contains($source, url)]"/>

	<xsl:variable name="has_tag" select="page/variables/tag"/>
	<xsl:variable name="canonical" select="if(page/@name = 'index') then '' else tokenize($source, '\?')[1]"/>
	<xsl:variable name="canonical_link" select="concat($base, if(not($local_seo) and not($has_tag)) then $canonical else $source)" />

	<xsl:variable name="eng_alternate_link" select="concat($eng_base, if(not($local_seo)) then $canonical else $source)" />

	<xsl:variable name="seo" select="if(not($local_seo)) then //seo else $local_seo"/>

	<!-- LOCAL - это значения полей, которые должны быть, в случае если не создан (или не доступен для создания) айтем seo -->
	<xsl:variable name="local_title" select="''"/>
	<xsl:variable name="local_keywords" select="''"/>
	<xsl:variable name="local_description" select="''"/>
	<xsl:variable name="local_h1" select="''"/>
	<xsl:variable name="extra_title" select="''"/>
	<xsl:variable name="extra_keywords" select="''"/>
	<xsl:variable name="extra_description" select="''"/>

	<!-- SEO - это значения полей, которые получаются с учетом заданных (или не заданных) значений в айтеме SEO  -->
	<!-- Их можно в качестве исключения использовать локально на страницах для установки ОСНОВНЫХ значений, если нужна какая-то особая логика  -->
	<xsl:variable name="seo_title" select="if ($seo and $seo/title and $seo/title != '') then concat($seo/title, ' ', $extra_title) else $local_title"/>
	<xsl:variable name="seo_keywords" select="if ($seo and $seo/keywords and $seo/keywords != '') then concat($seo/keywords, ' ', $extra_keywords) else $local_keywords"/>
	<xsl:variable name="seo_description" select="if ($seo and $seo/description and $seo/description != '') then concat($seo/description, ' ', $extra_description) else $local_description"/>
	<xsl:variable name="seo_h1" select="if ($seo and $seo/h1 and $seo/h1 != '') then $seo/h1 else $local_h1"/>

	<!-- ОСНОВНЫЕ значения, используются для вывода соответствующих полей странице, которая возвращается пользователю -->
	<xsl:variable name="title" select="$seo_title"/>
	<xsl:variable name="keywords" select="$seo_keywords"/>
	<xsl:variable name="description" select="$seo_description"/>

	<xsl:variable name="h1" select="$seo_h1"/>


	<xsl:template name="TITLE">
		<title><xsl:value-of select="$title"/></title>
		<meta property="og:title" content="{$title}" />
	</xsl:template>

	<xsl:variable name="need_seo_progon" select="true()"/>



	<!-- ****************************    ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->


<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:template>

	<xsl:template name="CONTENT"/>

	<xsl:template name="SCRIPTS"/>

	<xsl:template name="POPUPS"/>

	<xsl:template name="CATALOG_MARKUP"/>


	<xsl:variable name="active_mmi" select="'index'"/>
	<xsl:variable name="proddd" select="page" />


	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template match="/">

	<xsl:call-template name="DOCTYPE"/>
	<html lang="ru" xmlns:og="http://ogp.me/ns#"
xmlns:fb="http://ogp.me/ns/fb#">
	<head>
		<meta charset="utf-8" />
		<xsl:if test="not($critical_item)">
			<meta name="robots" content="noindex"/>
		</xsl:if>
		<xsl:text disable-output-escaping="yes">
			&lt;!--
		</xsl:text>
<xsl:value-of select="$source"/>
		<xsl:text disable-output-escaping="yes">
			--&gt;
		</xsl:text>


		<meta property="og:url" content="{$canonical_link}" />
		<base href="{$base}" />
		<link rel="canonical" href="{$canonical_link}" />
		<link rel="alternate" href="{$eng_alternate_link}" hreflang="en" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<!-- Global site tag (gtag.js) - Google Analytics -->
		<script async="async" src="https://www.googletagmanager.com/gtag/js?id=UA-456325-50"></script>
		<script>
		  window.dataLayer = window.dataLayer || [];
		  function gtag(){dataLayer.push(arguments);}
		  gtag('js', new Date());

		  gtag('config', 'UA-456325-50');
		</script>
		<!-- END_Global site tag (gtag.js) - Google Analytics -->

		<xsl:call-template name="TITLE" />
		<xsl:if test="$keywords"><meta name="keywords" content="{$keywords}" /></xsl:if>
		<xsl:if test="$description">
			<meta name="description" content="{$description}" />
			<meta property="og:description" content="{$description}" />
		</xsl:if>
		<xsl:for-each select="$seo/meta">
			<meta name="{name}" content="{value}" />
		</xsl:for-each>
		<xsl:if test="page/@name = 'prod' and $proddd/@path">
			<meta property="og:image" content="{$base}/{$proddd/@path}{$proddd/img_big}" />
			<meta property="og:type" content="product" />
			<meta property="og:type" content="website" />
		</xsl:if>
		<xsl:if test="page/@name != 'prod'">
			<meta property="og:image" content="{$base}/sitefiles/1/76/77/696/697/termobrest___building.jpg" />
		</xsl:if>
		<xsl:if test="page/@name=('index', 'context_form_success','armtorg', 'sec')">
			<meta property="og:type" content="website" />
		</xsl:if>
		<meta name="yandex-verification" content="6ed4821288e67a3f" />
		<meta name="google-site-verification" content="XUUvG4jtRNlzOvCXj4mUhja4izcZrVAr0Bf4KZjQt_U" />
		<link rel="stylesheet" href="css/app.css?version=1.6"/>
		<link rel="stylesheet" href="css/styles.css?version=1.1"/>
		<link rel="stylesheet" href="css/styles-seo.css?version=1.5"/>
		<link rel="stylesheet" href="css/fixes.css?version=1.2"/>
		<link rel="stylesheet" href="fotorama/fotorama.css"/>

		<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
		 
		<!-- Yandex.Metrika counter -->
		<script type="text/javascript">
	(function (d, w, c) {
		(w[c] = w[c] || []).push(function() {
			try {
				w.yaCounter44439058 = new Ya.Metrika({
					id:44439058,
					clickmap:true,
					trackLinks:true,
					accurateTrackBounce:true,
					webvisor:true,
					trackHash:true
				});
			} catch(e) { }
		});

		var n = d.getElementsByTagName("script")[0],
			s = d.createElement("script"),
			f = function () { n.parentNode.insertBefore(s, n); };
		s.type = "text/javascript";
		s.async = true;
		s.src = "https://mc.yandex.ru/metrika/watch.js";

		if (w.opera == "[object Opera]") {
			d.addEventListener("DOMContentLoaded", f, false);
		} else { f(); }
	})(document, window, "yandex_metrika_callbacks");
		</script>
<!-- /Yandex.Metrika counter -->

<xsl:if test="page/@name=('index', 'context_form_success','armtorg')">
		<style type="text/css">
			<xsl:text disable-output-escaping="yes">
			.cf7 &gt; .slide-image, #mobile-slide-image &gt; .mobile-slide-image{
				-webkit-transition: opacity 1s ease-in-out;
				-moz-transition: opacity 1s ease-in-out;
				-o-transition: opacity 1s ease-in-out;
				transition: opacity 1s ease-in-out;
				opacity:0;
				-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
				filter: alpha(opacity=0);
			}
			.cf7 .txt{
				-webkit-transition: opacity 1s ease-in-out;
				-moz-transition: opacity 1s ease-in-out;
				-o-transition: opacity 1s ease-in-out;
				transition: opacity 1s ease-in-out;
				opacity:0;
				-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
				filter: alpha(opacity=0);
				display: none;
			}
			.cf7 .txtbtn{
				-webkit-transition: opacity 1s ease-in-out;
				-moz-transition: opacity 1s ease-in-out;
				-o-transition: opacity 1s ease-in-out;
				transition: opacity 1s ease-in-out;
				opacity:0;
				-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
				filter: alpha(opacity=0);
				display: none;
			}
			.cf7 &gt; .slide-image.opaque, .cf7 .txt.opaque, .cf7 .txtbtn.opaque, #mobile-slide-image &gt; .mobile-slide-image.opaque{
				display: block;
				opacity:1;
				-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=100)";
				filter: alpha(opacity=1);
			}
			</xsl:text>
		</style>
</xsl:if>

<xsl:if test="page/@name = 'contacts'">
	<script type="application/ld+json">
	{
	  "@context": "http://schema.org",
	  "@type": "Organization",
	  "url": "http://termobrest.ru/",
	  "name": "ТермоБрест",
	  "contactPoint": {
		"@type": "ContactPoint",
		"telephone": "+375 (162) 53-64-80",
		"contactType": "Организация сбыта продукции"
	  }
	}
	</script>
</xsl:if>
<xsl:if test="page/@name = 'sec' or page/@name = 'catalog'">
<script type='application/ld+json'>
{
	"@context": "http://schema.org/",
	"@type": "Product",
	"name": "ТермоБрест",
	"aggregateRating": {
		"@type": "AggregateRating",
		"ratingCount": "21",
		"reviewCount": "21",
		"bestRating": "5",
		"ratingValue": "4,9",
		"worstRating": "1",
		"name": "ТермоБрест"
	}
}
</script>
<xsl:call-template name="CATALOG_MARKUP"/>
</xsl:if>

<xsl:if test="page/@name = 'prod'">
	<script type='application/ld+json'>
		{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat('&#34;',$h1, '&#34;')"/>,
			"image": "https://termobrest.ru/sitefiles/1/5/6/257/vn1n_02.jpg",
			 "description": <xsl:value-of select="concat('&#34;',substring(f:strip_html($proddd/text), 1,200), '&#34;')"/>,
			"brand": "Термобрест",
			"sku": <xsl:value-of select="concat('&#34;&#91;', $proddd/@id, '&#93;&#34;')"/>,
			"aggregateRating": {
				"@type": "AggregateRating",
				"ratingCount": "21",
				"reviewCount": "21",
				"bestRating": "5",
				"ratingValue": "4,9",
				"worstRating": "1",
				"name": "ТермоБрест"
			}
		}
	</script>
</xsl:if>
	
	</head>
	<body>
		<xsl:if test="page/@name=('index', 'context_form_success','armtorg')"><xsl:attribute name="class" select="'main-page'"/></xsl:if>

		<div class="height-wrapper">

			<header>
				<div class="header-wrapper hidden-xs navbar-fixed-top">
					<div class="container">
						<a href="{$base}" class="logo-placeholder"></a>
						<form method="post" action="{page/search_link}" class="navbar-form navbar-left hidden-xs">
							<div class="form-group">
								<input value="{page/variables/q}" name="q" type="text" class="form-control" placeholder="Поиск по каталогу" />
							</div>
							<button type="submit" class="btn btn-default" onclick="$(this).closest('form').submit()">&#160;</button>
						</form>
						<img src="img/logos_top.png" alt="" />
					</div>
				</div>

				<nav class="navbar navbar-default">
					<div class="container">
						<!-- Brand and toggle get grouped for better mobile display -->
						<div class="navbar-header">
							<button type="button" class="navbar-toggle collapsed"
								data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
								aria-expanded="false">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand hidden-sm hidden-md hidden-lg" href="#">
								<img src="img/termobrest_logo.png" alt="" />
							</a>
						</div>

						<!-- Collect the nav links, forms, and other content for toggling -->
						<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
							<ul class="nav navbar-nav">
								<a href="https://termobrest.net"><span>ENG</span>|РУС</a>
								<li class="dropdown{' active-link'[$active_mmi = 'catalog']}"> <!-- класс active выделяет текущий раздел -->
									<a href="{//catalog_link}" id="catalog-link" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Газовая арматура</a>
									<ul class="dropdown-menu">
										<xsl:for-each select="page/catalog/main_section">
											<li><a href="{show_section}"><xsl:value-of select="name"/></a></li>
										</xsl:for-each>
										<li><a href="{page/new_products_link}">Новинки</a></li>
									</ul>
								</li>
								<li class="dropdown{' active-link'[$active_mmi = 'about']}">
									<a href="{page/about/about_section[1]/show_section}" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">О компании</a>
									<ul class="dropdown-menu">
										<xsl:for-each select="page/about//about_section">
											<li>
												<a href="{show_section}"><xsl:value-of select="name"/></a>
											</li>
										</xsl:for-each>
									</ul>
								</li>
								<li class="dropdown{' active-link'[$active_mmi = 'news']}">
									<a href="{page/news/news_section[1]/show_section}" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Новости</a>
									<ul class="dropdown-menu">
										<xsl:for-each select="page/news/news_section">
											<li><a href="{show_section}"><xsl:value-of select="name"/></a></li>
										</xsl:for-each>
									</ul>
								</li>
								<li class="dropdown{' active-link'[$active_mmi = 'docs']}">
									<a href="{page/docs/doc_section[1]/show_section}" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Документы</a>
									<ul class="dropdown-menu">
										<xsl:for-each select="page/docs/doc_section[name != 'ПРАЙС-ЛИСТ для потребителей РФ']">
											<li>
												<a href="{show_section}"><xsl:value-of select="name"/></a>
											</li>
										</xsl:for-each>
									</ul>
								</li>
								<li class="dropdown{' active-link'[$active_mmi = '3d']}">
									<a href="{page/three_dmodels/doc_section[1]/show_section}" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">3D модели</a>
									<ul class="dropdown-menu">
										<xsl:for-each select="page/three_dmodels/doc_section">
											<li>
												<a href="{show_section}"><xsl:value-of select="name"/></a>
											</li>
										</xsl:for-each>
									</ul>
								</li>
								<li class="dropdown{' active-link'[$active_mmi = 'dealers']}">
									<a href="{page/dealers_link}" class="prevent dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Дилеры</a>
									<ul class="dropdown-menu">
										<li>
											<a href="{page/dealers_link}">Карта дилеров / партнеров</a>
										</li>
										<li>
											<a href="{page/all_dealers_link}">Дилеры СП «ТермоБрест» ООО</a>
										</li>
										<li>
											<a href="{page/all_partners_link}">Деловые партнеры СП «ТермоБрест» ООО</a>
										</li>
										<li>
											<a href="{page/dealers_text_page_link}">Рейтинг дилеров</a>
										</li>
										<li>
											<a href="https://termobrest.ru/docs/prais_list/">
												ПРАЙС-ЛИСТ для потребителей РФ
											</a>
										</li>
									</ul>
								</li>
								<li class="{'active-link'[$active_mmi = 'contacts']}">
									<a href="{page/contacts_link}">Контакты</a>
								</li>
								<li class="new-link">
								<!-- <li class="new-link {'active-link'[$active_mmi = 'about']}"> -->
									<a style="font-weight:bold" href="https://e-catalogue.termobrest.ru/">Электронный каталог</a>
								</li>
							</ul>
							<form method="post" action="{page/search_link}" class="navbar-form navbar-left hidden-sm hidden-md hidden-lg">
								<div class="form-group">
									<input value="{page/variables/q}" name="q" type="text" class="form-control" placeholder="Поиск по каталогу" />
								</div>
								<button type="submit" class="btn btn-default" onclick="$(this).closest('form').submit()">&#160;</button>
							</form>
						</div><!-- /.navbar-collapse -->
					</div><!-- /.container-fluid -->
				</nav>

			</header>
			<main>
				<xsl:call-template name="CONTENT" />
				<xsl:if test="$seo/progon != '' and not(page/@name=('index', 'context_form_success','armtorg')) and $need_seo_progon">
					<div class="container main-content">
						<div class="row">
							<div class="col-xs-12 col-sm-12 col-md-12">
								<div style="color: #545454; font-size: 12px;">
									<xsl:value-of select="$seo/progon" disable-output-escaping="yes"/>
								</div>
							</div>
						</div>
					</div>
				</xsl:if>
			</main>
			<footer class="footer">
				<div class="container">
					<div class="col-sm-10">
						<div class="buttons hidden-sm hidden-md hidden-lg">
							<xsl:apply-templates select="page/footer/footer_link[not(code) or (code != '1' and code != '2')]">
								<xsl:with-param name="block" select="true()"/>
							</xsl:apply-templates>
							<xsl:call-template name="FEEDBACK_2_BUTTON">
								<xsl:with-param name="block" select="true()"/>
							</xsl:call-template>
							<xsl:call-template name="PAPER_BUTTON">
								<xsl:with-param name="block" select="true()"/>
							</xsl:call-template>
							<xsl:call-template name="SUBSCRIBE_BUTTON">
								<xsl:with-param name="block" select="true()"/>
							</xsl:call-template>
						</div>
						<p>СП ТермоБрест ООО</p>
						<div class="btn-toolbar hidden-xs">
							<xsl:apply-templates select="page/footer/footer_link[not(code) or (code != '1' and code != '2')]"/>
							<xsl:call-template name="FEEDBACK_2_BUTTON"/>
							<xsl:call-template name="PAPER_BUTTON"/>
							<xsl:call-template name="SUBSCRIBE_BUTTON"/>
						</div>
						<div style="display: inline-block; padding-left: 30px; background: url(../img/icon_forever.png) no-repeat 0% 2px; font-size: 12px;">
							<a href="http://forever.by" class="forever">Разработка и продвижение сайтов</a><br/> студия веб-дизайна Forever
						</div>
						<img src="images/brest1000.png" alt="Бресту 1000 лет" title="Бресту 1000 лет" style="display: inline-block; margin-left: 24px; vertical-align: top;"/>
					</div>
					<div class="col-sm-2" style="padding: 0;">
						<a href="{page/contacts_link}" class="btn btn-sm btn-success" style="width: 100%; padding:0; margin-top: 30px">Обратная связь</a>
						<div class="icons-links" style="margin-top:0;">
							<a href="https://www.facebook.com/termobrest.ru/">
								<img src="images/soc-2.png" alt=""/>
							</a>
							<a href="https://www.instagram.com/termobrest.ru/">
								<img src="images/soc-4.png" alt=""/>
							</a>
							<a href="https://www.youtube.com/channel/UC0gvt4On84SIDHkMO4Y85cA">
								<img src="images/soc-3.png" alt=""/>
							</a>
							<a href="https://vk.com/termobrest">
								<img src="images/soc-1.png" alt=""/>
							</a>
							<a href="https://t.me/termobrest">
								<img src="images/telegram.png" alt=""/>
							</a>
						</div>
					</div>
					<!-- <div class="col-sm-12">
						<iframe src="//widget.stapico.ru/?q=termobrest.ru&amp;s=100&amp;w=3&amp;h=3&amp;b=1&amp;p=2&amp;effect=2" allowtransparency="true" frameborder="0" scrolling="no" style="border:none;overflow:hidden;width:356px; height: 481px" />

						<div class="fb-page" data-href="https://www.facebook.com/termobrest.ru" data-tabs="timeline" data-width="300" data-small-header="false" data-adapt-container-width="true" data-hide-cover="false" data-show-facepile="true"><blockquote cite="https://www.facebook.com/termobrest.ru" class="fb-xfbml-parse-ignore"><a href="https://www.facebook.com/termobrest.ru">СП "ТермоБрест"</a></blockquote></div>
					</div> -->
				</div>
			</footer>

		</div>

		
	<xsl:if test="page/@name=('index','context_form_success','armtorg')">
		<script type="text/javascript">
		$(document).ready(function() {
		  $(".cf7_controls").on('click', 'a', function(e) {
			e.preventDefault();
			if(typeof sliderTimeout != "undefined"){
				clearTimeout(sliderTimeout);
			}

			var newImage = $(this).index();
			el = $(this).closest(".cf7");
			showSlide(newImage, el);
		  });

		  autoplay();

		});

		function autoplay(){
			$(".cf7").each(function(){
				next = $(this).children(".slide-image.opaque").next(".slide-image").index();
				next = (next != -1)? next : 0;
				showSlide(next, $(this));
			});
			sliderTimeout = setTimeout(autoplay, 7000);
		}

		function showSlide(index, el){

			el.children(".slide-image").removeClass("opaque");
			$("#mobile-slide-image").children(".mobile-slide-image").removeClass("opaque");
			el.find(".txt").removeClass("opaque");
			el.find(".txt").eq(index).addClass("opaque");
			el.find(".txtbtn").removeClass("opaque").css('display', 'none');
			el.find(".txtbtn").eq(index).addClass("opaque").css('display', 'block');
			el.children(".slide-image").eq(index).addClass("opaque");
			$("#mobile-slide-image").children(".mobile-slide-image").eq(index).addClass("opaque");
			$(".cf7_controls a").removeClass("active");
			el.find(".cf7_controls a").eq(index).addClass("active");

			$(".desctop-addon").css({opacity: 0, display: "none"});
			$(".desctop-addon").eq(index).css({display : ""});
			$(".desctop-addon").eq(index).animate({"opacity": 1}, 500);

		}
		</script>
</xsl:if>
		<script src="js/bootstrap.min.js"></script>

		<script type="text/javascript">
			<xsl:text disable-output-escaping="yes">
				$(document).ready(function(){

					windowWidth = window.innerWidth ? window.innerWidth : $(window).width();

					if (windowWidth &gt; 767) {
							$d = $(".dropdown-toggle");
							$d.removeClass("dropdown-toggle")
							.removeAttr("data-toggle")
							.removeAttr("role")
							.removeAttr("aria-haspopup")
							.removeAttr("aria-expanded");
							$("#bs-example-navbar-collapse-1 *").unbind("click");
							$d.click(function(e){
								if($(this).attr("href") == "#"){
									e.preventDefault();
								}
							});
							<!-- document.location.href = $(this).attr("href"); -->


						style = ".dropdown:hover &gt; .dropdown-menu {display: block;}"
						$("head").append("&lt;style type='text/css' &gt;"+style+"&lt;/style&gt;");
					}else{
						$(".prevent").click(function(e){
							e.preventDefault();
						});
					}
				});
			</xsl:text>
		</script>

		<script type="text/javascript" id="form-min-js" src="js/jquery.form.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="fotorama/fotorama.js"></script>
		<xsl:if test="/page/@name != 'context_form'">
			<script type="text/javascript" src="js/gop-stop.js"></script>
			<script>
				gopRedirect();
			</script>
		</xsl:if>
		<xsl:call-template name="SCRIPTS"/>
		<xsl:call-template name="POPUPS"/>
		<xsl:call-template name="FEEDBACK_FORM_2"/>
		<xsl:call-template name="PAPER_FORM"/>
		<xsl:call-template name="SUBSCRIBE_FORM"/>
	</body>
	</html>
	</xsl:template>


	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->

	<xsl:template match="*" mode="content">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	<xsl:apply-templates select="galery_part | text_part | object_part"/>
	</xsl:template>

	<xsl:template match="text_part">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="object_part">
	<xsl:value-of select="code" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="galery_part">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	<div class="fotoramaContainer">
		<div class="fotorama" data-nav="thumbs" data-width="100%" data-allowfullscreen="true">
			<xsl:apply-templates select="picture_pair"/>
		</div>
	</div>
	</xsl:template>

	<xsl:template match="picture_pair">
	<a href="{@path}{big}" data-caption="{name}"><img src="{@path}{small}" height="65" alt="{name}"/></a>
	</xsl:template>

<!-- 	<xsl:template match="video">
	<a href="{link}" data-caption="{@path}{big}" title="{name}"><img src="{@path}{small}" height="65" alt="{name}"/></a>
	</xsl:template> -->

	<!-- ****************************    Добавление параметра к ссылке    ******************************** -->

	<xsl:function name="f:addQueryVar" as="xs:string">
		<xsl:param name="link" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value" as="xs:string"/>
		<xsl:value-of select="if (contains($link, '?')) then concat($link, '&amp;', $name, '=', $value) else concat($link, '?', $name, '=', $value)"/>
	</xsl:function>

	<!-- ****************************    Добавление параметра к ссылке    ******************************** -->

	<!-- Усановка параметра, если его нет, или замена значения параметра (в том числе удаление) -->
	<xsl:function name="f:set_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="encode-for-uri(string($value))"/>
		<xsl:value-of
			select="if (not($val_enc) or $val_enc = '') then replace(replace($url, concat('(\?|&amp;)', $name, '=', '.*?($|&amp;)'), '$1'), '&amp;$|\?$', '')
					else if (contains($url, concat($name, '='))) then replace($url, concat($name, '=', '.*?($|&amp;)'), concat($name, '=', $value, '$1'))
					else if (contains($url, '?')) then concat($url, '&amp;', $name, '=', $val_enc)
					else concat($url, '?', $name, '=', $val_enc)"/>
	</xsl:function>

	<!-- Добавление параметра, даже если такой параметр уже есть (множественные значения) -->
	<xsl:function name="f:add_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="encode-for-uri($value)"/>
		<xsl:value-of
			select="if (contains($url, '?')) then concat($url, '&amp;', $name, '=', $val_enc)
					else concat($url, '?', $name, '=', $val_enc)"/>
	</xsl:function>

	<!-- Удаление параметра с определенным значением -->
	<xsl:function name="f:remove_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="replace(encode-for-uri($value), '%20', '\\+')"/>
<!-- 		<xsl:variable name="start" select="substring-before($url, concat($name, '=', $val_enc))"/> -->
<!-- 		<xsl:variable name="end_b" select="substring-after($url, concat($name, '=', $val_enc))"/> -->
<!-- 		<xsl:variable name="end" select="if (starts-with($end_b, '&amp;')) then substring-after($end_b, '&amp;') else $end_b"/> -->
<!-- 		<xsl:value-of select="$url"/> -->
		<!--
		<xsl:value-of select="if (string-length($start) = 0) then $start else concat($start, $end)"/>
		-->
		<xsl:value-of
			select="replace(replace($url, concat('(\?|&amp;)', $name, '=', $val_enc, '($|&amp;)'), '$1'), '&amp;$|\?$', '')"/>
	</xsl:function>

	<xsl:template match="*" mode="LINK_ADD_VARIABLE_QUERY">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<a class="{$class}" href="{.}{'?'[not(contains(current(), '?'))]}{'&amp;'[contains(current(), '?')]}{$name}={$value}"><xsl:value-of select="$text"/></a>
	</xsl:template>

</xsl:stylesheet>
