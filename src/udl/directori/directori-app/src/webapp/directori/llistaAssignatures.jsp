<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
        <sakai:view_container title="msgs.smstitle_name">
        <h2><h:outputText value="#{msgs.smsllista_assignatures}: "/><h:outputText value="#{SMSTool.assistent.cognoms}, #{SMSTool.assistent.nom}"/></h2><br>
        <h:form>
					<h:dataTable value="#{SMSTool.llistaAssignatures}"  var="assignatura" rowClasses="borderGrayBackground,borderWhite">
						<h:column>
								<h:commandLink value="#{assignatura.title}" action="#{assignatura.processaAssignatura}" />
						</h:column>
					</h:dataTable>
	
                        <sakai:button_bar>
                                        <sakai:button_bar_item  action="#{SMSTool.tornaSMS}" value="#{msgs.info_torna}" />
                        </sakai:button_bar>

          </h:form>
        </sakai:view_container>
</f:view>
