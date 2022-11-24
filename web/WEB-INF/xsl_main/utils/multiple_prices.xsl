<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">
    <xsl:import href="utils.xsl"/>


    <xsl:variable name="price_catalogs" select="page/price_catalogs"/>
    <xsl:variable name="price_intervals_default" select="$price_catalogs/qty_quotient"/>
    <xsl:variable name="quotient_default" select="$price_catalogs/quotient"/>
    <xsl:variable name="is_quotient_for_pack" select="not($price_catalogs/qty_quotient_policy = 'количество')"/>


    <xsl:template name="ALL_PRICES">
        <xsl:param name="section_name"/>
        <xsl:param name="product"/>
        <xsl:param name="need_sum" select="false()"/>
        <xsl:param name="price_byn" select="$product/price"/>
        <xsl:variable name="intervals" select="$price_catalogs[name = $section_name]/qty_quotient"/>
        <xsl:variable name="price_intervals" select="if ($intervals) then $intervals else $price_intervals_default"/>
        <xsl:for-each select="$price_intervals">
            <xsl:variable name="pos" select="position()"/>
            <xsl:variable name="min" select="f:num(@key)"/>
            <xsl:variable name="max" select="if ($pos = last()) then 999999999 else (f:num($price_intervals[$pos + 1]/@key) - 1)"/>
            <xsl:variable name="quotient" select="f:num(@value)"/>
            <xsl:variable name="pack_quotient" select="if ($is_quotient_for_pack) then f:num($product/min_qty) else 1"/>
            <xsl:variable name="min_pack" select="$min * $pack_quotient"/>
            <xsl:variable name="max_pack" select="$max * $pack_quotient"/>


            <xsl:variable name="unit_price" select="$price_byn * $quotient"/>


            <xsl:variable name="pack_price_original" select="$price_byn * $min_qty"/>
            <xsl:variable name="unit_price" select="$price * $base_quotient * $quotient"/>
            <!--			<xsl:if test="$price_byn * $min_qty &lt; f:num(max)">-->
            <xsl:if test="$pack_price_original &lt; f:num(max)">
                <xsl:variable name="min_num_double" select="f:num(min) div $price_byn"/>
                <xsl:variable name="min_number" select="ceiling($min_num_double)"/>
                <xsl:variable name="number" select="if ($min_number &gt; 0) then ceiling($min_number div $min_qty) * $min_qty else $min_qty"/>
                <xsl:variable name="pack_number" select="if ($min_number &gt; 0) then ceiling($min_number div $min_qty) else 1"/>
                <xsl:if test="$pack_number &gt; 1 or $min_num_double &gt;= 0.5 * $pack_number">
                    <xsl:variable name="sum" select="$unit_price * $number"/>
                    <p>
                        <xsl:if test="$need_sum">x<xsl:value-of select="$number"/>&#160;=&#160;<xsl:value-of select="f:format_currency_precise($sum)"/></xsl:if>
                        <xsl:if test="not($need_sum)">
                            <xsl:value-of select="f:format_currency_precise($unit_price)"/>&#160;от&#160;<xsl:value-of select="$pack_number"/><xsl:if test="$min_qty &gt; 1">&#160;x&#160;упк(<xsl:value-of select="$min_qty" />)</xsl:if><xsl:if test="$min_qty = 1">&#160;шт.</xsl:if>
                        </xsl:if>
                    </p>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>