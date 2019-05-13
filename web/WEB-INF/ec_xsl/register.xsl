<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="is_jur" select="page/user_jur//@validation-error"/>
	<xsl:variable name="is_login" select="page/variables/login = 'true'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Вход/Регистрация</h1>

		<div class="page-content m-t">
			<xsl:if test="$message and not($success)">
				<div class="alert alert-danger">
					<h4>Ошибка</h4>
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<xsl:if test="$message and $success">
				<div class="alert alert-success">
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="{'active'[$is_login]}"><a href="#tab_login" role="tab" data-toggle="tab">Вход</a></li>
				<li role="presentation" class="{'active'[not($is_jur) and not($is_login)]}"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>
				<li role="presentation" class="{'active'[$is_jur]}"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">

				<div role="tabpanel" class="tab-pane{' active'[$is_login]}" id="tab_login">
					<p>Введите адрес электрнной почты и пароль.</p>
					<form action="{page/submit_login}" method="post" onsubmit="lock('tab_login')">
						<div class="form-group">
							<label>Электронная почта:</label>
							<input type="text" class="form-control" name="email"/>
						</div>
						<div class="form-group">
							<label>Пароль:</label>
							<input type="password" class="form-control" name="password"/>
						</div>
						<input type="submit" class="button" value="Войти"/>
					</form>
				</div>

				<div role="tabpanel" class="tab-pane{' active'[not($is_jur) and not($is_login)]}" id="tab_phys">
					<p>Заполните, пожалуйста, форму регистрации. Ваш email будет использован в качестве логина. Если не указан email, будет использован номер телефона.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<xsl:call-template name="USER_PHYS_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
						</xsl:call-template>
						<div class="form-group">
							<label>Пароль:</label>
							<input type="text" class="form-control" name="{$inp/password/@input}" value="{$inp/password}" error="{$inp/password/@validation-error}"/>
						</div>
						<input type="submit" class="button" value="Отправить анкету"/>
					</form>
				</div>

				<div role="tabpanel" class="tab-pane{' active'[$is_jur]}" id="tab_jur">
					<p>Заполните, пожалуйста, форму регистрации. Ваш email будет использован в качестве логина. Если не указан email, будет использован номер телефона.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
						</xsl:call-template>
						<div class="form-group">
							<label>Пароль:</label>
							<input type="text" class="form-control" name="{$inp/password/@input}" value="{$inp/password}" error="{$inp/password/@validation-error}"/>
						</div>
						<input type="submit" class="button" value="Отправить анкету"/>
					</form>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>