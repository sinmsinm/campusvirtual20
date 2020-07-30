<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>


<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />
<f:view>
	<sakai:view_container title="#{msgs.sample_title}">
		
	<h:form>
		<f:attribute name="prof" value="#{HDesk.perfil}" />
	
	<c:choose>
		<c:when test="${HDesk.perfil=='operador'}">
				<jsp:include page="operador/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='pas_pdi'}">
				<jsp:include page="pas_pdi/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='responsable'}">
				<jsp:include page="responsable/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='support'}">
				<jsp:include page="support/toolbar.jsp" />
		</c:when>
	</c:choose>
			
		<br>
				
		<center>
		<table width="50% "bgcolor="#f0f0f0" border=0> 
			<tr><td align="center">
				<h3><h:outputText value="#{msgs.text_nova_assist}"/></h3>
			</td></tr>
		
			<tr><td>
    	    <p valign="middle" align="center">
				<h:outputText value="#{msgs.text_nova_assist1}" style="font-weight: bold;"/>
			</p>
			</td></tr>
		
			<tr><td>
			<table width="100%" bgcolor="#f0f0f0" border=1 cellspacing="0" cellpadding="0">  		
			<tr><td>
			<table width="100%" bgcolor="#dddfe4" border=0>  		
			<tr><td width="25%">
				<h:outputText value="#{msgs.text_nova_assist2}" style="font-size: 12;"/></td><td>		
				<h:outputText value="#{HDesk.nom}" style="font-weight: bold; font-size: 12;"/>		
				<br>
			</tr></td>
			<tr><td>
				<h:outputText value="#{msgs.text_nova_assist3}" style="font-size: 12;"/></td><td>
				<h:inputText value="#{HDesk.despatx}" required="false" size="9" maxlength="9"/>
				<br>
			</td></tr>
			<tr><td>
				<h:outputText value="#{msgs.text_nova_assist4}" style="font-size: 12;"/></td><td>
				<h:inputText value="#{HDesk.telefon}" required="false" size="12" maxlength="12" />	
				<br>		
		    </td></tr>
	    	<tr><td>
				<h:outputText value="#{msgs.text_nova_assist5}" style="font-size: 12;"/></td><td>
				<h:selectOneMenu value="#{HDesk.campus_sel}" valueChangeListener="#{HDesk.setCampus_sel}" onchange="submit()" immediate="true">
						<f:selectItem itemValue="" itemLabel="..." />	
						<f:selectItems value="#{HDesk.llistaCampus}" />
				</h:selectOneMenu>
			</td></tr>
	    	<tr><td>
				<h:outputText value="#{msgs.text_nova_assist6}" style="font-size: 12;"/></td><td>
				<h:selectOneMenu value="#{HDesk.edifici_selec}">
						<f:selectItem itemValue="" itemLabel="..." />	
						<f:selectItems value="#{HDesk.llistaEdificis}" />
				</h:selectOneMenu>
			<br>
			</td></tr>
			<tr><td>
				<h:outputText value="#{msgs.text_nova_assist8}" style="font-size: 12;"/></td><td>
				<h:inputText value="#{HDesk.codi_udl}" required="false" size="10" maxlength="10" />	
				<br>		
		    </td></tr>	
			<tr><td>
				<h:outputText value="#{msgs.text_nova_assist7}" style="font-size: 12;"/></td><td>
				<h:selectOneMenu value="#{HDesk.categoria_selec}" valueChangeListener="#{HDesk.setCategoria_selec}" onchange="submit()" immediate="true">
						<f:selectItem itemValue="" itemLabel="..." />
						<f:selectItems value="#{HDesk.llistaCategoria}" />
				</h:selectOneMenu>
				<br>
			</td></tr></table>
	    	 <table width="100% "bgcolor="#dddfe4" border=0 cellspacing=1 cellpadding=1>   	
			<tr><td>
		  		<h:outputText value="#{msgs.text_nova_assist11}" style="font-size: 12;"/>
		  		<h:inputTextarea required="false" cols="69" rows="3" style="font-weight: bold; font-size: 12; color: #777777;" styleClass="text" value="#{HDesk.categoria}" readonly="true"/>		
		  	</td></tr>
			<tr><td>
				<h:outputText value="#{msgs.text_nova_assist9}" style="font-size: 12;"/><br>
				<h:inputTextarea id="obs" required="false" cols="80" rows="10" style="font-size: 12;" styleClass="text" value="#{HDesk.consulta}" />			
		 	<h:outputText value="#{msgs.text_nova_assist12}" style="font-size: 12;"/><br>
			</td></tr>
			<tr><td align="center">
				<sakai:button_bar>
					<sakai:button_bar_item value="#{msgs.text_nova_assist10}" action="#{HDesk.EnviaDades}"/>
				</sakai:button_bar>	
			</td></tr>
					
			</table>
			</td></tr>
			</table>
		
			</td></tr>
		</table>
		</center>

		</h:form>
</sakai:view_container>
</f:view>
