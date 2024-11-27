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

	<xsl:variable name="sec" select="page/news_section"/>
	<xsl:variable name="critical_item" select="$sec"/>

	<xsl:variable name="title" select="if($seo/title != '') then $seo/title else $sec/name"/>
	<xsl:variable name="local_description" select="$title"/>

	<xsl:template match="news_item_pages">
	<div class="btn-group pagination" role="group">
		<xsl:apply-templates select="page | current_page"/>
	</div>
	</xsl:template>

	<xsl:template match="current_page">
		<button type="button" class="btn btn-info active-link" onclick="location.href='{link}'"><xsl:value-of select="number"/></button>
	</xsl:template>

	<xsl:template match="page">
		<button type="button" class="btn btn-info" onclick="location.href='{link}'"><xsl:value-of select="number"/></button>
	</xsl:template>


	<xsl:variable name="news_src" select="page/news"/>
	<xsl:variable name="heading" select="if($sec/name != '') then $sec/name else 'Новости'"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs side-coloumn">
				<h1 class="no-top-margin"><xsl:value-of select="$heading"/></h1>
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
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin"><xsl:value-of select="$heading"/></h2>
					</div>
				</div>

				<div class="row section-items">
					<xsl:for-each select="$sec/news_item">
						<div class="col-xs-6 col-sm-6 col-md-4">
							<a href="{if(fullscreen_only = '1') then show_fullscreen else show_news_item}">
								<img src="{@path}{main_pic}" alt="" style="max-width: 100%;"/>
							</a>
							<a href="{if(fullscreen_only = '1') then show_fullscreen else show_news_item}"><xsl:value-of select="header"/></a>
						</div>
						<xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 3 = 0">
							<div class="clearfix hidden-xs hidden-sm"></div>
						</xsl:if>
					</xsl:for-each>
				</div>
				<xsl:apply-templates select="$sec/news_item_pages"/>

<!-- 
				<div class="row">
					<div class="col-xs-12 content-block bottom-links-block">
						<div style="border: 1px solid #363636; padding: 10px; margin-top: 20px;">
							<h3 style="text-align: center;">Продукция</h3>
								<xsl:for-each select="/page/catalog/main_section">
								
								<xsl:if test="position() = 1">
									<xsl:text disable-output-escaping="yes">
										&lt;div class="sep"&gt;
									</xsl:text>
								</xsl:if>
								
								<a href="{/page/base}/{show_section}">
									<xsl:value-of select="name"/>
								</a>
								
								<xsl:if test="position() mod 3 = 0 and position() != last()">
									<xsl:text disable-output-escaping="yes">
									&lt;/div&gt;&lt;div class="sep"&gt;
									</xsl:text>
								</xsl:if>

							</xsl:for-each>
						</div>
					</div>
				</div>
 -->
			</div>
		</div>


	</div>
	</xsl:template>

</xsl:stylesheet>