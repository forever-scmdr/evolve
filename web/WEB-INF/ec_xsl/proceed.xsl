<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template name="CONTENT">
		<xsl:call-template name="INC_MOBILE_HEADER"/>
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<a href="{page/cart_link}">Изменить заказ</a> &gt;
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1>Анкета покупателя</h1>

		<div class="page-content m-t">
			<h4>Описание продукта</h4>
			<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aperiam, earum cum modi labore fugiat veniam sapiente corporis est omnis debitis sit veritatis libero vero obcaecati amet, dicta quos, eos odit.</p>
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>
				<li role="presentation"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane active" id="tab_phys">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="itemform/cart_action/?action=post_phys&amp;-ti-=7&amp;-ip-=0&amp;-ii-=0&amp;-fi-=proceed_contacts_form" method="post">
						<div class="form-group">
							<label for="">Ваше имя:</label>
							<input type="text" class="form-control" id=""/>
						</div>
						<div class="form-group">
							<label for="">Адрес:</label>
							<input type="text" class="form-control" id=""/>
						</div>
						<div class="form-group">
							<label for="">Способ доставки <a href="">Подробнее об условиях доставки</a></label>
							<select class="form-control">
								<option>1</option>
								<option>2</option>
								<option>3</option>
								<option>4</option>
								<option>5</option>
							</select>
						</div>
						<div class="form-group">
							<label for="">Телефон:</label>
							<input type="text" class="form-control" id=""/>
						</div>
						<div class="form-group">
							<label for="">Электронная почта:</label>
							<input type="text" class="form-control" id=""/>
						</div>
						<div class="form-group">
							<label for="">Комментарий:</label>
							<textarea class="form-control" rows="3"></textarea>
						</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" value=""/> зарегистрироваться на сайте
							</label>
						</div>




						<input type="submit" name="" value="Отправить заказ"/>
					</form>
				</div>
				<div role="tabpanel" class="tab-pane" id="tab2">...</div>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>