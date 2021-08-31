<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'История заказов'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>




	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{page/personal_link}" class="path__link">Персональные данные</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CONTENT_INNER">
		<xsl:call-template name="MESSAGE"/>
		<div class="orders-history">
			<xsl:for-each select="page/purchase">
				<div class="orders-history__item past-order">
					<div class="past-order__link">
						<a href="{show_purchase}">Заказ № <xsl:value-of select="num"/></a>
					</div>
					<div class="past-order__date"><xsl:value-of select="date"/></div>
					<div class="past-order__price"><xsl:value-of select="sum"/>&#160;<xsl:value-of select="currency"/></div>
					<div class="past-order__button">
						<button class="button" onclick="location.href = '{to_cart_link}'; return false;"></button>
					</div>
					<div class="past-order__delete">
						<a href="{delete_purchase}" title="Удалить заказ из истории">
							<img src="img/icon-close.png" alt=""/>
						</a>
					</div>
				</div>
			</xsl:for-each>
		</div>

		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>