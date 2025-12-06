package corsair.gdx;

import space.earlygrey.shapedrawer.ShapeDrawer;

public interface GameMode {

    public void init();
    public void update();
    public boolean keyDown(int keycode);
    public boolean keyUp(int keycode);
    public void draw(ShapeDrawer drawer);  
}
