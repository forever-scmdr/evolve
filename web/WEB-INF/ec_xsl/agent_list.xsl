<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="agent_domains.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<!-- ****************************    ДЛЯ ВСЕХ СТРАНИЦ    ******************************** -->

<xsl:template name="DOCTYPE">
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:template>

	<xsl:variable name="country" select="page/variables/country"/>
	<xsl:variable name="region" select="page/variables/region"/>
	<xsl:variable name="city" select="page/variables/city"/>
	<xsl:variable name="type" select="page/variables/type"/>
	<xsl:variable name="branch" select="page/variables/branch"/>
	<xsl:variable name="show_cols" select="page/variables/cols"/>
	<xsl:variable name="query" select="page/variables/query"/>
	
	<xsl:variable name="all_cols" select="('country','region','city','organization','address','phone','email','site','contact_name','type','branch', 'desc')"/>
	<xsl:variable name="cols" select="if (not($show_cols) or $show_cols = '') then $all_cols else tokenize($show_cols, ',')"/>
	
	<xsl:variable name="message" select="page/variables/message"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="form | new_agent">
	<div class="menu popup" style="display: none" id="form_{@id}">
		<h3><xsl:if test="@id != '0'">Редактирование данных агента</xsl:if><xsl:if test="@id = '0'">Новый агент</xsl:if></h3>
		<form method="post" action="{submit}">
			<div class="coloumn">
				<label for="edit_country_{@id}">Страна:</label>
				<input id="edit_country_{@id}" type="text" name="{country/@input}" value="{country}"/>
				<label for="edit_region_{@id}">Область:</label>
				<input id="edit_region_{@id}" type="text" name="{region/@input}" value="{region}"/>
				<label for="edit_city_{@id}">Город:</label>
				<input id="edit_city_{@id}" type="text" name="{city/@input}" value="{city}"/>
				<label for="edit_organization_{@id}">Наименование организации:</label>
				<input id="edit_organization_{@id}" type="text" name="{organization/@input}" value="{organization}"/>
				<label for="edit_phone_{@id}">Телефон:</label>
				<textarea id="edit_phone_{@id}" name="{phone/@input}"><xsl:value-of select="phone"/></textarea>
				<label for="edit_desc_{@id}">Примечание:</label>
				<textarea id="edit_desc_{@id}" name="{desc/@input}"><xsl:value-of select="desc"/></textarea>
			</div>
			<div class="coloumn" style="margin-right: -20px;">
				<label for="edit_address_{@id}">Адрес:</label>
				<input id="edit_address_{@id}" type="text" name="{address/@input}" value="{address}"/>
				<label for="edit_email_{@id}">Эл. почта:</label>
				<input id="edit_email_{@id}" type="text" name="{email/@input}" value="{email}"/>
				<label for="edit_site_{@id}">Адрес сайта (если есть):</label>
				<input id="edit_site_{@id}" type="text" name="{phone/@site}" value="{site}"/>
				<label for="edit_contact_name_{@id}">Контактное лицо:</label>
				<input id="edit_contact_name_{@id}" type="text" name="{contact_name/@input}" value="{contact_name}"/>
				<label for="edit_boss_name_{@id}">Руководитель организации:</label>
				<input id="edit_boss_name_{@id}" type="text" name="{boss_name/@input}" value="{boss_name}"/>
				<label for="edit_type_{@id}">Род деятельности организации:</label>
				<select id="edit_type_{@id}" name="{type/@input}" value="{type}">
					<xsl:for-each select="$agent_types"><option><xsl:value-of select="."/></option></xsl:for-each>
				</select>
				<label for="edit_branch_{@id}">Отрасль:</label>
				<select id="edit_branch_{@id}" name="{branch/@input}" value="{branch}">
					<xsl:for-each select="$agent_branches"><option><xsl:value-of select="."/></option></xsl:for-each>
				</select>
			</div>
			<br/>
			<br/>
			<input type="submit" value="Сохранить" class="buttonSubmit"/>
			<a href="#" onclick="$(this).closest('.popup').hide('fade', 200); return false;">Не сохранять</a>
		</form>
	</div>
	</xsl:template>


	<xsl:template name="SEND_FORM">
	<div class="menu popup" style="display: none;" id="popup_send_email">
		<h3>Рассылка</h3>
		<p style="position: relative; top: -15px; font-size: 13px;">
			Выбрано контрагентов: <span class="email_agents_qty">8</span>
		</p>
		<form method="post" action="{page/add_email_to_queue}" target="_blank">
			<input type="hidden" name="agent_ids" value="" id="email_agent_ids"/>
			<input type="hidden" name="action" value="start"/>
			<div class="sendList">
         		<xsl:for-each select="page/email_catalog/email_section">
         		<h4><a href="#" onclick="$('#email_block_{@id}').toggle('blind', 200); return false;"><xsl:value-of select="name"/></a></h4>
				<div id="email_block_{@id}">
					<xsl:for-each select="email_post">
						<input type="checkbox" name="email_id" value="{@id}" id="radio_id_{@id}" style="position: static"/>
						<label for="radio_id_{@id}"><xsl:value-of select="topic"/></label><br/>
					</xsl:for-each>
				</div>
				</xsl:for-each>
			</div>
			<br/>
			<br/>
			<input type="submit" value="Отправить письма" class="buttonSubmit" onclick="$(this).closest('.popup').hide('fade', 200)"/>
			<a href="#" onclick="$(this).closest('.popup').hide('fade', 200); return false;">Отмена</a>
		</form>
	</div>
	</xsl:template>


	<xsl:template match="/">
	<xsl:call-template name="DOCTYPE"/>
	<html xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml" xmlns:f="f:f">
		<head>
			<base href="{page/base}"/>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<meta charset="utf-8" />
			<title>Контрагенты</title>
			<link rel="stylesheet" type="text/css" href="intra_css/styles.css" />
		</head>
		<body>
			<div class="header">
				<div class="headerContainer">
					<div class="user">Администратор <input type="submit" value="Выход" class="buttonSubmit"/></div>
					<a href=""><img src="intra_img/termo_logo.jpg" alt=""/></a>
				</div>
			</div>
			<div class="mainContainer">
				<h1 class="pageTitle">Контрагенты <span>(<xsl:value-of select="count(page/agent)"/> из <xsl:value-of select="page/all_agent_qty_count"/>)</span></h1>
				<div class="tools">
					<a href="#" class="settings" popup="popup_cols">Колонки</a>
					<a class="buttonSubmit">Печать</a>
					<a class="buttonSubmit" href="#" popup="popup_send_email">Рассылка (<span class="email_agents_qty">8</span>)</a>
					<xsl:variable name="aaf" select="page/all_agents_form"/>
					<form style="display:inline;" action="{$aaf/submit}" method="post" target="_blank" enctype="multipart/form-data">
						<input style="display:none;" type="file" name="{$aaf/file/@input}" id="browseFile" onchange="checkFile()"/>
						<label for="browseFile" class="buttonSubmit" style="font-size:13px; margin-right: 10px;">Загрузить файл</label>
						<input type="submit" class="buttonSubmit" value="Разобрать" style="display: none" id="parseFile"/>
					</form>
					<a class="buttonSubmit" href="{page/get_file}">Скачать файл</a>
					<a class="buttonSubmit" href="{page/synchronize_site}" target="_blank">Закачать на сайт</a>
					<div class="search">
						<form action="{page/query_base_link}" method="post">
							<input type="text" value="{$query}" name="query" placeholder="Поиск контрагентов"/>
							<input type="submit" value=""/>
						</form>
					</div>
				</div>
				<div class="edit">
					<xsl:apply-templates select="page/new_agent"/>
					<xsl:apply-templates select="page/agent/form"/>
					<xsl:apply-templates select="page/agent/form"/>
					<xsl:if test="$message">
						<h3 style="color: red"><xsl:value-of select="$message"/></h3>
					</xsl:if>
					<xsl:call-template name="SEND_FORM"/>
				</div>
				<div class="menu popup dropdown" style="top: 80px; left: 10px; display: none" id="popup_cols">
					<h3>Отображать колонки</h3>
					<form action="{page/cols_base_link}" method="post">
						<div class="coloumn">
							<input type="checkbox" name="cols" id="cb_country" value="country">
								<xsl:if test="'country' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_country">Страна</label><br/>
							
							<input type="checkbox" name="cols" id="cb_region" value="region">
								<xsl:if test="'region' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_region">Область</label><br/>
							
							<input type="checkbox" name="cols" id="cb_city" value="city">
								<xsl:if test="'city' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_city">Город</label><br/>
							
							<input type="checkbox" name="cols" id="cb_organization" value="organization">
								<xsl:if test="'organization' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_organization">Наименование организации</label><br/>
							
							<input type="checkbox" name="cols" id="cb_address" value="address">
								<xsl:if test="'address' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_address">Почтовый и фактический адрес</label><br/>
							
							<input type="checkbox" name="cols" id="cb_phone" value="phone">
								<xsl:if test="'phone' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_phone">Телефон</label><br/>
							
							<input type="checkbox" name="cols" id="cb_email" value="email">
								<xsl:if test="'email' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_email">Эл. почта</label><br/>
							
							<input type="checkbox" name="cols" id="cb_site" value="site">
								<xsl:if test="'site' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_site">Сайт</label><br/>
							
							<input type="checkbox" name="cols" id="cb_boss_name" value="boss_name">
								<xsl:if test="'boss_name' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_boss_name">Руководитель организации</label><br/>
							
							<input type="checkbox" name="cols" id="cb_contact_name" value="contact_name">
								<xsl:if test="'contact_name' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_contact_name">Контактное лицо</label><br/>
							
							<input type="checkbox" name="cols" id="cb_type" value="type">
								<xsl:if test="'type' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_type">Род деятельности</label><br/>
							
							<input type="checkbox" name="cols" id="cb_branch" value="branch">
								<xsl:if test="'branch' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_branch">Отрасль</label><br/>

							<input type="checkbox" name="cols" id="cb_desc" value="desc">
								<xsl:if test="'desc' = $cols"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
							</input>
							<label for="cb_desc">Примечание</label><br/>
						</div>
						<br/>
						<input type="submit" value="Применить" class="buttonSubmit"/>
					</form>
				</div>
				
				<xsl:if test="$country or $region or $city or $type or $branch or $query">
					<xsl:variable name="src_url" select="page/source_link"/>
					<div class="filter">
						<h3>Фильтр</h3>
						<xsl:for-each select="$country">
							<div class="tag" onclick="location.replace('{f:remove_url_param($src_url, 'country', .)}')">
								<xsl:value-of select="."/>
								<xsl:if test="position() = 1"><div class="label">страна</div></xsl:if>
							</div>
						</xsl:for-each>
						<xsl:for-each select="$region">
							<div class="tag" onclick="location.replace('{f:remove_url_param($src_url, 'region', .)}')">
								<xsl:value-of select="."/>
								<xsl:if test="position() = 1"><div class="label">область</div></xsl:if>
							</div>
						</xsl:for-each>
						<xsl:for-each select="$city">
							<div class="tag" onclick="location.replace('{f:remove_url_param($src_url, 'city', .)}')">
								<xsl:value-of select="."/>
								<xsl:if test="position() = 1"><div class="label">город</div></xsl:if>
							</div>
						</xsl:for-each>
						<xsl:for-each select="$type">
							<div class="tag" onclick="location.replace('{f:remove_url_param($src_url, 'type', .)}')">
								<xsl:value-of select="."/>
								<xsl:if test="position() = 1"><div class="label">род деятельности</div></xsl:if>
							</div>
						</xsl:for-each>
						<xsl:for-each select="$branch">
							<div class="tag" onclick="location.replace('{f:remove_url_param($src_url, 'branch', .)}')">
								<xsl:value-of select="."/>
								<xsl:if test="position() = 1"><div class="label">отрасль</div></xsl:if>
							</div>
						</xsl:for-each>
						<xsl:if test="$query">
							<div class="tag" onclick="location.replace('{page/query_base_link}')">
								<xsl:value-of select="$query"/>
								<div class="label">поиск</div>
							</div>
						</xsl:if>
					</div>
				</xsl:if>
				<table>
					<tr>
						<th>
							<a href="#" style="font-size: 25px; text-decoration: none" popup="form_0">✎</a>
						</th>
						<th>
							Выбрать: 
							<a href="#" onclick="$('.send_to_agent').prop('checked', true); updateEmailAgentList(); return false;">Всех</a>
							<a href="#" onclick="$('.send_to_agent').prop('checked', false); updateEmailAgentList(); return false;">Никого</a>
						</th>
						<xsl:if test="'country' = $cols">
							<th style="position: relative">
								<a href="{f:set_url_param(page/sort_base_link, 'sort', 'country')}">Страна</a><input type="checkbox" popup="country_popup"/>
								<xsl:variable name="min_width" select="ceiling(count(page/countries) div 10) * 220"/>
								<div class="menu popup dropdown" style="display: none; min-width: {$min_width}px" id="country_popup">
									<h3>Выберите страну</h3>
									<form action="{page/country_base_link}" method="post">
										<div class="coloumn">
											<xsl:for-each select="page/countries">
												<xsl:if test="position() mod 10 = 0">
													<xsl:text disable-output-escaping="yes">&lt;/div&gt;&lt;div class="coloumn"&gt;</xsl:text>
												</xsl:if>
												<input type="checkbox" name="country" id="check_country_{position()}" value="{country}">
													<xsl:if test="country = $country"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
												</input>
												<label for="check_country_{position()}"><xsl:value-of select="country"/></label><br/>
											</xsl:for-each>
										</div>
										<br/>
										<input type="submit" value="Применить" class="buttonSubmit"/>
									</form>
								</div>
							</th>
						</xsl:if>
						<xsl:if test="'region' = $cols">
							<th style="position: relative">
								<a href="{f:set_url_param(page/sort_base_link, 'sort', 'region')}">Область</a><input type="checkbox" popup="region_popup"/>
								<xsl:variable name="min_width" select="ceiling(count(page/regions) div 10) * 220"/>
								<div class="menu popup dropdown" style="display: none; min-width: {$min_width}px" id="region_popup">
									<h3>Выберите область</h3>
									<form action="{page/region_base_link}" method="post">
										<div class="coloumn">
											<xsl:for-each select="page/regions">
												<xsl:if test="position() mod 10 = 0">
													<xsl:text disable-output-escaping="yes">&lt;/div&gt;&lt;div class="coloumn"&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position() = 1 or preceding-sibling::regions[1]/country != country">
													<h4><xsl:value-of select="country"/></h4>
												</xsl:if>
												<input type="checkbox" name="region" id="check_region_{position()}" value="{region}">
													<xsl:if test="region = $region"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
												</input>
												<label for="check_region_{position()}"><xsl:value-of select="region"/></label><br/>
											</xsl:for-each>
										</div>
										<br/>
										<input type="submit" value="Применить" class="buttonSubmit"/>
									</form>
								</div>
							</th>
						</xsl:if>
						<xsl:if test="'city' = $cols">
							<th style="position: relative">
								<a href="{f:set_url_param(page/sort_base_link, 'sort', 'city')}">Город</a><input type="checkbox" checked="checked" popup="city_popup"/>
								<xsl:variable name="min_width" select="ceiling(count(page/cities) div 10) * 220"/>
								<div class="menu popup dropdown" style="display: none; min-width: {$min_width}px" id="city_popup">
									<h3>Выберите город</h3>
									<form action="{page/city_base_link}" method="post">
										<div class="coloumn">
											<xsl:for-each select="page/cities">
												<xsl:if test="position() mod 10 = 0">
													<xsl:text disable-output-escaping="yes">&lt;/div&gt;&lt;div class="coloumn"&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position() = 1 or preceding-sibling::cities[1]/country != country">
													<h4><xsl:value-of select="country"/></h4>
												</xsl:if>
												<input type="checkbox" name="city" id="check_city_{position()}" value="{city}">
													<xsl:if test="city = $city"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
												</input>
												<label for="check_city_{position()}"><xsl:value-of select="city"/></label><br/>
											</xsl:for-each>
										</div>
										<br/>
										<input type="submit" value="Применить" class="buttonSubmit"/>
									</form>
								</div>
							</th>
						</xsl:if>
						<xsl:if test="'organization' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'organization')}">Наименование организации</a></th>
						</xsl:if>
						<xsl:if test="'address' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'address')}">Почтовый и фактический адрес</a></th>
						</xsl:if>
						<xsl:if test="'phone' = $cols">
							<th style="min-width: 120px;"><a href="{f:set_url_param(page/sort_base_link, 'sort', 'phone')}">Телефон</a></th>
						</xsl:if>
						<xsl:if test="'email' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'email')}">Эл. почта</a></th>
						</xsl:if>
						<xsl:if test="'site' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'site')}">Сайт</a></th>
						</xsl:if>
						<xsl:if test="'boss_name' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'boss_name')}">Руководитель организации</a></th>
						</xsl:if>
						<xsl:if test="'contact_name' = $cols">
							<th><a href="{f:set_url_param(page/sort_base_link, 'sort', 'contact_name')}">Контактное лицо</a></th>
						</xsl:if>
						<xsl:if test="'type' = $cols">
							<th style="position: relative">
								<a href="{f:set_url_param(page/sort_base_link, 'sort', 'type')}">Род деятельности</a><input type="checkbox" popup="type_popup"/>
								<xsl:variable name="min_width" select="ceiling(count($agent_types) div 10) * 220"/>
								<div class="menu popup dropdown" style="display: none; min-width: {$min_width}px" id="type_popup">
									<h3>Выберите род деятельности</h3>
									<form action="{page/type_base_link}" method="post">
										<div class="coloumn">
											<xsl:for-each select="$agent_types">
												<xsl:if test="position() mod 10 = 0">
													<xsl:text disable-output-escaping="yes">&lt;/div&gt;&lt;div class="coloumn"&gt;</xsl:text>
												</xsl:if>
												<input type="checkbox" name="type" id="check_type_{position()}" value="{.}">
													<xsl:if test=". = $type"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
												</input>
												<label for="check_type_{position()}"><xsl:value-of select="."/></label><br/>
											</xsl:for-each>
										</div>
										<br/>
										<input type="submit" value="Применить" class="buttonSubmit"/>
									</form>
								</div>
							</th>
						</xsl:if>
						<xsl:if test="'branch' = $cols">
							<th style="position: relative">
								<a href="{f:set_url_param(page/sort_base_link, 'sort', 'branch')}">Отрасль</a><input type="checkbox" popup="branch_popup"/>
								<xsl:variable name="min_width" select="ceiling(count($agent_branches) div 10) * 220"/>
								<div class="menu popup dropdown" style="display: none; min-width: {$min_width}px" id="branch_popup">
									<h3>Выберите отрасль</h3>
									<form action="{page/branch_base_link}" method="post">
										<div class="coloumn">
											<xsl:for-each select="$agent_branches">
												<xsl:if test="position() mod 10 = 0">
													<xsl:text disable-output-escaping="yes">&lt;/div&gt;&lt;div class="coloumn"&gt;</xsl:text>
												</xsl:if>
												<input type="checkbox" name="branch" id="check_branch_{position()}" value="{.}">
													<xsl:if test=". = $branch"><xsl:attribute name="checked" select="'checked'"/></xsl:if>
												</input>
												<label for="check_branch_{position()}"><xsl:value-of select="."/></label><br/>
											</xsl:for-each>
										</div>
										<br/>
										<input type="submit" value="Применить" class="buttonSubmit"/>
									</form>
								</div>
							</th>
						</xsl:if>
						<xsl:if test="'desc' = $cols">
							<th>Примечание</th>
						</xsl:if>
						<th>
							<a style="font-size: 25px">✖</a>
						</th>
					</tr>
					<xsl:for-each select="page/agent">
						<tr>
							<td>
								<a href="#" style="text-decoration: none" popup="form_{@id}">✎</a>
							</td>
							<td>
								<input type="checkbox" class="send_to_agent" value="{@id}"/>
							</td>
							<xsl:if test="'country' = $cols">
								<td><xsl:value-of select="country"/></td>
							</xsl:if>
							<xsl:if test="'region' = $cols">
								<td><xsl:value-of select="region"/></td>
							</xsl:if>
							<xsl:if test="'city' = $cols">
								<td><xsl:value-of select="city"/></td>
							</xsl:if>
							<xsl:if test="'organization' = $cols">
								<td><a href="{show_proc}"><xsl:value-of select="organization"/></a></td>
							</xsl:if>
							<xsl:if test="'address' = $cols">
								<td><xsl:value-of select="address"/></td>
							</xsl:if>
							<xsl:if test="'phone' = $cols">
								<td>
									<xsl:variable name="phones_array" select="tokenize(phone, ',|;')"/>
									<xsl:for-each select="$phones_array">
										<div><xsl:value-of select="."/></div>
									</xsl:for-each>
								</td>
							</xsl:if>
							<xsl:if test="'email' = $cols">
								<td><a href="mailto:{email}"><xsl:value-of select="email"/></a></td>
							</xsl:if>
							<xsl:if test="'site' = $cols">
								<td><a href="{site}"><xsl:value-of select="site"/></a></td>
							</xsl:if>
							<xsl:if test="'boss_name' = $cols">
								<td><xsl:value-of select="boss_name"/></td>
							</xsl:if>
							<xsl:if test="'contact_name' = $cols">
								<td><xsl:value-of select="contact_name"/></td>
							</xsl:if>
							<xsl:if test="'type' = $cols">
								<td><xsl:value-of select="type"/></td>
							</xsl:if>
							<xsl:if test="'branch' = $cols">
								<td><xsl:value-of select="branch"/></td>
							</xsl:if>
							<xsl:if test="'desc' = $cols">
								<td><xsl:value-of select="desc"/></td>
							</xsl:if>
							<td>
								<a href="{delete}" style="text-decoration: none">✖</a>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
			<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
			<script type="text/javascript" src="js/jquery-ui.min.js"></script>
			<script type="text/javascript">
				<xsl:text disable-output-escaping="yes">
				function updateEmailAgentList() {
					var valsArray = $('.send_to_agent:checked').map(function() {
						return $(this).val();
					}).get();
					var agentIds = valsArray.join(',');
					$('#email_agent_ids').val(agentIds);
					$('.email_agents_qty').html(valsArray.length);
				}

				function checkFile() {
					if ($('#browseFile').val())
						$('#parseFile').show();
					else
						$('#parseFile').hide();
				}

				$(document).ready(function() {
					$('input[popup]').click(function() {
						var input = $(this);
						var popup = $('#' + input.attr('popup'));
						var wasVisible = popup.is(':visible');
						$('.menu').hide('fade', 200);
						if (wasVisible)
							return false;
						var th = input.closest('th');
						if ($(window).width() - th.offset().left &lt;= 200) {
							popup.css({left: 'auto', right: 0, top: th.height()});
							popup.removeClass('popup');
							popup.addClass('popupM');
						} else {
							popup.css({left: 0, right: 'auto', top: th.height()});
							popup.removeClass('popupM');
							popup.addClass('popup');
						}
						popup.show('fade', 200);
						return false;
					});
					
					$('a[popup]').click(function() {
						var popup = $('#' + $(this).attr('popup'));
						var wasVisible = popup.is(':visible');
						$('.menu').hide('fade', 200);
						if (!wasVisible)
							popup.show('fade', 200);
						return false;
					});
					
					$(document).click(function(event){
						if ($(event.target).closest('.popup, .popupM').length == 0)
							$('.menu').hide('fade', 200);
					});
					
					$(document).keydown(function(event) {
						if (event.which == 27) {
							event.preventDefault();
							$('.menu').hide('fade', 200);
						}
					});
					
					$('select[value]').each(function() {
						var value = $(this).attr('value');
						if (value != '')
							$(this).val(value);
					});
					
					$('input.send_to_agent').click(updateEmailAgentList);
					
					updateEmailAgentList();
					checkFile();
				});
				</xsl:text>
			</script>
		</body>
	</html>
	</xsl:template>

	<!-- ****************************    Добавление параметра к ссылке    ******************************** -->
	
	<!-- Удаление параметра с определенным значением -->
	<xsl:function name="f:remove_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="replace(encode-for-uri($value), '%20', '\\+')"/>
		<xsl:value-of 
			select="replace(replace($url, concat('(\?|&amp;)', $name, '=', $val_enc, '($|&amp;)'), '$1'), '&amp;$|\?$', '')"/>
	</xsl:function>

	<!-- Усановка параметра, если его нет, или замена значения параметра (в том числе удаление) -->
	<xsl:function name="f:set_url_param" as="xs:string">
		<xsl:param name="url" as="xs:string"/>
		<xsl:param name="name" as="xs:string"/>
		<xsl:param name="value"/>
		<xsl:variable name="val_enc" select="encode-for-uri(string($value))"/>
		<xsl:value-of 
			select="if (not($val_enc) or $val_enc = '') then replace(replace($url, concat('(\?|&amp;)', $name, '=', '.*?($|&amp;)'), '$1'), '&amp;$|\?$', '')
					else if (contains($url, concat($name, '='))) then replace($url, concat($name, '=', '.*?($|&amp;)'), concat($name, '=', $value, '$1'))
					else if (contains($url, '?')) then concat($url, '&amp;', $name, '=', $val_enc)
					else concat($url, '?', $name, '=', $val_enc)"/>
	</xsl:function>
	
	<xsl:template match="*" mode="LINK_ADD_VARIABLE_QUERY">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<a class="{$class}" href="{.}{'?'[not(contains(current(), '?'))]}{'&amp;'[contains(current(), '?')]}{$name}={$value}"><xsl:value-of select="$text"/></a>
	</xsl:template>

</xsl:stylesheet>