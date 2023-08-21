<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns:f="f:f"		version="2.0">	<xsl:import href="utils/utils.xsl"/>	<xsl:import href="snippets/constants.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="message" select="page/variables/message"/>	<xsl:variable name="prod_id" select="page/variables/product"/>	<xsl:variable name="bought_id" select="page/variables/bought"/>	<xsl:variable name="bought" select="if(page/variables/bought != '') then page/cart/bought[@id=$bought_id] else page/cart/bought[product/@id = $prod_id]"/>	<xsl:template match="/">		<div class="result" id="complect_ajax-{$prod_id}">			<script>insertAjax('cart_ajax')</script>			<div class="device-basic complectations">				<div class="complectation device-basic__column">					<h3>Сохраненные комплектации</h3>					<div style="margin-bottom:20px;"></div>					<xsl:variable name="need_toggler" select="count($bought) &gt; 1"/>					<xsl:for-each select="$bought">						<div class="bought-container">							<xsl:if test="$need_toggler">								<h3 class="toggle-header">									<span id="header-{@id}">										<span  class="toggler-name"><xsl:value-of select="if(complecatation_name != '') then complecatation_name else 'Без названия'"/></span>										&#160;<span class="toggler-sum">Сумма: <xsl:value-of select="sum"/></span>									</span>									<a class="toggler" data-text="-" onclick="									$('#header-{@id}').toggle();									$('#content-{@id}').toggle();									t = $(this).text();									$(this).text($(this).attr('data-text'));									$(this).attr('data-text',t);									">+</a>								</h3>							</xsl:if>							<div id="content-{@id}" style="{'display:none;'[$need_toggler]}">								<xsl:variable name="options" select="product/option"/>								<xsl:variable name="need_qty" select="$options[f:num(max) &gt; 1]"/>								<xsl:variable name="selected_options" select="option"/>								<form method="post" action="{update_complect_link}" ajax="true">									<input type="hidden" name="specific" value="{if(page/variables/bought != '') then 'yes' else 'no'}"/>									<input type="hidden" name="specific" value="{if(page/variables/bought != '') then 'yes' else 'no'}"/>									<table style="margin-bottom:14px;">										<tr style="border-bottom: 0 none; padding-bottom: 14px;">											<td colspan="{if($need_qty) then 5 else 4}" style="padding-left:0; padding-top:0; ">												<input type="text" name="complecatation_name" value="{complecatation_name}" placeholder="Название комплектации" class="input compl-name" style="namewidth:200px; font-size: 16px;"/>											</td>										</tr>										<tr>											<th></th>											<th>Код</th>											<th>Наименование</th>											<xsl:if test="$need_qty">												<th>Кол-во</th>											</xsl:if>											<th>Цена</th>										</tr>										<xsl:for-each select="$options">											<xsl:variable name="inp_type" select="if(group != '') then 'radio' else 'checkbox'"/>											<xsl:variable name="selected" select="$selected_options[@key = current()/@id]"/>											<xsl:variable name="qty" select="if($selected != '') then $selected/@value else 1"/>											<tr class="{if(mandatory='1') then 'mandatory'else ''}">												<td>													<xsl:if test="not($selected)">														<input id="inp-{@id}" type="{$inp_type}" name="option" value="{@id}"/>													</xsl:if>													<xsl:if test="$selected">														<input id="inp-{@id}" type="{$inp_type}" name="option" value="{@id}" checked="checked"/>													</xsl:if>												</td>												<td>													<label for="inp-{@id}">														<b><xsl:value-of select="code"/></b>													</label>												</td>												<td>													<label for="inp-{@id}">														<xsl:value-of select="name"/>													</label>												</td>												<xsl:if test="$need_qty">													<td>														<xsl:if test="f:num(max) &gt; 1">															<input class="input_type_number" type="number" value="{$qty}" min="1" name="qty-{@id}" max="{max}"/>														</xsl:if>													</td>												</xsl:if>												<td>													<xsl:if test="f:num(price_opt) &gt; 0">														<b><xsl:value-of select="price_opt"/></b><br/>													</xsl:if>													<xsl:value-of select="price"/>												</td>											</tr>										</xsl:for-each>										<tr>											<td colspan="{if($need_qty) then 5 else 4}" style="padding-left:0;">												Сумма: <xsl:value-of select="f:currency_decimal(sum)"/> EUR											</td>										</tr>									</table>									<xsl:if test="$bought_id != ''">										<input type="hidden" name="result" value="cart"/>									</xsl:if>									<input type="submit" class="button button_gray" value="Сохранить изменения" />									<span style="padding-left: 1.5rem;"></span>									<xsl:if test="not($bought_id != '')">										<a class="button delete" href="{delete_link}" ajax="true">Удалить</a>									</xsl:if>									<xsl:if test="$bought_id != ''">										<input type="submit" class="button" value="Создать новую" onclick="$(this).closest('form').attr('action', '{create_complect_link}');" />									</xsl:if>								</form>							</div>						</div>					</xsl:for-each>				</div>			</div>		</div>	</xsl:template></xsl:stylesheet>