// =====================================================================
// JENKINSFILE — CI/CD Pipeline for Selenium + Cucumber + TestNG Framework
// =====================================================================
// This file tells Jenkins exactly what to do when a build is triggered:
// checkout code, compile it, run tests, rerun any failures, publish
// reports, and notify the team of the result.
//
// This uses "Declarative Pipeline" syntax — the modern, recommended
// way to write Jenkins pipelines (as opposed to older "Scripted" style).
// =====================================================================

pipeline {

    // "agent any" = this pipeline can run on any available Jenkins agent/machine.
    // In bigger setups, this could instead specify a Docker image or a
    // specific labeled machine — "any" is the simplest starting point.
    agent any

    // -------------------------------------------------------------------
    // TOOLS — tells Jenkins which JDK and Maven installation to use.
    // These names ('JDK-17', 'Maven-3.9') must match EXACTLY what was
    // configured in: Jenkins → Manage Jenkins → Tools.
    // -------------------------------------------------------------------
    tools {
        jdk 'JDK-17'
        maven 'Maven-3.9'
    }

    // -------------------------------------------------------------------
    // PARAMETERS — these turn into a form with dropdowns/checkboxes
    // shown to whoever clicks "Build with Parameters" in Jenkins.
    // This lets one single pipeline serve many purposes (different
    // browsers, environments, tag filters) instead of needing a
    // separate pipeline for each combination.
    // -------------------------------------------------------------------
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'edge', 'firefox'],
            description: 'Which browser to run the tests on'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'staging', 'prod'],
            description: 'Which environment config to test against'
        )
        choice(
            name: 'TAGS',
            choices: ['@smoke', '@regression'],
            description: 'Which set of scenarios to run (tag-based filter)'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (no visible window) — recommended for CI agents'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '5',
            description: 'How many scenarios to run in parallel — size this to the agent machine\'s RAM'
        )
    }

    // -------------------------------------------------------------------
    // OPTIONS — general pipeline behavior settings (not test-related).
    // -------------------------------------------------------------------
    options {
        // Adds a timestamp next to every line in the console log —
        // useful for spotting slow steps.
        timestamps()

        // Prevents two builds of this same pipeline from running at the
        // same time (avoids two Selenium runs colliding on one agent).
        disableConcurrentBuilds()

        // Keeps only the last 30 builds' history/artifacts, then
        // auto-deletes older ones — prevents Jenkins' disk from filling up.
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    // =====================================================================
    // STAGES — the actual steps of the pipeline, run in order, top to bottom.
    // Each stage shows up as its own box in the Jenkins UI, so you can see
    // exactly which step passed/failed at a glance.
    // =====================================================================
    stages {

        // ---------------------------------------------------------------
        // STAGE 1: Pull the latest code from Git.
        // "checkout scm" automatically uses whatever repo/branch this
        // Jenkins job was configured to point at — no need to hardcode
        // a Git URL here.
        // ---------------------------------------------------------------
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ---------------------------------------------------------------
        // STAGE 2: Compile the Java code first, before running anything.
        // This catches compile errors early and fast, before wasting
        // time launching browsers for tests that wouldn't even run.
        // "-B" = "batch mode", keeps Maven's console output clean for CI.
        // ---------------------------------------------------------------
        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        // ---------------------------------------------------------------
        // STAGE 3: Run the actual test suite.
        // Every "-D..." flag below overrides a value that our Java
        // framework already knows how to read (via ConfigReader,
        // BrowserContext, etc.) — so nothing in the Java code needs
        // to change for these parameters to take effect.
        //
        // "returnStatus: true" means: if tests fail, DON'T immediately
        // fail this whole pipeline yet. Instead, capture the pass/fail
        // result as a number (0 = success, non-zero = failure) into
        // MAIN_RUN_EXIT_CODE, so the NEXT stage can decide what to do
        // about it (e.g., try a rerun first).
        // ---------------------------------------------------------------
        stage('Run Tests') {
            steps {
                script {
                    env.MAIN_RUN_EXIT_CODE = sh(
                        returnStatus: true,
                        script: """
                            mvn -B clean test \
                                -Dbrowser=${params.BROWSER} \
                                -Denv=${params.ENVIRONMENT} \
                                -Dheadless=${params.HEADLESS} \
                                -Dcucumber.filter.tags="${params.TAGS}" \
                                -DdataProviderThreadCount=${params.THREAD_COUNT}
                        """
                    ).toString()
                }
            }
        }

        // ---------------------------------------------------------------
        // STAGE 4: Rerun ONLY the scenarios that failed — but only if
        // Stage 3 actually had failures. The "when { expression {...} }"
        // block means: skip this entire stage if MAIN_RUN_EXIT_CODE is
        // "0" (i.e., everything already passed — no need to rerun).
        //
        // "-Prerun" activates the rerun Maven profile, which runs
        // RerunRunner.java against target/rerun.txt (the list of failed
        // scenarios that Cucumber automatically wrote during Stage 3).
        // ---------------------------------------------------------------
        stage('Rerun Failed Scenarios') {
            when {
                expression { env.MAIN_RUN_EXIT_CODE != '0' }
            }
            steps {
                echo "Main run had failures — rerunning failed scenarios only..."
                script {
                    env.RERUN_EXIT_CODE = sh(
                        returnStatus: true,
                        script: """
                            mvn -B test -Prerun \
                                -Dbrowser=${params.BROWSER} \
                                -Denv=${params.ENVIRONMENT} \
                                -Dheadless=${params.HEADLESS}
                        """
                    ).toString()
                }
            }
        }

        // ---------------------------------------------------------------
        // STAGE 5: Decide the FINAL build result.
        //
        // This step matters a lot — without it, using "returnStatus: true"
        // above would let Jenkins show a build as green/successful even
        // when tests actually failed, since we deliberately stopped the
        // pipeline from auto-failing earlier (to allow the rerun).
        // This stage makes the pass/fail decision explicit and correct:
        //
        //   - Passed first try           → SUCCESS
        //   - Failed first try, but the
        //     rerun passed                → UNSTABLE (a warning: something
        //                                    is flaky and worth investigating)
        //   - Still failing after rerun  → FAILURE
        // ---------------------------------------------------------------
        stage('Evaluate Results') {
            steps {
                script {
                    if (env.MAIN_RUN_EXIT_CODE == '0') {
                        currentBuild.result = 'SUCCESS'
                    } else if (env.RERUN_EXIT_CODE == '0') {
                        currentBuild.result = 'UNSTABLE'
                        echo "Some scenarios failed initially but passed on rerun — investigate flakiness."
                    } else {
                        currentBuild.result = 'FAILURE'
                        error("Tests failed even after rerun.")
                    }
                }
            }
        }
    }

    // =====================================================================
    // POST — actions that run AFTER all stages finish, regardless of
    // whether the pipeline passed or failed. This is where we publish
    // reports and send notifications.
    // =====================================================================
    post {

        // "always" runs no matter what the final result was — pass,
        // fail, or unstable. We want reports published every single time.
        always {

            // Reads Surefire's XML test results and feeds Jenkins' built-in
            // pass/fail trend graph (visible on the job's main page over time).
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Publishes our Extent Spark HTML report as a clickable link
            // directly on the Jenkins build page sidebar.
            // "reportDir" here matches extent.reporter.spark.out in
            // extent.properties — must stay in sync if that ever changes.
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports/SparkReport',
                reportFiles: 'Spark.html',
                reportName: 'Extent Spark Report'
            ])

            // Saves the PDF report, Cucumber JSON report, and the log file
            // as downloadable "build artifacts" attached to this specific run.
            archiveArtifacts artifacts: 'target/extent-reports/PdfReport/*.pdf', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/cucumber-reports/*.json', allowEmptyArchive: true
            archiveArtifacts artifacts: 'logs/automation.log', allowEmptyArchive: true
        }

        // Runs only when the final result was SUCCESS.
        success {
            echo "Build passed."
            // TODO: add a Slack/email notification here if desired.
        }

        // Runs only when the final result was UNSTABLE (passed on rerun).
        unstable {
            echo "Build passed only after rerun — flaky scenarios detected."
            // TODO: add a Slack/email notification here if desired.
        }

        // Runs only when the final result was FAILURE (still failing after rerun).
        failure {
            echo "Build failed even after rerun."
            // TODO: add a Slack/email notification here if desired.
        }
    }
}