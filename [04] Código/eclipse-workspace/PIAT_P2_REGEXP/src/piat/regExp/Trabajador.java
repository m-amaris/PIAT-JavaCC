package piat.regExp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Miguel Amarís Martos 54022315
 */
public class Trabajador implements Runnable {

	private final File fichero;
	private final Pattern pTraza;
	private final AtomicInteger lineasCorrectas, lineasIncorrectas;
	private final ConcurrentHashMap<String, String> hmServidores;
	private final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas;
	private final ConcurrentHashMap<String, Pattern> hmPatronesEstadisticasAgregadas;
	private final ConcurrentHashMap<String, AtomicInteger> hmUsuarios;
	private final AtomicInteger numTrabajadoresTerminados;

	private final static int INDICE_FECHA = 1;
	// private final static int INDICE_HORA = 2;
	private final static int INDICE_TIPO_SERVIDOR = 3;
	private final static int INDICE_NUMERO_SERVIDOR = 4;
	// private final static int INDICE_QUEUE_ID = 5;
	// private final static int INDICE_TRAZA = 6;

	public Trabajador(File fichero, Pattern pTraza, AtomicInteger lineasCorrectas, AtomicInteger lineasIncorrectas,
			ConcurrentHashMap<String, String> hmServidores,
			ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas,
			ConcurrentHashMap<String, Pattern> hmPatronesEstadisticasAgregadas,
			ConcurrentHashMap<String, AtomicInteger> hmUsuarios, AtomicInteger numTrabajadoresTerminados) {
		this.fichero = fichero;
		this.pTraza = pTraza;
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
		// System.out.println("["+Thread.currentThread().getName()+"] Empiezo");

		// Abrir el fichero y si no hay errores, procesar cada l�nea invocando al m�todo
		// procesarLinea()
		try {
			BufferedReader brInput = new BufferedReader(new FileReader(fichero));
			try {
				String linea;
				while ((linea = brInput.readLine()) != null) {
					procesarLinea(linea);
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

	/**
	 * Procesa una l�nea que contendr� una traza de un fichero de log.
	 * El procesamiento consiste en:
	 * - Verificar si es una traza con el formato correcto
	 * - Invocar a los m�todos correspondientes para cada estad�stico
	 *
	 * @param sLinea L�nea a analizar
	 */
	public void procesarLinea(String sLinea) {
		Matcher matcher;

		if (sLinea.trim().length() > 0) {
			matcher = pTraza.matcher(sLinea); // Realizar la casaci�n de la l�nea con el patr�n gen�rico de una traza
			if (matcher.matches()) { // Verificar que la l�nea es correcta
				lineasCorrectas.incrementAndGet();
				estadisticasServidor(matcher);
				estadisticasAgregadas(matcher);
				estadisticasUsuarios(matcher);
			} else {
				// System.out.println("La l�nea "+sLinea+" no tiene el formato correcto");
				lineasIncorrectas.incrementAndGet();
			}
		}
	}

	/**
	 * Este m�todo sirve para obtener las estad�sticas de servidor.
	 * Guarda en el mapa hmServidores como clave el nombre de servidor y como valor
	 * su tipo
	 *
	 * @param matcherLinea Objeto Matcher al que ya se le ha realizado una casaci�n
	 *                     y por tanto se puede extraer informaci�n de sus grupos
	 */
	private void estadisticasServidor(Matcher matcherLinea) {
		// Extraer de matcherLinea el nombre del servidor y el tipo
		String nombreServidor = matcherLinea.group(INDICE_TIPO_SERVIDOR) + matcherLinea.group(INDICE_NUMERO_SERVIDOR);
		String tipoServidor = matcherLinea.group(INDICE_TIPO_SERVIDOR);
		hmServidores.put(nombreServidor, tipoServidor); // Meter los valores obtenidos en el mapa
	}

	/**
	 * Este m�todo sirve para obtener las estad�sticas agregadas cuyos patrones
	 * est�n en el mapa hmPatronesEstadisticasAgregadas
	 * Para ello recorre el mapa hmPatronesEstadisticasAgregadas y en cada iteraci�n
	 * extrae un patron que prueba con la l�nea que se est� analizando.
	 * Si el patr�n casa con la informaci�n de la l�nea se actualiza el mapa
	 * hmEstadisticasAgregadas
	 *
	 * @param matcherLinea Objeto Matcher al que ya se le ha realizado una casaci�n
	 *                     y por tanto se puede extraer informaci�n de sus grupos
	 */
	private void estadisticasAgregadas(Matcher matcherLinea) {
		// Extraer de matcherLinea los valores que se necesitan a partir de los grupos
		// disponibles
		final String traza = matcherLinea.group(0); // Traza completa
		final String tipoServidor = matcherLinea.group(INDICE_TIPO_SERVIDOR); // Tipo de servidor
		final String fecha = matcherLinea.group(INDICE_FECHA);

		Matcher comparador;
		String clave, estadistico;
		Pattern patron;
		final AtomicInteger contadorAnterior;

		// Recorrer el mapa hmPatronesEstadisticasAgregadas, que contiene los patrones a
		// probar, para ver si cada uno de esos patrones casan con la traza a analizar
		for (Map.Entry<String, Pattern> entrada : hmPatronesEstadisticasAgregadas.entrySet()) {
			estadistico = entrada.getKey(); // String con el nombre del estad�stico
			patron = entrada.getValue(); // Patr�n asociado al estad�stico
			comparador = patron.matcher(traza); // Aplicar el patr�n a la traza a analizar
			if (comparador.matches()) { // Si casa, entonces actualizar el mapa hmEstadisticasAgregadas
				clave = tipoServidor + " " + " " + fecha + " " + estadistico; // La clave del mapa
																				// hmEstadisticasAgregadas est� formada
																				// por el tipo del servidor y el nombre
																				// del estad�stico
				// hmEstadisticasAgregadas
				// estara formada por el tipo del servidor, la fecha de la traza y el nombre del
				// estadistico.
				// Por ejemplo "security-in 2020-02-21 - msgBLOCKED"

				// A�adir el estad�stico al mapa. Si no existe el valor del contador es 1, pero
				// si existe, se recupera el valor y se incrementa
				contadorAnterior = hmEstadisticasAgregadas.putIfAbsent(clave, new AtomicInteger(1));
				if (contadorAnterior != null)
					contadorAnterior.incrementAndGet();
				break; // Este break sale del for, pues al ser los patrones disjuntos, en cuanto una
						// l�nea cumple un patr�n, no hace falta mirar otros patrones
			}
		}
	}

	/**
	 * Este m�todo sirve para obtener las estad�sticas del n� de correos que ha
	 * enviado cada usuario del sistema
	 * Por cada traza que se reconoce como traza en la que se ha enviado un mensaje,
	 * se actualiza el mapa hmUsuarios
	 *
	 * @param matcherLinea Objeto Matcher al que ya se le ha realizado una casaci�n
	 *                     y por tanto se puede extraer informaci�n de sus grupos
	 */
	private void estadisticasUsuarios(Matcher matcherLinea) {
		// Ver si la traza se corresponde a una traza que indica que se ha enviado un
		// mensaje
		// En ese caso, guardar en el mapa hmUsuarios el nombre del usuario como clave y
		// como valor el n 1 si no exist�a esa clave, pues en el caso de que existiera
		// hay que incrementar el valor
	
		// buscamos solo los mensajes del servidor msa y recogemos el correo del emisor:
		final String regex = ".*\\smsa.*message.*\\sfrom:\\s<([\\w-]+\\.[\\w-]+)@.*"; 
		final String traza = matcherLinea.group(0);
		final AtomicInteger contadorAnterior;

		Pattern patron;
		patron = Pattern.compile(regex);
		Matcher comparador = patron.matcher(traza);
		if (comparador.matches()) {
			contadorAnterior = hmUsuarios.putIfAbsent(comparador.group(1), new AtomicInteger(1));
			if (contadorAnterior != null) contadorAnterior.incrementAndGet();
		}

	}
}
