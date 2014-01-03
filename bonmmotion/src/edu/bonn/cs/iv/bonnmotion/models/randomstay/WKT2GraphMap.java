package edu.bonn.cs.iv.bonnmotion.models.randomstay;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import osm2wkt.Osm2Wkt;
import osm2wkt.Osm2Wkt.Landmark;
import edu.bonn.cs.iv.bonnmotion.BoundingBox;
import edu.bonn.cs.iv.bonnmotion.HttpMapRequest;
import edu.bonn.cs.iv.bonnmotion.Position;
import edu.bonn.cs.iv.bonnmotion.HttpMapRequest.ORSStartPositionFailedException;
import edu.bonn.cs.iv.bonnmotion.Scenario;
import edu.bonn.cs.iv.bonnmotion.models.RandomStreet;
import edu.bonn.cs.iv.graph.Edge;

/**
 * Try to randomly get a vertex(id) and walk to another 
 * @author ktchuang
 *
 */
public class WKT2GraphMap {
    public static String WKT_MAP_FILE_NAME = "";
    public static double MAP_MAX_SPEED;
    public static double MAP_MIN_SPEED;
    public WeightedPseudograph<Long, DefaultWeightedEdge> graph = null;
    public HashMap<Long,Landmark> landmark;
    public BoundingBox mapBBox = null;
	public WKT2GraphMap(Scenario scenario) {
		Osm2Wkt obj = new Osm2Wkt();
		
		if(!obj.readWkt(WKT_MAP_FILE_NAME)) 						return;
		if(!obj.fixCompleteness()) 					return;
		if(!obj.simplifyModel(true)) 				return;
		if(!obj.PreparingWeightedGraph()) 	        return;
		if(!obj.removeBogusEdges())					return;
		// fixCompleteness
		if(!obj.fixCompleteness())  				return;
		// simplifyModel
		if(!obj.simplifyModel(true))				return;		
		graph = obj.getWeightedGraph();
		RandomStreet.graph = this;
		landmark = obj.getLandmarks();
		
		double minX=Double.MAX_VALUE,minY=Double.MAX_VALUE;
		double maxX=Double.MIN_VALUE,maxY=Double.MIN_VALUE;
		for (Long v : landmark.keySet()) {
			Landmark m = landmark.get(v);
			if(m.x<minX) minX=m.x;
			if(m.y<minY) minY=m.y;
			if(m.x>maxX) maxX=m.x;
			if(m.y>maxY) maxY=m.y;			
        }
		mapBBox = new BoundingBox(minX,minY,maxX,maxY);
		scenario.transform_x = minX;
		scenario.transform_y = minY;
		scenario.max_trace_x = maxX;
		scenario.max_trace_y = maxY;
		
		System.out.println(WKT_MAP_FILE_NAME + " map: bbox's height="+(maxY-minY)+", width="+(maxX-minX));
	}
	public long getVertexCount() {
		return landmark.size();
	}
	public double getVertexX(Long id) {
		return ((Landmark)landmark.get(id)).x;		
	}
	public double getVertexY(Long id) {
		return ((Landmark)landmark.get(id)).y;			
	}
	public Long getVertexID(double x, double y) {
		double epsilon = 0.0001;
		long ret = -1;
		for (Long v : landmark.keySet()) {
			Landmark m = landmark.get(v);
			if(Math.abs(m.x - x) < epsilon && Math.abs(m.y - y) < epsilon){
				ret = v.longValue();
				break;
			}
        }
		if (ret == -1) {
			System.exit(0);
		}
		return ret;
	}

   /**
   *
   * @param src
   * @param dst
   * @param epsgCode
   * @param pref
   * @param ors_url
   * @return
   * @throws ORSStartPositionFailedException 
   */
  public Position[] getORSRouteWaypoints(Position src,
                                                Position dst,
                                                int epsgCode,
                                                String pref,
                                                String ors_url) throws ORSStartPositionFailedException
  {
  	Long s = getVertexID(src.x,src.y);
  	Long d = getVertexID(dst.x,dst.y);

  	DijkstraShortestPath alg = new DijkstraShortestPath(graph, s, d);
  	List<Long> path = (List<Long>)Graphs.getPathVertexList(alg.getPath());
  	Position[] ret = new Position[path.size()];
  	for (int i=0;i<ret.length;i++) {
  		ret[i] = new Position(getVertexX(path.get(i)),getVertexY(path.get(i)));
  	}
      return ret;
  }

  /**
  *
  * @param src
  * @param dst
  * @param epsgCode
  * @param pref
  * @param ors_url
  * @return
  */
  public double getORSRouteDistance(Position src,
                                          Position dst,
                                          int epsgCode,
                                          String pref,
                                          String ors_url)
  {
	  	Long s = getVertexID(src.x,src.y);
	  	Long d = getVertexID(dst.x,dst.y);

	  	DijkstraShortestPath alg = new DijkstraShortestPath(graph, s, d);
	  	return alg.getPathLength();
  }

}
