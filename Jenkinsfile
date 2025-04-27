pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/var/www/guarantee-manager'
    BUILD_DIR = 'frontend/dist/guarantee-manager'
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Build Angular app') {
      steps {
        dir('frontend') {
          sh 'npm install'
          sh 'npm run build --configuration=production'
        }
      }
    }

    stage('Deploy to VPS') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'ssh-key-id', keyFileVariable: 'KEY_PATH')
        ]) {
          sh """
            echo "Usuwanie starej aplikacji na serwerze..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'rm -rf $DEPLOY_DIR/*'

            echo "Kopiowanie nowej wersji aplikacji..."
            scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r $BUILD_DIR/* $SSH_USER@$SSH_HOST:$DEPLOY_DIR

            echo "Sprawdzanie konfiguracji Nginx..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'nginx -t'

            echo "Przeładowanie Nginx..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'systemctl reload nginx'
          """
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
    }
  }
}
