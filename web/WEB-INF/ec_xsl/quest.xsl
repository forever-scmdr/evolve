<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="quest_form.xsl"/>
	<xsl:import href="feedback_form.xsl"/>
	<xsl:import href="inner_page_base.xsl"/>
	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="sel" select="page/variables/p"/>
	<xsl:variable name="page" select="page/about/abstract_page[@key = $sel]"/>
	<xsl:variable name="content" select="page/info"/>

	<xsl:template name="INNER_CONTENT">
		<div class="col-sm-4 col-md-3 hidden-xs">
			<!-- <h1>О санатории</h1> -->
			<ul class="list-group side-menu">
				<xsl:for-each select="page/about/abstract_page">
					<li class="list-group-item{' active'[current()/@key = $sel]}">
						<a href="{show_page}"><xsl:value-of select="header"/></a>
					</li>
				</xsl:for-each>
			</ul>
		</div>
		<div class="col-xs-12 col-sm-8 col-md-9">
			<div class="row">
				<div class="col-xs-12">
					<div class="path hidden-xs">
						<a href="{page/index_link}">Главная страница</a> →
						
					</div>
					<h2 class="m-t-zero">Анкета гостя</h2>
					<div class="row">
						<div class="col-xs-12">
							<xsl:call-template name="QUEST_PLACEHOLDER"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row"></div>
		</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
		<xsl:call-template name="FEEDBACK_MODAL">
			<xsl:with-param name="feedback_link" select="page/client_feedback_link"/>
		</xsl:call-template>			
	</xsl:template>

</xsl:stylesheet>