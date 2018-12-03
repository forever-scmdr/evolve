<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</h1>

		<div class="page-content m-t">

			<xsl:if test="$products">
				<div class="view-container desktop">
					<div class="view">
						<span>Показывать:</span>
						<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
						<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
						<!-- <div class="checkbox">
							<label>
								<xsl:if test="not($only_available)">
									<input type="checkbox" onclick="window.location.href = '{page/show_only_available}'"/>
								</xsl:if>
								<xsl:if test="$only_available">
									<input type="checkbox" checked="checked" onclick="window.location.href = '{page/show_all}'"/>
								</xsl:if>
								в наличии
							</label>
						</div> -->
					</div>
				</div>
			</xsl:if>
			<div>
				<form action="">
					<input type="file"/>
					<input type="submit"/>
				</form>
				<h3>Таблица с результатами</h3>
				<table>
					<tr>
						<th>Фото</th>
						<th>Название</th>
						<th>Описание</th>
						<th>Производитель</th>
						<th>Код производителя</th>
						<th>Цена</th>
						<th>На складе</th>
						<th>Мин. заказ</th>
						<th>Заказать</th>
						<th></th>
					</tr>
					<tr>
						<td><a href=""><i class="fas fa-file-image"></i></a></td>
						<td><a href="">10A05-DC</a></td>
						<td>Диод: выпрямительный Шоттки; THT; 100В; 2x5А; TO220AB</td>
						<td><a href="">DC Components</a></td>
						<td><a href="">10A05</a></td>
						<td>23 руб./шт.</td>
						<td>112 шт.</td>
						<td>мин. 3 шт.</td>
						<td>
							<form action="">
								<input type="number" value="1"/>
								<input type="submit" value="Заказать"/>
							</form>
						</td>
						<td><a href=""><i class="fas fa-plus-square"></i></a></td>
					</tr>
					<tr>
						<td><a href=""><i class="fas fa-file-image"></i></a></td>
						<td><a href="">10A05-DC</a></td>
						<td>Диод: выпрямительный Шоттки; THT; 100В; 2x5А; TO220AB</td>
						<td><a href="">DC Components</a></td>
						<td><a href="">10A05</a></td>
						<td>23 руб./шт.</td>
						<td>112 шт.</td>
						<td>мин. 3 шт.</td>
						<td>
							<form action="">
								<input type="number" value="1"/>
								<input type="submit" value="Заказать"/>
							</form>
						</td>
						<td><a href=""><i class="fas fa-plus-square"></i></a></td>
					</tr>
				</table>
			</div>
			<div class="catalog-items{' lines'[$view = 'list']}">
				<xsl:if test="$view = 'table'">
					<xsl:apply-templates select="$products"/>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<xsl:apply-templates select="$products" mode="lines"/>
				</xsl:if>
				<xsl:if test="not($products)">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>

		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>