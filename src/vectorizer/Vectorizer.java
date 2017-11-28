package vectorizer;
import java.util.ArrayList;
import util.*;
/**
 * Luokka vektorisoinnin tekemiseen.
 */

public class Vectorizer {
	VectorizableImage vi;
	VectorizationParameters vp;
	boolean[][] haveBeen;
        final int width, height;
        ArrayList imageVectors;
        int[] zoneColors;
        int startPlaceDetected;
        ArrayList startLocations;
        int startLocationColor;
        /**
         * Luo uuden Vectorizerin ja laskee vektorisoinnin.
	 * Palauttaa annetun kartan vektoriesityksen.
	 * Jos kaksi vektorisoitavaa aluetta, sivuavat toisiaan,
	 * on rajalla molempien alueiden vektorit.
         * K�y l�pi kaikki pikselit kahteen kertaan.
         * Jos l�yt�� pinnan, jolla ei olla k�yty (tarkistaa sen haveBeen- taulukosta),
         * kutsuu PlaneIteratorin t�ihin.
	 * @param smoothness Kuinka sile� vektorisointi tehd��n.
	 * Esim. 1 tuottaa melkein ter�v�n pikselireunoja seuraavan
	 * vektorisoinnin.
         * @param vimage Vectorizable Image -interfacen t�ytt�v� olio.
         * @param vparam VectorizationParameters -interfacen t�ytt�v� olio.
	 */
	public Vectorizer(
			VectorizationParameters vparam,
			VectorizableImage vimage,
			int smoothness) 
	{
                startLocationColor=vparam.getStartLocationColor();
                startPlaceDetected=-1;
                startLocations=new ArrayList();
                Vector direction=new Vector(0,-1);
                imageVectors=new ArrayList();
                PlaneIterator planeiterator;
		this.vi=vimage;
		this.vp=vparam;
                zoneColors=vp.getZoneColors();
                width=vi.getWidth();
                height=vi.getHeight();
                int type1=0,type2=0,x=0,y=0;
                haveBeen=new boolean[width][height];
                for(x=0;x<width;x++)
                    for(y=0;y<height;y++){
                        haveBeen[x][y]=false;
                    }
                x=0;
                y=0;
                while(x<width-1) {
                    while(y<height){
                        startPlaceDetected=0;
                        type1=getZoneWithoutMarking(x,y);
                        type2=getZoneWithoutMarking(x+1,y);
                        if (startPlaceDetected!=0){
                            if (startLocations.size()<30)
                                startLocations.add(new Vector(x,y));
                            
                            type1=type2;
                        }
                        else startPlaceDetected=-1;
                        /** T�ss� voidaan tarkastaa, onko type2 erityinen pallon aloituspaikka
                         * ja kutsua koordinaateilla jotain metodia, joka tallentaa paikan
                         * mik�li n�in on.
                         * T�m�n j�lkeen on hyv� sijoittaa type2=type1,
                         * ettei pikseli� pidet� sein� tms. pikselin�.
                         */
                        if(type1!=type2 && !(haveBeen[x][y]&&haveBeen[x+1][y])){
                            if (type1>type2) {
                                direction=new Vector(0,1);
                                int temp=type1;
                                type1=type2;
                                type2=temp;
                            }
                            else direction=new Vector(0,-1);
                            planeiterator=new PlaneIterator
                                (this,
                                new Vector(x+1,y),
                                new ImageVector(new Vector(x+1,y),
                                                direction,
                                                type1,type2),
                                smoothness);
                            imageVectors.addAll(planeiterator.getImageVectors());
                        }
                        y++;
                    }
                    type1=0;
                    type2=0;
                    y=1;
                    x++;
                }
        }
        
        /**
         * 
         */
        public ArrayList getStartLocations(){
            return startLocations;
        }
        /** Palauttaa vektorisoidun pinnan, joka koostuu ImageVectoreista.
         */
        public ArrayList getImageVectors(){
            return imageVectors;
        }
	/** 
         * Merkkaa pikselin (x,y) k�ydyksi ja palauttaa sen tyypin numeron.
         * Saa k�ytt�� vain PlaneIteratorissa.
         */
	int getPixelZone(int x, int y){
                if (x<width&&y<height&&x>=0&&y>=0) haveBeen[x][y]=true;
                return getZoneWithoutMarking(x,y);
	}
        /** Palauttaa pikselin (x,y):n tyypin int:in. */
        int getZoneWithoutMarking(int x, int y){
		if (x>=width-1||y>=height-1||x<=0||y<=0) return 0;
                int i=0,color=vi.getPixel(x,y);
                
                if (startLocationColor==color){
                    if (startPlaceDetected==0)
                        startPlaceDetected=color;
                    i=x+1;
                    while (vi.getPixel(i,y)==color&&i<width)
                        i++;
                    if (i<width) //ep�kelpo starttipaikka...
                        color=vi.getPixel(i,y);
                    else return 0;
                }
                i=0;
                while(zoneColors.length>i){
                    if (zoneColors[i]==color)
                        return i;
                    i++;
                }
                return 0;
        }
}

