<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	
	<!-- Кнопка "Заказать" -->
	<xsl:variable name="GA_TO_CART">ga('send', 'event', 'Good', 'GoodToBasket'); yaCounter53439259.reachGoal('GoodToBasket'); return true;</xsl:variable>

	<!-- Ссылка на корзину -->
	<xsl:variable name="GA_CART_LINK">ga('send', 'event', 'Basket', 'EnterToBasket'); yaCounter53439259.reachGoal('EnterToBasket'); return true;</xsl:variable>


	<!-- Сравнить -->
	<xsl:variable name="GA_TO_COMPARE">ga('send', 'event', 'Good', 'GoodCompare'); yaCounter53439259.reachGoal('GoodCompare'); return true;</xsl:variable>

	<!-- В избранное -->
	<xsl:variable name="GA_TO_CHOSEN">ga('send', 'event', 'Good', 'GoodPutAside'); yaCounter53439259.reachGoal('GoodPutAside'); return true;</xsl:variable>


	<!-- Удалить из корзины -->
	<xsl:variable name="GA_DELETE">ga('send', 'event', 'Good', 'DelitGoodFromBasket'); yaCounter53439259.reachGoal('DelitGoodFromBasket'); return true;</xsl:variable>


	<!-- Продолжить оформление -->
	<xsl:variable name="GA_PROCEED">ga('send', 'event', 'Buy', 'BuyStep1'); yaCounter53439259.reachGoal('BuyStep1'); return true;</xsl:variable>

	<!-- Подтвердить физ. -->
	<xsl:variable name="GA_CONFIRM_PHYS">ga('send', 'event', 'Buy', 'BuyStep2Fiz'); yaCounter53439259.reachGoal('BuyStep2Fiz'); return true;</xsl:variable>

	<!-- Подтвердить юр. -->
	<xsl:variable name="GA_CONFIRM_JUR">ga('send', 'event', 'Buy', 'BuyStep2Yur'); yaCounter53439259.reachGoal('BuyStep2Yur'); return true;</xsl:variable>

	<!-- Телефоны -->
	<xsl:variable name="GA_PHONES">ga('send', 'event', 'Tell', 'ClickTell'); yaCounter53439259.reachGoal('ClickTell'); return true;</xsl:variable>

	<!-- Почта -->
	<xsl:variable name="GA_EMAIL">ga('send', 'event', 'Mail', 'SendMail'); yaCounter53439259.reachGoal('SendMail'); return true;</xsl:variable>


	<!-- Вход -->
	<xsl:variable name="GA_LOGIN">ga('send', 'event', 'Enter', 'EnterToLK'); yaCounter53439259.reachGoal('EnterToLK'); return true;</xsl:variable>

	<!-- Регистрация физ. -->
	<xsl:variable name="GA_REGISTER_PHYS">ga('send', 'event', 'Registration', 'RegistrationFiz'); yaCounter53439259.reachGoal('RegistrationFiz'); return true;</xsl:variable>


	<!-- Регистрация юр. -->
	<xsl:variable name="GA_REGISTER_JUR">ga('send', 'event', 'Registration', 'RegistrationYur'); yaCounter53439259.reachGoal('RegistrationYur'); return true;</xsl:variable>

	<!-- Сбросить фильтр -->
	<xsl:variable name="GA_RESET_FILTER">ga('send', 'event', 'Filter', 'ResetFilter'); yaCounter53439259.reachGoal('ResetFilter'); return true;</xsl:variable>

</xsl:stylesheet>