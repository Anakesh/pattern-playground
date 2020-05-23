package ru.anakesh.test.patternplayground.behavioral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>Шаблонный метод</p>
 * <p>Поведенческий паттерн проектирования, который определяет скелет алгоритма, перекладывая ответственность за некоторые его шаги на подклассы.
 * Паттерн позволяет подклассам переопределять шаги алгоритма, не меняя его общей структуры.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Изучите алгоритм и подумайте, можно ли его разбить на шаги. Прикиньте, какие шаги будут стандартными для всех вариаций алгоритма, а какие — изменяющимися.</li>
 *         <li>Создайте абстрактный базовый класс. Определите в нём шаблонный метод. Этот метод должен состоять из вызовов шагов алгоритма.
 *         Имеет смысл сделать шаблонный метод финальным, чтобы подклассы не могли переопределить его (если ваш язык программирования это позволяет).</li>
 *         <li>Добавьте в абстрактный класс методы для каждого из шагов алгоритма. Вы можете сделать эти методы абстрактными или добавить какую-то реализацию по умолчанию.
 *         В первом случае все подклассы должны будут реализовать эти методы, а во втором — только если реализация шага в подклассе отличается от стандартной версии.</li>
 *         <li>Подумайте о введении в алгоритм хуков. Чаще всего, хуки располагают между основными шагами алгоритма, а также до и после всех шагов.</li>
 *         <li>Создайте конкретные классы, унаследовав их от абстрактного класса. Реализуйте в них все недостающие шаги и хуки.</li>
 *     </ol>
 * </p>
 */
public class TemplateMethod {
    public static void main(String[] args) throws IOException {
        new TemplateMethod().run();
    }

    private void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Network network = null;
        System.out.print("Input user name: ");
        String userName = reader.readLine();
        System.out.print("Input password: ");
        String password = reader.readLine();

        // Вводим сообщение.
        System.out.print("Input message: ");
        String message = reader.readLine();

        System.out.println("\nChoose social network for posting message.\n" +
                "1 - Facebook\n" +
                "2 - Twitter");
        int choice = Integer.parseInt(reader.readLine());

        // Создаем сетевые объекты и публикуем пост.
        if (choice == 1) {
            network = new Facebook(userName, password);
        } else if (choice == 2) {
            network = new Twitter(userName, password);
        }
        network.post(message);
    }

    abstract class Network {
        String userName;
        String password;

        Network() {
        }

        /**
         * Публикация данных в любой сети.
         */
        public boolean post(String message) {
            // Проверка данных пользователя перед постом в соцсеть. Каждая сеть для
            // проверки использует разные методы.
            if (logIn(this.userName, this.password)) {
                // Отправка данных.
                boolean result = sendData(message.getBytes());
                logOut();
                return result;
            }
            return false;
        }

        abstract boolean logIn(String userName, String password);

        abstract boolean sendData(byte[] data);

        abstract void logOut();
    }

    class Facebook extends Network {
        public Facebook(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public boolean logIn(String userName, String password) {
            System.out.println("\nChecking user's parameters");
            System.out.println("Name: " + this.userName);
            System.out.print("Password: ");
            for (int i = 0; i < this.password.length(); i++) {
                System.out.print("*");
            }
            simulateNetworkLatency();
            System.out.println("\n\nLogIn success on Facebook");
            return true;
        }

        public boolean sendData(byte[] data) {
            boolean messagePosted = true;
            if (messagePosted) {
                System.out.println("Message: '" + new String(data) + "' was posted on Facebook");
                return true;
            } else {
                return false;
            }
        }

        public void logOut() {
            System.out.println("User: '" + userName + "' was logged out from Facebook");
        }

        private void simulateNetworkLatency() {
            try {
                int i = 0;
                System.out.println();
                while (i < 10) {
                    System.out.print(".");
                    Thread.sleep(500);
                    i++;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    class Twitter extends Network {

        public Twitter(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public boolean logIn(String userName, String password) {
            System.out.println("\nChecking user's parameters");
            System.out.println("Name: " + this.userName);
            System.out.print("Password: ");
            for (int i = 0; i < this.password.length(); i++) {
                System.out.print("*");
            }
            simulateNetworkLatency();
            System.out.println("\n\nLogIn success on Twitter");
            return true;
        }

        public boolean sendData(byte[] data) {
            boolean messagePosted = true;
            if (messagePosted) {
                System.out.println("Message: '" + new String(data) + "' was posted on Twitter");
                return true;
            } else {
                return false;
            }
        }

        public void logOut() {
            System.out.println("User: '" + userName + "' was logged out from Twitter");
        }

        private void simulateNetworkLatency() {
            try {
                int i = 0;
                System.out.println();
                while (i < 10) {
                    System.out.print(".");
                    Thread.sleep(500);
                    i++;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
