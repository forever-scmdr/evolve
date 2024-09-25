<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet 	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 	xmlns:ext="http://exslt.org/common"	xmlns="http://www.w3.org/1999/xhtml"	version="2.0"	xmlns:f="f:f"	exclude-result-prefixes="xsl ext">	<xsl:import href="utils/utils.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="is_jur" select="true()"/>	<xsl:variable name="is_phys" select="not($is_jur)"/>	<xsl:variable name="is_shop" select="page/variables/is_shop = 'true'"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base" />	<xsl:variable name="registration" select="page/registration[f:num(@id) &gt; 0]"/>	<xsl:variable name="is_reg_jur" select="$registration/@type = 'user_jur'"/>	<xsl:template match="/"><html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" /></head><body><div class="text-container">	<p style="font-style: italic;">	Это письмо отправлено автоматически.	</p>	<xsl:if test="$is_phys">		<p>Ваша заявка принята. В ближайшее время наши специалисты свяжутся с вами.</p>	</xsl:if>	<h1>Заказ №<xsl:value-of select="$cart/order_num"/></h1>	<div class="item-summ" style="padding-bottom: 20px;">		Позиций: <xsl:value-of select="count($cart/bought)"/><br/>		Сумма: <span><xsl:value-of select="f:exchange_cur($cart, 'sum', 0)"/></span>	</div>	<div class="checkout-cont1">		<div class="info" style="padding-bottom: 20px;">			<xsl:if test="not($is_phys)">				<p style="margin: 0;">					<span style="font-weight: bold;">Организация:</span>&nbsp; <xsl:value-of select="$contacts/organization"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">E-mail:</span>&nbsp; <xsl:value-of select="$contacts/email"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">ОРГН:</span>&nbsp; <xsl:value-of select="$contacts/orgn"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">КПП:</span>&nbsp; <xsl:value-of select="$contacts/kpp"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">ИНН:</span>&nbsp; <xsl:value-of select="$contacts/inn"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Юридический адрес:</span>&nbsp; <xsl:value-of select="$contacts/address"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Фактический адрес:</span>&nbsp; <xsl:value-of select="$contacts/fact_address"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Почтовый адрес:</span>&nbsp; <xsl:value-of select="$contacts/post_address"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Сайт организации:</span>&nbsp; <xsl:value-of select="$contacts/web_site"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Контактное лицо:</span>&nbsp; <xsl:value-of select="$contacts/contact_name"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Телефон контактного лица:</span>&nbsp; <xsl:value-of select="$contacts/contact_phone"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">E-mail контактного лица:</span>&nbsp; <xsl:value-of select="$contacts/contact_email"/>				</p>				<p style="margin: 0;">					<span style="font-weight: bold;">Дополнительно:</span>&nbsp; <xsl:value-of select="$contacts/comment"/>				</p>			</xsl:if>		</div>		<table style="border: 1px solid #555;margin: 40px 0 20px;border-collapse: collapse;vertical-align: top;">			<tr style="border: 1px solid #555;">				<th style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					Код				</th>				<th valign="top" class="line-h" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Наименование				</th>				<xsl:if test="$is_shop">					<th valign="top" class="line-h" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">						 Поставщик					</th>				</xsl:if>				<th valign="top" class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Кол				</th>				<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					 Цена				</th>				<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">					Стоимость				</th>				<xsl:if test="$is_shop">					<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">						Цена поставщика<p/> в валюте поставщика					</th>					<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">						Стоимость поставщика<p/> в валюте поставщика					</th>				</xsl:if>				<xsl:if test="$cart/bought/item_own_extras" >					<th class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;text-align: left;">						 Дополнительно					</th>				</xsl:if>			</tr>			<xsl:for-each select="$cart/bought">				<xsl:sort select="type"/><!--				<xsl:variable name="product" select="//page/product[code = current()/code]"/>-->				<xsl:variable name="product" select="product"/>				<xsl:variable name="has_price" select="if ($is_reg_jur) then ($product/price_opt and $product/price_opt != '0') else ($product/price and $product/price != '0')"/>				<xsl:variable name="price" select="if (f:num(price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>				<xsl:variable name="sum" select="if (f:num(price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>				<tr style="border: 1px solid #555;">					<td style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="$product/code"/>					</td>					<td valign="top" class="line-h" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<strong>							<xsl:if test="f:num($product/@id) &gt; 0">								<a href="{concat($main_host, $product/show_product)}" target="_blank">									<xsl:value-of select="$product/name"/>								</a>							</xsl:if>							<xsl:if test="f:num($product/@id) &lt; 0">								<xsl:value-of select="$product/name"/>							</xsl:if>						</strong>					</td>					<xsl:if test="$is_shop">						<td valign="top" class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">							<xsl:value-of select="$product/category_id"/>						</td>					</xsl:if>					<td valign="top" class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="qty"/>					</td>					<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="$price"/>						<xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if>					</td>					<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">						<xsl:value-of select="$sum"/>					</td>					<xsl:if test="$is_shop">						<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">							<xsl:value-of select="price_vendor"/>							<xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if>						</td>						<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">							<xsl:value-of select="sum_vendor"/>						</td>					</xsl:if>					<xsl:if test="$cart/bought/item_own_extras" >						<td class="price-cel1" style="border: 1px solid #555;border-collapse: collapse;vertical-align: top;padding: 10px;">							<xsl:if test="item_own_extras/extra1">								<p><xsl:value-of select="$product/extra_input[1]" />: <xsl:value-of select="item_own_extras/extra1"/></p>							</xsl:if>							<xsl:if test="item_own_extras/extra2">								<p><xsl:value-of select="$product/extra_input[2]" />: <xsl:value-of select="item_own_extras/extra2"/></p>							</xsl:if>							<xsl:if test="item_own_extras/extra3">								<p><xsl:value-of select="$product/extra_input[3]" />: <xsl:value-of select="item_own_extras/extra3"/></p>							</xsl:if>						</td>					</xsl:if>				</tr>			</xsl:for-each>		</table>	</div></div></body></html></xsl:template></xsl:stylesheet>