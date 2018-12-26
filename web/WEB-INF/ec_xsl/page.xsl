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
				<div class="s-content__media col-full">
					<div class="s-content__post-thumb">
						<img src="{concat($p/@path, $p/main_pic)}"
							 srcset="{concat($p/@path, $p/main_pic)} 2000w,
                                 {concat($p/@path, $p/medium_pic)} 1000w,
                                 {concat($p/@path, $p/small_pic)} 500w"
							 sizes="(max-width: 2000px) 100vw, 2000px" alt="" />
					</div>
				</div>
				<div class="col-full s-content__main">
					<xsl:apply-templates select="$p" mode="content"/>
					<div style="height: 3rem;"></div>
				</div>

			</div>
		</section>
	</xsl:template>

</xsl:stylesheet>