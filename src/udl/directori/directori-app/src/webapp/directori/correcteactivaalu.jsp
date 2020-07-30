<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>
<link rel="stylesheet" href="css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="css/bootstrap-theme.min.css" rel="stylesheet" />
<link rel="stylesheet" href="css/directori-activa.css" rel="stylesheet" />
<meta name="viewport" content="width=device-width, minimum-scale=0.9999, user-scalable=yes">

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
		<div class="row headerudl">
		<div class="col-xs-12 col-sm-6 col-md-6">
			<img class="img-responsive" src="image/logo-udl.png" />
		</div>
		
		<h:form>
		<div class="col-xs-12 col-sm-6 col-md-6"  >
		</div>
	</h:form>
	</div>
		
		<div class="container-fluid">
			<h2><h:outputText value="#{msgs.titolactiva}" /></h2>
	
			<div class="col-xs-12 alert alert-success"><h:outputText value="#{msgs.activaaluok}" escape="false" /> <strong><h:outputText value="#{ActivaTool.login}" /></strong></div>
				
				<div class="row">
					<div class="col-xs-12 col-sm-6 col-md-6">
					 	<div class="form-group">
					 		<label> <h:outputText value="#{msgs.nom}" /></label>
					 		<h:outputText  value="#{ActivaTool.nom}" />
					 	</div>
					</div>
					<div class="col-xs-12 col-sm-6 col-md-6">
						<div class="form-group">
					 		<label> <h:outputText value="#{msgs.cognoms}" /></label>
					 		<h:outputText value="#{ActivaTool.cognoms}" />
					 	</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-xs-12 col-sm-6 col-md-6">
					 	<div class="form-group">
					 		<label> <h:outputText value="#{msgs.activalogin}" /></label>
					 		<h:outputText  value="#{ActivaTool.login}" />
					 	</div>
					</div>
					<div class="col-xs-12 col-sm-6 col-md-6">
						<div class="form-group">
					 		<label> <h:outputText value="#{msgs.correuprincipal}" /></label>
					 		<h:outputText value="#{ActivaTool.emailComplet}" />
					 	</div>
					</div>
				</div>
	
				<br />
				<div class="row">
					<div class="col-xs-12 col-sm-12 col-md-12">
						<h:outputText escape="false" value="#{msgs.missatgeactivafinal}" />
					</div>
					<div class="col-xs-12 col-sm-12 col-md-12">
						<button  class="btn btn-primary center-block" onclick="window.print();" ><h:outputText value="#{msgs.print}" /></button>
					</div
				</div>
				
			</div>
			
</f:view>

<script>window.parent.postMessage('hello','http://activacio.udl.cat');</script>