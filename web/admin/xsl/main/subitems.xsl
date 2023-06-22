<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<!-- Выбор создаваемого айтема из списка -->
	<xsl:template match="item-to-add[item]">
	<div class="exist_item">
		<table>
			<tr>
				<td class="link">
					<select id="is_{@id}">
						<xsl:apply-templates select="item" mode="select"/>
					</select>
				</td>
				<td class="action">
					<a href="#" onclick="mainView($('#is_{@id}').find(':selected').val());return false;">
						<img class="add" src="admin/admin_img/action_add.png"/>
					</a>
				</td>
			</tr>
		</table>
	</div>
	<div class="spacer"><div></div></div>
	</xsl:template>

	<!-- Ссылка на создание айтема -->
	<xsl:template match="item-to-add[not(item)]">
	<div class="exist_item">
		<table>
			<tr>
				<td class="link">
					<a href="javascript:mainView('{create-link}')"><xsl:value-of select="@caption"/></a>
				</td>
				<td class="action">
					<a href="javascript:mainView('{create-link}')"><img class="add" src="admin/admin_img/action_add.png" /></a>
				</td>
			</tr>
		</table>
	</div>
	<div class="spacer"><div></div></div>
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
		<div class="side_block">
			<div class="head">
				<span>Создать:</span>
			</div>
			<div class="items">
				<xsl:apply-templates select="admin-page/item-to-add"/>
			</div>
			<div class="bottom"></div>
		</div>
		</xsl:if>

		<!-- Буфер обмена -->
		<div id="pasteBuffer"></div>
		
		<!-- Редактирование существующих айтемов -->
		<xsl:if test="admin-page/item">
		<div class="side_block drag_area">
			<div class="head">
				<span>Редактировать:</span>
			</div>
			<script>
				var refreshMain = function() {
					refreshView('main_view');
				}
				
				function getInlineEditForm(itemId, formUrl) {
					<xsl:text disable-output-escaping="yes">
					$('#inline_view').append("&lt;div id='inline_" + itemId + "'&gt;&lt;/div&gt;");
					</xsl:text>
					$.ajax({
						url: formUrl,
						dataType: "html",
						error: function(arg1, errorType, arg3) {
							$('#message_main').html('Ошибка выполнения AJAX запроса: ' + errorType);
						},
						success: function(data, status, arg3) {
							$('#inline_' + itemId).html(data);
						}
					});
				}
			</script>
			<div class="items">
<!-- 				<xsl:variable name="differentSubitems" select="count(admin-page/item-to-add) &gt; 1 or admin-page/item-to-add/item"/> -->
				<xsl:variable name="differentSubitems" select="count(admin-page/item-to-add) &gt; 1 or admin-page/item-to-add/item or admin-page/item/@type-id != admin-page/item/@type-id"/>
				<xsl:variable name="items" select="admin-page/item"/>
				<xsl:for-each select="admin-page/item">
					<xsl:variable name="prev" select="position() - 1"/>
					<xsl:variable name="spaceId">
						<xsl:choose>
							<xsl:when test="position() = 1">space0:<xsl:value-of select="@weight"/></xsl:when>
							<xsl:otherwise>space<xsl:value-of select="$items[$prev]/@weight"/>:<xsl:value-of select="@weight"/></xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="itemId" select="concat('item', @id, ':', @weight)"/>
					<div class="spacer drop" id="{$spaceId}"><div></div></div>
					<div class="exist_item drag" id="{$itemId}">
						<xsl:if test="$differentSubitems and @type-caption != @caption and @caption != ''">
						<div class="item_type">
							[<xsl:value-of select="@type-caption"/>]
						</div>
						</xsl:if>
						<table>
							<tr>
								<td class="link">
								<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
								<xsl:if test="@ref-id = @id">
									<a href="{edit-link}"><xsl:value-of select="$caption"/></a>
								</xsl:if>
								<xsl:if test="@ref-id != @id">
									<span style="color:grey">&lt;ссылка&gt;</span><xsl:value-of select="$caption"/>
								</xsl:if>
								</td>
								<td class="action">
									<a href="javascript:defaultView('{delete-link}','subitems',true,refreshMain)">
										<img src="admin/admin_img/action_delete.png" />
									</a>
								</td>
							</tr>
						</table>
						<div class="copyWhat">
							<div class="copySpacer"></div>
							<div class="copyButton">
								<a href="#" onclick="insertAjaxView('{copy-link}', 'pasteBuffer'); return false;">копировать</a>
							</div>
						</div>
					</div>
					<!-- Скрипт для загрузки параметров сабайтема, если он inline -->
					<xsl:if test="@type-inline = 'true'">
					<script>
					getInlineEditForm(<xsl:value-of select="@id"/>, "<xsl:value-of select="edit-inline-link"/>");
					</script>
					</xsl:if>
				</xsl:for-each>
				<xsl:variable name="itemsCount" select="count($items)"/>
				<xsl:variable name="spaceId" select="concat('space', $items[$itemsCount]/@weight, ':', ($itemsCount + 1) * 64)"/>
				<div class="spacer drop" id="{$spaceId}"><div></div></div>
			</div>
			<div class="bottom">
			
			</div>
		</div>
		</xsl:if>
		<script>
			var reorderLink = "<xsl:value-of select="admin-page/link[@name='reorder']"/>";
			$(".drag").draggable({
				axis: "y",
				containment: ".drag_area",
				cursor: "n-resize",
				revert: true,
				revertDuration: 200,
				zIndex: 100
			});
			$(".drop").droppable({
				accept: ".drag",
				hoverClass: "spacer_selected",
				drop: function(event, ui) {
					var itemNums = ui.draggable.attr('id').substring(4).split(':');
					var posNums = $(this).attr('id').substring(5).split(':');
					var itemId = itemNums[0]; // number conversion http://www.jibbering.com/faq/faq_notes/type_convert.html
					var itemWeight = itemNums[1];
					var weightBefore = posNums[0];
					var weightAfter = posNums[1]; 
					if (weightBefore != itemWeight &amp;&amp; weightAfter != itemWeight) {
						reorderLink = reorderLink.replace(":id:", itemId);
						reorderLink = reorderLink.replace(":wb:", weightBefore);
						reorderLink = reorderLink.replace(":wa:", weightAfter);
						defaultView(reorderLink, 'subitems');
						//alert(reorderLink);
					}
				}
			});
			insertAjaxView('<xsl:value-of select="admin-page/get-paste"/>', 'pasteBuffer');
		</script>
	</xsl:template>
		
</xsl:stylesheet>