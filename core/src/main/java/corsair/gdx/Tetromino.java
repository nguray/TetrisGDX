package corsair.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Tetromino {
    public int type = 0;
    public float x;
    public float y;
    private final Color color;
    public Vector2 vectors[] = new Vector2[4];


    private final static Vector2[] TypeTetrominos = {
        new Vector2(0,0),  new Vector2(0,0),   new Vector2(0,0), new Vector2(0,0),
        new Vector2(0,-1), new Vector2(0,0), new Vector2(-1,0), new Vector2(-1,1),
        new Vector2(0,-1), new Vector2(0,0), new Vector2(1,0), new Vector2(1,1),
        new Vector2(0,-1), new Vector2(0,0), new Vector2(0,1), new Vector2(0,2),
        new Vector2(-1,0), new Vector2(0,0), new Vector2(1,0), new Vector2(0,1),
        new Vector2(0,0), new Vector2(1,0), new Vector2(0,1), new Vector2(1,1),
        new Vector2(-1,-1), new Vector2(0,-1), new Vector2(0,0), new Vector2(0,1),
        new Vector2(1,-1), new Vector2(0,-1), new Vector2(0,0), new Vector2(0,1)
    };

    public final static Color[] colors = {
        new Color(0.0f,0.0f,0.0f,1.0f),
        new Color(0.0f,1.0f,0.0f,1.0f),
        new Color(1.0f,0.0f,0.0f,1.0f),
        new Color(1.0f,0.0f,1.0f,1.0f),
        new Color(0.0f,1.0f,1.0f,1.0f),
        new Color(0.0f,0.0f,1.0f,1.0f),
        new Color(1.0f,0.5f,0.0f,1.0f),
        new Color(1.0f,1.0f,0.0f,1.0f)
    };

    public Tetromino(int ityp,float x,float y)
    {
        this.type = ityp;
        this.x = x;
        this.y = y;
        this.color = colors[ityp];
        var id = ityp*4;
        for (int i=0;i<4;i++){
            var v = TypeTetrominos[id+i];
            vectors[i] = (new Vector2(v.x,v.y));
        }
    }
    
    public void draw(ShapeDrawer drawer)
    {
        //-------------------------------------------------------
        drawer.setColor(color);

        float cs = Globals.cellSize - 1;
        //drawer.filledRectangle(this.x, this.y, cs, cs);

        for (Vector2 v : vectors)
        {
            float vx = v.x * Globals.cellSize + this.x;
            float vy = v.y * Globals.cellSize + this.y;
            drawer.filledRectangle(vx, vy, cs, cs);
        }
        
    }

    public void RotateRight()
    {
        float     tmpx,tmpy;
        //-------------------------------------------
        if (type!=5){
            for (int i=0;i<vectors.length;i++){
                var v = vectors[i];
                tmpx = -v.y;
                tmpy = v.x;
                v.x = tmpx;
                v.y = tmpy;
                vectors[i]= v;
            }
        }

    }

    public void RotateLeft()
    {
        float     tmpx,tmpy;
        //-------------------------------------------
        if (type!=5){
            for (int i=0;i<vectors.length;i++){
                var v = vectors[i];
                tmpx = v.y;
                tmpy = -v.x;
                v.x = tmpx;
                v.y = tmpy;
                vectors[i]= v;
            }
        }            
    }

    public float MaxY1() {
        float ly; 
        float maxY = vectors[0].y;
        for (int i=1;i<vectors.length;i++){
            ly = vectors[i].y;
            if (ly > maxY) {
                maxY = ly;
            }
        }
        return maxY;
    }

    public float MinY1() {
        float ly; 
        float minY = vectors[0].y;
        for (int i=1;i<vectors.length;i++){
            ly = vectors[i].y;
            if (ly < minY) {
                minY = ly;
            }
        }
        return minY;
    }


    public float MinX1() {
        float lx; 
        float minX = vectors[0].x;
        for (int i=1;i<vectors.length;i++){
            lx = vectors[i].x;
            if (lx < minX) {
                minX = lx;
            }
        }
        return minX;
    }

    public float MaxX1() {
        float lx; 
        float maxX = vectors[0].x;
        for (int i=1;i<vectors.length;i++){
            lx = vectors[i].x;
            if (lx > maxX) {
                maxX = lx;
            }
        }
        return maxX;
    }

    public boolean IsOutLeft(){
        float l = MinX1()*Globals.cellSize + x - Globals.LEFT;
        return (l < 0);
    }

    public boolean IsOutRight(){
        float r = MaxX1()*Globals.cellSize + Globals.cellSize + x - Globals.LEFT;
        return (r > Globals.NB_COLUMNS*Globals.cellSize);
    }

    public boolean IsOutBottom(){
        float b = MinY1()*Globals.cellSize + y;
        return (b<Globals.BOTTOM);
    }


    public int Column(){
        //-------------------------------------------
        return (int) ((x-Globals.LEFT) / Globals.cellSize);
    }

    public boolean HitGround(int[] board){
        int x,y;

        interface HitFunc {
            Boolean run(float x, float y);
        }
        
        HitFunc Hit = (x1, y1) -> {
            int ix = (int) ((x1-Globals.LEFT) / Globals.cellSize);
            int iy = (int) ((Globals.TOP-y1-Globals.cellSize) / Globals.cellSize);

            //System.out.println("HitGround: x=" + x1 + " y=" + y1 + " ix=" + ix + " iy=" + iy);

            if ((ix >= 0) && ix < Globals.NB_COLUMNS && (iy >= 0) && (iy < Globals.NB_ROWS)){
                if (board[iy*Globals.NB_COLUMNS + ix] != 0) {
                    return Boolean.TRUE;
                }

            }
            return Boolean.FALSE;
        };

        for(var v : vectors){

            x = (int) (v.x*Globals.cellSize + this.x + 1);
            y = (int) (v.y*Globals.cellSize - Globals.cellSize + this.y + 1);
            if (Hit.run(x,y)){
                return true;
            }

            // ix = x / Globals.cellSize;
            // iy = y / Globals.cellSize;
            // if ((ix >= 0) && ix < Globals.NB_COLUMNS && (iy >= 0) && (iy < Globals.NB_ROWS)){
            //     iHit = iy*Globals.NB_COLUMNS + ix;
            //     if (board[iHit] != 0) {
            //         return true;
            //     }

            // }

            x = (int) (v.x*Globals.cellSize + Globals.cellSize - 1 + this.x);
            y = (int) (v.y*Globals.cellSize - Globals.cellSize + this.y + 1);
            if (Hit.run(x,y)){
                return true;
            }

            // ix = x / Globals.cellSize;
            // iy = y / Globals.cellSize;
            // if ((ix >= 0) && ix < Globals.NB_COLUMNS && (iy >= 0) && (iy < Globals.NB_ROWS)){
            //     iHit = iy*Globals.NB_COLUMNS + ix;
            //     if (board[iHit] != 0) {
            //         return true;
            //     }

            // }

            x = (int) (v.x*Globals.cellSize + Globals.cellSize - 1 + this.x);
            y = (int) (v.y*Globals.cellSize - 1 + this.y);
            if (Hit.run(x,y)){
                return true;
            }

            // ix = x / Globals.cellSize;
            // iy = y / Globals.cellSize;
            // if ((ix >= 0) && ix < Globals.NB_COLUMNS && (iy >= 0) && (iy < Globals.NB_ROWS)){
            //     iHit = iy*Globals.NB_COLUMNS + ix;
            //     if (board[iHit] != 0) {
            //         return true;
            //     }

            // }

            x = (int) (v.x*Globals.cellSize + this.x + 1);
            y = (int) (v.y*Globals.cellSize - 1 + this.y);
            if (Hit.run(x,y)){
                return true;
            }

            // ix = x / Globals.cellSize;
            // iy = y / Globals.cellSize;
            // if ((ix >= 0) && ix < Globals.NB_COLUMNS && (iy >= 0) && (iy < Globals.NB_ROWS)){
            //     iHit = iy*Globals.NB_COLUMNS + ix;
            //     if (board[iHit] != 0) {
            //         return true;
            //     }

            // }

        }


        return false;
    }


}
