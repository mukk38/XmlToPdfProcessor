<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
        <fo:root>
            <!-- Sayfa düzeni -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="297mm" page-width="210mm">
                    <fo:region-body margin="20mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- Sayfa içeriği -->
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <!-- İkon/Logo -->
                    <fo:block text-align="center" space-after="10mm">
                        <fo:external-graphic src="url('classpath:indir.jpg')" content-height="30mm"/>
                    </fo:block>

                    <!-- Başlık -->
                    <fo:block font-size="18pt" font-weight="bold" text-align="center" space-after="10mm">
                        XML İçeriği
                    </fo:block>

                    <!-- XML içeriğini işleme -->
                    <fo:block font-family="Helvetica" font-size="12pt">
                        <xsl:apply-templates select="*"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <!-- Her etiketi işleme şablonu -->
    <xsl:template match="*">
        <fo:block margin-left="{count(ancestor::*) * 10}mm" space-before="5mm">
            <!-- Etiket adı (kalın) -->
            <fo:block font-weight="bold">
                <xsl:value-of select="local-name()"/>:
            </fo:block>
            <!-- Öznitelikler (varsa, normal yazı tipi) -->
            <xsl:for-each select="@*">
                <fo:block margin-left="5mm">
                    Attribute <xsl:value-of select="local-name()"/>: <xsl:value-of select="."/>
                </fo:block>
            </xsl:for-each>
            <!-- Metin içeriği ve alt etiketler -->
            <xsl:apply-templates select="node()"/>
        </fo:block>
    </xsl:template>

    <!-- Metin düğümlerini işleme (normal yazı tipi) -->
    <xsl:template match="text()">
        <xsl:if test="normalize-space()">
            <fo:block margin-left="5mm">
                <xsl:value-of select="normalize-space()"/>
            </fo:block>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>