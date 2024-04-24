file:///C:/Users/miguel/Universidad/%5B02%5D%20Trabajo/%5B01%5D%20Universidad/%5B01%5D%20PFG/TFG_P2_REGEXP%20v2/src/piat/regExp/Trabajador.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala3-library_3\3.3.1\scala3-library_3-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.10\scala-library-2.13.10.jar [exists ]
Options:



action parameters:
offset: 2113
uri: file:///C:/Users/miguel/Universidad/%5B02%5D%20Trabajo/%5B01%5D%20Universidad/%5B01%5D%20PFG/TFG_P2_REGEXP%20v2/src/piat/regExp/Trabajador.java
text:
```scala
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
			System.out.println("\u00@@1B[31m"+"\n\n[Trabajador.java] Error detectado! Debería ser gestionado en el parser...\n\n"+ "\u001B[0m");
			e.printStackTrace();
		}

		// System.out.println("["+Thread.currentThread().getName()+"] Termino");
		System.out.print(".");
		numTrabajadoresTerminados.incrementAndGet();
	}
}

```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:933)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:168)
	scala.meta.internal.pc.MetalsDriver.run(MetalsDriver.scala:45)
	scala.meta.internal.pc.HoverProvider$.hover(HoverProvider.scala:34)
	scala.meta.internal.pc.ScalaPresentationCompiler.hover$$anonfun$1(ScalaPresentationCompiler.scala:352)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator