import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static DataWorker dataWorker = new DataWorker();
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args){
        AdditionalThread additionalThread;
        String firstCur;
        String secondCur;

        System.out.println("Enter from currency:");
        firstCur = getRightData();

        System.out.println("Enter to currency:");
        secondCur = getRightData();

        additionalThread = new AdditionalThread(firstCur, secondCur);
        additionalThread.start();
    }

    private static String getRightData(){
        String inputStr;
        inputStr = input.nextLine().toUpperCase();
        try {
            if (!dataWorker.checkData(inputStr)){
                System.out.println("Wrong currency. Please try again:");
                return getRightData();
            }
        } catch (IOException e) {
            System.out.println("Error: something went wrong during the checking currency");
            System.exit(-1);
        }
        return inputStr;
    }
}
