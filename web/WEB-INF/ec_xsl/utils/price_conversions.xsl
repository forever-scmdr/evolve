<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">

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

</xsl:stylesheet>