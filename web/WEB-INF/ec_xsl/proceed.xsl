<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление заказа'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="page/user_jur"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<a href="{page/cart_link}">Изменить заказ</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Анкета покупателя</h1>

		<div class="page-content m-t">
			<xsl:if test="$message">
				<div class="alert alert-danger">
					<h4>Ошибка</h4>
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="{'active'[not($is_jur)]}"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>
				<li role="presentation" class="{'active'[$is_jur]}"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">


				<div role="tabpanel" class="tab-pane{' active'[not($is_jur)]}" id="tab_phys">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<xsl:variable name="inp" select="if (page/user_phys) then page/user_phys/input else page/phys_form/input"/>
						<xsl:call-template name="USER_PHYS_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
						</xsl:call-template>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<!--
						<div class="checkbox">
							<label>
								<input type="checkbox" value=""/> зарегистрироваться на сайте
							</label>
						</div>
						-->

						<!--<div class="checkbox">-->
							<!--<label>-->
								<!--<input type="checkbox" name="{$inp/payment/@input}" value="erip"/> Оплата через ЕРИП-->
							<!--</label>-->
						<!--</div>-->

						<input type="submit" value="Отправить заявку"/>
					</form>
				</div>


				<div role="tabpanel" class="tab-pane{' active'[$is_jur]}" id="tab_jur">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="if (page/user_jur) then page/user_jur/input else page/jur_form/input"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
						</xsl:call-template>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<!--
						<div class="checkbox">
							<label>
								<input type="checkbox" value=""/> зарегистрироваться на сайте
							</label>
						</div>
						-->
						<!--<div class="checkbox">-->
							<!--<label>-->
								<!--<input type="checkbox" name="{$inp/payment/@input}" value="erip"/>Оплата через ЕРИП</label>-->
						<!--</div>-->

						<input type="submit" value="Отправить заказ"/>
					</form>
				</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>