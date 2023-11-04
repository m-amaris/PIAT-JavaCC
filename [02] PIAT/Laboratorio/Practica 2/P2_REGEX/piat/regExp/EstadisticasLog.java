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

	private final static int numMaxMsg = 500;	// N� m�ximo de mensajes enviados por un usuario para mostrarlo en las estad�sticas
	
	// Expresiones regulares para la fecha y la hora
	private final static String FECHA = "\\d{4}-\\d{2}-\\d{2}"; //  "[0-9]{4}-[0-9]{2}-[0-9]{2}"
	private final static String HORA = "\\d{2}:\\d{2}:\\d{2}"; //  "[0-9]{2}:[0-9]{2}:[0-9]{2}"
	// Expresiones regulares para el nombre del servidor. El nombre del servidor est� formado por el tipo + un n�mero. Ejemplo smtp-in3
	private final static String TIPO_SERVIDOR = "\\D+"; // "[^0-9]+"
	private final static String NUMERO_SERVIDOR = "\\d"; // "[0-9]+"
	private final static String QUEUE_ID = "\\[\\w+\\]";
	private final static String TRAZA = "\\w.*";
	
	// Patr�n de una traza cualquiera correcta de la que podemos extraer, en el grupo 1, el nombre del servidor
	// private final static String patronTraza = "^"+FECHA+"\\s+" + HORA + "\\s+(" + TIPO_SERVIDOR  + NUMERO_SERVIDOR+")+\\s+\\[\\w+\\]:.*";	
	private final static String regex = "^("+FECHA+")\\s+("+HORA+")\\s("+TIPO_SERVIDOR+")("+NUMERO_SERVIDOR+")\\s("+QUEUE_ID+"):\\s("+TRAZA+")$";
	/* Patrones que se usan en las estad�sticas agregadas */
	private final static String msgIn = ".*smtp-in.*connect from.*";		// Los mensajes que pasan al siguiente servidor son los que tienen la palabra SEC-PASSED en la traza
	private final static String msgOut = ".*smtp-out.*delivered.*"; 	// Los mensajes bloqueados son los que tienen la palabra SEC-BLOCKED en la traza	
	private final static String msgInfected = ".*security.*INFECTED.*"; 	// Los mensajes bloqueados son los que tienen la palabra SEC-BLOCKED en la traza	
	private final static String msgSPAM = ".*SPAM.*";		// Los mensajes que pasan al siguiente servidor son los que tienen la palabra SEC-PASSED en la traza
	private final static String code511 = ".*5.1.1\\s\\(bad\\sdestination\\smailbox\\saddress\\)"; 	// Los mensajes bloqueados son los que tienen la palabra SEC-BLOCKED en la traza	
	private final static String code432 = ".*4.3.2\\s\\(overload\\)*";		// Los mensajes que pasan al siguiente servidor son los que tienen la palabra SEC-PASSED en la traza

	public static void main(String[] args) throws InterruptedException {
	    Thread.currentThread().setName("Principal");
	    // Verificar que se pasa como argumento el directorio con los logs y obtenerlo
	    File directorioFuente=validarArgumentos(args);
		
		// En este array bidimensional se almacenan los nombres de los estad�sticos a obtener y el patr�n para que luego sea mas f�cil recorrerlo 
		// y meter sus valores en el mapa hmPatronesEstadisticasAgregadas 
		final String[][] patronesEstadisticasAgregadas= {
							{"msgIn",msgIn},
							{"msgOut",msgOut},
							{"msgInfected",msgInfected},
							{"msgSPAM",msgSPAM},
							{"Code 5.1.1",code511},
							{"Code 4.3.2",code432}							
						}; //Pattern.compile
		
		// Mapa donde se guarda como clave el nombre de la estad�stica agregada, y como valor, el patr�n para detectar el String de la traza que contiene ese estad�stico
		final ConcurrentHashMap <String,Pattern> hmPatronesEstadisticasAgregadas = new ConcurrentHashMap <String,Pattern> ();
		
		// Guardar en el mapa hmPatronesEstadisticasAgregadas todos los patrones que se usar�n para las estad�sticas agregadas.
		// Los patrones y el nombre del estad�stico est�n en el array bidimensional patronesEstadisticasAgregadas
		Pattern patron;
		String estadistico;		
		for (int i= 0; i < patronesEstadisticasAgregadas.length;i++) {
			estadistico=patronesEstadisticasAgregadas[i][0];
			patron = Pattern.compile(patronesEstadisticasAgregadas[i][1]);
			hmPatronesEstadisticasAgregadas.put(estadistico, patron);
		}		
		
		// Mapa donde recoger los contadores de cada estad�sticas agregada.
		// La clave indica el servidor, la fecha y el estad�stico, por lo que estar� formado por una String con esta estructura: "<tipoServidor> <fecha> <estad�stico>"
		// El valor es el contador de las veces que se encuentra el estad�stico anterior
		final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas = new ConcurrentHashMap <String,AtomicInteger>();	

		// Mapa donde guardar el nombre del servidor y el tipo de servidor
		final ConcurrentHashMap<String, String> hmServidores = new ConcurrentHashMap <String,String>();
		
		// Mapa donde se guarda como clave el nombre de un usuario y como valor un contador, para poder guardar cuantos mensajes env�an los usuarios del sistema
		final ConcurrentHashMap <String,AtomicInteger> hmUsuarios = new ConcurrentHashMap <String,AtomicInteger> ();	
		
		// Crear el patr�n que se usar� para analizar una traza, y del que, adem�s de conocer si es una traza correcta, se pueden extraer ciertos valores mediante los grupos
		Pattern pTraza = Pattern.compile (regex);

		/* Variables usadas para las estad�sticas generales */
		int numeroFicherosProcesados;
		final AtomicInteger lineasCorrectas = new AtomicInteger(0);
		final AtomicInteger lineasIncorrectas = new AtomicInteger(0);
		
		/****** Comienzo del procesamiento ******/
		
		// Obtener los ficheros de log del directorio
		final File[] ficheros=obtenerFicheros(directorioFuente);
		
		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.print ("\nLanzando hilos en los " + numDeNucleos + " n�cleos disponibles de este ordenador ");
		
		// Crear un pool donde ejecutar los hilos. El pool tendr� un tama�o del n� de n�cleos del ordenador
		// por lo que nunca podr� haber m�s hilos que ese n�mero en ejecuci�n simult�nea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecuci�n, poner como argumento un 1. Ir� mucho m�s lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

	    final long tiempoComienzo = System.currentTimeMillis();
	    final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
	    int numTrabajadores=0;
	    numeroFicherosProcesados=0;
		for (File fichero: ficheros){
			System.out.print (".");
			ejecutor.execute(new Trabajador(fichero,  pTraza,  lineasCorrectas,  lineasIncorrectas, hmServidores,hmEstadisticasAgregadas, hmPatronesEstadisticasAgregadas,hmUsuarios,numTrabajadoresTerminados));
			numeroFicherosProcesados++;
			numTrabajadores++;
			//break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out.print ("\nSe van a ejecutar "+numTrabajadores+" trabajadores en el pool. Esperar a que terminen ");
		
		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el �ltimo trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados=numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numTrabajadores-terminados)+" que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados
		System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" trabajadores");
		
		/****** Mostrar las estad�sticas ******/
		/* Estad�sticas generales */
		System.out.println("\n\n  Estad�sticas generales:");

		System.out.print("\t Servidores: ");
		// Crear un mapa temporal, llamado numServidores, que se usar� para almacenar como clave el tipo de servidor y como valor un contador de cuantos hay de ese tipo en el mapa hmServidores
		HashMap<String, Integer> numServidores = new HashMap <String,Integer>();
		// Recorrer todas las entradas de hmServidores
		for (Map.Entry<String, String> entrada : hmServidores.entrySet()) {
			// Se intenta meter la clave de hmServidores en numServidores:
			// 		si no existe se a�ade con el contador 1
			// 		si ya existe se incrementa el contador en 1
			String nombreServidor = entrada.getValue();
			numServidores.put (nombreServidor, numServidores.get(nombreServidor) == null ? 1 : Integer.valueOf(numServidores.get(nombreServidor)+1));
		}
		// Mostrar por la salida est�ndar todas las entradas del mapa numServidores
		for (Map.Entry<String, Integer> entrada : numServidores.entrySet()) {
			System.out.print(entrada.getKey()+"="+entrada.getValue()+" ");
		}
		
		System.out.println("\n\t Numero de ficheros procesados: "+ numeroFicherosProcesados);
		System.out.println("\t Numero de trazas procesadas: "+ lineasCorrectas.get()+lineasIncorrectas.get());
		System.out.println("\t Numero de trazas incorrectas: "+ lineasIncorrectas.get());

		
		/* Estad�sticas agregadas */
		System.out.println("\n\n  Estad�sticas agregadas:");
		
		// Para mostrar el contenido del mapa hmEstadisticasAgregadas de forma ordenado, se copia a un TreeMap y se muestra el contenido de este, pues un TreeMap almacena la informaci�n ordenada por la clave
		Map <String, AtomicInteger> mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmEstadisticasAgregadas);
		String serverName = null;
		String prevServ = null;
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			String str = entrada.getKey();	// recogemos la clave
			String str2[] = str.split(" ");	// dividimos la clave en nombreServidor, fecha, hora, etc...
			serverName = str2[0]; // el primer elemento de str2 es el nombre del servidor, el elemento que nos interesa
			str2[0] = ""; // ponemos el valor del servidor en blanco para que al hacer join() quitarlo de la clave
			String key = String.join(" ", str2); // unimos todos los elementos de la clave una vez hemos extraido el nombreServidor
			if(serverName== null || !serverName.equals(prevServ)) // si el nombreServidor es diferente al anterior lo imprimimos
				System.out.println("    "+serverName+": ");
			System.out.println("\t"+key+" = " +entrada.getValue().get());
			prevServ = serverName; //actualizamos el valor del nombreServidor anterior
	        }

		/* Estad�sticas de usuarios */
		System.out.println("\n  Estad�sticas de usuarios que han enviado m�s de "+numMaxMsg+" mensajes:");
		
		// Se reutiliza el TreeMap anterior para mostrar el mapa hmUsuarios ordenado
		mapaOrdenado.clear();
		mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmUsuarios);
		// System.out.println(mapaOrdenado);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			if (entrada.getValue().get()>=numMaxMsg) 
				System.out.printf("\t%-20s %5d %n", entrada.getKey()+":",entrada.getValue().get());
		}

		
		final long tiempoFinal = System.currentTimeMillis();
		System.out.println("\nTiempo de ejecuci�n: "+ ((long)(tiempoFinal-tiempoComienzo)/1000) + " segundos");
		System.out.println("\n\n");
	}
	
	
	/* M�todos auxiliares */
	
	/**
	 * Devuelve un array de elementos File que se corresponden con los ficheros con extensi�n log encontrados en el directorio que se pasa como par�metro
	 *
	 * @param	directorioFuente	Directorio que contiene los ficheros de log
	 */
	private static File[] obtenerFicheros(File directorioFuente) {
	
		final File[] ficheros = directorioFuente.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".log")){
						return true;
					}
					return false;
				}
			}
		);
		return ficheros;
	}
	
 
	/**
	 * Verifica que se ha pasado un argumento con el nombre del directorio y que este existe y se puede leer. En caso contrario aborta la aplicaci�n.
	 *
	 * @param	args	Argumentos a analizar
	 */
	private static File validarArgumentos(String[] args) {
		// Validaci�n de argumentos
		if (args.length != 1){
			mostrarUso("ERROR: faltan argumentos");
			System.exit(-1);
		}
	
		// Verificaci�n de que el directorio existe y puede ser le�do
		File directorioFuente = new File(args[0]);
		if (!directorioFuente.isDirectory() || !directorioFuente.canRead()){
			mostrarUso("ERROR: El directorio '" + args[0] + "' no existe o no puede ser le�do");
			System.exit(-1);
		}
		return (directorioFuente);
	}
	
	
	/**
	 * Muestra mensaje de uso del programa.
	 *
	 * @param	mensaje	Mensaje adicional informativo en la l�nea inicial (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje);
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <directorio fuente>\n" +
				"\t\t donde <directorio fuente> es el path al directorio donde est�n los ficheros de log\n");
	}
}
