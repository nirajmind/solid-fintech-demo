pipeline {
    agent any

    environment {
        // Define your app name
        APP_NAME = 'solid-project'
        // SonarQube Token (You'll add this in Jenkins credentials later)
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_BIN = 'apt-get update && apt-get install -y docker.io'
    }

    stages {

            stage('Initialize Tools') {
                        steps {
                                // Use sudo to gain permissions for apt-get
                                sh 'sudo apt-get update && sudo apt-get install -y docker.io'
                            }
            }

                    // Stage 2: Build & Test (Now 'mvn' and 'docker' are available)
                    stage('Build & Test') {
                        steps {
                            sh 'chmod +x ./mvnw'
                            sh './mvnw clean package verify'
                        }
                    }

                    // Stage 3: Docker Build
                    stage('Docker Build') {
                        steps {
                            sh "docker build -t solid-project:latest ."
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

    }

    post {
        always {
            // Clean up workspace to save space
            cleanWs()
        }
    }
}