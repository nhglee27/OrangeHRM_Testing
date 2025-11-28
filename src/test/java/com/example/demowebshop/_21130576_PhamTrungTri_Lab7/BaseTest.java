package com.example.demowebshop._21130576_PhamTrungTri_Lab7;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class BaseTest {

    // Constants
    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static final String VIDEO_FOLDER_PATH = "src/test/java/com/example/demowebshop/_21130576_PhamTrungTri_Lab7/videos";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String FFMPEG_DOWNLOAD_URL = "https://ffmpeg.org/download.html";
    
    private static final int IMPLICIT_WAIT_SECONDS = 5;
    private static final int EXPLICIT_WAIT_SECONDS = 10;
    private static final int RECORDING_STOP_DELAY = 2000;
    private static final int FILE_WRITE_DELAY = 3000;
    
    private static final int VIDEO_DEPTH = 24;
    private static final int VIDEO_FRAME_RATE = 15;
    private static final float VIDEO_QUALITY = 0.7f;
    private static final int KEY_FRAME_INTERVAL = 15 * 60;
    private static final int BLACK_FRAME_RATE = 30;
    
    private static final String VIDEO_EXTENSION_AVI = ".avi";
    private static final String VIDEO_EXTENSION_MP4 = ".mp4";
    private static final int KB_DIVISOR = 1024;

    // Video conversion constants
    private static final String FFMPEG_CODEC_VIDEO = "libx264";
    private static final String FFMPEG_PRESET = "medium";
    private static final String FFMPEG_CRF = "23";
    private static final String FFMPEG_CODEC_AUDIO = "aac";
    private static final String FFMPEG_AUDIO_BITRATE = "128k";

    // Protected fields
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static String baseUrl = BASE_URL;
    
    // Private fields
    private static ScreenRecorder screenRecorder;
    private static File videoFolder;

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        initializeDriver();
        createVideoFolder();
        System.out.println("WebDriver started!");
    }

    private static void initializeDriver() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECONDS));
        wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
    }

    private static void createVideoFolder() {
        videoFolder = new File(VIDEO_FOLDER_PATH);
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
    }

    @BeforeEach
    void startRecording(TestInfo testInfo) {
        try {
            GraphicsConfiguration gc = getGraphicsConfiguration();
            Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            String testName = getTestName(testInfo);
            File videoFile = new File(videoFolder, testName + VIDEO_EXTENSION_AVI);

            screenRecorder = createScreenRecorder(gc, captureSize, videoFile);
            screenRecorder.start();
            System.out.println("Bắt đầu ghi video cho test: " + testInfo.getDisplayName());
        } catch (Exception e) {
            handleRecordingError("bắt đầu", e);
        }
    }

    private GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
    }

    private String getTestName(TestInfo testInfo) {
        return testInfo.getTestMethod().orElseThrow().getName();
    }

    private ScreenRecorder createScreenRecorder(GraphicsConfiguration gc, Rectangle captureSize, File videoFile) 
            throws java.io.IOException, java.awt.AWTException {
        return new ScreenRecorder(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_MJPG,
                        CompressorNameKey, ENCODING_AVI_MJPG,
                        DepthKey, VIDEO_DEPTH,
                        FrameRateKey, Rational.valueOf(VIDEO_FRAME_RATE),
                        QualityKey, VIDEO_QUALITY,
                        KeyFrameIntervalKey, KEY_FRAME_INTERVAL),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(BLACK_FRAME_RATE)),
                null, videoFile);
    }

    @AfterEach
    void stopRecording(TestInfo testInfo) {
        try {
            safeSleep(RECORDING_STOP_DELAY);
            
            if (screenRecorder != null) {
                screenRecorder.stop();
                safeSleep(FILE_WRITE_DELAY);
                
                String testName = getTestName(testInfo);
                File aviFile = new File(videoFolder, testName + VIDEO_EXTENSION_AVI);
                
                if (isValidVideoFile(aviFile)) {
                    logVideoInfo(aviFile, testName);
                    convertToMP4(aviFile, testName);
                } else {
                    System.err.println(" Cảnh báo: File video có thể chưa được tạo hoặc rỗng");
                }
                
                screenRecorder = null;
            }
        } catch (Exception e) {
            handleRecordingError("dừng", e);
        }
    }

    private void safeSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isValidVideoFile(File file) {
        return file.exists() && file.length() > 0;
    }

    private void logVideoInfo(File aviFile, String testName) {
        System.out.println(" Đã lưu video: " + testName + VIDEO_EXTENSION_AVI);
        System.out.println(" Thư mục: " + videoFolder.getAbsolutePath());
        System.out.println(" Kích thước: " + (aviFile.length() / KB_DIVISOR) + " KB");
    }

    private void handleRecordingError(String action, Exception e) {
        System.err.println(" Lỗi khi " + action + " ghi video: " + e.getMessage());
        e.printStackTrace();
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println(" WebDriver closed!");
        }
    }

    private void convertToMP4(File aviFile, String testName) {
        File mp4File = new File(videoFolder, testName + VIDEO_EXTENSION_MP4);
        
        if (!isFFmpegAvailable()) {
            printFFmpegNotFoundMessage();
            return;
        }
        
        try {
            List<String> command = buildFFmpegCommand(aviFile, mp4File);
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && isValidVideoFile(mp4File)) {
                logMP4ConversionSuccess(mp4File, testName);
            } else {
                System.err.println(" Lỗi khi convert sang MP4. File AVI vẫn có sẵn.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(" Lỗi khi chờ quá trình convert: " + e.getMessage());
        } catch (IOException e) {
            printFFmpegNotFoundMessage();
        } catch (Exception e) {
            System.err.println(" Lỗi khi convert video: " + e.getMessage());
        }
    }

    private boolean isFFmpegAvailable() {
        try {
            ProcessBuilder checkFFmpeg = new ProcessBuilder("ffmpeg", "-version");
            Process checkProcess = checkFFmpeg.start();
            int checkResult = checkProcess.waitFor();
            return checkResult == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void printFFmpegNotFoundMessage() {
        System.out.println(" FFmpeg không được tìm thấy. Video sẽ chỉ có định dạng AVI.");
        System.out.println(" Để convert sang MP4, vui lòng cài đặt FFmpeg từ: " + FFMPEG_DOWNLOAD_URL);
    }

    private List<String> buildFFmpegCommand(File aviFile, File mp4File) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(aviFile.getAbsolutePath());
        command.add("-c:v");
        command.add(FFMPEG_CODEC_VIDEO);
        command.add("-preset");
        command.add(FFMPEG_PRESET);
        command.add("-crf");
        command.add(FFMPEG_CRF);
        command.add("-c:a");
        command.add(FFMPEG_CODEC_AUDIO);
        command.add("-b:a");
        command.add(FFMPEG_AUDIO_BITRATE);
        command.add("-movflags");
        command.add("+faststart");
        command.add("-y");
        command.add(mp4File.getAbsolutePath());
        return command;
    }

    private void logMP4ConversionSuccess(File mp4File, String testName) {
        System.out.println(" Đã convert sang MP4: " + testName + VIDEO_EXTENSION_MP4);
        System.out.println(" Kích thước MP4: " + (mp4File.length() / KB_DIVISOR) + " KB");
        System.out.println(" Video MP4 sẵn sàng để tải lên Canva!");
    }

    protected void loginAsAdmin() {
        driver.get(baseUrl);
        
        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameField.sendKeys(ADMIN_USERNAME);
        passwordField.sendKeys(ADMIN_PASSWORD);
        loginButton.click();
    }
}
