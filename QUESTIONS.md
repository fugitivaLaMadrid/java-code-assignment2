# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
yes, i caould see that is a mix between DbWareHOuse which is database entity and Warehouse is DTO 
Conversion between this two can make human errors and make code more complex to read

for this an posible approach is keep the db entities and dto model separate. 

Another was WarehouseREsourceImpl which us a warehouseRepositoty.list(query) Rest should do not know what is in the query details

Add new service layer convert DbWarehouse to Warehouse dto and vice versa

@Inject replace wiht constructor injection  help to improuve testability

my suggestion is to have a service layer between resource and repository to handle business logic and data transformation
```

----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```

to have a long-live project im favor of openapi for alli (Warehouse, Product and Store) it reduces human error and make easier to mantain the code
but for a short-live project or a prototype direct coding can be faster to implement
OpenAPI Pros:
- Consistency: Ensures uniformity across different API endpoints.
- Documentation: Auto-generates up-to-date documentation.
- Client Generation: Facilitates client SDK generation for various languages.
- Validation: Built-in request/response validation. 
OpenAPI Cons:
- Learning Curve: Requires understanding of OpenAPI specifications.
- Overhead: May introduce additional complexity for simple APIs.
- Tooling Dependency: Relies on tools for generation and validation.txt
Direct Coding Pros:
- Speed: Faster for small or simple APIs.
- Flexibility: More control over implementation details.
- Simplicity: Less overhead for straightforward use cases.
Direct Coding Cons:
- Inconsistency: Risk of diverging implementations.
- Documentation Drift: Manual documentation can become outdated.txt
- Maintenance Burden: More effort to maintain and update.txt
Overall, for maintainability and scalability, I would choose OpenAPI for all endpoints.

```