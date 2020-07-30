<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
        <sakai:view_container title="msgs.title_name">
        <h2><h:outputText value="#{msgs.info_title}"/></h2><br>
        <h:form>
                <table border=0>
                <td>
                        <h:panelGrid columns="2" rowClasses="borderGrayBackground,borderWhite">

                                <h:outputText  value="#{msgs.info_nom}" style="font-weight: bold;"/>
                                <h:outputText  value="#{DirectoriTool.assistent.nom}" />
                                <h:outputText  value="#{msgs.info_cognoms}" style="font-weight: bold;"/>
                                <h:outputText  value="#{DirectoriTool.assistent.cognoms}" />
                                <h:outputText  value="#{msgs.info_email}" style="font-weight: bold;"/>
                                <h:outputLink  value="mailto:#{DirectoriTool.assistent.email}" >
    	                            <h:outputText  value="#{DirectoriTool.assistent.email}" />
	  	                        </h:outputLink>
								<h:outputText  value="#{msgs.info_centre}" style="font-weight: bold;"/>
                                <h:outputText  value="#{DirectoriTool.assistent.centre}" />

						<%--	<h:outputText  value="#{msgs.info_ubicacio}" style="font-weight: bold;"/>
                                <h:outputText  value="#{DirectoriTool.assistent.roomNumber}" />
                                <h:outputText  value="#{msgs.info_tel}" style="font-weight: bold;"/>
                                <h:outputText  value="#{DirectoriTool.assistent.tlf}" /> --%>


                        </h:panelGrid>
                </td></tr>
                </table>
                        <sakai:button_bar>
                                        <sakai:button_bar_item  action="#{DirectoriTool.torna}" value="#{msgs.info_torna}" />
                        </sakai:button_bar>

          </h:form>
        </sakai:view_container>
</f:view>
