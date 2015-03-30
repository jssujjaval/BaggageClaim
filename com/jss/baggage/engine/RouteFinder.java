package com.jss.baggage.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.jss.baggage.model.Baggage;
import com.jss.baggage.model.Departures;
import com.jss.baggage.model.Edge;
import com.jss.baggage.model.Graph;
import com.jss.baggage.model.Node;

public class RouteFinder {

	private static final String SPACE = " ";
	private static final String EMPTY_STR = "";
	private static final String COLON = ": ";
	private static final String ERROR_MSG = "Error while finding an optimal route. Destination node may not be reachable.";
	private static final String ARRIVAL_ROUTE = "ARRIVAL BaggageClaim  NA NA";

	private static Map<String, Node> nodes;
	private static Map<String, Departures> departures;

	private static List<Edge> edges;
	private static List<Baggage> baggages;

	public static void main(String[] arg) {
		nodes = new HashMap<String, Node>();
		edges = new ArrayList<Edge>();

		departures = new HashMap<String, Departures>();
		baggages = new ArrayList<Baggage>();

		System.out.println("Please Provide inputs :");
		Scanner scan = new Scanner(System.in);
		String line;

		while ((line = scan.nextLine()) != null) {
			
			if (line.equals(EMPTY_STR)) {
				break;
			}
			if (line.equals(SECTIONS.CONVEYOR.getStatusCode())) {
				SECTIONS.setStatusCode(SECTIONS.CONVEYOR.getStatusCode());
				continue;
			} else if (line.equals(SECTIONS.DEPATURES.getStatusCode())) {
				SECTIONS.setStatusCode(SECTIONS.DEPATURES.getStatusCode());
				setDepartures(ARRIVAL_ROUTE);
				continue;
			} else if (line.equals(SECTIONS.BAGS.getStatusCode())) {
				SECTIONS.setStatusCode(SECTIONS.BAGS.getStatusCode());
				continue;
			}

			if (SECTIONS.getLastStatusCode().equals(SECTIONS.CONVEYOR.getStatusCode())) {
				addConveyor(line);
			} else if (SECTIONS.getLastStatusCode().equals(SECTIONS.DEPATURES.getStatusCode())) {
				setDepartures(line);
			} else if (SECTIONS.getLastStatusCode().equals(SECTIONS.BAGS.getStatusCode())) {
				addBaggage(line);
			}

		}

		if(validate()){
			findRoute();
		}

	}

	private static boolean validate(){
		boolean result = true;
		if(baggages.isEmpty()){
			System.out.println("Error : Bags are not available.");
			result = false;
		}
		if(departures.isEmpty()){
			System.out.println("Error : Departure information is not available.");
			result = false;
		}
		if(nodes.isEmpty()){
			System.out.println("Error : Conveyor System is not available.");
			result = false;
		}
		return result;
	}
	
	private static void findRoute() {
		Graph graph = new Graph(new ArrayList<Node>(nodes.values()), edges);
		RouteOptimizer routeOptimizer = new RouteOptimizer(graph);

		StringBuffer output;
		for (Baggage b : baggages) {
			output = new StringBuffer();
			output.append(b).append(SPACE);

			try {
				Node source = getVertex(b.getEnetryPoint());
				Departures d = (departures.get(b.getFlightId()));
				Node dest = getVertex(d.getFlightGate());
				routeOptimizer.optimize(source);
				LinkedList<Node> path = routeOptimizer.getPath(dest);
				for (Node vertex : path) {
					output.append(vertex).append(SPACE);
				}
				output.append(COLON).append(routeOptimizer.getShortestDistance(dest));

			} catch (Exception e) {
				output.append(COLON).append(ERROR_MSG);
			}
			System.out.println(output);

		}	
		
	}

	private static void addConveyor(String line) {
		try{
			String[] params = line.split(SPACE);
			Node source = getVertex(params[0]);
			Node dest = getVertex(params[1]);
			int duration = Integer.parseInt(params[2]);
	
			edges.add(new Edge(source, dest, duration));
			// set bi-direction
			edges.add(new Edge(dest, source, duration));
		}catch(Exception e){
			System.out.println("Invalid Conveyour : " + line);
		}
	}

	private static Node getVertex(String key) {
		Node v = nodes.get(key);
		if (v == null) {
			v = new Node(key);
			nodes.put(key, v);
		}
		return v;
	}

	private static void addBaggage(String line) {
		try{
			String args[] = line.split(SPACE);
			baggages.add(new Baggage(args[0], args[1], args[2]));
		}catch(Exception e){
			System.out.println("Invalid Baggage : " + line);
		}
	}

	private static void setDepartures(String line) {
		try{
			String args[] = line.split(SPACE);
			departures.put(args[0], new Departures(args[0], args[1], args[2], args[3]));
		}catch(Exception e){
			System.out.println("Invalid Departure : " + line);
		}
	}
	
	public enum SECTIONS {
		CONVEYOR("# Section: Conveyor System"), DEPATURES("# Section: Departures"), BAGS("# Section: Bags");

		static String lastStatusCode;
		private String statusCode;

		private SECTIONS(String s) {
			statusCode = s;
		}

		public String getStatusCode() {
			return statusCode;
		}

		public static void setStatusCode(String statusCode) {
			lastStatusCode = statusCode;
		}

		public static String getLastStatusCode() {
			return lastStatusCode;
		}

	}
}
