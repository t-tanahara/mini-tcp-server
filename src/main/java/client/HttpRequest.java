package client;

import java.io.InputStream;
import java.net.URLDecoder;

public class HttpRequest implements Request{
    private String filePath = null;
    private String hostName = null;

    public HttpRequest(InputStream input) {
        try {
            String line;
            while ((line = readLine(input)) != null) {
                if (line.equals("")){
                    break;
                }

                if (line.startsWith("GET")) {
                    filePath = URLDecoder.decode(line.split(" ")[1], "UTF-8");
                }
                if (line.startsWith("Host:")) {
                    hostName = line.substring("Host: ".length());
                }
            }
        } catch (Exception e) {

        }
    }

    private String readLine(InputStream input) throws Exception {
        int ch;
        String ret = "";
        while ((ch = input.read()) != -1) {
            if (ch == '\r') {
                // do nothing
            } else if (ch == '\n') {
                break;
            } else {
                ret += (char)ch;
            }
        }
        if (ch == -1) {
            return null;
        }
        return ret;
    }

    public String getFilePath() {
        if (filePath == null) {
            return null;
        }

        if (filePath.endsWith("/")) {
            filePath += "index.html";
        }
        return filePath;
    }

    public String getHostName() {
        return hostName;
    }
}
