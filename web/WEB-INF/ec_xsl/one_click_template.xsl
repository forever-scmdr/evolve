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
        <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        </head>
            <body>
                <div class="text-container">
                    <xsl:for-each select="page/form/input/field">
                        <p>
                            <xsl:value-of select="@caption"/>:&#160;
                            <xsl:if test="@name = 'product_code'">
                                <a href="{concat('https://www.ozon.ru/context/detail/id/', ., '/?partner=mysteryby')}" target="_blank">
                                    <xsl:value-of select="."/>
                                </a>
                            </xsl:if>
                            <xsl:if test="@name != 'product_code'">
                              <xsl:value-of select="."/>
                            </xsl:if>
                        </p>
                    </xsl:for-each>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>