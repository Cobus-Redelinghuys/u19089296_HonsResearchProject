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

        System.out.println(Integer.toBinaryString(-100).length());

        System.out.println(GeneDataType.Integer.convertToBinary((-100)));
        System.out.println((Integer)GeneDataType.Integer.convertFromBin(GeneDataType.Integer.convertToBinary(-100)));

        System.out.println(GeneDataType.Character.convertToBinary('a'));
        String bitString = GeneDataType.Character.convertToBinary('a');
        System.out.println((Integer)GeneDataType.Integer.convertFromBin(bitString));
        System.out.println((Character)GeneDataType.Character.convertFromBin(bitString));

        System.out.println(GeneDataType.Float.convertToBinary(5.4f));
        System.out.println((Float)GeneDataType.Float.convertFromBin(GeneDataType.Float.convertToBinary(5.4f)));

        System.out.println(GeneDataType.Boolean.convertToBinary(true));
        System.out.println((Boolean)GeneDataType.Boolean.convertFromBin(GeneDataType.Boolean.convertToBinary(true)));

        System.out.println(GeneDataType.Double.convertToBinary(5.4));
        System.out.println((Double)GeneDataType.Double.convertFromBin(GeneDataType.Double.convertToBinary(5.4)));
    }
}
