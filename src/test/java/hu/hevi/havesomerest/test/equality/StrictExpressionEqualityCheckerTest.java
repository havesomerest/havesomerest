package hu.hevi.havesomerest.test.equality;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4ClassRunner.class)
public class StrictExpressionEqualityCheckerTest {

    private StrictExpressionEqualityChecker underTest;

    @Before
    public void setUp() {
        JsonObjectHelper jsonObjectHelper = new JsonObjectHelper();
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(jsonObjectHelper);

        underTest = new StrictExpressionEqualityChecker(expressionEvaluator, jsonObjectHelper);
    }

    @org.junit.Test
    public void testEmptyEqualsShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{}"),
                                          new JSONObject("{}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedEmptyNotEqualsActualNotEmptyShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{}"),
                                          new JSONObject("{'key': 'value'}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedNotEmptyNotEqualsActualEmptyShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'value'}"),
                                          new JSONObject("{}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedKeyNotNotEqualsActualKeyShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'value'}"),
                                          new JSONObject("{'card': 'value'}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedStringValueNotEqualsActualValueShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'value'}"),
                                          new JSONObject("{'key': 'price'}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedEmptyArrayEqualsActualEmptyArrayShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': []}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedEmptyArrayNotEqualsActualArrayShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': []}"),
                                          new JSONObject("{'key': ['value']}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedArrayNotEqualsActualEmptyArrayShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': ['value']}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedArrayNotEqualsActualArrayShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': ['value']}"),
                                          new JSONObject("{'key': ['price']}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedEmptyObejctEqualsActualEmptyObjectShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': {}}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedEmptyObejctEqualsActualObjectShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': {}}"),
                                          new JSONObject("{'key': {'key2' : 'value2'}}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedObejctEqualsActualEmptyObjectShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': {'key2' : 'value2'}}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedObejctEqualsActualObjectShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': {'key2' : 'value2'}}"),
                                          new JSONObject("{'key': {'key2' : 'value2'}}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedExpressionIsNumberActualIsShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isNumber()'}"),
                                          new JSONObject("{'key': '134123'}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedNotExpressionActualIsExpressionShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '134123'}"),
                                          new JSONObject("{'key': '#isNumber()'}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedExpressionIsNumberActualIsArrayShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isNumber()'}"),
                                          new JSONObject("{'key': ['134123']}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsObjectActualIsShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isObject()'}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsObjectActualIsNotShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isObject()'}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsArrayActualIsShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isArray()'}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsArrayActualIsNotShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '#isArray()'}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertFalse(actual);
    }
}
