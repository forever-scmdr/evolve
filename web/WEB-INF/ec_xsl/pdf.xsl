<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:ext="http://exslt.org/common"
		xmlns="http://www.w3.org/1999/xhtml"
		version="2.0"
		xmlns:f="f:f"
		exclude-result-prefixes="xsl ext">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base" />

	<xsl:template match="/">
		<html lang="ru">
			<head>
				<meta charset="UTF-8"/>
				<title>Счет №2141223 от 04 марта 2019г.</title>
			</head>
			<style>
				body {
				font-size: 13px;
				}
				table, td {
				border-collapse: collapse;
				border: 1px solid #000;
				}
				table {
				margin-bottom: 16px;
				}
				td {
				padding: 2px 4px;
				}
				p {
				margin: 0;
				}
			</style>
			<body>
				<p>Частное торговое унитарное предприятие "Фрезерпром"</p>
				<p>УНП:691814125</p>
				<p>Р/сч: BY27TECN30121609100150000000</p>
				<p>в ОАО ''Технобанк'' 220002, г.Минск,ул.Кропоткина, 44 код TECNBY22</p>
				<p>Адрес: 220131, г.Минск, пер.Кольцова 4-й, 51, пом. 92</p>
				<h1>Счет №2141223 от 04 марта 2019г.</h1>
				<p>Заказчик: Индивидуальный предприниматель Жуковский Дмитрий Владимирович</p>
				<p>Плательщик: Индивидуальный предприниматель Жуковский Дмитрий Владимирович</p>
				<p>УНП:690191789</p>
				<p>Р/сч: BY76AKBB30130602126506200000</p>
				<p>в Филиал № 612 ОАО ''АСБ Беларусбанк'' в г.Борисове 222120, Минская обл., г.Борисов,пр-т Революции, 47</p>
				<p>код AKBBBY21612</p>
				<p>Адрес: 223311, Минская обл., г. Березино, ул. Победы д.51, кв.21, тел.: +37529 6656309, +3751715 62177</p>
				<h2>Цель приобретения</h2>
				<table>
					<tr>
						<td>№</td>
						<td>Наименование товара</td>
						<td>Ед. изм.</td>
						<td>Количество</td>
						<td>Цена, BYN</td>
						<td>Сумма, BYN</td>
						<td>Ставка НДС, %</td>
						<td>Сумма НДС, BYN</td>
						<td>Всего с НДС, BYN</td>
					</tr>
					<tr>
						<td>1</td>
						<td>945-218 Выключатель ЛШМ, рубанок Китай</td>
						<td>шт</td>
						<td>3</td>
						<td>3.65</td>
						<td>10.95</td>
						<td>Без НДС</td>
						<td></td>
						<td>10.95</td>
					</tr>
					<tr>
						<td>1</td>
						<td>945-218 Выключатель ЛШМ, рубанок Китай</td>
						<td>шт</td>
						<td>3</td>
						<td>3.65</td>
						<td>10.95</td>
						<td>Без НДС</td>
						<td></td>
						<td>10.95</td>
					</tr>
					<tr>
						<td>1</td>
						<td>945-218 Выключатель ЛШМ, рубанок Китай</td>
						<td>шт</td>
						<td>3</td>
						<td>3.65</td>
						<td>10.95</td>
						<td>Без НДС</td>
						<td></td>
						<td>10.95</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td><strong>Итого:</strong></td>
						<td><strong>81.71</strong></td>
						<td></td>
						<td></td>
						<td>81.71</td>
					</tr>
				</table>
				<p><strong>Сумма НДС: Ноль белорусских рублей 00 копеек</strong></p>
				<p><strong>Всего к оплате  на сумму с НДС: Восемьдесят один белорусский рубль 71 копейка</strong></p>
				<br/>
				<br/>
				<p>Директор _______________ (В.В. Давыдчик)</p>
				<br/>
				<br/>
				<p>Главный бухгалтер _______________ (М.Н. Шилович)</p>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>