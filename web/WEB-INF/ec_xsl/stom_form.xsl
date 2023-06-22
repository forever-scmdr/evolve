<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>


	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<base href="{page/base}"/>
			<meta charset="utf-8"/>
		</head>
		<body>
		<div>
			<div class="result" id="stom_ajax" style="display:block; font-size:14px;">
				<xsl:if test="$success">
					<div style="height: 400px;padding: 50px 30px;">
						<div style="margin-top: 150px;">
						<img src="images/logo.jpg" alt="logo sansputnik" style="float: left; margin-right: 15px; margin-bottom: 15px;"/> 
						<h2 style="font-size: 20px; margin-bottom: 12px;">Спасибо за заявку</h2>
						<p style="margin-bottom: 12px;">Наши специалисты связжутся с Вами в ближайшее время</p>
						<a onclick="insertAjax('stom_form')" style="text-decoration: underline; cursor: pointer;">Написать еще заявку</a>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="not($success)">
					<xsl:if test="$message">
						<p><b style="color: {if (page/variables/result = 'error') then 'red' else 'green'}"><xsl:value-of select="$message"/></b></p>
					</xsl:if>
					<form action="{$f/submit_link}" method="post" id="stom_form" >
						<div class="form-group">
							<label for="">Ф.И.О.:</label>
							<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Дата заезда:</label>
							<input type="text" name="{$f/booking_date/@input}" value="{$f/booking_date}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Год рождения:</label>
							<input type="text" name="{$f/birth/@input}" value="{$f/birth}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Адрес эл. почты:</label>
							<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Контактный телефон:</label>
							<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Опишите проблему:</label>
							<textarea class="form-control" rows="3" name="{$f/message/@input}"><xsl:value-of select="$f/message"/></textarea>
						</div>
						<div class="form-group">
							<label for="">Прикрепите файл:</label>
							<input type="file" style="border:0; padding:0; box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0);" name="{$f/file/@input}" value="{$f/file}" class="form-control" id="" placeholder=""/>
						</div>
						<button type="submit" class="btn btn-primary" onclick="postFormAjax('stom_form', 'stom_ajax'); return false;">Отправить заявку</button>
						<!-- <input type="submit" /> -->
					</form>
				</xsl:if>
			</div>
		</div>
		</body>
		</html>

	</xsl:template>


	<xsl:template name="STOM_MODAL">
		<xsl:param name="feedback_link"/>
		<div class="modal fade" id="modal-stom" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-md" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title">Заполните заявку</h4>
					</div>
					<div class="modal-body" id="stom_ajax">

					</div>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				insertAjax('stom_form');
			});
		</script>
	</xsl:template>


	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
		</xsl:text>
	</xsl:template>


</xsl:stylesheet>