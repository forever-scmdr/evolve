<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<xsl:variable name="queries" select="page/variables/q"/>
	<xsl:variable name="numbers" select="page/variables/n"/>
	<xsl:variable name="multiple" select="count($queries) &gt; 1"/>

	<xsl:variable name="price_catalogs" select="page/price_catalog"/>
	<xsl:variable name="price_intervals_default" select="$price_catalogs[name = 'default']/price_interval"/>
	<xsl:variable name="Q" select="f:num(page/price_catalog[name = 'default']/quotient)"/>
	<xsl:variable name="l" select="concat('digikey_search?query=', page/variables/q)"/>


	<xsl:template name="MAIN_CONTENT">
		<!-- MAIN COLOUMNS BEGIN -->
		<div class="container">
			<div class="row">
				<div class="col-xs-12 main-content">
					<div class="mc-container">
						<xsl:call-template name="INC_MOBILE_HEADER"/>
						<xsl:call-template name="CONTENT"/>
						<xsl:if test="$seo/text != '' and page/@name != 'section' and page/@name != 'sub'">
							<div class="page-content">
								<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
							</div>
						</xsl:if>
					</div>
				</div>
			</div>
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>





	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<a style="position: absolute; right: 0px; font-weight: bold;" href="{page/save_excel_file}"><img src="img/excel2.png"/> Сохранить результаты</a>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<xsl:if test="not($multiple)">
			<h1>Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</h1>
		</xsl:if>
		<xsl:if test="$multiple">
			<h1>Результаты поиска</h1>
		</xsl:if>

		<div class="page-content m-t">
			<xsl:if test="$products">
				<div>
					<!--<h3>Таблица с результатами</h3>-->
					<table class="srtable">
						<tr>
							<xsl:if test="$multiple">
								<th>Запрос</th>
							</xsl:if>
							<th>Название</th>
							<th>Описание</th>
							<th>Производитель</th>
							<!--<th>Код производителя</th>-->
							<th>Количество</th>
							<th>Срок поставки</th>
							<th>Единица</th>
							<th>Мин. заказ</th>
							<th>Цена (<xsl:value-of select="$currency_out" />)</th>
							<th>Сумма (<xsl:value-of select="$currency_out" />)</th>
							<th>Заказать</th>
							<xsl:if test="$multiple">
								<th></th>
							</xsl:if>
						</tr>
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
							<tbody id="extra-search-ajax">
								<tr>
									<td colspan="10">
										Идет поиск по дополнительному каталогу
									</td>
								</tr>
							</tbody>
						</xsl:if>
					</table>
				</div>
			</xsl:if>
			<xsl:if test="not($products)">
				<h4>По заданным критериям товары не найдены</h4>
				<div>
					<xsl:if test="not($multiple)">
						<table class="srtable">
							<tr>
								<th>Название</th>
								<th>Описание</th>
								<th>Производитель</th>
								<!--<th>Код производителя</th>-->
								<th>Количество</th>
								<th>Срок поставки</th>
								<th>Единица</th>
								<th>Мин. заказ</th>
								<th>Цена (<xsl:value-of select="$currency_out" />)</th>
								<th>Сумма (<xsl:value-of select="$currency_out" />)</th>
								<th>Заказать</th>
							</tr>
							<xsl:call-template name="MOCK" />
<!--							<tbody id="extra-search-ajax">-->
<!--								<tr>-->
<!--									<td colspan="10">-->
<!--										Идет поиск по дополнительному каталогу-->
<!--									</td>-->
<!--								</tr>-->
<!--							</tbody>-->
						</table>
					</xsl:if>
				</div>
			</xsl:if>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>

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
				<p>
					<!--|<xsl:value-of select="$Q"/> * <xsl:value-of select="$quotient"/> * <xsl:value-of select="$price"/>|-->
					<!--|<xsl:value-of select="$min_number"/> div <xsl:value-of select="$min_qty"/> * <xsl:value-of select="$min_qty"/>|-->
					<xsl:if test="$need_sum">x<xsl:value-of select="$number"/>&#160;=&#160;<xsl:value-of select="f:format_currency_precise($sum)"/></xsl:if>
					<xsl:if test="not($need_sum)"><xsl:value-of select="f:format_currency_precise($unit_price)"/></xsl:if>
				</p>
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
		<tr style="{'display: none'[$hidden]}" class="{if ($hidden) then concat('p_', $position) else 'parent'}">
			<xsl:if test="$multiple">
				<td><b><xsl:value-of select="item_own_extras/query" /></b></td>
			</xsl:if>
			<td>
				<xsl:choose>
					<xsl:when test="vendor_code">
						<a href="{show_product}"><xsl:value-of select="name" /></a>
					</xsl:when>
					<xsl:otherwise>
						<b><xsl:value-of select="name" /></b>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td><xsl:value-of select="name_extra" /></td>
			<td><xsl:value-of select="vendor" /></td>
			<!--<td><a><xsl:value-of select="code"/></a></td>-->
			<td><xsl:value-of select="qty"/></td>
			<td>
				<xsl:if test="not(vendor_code)">
					<xsl:if test="available and not(available = '0') and f:num(available) &gt; 0"><xsl:value-of select="f:num(available) * 7"/> дней</xsl:if>
					<xsl:if test="not(available) or available = '0'">склад</xsl:if>
					<xsl:if test="available and f:num(available) &lt; 0">по запросу</xsl:if>
				</xsl:if>
			</td>
			<td><xsl:value-of select="$unit"/></td>
			<td><xsl:value-of select="$min_qty"/></td>
			<xsl:if test="$has_price">
				<td>
					<xsl:call-template name="ALL_PRICES">
						<xsl:with-param name="section_name" select="plain_section/name"/>
						<xsl:with-param name="min_qty" select="$min_qty"/>
						<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price'))"/>
                        <xsl:with-param name="price_byn" select="f:num(price)"/>
						<xsl:with-param name="need_sum" select="false()"/>
					</xsl:call-template>
				</td>
				<td>
					<xsl:call-template name="ALL_PRICES">
						<xsl:with-param name="section_name" select="plain_section/name"/>
						<xsl:with-param name="min_qty" select="$min_qty"/>
						<xsl:with-param name="price" select="f:num(f:exchange(current(), 'price'))"/>
                        <xsl:with-param name="price_byn" select="f:num(price)"/>
						<xsl:with-param name="need_sum" select="true()"/>
					</xsl:call-template>
				</td>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<td colspan="2"><p>запрос цены</p></td>
			</xsl:if>
			<td id="cart_search_{@id}">
				<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_search_{@id}">
					<xsl:if test="$has_price">
						<input type="number" name="qty" value="{$num}" min="0" step="{$min_qty}"/>
						<input type="submit" value="Заказать"/>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<input type="number" name="qty" value="{$num}" min="0" step="{$min_qty}"/>
						<input type="submit" value="Запросить цену"/>
					</xsl:if>
				</form>
			</td>
			<xsl:if test="$multiple">
				<td class="toggle-plus">
					<xsl:if test="not($hidden)">
						<a href="#" onclick="$('.p_{$position}').toggle(); return false;">Показать другие</a><!-- <i class="fas fa-plus-square">Показать другие</i> -->
					</xsl:if>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>




	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script>
			$(document).ready(function() {
			//	insertAjax('<xsl:value-of select="$l"/>');
			});
		</script>
	</xsl:template>

	<xsl:template name="MOCK">
		<table xmlns="http://www.w3.org/1999/xhtml" xmlns:f="f:f">
			<tbody id="extra-search-ajax" class="result">
				<tr>
					<td><b>BATT CONTACT CLIP AA SOLDER LUG</b><p></p>
						<ul class="parameters">
							<li><b>Part Status:&#160;
							</b>Active
							</li>
							<li><b>Battery Type, Function:&#160;
							</b>Cylindrical, Clip
							</li>
							<li><b>Style:&#160;
							</b>Contact Clip
							</li>
							<li><b>Battery Cell Size:&#160;
							</b>AA
							</li>
							<li><b>Number of Cells:&#160;
							</b>1
							</li>
							<li><b>Battery Series:&#160;
							</b>-
							</li>
							<li><b>Mounting Type:&#160;
							</b>Chassis Mount
							</li>
							<li><b>Termination Style:&#160;
							</b>Solder Lug
							</li>
							<li><b>Height Above Board:&#160;
							</b>-
							</li>
							<li><b>Operating Temperature:&#160;
							</b>-
							</li>
						</ul>
					</td>
					<td></td>
					<td>Keystone Electronics</td>
					<td>2078</td>
					<td>14 дней</td>
					<td>шт.</td>
					<td>1</td>
					<td>
						<p>1.0800 USD</p>
						<p>0.9096 USD</p>
						<p>0.7912 USD</p>
						<p>0.7388 USD</p>
						<p>0.6514 USD</p>
					</td>
					<td>
						<p>x1 = 1.0800 USD</p>
						<p>x25 = 22.7400 USD</p>
						<p>x100 = 79.1200 USD</p>
						<p>x250 = 184.7000 USD</p>
						<p>x500 = 325.7000 USD</p>
					</td>
					<td id="cart_search_36-100-ND">
						<form action="cart_action/?action=addDigiKeyToCart&amp;code=36-100-ND" method="post" ajax="true" ajax-loader-id="cart_search_36-100-ND-dgk"><input type="number" name="qty" value="1" min="1" step="1"></input><input type="hidden" name="img" value="https://media.digikey.com/Photos/Keystone%20Elect%20Photos/MFG_100.jpg"></input><input type="hidden" name="map" value="1:1.08;25:0.9096;100:0.7912;250:0.7388;500:0.6514;"></input><input type="hidden" value="BATT CONTACT CLIP AA SOLDER LUG" name="name"></input><input type="hidden" value="2078" name="max"></input><input type="hidden" value="Keystone Electronics" name="vendor"></input><input type="hidden" value="100" name="vendor_code"></input><input type="hidden" value="Keystone Electronics" name="vendor"></input><input type="submit" value="Заказать"></input></form>
					</td>
				</tr>
				<tr>
					<td><b>100 1/4" HIGH SPEED CUTTER,1/8"</b><p></p>
						<ul class="parameters">
							<li><b>Part Status:&#160;
							</b>Active
							</li>
							<li><b>Accessory Type:&#160;
							</b>Cutter
							</li>
							<li><b>For Use With/Related Products:&#160;
							</b>-
							</li>
						</ul>
					</td>
					<td></td>
					<td>Dremel</td>
					<td>1</td>
					<td>14 дней</td>
					<td>шт.</td>
					<td>1</td>
					<td>
						<p>5.9900 USD</p>
					</td>
					<td>
						<p>x1 = 5.9900 USD</p>
					</td>
					<td id="cart_search_100DR-ND">
						<form action="cart_action/?action=addDigiKeyToCart&amp;code=100DR-ND" method="post" ajax="true" ajax-loader-id="cart_search_100DR-ND-dgk"><input type="number" name="qty" value="1" min="1" step="1"></input><input type="hidden" name="img" value="https://media.digikey.com/Photos/Dremel/MFG_100.jpg"></input><input type="hidden" name="map" value="1:5.99;"></input><input type="hidden" value="100 1/4&#34; HIGH SPEED CUTTER,1/8&#34;" name="name"></input><input type="hidden" value="1" name="max"></input><input type="hidden" value="Dremel" name="vendor"></input><input type="hidden" value="100" name="vendor_code"></input><input type="hidden" value="Dremel" name="vendor"></input><input type="submit" value="Заказать"></input></form>
					</td>
				</tr>
				<tr>
					<td><b>CAP CER 10PF 16V C0G/NP0 01005</b><p></p>
						<ul class="parameters">
							<li><b>Packaging:&#160;
							</b>Cut Tape (CT)
							</li>
							<li><b>Part Status:&#160;
							</b>Active
							</li>
							<li><b>Capacitance:&#160;
							</b>10pF
							</li>
							<li><b>Tolerance:&#160;
							</b>±5%
							</li>
							<li><b>Voltage - Rated:&#160;
							</b>16V
							</li>
							<li><b>Temperature Coefficient:&#160;
							</b>C0G, NP0
							</li>
							<li><b>Operating Temperature:&#160;
							</b>-55°C ~ 125°C
							</li>
							<li><b>Features:&#160;
							</b>-
							</li>
							<li><b>Ratings:&#160;
							</b>-
							</li>
							<li><b>Applications:&#160;
							</b>General Purpose
							</li>
							<li><b>Mounting Type:&#160;
							</b>Surface Mount, MLCC
							</li>
							<li><b>Package / Case:&#160;
							</b>01005 (0402 Metric)
							</li>
							<li><b>Size / Dimension:&#160;
							</b>0.016" L x 0.008" W (0.40mm x 0.20mm)
							</li>
							<li><b>Height - Seated (Max):&#160;
							</b>-
							</li>
							<li><b>Thickness (Max):&#160;
							</b>0.009" (0.22mm)
							</li>
							<li><b>Lead Spacing:&#160;
							</b>-
							</li>
							<li><b>Lead Style:&#160;
							</b>-
							</li>
							<li><b>Base Part Number:&#160;
							</b>GRM0225C1C
							</li>
						</ul>
					</td>
					<td></td>
					<td>Murata Electronics</td>
					<td>91514</td>
					<td>14 дней</td>
					<td>шт.</td>
					<td>1</td>
					<td>
						<p>0.1000 USD</p>
						<p>0.0470 USD</p>
						<p>0.0254 USD</p>
						<p>0.0208 USD</p>
						<p>0.0148 USD</p>
						<p>0.0116 USD</p>
						<p>0.0106 USD</p>
						<p>0.0097 USD</p>
					</td>
					<td>
						<p>x1 = 0.1000 USD</p>
						<p>x10 = 0.4700 USD</p>
						<p>x50 = 1.2700 USD</p>
						<p>x100 = 2.0800 USD</p>
						<p>x500 = 7.4100 USD</p>
						<p>x1000 = 11.6500 USD</p>
						<p>x2500 = 26.4750 USD</p>
						<p>x5000 = 48.7000 USD</p>
					</td>
					<td id="cart_search_490-13463-1-ND">
						<form action="cart_action/?action=addDigiKeyToCart&amp;code=490-13463-1-ND" method="post" ajax="true" ajax-loader-id="cart_search_490-13463-1-ND-dgk"><input type="number" name="qty" value="1" min="1" step="1"></input><input type="hidden" name="img" value="https://media.digikey.com/Renders/Murata%20Renders/01005-(0402-Metric)-0,22mm.jpg"></input><input type="hidden" name="map" value="1:0.1;10:0.047;50:0.0254;100:0.0208;500:0.01482;1000:0.01165;2500:0.01059;5000:0.00974;"></input><input type="hidden" value="CAP CER 10PF 16V C0G/NP0 01005" name="name"></input><input type="hidden" value="91514" name="max"></input><input type="hidden" value="Murata Electronics" name="vendor"></input><input type="hidden" value="GRM0225C1C100JA03L" name="vendor_code"></input><input type="hidden" value="Murata Electronics" name="vendor"></input><input type="submit" value="Заказать"></input></form>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

</xsl:stylesheet>