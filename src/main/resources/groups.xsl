<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:payload="http://javaops.ru"
                version="1.0"
                exclude-result-prefixes="payload">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:param name="project_name"/>
    <xsl:template match="/">
        <html>
            <body>
                <div>
                    <xsl:if test="count(//payload:Project[payload:name[text()=$project_name]]/payload:groups/payload:Group) &gt; 0">
                        <ol>
                            <xsl:for-each
                                    select="//payload:Project[payload:name[text()=$project_name]]/payload:groups/payload:Group">
                                <li>
                                    <xsl:value-of select="@name"/>
                                </li>
                            </xsl:for-each>
                        </ol>
                    </xsl:if>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>