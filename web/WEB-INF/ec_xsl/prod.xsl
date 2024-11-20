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

	<xsl:variable name="proddd" select="page/product"/>
	<xsl:variable name="msec" select="$proddd/main_section"/>
	<xsl:variable name="critical_item" select="$proddd"/>

<!-- 	<xsl:variable name="canonical" select="substring-before(concat(page/base,/,if(page/@name != 'index') then page/source_link else ''), '?')"/> -->

<!-- 	<xsl:variable name="sec" select="page/section"/> -->
<!-- 	<xsl:variable name="m" select="page/catalog/main_section[@id = $msec/@id]"/> -->
	<xsl:variable name="sec1" select="page/catalog/main_section[@id = $msec/@id]//section[filter_tag = $proddd/tag]"/>

	<xsl:variable name="sec" select="if(not($sec1)) then $msec else $sec1"/>


	<xsl:variable name="local_title" select="$proddd/name"/>
	<xsl:variable name="local_keywords" select="$proddd/name"/>
	<xsl:variable name="local_description" select="concat('Компания ТермоБрест производит, продает и организует поставки ', $proddd/name, ' по всей территории России, Казахстана, Украины, странах Европейского Союза и в Китае.')"/>
	
	<xsl:variable name="seo" select="$proddd/seo"/>

	<xsl:variable name="title" select="$seo_title"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:template name="INNER_CONTENT">
	<xsl:text disable-output-escaping="yes">
		&lt;!-- 
	</xsl:text>
		<xsl:value-of select="concat('https://termobrest.ru/admin_set_item.action?itemId=', $proddd/@id, '&amp;itemType=8')" />
	<xsl:text disable-output-escaping="yes">
		--&gt; 
	</xsl:text>		
	<div class="row">
		<div class="col-xs-12 col-md-6">
			<a href="#" data-toggle="modal" data-target="#q11-zoom">
				<img src="{if($proddd/img_small != '') then concat($proddd/@path,$proddd/img_small) else 'images/noimage.png'}" alt="{$proddd/alt}" title="{$proddd/alt}" style="max-width: 100%;" />
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
				<xsl:if test="not($proddd/product_tab)">
					<li role="presentation" class="active">
						<a href="#description" role="tab" data-toggle="tab">Описание</a>
					</li>
					<li role="presentation">
						<a href="#mods" role="tab" data-toggle="tab">Модификации</a>
					</li>
				</xsl:if>
				<xsl:if test="$proddd/product_tab">
					<xsl:for-each select="$proddd/product_tab">
						<xsl:variable name="p" select="position()"/><!-- current[$p = 1] -->
						<li role="presentation" class="active">
							<a href="#tab{$p}" role="tab" data-toggle="tab"><xsl:value-of select="name" /></a>
						</li>
					</xsl:for-each>								
				</xsl:if>
			</ul>

			<div class="tab-content">
				<xsl:if test="not($proddd/product_tab)">
					<div role="tabpanel" class="tab-pane active" id="description">
						<div class="col-xs-12" style="padding-top: 2rem;">
							<!-- <h3>Описание</h3> -->
							<xsl:value-of select="$proddd/text" disable-output-escaping="yes"/>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane active" id="mods">
						<div class="col-xs-12" style="padding-top: 2rem;">
							<!-- <h3>Модификации</h3> -->
							<xsl:value-of select="$proddd/size" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="$proddd/product_tab">
					<xsl:for-each select="$proddd/product_tab">
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
	<xsl:if test="$proddd/img_big != ''">
		<div class="modal fade" id="q11-zoom" tabindex="-1">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span>×</span>
						</button>
					</div>
					<div class="modal-body">
						<img src="{$proddd/@path}{$proddd/img_big}" alt="" style="max-width: 100%;"/>
					</div>
				</div>
			</div>
		</div>
	</xsl:if>
	<!-- modal фотоувеличение -->
</xsl:template>

</xsl:stylesheet>