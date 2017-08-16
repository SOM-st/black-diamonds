package bd.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;


/**
 * Nodes that need to be able to accept their arguments already evaluated should implement this
 * interface.
 *
 * <p>
 * The interface is useful during specialization. First, the arguments are evaluated, and
 * afterwards, a new specialization node is determined that then needs to be able to accept the
 * arguments.
 */
public interface PreevaluatedExpression {

  /**
   * Execute the node with the given arguments.
   *
   * @param frame current frame with local variables etc.
   * @param args already evaluated arguments for the node
   * @return result of node execution
   */
  Object doPreEvaluated(VirtualFrame frame, Object[] args);
}
