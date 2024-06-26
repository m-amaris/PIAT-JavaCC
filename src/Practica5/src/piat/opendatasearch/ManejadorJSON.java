package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
public class ManejadorJSON{
	
	private final String query;
    private final int numDatasets;
    private final HashMap<String, AtomicInteger> datasetsMap;
    private final List<String> titleList;
	
	public ManejadorJSON(String query, int numDatasets, HashMap<String, AtomicInteger> datasetsMap, List<String> titleList){
		this.query = query;
        this.numDatasets = numDatasets;
        this.datasetsMap = datasetsMap;
        this.titleList = titleList;
	}

	public String getQuery() {
		return this.query;
	}


    public int getNumDatasets() {
        return this.numDatasets;
    }
	
	public HashMap<String, Integer> getNumDatasetsMap() {
		HashMap<String, Integer> newMap = new HashMap<>();
		for (Map.Entry<String, AtomicInteger> entry : datasetsMap.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().get());
        }
		return newMap;
	}
	
	public List<String> getTitleList() {
		return this.titleList;
	}

}
