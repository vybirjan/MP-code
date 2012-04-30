package cz.cvut.fit.vybirjan.mp.encryptor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipInputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKeyImpl;

public class Main {

	// AAACmgAAAANBRVMqi4qyFEgssxYYPHRI+HjJ 666

	public static void printHelp() {
		System.out.println("Usage: java -jar encryptor.jar -[OPTIONS] [KEY] [SOURCE] [TARGET]");
		System.out.println("[KEY] - base64 encrypted key for ciphering algorithm");
		System.out.println("[SOURCE] - source file or directory");
		System.out.println("[TARGET] - target file or directory");
		System.out.println("Source and target must be either both files or both directories.");
		System.out.println("\nAvailable [OPTIONS] are:");
		System.out.println("  -d        Decrypt files. If not specified, files are encrypted using provided key.");
		System.out.println("  -f        Force all files to be encrypted as plain files.\n" +
				"            If not specified, files ending with .jar will be encrypted as JAR files.\n" +
				"            Only .class files are encrypted in JAR files.");
		System.out.println("  -g[TAG]  Generate encryption key with provided tag. If this option is specified,\n" +
				"            all other options and arguments are ignored and program generates. Tag must be integer.\n" +
				"            encryption key and prints it to output.");
		System.out.println("  -x        Overwrite source file. If specified, [TARGET] is ignored and all\n" +
				"            files are rewritten instead of being encrypted and copied.");
	}

	private static boolean decrypt = false;
	private static boolean plain = false;
	private static boolean overwrite = false;
	private static TaggedKey key = null;
	private static File in = null;
	private static File out = null;

	public static void main(String[] args) throws IOException {
		System.in.read();
		if (args.length == 0) {
			printHelp();
			System.exit(0);
		}

		if (args.length != 3 && args.length != 4 && !(args.length == 1 && args[0].startsWith("-") && args[0].indexOf('g') != -1)) {
			System.err.println("Invalid number of arguments");
			printHelp();
			System.exit(1);
		}

		if (args[0].startsWith("-")) {
			parseArgs(args[0].substring(1));
			key = FileEncryptor.deserializeKey(Utils.decode(args[1]));
			in = new File(args[2]);
			if (!overwrite) {
				if (args.length != 4) {
					System.err.println("Target file must be specified");
					System.exit(2);
				} else {
					out = new File(args[3]);
				}
			}
		} else {
			key = FileEncryptor.deserializeKey(Utils.decode(args[0]));
			in = new File(args[1]);
			out = new File(args[2]);
		}

		if (!in.exists()) {
			System.err.println(String.format("Source file '%s' not found", in.getAbsolutePath()));
			System.exit(3);
		}

		if (!overwrite) {
			if (!out.exists()) {
				try {
					if (!out.createNewFile()) {
						System.err.println(String.format("Failed to create target file '%s'\n", out.getAbsolutePath()));
						System.exit(3);
					}
				} catch (IOException e) {
					System.err.println(String.format("Failed to create target file '%s': %s\n", out.getAbsolutePath(), e.getMessage()));
					System.exit(3);
				}
			} else {
				System.err.println(String.format("Target file '%s' already exists", out.getAbsolutePath()));
				System.exit(3);
			}

			if ((in.isFile() && out.isDirectory()) || (in.isDirectory() && out.isFile())) {
				System.err.println("Source and target must be either both files or both directories.");
				System.exit(4);
			}

			if (in.equals(out)) {
				System.err.println("Source and target files must not be the same. Try using -x option.");
				System.exit(5);
			}
		}

		if (in.isFile()) {
			processFile(in, out);
		} else {
			processDir(in, out);
		}
	}

	private static void processDir(File dir, File targetPath) {
		long timeSum = 0;
		long sizeSum = 0;
		long totalTime = System.currentTimeMillis();
		int count = 0;

		for (File source : dir.listFiles()) {
			long time = System.currentTimeMillis();
			sizeSum += source.length();

			if (source.isFile()) {
				File target = prepareOutputFile(source, targetPath);
				processFile(source, target);
			} else {
				// processDir(source, targetPath == null ? null : new
				// File(targetPath, source.getName()));
			}

			time = System.currentTimeMillis() - time;

			timeSum += time;
			count++;
		}

		timeSum /= count;
		sizeSum /= count;

		long average = (sizeSum * 1000) / timeSum;
		totalTime = System.currentTimeMillis() - totalTime;
		System.out.println("Average: " + Utils.toHumanReadable(average) + "/s (" + average + ")");
		System.out.println("Total time: " + totalTime + "ms");
	}

	private static File prepareOutputFile(File input, File targetDir) {
		if (targetDir == null) {
			return null;
		}

		File target = new File(targetDir, input.getName());

		if (!targetDir.exists() && !targetDir.mkdirs()) {
			System.err.println("Failed to create directory '" + targetDir.getAbsolutePath() + "'");
			System.exit(6);
		}

		try {
			if (!target.createNewFile()) {
				System.err.println("Creating file failed");
				System.exit(6);
			}
		} catch (IOException e) {
			System.err.println("Creating file failed: " + e.getMessage());
			System.exit(6);
		}

		return target;
	}

	private static File createTempFile(File original) throws IOException {
		int tmpIndex = 1;
		while (true) {
			String tmpFileName = original.getAbsolutePath() + "." + tmpIndex + ".tmp";
			File f = new File(tmpFileName);
			if (!f.exists()) {
				if (f.createNewFile()) {
					return f;
				} else {
					throw new IOException("Failed to create file " + f.getAbsolutePath());
				}
			} else {
				tmpIndex++;
			}
		}
	}

	static byte[] readBuffer = new byte[10 * 1024 * 1024];
	static byte[] writeBuffer = new byte[10 * 1024 * 1024];

	private static void processFile(File in, File out) {
		System.out.format("Processing file '%s'\n", in.getAbsolutePath());
		long time = System.nanoTime();

		try {
			if (in.getName().toLowerCase().endsWith(".jar") && !plain) {
				readBuffer = Utils.ensureSize((int) in.length(), readBuffer);
				writeBuffer = Utils.ensureSize((int) (in.length() * 2), writeBuffer);

				int readFromFile = Utils.readFully(in, readBuffer);
				ByteArrayInputStream byteIn = new ByteArrayInputStream(readBuffer, 0, readFromFile);
				// just to read manifest
				JarInputStream inJar = new JarInputStream(byteIn, false);
				Manifest mf = inJar.getManifest();
				byteIn.reset();
				ZipInputStream inZip = new ZipInputStream(byteIn);

				int bytesWritten = 0;

				try {
					if (decrypt) {
						bytesWritten = FileEncryptor.decryptJarFileInMemory(inZip, mf, writeBuffer, key);
					} else {
						bytesWritten = FileEncryptor.encryptJarFileInMemory(inZip, mf, writeBuffer, key);
					}
				} finally {
					inJar.close();
				}
				// write either to out file or overwrite source
				if (out == null) {
					Utils.writeFully(in, writeBuffer, 0, bytesWritten);
				} else {
					Utils.writeFully(out, writeBuffer, 0, bytesWritten);
				}
			} else {
				if (out == null) {
					out = createTempFile(in);
				}

				if (decrypt) {
					FileEncryptor.decryptFile(in, out, key);
				} else {
					FileEncryptor.encryptFile(in, out, key);
				}

				if (!in.delete()) {
					throw new IOException("Failed to delete source file " + in.getAbsolutePath());
				}

				if (!out.renameTo(in)) {
					throw new IOException("Failed to rename tmp file to " + in.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			if (out != null) {
				out.delete();
			}

			System.err.format("Error processing file '%s': %s", in.getAbsolutePath(), e.getMessage());
			System.exit(7);
		}

		time = (System.nanoTime() - time) / 1000000;
		long speed = (in.length() * 1000) / time;

		System.out.format("Finished in %dms (%d B/s)\n", time, speed);
	}

	private static String generateKey(int tag) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecretKey key = keygen.generateKey();
			TaggedKeyImpl taggedKey = new TaggedKeyImpl(tag, key);
			return Utils.encode(FileEncryptor.serializeKey(taggedKey));
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Algorithm not available: " + e.getMessage());
			System.exit(8);
			return null;
		}
	}

	private static void parseArgs(String s) {
		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
				case 'd':
					decrypt = true;
					break;
				case 'f':
					plain = true;
					break;
				case 'g':
					if (i >= s.length() - 1) {
						System.err.println("Missing tag value");
						System.exit(2);
					}
					String tagStr = s.substring(i + 1);
					try {
						int tag = Integer.parseInt(tagStr);
						System.out.println(generateKey(tag));
						System.exit(0);
					} catch (NumberFormatException e) {
						System.err.println("Invalid tag value: " + tagStr);
						System.exit(2);
					}
					break;
				case 'x':
					overwrite = true;
					break;
				default:
					System.err.println("Unknown option '" + s.charAt(i) + "'");
					printHelp();
					System.exit(2);
			}
		}
	}
}
