package corsair.gdx;

public class Globals {
        public final static int WIN_WIDTH = 480;
        public final static int WIN_HEIGHT = 560;
  
        public final static int NB_ROWS = 20;
        public final static int NB_COLUMNS = 12;
        public static int cellSize = WIN_WIDTH / (NB_COLUMNS + 7);

        public final static int LEFT = 10;
        public final static int TOP = WIN_HEIGHT - 10;
        public final static int RIGHT = LEFT + NB_COLUMNS * cellSize;
        public final static int BOTTOM = TOP - NB_ROWS * cellSize;

}
