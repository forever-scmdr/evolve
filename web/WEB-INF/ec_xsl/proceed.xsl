<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление заявки'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<!--<xsl:variable name="is_jur" select="page/user_jur//@validation-error"/>-->
	<xsl:variable name="is_jur" select="true()"/>
	<xsl:variable name="jur" select="page/jur"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<a href="{page/cart_link}">Изменить зявку</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
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
				<!--<li role="presentation" class="{'active'[not($is_jur)]}"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>-->
				<!--<li role="presentation" class="{'active'[$is_jur]}"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>-->
				<li role="presentation" class="active"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">

<!--
				<div role="tabpanel" class="tab-pane{' active'[not($is_jur)]}" id="tab_phys">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заявки.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
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
						<input type="submit" value="Отправить заявку"/>
					</form>
				</div>
-->

				<div role="tabpanel" class="tab-pane active" id="tab_jur">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заявки.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<xsl:variable name="src" select="if ($inp/field != '') then $inp else if ($jur) then $jur else $inp"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="src" select="$src"/>
						</xsl:call-template>
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
						<input type="submit" value="Отправить заявку"/>
					</form>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>