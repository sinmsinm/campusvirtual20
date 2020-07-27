package cat.udl.asic.datacollector.api.entity;

import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.io.*;

public class Query implements Serializable {

	private static final long serialVersionUID = 6526471535622546146L;
	private String id=null;
	private String output=null;
	private String statement = null;
	private int type = 0;
	private String onSuccess= null;
	private String onError = null;
	private String onEmpty = null;
	private String onNonEmpty = null;
	private String columnKey = null;  /*The column name to licalize a single dataValue in case of simple 
	is the name of column of where is the value in case of collection if is the primary key*/
	
	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}
	private Map <String,DataSourceColumn> columns = null;
	private List <Parameter> parameterList = null;

	public static int T_WRITE = 0;
	public static int T_READ = 1;
	public static int T_REFERENCE = 2;
	
	
	
	public Query(String id, String output, String statement,int type, String columnKey, String onSuccess, 
			String onError, String onEmpty, String onNonEmpty,
			Map<String, DataSourceColumn> columns, List<Parameter> parameterList) {
		super();
		this.id = id;
		this.output = output;
		this.statement = statement;
		this.type = type;
		this.columnKey = columnKey;
		this.onSuccess = onSuccess;
		this.onError = onError;
		this.onEmpty = onEmpty;
		this.onNonEmpty = onNonEmpty;
		this.columns = columns;
		this.parameterList = parameterList;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}
	public String getOnSuccess() {
		return onSuccess;
	}
	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}
	public String getOnError() {
		return onError;
	}
	public void setOnError(String onError) {
		this.onError = onError;
	}
	public String getOnEmpty() {
		return onEmpty;
	}
	public void setOnEmpty(String onEmpty) {
		this.onEmpty = onEmpty;
	}
	public String getOnNonEmpty() {
		return onNonEmpty;
	}
	public void setOnNonEmpty(String onNonEmpty) {
		this.onNonEmpty = onNonEmpty;
	}


	
	public Map <String,DataSourceColumn> getColumns() {
		return columns;
	}
	public List<Parameter> getParameterList() {
		return parameterList;
	}
	
	public void setColumns(Map <String,DataSourceColumn> columns) {
		this.columns = columns;
	}
	public void setParameterList(List<Parameter> parameterList) {
		this.parameterList = parameterList;
	}
	
	   /**
	   * This is the default implementation of readObject.
	   * Customise if necessary.
	   */
	   private void readObject(
	     ObjectInputStream aInputStream
	   ) throws ClassNotFoundException, IOException {
	     //always perform the default de-serialization first
	     aInputStream.defaultReadObject();
	  }

	    /**
	    * This is the default implementation of writeObject.
	    * Customise if necessary.
	    */
	    private void writeObject(
	      ObjectOutputStream aOutputStream
	    ) throws IOException {
	      //perform the default serialization for all non-transient, non-static fields
	      aOutputStream.defaultWriteObject();
	    }
}
