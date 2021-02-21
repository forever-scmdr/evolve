<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/header"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="p" select="page/custom_page"/>

	<xsl:variable name="active_menu_item" select="$p/@key"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Home Page</a> <i class="fas fa-angle-right"></i>
				<xsl:for-each select="$p/parent">
					<a href="{show_page}"><xsl:value-of select="header"/></a> <i class="fas fa-angle-right"></i>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>

		<div class="catalog-items info">
			<xsl:for-each select="$p/custom_page">
				<div class="catalog-item page-item">
					 <a href="{show_page}" class="image-container" style="background-image: url('{@path}{main_pic}');"></a>
					 <!-- <a href="{show_page}">
					 						 <img src="{@path}{main_pic}" alt=""/>
					 						 <xsl:value-of select="header"/>
					 </a> -->
					<a href="{show_page}" style="display: block; margin-top: 16px;"><xsl:value-of select="header"/></a> 
					<div class="text" style="margin-top: 16px;">
						<xsl:value-of select="short" disable-output-escaping="yes"/>
					</div>
				</div>
			</xsl:for-each>
		</div>
		<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="$p" mode="content"/>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="INC_SIDE_MENU_INTERNAL">
		<div class="side-menu">
			<xsl:for-each select="page/custom_pages/custom_page[.//@id = $p/@id]"><!-- чтобы выводить все разделы надо удалить [custom_page/@id = $p/@id] -->
				<xsl:variable name="l1_active" select="@id = $p/@id"/>
				<div class="side-title">
					<xsl:value-of select="header"/>
				</div>
				<!--
				<xsl:variable name="l1_active" select="@id = $p/@id"/>
				<div class="level-1{' active'[$l1_active]}">
					<div class="capsule">
						<a href="{show_page}"><xsl:value-of select="header"/> </a>
					</div>
				</div>
				-->
				<xsl:if test=".//@id = $p/@id">
					<xsl:for-each select="custom_page">
						<xsl:variable name="l2_active" select="@id = $p/@id"/>
						<div class="level-2{' active'[$l2_active]}"><a href="{show_page}"><xsl:value-of select="header"/></a></div>
						<xsl:if test=".//@id = $p/@id">
							<xsl:for-each select="custom_page">
								<xsl:variable name="l3_active" select="@id = $p/@id"/>
								<div class="level-3{' active'[$l3_active]}"><a href="{show_page}"><xsl:value-of select="header"/></a></div>
								<xsl:if test=".//@id = $sel_sec_id">
									<xsl:for-each select="custom_page">
										<xsl:variable name="l4_active" select="@id = $p/@id"/>
										<div class="level-4{' active'[$l4_active]}"><a href="{show_page}"><xsl:value-of select="header"/></a></div>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>


</xsl:stylesheet>