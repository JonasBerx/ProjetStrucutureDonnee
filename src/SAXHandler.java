import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAXHandler extends DefaultHandler {
  protected Map<String, Country> countries;

  /**
   * TODO String for countrycode (cca3), Int for population
   * TODO List of adjecent countries (border)
   * TODO Processing of the xml into text for output into new xml file
   */

  boolean bfname = false;
  boolean bfborders = false;
  boolean bfpopulation = false;
  boolean bfcode = false;
  String name, cca3;
  int population;
  List<String> borders;
  AdjacencyMatrix am;

  public SAXHandler() {
    super();
    this.countries = new HashMap<>();
  }

  //Set this to true if you want to print
  boolean print = false;

  public String getName() {
    return name;
  }

  public int getPopulation() {
    return population;
  }

  public List<String> getBorders() {
    return borders;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (!qName.equalsIgnoreCase("border")) {
      System.out.println("Start element: " + qName);
    }
    if (qName.equalsIgnoreCase("country")) {
      name = attributes.getValue("name");
      cca3 = attributes.getValue("cca3");
      population = Integer.parseInt(attributes.getValue("population"));
      borders = new ArrayList<String>();
      System.out.println("CCA3 : " + cca3);
      System.out.println("Name : " + name);
      System.out.println("Population : " + population);
    }

    if (qName.equalsIgnoreCase("border")) {
      bfborders = true;
    }


  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (bfname) {
      System.out.println("Name : " + name);
      bfname = false;
    }

    if (bfcode) {
      System.out.println("CCA3 : " + cca3);
      bfcode = false;
    }

    if (bfpopulation) {
      System.out.println("Population : " + population);
      bfpopulation = false;
    }

    if (bfborders) {
      String border = new String(ch, start, length);
      borders.add(border);
      bfborders = false;
    }
  }

  public Map<String, Country> getCountries() {
    return countries;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("country")) {
      Country country = new Country(cca3, population, name);
      System.out.println("Setting borders: " + borders);
      country.setBordersString(borders);
      System.out.println(country.toString());
      countries.put(cca3, country);
      System.out.println("Borders : " + borders);
    }
    if (!qName.equalsIgnoreCase("border")) {
      System.out.println("End element: " + qName);
      System.out.println("-------------------");
    }
    if (qName.equalsIgnoreCase("countries")) {
      Map<String, Country> tempMap = new HashMap<>();
      for (Country country : countries.values()) {
        Country c2 = new Country(country.getCode(), country.getPopulation(), country.getName());
        c2.setBordersString(country.getBordersString());
        List<Country> listCountries = new ArrayList<Country>();
        for (String border : country.getBordersString()) {
          if(c2.getBorders().size() < country.getBordersString().size()) {
            c2.addBorders(countries.get(border));
          }
        }
        tempMap.put(c2.getCode(), c2);
      }
      countries = tempMap;
    }
  }

    public AdjacencyMatrix getGraph () {
      am = new AdjacencyMatrix(countries);
      for (Country country : countries.values()) {
        am.ajouterSommet(country);
      }
      for(Country country : countries.values()) {
        if(country.getBorders().size() == 1) {
          Travel t = new Travel(country, countries.get(country.getBordersString().get(0)));
          am.ajouterArc(t);
        }
        else if(country.getBorders().size() != 0){
          for (Country country2 : country.getBorders()) {
            Travel t = new Travel(country, country2);
            am.ajouterArc(t);
          }
        }
      }
      return am;
    }

    //  public void getGraph() throws Exception {
//    File xml = new File("countries.xml");
//    DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
//    DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
//    Document doc = docBuild.parse(xml);
//    NodeList countries = doc.getElementsByTagName("country");
//    // Adding all countries
//    for (int i = 0; i < countries.getLength(); i++) {
//      Node country = countries.item(i);
//      Element elCountry = (Element) country;
//      String code = elCountry.getAttribute("cca3");
//      String name = elCountry.getAttribute("name");
//      int population = Integer.parseInt(elCountry.getAttribute("population"));
//      Country country1 = new Country(code, population, name);
//      countriesMap.put(code, country1);
//    }
//    // Checking for borders and adding them to the countries
//    for (int i = 0; i < countries.getLength(); i++) {
//      Node country = countries.item(i);
//      Element elCountry = (Element) country;
//      String code = elCountry.getAttribute("cca3");
//      NodeList borders = elCountry.getElementsByTagName("border");
//      getBordersCode(borders, code);
//    }
//    System.out.println(countriesMap.values().toString());
//  }
//
//  // Convert code to country object and add it as border
//  private void getBorders(NodeList nl, String code) {
//    int length = nl.getLength();
//    List<Country> elements = new ArrayList<>();
//    for (int i = 0; i < length; i++) {
//      Country country = countriesMap.get(nl.item(i).getTextContent());
//      countriesMap.get(code).addBorders(country);
//    }
//  }
//
//  // Only add code to the border list
//  private void getBordersCode(NodeList nl, String code) {
//    int length = nl.getLength();
//    List<Country> elements = new ArrayList<>();
//    for (int i = 0; i < length; i++) {
//      countriesMap.get(code).addBordersString(nl.item(i).getTextContent());
//    }
//  }
  }
