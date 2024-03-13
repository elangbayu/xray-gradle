package io.github.elangbayu;

import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.path.json.JsonPath;
import net.lingala.zip4j.ZipFile;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Main class for the XRAY utility.
 */
public class XrayMain {
    private static final String FEATURE_ZIP = "src/test/resources/features/%s.zip";
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build();
    private static final Dotenv env = Dotenv.load();
    private static final String XRAY_URL = env.get("XRAY_URL");

    /**
     * Main method to run the XRAY utility.
     *
     * @param args Command-line arguments.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        String action = args[0];
        String scenario = args[1];
        switch (action) {
            case "download" -> downloadXrayScenario(scenario);
            case "upload" -> uploadXrayScenario(scenario);
            default -> System.err.println("Invalid argument: " + action);
        }
    }

    /**
     * Retrieves the XRAY authentication token.
     *
     * @return The XRAY authentication token.
     * @throws IOException If an I/O error occurs.
     */
    private static String getXrayToken() throws IOException {
        JsonObject credentials = new JsonObject();
        credentials.addProperty("client_id", env.get("XRAY_CLIENT_ID"));
        credentials.addProperty("client_secret", env.get("XRAY_CLIENT_SECRET"));
        RequestBody body = RequestBody.create(credentials.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(XRAY_URL + "/authenticate")
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string().replace("\"", "");
        }
    }

    /**
     * Downloads test scenarios from XRAY based on the specified tag.
     *
     * @param tag The tag to filter test scenarios.
     * @throws IOException If an I/O error occurs.
     */
    private static void downloadXrayScenario(String tag) throws IOException {
        System.out.println("Downloading scenarios with tag " + tag);
        Files.createDirectories(Paths.get("src/test/resources/features"));
        String token = getXrayToken();
        Request request = new Request.Builder()
                .url(XRAY_URL + "/export/cucumber?keys=" + tag)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                InputStream inputStream = response.body().byteStream();
                File destinationFile = new File(String.format(FEATURE_ZIP, tag));
                writeBuffertoFile(inputStream, destinationFile);
                inputStream.close();
                unzipFeatureFile(tag);
                Files.deleteIfExists(Paths.get(String.format(FEATURE_ZIP, tag)));
                System.out.println("File downloaded successfully: " + destinationFile.getAbsolutePath());
            } else {
                System.out.println("Download failed: " + response.code());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Uploads test execution results to XRAY.
     *
     * @param filename The name of the file containing test execution results.
     * @throws IOException If an I/O error occurs.
     */
    private static void uploadXrayScenario(String filename) throws IOException {
        System.out.println("Uploading scenarios from file " + filename);
        File file = new File("src/test/resources/features/" + filename);
        String token = getXrayToken();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename,
                        RequestBody.create(file, MediaType.parse("text/plain")))
                .build();
        Request request = new Request.Builder()
                .url(XRAY_URL + "/import/feature?projectKey=" + env.get("XRAY_PROJECT_KEY"))
                .header("Content-Type", "multipart/form-data")
                .header("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                assert response.body() != null;
                JsonPath jsonPath = new JsonPath(response.body().string());
                List<Map<String, String>> tests = new ArrayList<>(jsonPath.getList("updatedOrCreatedTests"));
                System.out.println("Successfully update or create below tests:");
                for (Map<String, String> test : tests) {
                    System.out.println("- " + test.get("key"));
                }
            }
        }
    }

    /**
     * Unzips the feature file with the specified filename.
     *
     * @param filename The name of the feature file to unzip.
     */
    private static void unzipFeatureFile(String filename) {
        try (ZipFile zip = new ZipFile(String.format(FEATURE_ZIP, filename))) {
            zip.extractAll("src/test/resources/features");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Writes the contents of the input stream to the specified file.
     *
     * @param inputStream     The input stream to read from.
     * @param destinationFile The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeBuffertoFile(InputStream inputStream, File destinationFile) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
        bufferedInputStream.close();
    }
}
