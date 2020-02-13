<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat(if($p/type != '') then concat($p/type, ' ') else '', 'Метабо ', $p/name, ' купить в Минске: цена, рассрочка')"/>
	<xsl:variable name="meta_description" select="concat(if($p/type != '') then $p/type else $p/name, ' Метабо от официального дилера №1 ✅ Доставка по Беларуси ☎☎☎ +375 29 566 61 16 Хорошая цена, рассрочка, гарантия 3 года!')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else concat($p/type,' Metabo ' ,$p/name)"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="price" select="if($discount_time) then format-number(f:num($p/price)*$discount, '#0.00') else $p/price"/>
	<xsl:variable name="price_old" select="if($discount_time) then $p/price else $p/price_old"/>

	<xsl:variable name="extra_xml" select="parse-xml(concat('&lt;extra&gt;', $p/extra_xml, '&lt;/extra&gt;'))/extra"/>

	<xsl:template name="MARKUP">

		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)" />,
			"image": <xsl:value-of select="concat($quote, $base, '/', $p/@path, $p/gallery[1], $quote)" />,
			"brand": <xsl:value-of select="concat($quote, $p/tag[1], $quote)" />,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($p/price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:currency_decimal($p/price), $quote)" /></xsl:if>
			<xsl:if test="f:num($p/price) = 0">"price":"15000.00"</xsl:if>
			}, "aggregateRating": {
			"@type": "AggregateRating",
			"ratingValue": "4.9",
			"ratingCount": "53",
			"bestRating": "5",
			"worstRating": "1",
			"name": <xsl:value-of select="concat($quote, translate($p/name, $quote, ''), $quote)" />
			}
			}
		</script>
	</xsl:template>

	<xsl:template name="CONTENT">
		<xsl:if test="$p/discount != ''">
			<div style="display:none;" id="dsc-device" data-code="{$p/code}" data-discount="{$p/discount}" data-start="{concat($p/show_window,'000')}" data-last="{concat($p/discount_last,'000')}">
				<xsl:value-of select="$p/name"/>
			</div>
		</xsl:if>
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>
		<!--<h2><xsl:value-of select="$p/type"/></h2>-->
		<xsl:value-of select="$p/name_extra"/>
		<div><br></br></div>
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
				<xsl:if test="$has_price">
					<div class="price">
						<xsl:if test="$price_old and not($price_old = '')"><p><span>Цена</span><b>
							<xsl:value-of select="$price_old"/> р.</b></p></xsl:if>
						<p>
							<xsl:if test="$price_old and not($price_old = '')"><span>Цена со скидкой</span></xsl:if>
							<e class="price-highlight" id="price-{$p/code}" data-price="f:num($price)" ><xsl:value-of select="if ($price) then $price else '0'"/> р.</e>
						</p>
					</div>
				</xsl:if>
				<div class="order">
					<xsl:variable name="available" select="$p/available = '1'"/>
					<div id="cart_list_{$p/code}" class="product_purchase_container">
						<form action="{$p/to_cart}" method="post">
							<xsl:if test="$available">
								<input type="number" name="qty" value="1" min="0"/>
								<input type="submit" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($available)">
								<input type="number" name="qty" value="1" min="0"/>
								<input type="submit" class="not_available" value="Под заказ"/>
							</xsl:if>
						</form>
					</div>

					 <a href="#" class="online-button product-button">Онлайн-рассрочка</a>
					
<!--					<xsl:choose>-->
<!--						<xsl:when test="$p/qty and $p/qty != '0'"><div class="quantity">Осталось <xsl:value-of select="$p/qty"/> шт.</div></xsl:when>-->
<!--						<xsl:otherwise><div class="quantity">Нет на складе</div></xsl:otherwise>-->
<!--					</xsl:choose>-->
				</div>
				<div class="art-number">
					№ для заказа: <xsl:value-of select="$p/code" />
				</div>
				<div class="extra-links">
					<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price">Моя цена</a>
					<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</div>
				<div class="links">
					<div id="compare_list_{$p/code}">
						<span><i class="fas fa-balance-scale"></i> <a href="{$p/to_compare}" ajax="true" ajax-loader-id="compare_list_{$p/code}" rel="nofollow">в сравнение</a></span>
					</div>
					<div id="fav_list_{$p/code}">
						<span><i class="fas fa-star"></i> <a href="{$p/to_fav}" rel="nofollow" ajax="true" ajax-loader-id="fav_list_{$p/code}">в избранное</a></span>
					</div>
				</div>
				<xsl:variable name="pres" select="$pp[product_code = $p/code]"/>
				<xsl:if test="$pres">
					<div class="hover-tag mobile">
						<i class="hover-tag__icon fas fa-gift" />
						<a href="" data-toggle="modal" data-target="#pres_{$p/code}">Подарок</a>
					</div>
					<div class="hover-tag desktop">
						<i class="hover-tag__icon fas fa-gift" />
						<a data-toggle="popover" data-trigger="hover" data-placement="bottom" data-html="true">
							<xsl:attribute name="data-content">
								<xsl:for-each select="$pres">
									<xsl:variable name="present" select="//page/present[code = current()/present_code]"/>
									<xsl:variable name="pic_path"
									              select="if ($present/main_pic) then concat($present/@path, $present/main_pic) else 'img/no_image.png'"/>
									<xsl:variable name="link" select="$present/show_product"/>
									<xsl:text disable-output-escaping="yes">&lt;div class="gift-item"&gt;</xsl:text>
									<xsl:text disable-output-escaping="yes">&lt;img src="</xsl:text><xsl:value-of select="$pic_path"/><xsl:text disable-output-escaping="yes">" alt=""/&gt;</xsl:text>
									<xsl:text disable-output-escaping="yes">&lt;h3&gt;</xsl:text>
									<xsl:if test="$link">
										<xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="$link"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
									</xsl:if>
									<xsl:value-of select="$present/name"/><xsl:text> </xsl:text><xsl:value-of select="$present/type"/>
									<xsl:if test="qty">
										(<xsl:value-of select="qty"/>)<xsl:text> </xsl:text>
									</xsl:if>
									<xsl:if test="$link">
										<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
									</xsl:if>
									<xsl:text disable-output-escaping="yes">&lt;/h3&gt;</xsl:text>
									<xsl:value-of select="$present/short" disable-output-escaping="yes"/>
									<xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
								</xsl:for-each>
							</xsl:attribute>
							Подарок
						</a>
					</div>
				</xsl:if>
				<div class="info-blocks">
					<div class="info-block">
						<xsl:value-of select="$p/short" disable-output-escaping="yes"/>
						<xsl:if test="$extra_xml/manual">
							<div class="extra-block">
								<i class="fas fa-file-alt"></i><a href="{$extra_xml/manual}" rel="nofollow" target="_blank"><strong>Руководство по эксплуатации</strong></a>
							</div>
						</xsl:if>
						<xsl:if test="$extra_xml/parts">
							<div class="extra-block">
								<i class="fas fa-file-alt"></i><a href="{$extra_xml/parts}" target="_blank" rel="nofollow"><strong>Список запчастей</strong></a>
							</div>
						</xsl:if>
						<!--
						<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
						-->
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
		</div>
		<xsl:if test="page/assoc">
			<h3>Вас также может заинтересовать</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>

		<div class="modal fade" tabindex="-1" role="dialog" id="pres_{$p/code}">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title">Подарки к выбранному товару</h4>
					</div>
					<div class="modal-body">
						<div class="catalog-items lines">
							<xsl:for-each select="$pp">
								<xsl:variable name="pres" select="//page/present[code = current()/present_code]"/>
								<div class="catalog-item">
									<xsl:variable name="pic_path"
									              select="if ($pres/main_pic) then concat($pres/@path, $pres/main_pic) else 'img/no_image.png'"/>
									<a class="image-container" style="background-image: url({$pic_path});"/>
									<div>
										<a title="{$pres/name}">
											<xsl:value-of select="$pres/name"/><xsl:text> </xsl:text>
											<xsl:value-of select="$pres/type"/><xsl:text> </xsl:text>
											<xsl:if test="qty">
												(<xsl:value-of select="qty"/>)<xsl:text> </xsl:text>
											</xsl:if>
										</a>
										<div class="art-number">
											№ для заказа: <xsl:value-of select="$pres/code"/>
										</div>
										<p><xsl:value-of select="$pres/short" disable-output-escaping="yes"/></p>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
					<!-- <div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						<button type="button" class="btn btn-primary">Save changes</button>
					</div> -->
				</div>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>