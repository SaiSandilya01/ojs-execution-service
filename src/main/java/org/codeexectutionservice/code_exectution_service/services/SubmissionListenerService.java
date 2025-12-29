package org.codeexectutionservice.code_exectution_service.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.codeexectutionservice.code_exectution_service.configurations.RabbitMQConnector;
import org.codeexectutionservice.code_exectution_service.models.SubmissionDAO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SubmissionListenerService {

  // private final SubmissionListenerService submissionListenerService;

  // public SubmissionListenerService(SubmissionListenerService
  // submissionListenerService){
  // this.submissionListenerService = submissionListenerService;
  // }

  @RabbitListener(queues = RabbitMQConnector.QUEUE)
  public void handleMessage(SubmissionDAO submissionDAO) {

    System.out.println(submissionDAO.getCode());
    exectuteSubmission(submissionDAO);

  }

  public void exectuteSubmission(SubmissionDAO submissionDAO) {
    try {
      Path workDir = Files.createTempDirectory("submission-" + submissionDAO.getSubmissionId());
      Path sourceFile = writeToPath(
          submissionDAO.getLanguage(), submissionDAO.getCode(), "Main", workDir);

      List<String> dockerCmd = buildDockerCommand(submissionDAO.getLanguage(), workDir, sourceFile);

      ProcessBuilder pb = new ProcessBuilder(dockerCmd);
      pb.redirectErrorStream(true);
      Process process = pb.start();

      boolean finished = process.waitFor(5, TimeUnit.SECONDS);
      if (!finished) {
        process.destroyForcibly();
      }
      int exitCode = process.exitValue();
      String output = new String(process.getInputStream().readAllBytes());

      System.out.println(output);

    } catch (Exception e) {
      System.out.println("Error executing the program");
    }
  }

  public Path writeToPath(String language, String code, String fileName, Path workDir) throws IOException {
    String name = switch (language) {
      case "JAVA" -> fileName + ".java";
      case "PYTHON" -> fileName + ".py";
      default -> fileName + ".txt";
    };

    Path file = workDir.resolve(name);
    Files.writeString(file, code);
    return file;
  }

  public List<String> buildDockerCommand(String language, Path workDir, Path sourceFile) {

    String fileName = sourceFile.getFileName().toString();

    if (language.equals("JAVA")) {
      String className = fileName.replace(".java", "");

      String workDirPath = workDir.toAbsolutePath().toString().replace("\\", "/");

      return List.of(
          "docker", "run",
          "--rm",
          "--network=none",
          "--cpus=1",
          "--memory=512m",
          "--pids-limit=64",
          "-v", workDirPath + ":/workdir",
          "-w", "/workdir",
          "eclipse-temurin:21-jdk",
          "bash", "-c",
          "javac " + fileName + " && java " + className);
    }

    return null;
  }

}
