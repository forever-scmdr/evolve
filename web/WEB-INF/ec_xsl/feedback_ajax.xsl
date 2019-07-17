<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"        xmlns="http://www.w3.org/1999/xhtml"        xmlns:f="f:f"        version="2.0">	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>	<xsl:variable name="message" select="page/variables/message"/>	<xsl:variable name="success" select="page/variables/result = 'success'"/>	<xsl:variable name="form" select="page/feedback_form"/>	<xsl:template match="/">		<div class="result" id="modal-feedback">			<div class="modal-dialog modal-sm" role="document">				<div class="modal-content">					<div class="modal-header">						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>						<div class="modal-title h4">Форма обратной связи</div>					</div>					<div class="modal-body">						<xsl:if test="$message">							<div class="alert alert-{if ($success) then 'success' else 'danger'}">								<xsl:value-of select="$message"/>							</div>						</xsl:if>						<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="modal-feedback">							<div class="form-group">								<label>Ваше имя:</label>								<input type="text" class="form-control" name="{$form/input/name/@input}" value="{$form/input/name}" placeholder="Иванов Иван"/>							</div>							<div class="form-group">								<label>Телефон:</label>								<input type="text" class="form-control" name="{$form/input/phone/@input}" value="{$form/input/phone}" placeholder="+375 29 123-45-67"/>							</div>							<div class="form-group">								<label>Электронная почта:</label>								<input type="text" class="form-control" name="{$form/input/email/@input}" value="{$form/input/email}" placeholder="example@xx.by"/>							</div>							<div class="form-group">								<label>Сообщение:</label>								<textarea class="form-control" rows="3"								          name="{$form/input/message/@input}"><xsl:value-of select="$form/input/message"/></textarea>							</div>							<input type="submit" class="button" value="Отправить сообщение"/>						</form>					</div>				</div>			</div>		</div>	</xsl:template>	<xsl:template name="FEEDBACK_FORM">		<div class="modal fade" tabindex="-1" role="dialog" id="modal-feedback" ajax-href="{page/feedback_form_link}" show-loader="yes">		+++		</div>	</xsl:template></xsl:stylesheet>