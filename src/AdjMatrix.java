import java.util.*;

public class AdjMatrix extends Graph {
  private static final int NO_PARENT = -1;
  private Map<Integer, Country> intCountry;
  private Map<Country, Integer> countryInt;
  private int nbCountry = 0;
  private Travel[][] travelMatrix = new Travel[0][0];
  private int[][] populationMatrix;

  private int[][] adjecencyMatrix;


  private static ArrayList<Country> shortestPath = new ArrayList<Country>();
  private Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();


  public AdjMatrix(Map<String, Country> countries) {
    super(countries);
    countryInt = new HashMap<Country, Integer>();
    intCountry = new HashMap<Integer, Country>();

  }

  private void setBorders(Country c) {
      for (String border : c.getBordersString()
      ) {
        c.addBorders(countries.get(border));
      }

  }

  public ArrayList<Integer> getNeighbours(int node) {
    ArrayList<Integer> neighbourInts = new ArrayList<>();
    for (Country c : intCountry.get(node).getBorders()
    ) {
      neighbourInts.add(countryInt.get(c));
    }
    return neighbourInts;
  }

  @Override
  public List<Integer> calculerItineraireMinimisantNombreDeFrontieres(String source, String destination) throws Exception {

    int sourceInt = countryInt.get(countries.get(source));
    int destinationInt = countryInt.get(countries.get(destination));

    System.out.println(sourceInt);
    System.out.println(countryInt.get(countries.get("FRA")));
    System.out.println(countryInt.get(countries.get("CHE")));
    System.out.println(countryInt.get(countries.get("AUT")));
    System.out.println(countryInt.get(countries.get("SVN")));
    System.out.println(countryInt.get(countries.get("HRV")));
    System.out.println(countryInt.get(countries.get("MNE")));
    System.out.println(countryInt.get(countries.get("ALB")));
    System.out.println(countryInt.get(countries.get("MKD")));
    System.out.println(countryInt.get(countries.get("BGR")));
    System.out.println(countryInt.get(countries.get("TUR")));
    System.out.println(countryInt.get(countries.get("IRN")));
    System.out.println(countryInt.get(countries.get("PAK")));
    System.out.println(destinationInt);

    ArrayList<Integer> shortestPathList = new ArrayList<Integer>();
    HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();

    if (sourceInt == destinationInt)
      return Collections.singletonList(sourceInt);
    Queue<Integer> queue = new LinkedList<Integer>();
    Stack<Integer> pathStack = new Stack<Integer>();

    queue.add(sourceInt);
    pathStack.add(sourceInt);
    visited.put(sourceInt, true);

    while (!queue.isEmpty()) {
      int u = queue.poll();
      ArrayList<Integer> adjList = getNeighbours(u);

      for (int v : adjList) {
        if (!visited.containsKey(v)) {
          queue.add(v);
          visited.put(v, true);
          pathStack.add(v);
          if (u == destinationInt)
            break;
        }
      }
    }


    //To find the path
    int node, currentSrc = destinationInt;
    shortestPathList.add(destinationInt);
    while (!pathStack.isEmpty()) {
      node = pathStack.pop();
      if (sontAdjacents(intCountry.get(currentSrc), intCountry.get(node))) {
        shortestPathList.add(node);
        currentSrc = node;
        if (node == sourceInt)
          break;
      }
    }
    Collections.reverse(shortestPathList);
    return shortestPathList;
  }

//    return shortestPathList;

//    shortestPath.clear();
//
//    Country f = countries.get(source);
//    Country t = countries.get(destincation);
//    // Keep in mind that the printed index = index +1 in the list.
//    System.out.println(source + ": " + countryInt.get(f));
//    System.out.println(destincation + ": " + countryInt.get(t));
//
//
//    // A list that stores the path.
//    ArrayList<Country> path = new ArrayList<Country>();
//
//    // If the source is the same as destination, I'm done.
//    if (source.equals(destincation)) {
//      path.add(f);
//      System.out.println(path);
//      return path;
//    }
//    // A queue to store the visited nodes.
//    ArrayDeque<Country> nextCountry = new ArrayDeque<>();
//
//    // A queue to store the visited nodes.
//    ArrayDeque<Country> visited = new ArrayDeque<>();
//    visited.offer(f);
//    nextCountry.offer(f);
//
//    while (!nextCountry.isEmpty()) {
//      Country next = nextCountry.poll();
//      System.out.println(next.getCode() + " ");
//
//      ArrayList<String> neighbours = getNeighbours(next.getCode());
//
//      int index = 0;
//      int size = neighbours.size();
//      while (index != size) {
//        Country neighbour = countries.get(neighbours.get(index));
//
//
//        path.add(neighbour);
//        visited.add(next);
//
//        if (next.equals(t)) {
//          System.out.println("---result---");
//          return processPath(f, t, path);
//        } else {
//          if (!visited.contains(neighbour)) {
//            visited.offer(neighbour);
//            nextCountry.offer(neighbour);
//          }
//        }
//        index++;
//      }
//    }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String from, String to) {
    Country f = countries.get(from);
    Country t = countries.get(to);
    int fromInt = countryInt.get(f);
    int toInt = countryInt.get(t);
    // Keep in mind that the printed index = index +1 in the list.
    System.out.println(from + ": " + countryInt.get(f));
    System.out.println(to + ": " + countryInt.get(t));

    int vertices = populationMatrix.length;

    int[] dist = new int[vertices];
    boolean[] visited = new boolean[vertices];

    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(visited, false);
    dist[fromInt] = 0;

    for (int i = 0; i < vertices - 1; i++) {
      int p = getMinIndex(dist, visited);
      visited[p] = true;

      for (int j = 0; j < vertices; j++) {
        if (!visited[j] && populationMatrix[p][j] != 0 && dist[p] != Integer.MAX_VALUE && dist[p] + populationMatrix[p][j] < dist[j]) {
          dist[j] = dist[p] + populationMatrix[p][j];
        }
      }
    }
    System.out.println(dist[toInt]+t.getPopulation());

  }

  public int getMinIndex(int[] dist, boolean[] visited) {
    int vertices = dist.length;

    int minDist = Integer.MAX_VALUE;
    int minIndex = -1;

    for (int i = 0; i < vertices; i++) {
      if (!visited[i] && dist[i] <= minDist) {
        minDist = dist[i];
        minIndex = i;
      }
    }
    return minIndex;
  }


  @Override
  protected void ajouterSommet(Country c) {
    if (nbCountry >= travelMatrix.length) {
      Travel[][] matrixTemp = new Travel[nbCountry + 1][nbCountry + 1];
      int[][] popMatrixTemp = new int[nbCountry + 1][nbCountry + 1];
      int[][] adjMatrixTemp = new int[nbCountry + 1][nbCountry + 1];
      for (int i = 0; i < travelMatrix.length; i++) {
        for (int j = 0; j < travelMatrix.length; j++) {
          matrixTemp[i][j] = travelMatrix[i][j];
          popMatrixTemp[i][j] = populationMatrix[i][j];
          adjMatrixTemp[i][j] = adjecencyMatrix[i][j];
        }
      }
      travelMatrix = matrixTemp;
      populationMatrix = popMatrixTemp;
      adjecencyMatrix = adjMatrixTemp;
    }
    if (!intCountry.containsValue(c) && !countryInt.containsKey(c)) {
      intCountry.put(nbCountry, c);
      countryInt.put(c, nbCountry);
      nbCountry++;
    }
  }

  @Override
  protected void ajouterArc(Travel t) {
    Country from = t.getDeparture();
    Country to = t.getDestination();
    int departureInt = countryInt.get(from);
    int destinationInt = countryInt.get(to);
    travelMatrix[departureInt][destinationInt] = t;
    populationMatrix[departureInt][destinationInt] = from.getPopulation();
    adjecencyMatrix[departureInt][destinationInt] = 1;
    adjecencyMatrix[destinationInt][departureInt] = 1;
  }

  @Override
  public Set<Travel> arcsSortants(Country c) {
    Set<Travel> going = new HashSet<>();
    int departureInt = countryInt.get(c);
    for (int i = 0; i < travelMatrix.length; i++) {
      Travel t = travelMatrix[departureInt][i];
      if (t != null)
        going.add(t);
    }

    System.out.println(going);
    return going;
  }

  @Override
  public boolean sontAdjacents(Country c1, Country c2) {
    int indiceA1 = countryInt.get(c1);
    int indiceA2 = countryInt.get(c2);
    return travelMatrix[indiceA1][indiceA2] != null && travelMatrix[indiceA2][indiceA1] != null;
  }

  @Override
  public boolean estMatrice() {
    return true;
  }

  @Override
  public boolean estMatriceNormal() {
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

  @Override
  public int[][] setMatrixInt() {
    return new int[0][];
  }
}
