package piat.opendatasearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class JSONWriter {
    private String query;
    private int numDatasets;
    private HashMap<String, Integer> datasetsMap;
    private List<String> titleList;

    public JSONWriter(String query, int numDatasets, HashMap<String, Integer> datasetsMap, List<String> titleList) {
        this.query = query;
        this.numDatasets = numDatasets;
        this.datasetsMap = datasetsMap;
        this.titleList = titleList;
    }

    public void writeJSONToFile(String filename) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("query", query);
            jsonObject.put("numDataset", numDatasets);

            JSONArray infDatasetsArray = new JSONArray();
            for (String key : datasetsMap.keySet()) {
                JSONObject datasetObject = new JSONObject();
                datasetObject.put("id", key);
                datasetObject.put("num", datasetsMap.get(key));
                infDatasetsArray.put(datasetObject);
            }
            jsonObject.put("infDatasets", infDatasetsArray);

            JSONArray titlesArray = new JSONArray();
            for (String title : titleList) {
                JSONObject titleObject = new JSONObject();
                titleObject.put("title", title);
                titlesArray.put(titleObject);
            }
            jsonObject.put("titles", titlesArray);

            try (FileWriter file = new FileWriter(filename)) {
                file.write(jsonObject.toString(4)); // Indent with 4 spaces for readability
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
