package tue.data;

import java.util.*;

/**
 * NOTE: fG = fancy G that UTF8 does not have
 * Definition of version graph:
 *
 * Given an evolving graph fG_I = {G_t_i, G_t_i+1, . . . , G_t_j }, its
 * version graph is an edge and node labeled, directed graph
 * VG_I = (V_I , E_I , L_u, L_e)
 */

public class RealVersionGraph implements IVersionGraph {
    private Interval interval;
    private final List<Snapshot> evolvingGraph = new LinkedList<>();
    private HashMap<Edge, IntervalSet> edgeIntervals = new HashMap<>();
    private HashMap<Vertex, IntervalSet> vertexIntervals = new HashMap<>();

    public RealVersionGraph() {

    }

    public RealVersionGraph(List<Snapshot> eg) {
        assert eg.size() >= 1;

        Integer start = eg.get(0).getTime();
        Integer end = eg.get(eg.size() - 1).getTime();
        eg.forEach(this::addSnapshot);
    }

    public void addSnapshot(Snapshot snap) {
        this.evolvingGraph.add(snap);

        snap.getEdges().forEach(this::l);
        snap.getVertices().forEach(this::l);

        Integer start = this.interval != null ? this.interval.getStartTime() : snap.getTime();
        Integer end = snap.getTime();

        this.interval = new Interval(start, end);
    }

    // V_I, vertices of graph
    public Set<Vertex> getVertices() {
        Set<Vertex> vertices = new HashSet<Vertex>();

        for (Snapshot graph : evolvingGraph){
            vertices.addAll(graph.getVertices());
        }

        return vertices;
    }

    public Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<Edge>();

        for (Snapshot graph : evolvingGraph){
            edges.addAll(graph.getEdges());
        }

        return edges;
    }

    @Override
    public Set<Vertex> neighbours(Vertex u) {
        return null;
    }

    public IntervalSet l(Edge e) {
        if(edgeIntervals.containsKey(e)) {
            return edgeIntervals.get(e);
        }

        IntervalSet set = new IntervalSet();
        boolean inInterval = false;
        Integer start = null;
        Integer end = null;
        for (Snapshot graph : evolvingGraph){
            if(graph.getEdges().contains(e))
            {
                if(!inInterval) {
                    start = graph.getTime();
                    inInterval = true;
                }

                end = graph.getTime();
            } else {
                inInterval = false;
                if(start != null && end != null) {
                    set.addInterval(new Interval(start, end));
                    start = null;
                    end = null;
                }
            }
        }
        edgeIntervals.put(e, set);
        return set;
    }

    public IntervalSet l(Vertex v) {
        if(vertexIntervals.containsKey(v)) {
            return vertexIntervals.get(v);
        }

        IntervalSet set = new IntervalSet();
        boolean inInterval = false;
        Integer start = null;
        Integer end = null;
        for (Snapshot graph : evolvingGraph){
            if(graph.getVertices().contains(v))
            {
                if(!inInterval) {
                    start = graph.getTime();
                    inInterval = true;
                }

                end = graph.getTime();
            } else {
                inInterval = false;
                if(start != null && end != null) {
                    set.addInterval(new Interval(start, end));
                    start = null;
                    end = null;
                }
            }
        }
        vertexIntervals.put(v, set);
        return set;
    }
}
