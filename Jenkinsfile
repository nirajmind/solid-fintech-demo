pipeline {
    agent any

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
                sh 'chmod +x ./mvnw' // ADD THIS LINE: Grant execute permission
                sh './mvnw clean package verify'
            }
        }

        // Stage 2: Code Quality
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') { // You'll name your server 'LocalSonar' in Jenkins
                    sh "./mvnw sonar:sonar -Dsonar.projectKey=${APP_NAME} -Dsonar.host.url=http://localhost:9000 -Dsonar.token=${SONAR_TOKEN}"
                }
            }
        }

        // Stage 3: Build Docker Image
        stage('Docker Build') {
            steps {
                script {
                    // Build the image using the Dockerfile we created
                    sh "docker build -t ${APP_NAME}:latest ."
                }
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