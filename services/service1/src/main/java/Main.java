//cat ./vstorage (näytä vstorage sisältö)
//curl localhost:8199/log (= GET localhost:8199/status)

//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"
//HTTP GET /status => Service2
//HTTP POST /log => Storage

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Main {
    public static void main(String[] args){
        DateTimeFormatter timestamp = DateTimeFormatter.ISO_INSTANT;
        String record1 = timestamp.toString() + ": Uptime 5 hours, free disk in root: 20000 MBytes";
        sendStorage(record1);
        String record2 = getService2Status();
        System.out.println(record1 + "/n" + record2);
    }
    
    public static String getService2Status() {
        try{
            URL url = new URL("http://localhost:8199/status");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Convert to String
            String responseString = response.toString();
            System.out.println(responseString);
            return responseString;

        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }

    public static void sendStorage(String data) {
        try{
            URL url = new URL("localhost:8199/log");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            conn.getOutputStream().close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
