package bd.primitives;

import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.source.SourceSection;

import bd.primitives.Primitive.NoChild;
import bd.settings.VmSettings;


/**
 * A Specializer defines when a node can be used as a eager primitive, how
 * it is to be instantiated, and acts as factory for them.
 *
 * @param <T> the type of the nodes created by this specializer
 * @param <Context> the type of the context object
 * @param <ExprT> the root type of expressions used by the language
 */
public class Specializer<T, Context, ExprT> {
  protected final Context                    context;
  protected final Primitive                  prim;
  protected final NodeFactory<T>             fact;
  private final NodeFactory<? extends ExprT> extraChildFactory;

  @SuppressWarnings("unchecked")
  public Specializer(final Primitive prim, final NodeFactory<T> fact, final Context context) {
    this.prim = prim;
    this.fact = fact;
    this.context = context;

    if (prim.extraChild() == NoChild.class) {
      extraChildFactory = null;
    } else {
      try {
        extraChildFactory =
            (NodeFactory<? extends ExprT>) prim.extraChild().getMethod("getInstance")
                                               .invoke(null);
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

    if (VmSettings.NODES_REQUIRE_EAGER_WRAPPER_BOOL) {
      args += 1;
    }

    if (VmSettings.NODES_REQUIRE_SOURCE_SECTION) {
      args += 1;
    }

    return args +
        (extraChildFactory != null ? 1 : 0) +
        (prim.requiresArguments() ? 1 : 0) +
        (prim.requiresContext() ? 1 : 0);
  }

  public T create(final Object[] arguments, final ExprT[] argNodes,
      final SourceSection section, final boolean eagerWrapper) {
    assert arguments == null || arguments.length == argNodes.length;
    int numArgs = numberOfNodeConstructorArguments(argNodes);

    Object[] ctorArgs = new Object[numArgs];
    int offset = 0;

    if (VmSettings.NODES_REQUIRE_EAGER_WRAPPER_BOOL) {
      ctorArgs[offset] = eagerWrapper;
      offset += 1;
    }

    if (VmSettings.NODES_REQUIRE_SOURCE_SECTION) {
      ctorArgs[offset] = section;
      offset += 1;
    }

    if (prim.requiresContext()) {
      ctorArgs[offset] = context;
      offset += 1;
    }

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
      ctorArgs[offset] = extraChildFactory.createNode(null, null);
      offset += 1;
    }

    return fact.createNode(ctorArgs);
  }
}
