package com.ded.misle.world.logic;

import com.ded.misle.utils.MathUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Implements the A* pathfinding algorithm for 2D grid-based worlds.
 * <p>
 * Given a start and goal {@link Point}, and a walkability predicate,
 * this class computes the optimal path (if any) using a weighted
 * cost-distance model with Manhattan distance as heuristic.
 * <p>
 * This implementation assumes a tile-based world where movement
 * is allowed in 4 cardinal directions (up, down, left, right).
 * Diagonal movement is not supported.
 * <p>
 * The algorithm favors paths with lower total cost {@code f = g + h},
 * where:
 * <ul>
 *   <li>{@code g} is the cumulative cost from the start node</li>
 *   <li>{@code h} is the heuristic estimate to the goal (Manhattan distance)</li>
 * </ul>
 * <p>
 * Once a path is found, it is reconstructed by walking backward
 * from the goal node to the start node using each node's {@code parent}.
 *
 * @see Path
 * @see PhysicsEngine
 * @see java.util.function.Predicate
 */
public class Pathfinder {
    /**
     * The default movement cost from one tile to an adjacent tile.
     * <p>
     * Used when computing the {@code g} cost of neighbors.
     */
    private static final int DEFAULT_TILE_COST = 1;

    /**
     * Finds the shortest walkable path from a start point to a goal point.
     * <p>
     * This method uses A* pathfinding to search for the path. If no path exists,
     * or the start or goal is blocked (not walkable), {@code null} is returned.
     * <p>
     * The {@code isWalkable} predicate is used to define what constitutes
     * a valid traversable tile. If the goal point is not walkable but is the
     * desired target, it is still considered valid.
     *
     * @param start the starting point of the path
     * @param goal the destination point of the path
     * @param isWalkable a predicate that returns {@code true} for traversable tiles
     * @return a {@link Path} representing the shortest walkable path, or {@code null} if unreachable
     */
    public Path findPath(Point start, Point goal, Predicate<Point> isWalkable) {
        Map<Point, Node> nodeMap = new HashMap<>();

        Node targetNode = getNode(nodeMap, goal);

        var openList = new ArrayList<Node>();
        var closedList = new ArrayList<Node>();

        openList.add(getNode(nodeMap, start));

        while (!openList.isEmpty()) {
            Node current = selectNodeWithLowestScore(openList);

            openList.remove(current);
            closedList.add(current);

            if (current.pos.equals(goal)) {
                return reconstructPath(current, start);
            }

            for (Node neighbor : getValidNeighbors(nodeMap, current, targetNode, closedList, isWalkable)) {
                int costToNeighbor = current.getG() + DEFAULT_TILE_COST;
                boolean isNew = !openList.contains(neighbor);

                if (isNew || costToNeighbor <= neighbor.getG()) {
                    neighbor.setG(costToNeighbor);
                    neighbor.parent = current;

                    if (isNew) {
                        neighbor.setH(neighbor.getDistance(targetNode));
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a cached {@code Node} for the given position,
     * or creates it if it doesn't exist yet.
     * <p>
     * Caching ensures that all positions share reference equality when needed,
     * allowing neighbor updates and comparisons to function reliably.
     *
     * @param nodeMap a cache mapping positions to node objects
     * @param pos the tile position
     * @return the corresponding {@code Node} instance
     */
    private Node getNode(Map<Point, Node> nodeMap, Point pos) {
        return nodeMap.computeIfAbsent(pos, Node::new);
    }

    /**
     * Selects the next node to explore from the open list.
     * <p>
     * This is the node with the lowest {@code f} cost.
     * In case of ties, the one with the lower {@code h} is preferred.
     *
     * @param nodes the list of candidate nodes
     * @return the most promising node for exploration
     */
    private Node selectNodeWithLowestScore(List<Node> nodes) {
        Node best = nodes.getFirst();
        for (Node node : nodes) {
            if (node.getF() < best.getF() ||
                (node.getF() == best.getF() && node.getH() < best.getH())) {
                best = node;
            }
        }
        return best;
    }

    /**
     * Filters out valid neighbor nodes for a given node.
     * <p>
     * A neighbor is valid if it is not in the closed list, and either walkable
     * or is the goal itself (even if not walkable).
     *
     * @param nodeMap the shared node cache
     * @param current the node being expanded
     * @param target the goal node (used to allow reaching blocked goals)
     * @param closed the set of already evaluated nodes
     * @param isWalkable predicate to test tile walkability
     * @return a list of valid neighbor nodes
     */
    private List<Node> getValidNeighbors(Map<Point, Node> nodeMap, Node current, Node target, List<Node> closed, Predicate<Point> isWalkable) {
        return current.getNeighbors(nodeMap)
            .stream()
            .filter(n -> (isWalkable.test(n.pos) || n.equals(target)) && !closed.contains(n))
            .toList();
    }

    /**
     * Reconstructs a path from the goal node back to the start node.
     * <p>
     * The resulting path does not include the start node itself, only
     * the steps required to reach the goal.
     *
     * @param goal the node at the goal position
     * @param start the original starting point
     * @return a {@link Path} of steps from start to goal
     */
    private Path reconstructPath(Node goal, Point start) {
        var path = new ArrayList<Point>();
        Node current = goal;

        while (!current.pos.equals(start)) {
            path.add(current.pos);
            current = current.parent;
        }

        Collections.reverse(path);
        return new Path(path.toArray(new Point[0]));
    }

    /**
     * Represents a node (tile) in the A* search space.
     * <p>
     * Each node stores its position, path cost metrics (g, h, f),
     * and its parent in the search tree for path reconstruction.
     * <p>
     * Neighbor retrieval is limited to the 4 adjacent tiles.
     */
    private class Node {
        public Node parent;
        public Point pos;
        private int g, h, f;

        /**
         * Creates a new node at the specified position.
         *
         * @param p the tile position this node represents
         */
        Node(Point p) {
            this.pos = p;
        }

        /**
         * Retrieves adjacent nodes (up, down, left, right).
         *
         * @param nodeMap the shared node cache
         * @return a list of adjacent nodes
         */
        private List<Node> getNeighbors(Map<Point, Node> nodeMap) {
            Point p = this.pos;
            return List.of(
                getNode(nodeMap, new Point(p.x + 1, p.y)),
                getNode(nodeMap, new Point(p.x - 1, p.y)),
                getNode(nodeMap, new Point(p.x, p.y + 1)),
                getNode(nodeMap, new Point(p.x, p.y - 1))
            );
        }

        public int getG() { return g; }
        public int getH() { return h; }
        public int getF() { return f; }

        public void setH(int h) {
            this.h = h;
            updateF();
        }

        public void setG(int g) {
            this.g = g;
            updateF();
        }

        private void updateF() {
            this.f = this.g + this.h;
        }

        /**
         * Computes the estimated distance from this node to another,
         * using the Manhattan distance metric.
         *
         * @param other the target node
         * @return the estimated cost to reach the target
         */
        public int getDistance(Node other) {
            return MathUtils.manhattan(pos, other.pos);
        }

        /**
         * Two nodes are considered equal if they share the same {@code Point}.
         *
         * @param obj the object to compare
         * @return true if the nodes share the same position
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Node node && pos.equals(node.pos);
        }

        /**
         * Returns the hash code of the underlying position.
         * Needed for correct usage in hash-based collections.
         *
         * @return hash code of the point
         */
        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}