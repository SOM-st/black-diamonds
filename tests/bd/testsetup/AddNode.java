package bd.testsetup;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;


@NodeChild(value = "left", type = ExprNode.class)
@NodeChild(value = "right", type = ExprNode.class)
public abstract class AddNode extends ExprNode {

  @Specialization
  public int add(final int left, final int right) {
    return left + right;
  }
}
