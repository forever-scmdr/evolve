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

	<xsl:variable name="cur_list" select="page/currencies"/>
	<xsl:variable name="quotient" select="f:num(page/price_catalog[name = 'promelec.ru']/quotient)"/>

	<xsl:template match="/">
		<table>
			<tbody id="extra-search-ajax-promelec" class="result">
				<xsl:if test="not(page/variables/admin = 'true')">
					<tr>

						<td colspan="5">
							RUB ratio = <xsl:value-of select="$cur_list/RUB_rate"/>
							<br/>
							Ratio quotient = <xsl:value-of select="f:num($cur_list/RUB_extra)+1.01"/>
							<br/>
							Promelec Quotient = <xsl:value-of select="$quotient"/><br/>
							100RUB = <xsl:value-of select="format-number(f:byn('100'), '#0.0000')"/>BYN
						</td>
						<td id="cart_search_mock100" colspan="5">
							<form action="cart_action/?action=addDigiKeyToCart&amp;code={'mock100'}" method="post" ajax="true" ajax-loader-id="cart_search_{'mock100'}">
								<input type="number" name="qty" value="1" min="1" step="1"/>
								<input type="hidden" name="img" value=""/>
								<input type="hidden" name="map" value="1:100"/>
								<input type="hidden" value="mock device" name="name"/>
								<input type="hidden" value="100500" name="max"/>
								<input type="hidden" value="forever" name="vendor"/>
								<input type="hidden" value="mock100" name="vendor_code"/>
								<input type="hidden" value="RUB" name="currency_code"/>
								<input type="hidden" value="promelec.ru" name="quotients"/>
								<input type="submit" value="Заказать девайс за 100 RUB"/>
							</form>
						</td>
					</tr>
				</xsl:if>
			<xsl:if test="not(page/result/row)">
				<tr>
					<td colspan="10" style="text-align: center;">
						<h2>В каталоге promelec.ru ничего не найдено</h2>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="page/result/row">
				<tr>
					<td colspan="10" style="text-align: center;">
						<h2>Результат поиска по каталогу promelec.ru</h2>
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
				<xsl:apply-templates select="page/result/row" />
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

	<xsl:template match="row">
		<xsl:variable name="main_pic" select="@photo_url"/>
		<xsl:variable name="vendor_code" select="@sort_name"/>
<!--		<xsl:variable name="in_stock" select="f:num(@quant) &gt; 0"/>-->
		<xsl:variable name="default_pb" select="pricebreaks"/>
		<xsl:variable name="pricebreaks" select="if($default_pb) then $default_pb else vendors/vendor[1]/pricebreaks"/>
		<xsl:variable name="map" select="string-join($pricebreaks/break/concat(@quant,':', @price),';')"/>


		<tr class="parent">
			<td>
					<b>
						<xsl:if test="$main_pic != ''">
							<a href="{$main_pic}" class="magnific_popup-image" title="{@name}">
								<xsl:value-of select="$vendor_code"/>
							</a>
						</xsl:if>
						<xsl:if test="not($main_pic != '')">
							<xsl:value-of select="$vendor_code"/>
						</xsl:if>
					</b>
			</td>
			<td>
				<xsl:value-of select="@name"/>
				<p><xsl:value-of select="@description"/></p>
			</td>
			<td>
				<xsl:value-of select="@producer_name"/>
			</td>
			<td><xsl:value-of select="@quant"/></td>
			<td>
				<xsl:value-of select="concat(/page/price_catalog/default_ship_time, ' дней')"/>
			</td>
			<td>шт.</td>
			<td><xsl:value-of select="f:num(@moq)"/></td>
			<td>
<!--				<xsl:if test="pricebreaks">-->
					<xsl:for-each select="$pricebreaks/break">
						<p>
							<xsl:value-of select="f:convert_curr(@price)"/>
						</p>
					</xsl:for-each>
<!--				</xsl:if>-->
			</td>
			<td>
<!--				<xsl:if test="pricebreaks">-->
					<xsl:for-each select="$pricebreaks/break">
						<p>
							<xsl:variable name="p" select="f:convert_curr(@price)"/>
							<xsl:variable name="sum" select="f:num(@quant) * f:num($p)"/>
							<xsl:value-of select="concat('x', @quant, ' = ', format-number($sum,'#0.0000'))" />
						</p>
					</xsl:for-each>
<!--				</xsl:if>-->
			</td>
			<xsl:if test="//page/variables/admin = 'true'">
				<td>
<!--					<xsl:if test="pricebreaks">-->
						<xsl:for-each select="break">
							<p>
								<xsl:value-of select="f:convert_curr_no_extra(@price)"/>
							</p>
						</xsl:for-each>
<!--					</xsl:if>-->
				</td>
				<td>promelec.ru</td>
				<td>

				</td>
			</xsl:if>

			<td id="cart_search_{@item_id}">
				<form action="cart_action/?action=addDigiKeyToCart&amp;code={@item_id}" method="post" ajax="true" ajax-loader-id="cart_search_{@id}">
					<input type="number" name="qty" value="{f:num(@moq)}" min="{f:num(@moq)}" step="{f:num(@moq)}"/>
					<input type="hidden" name="img" value="{$main_pic}"/>
					<input type="hidden" name="map" value="{$map}"/>
					<input type="hidden" value="{@name}" name="name"/>
					<input type="hidden" value="{@quant}" name="max"/>
					<input type="hidden" value="{@producer_name}" name="vendor"/>
					<input type="hidden" value="{@altname}" name="vendor_code"/>
					<input type="hidden" value="RUB" name="currency_code"/>
					<input type="hidden" value="promelec.ru" name="quotients"/>
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




	<xsl:function name="f:byn" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:variable name="rub" select="f:num($str) * $quotient"/>
		<xsl:sequence select="$rub * (f:num($cur_list/RUB_rate) * (f:num($cur_list/RUB_extra)+1.01)) div f:num($cur_list/RUB_scale)"/>
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
		<xsl:variable name="rub" select="f:num($str)"/>
		<xsl:sequence select="$rub * (f:num($cur_list/RUB_rate) * 1) div f:num($cur_list/RUB_scale)"/>
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