import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Fitness {
    public static float determineFitness(Chromosome input){
        FileManager.writeChromosomeToFile(input);
        executeSystem();
        ModuleReturns[] output = null;
        try{
            output = parseOutput();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } 

        for(ModuleReturns moduleReturns: output){
            System.out.println(moduleReturns);
        }

        return 0;
    }

    private static void executeSystem(){
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(GeneticAlgorithmConfig.interperterCommand + " " + GeneticAlgorithmConfig.interperterPath);
            InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
            String result[] = new String[2];
            result[0] = "";
            result[1] = "";
            int n1;
            while((n1 = isr.read()) > 0){
                result[0] += (char)n1; 
            }
            while((n1 = esr.read()) > 0){
                result[1] += (char)n1;
            }
            while(process.isAlive()){}
            if(process.exitValue() == 139)
                result[1] += "Seg fault";

            for(String res: result){
                System.out.print(res);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ModuleReturns[] parseOutput() throws Exception{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("Output.json"));
        } catch (Exception e){
            e.printStackTrace();
        }

        JSONArray outputArray = (JSONArray)obj;
        ModuleReturns[] tempArr = new ModuleReturns[outputArray.size()];
        for(int i=0; i < tempArr.length; i++){
            JSONObject jObject = (JSONObject)outputArray.get(i);
            tempArr[i] = new ModuleReturns(jObject);
        }
        return tempArr;
    }
}

@SuppressWarnings({"rawtypes", "unchecked"})
class FitnessMemory{
    public static HashMap<GeneConfig, HashMap<String, ArrayList<Chromosome>>>[] maps;

    static{
        ArrayList<HashMap<GeneConfig, HashMap<String, ArrayList<Chromosome>>>> mapLists = new ArrayList<>();
        for(GeneConfig geneConfig: ChromosomeConfig.geneConfigs){
            HashMap<GeneConfig, HashMap<String, ArrayList<Chromosome>>> temp = new HashMap<>();
            temp.put(geneConfig, new HashMap<>());
            mapLists.add(temp);
        }
        maps = mapLists.toArray(new HashMap[0]);
    }
}

class ModuleReturns{
    public final String stdout;
    public final String stderr;
    public final long executionDuration;

    public ModuleReturns(JSONObject obj) throws MalformedOutput{
        if(!obj.containsKey("stdout") || !obj.containsKey("stderr") || !obj.containsKey("duration"))
            throw new MalformedOutput();

        try{
            stdout = (String)obj.get("stdout");
            stderr = (String)obj.get("stderr");
            executionDuration = (Long)obj.get("duration");
        } catch(Exception e){
            e.printStackTrace();
            throw new MalformedOutput();
        }
       
    }

    class MalformedOutput extends Exception{
        public MalformedOutput(){
            super("Output received from critical system is malformed");
        }
    }

    @Override
    public String toString() {
        String res = "";
        res += "stdout: " + stdout + "stderr: " + stderr + "execution duration: " + executionDuration;
        return res;
    }
}