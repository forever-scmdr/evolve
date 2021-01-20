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
					<li><a href="create_excel_pricelist/?action-start">Экспорт каталога Excel</a></li>
					<li><a href="update_prices_from_excel/?action=start">Импорт прайса Excel</a></li>
					<li><a href="create_excel_pricelist_min/?action=start">Экспорт прайса Excel</a></li>
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
					<li class="visible" title="Содание новых типов объектов, управление ранее созданными">-->
						<a href="admin_types_init.type">
							Управление классами объектов
						</a>
					</li>
				</ul>

			</div>
		</div>
	</xsl:template>
		
</xsl:stylesheet>