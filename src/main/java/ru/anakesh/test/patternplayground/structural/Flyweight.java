package ru.anakesh.test.patternplayground.structural;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Легковес/Приспособленец/Кэш</p>
 * <p>Структурный паттерн проектирования, который позволяет вместить бóльшее количество объектов в отведённую оперативную память.
 * Легковес экономит память, разделяя общее состояние объектов между собой, вместо хранения одинаковых данных в каждом объекте.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Разделите поля класса, который станет легковесом, на две части:
 *             <ul>
 *                 <li>внутреннее состояние: значения этих полей одинаковы для большого числа объектов;</li>
 *                 <li>внешнее состояние (контекст): значения полей уникальны для каждого объекта.</li>
 *             </ul>
 *         </li>
 *         <li>Оставьте поля внутреннего состояния в классе, но убедитесь, что их значения неизменяемы.
 *         Эти поля должны инициализироваться только через конструктор.</li>
 *         <li>Превратите поля внешнего состояния в параметры методов, где эти поля использовались. Затем удалите поля из класса.</li>
 *         <li>Создайте фабрику, которая будет кешировать и повторно отдавать уже созданные объекты.
 *         Клиент должен запрашивать из этой фабрики легковеса с определённым внутренним состоянием, а не создавать его напрямую.</li>
 *         <li>Клиент должен хранить или вычислять значения внешнего состояния (контекст) и передавать его в методы объекта легковеса.</li>
 *     </ol>
 * </p>
 */
public class Flyweight {
    static int CANVAS_SIZE = 500;
    static int TREES_TO_DRAW = 1000000;
    static int TREE_TYPES = 2;

    public static void main(String[] args) {
        new Flyweight().run();
    }

    private void run() {
        Forest forest = new Forest();
        for (int i = 0; i < Math.floor(TREES_TO_DRAW / TREE_TYPES); i++) {
            forest.plantTree(random(0, CANVAS_SIZE), random(0, CANVAS_SIZE),
                    "Summer Oak", Color.GREEN, "Oak texture stub");
            forest.plantTree(random(0, CANVAS_SIZE), random(0, CANVAS_SIZE),
                    "Autumn Oak", Color.ORANGE, "Autumn Oak texture stub");
        }
        forest.setSize(CANVAS_SIZE, CANVAS_SIZE);
        forest.setVisible(true);

        System.out.println(TREES_TO_DRAW + " trees drawn");
        System.out.println("---------------------");
        System.out.println("Memory usage:");
        System.out.println("Tree size (8 bytes) * " + TREES_TO_DRAW);
        System.out.println("+ TreeTypes size (~30 bytes) * " + TREE_TYPES + "");
        System.out.println("---------------------");
        System.out.println("Total: " + ((TREES_TO_DRAW * 8 + TREE_TYPES * 30) / 1024 / 1024) +
                "MB (instead of " + ((TREES_TO_DRAW * 38) / 1024 / 1024) + "MB)");
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    class Tree {
        private int x;
        private int y;
        private TreeType type;

        public Tree(int x, int y, TreeType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public void draw(Graphics g) {
            type.draw(g, x, y);
        }
    }

    class TreeType {
        private String name;
        private Color color;
        private String otherTreeData;

        public TreeType(String name, Color color, String otherTreeData) {
            this.name = name;
            this.color = color;
            this.otherTreeData = otherTreeData;
        }

        public void draw(Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            g.fillRect(x - 1, y, 3, 5);
            g.setColor(color);
            g.fillOval(x - 5, y - 10, 10, 10);
        }
    }

    class TreeFactory {
        /**
         * Должен быть static
         */
        Map<String, TreeType> treeTypes = new HashMap<>();


        /**
         * Должен быть static
         */
        public TreeType getTreeType(String name, Color color, String otherTreeData) {
            TreeType result = treeTypes.get(name);
            if (result == null) {
                result = new TreeType(name, color, otherTreeData);
                treeTypes.put(name, result);
            }
            return result;
        }
    }

    class Forest extends JFrame {
        private TreeFactory treeFactory = new TreeFactory();
        private List<Tree> trees = new ArrayList<>();

        public void plantTree(int x, int y, String name, Color color, String otherTreeData) {
            TreeType type = treeFactory.getTreeType(name, color, otherTreeData);
            Tree tree = new Tree(x, y, type);
            trees.add(tree);
        }

        @Override
        public void paint(Graphics graphics) {
            for (Tree tree : trees) {
                tree.draw(graphics);
            }
        }
    }
}
