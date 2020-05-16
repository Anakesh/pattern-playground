package ru.anakesh.test.patternplayground.behavioral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Стратегия</p>
 * <p>Поведенческий паттерн проектирования, который определяет семейство схожих алгоритмов и помещает каждый из них в собственный класс,
 * после чего алгоритмы можно взаимозаменять прямо во время исполнения программы.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Определите алгоритм, который подвержен частым изменениям. Также подойдёт алгоритм, имеющий несколько вариаций, которые выбираются во время выполнения программы.</li>
 *         <li>Создайте интерфейс стратегий, описывающий этот алгоритм. Он должен быть общим для всех вариантов алгоритма.</li>
 *         <li>Поместите вариации алгоритма в собственные классы, которые реализуют этот интерфейс.</li>
 *         <li>В классе контекста создайте поле для хранения ссылки на текущий объект-стратегию, а также метод для её изменения.
 *         Убедитесь в том, что контекст работает с этим объектом только через общий интерфейс стратегий.</li>
 *         <li>Клиенты контекста должны подавать в него соответствующий объект-стратегию, когда хотят, чтобы контекст вёл себя определённым образом.</li>
 *     </ol>
 * </p>
 */
public class Strategy {
    private Map<Integer, Integer> priceOnProducts = new HashMap<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private Order order = new Order();
    private PayStrategy strategy;

    public static void main(String[] args) throws IOException {
        new Strategy().run();
    }

    private void run() throws IOException {
        priceOnProducts.put(1, 2200);
        priceOnProducts.put(2, 1850);
        priceOnProducts.put(3, 1100);
        priceOnProducts.put(4, 890);

        while (!order.isClosed()) {
            int cost;

            String continueChoice;
            do {
                System.out.print("Please, select a product:" + "\n" +
                        "1 - Mother board" + "\n" +
                        "2 - CPU" + "\n" +
                        "3 - HDD" + "\n" +
                        "4 - Memory" + "\n");
                int choice = Integer.parseInt(reader.readLine());
                cost = priceOnProducts.get(choice);
                System.out.print("Count: ");
                int count = Integer.parseInt(reader.readLine());
                order.setTotalCost(cost * count);
                System.out.print("Do you wish to continue selecting products? Y/N: ");
                continueChoice = reader.readLine();
            } while (continueChoice.equalsIgnoreCase("Y"));

            if (strategy == null) {
                System.out.println("Please, select a payment method:" + "\n" +
                        "1 - PalPay" + "\n" +
                        "2 - Credit Card");
                String paymentMethod = reader.readLine();

                // Клиент создаёт различные стратегии на основании
                // пользовательских данных, конфигурации и прочих параметров.
                if (paymentMethod.equals("1")) {
                    strategy = new PayByPayPal();
                } else {
                    strategy = new PayByCreditCard();
                }

                // Объект заказа делегирует сбор платёжных данны стратегии, т.к.
                // только стратегии знают какие данные им нужны для приёма
                // оплаты.
                order.processOrder(strategy);

                System.out.print("Pay " + order.getTotalCost() + " units or Continue shopping? P/C: ");
                String proceed = reader.readLine();
                if (proceed.equalsIgnoreCase("P")) {
                    // И наконец, стратегия запускает приём платежа.
                    if (strategy.pay(order.getTotalCost())) {
                        System.out.println("Payment has been successful.");
                    } else {
                        System.out.println("FAIL! Please, check your data.");
                    }
                    order.setClosed();
                }
            }
        }
    }

    interface PayStrategy {
        boolean pay(int paymentAmount);

        void collectPaymentDetails();
    }

    class PayByPayPal implements PayStrategy {
        private final Map<String, String> DATA_BASE = new HashMap<>();
        private final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
        private String email;
        private String password;
        private boolean signedIn;

        PayByPayPal() {
            DATA_BASE.put("amanda1985", "amanda@ya.com");
            DATA_BASE.put("qwerty", "john@amazon.eu");
        }

        /**
         * Собираем данные от клиента.
         */
        @Override
        public void collectPaymentDetails() {
            try {
                while (!signedIn) {
                    System.out.print("Enter the user's email: ");
                    email = READER.readLine();
                    System.out.print("Enter the password: ");
                    password = READER.readLine();
                    if (verify()) {
                        System.out.println("Data verification has been successful.");
                    } else {
                        System.out.println("Wrong email or password!");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private boolean verify() {
            setSignedIn(email.equals(DATA_BASE.get(password)));
            return signedIn;
        }

        /**
         * Если клиент уже вошел в систему, то для следующей оплаты данные вводить
         * не придется.
         */
        @Override
        public boolean pay(int paymentAmount) {
            if (signedIn) {
                System.out.println("Paying " + paymentAmount + " using PayPal.");
                return true;
            } else {
                return false;
            }
        }

        private void setSignedIn(boolean signedIn) {
            this.signedIn = signedIn;
        }
    }

    class PayByCreditCard implements PayStrategy {
        private final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
        private CreditCard card;

        /**
         * Собираем данные карты клиента.
         */
        @Override
        public void collectPaymentDetails() {
            try {
                System.out.print("Enter the card number: ");
                String number = READER.readLine();
                System.out.print("Enter the card expiration date 'mm/yy': ");
                String date = READER.readLine();
                System.out.print("Enter the CVV code: ");
                String cvv = READER.readLine();
                card = new CreditCard(number, date, cvv);

                // Валидируем номер карты...

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * После проверки карты мы можем совершить оплату. Если клиент продолжает
         * покупки, мы не запрашиваем карту заново.
         */
        @Override
        public boolean pay(int paymentAmount) {
            if (cardIsPresent()) {
                System.out.println("Paying " + paymentAmount + " using Credit Card.");
                card.setAmount(card.getAmount() - paymentAmount);
                return true;
            } else {
                return false;
            }
        }

        private boolean cardIsPresent() {
            return card != null;
        }
    }

    class CreditCard {
        private int amount;
        private String number;
        private String date;
        private String cvv;

        CreditCard(String number, String date, String cvv) {
            this.amount = 100_000;
            this.number = number;
            this.date = date;
            this.cvv = cvv;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    class Order {
        private int totalCost = 0;
        private boolean isClosed = false;

        public void processOrder(PayStrategy strategy) {
            strategy.collectPaymentDetails();
            // Здесь мы могли бы забрать и сохранить платежные данные из стратегии.
        }

        public int getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(int cost) {
            this.totalCost += cost;
        }

        public boolean isClosed() {
            return isClosed;
        }

        public void setClosed() {
            isClosed = true;
        }
    }

}
