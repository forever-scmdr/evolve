<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="is_jur" select="page/user_jur"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if (page/user_jur) then page/user_jur else page/user_phys"/>

	<xsl:template name="CONTENT">
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Оплата карточкой</h1>
		<p>Будет произведена оплата карточкой через систему webpay.</p>

		<h3>Заказ №<xsl:value-of select="$cart/order_num"/></h3>
		<div class="item-summ" style="padding-bottom: 20px;">
			Позиций: <xsl:value-of select="count($cart/bought)"/><br/>
			Сумма: <span><xsl:value-of select="$cart/sum"/> BYN</span>
		</div>
		<p><a class="script toggle" onclick="$('#order_details').toggle(); return false;">Подробности заказа</a></p>
		<div id="order_details" style="display: none;">
			<div class="checkout-cont1">
				<div class="info" style="padding-bottom: 20px;">
					<xsl:if test="$is_phys">
						<p>
							<span style="font-weight: bold;">Покупатель:</span>&#160; <xsl:value-of select="$contacts/name"/>
						</p>
						<p>
							<span style="font-weight: bold;">Телефон:</span>&#160; <xsl:value-of select="$contacts/phone"/>
						</p>
						<p>
							<span style="font-weight: bold;">E-mail:</span>&#160; <xsl:value-of select="$contacts/email"/>
						</p>
						<p>
							<span style="font-weight: bold;">Адрес:</span>&#160; <xsl:value-of select="$contacts/address"/>
						</p>
						<p>
							<span style="font-weight: bold;">Дополнительно:</span>&#160; <xsl:value-of select="$contacts/comment"/>
						</p>
					</xsl:if>
					<xsl:if test="not($is_phys)">
						<p>
							<span style="font-weight: bold;">Организация:</span>&#160; <xsl:value-of select="$contacts/organization"/>
						</p>
						<p>
							<span style="font-weight: bold;">Телефон/факс:</span>&#160; <xsl:value-of select="$contacts/phone"/>
						</p>
						<p>
							<span style="font-weight: bold;">Электнонный адрес:</span>&#160; <xsl:value-of select="$contacts/email"/>
						</p>
						<p>
							<span style="font-weight: bold;">Контактное лицо:</span>&#160; <xsl:value-of select="$contacts/contact_name"/>
						</p>
						<p>
							<span style="font-weight: bold;">Телефон контактного лица:</span>&#160; <xsl:value-of select="$contacts/contact_phone"/>
						</p>
						<p>
							<span style="font-weight: bold;">Юр. адрес:</span>&#160; <xsl:value-of select="$contacts/address"/>
						</p>
						<xsl:if test="not($contacts/no_account = 'да')">
							<p>
								<span style="font-weight: bold;">Расчетный счет:</span>&#160; <xsl:value-of select="$contacts/account"/>
							</p>
							<p>
								<span style="font-weight: bold;">Название банка:</span>&#160; <xsl:value-of select="$contacts/bank"/>
							</p>
							<p>
								<span style="font-weight: bold;">Адрес банка:</span>&#160; <xsl:value-of select="$contacts/bank_address"/>
							</p>
							<p>
								<span style="font-weight: bold;">Код банка:</span>&#160; <xsl:value-of select="$contacts/bank_code"/>
							</p>
						</xsl:if>
						<xsl:if test="$contacts/no_account = 'да'">
							<p>
								<span style="font-weight: bold;">Нет расчетного счета</span>
							</p>
						</xsl:if>
						<p>
							<span style="font-weight: bold;">УНП:</span>&#160; <xsl:value-of select="$contacts/unp"/>
						</p>
						<p>
							<span style="font-weight: bold;">Ф.И.О директора (индивидуального предпринимателя):</span>&#160; <xsl:value-of select="$contacts/director"/>
						</p>
						<p>
							<span style="font-weight: bold;">Действует на основании:</span>&#160; <xsl:value-of select="$contacts/base"/>
							<xsl:if test="$contacts/base != 'Устава'">
								&#160;№ <xsl:value-of select="$contacts/base_number"/> от <xsl:value-of select="$contacts/base_date"/>
							</xsl:if>
						</p>
						<p>
							<span style="font-weight: bold;">Дополнительно:</span>&#160; <xsl:value-of select="$contacts/comment"/>
						</p>
					</xsl:if>
				</div>

				<table>
					<tr>
						<th>
							Код
						</th>
						<th>
							Наименование
						</th>
						<th>
							Кол
						</th>
						<th>
							Цена
						</th>
						<th>
							Стоимость
						</th>
						<th>
							Наличие
						</th>
					</tr>
					<xsl:for-each select="$cart/bought">
						<xsl:sort select="type"/>
						<tr>
							<td>
								<xsl:value-of select="product/code"/>
							</td>
							<td valign="top">
								<strong><xsl:value-of select="product/name"/></strong>
							</td>
							<td valign="top">
								<xsl:value-of select="qty"/>
							</td>
							<td>
								<xsl:value-of select="product/price"/>
							</td>
							<td>
								<xsl:value-of select="sum"/>
							</td>
							<td>
								<xsl:value-of select="product/qty"/>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</div>

		<form action="https://securesandbox.webpay.by/" method="post">

			<div class="form-group">
				<label>Имя плательщика</label>
				<input class="form-control" type="text" name="wsb_customer_name" value="{$contacts/name}"/>
			</div>
			<div class="form-group">
				<label>Адрес плательщика</label>
				<input class="form-control" type="text" name="wsb_customer_address" value="{$contacts/address}"/>
			</div>
			<div class="form-group">
				<label>Телефон плательщика</label>
				<input class="form-control" type="text" name="wsb_phone" value="{$contacts/address}"/>
			</div>
			<div class="form-group">
				<label>Email плательщика</label>
				<input class="form-control" type="text" name="wsb_email" value="{$contacts/address}"/>
			</div>

			<input type="hidden" name="*scart"/>
			<input type="hidden" name="wsb_store" value="Магазин «метабо.бел»"/>
			<input type="hidden" name="wsb_version" value="2"/>
			<input type="hidden" name="wsb_language_id" value="russian"/>
			<input type="hidden" name="wsb_storeid" value="920427307"/>
			<input type="hidden" name="wsb_order_num" value="{concat('№', $cart/order_num)}"/>
			<input type="hidden" name="wsb_test" value="1"/>
			<input type="hidden" name="wsb_currency_id" value="BYN"/>
			<input type="hidden" name="wsb_seed" value="{$cart/item_own_extras/seed}"/>
			<input type="hidden" name="wsb_signature" value="{$cart/item_own_extras/signature}"/>
			<input type="hidden" name="wsb_service_date" value="Доставка до 1 января 2016 года"/>
			<input type="hidden" name="wsb_return_url" value="http://localhost:8080/webpay_success"/>
			<input type="hidden" name="wsb_cancel_return_url" value="http://localhost:8080/webpay_cancel"/>
			<input type="hidden" name="wsb_notify_url" value="http://localhost:8080/webpay_notify"/>

			<!-- Boughts -->
			<xsl:for-each select="$cart/bought">
				<xsl:variable name="p" select="position()-1"/>
				<input type="hidden" name="wsb_invoice_item_name[{$p}]" value="{product/name}"/>
				<input type="hidden" name="wsb_invoice_item_quantity[{$p}]" value="{qty}"/>
				<input type="hidden" name="wsb_invoice_item_price[{$p}]" value="{product/price}"/>
			</xsl:for-each>
			<!-- END_Boughts -->

			<input type="hidden" name="wsb_shipping_name" value="Доставка: {$contacts/ship_type}"/>
			<input type="hidden" name="wsb_shipping_price" value="0.00"/>

			<input type="hidden" name="wsb_total" value="{$cart/sum}"/>
			<input type="submit" value="Перейти к оплате через webpay"/>
		</form>
	</xsl:template>

</xsl:stylesheet>