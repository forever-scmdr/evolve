<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="jur" select="page/user_jur"/>
	<xsl:variable name="phys" select="page/user_phys"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">Анкета заказчика</div>
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
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
				<div class="form__item">
					<label class="form-label" for="newpass">Новый пароль:</label>
					<input class="input form__element" type="password" id="newpass" name="{$phys/input[2]/new-password-1/@input}"/>
				</div>
				<div class="form__item">
					<label class="form-label" for="newpassconfirm">Подтведите новый пароль:</label>
					<input class="input form__element" type="password" id="newpassconfirm" name="{$phys/input[3]/new-password-2/@input}" />
				</div>
				<button class="button" type="submit">Сохранить изменения</button>
			</form>
		</xsl:if>

		<xsl:if test="$jur">
			<form id="tab_jur" action="{$jur/confirm_link}" method="post" onsubmit="lock('tab_jur')">
				<xsl:variable name="inp" select="$jur/input[1]"/>
				<xsl:call-template name="USER_JUR_INPUTS">
					<xsl:with-param name="inp" select="$inp"/>
				</xsl:call-template>
				<div class="form__item">
					<label class="form-label" for="newpass">Новый пароль:</label>
					<input class="input form__element" type="password" id="newpass" name="{$phys/input[2]/new-password-1/@input}"/>
				</div>
				<div class="form__item">
					<label class="form-label" for="newpassconfirm">Подтведите новый пароль:</label>
					<input class="input form__element" type="password" id="newpassconfirm" name="{$phys/input[3]/new-password-2/@input}" />
				</div>
				<button class="button" type="submit">Сохранить изменения</button>
			</form>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>