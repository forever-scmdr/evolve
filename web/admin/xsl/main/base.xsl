<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="_inc_message.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:variable name="pre-last" select="count(/admin-page/path/item) - 1"/>
	<xsl:variable name="parent" select="/admin-page/path/item[$pre-last]" />
	

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="TITLE">
		CMS - Режим редактирования
	</xsl:template>

	<xsl:template name="HEAD">
		<head>
			<base href="{admin-page/domain}" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<meta http-equiv="Pragma" content="no-cache" />
			<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
			<link rel="stylesheet" type="text/css" href="admin/css/style.css" />
			<link rel="stylesheet" type="text/css" href="admin/css/context.css" />
			<link rel="stylesheet" type="text/css" href="admin/jquery-ui/jquery-ui.css" media="screen" />
			<link rel="stylesheet" type="text/css" href="admin/js/jquery.fancybox.min.css" media="screen" />
			<title>
				Система управления сайтом
				<xsl:value-of select="/admin-page/domain" />
			</title>
		</head>
	</xsl:template>

	<xsl:template name="JS">
		<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js" />
		<!-- UI -->
		<script type="text/javascript" src="admin/jquery-ui/jquery-ui.min.js"/>
		<!-- FORM -->
		<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
		<!-- MCE -->
		<script type="text/javascript" src="admin/tinymce/tinymce.min.js"/>
		<script type="text/javascript" src="admin/js/regional-ru.js"/>
		<!-- FANCYBOX -->
		<script type="text/javascript" src="admin/js/jquery.fancybox.min.js"/>
		<!-- AJAX -->
		<script type="text/javascript" src="admin/js/ajax.js"/>
		<!-- ADMIN -->
		<script type="text/javascript" src="admin/js/admin.js"/>
		<script type="text/javascript">

			function selectTab(tabId, url) {
				$('.wtf-list .active').removeClass('active');
				$('#' + tabId).addClass('active');
				mainView(url);
			}

			/**
			* Отправка AJAX запроса для обновления основной (центральной) части страницы
			* Отдельно выводится сообщение для пользователя
			*/
			function mainView(link, postProcess) {
				insertAjaxView(link, "main_view", false, "hidden_mes", "message_main", postProcess);
				$('#inline_view').html('');
			}
			/**
			* Отправка AJAX POST запроса для
			  обновления основной (центральной) части страницы
			* Отдельно выводится сообщение для пользователя
			*/
			function mainForm(formId, additionalHandling) {
				prepareForm(formId, "main_view", "hidden_mes", "message_main", additionalHandling);
			}
			$(document).ready(function() {
				insertAjaxView("<xsl:value-of select="admin-page/link[@name='subitems']" />", "subitems");
				insertAjaxView("<xsl:value-of select="admin-page/link[@name='parameters']" />", "main_view");
				$("#message_main").effect("highlight", 1000);
			});
		</script>
	</xsl:template>
	
	<!-- **************************** СТРАНИЦА ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<xsl:call-template name="HEAD" />
			<body>

				<!-- ************************ Основная форма **************************** -->
				<div class="mainwrap">
					<div class="header">
						<div class="left-col">
							<div class="logo">
								<a href="admin_initialize.action">
									<img src="admin/img/forever_logo.png" alt="forever-CMS" />
								</a>
							</div>
						</div>
						<div class="right-col">
							<a href="{/admin-page/domain}" class="gray" target="blank">
								<xsl:value-of select="/admin-page/domain" />
							</a>
							<a href="#" onclik="alert('Функция в разрабтке'); return false;">
								<img src="admin/img/visual_btn.png" alt="visual editor" />
							</a>
							<a href="admin/help.html" style="margin-left: 5px;">F1 - справка</a>
							<a href="logout.login?target=admin_initialize.action" class="logout" title="выйти">
								<xsl:value-of select="/admin-page/@username" />
							</a>
						</div>
					</div>
					<div class="path">
						<span class="pad"></span>
						<a href="{admin-page/root-link}">Корень</a>
						<xsl:for-each select="/admin-page/path/item">
							<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
							<a href="{edit-link}">
								<xsl:value-of select="@caption" />
							</a>
						</xsl:for-each>
					</div>
					<div class="mid">
						<div class="left-col">
							<!-- Поиск -->
							<div class="list position-relative">
								<form id="search-form" class="ajax-form" action="get_view.action" method="POST">
									<input type="text" id="key_search" name="key_search" placeholder="поиск по названию" />
									<input type="hidden" name="itemId" value="0"/>
									<input type="hidden" name="itemType" value="0"/>
									<input type="hidden" name="vt" value="subitems"/>
									<a onclick="postFormView('search-form')" >искать</a>
								</form>
								<a onclick="insertAjaxView('{admin-page/link[@name='subitems']}', 'subitems'); $('#key_search').val('');" style="text-decoration: underline;">Очистить поиск</a>
							</div>
							<div id="subitems">
								<div style="min-height: 100px; margin-bottom: 10px;"/>
							</div>
							<div class="list additional">
								<h4>Дополнительно</h4>
								<ul class="no-drag">
									<li class="visible" title="Загрзить товары из Yandex Market">
										<a href="integrate/?action=start">Интеграция каталога</a>
									</li>
									<li class="visible" title="Загрузить товары из файла во временный раздел">
										<a href="add_products/?action=start">Добваление товаров</a>
									</li>
									<li class="visible" title="Будет сгенерирован и презаписан sitemap.xml">
										<a href="generate_sitemap">Обновить карту сайта</a>
									</li>
									<li class="visible" title="Очищает все кеши. Длительная и ресурсоемкая операция.">
										<a href="admin_drop_all_caches.action">Очистить все кеши</a>
									</li>
									<li class="visible" title="Обновить список товаров для полнотекстового поиска">
										<a href="admin_reindex.action">Переиндексация</a>
									</li>
									<!--<li class="visible" title="Здесь можно добавлять или удалять значения выпадающих списков">-->
										<!--<a href="admin_domains_initialize.domain">-->
											<!--Управление доменами-->
										<!--</a>-->
									<!--</li>-->
									<!--<li class="visible" title="Измениение паролей, создание и удаление пользователей">-->
										<!--<a href="admin_users_initialize.user">-->
											<!--Управление пользователями-->
										<!--</a>-->
									<!--</li>-->
									<li class="visible" title="Создать типы товаров и фильтры">
										<a href="create_filters/?action=start" target="_blank">Создать типы товаров и фильтры</a>
									</li>
									<li class="visible" title="Содание новых типов объектов, управление ранее созданными">
										<a href="admin_types_init.type">
											Управление классами объектов
										</a>
									</li>
								</ul>
							</div>
						</div>
						<div class="right-col">
							<div class="inner">
								<h1 class="title">
									<xsl:if test="admin-page/item">
										<xsl:value-of select="admin-page/item/@caption" />
									</xsl:if>
									<xsl:if test="not(admin-page/item)">
										Справка
									</xsl:if>
								</h1>
								<div class="edit-arena">
									<xsl:variable name="item" select="admin-page/item"/>
									<xsl:if test="$item">
										<div class="wide">
											<div id="message_main" style="border: 2px solid #56C493; padding: 8px 12px; background: #D7F3E6; margin-bottom: 20px; color: #164F35">
												<xsl:value-of select="admin-page/message"/>
											</div>
											<div class="margin context-duplicate">
												<a id="hide-item" class="hide-link icon" href="javascript:positionOnly('#hide-item', 'Вы таки правда хотите скрыть этот раздел?', '{admin-page/status-link}', 'simple')">Скрыть</a>
												<xsl:if test="$item/@files-protected = 'true'">
													<a id="lock-files" class="secure-link icon" href="javascript:positionOnly('#lock-files', 'Снять защиту с файлов', '{admin-page/protect-files}', 'simple')">Разрешить доступ к файлам</a>
												</xsl:if>
												<xsl:if test="not($item/@files-protected = 'true')">
													<a id="lock-files" class="secure-link icon" href="javascript:positionOnly('#lock-files', 'Защитить файлы', '{admin-page/protect-files}', 'simple')">Запретить доступ к файлам</a>
												</xsl:if>
												<a id="new-owner" class="secure-link icon" href="javascript:positionOnly('#new-owner', 'Назначить нового владельца?', '{admin-page/get-users}', 'iframe')">
													Владелец (<xsl:value-of select="if (admin-page/owner-user) then admin-page/owner-user else 'не назначен'" />
													<xsl:if test="admin-page/owner-user = admin-page/@username"> - Я</xsl:if>)
												</a>
												<label>
													Назначить группу: (<xsl:value-of select="$item/@user-group-name"/>)&#160;
													<select class="confirm-select" id="new-owner-group" onchange="positionOnly('#new-owner-group', 'Изменить группу?', $(this).val(), 'simple')">
														<option value="{@href}"><xsl:value-of select="@name"/></option>
														<xsl:for-each select="admin-page/group">
															<option value="{@href}"><xsl:value-of select="@name"/></option>
														</xsl:for-each>
													</select>
												</label>
											</div>
											<div class="margin">
												<table class="wtf-list">
													<tr>
														<td class="active" id="tabParams">
															<a href="javascript:selectTab('tabParams', '{/admin-page/link[@name='parameters']}')" >Редактировть элемент</a>
														</td>
<!-- 														<td id="tabMountTo"> -->
<!-- 															<a href="javascript:selectTab('tabMountTo', '{/admin-page/link[@name='mountTo']}')" title="???">Прикпрепить элемент к</a> -->
<!-- 														</td> -->
<!-- 														<td id="tabToMount"> -->
<!-- 															<a href="javascript:selectTab('tabToMount', '{/admin-page/link[@name='toMount']}')" title="¿¿¿">Прикпрепить к элементу</a> -->
<!-- 														</td> -->
														<td id="tabMoveTo">
															<a href="javascript:selectTab('tabMoveTo', '{/admin-page/link[@name='moveTo']}')" title='Например из раздела "важные новости" в раздел "очень важные новости"'>Переместить выбранный элемент в</a>
														</td>
														<td id="tabToMove">
															<a href="javascript:selectTab('tabToMove', '{/admin-page/link[@name='toMove']}')">Переместить в выбранный элемент</a>
														</td>
													</tr>
												</table>
											</div>
										</div>
									</xsl:if>
									<div id="main_view">
										<!--<div style="min-height: 50px; margin-bottom: 10px;"/>-->
									</div>
									
								</div>
							</div>
						</div>
					</div>
				</div>

				<xsl:call-template name="CONTEXT_MENU"/>

				<xsl:call-template name="JS"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="CONTEXT_MENU">

		<xsl:param name="actions" select="'default'"/>

		<nav id="context_menu-{$actions}" class="context-menu">
			<ul class="context-menu__items">
				<xsl:call-template name="DEFAULT_CONTEXT_LINKS" />
				<xsl:choose>
					<xsl:when test="'default' = $actions">
						<xsl:call-template name="ITEM_CONTEXT_ACTIONS" />
					</xsl:when>
				</xsl:choose>
			</ul>
		</nav>
	</xsl:template>

	<xsl:template name="ITEM_CONTEXT_ACTIONS">
		<li class="context-menu__item">
			<a href="#" data-action="toggle" rel="Показать" class="context-menu__link">Скрыть</a>
		</li>
		<li class="context-menu__item">
			<a href="#" data-action="modify_access" class="context-menu__link">Запретить доступ к файлам</a>
		</li>
		<!--
		<li class="context-menu__item">
			<form method="post" action="set_user" id="chown">
				<xsl:variable name="curr" select="''" />
				<input type="hidden" name="id" value="" />
				<label>
					Назначить владельца
					<select name="user_id" value="0">
						<option value="0">Все</option>
						<option value="12">User 1</option>
						<option value="13">User 2</option>
						<option value="13">User 3</option>
					</select>
				</label>
			</form>
		</li>
		<li class="context-menu__item">
			<form method="post" action="set_user" id="chgroup">
				<input type="hidden" name="id" value="" />
				<label>
					Назначить группу
					<select name="group_id" value="13">
						<option value="0">Все</option>
						<option value="12">group 1</option>
						<option value="13">group 2</option>
						<option value="14">group 3</option>
					</select>
				</label>
			</form>
		</li>
		-->
	</xsl:template>

	<xsl:template name="DEFAULT_CONTEXT_LINKS" >
		<li class="context-menu__item">
			<a href="#" target="blank" class="context-menu__link link">Открыть в новой вкладке</a>
		</li>
		<li class="context-menu__item">
			<a href="#" data-action="new_window"  class="context-menu__link link">Открыть в новом окне</a>
		</li>
		<li class="context-menu__item">
			<a href="#" data-action="copy" class="context-menu__link link">Копировать ссылку</a>
		</li>
	</xsl:template>

</xsl:stylesheet>