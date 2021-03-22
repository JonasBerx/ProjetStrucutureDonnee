import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Graph {
  Map<String, Country> countries;

  public Graph(Map<String,Country> countries) {
    this.countries = countries;
  }
  public abstract List<Integer> calculerItineraireMinimisantNombreDeFrontieres(String bel, String ind) throws Exception;

  public abstract void calculerItineraireMinimisantPopulationTotale(String bel, String ind);

  public abstract String geefAncestors(int start, int destination);

  protected abstract void ajouterSommet(Country c);

  protected abstract void ajouterArc(Travel t);

  public abstract Set<Travel> arcsSortants(Country c);

  public abstract boolean sontAdjacents(Country c1, Country c2);

  public abstract boolean estMatrice();
  public abstract boolean estMatriceNormal();

  public abstract void init();

}
