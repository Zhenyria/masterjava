<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:payload="http://javaops.ru"
                version="1.0"
                exclude-result-prefixes="payload">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <body>
                <div>
                    <xsl:if test="count(payload:Payload/payload:users/payload:User) &gt; 0">
                        <ol>
                            <xsl:for-each select="payload:Payload/payload:users/payload:User">
                                <li>
                                    <xsl:value-of select="payload:fullName"/>
                                    <xsl:text> </xsl:text>
                                    <xsl:value-of select="@email"/>
                                </li>
                            </xsl:for-each>
                        </ol>
                    </xsl:if>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>