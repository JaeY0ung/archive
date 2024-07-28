pipeline {
    agent any

    stages {
        stage('Cleanup') {
            steps {
                cleanWs() // Clean the workspace before starting
            }
        }

        stage('Checkout') {
            steps {
                script {
                    // 명시적으로 체크아웃을 수행합니다.
                    dir('workspace') {
                        try {
                            checkout([
                                $class: 'GitSCM', 
                                branches: [[name: '*/master']], 
                                userRemoteConfigs: [[url: 'https://lab.ssafy.com/s11-webmobile2-sub2/S11P12A507.git', credentialsId: 'gitlab-access-token']]
                            ])
                        } catch (Exception e) {
                            echo "Git checkout failed: ${e}"
                            sh 'pwd'
                            sh 'ls -la'
                            sh 'git config --list'
                            sh 'git status'
                            error("Git checkout failed")
                        }
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('workspace/frontend') {
                    script {
                        sh 'docker build -t frontend-app:latest .'
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('workspace/backend') {
                    script {
                        sh 'docker build -t backend-app:latest .'
                    }
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                script {
                    sh 'docker stop frontend || true && docker rm frontend || true'
                    sh 'docker run -d --name frontend -p 3000:80 frontend-app:latest'
                }
            }
        }

        stage('Deploy Backend') {
            steps {
                script {
                    sh 'docker stop backend || true && docker rm backend || true'
                    sh 'docker run -d --name backend -p 8080:8080 backend-app:latest'
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
