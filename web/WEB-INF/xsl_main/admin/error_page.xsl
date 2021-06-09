<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../common_page_base.xsl"/>
	<xsl:import href="../templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="404"/>
	<xsl:variable name="h1" select="''"/>
	<xsl:variable name="p" select="page/error_page"/>

	<xsl:variable name="active_menu_item" select="$p/@key"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<div class="text">
			<xsl:apply-templates select="$p" mode="content"/>
			<div class="link" onclick="$('#bug_info').toggle()">
				<span>Отладочная информация</span>
			</div>
			<div class="content bug-info" id="bug_info" style="display: block;"></div>
		</div>
	</xsl:template>


	<xsl:template name="INC_SIDE_MENU_INTERNAL">
		<xsl:choose>
			<xsl:when test="$page_menu = 'catalog'"><xsl:call-template name="INC_SIDE_MENU_INTERNAL_CATALOG"/></xsl:when>
			<xsl:otherwise><xsl:call-template name="INC_SIDE_MENU_INTERNAL_PAGES"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="INC_SIDE_MENU_INTERNAL_PAGES">
		<div class="side-menu">
			<!-- чтобы выводить все разделы надо удалить [.//@id = $p/@id] -->
			<xsl:for-each select="page/custom_pages/custom_page[.//@id = $p/@id]/custom_page">
				<xsl:variable name="l1_active" select="@id = $p/@id"/>
				<div class="side-menu__item side-menu__item_level_1">
					<a href="{show_page}" class="side-menu__link{' side-menu__link_active'[$l1_active]}"><xsl:value-of select="header"/></a>
				</div>
				<xsl:if test=".//@id = $p/@id">
					<xsl:for-each select="custom_page | news">
						<xsl:variable name="l2_active" select="@id = $p/@id"/>
						<div class="side-menu__item side-menu__item_level_2">
							<a href="{show_page}" class="side-menu__link{' side-menu__link_active'[$l2_active]}"><xsl:value-of select="header"/></a>
						</div>
						<xsl:if test=".//@id = $p/@id">
							<xsl:for-each select="custom_page | news">
								<xsl:variable name="l3_active" select="@id = $p/@id"/>
								<div class="side-menu__item side-menu__item_level_3">
									<a href="{show_page}" class="side-menu__link{' side-menu__link_active'[$l3_active]}"><xsl:value-of select="header"/></a>
								</div>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>


</xsl:stylesheet>