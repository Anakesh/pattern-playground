package ru.anakesh.test.patternplayground.structural;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Адаптер</p>
 * <p>Структурный паттерн проектирования, который позволяет объектам с несовместимыми интерфейсами работать вместе.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Убедитесь, что у вас есть два класса с несовместимыми интерфейсами:
 *             <ul>
 *                 <li>полезный сервис — служебный класс, который вы не можете изменять (он либо сторонний, либо от него зависит другой код);</li>
 *                 <li>один или несколько клиентов — существующих классов приложения, несовместимых с сервисом из-за неудобного или несовпадающего интерфейса.</li>
 *             </ul>
 *         </li>
 *         <li>Опишите клиентский интерфейс, через который классы приложения смогли бы использовать класс сервиса.</li>
 *         <li>Создайте класс адаптера, реализовав этот интерфейс.</li>
 *         <li>Поместите в адаптер поле, которое будет хранить ссылку на объект сервиса. Обычно это поле заполняют объектом, переданным в конструктор адаптера.
 *         В случае простой адаптации этот объект можно передавать через параметры методов адаптера.</li>
 *         <li>Реализуйте все методы клиентского интерфейса в адаптере. Адаптер должен делегировать основную работу сервису.</li>
 *         <li>Приложение должно использовать адаптер только через клиентский интерфейс. Это позволит легко изменять и добавлять адаптеры в будущем</li>
 *     </ol>
 * </p>
 */
public class Adapter {
    public static void main(String[] args) {
        new Adapter().run();
    }

    private void run() {
        // Круглое к круглому — всё работает.
        RoundHole hole = new RoundHole(5);
        RoundPeg rpeg = new RoundPeg(5);
        if (hole.fits(rpeg)) {
            System.out.println("Round peg r5 fits round hole r5.");
        }

        SquarePeg smallSqPeg = new SquarePeg(2);
        SquarePeg largeSqPeg = new SquarePeg(20);
        // hole.fits(smallSqPeg); // Не скомпилируется.

        // Адаптер решит проблему.
        SquarePegAdapter smallSqPegAdapter = new SquarePegAdapter(smallSqPeg);
        SquarePegAdapter largeSqPegAdapter = new SquarePegAdapter(largeSqPeg);
        if (hole.fits(smallSqPegAdapter)) {
            System.out.println("Square peg w2 fits round hole r5.");
        }
        if (!hole.fits(largeSqPegAdapter)) {
            System.out.println("Square peg w20 does not fit into round hole r5.");
        }
    }

    @Data
    @AllArgsConstructor
    class RoundHole {
        private double radius;

        public boolean fits(RoundPeg peg) {
            return (this.getRadius() >= peg.getRadius());
        }
    }

    @Data
    @AllArgsConstructor
    class RoundPeg {
        private double radius;

        protected RoundPeg() {
        }
    }

    @Data
    @AllArgsConstructor
    class SquarePeg {
        private double width;
    }

    @Data
    @AllArgsConstructor
    class SquarePegAdapter extends RoundPeg {
        private SquarePeg peg;

        @Override
        public double getRadius() {
            return (Math.sqrt(Math.pow((peg.getWidth() / 2), 2) * 2));
        }
    }
}
