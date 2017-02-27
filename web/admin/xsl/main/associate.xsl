<?xml version="1.0" encoding="UTF-8"?><!-- commit me!! -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<xsl:template name="TITLE">CMS - Items</xsl:template><!-- ******************* TODO LOCAL ******************** -->
	
	<!-- Внешние переменные (переопределяются в шаблонах, которые импортируют этот файл) -->
	<xsl:variable name="input_type" select="'checkbox'"/>
	<xsl:variable name="button_img" select="'admin/admin_img/add_link.png'"/>
	<xsl:variable name="view_type" select="'associate'"/>
	<xsl:variable name="mount_text">Добавить в "<xsl:value-of select="$item/@caption"/>" ссылку на:</xsl:variable>
	<xsl:variable name="mounted_text">"<xsl:value-of select="$item/@caption"/>" содержит ссылки на:</xsl:variable>
	
	<!-- Внутренние переменные -->
	<xsl:variable name="item" select="/admin-page/item"/>
	<xsl:variable name="parent" select="/admin-page/path/item[position() = last()]"/>

	<xsl:template match="type[count(item) = 1]">
		<xsl:apply-templates select="item" mode="extended"/>
	</xsl:template>

	<xsl:template match="type">
		<h2 class="type"><xsl:value-of select="@caption"/></h2>
		<xsl:apply-templates select="item" mode="normal"/>
	</xsl:template>

	<xsl:template match="item" mode="extended">
	<div class="link_item">
		<xsl:if test="@type-caption != @caption">
		<div class="item_type">
			[<xsl:value-of select="@type-caption"/>]
		</div>
		</xsl:if>
		<xsl:apply-templates select="." mode="inner"/>
	</div>
	</xsl:template>
	
	<xsl:template match="item" mode="normal">
	<div class="link_item">
		<xsl:apply-templates select="." mode="inner"/>
	</div>
	</xsl:template>
	
	<xsl:template match="item" mode="inner">
	<table>
		<tr>
			<td class="action">
				<xsl:if test="input">
				<input name="{input/@name}" type="{$input_type}" value="{input}" />
				</xsl:if>
			</td>
			<td class="link">
				<a href="{link}"><xsl:value-of select="@caption"/></a>
			</td>
		</tr>
	</table>
	</xsl:template>


	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->



	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html>
			<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<meta http-equiv="Pragma" content="no-cache"/>
			<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
			<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css"/>
			<xsl:text disable-output-escaping="yes">
				&lt;!--[if IE 7]&gt;
				&lt;link href="css/ie.css" rel="stylesheet" type="text/css" /&gt;
				&lt;![endif]--&gt;
			</xsl:text>
			<title><xsl:call-template name="TITLE"/></title><!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO LOCAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
			<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
		
			<link rel="stylesheet" type="text/css" href="admin/css/style.css" />
			</head>
			<body>
			<script language="javascript" type="text/javascript" src="admin/js/admin.js"></script>
			<!-- ************************ Основная форма **************************** -->
			<!-- ******************************************************************** -->
			<div class="mainwrap">
				<xsl:call-template name="MESSAGE"/>
				<table class="type_1">
					<tr>
						<td class="left">
							<table class="voodoo_links">
								<!-- Путь -->
								<tr>
									<td class="p_1" colspan="2">
									<xsl:for-each select="/admin-page/path/item[position() != last()]">
										<a href="{link}"><xsl:value-of select="@caption"/></a>
										<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
									</xsl:for-each>
									<strong><xsl:value-of select="$parent/@caption"/></strong>
									</td>
								</tr>
								<tr>
									<td class="add_links" width="50%">
										<!-- add_links -->
										<h3><xsl:value-of select="$mount_text"/></h3>
										<!-- ************************ Айтемы, в которых можно создавать ссылку на текущий **************************** -->
										<form id="addForm" action="{admin-page/mount/link}" method="post">
											<xsl:apply-templates select="admin-page/mount/type"/>
											<xsl:if test="admin-page/mount//input">
											<div class="add_link_button">
												<a href="#" class="button" onclick="$('#addForm').submit(); return false;">
													Добавить ассоциации
												</a>
											</div>
											</xsl:if>
										</form>
										<!-- ************************************************************************************** -->
									</td>
									<xsl:if test="admin-page/mounted">
									<td class="remove_links">
										<!-- remove_links -->
										<h3><xsl:value-of select="$mounted_text"/></h3>
										<!-- ************************ Айтемы, которые содержат ссылку на текущий **************************** -->
										<form id="deleteForm" action="{admin-page/mounted/link}" method="post">
											<xsl:apply-templates select="admin-page/mounted/type"/>
											<xsl:if test="admin-page/mounted//input">
											<div class="add_link_button">
												<a href="#" class="button delete" onclick="$('#deleteForm').submit(); return false;">
													Удалить ассоциации
												</a>
											</div>
											</xsl:if>
										</form>
										<!-- ************************************************************************************** -->
									</td>
									</xsl:if>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<script>
				var inFormOrLink = false;
				$('a').on('click', function() { inFormOrLink = true; });
				$('form').on('submit', function() { inFormOrLink = true; });
				
				$(window).bind("beforeunload", function() { 
					if (!inFormOrLink)
						window.opener.location.href = '<xsl:value-of select="admin-page/link"/>';
				});
				</script>
			</div>
			</body>
		</html>
	</xsl:template>
		
</xsl:stylesheet>