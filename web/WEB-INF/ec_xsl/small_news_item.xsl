<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
    <xsl:import href="common_page_base.xsl"/>
    <xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="title" select="$ni/name"/>
    <xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
    <xsl:variable name="active_menu_item" select="'news'"/>


    <xsl:variable name="ni" select="page/small_news_item"/>
    <xsl:variable name="parent" select="/page/news[@id = $ni/news/@id]"/>
    <xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>
    <xsl:variable name="format"
                  select="if($ni/video_url != '') then 'video' else if($ni/top_gal/main_pic != '') then 'gallery' else 'standard'"/>


    <xsl:template name="FACEBOOK_MARKUP">
        <meta property="og:url" content="{concat($main_host, $canonical)}" />
        <meta property="og:type" content="article" />
        <meta property="og:locale" content="ru_RU" />
        <meta property="og:title" content="{$h1}" />
        <!-- <meta property="og:description" content="{$ni/short}" /> -->
        <meta property="og:image" content="http://tempting.pro/images/logo.png" />
       <meta property="og:app_id" content="552626568232392" />
    </xsl:template>

    <xsl:template name="CONTENT">
        <section class="s-content s-content--narrow s-content--no-padding-bottom white">
            <article class="row format-{$format}">
                <div class="s-content__header col-full">
                    <h1 class="s-content__header-title">
                        <xsl:value-of select="$h1"/>
                    </h1>

                    <ul class="s-content__header-meta">
                        <xsl:if test="$ni/source">
                            <li class="cat">
                                Источник: <a href="{$ni/source_link}" >
                                    <xsl:value-of select="$ni/source"/>
                                </a>
                            </li>
                        </xsl:if>
                        <li class="date" data-utc="{$ni/date/@millis}"><xsl:value-of select="$ni/date"/></li>
                    </ul>
                    <xsl:if test="$ni/complexity != '' or $ni/read_time != '' or $ni/size != ''">
                        <div class="tags">
                            <xsl:if test="$ni/complexity != ''">
                                <span class="entry__category yellow">
                                    <a>Сложность: <b><xsl:value-of select="$ni/complexity" /></b></a>
                                </span>
                            </xsl:if>
                            <xsl:if test="$ni/read_time != ''">
                                <span class="entry__category blue">
                                    <a>Среднее время прочтения: <b><xsl:value-of select="$ni/read_time" /></b></a>
                                </span>
                            </xsl:if>
                            <xsl:if test="$ni/size != ''">
                                <span class="entry__category red">
                                    <a>Размер статьи: <b><xsl:value-of select="$ni/size" /></b></a>
                                </span>
                            </xsl:if>
                        </div>
                    </xsl:if>

                    <!--<div class="tags">-->
                    <!--<span class="entry__category red">-->
                    <!--<a href="{$parent/show_page}" rel="next">-->
                    <!--<span>Еще</span>-->
                    <!--<xsl:value-of select="$parent/name"/>-->
                    <!--</a>-->
                    <!--</span>-->
                    <!--<span class="entry__category yellow">-->
                    <!--<a>Сложность: <b><xsl:value-of select="$ni/complexity" /></b></a>-->
                    <!--</span>-->
                    <!--<span class="entry__category blue">-->
                    <!--<a>Среднее время прочтения: <b><xsl:value-of select="$ni/read_time" /></b></a>-->
                    <!--</span>-->
                    <!--</div>-->
                    <!--<xsl:if test="$ni/tag">-->
                    <!--<div class="tags">-->
                    <!--<xsl:for-each select="$ni/tag">-->
                    <!--<xsl:variable name="class">-->
                    <!--<xsl:choose>-->
                    <!--<xsl:when test=". = 'Бизнес'">dark-blue</xsl:when>-->
                    <!--<xsl:when test=". = 'Политика'">red</xsl:when>-->
                    <!--<xsl:when test=". = 'Технологии'">yellow</xsl:when>-->
                    <!--<xsl:when test=". = 'Инфографика'">orange</xsl:when>-->
                    <!--<xsl:when test=". = 'Менеджмент'">blue</xsl:when>-->
                    <!--<xsl:otherwise>gray</xsl:otherwise>-->
                    <!--</xsl:choose>-->
                    <!--</xsl:variable>-->

                    <!--<span class="entry__category {$class}">-->
                    <!--<a href="{concat('all_news/?tag=', .)}">-->
                    <!--<xsl:value-of select="." />-->
                    <!--</a>-->
                    <!--</span>-->
                    <!--</xsl:for-each>-->
                    <!--</div>-->
                    <!--</xsl:if>-->
                </div>
                <div class="col-full s-content__main">
                    <div id="nil">
                        <xsl:apply-templates select="$ni" mode="content"/>

                        <xsl:if test="$ni/tag">
                            <p class="s-content__tags">
                                <span>Теги</span>
                                <span class="s-content__tag-list">
                                    <xsl:for-each select="$ni/tag">
                                        <a href="{concat('news/?tag=', .)}">
                                            <xsl:value-of select="."/>
                                        </a>
                                    </xsl:for-each>
                                </span>
                            </p>
                        </xsl:if>
                        <div class="ya-share2" data-services="vkontakte,facebook,twitter" data-limit="3"></div>
                    </div>
                    <xsl:if test="page/prev[@id != $ni/@id]|page/next[@id != $ni/@id]">
                        <div class="s-content__pagenav">
                            <div class="s-content__nav">
                                <xsl:if test="page/prev[@id != $ni/@id]">
                                    <div class="s-content__prev">
                                        <a href="{page/prev/show_page}" rel="prev">
                                            <span>Предыдущая новость</span>
                                            <xsl:value-of select="page/prev/name"/>
                                        </a>
                                    </div>
                                </xsl:if>
                                <xsl:if test="page/next[@id != $ni/@id]">
                                    <div class="s-content__next">
                                        <a href="{page/next/show_page}" rel="next">
                                            <span>Следующая новость</span>
                                            <xsl:value-of select="page/next/name"/>
                                        </a>
                                    </div>
                                </xsl:if>
                            </div>
                        </div>
                    </xsl:if>
                </div>
            </article>

            <xsl:call-template name="COMMENTS"/>

        </section>
    </xsl:template>

    <xsl:template name="COMMENTS">
        <div style="height: 3rem;"></div>
    </xsl:template>

    <xsl:template name="EXTRA_SCRIPTS">
        <script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
        <script src="//yastatic.net/share2/share.js"></script>
        
    </xsl:template>


</xsl:stylesheet>