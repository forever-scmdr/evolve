<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Заявка оформлена'" />

	<xsl:variable name="is_jur" select="true()"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>
	<xsl:variable name="prcat" select="page/price_catalogs/price_catalog"/>

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
				<xsl:if test="$is_phys">
					<p>Покупатель: <xsl:value-of select="$contacts/name"/></p>
					<p>Телефон: <xsl:value-of select="$contacts/phone"/></p>
					<p>E-mail: <xsl:value-of select="$contacts/email"/></p>
					<p>Адрес: <xsl:value-of select="$contacts/address"/></p>
					<p>Дополнительно: <xsl:value-of select="$contacts/comment"/></p>
				</xsl:if>
				<xsl:if test="not($is_phys)">
					<p>Организация: <xsl:value-of select="$contacts/organization"/></p>
					<p>E-mail: <xsl:value-of select="$contacts/email"/></p>
					<p>ОРГН: <xsl:value-of select="$contacts/orgn"/></p>
					<p>КПП: <xsl:value-of select="$contacts/kpp"/></p>
					<p>ИНН: <xsl:value-of select="$contacts/inn"/></p>
					<p>Юридический адрес: <xsl:value-of select="$contacts/address"/></p>
					<p>Фактический адрес: <xsl:value-of select="$contacts/fact_address"/></p>
					<p>Почтовый адрес: <xsl:value-of select="$contacts/post_address"/></p>
					<p>Сайт организации: <xsl:value-of select="$contacts/web_site"/></p>
					<p>Контактное лицо: <xsl:value-of select="$contacts/contact_name"/></p>
					<p>Телефон контактного лица: <xsl:value-of select="$contacts/contact_phone"/></p>
					<p>E-mail контактного лица: <xsl:value-of select="$contacts/contact_email"/></p>
					<p>Дополнительно: <xsl:value-of select="$contacts/comment"/></p>
				</xsl:if>
			</div>
			<div class="cart-confirm__table">
				<table>
					<tr>
<!--						<td>Код</td>-->
						<td>Наименование</td>
						<td>Поставщик</td>
						<td>Количество</td>
						<td>Цена</td>
						<td>Сумма</td>
						<xsl:if test="$cart/bought/item_own_extras" ><td>Дополнительно</td></xsl:if>
					</tr>
					<xsl:for-each select="$cart/bought">
						<xsl:sort select="type"/>
<!--						<xsl:variable name="product" select="//page/product[code = current()/code]"/>-->
						<xsl:variable name="p" select="product"/>
						<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>
                        <xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>
						<xsl:variable name="plain_section" select="$p/plain_section"/>
						<xsl:variable name="plain" select="if ($p/section_name and not($p/section_name = '')) then $p/section_name else $p/plain_section/name"/>
                        <xsl:variable name="pc" select="$prcat[name = $plain]"/>
						<tr>
<!--							<td><xsl:value-of select="$p/code"/></td>-->
							<td><xsl:value-of select="$p/name"/></td>
							<td><xsl:value-of select="$pc/other_name"/></td>
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