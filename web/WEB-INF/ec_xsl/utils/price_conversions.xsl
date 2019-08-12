<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">

    <xsl:decimal-format name="r" decimal-separator="." grouping-separator=" "/>
    <xsl:variable name="currency" select="page/variables/cur"/>
    <xsl:variable name="currency_out" select="if ($currency = 'BYN') then 'руб.' else $currency"/>

    <xsl:function name="f:num" as="xs:double">
        <xsl:param name="str" as="xs:string?"/>
        <xsl:sequence
                select="if ($str and $str != '') then number(replace(replace($str, '[&#160;\s]', ''), ',', '.')) else number(0)"/>
    </xsl:function>

    <xsl:function name="f:is_numeric" as="xs:boolean">
        <xsl:param name="str"/>
        <xsl:sequence select="number($str) = $str"/>
    </xsl:function>

    <xsl:function name="f:currency_decimal">
       <xsl:param name="str" as="xs:string?"/>
        <xsl:value-of select="format-number(f:num($str), '#0.00')"/>
    </xsl:function>

    <xsl:function name="f:format_currency">
        <xsl:param name="num"/>
        <xsl:value-of select="format-number($num, '#0.00')"/>
    </xsl:function>

    <xsl:function name="f:format_currency_precise">
        <xsl:param name="num"/>
        <xsl:value-of select="format-number($num, '#0.0000')"/>
    </xsl:function>

    <xsl:function name="f:exchange">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:value-of select="if ($currency = 'BYN') then $item/*[name() = $param_name] else $item/*[name() = concat($param_name, '_', $currency)]"/>
    </xsl:function>


    <xsl:function name="f:exchange_cur">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:variable name="is_byn" select="$currency = 'BYN'"/>
        <xsl:variable name="sum" select="if ($is_byn) then $item/*[name() = $param_name] else $item/*[name() = concat($param_name, '_', $currency)]"/>
        <xsl:value-of select="if ($is_byn) then concat($sum, ' р.') else concat($sum, ' ', $currency)"/>
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

    <!-- Перевод даты из CMS вида (23.11.2017) в XSL вид -->
    <xsl:function name="f:xsl_date" as="xs:date">
        <xsl:param name="str_date"/>
        <xsl:variable name="parts" select="tokenize(tokenize($str_date, '\s+')[1], '\.')"/>
        <xsl:sequence select="if ($parts[3]) then xs:date(concat($parts[3], '-', $parts[2], '-', $parts[1])) else xs:date('1970-01-01')"/>
    </xsl:function>

    <!-- Перевод даты из XSL вида в CMS вид (23.11.2017) -->
    <xsl:function name="f:format_date">
        <xsl:param name="date" as="xs:date"/>
        <xsl:sequence select="format-date($date, '[D01].[M01].[Y0001]')"/>
    </xsl:function>

    <!-- Выбирает нужную форму слова в зависимости от заданного числа. Формы слова - массив -->
    <xsl:function name="f:ending" as="xs:string">
        <xsl:param name="number_str"  />
        <xsl:param name="words" />
        <xsl:variable name="number" select="round(number($number_str))"/>
        <xsl:variable name="mod100" select="$number mod 100" />
        <xsl:variable name="mod10" select="$number mod 10" />
        <xsl:value-of select="if ($mod100 &gt; 10 and $mod100 &lt; 20) then $words[3]
					     else if ($mod10 = 1) then $words[1]
					     else if ($mod10 &gt; 0 and $mod10 &lt; 5) then $words[2]
					     else $words[3]" />
    </xsl:function>

    <!-- Перевод даты из CMS формата в запись число-месяц вида 15 февраля -->
    <xsl:function name="f:day_month_string" as="xs:string">
        <xsl:param name="date" as="xs:string" />
        <xsl:variable name="parts" select="tokenize(tokenize($date, '\s+')[1], '\.')"/>
        <xsl:variable name="month" select="number($parts[2])"/>
        <xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
        <xsl:value-of select="concat(number($parts[1]), ' ', $months[$month], ' ', $parts[3])"/>
    </xsl:function>


    <!-- Перевод даты из XSL формата в запись число-месяц-год вида 15 февраля 2018 -->
    <xsl:function name="f:day_month_year" as="xs:string">
        <xsl:param name="date" as="xs:date" />
        <xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
        <xsl:value-of select="concat(day-from-date($date), ' ', $months[month-from-date($date)], ' ', year-from-date($date))"/>
    </xsl:function>

    <!--
    Для того чтобы выбрать нужный option, у селекта устанавливается атрибут value.
    После загрузки страницы это значение jquery устанавливает в селект
     -->
    <xsl:template name="SELECT_SCRIPT">
        $(document).ready(function() {
            $('select[value]').each(function() {
                var value = $(this).attr('value');
                if (value != '')
                $(this).val(value);
            });
        });
    </xsl:template>

    <!-- Вставка переменной в ссылку (добавление как query string). match соответствует ссылке -->
    <xsl:template match="*" mode="querystr_var">
        <xsl:param name="name"/>
        <xsl:param name="value"/>
        <xsl:if test="contains(., '?')">
            <xsl:value-of select="."/>&amp;<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
        </xsl:if>
        <xsl:if test="not(contains(., '?'))">
            <xsl:value-of select="."/>?<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
        </xsl:if>
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

</xsl:stylesheet>