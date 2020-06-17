<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template name="DELIVERY_AND_PAYMENT">
		<xsl:param name="inp"/>
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
	</xsl:template>


	<xsl:template name="USER_PHYS_INPUTS">
		<xsl:param name="inp"/>
		<div class="form-group">
			<label>Ваше имя *:</label>
			<input type="text" class="form-control" name="{$inp/name/@input}" value="{$inp/name}" error="{$inp/name/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{$inp/address}" error="{$inp/address/@validation-error}"/>
		</div>
		
		<div class="form-group">
			<label>Мобильный телефон *:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$inp/phone}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Электронная почта:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{$inp/email}" error="{$inp/email/@validation-error}"/>
		</div>
	</xsl:template>


	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<div class="form-group">
			<label>Наименование организации *:</label>
			<input type="text" class="form-control" name="{$inp/organization/@input}"
			       value="{$inp/organization}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form-group">
			<label for="">Мобильный телефон:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$inp/phone}"/>
		</div>
		<div class="form-group">
			<label>E-mail:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{$inp/email}"/>
		</div>
		<div class="form-group">
			<label>Контактное лицо *:</label>
			<input type="text" class="form-control" name="{$inp/contact_name/@input}" value="{$inp/contact_name}"/>
		</div>
		<div class="form-group">
			<label>Телефон контактного лица:</label>
			<input type="text" class="form-control" name="{$inp/contact_phone/@input}" value="{$inp/contact_phone}"/>
		</div>
		<div class="form-group">
			<label>Юридический адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{$inp/address}"/>
		</div>
		<div class="form-group">
			<label>Расчетный счет:</label>
			<input type="text" class="form-control" name="{$inp/account/@input}" value="{$inp/account}"/>
		</div>
		<div class="form-group">
			<label>Название банка:</label>
			<input type="text" class="form-control" name="{$inp/bank/@input}" value="{$inp/bank}"/>
		</div>
		<div class="form-group">
			<label>Адрес банка:</label>
			<input type="text" class="form-control" name="{$inp/bank_address/@input}" value="{$inp/bank_address}"/>
		</div>
		<div class="form-group">
			<label>Код банка:</label>
			<input type="text" class="form-control" name="{$inp/bank_code/@input}" value="{$inp/bank_code}"/>
		</div>
		<div class="form-group">
			<label>УНП:</label>
			<input type="text" class="form-control" name="{$inp/unp/@input}" value="{$inp/unp}"/>
		</div>
		<div class="form-group">
			<label>Ф.И.О директора:</label>
			<input type="text" class="form-control" name="{$inp/director/@input}" value="{$inp/director}"/>
		</div>
	</xsl:template>


</xsl:stylesheet>