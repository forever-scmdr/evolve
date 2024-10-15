<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet 	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 	xmlns:ext="http://exslt.org/common"	xmlns="http://www.w3.org/1999/xhtml"	version="2.0"	xmlns:f="f:f"	exclude-result-prefixes="xsl ext">	<xsl:import href="utils/utils.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/plain" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="is_jur" select="true()"/>	<xsl:variable name="is_phys" select="not($is_jur)"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base" />	<xsl:variable name="registration" select="page/registration[f:num(@id) &gt; 0]"/>	<xsl:variable name="is_reg_jur" select="$registration/@type = 'user_jur'"/>	<xsl:variable name="prcat" select="page/price_catalogs/price_catalog"/>	<xsl:template match="/">	{"order_num":"<xsl:value-of select="$cart/order_num"/>","position_qty":<xsl:value-of select="count($cart/bought)"/>,"sum":<xsl:value-of select="f:exchange($cart, 'sum', 0)"/>,"customer":{	"type":"jur",	"organization":"<xsl:value-of select="$contacts/organization"/>",	"email":"<xsl:value-of select="$contacts/email"/>",		"orgn":"<xsl:value-of select="$contacts/orgn"/>",	"kpp":"<xsl:value-of select="$contacts/kpp"/>",	"inn":"<xsl:value-of select="$contacts/inn"/>",	"address":"<xsl:value-of select="$contacts/address"/>",	"fact_address":"<xsl:value-of select="$contacts/fact_address"/>",	"post_address":"<xsl:value-of select="$contacts/post_address"/>",	"web_site":"<xsl:value-of select="$contacts/web_site"/>",	"contact_name":"<xsl:value-of select="$contacts/contact_name"/>",	"contact_phone":"<xsl:value-of select="$contacts/contact_phone"/>",	"contact_email":"<xsl:value-of select="$contacts/contact_email"/>",	"comment":"<xsl:value-of select="$contacts/comment"/>"},	"positions":[	<xsl:for-each select="$cart/bought">		<xsl:variable name="product" select="product"/>		<xsl:variable name="has_price" select="if ($is_reg_jur) then ($product/price_opt and $product/price_opt != '0') else ($product/price and $product/price != '0')"/>        <xsl:variable name="price" select="if (f:num($product/price) != 0) then f:exchange(., 'price', 0) else 'по запросу'"/>        <xsl:variable name="sum" select="if (f:num($product/price) != 0) then f:exchange(., 'sum', 0) else ''"/>		<xsl:variable name="plain_section" select="$product/plain_section"/>		<xsl:variable name="plain" select="if ($product/section_name and not($product/section_name = '')) then $product/section_name else $product/plain_section/name"/>		<xsl:variable name="pc" select="$prcat[name = $plain]"/>		<xsl:variable name="next_delivery" select="if ($product/next_delivery and not($product/next_delivery = '')) then $product/next_delivery else $pc/default_ship_time"/>        <xsl:if test="position() &gt; 1">,</xsl:if>{"code":"<xsl:value-of select="$product/code"/>",		"name":"<xsl:value-of select="$product/name"/>",		"provider_name":"<xsl:value-of select="$pc/name"/>",		"provider_other_name":"<xsl:value-of select="$pc/other_name"/>",		"manufacturer":"<xsl:value-of select="$product/vendor"/>",		"vendor":"<xsl:value-of select="$product/category_id"/>",		"date_code":"<xsl:value-of select="$product/group_id"/>",		"leadtimedays":"<xsl:value-of select="$next_delivery"/>",		"qty_ordered":<xsl:value-of select="f:num(qty)"/>,		"quantity":<xsl:value-of select="f:num($product/qty)"/>,		"price":"<xsl:value-of select="$price"/>",		"sum":"<xsl:value-of select="$sum"/>"}	</xsl:for-each>	]}</xsl:template></xsl:stylesheet>