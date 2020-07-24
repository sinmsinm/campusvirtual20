package cat.udl.asic.pentaho;

import java.io.IOException;

import javax.servlet.ServletException;
import java.io.IOException; 
import javax.servlet.*; 
import javax.servlet.http.*; 

import java.net.URL;
import java.util.Properties;


import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;

public class ReportProvider extends HttpServlet {

	private Properties reportServiceProperties = null; 
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config); 
		ClassicEngineBoot.getInstance().start();
	} 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
					try {
				// load report definition
				Properties accessProperties = new Properties();
				
				ResourceManager manager = new ResourceManager();
				manager.registerDefaults();
				String reportId = req.getParameter("reportId");
				
				if (reportId==null){
					resp.sendError(505);
				}
				
				String reportPath = "file:"+ ToolManager.getCurrentPlacement().getConfig().getProperty("reposPath") +"/"+ reportId +".prpt";

				Resource res = manager.createDirectly(new URL(reportPath), MasterReport.class);
				MasterReport report = (MasterReport) res.getResource();
				
				//init parameters
				try{
					User currentUser = UserDirectoryService.getCurrentUser();
					
					if (currentUser!=null && currentUser.getProperties()!=null){
						String dni = (String) currentUser.getProperties().get("dni");
						if (dni != null){
							accessProperties.put("PDNI",dni);
						}
						accessProperties.put("uid",currentUser.getEid());
					}
					
					String toolParameters =  ToolManager.getCurrentPlacement().getConfig().getProperty("parameters");
					String siteId = ToolManager.getCurrentPlacement().getContext(); 
					
					if (toolParameters != null && !toolParameters.equals("")){
					String [] parameters = toolParameters.split(";");
					
						for (String parameter : parameters){
								String [] parts = parameter.split("=");	
								String key = parts[0];
								String value = parts[1];
								
								//Added this code to override the parameters injected
								if (value!=null && "ALLOW_FROM_GET".equals(value)){
									String repo_get =  (String) req.getParameter (key);
									value = repo_get;
								}
								
								accessProperties.put(key,value);
							}
					}
				}catch (Exception ex){
						ex.printStackTrace ();
				}
				
					for (int i=0;i<report.getParameterDefinition().getParameterCount();i++){
						String columnName = report.getParameterDefinition().getParameterDefinition(i).getName();
						report.getParameterValues().put(columnName,accessProperties.get(columnName));
				}


				String outputFormat = req.getParameter("outputFormat");
				if ("pdf".equals(outputFormat)) {
					// render in pdf
					resp.setContentType("application/pdf");
					PdfReportUtil.createPDF(report, resp.getOutputStream());
				} else if ("xls".equals(outputFormat)) {
					// render in excel
					resp.setContentType("application/vnd.ms-excel");
					ExcelReportUtil.createXLS(report, resp.getOutputStream());
				} else {
					// render in rtf
					resp.setContentType("application/rtf");
					RTFReportUtil.createRTF(report, resp.getOutputStream());
				}

			} catch (ResourceException e) {
				e.printStackTrace();
			} catch (ReportProcessingException e) {
				e.printStackTrace();
			}
			
			
	}

	
	
	
}
