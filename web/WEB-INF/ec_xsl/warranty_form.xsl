<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	xmlns:xs="http://www.w3.org/2001/XMLSchema"	xmlns="http://www.w3.org/1999/xhtml"	xmlns:f="f:f"	version="2.0">	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>	<xsl:variable name="message" select="page/variables/message"/>	<xsl:variable name="form" select="page/warranty_form"/>	<xsl:variable name="success" select="page/variables/success = 'true'"/>	<xsl:template match="/">		<div class="result" id="warranty">			<div class="modal-dialog" role="document">				<div class="modal-content">					<xsl:if test="$success">						<script>window.location.replace("<xsl:value-of select="page/confirmed_link"/>");</script>					</xsl:if>					<div class="modal-header">						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>						<div class="modal-title h4">XXL-гарантия на инструмент</div>					</div>					<div class="modal-body">						<p>Зарегистрируйте свой инструмент в течение 30 дней после покупки и получите расширенную XXL-гарантию на 36 месяцев.</p>						<xsl:if test="$message">							<div class="alert alert-danger">								<xsl:value-of select="$message"/>							</div>						</xsl:if>						<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="warranty">							<div class="form-group" style="display: none">								<label>Website url</label>								<input type="text" class="form-control" name="{$form/input/url/@input}" value="{$form/input/url}" />							</div>							<div class="form-group">								<label>Артикул изделия:</label>								<input type="text" class="form-control" name="{$form/input/code/@input}" value="{$form/input/code}" />							</div>							<div class="form-group">								<label>Серийный номер изделия:</label>								<input type="text" class="form-control" name="{$form/input/serial/@input}" value="{$form/input/serial}" />							</div>							<div class="form-group">								<label>Дата покупки:</label>								<input type="text" class="form-control" name="{$form/input/date/@input}" value="{$form/input/date}" />							</div>							<div class="form-group">								<label>Продавец:</label>								<input type="text" class="form-control" name="{$form/input/seller/@input}" value="{$form/input/seller}" />							</div>							<div class="form-group">								<label>Имя владельца:</label>								<input type="text" class="form-control" name="{$form/input/owner/@input}" value="{$form/input/owner}" />							</div>							<div class="form-group">								<label>Эл. почта владельца:</label>								<input type="text" class="form-control" name="{$form/input/email/@input}" value="{$form/input/email}" />							</div>							<div class="form-group">								<label>Номер телефона владельца:</label>								<input type="text" class="form-control" name="{$form/input/phone/@input}" value="{$form/input/phone}" />							</div>							<input type="submit" class="button" value="Отправить заявку"/>						</form>					</div>				</div>			</div>		</div>	</xsl:template>	<xsl:template name="WARRANTY_FORM">		<div class="modal fade" tabindex="-1" role="dialog" id="warranty" ajax-href="{page/warranty_form_link}" show-loader="yes">		+++		</div>	</xsl:template></xsl:stylesheet>