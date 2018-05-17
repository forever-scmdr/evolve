<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="extra_xml" select="parse-xml(concat('&lt;extra&gt;', $p/extra_xml, '&lt;/extra&gt;'))/extra"/>



	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{if (position() = 1) then show_section else show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$p/name"/></h1>
		<h2><xsl:value-of select="$p/type"/></h2>
		<h3><xsl:value-of select="$p/name_extra"/></h3>

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
					<xsl:if test="$extra_xml/spin">
						<div data-thumb="img/360grad.png" style="height: 100%">
							<iframe width="100%" height="100%" data-autoplay="0" src="{tokenize($extra_xml/spin/@link, ' ')[1]}"
							        frameborder="0" allowfullscreen="" style="display: block;"/>
						</div>
					</xsl:if>
					<xsl:for-each select="$extra_xml/video">
						<a href="{substring-before(replace(@link, '-nocookie.com/embed/', '.com/watch?v='), '?rel')}">1111</a>
					</xsl:for-each>
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="img/no_image.png"/>
					</xsl:if>
				</div>
			</div>
			<div class="product-info">
				<!-- <p>№ для заказа: <xsl:value-of select="$p/code" /></p> -->
				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
				<!-- <xsl:if test="$has_price"> -->
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span><xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.</p>
					</div>
				<!-- </xsl:if> -->
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
					<!--<xsl:choose>-->
						<!--<xsl:when test="$p/qty and $p/qty != '0'"><div class="quantity">Осталось <xsl:value-of select="$p/qty"/> шт.</div></xsl:when>-->
						<!--<xsl:otherwise><div class="quantity">Нет на складе</div></xsl:otherwise>-->
					<!--</xsl:choose>-->
				</div>
				<div class="art-number">
					№ для заказа: <xsl:value-of select="$p/code" />
				</div>
				<div class="links">
					<div id="compare_list_{$p/code}">
						<span><i class="fas fa-balance-scale"></i> <a href="{$p/to_compare}" ajax="true" ajax-loader-id="compare_list_{$p/code}">в сравнение</a></span>
					</div>
					<div id="fav_list_{$p/code}">
						<span><i class="fas fa-star"></i> <a href="{$p/to_fav}" ajax="true" ajax-loader-id="fav_list_{$p/code}">в избранное</a></span>
					</div>
				</div>
				<div class="info-blocks">
					<div class="info-block">
						<xsl:value-of select="$p/short" disable-output-escaping="yes"/>
						<xsl:if test="$extra_xml/manual">
							<div class="extra-block">
								<i class="fas fa-file-alt"></i><a href="{$extra_xml/manual}" target="_blank"><strong>Руководство по эксплуатации</strong></a>
							</div>
						</xsl:if>
						<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
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
					<li role="presentation" class="active"><a href="#text" role="tab" data-toggle="tab">Описание</a></li>
					<li role="presentation"><a href="#tech" role="tab" data-toggle="tab">Технические данные</a></li>
					<li role="presentation"><a href="#package" role="tab" data-toggle="tab">Объем поставки</a></li>
				</ul>
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="text">
						<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
					</div>
					<div role="tabpanel" class="tab-pane" id="tech">
						<xsl:value-of select="$p/product_extra[name = 'tech']/text" disable-output-escaping="yes"/>
					</div>
					<div role="tabpanel" class="tab-pane" id="package">
						<xsl:value-of select="$p/product_extra[name = 'package']/text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
			<xsl:if test="page/assoc">
				<h3>Вас также может заинтересовать</h3>
				<div class="catalog-items">
					<xsl:apply-templates select="page/assoc"/>
				</div>
			</xsl:if>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>