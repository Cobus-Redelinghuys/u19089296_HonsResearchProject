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
        } else {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            geneticAlgorithm.runAlgorithm();
            geneticAlgorithm.printDatabase();
            //geneticAlgorithm.DBAnalysis();
            geneticAlgorithm.showGraphs();
            geneticAlgorithm.printFinalChromosomes();
            System.exit(0);
        }
    }

}
