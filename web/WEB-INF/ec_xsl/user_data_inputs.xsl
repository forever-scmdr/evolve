<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="USER_PHYS_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="src" select="$inp"/>
		<div class="form-group">
			<label>Ваше имя *:</label>
			<input type="text" class="form-control" name="{$inp/name/@input}" value="{$src/name}" error="{$inp/name/@validation-error}"/>
		</div>
		<div class="form-group">
			<label for="">Адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{$src/address}" error="{$inp/address/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Способ доставки <a href="oplata_i_dostavka">Подробнее об условиях доставки</a></label>
			<select class="form-control" name="{$inp/ship_type/@input}" value="{$src/ship_type}" error="{$inp/ship_type/@validation-error}">
				<option>Самовывоз из офиса отдела продаж.<!--  Бесплатно. --></option>
				<option>Доставка транспортом Поставщика.<!--  От 250р бесплатно. Менее - 5р. --></option>
				<option>Доставка курьерской службой. <!-- Сумма заказа от 500р. --></option>
			</select>
		</div>
		<div class="form-group">
			<label>Телефон *:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$src/phone}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Электронная почта:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{$src/email}" error="{$inp/email/@validation-error}"/>
		</div>
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="src" select="$inp"/>
		<xsl:param name="need_password" select="false()"/>
		<div class="form-group">
			<label>E-mail<xsl:if test="$need_password"> (Логин)</xsl:if> *:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{$src/email}"/>
		</div>
		<xsl:if test="$need_password">
			<div class="form-group">
				<label>Пароль *:</label>
				<input type="text" class="form-control" name="{$inp/password/@input}" value="{$src/password}" error="{$inp/password/@validation-error}"/>
			</div>
		</xsl:if>
		<div class="form-group">
			<label>Контактное лицо *:</label>
			<input type="text" class="form-control" name="{$inp/contact_name/@input}" value="{$src/contact_name}"/>
		</div>
		<div class="form-group">
			<label>Телефон контактного лица *:</label>
			<input type="text" class="form-control" name="{$inp/contact_phone/@input}" value="{$src/contact_phone}"/>
		</div>
		<div class="form-group">
			<label>Наименование организации *:</label>
			<input type="text" class="form-control" name="{$inp/organization/@input}"
			       value="{$src/organization}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form-group">
			<label for="">Телефон/факс:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$src/phone}"/>
		</div>
		<!--<div class="form-group">-->
			<!--<label>Способ доставки <a href="oplata_i_dostavka">Подробнее об условиях доставки</a></label>-->
			<!--<select class="form-control" name="{$inp/ship_type/@input}" value="{$inp/ship_type}">-->
				<!--<option>Самовывоз из офиса отдела продаж.&lt;!&ndash;  Бесплатно. &ndash;&gt;</option>-->
				<!--<option>Доставка транспортом Поставщика.&lt;!&ndash;  От 250р бесплатно. Менее - 5р. &ndash;&gt;</option>-->
				<!--<option>Доставка курьерской службой.&lt;!&ndash;  Сумма заказа от 500р. &ndash;&gt;</option>-->
			<!--</select>-->
		<!--</div>-->


		<div class="form-group">
			<label>Юридический адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{$src/address}"/>
		</div>
		<div class="form-group">
			<label>УНП:</label>
			<input type="text" class="form-control" name="{$inp/unp/@input}" value="{$src/unp}"/>
		</div>
		<div class="form-group">
			<label>Расчетный счет:</label>
			<input type="text" class="form-control" name="{$inp/account/@input}" value="{$src/account}"/>
		</div>
		<div class="form-group">
			<label>Название банка:</label>
			<input type="text" class="form-control" name="{$inp/bank/@input}" value="{$src/bank}"/>
		</div>
		<div class="form-group">
			<label>Код банка:</label>
			<input type="text" class="form-control" name="{$inp/bank_code/@input}" value="{$src/bank_code}"/>
		</div>
		<div class="form-group">
			<label>Ф.И.О директора:</label>
			<input type="text" class="form-control" name="{$inp/director/@input}" value="{$src/director}"/>
		</div>
		<div class="form-group">
			<label>Действует на основании:</label>
			<input type="text" class="form-control" name="{$inp/base/@input}" value="{$src/base}"/>
		</div>
	</xsl:template>


</xsl:stylesheet>