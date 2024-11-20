<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="get_doc.xsl"/>
	<xsl:import href="feedback.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Каталог продукции'"/>

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:variable name="new_devices" select="/page/catalog/new_devices" />

	<xsl:variable name="seo" select="/page/seo_wrap/seo"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12 hidden-sm hidden-md hidden-lg">
						<div class="btn-group btn-group-justified" role="group">
							<a type="button" class="btn btn-primary" data-toggle="modal" data-target="#cat_side_menu">Меню каталога</a>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{page/index_link}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">Каталог продукции</h2>
						<div class="well accent">
							<p>Номенклатура продукции марки ТЕРМОБРЕСТ составляет более 7000
								типов, типоразмеров и исполнений изделий, которые по своим
								характеристикам образуют высококачественную группу
								запорно-регулирующей арматуры для газа и жидких сред.</p>
						</div>
						<p>
							<a href="{page/new_products_link}" class="btn btn-primary btn-md">Новинки продукции</a>
						</p>
					</div>
				</div>
				<div class="row section-links">
					<xsl:for-each select="page/catalog/main_section">
						<div class="col-xs-6 col-sm-4">
							<a href="{show_section}">
								<img onerror="this.src = 'images/noimage.png'" src="{@path}{img}" alt="{alt}" style="max-width: 100%;"/>
							</a>
							<a href="{show_section}"><xsl:value-of select="name"/></a>
						</div>
						<xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-sm hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 3 = 0">
							<div class="clearfix hidden-xs"></div>
						</xsl:if>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>
	
	<xsl:template name="POPUPS">
	<div class="modal fade" id="cat_side_menu" tabindex="-1">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Каталог продукции</h4>
				</div>
				<div class="modal-body">
					<ul class="list-group side-menu">
					<xsl:for-each select="page/catalog/main_section">
						<li class="list-group-item">
							<a href="{show_section}"><xsl:value-of select="name"/></a>
						</li>
					</xsl:for-each>
					</ul>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>
