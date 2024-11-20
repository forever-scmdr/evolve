<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="get_doc.xsl"/>
	<xsl:import href="feedback.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="catalog_menu.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="prod" select="page/product"/>
	<xsl:variable name="msec" select="$prod/main_section"/>
<!-- 	<xsl:variable name="sec" select="page/section"/> -->
<!-- 	<xsl:variable name="m" select="page/catalog/main_section[@id = $msec/@id]"/> -->
	<xsl:variable name="sec" select="page/catalog/main_section[@id = $msec/@id]//section[filter_tag = $prod/tag]"/>


	<xsl:variable name="local_title" select="$prod/name"/>
	<xsl:variable name="local_keywords" select="$prod/name"/>
	<xsl:variable name="local_description" select="concat('Компания ТермоБрест производит, продает и организует поставки ', $prod/name, ' по всей территории России, Казахстана, Украины, странах Европейского Союза и в Китае.')"/>
	
	<xsl:variable name="seo" select="$prod/seo"/>

	<xsl:variable name="title" select="concat($seo_title, ' купить у производителя')"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:template name="INNER_CONTENT">
	<div class="row">
		<div class="col-xs-12 col-md-6">
			<a href="#" data-toggle="modal" data-target="#q11-zoom">
				<img  onerror="this.src = 'images/noimage.png'" src="{$prod/@path}{$prod/img_small}" alt="{$prod/alt}" style="max-width: 100%;" />
			</a>
		</div>
		<xsl:variable name="all_docs" select="/page/img_doc | /page/file_doc"/>							
		<xsl:if test="$all_docs">
			<div class="col-md-6 hidden-xs hidden-sm documents-download">
				<h3 class="no-top-margin">Документация</h3>
				<xsl:for-each-group select="$all_docs" group-by="@id">
					<xsl:variable name="pos" select="position()"/>
					<xsl:if test="$pos = 1">
						<xsl:text disable-output-escaping="yes">&lt;ul class="list-unstyled"&gt;</xsl:text>
					</xsl:if>
					<xsl:if test="$pos = 9">
						<xsl:text disable-output-escaping="yes">&lt;ul class="collapse list-unstyled" id="docs"&gt;</xsl:text>
					</xsl:if>
					<xsl:apply-templates select="current-group()[1]"/>
					<xsl:if test="$pos = 8 or $pos = last()">
						<xsl:text disable-output-escaping="yes">&lt;/ul&gt;</xsl:text>
					</xsl:if>
				</xsl:for-each-group>
				<a class="action-link" data-toggle="collapse" href="#docs">Показать все документы</a>
			</div>			
		</xsl:if>
	</div>


	<div class="row">
		<div class="col-xs-12">

			<!-- tabs -->
			<ul class="nav nav-tabs" role="tablist">
				<xsl:if test="not($prod/product_tab)">
					<li role="presentation" class="active">
						<a href="#description" role="tab" data-toggle="tab">Описание</a>
					</li>
					<li role="presentation">
						<a href="#mods" role="tab" data-toggle="tab">Модификации</a>
					</li>
				</xsl:if>
				<xsl:if test="$prod/product_tab">
					<xsl:for-each select="$prod/product_tab">
						<xsl:variable name="p" select="position()"/><!-- current[$p = 1] -->
						<li role="presentation" class="active">
							<a href="#tab{$p}" role="tab" data-toggle="tab"><xsl:value-of select="name" /></a>
						</li>
					</xsl:for-each>								
				</xsl:if>
			</ul>

			<div class="tab-content">
				<xsl:if test="not($prod/product_tab)">
					<div role="tabpanel" class="tab-pane active" id="description">
						<div class="col-xs-12">
							<h3>Описание</h3>
							<xsl:value-of select="$prod/text" disable-output-escaping="yes"/>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane active" id="mods">
						<div class="col-xs-12">
							<h3>Модификации</h3>
							<xsl:value-of select="$prod/size" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="$prod/product_tab">
					<xsl:for-each select="$prod/product_tab">
						<xsl:variable name="p" select="position()"/>
						<div role="tabpanel" class="tab-pane active" id="tab{$p}">
							<div class="col-xs-12">
								<h3><xsl:value-of select="name" /></h3>
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>								
				</xsl:if>	
			</div>

			<!-- tabs end -->
		</div>
	</div>
</xsl:template>

<xsl:template match="file_doc">
	<xsl:variable name="class">
		<xsl:choose>
			<xsl:when test="file/@extenstion = 'xls' or file/@extenstion = 'xlsx'">
				fa-file-excel-o
			</xsl:when>
			<xsl:when test="file/@extenstion = 'pdf' ">
				fa-file-pdf-o
			</xsl:when>
			<xsl:when test="file/@extenstion = 'doc' or file/@extenstion = 'docx' ">
				fa-file-word-o
			</xsl:when>
			<xsl:otherwise>
				fa-file-o
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<li>
		<i class="fa {$class}"></i>
		<a href="{@path}{file}" target="_blank"><xsl:value-of select="name"/></a>
	</li>
</xsl:template>

<xsl:template match="img_doc">
	<li>
		<a href="{@path}{pic}" class="doc-link fancybox-full" rel="doc_{@id}">
			<i class="fa fa-file-image-o" aria-hidden="true" style="color: orange;"></i>
			<span>
				<xsl:value-of select="name" />
			</span>
		</a>
		<xsl:if test="back">
			<a href="{@path}{back}" style="display:none;" class="fancybox-full" rel="doc_{@id}"></a>
		</xsl:if>
	</li>
</xsl:template>


<xsl:template name="INNER_POPUPS">
	<!-- modal фотоувеличение -->
	<div class="modal fade" id="q11-zoom" tabindex="-1">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
				</div>
				<div class="modal-body">
					<img src="{$prod/@path}{$prod/img_big}" alt="" style="max-width: 100%;"/>
				</div>
			</div>
		</div>
	</div>
	<!-- modal фотоувеличение -->
</xsl:template>

</xsl:stylesheet>