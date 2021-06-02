<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:import href="snippets/constants.xsl"/>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="params" select="$p/params"/>
	<xsl:variable name="extra_xml" select="parse-xml(concat('&lt;extra&gt;', $p/extra_xml, '&lt;/extra&gt;'))/extra"/>
	<xsl:variable name="extra_text" select="$p/product_extra" />


	<xsl:variable name="has_price" select="f:num($p/price) != 0"/>
	<xsl:variable name="price" select="$p/price"/>
	<xsl:variable name="price_old" select="$p/price_old"/>

	<xsl:template match="/">
		<div>
			<div class="popup result" id="product-ajax-popup">
				<div class="popup__body">
					<div class="popup__content">
						<a class="popup__close" onclick="clearProductAjax();">×</a>
						<div class="popup__title title title_2">Товар: 
							<xsl:value-of select="$p/name" />
						</div>
						<div class="device-preview">
							<div class="device-preview__column">
								<div class="gallery">
									<xsl:call-template name="FOTORAMA"/>
								</div>
							</div>
							<div class="device-preview__column">
								<div class="device-preview__tags tags">
									<xsl:for-each select="$p/tag">
										<div class="tag">
											<xsl:value-of select="."/>
										</div>
									</xsl:for-each>
								</div>
								<div class="device-actions">
									<div class="device-actions__price price">
										<xsl:if test="$has_price">
											<div class="price__new">Цена: 
												<xsl:value-of select="$price"/> р.
											</div>
										</xsl:if>
										<xsl:if test="f:num($price_old) != 0">
											<div class="price__old">
												<xsl:value-of select="$price_old"/> р.
											</div>
										</xsl:if>
									</div>
									<div class="device-actions__icons add">
										<xsl:call-template name="COMPARE"/>
										<xsl:call-template name="FAV" />
									</div>
									<div class="device-actions__buttons order-buttons">
										<xsl:call-template name="CART" />&#160;<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click" class="button button_secondary">Купить в 1 клик</a>
									</div>
								</div>
								<div class="tabs">
									<div class="tabs__links">
										<a href="#tab-params" role="tab" data-toggle="tab" class="tabs__link tabs__link_active">Характеристики</a>
										<xsl:if test="$p/text != ''">
											<a href="#tab-text" role="tab" data-toggle="tab" class="tabs__link">Описание</a>
										</xsl:if>
										<xsl:for-each select="$extra_text[name != 'tech']">
											<a href="#tab-{@id}" role="tab" data-toggle="tab" class="tabs__link">
												<xsl:value-of select="f:tab_name(name)"/>
											</a>
										</xsl:for-each>
									</div>
									<div class="tabs__content active" role="tabpanel" id="tab-params">
										<table>
											<xsl:for-each select="$params/param">
												<tr>
													<td><xsl:value-of select="@caption"/></td>
													<td><xsl:value-of select="."/></td>
												</tr>
											</xsl:for-each>
										</table>
									</div>
									<div class="tabs__content" role="tabpanel" id="tab-text" style="display: none;">
										<div class="page-content">
											<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
										</div>
									</div>
									<xsl:for-each select="$extra_text">
										<div class="tabs__content" role="tabpanel" id="tab-{@id}" style="display: none;">
											<div class="page-content">
												<xsl:value-of select="text" disable-output-escaping="yes"/>
											</div>
										</div>
									</xsl:for-each>
								</div>
							</div>
						</div>
						<script type="text/javascript">
							insertAjax('cart_ajax');
							insertAjax('compare_ajax');
							insertAjax('fav_ajax');
						</script>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="FOTORAMA">
		<div class="fotorama" data-ratio="287/400" data-nav="thumbs" data-maxwidth="287" data-maxheight="400"
			 data-thumbheight="40" data-thumbwidth="40" id="fotorama-ajax">
			<xsl:if test="$extra_xml/spin">
				<div data-thumb="img/360.png" style="height: 100%">
					<iframe width="100%" height="100%" data-autoplay="0" src="{tokenize($extra_xml/spin/@link, ' ')[1]}"
							frameborder="0" allowfullscreen="" style="display: block;"/>
				</div>
			</xsl:if>
			<xsl:for-each select="$extra_xml/video">
				<a href="{substring-before(replace(@link, '-nocookie.com/embed/', '.com/watch?v='), '?rel')}">video</a>
			</xsl:for-each>
			<xsl:for-each select="$p/main_pic | $p/gallery">
				<img src="{$p/@path}{.}"/>
			</xsl:for-each>
			<xsl:if test="not($p/main_pic | $p/gallery)">
				<img src="img/no_image.png"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="COMPARE">
		<span id="compare_list_a_{$p/@id}">
			<a href="{$p/to_compare}" class="add__compare" ajax="true" ajax-loader-id="compare_list_a_{$p/@id}">
				<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
					 xmlns="http://www.w3.org/2000/svg">
					<path d="M18 20V10" stroke-width="2" stroke-linecap="round"
						  stroke-linejoin="round"/>
					<path d="M12 20V4" stroke-width="2" stroke-linecap="round"
						  stroke-linejoin="round"/>
					<path d="M6 20V14" stroke-width="2" stroke-linecap="round"
						  stroke-linejoin="round"/>
				</svg>
			</a>
		</span>
	</xsl:template>

	<xsl:template name="FAV">
		<span id="fav_list_a_{$p/@id}">
			<a href="{$p/to_fav}" class="add__favourite active"  ajax="true" ajax-loader-id="fav_list_a_{$p/@id}">
				<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
					<path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				</svg>
			</a>
		</span>
	</xsl:template>

	<xsl:template name="CART">
		<span id="cart_list_a_{$p/@id}">
			<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_a_{$p/@id}" style="display: inline;">
				<input type="number" style="width: 50px; margin-right: 7px;" class="text-input" name="qty"
					   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/min_qty) then $p/min_qty else 0.1}" />
				<xsl:if test="$has_price">
					<input type="submit" class="button button_primary" style="{if(f:num($p/qty) != 0) then '' else 'background-color: #707070; border-color: #707070;'}" value="{if($p/qty and $p/qty != '0') then $to_cart_available_label else $to_cart_na_label}"/>
				</xsl:if>
				<xsl:if test="not($has_price)">
					<input type="submit" class="button button_primary" style="background-color: #707070; border-color: #707070;" value="{$to_cart_na_label}"/>
				</xsl:if>
			</form>
		</span>
	</xsl:template>

	<xsl:function name="f:tab_name">
		<xsl:param name="name"/>
		<xsl:value-of select="if ($name = 'tech') then 'Характеристики' else if ($name = 'package') then 'Комплектация' else $name"/>
	</xsl:function>


</xsl:stylesheet>