package com.aiexpense.trackerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class TrackerBackEndApplication {

	public static void main(String[] args) {
		clearLogFile("src/main/java/com/aiexpense/trackerbackend/logging/app.log");
		SpringApplication.run(TrackerBackEndApplication.class, args);
		System.out.println("Hello....git ..");
	}

	private static void clearLogFile(String logFilePath) {
		try {
			// Convert relative path to absolute path
			Path absolutePath = Paths.get(logFilePath).toAbsolutePath();
			try (FileWriter fileWriter = new FileWriter(absolutePath.toString(), false)) {
				fileWriter.write("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
