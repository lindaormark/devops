//cat ./vstorage (näytä vstorage sisältö)
//curl localhost:8199/log (= GET localhost:8199/status)

//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"
//HTTP GET /status => Service2
//HTTP POST /log => Storage

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class CurlForwarder {

    public static void main(String[] args) throws IOException {
        int port = 8199;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Handle /status
        server.createContext("/status", new ForwardHandler("/status"));

        // Handle /log
        server.createContext("/log", new ForwardHandler("/log"));

        server.setExecutor(null); // default executor
        System.out.println("Listening on http://localhost:" + port);
        server.start();
    }

    static class ForwardHandler implements HttpHandler {
        private final String endpoint;

        ForwardHandler(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (endpoint.equals("/log")) {
                String targetUrl = "http://storage:8199";

                // Forward request
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                writer.write("Here's some cats!");
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                
                String response;
                try (InputStream in = conn.getInputStream()) {
                    response = new String(in.readAllBytes());
                    System.out.println("Response from " + targetUrl + ": " + response);
                } catch (IOException e) {
                    response = "Failed to reach " + targetUrl + ": " + e.getMessage();
                }
            } else if (endpoint.equals("/status")) {
                String targetUrl = "http://service2:8199";

                // Forward request
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                String response;
                try (InputStream in = conn.getInputStream()) {
                    response = new String(in.readAllBytes());
                    System.out.println("Response from " + targetUrl + ": " + response);
                } catch (IOException e) {
                    response = "Failed to reach " + targetUrl + ": " + e.getMessage();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }
        }
    }
}
