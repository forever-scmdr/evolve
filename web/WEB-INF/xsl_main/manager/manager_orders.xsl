<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:import href="../common_page_base.xsl"/>
<!--	<xsl:import href="snippets/custom_blocks.xsl"/>-->
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template name="q_mark"><path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"></path></xsl:template>


	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="top-info">
			<div class="container">
				<div class="top-info__wrap wrap" style="display: flex">
					<div class="top-info__content">
						<a href="">Заказы</a>
						<a href="">Список клиентов</a>
						<a href="">Лог оператора</a>
						<p>&#160;</p>
					</div>
					<div id="personal_desktop_login" ajax-href="{//page/personal_ajax_link}" ajax-show-loader="no">
						<a href="{page/login_link}" class="icon-link">
							<div class="icon">
								<img src="img/icon-lock.svg" alt="" />
							</div>
							<span class="icon-link__item">Вход / Регистрация</span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="MAIN_CONTENT" >
		<div class="filter filter_section">
			<a href="#" onclick="$('.filter_extra').toggle();$('#filters_container').slideToggle(200);return false;" class="icon-link filter__button button">
				<div class="icon"><img src="img/icon-gear.svg" alt=""/></div><span class="icon-link__item filter_extra" style="">Показать список клиентов</span><span class="icon-link__item filter_extra" style="display: none;">Скрыть подбор по клиентам</span></a>

			<input class="input header-search__input" type="text" placeholder="ИНН клиента" value="" style="margin-left: 40px;"/>
			<input class="input header-search__input" type="text" placeholder="Название компании" value="" autofocus=""/>
			<input class="input header-search__input" type="text" placeholder="Номер заказа" value="" autofocus=""/>
			<button class="button header-search__button" type="submit">Найти</button>
			<button class="button header-search__button" type="submit" style="float: right;">Обновить страницу</button>

			<form method="post">
				<div class="" style="display: none;" id="filters_container">
					<div class="filter__item active checkgroup">
						<div class="filter__title">Клиенты</div>
						<div style="display: flex;">
							<xsl:variable name="third_size" select="ceiling(count(page/user_jur) div 3)"/>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &lt;= xs:integer($third_size)]"/>
							</div>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &gt; xs:integer($third_size) and position() &lt;= $third_size * 2]"/>
							</div>
							<div style="flex: 1;">
								<xsl:apply-templates select="page/user_jur[position() &gt; $third_size * 2]"/>
							</div>
						</div>
					</div>
					<div class="filter__actions"><button class="button button_2" type="submit">Показать результат</button><button class="button button_2" onclick="location.href = '/soldering_desoldering_rework_stations/?show_filter=yes'; return false;">Сбросить</button></div>
				</div>
			</form>
		</div>
	</xsl:template>


	<xsl:template match="user_jur">
		<div class="filter__value">
			<label>
				<input name="val_{@id}" type="checkbox" value="{organization}"/>&#160;<xsl:value-of select="organization"/>
				<!-- Вызов информации по клиенту начало -->
				<svg onclick="infobox('{@id}')" class="infobox" focusable="false" aria-hidden="true" viewBox="0 0 24 24" data-testid="help-popup-trigger-icon">
					<xsl:call-template name="q_mark"/>
				</svg>
				<!-- Вызов информации по клиенту конец -->
				<div class="infobox_modal infobox_{@id}">
					<div class="text"><a class="popup__close" onclick="infobox('close');">X</a>
						<div>
							<p><strong>Анкета заказчика</strong></p>
							<p><strong>E-mail/логин:</strong> <a href="mailto:{email}"><xsl:value-of select="email"/></a></p>
							<p><strong>ИНН:</strong> <xsl:value-of select="inn"/></p>
							<p><strong>Наименование организации:</strong> <xsl:value-of select="organization"/></p>
							<p><strong>КПП:</strong> <xsl:value-of select="kpp"/></p>
							<p><strong>Адрес:</strong> <xsl:value-of select="address"/></p>
							<p><strong>E-mail организации:</strong> <a href="mailto:{corp_email}"><xsl:value-of select="corp_email"/></a></p>
							<p><strong>Телефон/факс:</strong> <xsl:value-of select="phone"/></p>
							<p><strong>Руководитель:</strong> <xsl:value-of select="boss"/></p>
							<p><strong>Должность руководителя:</strong> <xsl:value-of select="boss_position"/></p>
						</div>
					</div>
				</div>
			</label>
		</div>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
