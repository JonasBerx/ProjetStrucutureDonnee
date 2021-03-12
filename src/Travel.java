public class Travel {
  private final Country departure;
  private final Country destination;

  public Travel(Country departure, Country destination) {
    this.departure = departure;
    this.destination = destination;
  }

  public Country getDeparture() {
    return departure;
  }

  public Country getDestination() {
    return destination;
  }

  @Override
  public String toString() {
    return "Travel{" +
            "departure=" + departure +
            ", destination=" + destination +
            '}';
  }
}
