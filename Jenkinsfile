pipeline {
    // Defines where the pipeline runs. Use a specific label if you have one.
    agent any 

    // Define fixed environment variables
    environment {
        // Docker configuration
        DOCKERHUB_USER = 'nisha-subramaniyan'
        IMAGE_NAME = "ci-cd-sonarqube-docker"
        // Use BUILD_NUMBER for unique tags for successful deployments
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // SonarQube Tool Name configured in Manage Jenkins -> Global Tool Configuration
        SONAR_SCANNER_TOOL = 'SonarScanner' 
        SONAR_PROJECT_KEY = 'CI-CD-SonarQube-Docker'
        
        // Name of the SonarQube server configuration in Manage Jenkins -> Configure System
        SONAR_SERVER_NAME = 'Your-SonarQube-Server-Name' // <<-- *** IMPORTANT: CHANGE THIS NAME ***
    }

    // Tools setup - configure these names in Manage Jenkins -> Global Tool Configuration
    tools {
        // Change 'M3' to your configured Maven name (e.g., 'Maven_3.8')
        maven 'M3' 
    }

    stages {
        
        // --- 1. Clone Stage ---
        // This explicitly checks out the code, though the initial Declarative SCM does this too.
        stage('Clone Source Code') {
            steps {
                echo "Cloning repository: ${params.GIT_URL} on branch: ${params.GIT_BRANCH}"
                git branch: 'main', url: 'https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git'
            }
        }
        
        // --- 2. Build & Test Stage ---
        stage('Build & Test') {
            steps {
                echo 'Building and running unit tests with Maven...'
                // Use 'sh' for Linux/Unix agent, 'bat' for Windows agent
                sh 'mvn clean install -DskipTests'
                sh 'mvn test' 
            }
        }
        
        // --- 3. SonarQube Analysis Stage ---
        stage('SonarQube Analysis') {
            // Uses the SonarQube configuration named in the environment block
            withSonarQubeEnv(env.SONAR_SERVER_NAME) {
                // 'sonarkey' must be a Jenkins Secret Text credential
                withCredentials([string(credentialsId: 'sonarkey', variable: 'SONAR_AUTH_TOKEN')]) {
                    echo "Starting SonarQube analysis with project key: ${env.SONAR_PROJECT_KEY}"
                    
                    // Execute SonarScanner
                    sh "${tool env.SONAR_SCANNER_TOOL}/bin/sonar-scanner \
                        -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                        -Dsonar.login=${SONAR_AUTH_TOKEN} \
                        -Dsonar.host.url=${env.SONAR_HOST_URL}" // SONAR_HOST_URL is set by withSonarQubeEnv
                }
            }
        }

        // --- 4. Quality Gate Stage ---
        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube Quality Gate status...'
                timeout(time: 15, unit: 'MINUTES') {
                    // Waits for the quality gate of the server specified in the previous step
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        // --- 5. Docker Build Stage ---
        stage('Docker Build') {
            steps {
                echo "Building Docker image ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                sh "docker build -t ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
            }
        }
        
        // --- 6. Docker Push Stage ---
        stage('Docker Push') {
            // 'dockerhub' must be a Jenkins Username/Password credential
            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                steps {
                    echo 'Logging into DockerHub and pushing image...'
                    // Use --password-stdin for security to pipe the password
                    sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                    // Push the built image
                    sh "docker push ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                }
            }
            steps {
                echo 'Docker image successfully pushed to DockerHub!'
            }
        }

        // --- 7. Deploy Stage (Example) ---
        stage('Deploy') {
            steps {
                echo 'Starting application container locally...'
                // Stop and remove previous container (ignore errors if it doesn't exist)
                sh 'docker stop my-app-container || true'
                sh 'docker rm my-app-container || true'
                
                // Run the new container, exposing port 8080
                sh "docker run -d --name my-app-container -p 8080:8080 ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                echo 'Application deployed and running on port 8080.'
            }
        }
    }
}
