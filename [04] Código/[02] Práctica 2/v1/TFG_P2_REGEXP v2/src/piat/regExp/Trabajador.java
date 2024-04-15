package piat.regExp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import piat.javacc.*;

/**
 * @author Miguel Amarís Martos 54022315
 */
public class Trabajador implements Runnable {

	private final File fichero;
	private final AtomicInteger lineasCorrectas, lineasIncorrectas;
	private final ConcurrentHashMap<String, String> hmServidores;
	private final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas;
	private final ConcurrentHashMap<String, Pattern> hmPatronesEstadisticasAgregadas;
	private final ConcurrentHashMap<String, AtomicInteger> hmUsuarios;
	private final AtomicInteger numTrabajadoresTerminados;

	public Trabajador(File fichero, AtomicInteger lineasCorrectas, AtomicInteger lineasIncorrectas,
			ConcurrentHashMap<String, String> hmServidores,
			ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas,
			ConcurrentHashMap<String, Pattern> hmPatronesEstadisticasAgregadas,
			ConcurrentHashMap<String, AtomicInteger> hmUsuarios, AtomicInteger numTrabajadoresTerminados) {
		this.fichero = fichero;
		this.lineasCorrectas = lineasCorrectas;
		this.lineasIncorrectas = lineasIncorrectas;
		this.hmServidores = hmServidores;
		this.hmEstadisticasAgregadas = hmEstadisticasAgregadas;
		this.hmPatronesEstadisticasAgregadas = hmPatronesEstadisticasAgregadas;
		this.hmUsuarios = hmUsuarios;
		this.numTrabajadoresTerminados = numTrabajadoresTerminados;
	}

	public void run() {
		Thread.currentThread().setName("Trabajador del fichero " + fichero.getName());
		Parser parser;
		try {
			parser = new Parser(new ParserTokenManager(new SimpleCharStream(
					new StreamProvider(new FileInputStream(fichero.getCanonicalPath()), "UTF-8"))));
			parser.parse(
					lineasCorrectas, lineasIncorrectas, hmServidores, hmUsuarios,
					hmPatronesEstadisticasAgregadas, hmEstadisticasAgregadas);
		} catch (IOException | ParseException e) {
			System.out.println("\u001B[31m"+"\n\n[Trabajador.java] Error detectado! Debería ser gestionado en el parser...\n\n"+ "\u001B[0m");
			e.printStackTrace();
		}

		// System.out.println("["+Thread.currentThread().getName()+"] Termino");
		System.out.print(".");
		numTrabajadoresTerminados.incrementAndGet();
	}
}
