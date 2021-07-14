<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<!-- Перевод миллисекунд в XSL дату -->
	<xsl:function name="f:millis_to_date_time" as="xs:dateTime">
		<xsl:param name="millis"/>
		<xsl:sequence select="if ($millis) then xs:dateTime('1970-01-01T00:00:00Z') + number($millis) * xs:dayTimeDuration('PT0.001S') else xs:dateTime('1970-01-01T00:00:00Z')"/>
	</xsl:function>

	<!-- Миллисекунды в дату вида: "Fri, 23 Jan 2015 23:26:19 +0000" -->
	<xsl:function name="f:millis_to_rss" as="xs:string">
		<xsl:param name="millis"/>

		<xsl:if test="$millis">
			<xsl:variable name="datetime" select="f:millis_to_date_time($millis)"/>
			<xsl:variable name="d" select="substring(format-dateTime($datetime, '[F]'), 1,3)"/>
			<xsl:variable name="m" select="substring(format-dateTime($datetime, '[MNn]'), 1,3)"/>
			<xsl:variable name="format" select="concat($d, ', [D] ', $m,' [Y0001] [H01]:[m01]:[s01] +0000')"/>
			<xsl:sequence select="format-dateTime($datetime, $format)" />
		</xsl:if>
		<xsl:if test="not($millis)">
			<xsl:sequence select="''"/>
		</xsl:if>
	</xsl:function>

	<xsl:function name="f:date_time_to_millis">
		<xsl:param name="date" as="xs:dateTime"/>
		<xsl:sequence select="($date - xs:dateTime('1970-01-01T00:00:00Z')) div xs:dayTimeDuration('PT0.001S')"/>
	</xsl:function>

	<!-- Перевод даты из XSL вида в CMS вид (23.11.2017) -->
	<xsl:function name="f:format_date_time">
		<xsl:param name="date" as="xs:dateTime"/>
		<xsl:sequence select="format-dateTime($date, '[D01].[M01].[Y0001] [H01]:[m01]')"/>
	</xsl:function>

	<!-- UTC- Миллисекунды в бел. дату -->
	<xsl:function name="f:utc_millis_to_bel_date">
		<xsl:param name="millis"/>
		<xsl:variable name="m" select="f:num($millis) + (3*60*60*1000)"/>
		<xsl:sequence select="f:format_date_time(f:millis_to_date_time($m))"/>
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

	<!-- Перевод строки в число. Пуская строка переводится в 0 -->
	<xsl:function name="f:num">
		<xsl:param name="num_str"/>
		<xsl:value-of select="if (not($num_str) or $num_str = '') then 0 else number(translate(translate($num_str, '&#160;', ''), ',', '.'))"/>
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

	<!-- Перевод даты из CMS формата в запись число-месяц вида 15фев -->
	<xsl:function name="f:day_month_short_string" as="xs:string">
		<xsl:param name="date" as="xs:string" />
		<xsl:variable name="parts" select="tokenize(tokenize($date, '\s+')[1], '\.')"/>
		<xsl:variable name="month" select="number($parts[2])"/>
		<xsl:variable name="months" select="('янв', 'фев', 'мар', 'апр', 'мая', 'июн', 'июл', 'авг', 'сен', 'окт', 'ноя', 'дек')"/>
		<xsl:value-of select="concat(number($parts[1]), $months[$month])"/>
	</xsl:function>

	<!-- Перевод даты из XSL формата в запись число-месяц-год вида 15 февраля 2018 -->
	<xsl:function name="f:day_month_year" as="xs:string">
		<xsl:param name="date" as="xs:date" />
		<xsl:variable name="months" select="('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря')"/>
		<xsl:value-of select="concat(day-from-date($date), ' ', $months[month-from-date($date)], ' ', year-from-date($date))"/>
	</xsl:function>

	<xsl:function name="f:month_of_year" as="xs:string">
		<xsl:param name="date" as="xs:date" />
		<xsl:variable name="months" select="('январь', 'февраль', 'март', 'апрель', 'май', 'июнь', 'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь')"/>
		<xsl:value-of select="concat($months[month-from-date($date)], ' ', year-from-date($date))"/>
	</xsl:function>

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

	<xsl:function name="f:substring-after-last" as="xs:string">
		<xsl:param name="value" as="xs:string?"/>
		<xsl:param name="separator" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="contains($value, $separator)">
				<xsl:value-of select="f:substring-after-last(substring-after($value, $separator), $separator)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>


</xsl:stylesheet>