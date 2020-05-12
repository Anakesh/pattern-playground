package ru.anakesh.test.patternplayground.structural;

import java.io.File;

/**
 * <p>Фасад</p>
 * <p>Структурный паттерн проектирования, который предоставляет простой интерфейс к сложной системе классов, библиотеке или фреймворку</p>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Определите, можно ли создать более простой интерфейс, чем тот, который предоставляет сложная подсистема. Вы на правильном пути,
 *         если этот интерфейс избавит клиента от необходимости знать о подробностях подсистемы.</li>
 *         <li>Создайте класс фасада, реализующий этот интерфейс. Он должен переадресовывать вызовы клиента нужным объектам подсистемы.
 *         Фасад должен будет позаботиться о том, чтобы правильно инициализировать объекты подсистемы.</li>
 *         <li>Вы получите максимум пользы, если клиент будет работать только с фасадом.
 *         В этом случае изменения в подсистеме будут затрагивать только код фасада, а клиентский код останется рабочим.</li>
 *         <li>Если ответственность фасада начинает размываться, подумайте о введении дополнительных фасадов.</li>
 *     </ol>
 * </p>
 */
public class Facade {
    public static void main(String[] args) {
        new Facade().run();
    }

    private void run() {
        VideoConversionFacade converter = new VideoConversionFacade();
        File mp4Video = converter.convertVideo("youtubevideo.ogg", "mp4");
    }

    interface Codec {
    }

    class VideoFile {
        private String name;
        private String codecType;

        public VideoFile(String name) {
            this.name = name;
            this.codecType = name.substring(name.indexOf(".") + 1);
        }

        public String getCodecType() {
            return codecType;
        }

        public String getName() {
            return name;
        }
    }

    class MPEG4CompressionCodec implements Codec {
        public String type = "mp4";

    }

    class OggCompressionCodec implements Codec {
        public String type = "ogg";
    }

    class CodecFactory {
        public Codec extract(VideoFile file) {
            String type = file.getCodecType();
            if (type.equals("mp4")) {
                System.out.println("CodecFactory: extracting mpeg audio...");
                return new MPEG4CompressionCodec();
            } else {
                System.out.println("CodecFactory: extracting ogg audio...");
                return new OggCompressionCodec();
            }
        }
    }

    class BitrateReader {
        public VideoFile read(VideoFile file, Codec codec) {
            System.out.println("BitrateReader: reading file...");
            return file;
        }

        public VideoFile convert(VideoFile buffer, Codec codec) {
            System.out.println("BitrateReader: writing file...");
            return buffer;
        }
    }

    class AudioMixer {
        public File fix(VideoFile result) {
            System.out.println("AudioMixer: fixing audio...");
            return new File("tmp");
        }
    }

    class VideoConversionFacade {
        private CodecFactory codecFactory = new CodecFactory();
        private BitrateReader bitrateReader = new BitrateReader();

        public File convertVideo(String fileName, String format) {

            System.out.println("VideoConversionFacade: conversion started.");
            VideoFile file = new VideoFile(fileName);
            Codec sourceCodec = codecFactory.extract(file);
            Codec destinationCodec;
            if (format.equals("mp4")) {
                destinationCodec = new OggCompressionCodec();
            } else {
                destinationCodec = new MPEG4CompressionCodec();
            }
            VideoFile buffer = bitrateReader.read(file, sourceCodec);
            VideoFile intermediateResult = bitrateReader.convert(buffer, destinationCodec);
            File result = (new AudioMixer()).fix(intermediateResult);
            System.out.println("VideoConversionFacade: conversion completed.");
            return result;
        }
    }
}
