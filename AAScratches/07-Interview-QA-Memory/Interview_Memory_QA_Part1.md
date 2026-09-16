# Interview Memory Q&A — Part 1

Numbered, mixed-topic memory notes (Java 21 switch, Streams collectors, Spring Boot config, Spring Security, Spring Data JPA, GraphQL, MockMvc testing) kept in the original order and with the original item numbers.

## Table of Contents

- [1. Calling a specific interface's default method when two interfaces define it](#1-calling-a-specific-interfaces-default-method-when-two-interfaces-define-it)
- [2. Logging levels in application.properties](#2-logging-levels-in-applicationproperties)
- [3. Activating a Spring profile](#3-activating-a-spring-profile)
- [4. @Valid vs @Validated](#4-valid-vs-validated)
- [5. Constructor injection with Lombok @RequiredArgsConstructor](#5-constructor-injection-with-lombok-requiredargsconstructor)
- [4. Pattern-matching switch with a when guard](#4-pattern-matching-switch-with-a-when-guard)
- [5. Group employees by department and pick the highest-paid (Collectors.maxBy)](#5-group-employees-by-department-and-pick-the-highest-paid-collectorsmaxby)
- [6. Group by department and map to the highest-paid employee's name (collectingAndThen)](#6-group-by-department-and-map-to-the-highest-paid-employees-name-collectingandthen)
- [7. Group by department to a list of employee names (Collectors.mapping)](#7-group-by-department-to-a-list-of-employee-names-collectorsmapping)
- [8. Resilience4j circuit breaker, stateless JWT SecurityFilterChain and @EntityGraph for N+1](#8-resilience4j-circuit-breaker-stateless-jwt-securityfilterchain-and-entitygraph-for-n1)
- [9. GraphQL @QueryMapping / @MutationMapping with @Argument](#9-graphql-querymapping--mutationmapping-with-argument)
- [10. JpaRepository with JpaSpecificationExecutor and a paged findAll](#10-jparepository-with-jpaspecificationexecutor-and-a-paged-findall)
- [11. PageRequest with Sort](#11-pagerequest-with-sort)
- [12. Building a JPA Specification with predicates and a LEFT join](#12-building-a-jpa-specification-with-predicates-and-a-left-join)
- [13. @ControllerAdvice global exception handler and orElseThrow in the service](#13-controlleradvice-global-exception-handler-and-orelsethrow-in-the-service)
- [14. Controller method with @RequestBody, @PathVariable and @RequestParam](#14-controller-method-with-requestbody-pathvariable-and-requestparam)
- [15. MockMvc integration test with custom headers, params and a JSON body](#15-mockmvc-integration-test-with-custom-headers-params-and-a-json-body)
- [16. Fetching child collections with @EntityGraph and JOIN FETCH](#16-fetching-child-collections-with-entitygraph-and-join-fetch)
- [17. Bidirectional @OneToMany / @ManyToOne mapping](#17-bidirectional-onetomany--manytoone-mapping)
- [18. Entity with @Enumerated(EnumType.STRING) and a jsonb column via @JdbcTypeCode](#18-entity-with-enumeratedenumtypestring-and-a-jsonb-column-via-jdbctypecode)
- [19. Empty item](#19-empty-item)

## 1. Calling a specific interface's default method when two interfaces define it

Default method in two interfaces A, B and class C implements both A, B — how to make a call?

```java
@Override
public void m1 ( ) {
    A.super.m1 ( );
}
```

## 2. Logging levels in application.properties

Shorthand noted in the source: `llr`

```properties
# Root logging level
logging.level.root=INFO
# Package-specific logging level
logging.level.com.example.myapp=DEBUG
# Specific class logging level
logging.level.com.example.myapp.service.UserService=TRACE
```

## 3. Activating a Spring profile

```properties
spring.profiles.active
```

## 4. @Valid vs @Validated

- `@Valid` is used for Model or DTO validation.
- `@Validated` is used to validate attribute path, URL path or query params.

## 5. Constructor injection with Lombok @RequiredArgsConstructor

```java
@RequiredArgsConstructor
private final EmployeeRepository employeeRepository;
```

## 4. Pattern-matching switch with a when guard

(Item number 4 is used a second time in the source.)

```java
static String check(Object o) {
    return switch (o) {
        case String s -> "string";
        case Integer i when i > 10 -> "big int"; // ✅ 'when', not '&&'
        default -> "other";
    };
}
```

## 5. Group employees by department and pick the highest-paid (Collectors.maxBy)

(Item number 5 is used a second time in the source.)

```java
emplList.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                ));
```

## 6. Group by department and map to the highest-paid employee's name (collectingAndThen)

```java
Map<String, String> result = emplList.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.collectingAndThen(
            Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
            optEmp -> optEmp.map(Employee::getName).orElse(null)
        )
    ));
```

## 7. Group by department to a list of employee names (Collectors.mapping)

```java
Map<String, List<String>> result = emplList.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
```

## 8. Resilience4j circuit breaker, stateless JWT SecurityFilterChain and @EntityGraph for N+1

```java
// Circuit breaker (Resilience4j)
@CircuitBreaker(name="catalog", fallbackMethod="fallback")
public Product getProduct(String id) { ... }

// SecurityFilterChain (stateless JWT)
@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
  return http.csrf(csrf->csrf.disable())
    .sessionManagement(sm->sm.sessionCreationPolicy(STATELESS))
    .authorizeHttpRequests(auth->auth.requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
    .build();
}

// EntityGraph to kill N+1
@EntityGraph(attributePaths = {"items", "customer"})
List<Order> findByStatus(Status status);
```

## 9. GraphQL @QueryMapping / @MutationMapping with @Argument

```java
@MutationMapping
@QueryMapping
public GqlProductResponse getProduct(@Argument("supplierId" UUID supplierId){

}
```

## 10. JpaRepository with JpaSpecificationExecutor and a paged findAll

```java
@Repository
public interface SupplierSearchRepository extends JpaRepository<SupplierSearch,UUID>,
    JpaSpecificationExecutor<SupplierSearch>{
    Page<SupplierSearch> findAll(Specification<SupplierSearch> spec,Pageable pageable);
    }
```

## 11. PageRequest with Sort

```java
PageRequest pageRequest= PageRequest
        .of(pageNumber,pageSize,Sort.by(Sort.Direction.ASC,request.getSortedBy())
```

## 12. Building a JPA Specification with predicates and a LEFT join

```java
public static Specification<SupplierSearch> buildSupplierSpecification(RequestFilter filter){

return (root,criterQuery,criteriaBuilder)-> {
CriteriaQuery<?> query1=query.distinct(true);
List<Predicate> predicates= new ArrayList<>();
Join<SupplierSearch,SupplierOfferSearch> supplierOffers= root.join(suppliesOffers,LEFT);
    }
}
```

## 13. @ControllerAdvice global exception handler and orElseThrow in the service

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = MovieNotFoundException.class)
    public ResponseEntity<?> movieNotFound(MovieNotFoundException ex) {

        return ResponseEntity.of(Optional.of(ex.getMessage()));
    }
}

// logic in service
Movie movie1 = movie
         .orElseThrow(() -> new MovieNotFoundException("movie not found with " + uuid));
```

## 14. Controller method with @RequestBody, @PathVariable and @RequestParam

```java
@GetMapping(path = "/{id}")
public ResponseEntity<List<MovieDTO>> getMovies(@RequestBody MovieDTO movie,
                                                @PathVariable UUID id,
                                                @RequestParam String heroName) {

    return ResponseEntity.ok(movieService.getMovies());
}
```

## 15. MockMvc integration test with custom headers, params and a JSON body

```java
package com.real.interview.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.real.interview.dto.MovieDTO;
import com.real.interview.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @Test
    void testGetMovies_WithHeaders_ReturnsMovieListSuccessfully() throws Exception {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String heroName = "Superman";

        // Prepare mock response from service
        MovieDTO movie1 = new MovieDTO();
        movie1.setTitle("Man of Steel");

        MovieDTO movie2 = new MovieDTO();
        movie2.setTitle("Batman v Superman");

        List<MovieDTO> mockResponse = List.of(movie1, movie2);
        Mockito.when(movieService.getMovies()).thenReturn(mockResponse);

        // Prepare request body (though GET usually doesn't have one)
        MovieDTO requestMovie = new MovieDTO();
        requestMovie.setTitle("Dummy Request Movie");

        // Prepare custom headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer dummy-jwt-token");
        headers.add("X-Request-Id", UUID.randomUUID().toString());
        headers.add("Accept-Language", "en-US");

        // Act
        MvcResult result = mockMvc.perform(get("/{id}", movieId)
                        .headers(headers)
                        .param("heroName", heroName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMovie)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String jsonResponse = result.getResponse().getContentAsString();
        List<MovieDTO> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertThat(responseList)
                .isNotNull()
                .hasSize(2)
                .extracting(MovieDTO::getTitle)
                .containsExactly("Man of Steel", "Batman v Superman");

        Mockito.verify(movieService, Mockito.times(1)).getMovies();

        // Optional: verify headers if controller echoes them back (example only)
        // assertThat(result.getResponse().getHeader("X-Response-Id")).isNotNull();
    }
}
```

## 16. Fetching child collections with @EntityGraph and JOIN FETCH

```java
@EntityGraph(attributePaths = {"employees"})
@Query("SELECT d from Department d where d.id= :id")
Optional<Department> findWithEmployees(@Param("id") Long id);

@Query("select d from Department d left join fetch d.employees where d.id= :id ")
Optional<Department> findDepartmentWithEmployees(@Param("id") Long id);

@EntityGraph(attributePaths = {"employees"})
List<Department> findAll();
```

## 17. Bidirectional @OneToMany / @ManyToOne mapping

```java
// One-to-many mapping (bidirectional)
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

// Many-to-one mapping (bidirectional)
// foreign key in many side
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id")
private Department department;
```

## 18. Entity with @Enumerated(EnumType.STRING) and a jsonb column via @JdbcTypeCode

```java
class ApprovalRequestEntity {

    @Enumerated(EnumType.STRING)
    @Column(name="TYPE")
    private RequestType type;

    @Column(name="edit_params",columnDefinition="jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private GqlSupplierEdit editParams;
}
```

## 19. Empty item

Item 19 has no content in the source file; the heading is kept so the numbering stays intact.
