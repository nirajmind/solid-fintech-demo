pipeline {
    agent {
            docker {
                image 'maven:3.8-openjdk-17'
                // 2. Map the Docker socket so the Maven container can run 'docker build'
                args '-v /var/run/docker.sock:/var/run/docker.sock'
            }
        }

    environment {
        // Define your app name
        APP_NAME = 'solid-project'
        // SonarQube Token (You'll add this in Jenkins credentials later)
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        // Stage 1: Build and Test
        stage('Build & Test') {
            steps {
                            // Since the agent is now the Maven image, use 'mvn' directly
                            // and skip the unnecessary 'chmod +x' and './mvnw' calls.
                            sh 'mvn clean package verify'
                        }
        }

        // Stage 2: Code Quality
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') { // You'll name your server 'LocalSonar' in Jenkins
                    sh "export SONAR_TOKEN='${SONAR_TOKEN}'; ./mvnw sonar:sonar -Dsonar.projectKey=${APP_NAME} -Dsonar.host.url=http://sonar-service:9000"
                }
            }
        }

        // Stage 3: Build Docker Image
        stage('Docker Build') {
            steps {
                            // 'docker' command is now available because the agent image is rich
                            // and the socket is mounted.
                            sh "docker build -t solid-project:latest ."
                   }
        }
    }

    post {
        always {
            // Clean up workspace to save space
            cleanWs()
        }
    }
}