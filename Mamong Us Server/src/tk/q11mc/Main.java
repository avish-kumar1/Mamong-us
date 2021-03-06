package tk.q11mc;

import com.siinus.server.JavaServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    static JavaServer server;
    static HashMap<String, String> names = new HashMap<>();

    public static void main(String @NotNull [] args) throws IOException {
        server = new JavaServer();
        server.start(Integer.parseInt(args[0]));
        JavaServer.setHandler(new Protocol());
        new Console().start();
    }
}
