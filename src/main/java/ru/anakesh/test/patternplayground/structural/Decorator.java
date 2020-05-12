package ru.anakesh.test.patternplayground.structural;

import java.io.*;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * <p>Декоратор</p>
 * <p>Структурный паттерн проектирования, который позволяет динамически добавлять объектам новую функциональность, оборачивая их в полезные «обёртки».</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Убедитесь, что в вашей задаче есть один основной компонент и несколько опциональных дополнений или надстроек над ним.</li>
 *         <li>Создайте интерфейс компонента, который описывал бы общие методы как для основного компонента, так и для его дополнений.</li>
 *         <li>Создайте класс конкретного компонента и поместите в него основную бизнес-логику.</li>
 *         <li>Создайте базовый класс декораторов. Он должен иметь поле для хранения ссылки на вложенный объект-компонент.
 *         Все методы базового декоратора должны делегировать действие вложенному объекту.</li>
 *         <li>И конкретный компонент, и базовый декоратор должны следовать одному и тому же интерфейсу компонента.</li>
 *         <li>Теперь создайте классы конкретных декораторов, наследуя их от базового декоратора.
 *         Конкретный декоратор должен выполнять свою добавочную функцию, а затем (или перед этим) вызывать эту же операцию обёрнутого объекта.</li>
 *         <li>Клиент берёт на себя ответственность за конфигурацию и порядок обёртывания объектов.</li>
 *     </ol>
 * </p>
 */
public class Decorator {
    public static void main(String[] args) {
        new Decorator().run();
    }

    private void run() {
        String salaryRecords = "Name,Salary\nJohn Smith,100000\nSteven Jobs,912000";
        File outputFile = new File("out/OutputDemo.txt");
        if (outputFile.getParentFile().exists() || outputFile.getParentFile().mkdirs()) {
            DataSource encoded =
                    new CompressionDecorator(
                            new EncryptionDecorator(
                                    new FileDataSource(outputFile.getAbsolutePath()))
                    );
            encoded.writeData(salaryRecords);
            DataSource plain = new FileDataSource(outputFile.getAbsolutePath());

            System.out.println("- Input ----------------");
            System.out.println(salaryRecords);
            System.out.println("- Encoded --------------");
            System.out.println(plain.readData());
            System.out.println("- Decoded --------------");
            System.out.println(encoded.readData());
        }
    }

    interface DataSource {
        void writeData(String data);

        String readData();
    }

    class FileDataSource implements DataSource {
        private String name;

        public FileDataSource(String name) {
            this.name = name;
        }

        @Override
        public void writeData(String data) {
            File file = new File(name);
            try (OutputStream fos = new FileOutputStream(file)) {
                fos.write(data.getBytes(), 0, data.length());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public String readData() {
            char[] buffer = null;
            File file = new File(name);
            try (FileReader reader = new FileReader(file)) {
                buffer = new char[(int) file.length()];
                reader.read(buffer);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return new String(buffer);
        }
    }

    class DataSourceDecorator implements DataSource {
        private DataSource wrappee;

        DataSourceDecorator(DataSource source) {
            this.wrappee = source;
        }

        @Override
        public void writeData(String data) {
            wrappee.writeData(data);
        }

        @Override
        public String readData() {
            return wrappee.readData();
        }
    }

    class EncryptionDecorator extends DataSourceDecorator {

        public EncryptionDecorator(DataSource source) {
            super(source);
        }

        @Override
        public void writeData(String data) {
            super.writeData(encode(data));
        }

        @Override
        public String readData() {
            return decode(super.readData());
        }

        private String encode(String data) {
            byte[] result = data.getBytes();
            for (int i = 0; i < result.length; i++) {
                result[i] += (byte) 1;
            }
            return Base64.getEncoder().encodeToString(result);
        }

        private String decode(String data) {
            byte[] result = Base64.getDecoder().decode(data);
            for (int i = 0; i < result.length; i++) {
                result[i] -= (byte) 1;
            }
            return new String(result);
        }
    }

    class CompressionDecorator extends DataSourceDecorator {
        private int compLevel = 6;

        public CompressionDecorator(DataSource source) {
            super(source);
        }

        public int getCompressionLevel() {
            return compLevel;
        }

        public void setCompressionLevel(int value) {
            compLevel = value;
        }

        @Override
        public void writeData(String data) {
            super.writeData(compress(data));
        }

        @Override
        public String readData() {
            return decompress(super.readData());
        }

        private String compress(String stringData) {
            byte[] data = stringData.getBytes();
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
                DeflaterOutputStream dos = new DeflaterOutputStream(bout, new Deflater(compLevel));
                dos.write(data);
                dos.close();
                bout.close();
                return Base64.getEncoder().encodeToString(bout.toByteArray());
            } catch (IOException ex) {
                return null;
            }
        }

        private String decompress(String stringData) {
            byte[] data = Base64.getDecoder().decode(stringData);
            try {
                InputStream in = new ByteArrayInputStream(data);
                InflaterInputStream iin = new InflaterInputStream(in);
                ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
                int b;
                while ((b = iin.read()) != -1) {
                    bout.write(b);
                }
                in.close();
                iin.close();
                bout.close();
                return new String(bout.toByteArray());
            } catch (IOException ex) {
                return null;
            }
        }
    }

}
