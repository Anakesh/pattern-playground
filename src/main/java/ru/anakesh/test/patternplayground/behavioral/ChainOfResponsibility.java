package ru.anakesh.test.patternplayground.behavioral;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Цепочка обязанностей/CoR/ChainOfCommand</p>
 * <p>Поведенческий паттерн проектирования, который позволяет передавать запросы последовательно по цепочке обработчиков.
 * Каждый последующий обработчик решает, может ли он обработать запрос сам и стоит ли передавать запрос дальше по цепи.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>
 *             <p>Создайте интерфейс обработчика и опишите в нём основной метод обработки.</p>
 *             <p>Продумайте, в каком виде клиент должен передавать данные запроса в обработчик.
 *             Самый гибкий способ — превратить данные запроса в объект и передавать его целиком через параметры метода обработчика.</p>
 *         </li>
 *         <li>
 *             <p>Имеет смысл создать абстрактный базовый класс обработчиков, чтобы не дублировать реализацию метода получения следующего обработчика во всех конкретных обработчиках.</p>
 *             <p>Добавьте в базовый обработчик поле для хранения ссылки на следующий объект цепочки. Устанавливайте начальное значение этого поля через конструктор.
 *             Это сделает объекты обработчиков неизменяемыми. Но если программа предполагает динамическую перестройку цепочек, можете добавить и сеттер для поля.</p>
 *             <p>Реализуйте базовый метод обработки так, чтобы он перенаправлял запрос следующему объекту, проверив его наличие.
 *             Это позволит полностью скрыть поле-ссылку от подклассов, дав им возможность передавать запросы дальше по цепи, обращаясь к родительской реализации метода.</p>
 *         </li>
 *         <li>Один за другим создайте классы конкретных обработчиков и реализуйте в них методы обработки запросов. При получении запроса каждый обработчик должен решить:
 *             <ul>
 *                 <li>Может ли он обработать запрос или нет?</li>
 *                 <li>Следует ли передать запрос следующему обработчику или нет?</li>
 *             </ul>
 *         </li>
 *         <li>Клиент может собирать цепочку обработчиков самостоятельно, опираясь на свою бизнес-логику, либо получать уже готовые цепочки извне.
 *         В последнем случае цепочки собираются фабричными объектами, опираясь на конфигурацию приложения или параметры окружения.</li>
 *         <li>Клиент может посылать запросы любому обработчику в цепи, а не только первому.
 *         Запрос будет передаваться по цепочке до тех пор, пока какой-то обработчик не откажется передавать его дальше, либо когда будет достигнут конец цепи.</li>
 *         <li>Клиент должен знать о динамической природе цепочки и быть готов к таким случаям:
 *             <ul>
 *                 <li>Цепочка может состоять из единственного объекта.</li>
 *                 <li>Запросы могут не достигать конца цепи.</li>
 *                 <li>Запросы могут достигать конца, оставаясь необработанными.</li>
 *             </ul>
 *         </li>
 *     </ol>
 * </p>
 */
public class ChainOfResponsibility {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Server server;

    public static void main(String[] args) throws IOException {
        new ChainOfResponsibility().run();
    }

    private void run() throws IOException {
        init();
        boolean success;
        do {
            System.out.print("Enter email: ");
            String email = reader.readLine();
            System.out.print("Input password: ");
            String password = reader.readLine();
            success = server.logIn(email, password);
        } while (!success);
    }

    private void init() {
        server = new Server();
        server.register("admin@example.com", "admin_pass");
        server.register("user@example.com", "user_pass");

        // Проверки связаны в одну цепь. Клиент может строить различные цепи,
        // используя одни и те же компоненты.
        Middleware middleware = new ThrottlingMiddleware(2);
        middleware.linkWith(new UserExistsMiddleware(server))
                .linkWith(new RoleCheckMiddleware());

        // Сервер получает цепочку от клиентского кода.
        server.setMiddleware(middleware);
    }

    abstract class Middleware {
        private Middleware next;

        /**
         * Помогает строить цепь из объектов-проверок.
         */
        public Middleware linkWith(Middleware next) {
            this.next = next;
            return next;
        }

        /**
         * Подклассы реализуют в этом методе конкретные проверки.
         */
        public abstract boolean check(String email, String password);

        /**
         * Запускает проверку в следующем объекте или завершает проверку, если мы в
         * последнем элементе цепи.
         */
        protected boolean checkNext(String email, String password) {
            if (next == null) {
                return true;
            }
            return next.check(email, password);
        }
    }

    class ThrottlingMiddleware extends Middleware {
        private int requestPerMinute;
        private int request;
        private long currentTime;

        public ThrottlingMiddleware(int requestPerMinute) {
            this.requestPerMinute = requestPerMinute;
            this.currentTime = System.currentTimeMillis();
        }

        /**
         * Обратите внимание, вызов checkNext() можно вставить как в начале этого
         * метода, так и в середине или в конце.
         * <p>
         * Это даёт еще один уровень гибкости по сравнению с проверками в цикле.
         * Например, элемент цепи может пропустить все остальные проверки вперёд и
         * запустить свою проверку в конце.
         */
        public boolean check(String email, String password) {
            if (System.currentTimeMillis() > currentTime + 60_000) {
                request = 0;
                currentTime = System.currentTimeMillis();
            }

            request++;

            if (request > requestPerMinute) {
                System.out.println("Request limit exceeded!");
                Thread.currentThread().stop();
            }
            return checkNext(email, password);
        }
    }

    class UserExistsMiddleware extends Middleware {
        private Server server;

        public UserExistsMiddleware(Server server) {
            this.server = server;
        }

        public boolean check(String email, String password) {
            if (!server.hasEmail(email)) {
                System.out.println("This email is not registered!");
                return false;
            }
            if (!server.isValidPassword(email, password)) {
                System.out.println("Wrong password!");
                return false;
            }
            return checkNext(email, password);
        }
    }

    class RoleCheckMiddleware extends Middleware {
        public boolean check(String email, String password) {
            if (email.equals("admin@example.com")) {
                System.out.println("Hello, admin!");
                return true;
            }
            System.out.println("Hello, user!");
            return checkNext(email, password);
        }
    }

    class Server {
        private Map<String, String> users = new HashMap<>();
        private Middleware middleware;

        /**
         * Клиент подаёт готовую цепочку в сервер. Это увеличивает гибкость и
         * упрощает тестирование класса сервера.
         */
        public void setMiddleware(Middleware middleware) {
            this.middleware = middleware;
        }

        /**
         * Сервер получает email и пароль от клиента и запускает проверку
         * авторизации у цепочки.
         */
        public boolean logIn(String email, String password) {
            if (middleware.check(email, password)) {
                System.out.println("Authorization have been successful!");

                // Здесь должен быть какой-то полезный код, работающий для
                // авторизированных пользователей.

                return true;
            }
            return false;
        }

        public void register(String email, String password) {
            users.put(email, password);
        }

        public boolean hasEmail(String email) {
            return users.containsKey(email);
        }

        public boolean isValidPassword(String email, String password) {
            return users.get(email).equals(password);
        }
    }
}
