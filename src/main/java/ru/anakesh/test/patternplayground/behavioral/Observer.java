package ru.anakesh.test.patternplayground.behavioral;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Наблюдатель/Издатель-Подписчик/Слушатель</p>
 * <p>Поведенческий паттерн проектирования, который создаёт механизм подписки, позволяющий одним объектам следить и реагировать на события, происходящие в других объектах.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Разбейте вашу функциональность на две части: независимое ядро и опциональные зависимые части. Независимое ядро станет издателем. Зависимые части станут подписчиками.</li>
 *         <li>Создайте интерфейс подписчиков. Обычно в нём достаточно определить единственный метод оповещения.</li>
 *         <li>Создайте интерфейс издателей и опишите в нём операции управления подпиской. Помните, что издатель должен работать только с общим интерфейсом подписчиков.</li>
 *         <li>
 *             <p>Вам нужно решить, куда поместить код ведения подписки, ведь он обычно бывает одинаков для всех типов издателей.
 *             Самый очевидный способ — вынести этот код в промежуточный абстрактный класс, от которого будут наследоваться все издатели.</p>
 *             <p>Но если вы интегрируете паттерн в существующие классы, то создать новый базовый класс может быть затруднительно.
 *             В этом случае вы можете поместить логику подписки во вспомогательный объект и делегировать ему работу из издателей.</p>
 *         </li>
 *         <li>Создайте классы конкретных издателей. Реализуйте их так, чтобы после каждого изменения состояния они отправляли оповещения всем своим подписчикам.</li>
 *         <li>
 *             <p>Реализуйте метод оповещения в конкретных подписчиках.
 *             Не забудьте предусмотреть параметры, через которые издатель мог бы отправлять какие-то данные, связанные с происшедшим событием.</p>
 *             <p>Возможен и другой вариант, когда подписчик, получив оповещение, сам возьмёт из объекта издателя нужные данные.
 *             Но в этом случае вы будете вынуждены привязать класс подписчика к конкретному классу издателя.</p>
 *         </li>
 *         <li>Клиент должен создавать необходимое количество объектов подписчиков и подписывать их у издателей.</li>
 *     </ol>
 * </p>
 */
public class Observer {
    public static void main(String[] args) {
        new Observer().run();
    }

    private void run() {
        Editor editor = new Editor();
        editor.events.subscribe("open", new LogOpenListener("/path/to/log/file.txt"));
        editor.events.subscribe("save", new EmailNotificationListener("admin@example.com"));

        try {
            editor.openFile("test.txt");
            editor.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    interface EventListener {
        void update(String eventType, File file);
    }

    class EventManager {
        Map<String, List<EventListener>> listeners = new HashMap<>();

        public EventManager(String... operations) {
            for (String operation : operations) {
                this.listeners.put(operation, new ArrayList<>());
            }
        }

        public void subscribe(String eventType, EventListener listener) {
            List<EventListener> users = listeners.get(eventType);
            users.add(listener);
        }

        public void unsubscribe(String eventType, EventListener listener) {
            List<EventListener> users = listeners.get(eventType);
            users.remove(listener);
        }

        public void notify(String eventType, File file) {
            List<EventListener> users = listeners.get(eventType);
            for (EventListener listener : users) {
                listener.update(eventType, file);
            }
        }
    }

    class Editor {
        public EventManager events;
        private File file;

        public Editor() {
            this.events = new EventManager("open", "save");
        }

        public void openFile(String filePath) {
            this.file = new File(filePath);
            events.notify("open", file);
        }

        public void saveFile() throws Exception {
            if (this.file != null) {
                events.notify("save", file);
            } else {
                throw new Exception("Please open a file first.");
            }
        }
    }

    class EmailNotificationListener implements EventListener {
        private String email;

        public EmailNotificationListener(String email) {
            this.email = email;
        }

        @Override
        public void update(String eventType, File file) {
            System.out.println("Email to " + email + ": Someone has performed " + eventType + " operation with the following file: " + file.getName());
        }
    }

    class LogOpenListener implements EventListener {
        private File log;

        public LogOpenListener(String fileName) {
            this.log = new File(fileName);
        }

        @Override
        public void update(String eventType, File file) {
            System.out.println("Save to log " + log + ": Someone has performed " + eventType + " operation with the following file: " + file.getName());
        }
    }


}
