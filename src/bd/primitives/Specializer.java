package bd.primitives;

import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.source.SourceSection;

import bd.primitives.Primitive.NoChild;
import bd.primitives.nodes.EagerlySpecializable;
import bd.primitives.nodes.WithContext;
import bd.settings.VmSettings;


/**
 * A Specializer defines when a node can be used as a eager primitive, how
 * it is to be instantiated, and acts as factory for them.
 *
 * @param <Context> the type of the context object
 * @param <ExprT> the root type of expressions used by the language
 * @param <Id> the type of the identifiers used for mapping to primitives, typically some form
 *          of interned string construct
 */
public class Specializer<Context, ExprT, Id> {
  protected final Context                    context;
  protected final Primitive                  prim;
  protected final NodeFactory<ExprT>         fact;
  private final NodeFactory<? extends ExprT> extraChildFactory;

  private final int     extraArity;
  private final boolean requiresContext;

  @SuppressWarnings("unchecked")
  public Specializer(final Primitive prim, final NodeFactory<ExprT> fact,
      final Context context) {
    this.prim = prim;
    this.fact = fact;
    this.context = context;

    this.requiresContext = WithContext.class.isAssignableFrom(fact.getNodeClass());

    if (prim.extraChild() == NoChild.class) {
      extraChildFactory = null;
      extraArity = 0;
    } else {
      try {
        extraChildFactory =
            (NodeFactory<? extends ExprT>) prim.extraChild().getMethod("getInstance")
                                               .invoke(null);
        extraArity = extraChildFactory.getExecutionSignature().size();
      } catch (IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException
          | SecurityException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public boolean inParser() {
    return prim.inParser() && !prim.requiresArguments();
  }

  public boolean noWrapper() {
    return prim.noWrapper();
  }

  public boolean classSide() {
    return prim.classSide();
  }

  public String getName() {
    return fact.getClass().getSimpleName();
  }

  public boolean matches(final Object[] args, final ExprT[] argNodes) {
    // TODO: figure out whether we really want it like this with a VmSetting, or whether
    // there should be something on the context
    if (prim.disabled() && VmSettings.DYNAMIC_METRICS) {
      return false;
    }

    if (args == null || prim.receiverType().length == 0) {
      // no constraints, so, it matches
      return true;
    }

    for (Class<?> c : prim.receiverType()) {
      if (c.isInstance(args[0])) {
        return true;
      }
    }
    return false;
  }

  private int numberOfNodeConstructorArguments(final ExprT[] argNodes) {
    int args = argNodes.length;

    return args +
        (extraChildFactory != null ? 1 : 0) +
        (prim.requiresArguments() ? 1 : 0);
  }

  @SuppressWarnings("unchecked")
  public ExprT create(final Object[] arguments, final ExprT[] argNodes,
      final SourceSection section, final boolean eagerWrapper) {
    assert arguments == null || arguments.length >= argNodes.length;
    int numArgs = numberOfNodeConstructorArguments(argNodes);

    Object[] ctorArgs = new Object[numArgs];
    int offset = 0;

    if (prim.requiresArguments()) {
      assert arguments != null;
      ctorArgs[offset] = arguments;
      offset += 1;
    }

    for (int i = 0; i < argNodes.length; i += 1) {
      ctorArgs[offset] = eagerWrapper ? null : argNodes[i];
      offset += 1;
    }

    if (extraChildFactory != null) {
      ctorArgs[offset] = extraChildFactory.createNode(new Object[extraArity]);
      offset += 1;
    }

    ExprT node = fact.createNode(ctorArgs);
    ((EagerlySpecializable<ExprT, Id, Context>) node).initialize(section, eagerWrapper);
    if (requiresContext) {
      ((WithContext<?, Context>) node).initialize(context);
    }
    return node;
  }
}
