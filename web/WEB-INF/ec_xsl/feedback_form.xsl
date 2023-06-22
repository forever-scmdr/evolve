<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="serv" select="/page/service"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>


	<xsl:template match="/">
		<div>
			<div class="result" id="feedback_ajax" style="display:block; font-size:14px;">
				<xsl:if test="$success">
					<div style="height: 400px;padding: 50px 30px;">
						<div style="margin-top: 150px;">
						<img src="images/logo.jpg" alt="logo sansputnik" style="float: left; margin-right: 15px; margin-bottom: 15px;"/> 
						<h2 style="font-size: 20px; margin-bottom: 12px;">Спасибо за Ваш отзыв</h2>
						<p style="margin-bottom: 12px;">Ваш отзыв будет размещен на сайте после модерации.</p>
						<a onclick="insertAjax('feedback_form/?rn={page/variables/rn}')" style="text-decoration: underline; cursor: pointer;">Написать еще отзыв</a>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="not($success)">
					<p>Опишите, как вам отдыхалось, довольны ли сервисом, что запомнилось. 
					Отдельно опишите впечатления от своего номера. 
					Ваш отзыв повлияет на мнение наших новых гостей. 
					Не хотелось бы не оправдать их ожидания.</p>
					<xsl:if test="$message">
						<p><b style="color: {if (page/variables/result = 'error') then 'red' else 'green'}"><xsl:value-of select="$message"/></b></p>
					</xsl:if>
					<form action="{$f/submit_link}" method="post" id="my_form">
						<div class="form-group">
							<label for="">Ваше имя:</label>
							<input type="text" name="{$f/fio/@input}" value="{$f/fio}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Страна:</label>
							<input ype="text" name="{$f/country/@input}" value="{$f/country}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Даты заезда:</label>
							<input type="text" name="{$f/live_date/@input}" value="{$f/live_date}" class="form-control" id="" placeholder=""/>
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
							<label for="">Отзыв об отдыхе и лечении:</label>
							<textarea class="form-control" rows="3" name="{$f/service_feedback/@input}"><xsl:value-of select="$f/service_feedback"/></textarea>
						</div>
						<div class="form-group">
							<label for="">Отзыв о номере:</label>
							<textarea class="form-control" rows="3" name="{$f/room_feedback/@input}"><xsl:value-of select="$f/room_feedback"/></textarea>
						</div>
						<button type="submit" class="btn btn-primary" onclick="postFormAjax('my_form', 'feedback_ajax'); return false;">Отправить отзыв</button>
						<xsl:if test="page/variables/rn">
							<xsl:variable name="rn" select="page/variables/rn"/>
							<input type="hidden" name="rn" value="{$rn}"/>
							<input type="hidden" name="{$f/room_type/@input}" value="{//room[@id = $rn]/name}"/>
						</xsl:if>
						<input type="text" name="company" style="display:none;"/>
					</form>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="FEEDBACK_MODAL">
		<xsl:param name="feedback_link"/>
		<div class="modal fade" id="modal-guest-feedback" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-md" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title">Оставьте свой отзыв</h4>
					</div>
					<div class="modal-body" id="feedback_ajax">

					</div>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				insertAjax('<xsl:value-of select="$feedback_link"/>');
			});
		</script>
	</xsl:template>

</xsl:stylesheet>