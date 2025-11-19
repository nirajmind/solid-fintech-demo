This document serves as a comprehensive guide to the Cloud-Native Fintech Microservice you built. It covers the Architecture, Design Patterns, Security, and Infrastructure decisions.

Use this as your **"Cheat Sheet"** before the interview. It proves you can handle the full Software Development Life Cycle (SDLC).

---

# **🏛️ Project: Cloud-Native Secure Fintech Microservice**

**Objective:** Design and deploy a secure, scalable, containerized backend service using Java 17, Spring Boot, MongoDB, and Kubernetes.

---

## **1. 🛠️ Technology Stack & Architecture**

This project mimics a real-world banking environment (like ADIB's target state).

| Layer | Technology | Reason for Choice |
| :---- | :---- | :---- |
| **Language** | Java 17 | LTS version, records support, high performance. |
| **Framework** | Spring Boot 3 | Rapid development, vast ecosystem, "Convention over Configuration." |
| **Database** | MongoDB | Schema flexibility (NoSQL), high write throughput for user events. |
| **Security** | Spring Security \+ JWT | Stateless authentication (REST standard), scalable across microservices. |
| **Container** | Docker | "Build once, run anywhere." Dependency isolation. |
| **Orchestration** | Kubernetes (Minikube) | Automated deployment, scaling, self-healing (Cloud-Native standard). |
| **CI/CD** | Jenkins \+ SonarQube | Automated testing and code quality gates. |

### **🏗️ High-Level Architecture (Mermaid)**

Code snippet

graph TD  
    Client\[Client / Browser\] \--\>|HTTPS/LoadBalancer| K8s\[Kubernetes Cluster\]  
      
    subgraph "K8s Cluster (Minikube)"  
        Service\[LoadBalancer Service\] \--\> Pod1\[App Instance 1\]  
        Service \--\> Pod2\[App Instance 2\]  
          
        subgraph "Microservice Pod"  
            Filter\[Security Filter Chain\] \--\> Controller\[REST Controller\]  
            Controller \--\> ServiceLayer\[Service Layer\]  
        end  
          
        Pod1 \--\>|Persist Data| Mongo\[MongoDB Service\]  
        Pod2 \--\>|Persist Data| Mongo  
    end  
      
    Mongo \--\> PVC\[Persistent Volume\]

---

## **2. 🧩 Design Patterns Implemented**

This is crucial for the "Architect" portion of your interview.

### **A. SOLID Principles**

* **S (Single Responsibility):** Separated UserController (HTTP), UserService (Business Logic), and UserRepository (Data).  
* **O (Open/Closed):** Used UserService interface. We can swap implementations (e.g., AdminUserService) without changing the Controller.  
* **D (Dependency Injection):** Injected UserRepository into UserServiceImpl via constructor. This allowed us to switch from **H2 (SQL)** to **MongoDB (NoSQL)** without changing a single line of the Service logic.

### **B. Structural Patterns**

* **Repository Pattern:** UserRepository extends MongoRepository. It abstracts the raw database queries from the business logic.  
* **Filter Chain (Chain of Responsibility):** JwtRequestFilter sits in the Spring Security chain. It intercepts requests, validates tokens, and passes control to the next link.  
* **DTO (Data Transfer Object):** Used Java records (UserDTO, CreateUserRequest) to decouple the internal Database Entity from the external API contract.

---

## **3. 🔐 Security Architecture (JWT Flow)**

We implemented **Stateless Authentication**.

1. **Registration:** User POSTs credentials. Password is hashed using **BCrypt** and saved to MongoDB.  
2. **Login:** User POSTs credentials. AuthenticationManager verifies hash. If valid, we sign a **JWT (HS256)** containing the username and roles.  
3. **Access:** Client sends Authorization: Bearer \<token\>.  
4. **Validation:** JwtRequestFilter intercepts the request, validates the signature using the Secret Key, and sets the SecurityContext for that specific request thread.

---

## **4. 🚀 Infrastructure & DevOps Steps**

This demonstrates your ability to take code to production.

### **Step 1: Dockerization**

We used a **Multi-Stage** concept (locally building JAR, then packaging):

* **Base Image:** eclipse-temurin:17-jdk-alpine (Small, secure, production-ready).  
* **Efficiency:** We ignored target/ and .idea/ using .dockerignore and .gitignore.

### **Step 2: Kubernetes Manifests (k8s-full-stack.yaml)**

We defined the "Desired State" of our cluster:

* **PersistentVolumeClaim (PVC):** To ensure MongoDB data survives if the Pod crashes.  
* **Deployment:** Defines *how* to run the app (Image, Replicas, Env Vars).  
* **Service (LoadBalancer):** Exposes the app outside the cluster. We used minikube tunnel to simulate a real Cloud Load Balancer IP.

### **Step 3: Troubleshooting (STAR Method Stories)**

* **Situation:** The App kept crashing (CrashLoopBackOff) when deployed.  
* **Task:** Diagnose the failure.  
* **Action:** I ran kubectl logs and saw DataSource errors. I realized spring-boot-starter-data-jpa was still active, trying to connect to a SQL DB that didn't exist. I aggressively excluded the dependencies in pom.xml and SolidProjectApplication.java.  
* **Result:** The app started successfully, connecting purely to MongoDB.

---

## **5. 🧪 How to Demo (The "Script")**

If asked to demonstrate or explain your workflow:

1. **Start the Environment:**  
   PowerShell  
   minikube start  
   minikube tunnel

2. **Build & Deploy:**  
   PowerShell  
   \# Point shell to Minikube's Docker  
   & minikube \-p minikube docker\-env \-\-shell powershell | Invoke-Expression

   \# Build Image  
   docker build \-t solid\-project:latest .

   \# Deploy to K8s  
   kubectl apply \-f k8s\-full-stack.yaml

3. **Verify:**  
   PowerShell  
   kubectl get services  
   \# Copy the EXTERNAL-IP of solid-app-service

4. **Test Endpoint:**  
   * **POST** to http://\<EXTERNAL-IP\>:8080/api/users (Create User).  
   * **POST** to http://\<EXTERNAL-IP\>:8080/api/auth/login (Get JWT).  
   * **GET** to http://\<EXTERNAL-IP\>:8080/api/users/1 (Use JWT to fetch data).

---

## **6. 🔮 Future Improvements (Interview Brownie Points)**

If they ask "What would you add next?":

1. **ConfigMaps & Secrets:** Move the DB credentials and JWT Secret out of the code/YAML and into Kubernetes Secrets.  
2. **Liveness/Readiness Probes:** Add Spring Actuator (/actuator/health) and configure K8s probes so it doesn't send traffic if the DB connection is down.  
3. **Helm Charts:** Package the YAML files into a Helm Chart for versioned deployments.  
4. **Distributed Tracing:** Add Micrometer/Zipkin to trace requests as they move from the Gateway to the Service to the Database.


## **7. ⚙️ Operational Workflow & Troubleshooting (The "Real World" Guide)**

Use this section to demonstrate familiarity with Kubernetes daily operations and debugging.

### A. The "Cold Start" Protocol (Restarting the Environment)

Unlike Docker Compose, Minikube does not start automatically. When rebooting the machine, I follow this strict sequence to ensure the terminal talks to the correct Docker daemon:
Start the Cluster:
PowerShell
minikube start


Link Terminal to Cluster Docker (CRITICAL):
Why: By default, docker build uses the Windows Docker daemon. K8s can't see those images. This command points the terminal to Minikube's internal Docker daemon.
PowerShell
& minikube -p minikube docker-env --shell powershell | Invoke-Expression


Start the Network Tunnel:
Why: Required for LoadBalancer services to get an External IP (127.0.0.1) on Windows.
Command: minikube tunnel (Must run in a separate Administrator terminal).

### B. Resolving Port Conflicts (Jenkins vs. App)

Challenge: Both the Spring Boot App and Jenkins container listen on port 8080 internally. In Minikube/Windows, LoadBalancer services often share the single 127.0.0.1 IP, causing a collision where only one service is accessible.
Solution: I decoupled the External Service Port from the Internal Container Port in the Kubernetes Manifest (service.yaml).
Solid App: Stays on 8080:8080.
Jenkins: Remapped External Port to 8081.
YAML Implementation:

YAML


kind: Service
metadata:
  name: jenkins-service
spec:
  ports:
    - name: http-web
      port: 8081       # EXTERNAL: Traffic enters here
      targetPort: 8080 # INTERNAL: Forwards to standard Jenkins container


Impact: This allows both services to run side-by-side on the same host IP without modifying the standard Docker images.

C. Managing Secrets (Retrieving Initial Passwords)

Challenge: Upon first deployment, Jenkins generates a random admin password printed to stdout. If the logs rotate or scroll past, the password is lost from the console view.
Solution: Since the Pod is running, I used kubectl exec to read the file directly from the container's filesystem.
Identify the Pod:
PowerShell
kubectl get pods -l app=jenkins


Extract the Secret:
PowerShell
# Execute 'cat' command inside the running pod
kubectl exec deployment/jenkins-deployment -- cat /var/jenkins_home/secrets/initialAdminPassword


Impact: This technique demonstrates understanding of how to debug and interact with running containers in a K8s environment.
