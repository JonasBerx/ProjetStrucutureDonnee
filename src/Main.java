import java.io.File;
import java.util.Arrays;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Main {
	public static void main(String[] args) {
		try {
			File inputFile = new File("src/countries.xml");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			SAXHandler userhandler = new SAXHandler();
//			System.out.println(userhandler.getCountries().get("FRA"));
			saxParser.parse(inputFile, userhandler);
			System.out.println("-------------------");
			System.out.println("-------------------");
			Graph g = userhandler.getGraph();
			g.init();
//			System.out.println(g.getMatrixInt());
			System.out.println(g.countries.get("BEL"));
//			System.out.println(g.estMatriceNormal());
//			System.out.println(g.setMatrixInt());
//			g.arcsSortants(g.countries.get("BEL")).forEach(System.out::println);
////			System.out.println(g.arcsSortants(g.countries.get("BRN")));
//			System.out.println(g.sontAdjacents(g.countries.get("MYT"), g.countries.get("BRN")));
				System.out.println(g.calculerItineraireMinimisantNombreDeFrontieres("BEL", "FRA"));
//			g.calculerItineraireMinimisantPopulationTotale("BEL", "IND", "output2.xml");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
