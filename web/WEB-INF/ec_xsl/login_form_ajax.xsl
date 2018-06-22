<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="target" select="page/variables/target"/>

	<xsl:template match="/">
		<xsl:if test="not($success)">
			<div class="result" id="login_form">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
					<h4 class="modal-title">Вход</h4>
					<xsl:if test="$message">
						<h5 class="modal-title" style="color: red"><xsl:value-of select="$message"/></h5>
					</xsl:if>
				</div>
				<div class="modal-body">
					<form action="{page/login_link}" method="post" ajax="true" ajax-loader-id="login_form">
						<div class="form-group">
							<label>Электронная почта:</label>
							<input type="text" class="form-control" name="username"/>
						</div>
						<div class="form-group">
							<label>Пароль:</label>
							<input type="password" class="form-control" name="password"/>
						</div>
						<input type="submit" name="" value="Войти"/>
					</form>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="$success">
			<div class="result" id="login_form">
				<xsl:if test="$target">
					<script>document.location.href = "<xsl:value-of select="page/base"/>/<xsl:value-of select="$target"/>";</script>
				</xsl:if>
				<xsl:if test="not($target)">
					<script>document.location.reload();</script>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="LOGIN_FORM">
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-login">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content" id="login_form" ajax-href="{page/login_form_link}">

				</div>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>