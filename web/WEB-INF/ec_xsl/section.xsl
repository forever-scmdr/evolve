<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_section}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1><xsl:value-of select="$sel_sec/name"/></h1>
		<xsl:if test="not($sel_sec/product)">
			<div class="page-content m-t">
				<div class="catalog-items"><!-- добавить класс lines для отображения по строкам -->
					<xsl:for-each select="/page/catalog//section[@id = $sel_sec_id]/section">
						<div class="catalog-item">
							<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>
							<a href="{show_section}" class="image-container"><img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/></a>
							<div>
								<a href="{show_section}" style="height: unset;"><xsl:value-of select="name"/></a>
								<xsl:value-of select="short" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="$sel_sec/product">
			<div class="page-content m-t">
				<div class="view-container desktop">
					<div class="view">
						<span>Показывать:</span>
						<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
						<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
						<div class="checkbox">
							<label>
								<input type="checkbox"/> в наличии
							</label>
						</div>
						<span>
							<select class="form-control">
								<option>Сначала дешевые</option>
								<option>Сначала дорогие</option>
								<option>По алфавиту А→Я</option>
								<option>По алфавиту Я→А</option>
							</select>
						</span>
					</div>
					<div class="quantity">
						<span>Кол-во на странице:</span>
						<span>
							<select class="form-control">
								<option>10</option>
								<option>20</option>
								<option>30</option>
								<option>40</option>
							</select>
						</span>
					</div>
				</div>

				<div class="catalog-items{' lines'[$view = 'list']}"><!-- добавить класс lines для отображения по строкам -->
					<xsl:apply-templates select="$sel_sec/product"/>
				</div>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


</xsl:stylesheet>