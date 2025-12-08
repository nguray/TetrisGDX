package corsair.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * GameOverMode represents the game state displayed when the player loses.
 * This mode handles rendering the "GAME OVER" message and provides options
 * for the player to continue or exit the game.
 * 
 * <p>Features:
 * <ul>
 *   <li>Displays "GAME OVER" text centered on screen</li>
 *   <li>Shows instructions to press Space to continue</li>
 *   <li>Handles Space key input to return to standby mode</li>
 *   <li>Handles Escape key input to exit the application</li>
 * </ul>
 * 
 * @author Corsair
 * @see GameMode
 * @see MyGame
 */
public class GameOverMode implements GameMode{
    private final MyGame game;

    public GameOverMode(MyGame myGame){
        this.game = myGame;
    }
    
    @Override
    public void init() {
        // initialization logic (none for standby)
    }

    @Override
    public void draw(ShapeDrawer shapeDrawer) {
        // drawing logic (none for standby)
        float y = Globals.TOP-Globals.cellSize*6;
        game.font26.draw(game.batch, "GAME OVER",
                        Globals.LEFT+Globals.cellSize*3, y);

        y -= 64;
        game.font20.draw(game.batch, "Press Space to Continue",
                        Globals.LEFT+Globals.cellSize*1.8f, y);
    
    }

    @Override
    public void update() {
        // update logic (none for standby)
    }

    @Override
    public boolean keyUp(int keycode) {
        // handle key release; return false to indicate not consumed
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        // handle key press; return false to indicate not consumed
        switch (keycode) {
            case Input.Keys.SPACE:
                //--
                game.SetStandbyMode();
                break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            default:
                break;
        }
        return false;
    }

}
