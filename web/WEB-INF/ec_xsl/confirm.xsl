<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl xs f">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="utils_inc.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'news'"/>

	<xsl:variable name="rooms" select="page/rooms"/>
	<xsl:variable name="order" select="page/order"/>
	
	<xsl:variable name="main_form" select="$order/order_form[is_contractor = '1']"/>
	<xsl:variable name="kid_form" select="$order/order_form[person_type = 'Ребенок']"/>
	<xsl:variable name="adult_form" select="$order/order_form[person_type = 'Взрослый' and @id != $main_form/@id]"/>

	<xsl:template match="order_form">
		<div class="selected">
			<p>
				<b><xsl:value-of select="first_name"/><xsl:text> </xsl:text><xsl:value-of select="second_name"/><xsl:text> </xsl:text><xsl:value-of select="last_name"/></b>
				<br/>Гражданство: <xsl:value-of select="$order/citizen_name"/>
				<br/>Дата рождения: <xsl:value-of select="birth_date"/>
				<br/>Паспорт: <xsl:value-of select="passport"/>
				<br/>Выдан: <xsl:value-of select="passport_issued"/><xsl:text> </xsl:text><xsl:value-of select="passport_issued_date"/>
				<br/>Личный номер: <xsl:value-of select="id"/>
				<br/>Адрес: <xsl:value-of select="address"/>
				<br/>Тип путевки: <xsl:value-of select="voucher_type"/>
			</p>
		</div>
	</xsl:template>
	

	<xsl:template name="CONTENT">
	<div class="common">
		<div style="width: 450px; display: inline-block; vertical-align: top;" >
			<h1>Заявка принята</h1>
			<p>После проверки администрантром на Вашу электронную почту (<b><xsl:value-of select="$main_form/email"/></b>) будет выслано письмо, содержащее:
				<br/>1. ссылку для ондайн-оплаты;
				<br/>2. договор;
				<br/>3. счет-фактуру для оплаты;
				<br/>4. срок оплаты.
			</p>
			<p>Если у администратора будут вопросы, он свяжется с Вами по тел.: +375 29 123-45-67. </p>
			<h2>Детали заявки</h2>
			<xsl:for-each select="$order/free_room">
				<div class="selected">
					<p>
						<b><xsl:value-of select="type_name"/></b>, 
						<xsl:value-of select="f:day_month_year(f:millis_to_date(from))"/> - <xsl:value-of select="f:day_month_year(f:millis_to_date(to))"/>.
						<br/>
						Основные места: 
						<xsl:variable name="base_forms" select="$order//order_form[@id = current()/order_form_base and person_type = ('Взрослый', 'Ребенок')]"/>
						<xsl:for-each select="$base_forms">
							<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
							<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
						</xsl:for-each>
						<br/>
						<xsl:variable name="extra_forms" select="$order//order_form[@id = current()/order_form_extra and person_type = ('Взрослый', 'Ребенок')]"/>
						Дополнительные места: 
						<xsl:for-each select="$extra_forms">
							<xsl:value-of select="position()"/>) <xsl:value-of select="person_type"/>, <xsl:value-of select="voucher_type"/>
							<xsl:value-of select="if (position() = last()) then '. ' else ', '"/> 
						</xsl:for-each>
						<xsl:if test="not($extra_forms)">нет.</xsl:if>
					</p>
				</div>
			</xsl:for-each>
			<h2>Данные для заключения договора</h2>
			<xsl:apply-templates select="$main_form"/>
			<xsl:if test="$adult_form or $kid_form">
				<xsl:choose>
					<xsl:when test="$main_form/pay_only = '1'"><h3>Отдыхающие</h3></xsl:when>
					<xsl:otherwise><h3>Сопровождающие</h3></xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="$adult_form"/>
				<xsl:apply-templates select="$kid_form"/>
			</xsl:if>
			<h2>К оплате: <xsl:value-of select="$order/sum"/>&#160;<xsl:value-of select="$order/cur"/></h2>
		</div>
	</div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script><!-- 
		$(document).ready(function() {
			<xsl:if test="$adult &gt; 0">
				$('#adult_sel').val('<xsl:value-of select="$adult"/>');
				$('#infant_sel').val('<xsl:value-of select="$infant"/>');
			</xsl:if>
			<xsl:if test="$citizen_var or $citizen_var != ''">
				$('#citizen_var').val('<xsl:value-of select="$citizen_var"/>');
			</xsl:if>
		});
		 -->
		<xsl:call-template name="SELECT_SCRIPT"/>
		
		function submitOrder() {
			$('.birth-date').each(function() {
				var birth = $(this);
				var date = birth.find('.days').val() + '.' + birth.find('.months').val() + '.' + birth.find('.years').val();
				birth.find('input').val(date);
			});
			$('#order_forms').submit();			
		}
	</script>
	</xsl:template>

</xsl:stylesheet>