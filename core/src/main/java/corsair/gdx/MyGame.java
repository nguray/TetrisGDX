package corsair.gdx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class MyGame{

    class HighScore {
        String name;
        int    score;

        HighScore(String name, int score) {
            this.name  = name;
            this.score = score;
        }
    }

    public Tetromino curTetromino;
    public Tetromino nextTetromino;
    public Random random;
    public int[] board = new int[Globals.NB_ROWS * Globals.NB_COLUMNS];
    private final Color blueColor = new Color(0,0,0.25f,1.0f);

    public int score = 0;
    public int idTetrominoBag = 14;
    public int[] tetrominoBag = new int[] { 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7 };

    public long startTimeR = 0;
    public int i_color = 0;

    HighScore[] highScores = new HighScore[]{
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0),
        new HighScore("XXXX", 0)
    };
    int idHighScore = -1;
    String playerName = "";

	/** explosion sound **/
	private final Sound explosion;

    BitmapFont  font20;
    BitmapFont  font22;
    BitmapFont  font26;
	Music       music;

    public SpriteBatch batch;
    public Texture texture;
    public ShapeDrawer drawer;

    public StandbyMode standbyModeMode;
    public PlayMode    playMode;
    public GameOverMode gameOverMode;
    public HighScoresMode highScoresMode;

    public GameMode    currentMode;

    public MyGame(){
        System.out.println("MyGame initialized");

        random = new Random();
        random.setSeed((long) System.currentTimeMillis());

		explosion = Gdx.audio.newSound(Gdx.files.internal("109662__grunz__success.wav"));

		music = Gdx.audio.newMusic(Gdx.files.getFileHandle("Tetris.wav", FileType.Internal));
		music.setLooping(true);
        music.setVolume(0.3f);
		music.play();


        FreeTypeFontGenerator generator = 
                new FreeTypeFontGenerator(Gdx.files.internal("sansation.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 22;
        parameter.borderWidth = 1;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.color = new Color(1.0f, 1.0f, 0, 1.0f);;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
        font22 = generator.generateFont(parameter); // font size 22 pixels


        parameter.size = 20;
        parameter.shadowColor = new Color(1.0f, 0, 0, 1.0f);
        font20 = generator.generateFont(parameter); // font size 26 pixels

        parameter.size = 26;
        parameter.shadowColor = new Color(1.0f, 223.0f/255.0f, 0, 1.0f);
        font26 = generator.generateFont(parameter); // font size 26 pixels

        generator.dispose(); // don't forget to dispose to avoid memory leaks!


        batch = new SpriteBatch();

        //create single white pixel
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap);
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);

        drawer = new ShapeDrawer(batch, region);


        InitGame();

        NewTetromino();

        standbyModeMode = new StandbyMode(this);
        playMode        = new PlayMode(this);
        gameOverMode    = new GameOverMode(this);
        highScoresMode  = new HighScoresMode(this);

        //currentMode = (GameMode) playMode;
        //currentMode.init();
        SetStandbyMode();

        LoadHighScores();

    }

    public void SetStandbyMode()
    {
        currentMode = (GameMode) standbyModeMode;
        currentMode.init();
    }

    public void SetPlayMode()
    {
        currentMode = (GameMode) playMode;
        currentMode.init();
    }

    public void SetGameOverMode()
    {
        currentMode = (GameMode) gameOverMode;
        currentMode.init();
    }

    public void SetHighScoresMode()
    {
        currentMode = (GameMode) highScoresMode;
        currentMode.init();
    }
    
    public int TetrisRandomizer()
    {
        int iSrc;
        int iTyp = 0;
        if (idTetrominoBag < 14)
        {
            iTyp = tetrominoBag[idTetrominoBag];
            idTetrominoBag++;
        }
        else
        {
            //-- Shuttle bag
            if (random!=null)
            {
                for (int i = 0; i < tetrominoBag.length; i++)
                {
                    iSrc = random.nextInt( 0, 14);
                    iTyp = tetrominoBag[iSrc];
                    tetrominoBag[iSrc] = tetrominoBag[0];
                    tetrominoBag[0] = iTyp;
                }                    
            }
            iTyp = tetrominoBag[0];
            idTetrominoBag = 1;

        }
        return iTyp;
    }

    public void NewTetromino()
    {
        if (nextTetromino == null)
        {
            nextTetromino = new Tetromino( TetrisRandomizer(), Globals.RIGHT + 3* Globals.cellSize, 10 * Globals.cellSize);
        }
        curTetromino = nextTetromino;
        curTetromino.x = Globals.LEFT+Globals.cellSize*Globals.NB_COLUMNS/2;
        curTetromino.y = Globals.TOP + curTetromino.MaxY1() * Globals.cellSize;
        nextTetromino = new Tetromino( TetrisRandomizer(), Globals.RIGHT + 3* Globals.cellSize, 10 * Globals.cellSize);

    }

    public int ComputeCompledLines()
    {

        int nbLines = 0;
        boolean fCompleted;
        for (int r = 0; r < Globals.NB_ROWS; r++)
        {
            fCompleted = true;
            for (int c = 0; c < Globals.NB_COLUMNS; c++)
            {
                if (board[r * Globals.NB_COLUMNS + c] == 0)
                {
                    fCompleted = false;
                    break;
                }
            }
            if (fCompleted)
            {
                nbLines++;
            }
        }
        return nbLines;
    }

    public int FreezeCurTetromino()
    {
        int nbCompletedLines = 0;
        //----------------------------------------------------
        if (curTetromino!=null)
        {
            for (var v : curTetromino.vectors)
            {
                int x = (int) (v.x*Globals.cellSize + curTetromino.x + 1);
                int y = (int) (v.y*Globals.cellSize - Globals.cellSize + curTetromino.y + 1);

                int ix = (int) ((x-Globals.LEFT) / Globals.cellSize);
                int iy = (int) ((Globals.TOP-y-Globals.cellSize) / Globals.cellSize);


                if ((ix >= 0) && (ix < Globals.NB_COLUMNS) && (iy >= 0) && (iy < Globals.NB_ROWS))
                {
                    board[iy * Globals.NB_COLUMNS + ix] = curTetromino.type;
                }
            }
            //--
            nbCompletedLines = ComputeCompledLines();
            if (nbCompletedLines > 0)
            {
                score += ComputeScore(nbCompletedLines);
            }
        }
        return nbCompletedLines;

    }

    public void EraseFirstCompletedLine()
    {
        //---------------------------------------------------
        boolean fCompleted = false;
        for (int r = 0; r < Globals.NB_ROWS; r++)
        {
            fCompleted = true;
            for (int c = 0; c < Globals.NB_COLUMNS; c++)
            {
                if (board[r * Globals.NB_COLUMNS + c] == 0)
                {
                    fCompleted = false;
                    break;
                }
            }
            if (fCompleted)
            {
                //-- Décaler d'une ligne le plateau
                for (int r1 = r; r1 > 0; r1--)
                {
                    for (int c1 = 0; c1 < Globals.NB_COLUMNS; c1++)
                    {
                        board[r1 * Globals.NB_COLUMNS + c1] = board[(r1 - 1) * Globals.NB_COLUMNS + c1];
                    }
                }
                return;
            }
        }
    }

    public void InitGame()
    {
        //--------------------------------------------------------
        score = 0;

        for (int i = 0; i < board.length; i++)
        {
            board[i] = 0;
        }

    }

    public boolean IsGameOver()
    {
        //----------------------------------------
        for (int c = 0; c < Globals.NB_COLUMNS; c++)
        {
            if (board[c] != 0)
            {
                return true;
            }
        }
        return false;
    }

    public void drawBoard(ShapeDrawer drawer)
    {
        int     fx,fy;
        Color   fillColor;
        //----------------------------------------
        var a = Globals.cellSize - 2;

        for (int l = 0; l < Globals.NB_ROWS; l++)
        {
            for (int c = 0; c < Globals.NB_COLUMNS; c++)
            {
                var typ = board[l * Globals.NB_COLUMNS + c];
                if (typ != 0)
                {
                    fillColor = Tetromino.colors[typ];
                    drawer.setColor(fillColor);
                    fy = Globals.TOP - l*Globals.cellSize - Globals.cellSize;
                    fx = Globals.LEFT + c*Globals.cellSize;
                    drawer.filledRectangle(fx, fy, a, a);

                }
            }
        }

    }

    public void draw(){
        //-------------------------------------------------------
        batch.begin();

        // Draw the board background
        drawer.filledRectangle( Globals.LEFT, Globals.BOTTOM, 
            Globals.cellSize*Globals.NB_COLUMNS, Globals.cellSize*Globals.NB_ROWS, blueColor);
        
        // Draw tetrominos remains on board
        drawBoard(drawer);
        
        currentMode.draw(drawer);
        
        drawScore(batch);

        batch.end();


    }
    public void update()
    {
        //-----------------------------------------

        long currentTime = System.currentTimeMillis(); 

        if ((currentTime - startTimeR) > 500)
        {
            startTimeR = currentTime;
            nextTetromino.RotateRight();
            i_color++;

        }

        currentMode.update();

        //-- Check Game Over
        if (IsGameOver())
        {
           CheckHighScore();
        }


    }

    public int ComputeScore(int nbLines)
    {
        //----------------------------------------------------
        return  switch(nbLines)
        {
            case 0 -> 0;
            case 1 -> 40;
            case 2 -> 100;
            case 3 -> 300;
            case 4 -> 1200;
            default -> 2000;
        };
    }

    public void drawScore(com.badlogic.gdx.graphics.g2d.SpriteBatch batch)
    {
        //-- draw score
        font22.draw(batch, String.format("SCORE : %05d", score),
                        Globals.LEFT, Globals.BOTTOM-10);
    }

    public void SaveHighScores()
    {
        //--
        try {
            FileWriter myWriter = new FileWriter("highscores.txt");
            for (HighScore hs : highScores)
            {
                myWriter.write(hs.name + "," + hs.score + "\n");
            }
            myWriter.close();  // must close manually
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    
    }

    public void LoadHighScores()
    {
        //--
        try {
            var lines = Gdx.files.internal("highscores.txt").readString().split("\n");
            int i = 0;
            for (var line : lines)
            {
                var parts = line.split(",");
                if (parts.length == 2)
                {
                    highScores[i] = new HighScore(parts[0], Integer.parseInt(parts[1]));
                    i++;
                    if (i >= 10)
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while loading high scores.");
            e.printStackTrace();
        }
    
    }

    public int IsHighScore(int score)
    {
        //---------------------------------------------------
        for (int i = 0; i < 10; i++)
        {
            if (score > highScores[i].score)
            {
                return i;
            }
        }
        return -1;
    }

    public void InsertHighScore(int id, String name, int score)
    {
        if ((id >= 0) && (id < 10))
        {
            for (int i = 9; i > id; i--)
            {
                highScores[i] = highScores[i - 1];
            }
            highScores[id] =  new HighScore(name, score);
        }
    }

    public void CheckHighScore()
    {
        idHighScore = IsHighScore(score);
        if (idHighScore >= 0)
        {
            InsertHighScore(idHighScore, playerName, score);
            SetHighScoresMode();
            InitGame();
        }
        else
        {
            InitGame();
            SetGameOverMode();
        }
        
    }

    public void playExplosionSound()
    {
        explosion.play(0.5f);
    }   

    public void dispose() {
        if (explosion != null) {
            explosion.dispose();
        }
        if (music != null) {
            music.dispose();
        }
        if (font20!=null){
            font20.dispose();
        }
        if (font22!=null){
            font22.dispose();
        }
        if (font26!=null){
            font26.dispose();   
        }
        if (batch!=null){
            batch.dispose();
        }
        if (texture!=null){
            texture.dispose();
        }

        SaveHighScores();

    }

}