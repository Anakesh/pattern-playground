package ru.anakesh.test.patternplayground.creational;

import java.time.LocalDate;

/**
 * <p>Абстрактная фабрика</p>
 * <p>Упрощает маштабирование системы.</p>
 * <p>Порождающий паттерн проектирования,
 * который позволяет создавать семейства связанных объектов,
 * не привязываясь к конкретным классам создаваемых объектов</p>
 */
public class AbstractFactory {
    public static void main(String[] args) {
        new AbstractFactory().run();
    }

    private void run() {
        FurnitureFactory furnitureFactory = determinateFurnitureFactory();
        AssembleCenter assembleCenter = new AssembleCenter(furnitureFactory);
        assembleCenter.assemble();

    }

    private FurnitureFactory determinateFurnitureFactory() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (dayOfMonth % 2 == 0) {
            return new WoodFurnitureFactory();
        } else {
            return new StoneFurnitureFactory();
        }
    }

    interface Sofa {
        void assemble();
    }

    interface Table {
        void assemble();
    }

    interface FurnitureFactory {
        Sofa createSofa();

        Table createTable();
    }

    class AssembleCenter {
        private final Sofa sofa;
        private final Table table;


        AssembleCenter(FurnitureFactory factory) {
            this.sofa = factory.createSofa();
            this.table = factory.createTable();
        }

        public void assemble() {
            sofa.assemble();
            table.assemble();
        }
    }

    class WoodSofa implements Sofa {
        @Override
        public void assemble() {
            System.out.println("You had assembled wood sofa");
        }
    }

    class StoneSofa implements Sofa {
        @Override
        public void assemble() {
            System.out.println("You had assembled stone sofa");
        }
    }

    class WoodTable implements Table {

        @Override
        public void assemble() {
            System.out.println("You had assembled wood table");
        }
    }

    class StoneTable implements Table {

        @Override
        public void assemble() {
            System.out.println("You had assembled stone table");
        }
    }

    class WoodFurnitureFactory implements FurnitureFactory {

        @Override
        public Sofa createSofa() {
            return new WoodSofa();
        }

        @Override
        public Table createTable() {
            return new WoodTable();
        }
    }

    class StoneFurnitureFactory implements FurnitureFactory {

        @Override
        public Sofa createSofa() {
            return new StoneSofa();
        }

        @Override
        public Table createTable() {
            return new StoneTable();
        }
    }
}


