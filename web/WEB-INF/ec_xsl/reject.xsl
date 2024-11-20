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


	<xsl:variable name="local_title" select="'Отказ от рассылки'"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'about'"/>


	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12">
				<div class="row">
					<div class="col-xs-12 content-block">
						<h1 class="no-top-margin">Вы успешно отказались от рассылки</h1>
						На адрес <xsl:value-of select="//client_email"/> больше не будет осуществляться рассылка.
					</div>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>