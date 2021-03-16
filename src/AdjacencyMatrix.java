import java.util.*;

public class AdjacencyMatrix extends Graph{
  private Map<Integer, Country> intCountry;
  private Map<Country, Integer> countryInt;
  private int nbCountry = 0;
  private Travel[][] matrix = new Travel[0][0];

  public AdjacencyMatrix(Map<String,Country> countries) {
    super(countries);
    countryInt = new HashMap<Country, Integer>();
    intCountry = new HashMap<Integer, Country>();
  }


  @Override
  public void calculerItineraireMinimisantNombreDeFrontieres(String bel, String ind, String s) {

  }

  @Override
  public void calculerItineraireMinimisantPopulationTotale(String bel, String ind, String s) {

  }

  @Override
  protected void ajouterSommet(Country c) {
    if(nbCountry >= matrix.length) {
      Travel[][] matrixTemp = new Travel[nbCountry+1][nbCountry+1];
      for(int i=0; i<matrix.length; i++) {
        for(int j=0; j<matrix.length; j++) {
          matrixTemp[i][j] = matrix[i][j];
        }
      }
      matrix = matrixTemp;
    }

    if(!intCountry.containsValue(c) && !countryInt.containsKey(c)) {
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
  }

  @Override
  public Set<Travel> arcsSortants(Country c) {
    Set<Travel> going = new HashSet<>();
    int departureInt = countryInt.get(c);
    for(int i=0; i<matrix.length; i++) {
      Travel t = matrix[departureInt][i];
      if(t != null)
        going.add(t);
    }
    return going;
  }

  @Override
  public boolean sontAdjacents(Country c1, Country c2) {
    int indiceA1 = countryInt.get(c1);
    int indiceA2 = countryInt.get(c2);
    if(matrix[indiceA1][indiceA2] != null && matrix[indiceA2][indiceA1] != null)
      return true;
    else
      return false;
  }
}
