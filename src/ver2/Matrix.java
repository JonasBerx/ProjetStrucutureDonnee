package ver2;

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
  // This one is a bit questionable?
  private Travel[][] travelMatrix = new Travel[0][0];
  // country[][] matrix that contains a series of null and Country objects where a country means direct border
  private Country[][] adjecencyCountryMatrix = new Country[0][0];
  // int[][] that has the population as integer on said index as weight
  private int[][] populationMatrix = new int[0][0];

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
    System.out.println("---- Minimisant Frontieres Start ----");
    System.out.println("Source: " + countries.get(source) + "\tCountryInt: " + countryIntegerMap.get(countries.get(source)));
    System.out.println("Destination: " + countries.get(destination)+ "\tCountryInt: " + countryIntegerMap.get(countries.get(destination)));

    System.out.println("\nStarting BFS algorithm...");

    // Retrieve the country object values of the given country string
    Country sourceCountry = countries.get(source);
    Country destinationCountry = countries.get(destination);
    // Retrieve the integer values of the given countries
    int sourceInt = countryIntegerMap.get(sourceCountry);
    int destinationInt = countryIntegerMap.get(destinationCountry);

    // The code below generated a map with the link to all countries the source can reach and the amount of countries it has to pass to reach said country.
    // Map to keep track of distances
    Map<Country, Integer> distances = new HashMap<>();
    Map<Country, List<Country>> pathMap = new HashMap<Country, List<Country>>();
    Queue<Country> queue = new LinkedList<>();
    queue.offer(sourceCountry);
    distances.put(sourceCountry, 0);


    while (!queue.isEmpty()) {
      Country country = queue.poll();
      int distance = distances.get(country);

      for (Country nextCountry : getNeighbors(country)) {
        if (!distances.containsKey(nextCountry)) {
          queue.offer(nextCountry);
          distances.put(nextCountry, distance + 1);
        }
      }
    }
//    printMap(distances);

    if (distances.containsKey(destinationCountry)) {
      System.out.println(distances.get(destinationCountry) + 1);
    } else {
      throw new Exception("Cannot reach this country");
    }

    System.out.println("---- Minimisant Frontieres End ----\n");
  }


  private void printMap(Map<Country, Integer> visited) {
    for (Country s: visited.keySet()) {
      System.out.println(s + " : " + visited.get(s));
    }
  }

  private ArrayList<Country> getNeighbors(Country country) {
    if (country.getBorders().isEmpty() && !country.getBordersString().isEmpty()) {
      for (String s : country.getBordersString()
      ) {
        country.addBorders(countries.get(s));
      }
    }
    return (ArrayList<Country>) country.getBorders();
  }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String source, String destination, String outname) {
    System.out.println("---- Minimisant Population Start ----");

    // Retrieve the country object values of the given country string
    Country sourceCountry = countries.get(source);
    Country destinationCountry = countries.get(destination);

    int sourceInt = countryIntegerMap.get(sourceCountry);
    int destinationInt = countryIntegerMap.get(destinationCountry);

    System.out.println("---- Minimisant Population End ----\n");
  }

  private void printShortestPath(int[] parent, int source, int dest) {

    int level = 0;

    // If we reached root of shortest path tree
    if (parent[source] == -1)
    {
      System.out.printf("Shortest Path between"+
              "%d and %d is %s ", source, dest, source);

    }

    printShortestPath(parent, parent[source], dest);

    level++;
    if (source < this.nbCountry)
      System.out.printf("%d ", source);

//    return level;
  }


  @Override
  protected void addNode(Country c) {
    if (nbCountry >= travelMatrix.length) {
      Travel[][] tempTravel = new Travel[nbCountry + 1][nbCountry + 1];
      int[][] tempAdjecency = new int[nbCountry + 1][nbCountry + 1];
      Country[][] tempCountryAdjecency = new Country[nbCountry + 1][nbCountry + 1];
      int[][] tempPop = new int[nbCountry + 1][nbCountry + 1];

      for (int i = 0; i < travelMatrix.length; i++) {
        for (int j = 0; j < travelMatrix.length; j++) {
          tempTravel[i][j] = travelMatrix[i][j];
          tempAdjecency[i][j] = adjecenyMatrix[i][j];
          tempCountryAdjecency[i][j] = adjecencyCountryMatrix[i][j];
          tempPop[i][j] = populationMatrix[i][j];
        }
      }
      travelMatrix = tempTravel;
      adjecenyMatrix = tempAdjecency;
      adjecencyCountryMatrix = tempCountryAdjecency;
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
