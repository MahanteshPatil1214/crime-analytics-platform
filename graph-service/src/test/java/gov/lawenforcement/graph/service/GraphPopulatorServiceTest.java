package gov.lawenforcement.graph.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionContext;
import org.neo4j.driver.TransactionCallback;
import org.neo4j.driver.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphPopulatorServiceTest {

    private GraphPopulatorService graphPopulatorService;

    @Mock
    private JdbcTemplate jdbc;
    @Mock
    private Driver neo4jDriver;
    @Mock
    private Session session;
    @Mock
    private TransactionContext tx;

    @BeforeEach
    void setUp() {
        graphPopulatorService = new GraphPopulatorService(jdbc, neo4jDriver);
    }

    @Test
    void toNeo4jDouble_bigDecimal_convertsToDouble() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        BigDecimal bd = new BigDecimal("12.3456789");
        Object result = method.invoke(graphPopulatorService, bd);

        assertInstanceOf(Double.class, result);
        assertEquals(12.3456789, (Double) result, 0.0000001);
    }

    @Test
    void toNeo4jDouble_integer_convertsToDouble() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, 42);

        assertInstanceOf(Double.class, result);
        assertEquals(42.0, (Double) result, 0.001);
    }

    @Test
    void toNeo4jDouble_long_convertsToDouble() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, 100L);

        assertInstanceOf(Double.class, result);
        assertEquals(100.0, (Double) result, 0.001);
    }

    @Test
    void toNeo4jDouble_float_convertsToDouble() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, 3.14f);

        assertInstanceOf(Double.class, result);
        assertEquals(3.140000104904175, (Double) result, 0.001);
    }

    @Test
    void toNeo4jDouble_string_returnsAsIs() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, "not-a-number");

        assertInstanceOf(String.class, result);
        assertEquals("not-a-number", result);
    }

    @Test
    void toNeo4jDouble_null_returnsNull() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jDouble", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, (Object) null);

        assertNull(result);
    }

    @Test
    void toNeo4jString_convertsToString() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jString", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, BigDecimal.valueOf(123.45));
        assertEquals("123.45", result);
    }

    @Test
    void toNeo4jString_null_returnsNull() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jString", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(graphPopulatorService, (Object) null);
        assertNull(result);
    }

    @Test
    void toNeo4jString_date_returnsString() throws Exception {
        Method method = GraphPopulatorService.class.getDeclaredMethod("toNeo4jString", Object.class);
        method.setAccessible(true);

        java.time.LocalDate date = java.time.LocalDate.of(2024, 1, 15);
        Object result = method.invoke(graphPopulatorService, date);
        assertEquals("2024-01-15", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getStats_executesReadAndReturnsMap() {
        when(neo4jDriver.session()).thenReturn(session);
        doAnswer(invocation -> {
            TransactionCallback<Void> callback = invocation.getArgument(0);
            return callback.execute(tx);
        }).when(session).executeRead(any(TransactionCallback.class));

        org.neo4j.driver.Result nodeResult = mock(org.neo4j.driver.Result.class);
        when(nodeResult.hasNext()).thenReturn(false);

        doReturn(nodeResult).when(tx).run(contains("labels(n)"));

        org.neo4j.driver.Result relResult = mock(org.neo4j.driver.Result.class);
        when(relResult.hasNext()).thenReturn(false);

        doReturn(relResult).when(tx).run(contains("type(r)"));

        Map<String, Object> stats = graphPopulatorService.getStats();

        assertNotNull(stats);
        assertTrue(stats.containsKey("nodes"));
        assertTrue(stats.containsKey("relationships"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getStats_withNodeAndRelCounts() {
        when(neo4jDriver.session()).thenReturn(session);
        doAnswer(invocation -> {
            TransactionCallback<Void> callback = invocation.getArgument(0);
            return callback.execute(tx);
        }).when(session).executeRead(any(TransactionCallback.class));

        Value labelValue = mock(Value.class);
        when(labelValue.asList(any(Function.class))).thenReturn(List.of("Person"));

        Value countValue = mock(Value.class);
        when(countValue.asInt()).thenReturn(10);

        org.neo4j.driver.Record nodeRecord = mock(org.neo4j.driver.Record.class);
        when(nodeRecord.get("label")).thenReturn(labelValue);
        when(nodeRecord.get("count")).thenReturn(countValue);

        org.neo4j.driver.Result nodeResult = mock(org.neo4j.driver.Result.class);
        when(nodeResult.hasNext()).thenReturn(true, false);
        when(nodeResult.next()).thenReturn(nodeRecord);

        doReturn(nodeResult).when(tx).run(contains("labels(n)"));

        org.neo4j.driver.Result relResult = mock(org.neo4j.driver.Result.class);
        when(relResult.hasNext()).thenReturn(false);

        doReturn(relResult).when(tx).run(contains("type(r)"));

        Map<String, Object> stats = graphPopulatorService.getStats();

        assertNotNull(stats);
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) stats.get("nodes");
        assertEquals(1, nodes.size());
        assertEquals(List.of("Person"), nodes.get(0).get("label"));
        assertEquals(10, nodes.get(0).get("count"));
    }
}
