<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet 	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 	xmlns:ext="http://exslt.org/common"	xmlns="http://www.w3.org/1999/xhtml"	version="2.0"	xmlns:f="f:f"	exclude-result-prefixes="xsl ext">	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>	<xsl:variable name="is_phys" select="not($is_jur)"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base" /><xsl:template match="/"><html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" /></head><body><div class="text-container">	<h1>Заказ №<xsl:value-of select="$cart/order_num"/></h1>	<div class="item-summ" style="padding-bottom: 20px;">		Позиций: <xsl:value-of select="count($cart/bought)"/><br/>		Сумма: <span><xsl:value-of select="$cart/sum"/></span>	</div>	<div class="checkout-cont1">		<div class="info" style="padding-bottom: 20px;">			<!--<xsl:if test="$is_phys">-->				<p style="margin: 0;">					<span style="font-weight: bold;">Покупатель:</span>&nbsp; <xsl:value-of select="$contacts/name"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Телефон:</span>&nbsp; <xsl:value-of select="$contacts/phone"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">E-mail:</span>&nbsp; <xsl:value-of select="$contacts/email"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Адрес:</span>&nbsp; <xsl:value-of select="$contacts/address"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Способ доставки:</span>&nbsp; <xsl:value-of select="$contacts/ship_type"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Способ оплаты:</span>&nbsp; <xsl:value-of select="$contacts/pay_type"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Дополнительно:</span>&nbsp; <xsl:value-of select="$contacts/comment"/>				</p>			<!-- </xsl:if> -->			<!--<xsl:if test="not($is_phys)">				<p style="margin: 0;">					<span style="font-weight: bold;">Организация:</span>&nbsp; <xsl:value-of select="$contacts/organization"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Телефон/факс:</span>&nbsp; <xsl:value-of select="$contacts/phone"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Электнонный адрес:</span>&nbsp; <xsl:value-of select="$contacts/email"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Контактное лицо:</span>&nbsp; <xsl:value-of select="$contacts/contact_name"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Телефон контактного лица:</span>&nbsp; <xsl:value-of select="$contacts/contact_phone"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Способ доставки:</span>&nbsp; <xsl:value-of select="$contacts/ship_type"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Способ оплаты:</span>&nbsp; <xsl:value-of select="$contacts/pay_type"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Юр. адрес:</span>&nbsp; <xsl:value-of select="$contacts/address"/>				</p>				<xsl:if test="not($contacts/no_account = 'да')">					<p style="margin: 0;">						<span style="font-weight: bold;">Расчетный счет:</span>&nbsp; <xsl:value-of select="$contacts/account"/>					</p>					<p style="margin: 0;">						<span style="font-weight: bold;">Название банка:</span>&nbsp; <xsl:value-of select="$contacts/bank"/>					</p>					<p style="margin: 0;">						<span style="font-weight: bold;">Адрес банка:</span>&nbsp; <xsl:value-of select="$contacts/bank_address"/>					</p>					<p style="margin: 0;">						<span style="font-weight: bold;">Код банка:</span>&nbsp; <xsl:value-of select="$contacts/bank_code"/>					</p>				</xsl:if>				<xsl:if test="$contacts/no_account = 'да'">					<p style="margin: 0;">						<span style="font-weight: bold;">Нет расчетного счета</span>					</p>				</xsl:if>				<p style="margin: 0;">					<span style="font-weight: bold;">УНП:</span>&nbsp; <xsl:value-of select="$contacts/unp"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Ф.И.О директора (индивидуального предпринимателя):</span>&nbsp; <xsl:value-of select="$contacts/director"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Действует на основании:</span>&nbsp; <xsl:value-of select="$contacts/base"/> 					<xsl:if test="$contacts/base != 'Устава'">						&nbsp;№ <xsl:value-of select="$contacts/base_number"/> от <xsl:value-of select="$contacts/base_date"/>					</xsl:if>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Дополнительно:</span>&nbsp; <xsl:value-of select="$contacts/comment"/>				</p>			</xsl:if>-->		</div> 		<table style="border: 1px solid #555;margin: 40px 0 20px;border-collapse: collapse;vertical-align: top;">			<tr style="border: 1px solid #555;">				<th style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					Код				</th>				<th valign="top" class="line-h" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Наименование				</th>				<th valign="top" class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Кол				</th>				<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Цена				</th>				<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Стоимость				</th>				<!--<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">-->					 <!--Наличие-->				<!--</th>-->			</tr>			<xsl:for-each select="$cart/bought">				<xsl:sort select="type"/>				<xsl:variable name="product" select="//page/product[code = current()/code]"/>				<tr style="border: 1px solid #555;">					<td style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<a href="{concat('https://www.ozon.ru/context/detail/id/', $product/code, '/?partner=mysteryby')}"><xsl:value-of select="$product/code"/></a>					</td>					<td valign="top" class="line-h" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<strong>							<a href="{concat($main_host, $product/show_product)}" target="_blank">								<xsl:value-of select="$product/name"/>							</a>						</strong>					</td>					<td valign="top" class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="qty"/>					</td>					<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="$product/price"/>					</td>					<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="sum"/>					</td>					<!--<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">-->						<!--<xsl:value-of select="product/qty"/>-->					<!--</td>-->				</tr>			</xsl:for-each>		</table>	</div></div></body></html></xsl:template></xsl:stylesheet>