<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="parent" select="page/variables/parent"/>
	<xsl:variable name="sel" select="page/variables/sel"/>
	<xsl:variable name="info" select="page/services[@key = $sel] | page/info[@key = $sel]"/>
	<xsl:variable name="sec" select="page/services[@key = $parent]"/>
	<xsl:variable name="k2" select="page/variables/parent2"/>

	<xsl:template name="CONTENT">
		<xsl:call-template name="SIDE_MENU"/>
		<div class="right">
			<xsl:call-template name="PATH"/>
			<h1>
				<xsl:value-of select="$info/name | $info/header"/>
			</h1>
			<xsl:call-template name="FILES"/>
			<xsl:value-of select="$info/text" disable-output-escaping="yes"/>
			<xsl:apply-templates select="$info/text_part | $info/gallery_part"/>
		</div>
	</xsl:template>

	<xsl:template name="FILES">
		<xsl:if test="not($sec) and $info/file_list">
			<div class="file-list">
				<xsl:for-each select=" $info/file_list/file">
					<a class="download {file/@extenstion}" download="{name}.{file/@extenstion}" href="{@path}{file}" title="создан: {file/@created}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
		</xsl:if>
		<xsl:if test="$sec and $sec/file_list/file">
			<div class="file-list">
				<p><a class="javascript toggle" href="#files">Show documentation</a></p>
				<div id="files" style="display: none;">
				<xsl:for-each select="$sec/file_list/file">
					<a class="download {file/@extenstion}" download="{name}.{@extenstion}" href="{@path}{file}" title="создан: {file/@created}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
				</div>
			</div>
		</xsl:if>
	
	</xsl:template>

	<xsl:template name="PATH">
		<div class="path">
			<a href="{/page/index_link}">Homepage</a><xsl:call-template name="arrow"/>
			<xsl:if test="$sec">
				<a href="{$sec/show_page}"><xsl:value-of select="$sec/header"/></a><xsl:call-template name="arrow"/>
			</xsl:if>
			<xsl:if test="page/variables/parent2 != '-'">
				
				<xsl:variable name="p2" select="//service[@key = $k2]"/>
				<a href="{$p2/show_page}"><xsl:value-of select="$p2/name"/></a><xsl:call-template name="arrow"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="SIDE_MENU">

		<div class="submenu">
			<ul>
				<xsl:if test="$sec">
					<xsl:for-each select="$sec/service">
						<li>
							<a class="{'open'[current()/@key = $sel]}" href="{show_page}">
								<xsl:value-of select="name" />
							</a>
							<xsl:if test="current()/@key = $sel or current()/@key = $k2">
								<xsl:for-each select="service">
									<xsl:variable name="p" select="position()" />
									<xsl:if test="$p = 1">
										<xsl:text disable-output-escaping="yes">&lt;ul class="srv" &gt;</xsl:text>
									</xsl:if>
									<li>
										<a class="{'open'[current()/@key = $sel]}" href="{show_page}">
											<xsl:value-of select="name" />
										</a>
									</li>
									<xsl:if test="$p = last()">
										<xsl:text disable-output-escaping="yes">&lt;/ul&gt;</xsl:text>
									</xsl:if>
								</xsl:for-each>
							</xsl:if>
						</li>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="not($sec)">
					<xsl:for-each select="$info/service">
						<li>
							<a href="{show_page}">
								<xsl:value-of select="name" />
							</a>
						</li>
					</xsl:for-each>
				</xsl:if>
			</ul>
		</div>
	</xsl:template>

</xsl:stylesheet>