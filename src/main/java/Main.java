import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.lang.System;

public class Main {
    private static int PORT_NUMBER;
    private static String PATH_TO_CONF_FILE = "/etc/mini/conf/conf.yml";

    public static void main(String[] args) throws Exception {
        Conf conf = new Conf();

        if (args.length > 0){
            PATH_TO_CONF_FILE = args[0];
        }

        try (final InputStream in = Files.newInputStream(Paths.get(PATH_TO_CONF_FILE))) {
            Yaml çonfYml = new Yaml();
            conf = çonfYml.loadAs(in, Conf.class);
            PORT_NUMBER = conf.portNumber;
        } catch (NoSuchFileException e) {
            System.out.println("conf.yml not found!");
            return;
        }

        try (ServerSocket server = new ServerSocket(PORT_NUMBER)) {
            while (true) {
                Socket socket = server.accept();
                ServerThread thread = new ServerThread(conf, socket);
                thread.run();
            }
        }
    }
}
