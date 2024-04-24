package piat.regExp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author Miguel Amarís Martos 54022315
 */
public class EstadisticasLog {

	private final static int numMaxMsg = 500; // Num maximo de mensajes enviados por un usuario para mostrarlo en las
												// estadisticas

	/* Patrones que se usan en las estadisticas agregadas */
	private final static String msgIn = ".*smtp-in.*connect from.*"; // Los mensajes que pasan al siguiente servidor son
																		// los que tienen la palabra SEC-PASSED en la
																		// traza
	private final static String msgOut = ".*smtp-out.*delivered.*"; // Los mensajes bloqueados son los que tienen la
																	// palabra SEC-BLOCKED en la traza
	private final static String msgInfected = ".*security.*INFECTED.*"; // Los mensajes bloqueados son los que tienen la
																		// palabra SEC-BLOCKED en la traza
	private final static String msgSPAM = ".*SPAM.*"; // Los mensajes que pasan al siguiente servidor son los que tienen
														// la palabra SEC-PASSED en la traza
	private final static String code511 = ".*5.1.1\\s\\(bad\\sdestination\\smailbox\\saddress\\)"; // Los mensajes
																									// bloqueados son
																									// los que tienen la
																									// palabra
																									// SEC-BLOCKED en la
																									// traza
	private final static String code432 = ".*4.3.2\\s\\(overload\\)*"; // Los mensajes que pasan al siguiente servidor
																		// son los que tienen la palabra SEC-PASSED en
																		// la traza

	public static void main(String[] args) throws InterruptedException {
		Thread.currentThread().setName("Principal");

		// Verificar que se pasa como argumento el directorio con los logs y obtenerlo
		File directorioFuente = validarArgumentos(args);

		// En este array bidimensional se almacenan los nombres de los estadisticos a
		// obtener y el patron para que luego sea mas facil recorrerlo
		// y meter sus valores en el mapa hmPatronesEstadisticasAgregadas
		final String[][] patronesEstadisticasAgregadas = {
				{ "msgIn", msgIn },
				{ "msgOut", msgOut },
				{ "msgInfected", msgInfected },
				{ "msgSPAM", msgSPAM },
				{ "Code 5.1.1", code511 },
				{ "Code 4.3.2", code432 }
		}; // Pattern.compile

		// Mapa donde se guarda como clave el nombre de la estadistica agregada, y como
		// valor, el patron para detectar el String de la traza que contiene ese
		// estadistico
		final ConcurrentHashMap<String, Pattern> hmPatronesEstadisticasAgregadas = new ConcurrentHashMap<String, Pattern>();

		// Guardar en el mapa hmPatronesEstadisticasAgregadas todos los patrones que se
		// usaran para las estadisticas agregadas.
		// Los patrones y el nombre del estadistico estan en el array bidimensional
		// patronesEstadisticasAgregadas
		Pattern patron;
		String estadistico;
		for (int i = 0; i < patronesEstadisticasAgregadas.length; i++) {
			estadistico = patronesEstadisticasAgregadas[i][0];
			patron = Pattern.compile(patronesEstadisticasAgregadas[i][1]);
			hmPatronesEstadisticasAgregadas.put(estadistico, patron);
		}

		// Mapa donde recoger los contadores de cada estadisticas agregada.
		// La clave indica el servidor, la fecha y el estadistico, por lo que estara
		// formado por una String con esta estructura: "<tipoServidor> <fecha>
		// <estadistico>"
		// El valor es el contador de las veces que se encuentra el estad�stico anterior
		final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas = new ConcurrentHashMap<String, AtomicInteger>();

		// Mapa donde guardar el nombre del servidor y el tipo de servidor
		final ConcurrentHashMap<String, String> hmServidores = new ConcurrentHashMap<String, String>();

		// Mapa donde se guarda como clave el nombre de un usuario y como valor un
		// contador, para poder guardar cuantos mensajes envian los usuarios del sistema
		final ConcurrentHashMap<String, AtomicInteger> hmUsuarios = new ConcurrentHashMap<String, AtomicInteger>();

		// Crear el patron que se usara para analizar una traza, y del que, ademas de
		// conocer si es una traza correcta, se pueden extraer ciertos valores mediante
		// los grupos

		/* Variables usadas para las estadisticas generales */
		int numeroFicherosProcesados;
		final AtomicInteger lineasCorrectas = new AtomicInteger(0);
		final AtomicInteger lineasIncorrectas = new AtomicInteger(0);

		/****** Comienzo del procesamiento ******/

		// Obtener los ficheros de log del directorio
		final File[] ficheros = obtenerFicheros(directorioFuente);

		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.print("\nLanzando hilos en los " + numDeNucleos + " nucleos disponibles de este ordenador ");

		// Crear un pool donde ejecutar los hilos. El pool tendra un tamaño del num de
		// nucleos del ordenador
		// por lo que nunca podra haber mas hilos que ese numero en ejecucion
		// simultanea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecucion, poner como
		// argumento un 1. Ira mucho mas lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

		final long tiempoComienzo = System.currentTimeMillis();
		final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
		int numTrabajadores = 0;
		numeroFicherosProcesados = 0;
		for (File fichero : ficheros) {
			System.out.print(".");
			ejecutor.execute(new Trabajador(fichero, lineasCorrectas, lineasIncorrectas, hmServidores,
					hmEstadisticasAgregadas, hmPatronesEstadisticasAgregadas, hmUsuarios, numTrabajadoresTerminados));
			numeroFicherosProcesados++;
			numTrabajadores++;
			// break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out
				.print("\nSe van a ejecutar " + numTrabajadores + " trabajadores en el pool. Esperar a que terminen ");

		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown(); // Cerrar el ejecutor cuando termine el ultimo trabajador

		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que
		// quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados = numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado " + terminados + ". Esperando a los " + (numTrabajadores - terminados)
					+ " que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los
		// creados
		System.out.println("\nYa han terminado los " + numTrabajadoresTerminados.get() + " trabajadores");

		/****** Mostrar las estadisticas ******/

		/* Estadisticas generales */
		System.out.println("\n\n  Estadisticas generales:");

		System.out.print("\t Servidores: ");

		// Crear un mapa temporal, llamado numServidores, que se usara para almacenar
		// como clave el tipo de servidor y como valor un contador de cuantos hay de ese
		// tipo en el mapa hmServidores
		HashMap<String, Integer> numServidores = new HashMap<String, Integer>();

		// Recorrer todas las entradas de hmServidores
		for (Map.Entry<String, String> entrada : hmServidores.entrySet()) {

			// Se intenta meter la clave de hmServidores en numServidores:
			// si no existe se añade con el contador 1
			// si ya existe se incrementa el contador en 1
			String nombreServidor = entrada.getValue();
			numServidores.put(nombreServidor, numServidores.get(nombreServidor) == null ? 1
					: Integer.valueOf(numServidores.get(nombreServidor) + 1));
		}

		// Mostrar por la salida estandar todas las entradas del mapa numServidores
		for (Map.Entry<String, Integer> entrada : numServidores.entrySet()) {
			System.out.print(entrada.getKey() + "=" + entrada.getValue() + " ");
		}

		System.out.println("\n\t Numero de ficheros procesados: " + numeroFicherosProcesados);
		System.out.println("\t Numero de trazas procesadas: " + lineasCorrectas.get() + lineasIncorrectas.get());
		System.out.println("\t Numero de trazas incorrectas: " + lineasIncorrectas.get());

		/* Estadisticas agregadas */
		System.out.println("\n\n  Estadisticas agregadas:");

		// Para mostrar el contenido del mapa hmEstadisticasAgregadas de forma ordenado,
		// se copia a un TreeMap y se muestra el contenido de este, pues un TreeMap
		// almacena la informacion ordenada por la clave
		Map<String, AtomicInteger> mapaOrdenado = new TreeMap<String, AtomicInteger>(hmEstadisticasAgregadas);
		String serverName = null;
		String prevServ = null;
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {

			// Recogemos la clave
			String str = entrada.getKey();

			// Dividimos la clave en nombreServidor, fecha, hora, etc...
			String str2[] = str.split(" ");

			// El primer elemento de str2 es el nombre del servidor, el elemento que nos
			// interesa
			serverName = str2[0];

			// Ponemos el valor del servidor en blanco para que al hacer join() quitarlo de
			// la clave
			str2[0] = "";

			// Unimos todos los elementos de la clave una vez hemos extraido el
			// nombreServidor
			String key = String.join(" ", str2);

			// Si el nombreServidor es diferente al anterior lo imprimimos
			if (serverName == null || !serverName.equals(prevServ))
				System.out.println("    " + serverName + ": ");

			System.out.println("\t" + key + " = " + entrada.getValue().get());

			// Actualizamos el valor del nombreServidor anterior
			prevServ = serverName;
		}

		/* Estadisticas de usuarios */
		System.out.println("\n  Estadisticas de usuarios que han enviado mas de " + numMaxMsg + " mensajes:");

		// Se reutiliza el TreeMap anterior para mostrar el mapa hmUsuarios ordenado
		mapaOrdenado.clear();
		mapaOrdenado = new TreeMap<String, AtomicInteger>(hmUsuarios);
		// System.out.println(mapaOrdenado);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			if (entrada.getValue().get() >= numMaxMsg)
				System.out.printf("\t%-20s %5d %n", entrada.getKey() + ":", entrada.getValue().get());
		}

		final long tiempoFinal = System.currentTimeMillis();
		System.out.println("\nTiempo de ejecucion: " + ((long) (tiempoFinal - tiempoComienzo) / 1000) + " segundos");
		System.out.println("\n\n");
	}

	/* Metodos auxiliares */

	/**
	 * Devuelve un array de elementos File que se corresponden con los ficheros con
	 * extension log encontrados en el directorio que se pasa como parametro
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

	/**
	 * Verifica que se ha pasado un argumento con el nombre del directorio y que
	 * este existe y se puede leer. En caso contrario aborta la aplicaci�n.
	 *
	 * @param args Argumentos a analizar
	 */
	private static File validarArgumentos(String[] args) {

		// Validacion de argumentos
		if (args.length != 1) {
			mostrarUso("ERROR: faltan argumentos");
			System.exit(-1);
		}

		// Verificacion de que el directorio existe y puede ser leido
		File directorioFuente = new File(args[0]);
		if (!directorioFuente.isDirectory() || !directorioFuente.canRead()) {
			mostrarUso("ERROR: El directorio '" + args[0] + "' no existe o no puede ser le�do");
			System.exit(-1);
		}
		return (directorioFuente);
	}

	/**
	 * Muestra mensaje de uso del programa.
	 *
	 * @param mensaje Mensaje adicional informativo en la l�nea inicial (null si no
	 *                se desea)
	 */
	private static void mostrarUso(String mensaje) {
		Class<? extends Object> thisClass = new Object() {
		}.getClass();

		if (mensaje != null)
			System.err.println(mensaje);
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <directorio fuente>\n" +
						"\t\t donde <directorio fuente> es el path al directorio donde estan los ficheros de log\n");
	}
}
