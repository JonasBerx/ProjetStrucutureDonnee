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

  // Travel[][] matrix that contains a series of Travel object to destination indexes.
  private Travel[][] travelMatrix = new Travel[0][0];

  // int[][] matrix containing the weight for each country (population) -> WeightMatrix for Dijkstra
  private long[][] populationMatrix = new long[0][0];

  private List<Integer> indices = new ArrayList<>();

  private static final long NO_PARENT = -1;


  // Value used for parent matrix for BFS for values that havent been visited yet -> used during initialisation of array.
  public static final int infty = Integer.MAX_VALUE;

  public Matrix(Map<String, Country> countries) {
    super(countries);
    integerCountryMap = new HashMap<>();
    countryIntegerMap = new HashMap<>();

  }

  /**
   * @param source Containing the STRING value for the source country => cca3.
   * @param destination Containing the STRING value for the destination country => cca3
   * @param filename The desired output.xml name
   *
   * @exception RuntimeException if a country cannot be reached the function will throw an Exception.
   * */
  @Override
  public void calculerItineraireMinimisantNombreDeFrontieres(String source, String destination, String filename) throws RuntimeException {
    try {
      System.out.println("---- Minimisant Frontieres Start ----");

      // Retrieve the country object values of the given country string
      Country sourceCountry = countries.get(source);
      Country destinationCountry = countries.get(destination);
      if (sourceCountry == null || destinationCountry == null) {
        throw new RuntimeException("Cannot find country.");
      }

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
        throw new RuntimeException("Cannot reach destination country from source");
      }
      System.out.println(path);
      List<Country> intinary = collectCountries(path);
      System.out.println(intinary);
      writeToXml(filename, intinary);

      System.out.println("---- Minimisant Frontieres End ----\n");
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }

  }

  private void writeToXml(String filename, List<Country> itinerary) {
    if (filename == null || filename.trim().isEmpty()) {
      filename = "output.xml";
    }
    if (!filename.contains(".xml")) {
      filename += ".xml";
    }

    Document dom;
    Element e;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      // use factory to get an instance of document builder
      DocumentBuilder db = dbf.newDocumentBuilder();
      dom = db.newDocument();
      // Create root element and set its attributes.
      Element root = dom.createElement("itinaire");
      root.setAttribute("arrivee", itinerary.get(itinerary.size()-1).getName());
      root.setAttribute("depart", itinerary.get(0).getName());
      root.setAttribute("nbPays", String.valueOf(itinerary.size()));
      long population = 0;
      for (Country value : itinerary) {
        population += value.getPopulation();
      }
      root.setAttribute("sommePopulation", String.valueOf(population));
      // Create element for each country in the list and append it to the root element.
      for (Country country : itinerary) {
        e = dom.createElement("pays");
        e.setAttribute("cca3", country.getCode());
        e.setAttribute("nom", country.getName());
        e.setAttribute("population", String.valueOf(country.getPopulation()));
        root.appendChild(e);
      }
      // append the root element to the DOM.
      dom.appendChild(root);
      // set standalone to false.
      dom.setXmlStandalone(false);

      try {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty(OutputKeys.STANDALONE, "no");
        tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "itinaire.dtd");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // send DOM to file
        tr.transform(new DOMSource(dom),
                new StreamResult(new FileOutputStream(filename)));

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

  /**
   * @param source Containing the STRING value for the source country => cca3.
   * @param destination Containing the STRING value for the destination country => cca3
   * @param filename The desired output.xml name
   *
   * @exception RuntimeException if a country cannot be reached the function will throw an Exception.
   * */
  @Override
  public void calculerItineraireMinimisantPopulationTotale(String source, String destination, String filename) throws RuntimeException {
    try {
      System.out.println("---- Minimisant Population Start ----");
      // Retrieve the country object values of the given country string
      Country sourceCountry = countries.get(source);
      Country destinationCountry = countries.get(destination);
      if (sourceCountry == null || destinationCountry == null) {
        throw new RuntimeException("Cannot find country.");
      }

      // Initialize integers representing the country based on their position in the Map.
      int sourceInt = countryIntegerMap.get(sourceCountry);
      int destinationInt = countryIntegerMap.get(destinationCountry);

      // Initialize the Weighted Matrix used in Dijkstra's algorithm
      initWeightedPopMatrix(populationMatrix);

      // shortestDistances[i] will hold the shortest distance from src to i
      long[] shortestDistances = new long[nbCountry];

      // visited[] will contain the boolean value if the node is visited or is the shortest path to a certain node.
      boolean[] visited = new boolean[nbCountry];

      // Initialize all distances: all distances to Maxvalue => Integer.MAXVALUE and all added
      for (int countryIndex = 0; countryIndex < nbCountry;
           countryIndex++)
      {
        shortestDistances[countryIndex] = Integer.MAX_VALUE;
        visited[countryIndex] = false;
      }

      // Distance of source vertex from itself is 0 => In this case its set to the population of the source country.
      shortestDistances[sourceInt] = integerCountryMap.get(sourceInt).getPopulation();

      // Parent array to store shortest path tree
      long[] parents = new long[nbCountry];

      // The starting country does not have a parent
      // its value is thus set to -1.
      parents[sourceInt] = NO_PARENT;

      // Find shortest path for all countries
      for (int i = 1; i < nbCountry; i++)
      {

        // Find the minimum distance vertex from the set of vertices that haven't been processed.
        int nearestVertex = -1;
        long shortestDistance = Integer.MAX_VALUE;
        for (int countryIndex = 0; countryIndex < nbCountry; countryIndex++) {
          if (!visited[countryIndex] && shortestDistances[countryIndex] < shortestDistance) {
            nearestVertex = countryIndex;
            shortestDistance = shortestDistances[countryIndex];
          }
        }

        // Mark the picked vertex as processed
        if (nearestVertex == -1) {
          break;
        } else {
          visited[nearestVertex] = true;
          // Update dist value of the adjacent vertices of the picked vertex.
          for (int countryIndex = 0; countryIndex < nbCountry; countryIndex++) {
            long edgeDistance = populationMatrix[nearestVertex][countryIndex];
            if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < shortestDistances[countryIndex])) {
              parents[countryIndex] = nearestVertex;
              shortestDistances[countryIndex] = shortestDistance + edgeDistance;
            }
          }
        }
      }
      if (shortestDistances[destinationInt] == Integer.MAX_VALUE) {
        throw new RuntimeException("Cannot reach this country");
      }

      System.out.print("Vertex\t Distance\tPath");
      System.out.print("\n" + sourceInt + " -> ");
      System.out.print(destinationInt + " \t\t ");
      System.out.print(shortestDistances[destinationInt] + "\t\t");
      printPath(destinationInt,parents);
      Collections.reverse(indices);
      writeToXml(filename,collectCountries(indices));
      System.out.println("\n---- Minimisant Population End ----\n");
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  // Function to print shortest path
  private void printPath(long currentVertex, long[] parents) {
    if (currentVertex == 0) {
      currentVertex = -1;
    }
    if (currentVertex == NO_PARENT) {
      return;
    }
    indices.add((int) currentVertex);
    printPath(parents[(int) currentVertex], parents);
    System.out.print(currentVertex + " ");
  }

  private void initWeightedPopMatrix(long[][] populationMatrix) {
    long[][] weightedAdjecencyMatrix = new long[nbCountry][nbCountry];
    for (int i = 0; i < nbCountry; i++) {
      for (int j = 0; j < nbCountry; j++) {
        weightedAdjecencyMatrix[i][j] = Integer.MAX_VALUE;
        if (populationMatrix[i][j] != 0) {
          weightedAdjecencyMatrix[i][j] = populationMatrix[i][j];
        }
      }
      weightedAdjecencyMatrix[i][i] = 0;
    }
  }

  @Override
  protected void addNode(Country c) {
    if (nbCountry >= travelMatrix.length) {
      Travel[][] tempTravel = new Travel[nbCountry + 1][nbCountry + 1];
      int[][] tempAdjecency = new int[nbCountry + 1][nbCountry + 1];
      long[][] tempPop = new long[nbCountry + 1][nbCountry + 1];

      for (int i = 0; i < travelMatrix.length; i++) {
        for (int j = 0; j < travelMatrix.length; j++) {
          tempTravel[i][j] = travelMatrix[i][j];
          tempAdjecency[i][j] = adjecenyMatrix[i][j];
          tempPop[i][j] = populationMatrix[i][j];
        }
      }
      travelMatrix = tempTravel;
      adjecenyMatrix = tempAdjecency;
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


}
