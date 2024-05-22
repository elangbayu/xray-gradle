package io.github.elangbayu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.junit.Rule;
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class XrayPluginTest {
  private static final Dotenv env = Dotenv.load();
  @Rule
  public static TemporaryFolder temporaryFolder = new TemporaryFolder();
  private static Project project;

  @BeforeAll
  public static void setUp() throws IOException {
    project = ProjectBuilder.builder().build();
    project.getPlugins().apply("xray-gradle");

    temporaryFolder.create();
  }

  @Test
  void testPluginExtension() {
    XrayExtension extension = project.getExtensions().findByType(XrayExtension.class);
    assertNotNull(extension);

    extension.setAction("download");
    extension.setScenario("ATI-988");

    assertEquals("download", extension.getAction());
    assertEquals("ATI-988", extension.getScenario());
  }

  @Test
  void testXrayTask() throws IOException {
    XrayExtension extension = project.getExtensions().findByType(XrayExtension.class);
    assert extension != null;
    extension.setAction("download");
    extension.setScenario("ATI-988");

    XrayTask task = (XrayTask) project.getTasks().findByName("xray");
    assertNotNull(task);

    File testDir = temporaryFolder.newFolder();
    project.setBuildDir(testDir);

    System.setProperty("XRAY_URL", env.get("XRAY_URL"));
    System.setProperty("XRAY_CLIENT_ID", env.get("XRAY_CLIENT_ID"));
    System.setProperty("XRAY_CLIENT_SECRET", env.get("XRAY_CLIENT_SECRET"));

    task.run();

    Path featuresDir = Paths.get("src/test/resources/features");
    assertTrue(Files.exists(featuresDir));
    try (Stream<Path> listFile = Files.list(featuresDir)) {
      assertTrue(listFile.anyMatch(file -> file.getFileName().toString().endsWith(".feature")));
    }
  }

  @Test
  void testUploadResult() throws IOException {
    XrayExtension extension = project.getExtensions().findByType(XrayExtension.class);
    assert extension != null;
    extension.setAction("upload");
    extension.setScenario("cucumber.json");

    XrayTask task = (XrayTask) project.getTasks().findByName("xray");
    assertNotNull(task);

    File testDir = temporaryFolder.newFolder();
    project.setBuildDir(testDir);

    System.setProperty("XRAY_URL", env.get("XRAY_URL"));
    System.setProperty("XRAY_CLIENT_ID", env.get("XRAY_CLIENT_ID"));
    System.setProperty("XRAY_CLIENT_SECRET", env.get("XRAY_CLIENT_SECRET"));
    System.setProperty("XRAY_PROJECT_KEY", env.get("XRAY_PROJECT_KEY"));

    task.run();
  }
}
