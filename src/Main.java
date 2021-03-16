import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Main {
	public static void main(String[] args) {
		try {
			File inputFile = new File("countries.xml");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			SAXHandler userhandler = new SAXHandler();
			System.out.println(userhandler.getCountries().get("FRA"));
			saxParser.parse(inputFile, userhandler);
			System.out.println("-------------------");
			System.out.println("-------------------");
			Graph g = userhandler.getGraph();
//			System.out.println(g.countries.get("BEL"));
			System.out.println(g.sontAdjacents(g.countries.get("FRA"), g.countries.get("BEL")));
			g.arcsSortants(g.countries.get("BEL")).forEach(System.out::println);
//			g.calculerItineraireMinimisantNombreDeFrontieres("BEL", "IND", "output.xml");
//			g.calculerItineraireMinimisantPopulationTotale("BEL", "IND", "output2.xml");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
