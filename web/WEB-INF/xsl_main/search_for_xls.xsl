<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="queries" select="page/variables/q"/>
	<xsl:variable name="numbers" select="page/variables/n"/>
	<xsl:variable name="multiple" select="count($queries) &gt; 1"/>

	<xsl:variable name="price_catalogs" select="page/price_catalog"/>
	<xsl:variable name="price_intervals_default" select="$price_catalogs[name = 'default']/price_interval"/>
	<xsl:variable name="Q" select="f:num(page/price_catalog[name = 'default']/quotient)"/>






	<xsl:template match="/">
		<result>
			<xsl:if test="$products">
				<xsl:if test="$multiple">
					<xsl:for-each select="$queries">
						<xsl:variable name="q" select="."/>
						<xsl:variable name="nn" select="$numbers[starts-with(., concat($q, ':'))][1]"/>
						<xsl:variable name="n" select="f:num(tokenize($nn, ':')[last()])"/>
						<xsl:variable name="p" select="position()"/>
						<xsl:variable name="price_query_products" select="$products[item_own_extras/query = $q and plain_section]"/>
						<xsl:variable name="no_price_query_products" select="$products[item_own_extras/query = $q and not(plain_section)]"/>
						<xsl:apply-templates select="$price_query_products[1]">
							<xsl:with-param name="number" select="$n"/>
							<xsl:with-param name="position" select="$p"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="$price_query_products[position() &gt; 1]">
							<xsl:with-param name="hidden" select="'hidden'"/>
							<xsl:with-param name="number" select="$n"/>
							<xsl:with-param name="position" select="$p"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="$no_price_query_products[1]">
							<xsl:with-param name="number" select="$n"/>
							<xsl:with-param name="position" select="$p"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="$no_price_query_products[position() &gt; 1]">
							<xsl:with-param name="hidden" select="'hidden'"/>
							<xsl:with-param name="number" select="$n"/>
							<xsl:with-param name="position" select="$p"/>
						</xsl:apply-templates>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="not($multiple)">
					<xsl:for-each select="$products[plain_section]">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
					<xsl:for-each select="$products[not(plain_section)]">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</result>
	</xsl:template>



	<xsl:template name="ALL_PRICES">
		<xsl:param name="section_name"/>
		<xsl:param name="price"/>
		<xsl:param name="min_qty"/>
		<xsl:param name="need_sum"/>
        <xsl:param name="price_byn" select="$price"/>
		<xsl:variable name="intervals" select="$price_catalogs[name = $section_name]/price_interval"/>
		<xsl:variable name="price_intervals" select="if ($intervals) then $intervals else $price_intervals_default"/>
		<xsl:variable name="quot" select="f:num($price_catalogs[name = $section_name]/quotient)"/>
		<xsl:variable name="base_quotient" select="if ($quot  &gt; 0) then $quot else $Q"/>
		<xsl:for-each select="$price_intervals">
			<xsl:variable name="quotient" select="f:num(quotient)"/>
			<xsl:variable name="unit_price" select="$price * $base_quotient * $quotient"/>
            <xsl:if test="$price_byn * $min_qty &lt; f:num(max)">
				<xsl:variable name="min_number" select="ceiling(f:num(min) div $price_byn)"/>
				<xsl:variable name="number" select="if ($min_number &gt; 0) then ceiling($min_number div $min_qty) * $min_qty else $min_qty"/>
				<xsl:variable name="sum" select="$unit_price * $number"/>
				<num_price>
					<xsl:if test="$need_sum">x<xsl:value-of select="$number"/>&#160;=&#160;<xsl:value-of select="f:format_currency_precise($sum)"/></xsl:if>
					<xsl:if test="not($need_sum)"><xsl:value-of select="f:format_currency_precise($unit_price)"/></xsl:if>
				</num_price>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>




	<xsl:template match="product">
		<xsl:param name="hidden"/>
		<xsl:param name="number"/>
		<xsl:param name="position"/>
		<xsl:variable name="unit" select="if (unit) then unit else 'шт.'"/>
		<xsl:variable name="min_qty" select="if (min_qty and f:num(min_qty) &gt; 0) then f:num(min_qty) else 1"/>
		<xsl:variable name="num" select="if ($number and $number &gt;= $min_qty) then $number else $min_qty"/>
		<xsl:variable name="has_price" select="price and f:num(price) &gt; 0.001"/>
		<product>
			<xsl:if test="$multiple">
				<query><xsl:value-of select="item_own_extras/query" /></query>
			</xsl:if>
			<name type="{if (vendor_code) then 'catalog' else 'list'}"><xsl:value-of select="name" /></name>
			<name_extra><xsl:value-of select="name_extra" /></name_extra>
			<vendor><xsl:value-of select="vendor" /></vendor>
			<!--<td><a><xsl:value-of select="code"/></a></td>-->
			<qty><xsl:value-of select="qty"/></qty>
			<available>
				<xsl:if test="not(vendor_code)">
					<xsl:if test="available and not(available = '0') and f:num(available) &gt; 0"><xsl:value-of select="f:num(available) * 7"/> дней</xsl:if>
					<xsl:if test="not(available) or available = '0'">склад</xsl:if>
					<xsl:if test="available and f:num(available) &lt; 0">по запросу</xsl:if>
				</xsl:if>
			</available>
			<unit><xsl:value-of select="$unit"/></unit>
			<min_qty><xsl:value-of select="$min_qty"/></min_qty>
			<xsl:if test="$has_price">
				<unit_price>
					<xsl:call-template name="ALL_PRICES">
						<xsl:with-param name="section_name" select="plain_section/name"/>
						<xsl:with-param name="min_qty" select="$min_qty"/>
						<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price', 0))"/>
                        <xsl:with-param name="price_byn" select="f:num(price)"/>
						<xsl:with-param name="need_sum" select="false()"/>
					</xsl:call-template>
				</unit_price>
				<total_price>
					<xsl:call-template name="ALL_PRICES">
						<xsl:with-param name="section_name" select="plain_section/name"/>
						<xsl:with-param name="min_qty" select="$min_qty"/>
						<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price', 0))"/>
                        <xsl:with-param name="price_byn" select="f:num(price)"/>
						<xsl:with-param name="need_sum" select="true()"/>
					</xsl:call-template>
				</total_price>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<total_price>запрос цены</total_price>
			</xsl:if>
			<price_original><xsl:value-of select="f:exchange(current(), 'price', 0)"/></price_original>
			<request_qty><xsl:value-of select="$num" /></request_qty>
			<plain_section>
				<name><xsl:value-of select="plain_section/name"/></name>
				<date><xsl:value-of select="plain_section/date"/></date>
			</plain_section>
		</product>
	</xsl:template>


</xsl:stylesheet>