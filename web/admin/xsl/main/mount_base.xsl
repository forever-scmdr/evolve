<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	
	<!-- Внешние переменные (переопределяются в шаблонах, которые импортируют этот файл) -->
	<xsl:variable name="input_type" select="'checkbox'"/>
	<xsl:variable name="button_img" select="'admin/admin_img/add_link.png'"/>
	<xsl:variable name="view_type" select="''"/>
	<xsl:variable name="mount_text"></xsl:variable>
	<xsl:variable name="mounted_text"></xsl:variable>
	<xsl:variable name="form_action" select="''"/>
	
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
				<a href="javascript:mainView('{link}')"><xsl:value-of select="@caption"/></a>
			</td>
		</tr>
	</table>
	</xsl:template>


	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->


	<xsl:template match="/">
		<xsl:call-template name="MESSAGE"/>
		<table class="type_1">
			<tr>
				<td class="left">
					<table class="voodoo_links">
						<!-- Путь -->
						<tr>
							<td class="p_1" colspan="2">
							<xsl:for-each select="/admin-page/path/item[position() != last()]">
								<a href="javascript:mainView('{link}')"><xsl:value-of select="@caption"/></a>
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
								<form id="addForm" action="{$form_action}" method="post">
									<input type="hidden" name="vt" value="{$view_type}"/>
									<input type="hidden" name="itemId" value="{$item/@id}"/>
									<input type="hidden" name="parentId" value="{$parent/@id}"/>
									<xsl:apply-templates select="admin-page/mount/type"/>
									<xsl:if test="admin-page/mount//input">
									<div class="add_link_button">
										<a href="#" onclick="$('#addForm').submit(); return false;">
											<img src="{$button_img}" alt="" />
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
								<form id="deleteForm" action="admin_delete_reference.action" method="post">
									<input type="hidden" name="vt" value="{$view_type}"/>
									<input type="hidden" name="itemId" value="{$item/@id}"/>
									<input type="hidden" name="parentId" value="{$parent/@id}"/>
									<xsl:apply-templates select="admin-page/mounted/type"/>
									<xsl:if test="admin-page/mounted//input">
									<div class="add_link_button">
										<a href="#" onclick="$('#deleteForm').submit(); return false;">
											<img src="admin/admin_img/remove_link.png" alt="" />
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
			var refreshSubitems = function() {
				refreshView('subitems');
			};
			mainForm('addForm', refreshSubitems);
			mainForm('deleteForm', refreshSubitems);
		</script>
	</xsl:template>
		
</xsl:stylesheet>