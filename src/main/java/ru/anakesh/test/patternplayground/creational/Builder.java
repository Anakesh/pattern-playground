package ru.anakesh.test.patternplayground.creational;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


/**
 * <p>Строитель</p>
 * <p>Упрощает создание объектов</p>
 * <p>Порождающий паттерн проектирования, который позволяет создавать сложные объекты пошагово.</p>
 * <p>Строитель даёт возможность использовать один и тот же код строительства для получения разных представлений объектов.</p>
 */
public class Builder {
    public static void main(String[] args) {
        new Builder().run();
    }

    void run() {
        CarDirector carDirector = new CarDirector();
        CarBuilder carBuilder = new CarBuilder();
        Car suv = carDirector.constructSUVCar();
        CarManual cityCar = carDirector.constructCityCarManual();
    }

    enum CarType {CITY_CAR, SPORTS_CAR, SUV}

    enum CarTransmission {SINGLE_SPEED, MANUAL, AUTOMATIC, SEMI_AUTOMATIC}

    abstract class CarParametersBuilder<T> {
        protected CarType carType;
        protected CarEngine carEngine;
        protected CarTransmission carTransmission;
        protected CarNavigator carNavigator;

        public CarParametersBuilder<T> setCarType(CarType carType) {
            this.carType = carType;
            return this;
        }

        public CarParametersBuilder<T> setCarEngine(CarEngine carEngine) {
            this.carEngine = carEngine;
            return this;
        }

        public CarParametersBuilder<T> setCarTransmission(CarTransmission carTransmission) {
            this.carTransmission = carTransmission;
            return this;
        }

        public CarParametersBuilder<T> setCarNavigator(CarNavigator carNavigator) {
            this.carNavigator = carNavigator;
            return this;
        }

        public abstract T build();
    }

    @Getter
    class CarBuilder extends CarParametersBuilder<Car> {
        @Override
        public Car build() {
            return new Car(carType, carEngine, carTransmission, carNavigator);
        }
    }

    class CarManualBuilder extends CarParametersBuilder<CarManual> {
        @Override
        public CarManual build() {
            return new CarManual(carType, carEngine, carTransmission, carNavigator);
        }
    }

    class CarDirector {
        private CarBuilder carBuilder = new CarBuilder();
        private CarManualBuilder carManualBuilder = new CarManualBuilder();

        public Car constructSUVCar() {
            return carBuilder.setCarEngine(new CarEngine(2.0)).setCarNavigator(new CarNavigator("somewhere")).setCarTransmission(CarTransmission.SEMI_AUTOMATIC).setCarType(CarType.SUV).build();
        }

        public CarManual constructCityCarManual() {
            return carManualBuilder.setCarNavigator(new CarNavigator("manual")).setCarType(CarType.CITY_CAR).build();
        }
    }

    @Data
    @AllArgsConstructor
    class Car {
        private CarType carType;
        private CarEngine carEngine;
        private CarTransmission carTransmission;
        private CarNavigator carNavigator;
    }

    @Data
    @AllArgsConstructor
    class CarManual {
        private CarType carType;
        private CarEngine carEngine;
        private CarTransmission carTransmission;
        private CarNavigator carNavigator;
    }

    @Data
    @AllArgsConstructor
    class CarEngine {
        private double volume;
    }

    @Data
    @AllArgsConstructor
    class CarNavigator {
        private String destination;
    }
}


