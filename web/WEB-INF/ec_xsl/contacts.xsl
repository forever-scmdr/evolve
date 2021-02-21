<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>

	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Contact Us'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="active_menu_item" select="'contacts'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Home Page</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>

		<div class="page-content m-t">
			
			<xsl:call-template name="FEEDBACK_FORM"></xsl:call-template>

			<!-- <form action="" class="feedback-form">
				<div class="feedback-form__item">
					<div class="feedback-form__label">Тitle<span>*</span></div>
					<div class="feedback-form__element">
						<select name="" id="">
							<option value="">Mr.</option>
							<option value="">Mrs.</option>
							<option value="">Ms.</option>
							<option value="">Dr.</option>
						</select>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Name<span>*</span></div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Surname<span>*</span></div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Email<span>*</span></div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Phone Number</div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Nationality</div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Country of Stay</div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Subject<span>*</span></div>
					<div class="feedback-form__element">
						<input type="text"/>
					</div>
				</div>
				<div class="feedback-form__item">
					<div class="feedback-form__label">Message<span>*</span></div>
					<div class="feedback-form__element">
						<textarea name="" id="" cols="30" rows="10"></textarea>
					</div>
				</div>
			</form> -->

			<h1 class="page-title">Contacts</h1>

			<xsl:value-of select="page/contacts/text" disable-output-escaping="yes"/>
			<!-- <h3>Расположение нашего офиса на карте</h3> -->
			<div class="map-container">
				<xsl:value-of select="page/contacts/map" disable-output-escaping="yes"/>
			</div>
			<xsl:value-of select="page/contacts/bottom_text" disable-output-escaping="yes"/>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/></xsl:template>

		<xsl:template name="INC_SIDE_MENU_INTERNAL"></xsl:template>

</xsl:stylesheet>
