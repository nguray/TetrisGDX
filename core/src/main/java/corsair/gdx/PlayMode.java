package corsair.gdx;

import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class PlayMode implements GameMode {

    private MyGame game;
    private int velH  = 0;
    private int nbCompletedLines = 0;
    private long startTimeV = 0;
    private long startTimeH = 0;
    private long startTimeE = 0;
    private int horizontalMove = 0;
    private int horizontalStartColumn = 0;
    private boolean fRotateTetromino = false;
    private boolean fDrop = false;
    private boolean fFastDown = false;
    private final Color redColor = new Color(1.0f,0,0,1.0f);

    public Supplier<Boolean> IsOutLimit=null;


    public PlayMode(MyGame game){
        this.game = game;
    }

    @Override
    public void init() {
        // Initialization code for PlayMode
        velH = 0;
        fDrop = false;
        fFastDown = false;
        fRotateTetromino = false;
        startTimeV = System.currentTimeMillis();
        startTimeH = startTimeV;
        startTimeE = startTimeV;

    }

    @Override
    public boolean keyDown(int keycode){
        // Handle input for PlayMode
        switch (keycode) {
            case Input.Keys.UP:
                fRotateTetromino = true;
                break;
            case Input.Keys.LEFT:
                velH = -1;
                IsOutLimit = game.curTetromino::IsOutLeft;
                break;
            case Input.Keys.RIGHT:
                velH = 1;
                IsOutLimit = game.curTetromino::IsOutRight;
                 break;
            case Input.Keys.DOWN:
                fFastDown = true;
                break;
            case Input.Keys.SPACE:
                fDrop = true;
                 break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode){
        // Handle input for PlayMode
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                velH = 0;
                break;
            case Input.Keys.DOWN:
                fFastDown = false;
            default:
                break;
        }
        return false;
    }

    @Override
    public void draw(ShapeDrawer drawer) {
        
        // Render graphics for PlayMode

        game.curTetromino.draw(drawer);

        game.nextTetromino.draw(drawer);

        drawer.setColor(redColor);
        drawer.circle(game.curTetromino.x, game.curTetromino.y, 5.0f);

    	//drawer.setColor(blueColor);
        //drawer.line(Globals.LEFT, Globals.BOTTOM, Globals.RIGHT, Globals.BOTTOM);
        //drawer.line(0, 0, Globals.WIN_WIDTH, Globals.WIN_HEIGHT);
        //drawer.filledRectangle(50, Globals.WIN_HEIGHT-50, 25, 25);

    }

@Override
    public void update() {
        // Update game state for PlayMode

        if (nbCompletedLines > 0)
        {
            long currentTime = System.currentTimeMillis(); 
            if ((currentTime - startTimeE) > 250)
            {
                startTimeE = currentTime;
                nbCompletedLines--;
                game.EraseFirstCompletedLine();
                game.playExplosionSound();
            }

        }

        if (horizontalMove != 0)
        {

            long currentTime = System.currentTimeMillis(); 

            if ((currentTime - startTimeH) > 20)
            {
                for (int i = 0; i < 5; i++)
                {

                    var backupX = game.curTetromino.x;
                    game.curTetromino.x += horizontalMove;

                    if (IsOutLimit.get())
                    {
                        game.curTetromino.x = backupX;
                        horizontalMove = 0;
                        break;
                    }
                    else
                    {
                        if (game.curTetromino.HitGround(game.board))
                        {
                            game.curTetromino.x = backupX;
                            horizontalMove = 0;
                            break;
                        }
                    }

                    if (horizontalMove != 0)
                    {
                        startTimeH = currentTime;
                        if (horizontalStartColumn != game.curTetromino.Column())
                        {
                            game.curTetromino.x = backupX;
                            horizontalMove = 0;
                            break;
                        }

                    }

                }
            }

        }else if (fRotateTetromino)
        {
            //-- Rotate current tetromino when there's no more horizontal move
            game.curTetromino.RotateRight();
            if (game.curTetromino.HitGround(game.board))
            {
                //-- Undo Rotate
                game.curTetromino.RotateLeft();
            }
            else if (game.curTetromino.IsOutRight())
            {
                var backupX = game.curTetromino.x;
                //-- Move Inside board
                while (game.curTetromino.IsOutRight())
                {
                    game.curTetromino.x--;
                }
                if (game.curTetromino.HitGround(game.board))
                {
                    game.curTetromino.x = backupX;
                    //-- Undo Rotate
                    game.curTetromino.RotateLeft();

                }
            }
            else if (game.curTetromino.IsOutLeft())
            {
                var backupX = game.curTetromino.x;
                //-- Move Inside Board
                while (game.curTetromino.IsOutLeft())
                {
                    game.curTetromino.x++;
                }
                if (game.curTetromino.HitGround(game.board))
                {
                    game.curTetromino.x = backupX;
                    //-- Undo Rotate
                    game.curTetromino.RotateLeft();
                }
            }
            fRotateTetromino = false;
        }
        else if (fDrop)
        {

            var currentTime = System.currentTimeMillis(); 

            if ((currentTime - startTimeV) > 10)
            {
                startTimeV = currentTime;
                for (int i = 0; i < 6; i++)
                {
                    //-- Move down to Check
                    game.curTetromino.y--;
                    if (game.curTetromino.HitGround(game.board))
                    {
                        game.curTetromino.y++;
                        nbCompletedLines = game.FreezeCurTetromino();
                        game.NewTetromino();
                        fDrop = false;
                        break;
                    }
                    else if (game.curTetromino.IsOutBottom())
                    {
                        game.curTetromino.y++;
                        nbCompletedLines = game.FreezeCurTetromino();
                        game.NewTetromino();
                        fDrop = false;
                        break;
                    }
                    if (fDrop && (velH != 0))
                    {
                        if ((currentTime - startTimeH) > 15)
                        {
                            var backupX = game.curTetromino.x;
                            game.curTetromino.x += velH;
                            if (IsOutLimit.get())
                            {
                                game.curTetromino.x = backupX;
                            }
                            else
                            {
                                if (game.curTetromino.HitGround(game.board))
                                {
                                    game.curTetromino.x = backupX;
                                }
                                else
                                {
                                    horizontalMove = velH;
                                    horizontalStartColumn = game.curTetromino.Column();
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        
        }else{

            long currentTime = System.currentTimeMillis(); 
            int limitElapse = fFastDown ? 15 : 30;

            if ((currentTime-startTimeV)>limitElapse){
                
                startTimeV = currentTime;
                for (int i = 0; i < 3; i++)
                {
                    game.curTetromino.y--;
                    
                    //System.out.println("Y="+game.curTetromino.y);

                    if (game.curTetromino.HitGround(game.board))
                    {
                        game.curTetromino.y++;
                        nbCompletedLines = game.FreezeCurTetromino();
                        game.NewTetromino();
                        break;
                    }
                    else if (game.curTetromino.IsOutBottom())
                    {
                        game.curTetromino.y++;
                        nbCompletedLines = game.FreezeCurTetromino();
                        game.NewTetromino();
                        break;
                    }

                    if (velH != 0)
                    {
                        if ((currentTime - startTimeH) > 15)
                        {

                            var backupX = game.curTetromino.x;
                            game.curTetromino.x += velH;

                            if (IsOutLimit.get())
                            {
                                game.curTetromino.x = backupX;
                            }
                            else
                            {
                                if (game.curTetromino.HitGround(game.board))
                                {
                                    game.curTetromino.x -= velH;
                                }
                                else
                                {
                                    startTimeH = currentTime;
                                    horizontalMove = velH;
                                    horizontalStartColumn = game.curTetromino.Column();
                                    break;
                                }
                            }

                        }

                    }


                }

            }

        }

    }




}
