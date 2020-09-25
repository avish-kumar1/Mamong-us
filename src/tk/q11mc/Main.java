package tk.q11mc;

import com.siinus.simpleGrafixShader.Light;
import com.siinus.simpleGrafixShader.ShaderImage;
import com.siinus.simpleGrafixShader.ShaderProgram;
import com.siinus.simpleGrafixShader.ShaderRenderer;

public class Main extends ShaderProgram {
    ShaderImage spritePlayer = new ShaderImage("/test.png");
    ShaderImage spriteWall = new ShaderImage("/test.png");
    Light light = new Light(150, 0xffffffff);
    Player player;
    Wall wall;

    public static void main(String[] args) {
        new Main().initShader();
    }

    public Main() {
        setIconImage(spritePlayer);
        wall = new Wall(this, spriteWall, 126, 126);
        player = new Player(this, spritePlayer, 126,126);
        wall.x = 500;
        wall.y = 250;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        for (GameObject object : GameObject.objects) {
            object.update();
        }
    }

    @Override
    public void render() {
        getRenderer().setBgColor(0xffffffff);
        for (GameObject object : GameObject.objects) {
            object.render();
        }
        //getShaderRenderer().drawLight(light, getInput().getMouseX(), getInput().getMouseY());
    }

    @Override
    public void stop() {

    }

    public ShaderRenderer getShader() {
        return getShaderRenderer();
    }
}