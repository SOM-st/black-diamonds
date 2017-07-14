package bd.primitives;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;


/**
 * A PrimitiveLoader provides the basic functionality to load the information about primitives
 * from the annotation, based on a list of {@link NodeFactory} objects.
 *
 * @param <Context> the type of the context object
 * @param <ExprT> the root type of expressions used by the language
 * @param <Id> the type of the identifiers used for mapping to primitives, typically some form
 *          of interned string construct
 * @param <EagerT> the type of eagerly specializable nodes
 */
public abstract class PrimitiveLoader<Context, ExprT, Id, EagerT> {

  protected final Context context;

  /** Primitives for selector. */
  protected final HashMap<Id, Specializer<? extends ExprT, Context, ExprT>> eagerPrimitives;

  /**
   * Initializes the PrimitiveLoader.
   *
   * @param context the object representing the language's context
   *
   */
  protected PrimitiveLoader(final Context context) {
    this.context = context;
    this.eagerPrimitives = new HashMap<>();
  }

  /** Returns all node factories that might contain primitives. */
  protected abstract List<NodeFactory<? extends ExprT>> getFactories();

  /**
   * Lookup a specializer for use during parsing.
   *
   * <p>
   * It is identified by a selector or id, and the argument nodes. If the lookup is successful,
   * the primitive allows in-parser specialization, and the argument nodes match the
   * expectations, than a specializer is returned. otherwise, null is returned.
   */
  @SuppressWarnings("unchecked")
  public final Specializer<EagerT, Context, ExprT> getParserSpecializer(final Id selector,
      final ExprT[] argNodes) {
    Specializer<? extends ExprT, Context, ExprT> specializer = eagerPrimitives.get(selector);
    if (specializer != null && specializer.inParser() && specializer.matches(null, argNodes)) {
      return (Specializer<EagerT, Context, ExprT>) specializer;
    }
    return null;
  }

  /**
   * Lookup a specializer for specialization during execution.
   *
   * <p>
   * If one is found for the given selector/id, it is checked whether the run-time arguments as
   * well as the argument nodes match for the specialization. If they match, the specializer is
   * returned, null is returned otherwise.
   */
  @SuppressWarnings("unchecked")
  public final Specializer<EagerT, Context, ExprT> getEagerSpecializer(final Id selector,
      final Object[] arguments, final ExprT[] argumentNodes) {
    Specializer<? extends ExprT, Context, ExprT> specializer =
        eagerPrimitives.get(selector);
    if (specializer != null && specializer.matches(arguments, argumentNodes)) {
      return (Specializer<EagerT, Context, ExprT>) specializer;
    }
    return null;
  }

  /**
   * Create a {@link Specializer} for the given {@link Primitive}.
   */
  @SuppressWarnings("unchecked")
  protected final <T> Specializer<T, Context, ExprT> createSpecializer(final Primitive prim,
      final NodeFactory<? extends ExprT> factory) {
    try {
      // Try with erased type signature
      return prim.specializer()
                 .getConstructor(Primitive.class, NodeFactory.class, Object.class)
                 .newInstance(prim, factory, context);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      try {
        // Try with concrete type signature
        return prim.specializer()
                   .getConstructor(Primitive.class, NodeFactory.class, context.getClass())
                   .newInstance(prim, factory, context);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Get the {@link Primitive} annotation from a {@link NodeFactory}.
   */
  protected final Primitive[] getPrimitiveAnnotation(
      final NodeFactory<? extends ExprT> factory) {
    Class<?> nodeClass = factory.getNodeClass();
    return nodeClass.getAnnotationsByType(Primitive.class);
  }
}
