package com.kco.game.maze.run;

import com.kco.game.maze.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/**
 * 迷宫
 * Created by pc on 2017/3/2.
 */
public class MazeApplication extends Application {

    public static final int WIDTH_SIZE = 10;
    public static final int ROW = 65;
    public static final int LINE = 130;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
//        AbstractMazeBuilder builder = new MazeMapBuilder(ROW, LINE);
        AbstractMazeBuilder builder = new MazeArrayBuilder(ROW, LINE);
        builder.makeMaze();
        Scene scene = new Scene(root, (LINE + 2) * WIDTH_SIZE, (ROW + 2) * WIDTH_SIZE);
        Canvas canvas = new Canvas(LINE * WIDTH_SIZE, ROW * WIDTH_SIZE);
        canvas.setTranslateX(WIDTH_SIZE);
        canvas.setTranslateY(WIDTH_SIZE);
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        drawBoxes(builder, ctx);
        drawPath(builder, ctx);
        root.getChildren().add(canvas);
        primaryStage.setScene(scene);
        primaryStage.setTitle("迷宫生成以及迷宫求解");
        primaryStage.show();
    }

    /**
     * 画迷宫路径
     * @param builder
     * @param ctx
     */
    private void drawPath(AbstractMazeBuilder builder, GraphicsContext ctx) {
        ctx.setStroke(Color.RED);
//        ctx.setLineWidth(3);
        List<AbstractMazeBuilder.MazePoint> mazePoints = builder.solvePath();
        AbstractMazeBuilder.MazePoint start = mazePoints.get(0);
        for (AbstractMazeBuilder.MazePoint point : mazePoints){
            ctx.strokeLine(WIDTH_SIZE / 2 + start.y * WIDTH_SIZE, WIDTH_SIZE / 2 + start.x * WIDTH_SIZE,
                    WIDTH_SIZE / 2 + point.y * WIDTH_SIZE, WIDTH_SIZE / 2 + point.x  * WIDTH_SIZE);
            start = point;
        }
    }

    /**
     * 画迷宫
     * @param builder
     * @param ctx
     */
    private void drawBoxes(AbstractMazeBuilder builder, GraphicsContext ctx) {
        ctx.setLineWidth(2);
        for (int i = 0; i < builder.getRow(); i ++){
            for (int j = 0; j < builder.getLine(); j ++){
                drawBox(builder.getBoxes(i,j),i,j, ctx);
            }
        }
    }

    private void drawBox(Box boxes,int row, int line, GraphicsContext ctx) {
        for (Position position : Position.values()){
            Wall wall = boxes.get(position);
            if (wall == Wall.ACCESS){
                continue;
            }
            switch (position){
                case TOP:
                    ctx.strokeLine(line * WIDTH_SIZE, row * WIDTH_SIZE,
                            (line + 1) * WIDTH_SIZE, row  * WIDTH_SIZE);
                    break;
                case RIGHT:
                    ctx.strokeLine((line + 1) * WIDTH_SIZE, row * WIDTH_SIZE,
                            (line + 1) * WIDTH_SIZE, (row + 1)  * WIDTH_SIZE);
                    break;
                case DOWN:
                    ctx.strokeLine((line + 1) * WIDTH_SIZE, (row + 1) * WIDTH_SIZE,
                            line * WIDTH_SIZE, (row + 1) * WIDTH_SIZE);
                    break;
                case LEFT:
                default:
                    ctx.strokeLine(line * WIDTH_SIZE, (row + 1) * WIDTH_SIZE,
                            line * WIDTH_SIZE, row  * WIDTH_SIZE);
                    break;
            }
        }
    }

}
