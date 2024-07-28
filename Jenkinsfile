pipeline {
    agent any

    environment {
        // Docker 이미지 이름 설정
        FRONTEND_IMAGE = 'frontend-app'
        BACKEND_IMAGE = 'backend-app'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git 'https://lab.ssafy.com/s11-webmobile2-sub2/S11P12A507.git'
            }
        }
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        // 프론트엔드 Docker 이미지를 빌드합니다.
                        docker.build("${FRONTEND_IMAGE}:${env.BUILD_ID}")
                    }
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        // 백엔드 Docker 이미지를 빌드합니다.
                        docker.build("${BACKEND_IMAGE}:${env.BUILD_ID}")
                    }
                }
            }
        }
        stage('Deploy Frontend') {
            steps {
                script {
                    // 기존 프론트엔드 컨테이너를 종료합니다.
                    sh 'docker stop frontend || true && docker rm frontend || true'
                    // 새 프론트엔드 컨테이너를 실행합니다.
                    sh "docker run -d --name frontend -p 3000:80 ${FRONTEND_IMAGE}:${env.BUILD_ID}"
                }
            }
        }
        stage('Deploy Backend') {
            steps {
                script {
                    // 기존 백엔드 컨테이너를 종료합니다.
                    sh 'docker stop backend || true && docker rm backend || true'
                    // 새 백엔드 컨테이너를 실행합니다.
                    sh "docker run -d --name backend -p 8080:8080 ${BACKEND_IMAGE}:${env.BUILD_ID}"
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
