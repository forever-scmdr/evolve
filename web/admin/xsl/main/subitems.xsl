<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<!-- Выбор создаваемого айтема из списка -->
	<xsl:template match="item-to-add[item]">
		<li class="dragable visible" style="position: relative; padding-right: 27px;">
			<select id="is_{@id}">
				<xsl:apply-templates select="item" mode="select"/>
			</select>
			<a class="create-button" onclick="mainView($('#is_{@id}').find(':selected').val());return false;"></a>
		</li>
		<li class="drop-zone"></li>
	</xsl:template>
	

	<!-- Ссылка на создание айтема -->
	<xsl:template match="item-to-add[not(item)]">
		<li class="dragable visible">
			<a href="javascript:mainView('{create-link}')" >
				<span class="name"><xsl:value-of select="@caption"/></span>
			</a>
		</li>
		<li class="drop-zone"></li>
	</xsl:template>

	<!-- Не выводить виртуальные айтемы -->
	<xsl:template match="item-to-add[@virtual = 'true']"></xsl:template>

	<!-- Один из типов айтема из списка для создания (по умолчанию) -->
	<xsl:template match="item[@default = 'true']" mode="select">
	<option value="{create-link}" selected="selected"><xsl:value-of select="@caption"/></option>
	</xsl:template>
	
	<!-- Один из типов айтема из списка для создания (не по умолчанию) -->
	<xsl:template match="item" mode="select">
	<option value="{create-link}"><xsl:value-of select="@caption"/></option>
	</xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">

		<xsl:call-template name="MESSAGE"/>
		

		<!-- Создание новых айтемов -->
		<xsl:if test="admin-page/assoc/item-to-add">
			<div class="list">
				<h4>Созадать</h4>
				<xsl:for-each select="admin-page/assoc">
					<h5><xsl:value-of select="@caption"/></h5>
					<ul class="create">
						<li class="drop-zone"></li>
						<xsl:apply-templates select="item-to-add"/>
					</ul>
				</xsl:for-each>
			</div>
		</xsl:if>

		<!-- Буфер обмена -->
		<div id="pasteBuffer"></div>
		
		<!-- Редактирование существующих айтемов -->
		<xsl:if test="admin-page/assoc/item">

			<xsl:variable name="differentSubitems" select="count(admin-page/assoc/item-to-add) &gt; 1 or admin-page/assoc/item-to-add/item or admin-page/assoc/item/@type-id != admin-page/assoc/item/@type-id"/>
			<div class="list">
				<h4>Редактировать</h4>
				<xsl:for-each select="admin-page/assoc">
					<xsl:value-of select="@caption"/>
					<ul class="edit drag_area">
						<div style="padding-top: 14px; padding-bottom: 14px">
							<xsl:for-each select="item">
								<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
								<xsl:variable name="itemId" select="concat('item', @id, ':', @weight)" />
								<li class="drop-zone"></li>
								<li class="dragable visible multiple" id="{$itemId}">
									<a href="{edit-link}" class="name" title="редактировать">
										<xsl:if test="$differentSubitems and @type-caption != @caption and @caption != ''">
											<span class="description">[<xsl:value-of select="@type-caption"/>]</span><br/>
										</xsl:if>
										<xsl:value-of select="$caption"/>
									</a>
									<a onclick="insertAjaxView('{copy-link}', 'pasteBuffer'); return false;" class="copy" title="копировать">копировать</a>
									<a href="javascript:defaultView('{delete-link}','subitems',true, refreshMain)" class="delete" title="удалить">удалить</a>
								</li>
							</xsl:for-each>
							<li class="drop-zone"></li>
						</div>
					</ul>
				</xsl:for-each>
			</div>
		</xsl:if>
		<script type="text/javascript">
			var reorderLink = "<xsl:value-of select="admin-page/link[@name='reorder']"/>";	
		</script>
		<script type="text/javascript" src="admin/js/subitems.js"></script>
		<script type="text/javascript">
			insertAjaxView('<xsl:value-of select="admin-page/get-paste"/>', 'pasteBuffer');
		</script>
	</xsl:template>
		
</xsl:stylesheet>