package com.ded.misle.world.logic;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class Pathfinder {
    private final static int DEFAULT_TILE_COST = 1;
    private final Map<Point, Node> nodeMap = new HashMap<>();

    private Node getNode(Point pos) {
        return nodeMap.computeIfAbsent(pos, Node::new);
    }

    public Path findPath(Point start, Point goal, Predicate<Point> isWalkable) {

        nodeMap.clear();

        Node targetNode = getNode(goal);

        var toSearch = new ArrayList<Node>();
        toSearch.add(getNode(start));

        var processed = new ArrayList<Node>();

        while (!toSearch.isEmpty()) {
            var current = toSearch.getFirst();

            for (Node t : toSearch) {
                if (t.getF() < current.getF() ||
                    t.getF() == current.getF() &&
                    t.getH() < current.getH()) {
                    current = t;
                }
            }

            processed.add(current);
            toSearch.remove(current);

            if (current.pos.equals(goal)) {
                var currentPathTile = current;
                var path = new ArrayList<Node>();

                while (!currentPathTile.pos.equals(start)) {
                    path.add(currentPathTile);
                    currentPathTile = currentPathTile.parent;
                }

                var pathPoints = new ArrayList<Point>();
                for (Node t : path) {
                    pathPoints.add(t.pos);
                }

                return new Path(pathPoints.reversed().toArray(new Point[0]));
            }

            for (Node neighbor : current.getNeighbors()
                .stream().filter(
                    t -> (isWalkable.test(t.pos) || t.equals(targetNode)) &&
                    !processed.contains(t)).toList()) {
                boolean inSearch = toSearch.contains(neighbor);

                int costToNeighbor = current.getG() + DEFAULT_TILE_COST;

                if (!inSearch || costToNeighbor <= neighbor.getG()) {
                    neighbor.setG(costToNeighbor);
                    neighbor.parent = current;

                    if (!inSearch) {
                        neighbor.setH(neighbor.getDistance(targetNode));
                        toSearch.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private class Node {
        public Node parent;
        public Point pos;
        private int g;
        private int h;
        private int f;

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

        public int getG() {
            return g;
        }

        public int getH() {
            return h;
        }

        public int getF() {
            return f;
        }

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
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return pos.equals(node.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}
