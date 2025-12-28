package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    private static final int[] DELTA_X = {-1, -1, -1,  0, 0,  1, 1, 1};
    private static final int[] DELTA_Y = {-1,  0,  1, -1, 1, -1, 0, 1};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null) return List.of();

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int endX = targetUnit.getxCoordinate();
        int endY = targetUnit.getyCoordinate();

        if (!isValidCoordinate(startX, startY) || !isValidCoordinate(endX, endY)) return List.of();
        if (startX == endX && startY == endY) return List.of(new Edge(startX, startY));

        boolean[][] obstacles = new boolean[WIDTH][HEIGHT];
        if (existingUnitList != null) {
            for (Unit u : existingUnitList) {
                if (u == null || !u.isAlive()) continue;
                if (u == attackUnit || u == targetUnit) continue;

                int x = u.getxCoordinate();
                int y = u.getyCoordinate();
                if (isValidCoordinate(x, y)) obstacles[x][y] = true;
            }
        }
        obstacles[startX][startY] = false;
        obstacles[endX][endY] = false;

        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) Arrays.fill(row, Integer.MAX_VALUE);

        Edge[][] parent = new Edge[WIDTH][HEIGHT];
        boolean[][] visited = new boolean[WIDTH][HEIGHT];

        PriorityQueue<PathNode> queue = new PriorityQueue<>();
        distance[startX][startY] = 0;
        queue.add(new PathNode(startX, startY, estimateCost(startX, startY, endX, endY)));

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            if (visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;

            if (current.x == endX && current.y == endY) break;

            for (int i = 0; i < 8; i++) {
                int nextX = current.x + DELTA_X[i];
                int nextY = current.y + DELTA_Y[i];

                if (!isValidCoordinate(nextX, nextY)) continue;
                if (obstacles[nextX][nextY]) continue;
                if (visited[nextX][nextY]) continue;

                int newDist = distance[current.x][current.y] + 1;
                if (newDist < distance[nextX][nextY]) {
                    distance[nextX][nextY] = newDist;
                    parent[nextX][nextY] = new Edge(current.x, current.y);
                    queue.add(new PathNode(nextX, nextY, newDist + estimateCost(nextX, nextY, endX, endY)));
                }
            }
        }

        return reconstructPath(parent, startX, startY, endX, endY);
    }

    private static List<Edge> reconstructPath(Edge[][] parent, int startX, int startY, int endX, int endY) {
        List<Edge> path = new ArrayList<>();
        int x = endX, y = endY;

        while (!(x == startX && y == startY)) {
            path.add(new Edge(x, y));
            Edge p = parent[x][y];
            if (p == null) return List.of();
            x = p.getX();
            y = p.getY();
        }

        path.add(new Edge(startX, startY));
        Collections.reverse(path);
        return path;
    }

    private static int estimateCost(int x, int y, int tx, int ty) {
        return Math.max(Math.abs(tx - x), Math.abs(ty - y));
    }

    private static boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private static class PathNode implements Comparable<PathNode> {
        final int x, y, f;
        PathNode(int x, int y, int f) {
            this.x = x;
            this.y = y;
            this.f = f;
        }
        @Override
        public int compareTo(PathNode o) {
            return Integer.compare(this.f, o.f);
    }
}
}
