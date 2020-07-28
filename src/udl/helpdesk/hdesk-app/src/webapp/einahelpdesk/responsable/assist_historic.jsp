<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />
<f:view>
	<sakai:view_container title="#{msgs.sample_title}"  rendered="#{HDesk.perfil=='responsable'}">
		
	<h:form>
		<jsp:include page="toolbar.jsp" />
			
		<br>
				
		<center>
		<table width="50% "bgcolor="#f0f0f0" border=0> 
		<tr><td align="center">
			<h3><h:outputText value="#{msgs.text_assist_historic1}"/> <h:outputText value="#{HDesk_responsable.ticket_sel}"/></h3>
		</td></tr>
		
		<tr><td>
		<table width="100% "bgcolor="#f0f0f0" border=1 cellspacing="0" cellpadding="0">  		
		<tr><td>
		<table width="100% "bgcolor="#dddfe4" border=0>  		
		<tr><td width="25%">
			<h:outputText value="#{msgs.text_assist_historic2}" style="font-size: 12;"/></td><td>		
			<h:outputText value="#{HDesk_responsable.assistencia.nom_usuari}" style="font-weight: bold; font-size: 12;"/>		
			<br>
		</tr></td>
		<tr><td>
                        <h:outputText value="#{msgs.text_assist_historic19}" style="font-size: 12;"/></td><td>
                        <h:outputText value="#{HDesk_responsable.assistencia.usuari}" style="font-weight: bold; font-size: 12;"/>
			<br>
		</tr></td>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic3}" style="font-size: 12;"/></td><td>
			<h:outputLink value="mailto:#{HDesk_responsable.assistencia.correu_usuari}">
			<h:outputText value="#{HDesk_responsable.assistencia.correu_usuari}" style="font-weight: bold; font-size: 12;"/>	
			</h:outputLink>
			<br>
		</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic4}" style="font-size: 12;"/></td><td>	
			<h:outputText value="#{HDesk_responsable.assistencia.strData_inici}" style="font-weight: bold; font-size: 12;"/>		
			</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic4b}" style="font-size: 12;"/></td><td>	
			<h:outputText value="#{HDesk_responsable.assistencia.strData_fi}" style="font-weight: bold; font-size: 12;"/>		
		</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic5}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.localitzacio}" style="font-weight: bold; font-size: 12;"/>
		</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic6}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.telefon}" style="font-weight: bold; font-size: 12;"/>	
	   </td></tr>
	    <tr><td>
			<h:outputText value="#{msgs.text_assist_historic7}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.nom_campus}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr>
	    <tr><td>
			<h:outputText value="#{msgs.text_assist_historic8}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.nom_edifici}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic11}" style="font-size: 12;"/></td><td>	
			<h:outputText value="#{HDesk_responsable.assistencia.codi_udl}" style="font-weight: bold; font-size: 12;"/>	
		</tr></td>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic9}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.nom_tecnic}" style="font-weight: bold; font-size: 12;" />
			<br>
		</td></tr>	
	  	<tr><td>
			<h:outputText value="#{msgs.text_assist_historic10}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.nom_categoria}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr>
	    <tr><td>
			<h:outputText value="#{msgs.text_assist_historic12}" style="font-size: 12;"/></td><td>
			<h:outputText value="#{HDesk_responsable.assistencia.nom_prioritat}" style="font-weight: bold; font-size: 12;"/>
		</td></tr></table>
	    <table width="100% "bgcolor="#dddfe4" border=0 cellspacing=1 cellpadding=1>  
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic16}" style="font-size: 12;"/><br>
			<h:inputTextarea  required="false" cols="80" rows="7" style="font-size: 12;" styleClass="text" value="#{HDesk_responsable.assistencia.consulta}" readonly="true"/>			
		</td></tr>
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic15}" style="font-size: 12;"/><br>
			<h:inputTextarea  required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;background:#f0f0f0" styleClass="text" value="#{HDesk_responsable.assistencia.solucio}" readonly="true"/>			
		</td></tr>
		
		<tr><td>
                        <h:outputText value="#{msgs.text_assist_historic20}" style="font-size: 12;"/><br>
                        <h:inputTextarea required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;background:#ffffe4" styleClass="text" value="#{HDesk_responsable.assistencia.solucio_interna}" readonly="true"/>
                </td></tr>

		</td></tr>
		</table>
		</td></tr>
		</table>
	<center>
		<sakai:button_bar>
                      <sakai:button_bar_item value="Tornar enrere" action="#{HDesk_responsable.RetornaCons}" />
                </sakai:button_bar>
	</center>
		</p></td></tr>
		</table>
		</center>

	</h:form>
</sakai:view_container>
<sakai:view_container rendered="#{HDesk.perfil!='responsable'}">
	<center><br><br>
	        <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" />
	  </center>
</sakai:view_container>
</f:view>
