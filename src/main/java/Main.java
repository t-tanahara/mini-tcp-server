import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.lang.System;

public class Main {
    public static Conf CONF = new Conf();

    public static void main(String[] args) throws Exception {
        if (args.length > 0){
            String pathToConfFile = args[0];

            try (final InputStream in = Files.newInputStream(Paths.get(pathToConfFile))) {
                Yaml çonfYml = new Yaml();
                CONF = çonfYml.loadAs(in, Conf.class);
            } catch (NoSuchFileException e) {
                System.out.println("conf.yml not found in " + pathToConfFile);
                return;
            }
        }


        try (ServerSocket server = new ServerSocket(CONF.portNumber)) {
            while (true) {
                Socket socket = server.accept();
                ServerThread thread = new ServerThread(socket);
                thread.run();
            }
        }
    }
}
