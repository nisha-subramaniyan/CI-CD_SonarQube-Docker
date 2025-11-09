pipeline {
    // Run the pipeline on any available Jenkins agent
    agent any 

    environment {
        // Docker configuration
        DOCKERHUB_USER = 'nisha-subramaniyan'
        IMAGE_NAME = "ci-cd-sonarqube-docker"
        // Use BUILD_NUMBER for unique tags
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // SonarQube Tool Name configured in Manage Jenkins -> Global Tool Configuration
        SONAR_SCANNER_TOOL = 'SonarScanner' 
        SONAR_PROJECT_KEY = 'CI-CD-SonarQube-Docker'
        
        // Name of the SonarQube server configuration in Manage Jenkins -> Configure System
        SONAR_SERVER_NAME = 'Your-SonarQube-Server-Name' // <<-- *** IMPORTANT: UPDATE THIS NAME ***
    }
    
    // Note: Removed 'tools' block as Python often doesn't require explicit tool configuration if in PATH

    stages {
        
        // --- 1. Clone Stage ---
        stage('Clone Source Code') {
            steps {
                echo "Cloning repository: https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git on branch: main"
                git branch: 'main', url: 'https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git'
            }
        }
        
        // --- 2. Build & Test Stage (Python) ---
        stage('Build & Test') {
            steps {
                echo 'Installing Python dependencies and running tests...'
                // Install dependencies using pip (assuming a requirements.txt file exists)
                sh 'pip install -r requirements.txt' 
                // Run Python unit tests (adjust command if you use pytest or a different runner)
                sh 'python -m unittest discover' 
            }
        }
        
        // --- 3. SonarQube Analysis Stage ---
        stage('SonarQube Analysis') {
            steps { // <--- FIXED: Added 'steps' block
                withSonarQubeEnv(env.SONAR_SERVER_NAME) {
                    // 'sonarkey' must be a Jenkins Secret Text credential
                    withCredentials([string(credentialsId: 'sonarkey', variable: 'SONAR_AUTH_TOKEN')]) {
                        echo "Starting SonarQube analysis..."
                        
                        // Execute SonarScanner
                        sh "${tool env.SONAR_SCANNER_TOOL}/bin/sonar-scanner \
                            -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                            -Dsonar.login=${SONAR_AUTH_TOKEN} \
                            -Dsonar.host.url=${env.SONAR_HOST_URL}"
                    }
                }
            }
        }

        // --- 4. Quality Gate Stage ---
        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube Quality Gate status...'
                timeout(time: 15, unit: 'MINUTES') {
                    // Waits for the quality gate check
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        // --- 5. Docker Build Stage ---
        stage('Docker Build') {
            steps {
                echo "Building Docker image ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                // Ensure your Dockerfile copies the Python app and runs it
                sh "docker build -t ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
            }
        }
        
        // --- 6. Docker Push Stage ---
        stage('Docker Push') {
            steps { // <--- FIXED: Added 'steps' block
                // 'dockerhub' must be a Jenkins Username/Password credential
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    echo 'Logging into DockerHub and pushing image...'
                    
                    // Secure Docker login using standard input
                    sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                    // Push the built image
                    sh "docker push ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                }
                echo 'Docker image successfully pushed to DockerHub!'
            }
        }

        // --- 7. Deploy Stage (Example: Running the container) ---
        stage('Deploy') {
            steps {
                echo 'Starting application container locally...'
                // Stop and remove previous container (ignore errors if it doesn't exist)
                sh 'docker stop my-app-container || true'
                sh 'docker rm my-app-container || true'
                
                // Run the new container, exposing port 8080 (adjust port as needed for your Python app)
                sh "docker run -d --name my-app-container -p 8080:8080 ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                echo 'Application deployed and running on port 8080.'
            }
        }
    }
}
