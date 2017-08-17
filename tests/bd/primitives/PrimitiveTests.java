package bd.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import bd.testsetup.AddNodeFactory;
import bd.testsetup.ExprNode;


public class PrimitiveTests {

  private final Primitives ps = new Primitives(null);

  @Test
  public void testPrimitiveAnnotation() {
    Primitive[] annotations = ps.getPrimitiveAnnotation(AddNodeFactory.getInstance());
    Primitive p = annotations[0];

    assertEquals("Int", p.className());
    assertEquals("+", p.primitive());
    assertEquals(1, annotations.length);
  }

  @Test
  public void testEagerSpecializer() {
    Specializer<Void, ExprNode, String> s = ps.getEagerSpecializer("+", null, null);
    assertNotNull(s);

    assertEquals("AddNodeFactory", s.getName());
  }

  @Test
  public void testParserSpecializer() {
    Specializer<Void, ExprNode, String> s = ps.getParserSpecializer("+", null);
    assertNotNull(s);

    assertEquals("AddNodeFactory", s.getName());
  }
}
