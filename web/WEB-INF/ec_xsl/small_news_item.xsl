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


    <xsl:template name="CONTENT">
        <section class="s-content s-content--narrow s-content--no-padding-bottom">
            <article class="row format-{$format}">
                <div class="s-content__header col-full">
                    <h1 class="s-content__header-title">
                        <xsl:value-of select="$h1"/>
                    </h1>
                    <ul class="s-content__header-meta">
                        <li class="date">
                            <xsl:value-of select="$ni/date"/>
                        </li>
                        <xsl:if test="$ni/tag">
                            <li class="cat">
                                Теги:&#160;
                                <xsl:for-each select="$ni/tag">
                                    <xsl:variable name="p" select="position()"/>
                                    <xsl:if test="$p != 1">
                                        <xsl:text> </xsl:text>
                                    </xsl:if>
                                    <a href="{concat('small_news/?tag=', .)}">
                                        <xsl:value-of select="."/>
                                    </a>
                                </xsl:for-each>
                            </li>
                        </xsl:if>
                    </ul>
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
                    </div>
                    <xsl:if test="page/prev|page/next">
                        <div class="s-content__pagenav">
                            <div class="s-content__nav">
                                <xsl:if test="page/prev">
                                    <div class="s-content__prev">
                                        <a href="{page/prev/show_page}" rel="next">
                                            <span>&lt; Предыдущая новость</span>
                                            <xsl:value-of select="page/prev/name"/>
                                        </a>
                                    </div>
                                </xsl:if>
                                <xsl:if test="page/next">
                                    <div class="s-content__next">
                                        <a href="{page/next/show_page}" rel="next">
                                            <span>Следующая новость &gt;</span>
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


</xsl:stylesheet>