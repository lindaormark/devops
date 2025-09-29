//cat ./vstorage (näytä vstorage sisältö)
//curl localhost:8199/log (= GET localhost:8199/status)

//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"
//HTTP GET /status => Service2
//HTTP POST /log => Storage

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;

public class Service1 {
    private static final int PORT = 8199;
    private static final String SERVICE2_URL = "http://service2:8199";
    private static final String STORAGE_URL = "http://storage:8199";
    private static final Path VSTORAGE_FILE = Path.of("/vstorage/log.txt");
    private static final HttpClient client = HttpClient.newHttpClient();
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/status", new ForwardHandler("/status"));
        server.createContext("/log", new ForwardHandler("/log"));

        server.setExecutor(null); // default executor
        System.out.println("Listening on http://localhost:" + PORT);
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
                String response = "";
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(STORAGE_URL))
                            .GET()
                            .build();
                    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                    response = resp.body();
                } catch (Exception e) {
                    response = "ERROR contacting Storage: " + e.getMessage();
                }

                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            
            } else if (endpoint.equals("/status")) {
                String record1 = generateStatus();

                System.out.println(record1);
                logToStorage(record1);
                logToVolume(record1);

                String record2 = "";
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(SERVICE2_URL))
                            .GET()
                            .build();
                    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                    record2 = resp.body();
                } catch (Exception e) {
                    record2 = "ERROR contacting Service2: " + e.getMessage();
                }

                String response = record1 + "\n" + record2;
                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }
        }
    }
    private static String generateStatus() {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        double uptimeHours = uptimeMs / (1000.0 * 60 * 60);
        long freeDiskMB;
        try {
            FileStore store = FileSystems.getDefault().getFileStores().iterator().next();
            freeDiskMB = store.getUsableSpace() / (1024 * 1024);
        } catch (Exception e) {
            freeDiskMB = -1;
        }
        String ts = Instant.now().toString();
        return String.format("%s: uptime %.2f hours, free disk in root: %d MBytes", ts, uptimeHours, freeDiskMB);
    }

    private static void logToStorage(String record) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(STORAGE_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(record))
                    .header("Content-Type", "text/plain")
                    .build();
            client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Failed to log to storage: " + e.getMessage());
        }
    }

    private static void logToVolume(String record) {
        try {
            File dir = VSTORAGE_FILE.getParent().toFile();
            if (!dir.exists()) dir.mkdirs();
            try (FileWriter fw = new FileWriter(VSTORAGE_FILE.toFile(), true)) {
                fw.write(record + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to log to volume: " + e.getMessage());
        }
    }
}
