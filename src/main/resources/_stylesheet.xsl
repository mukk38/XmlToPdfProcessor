<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="297mm" page-width="210mm">
                    <fo:region-body margin="20mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="14pt" font-weight="bold">XML İçeriği</fo:block>
                    <fo:block space-before="10mm">
                        <xsl:apply-templates select="*"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="*">
        <fo:block margin-left="10mm">
            <fo:block font-weight="bold"><xsl:value-of select="local-name()"/>:</fo:block>
            <fo:block><xsl:value-of select="."/></fo:block>
            <xsl:apply-templates select="*"/>
        </fo:block>
    </xsl:template>
</xsl:stylesheet>