// Starter code for LP5

package ypp170130;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;

import ypp170130.BinaryHeap.Index;
import ypp170130.BinaryHeap.IndexedHeap;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

/**
 *     Team No: 39
 *     @author Pranita Hatte: prh170230
 *     @author Prit Thakkar: pvt170000
 *     @author Shivani Thakkar: sdt170030
 *     @author Yash Pradhan: ypp170130
 *
 *     Long Project 5: Minimum spanning tree algorithms
 *
 *     Implementation of three versions of Prim's Algorithm and krushkal's Algorithm.
 */

public class MST extends GraphAlgorithm<MST.MSTVertex> {
    String algorithm;
    public long wmst;   //total weight of minimum spanning tree
    List<Edge> mst;     //list of edges in minimum spanning tree

    MST(Graph g) {
        super(g, new MSTVertex((Vertex) null));
    }

    public static class MSTVertex implements Index, Comparable<MSTVertex>, Factory {

        private boolean seen;
        private Vertex parent;
        private MSTVertex representative;   //representative MSTVertex used for kruskal algorithm
        private int distance;
        private Vertex vertex;              //for prim2
        private int rank;                   //rank of the vertex
        private int name;                   //name of edge that this MSTVertex has with its parent
        private int index;                  //index of the vertex used in Priority Queue

        MSTVertex(Vertex u) {
            this.seen = false;
            this.parent = null;
            this.distance = Integer.MAX_VALUE;
            this.vertex = u;
            this.rank = 0;
            this.representative = null;
            this.name = 0;
        }

        // for prim 2
        MSTVertex(MSTVertex u) {  // for prim2
            this(u.vertex);
        }

        public MSTVertex make(Vertex u) { return new MSTVertex(u); }

        /**
         * set the index of the MSTVertex
         * @param index
         */
        public void putIndex(int index) {
            this.index = index;
        }

        /**
         * @return index of MSTVertex
         */
        public int getIndex() { return this.index; }

        public boolean isSeen() {
            return seen;
        }

        public Vertex getParent() {
            return parent;
        }

        public MSTVertex getRepresentative() {
            return representative;
        }

        public int getDistance() {
            return distance;
        }

        public Vertex getVertex() {
            return vertex;
        }

        public int getRank() {
            return rank;
        }

        public int getName() {
            return name;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public void setParent(Vertex parent) {
            this.parent = parent;
        }

        public void setRepresentative(MSTVertex representative) {
            this.representative = representative;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public void setName(int name) {
            this.name = name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int compareTo(MSTVertex other) {
            if(other == null || this.distance > other.distance) return 1;
            else if(this.distance < other.distance) return -1;
            return 0;
        }

        /**
         * find method finds the represntative of the Vertex in mst
         * @return reprentative MSTVertex
         */
        public MSTVertex find(){
            if(this != this.representative){
                this.representative = this.representative.find();
            }
            return representative;
        }

        /**
         * union two minimum spanning tree and making one representative of the spanning tree
         * @param rv
         */
        public void union(MSTVertex rv){
            if(this.rank > rv.rank){
                rv.representative = this;
            }
            else if(this.rank < rv.rank){
                this.representative = rv;
            }
            else{
                this.rank++;
                rv.representative = this;
            }
        }
    }

    /**
     * Method to initialize each MSTVertex ing Graph used for kruskal's algorithm.
     */
    private void initializeKruskalVertex() {

        for(Vertex u: g){
            get(u).representative = get(u);
            get(u).rank = 0;
        }
    }

    /**
     * Method to find minimum spanning tree using kruskal's algorithm
     * @return total weight of minimum spanning tree
     */
    public long kruskal() {
        algorithm = "Kruskal";

        //initializing MSTVertex for each vertex in graph
        initializeKruskalVertex();

        Edge[] edgeArray = g.getEdgeArray();
        Arrays.sort(edgeArray);

        mst = new LinkedList<>();
        wmst = 0;

        for(Edge e: edgeArray){
            MSTVertex ru = get(e.fromVertex()).find();
            MSTVertex rv = get(e.toVertex()).find();

            if(ru != rv){
                //adding edge to mst edge list
                mst.add(e);
                ru.union(rv);
                wmst += e.getWeight();
            }
        }
        return wmst;
    }

    /**
     * Method to find minimum spanning tree using prim3 algorithm from source vertex
     * @param s source vertex
     * @return total weight of minimum spanning tree
     */
    public long prim3(Vertex s) {
        algorithm = "indexed heaps";

        //initialization of MSTVertex required for prim3 algorithm
        initialize();

        mst = new LinkedList<>();
        get(s).setDistance(0);
        wmst = 0;

        //IndexHeap of MSTVertex
        IndexedHeap<MSTVertex> q = new IndexedHeap<>(g.size());

        for(Vertex u: g) q.add(get(u));

        //Edge HashMap
        HashMap<Integer, Edge> edgeHashMap = new HashMap<>();

        while(!q.isEmpty()){

            MSTVertex u = q.remove();
            Vertex u1 = u.getVertex();
            u.setSeen(true);
            wmst+= u.getDistance();

            if(u.getParent()!=null){
                mst.add(edgeHashMap.get(u.getName()));
            }

            for(Edge e: g.incident(u1)){
                Vertex v = e.otherEnd(u1);
                if(!get(v).isSeen() && e.getWeight() < get(v).getDistance()){
                    edgeHashMap.put(e.getName(), e);
                    get(v).setDistance(e.getWeight());
                    get(v).setParent(u1);
                    get(v).setName(e.getName());
                    q.decreaseKey(get(v));
                }
            }
        }
        return wmst;
    }

    /**
     * Method to find minimum spanning tree using prim2 algorithm from source vertex
     * @param s source vertex
     * @return total weights of minimum spanning tree
     */
    public long prim2(Vertex s) {

        algorithm = "PriorityQueue<Vertex>";
        initialize();

        mst = new LinkedList<>();
        get(s).setDistance(0);
        wmst = 0;

        //Priority Queue of MSTVertex
        PriorityQueue<MSTVertex> q = new PriorityQueue<>();

        q.add(get(s));

        //Edge HashMap
        HashMap<Integer, Edge> edgeHashMap = new HashMap<>();

        while(!q.isEmpty()){
            MSTVertex u = q.remove();
            Vertex u1 = u.getVertex();

            if(!get(u1).isSeen()){
                get(u1).setSeen(true);
                wmst+= u.getDistance();

                if(u.getParent()!=null){
                    mst.add(edgeHashMap.get(u.getName()));
                }

                for(Edge e: g.incident(u1)){
                    Vertex v = e.otherEnd(u1);

                    if(!get(v).isSeen() && e.getWeight() < get(v).getDistance()){
                        edgeHashMap.put(e.getName(), e);
                        MSTVertex v1 = new MSTVertex(get(v));
                        v1.setDistance(e.getWeight());
                        v1.setParent(u1);
                        v1.setName(e.getName());
                        q.add(v1);
                    }
                }
            }
        }
        return wmst;
    }

    /**
     * Method to find Minimum spanning tree using prim1 algorithm from source vertex
     * @param s source Vertex
     * @return total weights of minimum spanning tree
     */
    public long prim1(Vertex s) {
        algorithm = "PriorityQueue<Edge>";

        //initializing MST Vertex fields
        initialize();

        mst = new LinkedList<>();
        get(s).setSeen(true);
        wmst = 0;
        PriorityQueue<Edge> q = new PriorityQueue<>();

        for(Edge e: g.incident(s)){
            q.add(e);
        }

        while(!q.isEmpty()){

            Edge e = q.remove();
            Vertex u = e.fromVertex();
            Vertex v = get(u).seen? e.otherEnd(u):u;
            if(get(v).isSeen()) continue;
            get(v).setSeen(true);
            get(v).setParent(u);
            wmst = wmst + e.getWeight();
            mst.add(e);

            for(Edge e1: g.incident(v)){
                Vertex w = e1.otherEnd(v);
                if(!get(w).isSeen()){
                    q.add(e1);
                }
            }
        }
        return wmst;
    }

    private void initialize(){
        for(Vertex u: g){
            get(u).setSeen(false);
            get(u).setParent(null);
            get(u).setDistance(Integer.MAX_VALUE);
        }
    }

    /**
     *
     * @param g Graph class instance
     * @param s source Vertex
     * @param choice algorithm choice
     * @return MST object
     */
    public static MST mst(Graph g, Vertex s, int choice) {
        MST m = new MST(g);
        switch(choice) {
            case 0:
                //mst using kruskal algorithm
                m.kruskal();
                break;
            case 1:
                //mst using prim1 algorithm
                m.prim1(s);
                break;
            case 2:
                //mst using prim2 algorithm
                m.prim2(s);
                break;
            default:
                //mst using prim3 algorithm
                m.prim3(s);
                break;
        }
        return m;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        int choice = 0; // Kruskal
        boolean readHere = false;

        if(readHere){
            /*
            * H:\LP5\src\input\mst-5-6-19.txt
            * H:\LP5\src\input\mst-50-140-84950.txt
            * H:\LP5\src\input\mst-10k-30k-1085305.txt
            * */
            File inputFile = new File("src\\input\\mst-10k-30k-1085305.txt");
            in = new Scanner(inputFile);
            choice = 0;
        }
        else{
            if (args.length == 0 || args[0].equals("-")) {
                in = new Scanner(System.in);
            } else {
                File inputFile = new File(args[0]);
                in = new Scanner(inputFile);
            }
            if (args.length > 1) { choice = Integer.parseInt(args[1]); }
        }

        Graph g = Graph.readGraph(in);
        Vertex s = g.getVertex(1);

        Timer timer = new Timer();
        MST m = mst(g, s, choice);
        System.out.println(m.algorithm + "\n" + m.wmst);

        System.out.println("No of Edges : V-1 : " + (m.mst).size());

//        for(Edge e: m.mst){
//            System.out.println(e);
//        }

        System.out.println(timer.end());
    }
}