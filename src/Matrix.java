import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Matrix extends Graph {
  // Map that keeps the country on an index and a map that keeps the index on a country
  private Map<Integer, Country> integerCountryMap;
  private Map<Country, Integer> countryIntegerMap;

  // Integer that will represent the total amount of countries found in the xml file
  private int nbCountry = 0;

  // int[][] matrix that contains a series of 0 and 1 where 1 means direct link and 0 means no link
  private int[][] adjecenyMatrix = new int[0][0];

  // boolean[][] matrix keeping track of which countries are bordering a country.
  private boolean[][] verbindingsMatrix;

  // Travel[][] matrix that contains a series of Travel object to destination indexes.
  private Travel[][] travelMatrix = new Travel[0][0];

  // country[][] matrix that contains a series of null and Country objects where a country means direct border
  private Country[][] adjecencyCountryMatrix = new Country[0][0];

  // int[][] matrix containing the weight for each country (population) -> WeightMatrix for Dijkstra
  private long[][] populationMatrix = new long[0][0];

  private long[][] weightedAdjecencyMatrix;

  // Value used for parent matrix for BFS for values that havent been visited yet -> used during initialisation of array.
  public static final int infty = Integer.MAX_VALUE;

  public Matrix(Map<String, Country> countries) {
    super(countries);
    integerCountryMap = new HashMap<Integer, Country>();
    countryIntegerMap = new HashMap<Country, Integer>();

  }

  public void showMatrices(String param) {
    System.out.println("---- ShowMatrices Start ----");
    switch (param) {
      case "adjacency" -> System.out.println(Arrays.deepToString(adjecenyMatrix));
      case "country" -> System.out.println(Arrays.deepToString(adjecencyCountryMatrix));
      case "travel" -> System.out.println(Arrays.deepToString(travelMatrix));
      default -> System.out.println("no parameter chosen");
    }
    System.out.println("---- ShowMatrices End ----\n");
  }

  @Override
  public void calculerItineraireMinimisantNombreDeFrontieres(String source, String destination, String outname) throws Exception {
    try {
      System.out.println("---- Minimisant Frontieres Start ----");
      System.out.println("Source: " + countries.get(source) + "\tCountryInt: " + countryIntegerMap.get(countries.get(source)));
      System.out.println("Destination: " + countries.get(destination) + "\tCountryInt: " + countryIntegerMap.get(countries.get(destination)));

      System.out.println("\nStarting BFS algorithm...");


      // Retrieve the country object values of the given country string
      Country sourceCountry = countries.get(source);
      Country destinationCountry = countries.get(destination);
      // Retrieve the integer values of the given countries
      int sourceInt = countryIntegerMap.get(sourceCountry);
      int destinationInt = countryIntegerMap.get(destinationCountry);
      // Main Algorithm
      if (sourceInt <= 0 || sourceInt > nbCountry || destinationInt <= 0 || destinationInt > nbCountry) {
        throw new IllegalArgumentException("Cannot access node.. index is below 0 or larger than the total size");
      }
      int[] ancestors = findAncestors(sourceInt, destinationInt);
      List<Integer> path = new LinkedList<>();
      int parent = ancestors[destinationInt - 1];
      while (parent != 0 && parent != infty) {
        path.add(0, destinationInt);
        destinationInt = parent;
        parent = ancestors[destinationInt - 1];
      }
      if (parent == 0) {
        path.add(0, destinationInt);
      }
      if (path.isEmpty()) {
        throw new Exception("Cannot reach destination country from source");
      }
      System.out.println(path);
      List<Country> intinary = collectCountries(path);
      System.out.println(intinary);
      // TODO write to XML function
      writeToXml(outname, intinary);

      System.out.println("---- Minimisant Frontieres End ----\n");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }

  private void writeToXml(String outname, List<Country> itinary) {
    Document dom;
    Element e = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      // use factory to get an instance of document builder
      DocumentBuilder db = dbf.newDocumentBuilder();
      dom = db.newDocument();
      Element root = dom.createElement("itinaire");
      root.setAttribute("arrivee", itinary.get(itinary.size()-1).getName());
      root.setAttribute("depart", itinary.get(0).getName());
      root.setAttribute("nbPays", String.valueOf(itinary.size()));
      long population = 0;
      for (int i = 0; i < itinary.size(); i++) {
        population += itinary.get(i).getPopulation();
      }
      root.setAttribute("sommePopulation", String.valueOf(population));
      for (Country country : itinary) {
        e = dom.createElement("pays");
        e.setAttribute("cca3", country.getCode());
        e.setAttribute("nom", country.getName());
        e.setAttribute("population", String.valueOf(country.getPopulation()));
        root.appendChild(e);
      }
      dom.appendChild(root);

      try {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty(OutputKeys.STANDALONE, "yes");
//        tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // send DOM to file
        tr.transform(new DOMSource(dom),
                new StreamResult(new FileOutputStream(outname)));

      } catch (TransformerException | IOException te) {
        System.out.println(te.getMessage());
      }
    } catch (ParserConfigurationException parserConfigurationException) {
      parserConfigurationException.printStackTrace();
    }
  }

  private List<Country> collectCountries(List<Integer> path) {
    List<Country> countries = new ArrayList<>();
    for (int country : path
    ) {
      countries.add(integerCountryMap.get(country));
    }
    return countries;
  }

  // Searches for the ancestors of a given sourceInt => Corresponding to a country integer in the IntegerCountry Hashmap and a destInt => corresponding to a country integer in the IntegerCountry Hashmap
  private int[] findAncestors(int sourceInt, int destInt) {
    int nodes = this.adjecenyMatrix.length;
    int[] ancestors = new int[nodes];
    for (int i = 0; i < nbCountry; i++) {
      ancestors[i] = infty;
    }
    Queue<Integer> queue = new LinkedList<>();
    queue.add(sourceInt);
    ancestors[sourceInt - 1] = 0;
    int currentInt = queue.remove();
    while (currentInt != destInt) {
      for (int i = 0; i < nodes; i++) {
        if (adjacent(integerCountryMap.get(currentInt), integerCountryMap.get(i)) && ancestors[i - 1] == infty) {
          queue.add(i);
          ancestors[i - 1] = currentInt;
        }
      }
      if (!queue.isEmpty()) {
        currentInt = queue.remove();
      } else {
        break;
      }
    }
    return ancestors;
  }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String source, String destination, String outname) throws Exception {
    System.out.println("---- Minimisant Population Start ----");
    // Retrieve the country object values of the given country string
    Country sourceCountry = countries.get(source);
    Country destinationCountry = countries.get(destination);
    Map<Country, Country> pathMap = new HashMap<>();


    int sourceInt = countryIntegerMap.get(sourceCountry);
    int destinationInt = countryIntegerMap.get(destinationCountry);

    initWeightedPopMatrix(populationMatrix);
    int vertices = nbCountry;

    long[] dist = new long[vertices];
    boolean[] visited = new boolean[vertices];

    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(visited, false);
    dist[sourceInt] = countries.get(source).getPopulation();

    for (int i = 0; i < vertices - 1; i++) {
      int p = getMinIndex(dist, visited);
      visited[p] = true;

      for (int j = 0; j < vertices; j++) {
        if (!visited[j] && populationMatrix[p][j] != 0 && dist[p] != Integer.MAX_VALUE && dist[p] + populationMatrix[p][j] < dist[j]) {
          dist[j] = dist[p] + populationMatrix[p][j];
        }
      }
    }

    cleanUpList(dist);
    System.out.println(Arrays.toString(dist));

    System.out.println("---- Minimisant Population End ----\n");
  }

  private void cleanUpList(long[] dist) {
    for (int i = 0; i < dist.length; i++) {
      if (dist[i] == Integer.MAX_VALUE) {
        dist[i] = 0;
      }
    }
  }


  private void initWeightedPopMatrix(long[][] populationMatrix) {
    weightedAdjecencyMatrix = new long[nbCountry][nbCountry];
    for (int i = 0; i < nbCountry; i++) {
      for (int j = 0; j < nbCountry; j++) {
        weightedAdjecencyMatrix[i][j] = Integer.MAX_VALUE;
        if (populationMatrix[i][j] != 0) {
          weightedAdjecencyMatrix[i][j] = populationMatrix[i][j];
        }
      }
      weightedAdjecencyMatrix[i][i] = 0;
    }
//    printIntMatrix(weightedAdjecencyMatrix);
  }

  private int getMinIndex(long[] dist, boolean[] visited) {
    int vertices = dist.length;

    long minDist = Integer.MAX_VALUE;
    int minIndex = -1;

    for (int i = 0; i < vertices; i++) {
      if (!visited[i] && dist[i] <= minDist) {
        minDist = dist[i];
        minIndex = i;
      }
    }
    return minIndex;
  }


  private static void printIntMatrix(long[][] matrix) {
    String result ="";
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        result += (matrix[i][j] == Integer.MAX_VALUE ? "inf" : matrix[i][j]) + "\t";
      }
      result += "\n";
    }
    result += "\n";

    System.out.println(result);
  }
  @Override
  protected void addNode(Country c) {
    if (nbCountry >= travelMatrix.length) {
      Travel[][] tempTravel = new Travel[nbCountry + 1][nbCountry + 1];
      int[][] tempAdjecency = new int[nbCountry + 1][nbCountry + 1];
      Country[][] tempCountryAdjecency = new Country[nbCountry + 1][nbCountry + 1];
      boolean[][] tempBool = new boolean[nbCountry+1][nbCountry+1];
      long[][] tempPop = new long[nbCountry + 1][nbCountry + 1];

      for (int i = 0; i < travelMatrix.length; i++) {
        for (int j = 0; j < travelMatrix.length; j++) {
          tempTravel[i][j] = travelMatrix[i][j];
          tempAdjecency[i][j] = adjecenyMatrix[i][j];
          tempCountryAdjecency[i][j] = adjecencyCountryMatrix[i][j];
          tempBool[i][j] = adjecenyMatrix[i][j] == 1;
          tempPop[i][j] = populationMatrix[i][j];
        }
      }
      travelMatrix = tempTravel;
      adjecenyMatrix = tempAdjecency;
      adjecencyCountryMatrix = tempCountryAdjecency;
      verbindingsMatrix = tempBool;
      populationMatrix = tempPop;
    }

    if (!integerCountryMap.containsValue(c) && !countryIntegerMap.containsKey(c)) {
      integerCountryMap.put(nbCountry, c);
      countryIntegerMap.put(c, nbCountry);
      nbCountry++;
    }

  }

  @Override
  protected void addArc(Travel t) {
    int sourceInt = countryIntegerMap.get(t.getDeparture());
    int destinationInt = countryIntegerMap.get(t.getDestination());

    travelMatrix[sourceInt][destinationInt] = t;
    adjecenyMatrix[sourceInt][destinationInt] = 1;
    adjecencyCountryMatrix[sourceInt][destinationInt] = t.getDestination();
    verbindingsMatrix[sourceInt][destinationInt] = true;
    populationMatrix[sourceInt][destinationInt] = t.getDestination().getPopulation();

  }

  @Override
  protected Set<Travel> outgoingArcs(Country c) {
    Set<Travel> outgoing = new HashSet<>();
    int sourceInt = countryIntegerMap.get(c);
    for (int i = 0; i < travelMatrix.length; i++) {
      Travel t = travelMatrix[sourceInt][i];
      if (t != null) {
        outgoing.add(t);
      }
    }
    return outgoing;
  }

  @Override
  protected boolean adjacent(Country c1, Country c2) {
    int index1 = countryIntegerMap.get(c1);
    int index2 = countryIntegerMap.get(c2);

    return adjecenyMatrix[index1][index2] != 0 && adjecenyMatrix[index2][index1] != 0;
  }

  @Override
  public boolean isValidMatrix() {
    if (travelMatrix == null || travelMatrix.length != travelMatrix[0].length) {
      return false;
    }
    for (int i = 0; i < travelMatrix.length; i++) {
      if (travelMatrix[i][i] != null) {
        return false;
      }
    }
    for (int i = 0; i < travelMatrix.length; i++) {
      for (int j = 0; j < travelMatrix.length; j++) {
        if (travelMatrix[i][j] == null) {;
          return false;
        }
      }

    }
    return true;
  }
}
