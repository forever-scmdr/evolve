<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="sec" select="$ni/news_section"/>

	<xsl:variable name="title" select="if(not($seo/title)) then concat($ni/header, ' - ', $sec/name) else $seo/title"/>
	<xsl:variable name="local_description" select="concat('Последние актуальные новости СП «ТермоБрест» ', $title)"/>

	<xsl:variable name="news_src" select="page/news"/>
	<xsl:variable name="heading" select="'Новости'"/>
	<xsl:variable name="critical_item" select="$ni"/>
	<xsl:template name="MENU_EXTRA"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs side-coloumn">
				<p style="font-size: 28px;" class="no-top-margin"><xsl:value-of select="$heading"/></p>
				<ul class="list-group side-menu">
					<xsl:for-each select="$news_src//news_section">
						<li class="list-group-item">
							<a href="{show_section}" class="{'active-link'[current()/@id = $sec/@id]}"><xsl:value-of select="name"/></a>
						</li>
					</xsl:for-each>
				</ul>
				<div class="hidden-sm">
					<a onclick="getSubscribeForm()" class="btn btn-default btn-block" type="button" data-toggle="modal" data-target="#subscribe">Подписаться на рассылку</a>
				</div>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-9">
				<article>
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
							<a href="{$sec/show_section}"><xsl:value-of select="$sec/name"/></a>
							→
						</div>
						<h1 class="no-top-margin"><xsl:value-of select="$ni/header"/></h1>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 content-block">
						<!-- <p style="font-size: 16px;">
							<a href="{$ni/fullscreen_link}">Полноэкранный режим</a>
						</p> -->
						<xsl:value-of select="$ni/text" disable-output-escaping="yes"/>
					</div>
				</div>
				</article>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>