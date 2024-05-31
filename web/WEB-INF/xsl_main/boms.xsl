<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:f="f:f" version="2.0">
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
				<th width="40%">Название</th>
				<th width="30px"></th>
				<th width="30px">строк</th>
				<th width="30%">Заметка</th>
				<th>Действие</th>
			</tr>
			<xsl:for-each select="page/bom_catalog/bom_list">
				<xsl:variable name="id" select="@id"/>
				<tr class="softgreen" id="bl_main_{@id}">
					<td><input type="checkbox"></input></td>
					<td><a href="#" style="font-size: larger" onclick="$('.hide_{$id}').toggle(); return false;">➕</a></td>
					<td onclick="showInputs(this)">
						<span><xsl:value-of select="name" /></span>
						<input name="name" type="text" value="{name}" style="width: 100%; display: none" onblur="hideInputs(this)" onfocusout="hideInputs(this)"/>
					</td>
					<td>
						<form action="{edit_name}" method="post"></form>
						<a href="#" style="font-size: larger; display: none" onclick="submitName(this); return false" class="edit_line" onmouseover="noHide = true;" onmouseleave="noHide = false;">✎</a>
					</td>
					<td><xsl:value-of select="count(line)" /></td>
					<td onclick="showInputs(this)">
						<span><xsl:value-of select="description" /></span>
						<input name="desc" type="text" value="{description}" style="width: 100%; display: none" onblur="hideInputs(this)" onfocusout="hideInputs(this)"/>
					</td>
					<td>[<a href="#">Повторить поиск</a>] [<a href="#">X</a>]</td>
				</tr>
				<tr class="hide_{$id}" style="background-color: #fffae5; display: none" id="bl_lines_{@id}">
					<td colspan="6" style="font-weight: initial;">
						<table>
							<xsl:for-each select="line">
								<tr>
									<td width="10px"><input type="checkbox"></input></td>
									<td width="20px"></td>
									<td width="40%"><xsl:value-of select="@key" /></td>
									<td width="40px"><xsl:value-of select="@value" /></td>
									<td width="30px">[<a href="#">X</a>]</td>
									<td colspan="3"></td>
								</tr>
							</xsl:for-each>
							<tr>
								<td></td>
								<td></td>
								<td><input name="query" style="width: 100%; border: #b0b0b0 solid 1px;"/></td>
								<td><input name="qty" style="width: 100%; border: #b0b0b0 solid 1px;"/></td>
								<td>[<a href="#">+</a>]</td>
								<td colspan="3"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr class="hide_{$id}" style="background-color: #fffae5; display: none">
					<td></td>
					<td colspan="5">[<a href="#">удалить выделенное</a>] [<a href="#">копировать список</a>] [<a href="#">повторить поиск</a>]</td>
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
				postFormData($(element).closest('td').find('form'), {'name' : name, 'desc' : desc}, line.attr('id'));
			}
		</script>
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