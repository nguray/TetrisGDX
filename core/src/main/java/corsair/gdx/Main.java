package corsair.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.utils.ScreenUtils;
// import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
// import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.awt.im.InputContext;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
/**
 * Main application class for the Tetris GDX game.
 * 
 * Extends InputAdapter and implements ApplicationListener to handle
 * the game lifecycle and input events for the LibGDX framework.
 * 
 * Responsibilities:
 * - Manages game initialization and resource cleanup
 * - Coordinates game updates and rendering
 * - Processes keyboard input and delegates to the current game mode
 * - Manages input processing pipeline
 * 
 * @author [Raymond NGUYEN THANH]
 * @version 1.0
 */

public class Main extends InputAdapter implements ApplicationListener {
    private MyGame  myGame;


    @Override
    public void create() {

        myGame = new MyGame();

        Gdx.input.setInputProcessor(this);

        InputContext context = InputContext.getInstance();
        System.out.println(context.getLocale().toString());

    }

    @Override
    public void render() {
    
        //--
        myGame.update();

        //--
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        myGame.draw();

    
    }
	@Override

    public void resize (int width, int height) {

	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}


    @Override
    public void dispose() {
        myGame.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        //-------------------------------------------------------
        return myGame.currentMode.keyDown(keycode);
    }   

    @Override
    public boolean keyUp(int keycode) {
        //-------------------------------------------------------
        return myGame.currentMode.keyUp(keycode);
    }


}
