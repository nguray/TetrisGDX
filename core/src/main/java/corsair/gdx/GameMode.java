package corsair.gdx;

import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Represents a game mode in the TetrisGDX application.
 * Implementations of this interface define the behavior and rendering of different game modes.
 */
public interface GameMode {

    public void init();
    public void update();
    public boolean keyDown(int keycode);
    public boolean keyUp(int keycode);
    public void draw(ShapeDrawer drawer);  
}
