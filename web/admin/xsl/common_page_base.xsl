<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<xsl:variable name="home_url" select="'http://eeee:8080/radio/'"/>

	<xsl:template name="TITLE">CMS - Items</xsl:template><!-- ******************* TODO LOCAL ******************** -->

	<!-- ****************************    Меню каталога    ******************************** -->

	<xsl:template match="itemdesc[@extendable = 'true']">
		<li>
			<a href="" class="closedArrow"></a><a href="{edit_link}"><xsl:value-of select="@caption"/></a>
			<xsl:if test="itemdesc or selected-itemdesc">
				<ul>
					<xsl:apply-templates select="itemdesc | selected-itemdesc"/>
				</ul>
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="itemdesc">
		<xsl:apply-templates select="itemdesc[.//@extendable = 'true'] | selected-itemdesc"/>
	</xsl:template>

	<xsl:template match="selected-itemdesc">
		<li>
			<a href="" class="closedArrow"></a><a href="{edit_link}" class="active"><xsl:value-of select="@caption"/></a>
			<xsl:if test="itemdesc">
				<ul>
					<xsl:apply-templates select="itemdesc"/>
				</ul>
			</xsl:if>
		</li>
	</xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html>
			<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<meta http-equiv="Pragma" content="no-cache"/>
			<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css"/>
			<xsl:text disable-output-escaping="yes">
				&lt;!--[if IE 7]&gt;
				&lt;link href="css/ie.css" rel="stylesheet" type="text/css" /&gt;
				&lt;![endif]--&gt;
			</xsl:text>
			<title><xsl:call-template name="TITLE"/></title><!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO LOCAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
			<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
			<link rel="stylesheet" href="admin/fancybox/jquery.fancybox.css" type="text/css" media="screen" />
			<script type="text/javascript" src="admin/fancybox/jquery.fancybox.pack.js"></script>
			<script type="text/javascript">
				$(document).ready(function() {
					$(".fancybox").fancybox({
						padding : 0
					});
				});
				</script>
			</head>
			<body>
			<script language="javascript" type="text/javascript" src="admin/js/admin.js"></script>
			<script language="javascript" type="text/javascript" src="admin/tiny_mce/tiny_mce.js"></script>
			<!-- ************************ Основная форма **************************** -->
			<!-- ******************************************************************** -->
			<div class="mainwrap">
				<div class="header">
					<div class="logo">
						<a href=""><img src="admin/admin_img/logo.png" alt=""/></a>
					</div>
					<div class="domain">
						<div class="domain_right">
							 <xsl:value-of select="//domain"/>
						</div>
					</div>
					<a href="admin/help.html"><img class="help" src="admin/admin_img/help.png" alt=""/></a>
					<table class="user">
					<tr>
						<td>
							admin
						</td>
						<td class="logout">
							<a href="logout.login?target=admin_initialize.action"><img src="admin/admin_img/logout.png" alt=""/></a>
						</td>
					</tr>
					</table>
				</div>
				<!-- ************************ Путь к текущему айтему **************************** -->
				<div class="path">
					<a href="admin_initialize.action">Корень</a>
				</div>
				<!-- **************************************************************************** -->
				<table class="main_table">
				<tr>
					<td class="side_col">
						<div class="side_block">
							<div class="head">
								<span>Типы объектов каталога:</span>
							</div>
							<div class="items">
								<!-- ************************ Уже существующие сабайтемы текущего **************************** -->
								<ul class="itemsMenu">
									<xsl:apply-templates select="//items/itemdesc[.//@extendable = 'true'] | //items/selected-itemdesc"/>
								</ul>
								<!-- ***************************************************************************************** -->
							</div>
							<div class="bottom">
							</div>
						</div>
						<!-- functions -->
						<div class="side_block">
							<div class="head">
								<span>Дополнительно</span>
							</div>
							<div class="items">
								<!-- item -->
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_initialize.action">Управление контентом</a>
										</td>
									</tr>
									</table>
								</div>
								<!-- /item -->
								<div class="spacer">
									<div>
									</div>
								</div>
								<!-- item -->
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_users_initialize.user">Управление пользователями</a>
										</td>
									</tr>
									</table>
								</div>
								<!-- /item -->
								<div class="spacer">
									<div>
									</div>
								</div>
								<!-- item -->
								<div class="exist_item">
									<table>
									<tr>
										<td class="link">
											<a href="admin_domains_initialize.domain">Управление доменами</a>
										</td>
									</tr>
									</table>
								</div>
								<!-- /item -->
							</div>
							<div class="bottom">
							</div>
						</div>
						<!-- /functions -->
					</td>
					<td class="main">
						<div class="warning">
							<div class="tl">
								<div class="bl">
									<div class="message">
										<span><xsl:value-of select="//message"/></span>
									</div>
								</div>
							</div>
						</div>
						<xsl:call-template name="CONTENT"/><!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO LOCAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
					</td>
				</tr>
				</table>
			</div>
			<div class="save">
				<a href="javascript:document.mainForm.submit()"><img src="admin/admin_img/save.png" alt=""/></a>
			</div>
			</body>
		</html>
	</xsl:template>
		
</xsl:stylesheet>