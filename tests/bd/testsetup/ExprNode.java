package bd.testsetup;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;


public abstract class ExprNode extends Node {

  public abstract Object executeGeneric(VirtualFrame frame);

  public int executeInt(final VirtualFrame frame) throws UnexpectedResultException {
    Object result = executeGeneric(frame);
    if (result instanceof Integer) {
      return (int) result;
    } else {
      throw new UnexpectedResultException(result);
    }
  }
}
