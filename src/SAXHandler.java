import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAXHandler extends DefaultHandler {
  protected Map<String, Country> countriesMap;

  /**
   * TODO String for countrycode (cca3), Int for population
   * TODO List of adjecent countries (border)
   * TODO Processing of the xml into text for output into new xml file
   */


  public SAXHandler() {
    countriesMap = new HashMap<String, Country>();
  }


  public void getGraph() throws Exception {
    File xml = new File("countries.xml");
    DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
    Document doc = docBuild.parse(xml);
    NodeList countries = doc.getElementsByTagName("country");
    // Adding all countries
    for (int i = 0; i < countries.getLength(); i++) {
      Node country = countries.item(i);
      Element elCountry = (Element) country;
      String code = elCountry.getAttribute("cca3");
      String name = elCountry.getAttribute("name");
      int population = Integer.parseInt(elCountry.getAttribute("population"));
      Country country1 = new Country(code, population, name);
      countriesMap.put(code, country1);
    }
    // Checking for borders and adding them to the countries
    for (int i = 0; i < countries.getLength(); i++) {
      Node country = countries.item(i);
      Element elCountry = (Element) country;
      String code = elCountry.getAttribute("cca3");
      NodeList borders = elCountry.getElementsByTagName("border");
      getBordersCode(borders, code);
    }
    System.out.println(countriesMap.values().toString());
  }

  // Convert code to country object and add it as border
  private void getBorders(NodeList nl, String code) {
    int length = nl.getLength();
    List<Country> elements = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      Country country = countriesMap.get(nl.item(i).getTextContent());
      countriesMap.get(code).addBorders(country);
    }
  }

  // Only add code to the border list
  private void getBordersCode(NodeList nl, String code) {
    int length = nl.getLength();
    List<Country> elements = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      countriesMap.get(code).addBordersString(nl.item(i).getTextContent());
    }
  }
}
