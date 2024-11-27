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

	<xsl:variable name="about" select="page/about_section"/>

	<xsl:variable name="local_title" select="concat($about/name, ' - О компании')"/>
	<xsl:variable name="local_description" select="concat($about/name, ' СП «ТермоБрест» ООО завод газовой арматуры')"/>


	<xsl:variable name="sec" select="page/about_section"/>
	<xsl:variable name="critical_item" select="$sec"/>



	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'about'"/>


	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs side-coloumn">
				<p style="font-size: 28px" class="no-top-margin">О компании</p>
				<ul class="list-group side-menu">
					<xsl:for-each select="page/about//about_section">
						<xsl:variable name="active" select="@id = $sec/@id"/>
						<li class="list-group-item">
							<a href="{show_section}" class="{'active-link'[$active]}"><xsl:value-of select="name"/></a>
						</li>
					</xsl:for-each>
				</ul>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-9">
				<article>
					<div class="row">
						<div class="col-xs-12">
							<div class="path hidden-xs">
								<a href="{$base}">Главная страница</a>
								→
							</div>
							<h1 class="no-top-margin">
								<xsl:if test="not($sec/header) or $sec/header = ''">
									<xsl:value-of select="$sec/name"/>
								</xsl:if>
								<xsl:if test="$sec/header !=''">
									<xsl:value-of select="$sec/header"/>
								</xsl:if>
							</h1>
						</div>
					</div>

					<div class="row">
						<div class="col-xs-12 content-block">
							<!-- <p style="font-size: 16px;">
								<a href="{$sec/fullscreen_link}">Полноэкранный режим</a>
							</p> -->
							<xsl:apply-templates select="$sec" mode="content"/>
						</div>
					</div>
				</article>
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