<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">
    <xsl:import href="utils.xsl"/>
    <xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>


    <xsl:variable name="price_catalogs" select="page/price_catalogs"/>
    <xsl:variable name="price_intervals_default" select="$price_catalogs/qty_quotient"/>
    <xsl:variable name="quotient_default" select="$price_catalogs/quotient"/>



    <xsl:function name="f:print_cur">
        <xsl:param name="sum"/>
        <xsl:variable name="is_byn" select="$currency = 'BYN'"/>
        <xsl:choose>
            <xsl:when test="f:is_numeric($sum)"><xsl:value-of select="if ($is_byn) then concat(f:format_currency_precise($sum), $BYN_cur) else concat(f:format_currency_precise($sum), f:cur())"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="f:format_currency_precise($sum)" /></xsl:otherwise>
        </xsl:choose>
    </xsl:function>


    <xsl:template name="ALL_PRICES">
        <xsl:param name="section_name"/>
        <xsl:param name="product"/>
        <xsl:param name="need_sum" select="false()"/>
        <xsl:param name="price_in_currency"/>
        <xsl:variable name="base_price" select="f:num($price_in_currency)"/>
        <xsl:variable name="catalog" select="$price_catalogs/price_catalog[name = $section_name]"/>
        <xsl:variable name="intervals" select="$catalog/qty_quotient"/>
        <xsl:variable name="price_intervals" select="if ($intervals) then $intervals else $price_intervals_default"/>
        <xsl:variable name="is_quotient_for_pack" select="not($catalog/qty_quotient_policy = 'количество')"/>
        <xsl:variable name="min_qty" select="if (f:num($product/min_qty) = 0) then f:num('1') else f:num($product/min_qty)"/>
        <xsl:variable name="pack_quotient" select="if ($is_quotient_for_pack) then $min_qty else f:num('1')"/>
        <xsl:for-each select="$price_intervals">
            <xsl:variable name="pos" select="position()"/>
            <xsl:variable name="min_interval_qty" select="f:num(@key)"/>
            <xsl:variable name="max_interval_qty" select="if ($pos = last()) then 999999999 else (f:num($price_intervals[$pos + 1]/@key) - 1)"/>
            <xsl:variable name="price_quotient" select="f:num(@value)"/>
            <xsl:variable name="min_pack" select="$min_interval_qty * $pack_quotient"/>
            <xsl:variable name="max_pack" select="$max_interval_qty * $pack_quotient"/>
            <xsl:variable name="unit_price" select="$base_price * $price_quotient"/>
            <xsl:variable name="pack_price" select="$base_price * $price_quotient * $pack_quotient"/>
            <xsl:variable name="pack_sum" select="$unit_price * $min_pack"/>
<!--           <p><b>-->
<!--                !<xsl:value-of select="$catalog/qty_quotient_policy"/>|-->
<!--           </b>-->
<!--           </p>-->
            <p>
                <xsl:if test="$need_sum">x<xsl:value-of select="$min_pack"/>&#160;=&#160;<xsl:value-of select="f:print_cur($pack_sum)"/></xsl:if>
                <xsl:if test="not($need_sum)">
                    <xsl:value-of select="f:print_cur($unit_price)"/>&#160;от&#160;<xsl:value-of select="$min_pack"/>&#160;шт.<xsl:if test="$pack_quotient != 1">&#160;-&#160;упк(<xsl:value-of select="$pack_quotient" />)</xsl:if>
                </xsl:if>
            </p>
        </xsl:for-each>
        <xsl:if test="not($price_intervals)">
            <xsl:variable name="q" select="f:num($catalog/quotient)"/>
            <xsl:variable name="unit_price" select="$base_price * $q"/>
            <xsl:value-of select="f:print_cur($unit_price)"/><xsl:if test="$pack_quotient != 1">&#160;-&#160;упк(<xsl:value-of select="$pack_quotient" />)</xsl:if>
        </xsl:if>
    </xsl:template>



    <xsl:template name="ALL_PRICES_API">
        <xsl:param name="product"/>
        <xsl:param name="need_sum" select="false()"/>
        <xsl:param name="need_original" select="false()"/>
        <xsl:variable name="price_intervals" select="$product/prices/break"/>
        <xsl:for-each select="$price_intervals">
            <xsl:variable name="pos" select="position()"/>
            <xsl:variable name="min_interval_qty" select="f:num(@qty)"/>
            <xsl:variable name="max_interval_qty" select="if ($pos = last()) then 999999999 else (f:num($price_intervals[$pos + 1]/@qty) - 1)"/>
            <xsl:variable name="min_pack" select="$min_interval_qty"/>
            <xsl:variable name="max_pack" select="$max_interval_qty"/>
            <xsl:variable name="unit_price" select="f:exchange(current(), 'price', 0)"/>
            <xsl:variable name="pack_sum" select="$unit_price * $min_pack"/>
            <!--           <p><b>-->
            <!--                !<xsl:value-of select="$catalog/qty_quotient_policy"/>|-->
            <!--           </b>-->
            <!--           </p>-->
            <p>
                <xsl:if test="$need_sum">x<xsl:value-of select="$min_pack"/>&#160;=&#160;<xsl:value-of select="f:print_cur($pack_sum)"/></xsl:if>
                <xsl:if test="not($need_sum)">
                    <xsl:variable name="out" select="if ($need_original) then concat(provider_price, '&#160;', $product/currency_id) else f:print_cur($unit_price)"/>
                    <xsl:value-of select="$out"/>&#160;от&#160;<xsl:value-of select="$min_pack"/>&#160;шт.
                </xsl:if>
            </p>
        </xsl:for-each>
        <xsl:if test="not($price_intervals)">
            <xsl:variable name="unit_price" select="f:exchange($product/price, 'price', 0)"/>
            <xsl:value-of select="f:print_cur($unit_price)"/>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>