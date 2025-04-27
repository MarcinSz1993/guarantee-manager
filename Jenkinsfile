pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/opt/guarantee-manager"  // Katalog na serwerze
        FRONTEND_TARGET_DIR = "/var/www/guarantee-manager" // Katalog frontendowy
        DB_USERNAME = credentials('db-username')  // Użycie poświadczeń Jenkins
        DB_PASSWORD = credentials('db-password')
        CLOUDINARY_API_KEY = credentials('cloudinary-api-key')
        CLOUDINARY_API_SECRET = credentials('cloudinary-api-secret')
        POSTGRES_PASSWORD = credentials('postgres-password')
        MAIL_USERNAME = credentials('smtp-username')
        MAIL_PASSWORD = credentials('smtp-password')
    }

    stages {
        stage('Checkout') {
            steps {
                dir("${DEPLOY_DIR}") {
                    deleteDir()  // Usuń poprzednią zawartość katalogu
                    git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'  // Pobierz repozytorium
                    sh 'ls -l'  // Wyświetl zawartość katalogu, aby upewnić się, że plik docker-compose.yml jest w repozytorium
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                dir("${DEPLOY_DIR}") {
                    sh 'ls -l'  // Upewnij się, że plik docker-compose.yml jest widoczny
                    sh 'docker-compose build'  // Budowanie obrazów Docker
                }
            }
        }

        stage('Stop Old Containers') {
            steps {
                dir("${DEPLOY_DIR}") {
                    sh 'docker-compose down'  // Zatrzymanie starych kontenerów
                }
            }
        }

        stage('Start New Containers') {
            steps {
                dir("${DEPLOY_DIR}") {
                    sh 'docker-compose up -d'  // Uruchomienie nowych kontenerów
                }
            }
        }

        stage('Prepare Frontend for NGINX') {
            steps {
                script {
                    // Upewniamy się, że katalog na frontend istnieje
                    sh "mkdir -p ${FRONTEND_TARGET_DIR}"

                    // Czyścimy katalog przed nową wrzutką
                    sh "rm -rf ${FRONTEND_TARGET_DIR}/*"

                    // Kopiujemy pliki z kontenera frontendowego do katalogu NGINX
                    sh "docker cp guarantee-frontend:/usr/share/nginx/html/. ${FRONTEND_TARGET_DIR}/"
                }
            }
        }

        stage('Deploy NGINX Config') {
            steps {
                script {
                    // Tworzymy katalogi, jeśli nie istnieją
                    sh "mkdir -p /etc/nginx/conf.d/"

                    // Kopiujemy plik konfiguracji NGINX dla naszego projektu
                    sh "cp ${DEPLOY_DIR}/frontend/guaranteemanager.conf /etc/nginx/conf.d/"

                    // Kopiujemy główny plik nginx.conf
                    sh "cp ${DEPLOY_DIR}/frontend/nginx.conf /etc/nginx/nginx.conf"
                }
            }
        }

//         stage('Restart NGINX') {
//             steps {
//                 script {
//                     // Zatrzymanie i ponowne uruchomienie NGINX
//                     sh 'systemctl restart nginx'
//                 }
//             }
//         }
    }
}
