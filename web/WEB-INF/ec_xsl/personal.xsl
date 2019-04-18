<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="jur" select="page/user_jur"/>
	<xsl:variable name="phys" select="page/user_phys"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Анкета заказчика</h1>

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
			<xsl:if test="$phys">
				<form id="tab_phys" action="{$phys/confirm_link}" method="post" onsubmit="lock('tab_phys')">
					<xsl:variable name="inp" select="$phys/input[1]"/>
					<xsl:call-template name="USER_PHYS_INPUTS">
						<xsl:with-param name="inp" select="$inp"/>
					</xsl:call-template>
					<div class="form-group">
						<label>Новый пароль:</label>
						<input type="password" class="form-control" name="{$phys/input[2]/new-password-1/@input}"/>
					</div>
					<div class="form-group">
						<label>Подтверждение нового пароля:</label>
						<input type="password" class="form-control" name="{$phys/input[3]/new-password-2/@input}"/>
					</div>
					<input type="submit" class="button" value="Отправить анкету"/>
				</form>
			</xsl:if>

			<xsl:if test="$jur">
				<form id="tab_jur" action="{$jur/confirm_link}" method="post" onsubmit="lock('tab_jur')">
					<xsl:variable name="inp" select="$jur/input[1]"/>
					<xsl:call-template name="USER_JUR_INPUTS">
						<xsl:with-param name="inp" select="$inp"/>
					</xsl:call-template>
					<div class="form-group">
						<label>Новый пароль:</label>
						<input type="password" class="form-control" name="{$jur/input[2]/new-password-1/@input}"/>
					</div>
					<div class="form-group">
						<label>Подтверждение нового пароля:</label>
						<input type="password" class="form-control" name="{$jur/input[3]/new-password-2/@input}"/>
					</div>
					<input type="submit" class="button" value="Отправить анкету"/>
				</form>
			</xsl:if>

		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>