import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdditionalThread extends Thread {
    private static DataWorker dataWorker = new DataWorker();
    private static NetWorker netWorker = new NetWorker(dataWorker);

    private String firstCur;
    private String secondCur;

    public AdditionalThread(String firstCur, String secondCur){
        this.firstCur = firstCur;
        this.secondCur = secondCur;
    }

    @Override
    public void run(){
        Gson gson = new GsonBuilder().registerTypeAdapter(RateObject.class, new RatesDeserializer()).create();
        BufferedReader reader;
        boolean onlineMode = true;
        Date cacheDate;
        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(!netWorker.checkConnection()){
            System.out.println("Network Connection: FAIL");
            onlineMode = false;
        } else {
            System.out.println("Network Connection: SUCCESS");
        }
        System.out.print("......");

        if (dataWorker.checkFile(firstCur, secondCur)) {
            try {
                reader = new BufferedReader(new FileReader(dataWorker.buildPath(firstCur, secondCur)));
                ApiResponse curData = gson.fromJson(reader, ApiResponse.class);
                System.out.print("......");
                cacheDate = dateFormat.parse(curData.getDate());
                if (isActualDate(cacheDate, today)) {
                    System.out.println("......");
                    printCurInfo(curData);
                    reader.close();
                } else {
                    if (onlineMode) {
                        try {
                            System.out.print("......");
                            if (netWorker.download(firstCur, secondCur)) {
                                System.out.println("......");
                                printCurInfo(curData);
                                reader.close();
                            } else {
                                reader.close();
                                System.exit(-1);
                            }
                        } catch (IOException e) {
                            System.out.println("Error: something went wrong during the downloading");
                            System.exit(-1);
                        }
                    } else {
                        System.out.println("......");
                        printCurInfo(curData);
                        reader.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: something went wrong during the work with data");
                System.exit(-1);
            } catch (ParseException e) {
                System.out.println("Error: something went wrong during the parsing date string");
                System.exit(-1);
            }
        } else {
            if (onlineMode) {
                try {
                    if (netWorker.download(firstCur, secondCur)) {
                        System.out.print("......");
                        reader = new BufferedReader(new FileReader(dataWorker.buildPath(firstCur, secondCur)));
                        ApiResponse curData = gson.fromJson(reader, ApiResponse.class);
                        System.out.println("......");
                        printCurInfo(curData);
                        reader.close();
                    } else {
                        System.exit(-1);
                    }
                } catch (IOException e) {
                    System.out.println("Error: something went wrong during the downloading");
                    System.exit(-1);
                }
            } else {
                System.out.println("............");
                System.out.println("Status: Program cannot get data for your request");
                System.out.println("Reason: cache is empty");
                System.out.println("Solution: try to connect to Internet");
            }

        }
    }

    private static void printCurInfo(ApiResponse curData){
        try {
            System.out.println(curData.getBase() + " => " + curData.getRates().getName() + " : " + curData.getRates().getRate());
            System.out.println("Date: " + curData.getDate());
        } catch (NullPointerException e){
            System.out.println("Error: program cannot find data");
            System.exit(-1);
        }
    }

    private static boolean isActualDate(Date cacheDate, Date today){
        try {
            return cacheDate.getYear() == today.getYear()
                    && cacheDate.getMonth() == today.getMonth()
                    && cacheDate.getDate() == today.getDate();
        } catch (NullPointerException e){
            return false;
        }
    }
}
