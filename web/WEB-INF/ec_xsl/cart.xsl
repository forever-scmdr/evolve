<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />

	<xsl:variable name="price_catalogs" select="page/price_catalog"/>
	<xsl:variable name="price_intervals_default" select="$price_catalogs[name = 'default']/price_interval"/>
	<xsl:variable name="Q" select="f:num(page/price_catalog[name = 'default']/quotient)"/>


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
                <div>
                    <xsl:if test="$need_sum">x<xsl:value-of select="$number"/>&#160;=&#160;<xsl:value-of select="f:format_currency_precise($sum)"/></xsl:if>
                    <xsl:if test="not($need_sum)"><xsl:value-of select="f:format_currency_precise($unit_price)"/></xsl:if>
                </div>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>



	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Список товаров</h1>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="p_p" select="//page/product[code = $p/code]"/>
							<xsl:variable name="min_qty" select="if ($p_p/min_qty and f:num($p_p/min_qty) &gt; 0) then f:num($p_p/min_qty) else 1"/>
							<xsl:variable name="has_price" select="f:num(f:exchange($p, 'price')) &gt; 0.0001"/>
							<xsl:variable name="price" select="if (f:num(f:exchange($p, 'price')) != 0) then f:exchange_cur($p, 'price') else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num(f:exchange(current(), 'sum')) != 0) then f:exchange_cur(current(), 'sum') else 'по запросу'"/>
							<div class="item">
								<!--
								<xsl:if test="not($p/plain_section)">
									<a href="{$p/show_product}" class="image-container">
										<img src="{if($p/main_pic != '') then concat($p/@path, $p/main_pic) else 'img/no_image.png'}" alt="{$p/name}"/>
									</a>
									<a href="{$p/show_product}" class="title"><xsl:value-of select="$p/name"/></a>
								</xsl:if>
								<xsl:if test="$p/plain_section">
								-->
									<span class="image-container">
										<img src="{if($p/main_pic != '') then concat($p/@path, $p/main_pic) else 'img/no_image.png'}" alt="{$p/name}"/>
									</span>
									<span class="title"><xsl:value-of select="$p/name"/></span>
								<!--</xsl:if>-->
								<!--
								<div class="price one">
									<p>
										<span>Цена</span>
										<xsl:value-of select="$price"/>
									</p>
								</div>
								-->
								<xsl:if test="$has_price">
									<div class="quantity" style="font-size: 11px">
										<span>Цена</span>
										<xsl:call-template name="ALL_PRICES">
											<xsl:with-param name="section_name" select="$p_p/plain_section/name"/>
											<xsl:with-param name="min_qty" select="$min_qty"/>
											<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price'))"/>
                                            <xsl:with-param name="price_byn" select="f:num(price)"/>
											<xsl:with-param name="need_sum" select="false()"/>
										</xsl:call-template>
									</div>
									<div class="quantity" style="font-size: 11px">
										<span>Сумма</span>
										<xsl:call-template name="ALL_PRICES">
											<xsl:with-param name="section_name" select="$p_p/plain_section/name"/>
											<xsl:with-param name="min_qty" select="$min_qty"/>
											<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price'))"/>
                                            <xsl:with-param name="price_byn" select="f:num(price)"/>
											<xsl:with-param name="need_sum" select="true()"/>
										</xsl:call-template>
									</div>
								</xsl:if>
								<div class="quantity">
									<span>Мин. заказ</span>
									<xsl:value-of select="$min_qty"/>
								</div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{f:num(qty)}" name="{input/qty/@input}" min="0" step="{$min_qty}"/>
								</div>
								<div class="quantity">
									<span>Сумма позиции</span>
									<xsl:value-of select="$sum"/>
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<a href="{delete}" class="delete" title="Удалить"><i class="fas fa-trash-alt"/></a>
							</div>
						</xsl:for-each>
						<div class="total">
							<xsl:if test="f:exchange(page/cart, 'sum') != '0'">
								<p>Итого: <xsl:value-of select="f:exchange_cur(page/cart, 'sum')"/></p>
							</xsl:if>
							<input type="submit" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
						</div>

					</form>
				</xsl:when>
				<xsl:otherwise>
					<h3>Корзина пуста</h3>
				</xsl:otherwise>
			</xsl:choose>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>