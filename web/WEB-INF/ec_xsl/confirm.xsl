<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="rooms" select="page/rooms"/>
	<xsl:variable name="order" select="page/order"/>
	
	<xsl:variable name="main_form" select="$order/order_form[is_contractor = '1']"/>
	<xsl:variable name="kid_form" select="$order/order_form[person_type = 'Ребенок']"/>
	<xsl:variable name="adult_form" select="$order/order_form[person_type = 'Взрослый' and @id != $main_form/@id]"/>

	<xsl:template match="order_form">
		<div class="selected">
			<p><strong><xsl:value-of select="first_name"/><xsl:text> </xsl:text><xsl:value-of select="second_name"/><xsl:text> </xsl:text><xsl:value-of select="last_name"/></strong></p>
			<p>Гражданство: <xsl:value-of select="$order/citizen_name"/></p>
			<p>Дата рождения: <xsl:value-of select="birth_date"/></p>
			<p>Паспорт: <xsl:value-of select="passport"/></p>
			<p>Выдан: <xsl:value-of select="passport_issued"/><xsl:text> </xsl:text><xsl:value-of select="passport_issued_date"/></p>
			<p>Личный номер: <xsl:value-of select="id"/></p>
			<p>Адрес: <xsl:value-of select="address"/></p>
			<p>Тип путевки: <xsl:value-of select="voucher_type"/></p>
		</div>
	</xsl:template>
	

	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12 col-sm-8 col-md-9">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Заявка принята</h2>
		<p>После проверки администратором на Вашу электронную почту (<strong><xsl:value-of select="$main_form/email"/></strong>) будет выслано письмо, содержащее:</p>
		<ol>
			<li>ссылку для онлайн-оплаты; </li>
			<li>договор; </li>
			<li>счет-фактуру для оплаты; </li>
			<li>срок оплаты.</li>
		</ol>
		<p>Если у администратора будут вопросы, он свяжется с Вами по тел.: <xsl:value-of select="$main_form/phone"/>.</p>
		<h3>Детали заявки</h3>
		<xsl:for-each select="$order/free_room">
			<p>
				<strong><xsl:value-of select="type_name"/>, 
				<xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> - <xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/>.
				</strong>
			</p>
			<p>
				Основные места: 
				<xsl:variable name="base_forms" select="$order//order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')]"/>
				<xsl:for-each select="$base_forms">
					<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
					<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
				</xsl:for-each>
			</p>
			<p>
				<xsl:variable name="extra_forms" select="$order//order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')]"/>
				Дополнительные места: 
				<xsl:for-each select="$extra_forms">
					<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
					<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
				</xsl:for-each>
				<xsl:if test="not($extra_forms)">нет.</xsl:if>
			</p>
		</xsl:for-each>
		<h3>Данные для заключения договора</h3>
		<xsl:apply-templates select="$main_form"/>
		<xsl:if test="$adult_form or $kid_form">
			<xsl:choose>
				<xsl:when test="$main_form/pay_only = '1'"><h3>Отдыхающие</h3></xsl:when>
				<xsl:otherwise><h3>Сопровождающие</h3></xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="$adult_form"/>
			<xsl:apply-templates select="$kid_form"/>
		</xsl:if>
		<h3>К оплате <xsl:value-of select="$order/sum"/>&#160;<xsl:value-of select="$order/cur"/></h3>
	</div>
	</xsl:template>

</xsl:stylesheet>