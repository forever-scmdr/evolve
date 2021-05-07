<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="CONTENT" >
		<div class="content__main">
			<div class="slider-container">
				<div class="slider">
					<div class="slider__item">
						<div class="slider__image">
							<img src="img/slider.png" alt=""/>
						</div>
						<div class="slider__content">
							<div class="slider__title">Свежие идеи для вашего творчества</div>
							<div class="slider__text">Модуль Arduino Leonardo на ATMega32u4</div>
						</div>
					</div>
					<div class="slider__item">
						<div class="slider__image">
							<img src="img/slider.png" alt=""/>
						</div>
						<div class="slider__content">
							<div class="slider__title">Свежие идеи для вашего творчества 2</div>
							<div class="slider__text">Модуль Arduino Leonardo на ATMega32u4</div>
						</div>
					</div>
				</div>
				<div class="slider-nav"></div>
			</div>
			<div class="partners">
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
			</div>
			<div class="show-d">
				<div class="title_1">Новые поступления</div>
				<div class="devices">
					<div class="devices__wrap">
						<xsl:apply-templates select="page/new"/>
					</div>
				</div>
			</div>
			<div class="show-t">
				<div class="title_1">Каталог товаров</div>
				<div class="sections">
					<div class="sections__wrap">
						<xsl:for-each select="page/catalog/section">
							<div class="section">
								<a class="section__image" href="{show_section}">
									<xsl:if test="not(pic_path !='')">
										<img src="sitepics/{substring(code, string-length(code) - 4)}.jpg" alt="{name}" />
									</xsl:if>
									<xsl:if test="pic_path !=''">
										<img src="sitepics/{pic_path}" alt="{name}" />
									</xsl:if>
								</a>
								<a class="section__title" href="{show_section}"><xsl:value-of select="name"/></a>
								<div class="section__description"></div>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>

		<div class="content__side-2">
			<div class="best-price">
				<div class="best-price__header">Лучшие цены<a class="best-price__link" href="catalog-section-special.html">Смотреть все</a>
				</div>
				<div class="best-price__slider">
					<xsl:apply-templates select="page/hit"/>
				</div>
				<div class="best-price__nav"></div>
			</div>
			<div class="partners">
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
				<a class="partners__item partner-banner" href="">
					<img src="img/partner-logo-1.png" alt=""/>
				</a>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>
