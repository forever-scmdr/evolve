<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="view" select="$pv/view"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="has_tab_cart" select="$cart/bought[qty != '0']"/>
	<xsl:variable name="has_tab_order" select="$cart/bought[qty_zero != '0']"/>
	<xsl:variable name="has_tab_fav" select="page/product"/>
	<xsl:variable name="tab_active" select="if ($pv/tab) then $pv/tab else if ($has_tab_cart) then 'tab_cart' else if ($has_tab_order) then 'tab_order' else 'tab_personal'"/>



	<xsl:template match="bought">
		<xsl:param name="is_available"/>
		<xsl:variable name="p" select="product"/>
		<xsl:variable name="zero" select="not($p/is_service = '1') and f:num($p/qty) &lt; 0.001"/>
		<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:currency_decimal($p/price), ' pуб.') else 'по запросу'"/>
		<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(sum), ' pуб.') else ''"/>
		<tr class="cart-item">
			<td class="cart-item__code">
				<div>Код:</div>
				<div><xsl:value-of select="$p/code"/></div>
			</td>
			<td class="cart-item__name">
				<a href="{$p/show_product}"><xsl:value-of select="string-join(($p/name, $p/name_extra), ' ')"/></a>
				<div><xsl:value-of select="vendor"/></div>
				<div class="cart-item__qty_mobile">
					<xsl:if test="$zero">
						<xsl:if test="not($p/soon != '0')">Нет в наличии</xsl:if>
						<xsl:if test="$p/soon != '0'">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></xsl:if>
						<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>
					</xsl:if>
					<xsl:if test="not($zero) and not($p/is_service = '1')">
						В наличии: <xsl:value-of select="concat($p/qty, ' ', $p/unit)"/>
					</xsl:if>
				</div>
			</td>
			<td class="cart-item__image">
				<a href="{$p/show_product}">
					<img src="sitepics/{$p/pic_path}.jpg" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg'"/>
				</a>
			</td>
			<td class="cart-item__qty">
				<input type="number" class="input" name="{input/qty/@input}" data-old="{qty}"
					   value="{qty_total}" min="{if ($p/min_qty) then $p/min_qty else 1}" step="{if ($p/step) then f:num($p/step) else 1}"/>
				<div class="hide_m">
					<xsl:if test="$zero">
						<xsl:if test="not($p/soon != '0')">Нет в наличии</xsl:if>
						<xsl:if test="$p/soon != '0'">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></xsl:if>
						<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>
					</xsl:if>
					<xsl:if test="not($zero) and not($p/is_service = '1')">
						В наличии: <strong><xsl:value-of select="concat($p/qty, ' ', $p/unit)"/></strong>
					</xsl:if>
				</div>
			</td>
			<td class="cart-item__price">
				<xsl:if test="not($zero)">
					<xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/>/<xsl:value-of select="$p/unit"/>
				</xsl:if>
				<xsl:if test="$zero">нет цены</xsl:if>
				<xsl:if test="$p/special_price = 'true' and not($zero)"><div>Спеццена</div></xsl:if>
				<xsl:if test="f:num($p/sec/discount_1) &gt; 0">
					<div class="sale">от <xsl:value-of select="$p/sec/limit_1"/>&#160;<xsl:value-of select="$p/unit"/> -
						<xsl:value-of select="$p/sec/discount_1"/>%<xsl:call-template name="BR"/>
						от <xsl:value-of select="$p/sec/limit_2"/>&#160;<xsl:value-of select="$p/unit"/> -
						<xsl:value-of select="$p/sec/discount_2"/>%
					</div>
				</xsl:if>
			</td>
			<td class="cart-item__sum">
				<xsl:if test="not($zero)">
					<xsl:value-of select="f:exchange(., 'sum', 0)"/>
					<span class="hide_m">&#160;<xsl:value-of select="if ($currency = 'BYN') then $BYN_cur else $currency"/></span>
				</xsl:if>
				<xsl:if test="$zero">нет цены</xsl:if>
			</td>
			<td class="cart-item__close">
				<a href="{if ($is_available) then delete_non_zero else delete_zero}">
					<img src="img/icon-close.png" alt=""/>
				</a>
			</td>
		</tr>
	</xsl:template>






	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CART_TOTAL">
		<form>
			<div class="cart-total">
				<div class="cart-total__text">
					<div>Итого: <span>187,72 руб.</span>
					</div>
					<div>Скидка: <span>5% - на товар не участвующий в спецпредложениях</span>
					</div>
					<div>Сумма скидки: <span>2,19 руб.</span>
					</div>
				</div>
				<div class="cart-total__sum">К оплате: 185,53 руб.</div>
				<div class="cart-total__warning">Реальная стоимость заказа может незначительно отличаться из-за округления цены в системе.</div>
				<a class="cart-total__link" href="">Правила предоставления скидок</a>
				<div class="cart-total__buttons">
					<button class="button">К оформлению</button>
				</div>
			</div>
		</form>
	</xsl:template>



	<xsl:template name="CONTENT_INNER">
		<div class="tabs">
			<div class="tabs__nav">
				<xsl:if test="$has_tab_cart">
					<a class="tab{' tab_active'[$tab_active = 'tab_cart']}" href="#tab_cart">
						<div class="tab__text">Корзина товаров</div>
						<div class="tab__qty"><xsl:value-of select="count($cart/bought[qty != '0'])"/></div>
					</a>
				</xsl:if>
				<xsl:if test="$has_tab_order">
					<a class="tab{' tab_active'[$tab_active = 'tab_order']}" href="#tab_order">
						<div class="tab__text">Позиции для запроса</div>
						<div class="tab__qty"><xsl:value-of select="count($cart/bought[qty_zero != '0'])"/></div>
					</a>
				</xsl:if>
				<a class="tab{' tab_active'[$tab_active = 'tab_personal']}" href="#tab_personal">
					<div class="tab__text">Персональный заказ</div>
					<div class="tab__qty"><xsl:value-of select="count($cart/custom_bought[nonempty = 'true'])"/></div>
				</a>
				<xsl:if test="$has_tab_fav">
					<a class="tab{' tab_active'[$tab_active = 'tab_favourite']}" href="#tab_favourite">
						<div class="tab__text">Избранное</div>
						<div class="tab__qty"><xsl:value-of select="count(page/product)"/></div>
					</a>
				</xsl:if>
			</div>
			<div class="tabs__content">
				<xsl:if test="$has_tab_cart">
					<div class="tab-container" id="tab_cart" style="{'display: none'[$tab_active != 'tab_cart']}">
						<form>
							<table class="cart-items">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="3">Описание</td>
										<td>Количество</td>
										<td>Цена, BYN</td>
										<td>Стоимость</td>
									</tr>
									<xsl:for-each-group select="$cart/bought[qty != '0']" group-by="type">
										<tr>
											<td colspan="7" style="background: #dddddd; font-size: 15px; padding: 5px 10px; font-weight: bold;">
												<xsl:value-of select="current-grouping-key()"/>
											</td>
											<tr><td colspan="7" style="padding-bottom: 15px;"></td></tr>
											<xsl:apply-templates select="current-group()"/>
										</tr>
									</xsl:for-each-group>
								</tbody>
							</table>
							<xsl:call-template name="CART_TOTAL"/>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_tab_order">
					<div class="tab-container" id="tab_order" style="{'display: none'[$tab_active != 'tab_order']}">
						<form>
							<table class="cart-items">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="3">Описание</td>
										<td>Количество</td>
										<td>Цена, BYN</td>
										<td>Стоимость</td>
									</tr>
									<xsl:for-each-group select="$cart/bought[qty_zero != '0']" group-by="type">
										<tr>
											<td colspan="7" style="background: #dddddd; font-size: 15px; padding: 5px 10px; font-weight: bold;">
												<xsl:value-of select="current-grouping-key()"/>
											</td>
											<tr><td colspan="7" style="padding-bottom: 15px;"></td></tr>
											<xsl:apply-templates select="current-group()"/>
										</tr>
									</xsl:for-each-group>
								</tbody>
							</table>
							<xsl:call-template name="CART_TOTAL"/>
						</form>
					</div>
				</xsl:if>
				<div class="tab-container" id="tab_personal" style="{'display: none'[$tab_active != 'tab_personal']}">
					<div class="personal-order">

						<xsl:for-each select="$cart/custom_bought">
							<xsl:sort select="position"/>
							<xsl:if test="position &lt; 5">
								<div class="personal-order__item personal">
									<div class="personal__mark">
										<input class="input" type="text" value="{mark}" name="{mark/@input}"/>
									</div>
									<div class="personal__type">
										<input class="input" type="text" value="{type}" name="{type/@input}"/>
									</div>
									<div class="personal__case">
										<input class="input" type="text" value="{case}" name="{case/@input}"/>
									</div>
									<div class="personal__qty">
										<input class="input" type="text" value="{qty}" name="{qty/@input}"/>
									</div>
									<div class="personal__link">
										<input class="input" type="text" value="{link}" name="{link/@input}"/>
									</div>
									<div class="personal__more">
										<input class="input" type="text" value="{extra}" name="{extra/@input}"/>
									</div>
								</div>
							</xsl:if>
						</xsl:for-each>
					</div>

				</div>
				<xsl:if test="$has_tab_fav">
					<div class="tab-container" id="tab_favourite" style="{'display: none'[$tab_active != 'tab_favourite']}">
						<xsl:if test="$view = 'table'">
							<div class="devices show_m">
								<div class="devices__wrap">
									<xsl:apply-templates select="page/product"/>
								</div>
							</div>
						</xsl:if>
						<xsl:if test="$view = 'list'">
							<table class="cart-items hide_m">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="2">Описание</td>
										<td>Количество</td>
										<td>Цена, BYN</td>
										<td> </td>
									</tr>
									<xsl:apply-templates select="page/product" mode="lines"/>
								</tbody>
							</table>
						</xsl:if>
						<xsl:call-template name="CART_TOTAL"/>
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script  type="text/javascript">
		$(document).ready(function() {
			$('.tab').click(function(e) {
				e.preventDefault();
				var a = $(this);
				$('.tab-container').hide();
				$('.tab-container' + a.attr('href')).show();
				$('.tab').removeClass('tab_active');
				a.addClass('tab_active');
			});
		});
		</script>
	</xsl:template>


</xsl:stylesheet>
