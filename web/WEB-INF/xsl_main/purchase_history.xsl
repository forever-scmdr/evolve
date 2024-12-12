<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="bom_ajax.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="hide_side_menu" select="true()"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
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
				<div class="orders__item past-order" id="pur_{@id}">
					<form method="post" action="page/validate_bom_link" id="ph_search_{@id}" style="display: none" class="search_repeat">
						<textarea name="q" style="display: none">
							<xsl:for-each select="bought">
								<xsl:value-of select="name"/><xsl:text> </xsl:text><xsl:value-of select="qty"/><xsl:text>&#xa;</xsl:text>
							</xsl:for-each>
						</textarea>
					</form>
					<div class="past-order__info">
						<div class="past-order__title"><a href="#" class="order_toggle">Заказ №<xsl:value-of select="num"/></a></div>
						<div class="past-order__date"><xsl:value-of select="date"/></div>
						<div class="past-order__title"><xsl:value-of select="status"/></div>
					</div>
					<div class="past-order__price">
						<div class="past-order__sum"><xsl:value-of select="sum"/> руб</div>
						<div class="past-order__qty">Позиций: <xsl:value-of select="qty"/></div>
					</div>
					<div class="past-order__action" style="display: none">
						<button class="button past-order__button submit_all_again" style="margin-right: 10px" onclick="submitBomSave('#pur_{@id}')">Сохранить список BOM</button>
						<button class="button past-order__button submit_all_again" onclick="repeatSearch('#ph_search_{@id}'); return false">Повторить поиск</button>
					</div>
					<!-- OLD
					<xsl:for-each select="bought">
						<xsl:variable name="code" select="code"/>
						<xsl:variable name="outer" select="if (outer_product) then parse-xml(concat('&lt;prod&gt;', outer_product, '&lt;/prod&gt;')) else none"/>
						<xsl:variable name="po" select="$outer/prod/product"/>
						<xsl:variable name="prod" select="if ($po) then $po else $products[code = $code]"/>
						<div class="past-order__product past-product" style="display: none">
							<div class="past-product__image">
								<xsl:if test="$prod"><img src="{$prod/@path}{$prod/main_pic}" alt="" /></xsl:if>
							</div>
							<div class="past-product__info">
								<xsl:if test="$prod and not($po)">
									<a href="{$prod/show_product}"><xsl:value-of select="$prod/name"/></a>
								</xsl:if>
								<xsl:if test="not($prod)">
									<xsl:value-of select="name"/>
								</xsl:if>
								<xsl:if test="$prod and $po">
									<xsl:value-of select="$prod/name"/>
								</xsl:if>
								<div class="past-product__artnumber">Производитель: <xsl:value-of select="$prod/vendor"/></div>
								<div class="past-product__old-price">
									<span>Поставщик: <xsl:value-of select="$prod/category_id"/></span>
								</div>
								<div class="past-product__old-price">
									<span>Описание: <xsl:value-of select="$prod/description"/></span>
								</div>
								<div class="past-product__old-price">
									<span style="margin-right: 15px;">Цена: <xsl:value-of select="price"/> руб. </span>
									<span style="margin-right: 15px;">Кол-во: <xsl:value-of select="qty"/> шт. </span>
									<span>Сумма: <xsl:value-of select="sum"/> руб. </span>
								</div>
								<div class="past-product__old-price">
									<div><a onclick="showDetails('product_ajax?prod={normalize-space($po/name)}')" >Полное описание</a></div>
								</div>
							</div>
							<xsl:if test="$prod">
								<xsl:variable name="has_price" select="$prod/price and $prod/price != '0'"/>
								<div class="past-product__action">
									<div class="past-product__price"><xsl:if test="$has_price"><xsl:value-of select="$prod/price"/> руб.</xsl:if></div>
									<div id="cart_list_{$prod/code}" class="product_purchase_container">
										<form action="{$prod/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$prod/code}">
											<xsl:if test="$has_price">
												<input class="input" type="hidden" name="qty" value="{qty}" min="0"/>
												<button type="submit" class="button">Повторить запрос</button>
											</xsl:if>
											<xsl:if test="not($has_price)">
												<input class="input" type="hidden" name="qty" value="{qty}" min="0"/>
												<button class="button button_not-available" type="submit">Повторить запрос</button>
											</xsl:if>
										</form>
									</div>
								</div>
							</xsl:if>
						</div>
					</xsl:for-each>
					-->
					<div class="past-order__product past-product" style="display: none">
						<div class="div-tr thead border-bottom">
							<div class="div-td">Наименование</div>
							<div class="div-td">Производитель</div>
							<div class="div-td">Поставщик</div>
							<div class="div-td">Описание</div>
							<div class="div-td">Цена</div>
							<div class="div-td"></div>
						</div>
					</div>
					<xsl:for-each select="bought">
						<xsl:variable name="code" select="code"/>
						<xsl:variable name="outer_escaped" select="replace(outer_product, '&amp;', '&amp;amp;')"/>
						<xsl:variable name="outer" select="if (outer_product) then parse-xml(concat('&lt;prod&gt;', $outer_escaped, '&lt;/prod&gt;')) else none"/>
						<xsl:variable name="po" select="$outer/prod/product"/>
						<xsl:variable name="prod" select="if ($po) then $po else $products[code = $code]"/>
						<div class="past-order__product past-product" style="display: none">
							<div class="div-tr">
								<div class="div-td">
									<div class="thn">Наименование</div>
									<div class="thd">
										<xsl:if test="$prod and not($po)">
											<a href="{$prod/show_product}"><xsl:value-of select="$prod/name"/></a>
										</xsl:if>
										<xsl:if test="not($prod)">
											<xsl:value-of select="name"/>
										</xsl:if>
										<xsl:if test="$prod and $po">
											<xsl:value-of select="$prod/name"/>
										</xsl:if>
									</div>
								</div>
								<div class="div-td">
									<div class="thn">Производитель</div>
									<div class="thd">
										<xsl:value-of select="$prod/vendor"/>
									</div>
								</div>
								<div class="div-td">
									<div class="thn">Поставщик</div>
									<div class="thd">
										<xsl:value-of select="$prod/category_id"/>
									</div>
								</div>
								<div class="div-td">
									<div class="thn">Описание</div>
									<div class="thd">
										<div><xsl:value-of select="$prod/description"/></div>
										<div><a onclick="showDetails('product_ajax?prod={normalize-space($po/name)}')" >Полное описание</a></div>
									</div>
								</div>
								<div class="div-td">
									<div class="thn">Цена</div>
									<div class="thd">
										<div>Цена: <xsl:value-of select="price"/> руб. </div>
										<div>Кол-во: <xsl:value-of select="qty"/> шт. </div>
										<div>Сумма: <xsl:value-of select="sum"/> руб. </div>
									</div>
								</div>
								<div class="div-td">
									<div class="thn"></div>
									<div class="thd">
										<a class="button" href="{repeat_search}">Найти предложения</a>
									</div>
								</div>
							</div>
						</div>
					</xsl:for-each>

				</div>
			</xsl:for-each>
		</div>
		<xsl:call-template name="SAVE_BOM_FORM"/>
		
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