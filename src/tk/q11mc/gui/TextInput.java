package tk.q11mc.gui;

import com.siinus.simpleGrafix.gfx.Font;
import com.siinus.simpleGrafix.gfx.ImageTile;
import tk.q11mc.Main;
import tk.q11mc.Utils;

public class TextInput extends GUIObject {
    private final ImageTile image;
    private final int color;
    private final Font font;

    private boolean activated = false;
    private StringBuilder text = new StringBuilder();

    /**
     * Creates a new GUI object.
     *
     * @param program The associated program
     * @param sprite  The texture
     * @param x       The offset x
     * @param y       The offset y
     * @param width   The width of the bounding box
     * @param height  The width of the bounding box
     * @param textColor The color of the text
     */
    public TextInput(Main program, ImageTile sprite, int x, int y, int width, int height, int textColor, Font font) {
        super(program, sprite, x, y, width, height);
        this.image = sprite;
        color = textColor;
        this.font = font;
    }

    @Override
    public void update() {
        if (isMouseOver() && program.getInput().isButtonDown(1)) {
            activated = true;
        }
        if (program.getInput().isButtonDown(1) && !isMouseOver()) {
            activated = false;
        }
        if (activated) {
            int kd;
            if ((kd = Utils.getKey(program.getInput())) > 0) {
                text.append((char) kd);
            }
        }
    }

    @Override
    public void render() {
        program.getRenderer().drawImageTile(image, x, y, 0, activated?1:0);
        program.getRenderer().drawText(text.toString(), x + 10, y + 10, color, font);
    }

    public StringBuilder getText() {
        return text;
    }
}