import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Fitness {
    public static double determineFitness(Chromosome input){
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

        double result = 0;
        result += LTL(output);

        return result;
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
            obj = jsonParser.parse(new FileReader("./Output.json"));
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

    private static double LTL(ModuleReturns[] output){
        return FitnessConfig.determineFitness(output);
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
    public final long exitValue;

    public ModuleReturns(JSONObject obj) throws MalformedOutput{
        if(!obj.containsKey("stdout") || !obj.containsKey("stderr") || !obj.containsKey("duration"))
            throw new MalformedOutput();

        try{
            stdout = (String)obj.get("stdout");
            stderr = (String)obj.get("stderr");
            executionDuration = (Long)obj.get("duration");
            exitValue = (Long)obj.get("exitvalue");
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

class FitnessConfig{
    public static final FitnessConfigField Safety;
    public static final FitnessConfigField Livelyness;
    public static final FitnessConfigField SegFault;
    public static final FitnessConfigField Exceptions;
    public static final ExecutionTimeField ExecutionTime;
    public static final IllegalOutputField IllegalOutput;
    public static final double LTLWeight;
    public static final double GWeight;
    public static final double MWeight;

    static{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("FitnessConfig.json"));
        } catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        Object res = null;
        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Safety"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Safety = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Livelyness"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Livelyness = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("SegFault"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            SegFault = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Exceptions"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Exceptions = (FitnessConfigField)res;
        }

        try{
            res = new ExecutionTimeField((JSONObject)jsonObject.get("ExecutionTime"));
        } catch (Exception e){
            e.printStackTrace();
            res = new ExecutionTimeField(); 
        } finally{
            ExecutionTime = (ExecutionTimeField)res;
        }

        try{
            res = new IllegalOutputField((JSONObject)jsonObject.get("IllegalOutput"));
        } catch (Exception e){
            e.printStackTrace();
            res = new IllegalOutputField();
        } finally{
            IllegalOutput = (IllegalOutputField)res;
        }

        try{
            res = (Double)jsonObject.get("LTLWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            LTLWeight = (Double)res;
        }

        try{
            res = (Double)jsonObject.get("MWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            MWeight = (Double)res;
        }

        try{
            res = (Double)jsonObject.get("GWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            GWeight = (Double)res;
        }
    }

    public static double determineFitness(ModuleReturns[] output){
        double result = 0;
        result += Safety(output);
        result += Livelyness(output);
        result += SegFault(output);
        result += Exception(output);
        result += ExecutionTime(output);
        result += IllegalOutput(output);
        return FitnessConfig.LTLWeight * result;
    }

    private static double Safety(ModuleReturns[] output){
        if(!Safety.enabled){
            return 0;
        }
        double result = 0;
        for(ModuleReturns moduleReturns: output){
            if(!(moduleReturns.stderr.isBlank() && moduleReturns.stderr.isEmpty())){
                result += 1;
            }
            if(moduleReturns.stdout.toUpperCase().contains("EXCEPTION")){
                result += 1;
            }
        }

        return Safety.weight * result/(output.length * 2);
    }

    private static double Livelyness(ModuleReturns[] output){
        if(!Livelyness.enabled)
            return 0;

        double result = 0;
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.exitValue != 0){
                result += 1;
            }
        }

        return Livelyness.weight * result / output.length;
    }

    private static double SegFault(ModuleReturns[] output){
        if(Safety.enabled)
            return 0;

        if(!SegFault.enabled)
            return 0;

        double result = 0;
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.stderr.toLowerCase().contains("segfault") || moduleReturns.stderr.toLowerCase().contains("segmentation fault") || moduleReturns.exitValue == 139){
                result += 1;
            }
        }
        return SegFault.weight * result / output.length;
    }

    private static double Exception(ModuleReturns[] output){
        if(Safety.enabled)
            return 0;

        if(!SegFault.enabled)
            return 0;

        double result = 0;
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.stdout.toLowerCase().contains("exception") || moduleReturns.stdout.toLowerCase().contains("exceptions")){
                result += 1;
            } else if (moduleReturns.stderr.toLowerCase().contains("exception") || moduleReturns.stderr.toLowerCase().contains("exceptions")){
                result += 1;
            }
        }
        return Exceptions.weight * result / output.length;
    }

    private static double ExecutionTime(ModuleReturns[] output){
        if(!ExecutionTime.enabled)
            return 0;

        double result = 0;
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.executionDuration > ExecutionTime.maxTime){
                result += 1;
            }
        }
        return ExecutionTime.weight * result / output.length;
    }

    private static double IllegalOutput(ModuleReturns[] output){
        if(!IllegalOutput.enabled)
            return 0;

        if(IllegalOutput.words.length <=0)
            return 0;

        double result = 0;
        for(ModuleReturns moduleReturns: output){
            for(String word: IllegalOutput.words){
                if(moduleReturns.stdout.contains(word)){
                    result += 1;
                }
            }
        }
        return IllegalOutput.weight * result / (IllegalOutput.words.length * output.length);
    }
    
}

class FitnessConfigField{
    public final boolean enabled;
    public final double weight;

    public FitnessConfigField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
        } catch (Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public FitnessConfigField(){
        enabled = false;
        weight = Double.NaN;
    }
}

class IllegalOutputField{
    public final boolean enabled;
    public final double weight;
    public final String[] words;

    public IllegalOutputField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
            JSONArray arr = (JSONArray)jsonObject.get("words");
            String[] res = new String[arr.size()];
            for(int i=0; i < res.length; i++){
                res[i] = (String)arr.get(i);
            }
            words = res;
        } catch (Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public IllegalOutputField(){
        enabled = false;
        weight = Double.NaN;
        words = new String[0];
    }
}

class ExecutionTimeField{
    public final boolean enabled;
    public final double weight;
    public final long maxTime;

    public ExecutionTimeField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
            maxTime = (Long)jsonObject.get("maxTime");
        } catch(Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public ExecutionTimeField(){
        enabled = false;
        weight = Double.NaN;
        maxTime = 0;
    }

}

class MalformedFitnessConfig extends Exception{
    MalformedFitnessConfig(){
        super("Malformed fitnress config file");
    }
}