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
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = Utils.responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);
                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) System.out.println(m);
                    }
                } finally {
                    is.close();
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
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream is = http.getInputStream();
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            numberOfRecords = gson.fromJson(strBuf, Integer.class);

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return numberOfRecords;
    }

  /*  private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }*/
}
