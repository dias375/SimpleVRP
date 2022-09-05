// simpleRouting uses nearestNeighbor method, the number of routes is double the number of trucks,
// then routes are paired (one straight route and one reversed route are linked).
// This results in 3 routes. However, this is a "greedy" result.
// Further improvements can be made using inter and intra search or TABU.

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class VRP {
    int NUMBER_OF_TRUCKS = 3;
    Node DEPOT_NODE = new Node(0,0,0);

    public static void main(String[] args) {
        System.out.println("Starting VRP");
        VRP vrp = new VRP();

        ArrayList<Node> allNodes = testNodesArrayList();

        vrp.simpleRouting(allNodes);
        System.out.println("Ending VRP");
    }

    private void simpleRouting(ArrayList<Node> allNodes){
        ArrayList<Route> initialRoutes = shortestInitialRoutes(allNodes);
        ArrayList<Route> finalRoutes = joinRoutes(initialRoutes);
        System.out.println(finalRoutes);
        for(Route route : finalRoutes){
            System.out.println(route.getDistance());
        }
    }

    private ArrayList<Route> joinRoutes(ArrayList<Route> initialRoutes) {
        ArrayList<Route> joinedRoutes = new ArrayList<>();
        for(Route outbound : initialRoutes){
            if(outbound.isJoined()){continue;}
            ArrayList<Node> availableNodes = new ArrayList<>();
            for(Route route : initialRoutes){
                if(!route.isJoined()) {
                    availableNodes.add(route.lastNode());
                }
            }

            Node outboundLastNode = outbound.lastNode();
            Node inboundLastNode = nearestNode(outboundLastNode, availableNodes, false);
            Route inbound = findRouteContainingNode(inboundLastNode, initialRoutes);
            Route roundRoute = joinRoutes(outbound, inbound);
            joinedRoutes.add(roundRoute);
            outbound.setJoined(true);
            inbound.setJoined(true);
        }

        return joinedRoutes;
    }

    private ArrayList<Route> shortestInitialRoutes(ArrayList<Node> allNodes){
        ArrayList<Route> initialRoutes = createArrayListOfRoutes(2);
        do {
            for (Route route : initialRoutes) {
                Node nearestNode = nearestNode(route.lastNode(), allNodes, true);
                if(nearestNode.isRouted()){break;}
                nearestNode.setRouted(true);
                route.addNode(nearestNode);
            }
        } while(areThereUnroutedNodes(allNodes));
        return initialRoutes;
    }

    private ArrayList<Route> createArrayListOfRoutes(int factor){
        ArrayList<Route> routes = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_TRUCKS*factor; i++){
            Route route = new Route(String.valueOf(i));
            route.addNode(DEPOT_NODE);
            routes.add(route);
        }
        return routes;
    }

    private Route joinRoutes (Route outbound, Route inbound){
        String routeId = outbound.getRouteId() + inbound.getRouteId();
        Route joinedRoute = new Route(routeId);
        for(Node node : outbound.getRoute()){
            joinedRoute.addNode(node);
        }
        Collections.reverse(inbound.getRoute());
        for(Node node : inbound.getRoute()){
            joinedRoute.addNode(node);
        }
        return joinedRoute;
    }

    private Route findRouteContainingNode(Node node, ArrayList<Route> routes){
        for(Route route : routes){
            if(route.getRoute().contains(node)){
                return route;
            }
        }
        return null;
    }

    private boolean areThereUnroutedNodes(ArrayList<Node> allNodes){
        for(Node node : allNodes){
            if(!node.isRouted()){
                return true;
            }
        }
        return false;
    }

    private Node nearestNode(Node referenceNode, ArrayList<Node> allNodes, boolean hasToBeUnrouted){
        Node bestNode = allNodes.get(1);
        double bestDistance = Integer.MAX_VALUE;
        for(Node candidateNode : allNodes){
            if(candidateNode.getNodeId() == 0){continue;}
            if(candidateNode == referenceNode){continue;}
            if(candidateNode.isRouted() && hasToBeUnrouted){continue;}
            double candidateDistance = distanceBetweenTwoNodes(referenceNode, candidateNode);
            if(candidateDistance<bestDistance){
                bestNode = candidateNode;
                bestDistance = candidateDistance;
            }
        }
        return bestNode;
    }

    private double distanceBetweenTwoNodes(Node firstNode, Node secondNode){
        return Point2D.distance(firstNode.getX(), firstNode.getY(), secondNode.getX(), secondNode.getY());
    }

    private static ArrayList<Node> testNodesArrayList(){
        ArrayList<Node> allNodes = new ArrayList<>();
        allNodes.add(new Node(160,65,1));
        allNodes.add(new Node(65,10,2));
        allNodes.add(new Node(10,507,3));
        allNodes.add(new Node(20,66,4));
        allNodes.add(new Node(344,233,5));
        allNodes.add(new Node(20,434,6));
        allNodes.add(new Node(30,867,7));
        allNodes.add(new Node(676,30,8));
        allNodes.add(new Node(30,30,9));
        allNodes.add(new Node(43,423,10));
        allNodes.add(new Node(567,87,11));
        allNodes.add(new Node(345,567,12));
        allNodes.add(new Node(787,6,13));
        allNodes.add(new Node(645,0,14));
        allNodes.add(new Node(0,342,15));
        allNodes.add(new Node(344,43,16));
        allNodes.add(new Node(500,400,17));
        return allNodes;
    }
}

class Node {
    private int x, y, nodeId;
    private boolean isRouted = false;

    public Node(int nodeId) {
        this.nodeId = nodeId;
    }

    public Node() {
    }

    public Node(int x, int y, int nodeId) {
        this.x = x;
        this.y = y;
        this.nodeId = nodeId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isRouted() {
        return isRouted;
    }

    public void setRouted(boolean routed) {
        isRouted = routed;
    }

    @Override
    public String toString() {
        return "nodeId=" + nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (x != node.x) return false;
        if (y != node.y) return false;
        return nodeId == node.nodeId;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + nodeId;
        return result;
    }
}

class Route{
    private ArrayList<Node> route = new ArrayList<>();
    private String routeId;
    private boolean isJoined;
    private double distance;

    public Route(String routeId) {
        this.routeId = routeId;
    }

    public ArrayList<Node> getRoute() {
        return route;
    }

    public void addNode(Node node){
        route.add(node);
    }

    public void setRoute(ArrayList<Node> route) {
        this.route = route;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Node lastNode(){
        return route.get(route.size()-1);
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public double getDistance() {
        double distance = 0;
        for(int index = 0; index+1 < route.size(); index++) {
            Node currentNode = route.get(index);
            Node nextNode = route.get(index+1);
            distance += Point2D.distance(currentNode.getX(), currentNode.getY(), nextNode.getX(), nextNode.getY());
        }
        return distance;
    }

    @Override
    public String toString() {
        return
                "routeId=" + routeId + route
                ;
    }
}