

package vectorizer;
import java.util.ArrayList;
import java.util.Iterator;
import util.*;
import core.*;
/**
 * 
 * Olio, joka seuraa kahden eri alueen v�list� rajaa.
 * luomisoperaatio PlaneIterator: laittaa iterVector:in palauttamia Vektoreita listaan.
 * ->iterVector: kutsuu moveToNextCross:ia toistuvasti ja yll�pit�� vektoria,
 * jonka katkaisee sopivassa kohdassa.
 * ->moveToNextCross: muuttaa iterpos:ia ja vector:ia niin, ett� uusi iterpos ilmaisee seuraavaa 
 * rajan risteyst�. T�ss� kutsutaan Vectorizerin getPixelZonea, joka merkkaa pisteet k�ydyksi.
 * ->searchDots: Muuttaa parametreina annettuja MutableVectoreita siten, ett�
 * ne ilmaisevat edess�olevien pikseleiden paikkoja.
 * 
 * K�ytet��n luomalla olio ja pyyt�m�ll� vektoreita getImageVectors()-metodilla.
 *
 */
public class PlaneIterator {
	int zoneright,zoneleft;
	Vector start, iterpos, planestart;
	Vector vector;
	ArrayList vectorList;
	int height,width,epsilon;
        Vectorizer vz;
        boolean dead;
       /**
        * Luo uuden PlaneIteratorin, alustaa, ja laskee vektorisaation.
        */
	public PlaneIterator(Vectorizer vzparam, Vector stopHere,ImageVector s, int maxerror) {
		vectorList=new ArrayList();
                start=new Vector(s.start);
		planestart=new Vector(stopHere);
                iterpos=new Vector(s.start);
		vector=new Vector(s.vector);
		zoneleft=s.getZoneLeft();
		zoneright=s.getZoneRight();
		epsilon=maxerror;
                vz=vzparam;
                dead=false;
                
                int k=0;
                //System.out.println("PlaneIterator... "+start.getX()+" "+start.getY()+" ja "+planestart.getX()+" "+planestart.getY());
                while (!dead) {
                    //System.out.println("Taas uusi...");
                    Vector v=iterVector();
                    k++;
                    if (k>10000) {
                        dead=true;
                       /** int k1=0;
                        java.util.Iterator k12=vectorList.iterator();
                        for(;k1<10;k1++) ((ImageVector)k12.next()).getVector().show();
                        v.show();
                        iterpos.show();
                        s.start.show();*/
                        System.out.println("Problem"+planestart.getX()+" "+planestart.getY());
                    }
                    if (v.squareLength()!=0) {
                        vectorList.add(new ImageVector(start, v,zoneleft, zoneright));
                        start=start.add(v);
                    if (iterpos.sub(start).squareLength()!=0) System.out.println("ITERPOS!=START"); 
                    }
                }
	}

        /** Palauttaa PlaneIteratorin luomisessa lasketut vektorit. */
	public ArrayList getImageVectors(){
		return vectorList;
	}

	
	/** 
	 * Laittaa parametreina annetut MutableVector:it ilmaisemaan kahden edess�
         * olevan pikselin paikkaa.
	 */
	private void searchDots(MutableVector left, MutableVector right){
		if (vector.isN()) {
			left.setAdd(iterpos).setAdd(Vector.NW);
			right.setAdd(iterpos).setAdd(Vector.N);
		} else if (vector.isS()) {
			left.setAdd(iterpos);
			right.setAdd(iterpos).setAdd(Vector.W);
		} else if (vector.isE()) {
			left.setAdd(iterpos).setAdd(Vector.W);
			right.setAdd(iterpos).setAdd(Vector.NW);
		} else {
			left.setAdd(iterpos).setAdd(Vector.N);
			right.setAdd(iterpos);
		}
	}
        
/** palauttaa -3,-2,-1:n, kun menee vasemmalle,suoraan,oikealle, 
 *  13 >=, kun toisenlaisia
 *  rajoja l�ydetty eli iteraatio loppuu.
 *  13-14=iteraatio loppuu, 1 niin error!!
 *  Muuttaa vectorin uudeksi Vectoriksi, lis�� iterpos:ia Vectorilla.
 *  Etsit��n ensin kahdesta edess� olevasta ruudusta.
 *  Menn��n eteenp�in, jos vasen on zoneleft:n v�rinen ja oikea zoneright:n.
 *  K��nnyt��n, jos kummatkin on toista n�ist� v�reist�.
 *  Jos toisenlaisia rajoja l�ydetty, aloittaa tiettyjen s��nt�jen mukaan
 *  uudet iteraattorit. J�lkimm�inen ei aivan viel� toimi.
 */
	private int moveToNextCross(){
		MutableVector right=new MutableVector((float) 0, (float) 0);
		MutableVector left=new MutableVector((float) 0, (float) 0);
                int r,l;
		searchDots(left,right);
                l=vz.getPixelZone((int)left.getX(), (int)left.getY());
		r=vz.getPixelZone((int)right.getX(), (int)right.getY());
                if (zoneleft!=l){
                    if (zoneright==r&&zoneright==l) {
                            vector=vector.turnLeft();
                            iterpos=iterpos.add(vector);
                            return -3;
                    }
                    else ;
                }
                else if (zoneright==r){
                        vector=new Vector(vector);
                        iterpos=iterpos.add(vector);
                        return -2;
                }
                else if (zoneleft==r){
                        vector=vector.turnRight();
                        iterpos=iterpos.add(vector);
                        return -1;
                }
                //muunlaisia rajoja on havaittu...
                if (zoneleft<=l) {
                    if (zoneleft<r) {
                        PlaneIterator p;
                        if (zoneleft<l) {
                            p=new PlaneIterator
                                (vz, planestart,
                                     new ImageVector
                                        (new Vector(iterpos),
                                        vector.turnLeft(),zoneleft,l),
                                epsilon);
                            vectorList.addAll(p.getImageVectors());
                        }
                        if (l<r) {        
                            p=new PlaneIterator
                                (vz, planestart, 
                                     new ImageVector
                                    (new Vector(iterpos),
                                    new Vector(vector),l,r),
                                epsilon);
                            vectorList.addAll(p.getImageVectors());
                        }
                        if (r<zoneright) {
                            p=new PlaneIterator
                                (vz, planestart,
                                    new ImageVector
                                    (new Vector(iterpos),
                                    new Vector(vector.turnRight()),r,zoneright),
                                epsilon);
                            vectorList.addAll(p.getImageVectors());                        
                        }
                        return 14;
                    }
                    else if (zoneleft==r){
                        PlaneIterator p=new PlaneIterator
                            (vz, planestart,
                                new ImageVector(new Vector(iterpos),
                                                vector.turnRight(),r,zoneright), 
                            epsilon);
                        vectorList.addAll(p.getImageVectors());
                        return 13;
                    }
                    else return 14;
                }
                else return 14;
	}

        /** Iteroi ja palauttaa uuden pintaa my�ten etenev�n vektorin.
         *  vnow on palautettava vektori.
         *  vr, vl, right ja left auttavat tarkistamaan, onko pinta tarpeeksi l�hell�
         *  syntyv�� vektoria. Temp:iin tallennetaan v�liaikaisesti mahdollisesti uusi
         *  right tai left.
         */
	private Vector iterVector(){
            MutableVector vnow=new MutableVector(0,0);
            Vector vr=new Vector(0,0), vl=new Vector(0,0),
                   right=new Vector(0,0), left=new Vector(0,0),temp;
            float iterationEpsilon=epsilon,length=0,prevLength=0;
            int stop=0,prevStop=0;
            int i=0,sameWay=0,allWay=0;
            while(stop<=0){
                stop=moveToNextCross();
                prevStop=stop;
                /**if (planestart.getX()==81&&planestart.getY()==1367)
                    System.out.println("vector: "+vector.getX()+" "+vector.getY());*/
                if (allWay==0){
                    vr=vector.turnLeft().scalarProduct(iterationEpsilon/2);
                    vl=vector.turnRight().scalarProduct(iterationEpsilon/2);
                }
                /**
                 * Lopetetaan, jos loppu tullut tai p��sty alkuun takaisin. 
                 * T�ss� vaiheessa ei vnow:hon lis�t� viel� vectoria, 
                 * sill� sit� ei v�ltt�m�tt� ole lis�tty iterpos:iin moveToNextCross:ssa.
                 * stop>=13 kun n�in on k�ynyt.
                 */
                if (iterpos.sub(planestart).squareLength()==0||stop>=13){
                    if (stop<13) vnow.setAdd(vector);
                    dead=true;
                    return new Vector(vnow);
                }
                vnow.setAdd(vector);
                if (stop<=0) {
                    /** Jos on menty suoraan, kannattaa vektori p�tk�ist� niin,
                     *  ett� se seuraa suoraa pitkin. T�m� if-else if-else-h�ss�kk� hoitaa
                     *  sen.
                     */
                    if (stop==-2) {
                        sameWay++;                        
                        //jos menty yli epsilon*2 verran samaan suuntaan 
                        //ja ei olla aina oltu suoralla.
                        if (sameWay>epsilon*2&&allWay>sameWay) {
                            //System.out.println("Katkaistaan suoruuden takia...");
                            iterpos=iterpos.sub(vector.scalarProduct(sameWay));
                            return vnow.sub(vector.scalarProduct(sameWay));
                        }
                    }
                    
                    //katkaistaan, jos menty pitk��n suoraan, ja onkin k��nnytty...
                    //kelataan ensin hiukan alkuun...
                    else if (allWay==sameWay&&sameWay>epsilon*4) {
                        iterpos=iterpos.sub(vector);
                        return vnow.sub(vector);            
                    }
                    else {
                        sameWay=1;
                    }
                    allWay++;
                    /** Jos ollaan tultu takaisin, p�tk�ist��n. */
                    prevLength=length;
                    length=vnow.squareLength();
                    if (prevLength>length) stop=12;
                    /** Jos on ollut suhteellisen suoraa koko ajan, niin voidaan tarkentaa... */
                    if (allWay==(int)10*epsilon) {
                        left=left.sub(vl);
                        right=right.sub(vr);
                        iterationEpsilon=epsilon/2;
                        vr=vr.scalarProduct(1/2);
                        vl=vl.scalarProduct(1/2);
                        left=left.add(vl);
                        right=right.add(vr);
                    }
                    /** Jos vnow ei en�� seuraa tarpeeksi tarkasti sein��, kannattaa
                     * vektori p�tk�ist�. T�m� hoitaa sen. */
                    temp=vnow.add(vl);
                    if (temp.vCrossProduct(left)>=0) {
                        left=temp;
                        if (left.vCrossProduct(right)>0)
                            stop=12;
                    }
                    temp=vnow.add(vr);
                    if (temp.vCrossProduct(right)<=0) {
                        right=temp;
                        if (left.vCrossProduct(right)>0)
                            stop=12;
                    }
                    //if (allWay<epsilon*2&&stop==12) stop=0;
                }
            }
            if (sameWay>0&&prevStop==-2) {
                //System.out.println("Otetaan takapakkia...");
                iterpos=iterpos.sub(vector.scalarProduct(sameWay));
                return vnow.sub(vector.scalarProduct(sameWay));
            }
            //System.out.println("Katkaistiin normaalisti...");
            return new Vector(vnow);
	}
}