<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:saxon="http://icl.com/saxon"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>

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


	<xsl:function name="f:date_to_millis" as="xs:dateTime">
		<xsl:param name="date" as="xs:string" />
		<xsl:variable name="parts" select="tokenize($date, '\.')"/>
		<xsl:variable name="str" select="concat($parts[3], '-', $parts[2], '-', $parts[1], 'T00:00:00')"/>
		<xsl:value-of select="xs:dateTime($str)"/>
	</xsl:function>


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




	<!-- ****************************    ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->


<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
</xsl:text>
</xsl:template>

	<xsl:template name="CONTENT"/>

	<xsl:template name="SCRIPTS"/>

	<xsl:template name="POPUPS"/>

	<xsl:template name="CATALOG_MARKUP"/>


	<xsl:variable name="active_mmi" select="'index'"/>
	<xsl:variable name="proddd" select="page" />


	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template match="/"><xsl:call-template name="DOCTYPE"/>
<rss xmlns:yandex="http://news.yandex.ru" xmlns:media="http://search.yahoo.com/mrss/" xmlns:turbo="http://turbo.yandex.ru" version="2.0">
    <channel>
        <title>СП «ТермоБрест» ООО</title>
        <link>https://termobrest.ru/ns/novosti/</link>
        <description>Корпоративные новости предприятия СП «ТермоБрест» ООО, анонсы мероприятий и информация о выставках</description>
        <turbo:analytics type="Yandex" id="44439058"></turbo:analytics>
        <turbo:analytics type="Google" id="UA-456325-50"></turbo:analytics>
        <yandex:related type="infinity">
            <link url="https://termobrest.ru/catalog/">Каталог газовой арматуры</link>
            <xsl:for-each select="//page/news_section">
            	<link url="https://termobrest.ru/{show_section}"><xsl:value-of select="name"/></link>
            </xsl:for-each>
        </yandex:related>
        <xsl:for-each select="page//news_item">
        <item turbo="true">
            <turbo:extendedHtml>true</turbo:extendedHtml>
            <link>https://termobrest.ru/<xsl:value-of select="show_news_item"/></link>
            <turbo:source>https://termobrest.ru/<xsl:value-of select="show_news_item"/></turbo:source>
            <!--<author>Иван Иванов</author>-->
            <category><xsl:value-of select="./../name"/></category>
            <pubDate><xsl:value-of select="concat(format-dateTime(f:date_to_millis(date), '[FNn,3-3], [D] [MNn,3-3] [Y0001] [H01]:[m01]:[s01]'), ' +0300')"/></pubDate>
            <metrics>
                <yandex schema_identifier="Идентификатор">
                    <breadcrumblist>
                        <breadcrumb url="https://termobrest.ru/" text="Главная"/>
                        <breadcrumb url="https://termobrest.ru/{./../show_section}" text="{./../name}"/>
                    </breadcrumblist>
                </yandex>
            </metrics>
            <turbo:content>
         <menu>
         <a href="https://termobrest.ru/catalog/">Главная страница</a>
         <a href="https://termobrest.ru/catalog/">Каталог газовой арматуры</a>
         <a href="https://termobrest.ru/about/termobrest_segodnya/">О компании</a>
         <a href="https://termobrest.ru/dealers/">Дилеры</a>
         <a href="https://termobrest.ru/ns/novosti/">Новости</a>
         <a href="https://termobrest.ru/ns/novinki_kompanii/">Новинки компании</a>
         <a href="https://termobrest.ru/ns/meropriyatiya/">Мероприятия</a>
         <a href="https://termobrest.ru/ns/novosti_dlya_dilerov/">Новости для дилеров</a>
         <a href="https://termobrest.ru/ns/publikacii/">Публикации</a>
         <a href="https://termobrest.ru/ns/video/">Видео</a>
         <a href="https://termobrest.ru/contacts/">КОНТАКТЫ</a>
         
         </menu>      
                <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
                </xsl:text>
                    <header>
                        <h1><xsl:value-of select="header"/></h1>
                    </header>
                    <xsl:value-of select="text" disable-output-escaping="yes"/>
                <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
            <div data-block="widget-feedback" data-title="Обратная связь" data-stick="false">
                        <div data-type="call" data-url="+375162536470,"></div>
                        <div data-type="mail" data-url="mailto:info@termobrest.ru"></div>
                        <div data-type="whatsapp" data-url="whatsapp://send?phone=+375445530001"></div>
    										<div data-type="viber" data-url="viber://chat?number=%2B375445530001"></div>
                        <div data-block="chat" data-type="telegram" data-url="https://t.me/termobrest"></div>
                        <div data-block="chat" data-type="vkontakte" data-url="https://vk.com/termobrest"></div>
                        <div data-block="chat" data-type="facebook" data-url="https://www.facebook.com/termobrest.ru/"></div>
                   
                    </div>
            <b>Поделиться</b>
            <div data-block="share" data-network="facebook, odnoklassniki, telegram, twitter, vkontakte"></div>
            
            </turbo:content>
        </item>
    	</xsl:for-each>
    </channel>
</rss>
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
