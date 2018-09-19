<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:f="f:f">
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="now" select="current-date()"/>
	<xsl:variable name="now_quartal" select="ceiling(month-from-date($now) div 3)"/>
	<xsl:variable name="max_year" select="if ($now_quartal = 1) then year-from-date($now) - 1 else year-from-date($now)"/>
	<xsl:variable name="years" select="($max_year, $max_year - 1, $max_year - 2, $max_year - 3, $max_year - 4, $max_year - 5)"/>

	<xsl:variable name="millis_to" select="if (page/variables/m_to) then number(page/variables/m_to) else f:date_to_millis($now)"/>
	<xsl:variable name="millis_from" select="if (page/variables/m_from) then number(page/variables/m_from) else f:date_to_millis($now - 365 * xs:dayTimeDuration('P1D'))"/>

	<xsl:variable name="date_to" select="f:millis_to_date($millis_to)"/>
	<xsl:variable name="date_from" select="f:millis_to_date($millis_from)"/>

	<xsl:variable name="quartal_to" select="ceiling(month-from-date($date_to) div 3)"/>
	<xsl:variable name="quartal_from" select="ceiling(month-from-date($date_from) div 3)"/>

	<xsl:variable name="year_to" select="year-from-date($date_to)"/>
	<xsl:variable name="year_from" select="year-from-date($date_from)"/>


	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
		</xsl:text>
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<base href="{page/base}" />
				<title>Дилеры - статистика</title>
				<link rel="stylesheet" href="css/app.css" />
				<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
			</head>
			<body>
				<div class="container">
					<div class="row p-t no-print">
						<div class="col-md-10">
							<h1 class="no-m-t">Модуль статистики и аналитики</h1>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-info btn-block">Распечатать отчет</button>
						</div>
					</div>
					<div class="row p-t select-time no-print">
						<div class="col-md-12">
							<div>
								<span>Период времени</span>
								<form action="{page/set_dates}" method="post" id="dates_form" onsubmit="prepareDates()">
									<select id="q_from" value="{$quartal_from}">
										<option value="1">1 квартал</option>
										<option value="2">2 квартал</option>
										<option value="3">3 квартал</option>
										<option value="4">4 квартал</option>
									</select>
									<select id="y_from" value="{$year_from}">
										<xsl:for-each select="$years">
											<option value="{.}"><xsl:value-of select="." /></option>
										</xsl:for-each>
										<option value="1980">Без ограничений</option>
									</select> -
									<select id="q_to" value="{$quartal_to}">
										<option value="1">1 квартал</option>
										<option value="2">2 квартал</option>
										<option value="3">3 квартал</option>
										<option value="4">4 квартал</option>
									</select>
									<select id="y_to" value="{$year_to}">
										<xsl:for-each select="$years">
											<option value="{.}"><xsl:value-of select="." /></option>
										</xsl:for-each>
										<option value="3000">Без ограничений</option>
									</select>
									<input type="hidden" name="m_from" value="{page/variables/m_from}"/>
									<input type="hidden" name="m_to" value="{page/variables/m_to}"/>
									<button type="submit" class="btn btn-default btn-sm">Применить</button>
								</form>
								<script>
									function prepareDates() {
										var datesForm = $('#dates_form');
										var q_from = $('#q_from').val();
										var q_to = $('#q_to').val();
										var y_from = $('#y_from').val();
										var y_to = $('#y_to').val();
										var date_from = new Date(y_from, q_from * 3 - 1);
										var date_to = new Date(y_to, q_to * 3 - 1);
										datesForm.find('input[name=m_from]').eq(0).val(date_from.getTime());
										datesForm.find('input[name=m_to]').eq(0).val(date_to.getTime());
									};
								</script>
							</div>
						</div>
					</div>
					<div class="row p-t no-print">
						<div class="col-md-12">
							<ul class="nav nav-tabs" role="tablist">
								<li class="active"><a href="#" >Дилеры</a></li>
								<li><a href="#">Контрагенты</a></li>
								<li><a href="#">Товары</a></li>
							</ul>
						</div>
					</div>
					<div class="row p-t-small no-print">
						<div class="col-md-12">
							<button type="button" class="btn btn-default btn-sm">Подбор по параметрам</button>
							<div class="search">
								<input type="text" value="Поиск по названию"/>
								<button type="button" class="btn btn-default btn-sm">Найти</button>
							</div>
						</div>
						<div class="col-md-12">
							<div class="parameters-container m-t-small no-print" style="display: block;">
								<h3 class="no-m-t m-b">Подбор по параметрам</h3>
								<div class="parameters">
									<div class="parameter">
										<div>Страна:</div>
										<div>
											<form action="{page/base_link}" method="post">
												<input list="countries" type="text" name="country"/>
												<datalist id="countries">
													<xsl:for-each select="page/country/country">
														<option value="{.}"></option>
													</xsl:for-each>
												</datalist>
												<button type="submit" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm" data-toggle="modal"
												        data-target="#country_list">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values">
											<xsl:for-each select="page/variables/country">
												<a href="{//page/remove_country_base}{.}"><xsl:value-of select="." /></a>
											</xsl:for-each>
										</div>
									</div>
									<div class="parameter">
										<div>Регион:</div>
										<div>
											<form action="">
												<input list="regions" type="text"/>
												<datalist id="regions">
													<option value="Витебская обл."></option>
													<option value="Минская обл."></option>
													<option value="Гомельская обл."></option>
													<option value="Могилевская обл."></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values">
											<a href="">Витебская обл.</a>
											<a href="">Минская обл.</a>
										</div>
									</div>
									<div class="parameter">
										<div>Город:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Дилер:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Контрагент:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Сфера:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Отрасль:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Тип товара:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values">
											<a href="">Электромагнитные клапаны</a>
										</div>
									</div>
									<div class="parameter">
										<div>Вид товара:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
									<div class="parameter">
										<div>Товар:</div>
										<div>
											<form action="">
												<input list="parameter-3" type="text"/>
												<datalist id="parameter-3">
													<option value="1"></option>
													<option value="2"></option>
													<option value="3"></option>
													<option value="4"></option>
												</datalist>
												<button type="button" class="btn btn-default btn-sm">Добавить</button>
												<button type="button" class="btn btn-default btn-sm">Выбрать из списка</button>
											</form>
										</div>
										<div class="chosen-values"></div>
									</div>
								</div>
								<button type="button" class="btn btn-success">Подобрать по параметрам</button>
								<button type="button" class="btn btn-info">Отменить</button>
							</div>
						</div>
					</div>
					<div class="row p-t">
						<div class="col-md-12">
							<h2>Статистика продаж дилеров с 1 кв. 2016 по 3 кв. 2017</h2>
							<div class="parameters">
								<div class="parameter">
									<div>Страна:</div>
									<div class="chosen-values">
										<a href="">Россия</a>
										<a href="">Беларусь</a>
										<a href="">Казахстан</a>
									</div>
								</div>
								<div class="parameter">
									<div>Регион:</div>
									<div class="chosen-values">
										<a href="">Витебская обл.</a>
										<a href="">Минская обл.</a>
									</div>
								</div>
								<div class="parameter">
									<div>Тип товара:</div>
									<div class="chosen-values">
										<a href="">Электромагнитные клапаны</a>
									</div>
								</div>
							</div>
							<div class="table-responsive">
								<table class="data-table main-table">
									<tr>
										<th class="no-print"><input type="checkbox"/></th>
										<th>№</th>
										<th>Организация</th>
										<th>Страна</th>
										<th>Город</th>
										<th>1 кв. 2016</th>
										<th>2 кв. 2016</th>
										<th>3 кв. 2016</th>
										<th>4 кв. 2016</th>
										<th>за 2016</th>
										<th>1 кв. 2017</th>
										<th>2 кв. 2017</th>
										<th>3 кв. 2017</th>
										<th>4 кв. 2017</th>
										<th>за 2017</th>
										<th>Всего</th>
									</tr>
									<tr>
										<td class="no-print"><input type="checkbox"/></td>
										<td>1.</td>
										<td>ООО НПФ Раско</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="checked">
										<td class="no-print"><input type="checkbox" checked="checked"/></td>
										<td>2.</td>
										<td>ООО НПФ Раско</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td class="no-print"><input type="checkbox"/></td>
										<td>3.</td>
										<td>ООО НПФ Раско</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="summary">
										<td class="no-print"><input type="checkbox"/></td>
										<td></td>
										<td>Итого:</td>
										<td></td>
										<td></td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
								</table>
							</div>
							<div class="subtable-controls no-print">
								<select name="" id="">
									<option value="">Все строки</option>
									<option value="">Итого</option>
								</select>
								<button type="button" class="btn btn-default btn-sm">Построить график</button>
								<button type="button" class="btn btn-default btn-sm">Показать детализацию</button>

							</div>
						</div>
					</div>
					<div class="row p-t details">
						<div class="col-md-12">
							<h2>Детализация</h2>
							<div class="no-print">
								<select name="" id="">
									<option value="">Общая для всех</option>
									<option value="">По дилерам</option>
								</select>
								<select name="" id="">
									<option value="">Тип, вид, товары</option>
									<option value="">Тип, вид</option>
									<option value="">Тип</option>
									<option value="">Контрагенты</option>
								</select>&#160;
								<label class="checkbox-inline"><!-- Только если выбран пункт Контрагенты -->
									<input type="checkbox" id="inlineCheckbox2" value="option2"/>Разбить по контрагентам
								</label>
							</div>
							<h3 class="p-t-small">Все дилеры из выборки</h3>
							<p>1 кв. 2016 - 3 кв. 2017</p>
							<div class="table-responsive">
								<table class="data-table">
									<tr>
										<th>№</th>
										<th>Организация</th>
										<th>Страна</th>
										<th>Город</th>
										<th>1 кв. 2016</th>
										<th>2 кв. 2016</th>
										<th>3 кв. 2016</th>
										<th>4 кв. 2016</th>
										<th>за 2016</th>
										<th>1 кв. 2017</th>
										<th>2 кв. 2017</th>
										<th>3 кв. 2017</th>
										<th>4 кв. 2017</th>
										<th>за 2017</th>
										<th>Всего</th>
									</tr>
									<tr>
										<td>1.</td>
										<td>Автоматикапро</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>2.</td>
										<td>Белгазналадка</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>3.</td>
										<td>Брандстройпроект</td>
										<td>РФ</td>
										<td>Москва</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="summary">
										<td></td>
										<td>Итого:</td>
										<td></td>
										<td></td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
								</table>
							</div>
							<div class="subtable-controls no-print">
								<select name="" id="">
									<option value="">Все строки</option>
									<option value="">Итого</option>
								</select>
								<button type="button" class="btn btn-default btn-sm">Построить график</button>
								<button type="button" class="btn btn-info btn-sm">Удалить график</button>
							</div>
							<div class="chart m-t-small">
								<h3 class="no-m-t">Статистика продаж дилеров с 1 кв. 2016 по 3 кв. 2017</h3>
								<p>Надо выводить настройки фильтрации, чтобы было понятно, кто выбран.</p>
							</div>
							<h3 class="p-t">НПФ Раско → Белэнергокомплект</h3>
							<p>1 кв. 2016 - 3 кв. 2017</p>
							<div class="table-responsive">
								<table class="data-table">
									<tr>
										<th>Название</th>
										<th>1 кв. 2016</th>
										<th>2 кв. 2016</th>
										<th>3 кв. 2016</th>
										<th>4 кв. 2016</th>
										<th>за 2016</th>
										<th>1 кв. 2017</th>
										<th>2 кв. 2017</th>
										<th>3 кв. 2017</th>
										<th>4 кв. 2017</th>
										<th>за 2017</th>
										<th>Всего</th>
									</tr>
									<tr class="accent">
										<td>Клапаны</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="accent">
										<td>- <strong>DN15</strong></td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Клапан ВН1/2В-1К</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Клапан ВН1/2В-1КЕ</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Клапан ВН1/2В-1КЕ=24В</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Клапан ВН1/2Н-0,2</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Клапан ВН1/2Н-0,2  =24В</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="accent">
										<td><strong>Фильтры</strong></td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Фильтр ФН1 1/2-1 м</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Фильтр ФН1 1/2-2</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Фильтр ФН1 1/2-2 УХЛ1</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Фильтр ФН1 1/2-2 фл.</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr>
										<td>- - Фильтр ФН1 1/2-2 фл. СТ.</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
									<tr class="summary">
										<td>Итого:</td>
										<td>123</td>
										<td>157</td>
										<td>201</td>
										<td>120</td>
										<td>601</td>
										<td>165</td>
										<td>183</td>
										<td>195</td>
										<td>-</td>
										<td>543</td>
										<td>944</td>
									</tr>
								</table>
							</div>
							<div class="subtable-controls no-print">
								<select name="" id="">
									<option value="">Все строки</option>
									<option value="">Итого</option>
								</select>
								<button type="button" class="btn btn-default btn-sm">Построить график</button>
							</div>



							<h1>Продажи (<xsl:value-of select="count(page/sale)" />)</h1>
							<div class="table-responsive">
								<table class="data-table">
									<tr>
										<th>Дилер</th>
										<th>Покупатель</th>
										<th>Дата</th>
										<th>Девайс</th>
									</tr>
									<xsl:for-each select="page/sale">
										<tr class="summary">
											<td><xsl:value-of select="dealer_code"/></td>
											<td><xsl:value-of select="agent_plain_name"/></td>
											<td><xsl:value-of select="register_date"/></td>
											<td><xsl:value-of select="device"/></td>
										</tr>
									</xsl:for-each>
								</table>
							</div>


							
							
						</div>
					</div>
				</div>


				<!-- modals -->


				<div id="country_list" class="modal fade" tabindex="-1" role="dialog">
					<div class="modal-dialog modal-sm" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button"  class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
								<h4 class="modal-title">Страна</h4>
							</div>
							<form action="{page/base_link}" method="post">
								<div class="modal-body list">
									<xsl:for-each select="page/country/country">
										<div class="checkbox"><label><input type="checkbox" name="country" value="{.}"/><xsl:value-of select="."/></label></div>
									</xsl:for-each>
								</div>
								<div class="modal-footer">
									<button type="submit"  class="btn btn-default btn-block">Добавить выбранное</button>
								</div>
							</form>
						</div>
					</div>
				</div>

				<script src="js/bootstrap.min.js"></script>
				<xsl:call-template name="SELECT_SCRIPT"/>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
