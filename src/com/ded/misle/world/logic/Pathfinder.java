package com.ded.misle.world.logic;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class Pathfinder {
    private static final int DEFAULT_TILE_COST = 1;
    private final Map<Point, Node> nodeMap = new HashMap<>();

    private Node getNode(Point pos) {
        return nodeMap.computeIfAbsent(pos, Node::new);
    }

    public Path findPath(Point start, Point goal, Predicate<Point> isWalkable) {
        nodeMap.clear();
        Node targetNode = getNode(goal);

        var openList = new ArrayList<Node>();
        var closedList = new ArrayList<Node>();

        openList.add(getNode(start));

        while (!openList.isEmpty()) {
            Node current = selectNodeWithLowestScore(openList);

            openList.remove(current);
            closedList.add(current);

            if (current.pos.equals(goal)) {
                return reconstructPath(current, start);
            }

            for (Node neighbor : getValidNeighbors(current, targetNode, closedList, isWalkable)) {
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

    private List<Node> getValidNeighbors(Node current, Node target, List<Node> closed, Predicate<Point> isWalkable) {
        return current.getNeighbors()
            .stream()
            .filter(n -> (isWalkable.test(n.pos) || n.equals(target)) && !closed.contains(n))
            .toList();
    }

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

    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private class Node {
        public Node parent;
        public Point pos;
        private int g, h, f;

        Node(Point p) {
            this.pos = p;
        }

        List<Node> getNeighbors() {
            Point p = this.pos;
            return List.of(
                getNode(new Point(p.x + 1, p.y)),
                getNode(new Point(p.x - 1, p.y)),
                getNode(new Point(p.x, p.y + 1)),
                getNode(new Point(p.x, p.y - 1))
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

        public int getDistance(Node other) {
            return heuristic(pos, other.pos);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Node node && pos.equals(node.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}