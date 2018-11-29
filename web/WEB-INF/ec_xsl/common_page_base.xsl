<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
    <xsl:import href="feedback_ajax.xsl"/>
    <xsl:import href="utils/price_conversions.xsl"/>

    <xsl:template name="BR">
        <xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text>
    </xsl:template>

    <!-- ****************************    SEO    ******************************** -->

    <xsl:variable name="url_seo" select="/page/url_seo_wrap/url_seo[url = /page/source_link]"/>
    <xsl:variable name="seo" select="if($url_seo != '') then $url_seo else //seo[1]"/>

    <xsl:variable name="title" select="'tempting.pro'"/>
    <xsl:variable name="meta_description" select="''"/>
    <xsl:variable name="base" select="page/base"/>
    <xsl:variable name="main_host"
                  select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else $base"/>

    <xsl:variable name="default_canonical"
                  select="if(page/@name != 'index') then concat('/', tokenize(page/source_link, '\?')[1]) else ''"/>
    <xsl:variable name="custom_canonical" select="//canonical_link[1]"/>

    <xsl:variable name="canonical" select="if($custom_canonical != '') then $custom_canonical else $default_canonical"/>

    <xsl:variable name="cur_sec" select="page//current_section"/>
    <xsl:variable name="sel_sec" select="if ($cur_sec) then $cur_sec else page/product/product_section[1]"/>
    <xsl:variable name="sel_sec_id" select="$sel_sec/@id"/>


    <xsl:variable name="active_menu_item"/>


    <!-- ****************************    ПОЛЬЗОВАТЕЛЬСКИЕ МОДУЛИ    ******************************** -->

    <xsl:variable name="source_link" select="/page/source_link"/>
    <xsl:variable name="modules" select="page/modules/named_code[not(url != '') or contains($source_link, url)]"/>

    <xsl:variable name="head-start-modules" select="$modules[place = 'head_start']"/>
    <xsl:variable name="head-end-modules" select="$modules[place = 'head_end']"/>
    <xsl:variable name="body-start-modules" select="$modules[place = 'body_start']"/>
    <xsl:variable name="body-end-modules" select="$modules[not(place != '') or place = 'body_end']"/>


    <!-- ****************************    ЛОГИЧЕСКИЕ ОБЩИЕ ЭЛЕМЕНТЫ    ******************************** -->


    <xsl:template name="INC_DESKTOP_HEADER">

    </xsl:template>


    <xsl:template name="INC_MOBILE_HEADER">

    </xsl:template>


    <xsl:template name="INC_FOOTER">
        <!-- modal feedback -->
        <xsl:call-template name="FEEDBACK_FORM"/>
        <!-- MODALS END -->
    </xsl:template>


    <xsl:template name="INC_MOBILE_MENU">

    </xsl:template>


    <xsl:template name="INC_MOBILE_NAVIGATION">

    </xsl:template>


    <xsl:template name="INC_SIDE_MENU_INTERNAL">

    </xsl:template>


    <!-- ****************************    ЭЛЕМЕНТЫ НЕ ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->

    <xsl:variable name="is_fav" select="page/@name = 'fav'"/>

    <xsl:template match="*" mode="product"></xsl:template>

    <xsl:template match="*" mode="product-lines"></xsl:template>

    <xsl:template name="CART_SCRIPT"></xsl:template>


    <!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


    <xsl:template name="MAIN_CONTENT"></xsl:template>
    <xsl:template name="CONTENT"/>
    <xsl:template name="EXTRA_SCRIPTS"/>


    <!-- ****************************    СТРАНИЦА    ******************************** -->


    <xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
        <html lang="ru">
            <head>
				<xsl:text disable-output-escaping="yes">
&lt;!--
				</xsl:text>
                <xsl:value-of select="page/source_link"/>
                <xsl:text disable-output-escaping="yes">
--&gt;
				</xsl:text>
                <base href="{$main_host}"/>
                <meta charset="utf-8"/>
                <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>

                <xsl:for-each select="$head-start-modules">
                    <xsl:value-of select="code" disable-output-escaping="yes"/>
                </xsl:for-each>

                <xsl:call-template name="SEO"/>
                <xsl:for-each select="$head-end-modules">
                    <xsl:value-of select="code" disable-output-escaping="yes"/>
                </xsl:for-each>
            </head>
            <body>
                <xsl:for-each select="$body-start-modules">
                    <xsl:value-of select="code" disable-output-escaping="yes"/>
                </xsl:for-each>

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
        <xsl:apply-templates select="text_part | gallery_part" mode="content"/>
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

    <xsl:template name="PAGE_TITLE">
        <xsl:param name="page"/>
        <xsl:if test="$page/header_pic != ''">
            <h1>
                <img src="{$page/@path}{$page/header_pic}" alt="{$page/header}"/>
            </h1>
        </xsl:if>
        <xsl:if test="not($page/header_pic) or $page/header_pic = ''">
            <h1>
                <xsl:value-of select="$page/header"/>
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

        <xsl:variable name="quote">"</xsl:variable>

        <link rel="canonical" href="{concat($main_host, $canonical)}"/>

        <xsl:if test="$seo">
            <xsl:apply-templates select="$seo"/>
        </xsl:if>
        <xsl:if test="not($seo) or $seo = ''">
            <title>
                <xsl:value-of select="$title"/>
            </title>
            <meta name="description" content="{replace($meta_description, $quote, '')}"/>
        </xsl:if>
        <meta name="google-site-verification" content="{page/url_seo_wrap/google_verification}"/>
        <meta name="yandex-verification" content="{page/url_seo_wrap/yandex_verification}"/>
        <xsl:call-template name="MARKUP"/>
        <xsl:call-template name="PAGINATION_LINKS"/>
    </xsl:template>

    <xsl:template name="MARKUP"/>

    <xsl:template match="seo | url_seo">
        <title>
            <xsl:value-of select="title"/>
        </title>
        <meta name="description" content="{description}"/>
        <meta name="keywords" content="{keywords}"/>
        <xsl:value-of select="meta" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template name="PAGINATION_LINKS">
        <xsl:variable name="pages" select="//*[ends-with(name(), '_pages')]"/>
        <xsl:if test="$pages">
            <xsl:variable name="current_page" select="number(page/variables/page)"/>

            <xsl:variable name="prev" select="$pages/page[$current_page - 1]"/>
            <xsl:variable name="next" select="$pages/page[$current_page]"/>

            <xsl:if test="$prev">
                <link rel="prev" href="{$prev/link}"/>
            </xsl:if>
            <xsl:if test="$next">
                <link rel="next" href="{$next/link}"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
