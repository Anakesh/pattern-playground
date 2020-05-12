package ru.anakesh.test.patternplayground.creational;

import java.time.LocalDate;

/**
 * <p>Фабричный метод</p>
 * <p>Упрощает маштабирование системы.</p>
 * <p>Порождающий паттерн проектирования,
 * который определяет общий интерфейс для создания объектов в суперклассе,
 * позволяя подклассам изменять тип создаваемых объектов.</p>
 */
public class FactoryMethod {
    public static void main(String[] args) {
        FactoryMethod factoryMethod = new FactoryMethod();
        ProductFabric productFabric = factoryMethod.determinateFabric();
        productFabric.workWithProduct();
    }

    private ProductFabric determinateFabric() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (dayOfMonth % 2 == 0) {
            return new FirstProductFabric();
        } else {
            return new SecondProductFabric();
        }
    }

    interface Product {
        void doStuff();
    }

    class FirstProduct implements Product {
        private final String info;

        FirstProduct(String info) {
            this.info = info;
        }


        public void doStuff() {
            System.out.println("FirstProduct with info: " + info);
        }
    }

    class SecondProduct implements Product {
        private final int number;

        SecondProduct(int number) {
            this.number = number;
        }

        public void doStuff() {
            System.out.println("SecondProduct with number: " + number);
        }
    }

    abstract class ProductFabric {

        public void workWithProduct() {
            Product product = createProduct();
            product.doStuff();
        }

        abstract Product createProduct();
    }

    class FirstProductFabric extends ProductFabric {

        Product createProduct() {
            return new FirstProduct("created in FirstProductFabric");
        }
    }

    class SecondProductFabric extends ProductFabric {

        Product createProduct() {
            return new SecondProduct(2);
        }
    }
}

