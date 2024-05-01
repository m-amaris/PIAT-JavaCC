package piat.regExp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import piat.javacc.*;

public class P2_REGEXP {
	public static void main(String[] args) throws FileNotFoundException, IOException {

		final ConcurrentHashMap<String, String> hmServidores = new ConcurrentHashMap<String, String>();
		final ConcurrentHashMap<String, AtomicInteger> hmUsuarios = new ConcurrentHashMap<String, AtomicInteger>();
		final long tiempoComienzo = System.currentTimeMillis();

		try {

			final File[] ficheros = obtenerFicheros(new File(args[0]));
			for (File fichero : ficheros) {
				// System.out.print(fichero.getName());
				// ejecutor.execute(new Trabajador(fichero, pTraza, lineasCorrectas,
				// lineasIncorrectas, hmServidores,hmEstadisticasAgregadas,
				// hmPatronesEstadisticasAgregadas,hmUsuarios,numTrabajadoresTerminados));
				// final Parser parser = new Parser(new ParserTokenManager(new
				// SimpleCharStream(new StreamProvider(new FileInputStream(args[0]),
				// "UTF-8"))));
				final Parser parser = new Parser(new ParserTokenManager(new SimpleCharStream(
						new StreamProvider(new FileInputStream(fichero.getCanonicalPath()), "UTF-8"))));
				parser.procesarLinea(hmServidores, hmUsuarios);
				// break; // Descomentando este break, solo se ejecuta el primer trabajador
			}
			System.out.println(hmUsuarios);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		final long tiempoFinal = System.currentTimeMillis();
		System.out.println("\nTiempo de ejecucion: " + ((long) (tiempoFinal - tiempoComienzo) / 1000) + " segundos");
	}

	/**
	 * Devuelve un array de elementos File que se corresponden con los ficheros con
	 * extensi�n log encontrados en el directorio que se pasa como par�metro
	 *
	 * @param directorioFuente Directorio que contiene los ficheros de log
	 */
	private static File[] obtenerFicheros(File directorioFuente) {

		final File[] ficheros = directorioFuente.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".log")) {
					return true;
				}
				return false;
			}
		});
		return ficheros;
	}
}
