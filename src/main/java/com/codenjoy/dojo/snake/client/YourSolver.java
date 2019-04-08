package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;
    private boolean obstacle = false;
    private Direction prev_dir = Direction.UP;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        ArrayList<Point> obstacles = new ArrayList<>();
        this.board = board;
        List<Point> walls = board.getWalls();
        int dimX = walls.stream().mapToInt(Point::getX).max().getAsInt()+1;
        int dimY = walls.stream().mapToInt(Point::getY).max().getAsInt()+1;
        List<Point> snake = board.getSnake();
        Point stone = board.getStones().get(0);
        obstacles.addAll(walls);
        obstacles.addAll(snake);
        obstacles.add(stone);

        Lee lee = new Lee(dimX, dimY);
        APoint next_step;
        obstacles.forEach(o -> lee.setObstacle(o.getX(), board.inversionY(o.getY())));
        lee.printMe();
        Optional<List<APoint>> trace_apple_opt =
                lee.trace(board.getHead(), board.getApples().get(0));
        if (!trace_apple_opt.isPresent()) {
            Optional<List<APoint>> trace_stone_opt =
                    lee.trace(board.getHead(), stone);
            if (!trace_stone_opt.isPresent()) {
                return Direction.UP.toString();
            }
            List<APoint> trace_stone = trace_stone_opt.get();
            next_step = trace_stone.get(1);
            return convert(next_step, board.getHead()).toString();
        }
        List<APoint> trace_apple = trace_apple_opt.get();
        next_step = trace_apple.get(1);
        System.out.println(board.getHead());
        System.out.println(trace_apple);
        return convert(next_step, board.getHead()).toString();
    }

    Direction convert(APoint dst, Point src) {
        if (dst.x()<src.getX()) return Direction.LEFT;
        if (dst.x()>src.getX()) return Direction.RIGHT;
        if (board.inversionY(dst.y())>src.getY()) return Direction.DOWN;
        if (board.inversionY(dst.y())<src.getY()) return Direction.UP;
        // TODO should be refactored
        return Direction.UP;
    }

//    String getDirection(){
//        Point apple = board.getApples().get(0);
//        Point head = board.getHead();
//
//
//        System.out.printf("size X: %d, size Y: %d", dimX, dimY);
//
//        String direction = "";
//        System.out.println(isDangerInFront());
//        if (isDangerInFront()) {
//            return turn();
//        }
//
//        if((prev_dir != Direction.RIGHT) && (apple.getX() < head.getX())){
//            prev_dir = Direction.LEFT;
//
//        } else if((prev_dir != Direction.LEFT) && (apple.getX() > head.getX())){
//            prev_dir = Direction.RIGHT;
//
//        } else if((prev_dir != Direction.UP) && (apple.getY() < head.getY())){
//            prev_dir = Direction.DOWN;
//
//        } else if ((prev_dir != Direction.DOWN) &&(apple.getY() > head.getY())){
//            prev_dir = Direction.UP;
//        }
//        return prev_dir.toString();
//    }

//    private String turn() {
//        // left or right, depending on the wall
//        return null;
//    }

//    boolean isDangerInFront() {
//        Point stone = board.getStones().get(0);
//        Point head = board.getHead();
//        if (prev_dir == Direction.UP) {
//            return (stone.getX()==head.getX())&&(head.getY()-stone.getY()==1);
//        }
//        if (prev_dir == Direction.DOWN) {
//            return (stone.getX()==head.getX())&&(head.getY()-stone.getY()==-1);
//        }
//        if (prev_dir == Direction.LEFT) {
//            return (stone.getY()==head.getY())&&(head.getX()-stone.getX()==1);
//        }
//        if (prev_dir == Direction.RIGHT) {
//            return (stone.getY()==head.getY())&&(head.getX()-stone.getX()==-1);
//        }
//        return false;
//    }



    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://46.101.112.224/codenjoy-contest/board/player/3y9senc3nizjlnbtbsax?code=2221657716954864687",
                new YourSolver(new RandomDice()),
                new Board());
    }

}
