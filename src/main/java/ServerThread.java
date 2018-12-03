import client.HttpRequest;
import server.Response;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class ServerThread implements Runnable {
    private String documentRoot;
    private String errorDocumentPath;
    private String serverName;

    private Socket socket;

    ServerThread(Socket socket) {
        this.documentRoot = Main.CONF.documentRoot;
        this.errorDocumentPath = Main.CONF.errorDocumentPath;
        this.serverName = Main.CONF.serverName;

        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream output;
        try {
            InputStream input = socket.getInputStream();
            HttpRequest request = new HttpRequest(input);

            String extension = null;

            if (request.getFilePath() == null){
                return;
            }

            extension = Util.getSuffix(request.getFilePath());

            output = socket.getOutputStream();

            FileSystem fs = FileSystems.getDefault();
            Path path = fs.getPath(documentRoot, request.getFilePath());
            Path absolutePath;

            try {
                absolutePath = path.toRealPath();
            } catch (NoSuchFileException e) {
                Response.sendNotFound(output, errorDocumentPath);
                return;
            }
            if (Files.isDirectory(absolutePath)) {
                String location = "http://"
                        + ((request.getHostName() != null) ? request.getHostName() : serverName)
                        + request.getFilePath() + "/";
                Response.sendMovedPermanently(output, location);
                return;
            }

            try (InputStream fileInput = new BufferedInputStream(Files.newInputStream(absolutePath))) {
                Response.sendOk(output, fileInput, extension);
            } catch (Exception e) {
                Response.sendNotFound(output, errorDocumentPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
