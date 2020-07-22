<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="string-join(($p/type, 'Метабо', $p/name, 'купить в Минске: хорошая цена, доставка'), ' ')"/>

	<xsl:variable name="meta_description" select="string-join(($p/type, $p/name, 'Метабо по выгодной цене.', 'Доставка по Беларуси +375(29)266-44-66','Доступная цена, гарантия, 20 лет на рынке!'),' ')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else string-join(($p/type, $p/name, 'Metabo'), ' ')"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:function name="f:tab_name">
		<xsl:param name="name"/>
		<xsl:value-of select="if ($name = 'tech') then 'Характеристики' else if ($name = 'package') then 'Комплектация' else $name"/>
	</xsl:function>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="extra_xml" select="parse-xml(concat('&lt;extra&gt;', $p/extra_xml, '&lt;/extra&gt;'))/extra"/>
	<xsl:variable name="price" select="if($discount_time) then format-number(f:num($p/price)*$discount, '#0.00') else $p/price"/>
	<xsl:variable name="price_old" select="if($discount_time) then $p/price else $p/price_old"/>

	<xsl:variable name="custom_canonical" select="concat('/',string-join(page/catalog//section[.//@id = $sel_sec_id]/@key, '/'), '/', $p/@key)"/>

	<xsl:template name="MARKUP">
		<xsl:variable name="price" select="$p/price"/>
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)" />,
			"image": <xsl:value-of select="concat($quote, $main_host, '/', $p/@path, $p/gallery[1], $quote)" />,
			"brand": <xsl:value-of select="concat($quote, $p/vendor, $quote)" />,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:currency_decimal($price), $quote)" /></xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15.00"</xsl:if>
			}, "aggregateRating": {
			"@type": "AggregateRating",
			"ratingValue": "4.9",
			"ratingCount": "53",
			"bestRating": "5",
			"worstRating": "1",
			"name": <xsl:value-of select="concat($quote, translate($p/name, $quote, ''), $quote)" />
			}
			}
		</script>
	</xsl:template>

	<xsl:template name="CONTENT">





		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a><!-- <i class="fas fa-angle-right"></i>  -->
				<!-- <a href="{page/catalog_link}">Каталог</a> -->
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<i class="fas fa-angle-right"></i>
					<a href="{show_products}"><xsl:value-of select="if(display_name != '') then display_name else name"/></a>
				</xsl:for-each>
				<i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<div class="artNumber">
			Артикул товара: <xsl:value-of select="$p/code"/>
		</div>
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-width="100%" data-maxwidth="100%" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="true">
					<xsl:if test="$extra_xml/spin">
						<div data-thumb="img/360.png" style="height: 100%">
							<iframe width="100%" height="100%" data-autoplay="0" src="{tokenize($extra_xml/spin/@link, ' ')[1]}"
							        frameborder="0" allowfullscreen="" style="display: block;"/>
						</div>
					</xsl:if>
					<xsl:for-each select="$extra_xml/video">
						<a href="{substring-before(replace(@link, '-nocookie.com/embed/', '.com/watch?v='), '?rel')}">1111</a>
					</xsl:for-each>
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="img/no_image.png"/>
					</xsl:if>
				</div>
			</div>
			<div class="product-info">
				<!-- new html -->
				<xsl:for-each select="$p/label">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<xsl:if test="f:num($price_old) != 0"><div class="price_old"><span><xsl:value-of select="$price_old"/> р.</span></div></xsl:if>
								<div class="price_normal"><xsl:value-of select="if ($price) then $price else '0'"/> р.</div>
							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="text-input" name="qty" value="1" min="0" />
									<input type="submit" style="{if($p/qty and $p/qty != '0') then '' else 'background-color: #707070;'}" class="button" value="{if($p/qty and $p/qty != '0') then 'Купить' else 'Заказать'}" />
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="text-input" name="qty" value="1" min="0" />
									<input type="submit" class="button" value="Запросить цену" />
								</xsl:if>
							</form>
						</div>
						<div class="device__actions device__actions_device-page">
							<div id="compare_list_{$p/@id}">
								<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
									<i class="fas fa-balance-scale"></i>сравнить
								</a>
							</div>
							<div id="fav_list_{$p/@id}">
								<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
									<i class="fas fa-star"></i>отложить
								</a>
							</div>
						</div>
						<xsl:choose>
							<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
							<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>

				<div class="extra-buttons">
					<a class="button secondary" data-toggle="modal" data-target="#cheaper">Нашли дешевле?</a>
					<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
					<!-- <a class="button secondary" data-toggle="modal" data-target="#warranty">XXL-гарантия</a> -->
				</div>

				<div class="extra-block">
					<xsl:if test="$extra_xml/manual">
							<div>
								<i class="fas fa-file-alt"></i><a href="{normalize-space($extra_xml/manual)}" rel="nofollow" target="_blank">Руководство по эксплуатации</a>
							</div>
					</xsl:if>
					<xsl:if test="$extra_xml/parts">
						<i class="fas fa-file-alt"></i><a href="{normalize-space($extra_xml/parts)}" target="_blank" rel="nofollow">Список запчастей</a>
					</xsl:if>
				</div>

				<xsl:if test="$has_lines">
					<xsl:variable name="param_names" select="distinct-values($p/line_product/params/param/@name)"/>
					<xsl:variable name="param_captions" select="distinct-values($p/line_product/params/param/@caption)"/>
					<xsl:variable name="col_qty" select="count($param_names) + 3"/>
					<div style="height: 340px; overflow-y: scroll; margin-bottom: 32px; padding-right: 16px;">
						<div class="multi-device" style="grid-template-columns: repeat({$col_qty}, auto);">
							<!-- <div>Артикул</div> -->
							<div>Название</div>
							<xsl:for-each select="$param_captions">
								<div><xsl:value-of select="." /></div>
							</xsl:for-each>
							<div>Цена</div>
							<div>Доступность</div>
							<!-- <div></div> -->
						
							<xsl:for-each select="$p/line_product">
								<xsl:variable name="lp" select="."/>
								<xsl:variable name="has_price" select="price and price != '0'"/>
								<div><xsl:value-of select="name" /></div>
								<!-- <div><xsl:value-of select="vendor_code" /></div> -->
								<xsl:for-each select="$param_names">
									<div><xsl:value-of select="$lp/params/param[@name = current()]" /></div>
								</xsl:for-each>
								<div class="multi-device__price">
									<xsl:if test="$has_price">
										<xsl:if test="price_old"><div class="multi-device__price_old"><xsl:value-of select="price_old"/> руб.</div></xsl:if>
										<div class="multi-device__price_new"><xsl:value-of select="if (price) then price else '0'"/></div>
									</xsl:if>
									<xsl:if test="not($has_price)">
										<div class="multi-device__price_new">по запросу</div>
									</xsl:if>
								</div>
								<div class="multi-device__we-have">
									<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
										<div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div>
									</xsl:if>
									<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
										<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div>
									</xsl:if>
								</div>
								<!-- <div class="multi-device__actions" id="cart_list_{@id}">
									<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
										<xsl:if test="$has_price">
											<input type="number" class="text-input" name="qty" value="1" min="0" />
											<input type="submit" class="button" value="Заказать" />
										</xsl:if>
										<xsl:if test="not($has_price)">
											<input type="number" class="text-input" name="qty" value="1" min="0" />
											<input type="submit" class="button" value="Запросить цену" />
										</xsl:if>
									</form>
								</div> -->
							</xsl:for-each>
						</div>
					</div>
					<div class="multi-device__links">
						<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
					</div>
					<!-- <div class="multi-device__links">
						<div id="compare_list_{$p/@id}">
							<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
								<i class="fas fa-balance-scale"></i>сравнить
							</a>
						</div>
						<div id="fav_list_{$p/@id}">
							<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</div> -->
				</xsl:if>

				<xsl:variable name="extra" select="parse-xml(concat('&lt;extra&gt;', $p/extra_xml, '&lt;/extra&gt;'))/extra"/>
				<div class="item-icons" style="margin-bottom: 24px;">
					<xsl:for-each select="$extra/pic"><span><img src="{@link}" alt="{.}"  data-toggle="tooltip" data-placement="left" title="{.}"/></span></xsl:for-each>
					<script>
						$(function () {
							$('[data-toggle="tooltip"]').tooltip()
						})
					</script>
				</div>

				<div class="device-benefits">
					<div class="device-benefits__item">
						<i class="fas fa-trophy device-benefits__icon"></i>
						<div class="device-benefits__label"><a href="https://www.metabo.com/com/en/info/company/metabo-worldwide/" target="_blank" rel="nofollow">Первый официальный дистрибутор</a></div>
					</div>

					

					<div class="device-benefits__item">
						<img src="img/benefits-icon-2.svg" alt=""/>
						<div class="device-benefits__label">Прямые поставки из Германии</div>
					</div>
					<div class="device-benefits__item">
						<img src="img/benefits-icon-1.svg" alt=""/>
						<div class="device-benefits__label">Опыт поставок и ремонта более 20 лет</div>
					</div>
				</div>
				<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>
				<!-- <div class="extra-contacts">
					<div class="extra-contacts__title">Звоните, чтобы получить помощь и консультацию</div>
					<div class="extra-contacts__items">
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 123-45-67</div>
							<div class="extra-contacts__text">Отдел продаж</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 123-45-67</div>
							<div class="extra-contacts__text">Гарантийный отдел</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 123-45-67</div>
							<div class="extra-contacts__text">Сервис</div>
						</div>
					</div>
				</div> -->
<!-- 				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div> -->
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
							<!-- <xsl:if test="$p/params">
							<li role="presentation" class="active">
									<a href="#tab1" role="tab" data-toggle="tab">Характеристики</a>
								</li>
							</xsl:if> -->

							<xsl:if test="$p/text != ''">
								<li role="presentation" class="active">
									<a href="#tab0" role="tab" data-toggle="tab">Описание</a>
								</li>
							</xsl:if>
							<xsl:for-each select="$p/product_extra">
								<xsl:variable name="pos" select="position()"/>
								<li role="presentation" class="{if(not($p/text != '') and $pos = 1) then 'active' else ''}">
									<a href="#tab{@id}" role="tab" data-toggle="tab"><xsl:value-of select="f:tab_name(name)"/></a>
								</li>
							</xsl:for-each>
					</ul>
				<div class="tab-content">
					<xsl:if test="$p/text != ''">
					<div role="tabpanel" class="tab-pane active" id="tab0">
						<div>
							<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
						</div>
					</div>
					</xsl:if>
					<!-- <xsl:if test="$p/params">
						<div role="tabpanel" class="tab-pane active" id="tab1">
							<table>
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<xsl:for-each select="$p/params/param">
									<tr>
										<td>
											<p><strong><xsl:value-of select="@caption"/></strong></p>
										</td>
										<td>
											<p><xsl:value-of select="."/></p>
										</td>
									</tr>
								</xsl:for-each>
							</table>

						</div>
					</xsl:if> -->
					<xsl:for-each select="$p/product_extra">
						<xsl:variable name="pos" select="position()"/>
						<div role="tabpanel" class="tab-pane {if(not($p/text != '') and $pos = 1) then 'active' else ''}" id="tab{@id}">
							<!-- <h4><xsl:value-of select="name"/></h4> -->
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
			<div class="someInfo">
				<xsl:for-each select="$common/catalog_texts/product_text">
				<div>
					<p><div class="someIcon"><i class="fas {icon}"></i></div>
						<strong>
							<xsl:if test="link != ''">
								<a href="link"><xsl:value-of select="name"/></a>
							</xsl:if>
							<xsl:if test="not(link != '')">
								<xsl:value-of select="name"/>
							</xsl:if>
						</strong>
					</p>
					<xsl:value-of select="text" disable-output-escaping="yes"/>
				</div>
				</xsl:for-each>
			</div>
		</div>

		<xsl:if test="page/box">
			<h3 style="margin-bottom: 16px; margin-top: 0"><strong>Комплектация BOX</strong></h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/box"/>
			</div>
		</xsl:if>

		<xsl:if test="page/assoc">
			<h3 style="margin-bottom: 16px; margin-top: 0"><strong>Вас также может заинтересовать</strong></h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>



</xsl:stylesheet>
