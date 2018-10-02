<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:f="f:f">
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="group_dealers" select="page/variables/group_dealers = 'yes'"/>
	<xsl:variable name="group_agents" select="page/variables/group_agents = 'yes'"/>
	<xsl:variable name="group_tags" select="page/variables/group_tags = 'yes'"/>
	<xsl:variable name="show_devices" select="page/variables/show_devices = 'yes'"/>
	<xsl:variable name="rep_tags" select="page/tag[report = 'да']"/>

	<xsl:variable name="selected_id" select="page/variables/selected"/>
	<xsl:variable name="selected" select="page/selected"/>

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

	<xsl:variable name="years_quartals" select="tokenize(page/variables/quartals, '!')"/>

	<xsl:variable name="sales" select="page/sale"/>
	<xsl:variable name="selected_sales" select="page/sale[agent_plain_name = $selected/plain_name]"/>

	<xsl:variable name="all_dealer" select="page/all_dealer"/>

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
				<link rel="stylesheet" href="stats/c3.min.css" />
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
							<h2 class="no-m-t">
								<xsl:if test="not(page/variables/m_to)">Период не задан</xsl:if>
								<xsl:if test="page/variables/m_to">
									Отчетный период:
									<xsl:value-of select="$quartal_from"/> квартал <xsl:value-of select="$year_from"/> года -
									<xsl:value-of select="$quartal_to"/> квартал <xsl:value-of select="$year_to"/> года
								</xsl:if>
							</h2>
							<div>
								<!--<span>Период времени</span>-->
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
										var date_from = new Date(y_from, q_from * 3 - 1, 1);
										var date_to = new Date(y_to, q_to * 3 - 1, 3);
										datesForm.find('input[name=m_from]').eq(0).val(date_from.getTime());
										datesForm.find('input[name=m_to]').eq(0).val(date_to.getTime());
									};
								</script>
							</div>
						</div>
					</div>
					<xsl:if test="page/variables/m_to">
						<div class="row p-t no-print">
							<div class="col-md-12">
								<ul class="nav nav-tabs" role="tablist">
									<li><a href="{page/dealers_link}" >Дилеры</a></li>
									<li class="active"><a href="{page/agents_link}">Контрагенты</a></li>
									<li><a href="{page/products_link}">Товары</a></li>
								</ul>
							</div>
						</div>
						<div class="row p-t-small no-print">
							<div class="col-md-12">
								<button type="button" class="btn btn-default btn-sm" onclick="$('#params_form').toggle(200)">Подбор по параметрам</button>
								<!--<div class="search">-->
									<!--<input type="text" value="Поиск по названию"/>-->
									<!--<button type="button" class="btn btn-default btn-sm">Найти</button>-->
								<!--</div>-->
							</div>
							<div class="col-md-12" id="params_form">
								<div class="parameters-container m-t-small no-print" style="display: block;">
									<h3 class="no-m-t m-b">Подбор по параметрам</h3>
									<div class="parameters">
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'country'"/>
											<xsl:with-param name="header" select="'Страна'"/>
											<xsl:with-param name="list" select="page/country/country"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'region'"/>
											<xsl:with-param name="header" select="'Регион'"/>
											<xsl:with-param name="list" select="page/region/region"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'city'"/>
											<xsl:with-param name="header" select="'Город'"/>
											<xsl:with-param name="list" select="page/city/city"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'dealer'"/>
											<xsl:with-param name="header" select="'Дилер'"/>
											<xsl:with-param name="list" select="page/dealer/organization"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'agent'"/>
											<xsl:with-param name="header" select="'Контрагент'"/>
											<xsl:with-param name="list" select="page/agent/organization"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'branch'"/>
											<xsl:with-param name="header" select="'Сфера'"/>
											<xsl:with-param name="list" select="page/branch/branch"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'tag'"/>
											<xsl:with-param name="header" select="'Тип товара'"/>
											<xsl:with-param name="list" select="page/tag/name"/>
										</xsl:call-template>
										<xsl:call-template name="parameter_input">
											<xsl:with-param name="input_name" select="'device'"/>
											<xsl:with-param name="header" select="'Товар'"/>
											<xsl:with-param name="list" select="page/device/device"/>
										</xsl:call-template>
									</div>
									<!--<button type="button" class="btn btn-success">Подобрать по параметрам</button>-->
									<a href="{page/this_page_link}" class="btn btn-info">Очистить критерии</a>
								</div>
							</div>
						</div>
						<div class="row p-t">
							<div class="col-md-12">
								<h2>Статистика продаж контрагентам с
									<xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> года по
									<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/> года
								</h2>
								<div class="parameters">
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Страна'"/>
										<xsl:with-param name="input_name" select="'country'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Регион'"/>
										<xsl:with-param name="input_name" select="'region'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Город'"/>
										<xsl:with-param name="input_name" select="'city'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Дилер'"/>
										<xsl:with-param name="input_name" select="'dealer'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Контрагент'"/>
										<xsl:with-param name="input_name" select="'agent'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Сфера'"/>
										<xsl:with-param name="input_name" select="'branch'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Тип товара'"/>
										<xsl:with-param name="input_name" select="'tag'"/>
									</xsl:call-template>
									<xsl:call-template name="parameter_values">
										<xsl:with-param name="header" select="'Товар'"/>
										<xsl:with-param name="input_name" select="'device'"/>
									</xsl:call-template>
								</div>
								<form method="post" action="{page/select_link}">
									<div class="table-responsive">
										<table class="data-table main-table" id="main_table">
											<tr>
												<th class="no-print">
													<input type="checkbox"
													       onclick="$('#main_table').find('input[type=checkbox]').prop('checked', $(this).prop('checked'))"/>
												</th>
												<th>№</th>
												<th>Организация</th>
												<th>Страна</th>
												<th>Город</th>
												<xsl:for-each select="$years_quartals">
													<xsl:variable name="parts" select="tokenize(., '\*')"/>
													<th class="chart_data_x"><xsl:value-of select="$parts[1]"/>кв. <xsl:value-of select="$parts[2]"/></th>
												</xsl:for-each>
												<th>Всего</th>
											</tr>
											<xsl:for-each select="page/all_agent">
												<xsl:variable name="name" select="plain_name"/>
												<tr class="chart_data_line">
													<td class="no-print">
														<input type="checkbox" name="selected" value="{@id}">
															<xsl:if test="@id = $selected_id">
																<xsl:attribute name="checked" select="'checked'"/>
															</xsl:if>
														</input>
													</td>
													<td><xsl:value-of select="position()" />.</td>
													<td class="chart_data_header"><xsl:value-of select="organization" /></td>
													<td><xsl:value-of select="country" /></td>
													<td><xsl:value-of select="city" /></td>
													<xsl:for-each select="$years_quartals">
														<xsl:variable name="parts" select="tokenize(., '\*')"/>
														<xsl:variable name="sale" select="$sales[quartal = $parts[1] and year = $parts[2] and agent_plain_name = $name]"/>
														<td class="chart_data_point" value="{if ($sale) then sum($sale/qty) else '0'}">
															<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
														</td>
													</xsl:for-each>
													<td><xsl:value-of select="sum($sales[agent_plain_name = $name]/qty)" /></td>
												</tr>
											</xsl:for-each>
											<tr class="summary chart_data_line">
												<td class="no-print"></td>
												<td></td>
												<td class="chart_data_header">Итого:</td>
												<td></td>
												<td></td>
												<xsl:for-each select="$years_quartals">
													<xsl:variable name="parts" select="tokenize(., '\*')"/>
													<xsl:variable name="sale" select="$sales[quartal = $parts[1] and year = $parts[2]]"/>
													<td class="chart_data_point total" value="{if ($sale) then sum($sale/qty) else '0'}">
														<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
													</td>
												</xsl:for-each>
												<td><xsl:value-of select="sum($sales/qty)" /></td>
											</tr>
										</table>
									</div>
									<div class="subtable-controls no-print chart_button_container">
										<button type="button" class="btn btn-default btn-sm" onclick="createChart(this)">Подробный график</button>
										<button type="button" class="btn btn-default btn-sm" onclick="createChart(this, true)">График Итого</button>
										<button type="submit" class="btn btn-default btn-sm">Показать детализацию</button>
									</div>
									<div class="chart m-t-small" style="display: none">
										<h3 class="no-m-t">
											Статистика продаж контрагентам с
											<xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> по
											<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/>
										</h3>
									</div>
								</form>
							</div>
						</div>
						<xsl:if test="page/selected">
							<div class="row p-t details">
								<div class="col-md-12">
									<h2>Детализация</h2>
									<div class="no-print">
										<form action="{page/apply_grouping_link}" method="post">
											<label class="checkbox-inline">
												<input type="checkbox" name="group_dealers" value="yes">
													<xsl:if test="$group_dealers">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>
												Разбить по дилерам
											</label>&#160;
											<label class="checkbox-inline">
												<input type="checkbox" name="group_agents" value="yes">
													<xsl:if test="$group_agents">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>
												Разбить по контрагентам
											</label>&#160;
											<label class="checkbox-inline">
												<input type="checkbox" name="group_tags" value="yes">
													<xsl:if test="$group_tags">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>
												Типы товаров
											</label>&#160;
											<label class="checkbox-inline">
												<input type="checkbox" name="show_devices" value="yes">
													<xsl:if test="$show_devices">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>
												Отдельные модели
											</label>&#160;
											<button type="submit" class="btn btn-default btn-sm">Применить</button>
										</form>
									</div>
									<h3 class="p-t-small">Все контрагенты из выборки</h3>
									<p>
										<xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> -
										<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/>
									</p>
									<div class="table-responsive">
										<table class="data-table">
											<tr>
												<th>№</th>
												<th>Организация</th>
												<th>Страна</th>
												<th>Город</th>
												<xsl:for-each select="$years_quartals">
													<xsl:variable name="parts" select="tokenize(., '\*')"/>
													<th class="chart_data_x"><xsl:value-of select="$parts[1]"/>кв. <xsl:value-of select="$parts[2]"/></th>
												</xsl:for-each>
												<th>Всего</th>
											</tr>
											<xsl:for-each select="page/selected">
												<xsl:variable name="name" select="plain_name"/>
												<tr class="chart_data_line">
													<td><xsl:value-of select="position()" />.</td>
													<td class="chart_data_header"><xsl:value-of select="organization" /></td>
													<td><xsl:value-of select="country" /></td>
													<td><xsl:value-of select="city" /></td>
													<xsl:for-each select="$years_quartals">
														<xsl:variable name="parts" select="tokenize(., '\*')"/>
														<xsl:variable name="sale" select="$selected_sales[quartal = $parts[1] and year = $parts[2] and agent_plain_name = $name]"/>
														<td class="chart_data_point" value="{if ($sale) then sum($sale/qty) else '0'}">
															<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
														</td>
													</xsl:for-each>
													<td><xsl:value-of select="sum($selected_sales[agent_plain_name = $name]/qty)" /></td>
												</tr>
											</xsl:for-each>
											<tr class="summary chart_data_line">
												<td></td>
												<td class="chart_data_header">Итого:</td>
												<td></td>
												<td></td>
												<xsl:for-each select="$years_quartals">
													<xsl:variable name="parts" select="tokenize(., '\*')"/>
													<xsl:variable name="sale" select="$selected_sales[quartal = $parts[1] and year = $parts[2]]"/>
													<td class="chart_data_point total" value="{if ($sale) then sum($sale/qty) else '0'}">
														<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
													</td>
												</xsl:for-each>
												<td><xsl:value-of select="sum($selected_sales/qty)" /></td>
											</tr>
										</table>
									</div>
									<div class="subtable-controls no-print chart_button_container">
										<button type="button" class="btn btn-default btn-sm" onclick="createChart(this)">Подробный график</button>
										<button type="button" class="btn btn-default btn-sm" onclick="createChart(this, true)">График Итого</button>
									</div>
									<div class="chart m-t-small" style="display: none">
										<h3 class="no-m-t">
											Статистика продаж контрагентам с
											<xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> по
											<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/>
										</h3>
									</div>

									<!--Группировка по контрагентам, но не по дилерам-->
									<xsl:if test="$group_agents and not($group_dealers)">
										<xsl:for-each-group select="$selected_sales" group-by="agent_plain_name">
											<h3 class="p-t"><xsl:value-of select="current-group()[1]/agent_name" /></h3>
											<xsl:call-template name="sales_table">
												<xsl:with-param name="list" select="current-group()"/>
											</xsl:call-template>
										</xsl:for-each-group>
									</xsl:if>

									<!--Группировка по дилерам, но не по контрагентам-->
									<xsl:if test="$group_dealers and not($group_agents)">
										<xsl:for-each-group select="$selected_sales" group-by="dealer_code">
											<h3 class="p-t"><xsl:value-of select="$selected[code = current-grouping-key()]/organization" /></h3>
											<xsl:call-template name="sales_table">
												<xsl:with-param name="list" select="current-group()"/>
											</xsl:call-template>
										</xsl:for-each-group>
									</xsl:if>

									<!--Группировка и по дилерам, и по контрагентам-->
									<xsl:if test="$group_agents and $group_dealers">
										<xsl:for-each-group select="$selected_sales" group-by="agent_plain_name">
											<xsl:variable name="agent" select="current-group()[1]/agent_name"/>
											<xsl:for-each-group select="current-group()" group-by="dealer_code">
												<h3 class="p-t"><xsl:value-of select="$agent" /> → <xsl:value-of select="$all_dealer[code = current-grouping-key()]/organization" /></h3>
												<xsl:call-template name="sales_table">
													<xsl:with-param name="list" select="current-group()"/>
												</xsl:call-template>
											</xsl:for-each-group>
										</xsl:for-each-group>
									</xsl:if>

									<!--Без группировки-->
									<xsl:if test="not($group_agents) and not($group_dealers)">
										<h3 class="p-t">Все продажи</h3>
										<xsl:call-template name="sales_table">
											<xsl:with-param name="list" select="$selected_sales"/>
										</xsl:call-template>
									</xsl:if>

								</div>
							</div>
						</xsl:if>
					</xsl:if>
				</div>


				<!-- modals -->


				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Страна'"/>
					<xsl:with-param name="input_name" select="'country'"/>
					<xsl:with-param name="list" select="page/country/country"/>
 				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Регион'"/>
					<xsl:with-param name="input_name" select="'region'"/>
					<xsl:with-param name="list" select="page/region/region"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Город'"/>
					<xsl:with-param name="input_name" select="'city'"/>
					<xsl:with-param name="list" select="page/city/city"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Дилер'"/>
					<xsl:with-param name="input_name" select="'dealer'"/>
					<xsl:with-param name="list" select="page/dealer/organization"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Контрагент'"/>
					<xsl:with-param name="input_name" select="'agent'"/>
					<xsl:with-param name="list" select="page/agent/organization"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Сфера'"/>
					<xsl:with-param name="input_name" select="'branch'"/>
					<xsl:with-param name="list" select="page/branch/branch"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Тип товара'"/>
					<xsl:with-param name="input_name" select="'tag'"/>
					<xsl:with-param name="list" select="page/tag/name"/>
				</xsl:call-template>
				<xsl:call-template name="list_modal">
					<xsl:with-param name="header" select="'Товар'"/>
					<xsl:with-param name="input_name" select="'device'"/>
					<xsl:with-param name="list" select="page/device/device"/>
				</xsl:call-template>


				<script src="js/bootstrap.min.js"></script>
				<script src="stats/require.js" data-main="stats/main"/>
				<xsl:call-template name="SELECT_SCRIPT"/>
			</body>
		</html>
	</xsl:template>



	<!-- Выпадающий список всех возможных значений для одного параметра фильтрации -->
	<xsl:template name="list_modal">
		<xsl:param name="input_name"/>
		<xsl:param name="header"/>
		<xsl:param name="list"/>
		<div id="{$input_name}_list" class="modal fade" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button"  class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title"><xsl:value-of select="$header" /></h4>
					</div>
					<form action="{page/base_link}" method="post">
						<div class="modal-body list">
							<xsl:for-each select="$list">
								<div class="checkbox"><label><input type="checkbox" name="{$input_name}" value="{.}"/><xsl:value-of select="."/></label></div>
							</xsl:for-each>
						</div>
						<div class="modal-footer">
							<button type="submit"  class="btn btn-default btn-block">Добавить выбранное</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>


	<!-- Поле ввода для одного параметра фильтрации -->
	<xsl:template name="parameter_input">
		<xsl:param name="input_name"/>
		<xsl:param name="header"/>
		<xsl:param name="list"/>
		<div class="parameter">
			<div><xsl:value-of select="$header" />:</div>
			<div>
				<form action="{page/base_link}" method="post">
					<input list="{$input_name}_l" type="text" name="{$input_name}"/>
					<datalist id="{$input_name}_l">
						<xsl:for-each select="$list">
							<option value="{.}"></option>
						</xsl:for-each>
					</datalist>
					<button type="submit" class="btn btn-default btn-sm">Добавить</button>
					<button type="button" class="btn btn-default btn-sm" data-toggle="modal"
					        data-target="#{$input_name}_list">Выбрать из списка</button>
				</form>
			</div>
			<div class="chosen-values">
				<xsl:for-each select="page/variables/*[name() = $input_name]">
					<a href="{//page/*[name() = concat('remove_', $input_name, '_base')]}{.}"><xsl:value-of select="." /></a>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>


	<!-- Список значений одного параметра фильтрации -->
	<xsl:template name="parameter_values">
		<xsl:param name="input_name"/>
		<xsl:param name="header"/>
		<xsl:if test="page/variables/*[name() = $input_name]">
			<div class="parameter">
				<div><xsl:value-of select="$header" />:</div>
				<div class="chosen-values">
					<xsl:for-each select="page/variables/*[name() = $input_name]">
						<a href="{//page/*[name() = concat('remove_', $input_name, '_base')]}{.}"><xsl:value-of select="." /></a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>
	</xsl:template>


	<!-- Список продаж в таблице продаж  -->
	<xsl:template name="sales">
		<xsl:param name="list"/>
		<xsl:if test="$group_tags">
			<xsl:for-each select="$rep_tags">
				<xsl:variable name="tag" select="name"/>
				<tr class="accent chart_data_line">
					<td class="chart_data_header"><xsl:value-of select="$tag" /></td>
					<xsl:for-each select="$years_quartals">
						<xsl:variable name="parts" select="tokenize(., '\*')"/>
						<xsl:variable name="sale" select="$list[quartal = $parts[1] and year = $parts[2] and tag = $tag]"/>
						<td class="chart_data_point total" value="{if ($sale) then sum($sale/qty) else '0'}">
							<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
						</td>
					</xsl:for-each>
					<td><xsl:value-of select="sum($list[tag = $tag]/qty)" /></td>
				</tr>
				<xsl:if test="$show_devices">
					<xsl:for-each-group select="$list[tag = $tag]" group-by="device">
						<xsl:variable name="sales" select="current-group()"/>
						<xsl:variable name="device" select="current-grouping-key()"/>
						<tr class="chart_data_line">
							<td class="chart_data_header">- <xsl:value-of select="$device"/></td>
							<xsl:for-each select="$years_quartals">
								<xsl:variable name="parts" select="tokenize(., '\*')"/>
								<xsl:variable name="sale" select="$sales[quartal = $parts[1] and year = $parts[2] and device = $device]"/>
								<td class="chart_data_point" value="{if ($sale) then sum($sale/qty) else '0'}">
									<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
								</td>
							</xsl:for-each>
							<td><xsl:value-of select="sum($sales[device = $device]/qty)" /></td>
						</tr>
					</xsl:for-each-group>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<tr class="accent chart_data_line">
			<td class="chart_data_header">Все устройства</td>
			<xsl:for-each select="$years_quartals">
				<xsl:variable name="parts" select="tokenize(., '\*')"/>
				<xsl:variable name="sale" select="$list[quartal = $parts[1] and year = $parts[2]]"/>
				<td class="chart_data_point total" value="{if ($sale) then sum($sale/qty) else '0'}">
					<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
				</td>
			</xsl:for-each>
			<td><xsl:value-of select="sum($list/qty)" /></td>
		</tr>
		<xsl:if test="$show_devices">
			<xsl:for-each-group select="$list" group-by="device">
				<xsl:variable name="sales" select="current-group()"/>
				<xsl:variable name="device" select="current-grouping-key()"/>
				<tr class="chart_data_line">
					<td class="chart_data_header">- <xsl:value-of select="$device"/></td>
					<xsl:for-each select="$years_quartals">
						<xsl:variable name="parts" select="tokenize(., '\*')"/>
						<xsl:variable name="sale" select="$sales[quartal = $parts[1] and year = $parts[2] and device = $device]"/>
						<td class="chart_data_point" value="{if ($sale) then sum($sale/qty) else '0'}">
							<xsl:value-of select="if ($sale) then sum($sale/qty) else '-'" />
						</td>
					</xsl:for-each>
					<td><xsl:value-of select="sum($sales[device = $device]/qty)" /></td>
				</tr>
			</xsl:for-each-group>
		</xsl:if>
	</xsl:template>



	<!-- Отдельная таблица продаж (по определенной группировке, например, по дилерам) -->
	<!-- Не зависит от группировки, т.к. список продаж передается как параметр -->
	<xsl:template name="sales_table">
		<xsl:param name="list"/>
		<p><xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> -
			<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/></p>
		<div class="table-responsive">
			<table class="data-table">
				<tr>
					<th>Название</th>
					<xsl:for-each select="$years_quartals">
						<xsl:variable name="parts" select="tokenize(., '\*')"/>
						<th class="chart_data_x"><xsl:value-of select="$parts[1]"/>кв. <xsl:value-of select="$parts[2]"/></th>
					</xsl:for-each>
					<th>Всего</th>
				</tr>
				<xsl:call-template name="sales">
					<xsl:with-param name="list" select="$list"/>
				</xsl:call-template>
			</table>
		</div>
		<div class="subtable-controls no-print chart_button_container">
			<button type="button" class="btn btn-default btn-sm" onclick="createChart(this)">Подробный график</button>
			<button type="button" class="btn btn-default btn-sm" onclick="createChart(this, true)">График Итого</button>
		</div>
		<div class="chart m-t-small" style="display: none">
			<h3 class="no-m-t">
				Статистика продаж контрагентам с
				<xsl:value-of select="$quartal_from"/> кв. <xsl:value-of select="$year_from"/> по
				<xsl:value-of select="$quartal_to"/> кв. <xsl:value-of select="$year_to"/>
			</h3>
		</div>
	</xsl:template>

</xsl:stylesheet>
