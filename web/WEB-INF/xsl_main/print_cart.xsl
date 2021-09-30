<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl" />
	<xsl:template name="TITLE" />
	<xsl:variable name="cart" select="//cart" />

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<xsl:variable name="rates" select="page/currencies"/>


	<xsl:variable name="top_td_style" >width:100.0%;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:4.25pt 5.4pt 4.25pt 5.4pt</xsl:variable>
	<xsl:variable name="td_style" >border:solid windowtext 1.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;height:15.1pt</xsl:variable>
	<xsl:variable name="td_style_2" >border:solid windowtext 1.0pt;border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;height:15.1pt</xsl:variable>
	<xsl:variable name="td_style_3" >border:solid windowtext 1.0pt;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;height:15.1pt</xsl:variable>
	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<base href="{page/base}" />
				<title>Ваш заказ (версия для печати)</title>
				<style type="text/css">

					.header{
					padding: 15px 40px;
					color: #fff;
					text-transform: upprecase;
					font-size: 20px;
					font-family: consolas;
					margin-bottom: 20px;
					background: #555;
					font-weight: bold;
					-webkit-box-shadow: 2px 10px 13px 0px rgba(0,0,0,0.75);
					-moz-box-shadow: 2px 10px 13px 0px rgba(0,0,0,0.75);
					box-shadow: 2px 10px 13px 0px rgba(0,0,0,0.75);
					}

					.header a{
					display: inline-block;
					margin-right: 30px;
					text-decoration: none;
					cursor: pointer;
					color: #fff;
					}
					.header a:hover{
					text-decoration: underline;
					}

					/* Font Definitions */
					@font-face
					{font-family:"Cambria Math";
					panose-1:2 4 5 3 5 4 6 3 2 4;
					mso-font-charset:1;
					mso-generic-font-family:roman;
					mso-font-format:other;
					mso-font-pitch:variable;
					mso-font-signature:0
					0 0 0 0 0;}
					@font-face
					{font-family:Calibri;
					panose-1:2 15 5 2 2 2 4 3 2 4;
					mso-font-charset:204;
					mso-generic-font-family:swiss;
					mso-font-pitch:variable;
					mso-font-signature:-536870145 1073786111 1
					0 415 0;}
					/* Style Definitions */
					p.MsoNormal, li.MsoNormal, div.MsoNormal
					{mso-style-unhide:no;
					mso-style-qformat:yes;
					mso-style-parent:"";
					margin-top:0cm;
					margin-right:0cm;
					margin-bottom:8.0pt;
					margin-left:0cm;
					line-height:106%;
					mso-pagination:widow-orphan;
					font-size:11.0pt;
					font-family:"Calibri",sans-serif;
					mso-ascii-font-family:Calibri;
					mso-ascii-theme-font:minor-latin;
					mso-fareast-font-family:Calibri;
					mso-fareast-theme-font:minor-latin;
					mso-hansi-font-family:Calibri;
					mso-hansi-theme-font:minor-latin;
					mso-bidi-font-family:"Times New
					Roman";
					mso-bidi-theme-font:minor-bidi;
					mso-fareast-language:EN-US;}
					span.SpellE
					{mso-style-name:"";
					mso-spl-e:yes;}
					.MsoChpDefault
					{mso-style-type:export-only;
					mso-default-props:yes;
					font-size:10.0pt;
					mso-ansi-font-size:10.0pt;
					mso-bidi-font-size:10.0pt;
					font-family:"Calibri",sans-serif;
					mso-ascii-font-family:Calibri;
					mso-ascii-theme-font:minor-latin;
					mso-fareast-font-family:Calibri;
					mso-fareast-theme-font:minor-latin;
					mso-hansi-font-family:Calibri;
					mso-hansi-theme-font:minor-latin;
					mso-bidi-font-family:"Times New
					Roman";
					mso-bidi-theme-font:minor-bidi;
					mso-fareast-language:EN-US;}
					@page WordSection1
					{size:595.3pt 841.9pt;
					margin:2.0cm 42.5pt 2.0cm
					3.0cm;
					mso-header-margin:35.4pt;
					mso-footer-margin:35.4pt;
					mso-paper-source:0;}
					div.WordSection1
					{page:WordSection1;}
					table{border-collapse: collapse;}
					table{
					border-collapse: collapse;
					}

					@media print{
					.no-print{display: none;}
					}
				</style>
			</head>
			<body style="tab-interval:35.4pt">

				<div class="no-print header">
					<a href="{/page/cart_link}">Назад к заказу</a>
					<a onclick="window.print()">Печать</a>
					<a href="pdf_order" title="Скачать в виде докумета PDF" download="{concat('Заказ_от_', f:format_date(current-date()), '.pdf')}">Сохранить</a>
					<a href="{/page/proceed_link}" style="padding: 6px 12px; background: #588423; border-radius: 6px;">Оформить</a>
				</div>
				<xsl:if test="count($cart/bought) != 0 or $cart/custom_bought[nonempty = 'true']">
					<div class="WordSection1">
						<p class="MsoNormal">
							<b>
								<span style="font-size:16.0pt;line-height:106%;">
									Заказ № <xsl:value-of select="$cart/order_num"/> от:
									<xsl:value-of select="f:format_date(current-date())" />.
								</span>
							</b>
						</p>

						<p class="MsoNormal">
							<span style="font-size:14.0pt;line-height:106%; ">Магазин радиодеталей </span>
							<span class="SpellE">
								<u>
									<span style="font-size:14.0pt;line-height:106%;">belchip.by</span>
								</u>
							</span>
						</p>

						<xsl:if test="$cart/bought[qty_avail != '0']">
							<table class="MsoTableGrid" border="1" cellspacing="0"
								cellpadding="0" width="100%" style="{$top_td_style}">
								<tr style='mso-yfti-irow:1;height:15.1pt'>
										<td width="9%" valign="top"
											style="width:9.06%;{$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																					  normal">
												<span style="font-size:12.0pt;">Арт.</span>
											</p>
										</td>
										<td width="45%" valign="top"
											style="width:45.48%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																					  normal">
												<span style="font-size:12.0pt;">Наименование</span>
											</p>
										</td>
										<td width="10%" valign="top"
											style="width:10.64%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																					  normal">
												<span style="font-size:12.0pt;">Кол.</span>
											</p>
										</td>
										<td width="18%" valign="top"
											style="width:18.18%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																					  normal">
												<span style="font-size:12.0pt;">цена, <xsl:value-of select="$curr_out"/></span>
											</p>
										</td>
										<td width="16%" valign="top"
											style="width:16.64%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																					  normal">
												<span style="font-size:12.0pt;">стоимость</span>
											</p>
										</td>
									</tr>
								<xsl:for-each-group select="$cart/bought[qty != '0']" group-by="type">
									<xsl:variable name="code" select="code" />
<!-- 									<tr> -->
<!-- 										<td width="100%" colspan="5" -->
<!-- 											style="width:100.0%; -->
<!-- 										border:solid windowtext 1.0pt; -->
<!-- 										mso-border-alt:solid windowtext .5pt; -->
<!-- 										padding:4.25pt 5.4pt 4.25pt 5.4pt; height:19.6pt"> -->
<!-- 											<p class="MsoNormal" align="center" -->
<!-- 												style=" -->
<!-- 										margin-bottom:0cm;margin-bottom:.0001pt; -->
<!-- 										text-align:center;line-height:normal"> -->
<!-- 												<span style='font-size:12.0pt'> -->
<!-- 													<xsl:value-of select="current-grouping-key()" /> -->
<!-- 												</span> -->
<!-- 											</p> -->
<!-- 										</td> -->
<!-- 									</tr> -->

									<xsl:for-each select="current-group()">
										<xsl:variable name="p_unit" select="if (not(product/unit) or product/unit = '') then 'шт.' else product/unit" />
										<tr style='mso-yfti-irow:1;height:15.1pt'>
											<td width="9%" valign="top"
												style="width:9.06%;border:solid windowtext 1.0pt;
																							  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
																							  padding:4.25pt 5.4pt 4.25pt 5.4pt;height:15.1pt">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														<xsl:value-of select="code" />
													</span>
												</p>
											</td>
											<td width="45%" valign="top"
												style="width:45.48%;border-top:none;border-left:
																							  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
																							  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
																							  mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;
																							  height:15.1pt">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														<p class="MsoNormal"
															style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
														  normal">
															<span
																style="font-size:12.0pt;color:#7F7F7F;mso-themecolor:background1;
														  mso-themeshade:128;mso-style-textfill-fill-color:#7F7F7F;mso-style-textfill-fill-themecolor:
														  background1;mso-style-textfill-fill-alpha:100.0%;mso-style-textfill-fill-colortransforms:
														  lumm=50000">
																<xsl:value-of select="product/name" />
															</span>
														</p>
													</span>
													<p class="MsoNormal"
														style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
													  normal">
														<u>
															<span style="font-size:12.0pt;">
																<xsl:value-of select="product/name_extra" />
															</span>
														</u>
													</p>
												</p>
											</td>
											<td width="10%" valign="top"
												style="width:10.64%;border-top:none;border-left:
																							  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
																							  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
																							  mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;
																							  height:15.1pt">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														<xsl:value-of select="qty" />
													</span>
												</p>
											</td>
											<td width="18%" valign="top"
												style="width:18.18%;border-top:none;border-left:
																							  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
																							  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
																							  mso-border-alt:solid windowtext .5pt;padding:4.25pt 5.4pt 4.25pt 5.4pt;
																							  height:15.1pt">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														<xsl:if test="product/price">
															<xsl:value-of select="f:exchange_cur(product, 'price', 0)"/>/<xsl:value-of select="product/unit"/>
														</xsl:if>
													</span>
												</p>
											</td>
											<td width="16%" valign="top"
												style="width:16.64%; {$td_style}">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														<xsl:if test="sum">
															<xsl:value-of select="f:exchange_cur(., 'sum', 0)"/>
														</xsl:if>
													</span>
												</p>
											</td>
										</tr>
									</xsl:for-each>
								</xsl:for-each-group>

							</table>
						</xsl:if>
						<xsl:if test="$cart/bought[qty_zero != '0']">
							<p class="MsoNormal" style="margin-bottom:0cm;line-height: normal">
							<span style="font-size:15.0pt;">&nbsp;</span>
							</p>
							<p class="MsoNormal"
								style="line-height: normal">
								<span style="font-size:15.0pt;">
									Позиции для запроса
								</span>
							</p>

							<table class="MsoTableGrid" border="1" cellspacing="0"
								cellpadding="0" width="100%" style="width:100.0%;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:4.25pt 5.4pt 4.25pt 5.4pt">
								<tr style='mso-yfti-irow:1;height:15.1pt'>
										<td width="9%" valign="top"
											style="width:9.06%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:normal">
												<span style="font-size:12.0pt;">Арт.</span>
											</p>
										</td>
										<td width="45%" valign="top"
											style="width:45.48%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
												<span style="font-size:12.0pt;">Наименование</span>
											</p>
										</td>
										<td width="10%" valign="top"
											style="width:10.64%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:normal">
												<span style="font-size:12.0pt;">Кол.</span>
											</p>
										</td>
										<td width="18%" valign="top"
											style="width:18.18%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:normal">
												<span style="font-size:12.0pt;">цена, BYN</span>
											</p>
										</td>
										<td width="16%" valign="top"
											style="width:16.64%; {$td_style_3}">
											<p class="MsoNormal"
												style="margin-bottom:0cm;margin-bottom:.0001pt;	line-height: normal">
												<span style="font-size:12.0pt;">стоимость</span>
											</p>
										</td>
									</tr>
								<xsl:for-each-group select="$cart/bought[qty_zero != '0']" group-by="type">
									<xsl:variable name="code" select="code" />
<!-- 									<tr> -->
<!-- 										<td width="100%" colspan="5" -->
<!-- 											style="width:100.0%; -->
<!-- 										border:solid windowtext 1.0pt; -->
<!-- 										mso-border-alt:solid windowtext .5pt; -->
<!-- 										padding:4.25pt 5.4pt 4.25pt 5.4pt; height:19.6pt"> -->
<!-- 											<p class="MsoNormal" align="center" -->
<!-- 												style=" -->
<!-- 										margin-bottom:0cm;margin-bottom:.0001pt; -->
<!-- 										text-align:center;line-height:normal"> -->
<!-- 												<span style='font-size:12.0pt'> -->
<!-- 													<xsl:value-of select="current-grouping-key()" /> -->
<!-- 												</span> -->
<!-- 											</p> -->
<!-- 										</td> -->
<!-- 									</tr> -->
									
									<xsl:for-each select="current-group()">
										<xsl:variable name="p_unit"
											select="if (not(product/unit) or product/unit = '') then 'шт.' else product/unit" />
										<tr style='mso-yfti-irow:1;height:15.1pt'>
											<td width="9%" valign="top"
												style="width:9.06%;	{$td_style_2}">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:normal">
													<span style="font-size:12.0pt;">
														<xsl:value-of select="code" />
													</span>
												</p>
											</td>
											<td width="45%" valign="top"
												style="width:45.48%; {$td_style}">
												<p class="MsoNormal" style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:normal">
													<span style="font-size:12.0pt;">
														<p class="MsoNormal"
															style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
															<span style="font-size:12.0pt;color:#7F7F7F;mso-themecolor:background1;mso-themeshade:128;mso-style-textfill-fill-color:#7F7F7F;mso-style-textfill-fill-themecolor:background1;mso-style-textfill-fill-alpha:100.0%;mso-style-textfill-fill-colortransforms: lumm=50000">
																<xsl:value-of select="product/name" />
															</span>
														</p>
													</span>
													<p class="MsoNormal"
														style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
														<u>
															<span style="font-size:12.0pt;">
																<xsl:value-of select="product/name_extra" />
															</span>
														</u>
													</p>
												</p>
											</td>
											<td width="10%" valign="top"
												style="width:10.64%; {$td_style}">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
													<span style="font-size:12.0pt;">
														<xsl:value-of select="qty_zero" />
													</span>
												</p>
											</td>
											<td width="18%" valign="top"
												style="width:18.18%; {$td_style}">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
													<span style="font-size:12.0pt;">
														<!-- <xsl:call-template name="rub_kop_unit"> <xsl:with-param 
															name="price" select="product/price"/> <xsl:with-param name="unit" select="$p_unit"/> 
															</xsl:call-template> -->
														Цена будет сформирована после запроса у поставщика
													</span>

												</p>
											</td>
											<td width="16%" valign="top"
												style="width:16.64%; {$td_style}">
												<p class="MsoNormal"
													style="margin-bottom:0cm;margin-bottom:.0001pt;line-height:
																							  normal">
													<span style="font-size:12.0pt;">
														Рассчитывается после формирования цены
													</span>
												</p>
											</td>
										</tr>
									</xsl:for-each>
								</xsl:for-each-group>

							</table>
						</xsl:if>
						<xsl:if test="$cart/custom_bought[nonempty = 'true']">
							<p class="MsoNormal" style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
							<span style="font-size:15.0pt;">&nbsp;</span>
							</p>
							<p class="MsoNormal" style="line-height: normal">
								<span style="font-size:15.0pt;">
									Персональный заказ
								</span>
							</p>
							<table class="MsoTableGrid" border="1" cellspacing="0"
								cellpadding="0" width="100%" style="width:100.0%;border:none;mso-border-alt:solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:4.25pt 5.4pt 4.25pt 5.4pt">
								<tr>
									<td style="border:solid windowtext 1.0pt; mso-border-alt:solid windowtext .5pt; padding:4.25pt 5.4pt 4.25pt 5.4pt;">
										Маркировка
									</td>
									<td style="border:solid windowtext 1.0pt; mso-border-alt:solid windowtext .5pt; padding:4.25pt 5.4pt 4.25pt 5.4pt;">
										Тип прибора
									</td>
									<td style="border:solid windowtext 1.0pt; mso-border-alt:solid windowtext .5pt; padding:4.25pt 5.4pt 4.25pt 5.4pt;">
										Корпус
									</td>
									<td style="border:solid windowtext 1.0pt; mso-border-alt:solid windowtext .5pt; padding:4.25pt 5.4pt 4.25pt 5.4pt;">
										Количество
									</td>
									<td style="border:solid windowtext 1.0pt; mso-border-alt:solid windowtext .5pt; padding:4.25pt 5.4pt 4.25pt 5.4pt;">
										Дополнительная информация
									</td>
								</tr>
								<xsl:for-each select="$cart/custom_bought[nonempty = 'true']">
									<tr>
										<td style="{$td_style_2}">
											<xsl:value-of select="mark"/>
										</td>
										<td style="{$td_style}">
											<xsl:value-of select="type"/>
										</td>
										<td style="{$td_style}">
											<xsl:value-of select="case"/>
										</td>
										<td style="{$td_style}">
											<xsl:value-of select="qty"/>
										</td>
										<td style="{$td_style}">
											<xsl:value-of select="extra"/>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</xsl:if>
						<p class="MsoNormal"
							style="margin-bottom:0cm;margin-bottom:.0001pt;line-height: normal">
							<span style="font-size:15.0pt;">&nbsp;</span>
						</p>

						<p class="MsoNormal">
							<u>
								<span style="">
									<span style="text-decoration:none">
										<br />
									</span>
								</span>
							</u>
						</p>
						<xsl:if test="$cart/sum !=  '0,00'">
						<p class="MsoNormal">
							<span style="font-size:16.0pt;line-height:106%;">
								Итого:
								<b style="mso-bidi-font-weight:normal">
									<xsl:if test="$cart/simple_sum">
										<xsl:value-of select="f:exchange_cur($cart, 'simple_sum', '0')" />
									</xsl:if>
								</b>
								
								<xsl:if test="$cart/discount &gt; 0">
									<br />
									Скидка:
									<b style="mso-bidi-font-weight:normal">
										<xsl:value-of select="$cart/discount" />%
									</b>
									- на товар не участвующий
									в спец. предложениях
									<br />
									Сумма скидки:
									<b style="mso-bidi-font-weight:normal">
										<xsl:if test="$cart/margin">
											<xsl:value-of select="f:exchange_cur($cart, 'margin', '0')" />
										</xsl:if>
									</b>
									
									<br />
									К оплате:
									<b style="mso-bidi-font-weight:normal">
										<xsl:if test="$cart/sum">
											<xsl:value-of select="f:exchange_cur($cart, 'sum', '0')" />
										</xsl:if>
									</b>
									<span style="mso-spacerun:yes">  </span>
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