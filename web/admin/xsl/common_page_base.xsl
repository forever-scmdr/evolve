<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
	</xsl:template>

	<xsl:variable name="home_url" select="'http://eeee:8080/radio/'" />

	<xsl:template name="TITLE">
		CMS - Items
	</xsl:template><!-- ******************* TODO LOCAL ******************** -->

	<!-- **************************** Меню каталога ******************************** -->

	<xsl:template match="itemdesc[@extendable = 'true']">
		<li>
			<a href="{edit_link}">
				<xsl:value-of select="@caption" />
			</a>
			<xsl:if test="itemdesc or selected-itemdesc">
				<ul>
					<xsl:apply-templates select="itemdesc | selected-itemdesc" />
				</ul>
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="itemdesc">
		<xsl:apply-templates select="itemdesc[.//@extendable = 'true'] | selected-itemdesc" />
	</xsl:template>

	<xsl:template match="selected-itemdesc">
		<li>
			<a href="{edit_link}" class="active">
				<xsl:value-of select="@caption" />
			</a>
			<xsl:if test="itemdesc">
				<ul>
					<xsl:apply-templates select="itemdesc" />
				</ul>
			</xsl:if>
		</li>
	</xsl:template>

	<!-- **************************** СТРАНИЦА ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<head>
				<base href="{admin-page/domain}" />
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<meta http-equiv="Pragma" content="no-cache" />
				<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
				<link rel="stylesheet" href="admin/js/jquery.fancybox.min.css" type="text/css" media="screen" />
				<link rel="stylesheet" type="text/css" href="admin/css/style.css" />

				<link href="admin/jquery_css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" media="screen" />

				<title>
					Система управления сайтом
					<xsl:value-of select="/admin-page/domain" />
				</title>
				<script src="admin/js/jquery-3.2.1.min.js"></script>
				<script type="text/javascript" src="admin/js/admin.js"></script>
			</head>
			<body>

				<div class="mainwrap">
					<div class="header">
						<div class="left-col">
							<div class="logo">
								<a href="admin_initialize.action">
									<img src="admin/img/forever_logo.png" alt="forever-CMS" />
								</a>
							</div>
						</div>
						<div class="right-col">
							<a href="{/admin-page/domain}" class="gray" target="blank">
								<xsl:value-of select="/admin-page/domain" />
							</a>
							<a href="#" onclik="alert('Функция в разрабтке'); return false;">
								<img src="admin/img/visual_btn.png" alt="visual editor" />
							</a>
							<a href="admin/help.html" style="margin-left: 5px;">F1 - справка</a>
							<a href="logout.login?target=admin_initialize.action" class="logout" title="выйти">
								<xsl:value-of select="/admin-page/@username" />
							</a>
						</div>
					</div>
					<!-- ************************ Путь к текущему айтему **************************** -->
					<div class="path">
						<span class="pad"></span>
						<a href="admin_initialize.action">Корень</a>
						<b>
							<xsl:value-of select="/admin-page/path/item[position() = last()]/@caption" />
						</b>
					</div>
					<div class="mid">
						<div class="left-col">
							<div class="list">
								<h4>Редактируемые классы:</h4>
								<ul class="itemsMenu">
									<xsl:apply-templates select="//items/itemdesc[.//@extendable = 'true'] | //items/selected-itemdesc" />
								</ul>
							</div>
							<div class="list">
								<h4>Дополнительно</h4>
								<ul class="no-drag">
									<li class="visible" title="Здесь можно добавлять или удалять значения выпадающих списков">
										<a href="admin_drop_all_caches.action">Очистить все кеши</a>
									</li>
									<li class="visible" title="Здесь можно добавлять или удалять значения выпадающих списков">
										<a href="admin_reindex.action">Переиндексация</a>
									</li>
									<li class="visible" title="Здесь можно добавлять или удалять значения выпадающих списков">
										<a href="admin_domains_initialize.domain">
											Упарвление доменами
										</a>
									</li>
									<li class="visible" title="Измениение паролей, создание и удаление пользователей">
										<a href="admin_users_initialize.user">
											Упарвление пользователями
										</a>
									</li>
									<li class="visible" title="???">
										<a href="admin_types_init.type">
											Упарвление классами объектов
										</a>
									</li>
									<li class="visible" title="Функция в разработке">
										<a href="#" onclick="alert('Функция в разработке'); return false;">
											Упарвление миром
										</a>
									</li>
								</ul>
							</div>
						</div>
						<div class="right-col">
							<div class="inner">
								<xsl:call-template name="CONTENT" />
							</div>
						</div>
					</div>
				</div>
			</body>
			<script type="text/javascript" src="admin/js/jquery.fancybox.min.js"></script>
			<script type="text/javascript">
				$(document).ready(function() {
					$(".fancybox").fancybox({
					padding : 0
					});
					$(".active").parent("li").children("ul").show();
					$(".active").parents("ul").show();
				});
			</script>
			
		</html>
	</xsl:template>

	<xsl:template name="CONTENT">
		<h1 class="title" style="margin-bottom: 20px;">Выберите редактируемый класс</h1>
	</xsl:template>

</xsl:stylesheet>