public class App {
    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            for(String arg: args){
                if(arg.equals("GAC")){
                    FileManager.writeGeneticAlgorithmConfigFile();
                }

                if(arg.equals("CC")){
                    FileManager.writeChromosomeConfigFile();
                }
            }
        }
        System.out.println(GeneticAlgorithmConfig.crossoverProp);
        System.out.println(GeneticAlgorithmConfig.crossOverType);
        System.out.println(ChromosomeConfig.geneConfigs[0].maxValue());
    }

}
