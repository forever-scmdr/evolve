<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Показать статус заказа'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>

	<xsl:variable name="usr" select="page/user[@type = ('user_phys', 'user_jur')]"/>
	<xsl:variable name="email" select="$user/email"/>
	<xsl:variable name="number" select="$user/purchase/num"/>


	<xsl:template name="CONTENT_INNER">
		<div class="text">
			<form method="POST" action="{page/one_c_link}" id="order_status_form" ajax="true" ajax-loader-id="order_status">
				<table class="order-search-form">
					<tr>
						<td>
							Номер заказа
						</td>
						<td>
							<input id="order_number" type="text" name="order_number" value="{if($pv/order_number) then $pv/order_number else $number}" class="input"/>
						</td>
					</tr>
					<tr>
						<td>
							Адрес электронной почты
						</td>
						<td>
							<input type="text" name="email" value="{if($pv/email) then $pv/email else $email}" placeholder="указанный в заказе" class="input"/>
						</td>
					</tr>
					<tr>
						<td>

						</td>
						<td>
							<button class="button button_big button_secondary" type="submit" style="width: 176px">Найти</button>
						</td>
					</tr>
				</table>
			</form>

			<div id="order_status">
				<xsl:if test="not($pv/number) and $user/purchase">
					<xsl:attribute name="ajax-href" select="$user/purchase/one_c_restore"/>
				</xsl:if>
			</div>
		</div>
		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>