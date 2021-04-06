<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:function name="f:not_empty">
		<xsl:param name="first"/>
		<xsl:param name="second"/>
		<xsl:value-of select="if (not($first = '')) then $first else $second"/>
	</xsl:function>


	<xsl:template name="USER_PHYS_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<div class="form__item">
			<label class="form-label" for="form_name">Ваше имя:</label>
			<input class="input form__element" type="text" id="form_name"
				   name="{$inp/name/@input}" value="{f:not_empty($inp/name, $vals/name)}" error="{$inp/name/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_address">Адрес:</label>
			<input class="input form__element" type="text" id="form_address"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}" error="{$inp/address/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_phone">Телефон:</label>
			<input class="input form__element" type="text" id="form_phone"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_name">Электронная почта:</label>
			<input class="input form__element" type="text" id="form_name"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}" error="{$inp/email/@validation-error}"/>
		</div>
		<xsl:if test="page/@name != 'register'">
			<div class="form__item"><label class="form-label" for="form_ship">Способ доставки: <a href="{$domain/cart_and_feedback_settings/delivery/link}">подробнее</a></label>
				<select class="form__element" id="form_ship"
						name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}" error="{$inp/ship_type/@validation-error}">
					<option value="">Выберите способ доставки</option>
					<xsl:for-each select="$domain/cart_and_feedback_settings/delivery/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты: <a href="{$domain/cart_and_feedback_settings/payment/link}">подробнее</a></label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="$domain/cart_and_feedback_settings/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
		</xsl:if>
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<div class="form__item">
			<label class="form-label" for="form_org">Наименование организации:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/organization/@input}" value="{f:not_empty($inp/organization, $vals/organization)}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">Телефон/факс:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_email">E-mail:</label>
			<input class="input form__element" type="text" id="form_email"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact">Контактное лицо:</label>
			<input class="input form__element" type="text" id="form_contact"
				   name="{$inp/contact_name/@input}" value="{f:not_empty($inp/contact_name, $vals/contact_name)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact_phone">Телефон контактного лица:</label>
			<input class="input form__element" type="text" id="form_contact_phone"
				   name="{$inp/contact_phone/@input}" value="{f:not_empty($inp/contact_phone, $vals/contact_phone)}"/>
		</div>
		<xsl:if test="page/@name != 'register'">
			<div class="form__item"><label class="form-label" for="form_ship">Способ доставки: <a href="{$domain/cart_and_feedback_settings/delivery/link}">подробнее</a></label>
				<select class="form__element" id="form_ship"
						name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}" error="{$inp/ship_type/@validation-error}">
					<option value="">Выберите способ доставки</option>
					<xsl:for-each select="$domain/cart_and_feedback_settings/delivery/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:  <a href="{$domain/cart_and_feedback_settings/payment/link}">подробнее</a></label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="$domain/cart_and_feedback_settings/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
		</xsl:if>
		<div class="form__item">
			<label class="form-label" for="form_ja">Юридический адрес:</label>
			<input class="input form__element" type="text" id="form_ja"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_account">Расчетный счет:</label>
			<input class="input form__element" type="text" id="form_account"
				   name="{$inp/account/@input}" value="{f:not_empty($inp/account, $vals/account)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_bank">Название банка:</label>
			<input class="input form__element" type="text" id="form_bank"
				   name="{$inp/bank/@input}" value="{f:not_empty($inp/bank, $vals/bank)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_banka">Адрес банка:</label>
			<input class="input form__element" type="text" id="form_banka"
				   name="{$inp/bank_address/@input}" value="{f:not_empty($inp/bank_address, $vals/bank_address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_bankc">Код банка:</label>
			<input class="input form__element" type="text" id="form_bankc"
				   name="{$inp/bank_code/@input}" value="{f:not_empty($inp/bank_code, $vals/bank_code)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_unp">УНП:</label>
			<input class="input form__element" type="text" id="form_unp"
				   name="{$inp/unp/@input}" value="{f:not_empty($inp/unp, $vals/unp)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_dir">Ф.И.О директора:</label>
			<input class="input form__element" type="text" id="form_dir"
				   name="{$inp/director/@input}" value="{f:not_empty($inp/director, $vals/director)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_base">Действует на основании:</label>
			<input class="input form__element" type="text" id="form_base"
				   name="{$inp/base/@input}" value="{f:not_empty($inp/base, $vals/base)}"/>
		</div>
	</xsl:template>


</xsl:stylesheet>