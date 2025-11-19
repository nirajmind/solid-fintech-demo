This is an excellent finding by SonarQube. In a production environment (and even on your laptop), **Resource Limits are mandatory.**

Without them, if Jenkins or SonarQube decides to use 100% of your RAM, Kubernetes will allow it, potentially crashing your entire machine (or the Minikube VM).

Here is the "Wise" configuration. I have chosen these numbers specifically for a **local Minikube environment**—enough to run smoothly, but capped so they don't kill your laptop.

### **🛠️ Update k8s-full-stack.yaml**

You need to add the resources block to **every** container section in your YAML file.

#### **1. MongoDB (Lightweight)**

Mongo is efficient. We give it just enough to run.

YAML

\# Inside mongo-deployment \-\> spec \-\> containers  
        \- name: mongo  
          image: mongo:latest  
          ports:  
            \- containerPort: 27017  
          \# \--- ADD THIS BLOCK \---  
          resources:  
            requests:  
              memory: "256Mi"   \# Minimum RAM to start  
              cpu: "100m"       \# 10% of a CPU core  
            limits:  
              memory: "512Mi"   \# Maximum RAM allowed  
              cpu: "500m"       \# 50% of a CPU core  
          \# \----------------------  
          volumeMounts:  
            \- name: mongo-storage  
              mountPath: /data/db

#### **2. Solid App (Spring Boot)**

Java apps need a "heap." If we limit it too tight (e.g., 100MB), the JVM will crash with OutOfMemoryError. 512MB is a safe standard for a small microservice.

YAML

\# Inside solid-app-deployment \-\> spec \-\> containers  
        \- name: solid-app  
          image: solid-project:latest  
          imagePullPolicy: Never  
          ports:  
            \- containerPort: 8080  
          \# \--- ADD THIS BLOCK \---  
          resources:  
            requests:  
              memory: "256Mi"  
              cpu: "200m"  
            limits:  
              memory: "512Mi"  
              cpu: "1000m"      \# Allow full core burst during startup  
          \# \----------------------  
          env:  
            \- name: SPRING\_DATA\_MONGODB\_URI  
              value: "mongodb://mongo-service:27017/solid-db"

#### **3. SonarQube (The Heavy Lifter)**

SonarQube runs Java **AND** Elasticsearch inside. It is memory hungry. If you give it less than 1GB, it will likely crash instantly.

YAML

\# Inside sonar-deployment \-\> spec \-\> containers  
        \- name: sonar  
          image: sonarqube:community  
          ports:  
            \- containerPort: 9000  
          \# \--- ADD THIS BLOCK \---  
          resources:  
            requests:  
              memory: "1Gi"     \# Sonar needs a lot to start  
              cpu: "500m"  
            limits:  
              memory: "2Gi"     \# Hard cap to stop it freezing your PC  
              cpu: "1500m"  
          \# \----------------------  
          env:  
            \- name: SONAR\_ES\_BOOTSTRAP\_CHECKS\_DISABLE  
              value: "true"

#### **4. Jenkins (Java Controller)**

Jenkins is moderate, but can spike during builds.

YAML

\# Inside jenkins-deployment \-\> spec \-\> containers  
        \- name: jenkins  
          image: jenkins/jenkins:lts  
          ports:  
            \- containerPort: 8080  
            \- containerPort: 50000  
          \# \--- ADD THIS BLOCK \---  
          resources:  
            requests:  
              memory: "512Mi"  
              cpu: "200m"  
            limits:  
              memory: "1Gi"  
              cpu: "1000m"  
          \# \----------------------  
          volumeMounts:  
            \- name: jenkins-storage  
              mountPath: /var/jenkins\_home

---

### **🚀 Apply the Limits**

1. Save the file.  
2. Apply the changes:  
   PowerShell  
   kubectl apply \-f k8s\-full-stack.yaml

   *(Kubernetes will likely restart your pods one by one to apply these new rules).*

### **🧠 Interview Context: "Why did you choose these numbers?"**

If asked, here is your answer:

"I followed the **Request/Limit pattern** to ensure Quality of Service (QoS).

1. **Requests:** I set these based on the *minimum* required for the JVM to start without crashing (e.g., 1GB for SonarQube, 256MB for the Microservice). This guarantees the scheduler only places pods on nodes with enough capacity.  
2. **Limits:** I set these to prevent a memory leak in one application (like Jenkins) from starving the database or the main application, ensuring the system remains stable even under load."