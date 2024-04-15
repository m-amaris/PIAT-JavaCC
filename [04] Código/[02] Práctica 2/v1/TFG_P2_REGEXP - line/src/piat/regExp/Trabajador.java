package piat.regExp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		// Abrir el fichero y si no hay errores, procesar cada l�nea invocando al m�todo
		// procesarLinea()
		try {
			BufferedReader brInput = new BufferedReader(new FileReader(fichero));
			try {
				String linea;
				while ((linea = brInput.readLine()) != null) {
					try {
						final Parser parser = new Parser(linea);
						parser.procesarLinea(
								lineasCorrectas, hmServidores, hmUsuarios,
								hmPatronesEstadisticasAgregadas, hmEstadisticasAgregadas);
					} catch (ParseException | TokenMgrException e) {
						lineasIncorrectas.incrementAndGet();
					}
				}
			} catch (Exception e) {
				System.err.println("ERROR (leyendo el fichero): " + e.getMessage());
			} finally {
				try {
					brInput.close();

				} catch (IOException e) {
					System.err.println("ERROR (cerrando el fichero)" + e.getMessage());
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("ERROR (fichero no encontrado): " + e.getMessage());
		}

		// System.out.println("["+Thread.currentThread().getName()+"] Termino");
		System.out.print(".");
		numTrabajadoresTerminados.incrementAndGet();
	}
}
