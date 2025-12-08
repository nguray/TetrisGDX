package corsair.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * HighScoresMode manages the high scores display and player name input screen.
 * 
 * This class implements the GameMode interface and handles:
 * - Displaying high scores in a ranked list
 * - Allowing players to input their name when achieving a high score
 * - Keyboard input for name editing (backspace, character entry)
 * - Navigation back to standby mode or exit
 * 
 * Features:
 * - Supports up to 6 character names
 * - Default name "XXXX" if player doesn't enter a name
 * - Real-time name display update while editing
 * - Golden highlight for the currently edited high score entry
 * - Automatic save to persistent storage upon completion
 * 
 * Keyboard Controls:
 * - SPACE or ENTER: Confirm and return to standby mode
 * - BACKSPACE: Delete last character from player name
 * - ESCAPE: Exit application
 * - Other keys: Add character to player name (up to 6 characters)
 * 
 * @author Corsair
 */
public class HighScoresMode implements GameMode{
    private final MyGame game;


    public HighScoresMode(MyGame myGame){
        this.game = myGame;


    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        if (game.idHighScore>=0)
        {
            game.highScores[game.idHighScore].name = game.playerName;
        }
        
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        //System.out.println("Key Down: "+keycode);
        switch (keycode) {
            case Input.Keys.SPACE:
                //--
                game.SetStandbyMode();
                break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.BACKSPACE:
                if (game.playerName.length()>0)
                {
                    game.playerName = game.playerName.substring(0, game.playerName.length()-1);
                    game.highScores[game.idHighScore].name = game.playerName;
                }
                break;
            case Input.Keys.ENTER:
                //--
                if (game.playerName.length()==0)
                {
                    game.playerName = "XXXX";
                    game.highScores[game.idHighScore].name = game.playerName;
                }
                game.SaveHighScores();
                game.SetStandbyMode();
                break;
            default:

                if (game.playerName.length()<6)
                {

                    var ch = Input.Keys.toString(keycode);
                    if (ch.length()>1)
                        ch = ch.substring(ch.length() - 1);
                    game.playerName += ch;
                    game.highScores[game.idHighScore].name = game.playerName;

                }
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void draw(ShapeDrawer drawer) {
        // TODO Auto-generated method stub
        //System.out.println(game.score);
        float y = Globals.TOP-Globals.cellSize;
        game.font22.draw(game.batch, "HIGH SCORES",
                        Globals.LEFT+ Globals.cellSize*3.0f, y);

        //-- Draw selected score entry
        if (game.idHighScore>=0)
        {
            y -= Globals.cellSize*1.5f + Globals.cellSize*1.5f*(game.idHighScore+1) + 6;
            drawer.rectangle(Globals.LEFT+2, y, Globals.RIGHT-Globals.LEFT-4.0f, Globals.cellSize*1.5f-2.0f, Color.GOLDENROD);
        }

        y = Globals.TOP-Globals.cellSize;
        y -= 16.0f;
        for(int i=0; i<game.highScores.length; i++)
        {
            y -= Globals.cellSize*1.5f;
            game.font22.draw(game.batch, String.format("%s", game.highScores[i].name),
                            Globals.LEFT+ Globals.cellSize, y);
            game.font22.draw(game.batch, String.format("%06d", game.highScores[i].score),
                            Globals.LEFT+ Globals.cellSize*7.0f, y);

        }
        y -= Globals.cellSize*2.0f;
        game.font20.draw(game.batch, "Press Enter to Continue",
                        Globals.LEFT+Globals.cellSize*1.5f, y);
    }
    
    
}
