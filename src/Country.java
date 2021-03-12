import java.util.ArrayList;
import java.util.List;

public class Country {
  private final String name;
  private final List<Country> borders;
  private final List<String> bordersString;
  private final int population;
  private final String code;


  public Country(String code, int population, String name) {
    this.name = name;
    this.borders = new ArrayList<>();
    this.bordersString = new ArrayList<>();
    this.population = population;
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public List<Country> getBorders() {
    return borders;
  }

  public int getPopulation() {
    return population;
  }

  public String getCode() {
    return code;
  }

  public void addBorders(Country country) {
    this.borders.add(country);
  }

  public void addBordersString(String code) {
    this.bordersString.add(code);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Country other = (Country) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Country [code=" + code + ", name=" + name + ", population=" + population + ", borders" + borders + "]";
  }
}
