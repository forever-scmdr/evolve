<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">

    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
	</xsl:text>
        <html lang="ru">
            <head>
                <base href="{page/base}"/>
                <meta charset="utf-8"/>

            </head>
            <body>
                <xsl:if test="not(//stack)">
                    <h1><xsl:value-of select="//h1"/></h1>
                    <a href="{page/base}/files/{'integration_file.xls'}" download="{'Файл интеграции'}.xls">Скачать</a>
                </xsl:if>
                <xsl:if test="//stack">
                    <h1>Создание прайс-листа завершено с ошибками. Обратитесь в службу поддержки.</h1>
                    <pre>
                        <xsl:value-of select="//stack"/>
                    </pre>
                </xsl:if>п
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>