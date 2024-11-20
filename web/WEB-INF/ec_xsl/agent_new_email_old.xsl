<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY rarr "&#x02192;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">

	<xsl:variable name="text" select="page/email_post" />
	<xsl:variable name="base" select="page/base" />

	<xsl:template match="/">
	<body style="background-color: #E6E6E6; margin: 0;">
		<div style="display:none;font-size:1px;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;mso-hide:all;font-family: sans-serif;">
			<topic><xsl:value-of select="$text/topic"/></topic>
		</div>
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="center" style="background-color: #E6E6E6;">
					<div style="max-width: 600px; width: 600px; margin: auto; background-color: #fff; font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 13px;">
						<!-- <table style="background-color: #e6e6e6;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="300" valign="top" style="width: 300px; padding: 5px 15px; font-size: 12px;">
									<p style="text-align: center;">
										+375 (162) 53-64-70; <a style="color: black" href="sproject@termobrest.ru">sproject@termobrest.ru</a>
									</p>
									
									
								</td>
							</tr>
						</table> -->
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td style="padding: 0; font-size: 0;">
									<img src="img/header_2.jpg" alt=""/>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<h2 style="text-align: center; text-transform: uppercase; margin-top: 20px;">Уважаемые партнёры!</h2>
									<p style="text-align: justify; font-size: 18px; font-weight: bold; line-height: 1.4em; margin-bottom: 30px; max-width: 570px; margin-left: auto; margin-right: auto;">СП «ТермоБрест» совместно с официальным дилером ООО «Ермак Газ» примут участие в специализированной выставке «Нефть и газ. Топливно-энергетический комплекс», которая состоится 19 - 22 сентября в г. Тюмени, РФ.</p>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="300" style="width: 300px; vertical-align: top; padding: 0 15px;" cellpadding="0" cellspacing="0">
									<p style="margin-top: 0; font-size: 17px; line-height: 1.5;"><strong>Приглашаем посетить наш стенд № 50</strong> в выставочном зале «Тюменская ярмарка» по адресу г. Тюмень, ул. Севастопольская, 12.
									<br/><br/>
									</p>
									<p style="text-align: justify; font-size: 18px; font-weight: bold; line-height: 1.4em; margin-bottom: 30px; max-width: 570px; margin-left: auto; margin-right: auto;">СП «ТермоБрест» представит на выставке весь спектр производимой продукции – <a href="http://termobrest.ru/catalog/">запорно-регулирующей газовой арматуры и приборов автоматики безопасности газовых систем, включая новинки:</a></p>									
								</td>
								<td width="300" style="width: 300px; vertical-align: top; padding: 0 15px;" cellpadding="0" cellspacing="0">
									<img src="img/banner.jpg" alt="" style="width: 270px;" width="270"/>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_1.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/klapany_elektromagnitnye/?tag=%D0%94%D0%BE%D0%BF%D0%BE%D0%BB%D0%BD%D0%B8%D1%82%D0%B5%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5%20%D1%83%D1%81%D1%82%D1%80%D0%BE%D0%B9%D1%81%D1%82%D0%B2%D0%B0%20%D0%B8%20%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5%20%D0%BA%D0%BE%D1%80%D0%BF%D1%83%D1%81%D0%B0:%20%D1%83%D0%B3%D0%BB%D0%BE%D0%B2%D0%BE%D0%B9%20%D0%BA%D0%BE%D1%80%D0%BF%D1%83%D1%81">угловые электромагнитные клапаны;</a>
									</h3>
								</td>
							</tr>
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_2.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/filtry/">фильтры газовые с максимальным рабочим давлением до 16 бар;</a>
									</h3>
								</td>
							</tr>
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_3.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/filtry/">фильтры газовые с электронными индикаторами загрязненности фильтроэлемента;</a>
									</h3>
								</td>
							</tr>
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_4.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/regulyatory_stabilizatory_davleniya/alyuminievyi_korpus873/">регуляторы нулевого давления и соотношения газ/воздух;</a>
									</h3>
								</td>
							</tr>
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_5.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/datchiki_rele_davleniya/">электронные датчики-реле давления;</a>
									</h3>
								</td>
							</tr>
							<tr>
								<td width="100" style="width: 100px; vertical-align: top; padding: 0 15px;">
									<img src="img/s_dev_6.jpg" alt="" style="width: 100px;" width="100"/>
								</td>
								<td width="500" style="width: 500px; vertical-align: middle; padding: 0 15px;">
									<h3>
										<a href="http://termobrest.ru/sec/smesiteli_gazov/">смесители газов.</a>
									</h3>
								</td>
							</tr>
						</table>						
						<table style="background-color: #fff; margin-top: 20px;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<p style="text-align: justify; font-size: 16px; font-weight: normal; line-height: 1.4em; margin-bottom: 30px; max-width: 570px; margin-left: auto; margin-right: auto;">
									Посетители стенда ТЕРМОБРЕСТ смогут получить информационные материалы, а также задать интересующие вопросы специалистам СП «ТермоБрест» и ООО «Ермак Газ».
									<br/><br/>
									</p>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td style="padding: 0 15px;">
									<p style="width: 570px; margin-right: auto; margin-left: auto; margin-top: 20px; text-align: justify; font-size: 16px; line-height: 1.4;"><strong>Основные задачи выставки в Тюмени</strong> – содействие развитию предприятий топливно-энергетического комплекса, демонстрация современного оборудования и технологий для нефтегазовой промышленности, расширение взаимовыгодного научно-технического сотрудничества и установление деловых связей с российскими и иностранными партнерами, направленное на дальнейшее развитие нефтегазовой отрасли. Ежегодно выставка собирает более 200 экспонентов и сопровождается насыщенной деловой программой.</p>
									
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<p style="text-align: justify; font-size: 16px; font-weight: normal; line-height: 1.4em; margin-bottom: 30px; max-width: 570px; margin-left: auto; margin-right: auto;">
									Узнать подробнее о мероприятии, а также получить приглашение можно перейдя по ссылке <a href="http://expo72.ru/vistavki/2017/2902/">http://expo72.ru/vistavki/2017/2902/</a></p>
								</td>
							</tr>
						</table>
						<!-- <table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<h2 style="text-align: center; text-transform: uppercase; margin-top: 70px; margin-bottom: 30px;">Преимущества работы с нами</h2>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="300" style="width: 300px; padding: 30px 15px 0;">
									<ul>
										<li>Собственная современная производственная база и штат сотрудников высокой квалификации.</li>
										<li>Многолетний опыт внедрения передовых технологий в области арматуростроения.</li>
									</ul>
								</td>
								<td width="300" style="width: 300px;">
									<img src="img/img_1.jpg" alt=""/>
								</td>
							</tr>
							<tr>
								<td width="300" style="width: 300px;">
									<img src="img/img_2.jpg" alt=""/>
								</td>
								<td width="300" style="width: 300px; padding: 0 15px;">
									<ul>
										<li>Разветвленная сеть дилеров в СНГ, ЕС и Китае.</li>
										<li>Обширная география продаж: продукция предприятия реализуется на всей территории Евразии от Норильска до Ханоя, от Южно-Сахалинска до Дюссельдорфа.</li>
									</ul>
								</td>
							</tr>
							<tr>
								<td width="300" style="width: 300px; padding: 0 15px;">
									<ul>
										<li>Сроки поставки партии продукции любой сложности и комплектации - не более 10 дней.</li>
										<li>Политика единых цен.</li>
									</ul>
								</td>
								<td width="300" style="width: 300px;">
									<img src="img/img_3.jpg" alt=""/>
								</td>
							</tr>
							<tr>
								<td width="300" style="width: 300px;">
									<img src="img/img_4.jpg" alt=""/>
								</td>
								<td width="300" style="width: 300px; padding: 0 15px;">
									<ul>
										<li>Широкий диапазон климатических исполнений арматуры марки ТЕРМОБРЕСТ делает возможным ее применение во всех климатических поясах.</li>
									</ul>
								</td>
							</tr>
							<tr>
								<td width="300" style="width: 300px; padding: 0 15px;">
									<ul>
										<li>Вся продукция предприятия сертифицирована в системах <img src="img/sertificates.jpg" alt="" />.</li>
									</ul>
								</td>
								<td width="300" style="width: 300px;">
									<img src="img/img_5.jpg" alt=""/>
								</td>
							</tr>
							<tr>
								<td width="300" style="width: 300px;">
									<img src="img/img_6.jpg" alt=""/>
								</td>
								<td width="300" style="width: 300px; padding: 0 15px;">
									<ul>
										<li>Многие годы качеству марки ТЕРМОБРЕСТ доверяют ведущие предприятия нефтегазовой отрасли и теплоэнергетики, такие как: ГАЗПРОМ, ЛУКОЙЛ, РОСНЕФТЬ, СУРГУТНЕФТЕГАЗ, ТГК России и др.</li>
									</ul>
								</td>
							</tr>
						</table>
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<h2 style="text-align: center; text-transform: uppercase; margin-top: 20px;">География поставок продукции</h2>
									<img src="img/map.jpg" alt=""/>
								</td>
							</tr>
						</table> -->
						<!-- <table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td>
									<h2 style="text-align: center; text-transform: uppercase; margin-top: 20px; margin-bottom: 20px;">Сайт предприятия <a style="color: #009792;" href="http://termobrest.ru">www.termobrest.ru</a></h2>
								</td>
							</tr>
						</table> -->
						
						<table cellspacing="0" cellpadding="0" width="600" style="background-color: #2F2F2F; color: #fff;">
							<tr>
								<td style=" padding: 20px 15px;"><h4 style="margin-top: 0;">СП «ТермоБрест» ООО</h4></td>
							</tr>
						</table>
						<table cellspacing="0" cellpadding="0" width="600" style="background-color: #2F2F2F; color: #939393;">
							<tr>
								<td width="400" style="width: 400px; vertical-align: top; padding: 0 15px 30px;">224014 Республика Беларусь, г.&nbsp;Брест, ул.&nbsp;Писателя Смирнова, 168</td>
								<td width="200" style="width: 200px; vertical-align: top; padding: 0 15px; text-align: right;">
									<a href="http://termobrest.by"><img src="img/icon_site.jpg" alt=""/></a>
									<a href="https://www.youtube.com/channel/UC0gvt4On84SIDHkMO4Y85cA"><img src="img/icon_yt.jpg" alt=""/></a>
									<a href="https://www.facebook.com/termobrestvalves/"><img src="img/icon_fb.jpg" alt=""/></a>
								</td>
							</tr>
						</table>
						<table style="background-color: #e6e6e6;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px;">
									<p><strong>Приемная&nbsp;ТЕРМОБРЕСТ:</strong></p>
									<p>
										+375 (162) 53-63-90;
									</p>
									<p><a style="color: black" href="info@termobrest.ru">info@termobrest.ru</a></p>
									
								</td>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px;">
									<p><strong>Отдел маркетинга:</strong></p>
									<p>
										+375 (162) 53-64-70;
									</p>
									<p><a style="color: black" href="sproject@termobrest.ru">sproject@termobrest.ru</a></p>
									
								</td>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px;">
									<p><strong>Техническая поддержка:</strong></p>
									<p>
										+375 (162) 53-64-13;<br/>
									</p>
									<p><a style="color: black" href="konstruktor@termobrest.ru">konstruktor@termobrest.ru</a></p>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</body>
	</xsl:template>

</xsl:stylesheet>