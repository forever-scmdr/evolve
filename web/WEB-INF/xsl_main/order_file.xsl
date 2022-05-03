<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet 	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 	xmlns:saxon="http://saxon.sf.net/"	xmlns:ext="http://exslt.org/common"	version="2.0"	xmlns:f="f:f"	exclude-result-prefixes="xsl saxon ext f">	<xsl:import href="utils/utils.xsl"/>	<xsl:output method="xml" encoding="UTF-8" media-type="application/xml" indent="yes" omit-xml-declaration="yes"/><xsl:variable name="cart" select="page/cart"/><xsl:variable name="contacts" select="page/user[@type = ('user_phys', 'user_jur')]"/><xsl:variable name="url_base" select="page/variables/url_base"/><xsl:variable name="is_phys" select="$contacts/@type = 'user_phys'"/>	<xsl:variable name="catalog_settings" select="page/price_catalogs/price_catalog"/><xsl:template match="/"><order num="{page/variables/order_num}">	<positions><xsl:value-of select="count($cart/bought)"/></positions>	<currency><xsl:value-of select="$cart/currency"/></currency>	<sum><xsl:value-of select="$cart/sum"/></sum>	<sum_words><xsl:value-of select="$cart/sum"/></sum_words>	<sum_no_discount><xsl:value-of select="$cart/simple_sum"/></sum_no_discount>	<discount_percent><xsl:value-of select="$cart/discount"/></discount_percent>	<discount><xsl:value-of select="$cart/margin"/></discount>	<xsl:if test="$is_phys">		<xsl:variable name="payment_item" select="//payment[@id = $contacts/payment]"/>		<phys send-by-mail="{if(contains($contacts/lower-case(get_order_from), 'почтой')) then 'yes' else if(contains($contacts/lower-case(get_order_from), 'курьер')) then 'kur' else 'no'}">			<data name="Фамилия"><xsl:value-of select="$contacts/second_name"/></data>			<data name="Имя"><xsl:value-of select="$contacts/name"/></data>			<data name="Телефон"><xsl:value-of select="$contacts/phone"/></data>			<data name="E-mail"><xsl:value-of select="$contacts/email"/></data>			<data name="Адрес доставки почтой"><xsl:value-of select="$contacts/post_index"/><xsl:text> </xsl:text><xsl:value-of select="$contacts/post_city"/><xsl:text> </xsl:text><xsl:value-of select="$contacts/post_address"/></data>			<data name="Дополнительно"><xsl:value-of select="$contacts/comment"/></data>			<data name="В случае отсутствия некоторых позиций"><xsl:value-of select="$contacts/if_absent"/></data>			<data name="Способ доставки"><xsl:value-of select="$contacts/get_order_from"/></data>			<data name="Оплата"><xsl:value-of select="$payment_item/xml_name"/></data><!--			<data name="Оплата"><xsl:value-of select="$contacts/pay_type"/></data>-->			<personal_percent><xsl:value-of select="$contacts/discount"/></personal_percent>		</phys>	</xsl:if>	<xsl:if test="not($is_phys)">	<jur send-by-mail="{if ($contacts/post_address != '') then 'yes' else 'no'}" has-no-bank="{if ($contacts/no_account = 'да') then 'yes' else 'no'}">		<data name="Организация"><xsl:value-of select="$contacts/organization"/></data>		<data name="Телефон/факс"><xsl:value-of select="$contacts/phone"/></data>		<data name="Электнонный адрес"><xsl:value-of select="$contacts/email"/></data>		<data name="Отправить договор на этот"><xsl:value-of select="$contacts/send_contract_to"/></data>		<data name="Контактное лицо"><xsl:value-of select="$contacts/contact_name"/></data>		<data name="Телефон контактного лица"><xsl:value-of select="$contacts/contact_phone"/></data>		<data name="Адрес доставки почтой"><xsl:value-of select="$contacts/post_address"/></data>		<data name="Юр. адрес"><xsl:value-of select="$contacts/address"/></data>		<data name="Расчетный счет"><xsl:value-of select="$contacts/account"/></data>		<data name="Название банка"><xsl:value-of select="$contacts/bank"/></data>		<data name="Адрес банка"><xsl:value-of select="$contacts/bank_address"/></data>		<data name="Код банка"><xsl:value-of select="$contacts/bank_code"/></data>		<data name="УНП"><xsl:value-of select="$contacts/unp"/></data>		<data name="Должность руководителя"><xsl:value-of select="$contacts/boss"/></data>		<data name="Ф.И.О руководителя"><xsl:value-of select="$contacts/director"/></data>		<data name="Действует на основании"><xsl:value-of select="$contacts/base"/></data>		<data name="Номер доверенности"><xsl:value-of select="$contacts/base_number"/></data>		<data name="Дата выдачи доверенности"><xsl:value-of select="$contacts/base_date"/></data><!--		<data name="Цель приобретения"><xsl:value-of select="$contacts/jur_aim"/></data>-->		<data name="Источник финансирования"><xsl:value-of select="$contacts/fund"/></data>		<data name="Дополнительно"><xsl:value-of select="$contacts/jur_message"/></data>	</jur>	</xsl:if>	<in_store>	<xsl:for-each select="$cart/bought[qty_avail != '0' and not(product/available = '-1')]">		<xsl:sort select="type"/>		<product code="{product/code}" store-b="{product/qty1}" store-s="{product/qty2}" qty="{qty_avail}" unit="{product/unit}" price="{price}" sum="{sum}" special="{if (product/special_price = 'true') then 'true' else 'false'}">			<position><xsl:value-of select="position()"/></position>			<name><xsl:value-of select="product/name"/></name>			<mark><xsl:value-of select="product/name_extra"/></mark>		</product>	</xsl:for-each>	</in_store>	<xsl:if test="$cart/bought/product[available = '-1']">		<in_stock>			<xsl:for-each select="$cart/bought[product/available = '-1']">				<xsl:sort select="type"/>				<xsl:variable name="p" select="product"/>				<xsl:variable name="settings" select="$catalog_settings[name = $p/group_id]"/>				<xsl:variable name="ship_time_calculated_days" select="$settings/ship_time_days[@key = current()/store]/@value"/>				<xsl:variable name="ship_time_days" select="if ($ship_time_calculated_days) then $ship_time_calculated_days else $settings/default_ship_time_days"/>				<product stock="{substring-before($p/group_id, '.')}" producer="{$p/vendor}" code="{concat(substring-before($p/store, '.'), $p/code)}"						 delivery_time="{$ship_time_days}" qty="{qty_avail}" unit="{$p/unit}" price="{price}" sum="{sum}">					<position><xsl:value-of select="position()"/></position>					<name><xsl:value-of select="$p/name"/></name>					<mark><xsl:value-of select="$p/vendor_code"/></mark>				</product>			</xsl:for-each>		</in_stock>	</xsl:if>	<not_in_store>	<xsl:for-each select="$cart/bought[qty_zero != '0']">	<xsl:sort select="type"/>		<product code="{product/code}" qty="{qty_zero}" unit="{product/unit}" old-price="{price}">			<position><xsl:value-of select="position()"/></position>			<name><xsl:value-of select="product/name"/></name>			<mark><xsl:value-of select="product/name_extra"/></mark>		</product>		</xsl:for-each>	</not_in_store>	<personal>	<xsl:for-each select="$cart/custom_bought[nonempty = 'true']">		<xsl:sort select="position"/>		<product>			<mark><xsl:value-of select="mark"/></mark>			<type><xsl:value-of select="type"/></type>			<case><xsl:value-of select="case"/></case>			<qty><xsl:value-of select="qty"/></qty>			<link><xsl:value-of select="link"/></link>			<extra><xsl:value-of select="extra"/></extra>		</product>	</xsl:for-each>	</personal></order></xsl:template></xsl:stylesheet>