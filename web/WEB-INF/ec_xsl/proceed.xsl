<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="page/user_jur//@validation-error"/>

	<xsl:template name="CONTENT">
		<xsl:call-template name="INC_MOBILE_HEADER"/>
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<a href="{page/cart_link}">Изменить заказ</a> &gt;
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1>Анкета покупателя</h1>

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
					<form action="{page/confirm_link}" method="post">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<div class="form-group">
							<label>Ваше имя:</label>
							<input type="text" class="form-control" name="{$inp/name/@input}" value="{$inp/name}" error="{$inp/name/@validation-error}"/>
						</div>
						<div class="form-group">
							<label for="">Адрес:</label>
							<input type="text" class="form-control" name="{$inp/address/@input}" value="{$inp/address}" error="{$inp/address/@validation-error}"/>
						</div>
						<div class="form-group">
							<label>Способ доставки <a href="">Подробнее об условиях доставки</a></label>
							<select class="form-control" name="{$inp/ship_type/@input}" value="{$inp/ship_type}" error="{$inp/ship_type/@validation-error}">
								<option>1</option>
								<option>2</option>
								<option>3</option>
								<option>4</option>
								<option>5</option>
							</select>
						</div>
						<div class="form-group">
							<label>Телефон:</label>
							<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$inp/phone}" error="{$inp/phone/@validation-error}"/>
						</div>
						<div class="form-group">
							<label>Электронная почта:</label>
							<input type="text" class="form-control" name="{$inp/email/@input}" value="{$inp/email}" error="{$inp/email/@validation-error}"/>
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
						<input type="submit" value="Отправить заказ"/>
					</form>
				</div>


				<div role="tabpanel" class="tab-pane{' active'[$is_jur]}" id="tab_jur">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<div class="form-group">
							<label>Наименование организации:</label>
							<input type="text" class="form-control" name="{$inp/organization/@input}"
							       value="{$inp/organization}" error="{$inp/organization/@validation-error}"/>
						</div>
						<div class="form-group">
							<label for="">Телефон/факс:</label>
							<input type="text" class="form-control" name="{$inp/phone/@input}" value="{$inp/phone}"/>
						</div>
						<div class="form-group">
							<label>Способ доставки <a href="">Подробнее об условиях доставки</a></label>
							<select class="form-control" name="{$inp/ship_type/@input}" value="{$inp/ship_type}">
								<option>1</option>
								<option>2</option>
								<option>3</option>
								<option>4</option>
								<option>5</option>
							</select>
						</div>
						<div class="form-group">
							<label>E-mail:</label>
							<input type="text" class="form-control" name="{$inp/email/@input}" value="{$inp/email}"/>
						</div>
						<div class="form-group">
							<label>Контактное лицо:</label>
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
						<input type="submit" value="Отправить заказ"/>
					</form>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>