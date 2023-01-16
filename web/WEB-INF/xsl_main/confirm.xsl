<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Заявка оформлена'" />

	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>
	<xsl:variable name="has_simple" select="page/variables/simple_order_num != ''"/>
	<xsl:variable name="has_complex" select="page/variables/complex_order_num != ''"/>
	<xsl:variable name="need_tabs" select="$has_complex and $has_simple"/>

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
		<xsl:if test="$need_tabs">
			<div class="tabs">
				<div class="tabs__nav">
					<a href="#regular" class="tab tab_active">Заказ (обычные товары)</a>
					<a href="#complex" class="tab">Предаказ (товары с опциями)</a>
				</div>
				<div class="tabs__content">
					<div class="tab-container" id="regular">
						<xsl:call-template name="REGULAR_CART"/>
					</div>
					<div class="tab-container" id="complex" style="display:none;">
						<xsl:call-template name="COMPLEX_CART"/>
					</div>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not($need_tabs) and $has_simple">
			<xsl:call-template name="REGULAR_CART"/>
		</xsl:if>
		<xsl:if test="not($need_tabs) and $has_complex">
			<xsl:call-template name="COMPLEX_CART"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="REGULAR_CART">
		<div class="cart-confirm">
			<div class="cart-confirm__text">
				<p>Заявка №<xsl:value-of select="page/variables/simple_order_num"/></p>
				<p>Позиций: <xsl:value-of select="$cart/qty"/></p>
				<p>Сумма: <xsl:value-of select="f:format_currency_thousands(f:num($cart/sum_discount))"/></p>


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
					<xsl:for-each select="$cart/bought[f:num(is_complex) = 0]">
						<xsl:apply-templates select="current()" mode="simple"/>
					</xsl:for-each>
				</table>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="COMPLEX_CART">
		<div class="cart-confirm">
			<div class="cart-confirm__text">
				<p>Заявка №<xsl:value-of select="page/variables/complex_order_num"/></p>
				<p>Позиций: <xsl:value-of select="$cart/qty"/></p>
				<p>Сумма: <xsl:value-of select="f:format_currency_thousands(f:num($cart/p_sum_discount))"/></p>

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

			</div>
			<div class="cart-confirm__table">
				<table>
					<tr>
						<td>Код</td>
						<td>Наименование</td>
						<td>Цена</td>
						<td>Сумма</td>
						<xsl:if test="$cart/bought/item_own_extras" ><td>Дополнительно</td></xsl:if>
					</tr>
					<xsl:for-each select="$cart/bought[f:num(is_complex) = 1]">
						<xsl:apply-templates select="current()" mode="complex"/>
					</xsl:for-each>
				</table>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="bought" mode="simple">
		<xsl:variable name="product" select="//page/product[code = current()/code]"/>
		<tr>
			<td><xsl:value-of select="$product/code"/></td>
			<td>
				<xsl:value-of select="$product/name"/>
			</td>
			<td><xsl:value-of select="qty"/></td>
			<td><xsl:value-of select="$product/price"/><xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if></td>
			<td><xsl:value-of select="f:format_currency_thousands(f:num(sum))"/></td>
			<xsl:if test="$cart/bought/item_own_extras" >
				<td>
					<xsl:if test="item_own_extras/extra1"><p><xsl:value-of select="item_own_extras/extra1"/></p></xsl:if>
					<xsl:if test="item_own_extras/extra2"><p><xsl:value-of select="item_own_extras/extra2"/></p></xsl:if>
					<xsl:if test="item_own_extras/extra3"><p><xsl:value-of select="item_own_extras/extra3"/></p></xsl:if>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="bought" mode="complex">
		<xsl:variable name="product" select="//page/product[code = current()/code]"/>
		<tr>
			<td><xsl:value-of select="$product/code"/></td>
			<td>
				<xsl:value-of select="$product/name"/>
				<xsl:if test="pseudo_option">
					<ul>
						<xsl:for-each select="pseudo_option">
							<li>
								<xsl:value-of select="concat(name ,': ', f:format_currency_thousands(f:num(price)))"/>
							</li>
						</xsl:for-each>
					</ul>
				</xsl:if>
			</td>
			<td><xsl:value-of select="$product/price"/><xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if></td>
			<td><xsl:value-of select="f:format_currency_thousands(f:num(sum))"/></td>
			<xsl:if test="$cart/bought/item_own_extras" >
				<td>
					<xsl:if test="item_own_extras/extra1"><p><xsl:value-of select="item_own_extras/extra1"/></p></xsl:if>
					<xsl:if test="item_own_extras/extra2"><p><xsl:value-of select="item_own_extras/extra2"/></p></xsl:if>
					<xsl:if test="item_own_extras/extra3"><p><xsl:value-of select="item_own_extras/extra3"/></p></xsl:if>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>

</xsl:stylesheet>