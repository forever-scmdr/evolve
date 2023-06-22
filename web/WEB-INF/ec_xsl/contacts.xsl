<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="c" select="page/contacts"/>

	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Контактная информация</h2>
		<div class="row">
			<div class="col-md-6">
				<h4>Телефоны</h4>
				<xsl:value-of select="$c/phones" disable-output-escaping="yes"/>
			</div>
			<div class="col-md-6">
				<h4>Другие способы связи</h4>
				<xsl:value-of select="$c/other" disable-output-escaping="yes"/>
			</div>
			<xsl:if test="$c/germany != ''">
				<div class="col-md-4">
					<h4>Эксклюзивный представитель в Германии</h4>
					<xsl:value-of select="$c/germany" disable-output-escaping="yes"/>
				</div>
			</xsl:if>
		</div>
		<div class="row m-t-default">
			<div class="col-xs-12">
				<h4>Как к нам добраться из Минска</h4>
				<xsl:value-of select="$c/form_minsk" disable-output-escaping="yes"/>
			</div>
			<div class="col-md-10">
				<xsl:value-of select="$c/map" disable-output-escaping="yes"/>
			</div>
			<div class="col-md-2">
				<h4>GPS-координаты</h4>
				<xsl:value-of select="$c/gps" disable-output-escaping="yes"/>
			</div>
		</div>
	</div>
	</xsl:template>


</xsl:stylesheet>