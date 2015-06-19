<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="4.0"
encoding="iso-8859-1" indent="yes"/>

  <xsl:template match="run">   
    <html xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
    <object id="AdobeSVG"
    	CLASSID="clsid:78156a80-c6a1-4bbf-8e6a-3cd390eeb4e2">
    </object>
	<xsl:processing-instruction name = "import" >
	    namespace="svg" implementation="#AdobeSVG"
	</xsl:processing-instruction> 

<head><title>Run Fitness</title></head>

<body>
  
    <xsl:variable name="popSize">
		<xsl:value-of select="search-parameters/population-size" />
    </xsl:variable>     
    <svg:svg xmlns:svg="http://www.w3.org/2000/svg" width="{($popSize * 2) + 400}px" height="460px">

   <!--Write title and parameters-->
   
      <svg:text style="font-size:12" x="10" y="10">
        ID = <xsl:value-of select="@id" />
        Time/Date = <xsl:value-of select="@timedatestamp" />
      </svg:text>
      <svg:text style="font-size:10" x="10" y="25">
        PopSize=<xsl:value-of select="search-parameters/population-size" />; 
        NumGenerations=<xsl:value-of select="search-parameters/generations" />; 
      </svg:text>

   <!--Draw and label horizontal axis-->
    
    <svg:path id="generation" stroke-width="1" stroke="black" fill="none" 
          d="M 40 340 L 540 340" />
    <svg:text fill="blue" font-size="12" font-family="Verdana">
    <svg:textPath dy="30" startOffset="25%" xlink:href="#generation">Generation</svg:textPath>
    </svg:text>

    <svg:path stroke-width="1" stroke="black" fill="none" 
          d="M 140 345 L 140 340" />
    <svg:path stroke-width="1" stroke="black" fill="none" 
          d="M 240 345 L 240 340" />
    <svg:path stroke-width="1" stroke="black" fill="none" 
          d="M 340 345 L 340 340" />
    <svg:path stroke-width="1" stroke="black" fill="none" 
          d="M 440 345 L 440 340" />
    <svg:path stroke-width="1" stroke="black" fill="none" 
          d="M 540 345 L 540 340" />

                   
    <xsl:variable name="generations">
		<xsl:value-of select="search-parameters/generations" />
    </xsl:variable>
    
    <svg:text x="132" y="355" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="$generations div 5" />
    </svg:text>
    <svg:text x="232" y="355" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="$generations div 5 * 2" />
    </svg:text>
    <svg:text x="332" y="355" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="$generations div 5 * 3" />
    </svg:text>
    <svg:text x="432" y="355" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="$generations div 5 * 4" />
    </svg:text>
    <svg:text x="532" y="355" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="$generations div 5 * 5" />
    </svg:text>

    
   <!--Draw vertical labels-->

    <svg:path id="individuals" stroke-width="1" stroke="black" fill="none" 
          d="M 40 340 L 40 40" />
     
    <svg:text fill="blue" font-size="12" font-family="Verdana">
    <svg:textPath dy="-30" startOffset="40%" xlink:href="#individuals">Individuals</svg:textPath>
    </svg:text>

    <svg:text x="15" y="285" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="($popSize * 0.2)" />
    </svg:text>
    <svg:text x="15" y="225" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="($popSize * 0.4)" />
    </svg:text>
    <svg:text x="15" y="165" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="($popSize * 0.6)" />
    </svg:text>
    <svg:text x="15" y="105" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="($popSize * 0.8)" />
    </svg:text>
    <svg:text x="15" y="45" fill="black" font-size="11" font-family="Verdana">
        <xsl:value-of select="($popSize)" />
    </svg:text>

    
   <!--Draw species per generation-->

   <xsl:variable name="popIncrement">
	     <xsl:value-of select="(300 div $popSize)"/>
   </xsl:variable>
    
    <xsl:for-each select="generation">

       <xsl:variable name="xplot">
    	     <xsl:value-of select="(((500 div $generations) * @id) + 42)"/>
       </xsl:variable>
             
       <xsl:for-each select="specie">
           <xsl:variable name="specieColor">
             <xsl:choose>
               <xsl:when test="((position() mod 3 = 0) and (@id = preceding::specie/@id))">
                 <xsl:text>CornflowerBlue</xsl:text>
               </xsl:when>
               <xsl:when test="(position() mod 3) = 1 and (@id = preceding::specie/@id)">
                 <xsl:text>Yellow</xsl:text>
               </xsl:when>
               <xsl:when test="(position() mod 3) = 2 and (@id = preceding::specie/@id)">
                 <xsl:text>Plum</xsl:text>
               </xsl:when>
               <xsl:otherwise>
                <xsl:text>Chartreuse</xsl:text>
               </xsl:otherwise>
             </xsl:choose>
           </xsl:variable>
       
           <xsl:variable name="specieCount">
    	      <xsl:value-of select="@count" />
           </xsl:variable>
           <xsl:variable name="yplotFrom" 
                     select="(340 - (sum(preceding-sibling::specie/@count) * $popIncrement))" />
           <xsl:variable name="yplotTo" 
                     select="(($yplotFrom - ($specieCount * $popIncrement)) + 0.5)" />       
           <svg:path stroke-width="3" stroke="{$specieColor}" fill="none" 
                 d="M {$xplot} {$yplotFrom} L {$xplot} {$yplotTo}" />
       </xsl:for-each>

    </xsl:for-each>
       
   
    </svg:svg>
    </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

