// Jenkinsfile for CI/CD Pipeline with SonarQube and Docker

pipeline {
    agent any
    
    // Global environment variables
    environment {
        // SonarQube setup
        SONAR_SCANNER_HOME = tool 'SonarScanner' // Name of the SonarScanner installation in Jenkins
        // Docker setup
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub' // Jenkins ID for DockerHub credentials
        DOCKERHUB_USERNAME = 'nisha subramaniyan'
        DOCKERHUB_PASSWORD = credentials('dockerhub') // Use credentials binding
        IMAGE_NAME = "nisha-subramaniyan/ci-cd-sonar-docker"
        IMAGE_TAG = "build-${BUILD_NUMBER}"
        SONAR_KEY = credentials('sonarkey') // Jenkins ID for SonarQube Token
    }
    
    stages {
        // 1. Trigger (Implicitly handled by the SCM/Webhook configuration in Jenkins Job)
        // Stage 1: Checkout (Implicitly handled by Jenkins)
        stage('Checkout Code') {
            steps {
                echo 'Checking out code...'
                // Code checkout is usually done automatically by Jenkins when configuring the SCM.
            }
        }
        
        // 2. Build & Test
        stage('Build & Test') {
            steps {
                echo 'Building and running tests...'
                // Assuming a Maven/Gradle setup for a Java project. 
                // Using a simple shell command for a basic setup.
                sh 'javac -cp junit-platform-console-standalone-1.8.1.jar App.java testapp.java'
                sh 'java -jar junit-platform-console-standalone-1.8.1.jar -cp . --scan-classpath'
            }
        }

        // 3. SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv('SonarQubeServer') { // 'SonarQubeServer' is the name of your SonarQube server configuration in Jenkins
                    sh "${SONAR_SCANNER_HOME}/bin/sonar-scanner \
                      -Dsonar.projectKey=CI-CD_SonarQube-Docker \
                      -Dsonar.sources=. \
                      -Dsonar.host.url=http://your-sonarqube-ip:9000 \
                      -Dsonar.login=${SONAR_KEY}" 
                }
            }
        }

        // 4. Quality Gate Check (Fail if not met)
        stage('Quality Gate Check') {
            steps {
                echo 'Waiting for Quality Gate status...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // 5. Docker Build
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                script {
                    // Build the Docker image
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    sh "docker build -t ${IMAGE_NAME}:latest ."
                }
            }
        }

        // 6. Push to DockerHub
        stage('Push to DockerHub') {
            steps {
                echo 'Pushing image to DockerHub...'
                // Use the withCredentials binding for secure login
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                    sh "docker logout"
                }
            }
        }

        // 7. Deploy
        stage('Deploy') {
            steps {
                echo 'Deploying container locally...'
                // Stop and remove any old running container, then run the new one
                sh 'docker stop my-app-container || true'
                sh 'docker rm my-app-container || true'
                sh "docker run -d --name my-app-container ${IMAGE_NAME}:latest"
                echo 'Deployment complete. Container is running.'
            }
        }
    }
}
