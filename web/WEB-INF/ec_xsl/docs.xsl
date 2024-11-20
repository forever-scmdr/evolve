<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="inputs_inc.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="sec" select="page/doc_section"/>
	<xsl:variable name="critical_item" select="$sec"/>
	<xsl:variable name="parent" select="if(/page/docs/*[@id = $sec/@id]) then page/docs else page/three_dmodels"/>
	<!-- <xsl:variable name="title" select="concat($sec/name, ' - Документы и сертификаты')"/>	 -->


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="if(not(starts-with($sec/name, '3D-модели'))) then 'docs' else '3d'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-sm-4 col-md-3 hidden-xs side-coloumn">
				<h1 class="no-top-margin"><xsl:value-of select="if($seo/h1 != '') then $seo/h1 else $sec/name"/></h1>
				<ul class="list-group side-menu">
					<xsl:for-each select="$parent/doc_section">
						<li class="list-group-item">
							<a href="{show_section}" class="{'active-link'[current()/@id = $sec/@id]}"><xsl:value-of select="name"/></a>
						</li>
					</xsl:for-each>
				</ul>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-9">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
							<xsl:if test="$parent = page/docs">
								<a href="{page/all_docs_link}">Все документы</a>
							</xsl:if>
							<xsl:if test="$parent = page/three_dmodels">
								<a href="{page/all_tdm_link}">Все 3D модели</a>
							</xsl:if>
							→

						</div>
						<h2 class="no-top-margin"><xsl:value-of select="$sec/name"/></h2>
					</div>
				</div>

				<div class="row section-items">
					<xsl:for-each select="$sec/img_doc | $sec/file_doc">
						<xsl:apply-templates select="current()"/>
						<xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 3 = 0">
							<div class="clearfix hidden-xs hidden-sm"></div>
						</xsl:if>
					</xsl:for-each>
				</div>
<!-- 
				<div class="row">
					<div class="col-xs-12 content-block bottom-links-block">
						<div style="border: 1px solid #363636; padding: 10px; margin-top: 20px;">
							<h3 style="text-align: center;">Продукция</h3>
								<xsl:for-each select="/page/catalog/main_section">
								
								<xsl:if test="position() = 1">
									<xsl:text disable-output-escaping="yes">
										&lt;div class="sep"&gt;
									</xsl:text>
								</xsl:if>
								
								<a href="{/page/base}/{show_section}">
									<xsl:value-of select="name"/>
								</a>
								
								<xsl:if test="position() mod 3 = 0 and position() != last()">
									<xsl:text disable-output-escaping="yes">
									&lt;/div&gt;&lt;div class="sep"&gt;
									</xsl:text>
								</xsl:if>

							</xsl:for-each>
						</div>
					</div>
				</div>
 -->				
			</div>
		</div>
	</div>
	</xsl:template>

	<xsl:template match="file_doc">
		<xsl:if test="not(small) or small = ''">
			<div class="col-xs-6 col-sm-6 col-md-4">
<!-- 				<a href="{@path}{file}" target="_blank"> -->
<!-- 					<img src="img/document.png" alt="" style="max-width: 100%;"/> -->
<!-- 				</a> -->
				<a href="{@path}{file}" target="_blank" title="{title}">
					<xsl:value-of select="name"/>
				</a>
			</div>
<!-- 			<div class="w-col w-col-4"> -->
<!-- 				<a href="{@path}{file}" class="{file/@extenstion} doc12" target="_blank"> -->
<!-- 					<h5 class="document_heading"> -->
<!-- 						<xsl:value-of select="name"/> -->
<!-- 					</h5> -->
<!-- 				</a> -->
<!-- 			</div> -->
		</xsl:if>
		<xsl:if test="small != ''">
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="{@path}{file}" target="_blank">
					<img src="{@path}{small}" alt="{if(alt != '') then alt else name}" style="max-width: 100%;" title="{if(title != '') then title else name}"/>
				</a>
				<a href="{@path}{file}" target="_blank">
					<xsl:value-of select="name"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="img_doc">
		<div class="col-xs-6 col-sm-6 col-md-4">
			<a href="{@path}{pic}" data-toggle="modal" data-target="#doc{@id}-zoom">
				<img src="{@path}{small}" alt="{if(alt != '') then alt else name}" title="{if(title != '') then title else name}" style="max-width: 100%;"/>
			</a>
			<a href="{@path}{pic}" data-toggle="modal" data-target="#doc{@id}-zoom"><xsl:value-of select="name"/></a>
		</div>
		<div class="modal fade" id="q11-zoom" tabindex="-1">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span>×</span></button>
					</div>
					<div class="modal-body">
						<img src="{@path}{pic}" alt="{if(alt != '') then alt else name}" title="{if(title != '') then title else name}" style="max-width: 100%;"/>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>