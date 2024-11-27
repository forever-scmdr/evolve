<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="m" select="page/main"/>
	<xsl:variable name="seo" select="$m/seo"/>
	<xsl:variable name="title" select="if($seo/title != '') then $seo/title else 'производство запорной и запорно-регулирующей арматуры. Электромагнитные газовые клапаны, заслонки регулирующие, фильтры газовые, блоки электромагнитных клапанов, приборы автоматики безопасности (датчики-реле давления ДРД)'"/>
	<xsl:variable name="critical_item" select="page"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template name="CONTENT">

	<xsl:if test="$seo/progon != ''">
		<style type="text/css">
			.spacer{
				height: 3rem;
			}
			.footer{
				position: static;
			}
			.main-content.news{
				position: static;
				margin-top: 2rem;
				margin-bottom: 2rem;
			}
			.main-content.seo-text{
				position: static;
				margin-bottom: 2rem;
			}
		</style>
	</xsl:if>
		
	<div class="cf7 vl" style="width: 100%;">
		<xsl:for-each select="page/slide">
			<xsl:variable name="first" select="position() = 1"/>
			<div class="slide-image{' opaque'[$first]}" style="background: url({@path}{pic}) 50% 50%;">
				<div class="slide-mask"></div>
			</div>
		</xsl:for-each>
	
	
		<div class="spacer"></div>
	
		<div class="container slider vl-bl-1">
			<div class="row">
				<div class="col-sm-5 visible-xs-block" id="mobile-slide-image" style="height: 320px;">
					<xsl:for-each select="page/slide">
						<xsl:variable name="first" select="position() = 1"/>
						<div class="row mobile-slide-image hidden-sm hidden-md hidden-lg{' opaque'[$first]}" 
							style="background-image: url({@path}{pic}); display: block; position: absolute; width: 100%; height: 320px;">
							<xsl:if test="small_pic != ''">
								<div class="img-bgr">
									<img src="{@path}{small_pic}" alt="-"/>
								</div>
							</xsl:if>		
						</div>
					</xsl:for-each>		
				</div>
				<xsl:for-each select="page/slide">
					<xsl:variable name="first" select="position() = 1"/>
					<xsl:variable name="hidden" select="not(small_pic) or small_pic = ''"/> 
					<div style="{'visibility: hidden;'[$hidden]}{' display: none;'[not($first)]}" class="col-sm-5 hidden-xs desctop-addon">
						<img src="{@path}{small_pic}" alt="-"/>
					</div>
				</xsl:for-each>
				<div class="col-sm-7 text-left">
					<div class="slider-text">
						<xsl:for-each select="page/slide">
							<xsl:variable name="first" select="position() = 1"/>
							<div class="txt{' opaque'[$first]}">
								<h2 class="no-top-margin"><xsl:value-of select="header"/></h2>
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
						</xsl:for-each>
						<div class="row">
							<div class="col-xs-12 col-sm-6">
								<xsl:for-each select="page/slide">
									<xsl:variable name="first" select="position() = 1"/>
									<a class="btn btn-primary btn-lg txtbtn{' opaque'[$first]}" type="button" data-toggle="modal" data-target="" href="{href}">
										<xsl:value-of select="link_text"/>
									</a>
								</xsl:for-each>
							</div>
							<div class="col-xs-12 col-sm-6 slider-navigation">
								<div class="cf7_controls">
									<xsl:for-each select="page/slide">
										<xsl:variable name="first" select="position() = 1"/>
										<a href="" class="{'active'[$first]}"></a>
									</xsl:for-each>
								</div>
							</div>
						</div>
						<!-- <a class="btn btn-primary btn-lg" type="button" data-toggle="modal" 
							data-target="">О компании</a> -->
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="container main-content news vl">
		<div class="row">
			<div class="col-xs-12 col-sm-4 col-md-4">
				<div style="position:relative;height:0;padding-bottom:56.25%">
					<iframe src="https://www.youtube.com/embed/uYlnpheuVKU?ecver=2"
						width="640" height="360" frameborder="0"
						style="position:absolute;width:100%;height:100%;left:0"
						allowfullscreen="allowfullscreen"></iframe>
				</div>
			</div>
			<div class="col-xs-12 col-sm-8 col-md-8">
				<div class="row main-page-news">
					<div class="col-xs-12">
						<!-- tabs -->
						<ul class="nav nav-tabs" role="tablist">
							<xsl:for-each select="page/id_news/news_section">
								<xsl:variable name="active" select="position() = 1"/>
								<li role="presentation" class="{'active'[$active]}">
									<a href="#tab{position()}" role="tab" data-toggle="tab"><xsl:value-of select="name"/></a>
								</li>
							</xsl:for-each>
						</ul>
						<div class="tab-content">
							<xsl:for-each select="page/id_news/news_section">
								<xsl:variable name="active" select="position() = 1"/>
								<div role="tabpanel" class="tab-pane{' active'[$active]}" id="tab{position()}">
									<div class="col-xs-12">
										<div class="row">
											<xsl:for-each select="news_item">
												<div class="col-sm-4">
													<div class="date"><xsl:value-of select="date"/></div>
													<a href="{show_news_item}"><xsl:value-of select="header"/></a>
												</div>
											</xsl:for-each>
											<a href="{show_section}" style="position: absolute; bottom: -25px; left: 15px; color: #BF0000;">Перейти в раздел «<xsl:value-of select="name"/>»</a>
										</div>
									</div>
									<!-- <div class="col-xs-12">
										<p style="text-align: right; margin:10px 0 0 0; padding-top: 10px; border-top: 1px solid #ccc">
										
											<a href="{show_section}">Перейти в раздел →</a>
										</p>
									</div> -->
								</div>
							</xsl:for-each>
						</div>
						<!-- tabs end -->
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="container main-content seo-text vl" style="margin-top:2rem;">
		<!-- VL: сверстаная страница -->
		<div class="gal gal_full vl-relative">
			<img src="/sitefiles/1/63/1161/img_7268_.jpg" alt="TERMOBREST" />
			<div class="vl-bl-2"><div><span>РАЗРАБОТКА И ПРОИЗВОДСТВО:</span></div> <div><span>ГАЗОВОЙ ТРУБОПРОВОДНОЙ АРМАТУРЫ</span></div> <div><span>ПРИБОРОВ ДИСТАНЦИОННОЙ АВТОМАТИКИ БЕЗОПАСНОСТИ</span></div></div>
		</div>
		<div class="text-center my-5"><h3 class="vl-block-title">ПРОИЗВОДСТВО:</h3></div>
		<div class="jumbotron text-center text-xl-left">
			<div class="row mb-5">
				<div class="col-md-5"><img src="/img/_v1/vp-11.jpg" alt="" class="rounded shadow"/></div>
				<div class="col-md-7 fs-2 mt-4">
					<p>Завод газовой арматуры «ТЕРМОБРЕСТ» — это современное высокотехнологичное предприятие полного цикла: от НИОКР до производства готовых изделий.</p>
					<a href="/catalog/" class="btn btn-primary mx-3">Каталог</a> <a href="/contacts/" class="btn btn-primary mx-3">Контакты</a>
				</div>
			</div>
		</div>
		<div class="row mb-5">
			<div class="col-xs-6 col-sm-6 col-md-4 mb-5 text-center"><img src="/img/_v1/vp-2.png" alt=""  class="rounded shadow vl-persp-1"/></div>
			<div class="col-xs-6 col-sm-6 col-md-4 mb-5 text-center pull-right"><img src="/img/_v1/vp-3.png" alt=""  class="rounded shadow vl-persp-2"/></div>
			<div class="col-xs-12 col-sm-12 col-md-4"><p>Общая площадь производственных участков составляет более 12 000 м2 и состоит из трех площадок, сосредоточенных в г. Бресте, а также литейного цеха в г. Лунинец.</p><p> Завод является одним из самых современных машиностроительных предприятий на Евразийском континенте и входит в тройку лидеров арматуростроительной отрасли.</p></div>
		</div>

		<div class="text-center my-5"><h3 class="vl-block-title">НОМЕНКЛАТУРА ПРОДУКЦИИ:</h3></div>
		<p>На сегодняшний день номенклатура выпускаемой заводом продукции составляет <b>более 12 000 типов</b>, типоразмеров и исполнений изделий и включает в себя следующие группы:</p>
		<div class="row mb-5">
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/klapany_elektromagnitnye/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-1.jpg" alt="" />
					<span>Клапаны электромагнитные двухпозиционные</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/klapany_elektromagnitnye_dvoinye/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-2.jpg" alt="" />
					<span>Клапаны электромагнитные газовые двойные</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/bloki_klapanov_gazovyh/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-3.jpg" alt="" />
					<span>Блоки клапанов газовых</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/klapany_predohranitelno_zapornye/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-4.jpg" alt="" />
					<span>Клапаны предохранительно-запорные газовые</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/klapany_predohranitelno_sbrosnye/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-5.jpg" alt="" />
					<span>Клапаны предохранительно-сбросные газовые</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/regulyatory_stabilizatory_davleniya/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-6.jpg" alt="" />
					<span>Регуляторы-стабилизаторы давления газа</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/zaslonki_reguliruyuschie/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-7.jpg" alt="" />
					<span>Заслонки регулирующие</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/filtry/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-8.jpg" alt="" />
					<span>Фильтры газовые</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/smesiteli_gazov/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-9.jpg" alt="" />
					<span>Смесители газов</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/datchiki_rele_davleniya/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-10.jpg" alt="" />
					<span>Датчики-реле давления газа</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/sec/bloki_kontrolya_germetichnosti/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-11.jpg" alt="" />
					<span>Блоки контроля герметичности</span>
    			</a>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-4">
				<a href="/new_products/" class="thumbnail text-center vl-overflow vl-hover">
      				<img src="/img/_v1/i-12.jpg" alt="" />
					<span><b class="text-danger">NEW</b> Новинки</span>
    			</a>
			</div>
		</div>
		<div class="mb-5">
			<div class="text-center my-5"><h3 class="vl-block-title">СФЕРА ПРИМЕНЕНИЯ:</h3></div>
			<div class="vl-frame">
				<p>Продукция, производимая заводом, широко применяется в системах обеспечения безопасности и регулирования теплоэнергетических установок промышленного и бытового назначения, в сфере газоснабжения и газопотребления, а также во всех сферах жизнедеятельности, где есть газ.</p>
				<img src="sitefiles/1/63/1161/obekty_02.png" alt="География продаж TERMOBREST"  class="rounded shadow"/>
			</div>
		</div>
		<div class="mb-5">
			<div class="text-center my-5"><h3 class="vl-block-title">ПРЕИМУЩЕСТВА РАБОТЫ С НАМИ:</h3></div>
			<ul class="fs-15 vl-list">
<li><em><span>Собственная современная производственная база и штат сотрудников высокой квалификации;</span></em></li>
<li><em><span>Более чем 34-летний опыт внедрения передовых технологий в области арматуростроения;</span></em></li>
<li><em><span>Разветвленная сеть дилеров в СНГ, ЕС и Китае;</span></em></li>
<li><em><span>Сроки поставки партии продукции любой сложности и комплектации – не более 10 дней;</span></em></li>
<li><em><span>Вся продукция сертифицирована в системах EAC и CE;</span></em></li>
<li><em><span>Гарантийный срок на всю линейку продукции «ТЕРМОБРЕСТ» – 24 месяца с момента ввода в эксплуатацию;</span></em></li>
<li><em><span>Широкий диапазон климатических исполнений арматуры марки «ТЕРМОБРЕСТ» (от -60 до +60 °С) делает возможным ее применение во всех климатических поясах;</span></em></li>
<li><em><span>Продукция поставляется в более чем 30 стран мира: от Норильска до Ханоя, от Южно-Сахалинска до Уэстона (США);</span></em></li>
<li><em><span>Вся арматура может выпускаться во взрывозащищенном и сейсмостойком исполнении.</span></em></li>
</ul>
		</div>
		<div class="mb-5">
			<div class="text-center my-5"><h3 class="vl-block-title">НАШИ ПАРТНЕРЫ:</h3></div>
			<p>Многие годы качеству марки «<a href="https://regulgaz.ru/" style="text-decoration: none;"><span style="color: #333;">ТЕРМОБРЕСТ</span></a>» доверяют крупнейшие предприятия нефтегазодобывающего и теплоэнергетического комплекса, такие как «ГАЗПРОМ», «ЛУКОЙЛ», «РОСНЕФТЬ», «СУРГУТНЕФТЕГАЗ», «ТАТНЕФТЬ», «РУССКАЯ МЕДНАЯ КОМПАНИЯ» «БАШНЕФТЬ», CATERPILLAR, KOMATSU и многие другие.</p>
			<img src="sitefiles/1/63/1161/logotipy_02.png" alt="География продаж TERMOBREST" />
		</div>
		<!-- VL: END -->
		<!--
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12">
				<div style="color: #545454; font-size: 12px;">
					<xsl:value-of select="$seo/progon" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
		-->
	</div>

	</xsl:template>

	<xsl:template name="SCRIPTS"></xsl:template>

</xsl:stylesheet>
