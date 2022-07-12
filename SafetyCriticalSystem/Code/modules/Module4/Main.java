import java.io.File;
import java.util.Scanner;

public class Main{
    public static int readFile(){
        int returnVal = 0;
        try {
            File myObj = new File("./modules/Module4/config.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                returnVal = Integer.parseInt(data);
                break;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return returnVal;
    }
    
    public static void main(String[] args) {
        Integer[] arr = new Integer[readFile()];
        for(int i=0; i < arr.length; i++){
            arr[i] = i;
        }
        int index = Integer.parseInt(args[0]);
        System.out.println(arr[index]);

    }
}