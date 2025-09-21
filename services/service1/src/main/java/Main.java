//cat ./vstorage (näytä vstorage sisältö)
//curl localhost:8199/log (= GET localhost:8199/status)

//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"
//HTTP GET /status => Service2
//HTTP POST /log => Storage

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public void writeToFile() throws IOException {
    String str = "Hello, I like cats";
    BufferedWriter writer = new BufferedWriter(new FileWriter("docker-status.txt"));
    writer.write(str);
    writer.close();
    }
}

