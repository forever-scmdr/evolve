<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление завершено успешно'"/>
	<xsl:variable name="p" select="page/custom_page"/>

	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>


	<xsl:variable name="f" select="page/user[@type = ('user_phys', 'user_jur')]"/>
	<xsl:variable name="is_phys" select="$f/@type = 'user_phys'"/>
	<xsl:variable name="c" select="page/cart[1]"/>

	<!-- Есть товары -->
	<xsl:variable name="has_non_zero" select="$c/qty != '0'"/>
	<!-- Есть позиции под заказ -->
	<xsl:variable name="has_zero" select="$c/zero_qty != '0'"/>
	<!-- Есть индивидуальный заказ -->
	<xsl:variable name="has_custom" select="$c/custom_bought[nonempty = 'true']"/>


	<xsl:variable name="get_order_from" select="$f/get_order_from"/>
	<xsl:variable name="POST" select="contains($get_order_from, 'почт')" />
	<xsl:variable name="KUR" select="contains($get_order_from, 'урьер')" />
	<xsl:variable name="SELF" select="contains($get_order_from, 'амовывоз')" />


	<xsl:variable name="h1">
		<xsl:choose>
			<xsl:when test="$has_non_zero">Заказ №<xsl:value-of select="$c/order_num"/> принят. Копия заказа отправлена на указанный email (<xsl:value-of select="$f/email"/>).|<xsl:value-of select="$is_phys" /></xsl:when>
			<xsl:otherwise>Заявка принята. Копия заявки отправлена на указанный email (<xsl:value-of select="$f/email"/>).</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>


	<xsl:variable name="in_stock_text">
		<xsl:choose>
			<xsl:when test="$POST">
				<xsl:value-of select="page/order_emails/post" disable-output-escaping="yes" />
			</xsl:when>
			<xsl:when test="$KUR">
				<xsl:value-of select="page/order_emails/kur" disable-output-escaping="yes" />
			</xsl:when>
			<xsl:when test="$SELF">
				<xsl:value-of select="page/order_emails/self" disable-output-escaping="yes" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="page/order_emails/self" disable-output-escaping="yes" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="out_of_stock_text" select="page/order_emails/custom"/>



	<xsl:template name="CONTENT_INNER">
		<div class="text">
			<xsl:if test="not($is_phys)">
				<xsl:value-of select="page/order_emails/jur_text" disable-output-escaping="yes"/>
				<!--
				<xsl:if test="$has_non_zero">
					<xsl:value-of select="$in_stock_text" disable-output-escaping="yes"/>
				</xsl:if>
				-->
				<xsl:if test="$has_zero or $has_custom">
					<xsl:value-of select="$out_of_stock_text" disable-output-escaping="yes"/>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$is_phys">
				<xsl:if test="$has_non_zero">
					<div  style="font-size:20px">
						<xsl:value-of select="$in_stock_text" disable-output-escaping="yes"/>
					</div>
				</xsl:if>
				<xsl:if test="$has_zero or $has_custom">
					<div style="font-size: 20px;{' margin-top: 10px;'[$has_non_zero]}"><xsl:value-of select="$out_of_stock_text" disable-output-escaping="yes"/></div>
				</xsl:if>
			</xsl:if>
		</div>
		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>