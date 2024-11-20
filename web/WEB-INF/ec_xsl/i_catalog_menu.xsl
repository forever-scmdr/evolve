<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="prod" select="false()"/>


	<!-- ****************************          МЕНЮ            **************************** -->



	<xsl:template match="main_section[@id = $msec/@id]">
		<xsl:variable name="active" select="not($sec)"/>
		<li class="list-group-item">
			<a href="{show_section}" class="{'active-link'[$active]}"><xsl:value-of select="name"/></a>
			<xsl:apply-templates select="section" mode="second"/>
		</li>
	</xsl:template>
	
	<xsl:template match="main_section[not(@id = $msec/@id)]">
		<li class="list-group-item">
			<a href="{show_section}"><xsl:value-of select="name"/></a>
		</li>
	</xsl:template>
	
	<xsl:template match="section[.//@id = $sec/@id]" mode="second">
		<xsl:variable name="active" select="@id = $sec/@id"/>
		<a class="level-2{' active-link'[$active]}" href="{show_section}"><xsl:value-of select="name"/></a>
		<xsl:apply-templates select="section" mode="third"/>
	</xsl:template>
	
	<xsl:template match="section" mode="second">
		<a class="level-2" href="{show_section}"><xsl:value-of select="name"/></a>
	</xsl:template>
	
	<xsl:template match="section[@id = $sec/@id]" mode="third">
		<xsl:variable name="active" select="@id = $sec/@id"/>
		<a class="level-3{' active-link'[$active]}" href="{show_section}"><xsl:value-of select="name"/></a>
	</xsl:template>
	
	<xsl:template match="section" mode="third">
		<a class="level-3" href="{show_section}"><xsl:value-of select="name"/></a>
	</xsl:template>



	<!-- ****************************          СТРАНИЦА            **************************** -->
	


	<xsl:template name="FILTER"/>
	
	<xsl:template name="INNER_CONTENT"/>

	<xsl:template name="CONTENT">
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs">
				<h1 class="no-top-margin">Продукция</h1>
				<ul class="list-group side-menu">
					<xsl:apply-templates select="page/catalog/main_section"/>
					<li class="list-group-item">
						<a href="{page/new_products_link}" class="{'active-link'[current()/page/@name = ('new_products', 'i-new_products')]}">Новинки</a>
					</li>
				</ul>
				<div class="hidden-sm">
					<h5>Помощь в подборе продукции:</h5>
					<a class="btn btn-default btn-block" type="button" data-toggle="modal" data-target="#pressure_calc">Расчет потерь давления</a>
					<a class="btn btn-default btn-block" type="button" data-toggle="modal" data-target="#arm_calc">Подбор клапана</a>
				</div>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-9">
				<div class="row">
					<div class="col-xs-12 hidden-sm hidden-md hidden-lg">
						<div class="btn-group btn-group-justified" role="group">
							<a type="button" class="btn btn-primary" data-toggle="modal" data-target="#cat_side_menu">Меню каталога</a>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{page/catalog_link}">Каталог продукции</a>
							→
							<xsl:if test="$sec">
								<a href="{$msec/show_section}"><xsl:value-of select="$msec/name"/></a>
								→
								<xsl:for-each select="page/catalog//section[.//section/@id = $sec/@id or ($prod and .//@id = $sec/@id)]">
									<a href="{show_section}"><xsl:value-of select="name"/></a>
									→
								</xsl:for-each>
							</xsl:if>
						</div>
						<h2 class="no-top-margin"><xsl:value-of select="if ($prod) then $prod/name else if ($sec) then $sec/name else $msec/name"/></h2>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<div class="btn-toolbar">
							<xsl:if test="$msec">
								<div class="btn-group btn-group-sm hidden-xs hidden-sm">
									<a class="btn btn-default" type="button" data-toggle="modal" data-target="#device_purpose">Предназначение</a>
									<a class="btn btn-default" type="button" data-toggle="modal" data-target="#name_structure">
										Структура <xsl:value-of select="if ($sec and $sec/serial_sample and $sec/serial_sample != '') then $sec/serial_sample else $msec/serial_sample" />
									</a>
								</div>
								<div class="btn-group btn-group-sm hidden-xs hidden-sm">
									<xsl:call-template name="FEEDBACK_BUTTON_1"/>
									<xsl:call-template name="DOC_BUTTON_1"/>
								</div>
							</xsl:if>
						</div>
						<div class="btn-group btn-group-justified hidden-md hidden-lg" role="group">
							<a class="btn btn-default" type="button" data-toggle="collapse" data-target="#q11-filter">Фильтр</a>
							<div class="btn-group" role="group">
								<a class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
									Информация
									<span class="caret"></span>
								</a>
								<ul class="dropdown-menu dropdown-menu-right">
									<li>
										<a href="#" data-toggle="modal" data-target="#device_purpose">Предназначение</a>
									</li>
									<li>
										<a href="#" data-toggle="modal" data-target="#name_structure">
										Структура <xsl:value-of select="if ($sec and $sec/serial_sample and $sec/serial_sample != '') then $sec/serial_sample else $msec/serial_sample" />
										</a>
									</li>
									<li role="separator" class="divider"></li>
									<li>
										<a href="#" data-toggle="modal" data-target="#pressure_calc">Расчет потерь давления</a>
									</li>
									<li>
										<a href="#" data-toggle="modal" data-target="#arm_calc">Подбор клапана</a>
									</li>
									<li role="separator" class="divider"></li>
									<li>
										<xsl:call-template name="FEEDBACK_BUTTON_2"/>
									</li>
									<li>
										<xsl:call-template name="DOC_BUTTON_2"/>
									</li>
								</ul>
							</div>
						</div>
						<!-- Фильтр -->

						<xsl:call-template name="FILTER"/>

						<!-- /Фильтр -->
					</div>
				</div>
				
				<xsl:call-template name="INNER_CONTENT"/>
				
			</div>
		</div>
	</div>
</xsl:template>



<xsl:template name="INNER_POPUPS"/>


<xsl:template name="POPUPS">
	<!-- modal меню каталога -->
	<div class="modal fade" id="cat_side_menu" tabindex="-1">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Каталог продукции</h4>
				</div>
				<div class="modal-body">
					<ul class="list-group side-menu">
						<xsl:apply-templates select="page/catalog/main_section"/>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<!-- modal меню каталога -->
	
    <!-- modal структура наименования -->
	<div class="modal fade" id="name_structure" tabindex="-1">
		<div class="modal-dialog modal-md">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">
						Структура наименования
						<xsl:value-of select="if ($sec and $sec/serial_sample and $sec/serial_sample != '') then $sec/serial_sample else $msec/serial_sample" />
					</h4>
				</div>
				<div class="modal-body">
					<xsl:value-of select="if ($sec and $sec/text and $sec/text != '') then $sec/text else $msec/text" disable-output-escaping="yes" />
				</div>
			</div>
		</div>
	</div>
    <!-- modal структура наименования -->

    <!-- modal предназначение -->
	<div class="modal fade" id="device_purpose" tabindex="-1">
		<div class="modal-dialog modal-md">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Предназначение</h4>
				</div>
				<div class="modal-body">
					<xsl:value-of select="if ($sec and $sec/use and $sec/use != '') then $sec/use else $msec/use" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</div>
    <!-- modal предназначение -->
    
    <!-- modal расчет потерь давления -->
	<div class="modal fade" id="pressure_calc" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Расчет потерь давления</h4>
				</div>
				<div class="modal-body">
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
						}
						
						function setDy()
						{
							xiOptions = document.getElementById("xi");
							xiString = xiOptions.options[xiOptions.selectedIndex].value;
							document.getElementById("Dy").innerHTML = xiString.split(";")[1];
						}
					</script>
					<form>
						<div class="form-group">
							<label for="xi">Тип арматуры</label>
							<select class="form-control" name="xi" id="xi" onchange="setDy()">
								<optgroup label="Фильтр">
									<option value="2.9;15">ФН&#189;</option>
									<option value="2.3;20">ФН&#190;</option>
									<option value="2.2;25">ФН1 муфт.</option>
									<option value="3.1;25">ФН1 фл.</option>
									<option value="7.1;40">ФН1&#189;</option>
									<option value="5.9;50">ФН2</option>
									<option value="3.1;65">ФН2&#189;</option>
									<option value="3.5;80">ФН3</option>
									<option value="5.6;100">ФН4</option>
									<option value="2.5;150">ФН6</option>
									<option value="3.5;200">ФН8</option>
								</optgroup>
								<optgroup label="Клапан отсечной">
									<option value="5.6;15">ВН&#189;Н</option>
									<option value="5.9;20">ВН&#190;Н</option>
									<option value="9.0;25">ВН1Н муфт.</option>
									<option value="6.5;25">ВН1Н фл.</option>
									<option value="10.7;40">ВН1&#189;Н</option>
									<option value="13.2;50">ВН2Н</option>
									<option value="10.5;65">ВН2&#189;Н</option>
									<option value="10.7;80">ВН3Н</option>
									<option value="12.5;100">ВН4Н</option>
									<option value="10;150">ВН6Н</option>
									<option value="10;200">ВН8Н</option>
								</optgroup>
								<optgroup label="Клапан отсечной с регулятором">
									<option value="10.8;40">ВН1&#189;М</option>
									<option value="14.8;50">ВН2М</option>
									<option value="15.4;65">ВН2&#189;М</option>
									<option value="16.6;80">ВН3М</option>
									<option value="18.5;100">ВН4М</option>
									<option value="14.5;150">ВН6М</option>
									<option value="14.5;200">ВН8М</option>
								</optgroup>
							</select>
						</div>
						<div class="form-group">
							<label>Номиранльный диаметр DN, мм</label>
							<p id="Dy"></p>
						</div>
						<div class="form-group">
							<label>Давление на входе Р, кПа</label>
							<input name="P" id="P" type="text" class="form-control"/>
						</div>
						<div class="form-group">
							<label>Расход Qн, нм*3/ч</label>
							<input name="Qn" id="Qn" type="text" class="form-control"/>
						</div>
						<div class="form-group">
							<label>Тип газа</label>
							<select name="R" id="R" class="form-control">
								<option value="52.8">Метан (природный газ)</option>
								<option value="29.27">Воздух</option>
							</select>
						</div>
						<div class="form-group">
							<label>Температура окружающей среды, С</label>
							<input name="t" id="t" type="text" class="form-control"/>
						</div>

						<button type="button" class="btn btn-primary" onclick="count()">Рассчитать</button>
					</form>
					<div class="form-group" id="result_block" style="display:none">
						<hr />
						<label>Результат расчета: Потери давления</label>
						<strong>Р=<strong id="result">1.83</strong>кПа</strong>
					</div>
				</div>
			</div>
		</div>
	</div>
    <!-- modal расчет потерь давления -->

	<!-- modal подбор артматуры расчет -->
	<div class="modal fade" id="arm_calc" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Подбор арматуры</h4>
				</div>
				<div class="modal-body">
					<script>
						function roundNumber_a(num, dec) 
						{
							var result = Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
							return result;
						}
		
						function count_a()
						{
							var armArray = Array;
							armArray["filter"] = ["2.9;15", "2.3;20", "2.2;25", "3.1;25", "7.1;40", "5.9;50", "3.1;65", "3.5;80", "5.6;100", "2.5;150", "3.5;200"];
							armArray["klapan"] = ["5.6;15", "5.9;20", "9.0;25", "6.5;25", "10.7;40", "13.2;50", "10.5;65", "10.7;80", "12.5;100", "10;150", "10;200"];
							armArray["klapreg"] = ["10.8;40", "14.8;50", "15.4;65", "16.6;80", "18.5;100", "14.5;150", "14.5;200"];
							
							xiOptions = document.getElementById("xi_a");
							ROptions = document.getElementById("R_a");
							
							xiDyArray = armArray[xiOptions.options[xiOptions.selectedIndex].value];
							R  = ROptions.options[ROptions.selectedIndex].value - 0;
							Qn = document.getElementById("Qn_a").value - 0;
							P  = document.getElementById("P_a").value - 0;
							t  = document.getElementById("t_a").value - 0;
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
							document.getElementById("result_a").innerHTML = resultTable;
							document.getElementById("result_block_a").style.display = "block";
						}
					</script>
					<form>
						<div class="form-group">
							<label for="xi_a">Тип арматуры</label>
							<select class="form-control" id="xi_a" name="xi_a">
								<option value="filter">Фильтр</option>
								<option value="klapan">Клапан отсечной</option>
								<option value="klapreg">Клапан отсечной с регулятором</option>
							</select>
						</div>
						<div class="form-group">
							<label>Давление на входе Р, кПа</label>
							<input name="P_a" id="P_a" type="text" class="form-control"/>
						</div>
						<div class="form-group">
							<label>Расход Qн, нм*3/ч</label>
							<input name="Qn_a" id="Qn_a" type="text" class="form-control"/>
						</div>
						<div class="form-group">
							<label>Тип газа</label>
							<select name="R_a" id="R_a" class="form-control">
								<option value="52.8">Метан (природный газ)</option>
								<option value="29.27">Воздух</option>
							</select>
						</div>
						<div class="form-group">
							<label>Температура окружающей среды, С</label>
							<input name="t_a" id="t_a" class="form-control"/>
						</div>

						<button type="button" onclick="count_a()" class="btn btn-primary btn-block">Рассчитать</button>
					</form>
					<div id="result_block_a" style="display:none">
						<hr />
						<label>Результат расчета:</label>
						<div id="result_a" class="clearfix">
							<div style="clear: both;"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- modal подбор артматуры расчет -->

    <!-- modal помощь в выборе -->
	<xsl:call-template name="FEEDBACK_FORM"/>
    <!-- modal помощь в выборе -->
 
    <!-- modal CAD-файл -->
	<xsl:call-template name="DOC_FORM"/>
    <!-- modal CAD-файл -->

	<xsl:call-template name="INNER_POPUPS"/>

</xsl:template>

</xsl:stylesheet>