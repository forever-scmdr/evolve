<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../snippets/product.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="currencies" select="page/currencies"/>
	<xsl:variable name="queries" select="page/command/product_list/result/query"/>

	<xsl:variable name="search_result_el" select="page/command/product_list/result"/>
	<xsl:variable name="result_queries" select="$search_result_el/query"/>
	<xsl:variable name="products" select="$result_queries/product"/>
	<xsl:variable name="query" select="$p/name"/>
	<xsl:variable name="is_not_ajax" select="not(page/@name = 'manager_order_ajax')"/>


	<xsl:template match="/">
		<xsl:for-each select="page/user_jur/purchase">
			<div class="result" id="pur_{@id}" mode="replace">
				<xsl:apply-templates select="."/>
			</div>
		</xsl:for-each>
	</xsl:template>



	<xsl:template match="purchase">
		<div class="orders__item past-order" id="pur_{@id}" mode="replace">
			<div class="past-order__info">
				<div class="past-order__title"><input type="checkbox" class="check_all" name="" value=""/><a href="#" class="order_toggle">Заказ №<xsl:value-of select="num"/></a></div>
				<div class="past-order__redact">Изменен 11.11.2024 17:26</div>
			</div>
			<div class="past-order__info_datemain">
				<div class="past-order__title"><a href="#" class="order_toggle"><xsl:value-of select="date"/></a></div>
			</div>

			<div class="past-order__info_pay">
				<div class="past-order__title"><a href="#" class="order_toggle">Оплачен</a></div>
				<div class="past-order__date"><a href="#">Скачать счет</a></div>
			</div>

			<div class="past-order__info_datepay">
				<div class="past-order__title"><a href="#" class="order_toggle">06.12.2024 11:20</a></div>
				<div class="past-order__date">112 013 156,79 руб</div>
			</div>

			<div class="past-order__info_status">
				<div class="past-order__title"><a href="#" class="order_toggle">Отправлен в 1С</a></div>
				<div class="past-order__date">07.12.2024 18:43</div>
			</div>

			<div class="past-order__info_gruz">
				<div class="past-order__title"><a href="#" class="order_toggle">Отгружен частично</a></div>
				<div class="past-order__date">11.11.2024 14:50</div>
			</div>

			<div class="past-order__info_firm">
				<xsl:variable name="customer" select=".."/>
				<div class="past-order__title"><a href="#" class="order_toggle"><xsl:value-of select="$customer/organization"/></a>
					<!-- Вызов информации по клиенту начало -->
					<svg onclick="infobox('{$customer/@id}')" class="infobox" focusable="false" aria-hidden="true" viewBox="0 0 24 24" data-testid="help-popup-trigger-icon">
						<path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"></path>
					</svg>
					<!-- Вызов информации по клиенту конец --></div>
				<div class="past-order__date">ИНН: <xsl:value-of select="$customer/inn"/></div>
			</div>

			<div class="past-order__price">
				<div class="past-order__sum"><xsl:value-of select="sum"/> руб</div>
				<div class="past-order__qty">Строки: <xsl:value-of select="count(bought)"/></div>
			</div>
			<div class="past-order__product past-product" style="display: none">
				<div class="past-order__action" style="display: none; padding: 5px;">
					<button class="button past-order__button" style="margin-right: 10px" onclick="">Отправить в 1С</button>
					<button class="button past-order__button extra_query_toggle" style="margin-right: 10px">Добавить позиции</button>
					<button class="button_alt past-order__button extra-query extra_query_button" style="margin-right: 10px">Найти</button>
					<button class="button_alt past-order__button extra_buy_button" action="{add_to_purchase}">Добавить отмеченное</button>
					<a class="button" popup="modal_popup" href="feedback_ajax"  style="float: right;">Отменить заказ</a>
				</div>
			</div>
			<div class="past-order__product extra-query" style="display: none; padding-top: 5px; padding-bottom: 5px">
				<form method="post" action="{post_extra_query}" id="extra_search_form{@id}" class="extra_query_form">
					<textarea  class="input header-search__input" placeholder="Введите запрос. ОБЯЗАТЕЛЬНО С КОЛИЧЕСТВОМ через пробел"
							   autocomplete="off" name="q" autofocus="" style="width:100%; height: 100px;">
					</textarea>
				</form>
			</div>
			<div class="past-order__product extra-query" style="padding-bottom: 5px">
				<div id="search_bom_ajax{@id}">
				</div>
			</div>

			<div class="past-order__product past-product" style="display: none; padding-top: 5px">
				<div class="div-tr thead border-bottom">
					<div class="div-td">Наименование</div>
					<div class="div-td">Производитель</div>
					<div class="div-td">Площадка</div>
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
							<div class="thn">Площадка</div>
							<div class="thd">
								<xsl:value-of select="$prod/category_id"/>
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
	</xsl:template>


</xsl:stylesheet>