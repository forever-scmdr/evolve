<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Заявка оформлена'" />

	<xsl:variable name="registration" select="page/registration[f:num(@id) &gt; 0]"/>
	<xsl:variable name="is_reg_jur" select="$registration/@type = 'user_jur'"/>

	<xsl:variable name="is_jur" select="true()"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="page/user_jur/input"/>

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
				<p>Сумма: <xsl:value-of select="f:exchange_cur($cart, 'sum', 0)"/></p>

				<p>E-mail/логин: <xsl:value-of select="$contacts/email"/></p>
				<p>ИНН: <xsl:value-of select="$contacts/inn"/></p>
				<p>Наименование организации: <xsl:value-of select="$contacts/organization"/></p>
				<p>КПП: <xsl:value-of select="$contacts/kpp"/></p>
				<p>Адрес: <xsl:value-of select="$contacts/address"/></p>
				<p>E-mail организации: <xsl:value-of select="$contacts/corp_email"/></p>
				<p>Телефон/факс: <xsl:value-of select="$contacts/phone"/></p>
				<p>Руководитель: <xsl:value-of select="$contacts/boss"/></p>
				<p>Должность руководителя: <xsl:value-of select="$contacts/boss_position"/></p>
				<p>Способ доставки: <xsl:value-of select="$contacts/ship_type"/></p>
				<p>Способ оплаты: <xsl:value-of select="$contacts/pay_type"/></p>
				<p>Дополнительно: <xsl:value-of select="$contacts/comment"/></p>
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
						<xsl:variable name="p" select="product"/>
<!--						<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>-->
<!--                        <xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>-->
						<xsl:variable name="price" select="price"/>
						<xsl:variable name="sum" select="sum"/>
						<tr>
							<td><xsl:value-of select="$p/code"/></td>
							<td><xsl:value-of select="$p/name"/></td>
							<td><xsl:value-of select="qty"/></td>
							<td><xsl:value-of select="$price"/><xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if></td>
							<td><xsl:value-of select="$sum"/></td>
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