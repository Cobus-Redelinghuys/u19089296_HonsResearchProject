import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

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

    public static String[][] executeSystem(Input input){
        String[][] result = new String[moduleConfigs.length][2];
        for(int i=0; i < result.length; i++){
            result[i] = moduleConfigs[i].executeModule(input.moduleInputs.get(moduleConfigs[i].moduleName));
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

    public String[] executeModule(ArrayList<String> inputs){
        String[] result = new String[2];
        String args = "";
        for(String arg: inputs){
            args += arg + " ";
        }
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(executionCommand + " " + relativePath + " " + args);
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
                System.out.println(n1);
                result[1] += (char)n1;
            }
            while(process.isAlive()){}
            if(process.exitValue() == 139)
                result[1] += "Seg fault";
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}

class Input{
    public final HashMap<String, ArrayList<String>> moduleInputs;

    public Input() throws InputException{
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("Input.json"));
        } catch(Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        for(ModuleConfig moduleConfig: ModulesConfig.moduleConfigs){
            if(!jsonObject.containsKey(moduleConfig.moduleName) || (map.containsKey(moduleConfig.moduleName))){
                throw new InputException(moduleConfig);
            } else {
                ArrayList<String> inputs = new ArrayList<>();
                if(jsonObject.get(moduleConfig.moduleName).getClass().equals(JSONArray.class)){
                    JSONArray jsonArray = (JSONArray)jsonObject.get(moduleConfig.moduleName);
                    for(Object object : jsonArray){
                        inputs.add(object.toString());
                    }
                } else {
                    inputs.add(jsonObject.get(moduleConfig.moduleName).toString());  
                }
                map.put(moduleConfig.moduleName, inputs);
                if(map.get(moduleConfig.moduleName).size() != moduleConfig.numberOfCLArguments)
                    throw new InputException(moduleConfig);
 
            }
        }
        moduleInputs = map;
    }

    class InputException extends Exception{
        public final ModuleConfig errorModule;

        public InputException(ModuleConfig moduleConfig){
            errorModule = moduleConfig;
        }

        @Override
        public String toString() {
            return "Incorrect input for module: " + errorModule.moduleName;
        }
    }
}