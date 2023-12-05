package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private final Gson gson;
    private int allNumberOfRecords; // /get?from=n
    private String login;

    public GetThread(String login) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.login = login;
    }

    @Override
    public void run() { // WebSockets
        try {
            while (!Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + allNumberOfRecords + "&sender=" + login);
                String strBuf = Utils.getStringFromResponse(url);
                JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                if (list != null) {
                    for (Message m : list.getList()) System.out.println(m);
                }
                allNumberOfRecords = getAllNumberOfRecords();
                Thread.sleep(500);
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getAllNumberOfRecords() {
        Integer numberOfRecords = 0;
        try {
            URL url = new URL(Utils.getURL() + "/getNumberOfRecords");
            String strBuf = Utils.getStringFromResponse(url);
            numberOfRecords = gson.fromJson(strBuf, Integer.class);
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return numberOfRecords;
    }
}
