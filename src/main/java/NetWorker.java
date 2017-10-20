import java.net.*;
import java.io.*;

public class NetWorker {
    private final boolean SUCCESS = true;
    private final boolean FAIL = false;
    private DataWorker dataWorker;

    public NetWorker (DataWorker dataWorker){
        this.dataWorker = dataWorker;
    }

    public boolean download(String firstCur, String secondCur) throws IOException {
        URL apiFixer = new URL("http://api.fixer.io/latest?base=" + firstCur + "&symbols=" + secondCur);
        HttpURLConnection fixerConnection = (HttpURLConnection) apiFixer.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(fixerConnection.getInputStream()));
        String inputLine;
        inputLine = in.readLine();
        fixerConnection.disconnect();
        in.close();
        if (dataWorker.updateData(inputLine, firstCur, secondCur)) {
            return SUCCESS;
        }else {
            return FAIL;
        }
    }

    public boolean checkConnection() {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://api.fixer.io");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
        } catch (MalformedURLException e) {
            System.out.println("Error: something went wrong during the connection");
        } catch (IOException e) {
            System.out.println("Error: something went wrong during the connection");
            return FAIL;
        }
        if (connection != null) {
            connection.disconnect();
        }
        return SUCCESS;
    }
}