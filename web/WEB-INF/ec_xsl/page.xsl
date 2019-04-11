<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="p" select="page/custom_page"/>

	<xsl:variable name="active_menu_item" select="$p/@key"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>


	<xsl:template name="CONTENT">
		<section class="s-content s-content--narrow s-content--no-padding-bottom">
			<div class="row">
				<div class="s-content__header col-full">
					<h1 class="s-content__header-title">
						<xsl:value-of select="$h1"/>
					</h1>
				</div>
				<div class="col-full s-content__main">
					<xsl:apply-templates select="$p" mode="content"/>
					<div class="ya-share2" data-services="vkontakte,facebook,twitter" data-limit="3"></div>
					<div style="height: 3rem;"></div>
				</div>

			</div>
		</section>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
		<script src="//yastatic.net/share2/share.js"></script>
		
	</xsl:template>

</xsl:stylesheet>