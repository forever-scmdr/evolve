<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Заявка оформлена'" />

	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Home Page</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Спасибо за заявку!</h1>


		<h3>Заявка №<xsl:value-of select="$cart/order_num"/></h3>
		<div class="item-summ" style="padding-bottom: 20px;">
			Позиций: <xsl:value-of select="count($cart/bought)"/><br/>
			Сумма: <span><xsl:value-of select="$cart/sum"/></span> руб.
		</div>
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

			<div class="table-responsive">
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
						<!-- 	<th>
                                Наличие
                            </th> -->
					</tr>
					<xsl:for-each select="$cart/bought">
						<xsl:sort select="type"/>
						<xsl:variable name="product" select="//page/product[code = current()/code]"/>
						<tr>
							<td>
								<xsl:value-of select="$product/code"/>
							</td>
							<td valign="top">
								<strong><xsl:value-of select="$product/name"/></strong>
							</td>
							<td valign="top">
								<xsl:value-of select="qty"/>
							</td>
							<td>
								<xsl:value-of select="$product/price"/>
								<xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if>
							</td>
							<td>
								<xsl:value-of select="sum"/>
							</td>
							<!-- <td>
								<xsl:value-of select="$product/qty"/>
							</td> -->
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</div>
		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>