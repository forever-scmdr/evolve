<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="agent_types" select="('Проектная организация', 'Монтажная организация', 'Теплоснабжение', 'Газораспределение', 'Эксплуатирующая организация', 'Торгующая организация', 'Дилер', 'Экспертиза и надзор', 'Производитель котельного оборудования', 'Производитель горелочного оборудования', 'Производитель ГРП/ШРП/ГРУ')"/>

	<xsl:variable name="agent_branches" select="('Нефтяная (добыча и переработка)', 'Металлургия', 'Химическая', 'Газовая ', 'Теплоэнергетика', 'ЖКХ', 'Строительная', 'Сельское хозяйство', 'Пищевая')"/>


</xsl:stylesheet>