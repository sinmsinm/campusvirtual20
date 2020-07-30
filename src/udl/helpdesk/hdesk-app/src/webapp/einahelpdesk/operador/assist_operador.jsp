<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />
<f:view>
	<sakai:view_container title="#{msgs.sample_title}" rendered="#{HDesk.perfil=='operador'}">
		
	<h:form>
		<jsp:include page="toolbar.jsp" />
			
		<br>
		<center>
		<table width="50% "bgcolor="#f0f0f0" border=0> 
		<tr><td align="center">
			<h3><h:outputText value="#{msgs.text_assist_admin3}"/> <h:outputText value="#{HDesk_operador.ticket_sel}"/> </h3>
		</td></tr>
		
		<tr><td>
				
		<table width="100%" bgcolor="#dddfe4" border=0 cellspacing=0 cellpadding=3>  		
		<tr><td>
		<table width="100%" bgcolor="#f0f0f0" border=0 cellspacing=1 cellpadding=1>  		
		<tr><td width="25%">
			<h:outputText value="#{msgs.text_assist_historic2}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.nom_usuari}" style="font-weight: bold; font-size: 12;"/>		
			<td>
                        <h:outputText value="#{msgs.text_assist_historic18}" style="font-size: 12;"/>
                        <h:selectBooleanCheckbox value="#{HDesk_operador.envia_correu_usuari}" required="false" disabled="false" />
                        </td>
		 </td></tr><tr><td>
                        <h:outputText value="#{msgs.text_assist_historic19}" style="font-size: 12;"/></td><td>
                        <h:outputText value="#{HDesk_operador.assistencia.usuari}" style="font-weight: bold; font-size: 12;"/>

		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic3}" style="font-size: 12;"/> </td><td>
			<h:outputLink value="mailto:#{HDesk_operador.assistencia.correu_usuari}">
			<h:outputText value="#{HDesk_operador.assistencia.correu_usuari}" style="font-weight: bold; font-size: 12;"/>	
			</h:outputLink>
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic4}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.strData_inici}" style="font-weight: bold; font-size: 12;"/>		
		</td></tr>
			    <c:choose>
                <c:when test="${HDesk_operador.assistencia.estat==1}">
						<tr><td>
						<h:outputText value="#{msgs.text_assist_historic4b}" style="font-size: 12;"/> </td><td>
						<h:outputText value="#{HDesk_operador.assistencia.strData_fi}" style="font-weight: bold; font-size: 12;"/>		
						</td></tr>
		       	</c:when>
                <c:otherwise>
                </c:otherwise>
                </c:choose>       
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic5}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.localitzacio}" style="font-weight: bold; font-size: 12;"/>
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic6}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.telefon}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr><tr><td>	
			<h:outputText value="#{msgs.text_assist_historic7}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.nom_campus}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic8}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.nom_edifici}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic11}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.codi_udl}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic9}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.nom_tecnic}" style="font-weight: bold; font-size: 12;" />
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic14}" style="font-size: 12;"/> </td><td>
			<h:selectOneMenu value="#{HDesk_operador.tecnic_sel}" style="background:#f8ffbe">
				<f:selectItem itemValue="" itemLabel="..." />
				<f:selectItems value="#{HDesk_operador.operadors}" />
  			</h:selectOneMenu>
			<td>
			<h:outputText value="#{msgs.text_assist_historic18}" style="font-size: 12;"/>
  			<h:selectBooleanCheckbox value="#{HDesk_operador.envia_correu}" required="false" disabled="false" />
  			</td>	
  			</td></tr><tr><td></td><td>
  			<h:outputText value="#{msgs.avis}"/>
  		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic10}" style="font-size: 12;"/> </td><td>
			<h:outputText value="#{HDesk_operador.assistencia.nom_categoria}" style="font-weight: bold; font-size: 12;"/>	
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_historic12}" style="font-size: 12;"/> </td><td>
				<h:outputText value="#{HDesk_operador.assistencia.nom_prioritat}" style="font-weight: bold; font-size: 12;"/>
								<h:outputText value="#{msgs.canvi}" />
							
			<h:selectOneMenu id="menuTipus2" value="#{HDesk_operador.prioritat_sel}" required="false" style="background:#f8ffbe">
	
									<f:selectItem itemValue="" itemLabel="..." />
					  					<f:selectItem itemValue="1" itemLabel="Normal"/>
							  			<f:selectItem itemValue="2" itemLabel="Urgent"/>
							  			<f:selectItem itemValue="0" itemLabel="Critica"/>
			</h:selectOneMenu>
		</td></tr><tr><td>
			<h:outputText value="#{msgs.text_assist_admin1}" style="font-size: 12;"/> </td><td>
								<h:outputText value="#{HDesk_operador.assistencia.nom_estat}" style="font-weight: bold; font-size: 12;"/>
								<h:outputText value="#{msgs.canvi}" />
			<h:selectOneMenu id="menuTipus1"  value="#{HDesk_operador.estat_sel}" required="false" style="background:#f8ffbe">
						<f:selectItem itemValue="" itemLabel="..." />
        							<f:selectItem itemValue="0" itemLabel="Activa"/>
						  			<f:selectItem itemValue="1" itemLabel="Resolta"/>
					        		<f:selectItem itemValue="2" itemLabel="Historic"/>
			</h:selectOneMenu>
		</td></tr></table>
	    <table width="100% "bgcolor="#f0f0f0" border=0 cellspacing=1 cellpadding=1>  		
		
		<tr><td>
			<h:outputText value="#{msgs.text_assist_historic16}" style="font-size: 12;"/><br>
			<h:inputTextarea required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;" styleClass="text" value="#{HDesk_operador.assistencia.consulta}" readonly="true"/>			
			<h:outputText value="#{msgs.text_assist_historic15}" style="font-size: 12;"/><br>
			<h:inputTextarea required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;background:#f0f0f0" styleClass="text" value="#{HDesk_operador.assistencia.solucio}" readonly="true"/>			
			<br><h:outputText value="#{msgs.text_assist_admin2}" style="font-size: 12;"/><br>
			<h:inputTextarea required="false" cols="80" rows="4" style="font-size: 12;" styleClass="text" value="#{HDesk_operador.seguiment}" />		
			<h:outputText value="#{msgs.text_assist_historic20}" style="font-size: 12;"/><br>
                        <h:inputTextarea required="false" cols="69" rows="7" style="font-weight: bold; font-size: 12; color: #777777;background:#ffffe4" styleClass="text" value="#{HDesk_operador.assistencia.solucio_interna}" readonly="true"/>
                        <br><h:outputText value="#{msgs.text_assist_historic21}" style="font-size: 12;"/><br>
                        <h:inputTextarea required="false" cols="80" rows="4" style="font-size: 12;background:#ffffe4" styleClass="text" value="#{HDesk_operador.seguiment_intern}" /> 
		</td><td></td></tr>
		<tr><td align="center">
		<sakai:button_bar>
			<sakai:button_bar_item value="#{msgs.boto_desar}" action="#{HDesk_operador.processActionDesarAssistencia}" />
			<sakai:button_bar_item value="Tornar enrere" action="#{HDesk_operador.RetornaCons}" />		   
			<sakai:button_bar_item value="#{msgs.boto_informe}" action="#{HDesk_operador.imprimeixUnaAssistencia}" rendered="#{HDesk_operador.isHistoric==0}"/>
		</sakai:button_bar>
		</td></tr>
		

		</table>
		</td></tr>
		</table>
		
		</p></td></tr>
		</table>
		</center>
	</h:form>
</sakai:view_container>
<sakai:view_container rendered="#{HDesk.perfil!='operador'}">
	<center><br><br>
	        <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" />
	  </center>
</sakai:view_container>
</f:view>
