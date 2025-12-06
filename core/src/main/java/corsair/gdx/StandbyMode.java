package corsair.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import space.earlygrey.shapedrawer.ShapeDrawer;

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
    public void draw(ShapeDrawer shapeDrawer) {
        // drawing logic (none for standby)

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
