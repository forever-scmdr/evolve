<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Заявка оформлена'" />

	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">Заявка оформлена</div>
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<div class="cart-confirm">
			<div class="cart-confirm__text">
				<p>Заявка №<xsl:value-of select="$cart/order_num"/></p>
				<p>Позиций: <xsl:value-of select="$cart/qty"/></p>
				<p>Сумма: <xsl:value-of select="$cart/sum"/></p>
				<xsl:if test="$is_phys">
					<p>Покупатель: <xsl:value-of select="$contacts/name"/></p>
					<p>Телефон: <xsl:value-of select="$contacts/phone"/></p>
					<p>E-mail: <xsl:value-of select="$contacts/email"/></p>
					<p>Адрес: <xsl:value-of select="$contacts/address"/></p>
					<p>Дополнительно: <xsl:value-of select="$contacts/comment"/></p>
				</xsl:if>
				<xsl:if test="not($is_phys)">
					<p>Организация: <xsl:value-of select="$contacts/organization"/></p>
					<p>Телефон/факс: <xsl:value-of select="$contacts/phone"/></p>
					<p>Электнонный адрес: <xsl:value-of select="$contacts/email"/></p>
					<p>Контактное лицо: <xsl:value-of select="$contacts/contact_name"/></p>
					<p>Телефон контактного лица: <xsl:value-of select="$contacts/contact_phone"/></p>
					<p>Юр. адрес: <xsl:value-of select="$contacts/address"/></p>
					<xsl:if test="not($contacts/no_account = 'да')">
						<p>Расчетный счет: <xsl:value-of select="$contacts/account"/></p>
						<p>Название банка: <xsl:value-of select="$contacts/bank"/></p>
						<p>Адрес банка: <xsl:value-of select="$contacts/bank_address"/></p>
						<p>Код банка: <xsl:value-of select="$contacts/bank_code"/></p>
					</xsl:if>
					<xsl:if test="$contacts/no_account = 'да'">
						<p>Нет расчетного счета</p>
					</xsl:if>
					<p>УНП: <xsl:value-of select="$contacts/unp"/></p>
					<p>Ф.И.О директора (индивидуального предпринимателя): <xsl:value-of select="$contacts/director"/></p>
					<p>
						Действует на основании: <xsl:value-of select="$contacts/base"/>
						<xsl:if test="$contacts/base != 'Устава'">
							&#160;№ <xsl:value-of select="$contacts/base_number"/> от <xsl:value-of select="$contacts/base_date"/>
						</xsl:if>
					</p>
					<p>Дополнительно: <xsl:value-of select="$contacts/comment"/></p>
				</xsl:if>
			</div>
			<div class="cart-confirm__table">
				<table>
					<tr>
						<td>Код</td>
						<td>Наименование</td>
						<td>Количество</td>
						<td>Цена</td>
						<td>Сумма</td>
						<xsl:if test="$cart/bought/item_own_extras" ><td>Дополнительно</td></xsl:if>
					</tr>
					<xsl:for-each select="$cart/bought">
						<xsl:sort select="type"/>
<!--						<xsl:variable name="product" select="//page/product[code = current()/code]"/>-->
						<xsl:variable name="product" select="product"/>
						<tr>
							<td><xsl:value-of select="$product/code"/></td>
							<td><xsl:value-of select="$product/name"/></td>
							<td><xsl:value-of select="qty"/></td>
							<td><xsl:value-of select="price"/><xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if></td>
							<td><xsl:value-of select="sum"/></td>
							<xsl:if test="$cart/bought/item_own_extras" >
								<td>
									<xsl:if test="item_own_extras/extra1"><p><xsl:value-of select="item_own_extras/extra1"/></p></xsl:if>
									<xsl:if test="item_own_extras/extra2"><p><xsl:value-of select="item_own_extras/extra2"/></p></xsl:if>
									<xsl:if test="item_own_extras/extra3"><p><xsl:value-of select="item_own_extras/extra3"/></p></xsl:if>
								</td>
							</xsl:if>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</div>
		<!-- <h3>Заявка №<xsl:value-of select="$cart/order_num"/></h3>
		<div class="checkout-cont1">

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
						<th>
		                                Наличие
		                            </th>
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
							<td>
								<xsl:value-of select="$product/qty"/>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</div> -->
	</xsl:template>

</xsl:stylesheet>