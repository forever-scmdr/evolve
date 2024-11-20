<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	version="1.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>



	<xsl:template name="check_option">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="caption"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<option value="{$value}" selected="selected"><xsl:value-of select="$caption"/></option>
			</xsl:when>
			<xsl:otherwise>
				<option value="{$value}"><xsl:value-of select="$caption"/></option>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="check_value_options">
		<xsl:param name="values"/>
		<xsl:param name="check"/>
		<xsl:param name="captions"/>
		<xsl:for-each select="$values">
			<xsl:variable name="i" select="position()"/>
			<xsl:choose>
				<xsl:when test=". = $check">
					<option value="{.}" selected="selected"><xsl:value-of select="$captions[$i]"/></option>
				</xsl:when>
				<xsl:otherwise>
					<option value="{.}"><xsl:value-of select="$captions[$i]"/></option>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="check_caption_options">
		<xsl:param name="values"/>
		<xsl:param name="check"/>
		<xsl:param name="captions"/>
		<xsl:for-each select="$captions">
			<xsl:variable name="i" select="position()"/>
			<xsl:choose>
				<xsl:when test=". = $check">
					<option value="{$values[$i]}" selected="selected"><xsl:value-of select="."/></option>
				</xsl:when>
				<xsl:otherwise>
					<option value="{$values[$i]}"><xsl:value-of select="."/></option>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="simple_check_option">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<option value="{$value}" selected="selected"><xsl:value-of select="$value"/></option>
			</xsl:when>
			<xsl:otherwise>
				<option value="{$value}"><xsl:value-of select="$value"/></option>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="simple_check_options">
		<xsl:param name="values"/>
		<xsl:param name="check"/>
		<xsl:for-each select="$values">
			<xsl:choose>
				<xsl:when test=". = $check">
					<option value="{.}" selected="selected"><xsl:value-of select="."/></option>
				</xsl:when>
				<xsl:otherwise>
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="check_radio">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="radio" group="{$name}" checked="checked" value="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="radio" group="{$name}" value="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="check_checkbox">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="checkbox" checked="checked" value="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="checkbox" value="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="item_check_checkbox">
		<xsl:param name="value"/>
		<xsl:param name="param"/>
		<xsl:choose>
			<xsl:when test="$value = $param">
				<input name="{$param/@input}" type="checkbox" checked="checked" value="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$param/@input}" type="checkbox" value="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="simple_check_checkboxes">
		<xsl:param name="values"/>
		<xsl:param name="checks"/>
		<xsl:param name="name"/>
		<xsl:for-each select="$checks">
			<xsl:choose>
				<xsl:when test="$values = .">
					<input name="{$name}" type="checkbox" checked="checked" value="{.}" />&nbsp;<xsl:value-of select="."/>&nbsp;
				</xsl:when>
				<xsl:otherwise>
					<input name="{$name}" type="checkbox" value="{.}" />&nbsp;<xsl:value-of select="."/>&nbsp;
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- Вставка переменной в ссылку (добавление как query string). match соответствует ссылке -->
	<xsl:template match="*" mode="querystr_var">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:if test="contains(., '?')">
			<xsl:value-of select="."/>&amp;<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
		</xsl:if>
		<xsl:if test="not(contains(., '?'))">
			<xsl:value-of select="."/>?<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
		</xsl:if>
	</xsl:template>

	<!-- TINY_MCE -->
	<xsl:template name="MCE_UPLOAD">
	<script type="text/javascript" src="admin/tinymce/tinymce.min.js"></script>
	<script type="text/javascript">
	var startUploadUrl = "<xsl:value-of select="page/image_upload_link"/>";
	<xsl:text disable-output-escaping="yes">
	tinymce.init({
	    selector: "textarea.mce_big",
	    language : 'ru',
	    theme: "modern",
//	    content_css: "css/main.css", - стили этого файла потом появляются в выпадающем меню Формат
	    autoresize_min_height: 300,
	    autoresize_max_height: 700,
	    plugins: [
//	    	 "importcss", - плагин, который отвечает за вывод списка стилей файла content_css в выпадающем меню
	    	 "autoresize",
	    	 "imageUpload", 
	         "advlist autolink link image lists charmap preview hr anchor pagebreak",
	         "searchreplace visualblocks visualchars fullscreen insertdatetime media nonbreaking",
	         "table contextmenu directionality paste textcolor"
	   ],
	   statusbar: false,
	   menubar: false,
	   toolbar: "insertfile undo redo | bold italic | link upload image"
	 });
	tinymce.init({
	    selector: "textarea.mce_medium",
	    language : 'ru',
	    plugins: ["paste autolink link"],
	    menubar : false,
	    statusbar: false,
	    height: 200,
	    theme: "modern",
	    toolbar: "insertfile undo redo | bold italic | link"
	 });
	tinymce.init({
	    selector: "textarea.mce_small",
	    language : 'ru',
	    plugins: ["paste autolink link"],
	    menubar : false,
	    statusbar: false,
	    height: 100,
	    theme: "modern",
	    toolbar: "insertfile undo redo | bold italic | link"
	 });
	</xsl:text>
	</script>
	</xsl:template>


	<xsl:template name="MCE_NO_UPLOAD">
	<script type="text/javascript" src="admin/tinymce/tinymce.min.js"></script>
	<script type="text/javascript">
	var startUploadUrl = "<xsl:value-of select="page/image_upload_link"/>";
	<xsl:text disable-output-escaping="yes">
	tinymce.init({
	    selector: "textarea.mce_big",
	    language : 'ru',
	    theme: "modern",
//	    content_css: "css/main.css", - стили этого файла потом появляются в выпадающем меню Формат
	    autoresize_min_height: 300,
	    autoresize_max_height: 700,
	    plugins: [
//	    	 "importcss", - плагин, который отвечает за вывод списка стилей файла content_css в выпадающем меню
	    	 "autoresize",
	    	 "imageUpload", 
	         "advlist autolink link image lists charmap preview hr anchor pagebreak",
	         "searchreplace visualblocks visualchars fullscreen insertdatetime media nonbreaking",
	         "table contextmenu directionality paste textcolor"
	   ],
	   statusbar: false,
	   menubar: false,
	   toolbar: "insertfile undo redo | bold italic | link image"
	 });
	tinymce.init({
	    selector: "textarea.mce_medium",
	    language : 'ru',
	    plugins: ["paste autolink link"],
	    menubar : false,
	    statusbar: false,
	    height: 200,
	    theme: "modern",
	    toolbar: "insertfile undo redo | bold italic"
	 });
	tinymce.init({
	    selector: "textarea.mce_small",
	    language : 'ru',
	    plugins: ["paste autolink link"],
	    menubar : false,
	    statusbar: false,
	    height: 100,
	    theme: "modern",
	    toolbar: "insertfile undo redo | bold italic"
	 });
	</xsl:text>
	</script>
	</xsl:template>

	<!-- Регионы -->

	<xsl:template match="region" mode="regions">
		<td>
			<a href="#" onclick="chooseRegion('{name}'); return false;"><b><xsl:value-of select="name"/></b></a>
			<ul>
				<xsl:apply-templates select="city" mode="regions"/>
			</ul>
		</td>
	</xsl:template>
	
<!-- 	<xsl:template match="city[name = $region]" mode="regions"> -->
<!-- 		<li><a href="#" class="regLink active"><xsl:value-of select="name"/></a></li> -->
<!-- 	</xsl:template> -->
	
	<xsl:template match="city" mode="regions">
		<li><a href="#" class="regLink" onclick="chooseRegion('{name}'); return false;"><xsl:value-of select="name"/></a></li>
	</xsl:template>
	
	<xsl:template name="REGIONS">
		<script>
			function chooseRegion(region) {
				setRegion(region);
				$('#regions').fadeOut(150);
			}
		</script>
		<div class="popup" style="display:none;margin-left: 0px; left: auto; width: auto" id="regions">
			<div class="inner" style="padding: 15px;">
				<a href="#" class="close">×</a>
				<h1>Выберите регион</h1>
				<p><a href="#" onclick="chooseRegion('Вся Беларусь'); return false;">Вся Беларусь</a></p>
				<table style="border-spacing: 7px 7px; border-collapse: separate">
					<tr>
						<xsl:apply-templates select="/page/region" mode="regions"/>
					</tr>
				</table>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>