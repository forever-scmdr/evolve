<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Заказы</h1>
		<div class="page-content m-t">
			<div class="orders-list">
				<xsl:for-each select="page/purchase">
					<div class="order">
						<div class="short-info">
							<div><a href="#" class="order_toggle">Заказ № <xsl:value-of select="num"/></a><span>от <xsl:value-of select="date"/></span></div>
							<div><strong><xsl:value-of select="sum"/> р.</strong><span><xsl:value-of select="qty"/> позиций</span></div>
							<div class="order-buttons" style="display: none">
								<a href="#" class="button desktop submit_all_again">Повторить заказ</a>
								<a href="#" class="button mobile submit_all_again"><i class="fas fa-redo"></i></a>
							</div>
						</div>
						<div class="order-items" style="display: none">
							<div class="order-item desktop">
								<div class="image-container"></div>
								<div class="info-container">
									<div>Название</div>
									<div>Кол-во</div>
									<div>Цена за ед.</div>
									<div>Сумма</div>
									<div>
										<span>Текущая цена</span>
									</div>
								</div>
							</div>
							<xsl:for-each select="bought">
								<xsl:variable name="code" select="code"/>
								<xsl:variable name="prod" select="$products[code = $code]"/>
								<div class="order-item">
									<div class="image-container">
										<xsl:if test="$prod">
											<a href="{$prod/show_product}"><img src="{$prod/@path}{$prod/main_pic}" alt=""/></a>
										</xsl:if>
									</div>
									<div class="info-container">
										<xsl:if test="$prod">
											<div><a href="{$prod/show_product}"><xsl:value-of select="concat($prod/name, ' ', $prod/code)"/></a></div>
										</xsl:if>
										<xsl:if test="not($prod)">
											<div><a><xsl:value-of select="concat(name, ' ', code)"/></a></div>
										</xsl:if>
										<div><xsl:value-of select="qty"/> шт.<span class="mobile">&#160;×&#160;</span></div>
										<div><xsl:value-of select="price"/> р.<span class="mobile">&#160;=&#160;</span></div>
										<div><xsl:value-of select="sum"/> р.</div>
										<div>
											<xsl:if test="$prod">
												<xsl:variable name="has_price" select="$prod/price and $prod/price != '0'"/>
												<div><xsl:if test="$has_price"><xsl:value-of select="$prod/price"/> р.</xsl:if></div>
												<div id="cart_list_{$prod/code}" class="product_purchase_container">
													<form action="{$prod/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$prod/code}">
														<xsl:if test="$has_price">
															<input type="number" name="qty" value="{qty}" min="0"/>
															<input type="submit" value="В корзину"/>
														</xsl:if>
														<xsl:if test="not($has_price)">
															<input type="number" name="qty" value="1" min="0"/>
															<input type="submit" class="not_available" value="Под заказ"/>
														</xsl:if>
													</form>
												</div>
											</xsl:if>
										</div>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			$(document).ready(function() {
				$('.order_toggle').click(function(event) {
					event.preventDefault();
					var order = $(this).closest('.order');
					order.toggleClass('active');
					order.find('.order-items').toggle('blind', 200);
					order.find('.order-buttons').toggle();
				});

				$('.submit_all_again').click(function(event) {
					event.preventDefault();
					$(this).closest('.order').find('input[type=submit]').trigger('click');
				});
			});
		</script>
	</xsl:template>

</xsl:stylesheet>