import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
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

    @SuppressWarnings("unchecked")
    public static JSONArray executeSystem(Input input){
        JSONArray result = new JSONArray();
        /*for(int i=0; i < moduleConfigs.length; i++){
            result.add(moduleConfigs[i].executeModule(input.moduleInputs.get(moduleConfigs[i].moduleName)));
        }*/
        ModuleRunner[] moduleRunners = new ModuleRunner[moduleConfigs.length];
        for(int i=0; i < moduleRunners.length; i++){
            moduleRunners[i] = new ModuleRunner(moduleConfigs[i], input.moduleInputs.get(moduleConfigs[i].moduleName));
            moduleRunners[i].start();
        }
        try{
            for(int i=0; i < moduleRunners.length; i++){
                moduleRunners[i].join();
                result.add(moduleRunners[i].getResults());
            }
        }catch(Exception e){
            e.printStackTrace();
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

    @SuppressWarnings("unchecked")
    public JSONObject executeModule(ArrayList<String> inputs){
        String[] result = new String[2];
        String args = "";
        LocalTime start = null;
        LocalTime end;
        int exitValue = 0;
        for(String arg: inputs){
            if(moduleName.equals("module5")){
                arg = Long.toBinaryString(Long.parseLong(arg));
            }
            args += arg + " ";
        }
        try{
            start = LocalTime.now();
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(executionCommand + " " + relativePath + " " + args);
            InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
            result[0] = "";
            result[1] = "";
            int n1;
            ProcessMonitor processMonitor = new ProcessMonitor(process, start);
            processMonitor.start();
            while((n1 = isr.read()) > 0){
                result[0] += (char)n1; 
            }
            while((n1 = esr.read()) > 0){
                result[1] += (char)n1;
            }
            while(process.isAlive()){}
            processMonitor.join();
            if(processMonitor.infiniteLoop)
                result[1] += "Infinite loop";
            if(process.exitValue() == 139)
                result[1] += "Seg fault";
            exitValue = process.exitValue();
        } catch (Exception e){
            result[1] += e.getMessage();
            e.printStackTrace();
        }
        end = LocalTime.now();
        JSONObject obj = new JSONObject();
        obj.put("stdout", result[0]);
        obj.put("stderr", result[1]);
        obj.put("duration", Duration.between(start, end).toMillis());
        obj.put("exitvalue", exitValue);
        return obj;
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
        /*for(ModuleConfig moduleConfig: ModulesConfig.moduleConfigs){
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
        }*/
        for(long i=0; i < jsonObject.size(); i++){
            ArrayList<String> inputs = new ArrayList<>();
            int index = (int)i;
            String str = String.valueOf(index);
            //var temp = jsonObject.get(str);
            if(jsonObject.get(str).getClass().equals(JSONArray.class)){
                JSONArray jsonArray = (JSONArray)jsonObject.get(str);
                for(Object object : jsonArray){
                    inputs.add(object.toString());
                }
            } else {
                inputs.add(jsonObject.get(str).toString());  
            }
            map.put(ModulesConfig.moduleConfigs[index].moduleName, inputs);
            if(map.get(ModulesConfig.moduleConfigs[index].moduleName).size() != ModulesConfig.moduleConfigs[index].numberOfCLArguments){
                throw new InputException(ModulesConfig.moduleConfigs[index]);
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

class ProcessMonitor extends Thread{
    private Process module;
    private LocalTime start;
    public boolean infiniteLoop = false;

    public ProcessMonitor(Process module, LocalTime start){
        this.module = module;
        this.start = start;
    }

    @Override
    public void run() {
        while(module.isAlive()){
            LocalTime temp = LocalTime.now();
            if(Math.abs(Duration.between(temp, start).toMillis()) > 30000){
                module.destroyForcibly();
                infiniteLoop = true;
                break;  
            }
        }
    }
}

class ModuleRunner extends Thread{
    private ModuleConfig moduleConfig;
    private ArrayList<String> inputs;
    private JSONObject result = null;

    public ModuleRunner(ModuleConfig moduleConfig, ArrayList<String> inputs){
        this.moduleConfig = moduleConfig;
        this.inputs = inputs;
    }

    @Override
    public void run() {
        result = moduleConfig.executeModule(inputs);
    }

    public JSONObject getResults(){
        return result;
    }
}