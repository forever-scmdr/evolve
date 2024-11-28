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

	<xsl:variable name="h1"/>

	<xsl:variable name="gd_ajax_id" select="'get_doc'" />

	<xsl:template match="/">
		<div class="result" id="{$gd_ajax_id}">
			<xsl:if test="not($success)">
				<p>
					Оставьте заявку, и наш специалист свяжется с вами для предоставления запрашиваемого документа.
				</p>
				<xsl:if test="$message">
					<p style="color: red">
						<xsl:value-of select="$message" />
					</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<input type="hidden" name="topic" value="Запрос 3D-модели" />
					<input type="hidden" name="email" value="info@termobrest.ru, sproject@termobrest.ru" />
					<input type="hidden" name="src_page" value="get_doc" />
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
						<label>Контактный телефон:*</label>
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
						<label>Марка запрашиваемой модели:*</label>
						<textarea style="height:80px;" name="{$f/mark/@input}" class="form-control">
							<xsl:value-of select="if ($f/mark != '') then $f/mark else page/variables/mark"/>
						</textarea>
					</div>
					<div>
						<script type="text/javascript">
							function reloadCaptcha(){
								var d = new Date();
								$(".captcha").attr("src", "/CaptchaImg.png?"+d.getTime());
							}
						</script>

						<img id="captcha_image" class="captcha" alt="captcha image" src="/CaptchaImg.png"/>
						<img src="images/button_reload.png" onclick="reloadCaptcha()" alt="reload" width="40" height="40"/>
						<input name="answer" />
					</div>

					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$gd_ajax_id}', initSelects); return false;">Отправить запрос</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p>
					<xsl:value-of select="$message" />
				</p>
				<script>
					dataLayer.push({'event':'Запросить 3d модель - отправлено'});
				</script>
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
						<h4 class="modal-title">Запросить 3D модель</h4>
					</div>
					<div class="modal-body" id="{$gd_ajax_id}">

					</div>
				</div>
			</div>
		</div>
		<script>
		function getDocForm() {
			<!-- insertAjax('<xsl:value-of select="if (page/product) then page/product/get_doc_link else page/get_doc_link" />', 'get_doc_popup', function() {
				initSelects();
			}); -->
			insertAjax('<xsl:value-of select="if (page/product) then concat(page/get_doc_link, '?mark=', $h1) else page/get_doc_link" />', 'get_doc_popup', function() {
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
		<a class="btn btn-default" type="button" data-toggle="modal" data-target="#get_doc_popup" onclick="getDocForm()">Запросить 3D модель</a>
	</xsl:template>

	<xsl:template name="DOC_BUTTON_2">
		<a href="#" data-toggle="modal" data-target="#get_doc_popup" onclick="getDocForm()">Запросить 3D модель</a>
	</xsl:template>

	<xsl:template name="PROD_DOC_BUTTON">
		<a href="#" class="w-button summon-form popupButton" rel="get_doc_popup"
			onclick="getDocForm()">Запросить 3D модель</a>
	</xsl:template>

</xsl:stylesheet>