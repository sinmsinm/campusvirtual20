/**
 * Copyright (c) 2011 Universitat de Lleida
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
 * Authors: Alex Ballesté,
 * Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat) and usuaris-cvirtual@llistes.udl.cat
 * Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
 *
 **/

package cat.udl.asic.datacollector.impl.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Event;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.Query;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataCollectorService;
import cat.udl.asic.datacollector.api.service.DataSourceService;

//localized driver
import oracle.sql.TIMESTAMP;

public class EventDBDataSourceServiceImpl extends DBDataSourceServiceImpl {

	private static Log M_log = LogFactory.getLog(EventDBDataSourceServiceImpl.class);
	private DataCollectorService dataCollectorService;

	private Connection transConnection = null;
	private SectionInfo currentSection;

	@Override
	public boolean deleteDataSourceValue(SectionInfo currentSection, DataSource dataSource, String key) {

		if (dataSource.isDeletable()) {
			M_log.debug("Datasource is updatable " + dataSource.getId());
			Object retObject = null;
			Event dltEvent = dataSource.getDeleteEvent();

			if (dltEvent != null) { // Implies it has an event to get
				String flow = dltEvent.getFlowMode();
				M_log.debug("dltDataSourceValue:The event is working in " + flow + " mode");
				if ("transaction".equals(flow)) {
					retObject = execTransaction(dltEvent, currentSection,dataSource, null, key);
				} else if ("sequence".equals(flow)) {
					retObject = execSequence(dltEvent, currentSection,dataSource, null, key);
				}
				
				if (retObject != null && !(retObject instanceof Boolean)){
					return true;
				}else if (retObject != null && retObject instanceof Boolean){
					return (Boolean) retObject;
				}
			}
		}
		return false;
	}

	@Override
	public DataSourceValue getDataSourceValue(SectionInfo currentSection, DataSource dataSource) {

		if (dataSource.isReadable()) {
			M_log.debug("Datasource is readable " + dataSource.getId());

			Event getEvent = dataSource.getGetEvent();

			if (getEvent != null){ // Implies it has an event to get
				String flow = getEvent.getFlowMode();
				M_log.debug("getDataSourceValue:The event is working in " + flow + " mode");
				if ("transaction".equals(flow)) {
					return (DataSourceValue) execTransaction(getEvent, currentSection,dataSource, null, null);
				} else if ("sequence".equals(flow)) {
					return (DataSourceValue) execSequence(getEvent, currentSection,dataSource, null, null);
				}
			}
		}
		return null;
	}

	@Override
	public boolean saveDataSourceValue(SectionInfo currentSection, DataSource dataSource, DataSourceValue dataSourceValue) {
		
		if (dataSource.isUpdatable()) {
			M_log.debug("Datasource is updatable " + dataSource.getId());

			Event updEvent = dataSource.getUpdateEvent();
			Object retObject = null;
			if (updEvent != null) { // Implies it has an event to get
				String flow = updEvent.getFlowMode();
				M_log.debug("updDataSourceValue:The event is working in " + flow + " mode");
				
				if ("transaction".equals(flow)) {
					retObject = execTransaction(updEvent, currentSection,dataSource, dataSourceValue, null);
				} else if ("sequence".equals(flow)) {
					retObject = execSequence(updEvent, currentSection,dataSource, dataSourceValue, null);
				}
				
				if (retObject != null && !(retObject instanceof Boolean)){
					return true;
				}else if (retObject != null && retObject instanceof Boolean){
					return (Boolean) retObject;
				}
			}
		}

		return false;
	}

	protected Connection getSimpleConnection() {
		Connection c = null;
		try {
			c = getSqlService().borrowConnection();
			c.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	private void returnConnection(Connection conn) {
		getSqlService().returnConnection(conn);
	}

	private Connection beginTransaction() {
		try {
			Connection c = getSqlService().borrowConnection();
			c.setAutoCommit(false);
			return c;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Close connection
	private void finishTransaction(Connection conn, boolean commit) {
		try {
			if (commit) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}

		getSqlService().returnConnection(conn);
		conn = null;
	}

	private Object execSequence(Event event, SectionInfo currentSection, DataSource curDataSource, DataSourceValue userValue, String key) {
		try {

			M_log.debug("getDataSourceValue:Transaction is started");
			Map queryTemporalValues = new LinkedHashMap<String, Parameter>();

			if (key != null) {
				Parameter deleteKey = new Parameter("deleteKey", key, null);
				queryTemporalValues.put("deleteKey", deleteKey);
			}
			Iterator<Query> itq = event.getQueryMap().values().iterator();
			Query currentQuery = null;
			Query nextQuery = null;

			if (itq.hasNext()) {
				nextQuery = itq.next();

				do {
					if (nextQuery== null || (currentQuery!=null && currentQuery.equals(nextQuery))){
						break;
					}
					currentQuery = nextQuery;
					String onSuccess = currentQuery.getOnSuccess();
					String onEmpty = currentQuery.getOnEmpty();
					String onNonEmpty = currentQuery.getOnNonEmpty();
					String onError = currentQuery.getOnError();

					QueryState qState = new QueryState(false, true);
					M_log.debug("Start processing query " + currentQuery.getId() + " on mode " + currentQuery.getType());
					DataSourceValue dsv = null;

					if (currentQuery.getType() == Query.T_READ) {
						Connection conn = getSimpleConnection();
						dsv = executeQuery(currentQuery, currentSection,curDataSource, userValue, queryTemporalValues, conn, qState);
						returnConnection(conn);

						if (dsv != null) {
							M_log.debug("getDataSourceValue:execute query successfully and has a valid result");
							M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
							M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());

						} else {
							M_log.debug("getDataSourceValue:execute query successfully and has an  INvalid result");
							M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
							M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());
						}

						if (qState.isSuccess()) {
							/* Store the result on sessioninfo */
							if ("session".equals(currentQuery.getOutput())) {
								Iterator it = dsv.getValue().values().iterator();
								if (it.hasNext()) {
									M_log.debug("getDataSourceValue:Storing the result on secction " + currentSection.getId());
									DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
									for (DataSourceColumn dc : currentQuery.getColumns().values()) {
										Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getId()), "");
										currentSection.addParameter(p);
										M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
									}
								}
							}else if ("usession".equals(currentQuery.getOutput())) {
								Iterator it = dsv.getValue().values().iterator();
								if (it.hasNext()) {
									Session session = SessionManager.getCurrentSession();
									M_log.debug("getDataSourceValue:Storing the result on user session " + currentSection.getId());
									DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
									for (DataSourceColumn dc : currentQuery.getColumns().values()) {
										Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getId()), "");
										session.setAttribute (p.getId(),p.getValue());
										currentSection.addParameter(p);
										M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
									}
								}
							}else if ("query".equals(currentQuery.getOutput())) {
								/* Store the result on query variable */
								Iterator it = dsv.getValue().values().iterator();
								if (it.hasNext()) {
									M_log.debug("getDataSourceValue:Storing the result on query " + currentSection.getId());
									DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
									for (DataSourceColumn dc : currentQuery.getColumns().values()) {
										Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getId()), "");
										queryTemporalValues.put(p.getId(), p);
										M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
									}
								}

							} else if ("return".equals(currentQuery.getOutput())) {
								M_log.debug("getDataSourceValue:Returning DataSourceValue on estandard output");
								return dsv;
							}

							M_log.debug("onSuccess " + onSuccess);
							M_log.debug("onEmpty " + onEmpty);
							M_log.debug("onNonEmpty " + onNonEmpty);

							if (onSuccess != null && !"".equals(onSuccess)) {
								if (":return".equals(onSuccess)) {
									return dsv;
								} else if (":error".equals(onSuccess)) {
									return null;
								} else {
									nextQuery = event.getQueryMap().get(onSuccess);
								}

							} else if (onEmpty != null && !"".equals(onEmpty) && qState.isEmtpy()) {
								if (":return".equals(onEmpty)) {
									return dsv;
								} else if (":error".equals(onEmpty)) {
									return null;
								} else {
									nextQuery = event.getQueryMap().get(onEmpty);
								}
							} else if (onNonEmpty != null && !"".equals(onNonEmpty) && !qState.isEmtpy()) {
								if (":return".equals(onNonEmpty)) {
									return dsv;
								} else if (":error".equals(onNonEmpty)) {
									return null;
								} else {
									nextQuery = event.getQueryMap().get(onNonEmpty);
								}
							}
						} else {
							if ("return".equals(currentQuery.getOutput())) {
								return null;
							}

							if (onError != null && !"".equals(onError)) {

								if (":return".equals(onError)) {
									return null;
								} else if (":error".equals(onError)) {
									return null;
								} else {
									nextQuery = event.getQueryMap().get(onError);
								}
							}

							M_log.debug("getDataSourceValue:Not success Value");
						}
					} else if (currentQuery.getType() == Query.T_WRITE) {
						Connection conn = getSimpleConnection();
						boolean updated = updateQuery(currentQuery, currentSection, userValue, queryTemporalValues, conn, qState);
						returnConnection(conn);

						if (updated) {

							if (!qState.isSuccess()) {
								M_log.debug("getDataSourceValue:execute query successfully and has a valid result");
								M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
								M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());
								M_log.debug("getDataSourceValue:Finisht transaction point 2");

								if (onError != null && !"".equals(onError)) {

									if (":return".equals(onError)) {
										return false;
									} else if (":error".equals(onError)) {
										return false;
									} else {
										nextQuery = event.getQueryMap().get(onError);
									}

								}
								M_log.debug("Nothig to do then error");

								return null;
							} else {
								if ("session".equals(currentQuery.getOutput())) {
									Parameter p = new Parameter(currentQuery.getId() + ":success", new Boolean(true), "");
									currentSection.addParameter(p);
									M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
								}else if ("query".equals(currentQuery.getOutput())) {
									Parameter p = new Parameter(currentQuery.getId() + ":success", new Boolean(true), "");
									queryTemporalValues.put(p.getId(), p);
								} else if ("return".equals(currentQuery.getOutput())) {
									return true;
								}

								if (onSuccess != null && !"".equals(onSuccess)) {
									if (":return".equals(onSuccess)) {
										return true;
									} else if (":error".equals(onSuccess)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onSuccess);
									}

								} else if (onEmpty != null && !"".equals(onEmpty) && qState.isEmtpy()) {
									if (":return".equals(onEmpty)) {
										return true;
									} else if (":error".equals(onEmpty)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onEmpty);
									}
								} else if (onNonEmpty != null && !"".equals(onNonEmpty) && !qState.isEmtpy()) {
									if (":return".equals(onNonEmpty)) {
										return true;
									} else if (":error".equals(onNonEmpty)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onNonEmpty);
									}
								}

							}
						} else {
							if (onError != null && !"".equals(onError)) {

								if (":return".equals(onError)) {
									return false;
								} else if (":error".equals(onError)) {
								} else {
									nextQuery = event.getQueryMap().get(onError);
								}
							}
						}
					} else if (currentQuery.getType() == Query.T_REFERENCE) {
						dataCollectorService = (DataCollectorService) ComponentManager.get(DataCollectorService.class);
						Section tempSection = dataCollectorService.getSection(currentSection.getId());
						DataSource dv = tempSection.getDataSource(currentQuery.getStatement());

						if ("get".equals(currentQuery.getColumnKey())) {
							DataSourceValue newdsv = dataCollectorService.getDataSourceValue(currentSection, dv);

							if (newdsv != null) {
								qState.setSuccess(true);
								qState.setEmtpy(newdsv.getValue().isEmpty());
								M_log.debug("getDataSourceValue:execute query successfully and has a valid result");

							} else {
								qState.setSuccess(false);
								M_log.debug("getDataSourceValue:execute query successfully and has an  INvalid result");
							}

							M_log.debug("NEWDSV getDataSourceValue:Success " + qState.isSuccess());
							M_log.debug("NEWDSV getDataSourceValue:Empty " + qState.isEmtpy());

							if (qState.isSuccess()) {
								/* Store the result on sessioninfo */
								if ("session".equals(currentQuery.getOutput())) {
									Iterator it = newdsv.getValue().values().iterator();
									if (it.hasNext()) {
										M_log.debug("getDataSourceValue:Storing the result on secction " + currentSection.getId());
										DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
										for (DataSourceColumn dc : currentQuery.getColumns().values()) {
											Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getColumnName()), "");
											currentSection.addParameter(p);
											M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
										}
									}
								}else if ("query".equals(currentQuery.getOutput())) {
									/* Store the result on query variable */
									Iterator it = newdsv.getValue().values().iterator();
									if (it.hasNext()) {
										M_log.debug("getDataSourceValue:Storing the result on query " + currentSection.getId());
										DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
										for (DataSourceColumn dc : currentQuery.getColumns().values()) {
											Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getColumnName()), "");
											queryTemporalValues.put(p.getId(), p);
											M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
										}
									}

								} else if ("return".equals(currentQuery.getOutput())) {
									M_log.debug("getDataSourceValue:Returning DataSourceValue on estandard output");
									return newdsv;
								}

								M_log.debug("onSuccess " + onSuccess);
								M_log.debug("onEmpty " + onEmpty);
								M_log.debug("onNonEmpty " + onNonEmpty);

								if (onSuccess != null && !"".equals(onSuccess)) {
									if (":return".equals(onSuccess)) {
										return newdsv;
									} else if (":error".equals(onSuccess)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onSuccess);
									}

								} else if (onEmpty != null && !"".equals(onEmpty) && qState.isEmtpy()) {
									if (":return".equals(onEmpty)) {
										return newdsv;
									} else if (":error".equals(onEmpty)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onEmpty);
									}
								} else if (onNonEmpty != null && !"".equals(onNonEmpty) && !qState.isEmtpy()) {
									if (":return".equals(onNonEmpty)) {
										return newdsv;
									} else if (":error".equals(onNonEmpty)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onNonEmpty);
									}
								}
							} else {
								if ("return".equals(currentQuery.getOutput())) {
									return null;
								}

								if (onError != null && !"".equals(onError)) {

									if (":return".equals(onError)) {
										return null;
									} else if (":error".equals(onError)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onError);
									}
								}
								M_log.debug("getDataSourceValue:Not success Value");
								return null;

							}
						} else if ("put".equals(currentQuery.getColumnKey()) || "delete".equals(currentQuery.getColumnKey())) {

							boolean result = false;
							
							
							if ("put".equals(currentQuery.getColumnKey())) {
								/* Set a datasourcevalue from configuration */
								
									DataSourceValueRow newdsvr = new DataSourceValueRow(userValue.getId());
									
									for (DataSourceColumn dc : currentQuery.getColumns().values()) {
									
										if ("all".equals(dc.getId())){
											result = dataCollectorService.saveDataSourceValue(currentSection, dv, userValue);
											break;
										}
										
										newdsvr.addColumnValue(dc.getId(), userValue.getValue().get(dc.getColumnName()));
										M_log.debug("Modifiing attri "+ dc.getId() + " value " + userValue.getValue().get(dc.getColumnName()));
												
									}
									result = dataCollectorService.saveDataSourceValue(currentSection, dv, newdsvr);	
								
							} else {
								result = dataCollectorService.deleteDataSourceValue(currentSection, dv, key);
							}

							qState.setSuccess(result);

							if (!qState.isSuccess()) {
								M_log.debug("getDataSourceValue:execute query successfully and has a valid result");
								M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
								M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());
								M_log.debug("getDataSourceValue:Finisht transaction point 2");

								if (onError != null && !"".equals(onError)) {

									if (":return".equals(onError)) {
										return false;
									} else if (":error".equals(onError)) {
										return false;
									} else {
										nextQuery = event.getQueryMap().get(onError);
									}

								}
								M_log.debug("Nothig to do then error");

								return null;
							} else {
								if ("session".equals(currentQuery.getOutput())) {
									Parameter p = new Parameter(currentQuery.getId() + ":success", new Boolean(true), "");
									currentSection.addParameter(p);
									M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
								} else if ("query".equals(currentQuery.getOutput())) {
									Parameter p = new Parameter(currentQuery.getId() + ":success", new Boolean(true), "");
									queryTemporalValues.put(p.getId(), p);
								} else if ("return".equals(currentQuery.getOutput())) {
									return true;
								}

								if (onSuccess != null && !"".equals(onSuccess)) {
									if (":return".equals(onSuccess)) {
										return true;
									} else if (":error".equals(onSuccess)) {
										return null;
									} else {
										nextQuery = event.getQueryMap().get(onSuccess);
									}

								} else if (onError != null && !"".equals(onError)) {

									if (":return".equals(onError)) {
										return false;
									} else if (":error".equals(onError)) {
										return false;
									} else {
										nextQuery = event.getQueryMap().get(onError);
									}
								}

							}
						}
					}

				} while (!"return".equals(currentQuery.getOutput()));
			}

		} catch (Exception ex) {
			M_log.debug("getDataSourceValue:Exception and return null");
			ex.printStackTrace();
			return null;
		}
		return null;

	}

	private Object execTransaction(Event event, SectionInfo currentSection,DataSource curDataSource, DataSourceValue userValue, String key) {
		try {
			Connection conn = beginTransaction();
			M_log.debug("getDataSourceValue:Transaction is started");
			Map queryTemporalValues = new LinkedHashMap<String, Parameter>();

			if (key != null) {
				Parameter deleteKey = new Parameter("deleteKey", key, null);
				queryTemporalValues.put("deleteKey", deleteKey);
			}

			for (Query query : event.getQueryMap().values()) {
				M_log.debug("Start processing query " + query.getId() + " on mode " + query.getType());
				DataSourceValue dsv = null;
				QueryState qState = new QueryState(false, true);

				if (query.getType() == Query.T_READ) {
					dsv = executeQuery(query, currentSection, curDataSource, userValue, queryTemporalValues, conn, qState);

					if (dsv != null) {
						M_log.debug("getDataSourceValue:execute query successfully and has a valid result");
						M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
						M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());

					} else {
						M_log.debug("getDataSourceValue:execute query successfully and has an  INvalid result");
						M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
						M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());
					}

					if (qState.isSuccess()) {
						/* Store the result on sessioninfo */
						if ("session".equals(query.getOutput())) {

							Iterator it = dsv.getValue().values().iterator();
							if (it.hasNext()) {
								M_log.debug("getDataSourceValue:Storing the result on secction " + currentSection.getId());
								DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
								for (DataSourceColumn dc : query.getColumns().values()) {
									Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getId()), "");
									currentSection.addParameter(p);
									M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
								}
							}
						} else if ("query".equals(query.getOutput())) {
							/* Store the result on query variable */
							Iterator it = dsv.getValue().values().iterator();
							if (it.hasNext()) {
								M_log.debug("getDataSourceValue:Storing the result on query " + currentSection.getId());
								DataSourceValueRow dsvr = (DataSourceValueRow) it.next();
								for (DataSourceColumn dc : query.getColumns().values()) {
									Parameter p = new Parameter(dc.getId(), dsvr.getValue().get(dc.getId()), "");
									queryTemporalValues.put(p.getId(), p);
									M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
								}
							}

						} else if ("return".equals(query.getOutput())) {
							M_log.debug("getDataSourceValue:Returning DataSourceValue on estandard output");
							finishTransaction(conn, true);
							return dsv;
						}
					} else {
						M_log.debug("getDataSourceValue:Finisht transaction point 1");
						finishTransaction(conn, false);
						return null;
					}
				} else if (query.getType() == Query.T_WRITE) {
					if (updateQuery(query, currentSection, userValue, queryTemporalValues, conn, qState)) {

						if (!qState.isSuccess()) {
							M_log.debug("getDataSourceValue:execute query successfully and has a valid result");
							M_log.debug("getDataSourceValue:Success " + qState.isSuccess());
							M_log.debug("getDataSourceValue:Empty " + qState.isEmtpy());
							M_log.debug("getDataSourceValue:Finisht transaction point 2");
							finishTransaction(conn, false);
							return false;
						} else {
							if ("session".equals(query.getOutput())) {
								Parameter p = new Parameter(query.getId() + ":success", new Boolean(true), "");
								currentSection.addParameter(p);
								M_log.debug("getDataSourceValue:Stored Param " + p.getId() + " with value" + p.getValue());
							} else if ("query".equals(query.getOutput())) {
								Parameter p = new Parameter(query.getId() + ":success", new Boolean(true), "");
								queryTemporalValues.put(p.getId(), p);
							} else if ("return".equals(query.getOutput())) {
								finishTransaction(conn, true);
								return true;
							}
						}
					} else {
						finishTransaction(conn, false);
						return false;
					}
				}
			}

			M_log.debug("getDataSourceValue:Finish transaction at point 3");
			finishTransaction(conn, false);

		} catch (Exception ex) {
			M_log.debug("getDataSourceValue:Exception and return null");
			ex.printStackTrace();
			return null;
		}
		return null;

	}

	private DataSourceValue executeQuery(Query query, SectionInfo currentSection, DataSource curDataSource, DataSourceValue userValue, Map queryTemporalValues, Connection curConn, QueryState queryState) {

		String statement = query.getStatement();
		Object objectList[] = new Object[query.getParameterList().size()];

		/* Prepare the object list from parameter settings */
		int paramNum = 0;

		for (Parameter param : query.getParameterList()) {
			Object parvalue = null;
			// The search of parameter must be by name.

			if ("query".equals(param.getFrom())) {
				Parameter paramSec = (Parameter) queryTemporalValues.get(param.getId());
				if (paramSec != null) {
					parvalue = paramSec.getValue();
					M_log.debug("executeQuery: Injectiong value from session " + paramSec.getId() + " value " + parvalue);
				} else {
					M_log.debug("executeQuery: jectiong value from session but there isn't value" + paramSec.getId());
				}
			} else if ("session".equals(param.getFrom())) {
				Parameter paramSec = currentSection.getParameter(param.getId());

				if (paramSec != null) {
					parvalue = paramSec.getValue();
					M_log.debug("executeQuery: Injectiong value from session " + paramSec.getId() + " value " + parvalue);
				} else {
					M_log.debug("executeQuery: jectiong value from session but there isn't value" + paramSec.getId());
				}
			} else if ("datasourcecolumn".equals(param.getFrom())) {
				M_log.debug("executeQuery: reading from dataSource Value class" + userValue.getClass().toString());

				if (userValue != null) {
					DataSourceValue rowValue = (DataSourceValue) userValue;
					parvalue = rowValue.getValue().get(param.getId());
					M_log.debug("executeQuery: Injectiong value from session " + param.getId() + " value " + parvalue);
				}
			}else if ("deleteKey".equals(param.getFrom())) {
				M_log.debug("executeQuery: reading from deleteKey Value class" + userValue.getClass().toString());

				Parameter paramSec = (Parameter) queryTemporalValues.get("deleteKey");
				if (paramSec != null) {
					parvalue = paramSec.getValue();
					M_log.debug("executeQuery: Injectiong value from deleteKey " + paramSec.getId() + " value " + parvalue);
				} else {
					M_log.debug("executeQuery: jectiong value from deleteKey but there isn't value" + paramSec.getId());
				}
			}else if ("request".equals(param.getFrom())){
					Map<String, Object> reqParams = curDataSource.getRequestParameters();
					if (reqParams!= null){
						parvalue = reqParams.get(param.getId());
						if (parvalue==null){
							parvalue = param.getValue();
						}
						if (parvalue==null){
							return null;
						}
						
						M_log.debug("execute Query: Injection form request for param" + param.getId() + " value " + parvalue);
					}
			}

			if (parvalue == null) {// maybe has defaultValue
				parvalue = param.getValue();
			}

			objectList[paramNum] = parvalue;
			paramNum++;
		}

		final Query lastQuery = query;
		final QueryState lastQueryState = queryState;

		M_log.debug("executequery: It's going to execute " + query.getStatement() + " with parameters " + objectList);

		Object retValue = getSqlService().dbRead(curConn, query.getStatement(), objectList, new org.sakaiproject.db.api.SqlReader() {

			public Object readSqlResultRecord(ResultSet result) throws SqlReaderFinishedException {
				Object value = null;

				if (result != null) {
					
					try {
						M_log.debug("executequery:readSqlResult: Has result");
						DataSourceValueRow dsvr = new DataSourceValueRow(result.getString(lastQuery.getColumnKey()));

						for (DataSourceColumn dsc : lastQuery.getColumns().values()) {
							M_log.debug("executequery:readSqlResult: Looking column name" + dsc.getColumnName());
							value = result.getObject(dsc.getColumnName());
							if (value == null) {
								value = "";
							}
							M_log.debug("executequery:readSqlResult: Looking column value" + value);

							if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
								TIMESTAMP to = (TIMESTAMP) result.getObject(dsc.getColumnName());
								value = TIMESTAMP.toTimestamp(to.toBytes());
							}
							dsvr.addColumnValue(dsc.getId(), value);
						}
						value = dsvr;
					} catch (SQLException e) {
						e.printStackTrace();
						return "value not found";
					}
				}
				return value;
			}
		});

		if (retValue != null) {

			M_log.debug("retValue is not null " + retValue.getClass().toString());
			lastQueryState.setSuccess(true);
			
			for (Object o : (List) retValue) {
				M_log.debug("Class " + o.getClass() + " value " + o.toString());
			}

			Map valuesMap = new LinkedHashMap(); // Its linked for the order
			Map map = new HashMap();

			for (DataSourceValueRow curDa : (List<DataSourceValueRow>) retValue) {
				M_log.debug("executeQuery:Process row " + curDa.getId() + " value" + curDa);
				valuesMap.put(curDa.getId(), curDa);
				queryState.setEmtpy(false);
			}

			DataSourceValue successValue = new DataSourceValue(query.getId(), currentSection.getId(), valuesMap, false);
			M_log.debug("executeQuery: Creat el valor");

			return successValue;
		}

		return null;
	}

	private boolean updateQuery(Query query, SectionInfo currentSection, DataSourceValue userValue, Map queryTemporalValues, Connection curConn, QueryState queryState) {

		Object objectList[] = new Object[query.getParameterList().size()];
		int paramNum = 0;

		for (Parameter param : query.getParameterList()) {
			Object parvalue = null;
			// The search of parameter must be by name.

			if ("query".equals(param.getFrom())) {
				Parameter paramSec = (Parameter) queryTemporalValues.get(param.getId());

				if (paramSec != null) {
					parvalue = paramSec.getValue();
					M_log.debug("updateQuery: Injectiong value from session " + paramSec.getId() + " value " + parvalue);
				} else {
					M_log.debug("updateQuery: jectiong value from session but there isn't value" + paramSec.getId());
				}

			} else if ("session".equals(param.getFrom())) {
				Parameter paramSec = currentSection.getParameter(param.getId());

				if (paramSec != null) {
					parvalue = paramSec.getValue();
					M_log.debug("updateQuery: Injectiong value from session " + paramSec.getId() + " value " + parvalue);
				} else {
					M_log.debug("updateQuery: Injectiong value from session but there isn't value" + paramSec.getId());
				}
			} else if ("datasourcecolumn".equals(param.getFrom())) {
				M_log.debug("executeQuery: reading from dataSource Value class" + userValue.getClass().toString());

				if (userValue != null) {
					DataSourceValue rowValue = (DataSourceValue) userValue;
					parvalue = rowValue.getValue().get(param.getId());
					M_log.debug("executeQuery: Injectiong value from session " + param.getId() + " value " + parvalue);
				}
			}

			if (parvalue == null) {// maybe has defaultValue
				parvalue = param.getValue();
			}

			objectList[paramNum] = parvalue;
			paramNum++;
		}

		M_log.debug("updateQuery: It's going to execute " + query.getStatement() + " with parameters " + objectList);

		if (getSqlService().dbWrite(curConn, query.getStatement(), objectList)) {
			M_log.debug("updatequery:dbwrite: Has result");
			queryState.setSuccess(true);
		} else {
			queryState.setSuccess(false);
			queryState.setEmtpy(true);
		}

		return queryState.isSuccess();
	}

	private class QueryState {

		private boolean success = false;
		private boolean emtpy = true;

		public QueryState(boolean success, boolean emtpy) {
			super();
			this.success = success;
			this.emtpy = emtpy;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public boolean isEmtpy() {
			return emtpy;
		}

		public void setEmtpy(boolean emtpy) {
			this.emtpy = emtpy;
		}
	}

}
