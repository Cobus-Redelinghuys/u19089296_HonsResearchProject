import java.util.ArrayList;
import java.util.Arrays;

public class Tester {
    public static void TestConversions(){
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

    public static void TestCrossOver(){
        Chromosome c1 = ChromosomeConfig.generatChromosome();
        Chromosome c2 = ChromosomeConfig.generatChromosome();
        ArrayList<Chromosome> chromosomes = new ArrayList<>();
        chromosomes.add(c1);
        chromosomes.add(c2);
        for(CrossOverType crossOverType: CrossOverType.values()){
            chromosomes.addAll(Arrays.asList(crossOverType.crossOver(c1, c2)));
        }

        for(Chromosome chromosome: chromosomes){
            System.out.println(chromosome.toString());
        }
    }

    public static void TestMutation(){
        Chromosome c1 = ChromosomeConfig.generatChromosome();
        System.out.println(c1.toString());
        for(MutationType mutationType: MutationType.values()){
            System.out.println(mutationType.mutate(c1).toString());
        }
    }
}
//