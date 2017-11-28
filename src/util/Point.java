package util;

/**
 * Sijaintia rasteroidussa avaruudessa säilyttävä luokka.
 */
public class Point {
	private int x;
	private int y;
        public Point(int xParam, int yParam){
            x=xParam;
            y=yParam;
        }
        public Point show(){
            System.out.println("Point: "+x+" "+y);
            return this;
        }
        public int getX(){
            return x;
        }
        public int getY(){
            return y;
        }

}
