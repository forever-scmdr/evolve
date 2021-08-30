<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl" />
	<xsl:template name="TITLE" />
	<xsl:variable name="cart" select="page/cart" />
	<xsl:variable name="base" select="page/base" />

	
	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
		</xsl:text>
	</xsl:template>
	
	
	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<head>
				<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
				<base href="{page/base}" />
				<title>Заказ от: <xsl:value-of select="f:format_date(current-date())" /></title>
				<style>		
			        @page {
			            size: A4 portrait;
			        }
			
			        @media print {
			            .new_page {
			                page-break-after: always;
			            }
			        }			
					*{ font-family: 'Arial Unicode MS';}
					table, td{border-collapse: collapse; cell-padding:0; cell-spacing:0; font-size: 12px;}
					td{border: 1px solid #bbb; padding: 10px 5px; vertical-align: middle;}
					.name{width: 60%;}
				</style>
			</head>
			<body >
				<xsl:if test="count(//bought) != 0 or $cart/custom_bought[nonempty = 'true']">
					<div>
						<h1>
							<b>Заказ от: <xsl:value-of select="f:format_date(current-date())" />.</b>
						</h1>
						<p>Магазин радиодеталей <a href="{page/base}">belchip.by</a></p>

						<xsl:if test="$cart/bought[qty_avail != '0']">
							<table>
								<tr>
									<td>Арт.</td>
									<td class="name">Наименование</td>
									<td>Кол.</td>
									<td>Цена, <xsl:value-of select="$cart/currency"/></td>
									<td>Стоимость</td>
								</tr>
								<xsl:for-each-group select="$cart/bought[qty_avail != '0']" group-by="type">
									<xsl:for-each select="current-group()">
										<xsl:variable name="p_unit" select="if (not(product/unit) or product/unit = '') then 'шт.' else product/unit" />
										<tr>
											<td><xsl:value-of select="code" /></td>
											<td ><a href="{concat($base,'/', product/show_product)}"><xsl:value-of select="concat(product/name, ' ', product/name_extra)" /></a></td>
											<td><xsl:value-of select="qty_avail" /></td>
											<td><xsl:if test="product/price">
													<xsl:call-template name="rub_kop_unit">
														<xsl:with-param name="price" select="price" />
														<xsl:with-param name="unit" select="$p_unit" />
													</xsl:call-template>
												</xsl:if>
											</td>
											<td><xsl:if test="sum">
													<xsl:call-template name="rub_kop_unit">
														<xsl:with-param name="price" select="sum" />
													</xsl:call-template>
												</xsl:if>											
											</td>
										</tr>
									</xsl:for-each>
								</xsl:for-each-group>

							</table>
						</xsl:if>
						<xsl:if test="$cart/bought[qty_zero != '0']">
							<p>Позиции для запроса</p>

							<table>
								<tr>
									<td>Арт.</td>
									<td class="name">Наименование</td>
									<td>Кол.</td>
									<td>Цена, BYN</td>
									<td>Стоимость</td>
								</tr>
								<xsl:for-each-group select="$cart/bought[qty_zero != '0']" group-by="type">
									<xsl:for-each select="current-group()">
										<xsl:variable name="p_unit" select="if (not(product/unit) or product/unit = '') then 'шт.' else product/unit" />
										<tr>
											<td><xsl:value-of select="code" /></td>
											<td ><a href="{concat($base, '/', product/show_product)}"><xsl:value-of select="concat(product/name, ' ', product/name_extra)" /></a></td>
											<td><xsl:value-of select="qty_zero" /></td>
											<td>Цена будет сформирована после запроса у поставщика</td>
											<td>Рассчитывается после формирования цены</td>
										</tr>
									</xsl:for-each>
								</xsl:for-each-group>

							</table>
						</xsl:if>
						<xsl:if test="$cart/custom_bought[nonempty = 'true']">
							<p>Персональный заказ</p>
							<table>
								<tr>
									<td>
										Маркировка
									</td>
									<td>
										Тип прибора
									</td>
									<td>
										Корпус
									</td>
									<td>
										Количество
									</td>
									<td>
										Ссылка
									</td>
									<td>
										Дополнительная информация
									</td>
								</tr>
								<xsl:for-each select="$cart/custom_bought[nonempty = 'true']">
									<tr>
										<td>
											<xsl:value-of select="mark"/>
										</td>
										<td>
											<xsl:value-of select="type"/>
										</td>
										<td>
											<xsl:value-of select="case"/>
										</td>
										<td>
											<xsl:value-of select="qty"/>
										</td>
										<td>
											<a href="{link}"><xsl:value-of select="link"/></a>
										</td>
										<td>
											<xsl:value-of select="extra"/>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</xsl:if>
						<xsl:if test="$cart/sum !=  '0,00'">
						<p>
							<span style="font-size:16.0pt;line-height:106%;">
								Итого:<b>
									<xsl:if test="$cart/simple_sum">
										<xsl:value-of select="concat($cart/simple_sum, ' ', $cart/currency)" />
									</xsl:if>
									</b>
								<xsl:if test="$cart/discount &gt; 0">
									<br />
									Скидка:
									<b>
										<xsl:value-of select="$cart/discount" /> %
									</b>
									- на товар не участвующий в спец. предложениях
									<br />
									Сумма скидки:
									<b>
										<xsl:if test="$cart/margin">
											<xsl:value-of select="concat($cart/margin, ' ', $cart/currency)" />
										</xsl:if>
									</b>
									<br />
									К оплате:
									<b>
										<xsl:if test="$cart/sum">
											<xsl:value-of select="concat($cart/sum, ' ', $cart/currency)" />
										</xsl:if>
									</b>
								</xsl:if>
							</span>
						</p>
						</xsl:if>
					</div>
				</xsl:if>
			</body>
		</html>
	</xsl:template>


	<!-- Перевод XSL даты в миллисекунды -->
	<xsl:function name="f:date_to_millis">
		<xsl:param name="date" as="xs:date" />
		<xsl:sequence
			select="($date - xs:date('1970-01-01')) div xs:dayTimeDuration('PT0.001S')" />
	</xsl:function>

	<!-- Перевод миллисекунд в XSL дату -->
	<xsl:function name="f:millis_to_date" as="xs:date">
		<xsl:param name="millis" />
		<xsl:sequence
			select="if ($millis) then xs:date('1970-01-01') + $millis * xs:dayTimeDuration('PT0.001S') else xs:date('1970-01-01')" />
	</xsl:function>

	<!-- Перевод даты из CMS вида (23.11.2017) в XSL вид -->
	<xsl:function name="f:xsl_date" as="xs:date">
		<xsl:param name="str_date" />
		<xsl:variable name="parts"
			select="tokenize(tokenize($str_date, '\s+')[1], '\.')" />
		<xsl:sequence
			select="if ($parts[3]) then xs:date(concat($parts[3], '-', $parts[2], '-', $parts[1])) else xs:date('1970-01-01')" />
	</xsl:function>

	<!-- Перевод даты из XSL вида в CMS вид (23.11.2017) -->
	<xsl:function name="f:format_date">
		<xsl:param name="date" as="xs:date" />
		<xsl:sequence select="format-date($date, '[D01].[M01].[Y0001]')" />
	</xsl:function>

	<!-- Перевод строки в число. Пуская строка переводится в 0 -->
	<xsl:function name="f:num">
		<xsl:param name="num_str" />
		<xsl:value-of
			select="if (not($num_str) or $num_str = '') then 0 else number(translate(translate($num_str, '&#160;', ''), ',', '.'))" />
	</xsl:function>
</xsl:stylesheet>