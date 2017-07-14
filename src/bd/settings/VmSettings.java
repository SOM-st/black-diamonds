package bd.settings;

/**
 * VmSettings are determined based on Java properties. They are used to configure VM-wide
 * properties, for instance whether a tool is enabled or not.
 */
public class VmSettings {
  public static final boolean DYNAMIC_METRICS;

  static {
    DYNAMIC_METRICS = false;
  }
}
