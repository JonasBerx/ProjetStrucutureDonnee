import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AdjacencyMatrix extends Graph {
  private Map<Integer, Country> intCountry;
  private Map<Country, Integer> countryInt;
  private int nbCountry = 0;
  private Travel[][] matrix = new Travel[0][0];
  private int[][] matrixInt;
  private boolean[][] verbindingsMatrix;
  public static final int infty = Integer.MAX_VALUE;

  public AdjacencyMatrix(Map<String, Country> countries) {
    super(countries);
    countryInt = new HashMap<Country, Integer>();
    intCountry = new HashMap<Integer, Country>();
  }

  @Override
  public void init(){
    if (!isGeldigeVerbindingsMatrix(matrixInt))
      throw new IllegalArgumentException("No valid nabijheidsmatrix");

    this.verbindingsMatrix = new boolean[matrixInt.length][matrixInt.length];
    for (int i = 0; i < matrixInt.length; i++)
      for (int j = 0; j < matrixInt.length; j++)
        this.verbindingsMatrix[i][j] = matrixInt[i][j] == 1;
  }

  public boolean isGeldigeVerbindingsMatrix(int[][] matrix) {
    if (matrix == null || matrix.length != matrix[0].length)
      return false;

    for (int i = 0; i < matrix.length; i++)
      if (matrix[i][i] != 0)
        return false;

    for (int i = 0; i < matrix.length; i++)
      for (int j = 0; j < matrix.length; j++) {
        if (matrix[i][j] != 0 && matrix[i][j] != 1) {
          return false;
        }
      }
    return true;
  }

  private int getNodes() {
    return matrix.length;
  }

  @Override
  public List<Integer> calculerItineraireMinimisantNombreDeFrontieres(String bel, String ind){

    Country x = countries.get(bel);
    Country y= countries.get(ind);
    int start = countryInt.get(x);
    int destination = countryInt.get(y);
    List<Integer> pad = new ArrayList<>();
    List<String> listc = new ArrayList<>();
    if (start <= 0 || start > this.getAantalKnopen() || destination <= 0 ||
            destination > this.getAantalKnopen())
      throw new IllegalArgumentException();
    int[] ancestors = this.findAncestors(start, destination);
    List<Integer> path = new LinkedList<>();
    int ouder = ancestors[destination - 1];
    while (ouder != 0 && ouder != infty) {
      path.add(0, destination);;
      destination = ouder;
      ouder = ancestors[destination - 1];
    }
    if (ouder == 0) {
      path.add(0,destination);
    }

    return path;
  }

  private int getAantalKnopen() {
    return this.matrixInt.length;
  }

  private boolean rechtstreekseVerbinding(int van, int tot) {
//    System.out.println("verbinding van "+van+" tot "+tot+"?");
    return verbindingsMatrix[van - 1][tot - 1];
  }

  private void initArray(int[] array, int value) {
    for (int i = 0; i < array.length; i++)
      array[i] = value;

    System.out.println(Arrays.toString(array));
  }

  private int[] findAncestors(int start, int destination) {
    int aantalKnopen = this.getAantalKnopen();
    int[] ancestors = new int[aantalKnopen];
    Queue<Integer> queue = new LinkedList<>();
    queue.add(start);
    ancestors[start - 1] = 0;
    int huidig = queue.remove();
    while (huidig != destination) {
      for (int i = 1; i <= aantalKnopen; i++) {
        if (rechtstreekseVerbinding(huidig, i) && ancestors[i - 1] == infty) {
          //voeg knoop i toe aan queue
          queue.add(i);
          //duid aan dat huidig de ouder is van i in ancestormatrix
          ancestors[i - 1] = huidig;
        }
      }
      //voorste element van queue wordt nieuwe huidige knoop
      if (!queue.isEmpty()) {
        huidig = queue.remove(); //of .poll() wat geen exception gooit
      } else {
        //queue is leeg, stop maar
        break;
      }
    }
    return ancestors;
  }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String bel, String ind) {

  }

  @Override
  public String geefAncestors(int start, int destination) {
    String res = "Ancestors van "+start+" naar "+destination+":\n";
    int[] ancestors = this.findAncestors(start, destination);
    for (int a=0; a<ancestors.length; a++)
      res += ancestors[a]!=infty?ancestors[a]:"infty"+" ";

    return res;
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
      for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix.length; j++) {
          matrixTemp[i][j] = matrix[i][j];
          intMatrixTemp[i][j] = matrixInt[i][j];
        }
      }
      matrix = matrixTemp;
      matrixInt = intMatrixTemp;
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
  }

  @Override
  public Set<Travel> arcsSortants(Country c) {
    Set<Travel> going = new HashSet<>();
    int departureInt = countryInt.get(c);
    for (int i = 0; i < matrix.length; i++) {
      Travel t = matrix[departureInt][i];
      if (t != null)
        going.add(t);
    }

    return going;
  }

  @Override
  public boolean sontAdjacents(Country c1, Country c2) {
    int indiceA1 = countryInt.get(c1);
    int indiceA2 = countryInt.get(c2);
    if (matrix[indiceA1][indiceA2] != null && matrix[indiceA2][indiceA1] != null)
      return true;
    else
      return false;
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
