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
 */
public abstract class PrimitiveLoader<Context, ExprT, Id> {

  protected final Context context;

  /** Primitives for selector. */
  private final HashMap<Id, Specializer<Context, ExprT, Id>> eagerPrimitives;

  /**
   * Initializes the PrimitiveLoader.
   *
   * @param context the object representing the language's context
   */
  protected PrimitiveLoader(final Context context) {
    this.context = context;
    this.eagerPrimitives = new HashMap<>();
  }

  /** Returns all node factories that might contain primitives. */
  protected abstract List<NodeFactory<? extends ExprT>> getFactories();

  /**
   * Setup the lookup data structures for VM primitive registration as well as
   * eager primitive replacement.
   *
   * <p>
   * This methods should be called when the constructor completes.
   */
  protected void initialize() {
    List<NodeFactory<? extends ExprT>> primFacts = getFactories();
    for (NodeFactory<? extends ExprT> primFact : primFacts) {
      Primitive[] prims = getPrimitiveAnnotation(primFact);
      if (prims != null) {
        for (bd.primitives.Primitive prim : prims) {
          Specializer<Context, ExprT, Id> specializer = createSpecializer(prim, primFact);
          registerPrimitive(prim, specializer);

          if (!("".equals(prim.selector()))) {
            Id selector = getId(prim.selector());
            assert !eagerPrimitives.containsKey(
                selector) : "clash of selectors and eager specialization";
            eagerPrimitives.put(selector, specializer);
          }
        }
      }
    }
  }

  /**
   * {@link #initialize()} iterates over all factories and consequently primitives provided by
   * {@link #getFactories()}. These primitives are exposed by this method to allow custom
   * handling, for instance to install them in language-level classes.
   *
   * @param prim the primitive annotation
   * @param specializer the specializer object for this primitive
   */
  protected abstract void registerPrimitive(Primitive prim,
      Specializer<Context, ExprT, Id> specializer);

  /**
   * Gets a Java string and needs to return an identifier that is used to map to the primitive.
   * Typically, this is some form of symbol or interned string that can be safely compared with
   * reference equality.
   */
  protected abstract Id getId(String id);

  /**
   * Lookup a specializer for use during parsing.
   *
   * <p>
   * It is identified by a selector or id, and the argument nodes. If the lookup is successful,
   * the primitive allows in-parser specialization, and the argument nodes match the
   * expectations, than a specializer is returned. otherwise, null is returned.
   */
  public final Specializer<Context, ExprT, Id> getParserSpecializer(final Id selector,
      final ExprT[] argNodes) {
    Specializer<Context, ExprT, Id> specializer = eagerPrimitives.get(selector);
    if (specializer != null && specializer.inParser() && specializer.matches(null, argNodes)) {
      return specializer;
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
  public final Specializer<Context, ExprT, Id> getEagerSpecializer(final Id selector,
      final Object[] arguments, final ExprT[] argumentNodes) {
    Specializer<Context, ExprT, Id> specializer = eagerPrimitives.get(selector);
    if (specializer != null && specializer.matches(arguments, argumentNodes)) {
      return specializer;
    }
    return null;
  }

  /**
   * Create a {@link Specializer} for the given {@link Primitive}.
   */
  @SuppressWarnings("unchecked")
  private <T> Specializer<Context, ExprT, Id> createSpecializer(final Primitive prim,
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
  Primitive[] getPrimitiveAnnotation(final NodeFactory<? extends ExprT> factory) {
    Class<?> nodeClass = factory.getNodeClass();
    return nodeClass.getAnnotationsByType(Primitive.class);
  }
}
