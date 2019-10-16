<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	xmlns:xs="http://www.w3.org/2001/XMLSchema"	xmlns="http://www.w3.org/1999/xhtml"	xmlns:f="f:f"	version="2.0">	<xsl:import href="common_page_base.xsl"/>	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>	<xsl:strip-space elements="*"/>	<xsl:template name="CONTENT">		<style>			table.compare td {				vertical-align: top;			}			table.compare td > strong {				display: block;				font-size: .8em;				margin-bottom: 1rem;				background-color: #eee;				padding: .5rem;			}			table.compare td > span {				font-size: .8em;				display: block;			}			table.compare td > span.param-name {				color: gray;			}			table.compare td > span.param-value {				margin-bottom: 1rem;			}			table.compare{				width: auto;				min-width: 0%;			}		</style>		<div class="path-container">			<div class="path-container">				<div class="path">					<a href="{page/index_link}">Главная страница</a> >				</div>				<xsl:call-template name="PRINT"/>			</div>		</div>		<h1>Сравнение</h1>		<div class="page-content m-t">			<div class="table-responsive">				<table class="compare">					<tr class="catalog-items">						<xsl:for-each select="page/product">							<td>								<xsl:apply-templates select="."/>							</td>						</xsl:for-each>						<!-- <xsl:apply-templates select="page/product" mode="item"/> -->					</tr>										<xsl:variable name="product_params" select="page/product/params"/>					<xsl:for-each-group select="page/product/params/param" group-by="@caption">						<xsl:variable name="cgc" select="current-grouping-key()" />						<tr>							<xsl:for-each select="$product_params">								<xsl:variable name="params" select="current()"/>								<xsl:variable name="p" select="$params/param[@caption = $cgc]"/>								<td>									<span class="param-name"><xsl:value-of select="$cgc"/></span>									<span class="param-value" style="{'opacity: .35'[not($p != '')]}">										<xsl:value-of select="if($p != '') then $p else 'нет данных'"/>									</span>								</td>							</xsl:for-each>							<!-- <xsl:for-each-group  select="page/product" group-by="@id">								<xsl:apply-templates select="current-group()[1]/params[1]" />							</xsl:for-each-group> -->							<!-- <xsl:apply-templates select="page/product/params[1]" /> -->						</tr>					</xsl:for-each-group>				</table>			</div>		</div>		<script type="text/javascript">			$(document).ready(function(){				var l = $("table.compare tr:eq(0) td").length;				var pcnt = 100/l;				$("table.compare tr:eq(0) td").css({width : pcnt+"%"});			});		</script>	</xsl:template>	<xsl:template match="params">		<td>			<xsl:for-each select="param">				<span class="param-name"><xsl:value-of select="@caption"/></span>				<span class="param-value">					<xsl:value-of select="."/>				</span>			</xsl:for-each>		</td>	</xsl:template>	<xsl:template match="product" mode="item">		<xsl:variable name="has_price" select="price and price != '0'"/>		<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>					<td>				<div class="catalog-item">				<!--				<div class="tags">					<span>Акция</span>				</div>				-->				<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>				<a href="{show_product}" class="image-container" style="background-image: url({$pic_path});">					<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->				</a>				<div class="name">					<a href="{show_product}" title="{name}"><xsl:value-of select="name"/></a>					<p><xsl:value-of select="substring-before(substring-after(short, 'description&quot;&gt;'), '&lt;')" disable-output-escaping="yes"/></p>				</div>				<div class="art-number">					<p>арт. <xsl:value-of select="code"/></p>				</div>				<div class="price">					<xsl:if test="$has_price">						<!--<p><span>Старая цена</span>100 р.</p>-->						<p><!--<span>Новая цена</span>--><xsl:value-of select="price"/></p>					</xsl:if>					<xsl:if test="not($has_price)">						<p><span>&#160;</span>&#160;</p>						<p><span>&#160;</span>&#160;</p>					</xsl:if>				</div>				<div class="order">					<div id="cart_list_{code}" class="product_purchase_container">						<form action="{to_cart}" method="post">							<xsl:if test="$has_price">								<input type="number" name="qty" value="1" min="0"/>								<input type="submit" value="В корзину"/>							</xsl:if>							<xsl:if test="not($has_price)">								<input type="number" name="qty" value="1" min="0"/>								<input type="submit" class="not_available" value="Под заказ"/>							</xsl:if>						</form>					</div>					<xsl:choose>						<xsl:when test="qty and qty != '0'"><div class="quantity">Осталось <xsl:value-of select="qty"/> шт.</div></xsl:when>						<xsl:otherwise><div class="quantity">Нет на складе</div></xsl:otherwise>					</xsl:choose>										<div class="links">						<div>							<span class="active"><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>						</div>						<div id="fav_list_{code}">							<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i><!-- в избранное --></a></span>						</div>					</div>				</div>			</div>		</td>		</xsl:template></xsl:stylesheet>