<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Расчет потеть давления'"/>
	<xsl:variable name="critical_item" select="page"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:template name="CONTENT">
	<div class="w-section top-bottom-padding">
		<div class="w-container bg">
			<div class="path-line">
				<a class="path-link" href="{$base}">Главная страница</a>
				→
				<a class="path-link" href="{page/catalog_link}">Каталог продукции</a>
				→
			</div>
			<h2 class="first-heading page-heading">Расчет потерь давления</h2>
			<div class="text-content">
			<script>
				function roundNumber(num, dec) 
				{
					var result = Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
					return result;
				}

				function count()
				{
					xiOptions = document.getElementById("xi");
					ROptions = document.getElementById("R");
					
					xiString = xiOptions.options[xiOptions.selectedIndex].value;
					xiArray = xiString.split(";");
					var xi = xiArray[0] - 0;
					var Dy = xiArray[1] - 0;
					R  = ROptions.options[ROptions.selectedIndex].value - 0;
					Qn = document.getElementById("Qn").value - 0;
					P  = document.getElementById("P").value - 0;
					t  = document.getElementById("t").value - 0;
				    	// Перевод в кГ/см2
				    	P  = P / 100 * 1.02;
				
					Tabs  = 273 + t;
					Q     = Qn / (P + 1);
					gamma = 10333 * (P + 1) / (R * Tabs);
					dP    = (xi * gamma *  Q * Q) / (0.0157 * Dy * Dy * Dy * Dy);
					
					document.getElementById("result").innerHTML = roundNumber(dP, 2);
					document.getElementById("result_block").style.display = "block";
					dataLayer.push({'event':'Расчет потерь давления - отправлено'});
				}
				
				function setDy()
				{
					xiOptions = document.getElementById("xi");
					xiString = xiOptions.options[xiOptions.selectedIndex].value;
					document.getElementById("Dy").innerHTML = xiString.split(";")[1];
				}
			</script>
			<form>
				<table class="calc_input" style="border-color:#ffffff;">
					<tr>
						<td>
							Тип рассчитываемой арматуры:
						</td>
						<td>
							<select name="xi" id="xi" size="1" onchange="setDy()">
								<optgroup label="Фильтр">
									<option value="2.9;15">ФН&#189;</option>
									<option value="2.3;20">ФН&#190;</option>
									<option value="2.2;25">ФН1 муфт.</option>
									<option value="3.1;25">ФН1 фл.</option>
									<option value="7.1;40">ФН1&#189;
									</option>
									<option value="5.9;50">ФН2</option>
									<option value="3.1;65">ФН2&#189;
									</option>
									<option value="3.5;80">ФН3</option>
									<option value="5.6;100">ФН4</option>
									<option value="2.5;150">ФН6</option>
									<option value="3.5;200">ФН8</option>
								</optgroup>
								<optgroup label="Клапан отсечной">
									<option value="5.6;15">ВН&#189;Н
									</option>
									<option value="5.9;20">ВН&#190;Н
									</option>
									<option value="9.0;25">ВН1Н муфт.</option>
									<option value="6.5;25">ВН1Н фл.</option>
									<option value="10.7;40">ВН1&#189;Н
									</option>
									<option value="13.2;50">ВН2Н</option>
									<option value="10.5;65">ВН2&#189;Н
									</option>
									<option value="10.7;80">ВН3Н</option>
									<option value="12.5;100">ВН4Н</option>
									<option value="10;150">ВН6Н</option>
									<option value="10;200">ВН8Н</option>
								</optgroup>
								<optgroup label="Клапан отсечной с регулятором">
									<option value="10.8;40">ВН1&#189;М
									</option>
									<option value="14.8;50">ВН2М</option>
									<option value="15.4;65">ВН2&#189;М
									</option>
									<option value="16.6;80">ВН3М</option>
									<option value="18.5;100">ВН4М</option>
									<option value="14.5;150">ВН6М</option>
									<option value="14.5;200">ВН8М</option>
								</optgroup>
							</select>
						</td>
					</tr>
					<tr>
						<td>
							Диаметр номинальный DN, мм:
						</td>
						<td id="Dy"></td>
					</tr>
					<tr>
						<td>
							Давление на входе Р, кПа:
						</td>
						<td>
							<input name="P" id="P" type="text" />
						</td>
					</tr>
					<tr>
						<td>
							Расход Qн, нм&#252;/ч:
						</td>
						<td>
							<input name="Qn" id="Qn" type="text" />
						</td>
					</tr>
					<tr>
						<td>
							Тип газа
						</td>
						<td>
							<select name="R" id="R" size="1">
								<option value="52.8">Метан (природный газ)</option>
								<option value="29.27">Воздух</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>
							Температура окружающей среды, С:
						</td>
						<td>
							<input name="t" id="t" type="text" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<input type="button" value="Рассчитать" onclick="count()" class="send_button"/>
						</td>
					</tr>
				</table>
			</form>
			<div id="result_block" style="display:none">
				<hr />
				<div class="title_2">Результат расчета:</div>
				Потери давления
				<strong>
					Р=
					<strong id="result">1.83</strong>
					кПа
				</strong>
			</div>			
		</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>