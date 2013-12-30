package edu.bonn.cs.iv.bonnmotion.models.randomstay;

import java.util.*;

import osm2wkt.Osm2Wkt.Landmark;
import edu.bonn.cs.iv.bonnmotion.Position;
public class RandomStayPoint {
    public static int NUM_STAY_POINT = 100;
    public static double  MAX_STAY_TIME = 10;
    public static double STAY_PROB = 0.8;
    public static double STAY_REGION_SQUARE = 25;
	public double x;
    public double y;
    public Position[] stayPos = new Position[NUM_STAY_POINT];
    public double[] stayTime = new double[NUM_STAY_POINT];
    WKT2GraphMap graph = null;
    public RandomStayPoint() {
	  
    }
    
    public void init_vertics(WKT2GraphMap graph) {
      this.graph = graph;
  	  Random rv = new Random();
  	  Random rv_t = new Random();
  	  for (int i = 0;i<NUM_STAY_POINT;i++) {
  		  Object[] lm = graph.landmark.keySet().toArray();
  		  int node = (int)rv.nextDouble()*lm.length;
  		  Landmark l=graph.landmark.get((Long)lm[node]);
  		  stayPos[i] = new Position(l.x, l.y);
  		  stayTime[i] = rv_t.nextDouble() * MAX_STAY_TIME;
  	  }   	
    }
    public void init_pos(double x, double y) {
	  this.x = x;
	  this.y = y;
	  Random rv = new Random();
	  Random rv_t = new Random();
	  for (int i = 0;i<NUM_STAY_POINT;i++) {
		  stayPos[i] = new Position(rv.nextDouble()*this.x,rv.nextDouble()*this.y);
		  stayTime[i] = rv_t.nextDouble() * MAX_STAY_TIME;
	  }
    } 
    public double getStayTime(Position loc) {
    	double ret = 0;
    	boolean stay = false;
    	int i = 0;
    	Random rv_t = new Random();
    	while (i<NUM_STAY_POINT && !stay) {
    		if (((Math.pow((loc.x-stayPos[i].x), 2)) + (Math.pow((loc.y-stayPos[i].y), 2))) 
    				<= STAY_REGION_SQUARE) {
    			if (rv_t.nextDouble() <= STAY_PROB) {
    				stay = true;
    				ret = stayTime[i];
    			}
    		}
    		i++;
    	}
    	return ret;
    }
  
}
