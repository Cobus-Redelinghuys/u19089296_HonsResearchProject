import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Fitness {
    public static float determineFitness(Chromosome input){
        FileManager.writeChromosomeToFile(input);
        System.out.println("Fitness determined");
        executeSystem();
        System.out.println("Finished");
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
