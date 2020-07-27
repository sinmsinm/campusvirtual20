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
 * Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat)  and usuaris-cvirtual@llistes.udl.cat
 * Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
 *
 **/
package cat.udl.asic.datacollector.impl.component;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.Event;
import cat.udl.asic.datacollector.api.entity.Query;
import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.service.ImportService;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

public class DomImportImpl implements ImportService {

	private static Log M_log = LogFactory.getLog(DomImportImpl.class);

	private String blocksPath = "";
	private String sectionsPath = "";
	private String sectionSchemaFile = "";
	private String blockSchemaFile = "";

	public String getSectionSchemaFile() {
		return sectionSchemaFile;
	}

	public String getBlockSchemaFile() {
		return blockSchemaFile;
	}

	public void setSectionSchemaFile(String sectionSchemaFile) {
		this.sectionSchemaFile = sectionSchemaFile;
	}

	public void setBlockSchemaFile(String blockSchemaFile) {
		this.blockSchemaFile = blockSchemaFile;
	}

	public Block loadBlock(String blockFileName) throws java.io.IOException {
		Block b = null;
		Properties blockProps = null;
		// Load Mapping

		blockFileName = blockFileName.replaceAll("_", "/");
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File xmlDocument = new File(blocksPath + blockFileName + ".xml");
			InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));
			Document document = builder.parse(inputSource);

			Element block = document.getDocumentElement();

			b = new Block();
			b.setId(block.getAttribute("id"));
			b.setName(block.getAttribute("name"));
			b.setDescription(block.getAttribute("description"));
			b.setLongDescription(block.getAttribute("longdescription"));

			Element blockproperties = (Element) block.getElementsByTagName("properties").item(0);
			if (blockproperties != null && blockproperties.hasChildNodes()) {

				NodeList propertiesList = blockproperties.getElementsByTagName("property");

				blockProps = new Properties();

				for (int i = 0; i < propertiesList.getLength(); i++) {
					Element prop = (Element) propertiesList.item(i);
					blockProps.put(prop.getAttribute("id"), prop.getAttribute("value"));
				}
				b.setExtraProperties(blockProps);
			}

			/* S'hauria de mirar si hem de passar tota la section o no? */
			Element sections = (Element) block.getElementsByTagName("sections").item(0);

			List<String> sectionIdList = new ArrayList<String>();

			for (int i = 0; i < sections.getElementsByTagName("section").getLength(); i++) {
				Element section = (Element) sections.getElementsByTagName("section").item(i);
				sectionIdList.add(section.getAttribute("id"));
			}

			b.setSectionIdList(sectionIdList);

			return b;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new java.io.IOException("Error al convertir el XML");
		}
	}

	public Section loadSection(String sectionFileName) throws java.io.IOException {
		Properties sectionProps = null;
		Section s = null;

		/* transform to directories structure for better classification */
		sectionFileName = sectionFileName.replaceAll("_", "/");

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File xmlDocument = new File(sectionsPath + sectionFileName + ".xml");
			InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));
			Document document = builder.parse(inputSource);

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			File schemaFile = new File(sectionSchemaFile);
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			validator.validate(new javax.xml.transform.dom.DOMSource(document));

			/* first we put the attributes */
			s = new Section();
			Element section = document.getDocumentElement();
			s.setId(section.getAttribute("id"));
			s.setDescription(section.getAttribute("description"));
			s.setName(section.getAttribute("name"));
			s.setLongDescription(section.getAttribute("longdescription"));

			Element secproperties = (Element) section.getElementsByTagName("properties").item(0);
			if (secproperties != null && secproperties.hasChildNodes()) {

				NodeList propertiesList = secproperties.getElementsByTagName("property");

				sectionProps = new Properties();
				for (int i = 0; i < propertiesList.getLength(); i++) {
					Element prop = (Element) propertiesList.item(i);
					sectionProps.put(prop.getAttribute("id"), prop.getAttribute("value"));
				}
				s.setExtraProperties(sectionProps);
			}

			Map<String, DataSource> dataSourceList = new HashMap<String, DataSource>();

			/* Next the datasources */
			Element DataSources = (Element) section.getElementsByTagName("datasources").item(0);

			for (int i = 0; i < DataSources.getElementsByTagName("datasource").getLength(); i++) {
				Element DS = (Element) DataSources.getElementsByTagName("datasource").item(i);

				String type = DS.getAttribute("type");
				DataSource dataSource = null;

				if ("simple.value".equals(type) || "collection.value".equals(type)) {
					dataSource = readSimpleDataSource(DS);
				} else if ("authz.value".equals(type)) {
					dataSource = readSimpleDataSource(DS);
				} else if ("scheduler.value".equals(type)) {
					dataSource = readSimpleDataSource(DS);
				} else if ("event.value".equals(type)) {
					dataSource = readEventDataSource(DS);
				}

				dataSourceList.put(dataSource.getId(), dataSource);
			}
			Element viewElement = (Element) section.getElementsByTagName("view").item(0);
			s.setDataSourceMap(dataSourceList);

			/* LAST the view */
			s.setViewTemplate(viewElement.getAttribute("templateSrc"));
			s.setViewMessages(viewElement.getAttribute("messagesSrc"));

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new java.io.IOException("Error transforming  XML");
		}

		return s;

	}

	private DataSource readSimpleDataSource(Element DS) {
		Map<String, DataSourceColumn> columns = null;
		List<Parameter> parameters = null;

		/* GET COLUMNS IF IT IS COMPOSED TYPE */
		/* Recollect the atributes */
		String sourceId = DS.getAttribute("id");
		String description = DS.getAttribute("description");
		String tableSrc = DS.getAttribute("tableSrc");
		String columnSrc = DS.getAttribute("columnName");
		String type = DS.getAttribute("type");
		String read = "true";
		String upd = "false";
		String dlt = "false";
		String escHtml = "true";

		if (DS.hasAttribute("readable")) {
			read = DS.getAttribute("readable");
		}
		boolean readable = Boolean.valueOf(read);

		if (DS.hasAttribute("updatable")) {
			upd = DS.getAttribute("updatable");
		}
		boolean updatable = Boolean.valueOf(upd);
		if (DS.hasAttribute("deletable")) {
			dlt = DS.getAttribute("deletable");
		}
		boolean deletable = Boolean.valueOf(dlt);

		if (DS.hasAttribute("escapeHtml")) {
			escHtml = DS.getAttribute("escapeHtml");
		}
		boolean escapeHtml = Boolean.valueOf(escHtml);

		String providerId = DS.getAttribute("providerId");
		if (providerId == null || providerId.equals("")) {
			providerId = "default";
		}

		Element DataSourcesColumns = (Element) DS.getElementsByTagName("datasourcecolumns").item(0);
		if (type.equals(DataSource.COMPOSED_TYPE)) {
			columns = new LinkedHashMap<String, DataSourceColumn>();

			for (int j = 0; j < DataSourcesColumns.getElementsByTagName("datasourcecolumn").getLength(); j++) {

				Element SubDS = (Element) DataSourcesColumns.getElementsByTagName("datasourcecolumn").item(j);
				String dcId = SubDS.getAttribute("id");
				String dccolName = SubDS.getAttribute("columnName");
				String dccolDescription = SubDS.getAttribute("description");

				columns.put(dcId, new DataSourceColumn(dcId, dccolName, dccolDescription));
			}
		}

		Element parametersElements = (Element) DS.getElementsByTagName("parameters").item(0);
		parameters = new ArrayList<Parameter>();

		for (int k = 0; k < parametersElements.getElementsByTagName("parameter").getLength(); k++) {

			Element parameter = (Element) parametersElements.getElementsByTagName("parameter").item(k);

			String paramId = parameter.getAttribute("id");
			String defaultValue = parameter.getAttribute("defaultValue");
			String column = parameter.getAttribute("columnName");

			/*
			 * Take care with defaultValue, now is String but it could be an
			 * integer or date, anything
			 */

			Parameter p = new Parameter(paramId, defaultValue, column);
			parameters.add(p);
		}

		DataSource dataSource = new DataSource(sourceId, description, type, updatable, escapeHtml, tableSrc, columnSrc, providerId, columns, parameters);
		dataSource.setParameterList(parameters);
		return dataSource;
	}

	private DataSource readEventDataSource(Element DS) {

		/* GET COLUMNS IF IT IS COMPOSED TYPE */
		/* Recollect the atributes */
		String sourceId = DS.getAttribute("id");
		String description = DS.getAttribute("description");
		String type = DS.getAttribute("type");

		String read = "true";
		String upd = "false";
		String dlt = "false";
		String escHtml = "true";

		if (DS.hasAttribute("readable")) {
			read = DS.getAttribute("readable");
		}
		boolean readable = Boolean.valueOf(read);
		if (DS.hasAttribute("updatable")) {
			upd = DS.getAttribute("updatable");
		}
		boolean updatable = Boolean.valueOf(upd);
		if (DS.hasAttribute("deletable")) {
			dlt = DS.getAttribute("deletable");
		}
		boolean deletable = Boolean.valueOf(dlt);
		
		if (DS.hasAttribute("escapeHtml")) {
			escHtml = DS.getAttribute("escapeHtml");
		}
		boolean escapeHtml = Boolean.valueOf(escHtml);

		
		String providerId = DS.getAttribute("providerId");

		if (providerId == null || providerId.equals("")) {
			providerId = "default";
		}

		Element events = (Element) DS.getElementsByTagName("events").item(0);

		DataSource dataSource = new DataSource(sourceId, description, type, readable, updatable, deletable, escapeHtml, providerId);
		M_log.debug("Detected " + events.getElementsByTagName("event").getLength() + " events");
		/* For each event */
		for (int i = 0; i < events.getElementsByTagName("event").getLength(); i++) {

			Element event = (Element) events.getElementsByTagName("event").item(i);
			String eventType = event.getAttribute("type");
			String eventFlow = event.getAttribute("flowMode");

			M_log.debug("Reading event " + 1 + " type " + eventType + " flow " + eventFlow);

			LinkedHashMap<String, Query> queriesMap = new LinkedHashMap<String, Query>();
			/* For each query */

			M_log.debug("Detected " + event.getElementsByTagName("query").getLength() + " queries");

			for (int j = 0; j < event.getElementsByTagName("query").getLength(); j++) {

				Element query = (Element) event.getElementsByTagName("query").item(j);

				String id = query.getAttribute("id");
				String output = query.getAttribute("output");
				String statement = query.getAttribute("statement");
				String columnKey = query.getAttribute("columnName");
				String onSuccess = query.getAttribute("onSuccess");
				String onError = query.getAttribute("onFail");
				String onEmpty = query.getAttribute("onEmpty");
				String onNonEmpty = query.getAttribute("onNonEmpty");
				int qtype = Query.T_READ;

				if (query.hasAttribute("type") && "write".equals(query.getAttribute("type"))) {
					qtype = Query.T_WRITE;
				} else if (query.hasAttribute("type") && "reference".equals(query.getAttribute("type"))) {
					qtype = Query.T_REFERENCE;
				}

				/* for each parameter */

				Element parametersElements = (Element) query.getElementsByTagName("parameters").item(0);
				List<Parameter> parameters = new ArrayList<Parameter>();

				for (int k = 0; k < parametersElements.getElementsByTagName("parameter").getLength(); k++) {

					Element parameter = (Element) parametersElements.getElementsByTagName("parameter").item(k);

					String paramId = parameter.getAttribute("id");
					String defaultValue = parameter.getAttribute("defaultValue");
					// String column = parameter.getAttribute ("columnName");
					String from = parameter.getAttribute("from");
					/*
					 * Take care with defaultValue, now is String but it could
					 * be an integer or date, anything
					 */

					Parameter p = new Parameter(paramId, defaultValue, from, null);
					parameters.add(p);
				}

				/* for each datasourcecolumn */
				Map<String, DataSourceColumn> columns = new LinkedHashMap<String, DataSourceColumn>();
				Element DataSourcesColumns = (Element) query.getElementsByTagName("datasourcecolumns").item(0);
				for (int l = 0; l < DataSourcesColumns.getElementsByTagName("datasourcecolumn").getLength(); l++) {

					Element SubDS = (Element) DataSourcesColumns.getElementsByTagName("datasourcecolumn").item(l);
					String dcId = SubDS.getAttribute("id");
					String dccolName = SubDS.getAttribute("columnName");
					String dccolDescription = SubDS.getAttribute("description");

					columns.put(dcId, new DataSourceColumn(dcId, dccolName, dccolDescription));
				}
				Query q = new Query(id, output, statement, qtype, columnKey, onSuccess, onError, onEmpty, onNonEmpty, columns, parameters);
				queriesMap.put(q.getId(), q);
			}

			Event currentEvent = new Event(eventType, eventFlow, queriesMap);

			if (Event.GET.equals(eventType)) {
				dataSource.setGetEvent(currentEvent);
			} else if (Event.PUT.equals(eventType)) {
				dataSource.setUpdateEvent(currentEvent);
			} else if (Event.DELETE.equals(eventType)) {
				dataSource.setDeleteEvent(currentEvent);
			}
		}

		return dataSource;
	}

	public String getBlocksPath() {
		return blocksPath;
	}

	public String getSectionsPath() {
		return sectionsPath;
	}

	public void setBlocksPath(String blocksPath) {
		this.blocksPath = blocksPath;
	}

	public void setSectionsPath(String sectionsPath) {
		this.sectionsPath = sectionsPath;
	}

	public List<String> getBlockList() throws java.io.IOException {
		File dir = new File(getBlocksPath());
		List fileList = new ArrayList();

		String[] children = dir.list();
		if (children == null) {
			// Either dir does not exist or is not a directory
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];
				if (filename.endsWith("xml")) {
					fileList.add(filename.substring(0, filename.length() - 4));
				}
			}
		}
		return fileList;
	}

}
