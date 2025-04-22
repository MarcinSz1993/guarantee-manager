pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/root/guarantee-manager'
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Deploy on VPS') {
      steps {
        sshagent(credentials: ['ssh-key-id']) {
          sh """
            echo "📡 Łączenie z serwerem i przygotowanie folderu"
            ssh -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST '
              mkdir -p $DEPLOY_DIR
            '

            echo "📦 Przesyłanie plików przez SCP"
            scp -r . $SSH_USER@$SSH_HOST:$DEPLOY_DIR

            echo "🚀 Restart aplikacji przez docker-compose"
            ssh $SSH_USER@$SSH_HOST '
              cd $DEPLOY_DIR &&
              docker-compose down &&
              docker-compose up -d --build
            '
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
      echo "❌ Deployment nie powiódł się!"
    }
  }
}
