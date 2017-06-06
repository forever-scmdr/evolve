<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<!-- Выбор создаваемого айтема из списка -->
	<xsl:template match="item-to-add[item]">
		<xsl:param name="ass" select="'ass_0'" />
		<li class="dragable visible {$ass}" style="position: relative; padding-right: 27px;">
			<select id="is_{@id}">
				<xsl:apply-templates select="item" mode="select"/>
			</select>
			<a class="create-button" onclick="mainView($('#is_{@id}').find(':selected').val());return false;"></a>
		</li>
		<li class="drop-zone {$ass}"></li>
	</xsl:template>
	

	<!-- Ссылка на создание айтема -->
	<xsl:template match="item-to-add[not(item)]">
		<xsl:param name="ass" select="'ass_0'" />
		<li class="dragable visible {$ass}">
			<a href="javascript:mainView('{create-link}')" >
				<span class="name"><xsl:value-of select="@caption"/></span>
			</a>
		</li>
		<li class="drop-zone {$ass}"></li>
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
		<xsl:if test="admin-page/item-to-add">
			<div class="list">
				<h4>Созадать</h4>
				<ul class="create">
					<li class="drop-zone"></li>
					<li class="assoc-name">
						<a href=".ass_0" class="toggle-hidden">Разделы</a>
					</li>
					<li class="drop-zone"></li>
					<xsl:apply-templates select="admin-page/item-to-add"/>
					<li class="drop-zone"></li>
					<li class="assoc-name">
						<a href=".ass_1" class="toggle-hidden">Сопутствующие товары</a>
					</li>
					<li class="drop-zone"></li>
					<xsl:apply-templates select="admin-page/item-to-add[1]">
						<xsl:with-param name="ass" select="'ass_1'"/>
					</xsl:apply-templates>
				</ul>
			</div>
		</xsl:if>

		<!-- Буфер обмена -->
		<div id="pasteBuffer"></div>
		
		<!-- Редактирование существующих айтемов -->
		<xsl:if test="admin-page/item">

			<xsl:variable name="differentSubitems" select="count(admin-page/item-to-add) &gt; 1 or admin-page/item-to-add/item or admin-page/item/@type-id != admin-page/item/@type-id"/>
			<xsl:variable name="items" select="admin-page/item"/>
			<div class="list">
				<h4>Редактировать</h4>
				<ul class="edit">
					
					<xsl:for-each select="admin-page/item">
						<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
						<xsl:variable name="prev" select="position() - 1" />
						<xsl:variable name="spaceId">
							<xsl:choose>
								<xsl:when test="position() = 1">space0:<xsl:value-of select="@weight" /></xsl:when>
								<xsl:otherwise>space<xsl:value-of select="$items[$prev]/@weight" />:<xsl:value-of select="@weight" /></xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="itemId" select="concat('item', @id, ':', @weight)" />
		
						<li class="drop-zone" id="{$spaceId}"></li>
						<li class="dragable visible multiple call-context-menu default" data-link="{edit-link}" data-del="{delete-link}">
							<a href="{edit-link}" class="name" title="радактировать">
								<xsl:if test="$differentSubitems and @type-caption != @caption and @caption != ''">
									<span class="description">[<xsl:value-of select="@type-caption"/>]</span><br/>
								</xsl:if>
								<xsl:value-of select="$caption"/>
							</a>
							<a class="hide_item" title="скрыть">скрыть</a>
							<a onclick="insertAjaxView('{copy-link}', 'pasteBuffer'); return false;" class="copy" title="копировать">копировать</a>
							<a href="javascript:defaultView('{delete-link}','subitems',true,refreshMain)" class="delete" title="удалить">удалить</a>
						</li>
					</xsl:for-each>
					<xsl:variable name="itemsCount" select="count($items)"/>
					<xsl:variable name="spaceId" select="concat('space', $items[$itemsCount]/@weight, ':', ($itemsCount + 1) * 64)"/>
					<li class="drop-zone" id="{$spaceId}"></li>
				</ul>
				<div class="pages">
					Старница:
					<div class="links-container big">
						<a href="#">1</a>
						<a href="#">2</a>
						<a href="#">3</a>
						<a href="#">4</a>
						<a href="#">5</a>
						<a href="#">6</a>
						<a href="#">7</a>
						<a href="#" class="active">8</a>
						<a href="#">9</a>
						<a href="#">10</a>
						<a href="#">11</a>
						<a href="#">12</a>
						<a href="#">13</a>
						<a href="#">14</a>
						<a href="#">15</a>
						<a href="#">16</a>
						<a href="#">17</a>
						<a href="#">18</a>
						<a href="#">19</a>
						<a href="#">20</a>
					</div>
				</div>
			</div>
		</xsl:if>

		<!-- Скрытые  -->
		<div class="list hidden-items">

			<xsl:variable name="differentSubitems" select="count(admin-page/item-to-add) &gt; 1 or admin-page/item-to-add/item or admin-page/item/@type-id != admin-page/item/@type-id"/>
			<xsl:variable name="items" select="admin-page/item"/>

			<h4>
				<a href="#hidden-list" class="toggle-hidden">Скрытые разделы</a>
			</h4>

			<ul class="edit" id="hidden-list" style="display: none;">

				<xsl:for-each select="admin-page/item">
					<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
					<xsl:variable name="prev" select="position() - 1" />
					<xsl:variable name="spaceId">
						<xsl:choose>
							<xsl:when test="position() = 1">space0:<xsl:value-of select="@weight" /></xsl:when>
							<xsl:otherwise>space<xsl:value-of select="$items[$prev]/@weight" />:<xsl:value-of select="@weight" /></xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="itemId" select="concat('item', @id, ':', @weight)" />

					<li class="drop-zone" id="{$spaceId}"></li>
					<li class="dragable visible multiple call-context-menu default" data-link="{edit-link}" data-del="{delete-link}">
						<a href="{edit-link}" class="name" title="радактировать">
							<xsl:if test="$differentSubitems and @type-caption != @caption and @caption != ''">
								<span class="description">[<xsl:value-of select="@type-caption"/>]</span><br/>
							</xsl:if>
							<xsl:value-of select="$caption"/>
						</a>
						<a class="show_item" title="показать">показать</a>
						<a onclick="insertAjaxView('{copy-link}', 'pasteBuffer'); return false;" class="copy" title="копировать">копировать</a>
						<a href="javascript:defaultView('{delete-link}','subitems',true,refreshMain)" class="delete" title="удалить">удалить</a>
					</li>
				</xsl:for-each>
				<xsl:variable name="itemsCount" select="count($items)"/>
				<xsl:variable name="spaceId" select="concat('space', $items[$itemsCount]/@weight, ':', ($itemsCount + 1) * 64)"/>
				<li class="drop-zone" id="{$spaceId}"></li>
			</ul>

		</div>

		<script type="text/javascript">
			var reorderLink = "<xsl:value-of select="admin-page/link[@name='reorder']"/>";
		</script>

		<script type="text/javascript" src="admin/js/subitems.js"></script>

		<script type="text/javascript">
			insertAjaxView('<xsl:value-of select="admin-page/get-paste"/>', 'pasteBuffer');
		</script>

		<!-- CONTEXT MENU -->
		<script type="text/javascript" src="admin/js/context-menu.js"></script>
	</xsl:template>
		
</xsl:stylesheet>