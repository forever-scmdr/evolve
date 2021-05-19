<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">
    
    <!-- RUR RATIO -->
    <xsl:variable name="rur" select="page/currency[name = 'RUR']"/>
    <xsl:variable name="ratio_rur" select="f:num($rur/ratio)"/>
    <xsl:variable name="q_rur" select="f:num($rur/q)"/>

    <xsl:variable name="curr" select="page/variables/currency" />



    <!-- AUTOCONVERSIONS -->

    <!-- Catalog -->
    <xsl:function name="f:price_ictrade">
        <xsl:param name="price" as="xs:string?" />
        <xsl:param name="unit" as="xs:string?" />
        <xsl:param name="min_q" as="xs:string?"/>
        <xsl:variable name="q" select="if(f:num($min_q) &gt; 1) then $min_q else ''"/>
        <xsl:variable name="u" select="if($unit != '') then concat('/',$q, $unit) else ''"/>
        <xsl:sequence select="concat((if($curr = 'byn') then f:currency_decimal($price) else f:byn_to_rur($price)), ' ', upper-case($curr), $u)"/>
    </xsl:function>

    <xsl:function name="f:sum_s" as="xs:double">
        <xsl:param name="bought" />
        <xsl:variable name="price" select="$bought/sum" />
        <xsl:variable name="aux" select="$bought/aux" />
        <xsl:variable name="q" select="if(not($aux != '')) then 1 + $q1_rur else 1"/>
        <xsl:sequence select="(f:num($price) * 100) div $ratio_rur * $q" />
    </xsl:function>

    <xsl:function name="f:cart_sum">
        <xsl:param name="cart"  />
        <xsl:sequence select="if($curr = 'byn') then concat(f:currency_decimal($cart/sum), ' ', upper-case($curr))  else concat(f:number_decimal(sum($cart/bought/f:sum_s(.))),' ',upper-case($curr))"/>
    </xsl:function>



    <!-- TO RUR CONVERSIONS -->
    <xsl:function name="f:byn_to_rur">
       <xsl:param name="price" as="xs:string?" />
       <xsl:variable name="price_rur" select="(f:num($price) * f:num($rur/scale)) div $ratio_rur * (1 + $q1_rur)" />
       <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <xsl:function name="f:currency_to_byn" >
        <xsl:param name="price" />
        <xsl:param name="shop" as="xs:element"/>

        <xsl:variable name=""
    </xsl:function>

    <xsl:function name="f:rur_to_rur">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_rur" select="f:num($price) * (1 + $q1_rur) * (1 + $q2_rur)" />
        <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <xsl:function name="f:rur_to_rur_promelec">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_rur" select="f:num($price) * (1 + $q1_rur) * (1 + $qp)" />
        <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <xsl:function name="f:usd_to_rur">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_usd * (1 + $q1_usd) * (1 + $q2_usd)"/>
        <xsl:variable name="price_rur" select="($price_byn * 100) div $ratio_rur"/>
        <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <xsl:function name="f:usd_to_rur_arrow">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_usd * (1 + $q1_usd) * (1 + $q2_usd)"/>
        <xsl:variable name="price_rur" select="($price_byn * 100) div $ratio_rur"/>
        <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <xsl:function name="f:eur_to_rur">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_eur * (1 + $q1_eur) * (1 + $q2_eur)" />
        <xsl:variable name="price_rur" select="($price_byn * 100) div $ratio_rur"/>
        <xsl:sequence select="f:number_decimal($price_rur)"/>
    </xsl:function>

    <!-- TO BYN CONVERSIONS -->
    <xsl:function name="f:rur_to_byn">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="(f:num($price) div 100) * $ratio_rur * (1 + $q1_rur) * (1 + $q2_rur)" />
        <xsl:sequence select="f:number_decimal($price_byn)"/>
    </xsl:function>

    <xsl:function name="f:rur_to_byn_promelec">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="(f:num($price) div 100) * $ratio_rur * (1 + $q1_rur) * (1 + $qp)" />
        <xsl:sequence select="f:number_decimal($price_byn)"/>
    </xsl:function>

    <xsl:function name="f:usd_to_byn">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_usd * (1 + $q1_usd) * (1 + $q2_usd)" />
        <xsl:sequence select="f:number_decimal($price_byn)"/>
    </xsl:function>

    <xsl:function name="f:usd_to_byn_arrow">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_usd * (1 + $q1_usd) * (1 + $q2_arrow)" />
        <xsl:sequence select="f:number_decimal($price_byn)"/>
    </xsl:function>

    <xsl:function name="f:eur_to_byn">
        <xsl:param name="price" as="xs:string?" />
        <xsl:variable name="price_byn" select="f:num($price) * $ratio_eur * (1 + $q1_eur) * (1 + $q2_eur)" />
        <xsl:sequence select="f:number_decimal($price_byn)"/>
    </xsl:function>

    <xsl:decimal-format name="r" decimal-separator="." grouping-separator=" "/>

    <xsl:function name="f:num" as="xs:double">
        <xsl:param name="str" as="xs:string?"/>
        <xsl:sequence
                select="if ($str and $str != '') then number(replace(replace($str, '[&#160;\s]', ''), ',', '.')) else number(0)"/>
    </xsl:function>

    <xsl:function name="f:currency_decimal">
        <xsl:param name="str" as="xs:string?"/>
        <xsl:value-of select="format-number(f:num($str), '#0.00')"/>
    </xsl:function>

    <xsl:function name="f:number_decimal">
        <xsl:param name="num"/>
        <xsl:value-of select="format-number($num, '#0.00')"/>
    </xsl:function>

    <xsl:function name="f:rub_kop" as="xs:string">
        <xsl:param name="price" as="xs:string"/>
        <xsl:param name="rub" as="xs:string"/>
        <xsl:param name="kop" as="xs:string"/>
        <xsl:variable name="parts" select="tokenize($price, '[\.,]')"/>
        <xsl:variable name="rub_qty" select="$parts[1]"/>
        <xsl:variable name="kop_qty"
                      select="if (starts-with($parts[2], '0')) then substring($parts[2], 2) else $parts[2]"/>
        <xsl:variable name="rub_str"
                      select="if ($rub_qty != '' and $rub_qty != '0') then concat($rub_qty, $rub) else ''"/>
        <xsl:variable name="kop_str"
                      select="if ($kop_qty != '' and $kop_qty != '0') then concat($kop_qty, $kop) else ''"/>
        <xsl:value-of select="concat($rub_str, $kop_str)"/>
    </xsl:function>

    <xsl:function name="f:rub_kop" as="xs:string">
        <xsl:param name="price" as="xs:string"/>
        <xsl:value-of select="f:rub_kop($price, '&#160;руб.&#160;', '&#160;коп.')"/>
    </xsl:function>

    <xsl:template name="rub_kop_unit">
        <xsl:param name="price" as="xs:string"/>
        <xsl:param name="rub" as="xs:string" select="'&#x20;руб.&#x20;'"/>
        <xsl:param name="kop" as="xs:string" select="'&#x20;коп.'"/>
        <xsl:param name="unit" as="xs:string" select="''"/>
        <xsl:variable name="parts" select="tokenize($price, '[\.,]')"/>
        <xsl:variable name="rub_qty" select="$parts[1]"/>
        <xsl:variable name="kop_qty"
                      select="if (starts-with($parts[2], '0')) then substring($parts[2], 2) else $parts[2]"/>
        <xsl:variable name="has_rub" select="$rub_qty != '' and $rub_qty != '0'"/>
        <xsl:variable name="has_kop" select="$kop_qty != '' and $kop_qty != '0'"/>
        <xsl:variable name="un" select="if ($unit != '') then concat('/', $unit) else ''"/>
        <div class="denoPrice">
            <xsl:if test="$has_rub">
                <xsl:value-of select="$rub_qty"/>
                <span>
                    <xsl:value-of select="$rub"/>
                    <xsl:if test="not($has_kop)">
                        <xsl:value-of select="$un"/>
                    </xsl:if>
                </span>
            </xsl:if>
            <xsl:if test="$has_kop">
                <xsl:value-of select="$kop_qty"/>
                <span>
                    <xsl:value-of select="$kop"/><xsl:value-of select="$un"/>
                </span>
            </xsl:if>
        </div>
    </xsl:template>


    <xsl:function name="f:substring-before-last" as="xs:string">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="delim" as="xs:string"/>
        <xsl:sequence select="
           if (matches($arg, f:escape-for-regex($delim)))
           then replace($arg,
                    concat('^(.*)', f:escape-for-regex($delim),'.*'),
                    '$1')
           else ''
        "/>
    </xsl:function>

    <xsl:function name="f:escape-for-regex" as="xs:string">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:sequence select="
            replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
        "/>
   </xsl:function>


    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
	</xsl:text>
        <html lang="ru">
            <head>
                <!--<base href="https://ttd.by"/> -->
                <meta charset="utf-8"/>
            </head>
            <body>
                <p><b>f:num </b>
                <xsl:value-of select="f:num('10 000,1')"/>
                </p>
                <p><b>f:rub_kop </b>
                    <xsl:value-of select="f:rub_kop('10 000,1')"/>
                </p>
                <p><b>rub_kop_unit</b>
                    <xsl:call-template name="rub_kop_unit">
                       <xsl:with-param name="kop" select="' kop'"/>
                       <xsl:with-param name="rub" select="' rub. '"/>
                       <xsl:with-param name="price" select="'10 000,1'"/>
                       <xsl:with-param name="unit" select="'sht.'"/>
                   </xsl:call-template>
                </p>

                <p><b>f:currency_decimal </b>
                    <xsl:value-of select="f:currency_decimal('10 000,1')"/>
                </p>

            </body>
        </html>

    </xsl:template>

    <xsl:function name="f:map">
        <xsl:param name="arg" />
        <xsl:variable name="q" select="$arg/min_qty"/>
        <xsl:variable name="p" select="$arg/price"/>
        <xsl:for-each select="$q">
            <xsl:variable name="pos" select="position()"/>
            <xsl:value-of select="concat(., ':', $p[$pos], if($pos != last()) then ';' else '')"/>
        </xsl:for-each>
    </xsl:function>

</xsl:stylesheet>