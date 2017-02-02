<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="_inc_params.xsl"/>
	<xsl:import href="_inc_base_head.xsl"/>
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
		<xsl:if test="$form/@id &gt; 0">
			<a href="#" onclick="openAssoc({@id});return false;">Редактировать ассоциации</a>
			<xsl:call-template name="BR"/>
		</xsl:if>
	</xsl:template>

	<!-- Множественное простое поле ввода -->
	<xsl:template match="field" mode="multiple">
		<xsl:choose>
			<xsl:when test="@domain">
				<select style="width: 300px; float: left;" onchange="this.nextElementSibling.value = this.value">
					<option/>
					<xsl:for-each select="//domain[@name=current()/@domain]/value">
						<xsl:sort select="."/>
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<input 
					style="width: 280px; margin-left: -299px; margin-top: 1px; border: none; float: left;" 
					class="field" type="text" name="multipleParamValue"/>
			</xsl:when>
			<xsl:otherwise>
				<input class="field" type="text" name="multipleParamValue" style="width: 280px;"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!--********************************************************************************** 
						     МНОЖЕСТВЕННЫЕ ПАРАМЕТРЫ (ЗНАЧЕНИЯ)
	***********************************************************************************-->


	<!-- Все значения одного множественного параметра -->
	<xsl:template match="field" mode="values">
		<div class="picbay_header"><span><xsl:value-of select="@caption"/></span></div>
		<xsl:for-each select="value">
		<div class="picbay_item">
			<table>
				<tr>
					<td class="file_path">
						<xsl:apply-templates select="."/>
					</td>
					<td class="file_delete">
						<a href="javascript:defaultView('admin_delete_parameter.action?multipleParamId={../@id}&amp;index={@index}&amp;itemId={$form/@id}', 'main_view', true)">
							<img src="admin/admin_img/picbay_delete.png" alt="" />
						</a>
					</td>
				</tr>
			</table>
		</div>
		</xsl:for-each>
	</xsl:template>

	<!-- Значение множественной картинки -->
	<xsl:template match="value[../@type='picture']">
		<a class="fancybox" href="{$form/@file-path}{.}">
			<img style="width: 100px" src="{$form/@file-path}{.}" alt="" />
		</a>
		<input class="special" name="" value="{$form/@file-path}{.}" type="text" onclick="$(this).select()"/>
	</xsl:template>
	
	<!-- Значение множественного файла -->
	<xsl:template match="value[../@type='file']">
		<a href="{$form/@file-path}{.}">Скачать файл</a>
		<input class="special" name="" value="{$form/@file-path}{.}" type="text" onclick="$(this).select()"/>
	</xsl:template>

	<!-- Значение ассоциации -->
	<xsl:template match="value[../@type='associated']">
		<xsl:value-of select="//admin-page/mount/item[@id = current()]/@caption"></xsl:value-of>
	</xsl:template>

	<!-- Значение множественной строки -->
	<xsl:template match="value">
		<xsl:value-of select="."/>
	</xsl:template>



	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->


	<xsl:template match="/">
	<xsl:choose>
		<xsl:when test="admin-page/visual = 'true'">
		<xsl:call-template name="DOCTYPE"/>
		<html>
			<xsl:call-template name="HEAD"/>
			<body>
				<div class="mainwrap">
					<xsl:call-template name="CONTENT"/>
				</div>
			</body>
		</html>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="CONTENT"/>
		</xsl:otherwise>
	</xsl:choose>
	</xsl:template>


	<xsl:template name="CONTENT">
	<xsl:call-template name="MESSAGE"/>
	<!-- Заменить Заголовок страницы, в случае если создается новый айтем (т.к. базовая страница в этом случае будет содержать
	название родительского айтема) -->
	<xsl:if test="$form/@id = 0">
	<script>$('h1.title').html('Создать: <xsl:value-of select="$form/@caption"/>')</script>
	</xsl:if>
	<table class="type_1">
	<tr>
		<xsl:if test="$form">
			<td class="left">
				<div class="forms">
					<!-- ************************ Основные (одиночные) параметры айтема **************************** -->
					<form name="mainForm" id="mainForm" action="{$form/@action-url}" enctype="multipart/form-data" method="post">
<!-- 						<xsl:for-each select="$form/hidden/field"> -->
<!-- 							<input type="hidden" name="{@input}" value="{@value}"/> -->
<!-- 						</xsl:for-each> -->
						<xsl:if test="$form/field[@quantifier='single']">
						<h2 class="title">Основные параметры</h2>
						</xsl:if>
						<xsl:for-each select="$form/field[@quantifier='single']">
							<div class="form_item">
								<p class="form_title"><xsl:value-of select="@caption"/></p>
								<xsl:if test="@description != ''">
									<p class="form_comment">[<xsl:value-of select="@description"/>]</p>
								</xsl:if>
								<xsl:apply-templates select="." mode="single"/>
							</div>
						</xsl:for-each>
						<!-- Уникальный строковый ключ айтема (в случае, если он нужен) -->
						<xsl:variable name="ukey" select="$form/extra[@input='ukey']"/>
						<xsl:if test="$ukey">
							<p class="form_title">Уникальный строковый идентификатор</p>
							<p class="form_comment">[для использования в качестве части URL]</p>
							<input class="field" type="text" name="{$ukey/@input}" value="{$ukey}"/>
						</xsl:if>
						<div class="save">
							<input type="image" src="admin/admin_img/save.png"></input>
						</div>
					</form>
					<!-- ************************ Множественные параметры айтема (добавление) **************************** -->
					<xsl:if test="$form/field[@quantifier='multiple'] and $form/@id != 0">
						<div class="horizontal_separator"></div>
						<h2 class="title">Дополнительные параметры</h2>
						<xsl:for-each select="$form/field[@quantifier='multiple']">
							<div class="form_item">
								<p class="form_title"><xsl:value-of select="@caption"/></p>
								<xsl:if test="@description != ''">
									<p class="form_comment">[<xsl:value-of select="@description"/>]</p>
								</xsl:if>
								<form id="addParameter{@id}" action="admin_add_parameter.action" enctype="multipart/form-data" method="post">
									<input type="hidden" name="multipleParamId" value="{@id}"/>
									<input type="hidden" name="itemId" value="{$form/@id}"/>
									<xsl:apply-templates select="." mode="multiple"/>
									<xsl:if test="@type != 'associated'">
										<input class="button" type="submit" value="добавить" />
									</xsl:if>
								</form>
							</div>
						</xsl:for-each>
					</xsl:if>
				</div>
			</td>
			<!-- ************************ Множественные параметры айтема (значения и удаление) **************************** -->
			<xsl:if test="$form/field[@quantifier='multiple']/value">
			<td class="right">
				<div class="picbay">
					<div class="picbay_head">
						Картинки и файлы 
					</div>
					<div class="picbay_main">
						<xsl:apply-templates select="$form/field[@quantifier='multiple']" mode="values"/>
					</div>
					<div class="picbay_bottom">
					
					</div>
				</div>
			</td>
			</xsl:if>
		</xsl:if>
	</tr>
	</table>
	<xsl:call-template name="TINY_MCE"/>
	<script>
	<xsl:for-each select="$form/field[@quantifier='multiple']">
	mainForm('addParameter<xsl:value-of select="@id"/>');
	</xsl:for-each>	
	</script>
	</xsl:template>
		
</xsl:stylesheet>