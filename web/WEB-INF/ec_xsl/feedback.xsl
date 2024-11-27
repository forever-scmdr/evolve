<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<div class="result" id="fform">
			<xsl:if test="not($success)">
				<p>
					Оставьте заявку, и наш специалист свяжется с вами, чтобы ответить на ваши вопросы
				</p>
				<xsl:if test="$message">
					<p style="color: red"><xsl:value-of select="$message"/></p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<input type="hidden" name="topic" value="Помощь специалиста"/>
					<div class="form-group">
						<label>ФИО:*</label>
						<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control"/>
					</div>
					<div class="form-group">
						<label>Наименование организации:*</label>
						<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control"/>
					</div>
					<div class="form-group">
						<label>Адрес организации:</label>
						<input type="text" name="{$f/jur_address/@input}" value="{$f/jur_address}" class="form-control"/>
					</div>
					<div class="form-group">
						<label>Контактный телефон:</label>
						<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control"/>
					</div>
					<div class="form-group">
						<label>E-mail:*</label>
						<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control"/>
					</div>
					<div class="form-group mandatory-input" title="обязательно для заполнения" style="display: none;">
						<label>Дополнительный E-mail:*</label>
						<input type="text" name="{$f/spam/@input}" value="{$f/spam}" class="form-control" />
					</div>
					<div class="form-group">
						<label for="xi">Текст сообщения:*</label>
						<textarea class="form-control" name="{$f/message/@input}"><xsl:value-of select="$f/message"/></textarea>
					</div>
				
					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), 'fform'); return false;">Отправить запрос</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p><xsl:value-of select="$message"/></p>
				<p><a style="cursor: pointer;" onclick="insertAjax('{page/feedback_link}', 'fform'); return false;">Написать еще сообщение</a></p>
				<script>
					dataLayer.push({'event':'Помощь специалиста - отправлено'});
				</script>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="FEEDBACK_FORM">
	<div class="modal fade" id="feedback" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Помощь специалиста</h4>
				</div>
				<div class="modal-body" id="fform">

				</div>
			</div>
		</div>
	</div>
	<script>
		function getForm() {
			insertAjax('<xsl:value-of select="page/feedback_form_link"/>', 'fform');
		}
	
		$(document).ready(function() {
			getForm();
		});
	</script>
	</xsl:template>

	<xsl:template name="FEEDBACK_BUTTON_1">
		<a class="btn btn-default" type="button" data-toggle="modal" data-target="#feedback" onclick="getForm()">Помощь специалиста</a>
	</xsl:template>

	<xsl:template name="FEEDBACK_BUTTON_2">
		<a href="#" data-toggle="modal" data-target="#feedback" onclick="getForm()">Помощь специалиста</a>
	</xsl:template>

</xsl:stylesheet>