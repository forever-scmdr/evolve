<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="feedback.xsl"/>
	<xsl:import href="feedback2.xsl"/>
	<xsl:import href="subscribe.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="src" select="/page/source_link" />
	<xsl:variable name="src_before" select="substring-before($src, 'i-')" />
	<xsl:variable name="src_after" select="substring-after($src, 'i-')" />
	<xsl:variable name="canonical" select="concat($src_before, $src_after)"/>

	<!-- ****************************    СПЕЦ ВОЗМОЖНОСТИ    ******************************** -->

	<xsl:decimal-format name="ru" decimal-separator="," grouping-separator="&#160;" />

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

	<!-- ****************************    ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->

<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:template>

	<xsl:variable name="title" select="''"/>

	<xsl:template name="TITLE">ТермоБрест<xsl:if test="$title != ''"> - </xsl:if><xsl:value-of select="$title"/></xsl:template>

	<xsl:template name="CONTENT"/>
	
	<xsl:template name="SCRIPTS"/>

	<xsl:template name="POPUPS"/>
	

	<xsl:variable name="active_mmi" select="'index'"/>
	<xsl:variable name="base" select="replace(/page/base, 'http://', 'https://')"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html>
	<head>
		<meta charset="utf-8" />
		<meta name="robots" content="noindex" />
		<base href="{$base}" />
		<link rel="canonical" href="{$base}/{if(page/@name != 'i-index') then page/$canonical else ''}" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<title><xsl:call-template name="TITLE" /></title>
		<meta name="yandex-verification" content="6ed4821288e67a3f" />
		<link rel="stylesheet" href="css/app.css"/>
		<link rel="stylesheet" href="fotorama/fotorama.css"/>
	</head>
	<body style="background: #fff; padding-top: 0;position:absolute;">
		<div id="contentWrap" style="padding:0; margin:0; height:auto;">
			<xsl:call-template name="CONTENT"/>
			<div style="padding-bottom:12px;clear:both;" data-iframe-height=""></div>
		</div>
		<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/jquery.form.min.js"></script>
		<script type="text/javascript" src="js/ajax.js"></script>
		<script type="text/javascript" src="fotorama/fotorama.js"></script>
		<xsl:call-template name="SCRIPTS"/>
		<xsl:call-template name="POPUPS"/>
		<xsl:call-template name="FEEDBACK_FORM_2"/>
		<xsl:call-template name="SUBSCRIBE_FORM"/>
		<!-- <script type="text/javascript">
			$(document).ready(function() {
				$(".fancybox").fancybox();
				$(".fancybox-full").fancybox({
					afterShow: function() {
						$('<div class="fancybox-fullsize" title="Esc - закрыть"></div>').appendTo(this.skin).click(function() {
						  $.fancybox.toggle();
						  $(this).toggleClass('fancybox-fullsize fancybox-fullsize-r');
						});
					  }
				});
				$('.popupButton').click(function() {
					$('#' + $(this).attr('rel')).fadeIn(200);
				});
				$('.close').click(function() {
					$(this).closest('.popup-container').fadeOut(200);
				});
			});
		</script> -->
		<xsl:if test="/page/@name != 'termobrest_news' and /page/@name != 'termobrest_news_item'">
			<script type="text/javascript" src="{$base}/js/postmessage.js"></script>
			<script type="text/javascript" src="{$base}/js/iframe-script.js"></script>
		</xsl:if>
		<script type="text/javascript" src="{$base}/js/iframeResizer.contentWindow.min.js"></script>
		<script>
		  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
		
		  ga('create', 'UA-456325-50', 'auto');
		  ga('send', 'pageview');
		
		</script>
	
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
	
	</body>
	</html>
	</xsl:template>
	

	<!-- ****************************    БЛОКИ НА СТРАНИЦЕ    ******************************** -->

	<xsl:template match="*" mode="content">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	<xsl:apply-templates select="gallery_part | text_part | object_part"/>
	</xsl:template>

	<xsl:template match="text_part">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="object_part">
	<xsl:value-of select="code" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template match="gallery_part">
	<xsl:value-of select="text" disable-output-escaping="yes"/>
	<div class="fotoramaContainer">
		<div class="fotorama" data-nav="thumbs" data-maxwidth="685" data-maxheight="464" data-allowfullscreen="true">
			<xsl:apply-templates select="picture_pair | video"/>
		</div>
	</div>
	</xsl:template>
	
	<xsl:template match="picture_pair">
	<a href="{@path}{big}"><img onerror="this.src = 'images/noimage.png'" src="{@path}{small}" height="65" alt="{name}"/></a>
	</xsl:template>
	
	<xsl:template match="video">
	<a href="{link}" data-img="{@path}{big}"><img onerror="this.src = 'images/noimage.png'" src="{@path}{small}" height="65" alt="{name}"/></a>
	</xsl:template>

	<!-- ****************************    Добавление параметра к ссылке    ******************************** -->
	
	<xsl:function name="f:addQueryVar" as="xs:string">
		<xsl:param name="link" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value" as="xs:string"/>
		<xsl:value-of select="if (contains($link, '?')) then concat($link, '&amp;', $name, '=', $value) else concat($link, '?', $name, '=', $value)"/>
	</xsl:function>
	
	<xsl:template match="*" mode="LINK_ADD_VARIABLE_QUERY">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<a class="{$class}" href="{.}{'?'[not(contains(current(), '?'))]}{'&amp;'[contains(current(), '?')]}{$name}={$value}"><xsl:value-of select="$text"/></a>
	</xsl:template>

</xsl:stylesheet>