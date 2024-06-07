<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="bom_ajax.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="snippets/custom_blocks.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="is_search_multiple" select="true()"/>

	<xsl:variable name="title" select="'Поиск BOM'"/>
	<xsl:variable name="h1" select="'Поиск BOM'"/>
	<xsl:variable name="command_xml" select="page/formatted/xml"/>
	<xsl:variable name="message" select="$command_xml/message"/>
	<xsl:variable name="error" select="$command_xml/error"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>
	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<xsl:if test="$message and $error">
			<div class="alert alert_danger">
				<div class="alert__title">Ошибка. <xsl:value-of select="$message"/></div>
				<div class="alert__text">
					<p><xsl:value-of select="$error"/>.</p>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="$message and not($error)">
			<div class="alert alert_success">
				<div class="alert__text">
					<p><xsl:value-of select="$message"/></p>
				</div>
			</div>
		</xsl:if>
		<div class="text">
			<form method="post" action="{page/input_bom_link}" enctype="multipart/form-data" id="query_form">
				<textarea class="input header-search__input" placeholder="Введите запрос"
						  autocomplete="off" name="q" autofocus="" style="width:100%; height: 200px;"><xsl:value-of select="$query" /></textarea>
				<div style="display: flex">
					<div>
						<input type="file" name="file" id="file" class="get-file" onchange="$(this).closest('div').find('label').text(fileName($(this).val()))"/>
						<label for="file" class="upload">Загрузить Excel/CSV/TXT файл с компьютера</label>
					</div>
					<div style="padding-top: 10px; padding-left: 20px">
						<a href="files/query.xlsx" download="bom.xlsx">Скачать пример...</a>
					</div>
				</div>
				<script>
					function fileName(name) {
						var index = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
						return name.substring(index + 1);
					}
				</script>
				<div style="display: flex">
					<div>
						<button class="button" type="submit" style="margin-right: 10px; ">Сформировать список BOM</button>
						<xsl:if test="page/formatted/xml/bom">
							<button class="button" type="submit" onclick="submitSearch(); return false;" style="margin-right: 10px;">Найти</button>
						</xsl:if>
						<xsl:call-template name="SAVE_BOM_BUTTON"/>
					</div>
					<!--
					<div style="right: 0px; position: absolute;">
						<button class="button" type="submit" onclick="$(this).closest('form').attr('action', 'search_prices')">Найти (отладка)</button>
						<button class="button" type="submit" onclick="$(this).closest('form').attr('action', '{page/input_bom_link}?action=clearCache')" style="margin-left: 10px;">Очистить кеш</button>
					</div>
					-->
				</div>
			</form>
			<br/>
			<xsl:if test="page/formatted/xml/bom">
				<table style="border-style: solid; min-width: auto; border: 1px solid #000;" class="query_list">
					<xsl:variable name="size" select="$command_xml/bom/max_size"/>
					<xsl:for-each select="$command_xml/bom/query">
						<xsl:variable name="p" select="position()"/>
						<xsl:variable name="border" select="if (@qty = '0') then '2px solid red;' else '1px solid #e0e0e0;'"/>
						<tr class="query_line" style="border: {$border}">
							<td><input name="n_{$p}" value="{.}" class="query_name" size="{$size}" style="border: 1px solid #404040;"/></td>
							<td><input name="q_{$p}" value="{@qty}" class="query_qty" type="number" style="border: 1px solid #e0e0e0;"/></td>
						</tr>
					</xsl:for-each>
				</table>
				<div style="display: flex">
					<div>
						<button class="button" type="submit" onclick="submitSearch(); return false;" style="margin-right: 10px;">Найти</button>
						<xsl:call-template name="SAVE_BOM_BUTTON"/>
					</div>
				</div>
			</xsl:if>
		</div>
		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text"/>
			</div>
		</xsl:if>
		<xsl:call-template name="SAVE_BOM_FORM"/>

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			function submitSearch() {
				var text = $('#query_form').find('textarea').text();
				if (text.length != 0)
					text += '\n';
				$('.query_list').find('tr').each(function() {
					var query = $(this).find('input').eq(0).val();
					var number = $(this).find('input').eq(1).val();
					text += query + ' ' + number + '\n';
				});
				$('#query_form').find('textarea').text(text);
				$('#query_form').attr('action', '<xsl:value-of select="page/validate_bom_link"/>');
				postForm('query_form', 'query_form', function() {
					$('#modal_popup').show();
				});
			}
		</script>
	</xsl:template>

</xsl:stylesheet>