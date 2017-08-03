package bd.settings;

public class AllDisabled implements Settings {

  @Override
  public boolean dynamicMetricsEnabled() {
    return false;
  }

  @Override
  public boolean nodesRequireSourceSection() {
    return false;
  }

  @Override
  public boolean nodesRequireEagerWrappingBoolean() {
    return false;
  }
}
