import java.util.Map;
import java.util.Set;

public abstract class Graph {
  Map<String, Country> countries;

  public Graph(Map<String, Country> countries) {
    this.countries = countries;
  }

  public abstract void calculerItineraireMinimisantNombreDeFrontieres(String source, String destination, String outname) throws Exception;

  public abstract void calculerItineraireMinimisantPopulationTotale(String source, String destination, String outname) throws Exception;

  protected abstract void addNode(Country c);

  protected abstract void addArc(Travel t);

  protected abstract Set<Travel> outgoingArcs(Country c);

  protected abstract boolean adjacent(Country c1, Country c2);


}
