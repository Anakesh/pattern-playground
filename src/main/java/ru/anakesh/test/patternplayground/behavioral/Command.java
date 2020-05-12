package ru.anakesh.test.patternplayground.behavioral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * <p>Команда/Действие/Транзакция/Action</p>
 * <p>Поведенческий паттерн проектирования, который превращает запросы в объекты, позволяя передавать их как аргументы при вызове методов,
 * ставить запросы в очередь, логировать их, а также поддерживать отмену операций.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Создайте общий интерфейс команд и определите в нём метод запуска.</li>
 *         <li>
 *             <p>Один за другим создайте классы конкретных команд.
 *             В каждом классе должно быть поле для хранения ссылки на один или несколько объектов-получателей, которым команда будет перенаправлять основную работу.</p>
 *             <p>Кроме этого, команда должна иметь поля для хранения параметров, которые нужны при вызове методов получателя.
 *             Значения всех этих полей команда должна получать через конструктор.</p>
 *             <p>И, наконец, реализуйте основной метод команды, вызывая в нём те или иные методы получателя.</p>
 *         </li>
 *         <li>Добавьте в классы отправителей поля для хранения команд.
 *         Обычно объекты-отправители принимают готовые объекты команд извне — через конструктор либо через сеттер поля команды.</li>
 *         <li>Измените основной код отправителей так, чтобы они делегировали выполнение действия команде.</li>
 *         <li>Порядок инициализации объектов должен выглядеть так:
 *             <ul>
 *                 <li>Создаём объекты получателей.</li>
 *                 <li>Создаём объекты команд, связав их с получателями.</li>
 *                 <li>Создаём объекты отправителей, связав их с командами.</li>
 *             </ul>
 *         </li>
 *     </ul>
 * </p>
 */
public class Command {
    public static void main(String[] args) {
        new Command().run();
    }

    private void run() {
        Editor editor = new Editor();
        editor.init();
    }

    abstract class Com {
        public Editor editor;
        private String backup;

        Com(Editor editor) {
            this.editor = editor;
        }

        void backup() {
            backup = editor.textField.getText();
        }

        public void undo() {
            editor.textField.setText(backup);
        }

        public abstract boolean execute();
    }

    class CopyCommand extends Com {

        public CopyCommand(Editor editor) {
            super(editor);
        }

        @Override
        public boolean execute() {
            editor.clipboard = editor.textField.getSelectedText();
            return false;
        }
    }

    class PasteCommand extends Com {

        public PasteCommand(Editor editor) {
            super(editor);
        }

        @Override
        public boolean execute() {
            if (editor.clipboard == null || editor.clipboard.isEmpty()) return false;

            backup();
            editor.textField.insert(editor.clipboard, editor.textField.getCaretPosition());
            return true;
        }
    }

    class CutCommand extends Com {

        public CutCommand(Editor editor) {
            super(editor);
        }

        @Override
        public boolean execute() {
            if (editor.textField.getSelectedText() == null || editor.textField.getSelectedText().isEmpty())
                return false;

            backup();
            String source = editor.textField.getText();
            editor.clipboard = editor.textField.getSelectedText();
            editor.textField.setText(cutString(source));
            return true;
        }

        private String cutString(String source) {
            String start = source.substring(0, editor.textField.getSelectionStart());
            String end = source.substring(editor.textField.getSelectionEnd());
            return start + end;
        }
    }

    class CommandHistory {
        private Stack<Com> history = new Stack<>();

        public void push(Com c) {
            history.push(c);
        }

        public Com pop() {
            return history.pop();
        }

        public boolean isEmpty() {
            return history.isEmpty();
        }
    }

    class Editor {
        public JTextArea textField;
        public String clipboard;
        private CommandHistory history = new CommandHistory();

        public void init() {
            JFrame frame = new JFrame("Text editor (type & use buttons, Luke!)");
            JPanel content = new JPanel();
            frame.setContentPane(content);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            textField = new JTextArea();
            textField.setLineWrap(true);
            content.add(textField);
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton ctrlC = new JButton("Ctrl+C");
            JButton ctrlX = new JButton("Ctrl+X");
            JButton ctrlV = new JButton("Ctrl+V");
            JButton ctrlZ = new JButton("Ctrl+Z");
            Editor editor = this;
            ctrlC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    executeCommand(new CopyCommand(editor));
                }
            });
            ctrlX.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    executeCommand(new CutCommand(editor));
                }
            });
            ctrlV.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    executeCommand(new PasteCommand(editor));
                }
            });
            ctrlZ.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    undo();
                }
            });
            buttons.add(ctrlC);
            buttons.add(ctrlX);
            buttons.add(ctrlV);
            buttons.add(ctrlZ);
            content.add(buttons);
            frame.setSize(450, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private void executeCommand(Com command) {
            if (command.execute()) {
                history.push(command);
            }
        }

        private void undo() {
            if (history.isEmpty()) return;

            Com command = history.pop();
            if (command != null) {
                command.undo();
            }
        }
    }


}
