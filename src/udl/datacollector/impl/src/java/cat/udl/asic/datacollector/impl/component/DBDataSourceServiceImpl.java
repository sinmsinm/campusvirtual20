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
* Authors: Alex Ballesté, Noemi Lorente, Xavier Noguero
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat), Noemi Lorente (noemi@oqua.udl.cat), Xavier Noguero (xnoguero@asic.udl.cat)  and usuaris-cvirtual@llistes.udl.cat
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
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.id.cover.IdManager;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;

//localized driver
import oracle.sql.TIMESTAMP;

public class DBDataSourceServiceImpl implements DataSourceService 
{
	
	private static Log M_log = LogFactory.getLog(DBDataSourceServiceImpl.class);
		private SqlService sqlService = null;
		
		
		public void init (){
		}

		
	public DataSourceValue getDataSourceValue(SectionInfo currentSection,
			DataSource dataSource) {
		
				final DataSource lastDataSource = dataSource; 
				DataSourceValue retDataSource = null;
				final String sectionId = currentSection.getId();
				
				M_log.debug("Process get for sectionId " + sectionId + " datasource " + dataSource.getId());
				try {
						Connection conn = sqlService.borrowConnection();
						M_log.debug("Has a connection");
						String sql = getSelectSQL(dataSource);
						
						M_log.debug("Read sql is:" + sql);
						
						int extraColumns = 0;
						
						if (dataSource.getSearchColumns()!=null){
							extraColumns = dataSource.getSearchColumns().size();
						}
						
						Object [] objectList = new Object [dataSource.getParameterList().size() + extraColumns];
						final boolean composed =DataSource.COMPOSED_TYPE.equals(dataSource.getType());
						
						int  paramNum = 0;
						
						for (Parameter param : dataSource.getParameterList()){
							Object parvalue =  null ;
							//The search of parameter must be by name.
							Parameter paramSec = currentSection.getParameter(param.getId());
							
							if (paramSec != null){
								parvalue = paramSec.getValue();	
							}else{//maybe has defaultValue
									parvalue = param.getValue();
							}
							
							objectList[paramNum] = parvalue;
							paramNum ++;
						}
						
						if (dataSource.getSearchColumns()!=null){
							
							for (String columnName : dataSource.getSearchColumns()){
								objectList[paramNum] = "%"+dataSource.getContains()+ "%"; 
								paramNum ++;
							}
							
						}
						 
						Object retValue =  sqlService.dbRead(conn,sql,objectList,new org.sakaiproject.db.api.SqlReader() {
							public Object readSqlResultRecord(ResultSet result)
									throws SqlReaderFinishedException {
									Object value = null;

								if (result!=null){
									try {
										if (!composed){
											value = result.getObject(1);
											
											if ("oracle.sql.TIMESTAMP".equals (value.getClass().getName())){
												TIMESTAMP to = (TIMESTAMP) result.getObject(1);
												value = TIMESTAMP.toTimestamp(to.toBytes()); 
											}
										}else{
											DataSourceValueRow dsvr = new DataSourceValueRow(result.getString(lastDataSource.getColumnKey()));
											for (DataSourceColumn dsc: lastDataSource.getColumns().values()){

													value = result.getObject(dsc.getColumnName());
													
													if (value==null){
														value="";
													}
													
													if ("oracle.sql.TIMESTAMP".equals (value.getClass().getName())){
														TIMESTAMP to = (TIMESTAMP) result.getObject(dsc.getColumnName());
								
														value = TIMESTAMP.toTimestamp(to.toBytes()); 
													}
													dsvr.addColumnValue (dsc.getId(),value);
											}
											value = dsvr;
										}
									} catch (SQLException e) {
										e.printStackTrace();
										return new String ("value not found");
									}
								}
								return value;
							}
						});
						
						if (retValue!=null){
							Map valuesMap = new LinkedHashMap (); //Its linked for the order
							if (!composed){
								if (((List) retValue).size()>0){ 
									valuesMap.put("simple.value",((List) retValue).get(0));
								}else{
									valuesMap.put("simple.value","value not found");
								}
							}else{
								Map map = new HashMap ();
								for (DataSourceValueRow curDa: (List<DataSourceValueRow>) retValue){
									//retValues.put (curDa.getId(),curDa.getValue());
									valuesMap.put (curDa.getId(),curDa);
								}
							}
							retDataSource = new DataSourceValue (dataSource.getId(),sectionId,valuesMap,currentSection.isUpdatable() && dataSource.isUpdatable()); 
						}
						sqlService.returnConnection(conn);
						
					} catch (SQLException e) {
						e.printStackTrace();
					}

		return retDataSource;
	}

	public boolean deleteDataSourceValue (SectionInfo currentSection,DataSource dataSource, String key){
		
		final DataSource lastDataSource = dataSource; 
		boolean hasDeleted=false;
		Object [] objectList =null;
		
		String sql = getDeleteSQL(dataSource);

		if (DataSource.SIMPLE_TYPE.equals (dataSource.getType())) {
			objectList  = new Object [dataSource.getParameterList().size()]; 
		}else{
			objectList  = new Object [dataSource.getParameterList().size()+1]; 
		}
		
		int paramNum = 0;
		
		for (Parameter param : dataSource.getParameterList()){
			//The search of parameter must be by name.
			Object parvalue=null;
			Parameter paramSec = currentSection.getParameter(param.getId());
			
			if (paramSec != null){
				parvalue = paramSec.getValue();	
			}else{//maybe has defaultValue
					parvalue = param.getValue();
			}
			
			objectList[paramNum] = parvalue;
			paramNum ++;
		}
		
		
		if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
			objectList[paramNum] = key;
		}
		
		try{
			Connection conn = sqlService.borrowConnection();
			hasDeleted = sqlService.dbWrite(conn,sql,objectList);
			sqlService.returnConnection(conn);
		}catch (SQLException ex){
				ex.printStackTrace();
		}
		return hasDeleted;
}
	
	public boolean saveDataSourceValue(SectionInfo currentSection,
		DataSource dataSource, DataSourceValue dataSourceValue) {
		boolean hasUpdated = false;
		boolean hasInserted = false;
		
		final DataSource lastDataSource = dataSource; 
		final DataSourceValue lastDataSourceValue= dataSourceValue;
		DataSourceValue retDataSource = null;
		
		
		if (dataSourceValue.getId()==null){
			dataSourceValue.setId("");
	    }
		
		if ("".equals(dataSourceValue.getId()) || dataSource.getId().equals(dataSourceValue.getId())){
				String idCreated =IdManager.createUuid();
				dataSourceValue.setId(idCreated);
		}
		
		Object [] objectList =null;
		final boolean composed =DataSource.COMPOSED_TYPE.equals(dataSource.getType());
				
				int  paramNum = 0;
				Map value = (Map) dataSourceValue.getValue();
				
				if (DataSource.getSIMPLE_TYPE().equals (dataSource.getType())){
					objectList  = new Object [dataSource.getParameterList().size()+1]; // one more for the value
					objectList [0] = value.get(DataSource.SIMPLE_TYPE);
					paramNum++;
				}else{
					objectList = new Object [dataSource.getParameterList().size() + dataSource.getColumns().size() + 1]; // one more for the id
					
					for (DataSourceColumn dsc : dataSource.getColumns().values()){
						
						objectList[paramNum] = value.get(dsc.getId());
						 paramNum ++;
					}
				}
				
				for (Parameter param : dataSource.getParameterList()){
					//The search of parameter must be by name.
					Object parvalue=null;
					Parameter paramSec = currentSection.getParameter(param.getId());
					
					if (paramSec != null){
						parvalue = paramSec.getValue();
					}else{//maybe has defaultValue
							parvalue = param.getValue();
					}

					objectList[paramNum] = parvalue;
					paramNum++;
				}
				
				//finally add th id if it is composed
				
				if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
					objectList[paramNum] = dataSourceValue.getId();
				}
				
				
		try{	
				String sql = getInsertSQL(dataSource);
				
				Connection conn = sqlService.borrowConnection();
				hasInserted = sqlService.dbWrite(conn,sql,objectList);
				
				if (!hasInserted){
					sql = getUpdateSQL(dataSource);
					hasUpdated = sqlService.dbWrite(conn,sql,objectList);
				}
				
				//conn.commit();
				sqlService.returnConnection(conn);
				return hasUpdated || hasInserted;
			}catch (SQLException ex){
					ex.printStackTrace ();
			}	
		
		return false;
	}
	
		public SqlService getSqlService() {
			return sqlService;
		}

		public void setSqlService(SqlService sqlService) {
			this.sqlService = sqlService;
		}

		
		private String getDeleteSQL (DataSource dataSource){
			String sql ="";

			if (dataSource.getParameterList().size() > 0) {
				sql = "DELETE FROM " +  dataSource.getTableSrc() ; 

				//Verify that is ordered by position
				Iterator<Parameter> it = dataSource.getParameterList().iterator();
				
				//Obtain first
				Parameter firstParameter = it.next(); 
				sql = sql + " WHERE " + firstParameter.getColumnName() + " =? " ;

				while (it.hasNext()){
						Parameter p = it.next();
						sql = sql  + " AND " + p.getColumnName() + " =? ";
				}

				if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
					sql = sql + " AND " + dataSource.getColumnKey() + "=? ";
				}
			}else if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
				sql = "DELETE FROM " +  dataSource.getTableSrc() + " WHERE " + dataSource.getColumnKey() + "=? ";
			}
			return sql;
		}
		
		
		private String getInsertSQL (DataSource dataSource){

			String sql= "INSERT INTO " +dataSource.getTableSrc() + " ("  ;
			int params = 0;
			String parametersS = "";
	
			if (DataSource.SIMPLE_TYPE.equals (dataSource.getType())){
					sql = sql + dataSource.getColumnKey() + " , " ;
					params++;
			}else{
				
				for (DataSourceColumn dsc: dataSource.getColumns().values()){
					parametersS = parametersS + dsc.getColumnName() + " , ";
					params ++;
				}
			}	
				
				Iterator<Parameter> it = dataSource.getParameterList().iterator();
				//Obtain first
				//Parameter firstParameter = it.next(); 

				while (it.hasNext()){
						Parameter p = it.next();
						parametersS = parametersS  + p.getColumnName() + " , "; 
						params ++;
				}
				
				sql = sql + parametersS;
				sql = sql.substring(0, sql.length() - 2);
				
				if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
					sql = sql + " , " + dataSource.getColumnKey();
					params++;
				}
				
				sql = sql + ") VALUES ("; 
				
			for (int i = 0;  i<params; i++){
					 sql = sql + " ?";
					 if (i<params-1){
						 sql = sql + ",";
					 }
				}
				sql = sql + ")";
			
			return sql;
		}
	
	
		private String getUpdateSQL (DataSource dataSource){
			String sql = null;

				sql = "UPDATE " + dataSource.getTableSrc() + " SET " ;
					
				if (DataSource.SIMPLE_TYPE.equals (dataSource.getType())){
					sql = sql + dataSource.getColumnKey() + "=? "; 
				}else{
					String parametersS = "";
					for (DataSourceColumn dsc: dataSource.getColumns().values()){
						parametersS = parametersS + dsc.getColumnName() + "=? , ";
					}
					sql = sql + parametersS.substring(0,parametersS.length()-2);
				}
				
				//In case of having parametes we must filter by them
			
			
			
			if (dataSource.getParameterList().size() > 0) {

				//Verify that is ordered by position
				Iterator<Parameter> it = dataSource.getParameterList().iterator();
				//Obtain first
				Parameter firstParameter = it.next(); 
				sql = sql + " WHERE " + firstParameter.getColumnName() + " =? " ;
				while (it.hasNext()){
						Parameter p = it.next();
						sql = sql  + "AND " + p.getColumnName() + " =? ";
				}
				
				if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){ //If composed we must add the id
 					 sql = sql + " AND " + dataSource.getColumnKey() + "=?";
				}
				//Iterator <String> = dataSource.getParameterList().
			}else{
				if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){ //If composed we must add the id
					 sql = sql + " WHERE " + dataSource.getColumnKey() + "=?";
				}
			}
			
			
			
			return sql;
		}
		
		
		private String getSelectSQL (DataSource dataSource){
			String sql = null;
			String sqlcolumns = null;
			String sqlorder = null;
			

			if (DataSource.SIMPLE_TYPE.equals (dataSource.getType())){
				sqlcolumns = dataSource.getColumnKey() ; 

				//In case of having parameters we must filter by them
			}else if (DataSource.COMPOSED_TYPE.equals (dataSource.getType())){
				sqlcolumns = dataSource.getColumnKey();
				
				for (DataSourceColumn dsc: dataSource.getColumns().values()){
					sqlcolumns = sqlcolumns + ", " + dsc.getColumnName();
				}
			}
			
			
			/*If order explicit then add it*/
			if (dataSource.getOrderedby() != null && !"".equals(dataSource.getOrderedby())){

				String orderColumnName = null; 
				for (DataSourceColumn dc : dataSource.getColumns().values()){
						if (dc.getId().equals(dataSource.getOrderedby())){
							orderColumnName =  dc.getColumnName();
							break;
						}
				}			
				
				if (orderColumnName!= null){
					sqlorder = " ORDER BY "  +  orderColumnName;  
					
					if (dataSource.getOrdertype()==DataSource.DSC_ORDER)
						sqlorder = sqlorder + " DESC";
					else 
						sqlorder = sqlorder + " ASC";
					
				}
			}	
			
			sql = "SELECT " + sqlcolumns;
			
			if (dataSource.getPage()>0 && dataSource.getRows() >0 ){				
				if(sqlService.getVendor().equals("oracle")){
					sql = "SELECT " + sqlcolumns + " FROM ( SELECT ROW_NUMBER() OVER ";
					
					if (sqlorder != null)
						sql = sql + " ( " +  sqlorder + " ) ";					
					else
						sql = sql + " ( ORDER BY " +  dataSource.getColumnKey() + " ASC ) ";
					
					sql = sql + " ROWORDER, " + sqlcolumns;				
				}
			}
			
			
			sql = sql + " FROM " + dataSource.getTableSrc() ;
			
			if (dataSource.getParameterList().size() > 0 || (dataSource.getSearchColumns()!=null && dataSource.getSearchColumns().size() >0)) {
				sql = sql + " WHERE ";
				
			}
			
			if (dataSource.getParameterList().size() > 0) {
				
				//Verify that is ordered by position
				Iterator<Parameter> it = dataSource.getParameterList().iterator();
				
				//Obtain first
				Parameter firstParameter = it.next(); 
				sql = sql + firstParameter.getColumnName() + " =? " ;

				while (it.hasNext()){
						Parameter p = it.next();
						sql = sql  + "AND " + p.getColumnName() + " =? ";
				}
			}
			
			//Search for extra parameters
			if (dataSource.getSearchColumns()!=null && dataSource.getSearchColumns().size() >0){
				
				if (dataSource.getParameterList().size() > 0) 
					sql = sql + " AND (";
				else  
					sql = sql + " (";
				
				
				Iterator <String> ite = dataSource.getSearchColumns().iterator();
				while (ite.hasNext() ){
						String columnName = ite.next();
						sql = sql  +  dataSource.getColumns().get(columnName).getColumnName() + "  like ? ";
						if (ite.hasNext()) sql = sql + " OR ";
				}
				sql = sql + ")";
			}
			
			if (sqlorder != null){
				sql = sql + sqlorder;
			}
					
					
			if (dataSource.getPage()>0 && dataSource.getRows() >0 ){
				
				int offset =  (dataSource.getPage() * dataSource.getRows()) - dataSource.getRows();	
				
				if (sqlService.getVendor().equals("mysql"))
					sql = sql + " LIMIT " + dataSource.getRows() + " OFFSET " + offset;					
				else if (sqlService.getVendor().equals("oracle"))					
					sql = sql + " ) WHERE ROWORDER BETWEEN " + (offset+1) + " AND " + (dataSource.getPage() * dataSource.getRows());
				
			}

			return sql;
		}

}

