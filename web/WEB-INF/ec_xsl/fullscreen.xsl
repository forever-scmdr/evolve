<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:saxon="http://icl.com/saxon"
	xmlns:f="f:f"
	version="2.0">
	
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>


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

	<xsl:variable name="source" select="page/source_link"/>
	<xsl:variable name="eng_base" select="'http://termobrest.net'"/>
	<xsl:variable name="base" select="'https://termobrest.ru'"/>
	<xsl:variable name="local_seo" select="//url_seo[contains($source, url)]"/>

	<xsl:variable name="canonical" select="if(page/@name = 'index') then '' else concat('/',tokenize($source, '\?')[1])"/>
	<xsl:variable name="canonical_link" select="concat($base, if(not($local_seo)) then $canonical else $source)" />

	<xsl:variable name="eng_alternate_link" select="concat($eng_base, if(not($local_seo)) then $canonical else $source)" />

	<xsl:variable name="seo" select="if(not($local_seo)) then //seo else $local_seo"/>

	<!-- LOCAL - это значения полей, которые должны быть, в случае если не создан (или не доступен для создания) айтем seo -->
	<xsl:variable name="local_title" select="''"/>
	<xsl:variable name="local_keywords" select="''"/>
	<xsl:variable name="local_description" select="''"/>
	<xsl:variable name="local_h1" select="''"/>

	<!-- SEO - это значения полей, которые получаются с учетом заданных (или не заданных) значений в айтеме SEO  -->
	<!-- Их можно в качестве исключения использовать локально на страницах для установки ОСНОВНЫХ значений, если нужна какая-то особая логика  -->
	<xsl:variable name="seo_title" select="if ($seo and $seo/title and $seo/title != '') then $seo/title else $local_title"/>
	<xsl:variable name="seo_keywords" select="if ($seo and $seo/keywords and $seo/keywords != '') then $seo/keywords else $local_keywords"/>
	<xsl:variable name="seo_description" select="if ($seo and $seo/description and $seo/description != '') then $seo/description else $local_description"/>
	<xsl:variable name="seo_h1" select="if ($seo and $seo/h1 and $seo/h1 != '') then $seo/h1 else $local_h1"/>

	<!-- ОСНОВНЫЕ значения, используются для вывода соответствующих полей странице, которая возвращается пользователю -->
	<xsl:variable name="title" select="$seo_title"/>
	<xsl:variable name="keywords" select="$seo_keywords"/>
	<xsl:variable name="description" select="$seo_description"/>

	<xsl:variable name="is_ni" select="page/news_item"/>
	<xsl:variable name="is_about" select="page/about_section"/>
	<xsl:variable name="sec" select="page/news_item | page/about_section"/>

	<xsl:variable name="h1" select="$seo_h1"/>


	<xsl:template name="TITLE">
		<title><xsl:value-of select="$title"/></title>
		<!-- <meta property="og:title" content="{$title}" /> -->
	</xsl:template>

	
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<base href="{$base}" />
				<meta name="yandex-verification" content="6ed4821288e67a3f" />
				<meta name="google-site-verification" content="XUUvG4jtRNlzOvCXj4mUhja4izcZrVAr0Bf4KZjQt_U" />
				<link rel="stylesheet" href="css/app.css?version=1.6"/>
				<link rel="stylesheet" href="css/styles.css?version=1.1"/>
				<link rel="stylesheet" href="css/styles-seo.css?version=1.5"/>
				<link rel="stylesheet" href="css/fixes.css?version=1.2"/>
				<link rel="stylesheet" href="fotorama/fotorama.css"/>
				<style type="text/css">
					*{font-size: 12px;
					font-family: Arial;}
					td{padding: 3px 5px; border: 1px solid #ccc;}
					table{
						bordder-collapse: collapse;
					}
				</style>
			</head>
			<body style="padding-top:0; background: #fff; padding: 15px 25px;">
				<div style="padding-bottom: 5px; border-bottom: 1px solid #000; margin-bottom: 20px;">
					<xsl:if test="$is_ni">
						<a href="{if(//fullscreen_only = '1') then //show_section else //ni_back }" style="font-size: 18px;">Выйти из полноэкранного режима</a>
					</xsl:if>
					<xsl:if test="$is_about">
						<a href="{//about_back}" style="font-size: 18px;">Выйти из полноэкранного режима</a>
					</xsl:if>
				</div>
				<div class="content-block" style="background: #fff;">
					<xsl:apply-templates select="$sec" mode="content"/>
				</div>
				<xsl:call-template name="SCRIPTS" />
			</body>
		</html>
	</xsl:template>


<xsl:template name="SCRIPTS">
	<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script type="text/javascript" id="form-min-js" src="js/jquery.form.min.js"></script>
	<script type="text/javascript" src="js/ajax.js"></script>
	<script type="text/javascript" src="fotorama/fotorama.js"></script>
	<script>
		$(document).ready(function(){
			$("table, td").removeAttr("width");
		});
	</script>
</xsl:template>	

<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:template>


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


</xsl:stylesheet>