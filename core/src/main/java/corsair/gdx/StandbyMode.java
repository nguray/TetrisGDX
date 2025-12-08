package corsair.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Represents the standby/menu mode of the Tetris game.
 * 
 * This mode displays the main menu screen with the game title and instructions.
 * It handles user input to transition to play mode or exit the application.
 * 
 * The StandbyMode implements the GameMode interface and manages:
 * - Rendering of the title screen with game name, credit, and start instructions
 * - User input handling for game transitions (Space to start, Escape to exit)
 * - No active game logic or updates while in this mode
 * 
 * @author [Your Name]
 * @version 1.0
 * @see GameMode
 * @see MyGame
 */

public class StandbyMode implements GameMode{
    private final MyGame game;

    public StandbyMode(MyGame myGame){
        this.game = myGame;
    }
    
    @Override
    public void init() {
        // initialization logic (none for standby)
    }

    @Override
    public void draw(ShapeDrawer drawer ) {
        // drawing logic (none for standby)
        float y = Globals.TOP-Globals.cellSize*4;
        game.font26.draw(game.batch, "Tetris in Java",
                        Globals.LEFT+Globals.cellSize*3, y);

        y -= 50;
        game.font20.draw(game.batch, "Powered by libGDX",
                        Globals.LEFT+Globals.cellSize*2.5f, y);
        y -= 70;
        game.font20.draw(game.batch, "Press Space to Start",
                        Globals.LEFT+Globals.cellSize*2.3f, y);


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
                game.SetPlayMode();
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
