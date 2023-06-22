<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>


	<xsl:template match="/">
		<head>
			 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		</head>
		<div>
			<div class="result" id="questionnaire">
				<xsl:if test="$success">
					<h2 style="font-size: 20px; margin-bottom: 12px;">
						Спасибо за заполнение анкеты!
					</h2>
					<p style="margin-bottom: 12px;">Ваш отзыв поможет сделать санаторий лучше.</p>
				</xsl:if>
				<xsl:if test="not($success)">
					<div class="wrap">
						<p class="lead">Для создания наиболее комфортных условий Вашего пребывания нам необходимо знать, что мы делаем неправильно. Просим уделить несколько минут и ответить на ряд вопросов. Эта информация поможет нам сделать пребывание в санатории более комфортным, улучшить качество обслуживания.</p>
						<form action="{$f/submit_link}" method="post" id="quest_form">
						<table>
							<tr>
								<td>Ваше гражданство</td>
								<td>
									<div>
										<input type="radio" name="{$f/country/@input}" value="РБ" id="rb" />
										<label for="rb">РБ</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/country/@input}" value="РФ" id="rf" />
										<label for="rf">РФ</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/country/@input}" value="Иное" id="other" />
										<label for="other">Иное</label>
									</div>
								</td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Это Ваш первый визит в наш санаторий?</td>
								<td>
									<div>
										<input type="radio"  name="{$f/first/@input}" value="1" id="first-y" />
										<label for="first-y">Да</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/first/@input}" value="0" id="first-n" />
										<label for="first-n">Нет</label>
									</div>
								</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Ваш пол</td>
								<td>
									<div>
										<input type="radio" name="{$f/sex/@input}" value="1" id="sex_m" />
										<label for="sex_m">Мужской</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sex/@input}" value="0" id="sex_f" />
										<label for="sex_f">Женский</label>
									</div>
								</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Ваш возраст</td>
								<td>
									<div>
										<input type="radio" name="{$f/age/@input}" value="до 25 лет" id="age-25" />
										<label for="age-25">до 25 лет</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/age/@input}" value="25—35" id="25—35" />
										<label for="25—35">25—35</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/age/@input}" value="36–45" id="36–45" />
										<label for="36–45">36–45</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/age/@input}" value="36–45" id="46–55" />
										<label for="46–55">46–55</label>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/age/@input}" value="старше 55" id="55-500" />
										<label for="55-500">старше 55</label>
									</div>
								</td>
							</tr>
						</table>
						<h4>1. Каким образом Вы приобретали путёвку? (напротив верного варианта поставить галочку)</h4>
						<table>
							<tr>
								<td>
									<div>
										<input type="radio" name="{$f/purchase_type/@input}" value="Турагентство" id="agency" />
										<label for="agency">Турагентство</label>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div>
										<input type="radio" name="{$f/purchase_type/@input}" value="ОАО «АСБ Беларусбанк»" id="bank" />
										<label for="bank">ОАО «АСБ Беларусбанк»</label>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<div>
										<input type="radio" name="{$f/purchase_type/@input}" value="Отдел маркетинга санатория" id="marketing" />
										<label for="marketing">Отдел маркетинга санатория</label>
									</div>
								</td>
							</tr>
						</table>
						<p>Иное:</p><textarea name="{$f/purchase_type/@input}"></textarea>
						<h4>2. Оцените следующие параметры по 11-тибальной шкале:</h4>
						<table class="ten">
							<tr>
								<th></th>
								<th>10</th>
								<th>9</th>
								<th>8</th>
								<th>7</th>
								<th>6</th>
								<th>5</th>
								<th>4</th>
								<th>3</th>
								<th>2</th>
								<th>1</th>
								<th>0</th>
							</tr>
							<tr>
								<td>качество обслуживания при бронировании</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/booking/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество обслуживания в регистратуре</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/reception/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>работа службы охраны</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/security/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>уровень комфорта и оснащение номера</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/comfort/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество уборка номера</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cleaning/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество Wi-fi</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/wifi/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>состояние территории санатория</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/territory/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>прием врача</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/doctor/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество обслуживания на медицинских процедурах</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>ассортимент оказываемых в санатории медицинских услуг</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/treatment_variety/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество обслуживания в столовой</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/dinner_serv/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество приготовления блюд</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/cooking/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>разнообразие предлагаемого меню</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/menu/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>разнообразие предлагаемых мероприятий</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/many_tusa/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>качество проведения мероприятий для досуга</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/good_tusa/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>уровень обслуживания в кафе (баре)</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>ассортимент в кафе (баре)</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/bar_menu/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>уровень обслуживания в СПА-центре</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>перечень предлагаемых услуг в СПА-центре</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/spa_menu/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>организация работы магазина</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/shop/@input}" value="0" />
									</div>
								</td>
							</tr>
							<tr>
								<td>организация работы спортивного комплекса</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/sport/@input}" value="0" />
									</div>
								</td>
							</tr>
						</table>
						<h4>3. Как в целом Вы можете оценить впечатления от пребывания в санатории по 11-ти бальной шкале?</h4>
						<table>
							<tr>
								<th>10</th>
								<th>9</th>
								<th>8</th>
								<th>7</th>
								<th>6</th>
								<th>5</th>
								<th>4</th>
								<th>3</th>
								<th>2</th>
								<th>1</th>
								<th>0</th>
							</tr>
							<tr>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="10" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="9" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="8" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="7" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="6" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="5" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="4" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="3" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="2" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="1" />
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/all/@input}" value="0" />
									</div>
								</td>
							</tr>
						</table>
						<h4>4. Какова вероятность того, что Вы порекомендуете «Спутник» друзьям (знакомым, коллегам) по 11-ти бальной шкале от 0 (ни в коем случае) до 10 (обязательно порекомендую)?</h4>
						<table>
							<tr>
								<th>10</th>
								<th>9</th>
								<th>8</th>
								<th>7</th>
								<th>6</th>
								<th>5</th>
								<th>4</th>
								<th>3</th>
								<th>2</th>
								<th>1</th>
								<th>0</th>
							</tr>
							<tr>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="10" onclick="set_reason(10)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="9" onclick="set_reason(9)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="8" onclick="set_reason(8)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="7" onclick="set_reason(7)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="6" onclick="set_reason(6)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="5" onclick="set_reason(5)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="4" onclick="set_reason(4)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="3" onclick="set_reason(3)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="2" onclick="set_reason(2)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="1" onclick="set_reason(1)"/>
									</div>
								</td>
								<td>
									<div>
										<input type="radio" name="{$f/recommend/@input}" value="0" onclick="set_reason(0)"/>
									</div>
								</td>
							</tr>
						</table>
						<p></p>
						<div id="reason_block" style="display: none">
							<p id="reason_text">Пожелания:</p>
							<textarea name="{$f/reason/@input}"></textarea>
						</div>
						<h4>5. Ваши пожелания и предложения:</h4><textarea name="{$f/comment/@input}"></textarea>
						<button type="submit" class="btn btn-primary" onclick="postFormAjax('quest_form', 'questionnaire'); return false;">Отправить отзыв</button>
						</form>
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="QUEST_PLACEHOLDER">
		<div id="questionnaire"></div>
		<script>
			$(document).ready(function() {
				insertAjax('/quest_form', 'questionnaire');
				$('#reason_block').hide();
			});

			function set_reason(mark) {
				if (mark == 9 || mark == 10) {
					$('#reason_text').text('Назовите главную причину, по которой Вы готовы порекомендовать наш санаторий');
				}
				else if (mark == 7 || mark == 8) {
					$('#reason_text').text('Чего Вам не хватило в работе санатория Спутник, чтобы поставить более высокую оценку?');
				}
				else {
					$('#reason_text').text('Вы поставили низкую оценку. Поясните, пожалуйста, почему. Нам важно Ваше мнение.');
				}
				$('#reason_block').show();
			}
		</script>
	</xsl:template>

</xsl:stylesheet>