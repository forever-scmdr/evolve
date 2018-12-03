<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>

	<xsl:template name="MARKUP">
		<xsl:variable name="price" select="$p/price"/>
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)" />,
			"image": <xsl:value-of select="concat($quote, $base, '/', $p/@path, $p/gallery[1], $quote)" />,
			"brand": <xsl:value-of select="concat($quote, $p/tag[1], $quote)" />,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:currency_decimal($price), $quote)" /></xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15000.00"</xsl:if>
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
				<a href="{$main_host}">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">
			<xsl:if test="not($seo/h1 = '')">
				<xsl:value-of select="$h1"/>
			</xsl:if>
			<xsl:if test="$seo/h1 = ''">
				<b><xsl:value-of select="$p/vendor"/></b><xsl:text> </xsl:text><xsl:value-of select="$p/vendor_code"/>
			</xsl:if>
		</h1>
		<p><xsl:value-of select="$p/name_extra"/></p>
		<div class="catalog-item-container">
			<!--
			<div class="tags">
				<span>Акция</span>
				<span>Скидка</span>
				<span>Распродажа</span>
				<span>Горячая цена</span>
			</div>
			-->
			<div class="gallery">
				<div class="fotorama" data-width="100%" data-maxwidth="100%" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="true">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="{concat($p/@path, $p/main_pic)}" alt="{$p/name}"/>
					</xsl:if>
				</div>
			</div>
			<div class="product-info">
				<xsl:for-each select="$p/tag">
					<div class="tag-container">
						<div class="device__tag"><xsl:value-of select="." /></div>
					</div>
				</xsl:for-each>
				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
				<xsl:if test="$has_price">
					<div class="price">
						<!-- <p><span>Старая цена</span>100 р.</p> -->
						<p><!-- <span>Новая цена</span> --><span>Цена</span><xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.</p>
					</div>
				</xsl:if>
				<div class="order">
					<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
					<div id="cart_list_{replace($p/code, '[)()]', '-')}" class="product_purchase_container">
						<form action="{$p/to_cart}" method="post" ajax="true">
							<xsl:if test="$has_price">
								<input type="number" name="qty" value="1" min="0"/>
								<input type="submit" value="Заказать"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" name="qty" value="1" min="0"/>
								<input type="submit" class="not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
					<xsl:choose>
						<xsl:when test="$p/qty and $p/qty != '0'"><div class="quantity">Осталось <xsl:value-of select="$p/qty"/> шт.</div></xsl:when>
						<xsl:otherwise><!-- <div class="quantity">Нет на складе</div> --></xsl:otherwise>
					</xsl:choose>
				</div>
				<div class="links">
					<div id="compare_list_{$p/code}">
						<span><i class="fas fa-balance-scale"></i> <a href="{$p/to_compare}" ajax="true" ajax-loader-id="compare_list_{$p/code}">сравнить</a></span>
					</div>
					<div id="fav_list_{$p/code}">
						<span><i class="fas fa-star"></i> <a href="{$p/to_fav}" ajax="true" ajax-loader-id="fav_list_{$p/code}">отложить</a></span>
					</div>
				</div>
				<div class="info-blocks">
					<strong>Информация о продукте</strong>
					<div class="some-parameters">
						<div>Код:</div><div class="value"><xsl:value-of select="$p/code"/></div>
						<div>Обозначение производителя:</div><div class="value"><xsl:value-of select="$p/vendor_code"/></div>
						<div>Производитель:</div><div class="value"><xsl:value-of select="$p/vendor"/></div>
						<xsl:for-each select="$p/manual">
							<div><xsl:value-of select="name"/></div><div class="value">
								<a href="https://tme.eu/{link}">Скачать</a>
							</div>
						</xsl:for-each>
						<!--<xsl:value-of select="$p/text" disable-output-escaping="yes"/>-->
					</div>
				</div>
			</div>
			<div class="description">
				
					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
							<xsl:if test="$p/params">
								<li role="presentation" class="active">
									<a href="#tab1" role="tab" data-toggle="tab">Спецификация</a>
								</li>
							</xsl:if>
							<xsl:if test="$p/text">
								<li role="presentation">
									<a href="#tab2" role="tab" data-toggle="tab">Описание</a>
								</li>
							</xsl:if>
							<xsl:if test="$p/product">
								<li role="presentation" class="{'active'[not($p/params)]}">
									<a href="#tab2" role="tab" data-toggle="tab">
										Другие расцветки
									</a>
								</li>
							</xsl:if>
							<xsl:for-each select="$p/product_extra">
								<li role="presentation">
									<a href="#tab{@id}" role="tab" data-toggle="tab"><xsl:value-of select="name"/></a>
								</li>
							</xsl:for-each>
					</ul>
				<div class="tab-content">
					<xsl:if test="$p/params">
						<div role="tabpanel" class="tab-pane active" id="tab1">
							<!--<xsl:value-of select="$p/text" disable-output-escaping="yes"/>-->
							<table>
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<xsl:for-each select="$p/params/param">
									<tr>
										<td>
											<xsl:value-of select="@caption"/>
										</td>
										<td style="text-align: right;">
											<strong>
												<xsl:value-of select="."/></strong>
										</td>
									</tr>
								</xsl:for-each>
							</table>

						</div>
					</xsl:if>
					<xsl:if test="$p/text">
						<div role="tabpanel" class="tab-pane" id="tab2">
							<h4>Описание</h4>
							<div class="catalog-items">
								<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<div role="tabpanel" class="tab-pane" id="tab{@id}">
							<h4><xsl:value-of select="name"/></h4>
							<div class="catalog-items">
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:if test="page/assoc">
			<h3>Вас также может заинтересовать</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc" mode="lines"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>