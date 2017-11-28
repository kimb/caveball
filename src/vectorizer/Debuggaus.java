/*
 * Debuggaus.java
 *
 * Created on 4. huhtikuuta 2004, 14:49
 */

package vectorizer;
import java.io.*;
import util.*;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Pieni Debuggausohjelma ei-graafiseen Vectorizerin ja GameMapin debuggaukseen.
 * Pystytään muuttamaan helposti kysymään käyttäjältä pikselin (x,y) -väriä.
 */

public class Debuggaus implements VectorizableImage, VectorizationParameters{

    BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
    int width=100,height=100;
    int[] colors={0,1};

    /** Luo olion, jonka avulla debugataan Vectorizeria ja GameMap:pia.
     *
     */ 
    public Debuggaus(String[] args) {
        Vector v=new Vector(1,0);
        v=v.turnLeft().show();
        v=v.turnLeft().show();
        v=v.turnLeft().show();
        v=v.turnLeft().show();
        v=v.turnRight().show();
        v=v.turnRight().show();
        v=v.turnRight().show();
        v=v.turnRight().show();
        v=new Vector(0,-1);
        v.scalarProduct(-1).show();
        System.out.println("heigth and width "+height+" "+width);
        System.out.println("Tyhjän väri: "+this.getZoneColors()[1]);
        Vectorizer vz=new Vectorizer((VectorizationParameters) this,
				(VectorizableImage) this, 2);
        System.out.println("vektoreita yhteensä: "+
				vz.getImageVectors().size());
        Iterator i=vz.getImageVectors().iterator();;
        v=new Vector(0,0);
        while (i.hasNext()) {
            v=v.add(((ImageVector)i.next()).getVector().show());
        }
        System.out.println("SUMMA: "+v.getX()+" "+v.getY());
        System.out.println("ans^2 "+v.squareLength());
  /**      GameMap map=new GameMap(vz.getImageVectors(),new ArrayList(),width,height);
        System.out.println("Alueessa oli vektoreita: "+map.getImageVectors(120,120,300,300).size());
        map=new GameMap(new ArrayList(),new ArrayList(), width, height);
        v=new Vector(2,1);
        System.out.println("Jos V vasemmalla W:tä, VxW >0"+v.vCrossProduct(new Vector(1,1)));
        v=new Vector(width-8,height-8);
        for (int k=0;k<10;k++)
        map.putImageVector(new ImageVector(new Vector(0,0), new Vector(k*10,height), 0, 1));
        i=map.getImageVectors(12,height,1,1).iterator();
        while(i.hasNext()) ((ImageVector)i.next()).getVector().show();*/
    }
    /** Palauttaa listan väreistä, joka kertoo, miten kuvaa pitää tulkita. 
     *  VectorizationParameters-interfacesta.
     */
    public int[] getZoneColors(){
        return colors;
    }
    
    /**
     * Luo uuden olion nimeltä debuggaus joka debuggaa ohjelmaa.
     * @param args annetut argumentit.
     */ 
    public static void main(String[] args) {
        Debuggaus db=new Debuggaus(args);
       
    }
    /** Metodi, jota voidaan luoda palauttamaan int helposti. */
    public void getInt(){
        try {
            in.readLine();
        } catch (IOException e) {;}
    }
    /** Palauttaa kuvan korkeuden. VectorizableImage- interfacesta.*/
    public int getHeight() {
        return height;
    }
    /** Palauttaa kuvan pikselin paikasta (x,y). VectorizableImage- interfacesta. */
    public int getPixel(int x, int y) {
        if ((x-width/2)*(x-width/2)+(y-height/2)*(x-height/2)>width*width/4) return 0;
        //if ((x/2-width/4)<y) return 0;
        if (x<2&&y<2) return 0;
        if (x==2&&y==2) return 0;
        return 1;
    }
    /** Palauttaa kuvan leveyden. VectorizableImage- interfacesta.*/
    public int getWidth() {
        return width;
    }
    
    public int getStartLocationColor() {
        return 2;
    }
    
}
