<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="get_doc.xsl"/>
	<xsl:import href="iframe_page_base.xsl"/>
	<xsl:import href="i_catalog_menu.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="msec" select="page/main_section"/>
	<xsl:variable name="sec" select="page/section"/>
	<xsl:variable name="base" select="page/base"/>


	<!-- ****************************    	ТЭГИ      **************************** -->

	<xsl:variable name="base_url" select="page/source_link"/>
	<xsl:variable name="page_url" select="page/base_link"/>

	<xsl:variable name="sec_tags" select="$sec/filter_tag"/>
	<xsl:variable name="extra_tags" select="page/variables/tag"/>

	<xsl:variable name="tags_single">
		<xsl:for-each select="distinct-values(page/main_section/section_tag/tag)">
			<xsl:sort select="contains(., ':')" order="descending" data-type="text"/>
			<xsl:sort select="." data-type="text" />
			<xsl:if test="not(. = $sec_tags)"><xsl:value-of select="concat(., '*|*')"/></xsl:if>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="tags_sorted" select="tokenize($tags_single,'\*\|\*')"/>
	
	<xsl:variable name="tag_groups_single">
		<xsl:for-each select="$tags_sorted[contains(., ':')]">
			<xsl:variable name="prev_idx" select="position() - 1"/>
			<xsl:variable name="group" select="normalize-space(tokenize(., ':')[1])"/>
			<xsl:variable name="prev_group" select="if ($prev_idx = 0) then '' else normalize-space(tokenize($tags_sorted[$prev_idx], ':')[1])"/>
			<xsl:if test="$group != $prev_group"><xsl:value-of select="concat($group, '*|*')"/></xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="groups" select="tokenize($tag_groups_single,'\*\|\*')"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:template name="FILTER">
	<xsl:if test="$groups != ''">
		<div class="collapse in filter" id="q11-filter" style="font-size: 12px;"><!-- класс  in делает блок с параметрами открытым по-умолчанию -->
			<div class="well">
				<div class="row">
					<xsl:variable name="pSortingValues" select="',Исходное состояние,Исполнение,Дополнительные устройства и исполнение корпуса,Регулирование,'"/>
					<xsl:for-each select="$groups[. != '' and . != 'Корпус']">
						<xsl:sort data-type="number" select="string-length(substring-before($pSortingValues,concat(',',.,',')))" />
						<xsl:variable name="group" select="." />
						<div class="col-xs-12 col-sm-6 col-md-3">
							<strong><xsl:value-of select="$group" /></strong>
							<xsl:for-each select="$tags_sorted[starts-with(., $group)]">
								<xsl:choose>
									<xsl:when test=". = $extra_tags">
										<xsl:variable name="href">
											<xsl:value-of select="$page_url" />
											<xsl:for-each select="$extra_tags[. != current()]">
												<xsl:value-of select="if (position() = 1) then concat('?tag=', .) else concat('&amp;tag=', .)" />
											</xsl:for-each>
										</xsl:variable>	
										<div class="checkbox">
											<label>
												<input type="checkbox" checked="checked" onchange="ieFucker('{$href}')"/>
												<xsl:value-of select="normalize-space(tokenize(., ':')[2])" />
											</label>
										</div>
									</xsl:when>
									<xsl:otherwise>
										<div class="checkbox">
											<label>
												<input type="checkbox" onchange="ieFucker('{f:addQueryVar($base_url, 'tag', .)}')"/>
												<xsl:value-of select="normalize-space(tokenize(., ':')[2])" />
											</label>
										</div>															
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</div>
						<xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 4 = 0">
							<div class="clearfix hidden-sm"></div>
						</xsl:if>
					</xsl:for-each>
					<script>
						function ieFucker(v){
							var ua = window.navigator.userAgent;
							if( ua.indexOf("Trident") == -1){
								document.location.replace(v);
							}
							else{
								var regExp = /([а-яА-я\s:])+/gim;
								found = v.match(regExp);
								if(found == null){
									document.location.replace("<xsl:value-of select="$base" />/"+v);
								}	
								<xsl:text disable-output-escaping="yes">
								for(i = 0; i&lt;found.length; i++){
								</xsl:text>
									v = v.replace(found[i], encodeURIComponent(found[i]));														
								}
								v = "<xsl:value-of select="$base" />/" + v;
								document.location.replace(v);
							}
						}
					</script>										
				</div>
				<div class="clearfix"></div>
<!-- 									<button type="button" class="btn btn-primary btn-sm">Показать результат (4)</button> -->
				<xsl:if test="$extra_tags">
					<button onclick="ieFucker('{$page_url}')" type="button" class="btn btn-info btn-sm">Сбросить выбор</button>
				</xsl:if>
			</div>
		</div>
	</xsl:if>
	</xsl:template>
	



	<xsl:template name="INNER_CONTENT">
	<div class="row section-items">
		<xsl:for-each select="page//product">
			<xsl:variable name="p" select="position()"/>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="{show_product}">
					<img src="{@path}{img_small}" alt="{alt}" style="max-width: 100%;" onerror="this.src = 'images/noimage.png'"/>
				</a>
				<a href="{show_product}"><xsl:value-of select="name"/></a>
			</div>
			<xsl:if test="$p mod 2 = 0">
				<div class="clearfix hidden-md hidden-lg"></div>
			</xsl:if>
			<xsl:if test="$p mod 3 = 0">
				<div class="clearfix hidden-xs hidden-sm"></div>
			</xsl:if>
		</xsl:for-each>
	</div>
	</xsl:template>

</xsl:stylesheet>