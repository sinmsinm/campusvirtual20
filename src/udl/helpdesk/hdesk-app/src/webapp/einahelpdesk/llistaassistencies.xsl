<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:fo="http://www.w3.org/1999/XSL/Format"
version="1.0">

<xsl:variable name="infermeria">Escola d'Infermeria</xsl:variable>

<xsl:template match="llista">

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<fo:layout-master-set>
        <fo:simple-page-master master-name="llistaa4"
                page-width="297mm" page-height="210mm"
                margin-top="0.0cm" margin-bottom="0.3cm"
                margin-left="0.5in" margin-right="0.5in">

                <fo:region-body margin-top="1.0cm" margin-bottom="0.5cm"/>
                <fo:region-after extent="1cm"/>
        </fo:simple-page-master>
</fo:layout-master-set>

<fo:page-sequence master-reference="llistaa4">
        <fo:static-content flow-name="xsl-region-after">
                <fo:block font-size="7pt" font-weight="bold" text-align="right"><fo:page-number/> de
                <fo:page-number-citation ref-id="theEnd"/></fo:block>
        </fo:static-content>

<fo:flow flow-name="xsl-region-body">

<fo:block>
	<xsl:variable name="ruta"><xsl:value-of select='logo'/></xsl:variable>
	<fo:external-graphic height="1.5cm" src="{$ruta}"/>
</fo:block>

<fo:block font-size="15pt"
        font-family="sans-serif"
        font-weight="bold"
        line-height="0.5cm"
        space-after.optimum="1pt"
        color="black"
        text-align="center"
        padding-top="0pt">
        Eina Suport Usuari - Llistat d'Assistències
</fo:block>

<fo:table   space-before.optimum="20pt" space-after.optimum="20pt">

        <fo:table-column column-width="2cm"/>
        <fo:table-column column-width="15cm"/>
	<fo:table-column column-width="2cm"/>
	<fo:table-column column-width="2cm"/>
	<fo:table-column column-width="4.2cm"/>
        <fo:table-column column-width="1cm"/>
	
        <fo:table-header>

          <fo:table-row  space-before.optimum="10pt" space-after.optimum="10pt">
             <fo:table-cell>
             	<fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">Ticket</fo:block>
             </fo:table-cell>

	     <fo:table-cell>
	      	<fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">Introduïda per (telèfon)  -----------------------------------> (data)  Descripció</fo:block>
             </fo:table-cell>
        
	     <fo:table-cell>
                <fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">Lloc</fo:block>
             </fo:table-cell>

             <fo:table-cell>
                <fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">Assignat</fo:block>
             </fo:table-cell>

	     <fo:table-cell>
                <fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">Tipus</fo:block>
             </fo:table-cell>

              <fo:table-cell>
                <fo:block font-weight="bold" text-align="left" vertical-align="middle" border-width="1pt" border-color="white" background-color="#7a1652" color="white">P/E</fo:block>
              </fo:table-cell>
    
	</fo:table-row>
        </fo:table-header>

        <fo:table-body>
                <xsl:apply-templates select='linies/linia' />
        </fo:table-body>
</fo:table>

        <fo:block id="theEnd" />
	
	  </fo:flow>
  </fo:page-sequence>

</fo:root>
</xsl:template>

<xsl:template match='linia'>
          <fo:table-row>

            <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
              	<xsl:value-of select="ticket"/>
              </fo:block>
            </fo:table-cell>

	    <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
                <xsl:value-of select="introduidaper"/> (Telèfon: <xsl:value-of select="telefon"/>) --------------------------------------------------------------------------------------------------------- (<xsl:value-of select="data"/>) <xsl:value-of select="descripcio"/>
              </fo:block>
            </fo:table-cell>
	
	     <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
                <xsl:if test="edifici='FDE'">Capp ------------- FDE ------------- <xsl:value-of select="despatx"/></xsl:if>
		<xsl:if test="edifici='F.Ciències Educació'">Educ ------------- Educ ------------- <xsl:value-of select="despatx"/></xsl:if>
		<xsl:if test="edifici='Facultat Medicina'">Salut ------------- Medic ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Arnau Vilanova'">Salut ------------- Arnau ------------- <xsl:value-of select="despatx"/></xsl:if>
		<xsl:if test="edifici='Rectorat'">Rect ------------- Rect ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Edifici 1'">ETSEA ------------- Edif 1 ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Edifici 2'">ETSEA ------------- Edif 2 ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Edifici 3'">ETSEA ------------- Edif 3 ------------- <xsl:value-of select="despatx"/></xsl:if>
		<xsl:if test="edifici='Edifici 4'">ETSEA ------------- Edif 4 ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici=$infermeria">Salut ------------- Inferm ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='EPS'">Capp ------------- EPS ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Polivalent'">Capp ------------- Poliv ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Emblematic'">Capp ------------- Emblem ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Annex'">Capp ------------- Annex ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Crea'">Capp ------------- CREA ------------- <xsl:value-of select="despatx"/></xsl:if>
                <xsl:if test="edifici='Edifici A-B'">ETSEA ------------- EdifA-B ------------- <xsl:value-of select="despatx"/></xsl:if>
		<xsl:if test="edifici='Palauet de direcció'">ETSEA ------------- Palau ------------- <xsl:value-of select="despatx"/></xsl:if> 
              </fo:block>
            </fo:table-cell>

	    <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
                <xsl:value-of select="assignadaa"/>
              </fo:block>
            </fo:table-cell>

	     <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
                <xsl:value-of select="tipusassistencia"/>
              </fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block  text-align="left" vertical-align="middle" space-before.optimum="10pt">
                <xsl:if test="prioritat='Normal'">N</xsl:if>
		<xsl:if test="prioritat='Urgent'">U</xsl:if>
		<xsl:if test="prioritat='Critica'">C</xsl:if>
		/
		<xsl:if test="estat='Activa'">A</xsl:if>
                <xsl:if test="estat='Resolta'">R</xsl:if>

              </fo:block>
            </fo:table-cell>
    
          </fo:table-row>
</xsl:template>

</xsl:stylesheet>
