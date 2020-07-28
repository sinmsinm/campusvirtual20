<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />
<f:view>
	<sakai:view_container title="#{msgs.sample_title}" rendered="#{HDesk.perfil=='pas_pdi'}">
		
	<h:form>
		<jsp:include page="toolbar.jsp" />
		
		<br>
		<center>
		<table width="50% "bgcolor="#f0f0f0" border=0> 
		<tr><td align="center">
			<h3><h:outputText value="#{msgs.text_assist_admin3}"/> <h:outputText value="#{HDesk_pas_pdi.ticket_sel}"/>  </h3>
		</td></tr>
		
		<tr><td>
				
		<table width="100%" bgcolor="#dddfe4" border=0 cellspacing=0 cellpadding=3>  		
		<tr><td>
		<table width="100%" bgcolor="#f0f0f0" border=0 cellspacing=1 cellpadding=1>  		
		<tr><td  width="25%">
			<h:outputText value="#{msgs.text_assist_historic2}" style="font-size: 12;"/></td><td>		
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_usuari}" style="font-weight: bold; font-size: 12;"/>		
			</td></tr><tr><td>
						
			<h:outputText value="#{msgs.text_assist_historic4}" style="font-size: 12;"/></td><td>	
			<h:outputText value="#{HDesk_pas_pdi.assistencia.strData_inici}" style="font-weight: bold; font-size: 12;"/>		
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic4b}" style="font-size: 12;"/></td><td>	
			<h:outputText value="#{HDesk_pas_pdi.assistencia.strData_fi}" style="font-weight: bold; font-size: 12;"/>		
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic5}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.localitzacio}" style="font-weight: bold; font-size: 12;"/>
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic6}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.telefon}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>	
			
			<h:outputText value="#{msgs.text_assist_historic7}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_campus}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic8}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_edifici}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic11}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.codi_udl}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic9}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_tecnic}" style="font-weight: bold; font-size: 12;" />
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic10}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_categoria}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic12}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_prioritat}" style="font-weight: bold; font-size: 12;"/>	
			</td></tr><tr><td>
			
			<h:outputText value="#{msgs.text_assist_admin1}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_pas_pdi.assistencia.nom_estat}" style="font-weight: bold; font-size: 12;"/>				
			</td></tr></table>
	    	
	    	<table width="100% "bgcolor="#f0f0f0" border=0 cellspacing=1 cellpadding=1>   	
			<tr><td>
			
			<h:outputText value="#{msgs.text_assist_historic16}" style="font-size: 12;"/><br>
			<h:inputTextarea id="obs" required="false" cols="69" rows="6" style="font-weight: bold; font-size: 12; color: #777777;" styleClass="text" value="#{HDesk_pas_pdi.assistencia.consulta}" readonly="true"/>			
			<br><br>
			
			<h:outputText value="#{msgs.text_assist_historic15}" style="font-size: 12;"/><br>
			<h:inputTextarea required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;background:#f0f0f0" styleClass="text" value="#{HDesk_pas_pdi.assistencia.solucio}" readonly="true"/>
			<br><br>
			
			
			<f:attribute name="res" value="#{HDesk_pas_pdi.assistencia.nom_estat}" /> 
            <c:choose>
			  <c:when test="${HDesk_pas_pdi.assistencia.nom_estat=='Activa'}">
			  	<%-- <h:outputText value="#{msgs.text_assist_admin2}" style="font-size: 12;"/><br> --%>
				Introduir text: <br>
			  	<h:inputTextarea id="obs1" required="false" cols="80" rows="6" style="font-size: 12;" styleClass="text" value="#{HDesk_pas_pdi.resposta}"/>			
			  	</td><td></td></tr>
				
			  	<tr><td align="center">
			  	<sakai:button_bar>
					<sakai:button_bar_item value="#{msgs.text_assist_pas_pdi}" action="#{HDesk_pas_pdi.EnviarResposta}"/>
					<sakai:button_bar_item id="tornar_a" value="Tornar enrere" action="#{HDesk_pas_pdi.RetornaCons}" />	
			  	</sakai:button_bar>	
			  </c:when>
			  <c:when test="${HDesk_pas_pdi.assistencia.nom_estat=='Resolta'}">
			  	<%-- <h:outputText value="#{msgs.text_assist_admin2}" style="font-size: 12;"/><br> --%>
				Introduir text: <br>
			  	<h:inputTextarea id="obs1" required="false" cols="80" rows="6" style="font-size: 12;" styleClass="text" value="#{HDesk_pas_pdi.resposta}"/>			
			  	</td><td></td></tr>
				
			  	<tr><td align="center">
			  	<sakai:button_bar>
					<sakai:button_bar_item value="#{msgs.text_assist_pas_pdi}" action="#{HDesk_pas_pdi.EnviarResposta}"/>
			  		<sakai:button_bar_item id="tornar_b" value="Tornar enrere" action="#{HDesk_pas_pdi.RetornaCons}" /> 
			  	</sakai:button_bar>	
			  </c:when>
			  <c:otherwise>   
              		<center>
			  	<sakai:button_bar>
			  <sakai:button_bar_item id="tornar_c" value="Tornar enrere" action="#{HDesk_pas_pdi.RetornaCons}" /> 
			  	</sakai:button_bar>	
			</center>
			</c:otherwise>
    		</c:choose>
			
		</td></tr>
		</table>
		
		</td></tr>
		</table>
		</center>

	</h:form>
</sakai:view_container>
<sakai:view_container rendered="#{HDesk.perfil!='pas_pdi'}">
	<center><br><br>
	        <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" />
	  </center>
</sakai:view_container>
</f:view>
