package maxflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

public class Solution {
    // This is Max Flow problem: https://en.wikipedia.org/wiki/Maximum_flow_problem
    // Going to use push-rebalance algorithm to solve it
    public static int solution(int[] entrances, int[] exits, int[][] path) {
        // We will use only one source and one target
        int source = entrances[0];
        int target = exits[0];
        Graph g = new Graph(source, target, path.length);
        Set<Integer> sourceSet = Arrays.stream(entrances).boxed().collect(toSet());
        Set<Integer> targetSet = Arrays.stream(exits).boxed().collect(toSet());
        for (int from = 0; from < path.length; from++) {
            int[] caps = path[from];
            for (int to = 0; to < caps.length; to++) {
                int cap = caps[to];
                // Ignore empty edges and self-loops
                if (cap == 0 || from == to) {
                    continue;
                }
                // Ignore all inputs of the sources and all outputs of the targets
                if (sourceSet.contains(to) || targetSet.contains(from)) {
                    continue;
                }
                // Create link with given capacity, or increase capacity of existing link
                g.link(
                    sourceSet.contains(from) ? source : from,
                    targetSet.contains(to) ? target : to,
                    cap);
            }
        }
        return g.solve();
    }

    static class Graph {
        // Indices of source and target vertices
        final int source;
        final int target;
        // Vertices heights
        final int[] heights;
        // Vertices excess flow
        final int[] excess;
        // Heap of vertices with excess flow
        final PriorityQueue<Integer> excessVerts;
        // List of bidirectional edges per vertex
        // BiEdge stores capacity for both directions
        final List<BiEdge>[] edges;

        @SuppressWarnings("unchecked")
        Graph(int source, int target, int size) {
            this.source = source;
            this.target = target;
            this.heights = new int[size];
            this.excess = new int[size];
            // We would process vertex with max height on each iteration, using custom comparator for this.
            // We are allowed to change heights[i] only after removing element from the heap.
            this.excessVerts = new PriorityQueue<>(comparing(i -> -heights[i]));
            this.edges = Stream.generate(ArrayList::new).limit(size).toArray(List[]::new);
        }

        void link(int a, int b, int cap) {
            Edge existing = availableEdges(a)
                    .filter(e -> e.to() == b)
                    .findFirst()
                    .orElse(null);
            if (existing == null) {
                BiEdge be = new BiEdge(a, b, cap);
                edges[a].add(be);
                edges[b].add(be);
                return;
            }
            BiEdge be = existing.be;
            be.caps[existing.fromIdx] += cap;
        }

        // https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm
        int solve() {
            // Init: set height for source and saturate all of its edges
            heights[source] = heights.length;
            availableEdges(source).forEach(e -> e.push(e.cap()));

            // Repeat until no vertices have excess flow
            while (!excessVerts.isEmpty()) {
                // Take highest vertex with excess flow
                int vert = excessVerts.poll();
                // Pick its edge pointing to lowest vertex
                Edge lowest = availableEdges(vert)
                        .min(comparing((Edge e) -> heights[e.to()]))
                        .orElseThrow(RuntimeException::new);
                // Relabel if required: increase height of current vertex
                if (heights[vert] <= heights[lowest.to()]) {
                    heights[vert] = heights[lowest.to()] + 1;
                }
                // Push excess flow downstream
                int delta = Math.min(excess[vert], lowest.cap());
                lowest.push(delta);
                // Re-add current vertex if excess flow is more than residual capacity
                if (excess[vert] > 0) {
                    excessVerts.add(vert);
                }
            }
            // Excess flow on target node is our answer
            return excess[target];
        }

        Stream<Edge> availableEdges(int from) {
            return edges[from].stream()
                    .map(be -> new Edge(be, from))
                    .filter(e -> e.cap() > 0);
        }

        // Convenience wrapper for BiEdge, from one of its vertices point of view
        class Edge {
            final BiEdge be;
            final int fromIdx;
            Edge(BiEdge be, int from) {
                this.be = be;
                this.fromIdx = be.verts[0] == from ? 0 : 1;
            }

            // Push some flow downstream and update capacities in both directions
            // Add downstream vertex to excessVerts if it is not already there
            void push(int amount) {
                if (target != to() && excess[to()] == 0) {
                    excessVerts.add(to());
                }
                excess[from()] -= amount;
                excess[to()] += amount;
                be.caps[fromIdx] -= amount;
                be.caps[1 - fromIdx] += amount;
            }

            // Residual capacity in given direction
            int cap() {
                return be.caps[fromIdx];
            }

            int from() {
                return be.verts[fromIdx];
            }

            int to() {
                return be.verts[1 - fromIdx];
            }

            @Override
            public String toString() {
                return from() + " ===" + cap() + "==> " + to();
            }
        }
    }

    // Bidirectional edge
    static class BiEdge {
        // Indexes of linked vertices
        final int[] verts;
        // Residual capacities for both directions
        // caps[0] - capacity from vers[0] to verts[1]
        // caps[1] - capacity from vers[1] to verts[0]
        final int[] caps;
        BiEdge(int a, int b, int cap) {
            this.verts = new int[] {a, b};
            this.caps = new int[] {cap, 0};
        }

        @Override
        public String toString() {
            return verts[0] + " <==" + caps[1] + "===" + caps[0] + "==> " + verts[1];
        }
    }
}
