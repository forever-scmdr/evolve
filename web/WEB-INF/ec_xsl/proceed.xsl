<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление заказа'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="page/user_jur//@validation-error"/>
	<xsl:variable name="phys_reg" select="page/phys"/>
	<xsl:variable name="jur_reg" select="page/jur"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> &gt;
				<a href="{page/cart_link}">Изменить заказ</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Анкета покупателя</h1>

		<div class="page-content m-t">
			<xsl:if test="$message">
				<div class="alert alert-danger">
					<h4>Ошибка</h4>
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="{'active'[not($is_jur)]}"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>
				<li role="presentation" class="{'active'[$is_jur]}"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">


				<div role="tabpanel" class="tab-pane{' active'[not($is_jur)]}" id="tab_phys">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<div class="form-group">
							<label>Ваше имя *:</label>
							<input type="text" class="form-control" name="{$inp/name/@input}" value="{if($inp/name != '') then $inp/name else $phys_reg/name}" error="{$inp/name/@validation-error}"/>
						</div>
						<div class="form-group">
							<label for="">Адрес:</label>
							<input type="text" class="form-control" name="{$inp/address/@input}" value="{if($inp/address != '') then $inp/address else $phys_reg/address}" error="{$inp/address/@validation-error}"/>
						</div>
						<div class="form-group">
							<label>Способ доставки <a href="oplata_i_dostavka">Подробнее об условиях доставки и оплаты</a></label>
							<select class="form-control" name="{$inp/ship_type/@input}" value="{$inp/ship_type}" error="{$inp/ship_type/@validation-error}">
								<option>Самовывоз г.Минск ул.Толбухина 12а</option>
								<option>Самовывоз г.Минск ул.Я.Коласа 30</option>
								<option>Самовывоз г.Минск ул.Притыцкого 42</option>
								<option>Доставка курьером</option>
								<option>Белпочта</option>
								<option>Доставка AutoLight EXPRESS</option>
							</select>
						</div>
						<div class="form-group">
							<label>Способ оплаты</label>
							<select class="form-control" name="{$inp/pay_type/@input}" value="{$inp/pay_type}" error="{$inp/pay_type/@validation-error}">
								<option>Наличные</option>
								<option>Наложенным платежом</option>
								<option>Оплата онлайн</option>
							</select>
						</div>
						<div class="form-group">
							<label>Мобильный телефон *:</label>
							<input type="text" class="form-control" name="{$inp/phone/@input}" value="{if($inp/phone != '') then $inp/phone else $phys_reg/phone}" error="{$inp/phone/@validation-error}"/>
						</div>
						<div class="form-group">
							<label>Электронная почта *:</label>
							<input type="text" class="form-control" name="{$inp/email/@input}" value="{if($inp/email != '') then $inp/email else $phys_reg/email}" error="{$inp/email/@validation-error}"/>
						</div>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>



						<!--
						<div class="checkbox">
							<label>
								<input type="checkbox" value=""/> зарегистрироваться на сайте
							</label>
						</div>
						-->
						<input type="submit" class="button" value="Отправить заказ"/>
					</form>
				</div>


				<div role="tabpanel" class="tab-pane{' active'[$is_jur]}" id="tab_jur">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<div class="form-group">
							<label>Наименование организации *:</label>
							<input type="text" class="form-control" name="{$inp/organization/@input}"
							       value="{if($inp/organization != '') then $inp/organization else $jur_reg/organization}" error="{$inp/organization/@validation-error}"/>
						</div>
						<div class="form-group">
							<label for="">Мобильный телефон *:</label>
							<input type="text" class="form-control" name="{$inp/phone/@input}" value="{if($inp/phone != '') then $inp/phone else $jur_reg/phone}"/>
						</div>
						<div class="form-group">
							<label>Способ доставки <a href="oplata_i_dostavka">Подробнее об условиях доставки и оплаты</a></label>
							<select class="form-control" name="{$inp/ship_type/@input}" value="{$inp/ship_type}" error="{$inp/ship_type/@validation-error}">
								<option>Самовывоз г.Минск ул.Толбухина 12а</option>
								<option>Самовывоз г.Минск ул.Я.Коласа 30</option>
								<option>Самовывоз г.Минск ул.Притыцкого 42</option>
								<option>Доставка курьером</option>
								<option>Белпочта</option>
								<option>Доставка AutoLight EXPRESS</option>
							</select>
						</div>
						<div class="form-group">
							<label>Способ оплаты</label>
							<select class="form-control" name="{$inp/pay_type/@input}" value="{$inp/pay_type}" error="{$inp/pay_type/@validation-error}">
								<option>Наличные</option>
								<option>Наложенным платежом</option>
								<option>Оплата онлайн</option>
							</select>
						</div>
						<div class="form-group">
							<label>E-mail *:</label>
							<input type="text" class="form-control" name="{$inp/email/@input}" value="{if($inp/email != '') then $inp/email else $jur_reg/email}"/>
						</div>
						<div class="form-group">
							<label>Контактное лицо *:</label>
							<input type="text" class="form-control" name="{$inp/contact_name/@input}" value="{if($inp/contact_name != '') then $inp/contact_name else $jur_reg/contact_name}"/>
						</div>
						<div class="form-group">
							<label>Телефон контактного лица:</label>
							<input type="text" class="form-control" name="{$inp/contact_phone/@input}" value="{if($inp/contact_phone != '') then $inp/contact_phone else $jur_reg/contact_phone}"/>
						</div>
						<div class="form-group">
							<label>Юридический адрес:</label>
							<input type="text" class="form-control" name="{$inp/address/@input}" value="{if($inp/address != '') then $inp/address else $jur_reg/address}"/>
						</div>
						<div class="form-group">
							<label>Расчетный счет:</label>
							<input type="text" class="form-control" name="{$inp/account/@input}" value="{if($inp/account != '') then $inp/account else $jur_reg/account}"/>
						</div>
						<div class="form-group">
							<label>Название банка:</label>
							<input type="text" class="form-control" name="{$inp/bank/@input}" value="{if($inp/bank != '') then $inp/bank else $jur_reg/bank}"/>
						</div>
						<div class="form-group">
							<label>Адрес банка:</label>
							<input type="text" class="form-control" name="{$inp/bank_address/@input}" value="{if($inp/bank_address != '') then $inp/bank_address else $jur_reg/bank_address}"/>
						</div>
						<div class="form-group">
							<label>Код банка:</label>
							<input type="text" class="form-control" name="{$inp/bank_code/@input}" value="{if($inp/bank_code != '') then $inp/bank_code else $jur_reg/bank_code}"/>
						</div>
						<div class="form-group">
							<label>УНП:</label>
							<input type="text" class="form-control" name="{$inp/unp/@input}" value="{if($inp/unp != '') then $inp/unp else $jur_reg/unp}"/>
						</div>
						<div class="form-group">
							<label>Ф.И.О директора:</label>
							<input type="text" class="form-control" name="{$inp/director/@input}" value="{if($inp/director != '') then $inp/director else $jur_reg/director}"/>
						</div>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<!--
						<div class="checkbox">
							<label>
								<input type="checkbox" value=""/> зарегистрироваться на сайте
							</label>
						</div>
						-->
						<input type="submit" class="button" value="Отправить заказ"/>
					</form>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>