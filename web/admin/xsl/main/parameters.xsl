<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="_inc_params.xsl"/>
	<xsl:import href="_documentation.xsl"/>

	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>


	<!--********************************************************************************** 
						     МНОЖЕСТВЕННЫЕ ПАРАМЕТРЫ (ПОЛЯ ВВОДА)
	***********************************************************************************-->


	<!-- Множественный файл или картинка -->
	<xsl:template match="field[ @type='file' or @type='picture' ]" mode="multiple">
		<input type="file" name="multipleParamValue" multiple="multiple"/>
	</xsl:template>

	<!--  Редактирование ассоциаций -->
	<xsl:template match="field[ @type='associated' ]" mode="multiple">
		<xsl:variable name="form" select=".."/>
		<xsl:if test="$form/@id &gt; 0">
			<input class="button" type="submit" onclick="openAssoc({@id});return false;" value="Редактировать ассоциации" />
		</xsl:if>
	</xsl:template>

	<!-- Множественное простое поле ввода -->
	<xsl:template match="field" mode="multiple">
		<xsl:if test="@domain">
			<select style="width: 270px; position: absolute; margin-left: 12px;padding-bottom:4px" onchange="this.nextElementSibling.value = this.value">
				<option/>
				<xsl:for-each select="//domain[@name=current()/@domain]/value">
					<xsl:sort select="."/>
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</xsl:if>
		<input 
			style="width: 245px; margin-top: 1px; border: 1px solid #ccc; z-index: 10; position: relative;" 
			class="field" type="text" name="multipleParamValue"/>
	</xsl:template>


	<!-- Множественное простое поле ввода -->
	<xsl:template match="field[@type='tuple']" mode="multiple">
		<input
				style="width: 245px; margin-top: 1px; border: 1px solid #ccc; z-index: 10; position: relative;"
				class="field tuple tuple_key" type="text"/>
		<input
				style="width: 245px; margin-top: 1px; border: 1px solid #ccc; z-index: 10; position: relative;"
				class="field tuple tuple_value" type="text"/>
		<input type="hidden" class="tuple_format" value="{if (@format and not(@format = '')) then @format else '$+$'}"/>
		<input type="hidden" class="tuple_full" name="multipleParamValue"/>
	</xsl:template>


	<!--********************************************************************************** 
						     МНОЖЕСТВЕННЫЕ ПАРАМЕТРЫ (ЗНАЧЕНИЯ)
	***********************************************************************************-->


	<!-- Все значения одного множественного параметра -->
	<xsl:template match="field" mode="values">
		<xsl:if test="value">
			<div class="list pics">
				<h4><xsl:value-of select="@caption"/></h4>
				<xsl:for-each select="value">
					<xsl:apply-templates select="."/>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Значение множественной картинки -->
	<xsl:template match="value[../@type='picture']">
		<xsl:variable name="form" select="../.."/>
		<div class="pic" id="param-{../@id}-{@index}">
			<img src="{$form/@file-path}{.}" alt="{.}"/>
			<a href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={../@id}&amp;index={@index}&amp;itemId={$form/@id}', 'main_view', null, '#param-{../@id}-{@index}')" class="delete">Удалить</a>
			<input type="text" name="" value="{$form/@file-path}{.}" title="{.}" onclick="$(this).select()" />
		</div>
	</xsl:template>
	
	<!-- Значение множественного файла -->
	<xsl:template match="value[../@type='file']">
		<xsl:variable name="form" select="../.."/>
		<div class="pic file" id="param-{../@id}-{@index}">
			<a href="{$form/@file-path}{.}" target="blank" >Открыть файл</a>
			<a href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={../@id}&amp;index={@index}&amp;itemId={$form/@id}', 'main_view', null, '#param-{../@id}-{@index}')" class="delete">Удалить</a>
			<input class="special" name="" value="{$form/@file-path}{.}" type="text" onclick="$(this).select()"/>
		</div>
		
	</xsl:template>

	<!-- Значение ассоциации -->
	<xsl:template match="value[../@type='associated']">
		<xsl:variable name="current" select="//admin-page/mount/item[@id = current()]" />
		<xsl:variable name="form" select="../.."/>
		<div class="pic assoc" id="param-{../@id}-{@index}">
			<a href="admin_set_item.action?itemId={$current/@id}&amp;itemType={$current/@type-id}" target="blank" title="Редактировать элемент">
				<xsl:value-of select="$current/@caption"/>
			</a>
			<a href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={../@id}&amp;index={@index}&amp;itemId={$form/@id}', 'main_view', null, '#param-{../@id}-{@index}')" class="delete">Удалить</a>
		</div>
	</xsl:template>

	<!-- Значение множественной строки -->
	<xsl:template match="value">
		<xsl:variable name="form" select="../.."/>
		<div class="pic" id="param-{../@id}-{@index}">
			<span><xsl:if test="@key"><xsl:value-of select="@key"/>: </xsl:if><xsl:value-of select="."/></span>
			<a href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={../@id}&amp;index={@index}&amp;itemId={$form/@id}', 'main_view', null, '#param-{../@id}-{@index}')" class="delete">Удалить</a>
		</div>
	</xsl:template>



	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->


	<xsl:template match="/">
	<xsl:variable name="form" select="admin-page/form"/>
	<xsl:if test="not(admin-page/form)">
		<xsl:call-template name="DOCUMENTATION"/>	
	</xsl:if>
	<xsl:call-template name="MESSAGE"/>

		<xsl:if test="$form/@id = 0">
			<script>
				$('h1.title').html('Создать:<xsl:value-of select="$form/@caption" />');
			</script>
		</xsl:if>
		<div class="wide">
			<div class="margin">
				<form name="mainForm" id="mainForm" action="{$form/@action-url}" enctype="multipart/form-data" method="post">

					<xsl:if test="$form/field[@quantifier='single']">
						<div class="single-params">
							<!--<h2>Одиночные параметры</h2>-->
							<br/>
							<xsl:for-each select="$form/field[@quantifier='single']">
								<div class="form-item {@type}">
									<xsl:apply-templates select="." mode="single" />
								</div>
							</xsl:for-each>
						</div>
					</xsl:if>
					<!-- Уникальный строковый ключ айтема (в случае, если он нужен) -->
					<xsl:variable name="ukey" select="$form/extra[@name='ukey']" />
					<xsl:if test="$ukey">
						<div style="padding: 20px 0;">
							<p class="form_title">Уникальный строковый идентификатор</p>
							<p class="form_comment">[для использования в качестве части URL]</p>
							<input class="field" type="text" name="{$ukey/@input}" value="{$ukey}" />
						</div>
					</xsl:if>
					<input type="hidden" name="goToParent" id="parent-url"/>
					<xsl:if test="admin-page/inline-form">
						<hr style="display:block; border: 2px solid black;"/>
						<xsl:for-each select="admin-page/inline-form">
							<h2><xsl:value-of select="@key"/></h2>
							<xsl:for-each select="field[@quantifier='single']">
								<div class="form-item {@type}">
									<xsl:apply-templates select="." mode="single" />
								</div>
							</xsl:for-each>
							<xsl:if test="position() != last()">
								<hr style="display:block; border: 1px solid #cccccc;"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>

					<xsl:if test="$form">
						<div class="footer">
							<div class="save-links save">
								<a id="save" onclick="document.mainForm.submit();" title="Сохранить и остаться на странице">Сохранить</a>
								<a id="save-and-exit" onclick="$('#parent-url').val('true'); document.mainForm.submit();"
								   title='Сохранить и перейти родительский раздел"'>Сохранить и выйти</a>
							</div>
						</div>
					</xsl:if>

					<!-- ************************ Псевдопараметр, в случае если у айтема нет параметров **************************** -->
					<xsl:for-each select="$form/extra[@name='pseudo']">
						<input type="hidden" name="{@input}" value="blank"/>
					</xsl:for-each>
				</form>
				
				<!-- ************************ Множественные параметры айтема (добавление) **************************** -->
				<xsl:if test="$form/field[@quantifier='multiple'] and $form/@id != 0">
					<div class="horizontal_separator"></div>
					<h2 class="title">Дополнительные параметры</h2>
					<xsl:for-each select="$form/field[@quantifier='multiple']">
						<div class="multiple_param">
							<p class="form_title">
								<xsl:value-of select="@caption" />
							</p>
							<xsl:if test="@description != ''">
								<p class="form_comment">[<xsl:value-of select="@description" />]
								</p>
							</xsl:if>
							<form id="addParameter{@id}" action="admin_add_parameter.action" enctype="multipart/form-data" method="post">
								<input type="hidden" name="multipleParamId" value="{@id}" />
								<input type="hidden" name="itemId" value="{$form/@id}" />
								<xsl:apply-templates select="." mode="multiple" />
								<xsl:if test="@type != 'associated'">
									<input class="button" type="submit" value="добавить" />
								</xsl:if>
							</form>
						</div>
					</xsl:for-each>
				</xsl:if>
			</div>
		</div>
		<div class="narrow pull-up">
			<xsl:apply-templates select="$form/field[@quantifier='multiple']" mode="values"/>
		</div>
	<xsl:call-template name="TINY_MCE"/>
	<script>
		var form = null;
	<xsl:for-each select="$form/field[@quantifier='multiple']">
		form = $('#addParameter<xsl:value-of select="@id"/>');
		form.find('.tuple').change(function() {
			var frm = $(this).closest('form');
			var val = frm.find('.tuple_key').val() + frm.find('.tuple_format').val() + frm.find('.tuple_value').val();
			frm.find('.tuple_full').val(val);
		});
		prepareSimpleFormView('addParameter<xsl:value-of select="@id"/>');
	</xsl:for-each>
	</script>
	</xsl:template>
		
</xsl:stylesheet>