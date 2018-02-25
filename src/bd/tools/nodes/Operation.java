package bd.tools.nodes;

/**
 * Interface for tools to identify the operation a node is implementing.
 */
public interface Operation {
  /**
   * The name or identifier of an operation implemented by a node.
   * An addition node could return for instance <code>"+"</code>. The name should be
   * understandable by humans, and might be shown in a user interface.
   */
  String getOperation();

  /**
   * The number of arguments on which the operation depends.
   */
  int getNumArguments();
}
