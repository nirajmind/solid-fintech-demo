pipeline {
    agent {
            // Run the entire pipeline on a node that can execute Docker commands.
            // We set the base agent to 'any' and will use the 'docker' step within stages.
            label 'master'
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
                sh 'chmod +x ./mvnw' // ADD THIS LINE: Grant execute permission
                sh './mvnw clean package verify'
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
                script {
                    // Build the image using the Dockerfile we created
                    sh "docker build -t solid-project:latest ."
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