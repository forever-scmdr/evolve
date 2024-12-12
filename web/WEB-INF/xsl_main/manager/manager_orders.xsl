<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/utils.xsl"/>
	<xsl:import href="../common_page_base.xsl"/>
<!--	<xsl:import href="snippets/custom_blocks.xsl"/>-->
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template name="q_mark"><path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"></path></xsl:template>
	<xsl:variable name="main_styles" select="'css/styles_bek.css?version=1.65'"/>
	<xsl:variable name="products" select="page/product"/><!-- всегда пустой список, сделано ради единообразия -->


	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="top-info">
			<div class="container">
				<div class="top-info__wrap wrap" style="display: flex">
					<div class="top-info__content">
						<a href="">Заказы</a>
						<a href="">Список клиентов</a>
						<a href="">Лог оператора</a>
						<p>&#160;</p>
					</div>
					<div id="personal_desktop_login" ajax-href="{//page/personal_ajax_link}" ajax-show-loader="no">
						<a href="{page/login_link}" class="icon-link">
							<div class="icon">
								<img src="img/icon-lock.svg" alt="" />
							</div>
							<span class="icon-link__item">Вход / Регистрация</span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="MAIN_CONTENT">

		<!-- (начало) Список клиентов менеджера -->

		<div class="filter filter_section">
			<a href="#" onclick="$('.filter_extra').toggle();$('#filters_container').slideToggle(200);return false;" class="icon-link filter__button button">
				<div class="icon"><img src="img/icon-gear.svg" alt=""/></div>
				<span class="icon-link__item filter_extra" style="">Показать список клиентов</span>
				<span class="icon-link__item filter_extra" style="display: none;">Скрыть подбор по клиентам</span>
			</a>

			<input class="input header-search__input" type="text" placeholder="ИНН клиента" value="" style="margin-left: 40px;"/>
			<input class="input header-search__input" type="text" placeholder="Название компании" value="" autofocus=""/>
			<input class="input header-search__input" type="text" placeholder="Номер заказа" value="" autofocus=""/>
			<button class="button header-search__button" type="submit">Найти</button>
			<button class="button header-search__button" type="submit" style="float: right;">Обновить страницу</button>

			<form method="post">
				<div class="" style="display: none;" id="filters_container">
					<div class="filter__item active checkgroup">
						<div class="filter__title">Клиенты</div>
						<div style="display: flex;">
							<xsl:variable name="third_size" select="ceiling(count(page/user_jur) div 3)"/>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &lt;= xs:integer($third_size)]"/>
							</div>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &gt; xs:integer($third_size) and position() &lt;= $third_size * 2]"/>
							</div>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &gt; $third_size * 2]"/>
							</div>
						</div>
					</div>
					<div class="filter__actions"><button class="button button_2" type="submit">Показать результат</button><button class="button button_2" onclick="location.href = '/soldering_desoldering_rework_stations/?show_filter=yes'; return false;">Сбросить</button></div>
				</div>
			</form>
		</div>

		<!-- (конец) Список клиентов менеджера -->

		<!-- (начало) фильтр заказов -->

		<div class="search_filter">
			<div class="item">
				<div class="box" style="width: 300px">
					<div class="title">Дата заказа</div>
					<div>
						<div class="value"><label>от<input type="date" name="" value="{f:format_date_us(current-date())}" style="width: 85%"/></label></div>
					</div>
					<div>
						<div class="value">до<input type="date" name="" value="{f:format_date_us(current-date())}" style="width: 85%"/></div>
					</div>
				</div>
			</div>

			<div class="item">
				<div class="box">
					<div class="title">Статус оплаты</div>
					<div class="chbox">
						<div class="value">Любой</div>
						<div class="options"><label class="option"><input type="checkbox" name="" value=""/>Счет выстален</label>
							<label class="option"><input type="checkbox" name="" value=""/>Оплачен</label>
							<label class="option"><input type="checkbox" name="" value=""/>Оплачен (99%)</label>
							<label class="option"><input type="checkbox" name="" value=""/>Не Оплачен</label>
							<label class="option"><input type="checkbox" name="" value=""/>Частично</label>
						</div>
					</div>
				</div>
			</div>

			<div class="item">
				<div class="box" style="width: 300px">
					<div class="title">Дата оплаты</div>
					<div>
						<div class="value"><label>от<input type="date" name="" value="" style="width: 85%"/></label></div>
					</div>
					<div>
						<div class="value">до<input type="date" name="" value="" style="width: 85%"/></div>
					</div>
				</div>
			</div>


			<div class="item">
				<div class="box">
					<div class="title">Статус заказа</div>
					<div class="chbox">
						<div class="value">Любой</div>
						<div class="options"><label class="option"><input type="checkbox" name="" value=""/>Новый</label>
							<label class="option"><input type="checkbox" name="" value=""/>Отправлен в 1С</label>
							<label class="option"><input type="checkbox" name="" value=""/>У поставщика</label>
							<label class="option"><input type="checkbox" name="" value=""/>Просрочен</label>
							<label class="option"><input type="checkbox" name="" value=""/>Отменен</label>
							<label class="option"><input type="checkbox" name="" value=""/>Возврат</label>
							<label class="option"><input type="checkbox" name="" value=""/>Отгружен</label>
						</div>
					</div>
				</div>
			</div>
			<div class="item">
				<div class="box">
					<div class="title">Статус отгрузки</div>
					<div class="chbox">
						<div class="value">Любой</div>
						<div class="options"><label class="option"><input type="checkbox" name="" value=""/>Не отгружен</label>
							<label class="option"><input type="checkbox" name="" value=""/>Отгружен частично</label>
							<label class="option"><input type="checkbox" name="" value=""/>Отгружен полностью</label>
						</div>
					</div>
				</div>
			</div>
			<div class="item"><a href="#" class="button button_request clear_filter_button">Снять фильтры</a></div>
		</div>

		<!-- (конец) фильтр заказов -->

		<div class="content">
			<div class="container">
				<div class="content__wrap">
					<div class="content__main no-left-col">

						<!-- (начало) заказы -->
						<!-- (начало) заголовки заказов -->

						<div class="main_tab_header">
							<div class="orders__item_tab past-order">
								<div class="past-order__info">
									<div class="past-order__title"><input type="checkbox" class="check_all" name="" value=""/>Номер заказа</div>
								</div>

								<div class="past-order__info_datemain">
									<div class="past-order__title">Дата заказа</div>
								</div>

								<div class="past-order__info_pay">
									<div class="past-order__title">Оплата</div>
								</div>

								<div class="past-order__info_datepay">
									<div class="past-order__title">Дата оплаты</div>
									<div class="past-order__date">Оплаченная сумма</div>
								</div>

								<div class="past-order__info_status">
									<div class="past-order__title">Статус заказа</div>
								</div>

								<div class="past-order__info_gruz">
									<div class="past-order__title">Статус отгрузки</div>
								</div>

								<div class="past-order__info_firm">
									<div class="past-order__title">Клиент/ИНН</div>
								</div>

								<div class="past-order__price">
									<div class="past-order__sum"><button class="button past-order__button submit_all_again" onclick="; return false">Отправить в 1С</button></div>
								</div>
							</div>
						</div>

						<!-- (конец) заголовки заказов -->

						<!-- (начало) сами заказы -->

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
										<div class="past-order__title"><input type="checkbox" class="check_all" name="" value=""/><a href="#" class="order_toggle">Заказ №<xsl:value-of select="num"/></a></div>
										<div class="past-order__redact">Изменен 11.11.2024 17:26 TODO</div>
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
										<div class="past-order__title"><a href="#" class="order_toggle">ООО "РЕГУЛ-ГАЗ"</a>
											<!-- Вызов информации по клиенту начало -->
											<svg onclick="infobox(1)" class="infobox" focusable="false" aria-hidden="true" viewBox="0 0 24 24" data-testid="help-popup-trigger-icon">
												<path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"></path>
											</svg>
											<!-- Вызов информации по клиенту конец --></div>
										<div class="past-order__date">ИНН: 7716529550</div>
									</div>

									<div class="past-order__price">
										<div class="past-order__sum"><xsl:value-of select="sum"/> руб</div>
										<div class="past-order__qty">Строки: <xsl:value-of select="count(bought)"/></div>
									</div>
									<div class="past-order__product past-product" style="display: none">
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
							</xsl:for-each>
						</div>

						<!-- (конец) сами заказы -->

						<!-- (конец) заказы -->


					</div>
				</div>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CONTENT" >



	</xsl:template>


	<xsl:template match="user_jur">
		<div class="filter__value">
			<label>
				<input name="val_{@id}" type="checkbox" value="{organization}"/>&#160;<xsl:value-of select="organization"/>
				<!-- Вызов информации по клиенту начало -->
				<svg onclick="infobox('{@id}')" class="infobox" focusable="false" aria-hidden="true" viewBox="0 0 24 24" data-testid="help-popup-trigger-icon">
					<xsl:call-template name="q_mark"/>
				</svg>
				<!-- Вызов информации по клиенту конец -->
				<div class="infobox_modal infobox_{@id}">
					<div class="text"><a class="popup__close" onclick="infobox('close');">X</a>
						<div>
							<p><strong>Анкета заказчика</strong></p>
							<p><strong>E-mail/логин:</strong> <a href="mailto:{email}"><xsl:value-of select="email"/></a></p>
							<p><strong>ИНН:</strong> <xsl:value-of select="inn"/></p>
							<p><strong>Наименование организации:</strong> <xsl:value-of select="organization"/></p>
							<p><strong>КПП:</strong> <xsl:value-of select="kpp"/></p>
							<p><strong>Адрес:</strong> <xsl:value-of select="address"/></p>
							<p><strong>E-mail организации:</strong> <a href="mailto:{corp_email}"><xsl:value-of select="corp_email"/></a></p>
							<p><strong>Телефон/факс:</strong> <xsl:value-of select="phone"/></p>
							<p><strong>Руководитель:</strong> <xsl:value-of select="boss"/></p>
							<p><strong>Должность руководителя:</strong> <xsl:value-of select="boss_position"/></p>
						</div>
					</div>
				</div>
			</label>
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

</xsl:stylesheet>
