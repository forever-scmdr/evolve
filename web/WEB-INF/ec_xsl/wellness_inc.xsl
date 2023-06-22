<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="utils_inc.xsl" />
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="well" select="$content/module"/>
	<xsl:variable name="form" select="$content/wellness_form"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_success" select="not(page/variables/error = 'true')"/>

	<xsl:template match="info" mode="wellness">
		<xsl:apply-templates select="$content" mode="content"/>
		<h3>Веллнес-модули:</h3>
		<div class="row">
			<xsl:for-each select="$well">
				<div class="col-md-3 col-sm-6 col-xs-6">
					<a href="" class="section-thumbnail" style="background-image: url({@path}{main_pic});" data-toggle="modal" data-target="#modal-wellness-{@id}"></a>
					<a href="" data-toggle="modal" data-target="#modal-wellness-description"><h5>Модуль <xsl:value-of select="number"/>: <xsl:value-of select="name"/></h5></a>
				</div>
				<div class="modal fade" id="modal-wellness-{@id}" tabindex="-1" role="dialog">
					<div class="modal-dialog modal-md" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">×</span>
								</button>
								<h4 class="modal-title">Описание</h4>
							</div>
							<div class="modal-body">
								<div class="image-container">
									<img src="{@path}{main_pic}" alt=""/>
								</div>
								<h3>Модуль №<xsl:value-of select="number"/>. <xsl:value-of select="name"/></h3>
								<div class="table-responsive">
									<table class="bordered">
										<tr>
											<td style="width: 50%">1-й день</td>
											<td>2-й день</td>
										</tr>
										<tr>
											<td>
												<xsl:value-of select="first_day" disable-output-escaping="yes"/>
											</td>
											<td>
												<xsl:value-of select="second_day" disable-output-escaping="yes"/>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
				<xsl:if test="position() mod 4 = 0">
					<div class="clearfix"></div>
				</xsl:if>
			</xsl:for-each>
		</div>
		<h2 style="margin-bottom: 30px;">Формирование своей веллнес-программы</h2>
		<div class="row">
			<div class="col-md-5">
				<form action="{$form/submit}" enctype="multipart/form-data" method="post">
					<h3 style="margin-top: 0;">Выбор модулей:</h3>
					<div class="form-group">
						<label>Длительность вашей путевки, дней:</label>
						<select id="days_select" name="days" class="form-control" value="{$form/extra[@input='days']}">
							<option>Выберите</option>
							<option>4</option>
							<option>6</option>
							<option>8</option>
							<option>10</option>
							<option>12</option>
							<option>14</option>
							<option>16</option>
							<option>18</option>
							<option>20</option>
							<option>22</option>
							<option>24</option>
							<option>26</option>
						</select>
					</div>
					<div class="form-group">
						<label for="">Дата заезда:</label>
						<input type="text" name="{$form/check_in_date/@input}" value="{$form/check_in_date}" class="form-control" id="" placeholder=""/>
					</div>
					<xsl:for-each select="(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39)">
						<xsl:variable name="day" select="."/>
						<xsl:variable name="value" select="$form/extra[@input=concat('day_', $day)]"/>
						<div id="wday{$day}" class="form-group wday" style="{'display: none'[not($value) or $value = '']}">
							<label>День <xsl:value-of select="$day"/>–<xsl:value-of select="$day + 1"/>:</label>
							<select id="wsel{$day}" name="day_{$day}" class="form-control" value="{$value}">
								<option value="">Выберите модуль</option>
								<option value="-">-Нет модуля-</option>
								<xsl:for-each select="$well">
									<option>№<xsl:value-of select="number"/><xsl:text> </xsl:text><xsl:value-of select="name"/></option>
								</xsl:for-each>
							</select>
						</div>
					</xsl:for-each>
					<h3 style="margin-top: 50px;" id="wform">Ваши контактные данные:</h3>
					<div class="form-group">
						<label for="">Ф.И.О.</label>
						<input type="text" name="{$form/first_name/@input}" value="{$form/first_name}" class="form-control" id="" placeholder=""/>
					</div>
					<div class="form-group">
						<label for="">Гражданство</label>
						<select id="" class="form-control" name="{$form/citizen_name/@input}" value="{$form/citizen_name}">
							<option value="">Выберите гражданство</option>
							<option value="Беларусь">Беларусь</option>
							<option value="Россия">Россия</option>
							<option value="Казахстан">Казахстан</option>
							<option value="Другое">Другое</option>
						</select>
					</div>
					<div class="form-group">
						<label for="">Дата рождения:</label>
						<input type="text" name="{$form/birth_date/@input}" value="{$form/birth_date}" class="form-control" id="" placeholder=""/>
					</div>
					<div class="form-group">
						<label for="">Адрес эл. почты:</label>
						<input type="text" name="{$form/email/@input}" value="{$form/email}" class="form-control" id="" placeholder=""/>
					</div>
					<div class="form-group">
						<label for="">Номер телефона:</label>
						<input type="text" name="{$form/phone/@input}" value="{$form/phone}" class="form-control" id="" placeholder=""/>
					</div>
					<!-- <div class="form-group">
						<label for="">Дата заезда:</label>
						<input type="text" name="{$form/check_in_date/@input}" value="{$form/check_in_date}" class="form-control" id="" placeholder=""/>
					</div> -->
					<xsl:if test="$message">
						<xsl:if test="$is_success">
							<div id="wmessage" class="alert alert-success" role="alert"><strong>Заявка отправлена.</strong> На вашу электронную почту должна прийти копия заявки.</div>
						</xsl:if>
						<xsl:if test="not($is_success)">
							<div id="wmessage" class="alert alert-danger" role="alert"><strong>Внимание!</strong> Заполните все поля.</div>
						</xsl:if>
					</xsl:if>
					<button type="submit" class="btn btn-primary">Отправить заявку</button>
				</form>
			</div>
			<div class="col-md-7">
				<div class="well">
					<xsl:value-of select="$content/help" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="SCRIPTS">
	<script>
		<xsl:call-template name="SELECT_SCRIPT"/>
		<xsl:text disable-output-escaping="yes">
		$(document).ready(function() {
			$('#days_select').change(function() {
				for (i = $(this).val() * 1 + 1; i &lt;= 39; i += 2) {
					$('#wsel' + i).val('');
					$('#wday' + i).hide();
				}
				for (i = 1; i &lt; $(this).val() * 1; i += 2) {
					$('#wday' + i).show();
				}
			});
			if ($('#wmessage').length &gt; 0)
			$('html, body').animate({
			    scrollTop: ($('#wform').offset().top)
			},500);
		});
		</xsl:text>
	</script>
	</xsl:template>

</xsl:stylesheet>