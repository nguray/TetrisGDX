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

    /**
     * Computes the number of completed lines on the game board.
     * A line is considered completed when all columns in that row contain a non-zero value (a placed block).
     * 
     * @return the number of completed lines on the board
     */
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

    /**
     * Freezes the current tetromino on the game board and computes completed lines.
     * 
     * This method places the current tetromino's blocks onto the game board by converting
     * their world coordinates to board indices. It then checks for completed lines and
     * updates the score accordingly.
     * 
     * The method iterates through each vector (block) of the current tetromino, calculates
     * its grid position, and marks it on the board if it falls within valid bounds.
     * After freezing the tetromino, it computes any completed lines and awards points.
     * 
     * @return the number of lines completed after freezing the tetromino
     */
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

    /**
     * Erases the first completed line found on the game board.
     * 
     * Scans the board from top to bottom to find the first row where all cells are filled (non-zero).
     * When a completed row is found, shifts all rows above it down by one position, effectively
     * removing the completed line. The top row is cleared in the process.
     * 
     * The method uses a 1D array representation of the 2D board, where each cell is accessed
     * using the formula: board[row * NB_COLUMNS + column].
     * 
     * After erasing a line, the method returns immediately without checking for additional
     * completed lines. Subsequent calls are needed to erase any remaining completed lines.
     */
    public void EraseFirstCompletedLine()
    {
        //---------------------------------------------------
        for (int r = 0; r < Globals.NB_ROWS; r++)
        {
            boolean fCompleted = true;
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
                //-- Décaler d'une ligne du plateau
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

    /**
     * Checks if the game is over by examining the top row of the board.
     * <p>
     * The game is considered over if any cell in the top row (represented by the
     * first row of the board array) is occupied (i.e., not zero).
     * </p>
     *
     * @return {@code true} if the game is over (at least one cell in the top row is occupied),
     *         {@code false} otherwise.
     */
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

    /**
     * Draws the current state of the game board using the provided ShapeDrawer.
     * <p>
     * Iterates through each cell of the board and, for non-empty cells, draws a filled rectangle
     * at the corresponding position with the color associated with the tetromino type.
     * </p>
     *
     * @param drawer the ShapeDrawer used to render filled rectangles for each occupied cell
     */
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

    /**
     * Renders the current game frame.
     * <p>
     * This method handles the following rendering tasks:
     * <ul>
     *   <li>Begins the sprite batch for drawing.</li>
     *   <li>Draws the board background using the specified color.</li>
     *   <li>Renders the tetromino remains present on the board.</li>
     *   <li>Delegates additional drawing to the current game mode.</li>
     *   <li>Displays the current score.</li>
     *   <li>Ends the sprite batch after all drawing operations are complete.</li>
     * </ul>
     */
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

    /**
     * Updates the game state each frame.
     * 
     * Handles rotation of the next tetromino every 500 milliseconds,
     * updates the current game mode, and checks for game over conditions.
     * If the game is over, validates and stores the high score.
     */
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

    /**
     * Computes the score based on the number of lines cleared.
     *
     * @param nbLines the number of lines cleared in a single move
     * @return the score awarded for clearing the specified number of lines:
     *         - 0 lines: 0 points
     *         - 1 line: 40 points
     *         - 2 lines: 100 points
     *         - 3 lines: 300 points
     *         - 4 lines: 1200 points
     *         - 5+ lines: 2000 points
     */
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

    /**
     * Draws the current score on the screen using the provided SpriteBatch.
     * 
     * The score is displayed in the bottom-left area of the screen with a
     * 6-digit zero-padded format (e.g., "SCORE : 000123").
     * 
     * @param batch the SpriteBatch used to render text to the screen
     */
    public void drawScore(com.badlogic.gdx.graphics.g2d.SpriteBatch batch)
    {
        //-- draw score
        font22.draw(batch, String.format("SCORE : %06d", score),
                        Globals.LEFT, Globals.BOTTOM-10);
    }

    /**
     * Saves the current high scores to a file named "highscores.txt".
     * 
     * Each high score is written to the file in CSV format with the player name
     * and score separated by a comma on each line.
     * 
     * @throws IOException if an I/O error occurs during file writing
     */
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

    /**
     * Loads high scores from a text file and populates the highScores array.
     * 
     * Reads from "highscores.txt" located in the internal files directory.
     * The file format should contain comma-separated values with the player name
     * and score on each line (e.g., "PlayerName,1000").
     * 
     * The method parses up to 10 high score entries into the highScores array.
     * Lines that do not contain exactly 2 comma-separated values are skipped.
     * 
     * If an error occurs during file reading or parsing, an error message is
     * printed to the console and the exception is logged via printStackTrace().
     * 
     * @throws NumberFormatException if a score value cannot be parsed as an integer
     */
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

    /**
     * Determines if a given score qualifies as a high score and returns its position.
     *
     * @param score the score to check against the high scores list
     * @return the index position (0-9) where the score would be placed in the high scores list,
     *         or -1 if the score does not qualify as a high score
     */
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

    /**
     * Inserts a high score at the specified position in the high scores array.
     * 
     * If the given id is within the valid range (0-9), this method shifts existing
     * high scores down by one position and inserts the new high score at the specified id.
     * High scores at positions greater than id are shifted to make room for the new entry.
     * 
     * @param id the position at which to insert the high score (must be between 0 and 9 inclusive)
     * @param name the name associated with the high score
     * @param score the score value to insert
     */
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

    /**
     * Checks if the current score qualifies as a high score and handles the result.
     * 
     * If the score is a high score, inserts it into the high scores list with the player's name,
     * transitions to high scores display mode, and reinitializes the game.
     * If the score is not a high score, reinitializes the game and transitions to game over mode.
     */
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