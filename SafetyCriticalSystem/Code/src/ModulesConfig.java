import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ModulesConfig {
    public static final ModuleConfig[] moduleConfigs;

    static{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("ModuleConfig.json"));
        } catch(Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray jsonArray = (JSONArray)jsonObject.get("modules");
        ModuleConfig[] tempArr = new ModuleConfig[jsonArray.size()];
        for(int i=0; i < tempArr.length; i++){
            JSONObject jObject = (JSONObject)jsonArray.get(i);
            tempArr[i] = new ModuleConfig(jObject);
        }
        moduleConfigs = tempArr;
    }

}

class ModuleConfig{
    public final String moduleName;
    public final boolean enabled;
    public final String relativePath;
    public final String executionCommand;
    public final int numberOfCLArguments;

    public ModuleConfig(JSONObject object){
        Object res = null;
        try{
            res = object.get("moduleName");
        } catch(Exception e){
            e.printStackTrace();
            res = "module1";
        } finally{
            moduleName = (String)res;
        }

        try{
            res = object.get("enabled");
        } catch(Exception e){
            e.printStackTrace();
            res = true;
        } finally{
            enabled = (Boolean)res;
        }

        try{
            res = object.get("relativePath");
        } catch(Exception e){
            e.printStackTrace();
            res = true;
        } finally{
            relativePath = (String)res;
        }

        try{
            res = object.get("executionCommand");
        } catch(Exception e){
            e.printStackTrace();
            res = true;
        } finally{
            executionCommand = (String)res;
        }

        try{
            res = object.get("numberOfCLArguments");
        } catch(Exception e){
            e.printStackTrace();
            res = true;
        } finally{
            numberOfCLArguments = ((Long)res).intValue();
        }
    }

    //public 
}