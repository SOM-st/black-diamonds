package bd.settings;

/**
 * VmSettings are determined based on Java properties. They are used to configure VM-wide
 * properties, for instance whether a tool is enabled or not.
 */
public class VmSettings {
  public static final boolean DYNAMIC_METRICS;

  public static final boolean NODES_REQUIRE_EAGER_WRAPPER_BOOL;
  public static final boolean NODES_REQUIRE_SOURCE_SECTION;

  static {
    Settings s = getSettings();

    DYNAMIC_METRICS = s.dynamicMetricsEnabled();
    NODES_REQUIRE_EAGER_WRAPPER_BOOL = s.nodesRequireEagerWrappingBoolean();
    NODES_REQUIRE_SOURCE_SECTION = s.nodesRequireSourceSection();
  }

  private static Settings getSettings() {
    String className = System.getProperty("bd.settings");

    try {
      Class<?> clazz = VmSettings.class.getClassLoader().loadClass(className);
      return (Settings) clazz.newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      // Checkstyle: stop
      System.err.println("[BlackDiamonds] Could not load settings class: " + className);
      e.printStackTrace();
      return new AllDisabled();
      // Checkstyle: resume
    }
  }
}
