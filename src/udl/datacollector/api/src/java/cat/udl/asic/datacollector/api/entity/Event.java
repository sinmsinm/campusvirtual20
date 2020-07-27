package cat.udl.asic.datacollector.api.entity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.Serializable;
import java.io.*;

public class Event implements Serializable {

	private static final long serialVersionUID = 6526471533622776141L;
	private String type=null;
	private String flowMode = null;
	private LinkedHashMap <String,Query> queryMap =null;
	

	public static String GET ="get"; 
	public static String PUT ="put";
	public static String DELETE ="delete";
	
	
	
	public Event(String type, String flowMode) {
		super();
		this.type = type;
		this.flowMode = flowMode;
	}
	
	
	
	public Event(String type, String flowMode,
			LinkedHashMap<String, Query> queryMap) {
		super();
		this.type = type;
		this.flowMode = flowMode;
		this.queryMap = queryMap;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlowMode() {
		return flowMode;
	}
	public void setFlowMode(String flowMode) {
		this.flowMode = flowMode;
	}
	
	
	public LinkedHashMap<String, Query> getQueryMap() {
		return queryMap;
	}



	public void setQueryMap(LinkedHashMap<String, Query> queryMap) {
		this.queryMap = queryMap;
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
