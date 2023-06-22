<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:import href="wellness_inc.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="sel" select="page/variables/sel"/>
	<xsl:variable name="info" select="page/services//service[@key = $sel]"/>
	<xsl:variable name="content" select="page/info"/>
	<xsl:variable name="parent" select="page/services//service[./service/@key = $sel]"/>
	<xsl:variable name="sec" select="page/services[.//@key = $sel]"/>

	<xsl:template name="INNER_CONTENT">
		<div class="col-sm-4 col-md-3 hidden-xs">
			<!-- <h1>О санатории</h1> -->
			<ul class="list-group side-menu">
				<xsl:for-each select="$sec/service">
					<li class="list-group-item{' active'[current()/@key = $sel]}">
						<a href="{if (service) then show_pages else show_page}"><xsl:value-of select="name"/></a>
					</li>
					<xsl:if test="service and .//@key = $sel">
						<xsl:for-each select="service">
							<li class="list-group-item level-2{' active'[current()/@key = $sel]}">
								<a href="{show_page}"><xsl:value-of select="name"/></a>
							</li>
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
			</ul>
		</div>
		<div class="col-xs-12 col-sm-8 col-md-9">
			<div class="row">
				<div class="col-xs-12">
					<div class="path hidden-xs">
						<a href="{page/index_link}">Главная страница</a> →
						<a><xsl:value-of select="$sec/header"/></a> →
						<xsl:for-each select="page/services//service[.//service/@key = $sel]">
							<a href="{show_pages}"><xsl:value-of select="name"/></a> →
						</xsl:for-each>
					</div>
					<h2 class="m-t-zero"><xsl:value-of select="$info/name"/></h2>
					<div class="row">
						<div class="col-xs-12">
							<xsl:call-template name="FILES"/>
							<xsl:if test="not($info/section)">
								<xsl:if test="not($content/module)">
									<xsl:apply-templates select="$content" mode="content"/>
								</xsl:if>
								<xsl:if test="$content/module">
									<xsl:apply-templates select="$content" mode="wellness"/>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$info/service">
								<xsl:for-each select="$info/service">
									<div class="col-md-4 col-sm-6 col-xs-12">
										<a href="{show_page}" class="section-thumbnail">
											<xsl:if test="header_pic and not(header_pic = '')">
												<xsl:attribute name="style" select="concat('background-image: url(', @path, header_pic, ');')"/>
											</xsl:if>
										</a>
										<a href="{show_page}">
											<h4><xsl:value-of select="name"/></h4>
										</a>
										<xsl:value-of select="short" disable-output-escaping="yes"/>
									</div>
									<xsl:if test="position() mod 3 = 0">
										<div class="clearfix"></div>
									</xsl:if>
								</xsl:for-each>
							</xsl:if>
						</div>
					</div>
				</div>
			</div>
			<div class="row"></div>
		</div>
		<xsl:call-template name="STOM_MODAL"/>
	</xsl:template>

	<xsl:template name="FILES">
		<xsl:variable name="file_src" select="if ($sec/file_list) then $sec else (if ($parent and $parent/file_list) then $parent else $sec)"/>
		<xsl:if test="$file_src/file_list">
			<p class="p-b-small" style="{'display: inline-block;'[$sel = 'stomatologiya' or $parent//@key = 'stomatologiya']}">
				
				<i class="fa fa-file"></i> <a href="" data-toggle="modal" data-target="#modal-doc-list">Показать список документов</a>
			</p>
			
			<xsl:if test="$sel = 'stomatologiya' or $parent//@key = 'stomatologiya'">
				<p style="display: inline-block; margin-left: 40px;">
					
						<button class="btn btn-primary btn-block" type="submit" data-toggle="modal" data-target="#modal-stom">Подать заявку на стоматологические услуги</button>
					
				</p>
			</xsl:if>	
			<div class="modal fade" id="modal-doc-list" tabindex="-1" role="dialog">
				<div class="modal-dialog modal-md" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
							<h4 class="modal-title">Список документов</h4>
						</div>
						<div class="modal-body">
							<ul class="list-unstyled">
								<xsl:for-each select="$file_src/file_list/file">
								<li>
									<i class="fa fa-file-pdf-o"></i>
									<a class="{file/@extenstion}" hdownload="{name}.{@extenstion}" href="{@path}{file}" target="_blank" title="создан: {file/@created}">
										<xsl:value-of select="name"/>
									</a>
								</li>
								</xsl:for-each>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>