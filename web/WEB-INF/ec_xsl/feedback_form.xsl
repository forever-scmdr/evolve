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
					<form action="{$f/submit_link}" method="post" id="my_form">
						<div class="r_form" style="padding-top: 25px;">
							<h2 >Оставьте свой отзыв</h2>
						
							<p>Опишите, как вам отдыхалось, довольны ли сервисом, что
								запомнилось. Отдельно опишите впечатления от своего номера. Ваш	отзыв
								повлияет на мнение наших новых гостей. Не хотелось бы не оправдать их
								ожидания.
							</p>
							<br/>
								<xsl:if test="$message">
								<h3 style="color: {if (page/variables/result = 'error') then 'red' else 'green'}"><xsl:value-of select="$message"/></h3>
							</xsl:if>
							<div class="fields">
								<p>Ваше имя*:</p>
								<input type="text" name="{$f/fio/@input}" value="{$f/fio}"/>
								<p>Страна*:</p>
								<input type="text" name="{$f/country/@input}" value="{$f/country}" />
								<p>Даты заезда*:</p>
								<input type="text" name="{$f/live_date/@input}" value="{$f/live_date}" />
								<p>Год рождения:</p>
								<input type="text" name="{$f/birth/@input}" value="{$f/birth}" />
								<p>Адрес эл. почты*:</p>
								<input type="text" name="{$f/email/@input}" value="{$f/email}" />
								<p>Контактный телефон:</p>
								<input type="text" name="{$f/phone/@input}" value="{$f/phone}" />
								<xsl:if test="not(page/variables/rn)">
								<p>Отзыв об отдыхе и лечении:</p>
								<textarea name="{$f/service_feedback/@input}"><xsl:value-of select="$f/service_feedback"/></textarea>
								</xsl:if>
								<p>Отзыв о номере:</p>
								<textarea name="{$f/room_feedback/@input}"><xsl:value-of select="$f/room_feedback"/></textarea>
								<!--<input id="agree" name="{$f/post_agree/@input}" value="да" type="checkbox" >
									<xsl:if test="$f/post_agree = 'да'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
								</input>
								 <label for="agree">Разрешаю опубликовать отзыв на сайте</label><br/> -->
								<input id="sbmt" class="big_button" type="submit" value="Оставить отзыв" onclick="postFormAjax('my_form', 'feedback_ajax');	return false;" />
								<xsl:if test="page/variables/rn">
									<xsl:variable name="rn" select="page/variables/rn"/>
									<input type="hidden" name="rn" value="{$rn}"/>
									<input type="hidden" name="{$f/room_type/@input}" value="{//room[@id = $rn]/name}"/>
								</xsl:if>
								<input type="text" name="company" style="display:none;"/>
							</div>
						</div>
					</form>
				</xsl:if>
			</div>



		</div>



	</xsl:template>


</xsl:stylesheet>