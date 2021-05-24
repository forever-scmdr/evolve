<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">

	<!-- RUR RATIO -->
	<xsl:variable name="rub" select="/page/currency[name = 'RUB']"/>
	<xsl:variable name="ratio_rur" select="f:num($rub/ratio)"/>
	<xsl:variable name="q_rur" select="1 + f:num($rub/q)"/>
	<xsl:variable name="nothing"/>

	<xsl:variable name="curr" select="page/variables/currency" />


	<!-- Ictrade -->
	<xsl:function name="f:price_ictrade">
		<xsl:param name="price" as="xs:string?" />
		<xsl:sequence select="(if($curr = 'byn') then f:currency_decimal($price) else f:number_decimal(f:byn_to_currency(f:num($price) * $q_rur, $rub)))"/>
	</xsl:function>

	<xsl:function name="f:sum_s" as="xs:double">
		<xsl:param name="bought" />
		<xsl:variable name="price" select="$bought/sum" />
		<xsl:variable name="aux" select="$bought/aux" />
		<xsl:variable name="q" select="if(not($aux != '')) then 1 + $q_rur else 1"/>
		<xsl:sequence select="(f:num($price) * 100) div $ratio_rur * $q" />
	</xsl:function>

	<xsl:function name="f:cart_sum">
		<xsl:param name="cart"  />
		<xsl:sequence select="if($curr = 'byn') then concat(f:currency_decimal($cart/sum), ' ', upper-case($curr))  else concat(f:number_decimal(sum($cart/bought/f:sum_s(.))),' ',upper-case($curr))"/>
	</xsl:function>

	<xsl:function name="f:byn_to_currency">
		<xsl:param name="price" />
		<xsl:param name="currency" />
		<xsl:sequence select="(f:num(string($price)) div f:num($currency/ratio)) * f:num($currency/scale)"/>
	</xsl:function>

	<xsl:function name="f:currency_to_byn">
		<xsl:param name="price" />
		<xsl:param name="currency" />

		<xsl:variable name="price_num" select="f:num(string($price))"/>
		<xsl:sequence select="($price_num div f:num($currency/scale)) * f:num($currency/ratio)"/>
	</xsl:function>

	<xsl:function name="f:apply_quotients" >
		<xsl:param name="price" />
		<xsl:param name="shop"/>

		<xsl:variable name="currency_q" select="f:num($shop/currency/q)"/>
		<xsl:variable name="shop_q" select="f:num($shop/q)"/>

		<xsl:sequence select="f:num(string($price)) * (1 + $currency_q) * (1 + $shop_q)"/>
	</xsl:function>

	<xsl:function name="f:price_output">
		<xsl:param name="price" />
		<xsl:param name="shop"/>

		<xsl:if test="not($shop)">
			<xsl:sequence select="f:price_ictrade($price)"/>
		</xsl:if>
		<xsl:if test="$shop">
			<xsl:variable name="byn_price" select="f:apply_quotients(f:currency_to_byn($price, $shop/currency), $shop)"/>
			<xsl:sequence select="f:number_decimal(if($curr = 'byn') then $byn_price else f:byn_to_currency($byn_price, $rub))"/>
		</xsl:if>
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
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;</xsl:text>
		<html lang="ru">
			<head>
				<!--<base href="https://ttd.by"/> -->
				<meta charset="utf-8"/>
			</head>
			<body>
				<!-- write tests here -->
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