<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">Заказы</div>
	</xsl:template>
	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<div class="orders">
			<xsl:for-each select="page/purchase">
				<div class="orders__item past-order">
					<div class="past-order__info">
						<div class="past-order__title"><a href="#" class="order_toggle">Заказ №<xsl:value-of select="num"/></a></div>
						<div class="past-order__date"><xsl:value-of select="date"/></div>
					</div>
					<div class="past-order__price">
						<div class="past-order__sum"><xsl:value-of select="sum"/> руб</div>
						<div class="past-order__qty">Позиций: <xsl:value-of select="qty"/></div>
					</div>
					<div class="past-order__action" style="display: none">
						<button class="button past-order__button submit_all_again"><xsl:value-of select="$to_cart_available_label"/></button>
					</div>
					<xsl:for-each select="bought">
						<xsl:variable name="code" select="code"/>
						<xsl:variable name="prod" select="$products[code = $code]"/>
						<div class="past-order__product past-product" style="display: none">
							<div class="past-product__image">
								<xsl:if test="$prod"><img src="{$prod/@path}{$prod/main_pic}" alt="" /></xsl:if>
							</div>
							<div class="past-product__info">
								<xsl:if test="$prod">
									<a href="{$prod/show_product}"><xsl:value-of select="$prod/name"/></a>
								</xsl:if>
								<xsl:if test="not($prod)">
									<xsl:value-of select="name"/>
								</xsl:if>
								<div class="past-product__artnumber">Артикул: <xsl:value-of select="if ($prod) then $prod/code else code"/></div>
								<div class="past-product__old-price">
									<span>Цена: <xsl:value-of select="price"/> руб.</span>
									<span>Кол-во: <xsl:value-of select="qty"/> шт.</span>
									<span>Сумма: <xsl:value-of select="sum"/> руб.</span>
								</div>
							</div>
							<xsl:if test="$prod">
								<xsl:variable name="has_price" select="$prod/price and $prod/price != '0'"/>
								<div class="past-product__action">
									<div class="past-product__price"><xsl:if test="$has_price"><xsl:value-of select="$prod/price"/> руб.</xsl:if></div>
									<div id="cart_list_{$prod/code}" class="product_purchase_container">
										<form action="{$prod/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$prod/code}">
											<xsl:if test="$has_price">
												<input class="input" type="number" name="qty" value="{qty}" min="0"/>
												<button type="submit" class="button"><xsl:value-of select="$to_cart_available_label"/></button>
											</xsl:if>
											<xsl:if test="not($has_price)">
												<input class="input" type="number" name="qty" value="1" min="0"/>
												<button class="button button_not-available" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
											</xsl:if>
										</form>
									</div>
								</div>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</xsl:for-each>
		</div>
		
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			$(document).ready(function() {
				$('.order_toggle').click(function(event) {
					event.preventDefault();
					var order = $(this).closest('.orders__item');
					order.toggleClass('orders__item_active');
					order.find('.past-order__action').toggle(0);
					order.find('.past-order__product').toggle('fade', 200); // 'blind'
				});

				$('.submit_all_again').click(function(event) {
					event.preventDefault();
					$(this).closest('.orders__item').find('input[type=submit]').trigger('click');
				});
			});
		</script>
	</xsl:template>

</xsl:stylesheet>