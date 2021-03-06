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
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isNumber()'}"),
                                          new JSONObject("{'key': '134123'}"));

        // THEN
        assertTrue(actual);

    }

    @org.junit.Test
    public void testExpectedNotExpressionActualIsExpressionShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': '134123'}"),
                                          new JSONObject("{'key': 'isNumber()'}"));

        // THEN
        assertFalse(actual);

    }

    @org.junit.Test
    public void testExpectedExpressionIsNumberActualIsArrayShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isNumber()'}"),
                                          new JSONObject("{'key': ['134123']}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsObjectActualIsShouldReturnTrue() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{'key': 'isObject()'}");
        JSONObject actualResponse = new JSONObject("{'key': {}}");

        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsObjectActualIsNotShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isObject()'}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsObjectActualIsStringShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isObject()'}"),
                                          new JSONObject("{'key': 'value'}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsArrayActualIsShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isArray()'}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsArrayActualIsNotShouldReturnFalse() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isArray()'}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsPresentActualIsObjectShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isPresent()'}"),
                                          new JSONObject("{'key': {}}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsPresentActualIsArrayShouldReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isPresent()'}"),
                                          new JSONObject("{'key': []}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExpectedExpressionIsPresentActualIsStringReturnTrue() {
        // GIVEN
        // WHEN
        Boolean actual = underTest.equals(new JSONObject("{'key': 'isPresent()'}"),
                                          new JSONObject("{'key': 'value'}"));

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testEx() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{'kind': 'COMMENT', 'items': [{'id': 'isNumber', 'comment': 'isString'}]}");
        JSONObject actualResponse = new JSONObject("{'kind': 'COMMENT', 'items': [{'id': 'isNumber', 'comment': 'isString'}]}");

        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testEx2() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{'kind': 'COMMENT', 'items': [{'id': 'isNumber()', 'comment': 'isString()'}]}");
        JSONObject actualResponse = new JSONObject("{'kind': 'COMMENT', 'items': [{'id': 'isNumber()', 'comment': 'isString()'}]}");

        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExWithMatchingIsNumberShouldReturnTrue() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{'items': [{'id': 'isNumber()'}]}");
        JSONObject actualResponse = new JSONObject("{'items': [{'id': '123'}]}");

        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertTrue(actual);
    }

    @org.junit.Test
    public void testExWWhenExpectedObjectIsCompletelyDifferentFromActualShouldReturnFalse() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{" +
                                                             "'status':\n" + "'ACTIVE',\n" +
                                                             "'updated':\n" + "'2016-09-15',\n" +
                                                             "'url':\n" + "'isString()'\n" +
                                                             "}");

        JSONObject actualResponse = new JSONObject("{" +




                                                           "'name':\n" + "'My Blog Url',\n" +
                                                           "'pages':\n" + "'isObject()',\n" +
                                                           "'posts':\n" + "'mypostitemitemmypost',\n" +
                                                           "'published':\n" + "'2016-08-03',\n" +
                                                           "'status':\n" + "'ACTIVE',\n" +

                                                           "}");
        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertFalse(actual);
    }

    @org.junit.Test
    public void testExWWhenExpectedIsObjectHasEqualAfterwardsShouldReturnFalse() {
        // GIVEN
        JSONObject expectedResponse = new JSONObject("{" +
                                                             "'status':\n" + "'ACTIVE',\n" +
                                                             "'pages':\n" + "'isObject()',\n" +
                                                             "'updated':\n" + "'2016-09-15',\n" +
                                                             "}");

        JSONObject actualResponse = new JSONObject("{" +
                                                           "'status':\n" + "'ACTIVE',\n" +
                                                           "'pages':\n" + "'myresponsenotobject',\n" +
                                                           "'updated':\n" + "'2016-09-15',\n" +

                                                           "}");
        // WHEN
        Boolean actual = underTest.equals(expectedResponse, actualResponse);

        // THEN
        assertFalse(actual);
    }

}
