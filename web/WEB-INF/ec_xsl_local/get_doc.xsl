<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f" version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html"
		indent="yes" omit-xml-declaration="yes" />

	<xsl:variable name="success" select="page/variables/result = 'success'" />
	<xsl:variable name="message" select="page/variables/message" />
	<xsl:variable name="f" select="page/form" />

	<!-- **************************** СТРАНИЦА ******************************** -->

	<xsl:variable name="gd_ajax_id" select="'get_doc'" />

	<xsl:template match="/">
		<div class="result" id="{$gd_ajax_id}">
			<xsl:if test="not($success)">
				<p>
					Для получения документации укажите, пожалуйста, ваш email.
					После получения заявки наши специалисты свяжутся с Вами.
				</p>
				<xsl:if test="$message">
					<p style="color: red">
						<xsl:value-of select="$message" />
					</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<input type="hidden" name="required" value="email, mark" />
					<input type="hidden" name="topic" value="Получить КД" />
					<input type="hidden" name="email" value="support@forever-ds.com, info@termobrest.ru, sproject@termobrest.ru" />
					<input type="hidden" name="src_page" value="get_doc" />
					<div class="form-group">
						<label>Email:</label>
						<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control"/>
					</div>
					<div class="form-group">
						<label>Марка изделия:</label>
						<input type="text" name="{$f/mark/@input}" value="{if ($f/mark != '') then $f/mark else page/variables/mark}" class="form-control"/>
					</div>
					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$gd_ajax_id}', initSelects); return false;">Отправить запрос</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p>
					<!-- <xsl:value-of select="$message" /> -->
					Ваш запрос принят.<br/>
					Подготовка необходимых файлов может занять некоторое время.<br/>
					В случае необходимости наш специалист свяжется с Вами.<br/>
					Благодарим за ожидание.
				</p>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="DOC_FORM">
		<div class="modal fade" id="get_doc_popup" tabindex="-1">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span>×</span>
						</button>
						<h4 class="modal-title">Запросить CAD-файл</h4>
					</div>
					<div class="modal-body" id="{$gd_ajax_id}">

					</div>
				</div>
			</div>
		</div>
		<script>
		function getDocForm() {
			insertAjax('<xsl:value-of select="if (page/product) then page/product/get_doc_link else page/get_doc_link" />', 'get_doc_popup', function() {
				initSelects();
			});
		}

		$(document).ready(function() {
			getDocForm();
		});

		function initSelects() {
			$('select[value]').each(function() {
				var value = $(this).attr('value');
				if (value != '')
					$(this).val(value);
			});
		}
		</script>
	</xsl:template>

	<xsl:template name="DOC_BUTTON_1">
		<a class="btn btn-default" type="button" data-toggle="modal" data-target="#get_doc_popup" onclick="getDocForm()">Запросить CAD-файл</a>
	</xsl:template>

	<xsl:template name="DOC_BUTTON_2">
		<a href="#" data-toggle="modal" data-target="#get_doc_popup" onclick="getDocForm()">Запросить CAD-файл</a>
	</xsl:template>

	<xsl:template name="PROD_DOC_BUTTON">
		<a href="#" class="w-button summon-form popupButton" rel="get_doc_popup"
			onclick="getDocForm()">Запросить CAD-файл</a>
	</xsl:template>

</xsl:stylesheet>