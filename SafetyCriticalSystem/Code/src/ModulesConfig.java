import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

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
        ArrayList<ModuleConfig> tempArr = new ArrayList<>();
        for(int i=0; i < jsonArray.size(); i++){
            JSONObject jObject = (JSONObject)jsonArray.get(i);
            if((Boolean)jObject.get("enabled"))
                tempArr.add(new ModuleConfig(jObject));
        }
        moduleConfigs = tempArr.toArray(new ModuleConfig[0]);
    }

    public static String[][] executeSystem(){
        String[][] result = new String[moduleConfigs.length][2];
        for(int i=0; i < result.length; i++){
            result[i] = moduleConfigs[i].executeModule();
        }
        return result;
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

    public String[] executeModule(){
        String[] result = new String[2];
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(executionCommand + " " + relativePath);
            InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
            result[0] = "";
            result[1] = "";
            int n1;
            while((n1 = isr.read()) > 0){
                result[0] += (char)n1; 
            }
            while((n1 = esr.read()) > 0){
                result[1] += (char)n1;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}