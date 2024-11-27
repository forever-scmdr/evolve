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


	<xsl:variable name="title" select="'Дилеры'"/>
	<xsl:variable name="critical_item" select="//dealer"/>


	<xsl:template match="dealer">
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
	</xsl:template>




	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'dealers'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs side-coloumn">
				<h1 class="no-top-margin">Дилеры</h1>
				<ul class="list-group side-menu">
					<li class="list-group-item">
						<a href="{page/all_dealers_link}" class="active-link">Дилеры СП «ТермоБрест» ООО</a>
					</li>
					<li class="list-group-item">
						<a href="{page/dealers_text_page_link}">Рейтинг дилеров</a>
					</li>
					<li class="list-group-item">
						<a href="{page/all_partners_link}">Деловые партнёры СП «ТермоБрест» ООО</a>
					</li>
				</ul>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-9">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">Дилеры СП ТермоБрест ООО</h2>
					</div>
				</div>

				<!-- <div class="row">
					<div class="col-xs-12 content-block">
						<p>
							Приглашаем к сотрудничесвту организации:
							<br/><b>Дальневотсочного округа РФ</b>
							<br/><b>Казахстана</b>
						</p>
					</div>
				</div> -->

				<div class="row">
					<div class="col-xs-12 content-block">
						<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
							<div class="panel panel-default">
								<div class="panel-heading" role="tab" id="headingOne">
									<h3 class="panel-title">
										<a role="button" data-toggle="collapse" data-parent="#accordion"
											href="#collapseRus" aria-expanded="true" aria-controls="collapseOne">
											Россия
										</a>
									</h3>
								</div>
								<div id="collapseRus" class="panel-collapse collapse" role="tabpanel">
									<div class="panel-body">
										<xsl:for-each select="//region[contains(name, 'ФО')]">
											<xsl:apply-templates select="dealer[not(is_partner = 'да') and name != '']" />
										</xsl:for-each>
										<div class="row dealer">
											<div class="col-md-12" style="text-align: center;"><b>Приглашаем к сотрудничеству организации Дальневосточнго ФО</b></div>
										</div>
									</div>
								</div>
							</div>
							<xsl:for-each select="//region[not(contains(name, 'ФО'))]">
								<xsl:if test="dealer[not(is_partner = 'да')]">
									<div class="panel panel-default">
										<div class="panel-heading" role="tab" id="heading{@id}">
											<h4 class="panel-title">
												<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapse{@id}">
													<xsl:value-of select="name"/>
												</a>
											</h4>
										</div>
										<div id="collapse{@id}" class="panel-collapse collapse" role="tabpanel">
											<div class="panel-body">
												<xsl:apply-templates select="dealer[not(is_partner = 'да')]" />
											</div>
										</div>
									</div>
								</xsl:if>

								<!-- <xsl:if test="name = 'Западная Европа'">
									<div class="panel panel-default">
										<div class="panel-heading" role="tab" id="headingOther">
											<h4 class="panel-title">
												<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOther">
													Страны Евросоюза и Китай
												</a>
											</h4>
										</div>
										<div id="collapseOther" class="panel-collapse collapse" role="tabpanel">
											<div class="panel-body">
												<xsl:apply-templates select="dealer[not(is_partner = 'да')]" />
												<xsl:apply-templates select="//region[name = 'Китай']/dealer" />
											</div>
										</div>
									</div>
								</xsl:if> -->
							</xsl:for-each>
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

</xsl:stylesheet>
