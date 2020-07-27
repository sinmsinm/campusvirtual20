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

package cat.udl.asic.datacollector.impl.component;

import java.util.List;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataCollectorService;
import cat.udl.asic.datacollector.api.service.DataSourceService;
import cat.udl.asic.datacollector.api.service.ImportService;

public class ComposedDataCollectorServiceImpl implements DataCollectorService {

	
	private DataSourceServiceManager dssm = null;
	private ImportService importService = null;
	
	
	public Section getSection(String sectionId) {
		//Get the section into cache Manager
		
		Section sec = null;
		try{
			sec = importService.loadSection(sectionId);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		//If it doesn't exist then get Section 
		
		return sec;
	}

	public DataSourceValue getDataSourceValue(SectionInfo currentSectionInfo, DataSource currentDataSource) {
		DataSourceService currentService = dssm.getDataSourceService(currentDataSource.getServiceProviderId());	
		return currentService.getDataSourceValue(currentSectionInfo,currentDataSource);
	}


	public boolean saveDataSourceValue(SectionInfo currentSectionInfo,DataSource currentDataSource,DataSourceValue dataSourceValue) {
		DataSourceService currentService = dssm.getDataSourceService(currentDataSource.getServiceProviderId());	
		return currentService.saveDataSourceValue(currentSectionInfo,currentDataSource,dataSourceValue);
	}
	
	public boolean deleteDataSourceValue(SectionInfo currentSectionInfo,DataSource currentDataSource,String key) {
		DataSourceService currentService = dssm.getDataSourceService(currentDataSource.getServiceProviderId());	
		return currentService.deleteDataSourceValue(currentSectionInfo,currentDataSource,key);
	}
	

	public void setServiceManager(DataSourceServiceManager dssm) {
		this.dssm = dssm;
	}

	public Block getBlock(String blockId) {
		Block block = null;
		try{
			block = importService.loadBlock(blockId);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		return block;
	}


	public ImportService getImportService() {
		return importService;
	}


	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public List<String> getVisibleBlockIds() {
		List <String> blockIdsList = null;
		try{
			blockIdsList=importService.getBlockList();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return blockIdsList;
	}


}
