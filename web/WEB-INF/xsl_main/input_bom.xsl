<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="snippets/custom_blocks.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="is_search_multiple" select="true()"/>

	<xsl:variable name="title" select="'Поиск BOM'"/>
	<xsl:variable name="h1" select="'Поиск BOM'"/>


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
		<div class="text">
			<form method="post" action="{page/input_bom_link}">
				<textarea class="input header-search__input" placeholder="Введите поисковый запрос"
						  autocomplete="off" name="q" autofocus="" style="width:100%; height: 200px;"><xsl:value-of select="$query" /></textarea>
				<button class="button" type="submit">Сформировать спецификацию</button>
				<button class="button" type="submit" onclick="$(this).closest('form').attr('action', 'search_prices')" style="margin-left: 20px">Найти</button>
			</form>
			<br/>
			<xsl:if test="page/formatted/xml/bom">
				<table style="border-style: solid; min-width: auto; border: 1px solid #000;">
					<xsl:variable name="size" select="page/formatted/xml/bom/max_size"/>
					<xsl:for-each select="page/formatted/xml/bom/query">
						<xsl:variable name="p" select="position()"/>
						<tr class="query_line" style="border: 1px solid #e0e0e0;">
							<td><input name="n_{$p}" value="{.}" class="query_name" size="{$size}" style="border: 1px solid #404040;"/></td>
							<td><input name="q_{$p}" value="{@qty}" class="query_qty" type="number" style="border: 1px solid #e0e0e0;"/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
		</div>
		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text"/>
			</div>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>