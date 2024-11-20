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


	<xsl:variable name="title" select="'Карта дилеров'"/>
	<xsl:variable name="critical_item" select="page/region/dealer"/>

	<xsl:template match="region">
	<div class="modal fade" id="region_{@id}" tabindex="-1">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title"><xsl:value-of select="name"/></h4>
				</div>
				<div class="modal-body">
					<xsl:for-each select="dealer">
						<div class="row dealer">
							<div class="col-md-2"><xsl:value-of select="city"/></div>
							<div class="col-md-3"><xsl:value-of select="name"/></div>
							<div class="col-md-4">
								<xsl:value-of select="phones" disable-output-escaping="yes"/>
							</div>
							<div class="col-md-3">
								<xsl:value-of select="extra" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'dealers'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">Карта дилеров</h2>
					</div>
				</div>

				<div class="row">
					<div class="col-xs-12 content-block">

						<!-- tabs -->
						<ul class="nav nav-tabs" role="tablist">
							<li role="presentation">
								<a href="#" role="tab" rel="Беларусь" data-toggle="modal" data-target="#region_{page/region[name='Беларусь']/@id}">Беларусь</a>
							</li>
							<li role="presentation">
								<a href="#russia" role="tab" data-toggle="tab">Россия</a>
							</li>
							<li role="presentation">
								<a href="#ukraine" role="tab" rel="Украина" data-toggle="modal" data-target="#region_{page/region[name='Украина']/@id}">Украина</a>
							</li>
							<li role="presentation">
								<a href="#kazakhstan" role="tab" rel="Казахстан" data-toggle="modal" data-target="#region_{page/region[name='Казахстан']/@id}">Казахстан</a>
							</li>
							<li role="presentation">
								<a href="#other" role="tab" data-toggle="tab">Страны Евросоюза и Китай</a>
							</li>
							<li role="presentation">
								<a href="#" role="tab" rel="Египет" data-toggle="modal" data-target="#region_{page/region[name='Египет']/@id}">Египет</a>
							</li>
						</ul>

						<div class="tab-content">

							<div role="tabpanel" class="tab-pane" id="belarus"></div>

							<div role="tabpanel" class="tab-pane" id="russia">
								<div class="row">
									<div class="col-xs-12 switch-region">
										<button class="btn btn-info btn-xs" data-toggle="modal"
											data-target="#region_{page/region[name='Центральный ФО']/@id}" rel="Центральный ФО">Центральный</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal"
											data-target="#region_{page/region[name='Южный ФО']/@id}" rel="Южный ФО">Южный</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal"
											data-target="#region_{page/region[name='Северо-Западный ФО']/@id}" rel="Северо-Западный ФО">Северо-Западный</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Дальневосточный ФО']/@id}" rel="Дальневосточный ФО">Дальневосточный</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Сибирский ФО']/@id}" rel="Сибирский ФО">Сибирский</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Уральский ФО']/@id}" rel="Уральский ФО">Уральский</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Приволжский ФО']/@id}" rel="Приволжский ФО">Приволжский</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Северо-Кавказский ФО']/@id}" rel="Северо-Кавказский ФО">Северо-Кавказский</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Крымский ФО']/@id}" rel="Крымский ФО">Крымский</button>
									</div>
								</div>
							</div>

							<div role="tabpanel" class="tab-pane" id="ukraine"></div>
							<div role="tabpanel" class="tab-pane" id="kazakhstan"></div>
							<div role="tabpanel" class="tab-pane" id="other">
								<div class="row">
									<div class="col-xs-12 switch-region">
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Европейский союз']/@id}" rel="Европейский союз">Европейский союз</button><xsl:text> </xsl:text>
										<button class="btn btn-info btn-xs" data-toggle="modal" 
											data-target="#region_{page/region[name='Китай']/@id}" rel="Китай">Китай</button>
									</div>
								</div>
							</div>

						</div>

						<div class="dealers-map">
							<div id="legend-Дальневосточный_ФО" class="map-legend" style="top: 145px; right: 0; margin-right: 15%; display: block;">
								<span>Приглашаем к сотрудничеству</span>
								<span>организации</span>
								<span>Дальневосточнго ФО</span>
							</div>
							<object class="scroll" id="termo_map_1" data="images/termobrest_map.svg" style="width:100%;" type="image/svg+xml"></object>
						</div>

					</div>
				</div>
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

<xsl:template name="SCRIPTS">
	<script type="text/javascript" src="js/maptip.js"></script>
</xsl:template>

<xsl:template name="POPUPS">
	<xsl:apply-templates select="page/region[name='Беларусь']"/>
	<xsl:apply-templates select="page/region[name='Украина']"/>
	<xsl:apply-templates select="page/region[name='Казахстан']"/>
	<xsl:apply-templates select="page/region[name='Центральный ФО']"/>
	<xsl:apply-templates select="page/region[name='Южный ФО']"/>
	<xsl:apply-templates select="page/region[name='Северо-Западный ФО']"/>
	<xsl:apply-templates select="page/region[name='Дальневосточный ФО']"/>
	<xsl:apply-templates select="page/region[name='Сибирский ФО']"/>
	<xsl:apply-templates select="page/region[name='Уральский ФО']"/>
	<xsl:apply-templates select="page/region[name='Приволжский ФО']"/>
	<xsl:apply-templates select="page/region[name='Северо-Кавказский ФО']"/>
	<xsl:apply-templates select="page/region[name='Крымский ФО']"/>
	<!-- <xsl:apply-templates select="page/region[name='Европейский союз']"/> -->
	<xsl:apply-templates select="page/region[name='Европейский союз']"/>
	<xsl:apply-templates select="page/region[name='Китай']"/>
	<xsl:apply-templates select="page/region[name='Египет']"/>
</xsl:template>

</xsl:stylesheet>