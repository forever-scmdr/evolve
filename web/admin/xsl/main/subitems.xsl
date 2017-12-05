<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>
	<xsl:variable name="admin_name" select="admin-page/@username"/>
	<xsl:variable name="users" select="admin-page/user"/>

	<!-- Выбор создаваемого айтема из списка -->
	<xsl:template match="item-to-add[item]">
		<xsl:param name="ass" />
		<li class="visible {$ass}" style="position: relative; padding-right: 27px;">
			<select id="is_{@id}">
				<xsl:apply-templates select="item" mode="select"/>
			</select>
			<xsl:variable name="link">
				<xsl:choose>
					<xsl:when test="@assocId = '0'">mainView($('#is_<xsl:value-of select="@id"/>').find(':selected').val());return false;</xsl:when>
					<xsl:otherwise>openAssoc('<xsl:value-of select="open-associated-link"/>');return false;</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<a class="create-button" onclick="{$link}"></a>
		</li>
		<li class="drop-zone {$ass}"></li>
	</xsl:template>
	

	<!-- Ссылка на создание айтема -->
	<xsl:template match="item-to-add[not(item)]">
		<xsl:param name="ass" />
		<li class="visible {$ass}">
			<xsl:variable name="link">
				<xsl:choose>
					<xsl:when test="@assocId = '0'">mainView('<xsl:value-of select="create-link"/>')</xsl:when>
					<xsl:otherwise>openAssoc('<xsl:value-of select="open-associated-link"/>')</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<a href="#" onclick="{$link}" >
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
		<xsl:if test="admin-page/assoc/item-to-add">
			<div class="list">
				<h4>Создать</h4>
				<ul class="create">
					<xsl:for-each select="admin-page/assoc">
						<li class="drop-zone"></li>
						<li class="assoc-name">
							<a href=".ass_{@id}" class="toggle-hidden"><xsl:value-of select="@caption"/></a>
						</li>
						<li class="drop-zone"></li>
						<xsl:apply-templates select="item-to-add">
							<xsl:with-param name="ass" select="concat('ass_', @id)"/>
						</xsl:apply-templates>
					</xsl:for-each>
				</ul>
			</div>
		</xsl:if>

		<!-- Буфер обмена -->
		<div id="pasteBuffer"></div>
		
		<!-- Редактирование существующих айтемов -->
		<xsl:variable name="reorder_link" select="admin-page/link[@name='reorder']"/>
		<xsl:if test="admin-page/assoc/item">

			<xsl:variable name="differentSubitems" select="count(admin-page/assoc/item-to-add) &gt; 1 or admin-page/assoc/item-to-add/item or admin-page/assoc/item/@type-id != admin-page/assoc/item/@type-id"/>
			<div class="list">
				<h4>Редактировать</h4>
				<xsl:for-each select="admin-page/assoc">
					<xsl:variable name="ass" select="concat('ass_', @id)"/>
					<xsl:variable name="ass_id" select="@id"/>
					<xsl:variable name="asc" select="if (count(item) &gt; 1 and number(item[1]/@weight) &gt; number(item[2]/@weight)) then false() else true()"/>
					<ul class="edit drag_area">
						<li class="assoc-name">
							<a href=".ass_{@id}" class="toggle-hidden"><xsl:value-of select="@caption"/></a>
						</li>

						<xsl:variable name="itemCount" select="count(item)" />

						<xsl:for-each select="item">
							<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
							<xsl:variable name="hidden" select="@status = '1'"/>
							<xsl:variable name="dropPos" select="if ($asc) then position() - 1 else $itemCount - position() + 1"/>
							<xsl:variable name="owner" select="$users[@id = current()/@user-id]"/>
							<li class="drop-zone {$ass}" href="{replace(replace($reorder_link, ':pos:', string($dropPos)), ':assoc:', $ass_id)}"></li>
							<li class="dragable visible multiple call-context-menu default {$ass}" data-link="{edit-link}" data-del="{delete-link}" id="{@id}">
								<xsl:if test="$hidden"><xsl:attribute name="style" select="'background-color: #c8c8c8'"/></xsl:if>
								<div class="drag" title="нажмите, чтобы перемещать элемент"></div>
								<a href="{edit-link}" class="name" title="редактировать">
									<xsl:if test="$differentSubitems and @type-caption != @caption and @caption != ''">
										<span class="description">[<xsl:value-of select="@type-caption"/>] </span>
									</xsl:if>
									<xsl:if test="@user-group-name != 'common'">
										<span class="description" style="color: #7777bb">[<xsl:value-of select="@user-group-name"/>] </span>
									</xsl:if>
									<xsl:if test="$owner">
										<span class="description" style="color: #bb7777">[<xsl:value-of select="$owner/@name"/>] </span>
									</xsl:if>
									<xsl:if test="($differentSubitems and @type-caption != @caption and @caption != '') or $owner"><br/></xsl:if>
									<xsl:value-of select="$caption"/>
								</a>
								<div class="controls">
									<xsl:if test="$hidden">
										<a id="shl-{@id}" href="javascript:confirmAjaxView('{status-link}', 'subitems', null, '#shl-{@id}')" class="show_item" title="показать">показать</a>
									</xsl:if>
									<xsl:if test="not($hidden)">
										<a id="shl-{@id}" href="javascript:confirmAjaxView('{status-link}', 'subitems', null, '#shl-{@id}')" class="hide_item" title="скрыть">скрыть</a>
									</xsl:if>
									<a onclick="insertAjaxView('{copy-link}', 'pasteBuffer'); return false;" class="copy" title="копировать">копировать</a>
									<a id="dl-{@id}" href="javascript:confirmAjaxView('{delete-link}', 'subitems', refreshMain, '#dl-{@id}')" class="delete" title="удалить">удалить</a>
								</div>
							</li>
						</xsl:for-each>
						<xsl:variable name="lastPos" select="if ($asc) then $itemCount else 0"/>
						<li class="drop-zone" href="{replace(replace($reorder_link, ':pos:', string($lastPos)), ':assoc:', $ass_id)}"></li>
					</ul>
				</xsl:for-each>
				<xsl:if test="admin-page/page">
					<div class="pages">
						Страница:
						<div class="links-container big">
							<xsl:variable name="current" select="admin-page/current-page"/>
							<xsl:for-each select="admin-page/page">
								<a href="#" onclick="simpleAjaxView('{@href}', 'subitems'); return false;"
								   class="{'active'[current() = $current]}"><xsl:value-of select="."/></a>
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
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