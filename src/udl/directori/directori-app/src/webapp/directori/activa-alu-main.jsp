
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8");%>

<link rel="stylesheet" href="css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="css/bootstrap-theme.min.css" rel="stylesheet" />
<link rel="stylesheet" href="css/directori-activa.css" rel="stylesheet" />
<meta name="viewport" content="width=device-width, minimum-scale=0.9999, user-scalable=yes">
<script type="text/javascript" src="/library/js/jquery/jquery-1.9.1.min.js"></script>

<script>
	$(document).ready (function (){
		$(".myForm").submit (function (){
			$(".sendButton").css ("display","none");
			$(".processButton").css ("display","inline-block");
		});
	});
</script>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<div class="row headerudl">
		<div class="col-xs-12 col-sm-6 col-md-6">
			<img class="img-responsive" src="image/logo-udl.png" />
		</div>
		
		<h:form styleClass="myform">
		<div class="col-xs-12 col-sm-6 col-md-6"  >
			<div class="langbar">
				[<h:commandLink  action="#{ActivaTool.canviaIdiomaActivacioAluCa}" value="#{msgs.activaalucatala}"  />]
				[<h:commandLink  action="#{ActivaTool.canviaIdiomaActivacioAluEs}" value="#{msgs.activaaluesp}" />]
				[<h:commandLink  action="#{ActivaTool.canviaIdiomaActivacioAluEng}" value="#{msgs.activaalueng}" />]
			</div>
		</div>
	</h:form>
	</div>
	<div class="container-fluid">
		<h2><h:outputText value="#{msgs.titolactiva}" /></h2>
	
		<p>
		
			<h:outputText escape="false" value="#{msgs.activainfo1}"/>
			<ol>
				<li><h:outputText escape="false" value="#{msgs.activainfo2}"/></li>
				<li><h:outputText escape="false" value="#{msgs.activainfo3}"/></li>
				<li><h:outputText escape="false" value="#{msgs.activainfo4}"/></li>
			</ol>
		<h:outputText escape="false" value="#{msgs.activaalusuport}"/> 
		</p>
		<div class="row bottom-buffer">
			<div class="col-xs-1 col-sm-2 col-md-2"></div>
			<div class="col-xs-11 col-sm-8 col-md-8"><img class="img-thumbnail img-responsive" src="image/resaltat-amb-dni.png" /></div>
			<div class="col-xs-1 col-sm-2 col-md-2"></div>
		</div>
		
		
			<h:panelGroup rendered="#{ActivaTool.errorMsg != null && ActivaTool.errorMsg != ''}" styleClass="col-xs-12 alert alert-danger"> <h:outputText escape="false" value="#{ActivaTool.errorMsg}" /></h:panelGroup>
			<h:form styleClass="myForm">
			
					<div class="row">
	  					<div class="col-xs-12 col-sm-6 col-md-6">
	  					 	<div class="form-group">
	  					 		<label> <h:outputText value="#{msgs.activaaluident}" /></label>
	  					 		<h:inputText styleClass="form-control" value="#{ActivaTool.identificador}" />
	  					 	</div>
	  					</div>
	  					<div class="col-xs-12 col-sm-6 col-md-6">
	  						<div class="form-group">
	  					 		<label> <h:outputText value="#{msgs.activaaluexp}" /></label>
	  					 		<h:inputText styleClass="form-control" value="#{ActivaTool.codi}" />
	  					 	</div>
	  					</div>
	  				</div> 
	  				<div class="row">
	  					<div class="col-xs-12 col-sm-6 col-md-6">
	  						<div class="form-group">
	  					 		<label> <h:outputText value="#{msgs.activapassword}" /></label>
	  					 		<h:inputSecret styleClass="form-control" value="#{ActivaTool.password}" />
	  					 	</div>
	  					</div>
	  					<div class="col-xs-12 col-sm-6 col-md-6">
	  						<div class="form-group">
	  					 		<label> <h:outputText value="#{msgs.activapasswordveri}" /></label>
	  					 		<h:inputSecret styleClass="form-control" value="#{ActivaTool.passwordVerificacio}" />
	  					 	</div>
	  					</div>
	  				</div>
					<div class="btn btn-primary processButton disabled" style="display:none" ><i class="glyphicon glyphicon-repeat normal-right-spinner"></i> Processant... </div>
					<h:commandButton styleClass="btn btn-primary sendButton" action="#{ActivaTool.activaAlu}" value="#{msgs.activaactiva}"/>
			</h:form>
		</div>
</f:view>
		