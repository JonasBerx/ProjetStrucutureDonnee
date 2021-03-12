import java.util.Map;
import java.util.Set;

public abstract class Graph {
  protected Map<String, Country> countries ;
  public abstract void calculerItineraireMinimisantNombreDeFrontieres(String bel, String ind, String s);

  public abstract void calculerItineraireMinimisantPopulationTotale(String bel, String ind, String s);

  public Country getAirport(String code) {
    return countries.get(code);
  }

  protected abstract void ajouterSommet(Country c);

  protected abstract void ajouterArc(Travel t);

  public abstract Set<Travel> arcsSortants(Country c);

  public abstract boolean sontAdjacents(Country c1, Country c2);
}
