package corsair.gdx;

import space.earlygrey.shapedrawer.ShapeDrawer;

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
        return false;
    }

}
