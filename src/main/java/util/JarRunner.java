package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Used to run a jar with commands and return the output from the jar as a string
 * @author echic
 *
 */
public class JarRunner {

	public String outputFromJar(String jar, String[] additionalArgs) {
		StringBuilder ret = new StringBuilder();

		// Create a new array to hold the complete command
		String[] command = new String[2 + additionalArgs.length];
		command[0] = "java";
		command[1] = "-jar";
		System.arraycopy(additionalArgs, 0, command, 2, additionalArgs.length);

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);

			Process process = processBuilder.start();

			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = reader.readLine()) != null) {
				// Process each line of output
				ret.append(line);
			}

			reader.close();
			int exitCode = process.waitFor();

			System.out.println("External process exited with code: " + exitCode);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return ret.toString();
	}
}
