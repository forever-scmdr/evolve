<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_base_head.xsl"/>
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<xsl:template name="TITLE">CMS - Режим редактирования</xsl:template><!-- ******************* TODO LOCAL ******************** -->

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html>
			<xsl:call-template name="HEAD"/>
			<body>
			<!-- ************************ Основная форма **************************** -->
			<!-- ******************************************************************** -->
			<div class="mainwrap">
				<div class="header">
					<div class="logo">
						<a href=""><img src="admin/admin_img/logo.png" alt=""/></a>
					</div>
					<div class="domain">
						<div class="domain_right">
							 <xsl:value-of select="/admin-page/domain"/>
						</div>
					</div>
					<a href="admin/help.html"><img class="help" src="admin/admin_img/help.png" alt=""/></a>
					<table class="user">
					<tr>
						<td>
							<xsl:value-of select="/admin-page/@username"/>
						</td>
						<td class="logout">
							<a href="logout.login?target=admin_initialize.action"><img src="admin/admin_img/logout.png" alt=""/></a>
						</td>
					</tr>
					</table>
				</div>
				<div class="path">
					<xsl:for-each select="/admin-page/path/item[position() != last()]">
					<a href="{edit-link}"><xsl:value-of select="@caption"/></a><xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					</xsl:for-each>
					<strong><xsl:value-of select="/admin-page/path/item[position() = last()]/@caption"/></strong>
				</div>
				<table class="main_table">
				<tr>
					<td class="side_col">
						<div id="subitems">
							
						<!-- Сюда помещаются айтемы для создания и уже созданные -->
						
						</div>
						<div class="side_block">
							<div class="head">
								<span>Дополнительно</span>
							</div>
							<div class="items">
								<div class="exist_item">
									<table>
										<tr>
											<td class="link">
												<a href="admin_types_init.type">Управление классами объектов</a>
											</td>
										</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_users_initialize.user">Управление пользователями</a>
										</td>
									</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_domains_initialize.domain">Управление доменами</a>
										</td>
									</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_reindex.action">Переиндексация</a>
										</td>
									</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_drop_all_caches.action">Очистить все кеши</a>
										</td>
									</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="update_prices">Изменение цен</a>
										</td>
									</tr>
									</table>
								</div>
								<div class="spacer">
									<div>
									</div>
								</div>
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="booking">Онлайн-бронирование</a>
										</td>
									</tr>
									</table>
								</div>
							</div>
							<div class="bottom">
							</div>
						</div>
					</td>
					<td class="main">
						<div class="warning">
							<div class="tl">
								<div class="bl">
									<div class="message">
										<span id="message_main">
											<xsl:value-of select="admin-page/message"/>
										</span>
									</div>
								</div>
							</div>
						</div>
						<h1 class="title">
							<xsl:if test="admin-page/item"><xsl:value-of select="admin-page/item/@caption"/></xsl:if>
							<xsl:if test="not(admin-page/item)">Корневой элемент</xsl:if>
						</h1>
						<script>
						function selectTab(tabId, url) {
							$('.tab_bgr_selected').removeClass('tab_bgr_selected').addClass('tab_bgr');
							$('#' + tabId).addClass('tab_bgr_selected');
							mainView(url);
						}
						</script>
						<xsl:if test="admin-page/item">
						<div class="tabs_container">
							<div class="tabs">
								<div class="tab_bgr_selected" id="tabParams">
									<div class="tab_left">
										<div class="tab_right">
											<a href="javascript:selectTab('tabParams', '{/admin-page/link[@name='parameters']}')">
											Свойства элемента</a>
										</div>
									</div>
								</div>
								<div class="tab_bgr" id="tabMountTo">
									<div class="tab_left">
										<div class="tab_right">
											<a href="javascript:selectTab('tabMountTo', '{/admin-page/link[@name='mountTo']}')">
											Связи нижнего уровня</a>
										</div>
									</div>
								</div>
								<div class="tab_bgr" id="tabToMount">
									<div class="tab_left">
										<div class="tab_right">
											<a href="javascript:selectTab('tabToMount', '{/admin-page/link[@name='toMount']}')">
											Связи верхнего уровня</a>
										</div>
									</div>
								</div>
								<div class="tab_bgr" id="tabMoveTo">
									<div class="tab_left">
										<div class="tab_right">
											<a href="javascript:selectTab('tabMoveTo', '{/admin-page/link[@name='moveTo']}')">
											Переместить выбранный элемент в</a>
										</div>
									</div>
								</div>
								<div class="tab_bgr" id="tabToMove">
									<div class="tab_left">
										<div class="tab_right">
											<a href="javascript:selectTab('tabToMove', '{/admin-page/link[@name='toMove']}')">
											Переместить в выбранный элемент</a>
										</div>
									</div>
								</div>
								<div class="clear"></div>
							</div>
						</div>
						</xsl:if>
						<div id="main_view">							
						
						<!-- Сюда помещаются формы для редактирования айтема (параметры, связи, перемещение) -->

						</div>
						<div id="inline_view">							
						
						<!-- Сюда помещаются формы для редактирования inline-сабайтемов редактируемого айтема -->

						</div>
					</td>
				</tr>
				</table>
			</div>
			<script type="text/javascript">
			/**
			 * Отправка AJAX запроса для обновления основной (центральной) части страницы
			 * Отдельно выводится сообщение для пользователя
			 */
			function mainView(link, postProcess) {
				insertAjaxView(link, "main_view", false, "hidden_mes", "message_main", postProcess);
				$('#inline_view').html('');
			}
			/**
			 * Отправка AJAX запроса для обновления указанной части страницы
			 * Отдельно выводится сообщение для пользователя
			 */
			function defaultView(link, viewId, confirm, postProcess) {
				insertAjaxView(link, viewId, confirm, "hidden_mes", "message_main", postProcess);
			}
			/**
			 * Отправка AJAX POST запроса для обновления основной (центральной) части страницы
			 * Отдельно выводится сообщение для пользователя
			 */
			function mainForm(formId, additionalHandling) {
				prepareForm(formId, "main_view", "hidden_mes", "message_main", additionalHandling);
			}
			$(document).ready(function() {
				insertAjaxView("<xsl:value-of select="admin-page/link[@name='subitems']"/>", "subitems");
				insertAjaxView("<xsl:value-of select="admin-page/link[@name='parameters']"/>", "main_view");
			});
			</script>
			</body>
		</html>
	</xsl:template>
		
</xsl:stylesheet>