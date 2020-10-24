package reflections;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Solution {
    // Input parameters
    final Point dimensions;
    final Point source;
    final Point target;
    final int distance2; // square of distance

    // Temporary variables
    Set<Point> obstacleDirections;
    int targetHits;

    private Solution(Point dimensions, Point source, Point target, int distance) {
        this.dimensions = dimensions;
        this.source = source;
        this.target = target;
        this.distance2 = distance * distance;
        this.targetHits = 0;
    }

    public static int solution(int[] dimensions, int[] your_position, int[] guard_position, int distance) {
        return new Solution(
            new Point(dimensions[0], dimensions[1]),
            new Point(your_position[0], your_position[1]),
            new Point(guard_position[0], guard_position[1]),
            distance
        ).solve();
    }

    private int solve() {
        obstacleDirections = new HashSet<>();
        targetHits = 0;

        // Instead of beams reflecting off the walls, we can imagine infinite grid of reflected rooms with no walls.
        // For each reflection we check the line between source in original room and target in reflected room.
        // - we check that distance between source and target less than input value.
        // - we check that there is no obstacles between source and target.
        //   Obstacles are:
        //   - sources in other reflected rooms
        //   - targets in other reflected rooms
        //   - corners of reflected rooms
        //
        // We process reflected rooms from center outwards, perimeter by perimeter:
        //     2
        //   2 1 2
        // 2 1 0 1 2
        //   2 1 2
        //     2
        // We stop processing when all rooms in the perimeter are too far (distance to target is more than input value).
        //
        checkDistanceAndSolveForReflection(0, 0);
        int offset = 1;
        while (checkDistanceAndSolveForPerimeter(offset)) {
            offset++;
        }

        return targetHits;
    }

    // Solve all rooms in the perimeter and increment `targetHits`.
    // Return true if for at least one room in the perimeter
    // distance between original source and reflected target less than `distance`.
    private boolean checkDistanceAndSolveForPerimeter(int offset) {
        int roomX = offset;
        int roomY = 0;
        boolean anyDistanceisValid = false;
        // Process rooms clockwise staring from rightest room
        while (roomX > 0) {
            anyDistanceisValid |= checkDistanceAndSolveForReflection(roomX, roomY);
            roomX--;
            roomY++;
        }
        while (roomY > 0) {
            anyDistanceisValid |= checkDistanceAndSolveForReflection(roomX, roomY);
            roomX--;
            roomY--;
        }
        while (roomX < 0) {
            anyDistanceisValid |= checkDistanceAndSolveForReflection(roomX, roomY);
            roomX++;
            roomY--;
        }
        while (roomY < 0) {
            anyDistanceisValid |= checkDistanceAndSolveForReflection(roomX, roomY);
            roomX++;
            roomY++;
        }
        return anyDistanceisValid;
    }

    // Solve reflected room identified by `roomX` and `roomY` - offsets from original room (0, 0)
    // Return true if distance between original source and reflected target less than `distance`.
    private boolean checkDistanceAndSolveForReflection(int roomX, int roomY) {
        Point reflTarget = reflectedPoint(target, roomX, roomY);
        // Check if target is too far and return early
        if (distance2 < source.dist2(reflTarget)) {
            return false;
        }
        // Add reflected source to obstacle set, skip for original room to avoid division by zero
        if (roomX != 0 || roomY != 0) {
            addObstacleDirection(reflectedPoint(source, roomX, roomY));
        }
        // check no obstacles between source and reflected target and increment result counter
        if (noObstaclesFromSource(reflTarget)) {
            targetHits++;
        }
        // Add reflected target to obstacle set
        addObstacleDirection(reflTarget);
        // Add room corners to obstacle set
        if (roomX <= 0 && roomY <= 0) {
            addObstacleDirection(topLeftCorner(roomX, roomY));
        }
        if (roomX >= 0 && roomY <= 0) {
            addObstacleDirection(topLeftCorner(roomX + 1, roomY));
        }
        if (roomX <= 0 && roomY >= 0) {
            addObstacleDirection(topLeftCorner(roomX, roomY + 1));
        }
        if (roomX >= 0 && roomY >= 0) {
            addObstacleDirection(topLeftCorner(roomX + 1, roomY + 1));
        }
        return true;
    }
    
    private Point reflectedPoint(Point point, int roomX, int roomY) {
        boolean invertX = roomX % 2 != 0;
        boolean invertY = roomY % 2 != 0;
        int roomCornerX = roomX * dimensions.x;
        int roomCornerY = roomY * dimensions.y;
        return new Point(
            roomCornerX + (invertX ? dimensions.x - point.x : point.x),
            roomCornerY + (invertY ? dimensions.y - point.y : point.y)
        );
    }

    private Point topLeftCorner(int roomX, int roomY) {
        return new Point(roomX * dimensions.x,roomY * dimensions.y);
    }

    private void addObstacleDirection(Point obstacle) {
        obstacleDirections.add(sourceDirectionVector(obstacle));
    }

    private boolean noObstaclesFromSource(Point reflTarget) {
        return !obstacleDirections.contains(sourceDirectionVector(reflTarget));
    }

    // We convert direction from source to target/obstacle into simples integer form
    private Point sourceDirectionVector(Point point) {
        int gcd = absGcd(point.x - source.x, point.y - source.y);
        return new Point(
            (point.x - source.x) / gcd,
            (point.y - source.y) / gcd
        );
    }

    private static int absGcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (a != 0) {
            int aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return b;
    }

    private static class Point {
        final int x;
        final int y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        // Square of distance
        int dist2(Point that) {
            int dx = that.x - this.x;
            int dy = that.y - this.y;
            return dx * dx + dy * dy;
        }
    }
}