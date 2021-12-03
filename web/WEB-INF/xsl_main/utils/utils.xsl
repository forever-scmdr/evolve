<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">

    <xsl:decimal-format name="r" decimal-separator="." grouping-separator=" "/>

    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                             GENERAL                                  ////////////-->
    <!--/////////////                                                                      ////////////-->

    <xsl:function name="f:value_or_default" as="xs:string">
        <xsl:param name="value"/>
        <xsl:param name="default"/>
        <xsl:value-of select="if ($value and not(normalize-space($value) = '')) then $value else $default"/>
    </xsl:function>

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


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                        FORMATTING NUMBERS                            ////////////-->
    <!--/////////////                                                                      ////////////-->


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
        <xsl:value-of select="if (floor($num) = $num) then format-number($num, '#0.##') else format-number($num, '#0.00')"/>
    </xsl:function>

    <xsl:function name="f:format_currency_precise">
        <xsl:param name="num"/>
        <xsl:value-of select="format-number($num, '#0.0000')"/>
    </xsl:function>


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                         CURRENCY EXCHANGE                            ////////////-->
    <!--/////////////                                                                      ////////////-->


    <xsl:variable name="rates" select="page/catalog/currencies"/>
    <xsl:variable name="rates_on" select="page/optional_modules/display_settings/currency_rates = 'on'"/>
    <xsl:variable name="currency" select="f:value_or_default(page/variables/cur, 'BYN')"/>
    <xsl:variable name="BYN_cur_out" select="if ($rates/BYN_caption and not($rates/BYN_caption = '')) then $rates/BYN_caption else if ($rates and $rates_on) then 'бел.р.' else 'pуб.'"/>
    <xsl:variable name="curr_out" select="if ($currency = 'BYN') then normalize-space($BYN_cur_out) else $currency"/>
    <xsl:variable name="ceil" select="if ($rates) then f:num($rates/*[name() = concat($currency, '_ceil')]) &gt; 0 else true()"/>
    <xsl:variable name="format" select="if($ceil) then '### ###' else '### ##0.00'"/>


    <xsl:function name="f:format">
        <xsl:param name="str" as="xs:string?"/>
        <xsl:sequence select="f:format_n(f:num($str))" />
    </xsl:function>

    <xsl:function name="f:format_n">
        <xsl:param name="n" as="xs:double?"/>
        <xsl:sequence select="format-number($n, $format, 'r')" />
    </xsl:function>

    <xsl:function name="f:exchange_param">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:value-of select="if ($currency = 'BYN') then $item/*[name() = $param_name] else $item/*[name() = concat($param_name, '_', $currency)]"/>
    </xsl:function>


    <xsl:function name="f:exchange_param_cur">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:variable name="is_byn" select="$currency = 'BYN'"/>
        <xsl:variable name="sum" select="f:exchange_param($item, $param_name)"/>
        <xsl:value-of select="if ($is_byn) then concat($sum, ' ', $BYN_cur_out) else concat($sum, ' ', $currency)"/>
    </xsl:function>

    <xsl:function name="f:exchange">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:param name="default"/>
        <xsl:variable name="sum_check" select="$item/*[name() = $param_name]"/>
        <xsl:variable name="is_byn" select="$currency = 'BYN'"/>
        <xsl:variable name="sum" select="if ($sum_check) then f:num($sum_check) else $default"/>
        <xsl:choose>
            <xsl:when test="not(f:is_numeric($sum))"><xsl:value-of select="$default" /></xsl:when>
            <xsl:when test="$is_byn"><xsl:value-of select="f:format_currency($sum)" /></xsl:when>
            <xsl:when test="$rates/*[name() = concat($currency, '_rate')]">
                <xsl:variable name="rate" select="f:num($rates/*[name() = concat($currency, '_rate')])"/>
                <xsl:variable name="scale" select="f:num($rates/*[name() = concat($currency, '_scale')])"/>
                <xsl:variable name="cur_price" select="$sum div $rate * $scale"/>
                <xsl:value-of select="if ($currency = 'RUB') then f:format_currency(ceiling($cur_price)) else f:format_currency($cur_price)" />
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="$sum" /></xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="f:exchange_cur">
        <xsl:param name="item"/>
        <xsl:param name="param_name"/>
        <xsl:param name="default"/>
        <xsl:variable name="is_byn" select="$currency = 'BYN'"/>
        <xsl:variable name="sum" select="f:exchange($item, $param_name, $default)"/>
        <xsl:variable name="cur_caption" select="$rates/*[name() = concat($currency, '_caption')]"/>
        <xsl:variable name="cur_out" select="if ($cur_caption) then $cur_caption else if ($is_byn) then $BYN_cur_out else $currency"/>
        <xsl:choose>
            <xsl:when test="f:is_numeric($sum)"><xsl:value-of select="concat($sum, ' ', $cur_out)"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$sum" /></xsl:otherwise>
        </xsl:choose>
    </xsl:function>


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                         CURRENCY OUTPUT                              ////////////-->
    <!--/////////////                                                                      ////////////-->


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


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                           DATE AND TIME                              ////////////-->
    <!--/////////////                                                                      ////////////-->


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


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                            WORD ENDING                               ////////////-->
    <!--/////////////                                                                      ////////////-->


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


    <!--///////////////////////////////////////////////////////////////////////////////////////////////-->
    <!--/////////////                                                                      ////////////-->
    <!--/////////////                               OTHER                                  ////////////-->
    <!--/////////////                                                                      ////////////-->


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

    <xsl:function name="f:var_into_url" as="xs:string">
        <xsl:param name="url"/>
        <xsl:param name="name"/>
        <xsl:param name="value"/>
        <xsl:variable name="union" select="if (contains($url, '\?')) then '&amp;' else '?'"/>
        <xsl:value-of select="concat($url, $union, $name, '=', $value)"/>
    </xsl:function>


    <xsl:template name="check_radio">
        <xsl:param name="value"/>
        <xsl:param name="check"/>
        <xsl:param name="name"/>
        <xsl:choose>
            <xsl:when test="$value = $check">
                <input name="{$name}" type="radio" checked="checked" value="{$value}" />
            </xsl:when>
            <xsl:otherwise>
                <input name="{$name}" type="radio" value="{$value}" />
            </xsl:otherwise>
        </xsl:choose>
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

    <xsl:function name="f:translit">
        <xsl:param name="el"/>
        <xsl:variable name="ru" select="' абвгдеёжзийклмнопрстуфхцчшщъыьэюя%-'"/>
        <xsl:variable name="en" select="'_abvgdeëğziïklmnoprstufhxcśś_ũ_Əjǎ-_'"/>
        <xsl:sequence select="translate(lower-case(string($el)), $ru, $en)"/>
    </xsl:function>

</xsl:stylesheet>