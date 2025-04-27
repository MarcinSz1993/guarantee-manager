pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/var/www/guarantee-manager'
    FRONTEND_DIR = 'frontend'
    NGINX_CONF_NAME = 'guaranteemanager'
    DOMAIN_NAME = 'guarantee-manager.duckdns.org'
  }

  stages {
    stage('Checkout code') {
      steps {
        echo "Checkout code from Git repository"
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Install Dependencies') {
      steps {
        echo "Installing NPM dependencies for frontend"
        dir(FRONTEND_DIR) {
          script {
            sh 'npm install --prefer-offline'
          }
        }
      }
    }

    stage('Build Angular App') {
      steps {
        echo "Building Angular app for production"
        dir(FRONTEND_DIR) {
          script {
            sh 'npm run build -- --configuration production --base-href /'
          }
        }
      }
    }

    stage('Deploy to VPS') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'ssh-key-id', keyFileVariable: 'KEY_PATH'),
          usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USERNAME', passwordVariable: 'DB_PASSWORD'),
          string(credentialsId: 'cloudinary-api-key', variable: 'CLOUDINARY_API_KEY'),
          string(credentialsId: 'cloudinary-api-secret', variable: 'CLOUDINARY_API_SECRET'),
          string(credentialsId: 'postgres-password', variable: 'POSTGRES_PASSWORD'),
          string(credentialsId: 'smtp-username', variable: 'MAIL_USERNAME'),
          string(credentialsId: 'smtp-password', variable: 'MAIL_PASSWORD')
        ]) {
          script {
            // Zmienna dla komend SSH
            def sshCmd = "ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST"

            // Tworzenie katalogu na serwerze
            sh """
              echo "Tworzenie katalogu na serwerze..."
              \$sshCmd 'mkdir -p $DEPLOY_DIR'
            """

            // Kopiowanie pliku Nginx do sites-available
            sh """
              echo "Kopiowanie pliku Nginx do sites-available..."
              scp -i \$KEY_PATH -o StrictHostKeyChecking=no frontend/guaranteemanager.conf $SSH_USER@$SSH_HOST:/etc/nginx/sites-available/${NGINX_CONF_NAME}
            """

            // Kopiowanie pliku docker-compose.yml na serwer
            sh """
              echo "Kopiowanie pliku docker-compose.yml na serwer..."
              scp -i \$KEY_PATH -o StrictHostKeyChecking=no docker-compose.yml $SSH_USER@$SSH_HOST:$DEPLOY_DIR/docker-compose.yml
            """

            // Tworzenie dowiązania symbolicznego i usuwanie domyślnej konfiguracji
            sh """
              echo "Tworzenie dowiązania symbolicznego w sites-enabled i usuwanie default (jeśli istnieje)..."
              \$sshCmd << EOF
                set -e
                rm -f /etc/nginx/sites-enabled/default
                ln -sf /etc/nginx/sites-available/${NGINX_CONF_NAME} /etc/nginx/sites-enabled/${NGINX_CONF_NAME}
EOF
            """

            // Testowanie konfiguracji Nginx i przeładowanie
            sh """
              echo "Testowanie i przeładowanie konfiguracji Nginx..."
              \$sshCmd 'nginx -t'
              \$sshCmd 'systemctl reload nginx'
            """

            // Kopiowanie plików frontend na serwer
            sh """
              echo "Kopiowanie plików frontend na serwer..."
              scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r frontend/dist/frontend/browser/* $SSH_USER@$SSH_HOST:$DEPLOY_DIR/
            """

            // Uruchamianie docker-compose na VPS
            sh """
              echo "Uruchamianie docker-compose na VPS..."
              \$sshCmd << EOF
                export MAIL_USERNAME="${MAIL_USERNAME}"
                export MAIL_PASSWORD="${MAIL_PASSWORD}"
                export DB_USERNAME="${DB_USERNAME}"
                export DB_PASSWORD="${DB_PASSWORD}"
                export POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
                export CLOUDINARY_API_KEY="${CLOUDINARY_API_KEY}"
                export CLOUDINARY_API_SECRET="${CLOUDINARY_API_SECRET}"

                docker-compose down -v
                docker-compose up -d --build
EOF
            """
          }
        }
      }
    }
  }

  post {
    success {
      echo "✅ Deployment zakończony sukcesem!"
    }
    failure {
      echo "❌ Deployment nie powiódł się."
      currentBuild.result = 'FAILURE'
    }
  }
}
