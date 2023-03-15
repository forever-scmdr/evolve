<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"

		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="msg" select="page/variables/message"/>
	<xsl:variable name="rslt" select="page/variables/result"/>

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
			<xsl:apply-templates select="page/purchase"/>
		</div>
		
	</xsl:template>


	<xsl:template match="purchase">
		<div class="orders__item past-order result" id="order-{@id}">
			<div class="past-order__info">
				<div class="past-order__title"><a href="#" class="order_toggle">Заказ №<xsl:value-of select="num"/></a></div>
				<div class="past-order__date"><xsl:value-of select="date"/></div>
			</div>
			<div class="past-order__price">
				<div class="past-order__sum"><xsl:value-of select="sum_discount"/> руб</div>
				<div class="past-order__qty">Позиций: <xsl:value-of select="sum(bought/f:num(qty_total))"/></div>
			</div>

			<xsl:if test="$msg">
				<div class="payments" onclick="$(this).remove()" style="{if($rslt = 'success') then 'background: lightgreen;' else 'background: yellow;'}">
					<div class="past-order__product past-product" style="padding: 0 10px;">
						<div class="past-product__image"></div>
						<div class="past-order__title"><xsl:value-of select="/page/variables/message"/></div>
					</div>
				</div>
			</xsl:if>

			<div class="payments" id="payments-{@id}">
				<div class="past-order__product past-product" style="{'display: none;'[not($msg)]}">
					<div class="past-product__image"></div>
					<div class="past-order__title">Расписание платежей</div>
				</div>
				<xsl:for-each select="payment_stage">
					<div class="past-order__product past-product" style="{'display: none;'[not($msg)]}">
						<div class="past-product__image"></div>
						<div class="payment">
							<form method="post" action="{update}" id="pmt{@id}">
								<input type="date" name="date" value="{f:date_value(date)}" data-old="{f:date_value(date)}"/>

								<input type="text" name="sum" value="{sum}" data-old="{sum}"/>
								<span class="percent">
									<b><xsl:value-of select="percent"/>%&#160;</b>
								</span>
								<button class="button" style="background: #bbb;" onclick="postForm('pmt{@id}', 'payments-{@id}'); return false;">Сохранить</button>
								<a class="delete" onclick="insertAjax('{delete}', 'payments-{@id}')">×</a>
							</form>
						</div>
					</div>
				</xsl:for-each>
			</div>
			<div class="payments">
				<div class="past-order__product past-product" style="{'display: none;'[not($msg)]}">
					<div class="past-product__image"></div>
					<button style="background: #bbb; padding: 8px 16px; color: #fff;" onclick="createPayment('payments-{@id}', '{create_payment}');">Добавить платеж</button>
					<!--								<button style="background: #bbb; padding: 8px 16px; color: #fff;" onclick="createPayment('payments-{@id}'); populatePayment('payments-{@id}');">Копировать платеж</button>-->
				</div>
			</div>


			<script type="text/javascript">
				function createPayment(id, action){
				$el = $('#'+id);
				<xsl:text disable-output-escaping="yes">

							div1 = $('&lt;div&gt;', {"class" : "past-order__product past-product"});
							div1.append($('&lt;div&gt;',{"class": "past-product__image"}));

							div =  $('&lt;div&gt;', {"class" : "payment"});
							now = new Date()*1;
							pid = "pmt"+now;

							form = $('&lt;form&gt;', {"method" : "post", "action": action, "id": pid});
							form.append($('&lt;input&gt;', {"type": "date", "name": "date"}));
							form.append($('&lt;span&gt;', {"class": "percent"}));
							form.append($('&lt;input&gt;', {"type": "text", "name": "sum", placeholder: "введите сумму"}));
							form.append($('&lt;button&gt;', {"text": "Сохранить", "class": "button", "style": "background: #e52d2a;", "onclick": "postForm('"+pid+"','"+id+"'); return false;"}));
							div.append(form);
							div1.append(div);
							$el.append(div1);
							</xsl:text>
				}
			</script>

			<xsl:for-each select="bought">
				<xsl:variable name="code" select="code"/>
				<xsl:variable name="prod" select="$products[code = $code]"/>
				<xsl:variable name="is_complex" select="f:num(is_complex) = 1"/>

				<div class="past-order__product past-product" style="{'display: none;'[not($msg)]}">
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
							<span>Кол-во: <xsl:value-of select="qty_total"/> шт.</span>
							<span>Сумма: <xsl:value-of select="sum"/> руб.</span>
						</div>

						<xsl:if test="$is_complex">
							<div class="past-product__old-price shipment_date" id="spdt{@id}">
								<xsl:if test="deadline_date != ''">
									<div class="unia_date">
										Дата поставки: <b><xsl:value-of select="substring(deadline_date, 1, 10)"/></b>
										<xsl:if test="not(status = 'date_confirmed')">
											&#160;<a href="{confirm_date}" ajax="true" title="Меня устраивает эта дата поставки">Подтвердить дату</a>
										</xsl:if>
									</div>
								</xsl:if>
								<xsl:if test="not(status = 'date_confirmed')">
									<div style="margin-top: 5px;">
										<form method="post" action="{update_bought}" id="dtf{@id}">
											<label>
												Предложить дату поставки
												<input type="date" name="date" value="{f:date_value(proposed_dealer_date)}"/>
											</label>
											<button class="button" onclick="postForm('dtf{@id}', 'spdt{@id}'); return false;">Отправить запрос</button>
										</form>
									</div>
								</xsl:if>
							</div>
						</xsl:if>

						<xsl:if test="$is_complex">
							<div class="options">
								<div class="options-header">Дополнительные модули</div>
								<xsl:for-each select="pseudo_option">
									<div class="option-li">
										<xsl:value-of select="concat(name, ': ')"/>
										<span class="price">
											<xsl:value-of select="f:format_currency_thousands(f:num(price))" />
										</span>
									</div>
								</xsl:for-each>
							</div>
						</xsl:if>

					</div>
					<xsl:if test="$prod">
						<xsl:variable name="has_price" select="$prod/price and $prod/price != '0'"/>
						<div class="past-product__action">
							<div class="past-product__price"><xsl:if test="$has_price"><xsl:value-of select="$prod/price"/> руб.</xsl:if></div>
						</div>
					</xsl:if>
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
			});
		</script>
	</xsl:template>

	<xsl:function name="f:date_value" as="xs:string?">
		<xsl:param name="date"/>
		<xsl:if test="$date and string-length($date) &gt; 9">
			<xsl:value-of select="string-join((substring($date, 7, 4), substring($date, 4, 2), substring($date, 1, 2)),'-')"/>
		</xsl:if>
	</xsl:function>

</xsl:stylesheet>