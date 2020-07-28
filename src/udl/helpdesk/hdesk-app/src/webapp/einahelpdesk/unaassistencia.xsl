<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:fo="http://www.w3.org/1999/XSL/Format"
version="1.0">

<xsl:template match="llista">

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<fo:layout-master-set>
        <fo:simple-page-master master-name="llistaa4"
                page-width="210mm" page-height="297mm"
                margin-top="0.2cm" margin-bottom="0.2cm"
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
        Eina Suport Usuari - Assistència
</fo:block>

<fo:table space-before.optimum="20pt" space-after.optimum="20pt">

        <fo:table-column column-width="5cm"/>
        <fo:table-column column-width="13cm"/>

		        	
        <!-- <fo:table-header>
  	</fo:table-header>-->
        
	<fo:table-body>
                <xsl:apply-templates select='linia' />
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
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Ticket:</fo:block>
            </fo:table-cell>

            <fo:table-cell>
	      <fo:block text-align="left" vertical-align="middle" background-color="white" color="black"> 
                <xsl:value-of select="ticket"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
	    <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>


	  <fo:table-row>
	    <fo:table-cell>
	       <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Emesa per:</fo:block>	
            </fo:table-cell>

	    <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="introduidaper"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Login usuari:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="login"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Correu:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="correu"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	<fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Data inici:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="datainici"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	<fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Data fi:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="datafi"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Despatx:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="despatx"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Telèfon:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="telefon"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Campus:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="campus"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Edifici:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="edifici"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Codi UdL màquina:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="codimaquina"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Assignada a:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="assignadaa"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Tipus d'assistència:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="tipusassistencia"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Prioritat:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="prioritat"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	 <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Estat:</fo:block>
          </fo:table-cell>

            <fo:table-cell>
               <fo:block text-align="left" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="estat"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

         <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>


	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Descripció:</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="justify" vertical-align="middle" background-color="white" color="black">
                <xsl:value-of select="descripcio"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Seguiment:</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="justify" background-color="white" color="black">
                <xsl:value-of select="seguiment"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="left" vertical-align="middle" background-color="white" color="white">-</fo:block>
            </fo:table-cell>
          </fo:table-row>

	  <fo:table-row>
            <fo:table-cell>
               <fo:block font-weight="bold" text-align="left" vertical-align="middle" background-color="white" color="black">Seguiment intern:</fo:block>
            </fo:table-cell>

            <fo:table-cell>
              <fo:block text-align="justify" background-color="white" color="black">
                <xsl:value-of select="seguimentintern"/>
              </fo:block>
            </fo:table-cell>
           </fo:table-row>


</xsl:template>


</xsl:stylesheet>
