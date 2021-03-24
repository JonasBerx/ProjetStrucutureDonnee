import java.util.*;

public class AdjacencyMatrix extends Graph {
  private Map<Integer, Country> intCountry;
  private Map<Country, Integer> countryInt;
  private int nbCountry = 0;
  private Travel[][] matrix = new Travel[0][0];
  private int[][] matrixInt;
  private int[][] matrixPop;


  public AdjacencyMatrix(Map<String, Country> countries) {
    super(countries);
    countryInt = new HashMap<Country, Integer>();
    intCountry = new HashMap<Integer, Country>();
  }

  private int getNodes() {
    return matrix.length;
  }

  @Override
  public List<Integer> calculerItineraireMinimisantNombreDeFrontieres( String bel, String ind){
    Country x = countries.get(bel);
    Country y= countries.get(ind);
    int start = countryInt.get(x);
    int destination = countryInt.get(y);
    List<Integer> list = new ArrayList<>();
    List<String> listc = new ArrayList<>();
    System.out.println(nbCountry);
    int fromCountryIndex = countryInt.get(countries.get(bel));
    System.out.println(fromCountryIndex);
    int toCountryIndex = countryInt.get(countries.get(ind));
    System.out.println(toCountryIndex);



    return null;
  }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String bel, String ind) {

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
    if (nbCountry >= matrix.length) {
      Travel[][] matrixTemp = new Travel[nbCountry + 1][nbCountry + 1];
      int[][] intMatrixTemp = new int[nbCountry+1][nbCountry+1];
      int[][] popMatrixTemp = new int[nbCountry][nbCountry];
      for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix.length; j++) {
          matrixTemp[i][j] = matrix[i][j];
          intMatrixTemp[i][j] = matrixInt[i][j];
          popMatrixTemp[i][j] = 0;
        }
      }
      matrix = matrixTemp;
      matrixInt = intMatrixTemp;
      matrixPop = popMatrixTemp;
    }

    if (!intCountry.containsValue(c) && !countryInt.containsKey(c)) {
      intCountry.put(nbCountry, c);
      countryInt.put(c, nbCountry);
      nbCountry++;
    }
  }

  @Override
  protected void ajouterArc(Travel t) {
    Country departure = t.getDeparture();
    List<Country> x = new ArrayList<>();
    departure.setBorders(x);
    Country destination = t.getDestination();
    destination.setBorders(x);
    int departureInt = countryInt.get(departure);
    int destinationInt = countryInt.get(destination);
    matrix[departureInt][destinationInt] = t;
    matrixInt[departureInt][destinationInt] = 1;
    matrixInt[departureInt][destinationInt] = t.getDestination().getPopulation();
  }

  @Override
  public Set<Travel> arcsSortants(Country c) {
    return null;
  }

//  @Override
//  public Set<Travel> arcsSortants(Country c) {
//    Set<Travel> going = new HashSet<>();
//    int departureInt = countryInt.get(c);
//    for (int i = 0; i < matrix.length; i++) {
//      Travel t = matrix[departureInt][i];
//      if (t != null)
//        going.add(t);
//    }
//
//    System.out.println(going);
//    return going;
//  }

  @Override
  public boolean sontAdjacents(Country c1, Country c2) {
    int indiceA1 = countryInt.get(c1);
    int indiceA2 = countryInt.get(c2);
    if (matrix[indiceA1][indiceA2] != null && matrix[indiceA2][indiceA1] != null)
      return true;
    else
      return false;
  }

  public int[][] setMatrixInt() {
    matrixInt = new int[matrix.length][matrix.length];
    for (int i0 = 0; i0 < matrix.length; i0++) {
      if (matrix[i0] == null) {
        matrixInt[i0] = null;
      } else {
        for (int i1 = 0; i1 < matrix[i0].length; i1++) {
          if (matrix[i0][i1] == null || matrix[i0][i1].getDestination() == null || matrix[i0][i1].getDeparture() == null) {
            matrixInt[i0][i1] = 0;
          } else {
            matrixInt[i0][i1] = 1;
          }
        }
      }
    }
//    for (int i = 0; i < matrixInt.length; i++) {
//      for (int j = 0; j < matrixInt.length; j++) {
//        if(matrixInt[i][j] == 1){
//          System.out.print(intCountry.get(i).getCode() + " ");
//        }
//        else{
//          System.out.print(matrixInt[i][j] + " ");
//        }
//      }
//      System.out.println();
//    }
    return matrixInt;
  }

  @Override
  public boolean estMatrice() {
    if (matrixInt == null || matrixInt.length != matrixInt[0].length) {
      return false;
    }

    for (int i = 0; i < matrixInt.length; i++) {
      if (matrixInt[i][i] != 0) {
        return false;
      }
    }

    for (int i = 0; i < matrixInt.length; i++) {
      for (int j = 0; j < matrixInt.length; j++) {
        if (matrixInt[i][j] != 0 && matrixInt[i][j] != 1) {
          return false;
        }
      }

    }

    return true;
  }

  @Override
  public boolean estMatriceNormal() {
    if (matrix == null || matrix.length != matrix[0].length) {
      return false;
    }

    for (int i = 0; i < matrix.length; i++) {
      if (matrix[i][i] != null) {
        return false;
      }
    }

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        if (matrix[i][j] == null) {;
          return false;
        }
      }

    }

    return true;
  }


}
