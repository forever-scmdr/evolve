<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>



	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_section}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1><xsl:value-of select="$p/name"/></h1>

		<div class="catalog-item-container">
			<!--
			<div class="tags">
				<span>Акция</span>
				<span>Скидка</span>
				<span>Распродажа</span>
				<span>Горячая цена</span>
			</div>
			-->
			<div class="gallery">
				<div class="fotorama" data-width="100%" data-maxwidth="100%" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="true">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}"/>
					</xsl:for-each>
				</div>
			</div>
			<div class="product-info">
				<div class="price">
					<p><span>Старая цена</span>100 р.</p>
					<p><span>Новая цена</span><xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.</p>
				</div>
				<div class="order">
					<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
					<div id="cart_list_{$p/code}" class="product_purchase_container">
						<form action="{$p/to_cart}" method="post">
							<xsl:if test="$has_price">
								<input type="number" name="qty" value="1" min="0"/>
								<input type="submit" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="number" name="qty" value="1" min="0"/>
								<input type="submit" class="not_available" value="Под заказ"/>
							</xsl:if>
						</form>
					</div>
					<!--<div class="quantity">Осталось 12 шт.</div>-->
				</div>
				<div class="links">
					<label><input type="checkbox"/> cравнение</label>
					<label><input type="checkbox"/> избранное</label>
					<!-- <span><i class="fas fa-balance-scale"></i> <a href="">в сравнение</a></span>
					<span><i class="fas fa-star"></i> <a href="">в избранное</a></span> -->
				</div>
				<div class="info-blocks">
					<div class="info-block">
						<xsl:value-of select="$p/short" disable-output-escaping="yes"/>
					</div>
					<!--
					<div class="info-block">
						<h4>Рассрочка от 3 до 12 месяцев</h4>
						<p><a href="">Условия рассрочки</a></p>
					</div>
					<div class="info-block">
						<h4>Бесплатная доставка по Минску</h4>
						<p>При сумме заказа до 100 рублей, доставка — 5 рублей.</p>
					</div>
					-->
				</div>
			</div>
			<div class="description">
				<ul class="nav nav-tabs" role="tablist">
					<li role="presentation" class="active"><a href="#tab1" role="tab" data-toggle="tab">Описание</a></li>
					<li role="presentation"><a href="#tab2" role="tab" data-toggle="tab">Технические данные</a></li>
					<xsl:if test="page/accessory">
						<li role="presentation"><a href="#tab3" role="tab" data-toggle="tab">Принадлежности</a></li>
					</xsl:if>
					<xsl:if test="page/set">
						<li role="presentation"><a href="#tab4" role="tab" data-toggle="tab">Наборы</a></li>
					</xsl:if>
					<xsl:if test="page/probe">
						<li role="presentation"><a href="#tab5" role="tab" data-toggle="tab">Зонды</a></li>
					</xsl:if>
					<xsl:if test="$p/apply">
						<li role="presentation"><a href="#tab6" role="tab" data-toggle="tab">Применение</a></li>
					</xsl:if>
				</ul>
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="tab1">
						<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
					</div>
					<div role="tabpanel" class="tab-pane" id="tab2">
						<h4>Технические данные</h4>
						<div class="table-responsive">
						<xsl:for-each select="parse-xml(concat('&lt;tech&gt;', $p/tech, '&lt;/tech&gt;'))/tech/tag">
							<table>
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<thead>
									<tr>
										<th colspan="2"><xsl:value-of select="@name"/></th>
									</tr>
								</thead>
								<xsl:for-each select="parameter">
									<tr>
										<td>
											<p><strong>
												<xsl:value-of select="name"/></strong></p>
										</td>
										<td>
											<xsl:for-each select="value">
												<p><xsl:value-of select="."/></p>
											</xsl:for-each>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</xsl:for-each>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane" id="tab3">
						<h4>Принадлежности</h4>
						<div class="slick-slider catalog-items">
							<xsl:apply-templates select="page/accessory"/>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane" id="tab4">
						<h4>Наборы</h4>
						<div class="slick-slider catalog-items">
							<xsl:apply-templates select="page/set"/>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane" id="tab5">
						<h4>Зонды</h4>
						<div class="slick-slider catalog-items">
							<xsl:apply-templates select="page/probe"/>
						</div>
					</div>
					<div role="tabpanel" class="tab-pane" id="tab6">
						<xsl:value-of select="$p/apply" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>