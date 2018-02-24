package bd.basic.nodes;

import com.oracle.truffle.api.nodes.Node;


/**
 * Dummy Node to work around Truffle's restriction that a node, which is going to
 * be instrumented, needs to have a parent.
 */
public final class DummyParent extends Node {
  @Child public Node child;

  public DummyParent(final Node node) {
    this.child = insert(node);
  }
}
