//package com.klimovich.myRestService.memory;
//
//import com.klimovich.myRestService.entity.Equation;
//import com.klimovich.myRestService.entity.MyResponse;
//import com.klimovich.myRestService.entity.MyResponseStatus;
//import com.klimovich.myRestService.entity.Pair;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//class CacheTest {
//    private AutoCloseable closeable;
//    @Mock
//    private Map<Equation, MyResponse> dataStorage;// = new HashMap<Equation, MyResponse>();
//    @InjectMocks
//    private Cache cache = new Cache();
//
//    @BeforeEach
//    void initService() {
//        closeable = MockitoAnnotations.openMocks(this);
//    }
//    @AfterEach
//    void closeService() throws Exception {
//        closeable.close();
//    }
//
//
////    public void saveEquation(@NotNull MyResponse response) {
////        Equation equation = new Equation();
////        equation.setNumbers(response.getEquation().getNumbers());
////        equation.setSegment(response.getEquation().getSegment());
////        equation.setAnswer(null);
////        dataStorage.put(equation, response);
////    }
////    public MyResponse getResponse(Equation equation) {
////        return dataStorage.get(equation);
////    }
////    public Integer getCacheSize() {
////        return dataStorage.size();
////    }
////    public List<MyResponse> getAllSavedEquations() {
////        List<MyResponse> listOfResponses = new ArrayList<MyResponse>();
////        dataStorage.forEach((key, value) -> listOfResponses.add(value));
////        return listOfResponses;
////    }
//
////    @Test
////    void testSaveEquation() {
////        Pair numbers = new Pair(1, 2);
////        Pair segment = new Pair(0, 5);
////        Integer answer = 1;
////        Equation equation = new Equation(numbers, segment);
////        equation.setAnswer(null);
////        MyResponse response = new MyResponse(
////                new Equation(numbers, segment, answer),
////                new MyResponseStatus(200, HttpStatus.OK));
////        dataStorage.put(equation, response);
////        when(dataStorage.size()).thenReturn(1);
////        assertEquals(1, dataStorage.size());
////    }
//
//    @Test
//    void testGetResponse() {
//    }
//
//    @Test
//    void testGetCacheSize() {
//        Pair numbers = new Pair(1, 2);
//        Pair segment = new Pair(0, 5);
//        Integer answer = 1;
//
//        Equation equation = new Equation(numbers, segment);
//        equation.setAnswer(null);
//        MyResponse response = new MyResponse(
//                new Equation(numbers, segment, answer),
//                new MyResponseStatus(200, HttpStatus.OK));
//        //when(dataStorage.put(equation, response)).thenReturn(response);
//        dataStorage.put(equation, response);
//       // dataStorage.put(equation, response);
//        when(dataStorage.size()).thenReturn(1);
//
//        assertEquals(1, cache.getCacheSize());// cache.getCacheSize());
//    }
//
//    @Test
//    void testGetAllSavedEquations() {
//    }
//}