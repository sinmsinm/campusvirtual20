<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<%	
	response.setContentType("text/html; charset=UTF-8");
	response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
	response.addDateHeader("Last-Modified", System.currentTimeMillis());
	response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
	response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />


<f:view>
<sakai:view_container title="#{msgs.sample_title}" rendered="#{HDesk.perfil=='support'}">
	<h:form>
	<jsp:include page="toolbar.jsp" />
	<br><br>

  <table width="90%" align="center" cellpadding=0 cellspacing=0 border=0>
                <tr><td align="center"><h3><h:outputText value="#{msgs.text_gest_tipus_assist}"/></h3></td></tr>

                <tr><td>
                <p valign="middle" align="center">
					<h:outputText value="#{msgs.text_gest_tipus_assist5}" style="font-weight: bold;"/>
				</p>
												
		<table width="60%" bgcolor="#f0f0f0" border=0 align="center">                
                <tr><td>
                	<h:outputText value="#{msgs.usuari}" /> 
                	<h:outputText value="#{msgs.administrador}" style="font-weight: bold; font-size: 15;"/><br><br> 
                	
                </td></tr>
                
                <tr><td><p align="justify">
                
                <table width="100%" bgcolor="#dddfe4" border=0>
                <tr><td>
                          <h:outputText value="#{msgs.text_gest_tipus_assist1}" />    
                </td></tr>   
                <tr><td>
                	<table width="100%" bgcolor="#f0f0f0" border=0>
                	<tr><td valign="middle">
                	<h:outputText value="#{msgs.text_gest_tipus_assist3}" /> <br>  
                	<h:selectOneMenu value="#{HDesk_support.categ_sel}" valueChangeListener="#{HDesk_support.setCateg_sel}" onchange="submit()" immediate="true">
					   	    <f:selectItems value="#{HDesk_support.categories}"/>
					</h:selectOneMenu>
					
                		</td><td valign="middle">
                		<sakai:button_bar>
							<sakai:button_bar_item value="#{msgs.boto_editar_desar}" action="#{HDesk_support.processActionEditarCat}"/>
							<sakai:button_bar_item value="#{msgs.boto_esborrar}" action="#{HDesk_support.processActionEsborrarCat}"/>
						</sakai:button_bar>
                		                		</td>
												</tr>
												<tr>
													<td colspan="2">
														<h:inputTextarea id="contingut" value="#{HDesk_support.descripcio}" cols="90" rows="8" />
													</td>
													<td></td>
												</tr>
											</table>
                	
					        </td></tr>
                </table>
         		
         		</p></td></tr>
         		
         		
         		<tr><td>	<br>	</td></tr>
         		
         		
         		<tr><td><p align="justify">
                
                <table width="100%" bgcolor="#dddfe4" border=0>
                <tr><td>
                          <h:outputText value="#{msgs.text_gest_tipus_assist2}" />  
                </td></tr>   
                <tr><td>
                	<table width="100%" bgcolor="#f0f0f0" border=0>
                	<tr><td valign="middle">
                	<h:outputText value="#{msgs.text_gest_tipus_assist6}" /> <br>   
                	<h:inputText value="#{HDesk_support.nou_tipus}" required="false" size="47" /></td><td valign="middle">
					<sakai:button_bar>
							<sakai:button_bar_item action="#{HDesk_support.processActionCrearCat}" value="#{msgs.boto_crear_tipus}"/><br><br>
					</sakai:button_bar></td></tr><tr><td colspan="2">
					<h:outputText value="#{msgs.text_gest_tipus_assist4}" /> <br>    
                	<h:inputTextarea value="#{HDesk_support.nova_descripcio}"  required="false" cols="90" rows="8" /><br>
                	</td><td></td></tr>  
                	<tr><td>	<br>	</td></tr>
                	</table>
                </td></tr>     
                </table>
         		</p></td></tr>
         		
         		</table>
             </td></tr>
          </table>

</h:form>
</sakai:view_container>
<sakai:view_container rendered="#{HDesk.perfil!='support'}">
		<center><br><br>
				  <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" /><br>
		</center> 
	</sakai:view_container>
</f:view>
