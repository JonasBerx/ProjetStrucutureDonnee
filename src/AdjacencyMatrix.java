import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdjacencyMatrix extends Graph{
  private Map<Integer, Country> intCountry;
  private Map<Country, Integer> countryInt;
  private int nbCountry = 0;
  private Travel[][] matrix = new Travel[0][0];

  public AdjacencyMatrix() {
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

    //Si l'a�roport n'est encore pr�sent dans aucune map
    if(!intCountry.containsValue(c) && !countryInt.containsKey(c)) {
      intCountry.put(nbCountry, c);
      countryInt.put(c, nbCountry);
      nbCountry++;
    }
  }

  @Override
  protected void ajouterArc(Travel t) {

  }

  @Override
  public Set<Travel> arcsSortants(Country c) {
    Set<Travel> going = new HashSet<>();
    //On r�cup�re l'indice de la source
    int departureInt = countryInt.get(c);
    //On parcourt la ligne des vols
    for(int i=0; i<matrix.length; i++) {
      //On r�cup�re le vol
      Travel t = matrix[departureInt][i];
      //Si le vol est pas null on l'ajoute au set
      if(t != null)
        going.add(t);
    }
    return going;
  }

  @Override
  public boolean sontAdjacents(Country c1, Country c2) {
    return false;
  }
}
