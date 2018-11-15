package server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class Response {
    public static void sendOk(OutputStream output, InputStream fileInput, String extension) throws Exception {
        //response headers
        writeLine(output, "HTTP/1.1 200 OK");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Server");
        writeLine(output, "Connection: close");
        writeLine(output, "Content-type: " + getContentType(extension));
        writeLine(output, "");

        int ch;
        while ((ch = fileInput.read()) != -1) {
            output.write(ch);
        }
    }

    public static void sendMovedPermanently(OutputStream output, String location) throws Exception {
        //response headers
        writeLine(output, "HTTP/1.1 301 Moved Permanently");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Server");
        writeLine(output, "Location: " + location);
        writeLine(output, "Content-type: text/html");
        writeLine(output, "");
    }

    public static void sendNotFound(OutputStream output, String errorDocumentRoot) throws Exception {
        //response headers
        writeLine(output, "HTTP/1.1 404 Not Found");
        writeLine(output, "Date: " + getDateStringUtc());
        writeLine(output, "Server: Server");
        writeLine(output, "Connection: close");
        writeLine(output, "Content-type: text/html");
        writeLine(output, "");

        try (InputStream input = new BufferedInputStream(new FileInputStream(errorDocumentRoot + "/404.html"))) {
            int ch;
            while ((ch = input.read()) != -1) {
                output.write(ch);
            }
        }
    }

    static void writeLine(OutputStream output, String str) throws Exception {
        for (char ch : str.toCharArray()) {
            output.write(ch);
        }
        output.write((int)'\r');
        output.write((int)'\n');
    }

    static String getDateStringUtc() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);

        df.setTimeZone(cal.getTimeZone());

        return df.format(cal.getTime()) + " GMT";
    }

    static final HashMap<String, String> contentTypeMap = new HashMap<String, String>() {{
        put("html", "text/html");
        put("htm",  "text/html");
        put("txt",  "text/plain");
        put("css",  "text/css");
        put("js",   "application/javascript");
        put("png",  "image/png");
        put("jpg",  "image/jpg");
        put("jpeg", "image/jpeg");
        put("gif",  "image/gif");
    }};

    //拡張子を受け取り、content-typeを返す
    static String getContentType(String extension) {
        String ret = contentTypeMap.get(extension.toLowerCase());
        if (ret == null) {
            return "text/html";
        }
        return ret;
    }
}
