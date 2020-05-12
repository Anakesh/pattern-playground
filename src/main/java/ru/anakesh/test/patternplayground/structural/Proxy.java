package ru.anakesh.test.patternplayground.structural;

import java.util.HashMap;

/**
 * <p>Заместитель</p>
 * <p>Структурный паттерн проектирования, который позволяет подставлять вместо реальных объектов специальные объекты-заменители.
 * Эти объекты перехватывают вызовы к оригинальному объекту, позволяя сделать что-то до или после передачи вызова оригиналу.</p>
 * <br/>
 * <p>Шаги реализации:
 *     <ol>
 *         <li>Определите интерфейс, который бы сделал заместитель и оригинальный объект взаимозаменяемыми.</li>
 *         <li>Создайте класс заместителя. Он должен содержать ссылку на сервисный объект. Чаще всего, сервисный объект создаётся самим заместителем.
 *         В редких случаях заместитель получает готовый сервисный объект от клиента через конструктор.</li>
 *         <li>Реализуйте методы заместителя в зависимости от его предназначения.
 *         В большинстве случаев, проделав какую-то полезную работу, методы заместителя должны передать запрос сервисному объекту.</li>
 *         <li>Подумайте о введении фабрики, которая решала бы, какой из объектов создавать — заместитель или реальный сервисный объект.
 *         Но, с другой стороны, эта логика может быть помещена в создающий метод самого заместителя.</li>
 *         <li>Подумайте, не реализовать ли вам ленивую инициализацию сервисного объекта при первом обращении клиента к методам заместителя.</li>
 *     </ol>
 * </p>
 */
public class Proxy {
    public static void main(String[] args) {
        new Proxy().run();
    }

    private void run() {
        YoutubeDownloader naiveDownloader = new YoutubeDownloader(new ThirdPartyYoutubeClass());
        YoutubeDownloader smartDownloader = new YoutubeDownloader(new YoutubeCacheProxy());

        long naive = test(naiveDownloader);
        long smart = test(smartDownloader);
        System.out.print("Time saved by caching proxy: " + (naive - smart) + "ms");

    }

    private long test(YoutubeDownloader downloader) {
        long startTime = System.currentTimeMillis();

        // User behavior in our app:
        downloader.renderPopularVideos();
        downloader.renderVideoPage("catzzzzzzzzz");
        downloader.renderPopularVideos();
        downloader.renderVideoPage("dancesvideoo");
        // Users might visit the same page quite often.
        downloader.renderVideoPage("catzzzzzzzzz");
        downloader.renderVideoPage("someothervid");

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.print("Time elapsed: " + estimatedTime + "ms\n");
        return estimatedTime;
    }

    interface ThirdPartyYoutubeLib {
        HashMap<String, Video> popularVideos();

        Video getVideo(String videoId);
    }

    class ThirdPartyYoutubeClass implements ThirdPartyYoutubeLib {

        @Override
        public HashMap<String, Video> popularVideos() {
            connectToServer("http://www.youtube.com");
            return getRandomVideos();
        }

        @Override
        public Video getVideo(String videoId) {
            connectToServer("http://www.youtube.com/" + videoId);
            return getSomeVideo(videoId);
        }

        // -----------------------------------------------------------------------
        // Fake methods to simulate network activity. They as slow as a real life.

        private int random(int min, int max) {
            return min + (int) (Math.random() * ((max - min) + 1));
        }

        private void experienceNetworkLatency() {
            int randomLatency = random(5, 10);
            for (int i = 0; i < randomLatency; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void connectToServer(String server) {
            System.out.print("Connecting to " + server + "... ");
            experienceNetworkLatency();
            System.out.print("Connected!" + "\n");
        }

        private HashMap<String, Video> getRandomVideos() {
            System.out.print("Downloading populars... ");

            experienceNetworkLatency();
            HashMap<String, Video> hmap = new HashMap<String, Video>();
            hmap.put("catzzzzzzzzz", new Video("sadgahasgdas", "Catzzzz.avi"));
            hmap.put("mkafksangasj", new Video("mkafksangasj", "Dog play with ball.mp4"));
            hmap.put("dancesvideoo", new Video("asdfas3ffasd", "Dancing video.mpq"));
            hmap.put("dlsdk5jfslaf", new Video("dlsdk5jfslaf", "Barcelona vs RealM.mov"));
            hmap.put("3sdfgsd1j333", new Video("3sdfgsd1j333", "Programing lesson#1.avi"));

            System.out.print("Done!" + "\n");
            return hmap;
        }

        private Video getSomeVideo(String videoId) {
            System.out.print("Downloading video... ");

            experienceNetworkLatency();
            Video video = new Video(videoId, "Some video title");

            System.out.print("Done!" + "\n");
            return video;
        }

    }

    class Video {
        public String id;
        public String title;
        public String data;

        Video(String id, String title) {
            this.id = id;
            this.title = title;
            this.data = "Random video.";
        }
    }

    class YoutubeCacheProxy implements ThirdPartyYoutubeLib {
        private ThirdPartyYoutubeLib youtubeService;
        private HashMap<String, Video> cachePopular = new HashMap<String, Video>();
        private HashMap<String, Video> cacheAll = new HashMap<String, Video>();

        public YoutubeCacheProxy() {
            this.youtubeService = new ThirdPartyYoutubeClass();
        }

        @Override
        public HashMap<String, Video> popularVideos() {
            if (cachePopular.isEmpty()) {
                cachePopular = youtubeService.popularVideos();
            } else {
                System.out.println("Retrieved list from cache.");
            }
            return cachePopular;
        }

        @Override
        public Video getVideo(String videoId) {
            Video video = cacheAll.get(videoId);
            if (video == null) {
                video = youtubeService.getVideo(videoId);
                cacheAll.put(videoId, video);
            } else {
                System.out.println("Retrieved video '" + videoId + "' from cache.");
            }
            return video;
        }

        public void reset() {
            cachePopular.clear();
            cacheAll.clear();
        }
    }

    class YoutubeDownloader {
        private ThirdPartyYoutubeLib api;

        public YoutubeDownloader(ThirdPartyYoutubeLib api) {
            this.api = api;
        }

        public void renderVideoPage(String videoId) {
            Video video = api.getVideo(videoId);
            System.out.println("\n-------------------------------");
            System.out.println("Video page (imagine fancy HTML)");
            System.out.println("ID: " + video.id);
            System.out.println("Title: " + video.title);
            System.out.println("Video: " + video.data);
            System.out.println("-------------------------------\n");
        }

        public void renderPopularVideos() {
            HashMap<String, Video> list = api.popularVideos();
            System.out.println("\n-------------------------------");
            System.out.println("Most popular videos on Youtube (imagine fancy HTML)");
            for (Video video : list.values()) {
                System.out.println("ID: " + video.id + " / Title: " + video.title);
            }
            System.out.println("-------------------------------\n");
        }
    }

}
