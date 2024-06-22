node{
  stage('Scm Checkout'){
    git'https://github.com/operal90/zippy_javas', branch: 'main'
  }
  stage('Compile Package'){
    def mvnHome = tool name: 'maven', type: 'maven'
    sh "${mvnHome}/bin/mvn package"
  }
}
