<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template name="CONTENT" >
		<div class="content__main">
			<xsl:if test="page/main_page/main_slider_frame">
				<div class="slider-container">
					<div class="slider" timeout="{page/main_page/timeout}">
						<xsl:for-each select="page/main_page/main_slider_frame">
							<div class="slider__item">
								<div class="slider__image">
									<a href="{link}">
									<img src="{concat(@path, pic)}" alt="{name}"/>
									</a>
								</div>
								<xsl:if test="f:num(hide_text) != 1">
									<div class="slider__content">
										<div class="slider__title">
											<xsl:value-of select="name"/>
										</div>
										<div class="slider__text">
											<xsl:value-of select="text" disable-output-escaping="yes"/>
										</div>
									</div>
								</xsl:if>
							</div>
						</xsl:for-each>
					</div>
					<div class="slider-nav"></div>
				</div>
			</xsl:if>
			<div class="partners">
				<xsl:for-each select="/page/main_page/link_block/link">
					<a class="partners__item partner-banner" href="{link}">
						<img src="{concat(@path, icon)}" alt="{name}"/>
					</a>
				</xsl:for-each>
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
								<a class="section__image" href="{show_products}">
									<xsl:if test="not(pic_path !='')">
										<img src="sitepics/{substring(code, string-length(code) - 4)}.jpg" alt="{name}" />
									</xsl:if>
									<xsl:if test="pic_path !=''">
										<img src="sitepics/{pic_path}" alt="{name}" />
									</xsl:if>
								</a>
								<a class="section__title" href="{show_products}"><xsl:value-of select="name"/></a>
								<div class="section__description"></div>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>

		<div class="content__side-2">
			<div class="best-price">
				<div class="best-price__header">Лучшие цены<a class="best-price__link" href="/best_price">Смотреть все</a>
				</div>
				<div class="best-price__slider">
					<xsl:apply-templates select="page/hit"/>
				</div>
				<div class="best-price__nav"></div>
			</div>
			<div class="partners">
				<xsl:for-each select="/page/main_page/link_block/link">
					<a class="partners__item partner-banner" href="{link}" target="_blank">
						<img src="{concat(@path, icon)}" alt="{name}"/>
					</a>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>
