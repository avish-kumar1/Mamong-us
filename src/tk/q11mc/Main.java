package tk.q11mc;

import com.siinus.simpleGrafix.Program;
import com.siinus.simpleGrafix.gfx.Font;
import com.siinus.simpleGrafix.gfx.Image;
import com.siinus.simpleGrafix.gfx.ImageTile;
import org.json.simple.JSONObject;
import tk.q11mc.chat.OutputChat;
import tk.q11mc.core.Camera;
import tk.q11mc.core.Handler;
import tk.q11mc.discord.DiscordRP;
import tk.q11mc.gui.Button;
import tk.q11mc.gui.TextInput;
import tk.q11mc.gui.TextQueue;
import tk.q11mc.io.FileIO;
import tk.q11mc.net.Multiplayer;
import tk.q11mc.objects.Player;
import tk.q11mc.objects.Wall;

import java.awt.event.KeyEvent;

public class Main extends Program {
    private static Main instance;

    Handler handler;
    public static ImageTile objectSheet = new ImageTile("/objectSheet.png",126,126);
    public static ImageTile mainMenu = new ImageTile("/mainMenu.png",300,100);
    public static ImageTile mpButton = new ImageTile("/mpButton.png",300,100);
    public static ImageTile spButton = new ImageTile("/spButton.png",300,100);
    public static ImageTile spriteText = new ImageTile("/text.png", 256, 64);
    private Image icon = new Image("/icon.png");
    private static DiscordRP discordRP = new DiscordRP();
    public static int width= 640, height = 350;
    Player player;
    Wall wall;
    Button singlePlayerButton;
    Button multiPlayerButton;
    Button btmm;
    TextInput ipField;
    TextInput portField;
    TextInput nameField;
    TextQueue tq;


    //OtherPlayer otherPlayer;


    public static Font arial32 = new Font("/font.png", 32, 37);

    Camera camera;

    public static GameState gameState = GameState.MAIN_MENU;
    boolean loadMP = false;

    public static void main(String[] args) {
        new Main().init();
    }

    public Main() {

        handler = new Handler();
        setIconImage(icon);
        wall = new Wall(this, 1, 126, 26, 0, 0);
        player = PlayerSprite.RED.getNewPlayer(this);
        singlePlayerButton = new Button(this, spButton, width/2, 300, 300, 100, this::startSingleplayer, new GameState[] {GameState.MAIN_MENU});
        multiPlayerButton = new Button(this, mpButton, width/2, 450, 300, 100, this::startMultiplayer, new GameState[] {GameState.MAIN_MENU});
        btmm = new Button(this, mainMenu, width/2, height/2, 256, 64, this::startMainMenu, new GameState[] {GameState.PAUSE});
        nameField = new TextInput(this, spriteText, width/2, 150, 256, 64, 0xff0000ff, arial32);
        nameField.setDefaultText("Name");
        ipField = new TextInput(this, spriteText, width/2, 550, 256, 64, 0xff000000, arial32);
        ipField.setDefaultText("IP");
        portField = new TextInput(this, spriteText, width/2, 650, 256, 64, 0xff000000, arial32);
        portField.setDefaultText("Port");
        tq = new TextQueue();
        tq.endAction = this::startMultiplayer;
        ipField.register(tq);
        portField.register(tq);
        wall.setX(500);
        wall.setY(250);

        camera = new Camera(player);

        instance = this;

        //otherPlayer = new OtherPlayer(this, spritePlayer, "Hi");
    }

    @Override
    public void start() {
        discordRP.start();
        getWindow().setScaleOnResize(true);
        getWindow().getFrame().setTitle("Mamong us");
        setCapFps(true);


        InputUtils.setInput(getInput());
        loadData();
    }

    @Override
    public void update() {
        InputUtils.update(getInput());
        OutputChat.update();

        if (InputUtils.isKeyDown(KeyEvent.VK_ESCAPE) && gameState != GameState.MAIN_MENU) {
            if (gameState == GameState.PAUSE) {
                gameState = GameState.SINGLEPLAYER;
            } else {
                System.out.println("pause");
                startPause();
            }
        }
        if (loadMP) {
            loadMP = false;
            connectMultiplayer();
        }

        handler.update();

        camera.update();
        discordRP.update(ipField.getText());
    }

    @Override
    public void render() {
        getRenderer().setBgColor(0xffffffff);
        handler.render();
        if (gameState == GameState.MULTIPLAYER) {
            double ping = (Multiplayer.getPing() * 1000);
            getRenderer().drawText("Ping: " + ((int) ping) + " ms", 10, 10, 0xff000000, arial32);
            getRenderer().drawText(OutputChat.text(), 100, 200, 0xff007f3f, arial32);
        }
        if (gameState == GameState.LOADING) {
            getRenderer().drawText("Loading...",10, 10,0xff000000, Main.arial32);
        }
        if (gameState == GameState.ERROR) {
            getRenderer().drawText("Connection refused!",10, 10,0xffff0000, arial32);
        }
        //getShaderRenderer().drawLight(light, getInput().getMouseX(), getInput().getMouseY());
    }

    @Override
    public void stop() {
        discordRP.shutdown();
    }

    public void startMainMenu() {
        if (gameState == GameState.MULTIPLAYER) {
            Multiplayer.disconnect();
        }
        gameState = GameState.MAIN_MENU;
    }

    public void startPause() {
        gameState = GameState.PAUSE;
    }

    public void startSingleplayer() {

        gameState = GameState.SINGLEPLAYER;
    }

    public void startMultiplayer() {
        gameState = GameState.LOADING;
        loadMP = true;
    }

    private void connectMultiplayer() {
        if (Multiplayer.connect(ipField.getText().toLowerCase(), Integer.parseInt(portField.getText()))) {
            if (nameField.getText().length()<=0) {
                nameField.setText("Player"+((int) (Math.random()*100)));
            }
            saveData();
            Multiplayer.send("connect "+nameField.getText());
            gameState = GameState.MULTIPLAYER;
        } else {
            gameState = GameState.ERROR;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public static Main getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    private void saveData() {
        JSONObject root = new JSONObject();
        root.put("name", nameField.getText());
        root.put("ip", ipField.getText());
        root.put("port", portField.getText());
        FileIO.saveJSON("./config.json", root);
    }

    private void loadData() {
        JSONObject root = FileIO.loadJSON("./config.json");
        if (root == null) {
            return;
        }
        nameField.setText((String) root.get("name"));
        ipField.setText((String) root.get("ip"));
        portField.setText((String) root.get("port"));
    }

}
