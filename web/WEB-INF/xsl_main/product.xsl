<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="title" select="concat($p/name, ' ', $p/name_extra)"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big" select="if (index-of($p/text, 'img src') &gt; -1 or string-length($p/text) &gt; 500) then $p/text else ''"/>
	<xsl:variable name="is_big" select="$p_big and not($p_big = '')"/>

	<xsl:variable name="zero" select="not($p/is_service = '1') and f:num($p/qty) &lt; 0.001"/>
	<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

	<xsl:template name="MARKUP">
		<xsl:variable name="price" select="$p/price"/>
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)" />,
			"image": <xsl:value-of select="concat($quote, $main_host, '/', $p/@path, $p/gallery[1], $quote)" />,
			"brand": <xsl:value-of select="concat($quote, $p/tag[1], $quote)" />,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:currency_decimal($price), $quote)" /></xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15000.00"</xsl:if>
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

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<div class="path__arrow"></div>
					<a class="path__link" href="{show_products}">
						<xsl:value-of select="name"/>
					</a>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="group">
		<xsl:if test="parameter/value != ''">
			<tr>
				<th colspan="2"><b><xsl:value-of select="@name"/></b></th>
			</tr>
			<xsl:apply-templates select="parameter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()/name))]"/>
		<xsl:if test="$param">
			<tr>
				<td><xsl:value-of select="$param/@caption"/></td>
				<td><xsl:value-of select="$param"/></td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CONTENT_INNER">
		<div class="device-basic">
			<div class="device-basic__column gallery">
				<!-- <div class="fotorama" data-width="100%" data-nav="thumbs" data-arrows="false" data-thumbheight="75" data-thumbwidth="75" data-allowfullscreen="native">
					<xsl:for-each select="('b', 'c', 'd')">
						<img src="sitepics/{$p/pic_path}{.}.jpg" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg';"/>
					</xsl:for-each>
					<xsl:for-each select="$p/filevid">
						<xsl:variable name="id" select="if(contains(., 'embed')) then substring-after(., '/embed/') else substring-after(., '/youtu.be/')"/>
						<a href="{concat('https://www.youtube.com/embed/', $id)}" class="fancy_vid fancybox.iframe" onclick="return false;"  rel="group_1">

							<img src="{concat('http://i3.ytimg.com/vi/', $id, '/hqdefault.jpg')}"/>
							видео
						</a>
					</xsl:for-each>
				</div>
				<script>
					$('.fotorama')
					.on('fotorama:fullscreenenter fotorama:fullscreenexit', function (e, fotorama) {
					if (e.type === 'fotorama:fullscreenenter') {
					// Options for the fullscreen
					fotorama.setOptions({
					fit: 'scaledown'
					});
					} else {
					// Back to normal settings
					fotorama.setOptions({
					fit: 'contain'
					});
					}
					})
					.fotorama();
				</script> -->

				<div class="pseudorama">

					<xsl:variable name="device-title" select="'Фотографии товаров являются наглядными примерами и могут отличаться от реального вида товара. Это не влияет на технические характеристики.'" />

					<xsl:for-each select="('b', 'c', 'd')">
						<a href="sitepics/{$p/pic_path}{.}.jpg" class="example1" id="{concat('example', position())}" rel="group_1" style="{if(position() != 1) then 'display:none;' else ''}" title="{$device-title}">
							<!-- <xsl:if test="position() = 1"> -->
								<img src="sitepics/{$p/pic_path}{.}.jpg" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg'; this.removeAttribute('onerror')"/>
							<!-- </xsl:if> -->
						</a>
					</xsl:for-each>
					<div class="thumbs">
						<xsl:for-each select="('b', 'c', 'd')">
							<a class="thumb{if(position() = 1) then ' active' else ''}" href="sitepics/{$p/pic_path}{.}.jpg" data-show="{concat('#example', position())}">
								<img src="sitepics/{$p/pic_path}{.}.jpg" alt="{$p/name}" onerror="$(this).closest('a').remove(); $('#example{position()}').remove(); if($('.thumbs a').length &lt; 2) $('.thumbs').remove();"/>
							</a>
						</xsl:for-each>
						<xsl:for-each select="$p/filevid">
							<xsl:variable name="id" select="if(contains(., 'embed')) then substring-after(., '/embed/') else substring-after(., '/youtu.be/')"/>
							<a href="{concat('https://www.youtube.com/embed/', $id)}" class="fancy_vid fancybox.iframe" onclick="return false;"  rel="group_1">
								<img src="{concat('http://i3.ytimg.com/vi/', $id, '/hqdefault.jpg')}"/>
								видео
							</a>
						</xsl:for-each>
					</div>

					<script type="text/javascript">
						$(document).ready(function() {
							$(".example1").fancybox({
								'titlePosition'	: 'over',
								'transitionEffect' : false,
								'titleFormat'	: function(title, currentArray, currentIndex, currentOpts) {
								   return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? '   ' + title : '') + '</span>';
								}
							});
							$(".zoom").fancybox();
							$(".fancy_vid").fancybox({
							  youtube:{}
							});

							$(".thumb").click(function(e){

								e.preventDefault();
								$(".example1").hide();
								$($(this).attr("data-show")).show();
								$(".thumb").removeClass("active");
								$(this).addClass("active");
							});

						});
					</script>
				</div>

			</div>
			<div class="device-basic__column product">
				<div class="product-info">
					<div class="basic-params">
						<div class="basic-params__item">
							<div class="basic-params__param">Код товара:</div>
							<div class="basic-params__value"><xsl:value-of select="$p/code"/></div>
						</div>
						<div class="basic-params__item">
							<div class="basic-params__param">Производитель:</div>
							<div class="basic-params__value"><xsl:value-of select="$p/vendor"/></div>
						</div>
						<div class="basic-params__item">
							<div class="basic-params__param">Страна происхождения:</div>
							<div class="basic-params__value"><xsl:value-of select="$p/country"/></div>
						</div>
					</div>
					<div class="full-params" style="margin-top:120px">
						<div class="product-subtitle">
							<img src="img/icon-device-icon-04.png" alt=""/>
							<span style="font-weight: normal;" >Технические характеристики</span>
						</div>
						<table>
							<xsl:for-each select="$p/params/param[@caption != 'Сертификат']">
								<tr>
									<td><xsl:value-of select="@caption" /></td>
									<td><xsl:value-of select="." /></td>
								</tr>
							</xsl:for-each>
						</table>
						<p style="padding-top: 10px"><b><xsl:value-of select="$p/params/param[@caption = 'Сертификат']" /></b></p>
					</div>
					<div class="product-download">
						<xsl:for-each select="$p/file[. != '']">
							<div class="product-download__item">
								<img src="img/icon-device-icon-02.png" alt=""/>
								<a href="sitedocs/{.}" title="скачать документацию (datasheet) по {../name_extra} в формате pdf">
									скачать документацию (datasheet) по <xsl:value-of select="../name_extra"/> в формате pdf
								</a>
							</div>
						</xsl:for-each>
					</div>
					<xsl:if test="$p/text != ''">
						<div class="product-description">
							<div class="product-subtitle">
								<img src="img/icon-device-icon-01.png" alt=""/>
								<span>Описание товара</span>
							</div>
							<div class="text">
								<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:if>
				</div>
				<div class="product-order">
					<xsl:if test="$p/special_price = 'true' and not($zero)"><span style="color:red">Спеццена</span></xsl:if>
					<div class="product-order__price">
						<xsl:if test="not($zero)">
							<xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/>/<xsl:value-of select="$p/unit"/>
						</xsl:if>
						<xsl:if test="$zero">
							<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>
						</xsl:if>
					</div>
					<div class="product-order__status">
						<xsl:if test="$zero">
							<xsl:if test="$p/soon != '0'">
								<div class="status__wait">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></div>
							</xsl:if>
							<xsl:if test="not($p/soon != '0')">
								<div class="status__na">Нет в наличии</div>
							</xsl:if>
						</xsl:if>
						<xsl:if test="f:num($p/sec/discount_1) &gt; 0">
							<div class="sale">от <xsl:value-of select="$p/sec/limit_1"/>&#160;<xsl:value-of select="$p/unit"/> -
								<xsl:value-of select="$p/sec/discount_1"/>%<xsl:call-template name="BR"/>
								от <xsl:value-of select="$p/sec/limit_2"/>&#160;<xsl:value-of select="$p/unit"/> -
								<xsl:value-of select="$p/sec/discount_2"/>%
							</div>
						</xsl:if>
						<xsl:if test="not($zero) and not($p/is_service = '1')">
							В наличии: <strong><xsl:value-of select="concat($p/qty, ' ', $p/unit)"/></strong>
						</xsl:if>
					</div>
					<div class="product-order__order" id="cart_list_{$p/@id}">
						<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
							<xsl:if test="$has_price">
								<input type="number" class="input input_type_number" name="qty"
									   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else 0.1}" />
								<button class="button" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="input input_type_number" name="qty"
									   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else 0.1}" />
								<button class="button button_secondary" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
							</xsl:if>
						</form>
					</div>
					<div class="product-order__add">
						<xsl:choose>
							<xsl:when test="$is_fav">
								<div>
									<a href="{$p/from_fav}"><xsl:value-of select="$fav_remove_label"/></a>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<div id="fav_list_{$p/@id}">
									<a href="{$p/to_fav}" ajax="true" ajax-loader-id="fav_list_{$p/@id}"><xsl:value-of select="$fav_add_label"/></a>
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
				<div class="tabs tabs_product">
					<div class="tabs__nav">
						<xsl:if test="page/analog">
							<a class="tab tab_active" href="#tab_analog">
								<div class="tab__text">Аналогичные товары</div>
							</a>
						</xsl:if>
						<xsl:variable name="has_analog" select="page/analog"/>
						<xsl:if test="page/related">
							<a class="tab{' tab_active'[not($has_analog)]}" href="#tab_related">
								<div class="tab__text">Смежные товары</div>
							</a>
						</xsl:if>
					</div>
					<div class="tabs__content">
						<div class="tab-container" id="tab_analog">
							<div class="devices devices_product">
								<div class="devices__wrap">
									<xsl:apply-templates select="page/analog"/>
								</div>
							</div>
						</div>
						<div class="tab-container" id="tab_related" style="display: none;">
							<div class="devices devices_product">
								<div class="devices__wrap">
									<xsl:apply-templates select="page/related"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
