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

	<xsl:variable name="paper_ajax_id" select="'paper_form_content'"/>

	<xsl:template match="/">
		<div class="result" id="{$paper_ajax_id}">
			<xsl:if test="not($success)">
				<xsl:if test="$message">
					<p style="color: red"><xsl:value-of select="$message"/></p>
				</xsl:if>
				<xsl:if test="not($message)">
					<p>Заполните все поля, отмеченные символом "*", чтобы отправить заявку на получение бумажного каталога СП ООО «ТермоБрест».</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<div class="form-group">
						<label for="xi">ФИО:*</label>
						<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Наименование организации:*</label>
						<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Адрес организации:</label>
						<input type="text" name="{$f/jur_address/@input}" value="{$f/jur_address}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="ad">Адрес для отправки каталога:*</label>
						<input type="text" name="{$f/address/@input}" value="{$f/address}" class="form-control" id="ad"/>
					</div>
					<div class="form-group">
						<label for="xi">Контактный телефон:*</label>
						<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">E-mail:*</label>
						<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control" id="xi"/>
					</div>
					<div class="form-group mandatory-input" title="обязательно для заполнения" style="display: none;">
						<label>Дополнительный E-mail:*</label>
						<input type="text" name="{$f/spam/@input}" value="{$f/spam}" class="form-control" />
					</div>
					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$paper_ajax_id}'); return false;">Отправить заявку</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p>Заявка успешно отправлена. Спасибо за интерес к нашей продукции.</p>
				<p><a onclick="insertAjax('{page/paper_form_link}', '{$paper_ajax_id}'); return false;">Написать еще заявку</a></p>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="PAPER_FORM">
	<div class="modal fade" id="paper_form" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Получить бумажный каталог</h4>
				</div>
				<div class="modal-body" id="{$paper_ajax_id}">

				</div>
			</div>
		</div>
	</div>	

	<script>
		function getPaperForm() {
			insertAjax('<xsl:value-of select="concat('https://termobrest.ru/',page/paper_form_link)"/>', '<xsl:value-of select="$paper_ajax_id"/>', function() { initSelects(); });
		}

		$(document).ready(function() {
			getPaperForm();
		});
	</script>
	</xsl:template>
	
	<xsl:template name="PAPER_BUTTON">
		<xsl:param name="block"/>
		<xsl:variable name="spec_name" select="page/footer/footer_link[code = '1']"/>
		<a onclick="getPaperForm()" class="btn btn-sm btn-success{' btn-block'[$block]}" type="button" data-toggle="modal" data-target="#paper_form">
			Получить бумажный каталог
		</a>
	</xsl:template>

</xsl:stylesheet>