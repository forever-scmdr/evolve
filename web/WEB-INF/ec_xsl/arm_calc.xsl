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

	<xsl:variable name="title" select="'Подбор арматуры'"/>
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
			<h2 class="first-heading page-heading">Подбор арматуры</h2>
			<div class="text-content">
			<script>
				function roundNumber(num, dec) 
				{
					var result = Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
					return result;
				}

				function count()
				{
					var armArray = Array;
					armArray["filter"] = ["2.9;15", "2.3;20", "2.2;25", "3.1;25", "7.1;40", "5.9;50", "3.1;65", "3.5;80", "5.6;100", "2.5;150", "3.5;200"];
					armArray["klapan"] = ["5.6;15", "5.9;20", "9.0;25", "6.5;25", "10.7;40", "13.2;50", "10.5;65", "10.7;80", "12.5;100", "10;150", "10;200"];
					armArray["klapreg"] = ["10.8;40", "14.8;50", "15.4;65", "16.6;80", "18.5;100", "14.5;150", "14.5;200"];
					
					xiOptions = document.getElementById("xi");
					ROptions = document.getElementById("R");
					
					xiDyArray = armArray[xiOptions.options[xiOptions.selectedIndex].value];
					R  = ROptions.options[ROptions.selectedIndex].value - 0;
					Qn = document.getElementById("Qn").value - 0;
					P  = document.getElementById("P").value - 0;
					t  = document.getElementById("t").value - 0;
				    // Перевод в кГ/см2
				    P  = P / 100 * 1.02;

					Tabs  = 273 + t;
					Q     = Qn / (P + 1);
					gamma = 10333 * (P + 1) / (R * Tabs);
					<xsl:text disable-output-escaping="yes">
					resultTable = "&lt;table&gt;&lt;tr&gt;&lt;td&gt;Условный проход Ду, мм&lt;/td&gt;&lt;td&gt;Потери давления Р, кПа&lt;/td&gt;&lt;/tr&gt;";
					for (i = 0; i &lt; xiDyArray.length; i++)
					{
						xiDy = xiDyArray[i].split(";");
						xi = xiDy[0] - 0;
						Dy = xiDy[1] - 0;
						dP = (xi * gamma *  Q * Q) / (0.0157 * Dy * Dy * Dy * Dy);
						PkPA = P / 1.02 * 100;
						if (dP &lt; PkPA)
						{
							resultTable += "&lt;tr&gt;&lt;td&gt;" + Dy + "&lt;/td&gt;&lt;td&gt;" + roundNumber(dP, 2) + "&lt;/td&gt;&lt;/tr&gt;";
						}					
					}
					resultTable += "&lt;/table&gt;";
					</xsl:text>
					document.getElementById("result").innerHTML = resultTable;
					document.getElementById("result_block").style.display = "block";
					dataLayer.push({'event':'Подбор арматуры - отправлено'});
				}
			</script>
			<form>
				<table class="calc_input" style="border-color:#ffffff;">
					<tr>
						<td>
							Тип рассчитываемой арматуры:
						</td>
						<td>
							<select name="xi" id="xi" size="1">
								<option value="filter">Фильтр</option>
								<option value="klapan">Клапан отсечной</option>
								<option value="klapreg">Клапан отсечной с регулятором</option>
							</select>
						</td>
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
							Расход Qн, нм*3/ч:
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
							<input type="button" value="Подобрать" onclick="count()" class="send_button"/>
						</td>
					</tr>
				</table>
			</form>
			<div id="result_block" style="display:none">
				<hr />
				<div class="title_2" style="font-size: 24px; font-weight: bold; margin: 40px 0 12px;">Результат расчета:</div>
				<div id="result" class="clearfix">
					<div style="clear: both;"></div>
				</div>
			</div>			
		</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>