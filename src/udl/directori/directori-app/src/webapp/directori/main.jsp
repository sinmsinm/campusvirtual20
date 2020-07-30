<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 


<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.title_name}">
		<h:form>
			<jsp:include page="toolbar.jsp" />


		<h2>
			<h:outputText value="#{msgs.llista_titol}"/>
		</h2>
				
		<h:outputText value="#{msgs.textautenticacio}" rendered="#{DirectoriTool.userIsInDirectory=='unknown'}" />


		<h:outputLink target = "_top" value="#{DirectoriTool.loginUrl}"  rendered="#{DirectoriTool.userIsInDirectory=='unknown'}" >
			<f:verbatim>Autenticar</f:verbatim>
		</h:outputLink>
	   <br/>
		<h:outputText value="#{msgs.textactivacio}" rendered="#{DirectoriTool.userIsInDirectory=='unknown'}" />
		<h:commandLink value ="#{msgs.titolactiva}" action="#{DirectoriTool.onActiva}"  rendered="#{DirectoriTool.userIsInDirectory=='unknown'}" />
		
 		<br /><br />	 	
 		<h:outputText value="#{msgs.titolcerca}" />
 		<h:inputText id ="valorcerca" value="#{DirectoriTool.cerca}" />
	 	<h:commandButton value="#{msgs.cerca}" action="#{DirectoriTool.fesCerca}" />
 	
 		<br/ >
 		<h:selectBooleanCheckbox  value="#{DirectoriTool.incloureEst}"/>
		<h:outputText value="#{msgs.inclou}"/> <br />

		<%-- <h:outputText value="#{msgs.senseacc}" style="color:#ff0000" /> --%>
 		
		<c:if test="${sessionScope.DirectoriTool.numPagTotal > 0}"  >
		
 		<p align="left">
	       	<h:outputText value="#{msgs.grup_agrupa}"/>
	       	
	       	<h:selectOneMenu valueChangeListener="#{DirectoriTool.canvia}" onchange="this.form.submit()" value="#{DirectoriTool.selite}" immediate="true">
			<f:selectItem itemLabel="5" itemValue="5"/>
				<f:selectItem itemLabel="10" itemValue="10"/>
				<f:selectItem itemLabel="15" itemValue="15"/>
				<f:selectItem itemLabel="30" itemValue="30"/>
				<f:selectItem itemLabel="60" itemValue="60"/>
				<f:selectItem itemLabel="100" itemValue="100"/>
			</h:selectOneMenu>
			<br>

	       	<h:outputText value="#{msgs.grup_pagina} #{DirectoriTool.numPag}/#{DirectoriTool.numPagTotal}"/>
	       	
	       	<h:commandLink action="#{DirectoriTool.procAnterior}" rendered="#{DirectoriTool.anterior != 'nogif.gif'}" >
        	       <h:graphicImage url="image/#{DirectoriTool.anterior}" />
    	   	</h:commandLink>
 			<h:commandLink action="#{DirectoriTool.procSeguent}" >
        	       <h:graphicImage url="image/#{DirectoriTool.seguent}" rendered="#{DirectoriTool.seguent != 'nogif.gif'}" />
	       </h:commandLink>
 		</p>

 		<br />

 	
 	
 		<h:dataTable border="0"  cellpadding="5" first="#{DirectoriTool.numReg}" rows="#{DirectoriTool.numPerPag}" value="#{DirectoriTool.llistaAssist}" var="assistent" rowClasses="borderGrayBackground,borderWhite">
		<tr><td> </td></tr>
			
			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{DirectoriTool.ordenaCol3}"> 
						<h:outputText value="#{msgs.llista_cognoms} "/>
						<h:graphicImage url="image/#{DirectoriTool.strOrd3}" rendered="#{DirectoriTool.strOrd3 != 'nogif.gif'}" />
 					</h:commandLink>					
					
				</f:facet>
				<h:commandLink action="#{assistent.processaUsuari}" >
					<h:outputText value="#{assistent.cognoms}"/>
				</h:commandLink>
    		</h:column>
			
			<h:column>
				<f:facet name="header">
						<h:outputText value="      " />
				</f:facet>

    		</h:column>

			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{DirectoriTool.ordenaCol2}"> 		
						<h:outputText value="#{msgs.llista_nom} "/>
						<h:graphicImage url="image/#{DirectoriTool.strOrd2}" rendered="#{DirectoriTool.strOrd2 != 'nogif.gif'}" />
 					</h:commandLink>					
					
				</f:facet>
					<h:outputText value=" #{assistent.nom}"/>
    		</h:column>
	
		 </h:dataTable>
     	</c:if>

 		</p>
		 
	</h:form>
	</sakai:view_container>
</f:view> 