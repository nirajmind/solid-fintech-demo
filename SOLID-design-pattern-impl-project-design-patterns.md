Here is a report on the key design patterns implemented in our solid-project.

This architecture leverages several foundational and structural design patterns to achieve the goals of SOLID, primarily focusing on decoupling, testability, and clear separation of concerns.

---

### **1\. 🧩 Dependency Injection (DI)**

* **Definition:** A specific implementation of the **Inversion of Control (IoC)** principle. Instead of an object creating its own dependencies, the dependencies are "injected" into it by an external container (the Spring ApplicationContext).  
* **Implementation in Our Project:** This is the most fundamental pattern used. We exclusively use **constructor injection** in every class that has dependencies:  
  * UserController depends on the UserService **interface**.  
  * UserServiceImpl depends on the UserRepository.  
  * SecurityConfig depends on the JwtRequestFilter.  
  * JwtRequestFilter depends on JwtUtil and UserDetailsService.  
* **Purpose:** This is the "D" in SOLID (Dependency Inversion). It massively **decouples** our components. The UserController has no idea that a UserServiceImpl exists; it only knows about the UserService interface. This makes it trivial to test (as we saw with mocks) and to swap out implementations later.

---

### **2\. 🏭 Factory Pattern**

* **Definition:** A pattern that provides an interface for creating objects in a superclass, but allows subclasses to alter the type of objects that will be created. We use a variant, the **Factory Method**.  
* **Implementation in Our Project:** Our @Configuration files (ApplicationConfig and SecurityConfig) are full of factory methods. Any method annotated with **@Bean** is a factory.  
  * The userDetailsService() method in ApplicationConfig is a factory that produces a UserDetailsService bean.  
  * The authenticationManager() method in SecurityConfig is a factory that requests the AuthenticationConfiguration and exposes the AuthenticationManager as a bean.  
* **Purpose:** This abstracts and centralizes the object creation logic. Spring manages the lifecycle of these beans, and any other class can simply "ask for" the bean (via DI) without knowing *how* it was constructed.

---

### **3\. 🏗️ Builder Pattern**

* **Definition:** A pattern used to construct complex objects step-by-step. It separates the construction of an object from its representation, allowing the same construction process to create different representations.  
* **Implementation in Our Project:** We used this pattern provided by Spring Security to create our in-memory user:  
  * In ApplicationConfig, the line User.builder().username(...).password(...).roles(...).build() is a perfect example.  
* **Purpose:** It creates highly **readable** and maintainable code for object setup. Instead of a messy constructor with many parameters (e.g., new User("user", "pass", "ROLE\_USER", true, true, ...)), we get a fluent, descriptive API.

---

### **4\. 🔀 Facade Pattern**

* **Definition:** Provides a simplified, unified interface to a more complex set of subsystems or interfaces.  
* **Implementation in Our Project:** Our **UserService interface** (and its implementation UserServiceImpl) acts as a Facade.  
  * The UserController just calls userService.getUserById(1L).  
  * It doesn't need to know about the complex logic hidden *inside* that method, which includes:  
    1. Calling the UserRepository.  
    2. Handling the Optional return.  
    3. Throwing a UserNotFoundException if it's empty.  
    4. Mapping the User entity to a UserDTO.  
* **Purpose:** It **hides complexity** from the "client" (the controller) and provides a clean, simple API for the business logic.

---

### **5\. 🛡️ Repository Pattern (Data Access Object \- DAO)**

* **Definition:** An abstraction layer that separates the business logic from the data persistence layer.  
* **Implementation in Our Project:** The **UserRepository interface** *is* the repository.  
  * We simply extend JpaRepository\<User, Long\>.  
* **Purpose:** This is a powerful abstraction. Our UserServiceImpl has no idea we are using JPA, Hibernate, or an H2 database. It just calls userRepository.findById(). We could (in theory) replace the entire persistence layer with MongoDB, and the UserServiceImpl code would **not change**, as long as the new repository still fulfills the UserRepository contract.

---

### **6\. ⛓️ Filter Pattern (Chain of Responsibility)**

* **Definition:** A pattern that creates a chain of objects to process a request. Each object in the chain can process the request or pass it along to the next object in the chain.  
* **Implementation in Our Project:** This is the core of Spring Security.  
  * Our **JwtRequestFilter** is one link in a long SecurityFilterChain.  
  * When a request comes in, it passes through many filters (for CORS, headers, etc.). It then hits our JwtRequestFilter, which validates the token and sets the SecurityContextHolder. Finally, it passes the request down the chain (filterChain.doFilter(request, response)) until it eventually hits the controller.  
* **Purpose:** It allows us to handle **cross-cutting concerns** (like security, logging, or compression) in a clean, modular way without polluting our controller or service logic.