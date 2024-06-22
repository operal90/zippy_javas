node {
    stage('Scm Checkout') {
        git url: 'https://github.com/operal90/zippy_javas', branch: 'main'
    }

    stage('Compile Package') {
        def mvnHome = tool name: 'maven', type: 'maven'
        sh "echo Using Maven Home: ${mvnHome}"
        sh "echo Running Maven Package"
        sh "${mvnHome}/bin/mvn -X package" // '-X' enables debug output
    }
}
