<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />

<f:view>
<sakai:view_container title="#{msgs.sample_title}">

	<h:form>
		<jsp:include page="toolbar.jsp" />
						
		<center>
			<table width="50% "bgcolor="#f0f0f0" border=0>            
			<tr><td><p align="justify">		
			
				<table cellspacing=0 cellpadding=0 border=0 align='center'>
				<tr><td>  
					<p valign="middle" align="center">
						<br><br>
						<h:outputText value="#{msgs.text_preg_frequents}" style="font-weight: bold; font-size: 15;"/> <br><br>
						<h:outputText value="#{msgs.text_preg_frequents3}" style="font-weight: bold; font-size: 15;"/> <br><br><br>
						<f:verbatim><a  accesskey="h" href="javascript:;" onclick="window.open('</f:verbatim>	<h:outputText value="#{HDesk.urlFaq}" /><f:verbatim>','Preguntes Freqüents','resize=yes,toolbar=no,scrollbars=yes, width=800,height=600')" onkeypress="window.open('</f:verbatim><h:outputText value="#{HDesk.urlFaq}" /><f:verbatim>','Preguntes Freqüents','resize=yes,toolbar=no,scrollbars=yes, width=800,height=600')"></f:verbatim>
							<h:outputText value="#{msgs.text_preg_frequents4}" style="font-weight: bold; font-size: 18;"/> <br>
						</a><br>
					</p>
				</td></tr>
				</table>	
			
			</p></td></tr>
			</table>
			
		</center>
				
		</h:form>

</sakai:view_container>
</f:view>
