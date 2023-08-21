<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="inputs.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>

	<xsl:template name="MOVE_SCRIPT">
		<script>
			function up(id) {
				var size = $("[id^='pord_']").length;
				var param = document.getElementById(id);
				var index = $("[id^='pord_']").index(param);
				if (index &gt; 0)
					param.parentNode.insertBefore(param, $("[id^='pord_']").get(index - 1));
			}
			
			function down(id) {
				var size = $("[id^='pord_']").length;
				var param = document.getElementById(id);
				var index = $("[id^='pord_']").index(param);
				if (index &lt; size - 1)
					param.parentNode.insertBefore(param, $("[id^='pord_']").get(index + 1).nextSibling);
			}
			
			function list() {
				var paramArray = $("[id^='pord_']");
				var result = "";
				for (i = 0; i &lt; paramArray.length; i++) {
					if (i > 0) result += ',';
					result += paramArray.get(i).getAttribute("name");
				}
				return result;
			}
			
			function submitItem() {
				$("#paramOrder").val(list());
				$('#item').submit();
			}
		</script>
	</xsl:template>

	<xsl:template match="itemdesc" mode="OPTION">
		<option value="{@ag-id}"><xsl:value-of select="@caption"/></option>
		<xsl:apply-templates select="itemdesc" mode="OPTION"/>
	</xsl:template>

	<xsl:template match="updating-itemdesc[@user-def = 'true']">
		<xsl:variable name="iid" select="@ag-id"/>
		<xsl:call-template name="MOVE_SCRIPT"/>
		<div class="buttonBar">
			<a href="#deleteOptions" class="button totalDelete fancybox" style="background: #DA4453;">Удалить этот класс</a>
			<a href="{create_link}" class="button totalAdd">Создать подкласс</a>
			<a href="#deleteOptions2" class="button totalDelete fancybox">Изменить базовый класс</a>
		</div>

	<div class="multiple_param edit-class add-param">
		<p class="form_title">Добавить параметр</p>
		<form id="new_param" action="{new_param_link}" method="post">
			<label style="margin-left: 12px;">
				Назавние параметра:
				<input name="paramName" type="text" class="textForm" value="{//data/paramName}" />
			</label>
			<label style="margin-left: 12px;">
				Описание параметра:
				<input name="description" type="text" class="textForm" value="{//data/paramName}" />
			</label>

			<label class="radioblock width-6 ilb">
				одиночный
				<input name="quantifier" id="single-new-param" type="radio" group="qu" checked="checked" value="single" />
			</label>
			<label class="radioblock width-6 ilb">
				множественный
				<input name="quantifier" type="radio" group="qu" value="multiple" />
			</label>
			<label style="margin-left: 12px;">
			Тип данных:
			<select name="typeName">
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'string'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Строковый'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'integer'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Целочисленный'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'long'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Целочисл. длинный'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'double'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Дробный'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'text'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Текстовый'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'date'" />
					<xsl:with-param name="check" select="//data/typeName" />
					<xsl:with-param name="caption" select="'Дата'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'picture'" />
					<xsl:with-param name="check" select="@type" />
					<xsl:with-param name="caption" select="'Изображение'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'file'" />
					<xsl:with-param name="check" select="@type" />
					<xsl:with-param name="caption" select="'Файл'" />
				</xsl:call-template>
				<xsl:call-template name="check_option">
					<xsl:with-param name="value" select="'associated'" />
					<xsl:with-param name="check" select="@type" />
					<xsl:with-param name="caption" select="'Ассоциация'" />
				</xsl:call-template>
			</select>
			</label>
			<label style="margin-left: 12px;">
				Список значений:
				<select name="domainName">
					<xsl:call-template name="check_option">
						<xsl:with-param name="value" select="''" />
						<xsl:with-param name="check" select="//data/domainName" />
						<xsl:with-param name="caption" select="' - '" />
					</xsl:call-template>
					<xsl:for-each select="//domains/domain">
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="." />
							<xsl:with-param name="check" select="//data/domainName" />
							<xsl:with-param name="caption" select="." />
						</xsl:call-template>
					</xsl:for-each>
				</select>
			</label>
			<label style="margin-left: 12px;">
				Формат:
				<input name="format" type="text" class="textForm" value="{//data/paramName}" />
			</label>
			<a href="#" onclick="$('#new_param').submit();return false;" class="button partialSave totalAdd">Создать параметр</a>
		</form>
	</div>
        
		<div class="multiple_param edit-class">
			<p class="form_title">Редактировать класс</p>
			<form id="item" action="{update_link}" method="post">
				<label style="margin-left: 12px;">
					Назавние класса:
					<input type="text" class="textForm" value="{@caption}" name="caption" />
				</label>
				<label style="margin-left: 12px;">
					Описание класса:
					<input type="text" class="textForm" value="{@description}" name="description" />
				</label>
				<input id="paramOrder" type="hidden" name="paramOrder" value="" />
				<input type="hidden" name="extends" value="{@super}" />
				<input type="hidden" name="name" value="{@name}" />
			</form>
			
			<xsl:if test="parameter[@owner-id = $iid]">
				<p class="form_title" style="background: #909090;">Редактируемые параметры</p>
				<ul class="edit-params">
					<xsl:for-each select="parameter[@owner-id = $iid]">
						<li id="pord_{@ag-id}" name="{@ag-id}">
							<a class="{@type} param" title="редактировать" onclick="$('#par_{@ag-id}').toggle(200);return false;">
								<xsl:value-of select="@caption" />
							</a>
							<span class="setPosition">
								<a href="#" class="up" onclick="up('pord_{@ag-id}');return false;"></a>
								<a href="#" class="down" onclick="down('pord_{@ag-id}');return false;"></a>
							</span>
							<a href="{delete_link}" class="delete" title="Удалить" >Удалить</a>
							
							<div id="par_{@ag-id}" class="parameterOptions" style="display:none;">
								<form id="id_{@ag-id}" action="{update_link}" method="post">
									<label >
										Название:
										<input name="caption" type="text" value="{@caption}" />
									</label>
									<label >
										Описание:
										<input name="description" type="text" value="{@description}" />
									</label>
									<div class="label">Количество зачений:<br/>
										<xsl:call-template name="check_radio">
											<xsl:with-param name="value" select="'false'" />
											<xsl:with-param name="check" select="@multiple" />
											<xsl:with-param name="name" select="'quantifier'" />
										</xsl:call-template>
										<label for="single" style="display: inline;">одиночный</label><br/>
										<xsl:call-template name="check_radio">
											<xsl:with-param name="value" select="'true'" />
											<xsl:with-param name="check" select="@multiple" />
											<xsl:with-param name="name" select="'quantifier'" />
										</xsl:call-template>
										<label for="multiple"  style="display: inline;">множественный</label>
									</div>
									
									<label>
										<span style="padding-bottom: 5px; display:block;">Тип данных:</span>
										<select name="typeName">
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'string'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Строковый'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'integer'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Целочисленный'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'long'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Целочисл. длинный'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'double'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Дробный'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'text'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Текстовый'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'date'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Дата'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'picture'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Изображение'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'file'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Файл'" />
											</xsl:call-template>
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="'associated'" />
												<xsl:with-param name="check" select="@type" />
												<xsl:with-param name="caption" select="'Ассоциация'" />
											</xsl:call-template>
										</select>
									</label>
									<label>
										<span style="padding-bottom: 5px; display:block;">Предопределенное значение:</span>
										<select name="domainName" >
											<xsl:variable name="domain" select="@domain" />
											<xsl:call-template name="check_option">
												<xsl:with-param name="value" select="''" />
												<xsl:with-param name="check" select="$domain" />
												<xsl:with-param name="caption" select="' - '" />
											</xsl:call-template>
											<xsl:for-each select="//domains/domain">
												<xsl:call-template name="check_option">
													<xsl:with-param name="value" select="." />
													<xsl:with-param name="check" select="$domain" />
													<xsl:with-param name="caption" select="." />
												</xsl:call-template>
											</xsl:for-each>
										</select>
									</label>
									<label >
										Формат:
										<input name="format" type="text" value="{@format}" />
									</label>
									<input name="paramName" type="hidden" value="{@name}" />
									<a href="#" onclick="$('#id_{@ag-id}').submit();return false;" class="button partialSave">Сохранить</a>
								</form>
							</div>
							
						</li>
					</xsl:for-each>
				</ul>
			</xsl:if>
		</div>
		
		<div class="multiple_param edit-class">
			<p class="form_title" style="background: #909090;">Базовые параметры</p>
			<ul class="edit-params">
				<xsl:for-each select="parameter[@owner-id != $iid]">
					<li class="{@type} base-param">
						<xsl:value-of select="@caption"/>
					</li>
				</xsl:for-each>
			</ul>
		</div>
		
		<div id="deleteOptions" class="deleteOptions" style="display: none;">
			<table>
				<tr>
					<td class="">
						<h3>Удалить навсегда</h3>
						<p>Все элементы этого класса будут удалены!</p>
					</td>
				</tr>
				<tr>
					<td class="delete">
						<a href="{//updating-itemdesc/delete_link}" class="button totalDelete">Удалить</a>
					</td>
				</tr>
			</table>
		</div>
		<div id="deleteOptions2" class="deleteOptions" style="display: none;">
			<table>
				<tr>
					<td>
						<form id="new_par" action="{//updating-itemdesc/delete_link}" method="post">
							<h3>Изменить базовый класс</h3>
							<select name="newId">
								<xsl:apply-templates select="//items/itemdesc[.//@extendable = 'true'] | //items/selected-itemdesc" mode="OPTION" />
							</select>
							
						</form>
					</td>
				</tr>
				<tr>
					<td>
						<a href="javascript:$('#new_par').submit()" class="button totalSave">Изменить</a>
					</td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="updating-itemdesc[@user-def = 'false']">
		<div class="buttonBar">
			<a href="{create_link}" class="button totalAdd">Создать подкласс</a>
		</div>
		<div class="multiple_param edit-class">
			<p class="form_title" style="">Сведения о классе</p>
			<p style="margin-left: 12px; margin-right: 12px; margin-bottom: 10px;">Название: <xsl:value-of select="@name"/></p>
			<xsl:if test="@description != ''">
				<p style="margin-left: 12px; margin-right: 12px; margin-bottom: 10px;">Описание: <xsl:value-of select="@description"/></p>
			</xsl:if>
			<p class="form_title" style="background: #909090;">Базовые параметры</p>
			<ul class="edit-params">
				<xsl:for-each select="parameter">
					<li class="{@type} base-param">
						<xsl:value-of select="@caption"/>
					</li>
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>


	<xsl:template name="CONTENT">
		<h1 class="title"  style="margin-bottom: 20px;">
			<xsl:value-of select="//updating-itemdesc/@caption"/>
		</h1>
		<div class="edit-arena">
			<div class="wide">
				<div class="margin">
					<xsl:apply-templates select="//updating-itemdesc"/>				
				</div>
			</div>
		</div>
	<div class="footer">
		<div class="save-links save">
			<a href="javascript:submitItem()" title="Сохранить и остаться на странице">Сохранить</a>
			<a href="javascript:submitItem(); document.location.href = 'admin_initialize.action'" title='Сохранить и перейти к структуре сайта"'>Сохранить и выйти</a>
		</div>
	</div>

	</xsl:template>

</xsl:stylesheet>