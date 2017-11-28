/*
 * GameMap.java
 *
 * 
 */

package vectorizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import util.*;
import core.*;
import physics.*;
/**
 * GameMap pit‰‰ sis‰ll‰‰n tiedot ImageVector:den
 * ja partikkeleiden paikoista ja karsii hieman ImageVectoreiden m‰‰r‰‰,
 * joilla tarvitsee suorittaa tˆrm‰ystestej‰.
 * 
 */
public class GameMap {
    private HashSet imageVectors;
    private HashSet wallCollisions;
    private int totalWidth, totalHeigth,areaWidth,areaHeigth;
    private Area[][] areas;
    static final int N=10;
    int startLocationRandomizer;
    GameData gm;
    Vector[] startLocations;
    /** 
     * Luo GameMap- olion, ja liitt‰‰ omille paikoilleen valmiiksi lasketut ImageVectorit 
     * ja partikkeleiden paikat.
     */
    public GameMap(Collection imageVectorsParam, int width1, int heigth1, GameData gmParam) {
        gm=gmParam;
        startLocationRandomizer=0;
        startLocations=gm.getMapImage().getStartLocations();
        totalWidth=width1;
        totalHeigth=heigth1;
        imageVectors=new HashSet(imageVectorsParam);
        wallCollisions=new HashSet();
        areaWidth=(int)(totalWidth/N)+1;
        areaHeigth=(int)(totalHeigth/N)+1;
        int x,y;
        areas=new Area[N][N];
        for (x=0;x<N;x++)
            for (y=0;y<N;y++) areas[x][y]=new Area(areaWidth, areaHeigth, (new Vector(x*areaWidth,y*areaHeigth)));
        Iterator i=imageVectorsParam.iterator();
        while (i.hasNext()) {
            putImageVector((ImageVector)i.next());
        }
    }
    public int getWidth(){
        return totalWidth;
    }
    
    public int getHeigth(){
        return totalHeigth;
    }
    /** Tyhjent‰‰ kaikki alueiden wallCollisions-HashSet:it ja laittaa annetut WallCollision:it
     * oikeisiin Area:iin.
     */
    public void putWallCollisions(WallCollision[] wallCollisionsParam){
        refreshWallCollisions();
        int i=0,length=wallCollisionsParam.length;
        if (length==0) return;
        for(;i<length;i++)
            putWallCollision(wallCollisionsParam[i]);
    }
    /**
     * Laittaa WallCollisionin oikealle paikalleen.
     */
    public void putWallCollision(WallCollision wc){ 
        Point p1=getArea(wc.getParticle().getPosition());
        int x=p1.getX(),y=p1.getY();
        if (x<0||x>=N||y<0||y>=N){
            System.out.println(" GAMEMAP: yksi lensi yli k‰enpes‰n. ");
            Vector[] vl=gm.getMapImage().getStartLocations();
            if (vl.length==0)
                wc.getParticle().setPosition(new Vector(totalWidth/2,totalHeigth/2));
            else wc.getParticle().setPosition(vl[vl.length]);
            p1=getArea(wc.getParticle().getPosition());
        }
        areas[x][y].putWallCollision(wc);
    }
    
    /** Palauttaa jonkun aloituspaikan... */
    public Vector getRandomStartLocation(){
        if (startLocations.length==0)
            return new Vector(totalWidth/2,totalHeigth/2);
        startLocationRandomizer++;
        if (startLocations.length==startLocationRandomizer)
            startLocationRandomizer=0;
        return startLocations[startLocationRandomizer];        
    }
    
    public void putParticles(Particle[] particlesParam){
        refreshParticles();
        int i=0,length=particlesParam.length;
        if (length==0) return;
        for(;i<length;i++)
            putParticle(particlesParam[i]);
    }
    public void putParticle(Particle p){
        Point p1=getArea(p.getPosition());
        int x=p1.getX(),y=p1.getY();
        if (x<0) x=0;
        else if (x>=totalWidth) x=N-1;
        if (y<0) y=0;
        else if (y>=totalHeigth) y=N-1;
        areas[x][y].putParticle(p);
    }

    
    /** Poistaa WallCollisionit ja Particles:nit Area:ista. */
    public void refresh(){
        int x,y;
        for (x=0;x<N;x++)
            for (y=0;y<N;y++)
                areas[x][y].refresh();
    }
    public void refreshWallCollisions(){
        int x,y;
        for (x=0;x<N;x++)
            for (y=0;y<N;y++)
                areas[x][y].refreshWallCollisions();
    }
    public void refreshParticles(){
        int x,y;
        for (x=0;x<N;x++)
            for (y=0;y<N;y++)
                areas[x][y].refreshParticles();
    }
    /**
     * Laittaa ImageVector iv:n kaikkiin Area -olioihin, joiden l‰pi t‰m‰ kulkee.
     * Aloittaa alkupisteen sis‰lt‰v‰st‰ ruudusta (p1), ja etenee kohti loppupistett‰ (p2).
     * Muuttujien selityksi‰: xsum=mit‰ lis‰t‰‰n x-koordinaattiin.
     * ysum=mit‰ lis‰t‰‰n y-koordinaattiin.
     * K‰y l‰pi j‰rjestyksess‰ onko kyseess‰ tapaus, jossa 1) pysyt‰‰n samassa ruudussa, 
     * 2) samalla rivill‰ 3) samalla sarakkeella, ja jos tapaus ei ole mik‰‰n n‰ist‰, alkaa
     * etsi‰ kohtia, joissa iv ylitt‰‰ x-akselin suuntaisen Area-olioiden vastuualuerajan.
     * N‰iss‰ kohdissa ei lis‰t‰k‰‰n y:hyn ysum:aa vaan x:‰‰n xsum:ma, ja lasketaan seuraava
     * rajanylityspaikan.
     * @param iv ImageVector, joka pit‰‰ laittaa karttaan.
     */
    public void putImageVector(ImageVector iv){
        Point p1,p2;
        int x=0,y=0,stop,i;
        int xsum,ysum;
        p1=getArea(iv.getStartPosition());
        p2=getArea(iv.getStartPosition().add(iv.getVector()));
        if (p2.getY()<p1.getY()) ysum=-1; else ysum=1;
        if (p2.getX()<p1.getX()) xsum=-1; else xsum=1;
        if (p1.getX()==p2.getX())
            //pysyt‰‰nkˆ samassa ruudussa?
            if (p1.getY()==p2.getY()){
                areas[p1.getX()][p1.getY()].putImageVector(iv);
            }
            //pysyt‰‰n samalla rivill‰.
            else {
                for(y=p1.getY();y!=p2.getY();y+=ysum) {
                    areas[p1.getX()][y].putImageVector(iv);
                }
                areas[p2.getX()][p2.getY()].putImageVector(iv);
            }
        else {
            //pysyt‰‰nkˆ samassa sarakkeessa?
            if (p1.getY()==p2.getY()) {
                for(x=p1.getX();x!=p2.getX();x+=xsum) {
                    areas[x][p1.getY()].putImageVector(iv);
                }
                areas[p2.getX()][p2.getY()].putImageVector(iv);
            }
            // Ei mik‰‰n edellisist‰.
            else {
                x=p1.getX();
                y=p1.getY();
                i=p1.getY();
                if (ysum>0) i+=1;
                stop=getIndexOfIntersection(iv,i);
                while (y!=p2.getY()||x!=p2.getX()){
                    areas[x][y].putImageVector(iv);
                    if (x==stop) {
                        y+=ysum;
                        i+=ysum;
                        stop=getIndexOfIntersection(iv,i);
                    }
                    else x+=xsum;
                }
                areas[p2.getX()][p2.getY()].putImageVector(iv);
            }
        }
    }
        
    /**
     * Palauttaa sen area:n x-indeksin, jossa i:nnes alueiden v‰linen raja leikkaa 
     * iv-ImageVector:ia. Katsotaan siis suoraa y=i*areaHeigth,
     * ja halutaan tiet‰‰, miss‰ kohden iv leikkaa sen.
     * @param iv ImageVector
     * @param i Monesko alueiden raja on kyseess‰.
     */
    private int getIndexOfIntersection(ImageVector iv, int i){
        Vector v=iv.getVector(),p=iv.getStartPosition();
        float temp=p.getX() + (i*areaHeigth-p.getY())*v.getX()/v.getY();
        return (int) temp/areaWidth;
    }
    
    /** 
     * Palauttaa vektorin osoittaman pisteen ruudun indeksit Point-luokan avulla. 
     * K‰ytetty tietoa (int) == "floor"
     * @param p paikkavektori, jonka Area:n indeksit halutaan tiet‰‰.
     */
    private Point getArea(Vector p) {
        return new Point((int)(p.getX()/areaWidth),(int)(p.getY()/areaHeigth));
    }
    
    /**
     * Palauttaa kaikki ImageVector:it suorakulmiosta, jonka keskikohta on pisteess‰
     * (xp,yp) ja sivut ovat (halfLength,halfHeight) p‰‰ss‰ t‰st‰.
     *@param xp Suorakulmion keskipisteen x-koordinaatti.
     *@param yp Suorakulmion keskipisteen y-koordinaatti.
     *@param halfLength Suorakulmion leveyden puolikas.
     *@param halfHeight Suorakulmion korkeuden puolikas.
     */

    public HashSet getImageVectors(int xp, int yp, int halfLength, int halfHeight){
        int xmin=(int)(xp-halfLength)/areaWidth,xmax=(int)(xp+halfHeight)/areaWidth,
            ymin=(int)(yp-halfHeight)/areaHeigth,ymax=(int)(yp+halfHeight)/areaHeigth;
        int x,y;
        if (ymax>=N) ymax=N-1;
        if (xmax>=N) xmax=N-1;
        if (ymin<0) ymin=0;
        if (xmin<0) xmin=0;
        HashSet iVectors=new HashSet();
        for(x=xmin;x<=xmax;x++)
            for(y=ymin;y<=ymax;y++){
                iVectors.addAll(areas[x][y].getImageVectors());
            }
        return iVectors;
    }

    /**
     * Palauttaa kaikki partikkelit pallon sis‰lt‰, jonka keskipiste on (xp,yp)
     * ja s‰de radius.
     * @param xp Hakupallon keskipisteen x-koordinaatti.
     * @param yp Hakupallon keskipisteen y-koordinaatti.
     * @param radius Hakupallon s‰de.
     *
     */
    public HashSet getwallCollisions(int xp, int yp, float radius){
        int xmin=(int)(xp-radius)/areaHeigth,xmax=(int)(yp-radius)/areaWidth,
            ymin=(int)(radius+xp)/areaHeigth,ymax=(int)(radius+yp)/areaWidth;
        if (ymax>=N) ymax=N-1;
        if (xmax>=N) xmax=N-1;
        if (xmin<0) xmin=0;
        if (ymin<0) ymin=0;
        int x,y;
        HashSet wCollisions=new HashSet();
        for(x=xmin;x<=xmax;x++)
            for(y=ymin;y<=ymax;y++)
                wCollisions.addAll(areas[x][y].getWallCollisions());
        return wCollisions;
    }
    
    public HashSet getParticles(int xp, int yp, int radius){
        int xmin=(int)(xp-radius)/areaHeigth,xmax=(int)(yp-radius)/areaWidth,
            ymin=(int)(radius+xp)/areaHeigth,ymax=(int)(radius+yp)/areaWidth;
        if (ymax>=N) ymax=N-1;
        if (xmax>=N) xmax=N-1;
        if (xmin<0) xmin=0;
        if (ymin<0) ymin=0;
        int x,y;
        HashSet pticles=new HashSet();
        for(x=xmin;x<=xmax;x++)
            for(y=ymin;y<=ymax;y++)
                pticles.addAll(areas[x][y].getParticles());
        return pticles;
    }
}
