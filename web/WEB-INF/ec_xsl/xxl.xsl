<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/header"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="p" select="page/custom_page"/>

	<xsl:variable name="active_menu_item" select="'garantiya_xxl_xxl'"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> >
				<xsl:for-each select="$p/parent">
					<a href="{show_page}"><xsl:value-of select="header"/></a> >
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>

		<div class="page-content m-t">
			<a class="button" data-toggle="modal" data-target="#warranty">XXL-гарантия</a>
			<p>Копия сертификата отправлена вам на  ящик <strong>usermail@mail.com</strong></p>
			<div>
				<a href="">Распечатать сертификат</a>
			</div>
			<div>
				<img src="img/{page/variables/serial}.jpg" style="max-width: 600px; box-shadow: 0 0 15px gray;" alt=""/>
			</div>
			<p>Товар с таким артикулом не найден. Обратитесь в службу сервиса с помощью <a href="">формы обратной связи</a>.</p>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>