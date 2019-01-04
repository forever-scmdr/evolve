<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        xmlns:functx="functx:functx"
        version="2.0">
    <xsl:template match="/">
        <root>
            <xsl:for-each select="//brand">
                <!--<xsl:value-of select="name"/>-->
                <xsl:if test="name != 'STIHL - лесная и садовая техника'">
                    <xsl:for-each select="current()//link">
                        <rule>
                            <from casesensitive="false">
                                <xsl:value-of select="."/>
                            </from>
                            <to type="permanent-redirect" qsappend="false" last="true">
                                <xsl:call-template name="redirect">
                                    <xsl:with-param name="old_link" select="."/>
                                </xsl:call-template>
                            </to>
                        </rule>
                    </xsl:for-each>
                </xsl:if>
            </xsl:for-each>
        </root>
    </xsl:template>

    <xsl:template name="redirect">
        <xsl:param name="old_link"/>
        <xsl:variable name="trim1" select="substring($old_link, 1, string-length($old_link)-1)"/>
        <xsl:variable name="starts-with" select="substring-before($trim1, '/')" />
        <xsl:variable name="trim2" select="f:substring-after-last($trim1, '/')"/>
        <xsl:variable name="replace1" select="if(ends-with($trim2, '_')) then substring($trim2, 1, string-length($trim2)-1) else $trim2"/>
        <xsl:variable name="replace2" select="replace($replace1, '(%26)|(%E2)|(%84)|(%A2)|(%C2)|(%AE)|(%21)|(%29)|(%28)', '')"/>
        <xsl:variable name="s" select="if($starts-with = 'brand') then 'sub/' else if($starts-with = 'section') then '' else 'product/'"/>
        <xsl:value-of select="concat('https://takt.by/', $s, $replace2)"/>
    </xsl:template>

    <xsl:function name="f:substring-after-last" as="xs:string"
                  xmlns:functx="http://www.functx.com">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:param name="delim" as="xs:string"/>

        <xsl:sequence select="
   replace ($arg,concat('^.*',f:escape-for-regex($delim)),'')
 "/>

    </xsl:function>
    <xsl:function name="f:escape-for-regex" as="xs:string"
                  xmlns:functx="http://www.functx.com">
        <xsl:param name="arg" as="xs:string?"/>

        <xsl:sequence select="
   replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
 "/>

    </xsl:function>
</xsl:stylesheet>