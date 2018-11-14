<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:ext="http://exslt.org/common"
	xmlns="http://www.w3.org/1999/xhtml"
	version="2.0"
	exclude-result-prefixes="xsl saxon ext">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="menu_style" select="'menu-container3'"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->



	<xsl:template name="CONTENT">
		<!--center column -->
		<td class="center-c">
			<div class="c-div">
				<div class="c-top">
					<table class="c-table">
						<tr>
							<td>
								<xsl:call-template name="SEARCH"/>
							</td>
							<td width="21"></td>
							<xsl:call-template name="NEWS"/>
						</tr>
					</table>
				</div>
				<xsl:variable name="f" select="page/cart_contacts"/>
				<xsl:variable name="c" select="page/cart"/>
				<xsl:choose>
					<xsl:when test="$f/organization and $f/organization != ''">
						<h1>Ваш заказ принят.</h1>
						<p style="font-size: 20px">
							Счёт на оплату будет отправлен на указанный e-mail/факс после обработки заявки специалистом. 
							Вопросы по обработке заявок и выставлению счёта: (017) 316-14-10 Дина<br/>
							Вопросы по готовности оплаченного заказа: (017) 318-78-78 Ирина (Бухгалтерия)
						</p>
						<p>&#160;</p>
						<p style="font-size: 20px">
							Копия списка выбранных товаров отправлена на e-mail. 
						</p>
						<p>&#160;</p>
						<p style="font-size: 20px">
							<i>Данная форма заказа не является основанием для оплаты или отпуска товара! Производите оплату только после подтверждения 
							заказа и заключения договора!</i>
						</p>
						<p style="font-size: 20px">
							<i>В случае отказа или невозможности оплаты выставленного договора в течении 5 дней просьба сообщить об этом 
							по телефону (017) 318-78-78 Ирина (Бухгалтерия)</i>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="has_zero" select="$c/bought/product/qty = '0'"/>
						<xsl:variable name="has_non_zero" select="$c/bought/product/qty != '0'"/>
						<xsl:variable name="has_custom" select="$c/custom_bought[nonempty = 'true']"/>
						<xsl:variable name="has_shipping" select="$f/need_post_address = 'да'"/>
						<xsl:if test="not($has_zero) and not($has_custom)">
							<xsl:if test="not($has_shipping)">
								<h1>Ваш заказ принят, но пока не собран.</h1>
							</xsl:if>
							<xsl:if test="$has_shipping">
								<h1>Ваш заказ с отправкой почтой принят.</h1>
							</xsl:if>
							<p style="font-size: 20px">Он будет обработан в течении 0-5 дней. Ожидайте звонка специалиста.</p>
						</xsl:if>
						<xsl:if test="$has_zero or $has_custom">
							<h1>Ваша заявка принята.</h1>
							<xsl:if test="$has_non_zero">
								<p style="font-size: 20px">Заказ на товары в наличии будет обработан в течении 0-5 дней. Ожидайте звонка специалиста.</p>
							</xsl:if>
							<p style="font-size: 20px">Ответ по Персональному заказу и/или Заказным позициям будет отправлен на Ваш e-mail.</p>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$f/pay_by and $f/pay_by = 'card'">
					<form id="payment" action=" https://test.paysec.by/pay/order.cfm" method="post">
						<input type="hidden" name="Merchant_ID" value="462823"/>
						<input type="hidden" name="OrderNumber" value="{$c/order_num}"/>
						<input type="hidden" name="OrderAmount" id="order_sum" value="{$c/sum}"/>
						<input type="hidden" name="OrderCurrency" value="BYR"/>
						<input type="hidden" name="FirstName" value="{$f/name}"/>
						<input type="hidden" name="LastName" value="{$f/second_name}"/>
						<input type="hidden" name="Email" value="{$f/email}"/>
						<input type="hidden" name="OrderComment" value="{$f/phys_message}"/>
						<input type="hidden" name="Submit" value="Оплатить"/>
					</form>
					<script>
						$(document).ready(function() {
							var sum = $('#order_sum').val();
							$('#order_sum').val(sum.replace(/\s+/g, ''));
							$('#payment').submit();
						});
					</script>
				</xsl:if>
			</div>
		</td>
		<!--/center column -->
	</xsl:template>

</xsl:stylesheet>