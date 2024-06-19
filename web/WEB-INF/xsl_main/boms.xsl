<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:f="f:f" version="2.0">
	<xsl:import href="bom_ajax.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="products" select="page/product"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">BOM-листы</div>
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<table>
			<tr>
				<th width="10px">#</th>
				<th width="20px"></th>
				<th width="30px"></th>
				<th width="30%">Название</th>
				<th width="30px">строк</th>
				<th width="40%">Заметка</th>
				<th>Действие</th>
			</tr>
			<xsl:for-each select="page/bom_catalog/bom_list">
				<xsl:variable name="id" select="@id"/>
				<tr class="softgreen" id="bl_main_{@id}">
					<td><input type="checkbox" class="check_all" name="bom" value="{@id}"/></td>
					<td>
						<a href="#" style="font-size: larger" onclick="$('.hide_{$id}').toggle(); return false;">
							<span class="hide_{$id}">➕</span>
							<span class="hide_{$id}" style="display: none">➖</span>
						</a>
					</td>
					<td>
						<form method="post" action="page/validate_bom_link" id="bl_search_{@id}" style="display: none" class="search_repeat">
							<textarea name="q">
								<xsl:for-each select="line">
									<xsl:value-of select="@key"/><xsl:text> </xsl:text><xsl:value-of select="@value"/><xsl:text>&#xa;</xsl:text>
								</xsl:for-each>
							</textarea>
						</form>
						<form action="{edit_name}" method="post" class="name_update"></form>
						<a href="#" style="font-size: larger; display: none" onclick="submitName(this); return false" class="edit_line" onmouseover="noHide = true;" onmouseleave="noHide = false;">✎</a>
					</td>
					<td onclick="showInputs(this)">
						<span><xsl:value-of select="name" /></span>
						<input name="name" type="text" value="{name}" style="width: 100%; display: none" onblur="hideInputs(this)" onfocusout="hideInputs(this)"/>
					</td>
					<td><xsl:value-of select="count(line)" /></td>
					<td onclick="showInputs(this)">
						<span><xsl:value-of select="description" /></span>
						<input name="desc" type="text" value="{description}" style="width: 100%; display: none" onblur="hideInputs(this)" onfocusout="hideInputs(this)"/>
					</td>
					<td>
						<a href="#" class="button" style="padding: 2px 16px; font-weight: normal; margin-right: 10px;" onclick="repeatSearch('#bl_search_{@id}'); return false">Повторить поиск</a>
						<a href="#" class="confirm-dialog" onclick="confirmLink('{delete_bom}', this); return false;">❌</a>
					</td>
				</tr>
				<tr class="hide_{$id}" style="background-color: #fffae5; display: none">
					<td colspan="7" style="font-weight: initial;" id="bl_lines_{@id}">
						<table style="margin-bottom: -9px">
							<xsl:for-each select="line">
								<tr>
									<td width="10px"><input name="name" type="checkbox" value="{.}"></input></td>
									<td width="20px"></td>
									<td width="40%"><xsl:value-of select="@key" /></td>
									<td width="40px"><xsl:value-of select="@value" /></td>
									<td width="30px"><a href="#" onclick="confirmAjax('{f:var_into_url(../delete_line, 'name', .)}', this); return false;" class="confirm-dialog">🗙</a></td>
									<td colspan="3"></td>
								</tr>
							</xsl:for-each>
							<tr>
								<td></td>
								<td></td>
								<td colspan="2">
									<form action="{add_line}" id="bl_add_{@id}" method="post" ajax="true" ajax-loader-id="bl_add_{@id}">
										<input name="name" style="width: 90%; border: #b0b0b0 solid 1px;"/>
										<input name="qty" type="number" min="1" style="width: 10%; border: #b0b0b0 solid 1px;"/>
									</form>
									<form action="{delete_line}" id="bl_delete_{@id}" ajax="true" ajax-loader-id="bl_lines_{@id}"/>
								</td>
								<td><a href="#" onclick="$('#bl_add_{@id}').submit(); return false;" class="button" style="padding: 1px 16px;">+</a></td>
								<td colspan="3"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr class="hide_{$id}" style="background-color: #fffae5; display: none;">
					<td></td>
					<td></td>
					<td colspan="5">
						<a href="#" class="button confirm-dialog" style="padding: 2px 16px; font-weight: normal; margin-right: 10px;"
						   onclick="confirmFunction(function() {{$('#bl_delete_{@id}').append($('#bl_lines_{@id}').find('input:checked')); $('#bl_delete_{@id}').submit(); }}, this); return false;">удалить выделенное</a>
						<a href="#" class="button" style="padding: 2px 16px; font-weight: normal; margin-right: 10px;"
						   onclick="submitBomSave('#bl_main_{@id}', '{name}', '{description}'); return false;">копировать список</a>
						<a href="#" class="button" style="padding: 2px 16px; font-weight: normal; margin-right: 10px;" onclick="repeatSearch('#bl_search_{@id}'); return false">повторить поиск</a>
						<script></script>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<script>
			var noHide = false;
			var prevFocused = null;

			function showInputs(element) {
				if (prevFocused == element)
					return;
				$(element).closest('tr').find('span').hide();
				$(element).closest('tr').find('.edit_line').show();
				$(element).closest('tr').find('input[type=text]').show();
				var input = $(element).find('input[type=text]').first()
				input.focus();
				var val = input.val();
				input.val('');
				input.val(val);
				prevFocused = element;
			}

			function hideInputs(element) {
				if (noHide)
					return;
				$(element).closest('tr').find('input[type=text]').hide();
				$(element).closest('tr').find('.edit_line').hide();
				$(element).closest('tr').find('span').show();
				prevFocused = null;
			}

			function submitName(element) {
				var line = $(element).closest('tr');
				var name = $(line.find('input[name=name]')).val();
				var desc = $(line.find('input[name=desc]')).val();
				postFormData($(element).closest('td').find('form.name_update'), {'name' : name, 'desc' : desc}, line.attr('id'));
			}

			function deleteBoms() {
				$('#delete_boms_form').empty();
				$('.check_all:checked').clone().appendTo($('#delete_boms_form'));
				if ($('.check_all:checked').length != 0)
					$('#delete_boms_form').submit();
			}

			function findBoms() {
                var allQs = '';
				$('.check_all:checked').closest('tr').find('.search_repeat').each(function() {
                    allQs += $(this).text();
				});
                $('#find_boms_form').find('textarea').text(allQs);
				repeatSearch('#find_boms_form');
			}

			function exportBomsXls() {
				$('#xls_boms_form').empty();
				$('.check_all:checked').clone().appendTo($('#xls_boms_form'));
				if ($('.check_all:checked').length != 0)
					$('#xls_boms_form').submit();
			}

			function exportBomsPdf() {
				$('#pdf_boms_form').empty();
				$('.check_all:checked').clone().appendTo($('#pdf_boms_form'));
                if ($('.check_all:checked').length != 0)
					$('#pdf_boms_form').submit();
			}

			function exportBomsEmail() {
				$('#email_boms_form').empty();
				$('.check_all:checked').clone().appendTo($('#email_boms_form'));
				if ($('.check_all:checked').length != 0)
					$('#email_boms_form').submit();
			}
		</script>
		<div style="display: none">
			<form method="post" action="{page/delete_all_boms}" id="delete_boms_form"></form>
			<form method="post" action="{page/export_all_boms}" id="xls_boms_form" target="_blank"></form>
			<form method="post" action="{page/print_all_boms}" id="pdf_boms_form" target="_blank"></form>
			<form method="post" action="{page/email_all_boms}" id="email_boms_form"></form>
			<form method="post" action="{page/validate_bom_link}" id="find_boms_form"><textarea name="q"></textarea></form>
		</div>
		<xsl:if test="page/bom_catalog/bom_list">
			<a class="button confirm-dialog" href="#" style="margin-right: 10px;" onclick="confirmFunction(function() {{ deleteBoms() }}, this); return false;">Удалить выделенные списки</a>
			<a class="button confirm-dialog" href="#" style="margin-right: 10px;" onclick="findBoms(); return false;">Повторить поиск для выделенного</a>
			<span style="margin-right: 5px;">Экспорт выделенного: </span>
			<a class="button confirm-dialog" href="#" style="margin-right: 10px;" onclick="exportBomsXls(); return false;">Excel</a>
			<a class="button confirm-dialog" href="#" style="margin-right: 10px;" onclick="exportBomsPdf(); return false;">PDF</a>
			<a class="button confirm-dialog" href="#" style="margin-right: 10px;" onclick="exportBomsEmail(); return false;">E-mail</a>
		</xsl:if>
		<xsl:call-template name="SAVE_BOM_FORM"/>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			$(document).ready(function() {
				$('.order_toggle').click(function(event) {
					event.preventDefault();
					var order = $(this).closest('.orders__item');
					order.toggleClass('orders__item_active');
					order.find('.past-order__action').toggle(0);
					order.find('.past-order__product').toggle('fade', 200); // 'blind'
				});

				$('.submit_all_again').click(function(event) {
					event.preventDefault();
					$(this).closest('.orders__item').find('input[type=submit]').trigger('click');
				});
			});
		</script>
	</xsl:template>

</xsl:stylesheet>