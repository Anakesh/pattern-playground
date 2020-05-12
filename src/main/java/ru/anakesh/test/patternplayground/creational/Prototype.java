package ru.anakesh.test.patternplayground.creational;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Прототип</p>
 * <p>Упрощает копирование объекта</p>
 * <p>Прототип — это порождающий паттерн проектирования, который позволяет копировать объекты, не вдаваясь в подробности их реализации</p>
 */
public class Prototype {
    public static void main(String[] args) {
        new Prototype().run();
    }

    private void run() {
        BundledShapeCache cache = new BundledShapeCache();

        Shape shape1 = cache.get("Big green circle");
        Shape shape2 = cache.get("Medium blue rectangle");
        Shape shape3 = cache.get("Medium blue rectangle");

        if (shape1 != shape2 && !shape1.equals(shape2)) {
            System.out.println("Big green circle != Medium blue rectangle (yay!)");
        } else {
            System.out.println("Big green circle == Medium blue rectangle (booo!)");
        }

        if (shape2 != shape3) {
            System.out.println("Medium blue rectangles are two different objects (yay!)");
            if (shape2.equals(shape3)) {
                System.out.println("And they are identical (yay!)");
            } else {
                System.out.println("But they are not identical (booo!)");
            }
        } else {
            System.out.println("Rectangle objects are the same (booo!)");
        }
    }

    @Data
    @NoArgsConstructor
    abstract class Shape {
        private int x;
        private int y;
        private String color;

        public Shape(Shape target) {
            this.x = target.x;
            this.y = target.y;
            this.color = target.color;
        }

        public abstract Shape clone();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class Circle extends Shape {
        private int radius;

        public Circle(Circle target) {
            super(target);
            this.radius = target.radius;
        }

        @Override
        public Shape clone() {
            return new Circle(this);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class Rectangle extends Shape {
        private int width;
        private int height;

        public Rectangle(Rectangle target) {
            super(target);
            this.height = target.height;
            this.width = target.width;
        }


        @Override
        public Shape clone() {
            return new Rectangle(this);
        }
    }

    class BundledShapeCache {
        private Map<String, Shape> cache = new HashMap<>();

        public BundledShapeCache() {
            Circle circle = new Circle();
            circle.setX(5);
            circle.setY(7);
            circle.setRadius(45);
            circle.setColor("Green");

            Rectangle rectangle = new Rectangle();
            rectangle.setX(6);
            rectangle.setY(9);
            rectangle.setWidth(8);
            rectangle.setHeight(10);
            rectangle.setColor("Blue");

            cache.put("Big green circle", circle);
            cache.put("Medium blue rectangle", rectangle);
        }

        public Shape put(String key, Shape shape) {
            cache.put(key, shape);
            return shape;
        }

        public Shape get(String key) {
            return cache.get(key).clone();
        }
    }
}
