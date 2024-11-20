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

	<xsl:variable name="m" select="page/main"/>
	<xsl:variable name="seo" select="$m/seo"/>
	<xsl:variable name="title" select="if($seo/title != '') then $seo/title else 'производство запорной и запорно-регулирующей арматуры. Электромагнитные газовые клапаны, заслонки регулирующие, фильтры газовые, блоки электромагнитных клапанов, приборы автоматики безопасности (датчики-реле давления ДРД)'"/>
	<xsl:variable name="critical_item" select="page"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template name="CONTENT">
	<div class="cf7" style="width: 100%;">
		<xsl:for-each select="page/slide">
			<xsl:variable name="first" select="position() = 1"/>
			<div class="slide-image{' opaque'[$first]}" style="background: url({@path}{pic}) 50% 50%;">
				<div class="slide-mask"></div>
			</div>
		</xsl:for-each>
	
	
		<div class="spacer"></div>
	
		<div class="container slider">
			<div class="row">
				<div class="col-sm-5 visible-xs-block" id="mobile-slide-image" style="height: 320px;">
					<xsl:for-each select="page/slide">
						<xsl:variable name="first" select="position() = 1"/>
						<div class="row mobile-slide-image hidden-sm hidden-md hidden-lg{' opaque'[$first]}" 
							style="background-image: url({@path}{pic}); display: block; position: absolute; width: 100%; height: 320px;">
							<xsl:if test="small_pic != ''">
								<img src="{@path}{small_pic}" alt="-"/>
							</xsl:if>		
						</div>
					</xsl:for-each>		
				</div>
				<xsl:for-each select="page/slide">
					<xsl:variable name="first" select="position() = 1"/>
					<xsl:variable name="hidden" select="not(small_pic) or small_pic = ''"/> 
					<div style="{'visibility: hidden;'[$hidden]}{' display: none;'[not($first)]}" class="col-sm-5 hidden-xs desctop-addon">
						<img src="{@path}{small_pic}" alt="-"/>
					</div>
				</xsl:for-each>
				<div class="col-sm-7">
					<div class="slider-text">
						<xsl:for-each select="page/slide">
							<xsl:variable name="first" select="position() = 1"/>
							<div class="txt{' opaque'[$first]}">
								<h2 class="no-top-margin"><xsl:value-of select="header"/></h2>
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
						</xsl:for-each>
						<div class="row">
							<div class="col-xs-12 col-sm-6">
								<xsl:for-each select="page/slide">
									<xsl:variable name="first" select="position() = 1"/>
									<a class="btn btn-primary btn-lg txtbtn{' opaque'[$first]}" type="button" data-toggle="modal" data-target="" href="{href}">
										<xsl:value-of select="link_text"/>
									</a>
								</xsl:for-each>
							</div>
							<div class="col-xs-12 col-sm-6 slider-navigation">
								<div class="cf7_controls">
									<xsl:for-each select="page/slide">
										<xsl:variable name="first" select="position() = 1"/>
										<a href="" class="{'active'[$first]}"></a>
									</xsl:for-each>
								</div>
							</div>
						</div>
						<!-- <a class="btn btn-primary btn-lg" type="button" data-toggle="modal" 
							data-target="">О компании</a> -->
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12 col-sm-4 col-md-4">
				<div style="position:relative;height:0;padding-bottom:56.25%">
					<iframe src="https://www.youtube.com/embed/uYlnpheuVKU?ecver=2"
						width="640" height="360" frameborder="0"
						style="position:absolute;width:100%;height:100%;left:0"
						allowfullscreen="allowfullscreen"></iframe>
				</div>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-8">
				<div class="row main-page-news">
					<div class="col-xs-12">
						<!-- tabs -->
						<ul class="nav nav-tabs" role="tablist">
							<xsl:for-each select="page/id_news/news_section">
								<xsl:variable name="active" select="position() = 1"/>
								<li role="presentation" class="{'active'[$active]}">
									<a href="#tab{position()}" role="tab" data-toggle="tab"><xsl:value-of select="name"/></a>
								</li>
							</xsl:for-each>
						</ul>
						<div class="tab-content">
							<xsl:for-each select="page/id_news/news_section">
								<xsl:variable name="active" select="position() = 1"/>
								<div role="tabpanel" class="tab-pane{' active'[$active]}" id="tab{position()}">
									<div class="col-xs-12">
										<div class="row">
											<xsl:for-each select="news_item">
												<div class="col-sm-4">
													<div class="date"><xsl:value-of select="date"/></div>
													<a href="{show_news_item}"><xsl:value-of select="header"/></a>
												</div>
											</xsl:for-each>
											<a href="{show_section}" style="position: absolute; bottom: -25px; left: 15px; color: #BF0000;">Перейти в раздел «<xsl:value-of select="name"/>»</a>
										</div>
									</div>
									<!-- <div class="col-xs-12">
										<p style="text-align: right; margin:10px 0 0 0; padding-top: 10px; border-top: 1px solid #ccc">
										
											<a href="{show_section}">Перейти в раздел →</a>
										</p>
									</div> -->
								</div>
							</xsl:for-each>
						</div>
						<!-- tabs end -->
					</div>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	
	</xsl:template>

</xsl:stylesheet>
