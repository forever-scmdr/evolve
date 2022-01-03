<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="MARKUP">
		<script type="application/ld+json">
			{
				"@context":"http://schema.org",
				"@type":"Organization",
				"url":"<xsl:value-of select="$main_host"/>/",
				"name":"<xsl:value-of select="$title"/>",
				"logo":"<xsl:value-of select="concat($main_host, '/img/logo_big.svg')"/>",
				"aggregateRating": {
					"@type": "AggregateRating",
					"ratingCount": "53",
					"reviewCount": "53",
					"bestRating": "5",
					"ratingValue": "4,9",
					"worstRating": "1",
					"name": "BAAZ"
				},
				"contactPoint": [
					<xsl:for-each select="page/common/phone" >
						<xsl:if test="position() != 1">,</xsl:if>{
						"@type":"ContactPoint",
						"telephone":"<xsl:value-of select="tokenize(., '_')[1]"/>",
						"contactType":"<xsl:value-of select="tokenize(., '_')[2]"/>"
						}
					</xsl:for-each>
				]
				<xsl:if test="page/common/email != ''">
				,"email":[<xsl:for-each select="page/common/email" >
						<xsl:if test="position() != 1">, </xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>]
				</xsl:if>
			}
		</script>
	</xsl:template>


	<xsl:template name="MAIN_CONTENT" />




	<xsl:template name="INDEX_BLOCKS">
		<!-- banner 1 -->
		<div class="container content">
			<div class="indexSlide">
		    	<div class="dots">
					<a class="dot" href="#" style="margin-left:290px; margin-top:300px;">
						<span class="title"><span class="arrow"></span>
		                  	Мы производим:
							Cистема подвески
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:360px; margin-top:270px;">
						<span class="title"><span class="arrow"></span>
							Мы производим: 
							Система рулевого управления
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:40px; margin-top:270px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Тормозная система
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:280px; margin-top:250px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Система сцепления
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:340px; margin-top:200px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Двигатель и трансмиссия
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:390px; margin-top:130px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Система электрооборудования
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:200px; margin-top:130px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Кузов и оборудование
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:60px; margin-top:210px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Шланги
						</span>
					</a>
					<a class="dot" href="#" style="margin-left:120px; margin-top:270px;">
						<span class="title">
							<span class="arrow"></span>
							Мы производим: 
							Крепеж
						</span>
					</a>
				</div>
				<div class="text">
					<img src="/img/text_main_page_big_title.png" alt="" />
					<p>
						Наше предприятие хорошо известно, как поставщик для 
						сборочных конвейеров таких автомобильных гигантов, 
						как МАЗ, МЗКТ, БелАЗ, ГАЗ, КамАЗ, Урал, ПАЗ, ЛАЗ, 
						ООО «Ликинский автобус» и других.
						
					</p>
					<div style="margin-top: 32px;">
						<a class="big" href="/catalog/">Каталог продукции</a>
						<div class="clear"></div>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<!-- banner 1 end -->
		<!-- slider 
		<div class="slider container">
			<xsl:for-each select="page/main_page/main_slider_frame">
				<div class="slider__item">
					<div class="slider__content">
						<div class="container slider__container">
							<div class="slider__body">
								<div class="slider__title"><xsl:value-of select="name" disable-output-escaping="yes"/></div>
								<div class="slider__text"><xsl:value-of select="text" disable-output-escaping="yes"/></div>
								<a class="slider__button button" href=""><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
							</div>
						</div>
					</div>
					<div class="slider__image">
						<img src="{@path}{pic}" alt="" />
					</div>
				</div>
			</xsl:for-each>
		</div>
		<div class="slider-nav"></div>
	 slider end -->
		<div class="container">
			<div class="indexText">
				<div><img src="img/text_main_page_partners.png" alt="наши партнеры"/></div>
				<div class="left">
					<div class="partners">
						<span data-link="www.autokraz.com.ua/" style="padding-top: 20px;">
							<img width="100" height="" class="white" src="img/images/logo_kraz.png" alt="КРАЗ"/>
							<img width="100" height="" class="red" src="img/images/logo_kraz_hover.png" alt="КРАЗ"/>
						</span>
						<span data-link="maz.by/" style="">
							<img width="74" height="44" class="white" src="img/images/logo_maz.png" alt="МАЗ"/>
							<img width="74" height="44" class="red" src="img/images/logo_maz_hover.png" alt="МАЗ"/>
						</span>
						<span data-link="kamaz.net" style="">
							<img style="padding-top: 18px" class="white" src="img/images/logo_kamaz.png" alt="КАМАЗ"/>
							<img style="padding-top: 18px" class="red" src="img/images/logo_kamaz_hover.png" alt="КАМАЗ"/>
						</span>
						<span data-link="www.amo-zil.ru/" style="">
							<img style="padding-top: 13px" class="white" src="img/images/logo_zil.png" alt="ЗИЛ"/>
							<img style="padding-top: 13px" class="red" src="img/images/logo_zil_hover.png" alt="ЗИЛ"/>
						</span>
						<span data-link="gazgroup.ru/" style="margin-top: 4px;;">
							<img class="white" src="img/images/logo_gaz.png" alt="ГАЗ"/>
							<img class="red" src="img/images/logo_gaz_hover.png" alt="ГАЗ"/>
						</span>
						<span data-link="www.uralaz.ru/" style="">
							<img style="padding-top: 0px" class="white" src="img/images/logo_ural.png" alt="Урал"/>
							<img style="padding-top: 0px" class="red" src="img/images/logo_ural_hover.png" alt="Урал"/>
						</span>
						<span data-link="liaz.gaz.ru/" style="">
							<img class="white" src="img/images/logo_liaz.png" alt="ЛИАЗ"/>
							<img class="red" src="img/images/logo_liaz_hover.png" alt="ЛИАЗ"/>
						</span>
						<span data-link="www.laz.ua/ru/" style="margin-top: -14px;">
							<img class="white" src="img/images/logo_laz.png" alt="ЛАЗ"/>
							<img class="red" src="img/images/logo_laz_hover.png" alt="ЛАЗ"/>
						</span>
						<span data-link="belaz.by/" style="">
							<img class="white" src="img/images/logo_belaz.png" alt="БЕЛАЗ"/>
							<img class="red" src="img/images/logo_belaz_hover.png" alt="БЕЛАЗ"/>
						</span>
						<span data-link="www.paz-bus.ru/start/index" style="margin-top: -12px;">
							<img class="white" src="img/images/logo_paz.png" alt="ПАЗ"/>
							<img class="red" src="img/images/logo_paz_hover.png" alt="ПАЗ"/>
						</span>
						
					</div>
					<div><img src="img/text_main_page_news.png" alt="новости"/></div>
					
					<xsl:variable name="news_item" select="/page/news_wrap/news_item"/>
					<xsl:if test="$news_item">
						<div class="news">
							
							<xsl:for-each select="$news_item">
								<div class="item">
									<p class="date">
										<xsl:value-of select="date"/>
									</p>
									<h3>
										<a href="{show_news_item}">
											<xsl:value-of select="header"/>
										</a>
									</h3>
									<div class="info-item__text">
										<xsl:value-of select="short" disable-output-escaping="yes"/>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</xsl:if>
				</div>
				<div class="right" style="hidden">
					<div id="cs-slider">
						<a href="news_item/amortizatory_dlya_tehniki_evropeiskogo_proizvodstva/" title="Слайд 1">
							<img src="img/ban-1.jpeg" alt="name" />
						</a>
						<a href="news_item/novye_izdeliya481/" title="ТСУ">
							<img src="img/ban-2.jpeg" alt="name" />
						</a>
						<a href="products/nelikvidy/" title="Неликвиды">
							<img src="img/ban-3.jpeg" alt="name" />
						</a>
					</div>
				</div>
			</div>
		</div>
		<!--
		<xsl:apply-templates select="page/main_page/custom_block[type='type_sections']"></xsl:apply-templates>
		-->
			
		<!-- products carousel -->
		<!--
		<div class="block devices-block ptb">
			<div class="container">
				<div class="title title_2">Выгодные предложения</div>
				<div class="devices-block__wrap device-carousel">
					<xsl:for-each select="page/main_page/product">
						<div class="devices-block__column">
							<xsl:apply-templates select="."/>
						</div>
					</xsl:for-each>
				</div>
				<div class="device-nav"></div>
			</div>
		</div>

		<xsl:apply-templates select="page/main_page/custom_block[type='type_digits']"></xsl:apply-templates>
		<div class="divider"></div>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_about']"></xsl:apply-templates>
		<div class="divider"></div>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_utp']"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_gifts']"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_map']"></xsl:apply-templates>
		-->
		<!-- <section class="news pt">
			<div class="container">
				<div class="block__title block__title_left">
					<a href="/novosti" style="text-decoration: none; color: black">Events</a>
				</div>
				<div class="grid">
					<xsl:for-each select="page//news_item">
						<div class="news__item">
							<a class="news__image-container" href="{show_news_item}"><img src="{@path}{main_pic}" alt="{name}" /></a>
							<div class="date"><xsl:value-of select="tokenize(date, ' ')[1]" /></div>
							<a class="news__title" href="{show_news_item}"><xsl:value-of select="header" /></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section> -->


		<xsl:if test="$seo[1]/text">
			
			<section class="s-info">
				<div style="margin-top:2rem;"></div>
				<div class="container">
					<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
				</div>
			</section>
		</xsl:if>

		<xsl:if test="$seo[1]/bottom_text">
			
			<section class="s-info">
				<div style="margin-top:2rem;"></div>
				<div class="container">
					<xsl:value-of select="$seo[1]/bottom_text" disable-output-escaping="yes"/>
				</div>
			</section>
		</xsl:if>

	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
		<script type="text/javascript" src="js/cubeslider-min.js"/>
		<script type="text/javascript">
		
				$('#cs-slider').cubeslider({
					cubesNum: {rows:1, cols:1},
					orientation: 'h',
					cubeSync: 50
					,autoplay: true
					,navigation: false
					,arrows: true
					,play: false
					,autoplayInterval: 4000
				});
			</script>
	</xsl:template>

</xsl:stylesheet>
