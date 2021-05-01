<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">

    <xsl:variable name="to_cart_available_label" select="'В корзину'"/>
    <xsl:variable name="to_cart_na_label" select="'Заказать'"/>
    <xsl:variable name="go_to_cart_label" select="'Оформить заказ'"/>

    <xsl:variable name="compare_add_label" select="'Выбрать'"/>
    <xsl:variable name="compare_remove_label" select="'Удалить'"/>
    <xsl:variable name="go_to_compare_label" select="'Сравнить'"/>

    <xsl:variable name="fav_add_label" select="'Добавить в избранное'"/>
    <xsl:variable name="fav_remove_label" select="'Удалить'"/>
    <xsl:variable name="go_to_fav_label" select="'Избранное'"/>

</xsl:stylesheet>