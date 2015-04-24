package com.alejandrorg.nejmfb.core;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Logger.
 * 
 * @author alejandro
 *
 */
public class MyLogger {

	private BufferedWriter buffer;

	/**
	 * Constructor.
	 * 
	 * @throws Exception
	 *             Puede lanzar excepción.
	 */
	public MyLogger(boolean b) throws Exception {
		try {
			this.buffer = new BufferedWriter(new FileWriter(Constants.LOG_FILE, b));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para logear.
	 * 
	 * @param s
	 *            Recibe el mensaje.
	 */
	public void log(String s) {
		try {
			System.out.println(s);
			this.buffer.write(s);
			this.buffer.newLine();
			this.buffer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logError(String s) {
		s = "[ERROR] " + s;
		try {
			System.err.println(s);
			this.buffer.write(s);
			this.buffer.newLine();
			this.buffer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para logear e imprimir por pantalla.
	 * 
	 * @param s
	 *            Recibe el mensaje.
	 */
	public void logWithSystemOut(String s) {
		System.out.println(s);
		log(s);
	}

	public void close() {
		try {
			this.buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MyLogger createLogger(boolean append) {
		try {
			MyLogger logger = new MyLogger(append);
			return logger;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error creating logger: " + e.getMessage());
			return null;
		}
	}

}
