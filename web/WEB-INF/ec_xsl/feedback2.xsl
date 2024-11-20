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

	<xsl:variable name="feed2_ajax_id" select="'feedback_two'"/>

	<xsl:template match="/">
		<div class="result" id="{$feed2_ajax_id}">
			<xsl:if test="not($success)">
				<xsl:if test="$message">
					<p style="color: red"><xsl:value-of select="$message"/></p>
				</xsl:if>
				<xsl:if test="not($message)">
					<p>Если на сайте что-то не работает, вы заметили ошибку или у вас есть
						предложение, как улучшить работу с сайтом, напишите нам.</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<div class="form-group">
						<label for="xi">ФИО:*</label>
						<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Наименование организации:</label>
						<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Адрес организации:</label>
						<input type="text" name="{$f/jur_address/@input}" value="{$f/jur_address}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Контактный телефон:</label>
						<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">E-mail:</label>
						<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control" id="xi"/>
					</div>
					<div class="form-group">
						<label for="xi">Текст отзыва:*</label>
						<textarea type="" name="{$f/message/@input}" class="form-control" id="xi"><xsl:value-of select="$f/message"/></textarea>
					</div>
			
					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$feed2_ajax_id}'); return false;">Отправить отзыв</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p>Спасибо, Ваш отзыв успешно отправлен.</p>
				<p><a onclick="insertAjax('{page/feedback_form_link2}', '{$feed2_ajax_id}'); return false;">Написать еще отзыв</a></p>
				<script>
					dataLayer.push({'event':'Помощь специалиста - отправлено'});
				</script>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="FEEDBACK_FORM_2">
	<div class="modal fade" id="fform2" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Отзыв о работе сайта</h4>
				</div>
				<div class="modal-body" id="{$feed2_ajax_id}">

				</div>
			</div>
		</div>
	</div>	

	<script>
		function getForm2() {
			insertAjax('<xsl:value-of select="page/feedback_form_link2"/>', '<xsl:value-of select="$feed2_ajax_id"/>', function() { initSelects(); });
		}

		$(document).ready(function() {
			getForm2();
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
	
	<xsl:template name="FEEDBACK_2_BUTTON">
		<xsl:param name="block"/>
		<xsl:variable name="spec_name" select="page/footer/footer_link[code = '1']"/>
		<a onclick="getForm2()" class="btn btn-sm btn-success{' btn-block'[$block]}" type="button" data-toggle="modal" data-target="#fform2">
			<xsl:value-of select="if ($spec_name) then $spec_name/name else 'Оставить отзыв о работе сайта'"/>
		</a>
	</xsl:template>

</xsl:stylesheet>