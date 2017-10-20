import java.io.*;

public class DataWorker{
    private final String CACHE_PATH = "src/main/resources/downloads";
    private final String BASES_PATH = "src/main/resources/data/bases";

    private final boolean SUCCESS = true;
    private final boolean FAIL = false;

    private File mCacheFile = null;
    private File mBasesFile = null;

    private PrintWriter mOutput = null;
    private BufferedReader mInput = null;

    public DataWorker(){
        mBasesFile = new File(BASES_PATH);
    }

    public String buildPath(String firstCur, String secondCur){
        return CACHE_PATH + "/" + firstCur.toLowerCase() + "_" + secondCur.toLowerCase() + ".json";
    }

    public boolean updateData(String data, String firstCur, String secondCur){
        mCacheFile = new File(buildPath(firstCur, secondCur));

        try {
            mOutput = new PrintWriter(new FileWriter(mCacheFile), true);
            mOutput.println(data);

        } catch (IOException e) {
            mOutput.close();
            System.out.println("Error: couldn't write file: " + buildPath(firstCur, secondCur));
            return FAIL;
        }
        mOutput.close();
        return SUCCESS;
    }

    public boolean checkData(String currency) throws IOException {
        try {
            mInput = new BufferedReader(new FileReader(mBasesFile.getPath()));
            String line;
            while ((line = mInput.readLine()) != null){
                if (currency.equals(line)){
                    mInput.close();
                    return SUCCESS;
                }
            }
        } catch (FileNotFoundException e){
            mInput.close();
            System.out.println("Error: couldn't read file 'bases'");
            return FAIL;
        }
        mInput.close();
        return FAIL;
    }

    public boolean checkFile(String firstCur, String secondCur){
        mCacheFile = new File(buildPath(firstCur, secondCur));
        return mCacheFile.exists();
    }
}
