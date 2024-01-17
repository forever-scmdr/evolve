<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:function name="f:not_empty">
		<xsl:param name="first"/>
		<xsl:param name="second"/>
		<xsl:value-of select="if (not($first = '')) then $first else $second"/>
	</xsl:function>





	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<div class="form__item">
			<label class="form-label" for="form_email">E-mail/логин:</label>
			<input class="input form__element" type="text" id="form_email"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">ИНН:</label>
			<input class="input form__element" type="text" id="inn_input"
				   name="{$inp/inn/@input}" value="{f:not_empty($inp/inn, $vals/inn)}" error="{$inp/inn/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">Наименование организации:</label>
			<input class="input form__element" type="text" id="input_organization"
				   name="{$inp/organization/@input}" value="{f:not_empty($inp/organization, $vals/organization)}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">КПП:</label>
			<input class="input form__element" type="text" id="input_kpp"
				   name="{$inp/kpp/@input}" value="{f:not_empty($inp/kpp, $vals/kpp)}" error="{$inp/kpp/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_ja">Адрес:</label>
			<input class="input form__element" type="text" id="input_address"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_ja">E-mail организации:</label>
			<input class="input form__element" type="text" id="input_corp_email"
				   name="{$inp/corp_email/@input}" value="{f:not_empty($inp/corp_email, $vals/corp_email)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">Телефон/факс:</label>
			<input class="input form__element" type="text" id="input_phone"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact">Руководитель:</label>
			<input class="input form__element" type="text" id="input_boss"
				   name="{$inp/boss/@input}" value="{f:not_empty($inp/boss, $vals/boss)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact">Должность руководителя:</label>
			<input class="input form__element" type="text" id="input_boss_position"
				   name="{$inp/boss_position/@input}" value="{f:not_empty($inp/boss_position, $vals/boss_position)}"/>
		</div>
		<xsl:if test="page/@name != 'register'">
			<div class="form__item"><label class="form-label" for="form_ship">Способ доставки: <a href="dostavka">подробнее</a></label>
				<select class="form__element" id="form_ship"
						name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}" error="{$inp/ship_type/@validation-error}">
					<option value="">Выберите способ доставки</option>
					<xsl:for-each select="page/common/delivery/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:  <a href="oplata">подробнее</a></label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="page/common/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
		</xsl:if>
		<xsl:if test="page/@name = 'register'">
			<div class="form__item">
				<label class="form-label" for="form_contact">подтверждаю согласие на обработку персональных данных:</label>
				<input class="input form__element" type="checkbox"
					   name="{$inp/confirm/@input}" value="да"/>
			</div>
		</xsl:if>
		<xsl:if test="page/@name = 'register'">
			<div class="form__item">
				<label class="form-label" for="form_contact">рассылка индивидуальных предложений:</label>
				<input class="input form__element" type="checkbox"
					   name="{$inp/inform/@input}" value="да" checked="checked"/>
			</div>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>