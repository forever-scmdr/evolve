<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>




	<xsl:template match="/">
		<table>
			<tbody id="extra-search-ajax" class="result">
			<xsl:if test="not(page/product[f:num(qty) != 0])">
				<!-- <xsl:if test="not(page/variables/admin = 'true')">
					<tr>
						<td colspan="10" style="text-align: center;">
							<h2>В дополнительном каталоге ничего не найдено</h2>
						</td>
					</tr>
				</xsl:if> -->
			</xsl:if>
			<xsl:if test="page/product[f:num(qty) != 0]">
				<!-- <xsl:if test="not(page/variables/admin = 'true')">
					<tr>
						<td colspan="10" style="text-align: center;">
							<h2>Результат поиска по дополнительному каталогу</h2>
						</td>
					</tr>
					<tr>
						<th>Название</th>
						<th>Описание</th>
						<th>Производитель</th>
						<th>Количество</th>
						<th>Срок поставки</th>
						<th>Единица</th>
						<th>Мин. заказ</th>
						<th>Цена (<xsl:value-of select="$currency_out"/>)</th>
						<th>Сумма (<xsl:value-of select="$currency_out"/>)</th>
						<xsl:if test="page/variables/admin = 'true'">
							<th>Начальная цена</th>
							<th>Склад</th>
							<th>Обновлено</th>
						</xsl:if>
						<th>Заказать</th>
					</tr>
				</xsl:if> -->
				<xsl:apply-templates select="page/product[f:num(qty) != 0]" />
			</xsl:if>
			<script>
				$(document).ready(function(){
					$(".magnific_popup-image")
					.magnificPopup({
         				type: 'image',
         				closeOnContentClick: true,
         				mainClass: 'mfp-img-mobile',
         				image: {
         					verticalFit: true
         				}
         			});
				});
			</script>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="product">
		<tr class="parent">
			<td>
				<xsl:if test="not(main_pic != '')">
					<b>
						<!-- <xsl:value-of select="name"/> -->
						<xsl:value-of select="vendor_code"/>
					</b>
				</xsl:if>
				<xsl:if test="not(main_pic = '')">
					<b>
						<a href="{main_pic}" class="magnific_popup-image" title="{name}">
							<!-- <xsl:value-of select="name"/> -->
							<xsl:value-of select="vendor_code"/>
						</a>
					</b>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="name"/>
				<!-- <xsl:value-of select="description" disable-output-escaping="yes"/> -->
				<p>
					<span onclick="$('#params_{@id}').toggle()" style="border-bottom: 1px dashed #707070; color: #707070; cursor: pointer">Характеристики</span>
				</p>
				<ul class="parameters" id="params_{@id}" style="display: none; margin-top: 8px; padding-left: 0;">
					<xsl:for-each select="parameter[@name != 'RoHSStatus' and @name != 'LeadStatus']">
						<li>
							<b>
								<xsl:value-of select="@name"/>:&nbsp;
							</b>
							<xsl:value-of select="."/>
						</li>
					</xsl:for-each>
				</ul>
			</td>
			<td>
				<xsl:value-of select="vendor"/>
			</td>
			<td><xsl:value-of select="qty"/></td>
			<td>
				<xsl:value-of select="concat(/page/price_catalog/default_ship_time, ' дней')"/>
			</td>
<!--			<td>шт.</td>-->
			<td><xsl:value-of select="min_qty"/></td>
			<td>
				<xsl:for-each select="spec_price_map" >
					<p>
						<xsl:value-of select="concat(f:convert_curr(@price), '&nbsp;от&nbsp;', @qty)"/>
					</p>
				</xsl:for-each>
			</td>
			<!--
			<td>
				<xsl:for-each select="spec_price_map" >
					<p>
						<xsl:value-of select="concat('x', @qty, ' = ', f:convert_curr(@sum))" />
					</p>
				</xsl:for-each>
			</td>
			-->
			<xsl:if test="//page/variables/admin = 'true'">
				<td>
					<xsl:for-each select="spec_price_map" >
					<p>
						<xsl:value-of select="f:convert_curr_no_extra(@price)"/>
					</p>
				</xsl:for-each>
				</td>
				<td>digikey.com</td>
				<td></td>
			</xsl:if>

			<td id="cart_search_{code}">
				<form action="cart_action/?action=addDigiKeyToCart&amp;code={code}" method="post" ajax="true" ajax-loader-id="cart_search_{@id}">
					<input type="number" name="qty" value="{min_qty}" min="{min_qty}" step="{min_qty}"/>
					<input type="hidden" name="img" value="{main_pic}"/>
					<input type="hidden" name="map" value="{spec_price}"/>
					<input type="hidden" value="{name}" name="name"/>
					<input type="hidden" value="{qty}" name="max"/>
					<input type="hidden" value="{vendor}" name="vendor"/>
					<input type="hidden" value="{vendor_code}" name="vendor_code"/>
					<input type="hidden" value="{vendor}" name="vendor"/>
					<input type="hidden" value="{url}" name="url"/>
					<input type="hidden" value="digikey.com" name="store"/>
					<input type="submit" value="Заказать"/>
				</form>
			</td>
		</tr>
	</xsl:template>


	<xsl:function name="f:convert_curr">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="$currency = 'BYN'">
				<xsl:sequence select="format-number(f:byn($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'USD'">
				<xsl:sequence select="format-number(f:usd($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'RUB'">
				<xsl:sequence select="format-number(f:rub($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'EUR'">
				<xsl:sequence select="format-number(f:eur($str), '#0.0000')"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:convert_curr_no_extra">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="$currency = 'BYN'">
				<xsl:sequence select="format-number(f:byn_n($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'USD'">
				<xsl:sequence select="format-number(f:usd_n($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'RUB'">
				<xsl:sequence select="format-number(f:rub_n($str), '#0.0000')"/>
			</xsl:when>
			<xsl:when test="$currency = 'EUR'">
				<xsl:sequence select="format-number(f:eur_n($str), '#0.0000')"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>

	<xsl:variable name="cur_list" select="page/currencies"/>
	<xsl:variable name="quotient" select="f:num(page/price_catalog[name = 'digikey.com']/quotient)"/>


	<xsl:function name="f:byn" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="usd" select="f:num($str) * $quotient"/>
		<xsl:sequence select="$usd * (f:num($cur_list/USD_rate) * (f:num($cur_list/USD_extra)+1)) div f:num($cur_list/USD_scale)"/>
	</xsl:function>
	<xsl:function name="f:usd" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/USD_rate) * (f:num($cur_list/USD_extra)+1) ) * f:num($cur_list/USD_scale)" />
	</xsl:function>

	<xsl:function name="f:eur" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/EUR_rate) * (f:num($cur_list/EUR_extra)+1) ) * f:num($cur_list/EUR_scale)" />
	</xsl:function>
	<xsl:function name="f:rub" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/RUB_rate) * (f:num($cur_list/RUB_extra)+1) ) * f:num($cur_list/RUB_scale)" />
	</xsl:function>

	<xsl:function name="f:byn_n" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="usd" select="f:num($str)"/>
		<xsl:sequence select="$usd * (f:num($cur_list/USD_rate) * 1) div f:num($cur_list/USD_scale)"/>
	</xsl:function>
	<xsl:function name="f:usd_n" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn_n($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/USD_rate) * 1) * f:num($cur_list/USD_scale)" />
	</xsl:function>

	<xsl:function name="f:eur_n" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn_n($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/EUR_rate) * 1) * f:num($cur_list/EUR_scale)" />
	</xsl:function>
	<xsl:function name="f:rub_n" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="byn" select="f:byn_n($str)"/>
		<xsl:sequence select="$byn div (f:num($cur_list/RUB_rate) * 1) * f:num($cur_list/RUB_scale)" />
	</xsl:function>


</xsl:stylesheet>