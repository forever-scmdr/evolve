<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="p" select="page/xxl"/>
	<xsl:variable name="title" select="$p/header"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="active_menu_item" select="'garantiya_xxl_xxl'"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>

	<xsl:variable name="serial" select="page/variables/serial"/>
	<xsl:variable name="success" select="$serial and not($serial = 'not_found')"/>
	<xsl:variable name="not_found" select="$serial and $serial = 'not_found'"/>


	<xsl:template name="BODY">
		<xsl:if test="$success"><img src="img/cert{page/variables/serial}.jpg" class="body_certificate"/></xsl:if>
	</xsl:template>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
				<xsl:for-each select="$p/parent">
					<a href="{show_page}"><xsl:value-of select="header"/></a> >
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>

		<div class="page-content m-t">
			<p>
				<a class="button" data-toggle="modal" data-target="#warranty">Оформить XXL-гарантию</a>
			</p>
			<p>
				<xsl:if test="$success">Ваш сертификат оформлен и доступен для печати, его копия отправлена на указанный e-mail.</xsl:if>
				<xsl:if test="$not_found">Товар с указанным артикулом не найден. Проверьте артикул или воспользуйтесь формой обратной связи</xsl:if>
			</p>
			<xsl:if test="$success">
				<p>
					<a href="#" onclick="window.print(); return false;">Распечатать сертификат</a>
				</p>
				<div>
					<img src="img/cert{page/variables/serial}.jpg" style="max-width: 600px; box-shadow: 0 0 15px gray;" alt=""/>
				</div>
			</xsl:if>
			<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>