import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
//  AdjacencyMatrix am;
  Matrix am;

  public SAXHandler() {
    super();
    this.countries = new HashMap<>();
  }

  //Set this to true if you want to print
  boolean print = true;

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
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (qName.equalsIgnoreCase("country")) {
      name = attributes.getValue("name");
      cca3 = attributes.getValue("cca3");
      population = Integer.parseInt(attributes.getValue("population"));
      borders = new ArrayList<>();
    }

    if (qName.equalsIgnoreCase("border")) {
      bfborders = true;
    }


  }

  @Override
  public void characters(char[] ch, int start, int length) {
    if (bfname) {
      bfname = false;
    }
    if (bfcode) {
      bfcode = false;
    }
    if (bfpopulation) {
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
  public void endElement(String uri, String localName, String qName) {
    if (qName.equalsIgnoreCase("country")) {
      Country country = new Country(cca3, population, name);
      country.setBordersString(borders);
      countries.put(cca3, country);
    }
    if (qName.equalsIgnoreCase("countries")) {
      Map<String, Country> tempMap = new HashMap<>();
      for (Country country : countries.values()) {
        Country c2 = new Country(country.getCode(), country.getPopulation(), country.getName());
        c2.setBordersString(country.getBordersString());
        List<Country> listCountries = new ArrayList<>();
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

    public Matrix getGraph () {
      am = new Matrix(countries);
      for (Country country : countries.values()) {
        am.addNode(country);
      }
      for(Country country : countries.values()) {
        if(country.getBorders().size() == 1) {
          Travel t = new Travel(country, countries.get(country.getBordersString().get(0)));
          am.addArc(t);
        }
        else if(country.getBorders().size() != 0){
          for (Country country2 : country.getBorders()) {
            Travel t = new Travel(country, country2);
            am.addArc(t);
          }
        }
      }
      return am;
    }
}
