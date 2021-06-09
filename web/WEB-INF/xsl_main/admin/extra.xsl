<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>



	<xsl:variable name="extra" select="page/optional_modules/admin_extra"/>



	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->


	<xsl:template match="/">
	<xsl:variable name="form" select="admin-page/form"/>

		<div id="hidden_mes" >Активированы дополнительные функции</div>

		<xsl:if test="$form/@id = 0">
			<script>
				$('h1.title').html('Дополнительные функции');
			</script>
		</xsl:if>
		<div class="wide">
			<div class="margin">
				<h2 class="title">Дополнительные функции</h2>

				<h3>SEO</h3>
				<ul class="no-drag">
					<li><a href="update_error_page">Обновить страницу 404</a></li>
				</ul>
				<h3>Интеграция Yandex.Market</h3>
				<ul class="no-drag">
					<li><a href="integrate?action=start">Импорт каталога YML</a></li>
					<li><a href="integrate?get_price=no&amp;action=start">Импорт каталога YML (без цен)</a></li>
					<li><a href="create_yml_file">Экспорт каталога YML</a></li>
					<li><a href="create_yml_file">Экспорт каталога YML (без цен)</a></li>
					<li><a href="create_yml_file">Экспорт каталога YML (только товары в наличии)</a></li>
				</ul>

				<h3>Интеграция Excel</h3>

				<ul class="no-drag">
					<li><a href="parse_excel?action=start">Импорт каталога Excel</a></li>
					<li><a href="create_excel_pricelist/?action=start&amp;aux_params=yes">Экспорт каталога Excel</a></li>
					<li><a href="section_list">Экспорт разделов в Excel</a></li>
					<li><a href="update_prices_from_excel/?action=start">Импорт прайса Excel</a></li>
					<!-- <li><a href="create_excel_pricelist_min/?action=start">Экспорт прайса Excel</a></li> -->
				</ul>

				<h3>Парсинг сайтов</h3>
				<ul>
					<li>Индивидуально для каждого сайта</li>
				</ul>

				<h3>Дополнительно</h3>
				<ul class="no-drag">
					<li class="visible" title="Создать фильтры по параметрам товаров">
						<a href="create_filters/?action=start" target="_blank">Создать фильтры</a>
					</li>
					<li class="visible" title="Создать фильтры по параметрам товаров">
						<a href="create_filters/?action=start" target="_blank">Создать фильтры</a>
					</li>
					<li class="visible" title="Обновить список товаров для полнотекстового поиска">
						<a href="admin_reindex.action">Переиндексация поиска</a>
					</li>
					<li class="visible" title="Будет сгенерирован и презаписан sitemap.xml">
						<a href="generate_sitemap">Обновить sitemap.xml</a>
					</li>
					<li class="visible" title="Будет сгенерирован и презаписан robots.txt">
						<a href="admin_load_robots.action">Редактировать robots.txt</a>
					</li>
				</ul>

				<h3>Служебные функции</h3>
				<ul class="no-drag">
					<li class="visible" title="Очищает все кеши. Длительная и ресурсоемкая операция.">
						<a href="admin_drop_all_caches.action">Очистить все кеши</a>
					</li>
					<li class="visible" title="Содание новых типов объектов, управление ранее созданными">
						<a href="admin_types_init.type">
							Управление классами объектов
						</a>
					</li>
				</ul>


				<!--
				<ul class="no-drag">
					<xsl:if test="$extra/section_list = '1'">
						<li class="visible" title="Список разделов для формирования прайс-листов" style="background: #0c609d;">
							<a href="section_list" style="color: #fff;">Список разделов</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/min_price_list = '1'">
						<li class="visible" style="background: #0c609d;">
							<a href="create_excel_pricelist_min/?action=start" style="color: #fff;">Минимальный прайс-лист</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/excel_import = '1'">
						<li class="visible" style="background: #0c609d;">
							<a href="parse_excel/?action=start" style="color: #fff;">
								Импорт каталога из Excel
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/excel_update_prices = '1'">
						<li class="visible" style="background: #0c609d;">
							<a href="update_prices_from_excel/?action=start" style="color: #fff;">
								Обновить цены из Excel
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/ym_integrate = '1'">
						<li class="visible" title="Загрзить товары из Yandex Market">
							<a href="integrate/?action=start">Интеграция Yandex Market</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/ym_generate = '1'">
						<li class="visible" title="Здесь можно добавлять или удалять значения выпадающих списков" target="_blank">
							<a href="create_yml_file">
								Сгенерировать файл Яндекс-маркет
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/get_pics = '1'">
						<li class="visible" title="Скачать картинки">
							<a href="download_pics/?action=start">Скачать картинки</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/create_users = '1'">
						<li class="visible" title="Создать пользователей">
							<a href="create_users/?action=start">Создать пользователей</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/create_filters = '1'">
						<li class="visible" title="Создать фильтры по параметрам товаров">
							<a href="create_filters/?action=start" target="_blank">Создать фильтры</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/update_sitemap = '1'">
						<li class="visible" title="Будет сгенерирован и презаписан sitemap.xml">
							<a href="generate_sitemap">Обновить карту сайта</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/clear_caches = '1'">
						<li class="visible" title="Очищает все кеши. Длительная и ресурсоемкая операция.">
							<a href="admin_drop_all_caches.action">Очистить все кеши</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/reindex = '1'">
						<li class="visible" title="Обновить список товаров для полнотекстового поиска">
							<a href="admin_reindex.action">Переиндексация</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/manage_types = '1'">
						<li class="visible" title="Содание новых типов объектов, управление ранее созданными">
							<a href="admin_types_init.type">
								Управление классами объектов
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/crawl = '1'">
						<li class="visible" title="Запустить парсинг suzuki.ru">
							<a href="crawl?action=start&amp;job=all">
								Запустить парсинг suzuki.ru
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$extra/publish_crawl = '1'">
						<li class="visible" title="Разместить результаты парсинга">
							<a href="integrate_parsed?action=start">
								Разместить результаты парсинга
							</a>
						</li>
					</xsl:if>
				</ul>

				-->
			</div>
		</div>
	</xsl:template>
		
</xsl:stylesheet>