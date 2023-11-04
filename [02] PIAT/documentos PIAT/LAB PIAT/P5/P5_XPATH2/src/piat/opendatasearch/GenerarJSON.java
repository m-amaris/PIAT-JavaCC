package piat.opendatasearch;

import java.util.List;

import piat.opendatasearch.XPathProcess.Propiedad;

/**
 * 
 * @author 
 *
 */

public class GenerarJSON {
	
	/* Strings para el pretty printing */ 
	private static final String openObject = "{";
	private static final String closeObject = "}";
	//private static final String openArray = "[\n\t\t";

	private static final String offsetLV1 = "\t";
	private static final String offsetLV2 = "\t\t";
	private static final String offsetLV3 = "\t\t\t";
	private static final String closeArray = "]";

	
	
	
	
	public static String toJSON(List<Propiedad>jsonElements) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(openObject + "\n");
		int size = jsonElements.size();
		int i = 1;
		
		for(Propiedad p : jsonElements) {

			//Si se trata de una Propiedad simple, es decir, del tipo nombre : valor
			if(!p.valor.equals("[")) 
				sbSalida.append(offsetLV1 + p.toString() + ",\n");
			else {
				toJSONArray(p, sbSalida);
				if(i<size)
					sbSalida.append(",\n");
				else 
					sbSalida.append("\n");
			}
			
			i++;
		}

		sbSalida.append(closeObject);
		return sbSalida.toString();
	}
	
	private static void toJSONArray(Propiedad p, StringBuilder sbSalida) {
		int index = 1;
		int size = p.valores.size();
		sbSalida.append(offsetLV1 + p.toString() + "\n");
		
		//Recorro las propiedades contenidas (pIn) en las propiedades compuestas p
		for(Propiedad pIn : p.valores) {
			//Si se trata de una propiedad compuesta por varias propiedades simples. (Caso titles)
			if(!pIn.nombre.equals("complex")) {
				sbSalida.append(offsetLV2 + openObject + pIn.toString() + closeObject);
				if(index < size)
					sbSalida.append(",\n");
			}
			//Si se trata de una propiedad compuesta por varias propiedades compuestas. (Del tipo infDatasets).
			else {
				sbSalida.append(offsetLV2 + openObject + "\n");
				Byte i = 0;
				for(Propiedad pInIn : pIn.valorDimN) {				
					sbSalida.append(offsetLV3 + pInIn.toString());
					if(i < 1) 
						sbSalida.append(",\n");
					else 
						sbSalida.append("\n");
					i++;
				}
			sbSalida.append(offsetLV2 + closeObject);
			if(index < size)
				sbSalida.append(",\n");
			}
			index++;
		}
		sbSalida.append("\n" + offsetLV1 + closeArray);
	}
	
	

}
