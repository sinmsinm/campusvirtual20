<!--[if IE]>  
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<![endif]--> 
<!--[if !IE]>  
	<?xml version="1.0" encoding="UTF-8" ?>  
<![endif]-->  

<%--
/**
 * Copyright (c) 2010 Universitat de Lleida
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
* Authors: Alex Ballesté
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat) and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/ 
 --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>
<%@ page import="cat.udl.asic.datacollector.api.service.InitializeService"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.sakaiproject.component.cover.ComponentManager" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<html>
	<head>
<!--  Fluid library -->
<script type="text/javascript" src="./javascript/Fluid-all.js" ></script> 

<!--  jQuery libraries -->
<link type="text/css" href="./javascript/jquery_ui/css/custom-theme/jquery-ui-1.7.2.custom.css" rel="stylesheet" />
<script src ="./javascript/jquery-1.3.2.min.js" type="text/javascript" ></script>
<script src="./javascript/jquery_ui/js/jquery-ui-1.7.2.custom.min.js" type="text/javascript" ></script>
<script src="./javascript/jquery_ui/development-bundle/ui/i18n/ui.datepicker-ca.js" type="text/javascript" ></script>

<!--  Template Trimpath library -->
<script src="./javascript/trimpath-template-1.0.38.js" type="text/javascript" ></script>

<!--  jQuery Plugins to sort tables an navigate -->
<link rel="stylesheet" href="./javascript/tablesorter/themes/blue/style.css" type="text/css" media="print, projection, screen" />
<link rel="stylesheet" href="./javascript/tablesorter/addons/pager/jquery.tablesorter.pager.css" type="text/css" media="print, projection, screen" />
<script src="./javascript/tablesorter/tablesorter_2_03_filter.js" type="text/javascript" ></script>
<script src="./javascript/tablesorter/addons/pager/jquery.tablesorter.pager.js" type="text/javascript" ></script>
<script src="./javascript/tablesorterfilter/tablesorter_filter.js" type="text/javascript" ></script>
 
 
<!--  Filter libray -->
<script src="./javascript/filterTable.js" type="text/javascript" ></script> 

<!--  jQuery validation Plugin  -->
<script src="./javascript/jquery-validate/jquery.validate.js" type="text/javascript" ></script>

<!--  Plugin to convert javascript objects to JSON format -->
<script src="javascript/jquery.json-2.2.min.js" type="text/javascript" ></script>

<!--  Plugin to encode data to prevent xss atacks - enabled only in the applyClass.js action to avoid bad actions instead of class names -->
 <script src="./javascript/Class.create.js" type="text/javascript"></script>  
 <script src="./javascript/jquery-encoder-0.1.0.js" type="text/javascript"></script>

<!--  DataCollector libraries -->
<script src="./javascript/dataAccess.js" type="text/javascript" ></script>
<script src="./javascript/jquery.TemplateLoader.js" type="text/javascript" ></script>
<script src="./javascript/screenManager.js" type="text/javascript" ></script>
<script src="./javascript/validator.js" type="text/javascript" ></script>

<%
	
    cat.udl.asic.datacollector.api.service.InitializeService initializer = (cat.udl.asic.datacollector.api.service.InitializeService) ComponentManager.get ("cat.udl.asic.datacollector.api.service.InitializeService");
    String mainview = org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getConfig().getProperty("mainview");
	String toolParameters =  org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getConfig().getProperty("parameters");
	String siteId = org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getContext(); 
	
	if (toolParameters != null && !toolParameters.equals("")){
	String [] parameters = toolParameters.split(";");
	
		for (String parameter : parameters){
				String [] parts = parameter.split("=");	
				String key = parts[0];
				String value = parts[1];
				initializer.addProperty(siteId,key,value);
			}
	}
%>
	
	<script type="text/javascript">
		  var currentSiteId = '<%= siteId %>';
		  $(document).ready(function(){
		  		getTemplate ('<%= mainview %>',null,'#mainapp', function(){});
		  });
					
	</script> 
</head>
<body>
 	<div id="mainapp" />
</body>
</html>
