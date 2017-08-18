package bd.primitives;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;

import bd.testsetup.AddNodeFactory;
import bd.testsetup.ExprNode;


public class Primitives extends PrimitiveLoader<Void, ExprNode, String> {
  protected Primitives(final Void context) {
    super(context);
    initialize();
  }

  @Override
  protected List<NodeFactory<? extends ExprNode>> getFactories() {
    List<NodeFactory<? extends ExprNode>> allFactories = new ArrayList<>();
    allFactories.add(AddNodeFactory.getInstance());
    return allFactories;
  }

  @Override
  protected void registerPrimitive(final Primitive prim,
      final Specializer<Void, ExprNode, String> specializer) { /* not needed for testing */ }

  @Override
  protected String getId(final String id) {
    return id.intern();
  }
}
