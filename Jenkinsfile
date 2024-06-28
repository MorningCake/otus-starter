pipeline {
  agent any
  tools {
    jdk 'jdk11'
  }

  environment {
      MS_TEAMS_HOOK_URL = credentials('MSTeamsJenkinsHookURL')
      PITS_NEXUS = credentials('3bbdfcd0-c1be-411d-b330-30af8851df5d')
      DEVELOPMENT_VERSION = '1.0.0'
      PRODUCTION_VERSION = '1.0.0'
      QA_VERSION = '1.0.0'
  }

  stages {
    stage('Check environment') {
      steps{
        echo "The build number is ${BUILD_NUMBER}"
        echo "The branch is ${BRANCH_NAME}"
        sh 'java --version'
      }
    }

    stage('Build') {
      steps {
          sh '''
            ./gradlew -PpitsNexusUser=$PITS_NEXUS_USR -PpitsNexusPassword=$PITS_NEXUS_PSW -PgitBranch=$BRANCH_NAME -PpitsModuleVersionQA=$QA_VERSION -PpitsModuleVersionProduction=$PRODUCTION_VERSION -PpitsModuleVersionDevelopment=$DEVELOPMENT_VERSION -PpitsModuleBuildNumber=${BUILD_NUMBER} clean javadoc sourcesJar jar
          '''
      }
    }

    stage('Test') {
      steps {
        sh '''
         ./gradlew -PpitsNexusUser=$PITS_NEXUS_USR -PpitsNexusPassword=$PITS_NEXUS_PSW -PgitBranch=$BRANCH_NAME -PpitsModuleVersionQA=$QA_VERSION -PpitsModuleVersionProduction=$PRODUCTION_VERSION -PpitsModuleVersionDevelopment=$DEVELOPMENT_VERSION -PpitsModuleBuildNumber=${BUILD_NUMBER} check

        '''
      }
    }

    stage('Deploy Development jar to Nexus') {
      when { expression { return env.BRANCH_NAME == 'develop'} }
      steps {
        sh './gradlew -PpitsNexusUser=$PITS_NEXUS_USR -PpitsNexusPassword=$PITS_NEXUS_PSW -PgitBranch=$BRANCH_NAME -PpitsModuleVersionQA=$QA_VERSION -PpitsModuleVersionProduction=$PRODUCTION_VERSION -PpitsModuleVersionDevelopment=$DEVELOPMENT_VERSION -PpitsModuleBuildNumber=${BUILD_NUMBER} publish'
      }
    }

    stage('Deploy Production jar to Nexus') {
      when { expression { return env.BRANCH_NAME == 'master'} }
      steps {
        sh './gradlew -PpitsNexusUser=$PITS_NEXUS_USR -PpitsNexusPassword=$PITS_NEXUS_PSW -PdeployType=production -PgitBranch=$BRANCH_NAME -PpitsModuleVersionQA=$QA_VERSION -PpitsModuleVersionProduction=$PRODUCTION_VERSION -PpitsModuleVersionDevelopment=$DEVELOPMENT_VERSION -PpitsModuleBuildNumber=${BUILD_NUMBER} publish'
      }
    }
  }

  post {
      always {
          office365ConnectorSend webhookUrl: "$MS_TEAMS_HOOK_URL"
      }
    }
}