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
			<section class="single_text">
				<div class="container">
					<div class="text_box">
						<div class="left">
							<h1><xsl:value-of select="$h1"/></h1>
							<xsl:apply-templates select="$p" mode="content"/>
						</div>
						<div class="right">
							<div class="head">
							<h2 class="title">Популярное</h2>
							<div class="line"></div>
								<a href="{page/popular_link}" class="look_all">Смотреть все 
									<img src="img/look_all_right.svg" alt="look_all" class="light" />
									<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
								</a>
							</div>
							<div class="right_news_list">
								<xsl:for-each select="/page/popular">
									<a href="{show_page}" class="item">
										<span class="text">
											<span class="top_info_box">
												<!-- <span class="name">Источник: <xsl:value-of select="if (source != '') then source else 'Respectiva'"/></span> -->
												<span class="dot"></span>
												<span class="when" data-millis="{date/@millis}"><xsl:value-of select="date"/></span>
											</span>
											<p><xsl:value-of select="name"/></p>
										</span>
										<img src="{@path}{small_pic}" alt="{twtter_description}" style="max-width: 78px;" class="list_img"/>
									</a>
								</xsl:for-each>
							</div>
						</div>
					</div>
				</div>
			</section>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
	<!-- 	<script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
		<script src="//yastatic.net/share2/share.js"></script> -->
		
	</xsl:template>

</xsl:stylesheet>