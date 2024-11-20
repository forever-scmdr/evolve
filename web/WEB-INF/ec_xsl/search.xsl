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


	<xsl:variable name="title" select="'Поиск'"/>
	
	<xsl:variable name="products" select="page/product" />
	<xsl:variable name="news" select="page/news_item | page/text_part/news_item" />
	


	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'catalog'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">Результаты поиска по запросу «<xsl:value-of select="page/variables/q"/>»</h2>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<!-- tabs -->
						<xsl:choose>
							<xsl:when test="not($products) and not($news)">
								<p>Ничего не найдено.</p>
							</xsl:when>
							<xsl:when test="not($products) and $news">
								<p>Найдено новостей: <xsl:value-of select="count($news)"/></p>
							</xsl:when>
							<xsl:when test="$products and not($news)">
								<p>Найдено продуктов: <xsl:value-of select="count($products)"/></p>
							</xsl:when>
							<xsl:otherwise>
								<ul class="nav nav-tabs" role="tablist">
									<li role="presentation" class="active">
										<a href="#search-catalog" aria-controls="home" role="tab"
											data-toggle="tab">Продукты (<xsl:value-of select="count($products)"/>)</a>
									</li>
									<li role="presentation">
										<a href="#search-news" aria-controls="profile" role="tab"
											data-toggle="tab">Новости (<xsl:value-of select="count($news)"/>)</a>
									</li>
								</ul>
							</xsl:otherwise>
						</xsl:choose>

						<div class="tab-content">
							<xsl:if test="$products">
								<div role="tabpanel" class="tab-pane active" id="search-catalog">
									<div class="row">
										<div class="col-xs-12">
											<ol class="search-results">
												<xsl:for-each select="$products">
													<li>
														<a href="{show_product}"><xsl:value-of select="name"/></a>
														<xsl:if test="mods">
															<p class="mods">
																<xsl:value-of select="mods"/>
															</p>
														</xsl:if>
													</li>
												</xsl:for-each>
											</ol>
										</div>
									</div>
								</div>
							</xsl:if>

							<xsl:if test="$news">
								<div role="tabpanel" class="tab-pane{'active'[count($products) = 0]}" id="search-news">
									<div class="row">
										<div class="col-xs-12">
											<ol class="search-results">
												<xsl:for-each select="$news">
													<xsl:variable name="is_secret" select="dealer_info_count != '0'" />
													<xsl:variable name="href" select="if(dealer_info) then dni_link else ni_link" />
													<li>
														<a href="{$href}"><xsl:value-of select="header"/></a>
														<p><xsl:value-of select="date"/></p>
													</li>
												</xsl:for-each>
											</ol>
										</div>
									</div>
								</div>
							</xsl:if>
						</div>
						<!-- tabs end -->
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
		var searchQuery = '<xsl:value-of select="page/variables/q"/>';
	</script>
	
</xsl:template>

<xsl:template name="SCRIPTS">
	<script type="text/javascript" src="js/highlight-match.js"></script>
</xsl:template>


</xsl:stylesheet>