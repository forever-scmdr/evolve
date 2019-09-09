<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="page/current_section/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>
	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt; <a href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_section}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<xsl:if test="$seo[1]/text">
			<div class="page-content m-t">
				<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
		<div class="page-content m-t">

			<!-- FILTERS -->

			<xsl:if test="$sel_sec/params_filter/filter">
				<div class="toggle-filters">
					<i class="fas fa-cog"></i> <a onclick="$('#filters_container').toggle('blind', 200);">Подбор по параметрам</a>
				</div>
			</xsl:if>

			<xsl:if test="$sel_sec/params_filter/filter">
				<form method="post" action="{$sel_sec/filter_base_link}">
					<div class="filters" style="{'display: none'[not($user_filter)]}" id="filters_container">
						<xsl:for-each select="$sel_sec/params_filter/filter/input">
							<xsl:variable name="name" select="@id"/>
							<div class="active checkgroup">
								<strong><xsl:value-of select="@caption"/></strong>
								<div class="values">
									<xsl:for-each select="domain/value">
										<div class="checkbox">
											<label>
												<input name="{$name}" type="checkbox" value="{.}">
													<xsl:if test=". = $user_filter/input[@id = $name]">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>&#160;<xsl:value-of select="."/>
											</label>
										</div>
									</xsl:for-each>
								</div>
							</div>
						</xsl:for-each>
						<div class="buttons">
							<input type="submit" value="Показать найденное"/>
							<input type="submit" value="Сбросить" onclick="location.href = '{page/reset_filter_link}'; return false;"/>
						</div>
					</div>
				</form>
			</xsl:if>
			<!-- END_FILTERS -->

			<div class="view-container desktop">
				<div class="view">
					<span>Показывать:</span>
					<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
					<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
				</div>
			</div>
			<div class="catalog-items{' lines'[$view = 'list']}"><!-- добавить класс lines для отображения по строкам -->
				<xsl:apply-templates select="page/current_section/section | page/current_section/product"/>
<!-- 				<xsl:for-each select="page/current_section/section">
					<xsl:variable name="main_pic" select="product[1]/gallery[1]"/>
					<div class="catalog-item">
						<xsl:variable name="pic_path" select="if ($main_pic) then concat(product[1]/@path, $main_pic) else 'img/no_image.png'"/>
						<a href="{show_products}" class="image-container"><img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')" alt="{name}"/></a>
						<div>
							<a href="{show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each> -->
			</div>
		</div>
		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

	<xsl:template match="section">
		<div class="catalog-item">
			<xsl:variable name="pic_path" select="if (main_pic != '') then concat(@path, main_pic) else 'img/no_image.png'"/>
			<a href="{show_products}" class="image-container" style="background-image: url({$pic_path});">
				<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')" alt="{name}"/> -->
			</a>
			<div>
				<a href="{show_products}" style="height: unset;">
					<xsl:value-of select="name"/>
				</a>
				<div class="price">
					<span>Вариантов расцветки: </span>
					<xsl:value-of select="count(product)"/>
				</div>
				<p class="inline-only">
					<xsl:for-each select="params/param">
						<xsl:if test="position() &gt; 1">
							<xsl:call-template name="BR"/>
						</xsl:if>
						<span class="caption">
							<xsl:value-of select="@caption"/>
						</span>
						<span class="value">
							<xsl:value-of select="."/>
						</span>
					</xsl:for-each>
				</p>
				<xsl:value-of select="short" disable-output-escaping="yes"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>