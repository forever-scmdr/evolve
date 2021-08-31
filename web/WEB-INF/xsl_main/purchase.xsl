<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="purchase" select="page/purchase"/>
	<xsl:variable name="title" select="concat($purchase/date, ', заказ №', $purchase/num)"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>




	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{page/personal_link}" class="path__link">Персональные данные</a>
				<div class="path__arrow"></div>
				<a href="{page/purchase_history_link}" class="path__link">История заказов</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CONTENT_INNER">
		<xsl:call-template name="MESSAGE"/>
		<form action="{$purchase/to_cart_link}" method="post">
			<div class="past-order-info">
				<div>Сумма: <xsl:value-of select="$purchase/sum"/>&#160;<xsl:value-of select="$purchase/currency"/></div>
				<button type="submit" class="button"></button>
			</div>

			<table class="cart-items">
				<tbody><tr class="cart-items__head">
					<td colspan="3">Описание</td>
					<td>Количество</td>
					<td>Цена сейчас, <xsl:value-of select="$currency" /></td>
					<td>Оплачено</td>
				</tr>
				<xsl:for-each select="$purchase/bought">
					<xsl:variable name="p" select="//page/product[code = current()/code]"/>
					<xsl:variable name="zero" select="not($p/is_service = '1') and f:num($p/qty) &lt; 0.001"/>
					<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
					<tr class="cart-item cart-item_past">
						<td class="cart-item__code">
							<div>Код:</div>
							<div><xsl:value-of select="code"/></div>
						</td>
						<td class="cart-item__name">
							<a href="{$p/show_product}"><xsl:value-of select="string-join(($p/name, $p/name_extra), ' ')"/></a>
							<div><xsl:value-of select="$p/vendor"/></div>
						</td>
						<td class="cart-item__image">
							<a href="{$p/show_product}">
								<img src="sitepics/{$p/pic_path}.jpg" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg'"/>
							</a>
						</td>
						<td class="cart-item__qty">
							<input name="{input/qty/@input}" class="input" type="number" value="{qty_total}"/>
							<div class="hide_m">
								<xsl:if test="$zero">
									<xsl:if test="not($p/soon != '0')">
										<div class="status__na">Нет в наличии</div>
									</xsl:if>
									<xsl:if test="$p/soon != '0'">
										<div class="status__wait">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></div>
									</xsl:if>
								</xsl:if>
								<xsl:if test="not($zero) and not(is_service = '1')">
									В наличии <strong class="qty"><xsl:value-of select="concat($p/qty, ' ', $p/unit)"/></strong>
								</xsl:if>
							</div>
						</td>
						<td class="cart-item__price">
							<xsl:if test="not($zero)">
								<xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/>/<xsl:value-of select="$p/unit"/>
							</xsl:if>
							<xsl:if test="$zero">-</xsl:if>
							<xsl:if test="$p/special_price = 'true' and not($zero)"><div style="color: red;">Спеццена</div></xsl:if>
							<xsl:if test="f:num($p/sec/discount_1) &gt; 0">
								<div class="sale">от <xsl:value-of select="$p/sec/limit_1"/>&#160;<xsl:value-of select="$p/unit"/> -
									<xsl:value-of select="$p/sec/discount_1"/>%<xsl:call-template name="BR"/>
									от <xsl:value-of select="$p/sec/limit_2"/>&#160;<xsl:value-of select="$p/unit"/> -
									<xsl:value-of select="$p/sec/discount_2"/>%
								</div>
							</xsl:if>
						</td>
						<td class="cart-item__sum"><xsl:value-of select="concat(sum, ' ', $purchase/currency)"/></td>
					</tr>
				</xsl:for-each>
				</tbody>
			</table>
			<div class="cart-total">
				<div class="cart-total__sum">К оплате: <xsl:value-of select="concat($purchase/sum, ' ', $purchase/currency)"/></div>
				<div class="cart-total__text">
					<span class="warning">Внимание!</span> Цены на товары могли измениться. Некоторых товаров может не быть в наличии. Вы сможете проверить цены и наличие и изменить заказ в корзине.
				</div>
				<div class="cart-total__buttons">
					<button type="submit" class="button">Копировать заказ в корзину</button>
				</div>
			</div>
		</form>

		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>