<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
    <xsl:import href="../utils/utils.xsl"/>

    <xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
    </xsl:template>

    <!-- ****************************    СТРАНИЦА    ******************************** -->

    <xsl:template match="/">
        <xsl:call-template name="DOCTYPE"/>
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <base href="{page/base}"/>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <title>Список разделов</title>
            </head>
            <body>
                <ul>
                    <li><a href="{page/create_pricelist}">ВСЕ (<xsl:value-of select="sum(//f:num(product_count))"/>)</a></li>
                    <xsl:apply-templates select="/page/catalog/section"/>
                </ul>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="section">
       <li>
           <a href="{create_link}">
               <xsl:value-of select="name"/>(<xsl:value-of select="sum(.//f:num(product_count))"/>)
           </a>
           <xsl:if test="section">
               <ul>
                 <xsl:apply-templates select="section"/>
               </ul>
           </xsl:if>
       </li>
   </xsl:template>
</xsl:stylesheet>