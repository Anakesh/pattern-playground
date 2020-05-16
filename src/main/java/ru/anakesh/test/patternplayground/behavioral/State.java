package ru.anakesh.test.patternplayground.behavioral;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Состояние</p>
 * <p>Поведенческий паттерн проектирования, который позволяет объектам менять поведение в зависимости от своего состояния. Извне создаётся впечатление, что изменился класс объекта.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Определитесь с классом, который будет играть роль контекста.
 *         Это может быть как существующий класс, в котором уже есть зависимость от состояния, так и новый класс, если код состояний размазан по нескольким классам.</li>
 *         <li>Создайте общий интерфейс состояний. Он должен описывать методы, общие для всех состояний, обнаруженных в контексте.
 *         Заметьте, что не всё поведение контекста нужно переносить в состояние, а только то, которое зависит от состояний.</li>
 *         <li>
 *             <p>Для каждого фактического состояния создайте класс, реализующий интерфейс состояния. Переместите код, связанный с конкретными состояниями в нужные классы.
 *             В конце концов, все методы интерфейса состояния должны быть реализованы во всех классах состояний.</p>
 *             <p>При переносе поведения из контекста вы можете столкнуться с тем, что это поведение зависит от приватных полей или методов контекста,
 *             к которым нет доступа из объекта состояния. Существует парочка способов обойти эту проблему.</p>
 *             <p>Самый простой — оставить поведение внутри контекста, вызывая его из объекта состояния. С другой стороны, вы можете сделать классы состояний вложенными в класс контекста,
 *             и тогда они получат доступ ко всем приватным частям контекста. Но последний способ доступен только в некоторых языках программирования (например, Java, C#).</p>
 *         </li>
 *         <li>Создайте в контексте поле для хранения объектов-состояний, а также публичный метод для изменения значения этого поля.</li>
 *         <li>Старые методы контекста, в которых находился зависимый от состояния код, замените на вызовы соответствующих методов объекта-состояния.</li>
 *         <li>В зависимости от бизнес-логики, разместите код, который переключает состояние контекста либо внутри контекста, либо внутри классов конкретных состояний.</li>
 *     </ol>
 * </p>
 */
public class State {

    public static void main(String[] args) {
        new State().run();
    }

    private void run() {
        Player player = new Player();
        UI ui = new UI(player);
        ui.init();
    }

    abstract class PlayerState {
        Player player;

        /**
         * Контекст передаёт себя в конструктор состояния, чтобы состояние могло
         * обращаться к его данным и методам в будущем, если потребуется.
         */
        PlayerState(Player player) {
            this.player = player;
        }

        public abstract String onLock();

        public abstract String onPlay();

        public abstract String onNext();

        public abstract String onPrevious();
    }

    class LockedState extends PlayerState {

        LockedState(Player player) {
            super(player);
            player.setPlaying(false);
        }

        @Override
        public String onLock() {
            if (player.isPlaying()) {
                player.changeState(new ReadyState(player));
                return "Stop playing";
            } else {
                return "Locked...";
            }
        }

        @Override
        public String onPlay() {
            player.changeState(new ReadyState(player));
            return "Ready";
        }

        @Override
        public String onNext() {
            return "Locked...";
        }

        @Override
        public String onPrevious() {
            return "Locked...";
        }
    }

    class ReadyState extends PlayerState {

        public ReadyState(Player player) {
            super(player);
        }

        @Override
        public String onLock() {
            player.changeState(new LockedState(player));
            return "Locked...";
        }

        @Override
        public String onPlay() {
            String action = player.startPlayback();
            player.changeState(new PlayingState(player));
            return action;
        }

        @Override
        public String onNext() {
            return "Locked...";
        }

        @Override
        public String onPrevious() {
            return "Locked...";
        }
    }

    class PlayingState extends PlayerState {

        PlayingState(Player player) {
            super(player);
        }

        @Override
        public String onLock() {
            player.changeState(new LockedState(player));
            player.setCurrentTrackAfterStop();
            return "Stop playing";
        }

        @Override
        public String onPlay() {
            player.changeState(new ReadyState(player));
            return "Paused...";
        }

        @Override
        public String onNext() {
            return player.nextTrack();
        }

        @Override
        public String onPrevious() {
            return player.previousTrack();
        }
    }

    class Player {
        private PlayerState state;
        private boolean playing = false;
        private List<String> playlist = new ArrayList<>();
        private int currentTrack = 0;

        public Player() {
            this.state = new ReadyState(this);
            setPlaying(true);
            for (int i = 1; i <= 12; i++) {
                playlist.add("Track " + i);
            }
        }

        public void changeState(PlayerState state) {
            this.state = state;
        }

        public PlayerState getState() {
            return state;
        }

        public boolean isPlaying() {
            return playing;
        }

        public void setPlaying(boolean playing) {
            this.playing = playing;
        }

        public String startPlayback() {
            return "Playing " + playlist.get(currentTrack);
        }

        public String nextTrack() {
            currentTrack++;
            if (currentTrack > playlist.size() - 1) {
                currentTrack = 0;
            }
            return "Playing " + playlist.get(currentTrack);
        }

        public String previousTrack() {
            currentTrack--;
            if (currentTrack < 0) {
                currentTrack = playlist.size() - 1;
            }
            return "Playing " + playlist.get(currentTrack);
        }

        public void setCurrentTrackAfterStop() {
            this.currentTrack = 0;
        }
    }

    class UI {
        private Player player;
        private JTextField textField = new JTextField();

        public UI(Player player) {
            this.player = player;
        }

        public void init() {
            JFrame frame = new JFrame("Test player");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel context = new JPanel();
            context.setLayout(new BoxLayout(context, BoxLayout.Y_AXIS));
            frame.getContentPane().add(context);
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            context.add(textField);
            context.add(buttons);

            // Контекст заставляет состояние реагировать на пользовательский ввод
            // вместо себя. Реакция может быть разной в зависимости от того, какое
            // состояние сейчас активно.
            JButton play = new JButton("Play");
            play.addActionListener(e -> textField.setText(player.getState().onPlay()));
            JButton stop = new JButton("Stop");
            stop.addActionListener(e -> textField.setText(player.getState().onLock()));
            JButton next = new JButton("Next");
            next.addActionListener(e -> textField.setText(player.getState().onNext()));
            JButton prev = new JButton("Prev");
            prev.addActionListener(e -> textField.setText(player.getState().onPrevious()));
            frame.setVisible(true);
            frame.setSize(300, 100);
            buttons.add(play);
            buttons.add(stop);
            buttons.add(next);
            buttons.add(prev);
        }
    }
}
