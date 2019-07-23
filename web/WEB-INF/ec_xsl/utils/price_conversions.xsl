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

     <xsl:function name="f:currency">
        <xsl:param name="str" as="xs:double?"/>
        <xsl:value-of select="format-number($str, '#0.00')"/>
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
    
    <xsl:function name="f:day-of-week" as="xs:integer?">
        <xsl:param name="date" as="xs:dateTime?"/>
        <xsl:sequence select="
          if (empty($date))
          then ()
          else xs:integer((xs:date($date) - xs:date('1901-01-06'))
                  div xs:dayTimeDuration('P1D')) mod 7
         "/>
    </xsl:function>

    <xsl:function name="f:delivery-date">
        <xsl:param name="date" as="xs:dateTime?"/>
        <xsl:variable name="day" select="f:day-of-week($date)"/>
        <xsl:variable name="duration" select="24*60*60*1000"/>
        <xsl:variable name="plus" select="if($day = 6) then 11 else if($day = 0) then 10 else 12" />
        <xsl:variable name="delta" select="if($day = 0 or $day = 6) then $plus else if ($day &lt; 5) then (5-$day + 5) else if(hours-from-dateTime($date) &lt; 18) then (5-$day + 5) else $plus"/>
        <xsl:variable name="millis" select="f:date_to_millis(xs:date($date)) + $duration * $delta"/>
        <xsl:sequence select="f:format_date(f:millis_to_date($millis))"/>
    </xsl:function>

    <!-- Перевод XSL даты в миллисекунды -->
    <xsl:function name="f:date_to_millis">
        <xsl:param name="date" as="xs:date"/>
        <xsl:sequence select="($date - xs:date('1970-01-01')) div xs:dayTimeDuration('PT0.001S')"/>
    </xsl:function>

    <!-- Перевод миллисекунд в XSL дату -->
    <xsl:function name="f:millis_to_date" as="xs:date">
        <xsl:param name="millis"/>
        <xsl:sequence select="if ($millis) then xs:date('1970-01-01') + $millis * xs:dayTimeDuration('PT0.001S') else xs:date('1970-01-01')"/>
    </xsl:function>

    <!-- Перевод даты из XSL вида в CMS вид (23.11.2017) -->
    <xsl:function name="f:format_date">
        <xsl:param name="date" as="xs:date"/>
        <xsl:sequence select="format-date($date, '[D01].[M01].[Y0001]')"/>
    </xsl:function>

    <!-- Перевод даты из CMS вида (23.11.2017) в XSL вид -->
    <xsl:function name="f:xsl_date" as="xs:date">
        <xsl:param name="str_date"/>
        <xsl:variable name="parts" select="tokenize(tokenize($str_date, '\s+')[1], '\.')"/>
        <xsl:sequence select="if ($parts[3]) then xs:date(concat($parts[3], '-', $parts[2], '-', $parts[1])) else xs:date('1970-01-01')"/>
    </xsl:function>

</xsl:stylesheet>