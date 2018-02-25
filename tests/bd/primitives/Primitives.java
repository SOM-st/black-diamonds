package bd.primitives;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;

import bd.testsetup.AbsNodeFactory;
import bd.testsetup.AddAbsNodeFactory;
import bd.testsetup.AddNodeFactory;
import bd.testsetup.AddWithSpecializerNodeFactory;
import bd.testsetup.ExprNode;
import bd.testsetup.LangContext;
import bd.testsetup.StringId;


public class Primitives extends PrimitiveLoader<LangContext, ExprNode, String> {
  protected Primitives(final LangContext context) {
    super(new StringId(), context);
    initialize();
  }

  @Override
  protected List<NodeFactory<? extends ExprNode>> getFactories() {
    List<NodeFactory<? extends ExprNode>> allFactories = new ArrayList<>();

    allFactories.add(AddNodeFactory.getInstance());
    allFactories.add(AddWithSpecializerNodeFactory.getInstance());
    allFactories.add(AbsNodeFactory.getInstance());
    allFactories.add(AddAbsNodeFactory.getInstance());

    return allFactories;
  }

  @Override
  protected void registerPrimitive(final Primitive prim,
      final Specializer<LangContext, ExprNode, String> specializer) {
    /* not needed for testing */
  }
}
